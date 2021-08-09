package np.com.ankitkoirala.youtubeplayer;

import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class YoutubeActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    private static final String TAG = "YoutubeActivity";

    static final String GOOGLE_API_KEY = "AIzaSyDV6nG1t2fNBdXdrrcWgfrOBVVBUcaJZ8k";
    static final String YOUTUBE_VIDEO_ID = "YkADj0TPrJA";
    static final String YOUTUBE_PLAYLIST_ID = "YkADj0TPrJA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_youtube);
        ConstraintLayout constraintLayout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.activity_youtube, null);
        setContentView(constraintLayout);

        YouTubePlayerView playerView = new YouTubePlayerView(this);
        playerView.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        constraintLayout.addView(playerView);
        playerView.initialize(GOOGLE_API_KEY, this);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        Log.d(TAG, "onInitializationSuccess: Provider is: " + provider.getClass().toString());
        Toast.makeText(this, "Youtube Player is initialized", Toast.LENGTH_SHORT).show();
        youTubePlayer.setPlaybackEventListener(this.playbackEventListener);
        youTubePlayer.setPlayerStateChangeListener(this.playerStateChangeListener);

        if(!wasRestored) {
            youTubePlayer.cueVideo(YOUTUBE_VIDEO_ID);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        int REQUEST_CODE = 1;

        if(youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(this, REQUEST_CODE).show();
        } else {
            String errMessage = String.format("Error while initializing Youtube Player: %s", youTubeInitializationResult.toString());
            Toast.makeText(this, errMessage, Toast.LENGTH_LONG).show();
        }
    }

    private YouTubePlayer.PlaybackEventListener playbackEventListener = new YouTubePlayer.PlaybackEventListener() {
        @Override
        public void onPlaying() {
            Toast.makeText(YoutubeActivity.this, "Video is playing", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPaused() {
            Toast.makeText(YoutubeActivity.this, "Video is paused", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStopped() {
            Toast.makeText(YoutubeActivity.this, "Video is stopped", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onBuffering(boolean b) {
            Toast.makeText(YoutubeActivity.this, "Video is buffering", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onSeekTo(int i) {
            Toast.makeText(YoutubeActivity.this, "Video is seeked to: " + i, Toast.LENGTH_SHORT).show();
        }
    };

    private YouTubePlayer.PlayerStateChangeListener playerStateChangeListener = new YouTubePlayer.PlayerStateChangeListener() {
        @Override
        public void onLoading() {
            Toast.makeText(YoutubeActivity.this, "Video is loading", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onLoaded(String s) {
            Toast.makeText(YoutubeActivity.this, "Video has been loaded", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAdStarted() {
            Toast.makeText(YoutubeActivity.this, "Fckn ad is started", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onVideoStarted() {
            Toast.makeText(YoutubeActivity.this, "Video is playing", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onVideoEnded() {
            Toast.makeText(YoutubeActivity.this, "Video has ended", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(YouTubePlayer.ErrorReason errorReason) {
            Toast.makeText(YoutubeActivity.this, "An error occured", Toast.LENGTH_SHORT).show();
        }
    };
}
