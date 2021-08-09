package np.com.ankitkoirala.tasktimer;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Locale;

public class DurationsRVAdapter extends RecyclerView.Adapter<DurationsRVAdapter.ViewHolder> {

    private static final String TAG = "DurationsRVAdapter";

    Cursor cursor;
    java.text.DateFormat dateFormat;

    public DurationsRVAdapter(Context context, Cursor cursor) {
        this.cursor = cursor;
        this.dateFormat = DateFormat.getDateFormat(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_durations_items, parent, false);
        return new DurationsRVAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DurationsRVAdapter.ViewHolder holder, int position) {
        if(cursor!=null && cursor.getCount() > 0) {
            if (!cursor.moveToPosition(position)) {
                throw new CursorIndexOutOfBoundsException(TAG + ": The given data position is not found");
            }

            String name = cursor.getString(cursor.getColumnIndex(DurationsContract.Columns.DURATIONS_TASK_NAME));
            String description = cursor.getString(cursor.getColumnIndex(DurationsContract.Columns.DURATIONS_DESCRIPTION));
            long startTime = cursor.getLong(cursor.getColumnIndex(DurationsContract.Columns.DURATIONS_START_TIME));
            long duration = cursor.getLong(cursor.getColumnIndex(DurationsContract.Columns.DURATIONS_DURATION));

            holder.name.setText(name);
            if(holder.description != null) {
                holder.description.setText(description);
            }

            String userDate = dateFormat.format(startTime * 1000);
            String totalTime = getDurationFormat(duration);

            holder.start.setText(userDate);
            holder.duration.setText(totalTime);
        }

    }

    @Override
    public int getItemCount() {
        return cursor != null ? cursor.getCount() : 0;
    }

    private String getDurationFormat(long seconds) {
        long hours = seconds / (60*60);
        long remainder = seconds - hours * 60 * 60;
        long minutes = remainder / 60;
        long sec = (remainder - minutes*60);

        return String.format(Locale.GERMANY, "%02d.%02d.%02d", hours, minutes, sec);
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

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView description;
        TextView start;
        TextView duration;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.td_name);
            description = itemView.findViewById(R.id.td_description);
            start = itemView.findViewById(R.id.td_start);
            duration = itemView.findViewById(R.id.td_duration);
        }
    }

}
