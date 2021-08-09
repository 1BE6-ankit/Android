package np.com.ankitkoirala.flickrbrowser;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FlickrDataAdapter extends RecyclerView.Adapter<FlickrDataAdapter.FlickrViewHolder>{

    private static final String TAG = "FlickrDataAdapter";
    private Context context;
    private ArrayList<Photo> photos;

    public FlickrDataAdapter(Context context, ArrayList<Photo> photos) {
        this.context = context;
        this.photos = photos;
    }

    @NonNull
    @Override
    public FlickrViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.browse, parent, false);
        FlickrViewHolder viewHolder = new FlickrViewHolder(view);

        return viewHolder;
    }

    public Photo getItem(int pos) {
        if(this.photos != null && this.photos.size() > pos) {
            return this.photos.get(pos);
        }

        return null;
    }

    public void updatePhotoList(ArrayList<Photo> photos) {
        this.photos = photos;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull FlickrViewHolder holder, int position) {
        Photo photo = photos.get(position);
        Picasso.get()
                .load(photo.getImage())
                .error(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)
                .into(holder.imageView);
        holder.textView.setText(photo.getTitle());
    }

    @Override
    public int getItemCount() {
        if(photos != null) return photos.size();
        return 0;
    }

    static class FlickrViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "FlickrViewHolder";
        TextView textView;
        ImageView imageView;

        public FlickrViewHolder(View itemView) {
            super(itemView);
            Log.d(TAG, "FlickrViewHolder: Constructor called");
            this.textView = itemView.findViewById(R.id.title);
            this.imageView = itemView.findViewById(R.id.thumbnail);
        }
    }

}
