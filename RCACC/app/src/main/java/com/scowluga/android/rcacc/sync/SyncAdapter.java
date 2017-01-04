package com.scowluga.android.rcacc.sync;

import android.accounts.Account;
import android.app.AlertDialog;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.drive.DriveId;
import com.google.api.services.oauth2.Oauth2;
import com.scowluga.android.rcacc.Main.MainActivity;
import com.scowluga.android.rcacc.Message.Message;
import com.scowluga.android.rcacc.sync.provider.FeedContract;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static com.scowluga.android.rcacc.sync.provider.FeedContract.Entry.STATUS_DELETE;
import static com.scowluga.android.rcacc.sync.provider.FeedContract.Entry.STATUS_NEW;

/**
 * Define a sync adapter for the app.
 *
 * <p>This class is instantiated in {@link SyncService}, which also binds SyncAdapter to the system.
 * SyncAdapter should only be initialized in SyncService, never anywhere else.
 *
 * <p>The system calls onPerformSync() via an RPC call through the IBinder object supplied by SyncService.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String TAG = "SyncAdapter";

    /**
     * Content resolver, for performing database operations.
     */
    private final ContentResolver mContentResolver;
    private final ContentObserver mContentObserver = new ContentObserver(null) {
        @Override
        public boolean deliverSelfNotifications() {
//            return super.deliverSelfNotifications();
            if (MainActivity.debugger) {
                String information = "Sync Complete";

            }

            return true;
        }

        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
        }
    };

    private final ConcurrentHashMap<String, DriveId> mTitleToDriveIdMap = new ConcurrentHashMap<>();

    /**
     * Project used when querying content provider. Returns all known fields.
     */
    public static final String[] PROJECTION = new String[] {
            FeedContract.Entry._ID,
            FeedContract.Entry.COLUMN_NAME_TITLE,
            FeedContract.Entry.COLUMN_NAME_AUDIENCE,
            FeedContract.Entry.COLUMN_NAME_NOTES,
            FeedContract.Entry.COLUMN_NAME_POSTDATE,
            FeedContract.Entry.COLUMN_NAME_STATUS
    };

    // Constants representing column positions from PROJECTION.
    public static final int COLUMN_ID = 0;
    public static final int COLUMN_TITLE = 1;
    public static final int COLUMN_AUDIENCE = 2;
    public static final int COLUMN_NOTES = 3;
    public static final int COLUMN_POSTDATE = 4;
    public static final int COLUMN_STATUS = 5;


    /**
     * Constructor. Obtains handle to content resolver for later use.
     */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);

        mContentResolver = context.getContentResolver();
//        mContentResolver.registerContentObserver(FeedContract.Entry.CONTENT_URI, false, mContentObserver);
        try {
            GoogleDriveDataStore.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructor. Obtains handle to content resolver for later use.
     */
    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);

        mContentResolver = context.getContentResolver();
