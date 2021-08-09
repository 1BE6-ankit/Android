package np.com.ankitkoirala.tasktimer;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class ReportsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        DatePickerDialog.OnDateSetListener,
        ConfirmDialog.OnDialogResult
{
    private static final String TAG = "ReportsActivity";
    
    private static final int LOADER_ID = 0;
    DurationsRVAdapter durationsRVAdapter;
    private GregorianCalendar gc = new GregorianCalendar();
    private boolean weekSelected = true;

    private Bundle mArgs = new Bundle();
    public static final String SELECTION_PARAM = "SELECTION_PARAM";
    public static final String SELECTION_ARGS_PARAM = "SELECTION_ARGS_PARAM";
    public static final String SORT_ORDER_PARAM = "SORT_ORDER_PARAM";

    private static final int CHOOSE_DATE_ID = 1;
    private static final int DELETE_DATE_ID = 2;

    private static final String SAVE_BUNDLE_CURRENT_DATE = "SAVE_BUNDLE_CURRENT_DATE";
    private static final String SAVE_BUNDLE_WEEK_SELECTED = "SAVE_BUNDLE_WEEK_SELECTED";

    private static final String CONFIRMED_DELETE_DATE = "CONFIRMED_DELTE_DATE";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        applyFilter();
        LoaderManager.getInstance(this).initLoader(LOADER_ID, mArgs, this);

        durationsRVAdapter = new DurationsRVAdapter(this, null);
        RecyclerView recyclerView = findViewById(R.id.durations_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(durationsRVAdapter);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable(SAVE_BUNDLE_CURRENT_DATE, gc.getTime());
        outState.putBoolean(SAVE_BUNDLE_WEEK_SELECTED, weekSelected);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Date date = (Date) savedInstanceState.getSerializable(SAVE_BUNDLE_CURRENT_DATE);
        weekSelected = savedInstanceState.getBoolean(SAVE_BUNDLE_WEEK_SELECTED, true);
        if(date != null) {
            gc.setTime(date);
            applyFilter();
            LoaderManager.getInstance(this).initLoader(LOADER_ID, mArgs, this);
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projections = {
                DurationsContract.Columns.DURATIONS_ID,
                DurationsContract.Columns.DURATIONS_TASK_NAME,
                DurationsContract.Columns.DURATIONS_DESCRIPTION,
                DurationsContract.Columns.DURATIONS_START_TIME,
                DurationsContract.Columns.DURATIONS_DURATION,
        };

        switch (id) {
            case LOADER_ID:
                String selection = null;
                String[] selectionArgs = null;
                String sortOrder = null;

                if(args != null) {
                    selection = args.getString(SELECTION_PARAM);
                    selectionArgs = args.getStringArray(SELECTION_ARGS_PARAM);
                    sortOrder = args.getString(SORT_ORDER_PARAM);
                }

                CursorLoader cursorLoader = new CursorLoader(
                        this,
                        DurationsContract.CONTENT_URI,
                        projections,
                        selection,
                        selectionArgs,
                        sortOrder
                );
                return cursorLoader;

            default:
                throw new IllegalArgumentException("Passed argument loader does not exist: " + id);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.reports_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.report_menu_item_day_week:
                weekSelected = !weekSelected;
                invalidateOptionsMenu();
                applyFilter();
                LoaderManager.getInstance(this).restartLoader(LOADER_ID, mArgs, this);
                break;

            case R.id.report_menu_item_pick_date:
                showDatePickerDialog(CHOOSE_DATE_ID, "Choose a date to display");
                break;

            case R.id.report_menu_item_delete:
                showDatePickerDialog(DELETE_DATE_ID, "All entries before the chosen date will be deleted.");
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showDatePickerDialog(int dialogId, String title) {
        DatePickerFilter datePickerFilter = new DatePickerFilter();
        Bundle bundle = new Bundle();
        bundle.putInt(DatePickerFilter.DATE_PICKER_ID, dialogId);
        bundle.putString(DatePickerFilter.DATE_PICKER_TITLE, title);
        bundle.putSerializable(DatePickerFilter.DATE_PICKER_DATE, gc.getTime());
        datePickerFilter.setArguments(bundle);
        datePickerFilter.show(getSupportFragmentManager(), null);
    }
    
    private void applyFilter() {
        if(weekSelected) {
            Date currentTime = gc.getTime();

            // get time for start of the week
            int currentDay = gc.get(GregorianCalendar.DAY_OF_WEEK);
            int startDayOfWeek = gc.getFirstDayOfWeek();
            gc.add(Calendar.DATE, -(currentDay - startDayOfWeek));
            Date startOfWeekTime = gc.getTime();

            // get time for end of the week
            gc.add(Calendar.DATE, 6);
            Date endOfWeekTime = gc.getTime();

            String selection = DurationsContract.Columns.DURATIONS_START_DATE + " BETWEEN ? AND ?";
            String[] selectionArgs = {getYYMMDD(startOfWeekTime), getYYMMDD(endOfWeekTime)};

            mArgs.putString(SELECTION_PARAM, selection);
            mArgs.putStringArray(SELECTION_ARGS_PARAM, selectionArgs);

            gc.setTime(currentTime);
        } else {
            Date currentTime = gc.getTime();
            String selection = DurationsContract.Columns.DURATIONS_START_DATE + " = ?";
            String[] selectionArgs = {getYYMMDD(currentTime)};

            mArgs.putString(SELECTION_PARAM, selection);
            mArgs.putStringArray(SELECTION_ARGS_PARAM, selectionArgs);
        }
    }

    private String getYYMMDD(Date date) {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(date);
        return String.format("%04d-%02d-%02d", gregorianCalendar.get(GregorianCalendar.YEAR),
                gregorianCalendar.get(GregorianCalendar.MONTH),
                gregorianCalendar.get(GregorianCalendar.DAY_OF_MONTH));
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.report_menu_item_day_week);
        if(item != null) {
            if(weekSelected) {
                item.setTitle(R.string.report_menu_item_week);
                item.setIcon(R.drawable.ic_baseline_filter_7_24);
            } else {
                item.setTitle(R.string.report_menu_item_day);
                item.setIcon(R.drawable.ic_baseline_filter_1_24);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if(durationsRVAdapter!=null && data!=null) {
            durationsRVAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        int dialogId = (int) view.getTag();

        switch (dialogId) {
            case CHOOSE_DATE_ID:
                gc.set(year, month, dayOfMonth, 0,0,0);
                applyFilter();
                LoaderManager.getInstance(this).restartLoader(LOADER_ID, mArgs, this );
                break;

            case DELETE_DATE_ID:
                gc.set(year, month, dayOfMonth, 0,0,0);

                ConfirmDialog confirmDialog = new ConfirmDialog();
                Bundle args = new Bundle();
                args.putInt(ConfirmDialog.DIALOG_ID, 0);
                args.putInt(ConfirmDialog.DIALOG_TITLE, R.string.timings_delete_confirm_mesage);
                args.putInt(ConfirmDialog.DIALOG_NEG_STRING, R.string.timings_delete_negative_message);
                args.putInt(ConfirmDialog.DIALOG_POS_STRING, R.string.timings_delete_positive_message);
                args.putSerializable(CONFIRMED_DELETE_DATE, gc.getTime());

                confirmDialog.setArguments(args);
                confirmDialog.show(getSupportFragmentManager(), null);
                break;

            default:
                throw new IllegalArgumentException("View with tag = " + dialogId + " is not valid");

        }

        Log.d(TAG, "onCreateDialog: year = " + year);
        Log.d(TAG, "onCreateDialog: month = " + month);
        Log.d(TAG, "onCreateDialog: dayOfMonth = " + dayOfMonth);
    }

    @Override
    public void onPositiveResult(int dialogId, Bundle args) {
        Log.d(TAG, "onPositiveResult: starts");
        Date deletionDate = (Date) args.getSerializable(CONFIRMED_DELETE_DATE);
        long timeInSecs = deletionDate.getTime() / 1000;
        String userDate = DateFormat.getDateFormat(this).format(timeInSecs * 1000);
        Log.d(TAG, "onPositiveResult: date: " + userDate);

//        GregorianCalendar gregorianCalendar = new GregorianCalendar();
//        gregorianCalendar.setTime(deletionDate);
//        Log.d(TAG, "onPositiveResult: year = " + gregorianCalendar.get(GregorianCalendar.YEAR) +
//                ", month = " + gregorianCalendar.get(GregorianCalendar.MONTH) +
//                ", dayOfMonth = " + gregorianCalendar.get(GregorianCalendar.DAY_OF_MONTH));

        String selection = TimingsContract.Columns.TIMINGS_START_TIME + " < ?";
        String[] selectionArgs = {Long.toString(timeInSecs)};

        getContentResolver().delete(TimingsContract.CONTENT_URI, selection, selectionArgs);
        applyFilter();
        LoaderManager.getInstance(this).restartLoader(LOADER_ID, mArgs, this );
    }

    @Override
    public void onNegativeResult(int dialogId, Bundle args) {
        Log.d(TAG, "onNegativeResult: starts");
    }

    @Override
    public void onCancel(int dialogId) {

    }
}
