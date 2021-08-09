package np.com.ankitkoirala.tasktimer;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.snackbar.Snackbar;

import np.com.ankitkoirala.tasktimer.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements TaskRecyclerAdapter.OnTaskClickListener,
        AddEditActivityFragment.OnSaveListener,
        ConfirmDialog.OnDialogResult
{

    private static final String TAG = "MainActivity";
    
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private AlertDialog aboutDialog;
    private Timing currentTiming = null;
    private static String CURRENT_TIMING = "CURRENT_TIMING";

    View mainFragmentView, addEditFragmentView;

    private boolean doublePane = false;
    private boolean editing = false;

    private final int DIALOG_ID_DELETE = 1;
    private final int DIALOG_ID_SAVE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: starts");
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            doublePane = true;
        }

        mainFragmentView = findViewById(R.id.nav_host_fragment_content_main);
        addEditFragmentView = findViewById(R.id.addEditContainer);

        if(!doublePane) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = fragmentManager.findFragmentById(R.id.addEditContainer);
            if(editing || fragment!=null) {
                mainFragmentView.setVisibility(View.GONE);
                addEditFragmentView.setVisibility(View.VISIBLE);
            } else {
                mainFragmentView.setVisibility(View.VISIBLE);
                addEditFragmentView.setVisibility(View.GONE);
            }
        } else {
            mainFragmentView.setVisibility(View.VISIBLE);
            addEditFragmentView.setVisibility(View.VISIBLE);
        }

//        ContentValues values = new ContentValues();
//        values.put(TasksContract.Columns.TASKS_NAME, "New task");
//        values.put(TasksContract.Columns.TASKS_DESCRIPTION, "Wen Task");
//        Uri uri = getContentResolver().insert(TasksContract.CONTENT_URI, values);
//
//        values = new ContentValues();
//        values.put(TasksContract.Columns.TASKS_NAME, "GME");
//        getContentResolver().update(TasksContract.getRowUri(2), values, null, null);
//        getContentResolver().delete(TasksContract.getRowUri(3), null, null);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                String[] projection = new String[] {DurationsContract.Columns.DURATIONS_START_DATE};
                Cursor cursor = getContentResolver().query(DurationsContract.CONTENT_URI, projection, null, null, null);
                if(cursor != null) {
                    Log.d(TAG, "onCreate: number of rows: " + cursor.getCount());
                    while(cursor.moveToNext()) {
                        Log.d(TAG, "onCreate: data: " + cursor.getString(cursor.getColumnIndex(DurationsContract.Columns.DURATIONS_START_DATE)));
                    }
                    cursor.close();
                }

