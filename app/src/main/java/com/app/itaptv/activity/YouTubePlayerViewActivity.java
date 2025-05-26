package com.app.itaptv.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.itaptv.R;
import com.app.itaptv.structure.FeedContentData;
import com.app.itaptv.utils.Log;
import com.app.itaptv.utils.Utility;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import org.jetbrains.annotations.NotNull;

public class YouTubePlayerViewActivity extends BaseActivity {

    public static String CONTENT_DATA = "contentData";
    FeedContentData feedContentData;
    boolean isFullScreen = false;
    String TAG = "YouTubePlayerViewActivity";

    YouTubePlayerView youTubePlayerView;
    TextView tvAudioTitleslider;
    ImageButton ibClose;
    RelativeLayout playerHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_you_tube_player_view);
        setTheme(R.style.Theme_Leanback);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        if (getIntent() != null) {
            feedContentData = (FeedContentData) getIntent().getParcelableExtra(CONTENT_DATA);
        }

        tvAudioTitleslider = findViewById(R.id.tvAudioTitleslider);
        ibClose = findViewById(R.id.ibclose);
        playerHolder = findViewById(R.id.playerHolder);

        youTubePlayerView = findViewById(R.id.youtube_player_view);
        getLifecycle().addObserver(youTubePlayerView);

        tvAudioTitleslider.setText(feedContentData.postTitle);

        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NotNull YouTubePlayer youTubePlayer) {
                super.onReady(youTubePlayer);
                String videoId = feedContentData.url;
                youTubePlayer.loadVideo(videoId, 0);
            }
        });


        youTubePlayerView.addFullScreenListener(new YouTubePlayerFullScreenListener() {
            @Override
            public void onYouTubePlayerEnterFullScreen() {
                //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                openFullscreenDialog();
            }

            @Override
            public void onYouTubePlayerExitFullScreen() {
                //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
                closeFullscreenDialog();
                //closeFullscreenDialog();
            }
        });

        ibClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public void openFullscreenDialog() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
        // int pixels = (int) (600 * scale + 0.5f);
        // Gets the layout params that will allow you to resize the layout
        ViewGroup.LayoutParams params = youTubePlayerView.getLayoutParams();
        // Changes the height and width to the specified *pixels*
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        youTubePlayerView.setLayoutParams(params);
        ibClose.setVisibility(View.GONE);

        //mFullScreenDialog.show();
        isFullScreen = true;
    }

    public void closeFullscreenDialog() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (250 * scale + 0.5f);
        // Gets the layout params that will allow you to resize the layout
        ViewGroup.LayoutParams params = youTubePlayerView.getLayoutParams();
        // Changes the height and width to the specified *pixels*
        params.height = pixels;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        youTubePlayerView.setLayoutParams(params);
        ibClose.setVisibility(View.VISIBLE);
        isFullScreen = false;
    }

    @Override
    public void onBackPressed() {
        if (isFullScreen) {
            closeFullscreenDialog();
        } else {
            super.onBackPressed();
        }
    }
}