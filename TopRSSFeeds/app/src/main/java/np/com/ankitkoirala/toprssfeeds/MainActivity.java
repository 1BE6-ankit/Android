package np.com.ankitkoirala.toprssfeeds;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    String xmlUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml";
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listVIew);

        XmlDownloader downloader = new XmlDownloader();
        downloader.execute(xmlUrl);
    }

    private class XmlDownloader extends AsyncTask<String, String, String> {

        private static final String TAG = "XmlDownloader";

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            XmlParser parser = new XmlParser();
            boolean res = parser.parse(s);
            if (res) {
                ArrayList<FeedEntry> entries = parser.getEntries();
                FeedAdapter<FeedEntry> feedAdapter = new FeedAdapter<>(MainActivity.this, R.layout.entry_view, entries);
                listView.setAdapter(feedAdapter);

                for(FeedEntry entry: entries) {
                    Log.d(TAG, "onPostExecute: " + entry.toString());
                }
            } else {
                Log.e(TAG, "onPostExecute: Xml not parsed");
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            String url = strings[0];
            return downloadXml(url);
        }

        private String downloadXml(String urlString) {
            try {
                StringBuilder stringBuilder = new StringBuilder();
                char[] buffer = new char[500];

                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                int nCharsRead;

                while(true) {
                    nCharsRead = bufferedReader.read(buffer);
                    if(nCharsRead < 0) {
                        break;
                    } else if(nCharsRead > 0) {
                        stringBuilder.append(String.copyValueOf(buffer, 0, nCharsRead));
                    }
                }

                return stringBuilder.toString();
            } catch (MalformedURLException e) {
                Log.d(TAG, "downloadXml: Error in url: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "downloadXml: IOException: " + e.getMessage());
            }

            return null;
        }
    }
}
