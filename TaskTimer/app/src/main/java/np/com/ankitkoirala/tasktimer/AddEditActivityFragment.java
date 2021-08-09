package np.com.ankitkoirala.tasktimer;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class AddEditActivityFragment extends Fragment {

    private static final String TAG = "AddEditActivityFragment";

    private enum OpType {ADD, EDIT}

    ;
    private OpType opType;

    EditText editName, editDescription, editSortOrder;
    Button btnSave;

    OnSaveListener listener;
    Task originalTask = null;

    interface OnSaveListener {
        void onSaveListener();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated: This shouldn't be called by default in Portrat mode");
        super.onActivityCreated(savedInstanceState);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        Activity activity = getActivity();
        if (!(activity instanceof OnSaveListener)) {
            throw new ClassCastException(getActivity().getClass().getSimpleName() + " does not implement OnSaveListener interface");
        }

        listener = (OnSaveListener) activity;
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach: starts");
        super.onDetach();
        listener = null;
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        Log.d(TAG, "onCreateView: starts");

        View view = inflater.inflate(R.layout.fragment_add_edit, container, false);
//        Toolbar toolbar = view.findViewById(R.id.toolbar);
//
//        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editName = view.findViewById(R.id.addedit_name);
        editDescription = view.findViewById(R.id.addedit_description);
        editSortOrder = view.findViewById(R.id.addedit_sortorder);
        btnSave = view.findViewById(R.id.addedit_save);

        Bundle bundle = getArguments();

        final Task task;
        if (bundle != null) {
            task = (Task) bundle.getSerializable(Task.class.getSimpleName());

            if (task != null) {
                Log.d(TAG, "onCreateView: Editing task: " + task);
                originalTask = task;
                opType = OpType.EDIT;

                editName.setText(task.getName());
                editDescription.setText(task.getDescription());
                editSortOrder.setText(task.getSortOrder() + "");
            } else {
                opType = OpType.ADD;
            }
        } else {
            Log.d(TAG, "onCreateView: No bundle passed");
            task = null;
            opType = OpType.ADD;
        }

        btnSave.setOnClickListener(v -> {
            int sortOrder;
            try {
                sortOrder = Integer.valueOf(editSortOrder.getText().toString());
            } catch (NumberFormatException e) {
                sortOrder = 0;
            }

            ContentValues values = new ContentValues();
            ContentResolver resolver = getActivity().getContentResolver();

            switch (opType) {
                case ADD:
                    if (editName.getText().toString().length() > 0) {
                        Log.d(TAG, "onCreateView: Adding new task");
                        values.put(TasksContract.Columns.TASKS_NAME, editName.getText().toString());
                        values.put(TasksContract.Columns.TASKS_DESCRIPTION, editDescription.getText().toString());
                        values.put(TasksContract.Columns.TASKS_SORTORDER, sortOrder);
                        resolver.insert(TasksContract.CONTENT_URI, values);
                    }
                    break;

                case EDIT:
                    if (task != null) {
                        if (!task.getName().equals(editName.getText().toString())) {
                            values.put(TasksContract.Columns.TASKS_NAME, editName.getText().toString());
                        }

                        if (!task.getDescription().equals(editDescription.getText().toString())) {
                            values.put(TasksContract.Columns.TASKS_DESCRIPTION, editDescription.getText().toString());
                        }

                        if (!String.valueOf(task.getSortOrder()).equals(editSortOrder.getText().toString())) {
                            values.put(TasksContract.Columns.TASKS_SORTORDER, sortOrder);
                        }

                        if (values.size() > 0) {
                            Log.d(TAG, "onCreateView: Editing task with id: " + task.getId());
                            resolver.update(TasksContract.getRowUri(task.getId()), values, null, null);
                        }
                    }
                    break;
            }

            if (listener != null) {
                listener.onSaveListener();
            }
        });

        return view;
    }

    public boolean canClose() {
        Log.d(TAG, "canClose: starts");
        if (originalTask != null) {
            if (!editName.getText().toString().equals(originalTask.getName()) ||
                    !editDescription.getText().toString().equals(originalTask.getDescription()) ||
                    !Long.valueOf(editSortOrder.getText().toString()).equals(originalTask.getSortOrder())) {
                return false;
            }
        }

        return true;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}