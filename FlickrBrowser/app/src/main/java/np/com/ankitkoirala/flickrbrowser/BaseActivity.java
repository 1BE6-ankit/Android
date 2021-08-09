package np.com.ankitkoirala.flickrbrowser;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class BaseActivity extends AppCompatActivity {

    final static String PHOTO_TRANSFER = "PHOTO_TRANSFER";
    final static String FLICKR_TAGS = "FLICKR_TAGS";

    public void showHome(boolean show) {
        ActionBar actionBar = getSupportActionBar();
        Toolbar toolbar = null;

        if(actionBar == null) {
            toolbar = findViewById(R.id.toolbar);

            if(toolbar != null) {
                setSupportActionBar(toolbar);
                actionBar = getSupportActionBar();
            }
        }

        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(show);
        }
    }

}
