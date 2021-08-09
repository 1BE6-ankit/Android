package np.com.ankitkoirala.flickrbrowser;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class PhotoDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);
        showHome(true);

        Intent intent = getIntent();
        Photo photo = (Photo) intent.getSerializableExtra(PHOTO_TRANSFER);
        if (photo != null) {
            Resources resources = getResources();
            ((TextView) findViewById(R.id.photoTitle)).setText(resources.getString(R.string.content_photoTitle, photo.getTitle()));
            ((TextView) findViewById(R.id.photoAuthor)).setText(resources.getString(R.string.content_photoAuthor, photo.getAuthor()));
            ((TextView) findViewById(R.id.photoTags)).setText(photo.getTags());

            ImageView imageView = findViewById(R.id.photoImage);
            Picasso.get().load(photo.getLink())
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(imageView);
        }
    }

}
