package com.scowluga.android.rcacc.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.media.MediaHttpDownloader;
import com.google.api.client.googleapis.media.MediaHttpDownloaderProgressListener;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.IOUtils;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.Permission;
import com.google.api.services.drive.model.User;
import com.scowluga.android.rcacc.Message.Message;
import com.scowluga.android.rcacc.sync.provider.FeedContract;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.scowluga.android.rcacc.sync.SyncAdapter.COLUMN_AUDIENCE;
import static com.scowluga.android.rcacc.sync.SyncAdapter.COLUMN_NOTES;
import static com.scowluga.android.rcacc.sync.SyncAdapter.COLUMN_POSTDATE;
import static com.scowluga.android.rcacc.sync.SyncAdapter.COLUMN_TITLE;

/**
 * Created by User on 12/19/2016.
 */

public class TestDrive {

    private static final String TAG = TestDrive.class.getSimpleName();

    private final Context mContext;
    private final ContentResolver mContentResolver;

    public TestDrive(Context context) {
        mContext = context;
        mContentResolver = context.getContentResolver();
    }

    public Context getContext() {
        return mContext;
    }

    private static class TTTAsyncJob extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String[] params) {
            try {
                RemoteDataStore store = GoogleDriveDataStore.getInstance();
                File localFile = getSampleFile();
                store.upload(localFile);

                InputStream download = store.download();
                pp(download);
                Log.i(TAG, "=========================================");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "something";
        }

        @Override
        protected void onPostExecute(String message) {
            //process message
        }

    }

    private static Map<String, String> driveList(Drive drive) throws IOException {
        FileList list = drive.files().list().setPageSize(10).setFields("nextPageToken, files(id, name)").execute();
        List<com.google.api.services.drive.model.File> files = list.getFiles();
        Map<String, String> results = new HashMap<>();
        if (files != null && files.size() > 0) {
            System.out.println("Files:");
            for (com.google.api.services.drive.model.File file : files) {
                System.out.printf("%s (%s)\n", file.getName(), file.getId());
                results.put(file.getId(), file.getName());
            }
        }
        return results;
    }

    private static final File getSampleFile() {

        File localFile = null;
        BufferedReader reader = null;
        try {
            InputStream resourceAsStream = TestDrive.class.getResourceAsStream("/testing.txt");

            String prefix = "LocalFilePrefix";
            String suffix = "LocalFileSuffix";
            localFile = File.createTempFile(prefix, suffix);

            FileOutputStream out = new FileOutputStream(localFile);
            IOUtils.copy(resourceAsStream, out);


            reader = new BufferedReader(new FileReader(localFile));
            String text = null;

            while ((text = reader.readLine()) != null) {
                Log.i(TAG, ">> " + text);
            }
        } catch (FileNotFoundException fnf) {
            fnf.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
            }
        }

        return localFile;
    }

    public void ttt(String userId) {
        Log.i(TAG, "--------------------------------------------");
        try {

            new AsyncTask<Void, Void, GoogleDriveDataStore>() {
                @Override
                protected GoogleDriveDataStore doInBackground(Void... params) {
                    try {
                        return GoogleDriveDataStore.getInstance();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    return null;
                }
            }.execute();

            AsyncTask<String, Void, String> job = new TTTAsyncJob();
            job.execute();

            GoogleDriveDataStore drive = GoogleDriveDataStore.getInstance();

//            tttPermission();

            List<Message> localMessages = queryMessages();
            for (Message msg : localMessages) {
                System.out.println("\t" + msg);
            }
            final List<Message> remoteMessages = new ArrayList<>();
            final RemoteFeedProcessor parser = new RemoteFeedProcessor();
            new AsyncTask<Void, Void, GoogleDriveDataStore>() {
                @Override
                protected GoogleDriveDataStore doInBackground(Void... params) {
                    try {
                        InputStream in = GoogleDriveDataStore.getInstance().download();
                        List<Message> messages = parser.decode(in);
                        remoteMessages.addAll(messages);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    return null;
                }
            }.execute();

            final List<Message> msgList = new ArrayList<>(remoteMessages);
            msgList.addAll(localMessages);

            final boolean resetRemoteData = false;

            new AsyncTask<Void, Void, GoogleDriveDataStore>() {
                @Override
                protected GoogleDriveDataStore doInBackground(Void... params) {
                    try {

                        File file = writeToFile(msgList);
                        if (resetRemoteData) {
                            GoogleDriveDataStore.getInstance().upload(file);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    return null;
                }
            }.execute();

            testInsert();
        } catch (Exception ioe) {
            ioe.printStackTrace();
        }

    }

    public static File writeToFile(List<Message> messages) throws IOException {
        final char delimiter = '|';
        File file = File.createTempFile("messages", ".txt");
        FileOutputStream fos = new FileOutputStream(file);
        for (Message m : messages) {
            StringBuilder sb = new StringBuilder();

            sb.append(m.getTitle()).append(delimiter);
            sb.append(m.getDate().getTime()).append(delimiter);
            sb.append(m.getAudience()).append(delimiter);
            sb.append(m.getNotes()).append("\n");
            fos.write(sb.toString().getBytes());
        }
        fos.close();
        return file;
    }
    private static final String[] PROJECTION = new String[] {
            FeedContract.Entry._ID,
            FeedContract.Entry.COLUMN_NAME_TITLE,
            FeedContract.Entry.COLUMN_NAME_AUDIENCE,
            FeedContract.Entry.COLUMN_NAME_NOTES,
            FeedContract.Entry.COLUMN_NAME_POSTDATE
    };

    private List<Message> queryMessages() {

        List<Message> results = new ArrayList<>();

        final ContentResolver contentResolver = getContext().getContentResolver();
        // Get list of all items
        Uri uri = FeedContract.Entry.CONTENT_URI; // Get all entries
        String selection = FeedContract.Entry.COLUMN_NAME_TITLE + " like ?";
        String[] selectionArgs = { "%Th%" };
        String orderBy = FeedContract.Entry.COLUMN_NAME_POSTDATE + " desc";
        Cursor c = contentResolver.query(uri, PROJECTION, null, null, orderBy);
        assert c != null;
        Log.i(TAG, "Found " + c.getCount() + " local entries. Computing merge solution...");

        // Find stale data

        while (c.moveToNext()) {
            int id = c.getInt(SyncAdapter.COLUMN_ID);
            String title = c.getString(COLUMN_TITLE);
            String audience = c.getString(COLUMN_AUDIENCE);
            String notes = c.getString(COLUMN_NOTES);
            long postdate = c.getLong(COLUMN_POSTDATE);
            results.add(new Message(title, new Date(postdate), audience, notes));
        }
        c.close();
        return results;
    }

    private void testInsert() {

        List<Message> messages = queryMessages();

        // title seven | 2016-12-17 15:57:22.123 | 777 | this is the 7th message

        Uri uri = FeedContract.Entry.CONTENT_URI; // Get all entries
        ContentValues value = new ContentValues();
        value.put(FeedContract.Entry.COLUMN_NAME_TITLE, "title eight");
        value.put(FeedContract.Entry.COLUMN_NAME_AUDIENCE, "audience-888");
        value.put(FeedContract.Entry.COLUMN_NAME_NOTES, "this is the 8th message - inserted manually.");
        value.put(FeedContract.Entry.COLUMN_NAME_POSTDATE, new Date().getTime());
        Uri insert = mContentResolver.insert(uri, value);

        List<Message> newMsgs = queryMessages();
        int delta = newMsgs.size() - messages.size();
        System.out.println("diff = " + delta);
    }



    private void testDelete() {
//        ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();
//        Uri deleteUri = FeedContract.Entry.CONTENT_URI.buildUpon()
//                .appendPath(Integer.toString(id)).build();
//        Log.i(TAG, "Scheduling delete: " + deleteUri);
//        batch.add(ContentProviderOperation.newDelete(deleteUri).build());

    }

    /** Uploads a file using either resumable or direct media upload. */
    private static com.google.api.services.drive.model.File uploadFile(Drive drive, boolean useDirectUpload, File uploadFile, String title)
            throws IOException {

        com.google.api.services.drive.model.File metadata = new com.google.api.services.drive.model.File();
        metadata.setName(title);
//        metadata.setTitle(uploadFile.getName());

        FileContent content = new FileContent("text/html", uploadFile);

        Drive.Files.Create create = drive.files().create(metadata, content);
//        MediaHttpUploader uploader = create.getMediaHttpUploader();
//        uploader.setDirectUploadEnabled(useDirectUpload);
//        uploader.setProgressListener(new FileUploadProgressListener());
        return create.execute();

//
//        Drive.Files.Insert insert = drive.files().insert(metadata, content);
//        MediaHttpUploader uploader = insert.getMediaHttpUploader();
//        uploader.setDirectUploadEnabled(useDirectUpload);
//        uploader.setProgressListener(new FileUploadProgressListener());
//        return insert.execute();
    }

    /** Updates the name of the uploaded file to have a "drivetest-" prefix. */
    private static com.google.api.services.drive.model.File updateFileWithTestSuffix(Drive drive, String id, String name) throws IOException {
        com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
//        fileMetadata.setTitle("drivetest-" + UPLOAD_FILE.getName());
//        fileMetadata.setTitle("drivetest-" + name);
        fileMetadata.setName("drivetest-" + name);

        Drive.Files.Update update = drive.files().update(id, fileMetadata);
        return update.execute();
    }

    /** Downloads a file using either resumable or direct media download. */
//    private static void downloadFile(HttpTransport transport, Drive drive, boolean useDirectDownload, com.google.api.services.drive.model.File uploadedFile)
//            throws IOException {
//        // create parent directory (if necessary)
//        java.io.File parentDir = new java.io.File(DIR_FOR_DOWNLOADS);
//        if (!parentDir.exists() && !parentDir.mkdirs()) {
//            throw new IOException("Unable to create parent directory");
//        }
//        OutputStream out = new FileOutputStream(new java.io.File(parentDir, uploadedFile.getName()));
//
//        MediaHttpDownloader downloader = new MediaHttpDownloader(transport, drive.getRequestFactory().getInitializer());
//        downloader.setDirectDownloadEnabled(useDirectDownload);
//        downloader.setProgressListener(new FileDownloadProgressListener());
////        downloader.download(new GenericUrl(uploadedFile.getDownloadUrl()), out);
//        downloader.download(new GenericUrl(uploadedFile.getWebViewLink()), out);
//    }

    private static class FileDownloadProgressListener implements MediaHttpDownloaderProgressListener {

        @Override
        public void progressChanged(MediaHttpDownloader downloader) {
            switch (downloader.getDownloadState()) {
                case MEDIA_IN_PROGRESS:
                    System.out.println("Download is in progress: " + downloader.getProgress());
                    break;
                case MEDIA_COMPLETE:
                    System.out.println("Download is Complete!");
                    break;
            }
        }
    }

    private static class FileUploadProgressListener implements MediaHttpUploaderProgressListener {

        @Override
        public void progressChanged(MediaHttpUploader uploader) throws IOException {
            switch (uploader.getUploadState()) {
                case INITIATION_STARTED:
                    System.out.println("Upload Initiation has started.");
                    break;
                case INITIATION_COMPLETE:
                    System.out.println("Upload Initiation is Complete.");
                    break;
                case MEDIA_IN_PROGRESS:
                    System.out.println("Upload is In Progress: "
                            + NumberFormat.getPercentInstance().format(uploader.getProgress()));
                    break;
                case MEDIA_COMPLETE:
                    System.out.println("Upload is Complete!");
                    break;
            }
        }
    }

    public void downloadFile() {
//        final GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(this, Arrays.asList(Drive.SCOPE_FILE));
//        credential.setSelectedAccountName(accountName);
//        final DriveFolder folder = Drive.DriveApi.getRootFolder(mGoogleApiClient);

//        FileOutputStream mOutput=null;
//        try {
//            GoogleAccountCredential crd = GoogleAccountCredential.usingOAuth2(this, Arrays.asList(Drive.SCOPE_FILE));
//            crd.setSelectedAccountName(accountName);
//            com.google.api.services.drive.Drive srv = new com.google.api.services.drive.Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), crd).build();
//            mInput = srv.getRequestFactory().buildGetRequest(new GenericUrl(url)).execute().getContent();
//            String outFileName = getApplicationContext().getDatabasePath(DatabaseHandler.DATABASE_NAME).getPath();
//            mOutput = new FileOutputStream(outFileName);
//            byte[] mBuffer = new byte[1024];
//            int mLength;
//            while ((mLength = mInput.read(mBuffer)) > 0) {
//                mOutput.write(mBuffer, 0, mLength);
//            }
//            mOutput.flush();
//            Log.d("TAG", "Successfully Downloaded contents");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }finally {
//            try {
//                //Close the streams
//                if(mOutput != null){
//                    mOutput.close();
//                }
////                if(mInput != null){
////                    mInput.close();
////                }
//            } catch (IOException e) {
//                Log.e("Tag", "failed to close databases");
//            }
//        }
    }

    private static void pp(InputStream in) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                Log.i(TAG, ">>> " + line);
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cat(String filename) {
        File file = new File(getContext().getFilesDir(), filename);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                Log.i(TAG, ">>> " + line);
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveToLocalFile(String filename) {
        File file = new File(getContext().getFilesDir(), filename);
//        List<Message> messages = getMessageList();
//        FileOutputStream outputStream;
//        outputStream = getContext().openFileOutput(filename, Context.MODE_PRIVATE);
//        outputStream.write(string.getBytes());
//        outputStream.close();

        try {
            FileWriter writer = new FileWriter(file);
            for (Message msg : getMessageList()) {
                writer.write(msg.toString());
                writer.write('\n');
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<Message> getMessageList() {

        final String[] projection = new String[] {
                FeedContract.Entry._ID,
                FeedContract.Entry.COLUMN_NAME_TITLE,
                FeedContract.Entry.COLUMN_NAME_AUDIENCE,
                FeedContract.Entry.COLUMN_NAME_NOTES,
                FeedContract.Entry.COLUMN_NAME_POSTDATE
        };

        final String orderBy = FeedContract.Entry.COLUMN_NAME_POSTDATE;
        Uri uri = FeedContract.Entry.CONTENT_URI; // Get all entries
        Cursor cursor = mContentResolver.query(uri, projection, null, null, orderBy);
        assert cursor != null;
        List<Message> messages = new ArrayList<>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String title = cursor.getString(1);
            String audience = cursor.getString(2);
            String notes = cursor.getString(3);
            long postdate = cursor.getLong(4);
            messages.add(new Message(title, new Date(postdate), audience, notes));

        }
        cursor.close();
        return messages;
    }
}
