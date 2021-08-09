package np.com.ankitkoirala.tasktimer;

import android.content.ContentUris;
import android.net.Uri;
import android.util.Log;

public class TimingsContract {

    private static final String TAG = "TimingsContract";
    
    public static final String TABLE_NAME = "Timings";
    public static final Uri CONTENT_URI = Uri.withAppendedPath(AppProvider.CONTENT_AUTHORITY_URI, TABLE_NAME);

    static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + AppProvider.AUTHORITY + "." + TABLE_NAME;
    static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + AppProvider.AUTHORITY + "." + TABLE_NAME;

    public static class Columns {
        public static final String TIMINGS_ID = "_id";
        public static final String TIMINGS_TASK_ID = "TaskID";
        public static final String TIMINGS_START_TIME = "StartTime";
        public static final String TIMINGS_DURATION = "duration";

        private Columns() {}
    }

    static long getTimingId(Uri uri) {
        Log.d(TAG, "getTaskId: Obtained Uri: " + uri);
        return ContentUris.parseId(uri);
    }

    static Uri getRowUri(long _id) {
        return ContentUris.withAppendedId(CONTENT_URI, _id);
    }

}
