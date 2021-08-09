package np.com.ankitkoirala.tasktimer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = "DBHelper";

    private static final String DB_NAME = "taskTimer.db";
    private static final int DB_VERSION = 3;

    private static DBHelper appDatabase = null;

    private DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static DBHelper getInstance(Context context) {
        if(appDatabase == null) {
            appDatabase = new DBHelper(context);
        }

        return appDatabase;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(TAG, "onCreate: Creating database");
        String sSQL = "CREATE TABLE " +  TasksContract.TABLE_NAME + " (" +
                TasksContract.Columns.TASKS_ID + " INTEGER PRIMARY KEY NOT NULL" + ", " +
                TasksContract.Columns.TASKS_NAME + " TEXT NOT NULL" + ", " +
                TasksContract.Columns.TASKS_DESCRIPTION + " TEXT" + ", " +
                TasksContract.Columns.TASKS_SORTORDER + " INTEGER" +
                ");";
        sqLiteDatabase.execSQL(sSQL);

        addTimingsTable(sqLiteDatabase);
        addDurationView(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade: starts");
        switch (oldVersion) {
            case 1:
                addTimingsTable(sqLiteDatabase);

            case 2:
                addDurationView(sqLiteDatabase);
                break;

            default:
                throw new IllegalStateException("onUpgrade with unknown version = " + oldVersion);
        }
    }

    private void addTimingsTable(SQLiteDatabase sqLiteDatabase) {
        String sSQL;
        sSQL = "CREATE TABLE " + TimingsContract.TABLE_NAME + "(" +
                TimingsContract.Columns.TIMINGS_ID + " INTEGER PRIMARY KEY NOT NULL, " +
                TimingsContract.Columns.TIMINGS_TASK_ID + " INTEGER NOT NULL, " +
                TimingsContract.Columns.TIMINGS_START_TIME + " INTEGER, " +
                TimingsContract.Columns.TIMINGS_DURATION  + " INTEGER" +
                ");";
        sqLiteDatabase.execSQL(sSQL);

        sSQL = "CREATE TRIGGER delete_timings_for_tasks " +
                " AFTER DELETE ON " + TasksContract.TABLE_NAME +
                " FOR EACH ROW " +
                " BEGIN" +
                " DELETE FROM " + TimingsContract.TABLE_NAME +
                " WHERE " + TimingsContract.Columns.TIMINGS_TASK_ID + "= OLD." + TasksContract.Columns.TASKS_ID +
                "; END;";
        sqLiteDatabase.execSQL(sSQL);
    }

    private void addDurationView(SQLiteDatabase sqLiteDatabase) {
        /*
        CREATE VIEW vsTaskDurations AS
        SELECT TimingsContract.Columns.TIMINGS_ID, TasksContract.Columns.TASK_NAME,
        TasksContract.Columns.TASKS_DESCRIPTION, TimingsContract.Columns.TIMINGS_START_TIME,
        DATE(TimingsContract.Columns.TIMINGS_START_TIME, 'unixepoch') AS StartDate,
        SUM(TimingsContract.Columns.TIMINGS_DURATION) AS Duration
        FROM TasksContract.TABLE_NAME INNER JOIN TimingsContract.TABLE_NAME
        ON TasksContract.Columns.TASKS_ID = TimingsContract.Columns.TIMINGS_TASK_ID
        GROUP BY TasksContract.Columns.TASKS_ID, DateTime
         */

        Log.d(TAG, "addDurationView: starts");
        String sSQL;
        sSQL = "CREATE VIEW " + DurationsContract.TABLE_NAME +
                " AS SELECT " +
                " " + TimingsContract.TABLE_NAME + "." + TimingsContract.Columns.TIMINGS_ID + ", " +
                " " + TasksContract.TABLE_NAME + "." + TasksContract.Columns.TASKS_NAME + " , " +
                " " + TasksContract.TABLE_NAME + "." + TasksContract.Columns.TASKS_DESCRIPTION + ", " +
                " " + TimingsContract.TABLE_NAME + "." + TimingsContract.Columns.TIMINGS_START_TIME +
                ", DATE(" + TimingsContract.TABLE_NAME + "." + TimingsContract.Columns.TIMINGS_START_TIME + ", 'unixepoch')" +
                " AS " + DurationsContract.Columns.DURATIONS_START_DATE + ", " +
                " SUM(" + TimingsContract.TABLE_NAME + "." + TimingsContract.Columns.TIMINGS_DURATION + ")" +
                " AS " + DurationsContract.Columns.DURATIONS_DURATION +
                " FROM " + TasksContract.TABLE_NAME + " INNER JOIN " + TimingsContract.TABLE_NAME +
                " ON " + TasksContract.TABLE_NAME + "." + TasksContract.Columns.TASKS_ID + "=" +
                " " +  TimingsContract.TABLE_NAME + "." + TimingsContract.Columns.TIMINGS_TASK_ID +
                " GROUP BY " + TasksContract.TABLE_NAME + "." +  TasksContract.Columns.TASKS_ID + "," +
                DurationsContract.Columns.DURATIONS_START_DATE + ";";
        sqLiteDatabase.execSQL(sSQL);
    }
}
