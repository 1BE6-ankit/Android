package np.com.ankitkoirala.toprssfeeds;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FeedAdapter<T extends FeedEntry> extends ArrayAdapter {

    ArrayList<T> entries;
    Context context;
    int resourceId;

    public FeedAdapter(@NonNull Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);
        this.entries = (ArrayList<T>) objects;
        this.context = context;
        this.resourceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        FeedEntry entry = entries.get(position);
        ViewHolder viewHolder;

        if(convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(resourceId, parent, false);

            TextView txtName = convertView.findViewById(R.id.txtName);
            TextView txtArtist = convertView.findViewById(R.id.txtArtist);
            TextView txtSummary = convertView.findViewById(R.id.txtSummary);

            viewHolder = new ViewHolder(txtName, txtArtist, txtSummary);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if(entry != null) {
            viewHolder.txtName.setText(entry.getName());
            viewHolder.txtArtist.setText(entry.getArtist());
            viewHolder.txtSummary.setText(entry.getSummary());
        }

        return convertView;
    }

    private class ViewHolder {
        TextView txtName;
        TextView txtArtist;
        TextView txtSummary;

        public ViewHolder(TextView txtName, TextView txtArtist, TextView txtSummary) {
            this.txtName = txtName;
            this.txtArtist = txtArtist;
            this.txtSummary = txtSummary;
        }
    }
}
