package np.com.ankitkoirala.tasktimer;

import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TaskRecyclerAdapter extends RecyclerView.Adapter<TaskRecyclerAdapter.ViewHolder> {

    private static final String TAG = "TaskRecyclerAdapter";

    Cursor cursor;
    OnTaskClickListener taskClickListener;

    interface OnTaskClickListener {
        void onEditBtnClick(Task task);
        void onRemoveBtnClick(Task task);
        void onTaskLongClick(Task task);
    }

    public TaskRecyclerAdapter(Cursor cursor, OnTaskClickListener taskClickListener) {
        this.cursor = cursor;
        this.taskClickListener = taskClickListener;
    }

    public void setTaskClickListener(OnTaskClickListener taskClickListener) {
        this.taskClickListener = taskClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_list_item, parent, false);
        return new TaskRecyclerAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskRecyclerAdapter.ViewHolder holder, int position) {
        if (cursor == null || cursor.getCount() == 0) {
            String instructions = "No tasks have been added.\n\nClick the add button to add a task.\n\n";

            holder.tlvName.setText("No Task found");
            holder.tlvDescription.setText(instructions);
            holder.tlvEdit.setVisibility(View.GONE);
            holder.tlvRemove.setVisibility(View.GONE);
        } else {
            if (!cursor.moveToPosition(position)) {
                throw new CursorIndexOutOfBoundsException(TAG + ": The given data position is not found");
            }

            Task task = new Task(cursor.getLong(cursor.getColumnIndex(TasksContract.Columns.TASKS_ID)),
                    cursor.getString(cursor.getColumnIndex(TasksContract.Columns.TASKS_NAME)),
                    cursor.getString(cursor.getColumnIndex(TasksContract.Columns.TASKS_DESCRIPTION)),
                    cursor.getInt(cursor.getColumnIndex(TasksContract.Columns.TASKS_SORTORDER)));

            holder.tlvName.setText(task.getName());
            holder.tlvDescription.setText(task.getDescription());
            holder.tlvEdit.setVisibility(View.VISIBLE);
            holder.tlvRemove.setVisibility(View.VISIBLE);

            View.OnClickListener btnClickListener = v -> {
                Log.d(TAG, "onBindViewHolder: Button clicked");
                Log.d(TAG, "onBindViewHolder: Task - " + task.getName());

                switch (v.getId()) {
                    case R.id.tlv_edit:
                        taskClickListener.onEditBtnClick(task);
                        break;

                    case R.id.tlv_remove:
                        taskClickListener.onRemoveBtnClick(task);
                        break;

                    default:
                        throw new IllegalArgumentException("The given view is not valid");
                }
            };

            View.OnLongClickListener taskLongClickListener = v -> {
                Log.d(TAG, "onBindViewHolder: taskLongClickListener called");
                taskClickListener.onTaskLongClick(task);

                return true;
            };

            holder.tlvEdit.setOnClickListener(btnClickListener);
            holder.tlvRemove.setOnClickListener(btnClickListener);
            holder.itemView.setOnLongClickListener(taskLongClickListener);
        }
    }

    public Cursor swapCursor(Cursor newCursor) {
        if (cursor == newCursor) {
            return null;
        }

        Cursor oldCursor = cursor;
        cursor = newCursor;
        if (newCursor != null) {
            notifyDataSetChanged();
        } else {
            notifyItemRangeRemoved(0, oldCursor .getCount());
        }

        return oldCursor;
    }

    @Override
    public int getItemCount() {
        if (cursor == null || cursor.getCount() == 0) return 1;

        return cursor.getCount();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tlvName;
        TextView tlvDescription;
        ImageButton tlvEdit;
        ImageButton tlvRemove;
        View itemView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tlvName = itemView.findViewById(R.id.tlv_name);
            tlvDescription = itemView.findViewById(R.id.tlv_description);
            tlvEdit = itemView.findViewById(R.id.tlv_edit);
            tlvRemove = itemView.findViewById(R.id.tlv_remove);
            this.itemView = itemView;
        }
    }

}
