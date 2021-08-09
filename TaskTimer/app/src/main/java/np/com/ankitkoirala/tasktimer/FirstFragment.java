package np.com.ankitkoirala.tasktimer;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.security.InvalidParameterException;

import np.com.ankitkoirala.tasktimer.databinding.FragmentFirstBinding;

public class  FirstFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "FirstFragment";

    private FragmentFirstBinding binding;
    private TaskRecyclerAdapter recyclerAdapter;

    private final int LOADER_ID = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: starts");
        super.onCreate(savedInstanceState);

        LoaderManager.getInstance(this).initLoader(LOADER_ID,null, this);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        Log.d(TAG, "onCreateView: starts");
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if(recyclerAdapter == null) {
            recyclerAdapter = new TaskRecyclerAdapter(null, (TaskRecyclerAdapter.OnTaskClickListener) getActivity());
        } else {
            recyclerAdapter.setTaskClickListener((TaskRecyclerAdapter.OnTaskClickListener) getActivity());
        }

        recyclerView.setAdapter(recyclerAdapter);

        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        binding.buttonFirst.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                NavHostFragment.findNavController(FirstFragment.this)
//                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
//            }
//        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = new String[] {
                TasksContract.Columns.TASKS_ID,
                TasksContract.Columns.TASKS_NAME,
                TasksContract.Columns.TASKS_DESCRIPTION,
                TasksContract.Columns.TASKS_SORTORDER};
        String sortOrder = TasksContract.Columns.TASKS_SORTORDER + ", " + TasksContract.Columns.TASKS_NAME + " COLLATE NOCASE";

        switch (id) {
            case LOADER_ID:
                return new CursorLoader(getActivity(),
                        TasksContract.CONTENT_URI,
                        projection,
                        null,
                        null,
                        sortOrder);

            default:
                throw new InvalidParameterException(TAG + " .onCreateLoader called with invalid parameters");
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "onLoadFinished: starts");
        recyclerAdapter.swapCursor(data);
//        if(data != null) {
//            while (data.moveToNext()) {
//                for(int i=0; i<data.getColumnCount(); i++) {
//                    Log.d(TAG, "onLoadFinished: " + data.getColumnName(i) + ": " + data.getString(i));
//                }
//                Log.d(TAG, "onLoadFinished: ========================");
//            }
//        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset: starts");
        recyclerAdapter.swapCursor(null);
    }
}