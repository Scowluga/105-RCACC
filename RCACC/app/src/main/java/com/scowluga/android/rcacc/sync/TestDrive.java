//package com.scowluga.android.rcacc.sync;
//
//import android.content.ContentResolver;
//import android.content.Context;
//import android.database.Cursor;
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.util.Log;
//
//import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
//import com.google.api.client.googleapis.media.MediaHttpDownloader;
//import com.google.api.client.googleapis.media.MediaHttpDownloaderProgressListener;
//import com.google.api.client.googleapis.media.MediaHttpUploader;
//import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;
//import com.google.api.client.http.FileContent;
//import com.google.api.client.http.GenericUrl;
//import com.google.api.client.http.HttpResponse;
//import com.google.api.client.http.javanet.NetHttpTransport;
//import com.google.api.client.json.jackson2.JacksonFactory;
//import com.google.api.client.util.DateTime;
//import com.google.api.client.util.IOUtils;
//import com.google.api.services.drive.Drive;
//import com.google.api.services.drive.DriveScopes;
//import com.google.api.services.drive.model.FileList;
//import com.google.api.services.drive.model.Permission;
//import com.google.api.services.drive.model.User;
//import com.scowluga.android.rcacc.sync.provider.FeedContract;
//import com.scowluga.android.rcacc.sync.provider.Message;
//
//import java.io.BufferedReader;
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.FileReader;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.text.NumberFormat;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * Created by User on 12/19/2016.
// */
//
//public class TestDrive {
//
//    private static final String TAG = TestDrive.class.getSimpleName();
//
////    private static final String DIR_FOR_DOWNLOADS = System.getProperty("user.dir") + "/../data/download";
//
//    private static String PRIVATE_KEY_FILE = "/secret/PhiSyncAdapter-129596f15df7.json";
//
//    private final Context mContext;
//    private final ContentResolver mContentResolver;
//
//    public TestDrive(Context context) {
//        mContext = context;
//        mContentResolver = context.getContentResolver();
//    }
//
//    public Context getContext() {
//        return mContext;
//    }
//
//    private static class TTTAsyncJob extends AsyncTask<String, Void, String> {
//        @Override
//        protected String doInBackground(String[] params) {
//            try {
//                RemoteDataStore store = GoogleDriveDataStore.getInstance();
//                File localFile = getSampleFile();
//                store.upload(localFile);
//
//                InputStream download = store.download();
//                pp(download);
//                Log.i(TAG, "=========================================");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return "something";
//        }
//
//        @Override
//        protected void onPostExecute(String message) {
//            //process message
//        }
//
//    }
//
//    private static class RetrieveTokenJob extends AsyncTask<String, Void, String> {
//
//        private Map<String, String> mUploadedFiles = new HashMap<>();
//
//        @Override
//        protected String doInBackground(String[] params) {
//
//            try {
//                GoogleCredential credential = GoogleCredential.fromStream(TestDrive.class.getResourceAsStream(PRIVATE_KEY_FILE))
//                        .createScoped(Collections.singleton(DriveScopes.DRIVE));
//
//                Drive drive = new Drive.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance(), credential)
//                        .setApplicationName("Auth Code Exchange Demo")
//                        .build();
//
//                int cmd = 5;
//                if (cmd == 0) {
//                    // create
//                    String[] titles = new String[]{
//                            "Testing - One",
//                            "Testing - Two",
//                            "Testing - Three",
//                            "Testing - Four",
//                            "Testing - Five",
//                            "Testing - Last"
//                    };
//                    File localFile = getSampleFile();
//                    for (String nm : titles) {
//                        com.google.api.services.drive.model.File uploadFile = uploadFile(drive, true, localFile, nm);
//                        mUploadedFiles.put(uploadFile.getId(), uploadFile.getName());
//                    }
//                }
//                Map<String, String> filesOnDrive = new HashMap<>();
//                if (true || cmd == 1) {
//                    // list
//                    filesOnDrive = driveList(drive);
//                    for (Map.Entry<String, String> entry : filesOnDrive.entrySet()) {
//                        final String id = entry.getKey();
//                        com.google.api.services.drive.model.File f = drive.files().get(id).execute();
//                        String fName = f.getName();
//                        String fDesc = f.getDescription();
//                        List<Permission> permissions = f.getPermissions();
//                        List<User> owners = f.getOwners();
//                        Boolean viewedByMe = f.getViewedByMe();
//                        DateTime fCreatedTime = f.getCreatedTime();
//                        boolean isSame = entry.getValue().equals(fName);
//                        System.out.println("File: " + fName + " - " + isSame);
//                    }
//                }
//
//                if (cmd == 2) {
//                    // update
//                    for (Map.Entry<String, String> entry : filesOnDrive.entrySet()) {
//                        String fileId = entry.getKey();
//                        com.google.api.services.drive.model.File metadata = new com.google.api.services.drive.model.File();
//                        metadata.setDescription("updated description (previous: " + "kdfj");
//                        metadata.setName("updated-name: " + entry.getValue());
//
//                        com.google.api.services.drive.model.File execute = drive.files().update(fileId, metadata).execute();
//                        boolean success = fileId.equals(execute.getId());
//                        if (!success) {
//                            System.err.println("failed: " + fileId);
//                        }
//                    }
//                }
//                if (cmd == 3) {
//                    // download
////                    com.google.api.services.drive.model.File downloaded = new com.google.api.services.drive.model.File();
////                    downloadFile(httpTransport, drive, true, downloaded);
//                    for (Map.Entry<String, String> entry : filesOnDrive.entrySet()) {
//                        String fileId = entry.getKey();
//
//                        com.google.api.services.drive.model.File f = drive.files().get(fileId).execute();
//                        String webContentLink = f.getWebContentLink();
//                        String webViewLink = f.getWebViewLink();
//                        Boolean isAppAuthorized = f.getIsAppAuthorized();
//                        Long size = f.getSize();
//                        List<Permission> permissions = f.getPermissions();
//                        String mimeType = f.getMimeType();
//                        boolean isSame = entry.getValue().equals(f.getName());
//                        InputStream inputStream = downloadFile(drive, f);
//                        pp(inputStream);
//
//                    }
//                }
//                if (cmd == 4) {
//                    // download
//                    for (Map.Entry<String, String> entry : filesOnDrive.entrySet()) {
//                        String fileId = entry.getKey();
//                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//                        drive.files().get(fileId).executeMediaAndDownloadTo(outputStream);
//
//                        try {
//                            BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(outputStream.toByteArray())));
//                            String line;
//                            while ((line = reader.readLine()) != null) {
//                                Log.i(TAG, ">>> " + line);
//                            }
//                            reader.close();
//                        } catch (FileNotFoundException e) {
//                            e.printStackTrace();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                }
//
//                if (cmd == 5) {
//                    // query
//                    String filename = "Testing - Four";
//                    String pageToken = null;
//                    do {
//                        FileList result = drive.files().list()
////                                .setQ("name contains '" + filename + "'")
//                                .setQ("name='" + filename + "'")
//                                .setSpaces("drive")
//                                .setFields("nextPageToken, files(id, name)")
//                                .setPageToken(pageToken)
//                                .execute();
//                        for (com.google.api.services.drive.model.File f: result.getFiles()) {
//                            System.out.printf("Found file: %s (%s)\n", f.getName(), f.getId());
//                        }
//                        pageToken = result.getNextPageToken();
//                    } while (pageToken != null);
//                    System.out.println(pageToken);
//                }
//
//                System.out.println("skdfjlkkkkkkkkkkkkkk");
//
//
//            } catch (Exception ioe) {
//                ioe.printStackTrace();
//            }
//
//            return "something";
//        }
//
//
//        /**
//         * Download a file's content.
//         *
//         * @param service Drive API service instance.
//         * @param file Drive File instance.
//         * @return InputStream containing the file's content if successful,
//         *         {@code null} otherwise.
//         */
//        private static InputStream downloadFile(Drive service, com.google.api.services.drive.model.File file) {
//            String link = file.getWebContentLink();
//            if (link == null || link.isEmpty()) {
//                // The file doesn't have any content stored on Drive.
//                link = file.getWebViewLink();
//            }
//            if (link == null || link.isEmpty()) {
//                // The file doesn't have any content stored on Drive.
//                return null;
//            }
//            try {
//                HttpResponse resp = service.getRequestFactory().buildGetRequest(new GenericUrl(link)).execute();
//                return resp.getContent();
//            } catch (IOException e) {
//                // An error occurred.
//                e.printStackTrace();
//                return null;
//            }
//        }
//
//        @Override
//        protected void onPostExecute(String message) {
//            //process message
//        }
//
//    }
//
//    private static Map<String, String> driveList(Drive drive) throws IOException {
//        FileList list = drive.files().list().setPageSize(10).setFields("nextPageToken, files(id, name)").execute();
//        List<com.google.api.services.drive.model.File> files = list.getFiles();
//        Map<String, String> results = new HashMap<>();
//        if (files != null && files.size() > 0) {
//            System.out.println("Files:");
//            for (com.google.api.services.drive.model.File file : files) {
//                System.out.printf("%s (%s)\n", file.getName(), file.getId());
//                results.put(file.getId(), file.getName());
//            }
//        }
//        return results;
//    }
//
//    private static final File getSampleFile() {
//
//        File localFile = null;
//        BufferedReader reader = null;
//        try {
//            InputStream resourceAsStream = TestDrive.class.getResourceAsStream("/testing.txt");
//
//            String prefix = "LocalFilePrefix";
//            String suffix = "LocalFileSuffix";
//            localFile = File.createTempFile(prefix, suffix);
//
//            FileOutputStream out = new FileOutputStream(localFile);
//            IOUtils.copy(resourceAsStream, out);
//
//
//            reader = new BufferedReader(new FileReader(localFile));
//            String text = null;
//
//            while ((text = reader.readLine()) != null) {
//                Log.i(TAG, ">> " + text);
//            }
//        } catch (FileNotFoundException fnf) {
//            fnf.printStackTrace();
//        } catch (IOException ioe) {
//            ioe.printStackTrace();
//        } finally {
//            try {
//                if (reader != null) {
//                    reader.close();
//                }
//            } catch (IOException e) {
//            }
//        }
//
//        return localFile;
//    }
//
//    public void ttt(String userId) {
//        Log.i(TAG, "--------------------------------------------");
//        try {
//
//            AsyncTask<String, Void, String> job = new TTTAsyncJob();
//            job.execute();
//
//        } catch (Exception ioe) {
//            ioe.printStackTrace();
//        }
//
//    }
//
//    /** Uploads a file using either resumable or direct media upload. */
//    private static com.google.api.services.drive.model.File uploadFile(Drive drive, boolean useDirectUpload, File uploadFile, String title)
//            throws IOException {
//
//        com.google.api.services.drive.model.File metadata = new com.google.api.services.drive.model.File();
//        metadata.setName(title);
////        metadata.setTitle(uploadFile.getName());
//
//        FileContent content = new FileContent("text/html", uploadFile);
//
//        Drive.Files.Create create = drive.files().create(metadata, content);
////        MediaHttpUploader uploader = create.getMediaHttpUploader();
////        uploader.setDirectUploadEnabled(useDirectUpload);
////        uploader.setProgressListener(new FileUploadProgressListener());
//        return create.execute();
//
////
////        Drive.Files.Insert insert = drive.files().insert(metadata, content);
////        MediaHttpUploader uploader = insert.getMediaHttpUploader();
////        uploader.setDirectUploadEnabled(useDirectUpload);
////        uploader.setProgressListener(new FileUploadProgressListener());
////        return insert.execute();
//    }
//
//    /** Updates the name of the uploaded file to have a "drivetest-" prefix. */
//    private static com.google.api.services.drive.model.File updateFileWithTestSuffix(Drive drive, String id, String name) throws IOException {
//        com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
////        fileMetadata.setTitle("drivetest-" + UPLOAD_FILE.getName());
////        fileMetadata.setTitle("drivetest-" + name);
//        fileMetadata.setName("drivetest-" + name);
//
//        Drive.Files.Update update = drive.files().update(id, fileMetadata);
//        return update.execute();
//    }
//
//    /** Downloads a file using either resumable or direct media download. */
////    private static void downloadFile(HttpTransport transport, Drive drive, boolean useDirectDownload, com.google.api.services.drive.model.File uploadedFile)
////            throws IOException {
////        // create parent directory (if necessary)
////        java.io.File parentDir = new java.io.File(DIR_FOR_DOWNLOADS);
////        if (!parentDir.exists() && !parentDir.mkdirs()) {
////            throw new IOException("Unable to create parent directory");
////        }
////        OutputStream out = new FileOutputStream(new java.io.File(parentDir, uploadedFile.getName()));
////
////        MediaHttpDownloader downloader = new MediaHttpDownloader(transport, drive.getRequestFactory().getInitializer());
////        downloader.setDirectDownloadEnabled(useDirectDownload);
////        downloader.setProgressListener(new FileDownloadProgressListener());
//////        downloader.download(new GenericUrl(uploadedFile.getDownloadUrl()), out);
////        downloader.download(new GenericUrl(uploadedFile.getWebViewLink()), out);
////    }
//
//    private static class FileDownloadProgressListener implements MediaHttpDownloaderProgressListener {
//
//        @Override
//        public void progressChanged(MediaHttpDownloader downloader) {
//            switch (downloader.getDownloadState()) {
//                case MEDIA_IN_PROGRESS:
//                    System.out.println("Download is in progress: " + downloader.getProgress());
//                    break;
//                case MEDIA_COMPLETE:
//                    System.out.println("Download is Complete!");
//                    break;
//            }
//        }
//    }
//
//    private static class FileUploadProgressListener implements MediaHttpUploaderProgressListener {
//
//        @Override
//        public void progressChanged(MediaHttpUploader uploader) throws IOException {
//            switch (uploader.getUploadState()) {
//                case INITIATION_STARTED:
//                    System.out.println("Upload Initiation has started.");
//                    break;
//                case INITIATION_COMPLETE:
//                    System.out.println("Upload Initiation is Complete.");
//                    break;
//                case MEDIA_IN_PROGRESS:
//                    System.out.println("Upload is In Progress: "
//                            + NumberFormat.getPercentInstance().format(uploader.getProgress()));
//                    break;
//                case MEDIA_COMPLETE:
//                    System.out.println("Upload is Complete!");
//                    break;
//            }
//        }
//    }
//
//    public void downloadFile() {
////        final GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(this, Arrays.asList(Drive.SCOPE_FILE));
////        credential.setSelectedAccountName(accountName);
////        final DriveFolder folder = Drive.DriveApi.getRootFolder(mGoogleApiClient);
//
////        FileOutputStream mOutput=null;
////        try {
////            GoogleAccountCredential crd = GoogleAccountCredential.usingOAuth2(this, Arrays.asList(Drive.SCOPE_FILE));
////            crd.setSelectedAccountName(accountName);
////            com.google.api.services.drive.Drive srv = new com.google.api.services.drive.Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), crd).build();
////            mInput = srv.getRequestFactory().buildGetRequest(new GenericUrl(url)).execute().getContent();
////            String outFileName = getApplicationContext().getDatabasePath(DatabaseHandler.DATABASE_NAME).getPath();
////            mOutput = new FileOutputStream(outFileName);
////            byte[] mBuffer = new byte[1024];
////            int mLength;
////            while ((mLength = mInput.read(mBuffer)) > 0) {
////                mOutput.write(mBuffer, 0, mLength);
////            }
////            mOutput.flush();
////            Log.d("TAG", "Successfully Downloaded contents");
////        } catch (IOException e) {
////            e.printStackTrace();
////        }finally {
////            try {
////                //Close the streams
////                if(mOutput != null){
////                    mOutput.close();
////                }
//////                if(mInput != null){
//////                    mInput.close();
//////                }
////            } catch (IOException e) {
////                Log.e("Tag", "failed to close databases");
////            }
////        }
//    }
//
//    private static void pp(InputStream in) {
//        try {
//            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//            String line;
//            while ((line = reader.readLine()) != null) {
//                Log.i(TAG, ">>> " + line);
//            }
//            reader.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void cat(String filename) {
//        File file = new File(getContext().getFilesDir(), filename);
//        try {
//            BufferedReader reader = new BufferedReader(new FileReader(file));
//            String line;
//            while ((line = reader.readLine()) != null) {
//                Log.i(TAG, ">>> " + line);
//            }
//            reader.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void saveToLocalFile(String filename) {
//        File file = new File(getContext().getFilesDir(), filename);
////        List<Message> messages = getMessageList();
////        FileOutputStream outputStream;
////        outputStream = getContext().openFileOutput(filename, Context.MODE_PRIVATE);
////        outputStream.write(string.getBytes());
////        outputStream.close();
//
//        try {
//            FileWriter writer = new FileWriter(file);
//            for (Message msg : getMessageList()) {
//                writer.write(msg.toString());
//                writer.write('\n');
//            }
//            writer.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private List<Message> getMessageList() {
//
//        final String[] projection = new String[] {
//                FeedContract.Entry._ID,
//                FeedContract.Entry.COLUMN_NAME_TITLE,
//                FeedContract.Entry.COLUMN_NAME_AUDIENCE,
//                FeedContract.Entry.COLUMN_NAME_NOTES,
//                FeedContract.Entry.COLUMN_NAME_POSTDATE
//        };
//
//        final String orderBy = FeedContract.Entry.COLUMN_NAME_POSTDATE;
//        Uri uri = FeedContract.Entry.CONTENT_URI; // Get all entries
//        Cursor cursor = mContentResolver.query(uri, projection, null, null, orderBy);
//        assert cursor != null;
//        List<Message> messages = new ArrayList<>();
//        while (cursor.moveToNext()) {
//            int id = cursor.getInt(0);
//            String title = cursor.getString(1);
//            String audience = cursor.getString(2);
//            String notes = cursor.getString(3);
//            long postdate = cursor.getLong(4);
//            messages.add(new Message(title, new Date(postdate), audience, notes));
//
//        }
//        cursor.close();
//        return messages;
//    }
//}
