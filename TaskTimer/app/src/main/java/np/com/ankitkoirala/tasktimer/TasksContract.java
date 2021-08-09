package np.com.ankitkoirala.tasktimer;

import android.content.ContentUris;
import android.net.Uri;
import android.util.Log;

public class TasksContract {

    private static final String TAG = "TasksContract";
    
    public static final String TABLE_NAME = "Tasks";
    public static final Uri CONTENT_URI = Uri.withAppendedPath(AppProvider.CONTENT_AUTHORITY_URI, TABLE_NAME);

    static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + AppProvider.AUTHORITY + "." + TABLE_NAME;
    static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + AppProvider.AUTHORITY + "." + TABLE_NAME;

    public static class Columns {
        public static final String TASKS_ID = "_id";
        public static final String TASKS_NAME = "Name";
        public static final String TASKS_DESCRIPTION = "Description";
        public static final String TASKS_SORTORDER = "Sortorder";

        private Columns() {}
    }

    static long getTaskId(Uri uri) {
        Log.d(TAG, "getTaskId: Obtained Uri: " + uri);
        return ContentUris.parseId(uri);
    }

    static Uri getRowUri(long _id) {
        return ContentUris.withAppendedId(CONTENT_URI, _id);
    }

}
