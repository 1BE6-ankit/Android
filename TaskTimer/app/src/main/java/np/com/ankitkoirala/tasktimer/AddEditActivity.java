package np.com.ankitkoirala.tasktimer;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

public class AddEditActivity extends AppCompatActivity implements AddEditActivityFragment.OnSaveListener, ConfirmDialog.OnDialogResult {

    private static final String TAG = "AddEditActivity";

    private static final int DIALOG_ID_SAVE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle arguments = getIntent().getBundleExtra(Task.class.getSimpleName());
        FragmentManager fragmentTransaction = getSupportFragmentManager();
        if(fragmentTransaction.findFragmentById(R.id.add_edit_activity_frame) == null) {
            AddEditActivityFragment fragment = new AddEditActivityFragment();
            fragment.setArguments(arguments);
            fragmentTransaction.beginTransaction()
                    .replace(R.id.add_edit_activity_frame, fragment)
                    .commit();

            toolbar.setNavigationOnClickListener(view -> {
                Log.d(TAG, "onCreate: home button clicked");
                createConfirmDialog();
            });
        }
    }

    public void createConfirmDialog() {
        FragmentManager manager = getSupportFragmentManager();
        AddEditActivityFragment addEditActivityFragment = (AddEditActivityFragment) manager.findFragmentById(R.id.add_edit_activity_frame);
        if(addEditActivityFragment != null && !addEditActivityFragment.canClose()) {
            Bundle args = new Bundle();
            args.putInt(ConfirmDialog.DIALOG_ID, DIALOG_ID_SAVE);
            args.putInt(ConfirmDialog.DIALOG_TITLE, R.string.save_add_edit_back_press);
            args.putInt(ConfirmDialog.DIALOG_POS_STRING, R.string.save_add_edit_pos_string);
            args.putInt(ConfirmDialog.DIALOG_NEG_STRING, R.string.save_add_edit_neg_string);

            ConfirmDialog confirmDialog = new ConfirmDialog();
            confirmDialog.setArguments(args);
            confirmDialog.show(getSupportFragmentManager(), null);
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        createConfirmDialog();
    }

    @Override
    public void onPositiveResult(int dialogId, Bundle args) {
        switch (dialogId) {
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
            case DIALOG_ID_SAVE:
                finish();
                break;

            default:
                throw new IllegalArgumentException("Dialog with id = " + dialogId + " does not exist");
        }
    }

    @Override
    public void onCancel(int dialogId) {

    }

    @Override
    public void onSaveListener() {
        finish();
    }
}