//                String[] projection = new String[] {TasksContract.Columns.TASKS_NAME, TasksContract.Columns.TASKS_DESCRIPTION};
//                Cursor cursor = getContentResolver().query(
//                        TasksContract.CONTENT_URI, projection, null, null, TasksContract.Columns.TASKS_SORTORDER);
//
//                if(cursor != null) {
//                    Log.d(TAG, "onCreate: number of rows: " + cursor.getCount());
//                    while(cursor.moveToNext()) {
//                        Log.d(TAG, "onCreate: data: " + cursor.getString(cursor.getColumnIndex(TasksContract.Columns.TASKS_NAME)));
//                    }
//                    cursor.close();
//                }
//
//                Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
//                startActivity(intent);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable(CURRENT_TIMING, currentTiming);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentTiming = (Timing) savedInstanceState.getSerializable(CURRENT_TIMING);
        TextView textView = findViewById(R.id.current_task_timing_heading);
        if(currentTiming == null) {
            textView.setText(getText(R.string.no_task_timing));
        } else {
            textView.setText("Task `" + currentTiming.getTask().getName() + "` is timed");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if(BuildConfig.DEBUG) {
            menu.findItem(R.id.menuitem_generate).setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.menuitem_add:
                handleMenuAddEdit(null);
                break;

            case R.id.menuitem_durations:
                startActivity(new Intent(this, ReportsActivity.class));
                break;

            case R.id.menuitem_settings:
                break;

            case R.id.menuitem_about:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("About");
//                builder.setIcon();

                final View aboutSection = getLayoutInflater().inflate(R.layout.about_section, null);
                ((TextView) aboutSection.findViewById(R.id.about_version)).setText("v" + BuildConfig.VERSION_NAME);
                builder.setView(aboutSection);
                aboutDialog = builder.create();
                aboutDialog.show();
                break;

            case R.id.menuitem_generate:
                GenerateTimings.generateTimings(getContentResolver());
                break;

            case android.R.id.home:
                Log.d(TAG, "onOptionsItemSelected: Home button pressed");
                onBackPressed();
                break;

            default:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveListener() {
        Log.d(TAG, "onSaveListener: Save btn clicked");
        removeAddEditFragment();
    }

    private void removeAddEditFragment() {
        editing = false;
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.addEditContainer);
        if(fragment != null) {
            fragmentManager.beginTransaction()
                    .remove(fragment)
                    .commit();
        }

        if(!doublePane) {
            mainFragmentView.setVisibility(View.VISIBLE);
            addEditFragmentView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onEditBtnClick(Task task) {
        Log.d(TAG, "onEditBtnClick: starts");
        editing = true;
        handleMenuAddEdit(task);
    }

    @Override
    public void onRemoveBtnClick(Task task) {
        Log.d(TAG, "onRemoveBtnClick: starts");

        Bundle args = new Bundle();
        args.putLong(Task.class.getSimpleName(), task.getId());
        args.putInt(ConfirmDialog.DIALOG_ID, DIALOG_ID_DELETE);
        args.putString(ConfirmDialog.DIALOG_TITLE, String.format("Are you sure you want to delete the task with id %d = %s", task.getId(), task.getName()));
        args.putInt(ConfirmDialog.DIALOG_POS_STRING, R.string.pos_remove_task);
        args.putInt(ConfirmDialog.DIALOG_NEG_STRING, R.string.neg_remove_task);

        ConfirmDialog confirmDialog = new ConfirmDialog();
        confirmDialog.setArguments(args);
        confirmDialog.show(getSupportFragmentManager(), null);
    }

    @Override
    public void onTaskLongClick(Task task) {
        Log.d(TAG, "onTaskLongClick: task with id=" + task.getId() + " is long clicked.");
        Toast.makeText(this, "Task with id=" + task.getId() + " is long clicked.", Toast.LENGTH_LONG).show();
        TextView currentTimingHeading = findViewById(R.id.current_task_timing_heading);

        Log.d(TAG, "onTaskLongClick: taskId: " + task.getId());
        if(currentTiming != null) {
            Log.d(TAG, "onTaskLongClick: currentTimingTaskId: " + currentTiming.getTask().getId());
            Log.d(TAG, "onTaskLongClick: COMPARISION: " + (task.getId() == currentTiming.getTask().getId()));
        }

        if(currentTiming != null && (task.getId() == currentTiming.getTask().getId())) {
            Log.d(TAG, "onTaskLongClick: Same task clicked");
            saveTiming(currentTiming);
            currentTiming = null;
            currentTimingHeading.setText(getString(R.string.no_task_timing));
        } else {
            if(currentTiming != null) {
                saveTiming(currentTiming);
            }

            currentTiming = new Timing(task);
            currentTimingHeading.setText("Task `" + task.getName() + "` is timed");
        }
    }

    private void saveTiming(@NonNull Timing timing) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TimingsContract.Columns.TIMINGS_TASK_ID, timing.getTask().getId());
        contentValues.put(TimingsContract.Columns.TIMINGS_START_TIME, timing.getStartTime());
        contentValues.put(TimingsContract.Columns.TIMINGS_DURATION, timing.getDuration());

        getContentResolver().insert(TimingsContract.CONTENT_URI, contentValues);
    }


    private void handleMenuAddEdit(Task task) {
        Log.d(TAG, "handleMenuAddEdit: starts");
        Bundle arguments = new Bundle();
        arguments.putSerializable(Task.class.getSimpleName(), task);

        editing = true;

        AddEditActivityFragment fragment = new AddEditActivityFragment();
        fragment.setArguments(arguments);
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.addEditContainer, fragment)
                .commit();

        if(!doublePane) {
            mainFragmentView.setVisibility(View.GONE);
            addEditFragmentView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPositiveResult(int dialogId, Bundle args) {
        switch (dialogId) {
            case DIALOG_ID_DELETE:
                long taskId = args.getLong(Task.class.getSimpleName());
                getContentResolver().delete(TasksContract.getRowUri(taskId), null, null);
                break;

            case DIALOG_ID_SAVE:
                break;

            default:
                throw new IllegalArgumentException("Dialog with id = " + dialogId + " does not exist");
        }
    }

    @Override
    public void onNegativeResult(int dialogId, Bundle args) {
        Log.d(TAG, "onNegativeResult: starts for dialog with id = " + dialogId);
        switch (dialogId) {
            case DIALOG_ID_DELETE:
                break;

            case DIALOG_ID_SAVE:
                removeAddEditFragment();
                break;

            default:
                throw new IllegalArgumentException("Dialog with id = " + dialogId + " does not exist");
        }
    }

    @Override
    public void onCancel(int dialogId) {

    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Log.d(TAG, "onBackPressed: BackButton pressed");

        FragmentManager manager = getSupportFragmentManager();
        AddEditActivityFragment addEditFragment = (AddEditActivityFragment) manager.findFragmentById(R.id.addEditContainer);

        if(addEditFragment != null) {
            if(!addEditFragment.canClose()) {
                showBackHomeConfirmation();
            } else {
                removeAddEditFragment();
            }
        } else {
            finish();
        }

    }

    private void showBackHomeConfirmation() {
        Bundle args = new Bundle();
        args.putInt(ConfirmDialog.DIALOG_ID, DIALOG_ID_SAVE);
        args.putInt(ConfirmDialog.DIALOG_TITLE, R.string.save_add_edit_back_press);
        args.putInt(ConfirmDialog.DIALOG_POS_STRING, R.string.save_add_edit_pos_string);
        args.putInt(ConfirmDialog.DIALOG_NEG_STRING, R.string.save_add_edit_neg_string);

        ConfirmDialog confirmDialog = new ConfirmDialog();
        confirmDialog.setArguments(args);
        confirmDialog.show(getSupportFragmentManager(), null);
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(aboutDialog != null && aboutDialog.isShowing()) {
            aboutDialog.dismiss();
        }
    }
}