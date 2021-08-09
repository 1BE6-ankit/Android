package np.com.ankitkoirala.tasktimer;

import android.content.ContentUris;
import android.net.Uri;
import android.util.Log;

public class DurationsContract {

    private static final String TAG = "DurationsContract";

    public static final String TABLE_NAME = "vwTaskDurations";
    public static final Uri CONTENT_URI = Uri.withAppendedPath(AppProvider.CONTENT_AUTHORITY_URI, TABLE_NAME);

    static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + AppProvider.AUTHORITY + "." + TABLE_NAME;
    static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + AppProvider.AUTHORITY + "." + TABLE_NAME;

    public static class Columns {
        public static final String DURATIONS_ID = "_id";
        public static final String DURATIONS_TASK_NAME = TasksContract.Columns.TASKS_NAME;
        public static final String DURATIONS_DESCRIPTION = TasksContract.Columns.TASKS_DESCRIPTION;
        public static final String DURATIONS_START_TIME = TimingsContract.Columns.TIMINGS_START_TIME;
        public static final String DURATIONS_START_DATE = "StartDate";
        public static final String DURATIONS_DURATION = TimingsContract.Columns.TIMINGS_DURATION;

        private Columns() {}
    }

    static long getDurationId(Uri uri) {
        Log.d(TAG, "getDurationId: Obtained Uri: " + uri);
        return ContentUris.parseId(uri);
    }

}
