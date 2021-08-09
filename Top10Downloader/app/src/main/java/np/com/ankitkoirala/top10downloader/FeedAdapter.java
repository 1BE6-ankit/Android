package np.com.ankitkoirala.top10downloader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class FeedAdapter extends ArrayAdapter {

    private final int layoutResource;
    private final LayoutInflater layoutInflater;
    private List<FeedEntry> entries;

    public FeedAdapter(@NonNull Context context, int resource, List<FeedEntry> entries) {
        super(context, resource);
        this.layoutResource = resource;
        this.layoutInflater = LayoutInflater.from(context);
        this.entries = entries;
    }

    @Override
    public int getCount() {
        return entries.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView == null) {
            convertView = layoutInflater.inflate(layoutResource, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        FeedEntry entry = entries.get(position);

        viewHolder.tvName.setText(entry.getName());
        viewHolder.tvArtist.setText(entry.getArtist());
        viewHolder.tvSummary.setText(entry.getSummary());

        return convertView;
    }

    private class ViewHolder {
        final TextView tvName;
        final TextView tvArtist;
        final TextView tvSummary;

        public ViewHolder(View view) {
            this.tvName = view.findViewById(R.id.tvName);
            this.tvArtist = view.findViewById(R.id.tvArtist);
            this.tvSummary = view.findViewById(R.id.tvSummary);
        }
    }
}
