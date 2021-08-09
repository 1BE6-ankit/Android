package np.com.ankitkoirala.top10downloader;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ListView listApps;

    private final String STORE_URL = "STORE_URL";
    private final String STORE_FEED_LIMIT = "STORE_FEED_LIMIT";

    String xmlUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";
    int feedLimit = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listApps = findViewById(R.id.xmlListView);

        downloadUrl(String.format(xmlUrl, feedLimit));
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        String savedXmlURL = (String) savedInstanceState.get(STORE_URL);
        int savedFeedLimit = (int) savedInstanceState.get(STORE_FEED_LIMIT);

        if(savedXmlURL != null) xmlUrl = savedXmlURL;
        if(savedFeedLimit == 10 || savedFeedLimit == 25) feedLimit = savedFeedLimit;

        downloadUrl(String.format(xmlUrl, feedLimit));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.feeds_menu, menu);
        if (feedLimit == 10) {
            menu.findItem(R.id.menuTop10).setChecked(true);
        } else {
            menu.findItem(R.id.menuTop25).setChecked(true);
        }
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(STORE_URL, xmlUrl);
        outState.putInt(STORE_FEED_LIMIT, feedLimit);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        String oldUrl = xmlUrl;
        boolean feedLimitChanged = false;

        switch (id) {
            case R.id.menuFree:
                xmlUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";
                break;
            case R.id.menuPaid:
                xmlUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=%d/xml";
                break;
            case R.id.menuSongs:
                xmlUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml";
                break;
            case R.id.menuTop10:
            case R.id.menuTop25:
                if (!item.isChecked()) {
                    feedLimit = 35 - feedLimit;
                    item.setChecked(true);
                    feedLimitChanged = true;
                }
                break;
            case R.id.menuRefresh:
                // do nothing
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        if(!oldUrl.equals(xmlUrl) || feedLimitChanged == true || id == R.id.menuRefresh) {
            downloadUrl(String.format(xmlUrl, feedLimit));
        }

        return true;
    }

    private void downloadUrl(String url) {
        Log.d(TAG, "downloadUrl: Starting DownloadData Async task");
        DownloadData downloadData = new DownloadData();
        downloadData.execute(url);
    }

    private class DownloadData extends AsyncTask<String, Void, String> {

        private static final String TAG = "DownloadData";

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d(TAG, "onPostExecute: parameter is " + s);
            ParseData parseData = new ParseData();
            boolean result = parseData.parse(s);

//            ArrayAdapter<FeedEntry> arrayAdapter = new ArrayAdapter<>(
//                    MainActivity.this,
//                    R.layout.list_item,
//                    parseData.getEntries()
//            );
//            listApps.setAdapter(arrayAdapter);
            FeedAdapter feedAdapter = new FeedAdapter(MainActivity.this, R.layout.list_record, parseData.getEntries());
            listApps.setAdapter(feedAdapter);
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: first parameter: " + strings[0]);
            String xmlData = downloadData(strings[0]);
            if (xmlData == null) {
                Log.e(TAG, "doInBackground: Error while downloading xml");
            }
            return xmlData;
        }

        private String downloadData(String urlPath) {
            StringBuilder xmlResult = new StringBuilder();

            try {
                URL url = new URL(urlPath);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                int responseCode = conn.getResponseCode();
                Log.d(TAG, "downloadData: Response code: " + responseCode);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                char[] bytesRead = new char[500];
                int nCharsRead;
                while (true) {
                    nCharsRead = bufferedReader.read(bytesRead);
                    if (nCharsRead < 0) {
                        break;
                    } else if (nCharsRead > 0) {
                        xmlResult.append(String.copyValueOf(bytesRead, 0, nCharsRead));
                    }
                }

                bufferedReader.close();

                return xmlResult.toString();
            } catch (MalformedURLException e) {
                Log.e(TAG, "downloadData: Incorrect URL: " + e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "downloadData: IOexecption reading data: " + e.getMessage());
            } catch (SecurityException e) {
                Log.e(TAG, "downloadData: Security exception: " + e.getMessage());
            }

            return null;
        }

    }
}
