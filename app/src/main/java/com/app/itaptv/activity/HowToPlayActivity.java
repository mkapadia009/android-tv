package com.app.itaptv.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.itaptv.R;
import com.app.itaptv.structure.GameData;
import com.app.itaptv.utils.AnalyticsTracker;
import com.app.itaptv.utils.Utility;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

public class HowToPlayActivity extends BaseActivity {

    ImageView ivGameImage;
    TextView tvDescription;
    GameData gameData;
    public static String GAME_DATA = "gameData";
    private static final String BOLLYWOOD_TRIVIA = "bollywood-trivia";
    private static final String CRICKET_TRIVIA = "cricket";
    private static final String KABADDI_TRIVIA = "kabaddi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setTheme(R.style.Theme_Leanback);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_how_to_play);
        setToolbar(false);
        showToolbarBackButton(R.drawable.white_arrow);
        setToolbarTitle(getResources().getString(R.string.label_how_to_play));
        setTitleColor(R.color.colorWhite);
        showToolbarTitle(true);
        init();
        showAd("btHowToPlay");
    }

    /**
     * Initializes views
     */
    private void init() {
        ivGameImage = findViewById(R.id.ivGameImage);
        tvDescription = findViewById(R.id.tvDescription);

        setDisplayData();

    }

    public void setDisplayData() {
        if (getIntent() != null) {
            gameData = getIntent().getExtras().getBundle("Bundle").getParcelable(GAME_DATA);
            Glide.with(this)
                    .load(gameData.rectangle_image)
                    .thumbnail(0.1f)
                    .apply(new RequestOptions().centerCrop().dontAnimate().diskCacheStrategyOf(DiskCacheStrategy.ALL))
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(ivGameImage);

            switch (gameData.postName) {
                case BOLLYWOOD_TRIVIA:
                    tvDescription.setText(getResources().getString(R.string.bollywood_how_to_play_desc));
                    break;
                case CRICKET_TRIVIA:
                    tvDescription.setText(getResources().getString(R.string.cricket_how_to_play_desc));
                    break;
                case KABADDI_TRIVIA:
                    tvDescription.setText(getResources().getString(R.string.kabaddi_how_to_play_desc));
                    break;

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        AnalyticsTracker.resumeTimer(AnalyticsTracker.GAME);
    }
}
