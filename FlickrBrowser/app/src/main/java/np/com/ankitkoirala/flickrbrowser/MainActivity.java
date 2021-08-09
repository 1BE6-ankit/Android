package np.com.ankitkoirala.flickrbrowser;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends BaseActivity implements FlickrData.OnFlickrDataDndComplete, RecyclerViewItemClickListener.OnClickRecyclerListener {

    private static final String TAG = "MainActivity";
    private FlickrDataAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showHome(false);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FlickrDataAdapter(this, null);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(new RecyclerViewItemClickListener(this, recyclerView, this));
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: Starts");
        super.onResume();
        FlickrData flickrData = new FlickrData(this,"https://www.flickr.com/services/feeds/photos_public.gne", "en-us", true);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String flickrQuery = pref.getString(FLICKR_TAGS, "GameStop");
        flickrData.execute(flickrQuery);
        Log.d(TAG, "onResume: ends");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_search) {
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFlicrDataDndComplete(ArrayList<Photo> photos, DownloadStatus status) {
        if(status == DownloadStatus.OK) {
            Log.d(TAG, "onFlicrDataDndComplete: data is: " + photos);
            this.adapter.updatePhotoList(photos);
        } else {
            Log.e(TAG, "onFlicrDataDndComplete: No OK status: " + status);
        }
    }

    @Override
    public void onItemClick(View v, int position) {
        Log.d(TAG, "onItemClick: Item clicked");
        Toast.makeText(MainActivity.this, "Item clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemLongClick(View v, int position) {
        Log.d(TAG, "onItemLongClick: Item long clicked");

        Intent myIntent = new Intent(MainActivity.this, PhotoDetailActivity.class);
        Photo photo = adapter.getItem(position);
        if(photo != null) {
            Toast.makeText(MainActivity.this, "Item long-clicked", Toast.LENGTH_SHORT).show();
            myIntent.putExtra(PHOTO_TRANSFER,  photo);
            MainActivity.this.startActivity(myIntent);
        } else {
            Toast.makeText(MainActivity.this, "Photo not available", Toast.LENGTH_SHORT).show();
        }

    }
}
