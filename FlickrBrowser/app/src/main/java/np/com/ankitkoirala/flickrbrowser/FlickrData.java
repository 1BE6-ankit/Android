package np.com.ankitkoirala.flickrbrowser;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

class FlickrData extends AsyncTask<String, Void, ArrayList<Photo>> implements GetRawData.OnDownloadComplete {

    private static final String TAG = "FlickrData";

    private OnFlickrDataDndComplete callback;
    private ArrayList<Photo> photos = null;
    private String baseUrl;
    private String lang;
    boolean selectAllTags;
    private boolean runningOnSameThread;

    interface OnFlickrDataDndComplete {
        void onFlicrDataDndComplete(ArrayList<Photo> photos, DownloadStatus status);
    }

    public FlickrData(OnFlickrDataDndComplete callback, String baseUrl, String lang, boolean selectAllTags) {
        this.callback = callback;
        this.baseUrl = baseUrl;
        this.lang = lang;
        this.selectAllTags = selectAllTags;
    }

    protected void executeOnSameThread(String tags) {
        Log.d(TAG, "executeOnSameThread: starts");
        runningOnSameThread = true;
        String url = generateUrl(tags);
        GetRawData getRawData = new GetRawData(this);
        getRawData.execute(url);
        Log.d(TAG, "executeOnSameThread: ends");
    }

    @Override
    protected void onPostExecute(ArrayList<Photo> photos) {
        Log.d(TAG, "onPostExecute: starts");
        if(callback != null) {
            callback.onFlicrDataDndComplete(photos, DownloadStatus.OK);
        }
        Log.d(TAG, "onPostExecute: ends");
    }

    @Override
    protected ArrayList<Photo> doInBackground(String... strings) {
        Log.d(TAG, "doInBackground: starts");
        String url = generateUrl(strings[0]);
        GetRawData getRawData = new GetRawData(this);
        getRawData.runInSameThread(url);
        Log.d(TAG, "doInBackground: ends");
        return photos;
    }

    private String generateUrl(String tags) {
        return Uri.parse(this.baseUrl).buildUpon()
                .appendQueryParameter("tags", tags)
                .appendQueryParameter("lang", this.lang)
                .appendQueryParameter("format", "json")
                .appendQueryParameter("nojsoncallback", "1")
                .appendQueryParameter("tagmode", (selectAllTags ? "ALL" : "ANY"))
                .build()
                .toString();
    }

    @Override
    public void onDownloadComplete(String result, DownloadStatus status) {
        Log.d(TAG, "onDownloadComplete: called with status = " + status);

        if(status == DownloadStatus.ERROR) {
            Log.d(TAG, "onDownloadComplete: There was an erorr in download");
        } else if(status == DownloadStatus.OK) {
            Log.d(TAG, "onDownloadComplete: Raw data successfully downloaded");

            try{
                photos = new ArrayList<>();
                JSONArray items = new JSONObject(result).getJSONArray("items");
                for(int i=0; i<items.length(); i++) {
                    JSONObject item = items.getJSONObject(i);

                    String title = item.getString("title");
                    String author = item.getString("author");
                    String authorId = item.getString("author_id");
                    String tags = item.getString("tags");

                    JSONObject media = item.getJSONObject("media");
                    String image = media.getString("m");
                    String link = image.replaceFirst("_m.", "_b.");

                    Photo photo = new Photo(title, author, authorId, tags, link, image);
                    this.photos.add(photo);

                    Log.d(TAG, "onDownloadComplete: Photo parsed: " + photo.toString());
                }
            } catch (JSONException e) {
                Log.e(TAG, "onDownloadComplete: Error while parsing json: " + e.getMessage());
            }
        }

        if(this.callback != null && runningOnSameThread) {
            callback.onFlicrDataDndComplete(this.photos, status);
        }

        Log.d(TAG, "onDownloadComplete: Complete");
    }
}
