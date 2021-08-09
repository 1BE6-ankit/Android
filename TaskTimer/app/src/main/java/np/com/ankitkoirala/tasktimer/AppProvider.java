package np.com.ankitkoirala.tasktimer;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AppProvider extends ContentProvider {

    private static final String TAG = "AppProvider";

    private static final int TASKS = 100;
    private static final int TASKS_ID = 101;

    private static final int TIMINGS = 200;
    private static final int TIMINGS_ID = 201;

    private static final int TASKS_DURATIONS = 400;
    private static final int TASKS_DURATIONS_ID = 401;

    public static final String AUTHORITY = "np.com.ankitkoirala.tasktimer.provider";
    public static final Uri CONTENT_AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

    private DBHelper dbHelper;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, TasksContract.TABLE_NAME, TASKS);
        uriMatcher.addURI(AUTHORITY, TasksContract.TABLE_NAME + "/#", TASKS_ID);

        uriMatcher.addURI(AUTHORITY, TimingsContract.TABLE_NAME, TIMINGS);
        uriMatcher.addURI(AUTHORITY, TimingsContract.TABLE_NAME + "/#", TIMINGS_ID);

        uriMatcher.addURI(AUTHORITY, DurationsContract.TABLE_NAME, TASKS_DURATIONS);
        uriMatcher.addURI(AUTHORITY, DurationsContract.TABLE_NAME + "/#", TASKS_DURATIONS_ID);
    }


    @Override
    public boolean onCreate() {
        dbHelper = DBHelper.getInstance(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        switch (uriMatcher.match(uri)) {
            case TASKS:
                queryBuilder.setTables(TasksContract.TABLE_NAME);
                break;

            case TASKS_ID:
                queryBuilder.setTables(TasksContract.TABLE_NAME);
                long taskId = TasksContract.getTaskId(uri);
                queryBuilder.appendWhere(TasksContract.Columns.TASKS_ID + "=" + taskId);
                break;

            case TIMINGS:
                queryBuilder.setTables(TimingsContract.TABLE_NAME);
                break;

            case TIMINGS_ID:
                queryBuilder.setTables(TimingsContract.TABLE_NAME);
                long timingId = TimingsContract.getTimingId(uri);
                queryBuilder.appendWhere(TimingsContract.Columns.TIMINGS_ID + "=" + timingId);
                break;

            case TASKS_DURATIONS:
                queryBuilder.setTables(DurationsContract.TABLE_NAME);
                break;

            case TASKS_DURATIONS_ID:
                queryBuilder.setTables(DurationsContract.TABLE_NAME);
                long durationId = DurationsContract.getDurationId(uri);
                queryBuilder.appendWhere(TimingsContract.Columns.TIMINGS_ID + "=" + durationId);
                break;

            default:
                throw new IllegalArgumentException("This is an unknown URI: " + uri);
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)) {
            case TASKS:
                return TasksContract.CONTENT_TYPE;

            case TASKS_ID:
                return TasksContract.CONTENT_ITEM_TYPE;

            case TIMINGS:
                return TimingsContract.CONTENT_TYPE;

            case TIMINGS_ID:
                return TimingsContract.CONTENT_ITEM_TYPE;

            case TASKS_DURATIONS:
                return DurationsContract.CONTENT_TYPE;

            case TASKS_DURATIONS_ID:
                return DurationsContract.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);
        }

//        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        Log.d(TAG, "insert: Inserting data at uri: " + uri);
        SQLiteDatabase db;
        long entryId;
        Uri entryUri;
        
        switch (uriMatcher.match(uri)) {
            case TASKS:
                db = dbHelper.getWritableDatabase();
                entryId = db.insert(TasksContract.TABLE_NAME, null, contentValues);
                if(entryId >= 0) {
                    Log.d(TAG, "insert: Entry succesfully added");
                    entryUri = TasksContract.getRowUri(entryId);
                } else {
                    Log.d(TAG, "insert: Unable to add entry");
                    throw new android.database.SQLException("Unable to insert data for uri: " + uri);
                }
                break;

            case TIMINGS:
                db = dbHelper.getWritableDatabase();
                entryId = db.insert(TimingsContract.TABLE_NAME, null, contentValues);
                if(entryId >= 0) {
                    Log.d(TAG, "insert: Entry succesfully added");
                    entryUri = TimingsContract.getRowUri(entryId);
                } else {
                    Log.d(TAG, "insert: Unable to add entry");
                    throw new android.database.SQLException("Unable to insert data for uri: " + uri);
                }
                break;

            default:
                throw new IllegalArgumentException("Uri not recognized: " + uri);
        }

        if(entryId >= 0) {
            Log.d(TAG, "insert: Data was inserted");
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return entryUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        SQLiteDatabase db;
        int count;
        String whereClause;
        long entryId;

        switch (uriMatcher.match(uri)) {
            case TASKS:
                db = dbHelper.getWritableDatabase();
                count = db.delete(TasksContract.TABLE_NAME, null, null);
                break;

            case TASKS_ID:
                db = dbHelper.getWritableDatabase();
                entryId = TasksContract.getTaskId(uri);
                whereClause = TasksContract.Columns.TASKS_ID + "=" + entryId;
                if(s!= null && s.length() > 0) {
                    whereClause += "AND (" + s +")";
                }
                count = db.delete(TasksContract.TABLE_NAME, whereClause, strings);
                break;

            case TIMINGS:
                db = dbHelper.getWritableDatabase();
                count = db.delete(TimingsContract.TABLE_NAME, s, strings);
                break;

            case TIMINGS_ID:
                db = dbHelper.getWritableDatabase();
                entryId = TimingsContract.getTimingId(uri);
                whereClause = TimingsContract.Columns.TIMINGS_ID + "=" + entryId;
                if(s!= null && s.length() > 0) {
                    whereClause += "AND (" + s +")";
                }
                count = db.delete(TimingsContract.TABLE_NAME, whereClause, strings);
                break;

            default:
                throw new android.database.SQLException("Unable to delete at uri: " + uri);
        }

        if(count >  0) {
            Log.d(TAG, "Data was deleted. Number of rows = " + count);
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        Log.d(TAG, "update: called with uri: " + uri);
        SQLiteDatabase db;
        int count;
        String whereClause;
        long entryId;

        switch (uriMatcher.match(uri)) {
            case TASKS:
                db = dbHelper.getWritableDatabase();
                count = db.update(TasksContract.TABLE_NAME, contentValues, s, strings);
                break;

            case TASKS_ID:
                db = dbHelper.getWritableDatabase();
                entryId = TasksContract.getTaskId(uri);
                whereClause = TasksContract.Columns.TASKS_ID + "=" + entryId;
                if(s!= null && s.length() > 0) {
                    whereClause += "AND (" + s +")";
                }
                count = db.update(TasksContract.TABLE_NAME, contentValues, whereClause, strings);
                break;

            case TIMINGS:
                db = dbHelper.getWritableDatabase();
                count = db.update(TimingsContract.TABLE_NAME, contentValues, s, strings);
                break;

            case TIMINGS_ID:
                db = dbHelper.getWritableDatabase();
                entryId = TimingsContract.getTimingId(uri);
                whereClause = TimingsContract.Columns.TIMINGS_ID + "=" + entryId;
                if(s!= null && s.length() > 0) {
                    whereClause += "AND (" + s +")";
                }
                count = db.update(TimingsContract.TABLE_NAME, contentValues, whereClause, strings);
                break;

            default:
                throw new android.database.SQLException("Unable to delete at uri: " + uri);
        }

        if(count >  0) {
            Log.d(TAG, "insert: Data was updated");
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return count;
    }
}
