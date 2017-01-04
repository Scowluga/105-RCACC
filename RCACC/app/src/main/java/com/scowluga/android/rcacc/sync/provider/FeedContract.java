package com.scowluga.android.rcacc.sync.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Field and table name constants for
 * {@link com.scowluga.android.rcacc.sync.provider.FeedProvider}.
 */
public class FeedContract {
    private FeedContract() {
    }

    /**
     * Content provider authority.
     */
    public static final String CONTENT_AUTHORITY = "com.scowluga.android.rcacc";

    /**
     * Base URI. (content://com.scowluga.android.rcacc)
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Path component for "entry"-type resources..
     */
    private static final String PATH_ENTRIES = "entries";

    /**
     * Columns supported by "entries" records.
     */
    public static class Entry implements BaseColumns {
        /**
         * MIME type for lists of entries.
         */
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/helloworld.content_type";
        /**
         * MIME type for individual entries.
         */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/helloworld.item_type";

        /**
         * Fully qualified URI for "entry" resources.
         */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_ENTRIES).build();

        /**
         * Table name where records are stored for "entry" resources.
         */
        public static final String TABLE_NAME = "message";

        public static final String COLUMN_NAME_ENTRY_ID = "message_id";

        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_AUDIENCE = "audience";
        public static final String COLUMN_NAME_NOTES = "notes";
        public static final String COLUMN_NAME_POSTDATE = "postdate";
        public static final String COLUMN_NAME_STATUS = "status";

        public static final String STATUS_NEW = "new";
        public static final String STATUS_DELETE = "delete";
    }
}