//        mContentResolver.registerContentObserver(FeedContract.Entry.CONTENT_URI, false, mContentObserver);
        try {
            GoogleDriveDataStore.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Called by the Android system in response to a request to run the sync adapter. The work
     * required to read data from the network, decode it, and store it in the content provider is
     * done here. Extending AbstractThreadedSyncAdapter ensures that all methods within SyncAdapter
     * run on a background thread. For this reason, blocking I/O and other long-running tasks can be
     * run <em>in situ</em>, and you don't have to set up a separate thread for them.
     .
     *
     * <p>This is where we actually perform any work required to perform a sync.
     * {@link AbstractThreadedSyncAdapter} guarantees that this will be called on a non-UI thread,
     * so it is safe to peform blocking I/O here.
     *
     * <p>The syncResult argument allows you to pass information back to the method that triggered the sync.
     */
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        Log.i(TAG, "Beginning network synchronization");
        try {
            InputStream stream = null;
            try {
                stream = GoogleDriveDataStore.getInstance().download();

                final List<Message> messagesRemote = RemoteFeedProcessor.decode(stream);
                updateLocalFeedData(messagesRemote, syncResult);
                // Makes sure that the InputStream is closed after the app is finished using it.
            } finally {
                if (stream != null) {
                    stream.close();
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "Error updating database: " + e.toString());
            syncResult.databaseError = true;
            return;
        }
        Log.i(TAG, "Network synchronization complete");
    }

    /**
     * Read XML from an input stream, storing it into the content provider.
     *
     * <p>This is where incoming data is persisted, committing the results of a sync. In order to
     * minimize (expensive) disk operations, we compare incoming data with what's already in our
     * database, and compute a merge. Only changes (insert/update/delete) will result in a database write.
     *
     * <p>As an additional optimization, we use a batch operation to perform all database writes at once.
     *
     * <p>Merge strategy:
     * 1. Get cursor to all items in feed<br/>
     * 2. For each item, check if it's in the incoming data.<br/>
     *    a. YES: Remove from "incoming" list. Check if data has mutated, if so, perform database UPDATE.<br/>
     *    b. NO: Schedule DELETE from database.<br/>
     * (At this point, incoming database only contains missing items.)<br/>
     * 3. For any items remaining in incoming list, ADD to database.
     */


    public void updateLocalFeedData(final List<Message> messagesRemote, final SyncResult syncResult)
            throws IOException, RemoteException, OperationApplicationException, ParseException {

        final ContentResolver contentResolver = getContext().getContentResolver();

        ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();

        // Build hash table of incoming entries
        HashMap<String, Message> remoteMessageMap = new HashMap<>();

        for (Message msg : messagesRemote) {
            remoteMessageMap.put(msg.getNotes(), msg);
        }
        boolean hasWifi = !remoteMessageMap.isEmpty();
        List<Message> toBeDeleted = new ArrayList<>();
        List<Message> toBeAdded = new ArrayList<>();

        // Get list of all items
        Uri uri = FeedContract.Entry.CONTENT_URI; // Get all entries
        boolean testing = false;
        if (testing) {
            contentResolver.delete(uri, null, null);
        }
        if (testing) {
            List<Message> resetMessages = new ArrayList<>();
            resetMessages.add(new Message("Welcome!", new Date(), "All",
                    "Hello and welcome to the official 105 RCACC application! Thank you for downloading the application, and we hope it is of convenience to you. \n" +
                            "\n" +
                            "For all cadets or parents, this application will give you quick access to not only the functions on our website, but also a newsfeed with all the latest information. For those interested, take a look around and learn about your future cadet corps! \n" +
                            "\n" +
                            "If there are any suggestions or improvements to the app, please select 'info' and fill out the contact form. Please rate this app, and enjoy your use! "));
            try {
                GoogleDriveDataStore.getInstance().upload(RemoteFeedProcessor.encode(resetMessages));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        Cursor c = contentResolver.query(uri, PROJECTION, null, null, null);
        int count = c.getCount();
        assert c != null;
        // Find stale data
        while (c.moveToNext()) {
            syncResult.stats.numEntries++;
            int id = c.getInt(COLUMN_ID);
            String title = c.getString(COLUMN_TITLE);
            String audience = c.getString(COLUMN_AUDIENCE);
            String notes = c.getString(COLUMN_NOTES);
            long postdate = c.getLong(COLUMN_POSTDATE);
            String status = c.getString(COLUMN_STATUS);
            Message match = remoteMessageMap.get(notes);
            if (match != null) { // Local and Remote have a message with same notes
                if (status != null && status.equals(STATUS_DELETE)) {
                    // Delete from remote Database
                    toBeDeleted.add(new Message(title, new Date(postdate), audience, notes));
                } else {
                    // Entry exists. Remove from entry map to prevent insert later.
                    remoteMessageMap.remove(notes);
                    // Check to see if the entry needs to be updated
                    Uri existingUri = FeedContract.Entry.CONTENT_URI.buildUpon()
                            .appendPath(Integer.toString(id)).build();
                    if (!match.getAudience().equals(audience)
                            || !match.getTitle().equals(title)
                            || postdate != match.getDate().getTime()) {

                        // Update existing record
                        Log.i(TAG, "Scheduling update: " + existingUri);
                        batch.add(ContentProviderOperation.newUpdate(existingUri)
                                .withValue(FeedContract.Entry.COLUMN_NAME_TITLE, match.getTitle())
                                .withValue(FeedContract.Entry.COLUMN_NAME_AUDIENCE, match.getAudience())
                                .withValue(FeedContract.Entry.COLUMN_NAME_POSTDATE, match.getDate().getTime())
                                .build());
                        syncResult.stats.numUpdates++;
                    } else { // Same messages
                        Log.i(TAG, "No action: " + existingUri);
                    }
                }
            } else if (hasWifi) {

                if (status != null && status.equals(STATUS_NEW)) {
                    // Add to Remote Database
                    toBeAdded.add(new Message(title, new Date(postdate), audience, notes));
                    // update status in local database
                    Uri updateUri = FeedContract.Entry.CONTENT_URI.buildUpon()
                            .appendPath(Integer.toString(id)).build();

                    batch.add(ContentProviderOperation.newUpdate(updateUri)
                            .withValue(FeedContract.Entry.COLUMN_NAME_STATUS, null)
                            .build());

                } else {
                    // Entry doesn't exist. Remove it from the database.
                    Uri deleteUri = FeedContract.Entry.CONTENT_URI.buildUpon()
                            .appendPath(Integer.toString(id)).build();
                    Log.i(TAG, "Scheduling delete: " + deleteUri);
                    batch.add(ContentProviderOperation.newDelete(deleteUri).build());
                    syncResult.stats.numDeletes++;
                }
            } else { // No Wifi
                // Do nothing
            }
        }
        c.close();

        // Add new items
        for (Message msg : remoteMessageMap.values()) {
            Log.i(TAG, "Scheduling insert: entry_id=" + msg.getTitle());
            batch.add(ContentProviderOperation.newInsert(FeedContract.Entry.CONTENT_URI)
                    .withValue(FeedContract.Entry.COLUMN_NAME_TITLE, msg.getTitle())
                    .withValue(FeedContract.Entry.COLUMN_NAME_AUDIENCE, msg.getAudience())
                    .withValue(FeedContract.Entry.COLUMN_NAME_NOTES, msg.getNotes())
                    .withValue(FeedContract.Entry.COLUMN_NAME_POSTDATE, msg.getDate().getTime())
                    .build());
            syncResult.stats.numInserts++;
        }
        Log.i(TAG, "Merge solution ready. Applying batch update");
        mContentResolver.applyBatch(FeedContract.CONTENT_AUTHORITY, batch);
        // This sample doesn't support uploads, but if *your* code does, make sure you set
        // syncToNetwork=false in the line above to prevent duplicate syncs.


        boolean uploadSuccess = false;
        if (hasWifi) {
            List<Message> toBeUploaded = new ArrayList<>(toBeAdded);
            for (Message m : messagesRemote) {
                if (!toBeDeleted.contains(m)) {
                    toBeUploaded.add(m);
                }
            }
            try {
                GoogleDriveDataStore.getInstance().upload(RemoteFeedProcessor.encode(toBeUploaded));
                uploadSuccess = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (uploadSuccess || !hasWifi) {
            mContentResolver.notifyChange(
                    FeedContract.Entry.CONTENT_URI, // URI where data was modified
                    null,                           // No local observer
                    false);                         // IMPORTANT: Do not sync to network
        }
    }


// ------------------------------------------------------------------------------------------------

//    // Old Merge Formula
//    public void updateLocalFeedData_old(final InputStream stream, final SyncResult syncResult)
//            throws IOException, RemoteException, OperationApplicationException, ParseException {
//
//        final RemoteFeedProcessor feedParser = new RemoteFeedProcessor();
//        final ContentResolver contentResolver = getContext().getContentResolver();
//
//        Log.i(TAG, "Parsing stream as Atom feed");
//        final List<Message> messages = feedParser.decode(stream);
//        Log.i(TAG, "Parsing complete. Found " + messages.size() + " entries");
//
//
//        ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();
//
//        // Build hash table of incoming entries
//        HashMap<String, Message> entryMap = new HashMap<>();
//        for (Message msg : messages) {
//            entryMap.put(msg.getTitle(), msg);
//        }
//
//        // Get list of all items
//        Log.i(TAG, "Fetching local entries for merge");
//        Uri uri = FeedContract.Entry.CONTENT_URI; // Get all entries
//        Cursor c = contentResolver.query(uri, PROJECTION, null, null, null);
//        assert c != null;
//        Log.i(TAG, "Found " + c.getCount() + " local entries. Computing merge solution...");
//
//        // Find stale data
//        int id;
//        String title;
//        String audience;
//        String notes;
//        long postdate;
//        while (c.moveToNext()) {
//            syncResult.stats.numEntries++;
//            id = c.getInt(COLUMN_ID);
//            title = c.getString(COLUMN_TITLE);
//            audience = c.getString(COLUMN_AUDIENCE);
//            notes= c.getString(COLUMN_NOTES);
//            postdate = c.getLong(COLUMN_POSTDATE);
//            Message match = entryMap.get(title);
//            if (match != null) {
//                // Entry exists. Remove from entry map to prevent insert later.
//                entryMap.remove(title);
//                // Check to see if the entry needs to be updated
//                Uri existingUri = FeedContract.Entry.CONTENT_URI.buildUpon()
//                        .appendPath(Integer.toString(id)).build();
//                if (!match.getAudience().equals(audience)
//                        || !match.getNotes().equals(notes)
//                        || postdate != match.getDate().getTime()) {
//
//                    // Update existing record
//                    Log.i(TAG, "Scheduling update: " + existingUri);
//                    batch.add(ContentProviderOperation.newUpdate(existingUri)
//                            .withValue(FeedContract.Entry.COLUMN_NAME_TITLE, title)
//                            .withValue(FeedContract.Entry.COLUMN_NAME_AUDIENCE, audience)
//                            .withValue(FeedContract.Entry.COLUMN_NAME_NOTES, notes)
//                            .withValue(FeedContract.Entry.COLUMN_NAME_POSTDATE, postdate)
//                            .build());
//                    syncResult.stats.numUpdates++;
//                } else {
//                    Log.i(TAG, "No action: " + existingUri);
//                }
//            } else {
//                // Entry doesn't exist. Remove it from the database.
//                Uri deleteUri = FeedContract.Entry.CONTENT_URI.buildUpon()
//                        .appendPath(Integer.toString(id)).build();
//                Log.i(TAG, "Scheduling delete: " + deleteUri);
//                batch.add(ContentProviderOperation.newDelete(deleteUri).build());
//                syncResult.stats.numDeletes++;
//            }
//        }
//        c.close();
//
//        // Add new items
//        for (Message msg : entryMap.values()) {
//            Log.i(TAG, "Scheduling insert: entry_id=" + msg.getTitle());
//            batch.add(ContentProviderOperation.newInsert(FeedContract.Entry.CONTENT_URI)
//                    .withValue(FeedContract.Entry.COLUMN_NAME_TITLE, msg.getTitle())
//                    .withValue(FeedContract.Entry.COLUMN_NAME_AUDIENCE, msg.getAudience())
//                    .withValue(FeedContract.Entry.COLUMN_NAME_NOTES, msg.getNotes())
//                    .withValue(FeedContract.Entry.COLUMN_NAME_POSTDATE, msg.getDate().getTime())
//                    .build());
//            syncResult.stats.numInserts++;
//        }
//        Log.i(TAG, "Merge solution ready. Applying batch update");
//        mContentResolver.applyBatch(FeedContract.CONTENT_AUTHORITY, batch);
//        mContentResolver.notifyChange(
//                FeedContract.Entry.CONTENT_URI, // URI where data was modified
//                null,                           // No local observer
//                false);                         // IMPORTANT: Do not sync to network
//        // This sample doesn't support uploads, but if *your* code does, make sure you set
//        // syncToNetwork=false in the line above to prevent duplicate syncs.
//    }

}
