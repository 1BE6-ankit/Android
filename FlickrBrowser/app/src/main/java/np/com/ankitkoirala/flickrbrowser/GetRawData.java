package np.com.ankitkoirala.flickrbrowser;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

enum DownloadStatus { IDLE, NOT_INITIALIZED, PROCESSING, ERROR, OK}

class GetRawData extends AsyncTask<String, Void, String> {

    private static final String TAG = "GetRawData";

    private DownloadStatus downloadStatus;
    private OnDownloadComplete callback;

    interface OnDownloadComplete {
        void onDownloadComplete(String result, DownloadStatus status);
    }

    public GetRawData(OnDownloadComplete callback) {
        this.downloadStatus = DownloadStatus.IDLE;
        this.callback = callback;
    }

    void runInSameThread(String url) {
        Log.d(TAG, "runInSameThread: starts");
        if(callback != null) {
            callback.onDownloadComplete(doInBackground(url), downloadStatus);
        }
        Log.d(TAG, "runInSameThread: ends");
    }

    @Override
    protected void onPostExecute(String s) {
        Log.d(TAG, "onPostExecute: parameter is: " + s);
        callback.onDownloadComplete(s, this.downloadStatus);
    }

    @Override
    protected String doInBackground(String... strings) {
        if(strings == null) {
            downloadStatus = DownloadStatus.NOT_INITIALIZED;
            return null;
        }

        URL url;
        HttpURLConnection connection = null;
        BufferedReader bufferedReader = null;

        try {
            downloadStatus = DownloadStatus.PROCESSING;

            StringBuilder builder = new StringBuilder();

            url = new URL(strings[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int response = connection.getResponseCode();
            Log.d(TAG, "doInBackground: Response code is: " + response);

            bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while(null != (line = bufferedReader.readLine())) {
                builder.append(line).append('\n');
            }

            downloadStatus = DownloadStatus.OK;
            return builder.toString();
        } catch (MalformedURLException e) {
            Log.e(TAG, "doInBackground: url error - " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "doInBackground: IOException - " + e.getMessage());
        } finally {
            if(connection != null) {
                connection.disconnect();
            }

            if(bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    Log.e(TAG, "doInBackground: closing buffered reader: " + e.getMessage());
                }
            }
        }

        downloadStatus = DownloadStatus.ERROR;
        return null;
    }
}
