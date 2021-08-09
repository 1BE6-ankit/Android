package np.com.ankitkoirala.tasktimer;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import java.util.GregorianCalendar;

public class GenerateTimings {

    public static void generateTimings(ContentResolver resolver) {

        String[] projections = {TasksContract.Columns.TASKS_ID};
        Uri taskUri = TasksContract.CONTENT_URI;
        Uri timingsUri = TimingsContract.CONTENT_URI;
        Cursor cursor = resolver.query(taskUri, projections, null, null, null);

        if(cursor != null && cursor.moveToFirst()) {
            do {
                int maxDuration = (24*60*60) / 6;
                int minLoops = 100;
                int maxLoops = 500;
                int loops = minLoops + getRandomNumber(maxLoops - minLoops);

                long taskId = cursor.getLong(cursor.getColumnIndex(TasksContract.Columns.TASKS_ID));

                for(int i=0; i<loops; i++) {
                    long startTime = getRandomTime() / 1000;
                    long duration = getRandomNumber(maxDuration);

                    ContentValues values = new ContentValues();
                    values.put(TimingsContract.Columns.TIMINGS_TASK_ID, taskId);
                    values.put(TimingsContract.Columns.TIMINGS_START_TIME, startTime);
                    values.put(TimingsContract.Columns.TIMINGS_DURATION, duration);

                    resolver.insert(timingsUri, values);
                }
            } while (cursor.moveToNext());
            cursor.close();
        }

    }

    private static int getRandomNumber(long max) {
        return (int) Math.round(Math.random() * max);
    }

    private static long getRandomTime() {
        int startYear = 2020;
        int endYear = 2021;

        int sec = getRandomNumber(60-1);
        int min = getRandomNumber(60-1);
        int hr = getRandomNumber(24-1);
        int month = getRandomNumber(12-1);
        int year = startYear + getRandomNumber(endYear - startYear);

        GregorianCalendar gc = new GregorianCalendar();
        int day = 1 + getRandomNumber(gc.getMaximum(GregorianCalendar.DAY_OF_MONTH) - 1);

        gc.set(year, month, day, hr, min, sec);
        return gc.getTimeInMillis();
    }

}
