package com.scowluga.android.rcacc.sync.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FeedProvider extends ContentProvider {

    public static final String TAG = "FeedProvider";

    FeedDatabase mDatabaseHelper;

    /**
     * Content authority for this provider.
     */
    private static final String AUTHORITY = FeedContract.CONTENT_AUTHORITY;

    // The constants below represent individual URI routes, as IDs. Every URI pattern recognized by
    // this ContentProvider is defined using sUriMatcher.addURI(), and associated with one of these IDs.
    //
    // When a incoming URI is run through sUriMatcher, it will be tested against the defined
    // URI patterns, and the corresponding route ID will be returned.

    // URI ID for route: /entries
    public static final int ROUTE_ENTRIES = 1;

    // URI ID for route: /entries/{ID}
    public static final int ROUTE_ENTRIES_ID = 2;

     // UriMatcher, used to decode incoming URIs.
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(AUTHORITY, "entries", ROUTE_ENTRIES);
        sUriMatcher.addURI(AUTHORITY, "entries/*", ROUTE_ENTRIES_ID);
    }

    @Override
    public boolean onCreate() {
        final Context context = getContext();
        mDatabaseHelper = new FeedDatabase(context);
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        mDatabaseHelper.onCreate(db);
        return true;
    }

    /**
     * Determine the mime type for entries returned by a given URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ROUTE_ENTRIES:
                return FeedContract.Entry.CONTENT_TYPE;
            case ROUTE_ENTRIES_ID:
                return FeedContract.Entry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    /**
     * Perform a database query by URI.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Log.i(TAG, "query ...");
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        SelectionBuilder builder = new SelectionBuilder();
        int uriMatch = sUriMatcher.match(uri);
        switch (uriMatch) {
            case ROUTE_ENTRIES_ID:
                // Return a single entry, by ID.
                String id = uri.getLastPathSegment();
                builder.where(FeedContract.Entry._ID + "=?", id);
            case ROUTE_ENTRIES:
                // Return all known entries.
                builder.table(FeedContract.Entry.TABLE_NAME)
                       .where(selection, selectionArgs);
                Cursor c = builder.query(db, projection, sortOrder);
                // Note: Notification URI must be manually set here for loaders to correctly
                // register ContentObservers.
                Context ctx = getContext();
                assert ctx != null;
                c.setNotificationUri(ctx.getContentResolver(), uri);
                return c;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    /**
     * Insert a new entry into the database.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        Log.i(TAG, "insert ...");

        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        assert db != null;
        final int match = sUriMatcher.match(uri);
        Uri result;
        switch (match) {
            case ROUTE_ENTRIES:
                long id = db.insertOrThrow(FeedContract.Entry.TABLE_NAME, null, values);
                result = Uri.parse(FeedContract.Entry.CONTENT_URI + "/" + id);
                break;
            case ROUTE_ENTRIES_ID:
                throw new UnsupportedOperationException("Insert not supported on URI: " + uri);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Send broadcast to registered ContentObservers, to refresh UI.
        Context ctx = getContext();
        assert ctx != null;
        ctx.getContentResolver().notifyChange(uri, null, false);
        return result;
    }

    /**
     * Delete an entry by database by URI.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        Log.i(TAG, "delete ...");

        SelectionBuilder builder = new SelectionBuilder();
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int count;
        switch (match) {
            case ROUTE_ENTRIES:
                count = builder.table(FeedContract.Entry.TABLE_NAME)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;
            case ROUTE_ENTRIES_ID:
                String id = uri.getLastPathSegment();
                count = builder.table(FeedContract.Entry.TABLE_NAME)
                       .where(FeedContract.Entry._ID + "=?", id)
                       .where(selection, selectionArgs)
                       .delete(db);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Send broadcast to registered ContentObservers, to refresh UI.
        Context ctx = getContext();
        assert ctx != null;
        ctx.getContentResolver().notifyChange(uri, null, false);
        return count;
    }

    /**
     * Update an etry in the database by URI.
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        Log.i(TAG, "update ...");

        SelectionBuilder builder = new SelectionBuilder();
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int count;
        switch (match) {
            case ROUTE_ENTRIES:
                count = builder.table(FeedContract.Entry.TABLE_NAME)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;
            case ROUTE_ENTRIES_ID:
                String id = uri.getLastPathSegment();
                count = builder.table(FeedContract.Entry.TABLE_NAME)
                        .where(FeedContract.Entry._ID + "=?", id)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        Context ctx = getContext();
        assert ctx != null;
        ctx.getContentResolver().notifyChange(uri, null, false);
        return count;
    }

    /**
     * SQLite backend for @{link FeedProvider}.
     *
     * Provides access to an disk-backed, SQLite datastore which is utilized by FeedProvider. This
     * database should never be accessed by other parts of the application directly.
     */
    static class FeedDatabase extends SQLiteOpenHelper {

        /** Schema version. */
        public static final int DATABASE_VERSION = 1;
        /** Filename for SQLite file. */
        public static final String DATABASE_NAME = "feed.db";

        private static final String TYPE_TEXT = " TEXT";
        private static final String TYPE_INTEGER = " INTEGER";
        private static final String COMMA_SEP = ",";

        /** SQL statement to create "message" table. */
        private static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE IF NOT EXISTS " + FeedContract.Entry.TABLE_NAME + " (" +
                        FeedContract.Entry._ID + " INTEGER PRIMARY KEY," +
                        FeedContract.Entry.COLUMN_NAME_TITLE    + TYPE_TEXT + COMMA_SEP +
                        FeedContract.Entry.COLUMN_NAME_AUDIENCE + TYPE_TEXT + COMMA_SEP +
                        FeedContract.Entry.COLUMN_NAME_NOTES + TYPE_TEXT + COMMA_SEP +
                        FeedContract.Entry.COLUMN_NAME_STATUS + TYPE_TEXT + COMMA_SEP +
                        FeedContract.Entry.COLUMN_NAME_POSTDATE + TYPE_INTEGER + ")";

        /** SQL statement to drop "entry" table. */
        private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + FeedContract.Entry.TABLE_NAME;

        public FeedDatabase(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }
    }

    /**
     * Helper for building selection clauses for {@link SQLiteDatabase}. Each
     * appended clause is combined using {@code AND}. This class is <em>not</em>
     * thread safe.
     */
    private static class SelectionBuilder {

        private String mTable = null;
    //    private Map<String, String> mProjectionMap = Maps.newHashMap();
        private Map<String, String> mProjectionMap = new HashMap<String, String>();
        private StringBuilder mSelection = new StringBuilder();
        private ArrayList<String> mSelectionArgs = new ArrayList();

        /**
         * Reset any internal state, allowing this builder to be recycled.
         */
        public SelectionBuilder reset() {
            mTable = null;
            mSelection.setLength(0);
            mSelectionArgs.clear();
            return this;
        }

        /**
         * Append the given selection clause to the internal state. Each clause is
         * surrounded with parenthesis and combined using {@code AND}.
         */
        public SelectionBuilder where(String selection, String... selectionArgs) {
            if (TextUtils.isEmpty(selection)) {
                if (selectionArgs != null && selectionArgs.length > 0) {
                    throw new IllegalArgumentException(
                            "Valid selection required when including arguments=");
                }

                // Shortcut when clause is empty
                return this;
            }

            if (mSelection.length() > 0) {
                mSelection.append(" AND ");
            }

            mSelection.append("(").append(selection).append(")");
            if (selectionArgs != null) {
                Collections.addAll(mSelectionArgs, selectionArgs);
            }

            return this;
        }

        public SelectionBuilder table(String table) {
            mTable = table;
            return this;
        }

        private void assertTable() {
            if (mTable == null) {
                throw new IllegalStateException("Table not specified");
            }
        }

        public SelectionBuilder mapToTable(String column, String table) {
            mProjectionMap.put(column, table + "." + column);
            return this;
        }

        public SelectionBuilder map(String fromColumn, String toClause) {
            mProjectionMap.put(fromColumn, toClause + " AS " + fromColumn);
            return this;
        }

        /**
         * Return selection string for current internal state.
         *
         * @see #getSelectionArgs()
         */
        public String getSelection() {
            return mSelection.toString();
        }

        /**
         * Return selection arguments for current internal state.
         *
         * @see #getSelection()
         */
        public String[] getSelectionArgs() {
            return mSelectionArgs.toArray(new String[mSelectionArgs.size()]);
        }

        private void mapColumns(String[] columns) {
            for (int i = 0; i < columns.length; i++) {
                final String target = mProjectionMap.get(columns[i]);
                if (target != null) {
                    columns[i] = target;
                }
            }
        }

        @Override
        public String toString() {
            return "SelectionBuilder[table=" + mTable + ", selection=" + getSelection()
                    + ", selectionArgs=" + Arrays.toString(getSelectionArgs()) + "]";
        }

        /**
         * Execute query using the current internal state as {@code WHERE} clause.
         */
        public Cursor query(SQLiteDatabase db, String[] columns, String orderBy) {
            return query(db, columns, null, null, orderBy, null);
        }

        /**
         * Execute query using the current internal state as {@code WHERE} clause.
         */
        public Cursor query(SQLiteDatabase db, String[] columns, String groupBy,
                            String having, String orderBy, String limit) {
            assertTable();
            if (columns != null) mapColumns(columns);
            Log.v(TAG, "query(columns=" + Arrays.toString(columns) + ") " + this);
            return db.query(mTable, columns, getSelection(), getSelectionArgs(), groupBy, having,
                    orderBy, limit);
        }

        /**
         * Execute update using the current internal state as {@code WHERE} clause.
         */
        public int update(SQLiteDatabase db, ContentValues values) {
            assertTable();
            Log.v(TAG, "update() " + this);
            return db.update(mTable, values, getSelection(), getSelectionArgs());
        }

        /**
         * Execute delete using the current internal state as {@code WHERE} clause.
         */
        public int delete(SQLiteDatabase db) {
            assertTable();
            Log.v(TAG, "delete() " + this);
            return db.delete(mTable, getSelection(), getSelectionArgs());
        }
    }
}
