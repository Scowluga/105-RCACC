package com.scowluga.android.rcacc.Message;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.scowluga.android.rcacc.sync.provider.FeedContract;
import com.scowluga.android.rcacc.sync.provider.FeedProvider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.scowluga.android.rcacc.sync.SyncAdapter.COLUMN_AUDIENCE;
import static com.scowluga.android.rcacc.sync.SyncAdapter.COLUMN_ID;
import static com.scowluga.android.rcacc.sync.SyncAdapter.COLUMN_NOTES;
import static com.scowluga.android.rcacc.sync.SyncAdapter.COLUMN_POSTDATE;
import static com.scowluga.android.rcacc.sync.SyncAdapter.COLUMN_STATUS;
import static com.scowluga.android.rcacc.sync.SyncAdapter.COLUMN_TITLE;
import static com.scowluga.android.rcacc.sync.SyncAdapter.PROJECTION;
import static com.scowluga.android.rcacc.sync.provider.FeedContract.Entry.STATUS_DELETE;
import static com.scowluga.android.rcacc.sync.provider.FeedContract.Entry.STATUS_NEW;

/**
 * Created by robertlu on 2016-10-31.
 */

public class MessageProvider {

    //The format for this code's dates
    public static final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");

    public static List<Message> getInfo(Context ctx){ //called by MessageDisplay

        List<Message> messageList = new ArrayList<>();

        final ContentResolver contentResolver = ctx.getContentResolver();

        Uri uri = FeedContract.Entry.CONTENT_URI; // Get all entries
        Cursor c = contentResolver.query(uri, PROJECTION, null, null, FeedContract.Entry.COLUMN_NAME_POSTDATE + " desc");
        assert c != null;

        // Find stale data
        while (c.moveToNext()) {
            int id = c.getInt(COLUMN_ID);
            String title = c.getString(COLUMN_TITLE);
            String audience = c.getString(COLUMN_AUDIENCE);
            String notes = c.getString(COLUMN_NOTES);
            long postdate = c.getLong(COLUMN_POSTDATE);
            String status = c.getString(COLUMN_STATUS);
            if (status == null || status.equals(STATUS_NEW)) {
                messageList.add(new Message(title, new Date(postdate), audience, notes, id));
            }
        }
        return messageList;
    };

    public static void addMessage(Context context, Message m) {

        final ContentResolver contentResolver = context.getContentResolver();
        Uri uri = FeedContract.Entry.CONTENT_URI; // Get all entries

        ContentValues v = new ContentValues();
        v.put(FeedContract.Entry.COLUMN_NAME_TITLE, m.getTitle());
        v.put(FeedContract.Entry.COLUMN_NAME_AUDIENCE, m.getAudience());
        v.put(FeedContract.Entry.COLUMN_NAME_POSTDATE, m.getDate().getTime());
        v.put(FeedContract.Entry.COLUMN_NAME_NOTES, m.getNotes());
        v.put(FeedContract.Entry.COLUMN_NAME_STATUS, STATUS_NEW);

        Uri insert = contentResolver.insert(uri, v);
    }

    public static void deleteMessage(Context context, Message m) {

        int id = m.get_id();
        Uri updateUri = FeedContract.Entry.CONTENT_URI.buildUpon()
                .appendPath(Integer.toString(id)).build();

        final ContentResolver contentResolver = context.getContentResolver();

        ContentValues v = new ContentValues();
        v.put(FeedContract.Entry.COLUMN_NAME_STATUS, STATUS_DELETE);

        int update = contentResolver.update(updateUri, v, null, null);
    }

    // Used in Events.
    public static String grandDelimiter = "XXXXX"; //Grand Delimiter for all 'Messages'
}
