package com.scowluga.android.rcacc.sync;

import android.content.Context;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

/**
 * Created by User on 12/30/2016.
 */

public class GoogleDriveDataStore implements RemoteDataStore {

    // TODO: Generate Private key.
    private static final String PRIVATE_KEY = "";

    private static GoogleDriveDataStore INSTANCE = null;

    private static final String APP_NAME = "RCACC 105 Cadet";
    private static final String PRIVATE_KEY_FILE = "/secret.json";
    private static final String DATA_FILE_NAME = "RCACC105-Events";

    private static final String FEED_TYPE = "text/plain";

    private final String mGoogleDriveFileId; // 0Bw9Z7mgSITv2dW45V1lWNzJMbk0
    private final String mGoogleDriveFileName = DATA_FILE_NAME;

    private final Drive mDriveService;

    private GoogleDriveDataStore() throws IOException {

        InputStream is = new ByteArrayInputStream(PRIVATE_KEY.getBytes());
        final GoogleCredential credential = GoogleCredential.fromStream(is)
                .createScoped(Collections.singleton(DriveScopes.DRIVE));

        mDriveService = new Drive.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance(), credential)
                .setApplicationName(APP_NAME).build();

        List<File> gFiles = getGoogleDriveFiles(mDriveService, mGoogleDriveFileName);
        final String fileId;
        if (gFiles == null || gFiles.isEmpty()) {
            File gFile = uploadFileToGoogleDrive(mDriveService, mGoogleDriveFileName, null); // empty file
            fileId = gFile.getId();
            assert fileId != null && !fileId.isEmpty();
        } else {
            fileId = gFiles.get(0).getId(); // XXX
            int howmany = gFiles.size();
            for (int i = 1; i < howmany; i++) {
                mDriveService.files().delete(gFiles.get(i).getId()).execute();
            }
        }
        mGoogleDriveFileId = fileId;
    }

    public static GoogleDriveDataStore getInstance() throws Exception {
        if (INSTANCE == null) {
            INSTANCE = new GoogleDriveDataStore();
        }
        return INSTANCE;
    }

    @Override
    public InputStream download() throws IOException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        mDriveService.files().get(mGoogleDriveFileId).executeMediaAndDownloadTo(out);
        return new ByteArrayInputStream(out.toByteArray());
    }

    @Override
    public void upload(java.io.File feed) throws IOException {

        File gFile = uploadFileToGoogleDrive(mDriveService, mGoogleDriveFileName, feed);
        if (!mGoogleDriveFileName.equals(gFile.getName())) {
            throw new IOException("Expect Google Drive File Name '" + mGoogleDriveFileName
                    + "', but got '" + gFile.getName() + "'.");
        }
    }

    private static File uploadFileToGoogleDrive(Drive drive, String gDriveFileName, java.io.File dataFile) throws IOException {

        File metadata = new File();
        metadata.setName(gDriveFileName);

        if (dataFile == null) {
            dataFile = java.io.File.createTempFile(APP_NAME, ".empty");
        }
        final FileContent content = new FileContent(FEED_TYPE, dataFile);

        final List<File> gFiles = getGoogleDriveFiles(drive, gDriveFileName);
        if (gFiles == null || gFiles.isEmpty()) {
            // create
            return drive.files().create(metadata, content).execute();
//        MediaHttpUploader uploader = create.getMediaHttpUploader();
//        uploader.setDirectUploadEnabled(useDirectUpload);
//        uploader.setProgressListener(new FileUploadProgressListener());
        }
        // update
        String fileId = gFiles.get(0).getId(); // XXX
        return drive.files().update(fileId, null, content).execute();
    }

    private static List<File> getGoogleDriveFiles(Drive drive, String gDriveFileName) throws IOException {

        FileList result = drive.files().list()
                .setQ("name='" + gDriveFileName + "'")
                .setSpaces("drive")
                .setFields("nextPageToken, files(id, name)")
                .execute();

        return result.getFiles();
    }
}
