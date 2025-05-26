package com.app.itaptv.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.app.itaptv.API.APIManager;
import com.app.itaptv.API.APIMethods;
import com.app.itaptv.API.APIRequest;
import com.app.itaptv.API.Url;
import com.app.itaptv.BuildConfig;
import com.app.itaptv.MyApp;
import com.app.itaptv.R;
import com.app.itaptv.roomDatabase.AnalyticsData;
import com.app.itaptv.roomDatabase.ListRoomDatabase;
import com.app.itaptv.utils.Constant;
import com.app.itaptv.utils.EmptyStateManager;
import com.app.itaptv.utils.LocalStorage;
import com.app.itaptv.utils.Log;
import com.app.itaptv.utils.Utility;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.AssetDataSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.trackier.sdk.TrackierSDK;
import com.yupple.campaigntracking.CampaingnTracking;
import com.yupple.campaigntracking.Interfaces.TrackingInterface;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class SplashActivity extends BaseActivity implements TrackingInterface {

    //Added for Google Analytics
    private FirebaseAnalytics mFirebaseAnalytics;

    private ExoPlayer player;
    private PlayerView playerView;
    private ImageView imageView;

    EmptyStateManager emptyState;

    View emptyLayout;

    private ListRoomDatabase listRoomDatabase;

    String type = "";
    String postID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setTheme(R.style.Theme_Leanback);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        emptyLayout = findViewById(R.id.emptyLayout);
        playerView = findViewById(R.id.splashPlayer);
        imageView = findViewById(R.id.imageView2);
        globalSettingApiCall();
        setUpEmptyState();

        callAnalyticsApi();
        //emptyState.hide();
        listRoomDatabase = ListRoomDatabase.getDatabase(this);
        List<AnalyticsData> analyticsData = listRoomDatabase.mediaDAO().getAnalytics();
        String json = new Gson().toJson(analyticsData);
        MyApp application = (MyApp) getApplication();
        application.isShowNotification = true;

        if (MyApp.getInstance().isFirstLoad) {
            MyApp.getInstance().isFirstLoad = false;
            if (BuildConfig.BUILD_TYPE.equalsIgnoreCase(Constant.DEBUG)) {
                //showLandingScreen();
                playSplashVideo();
            } else {
                playSplashVideo();
            }
        }else{
            showLandingScreen();
        }

        if (getIntent() != null) {
            Uri uri = getIntent().getData();
            if (uri != null) {
                try {
                    String parameters = Utility.decryptData((uri.toString().replace("https://www.itap.online?", "")), Constant.getSecretKey());
                    Uri data = Uri.parse("https://www.itap.online?" + parameters);
                    type = data.getQueryParameter(Constant.TYPE);
                    postID = data.getQueryParameter(Constant.POSTID);
                } catch (Exception e) {

                }
            }

            if (getIntent().getExtras() != null) {
                try {
                    String data = getIntent().getExtras().getString("page");
                    if (data != null) {
                        Constant.NOTIFICATION_PAGE = data;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void imageSplashScreen() {
        playerView.setVisibility(View.INVISIBLE);
        imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.splashscreenimage));
        new Handler().postDelayed(() -> {
            try {
                showLandingScreen();
            } catch (Exception ignored) {

            }
        }, 1000);
    }

    private boolean resumePlayer = false;

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.setPlayWhenReady(false);
            resumePlayer = true;
            playerView.setPlayer(null);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (player != null && resumePlayer) {
            player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
            player.setPlayWhenReady(true);
            playerView.setPlayer(player);
        }

        emptyState.hide();

        /*if (Utility.isConnectingToInternet(getApplicationContext())) {
            //emptyState.setMessage(APIManager.GENERIC_API_ERROR_MESSAGE);
            playerView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
            emptyState.hide();
        } else {
            playerView.setVisibility(View.GONE);
            imageView.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
            emptyState.setActionButton(EmptyStateManager.ACTION_RETRY);
            emptyState.showNoConnectionState();
        }*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.setPlayWhenReady(false);
            player.stop();
            player.release();
            player = null;
        }
        if (playerView != null) {
            playerView.setPlayer(null);
        }
    }

    @Override
    public void onBackPressed() {
    }

    private void playSplashVideo() {
        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
        playerView.setUseController(false);
        playerView.requestFocus();

        Uri uri = null;
        if (Utility.isTelevision()) {
            uri = Uri.parse("assets:///tv_splash_screen.mp4");
        } else {
            if (BuildConfig.BUILD_TYPE.equalsIgnoreCase(Constant.DEBUG)) {
                uri = Uri.parse("assets:///splash_video.mp4");
            } else if (BuildConfig.BUILD_TYPE.equalsIgnoreCase(Constant.DEMO)) {
                uri = Uri.parse("assets:///eskay.mp4");
            } else if (BuildConfig.BUILD_TYPE.equalsIgnoreCase(Constant.RELEASE)) {
                uri = Uri.parse("assets:///splash_video.mp4");
            }
        }

        DataSpec dataSpec = new DataSpec(uri);
        final AssetDataSource assetDataSource = new AssetDataSource(this);
        try {
            assetDataSource.open(dataSpec);

        } catch (AssetDataSource.AssetDataSourceException e) {
            e.printStackTrace();
            // imageSplashScreen();
            return;
        }

        DataSource.Factory factory = () -> assetDataSource;

        MediaSource mediaSource = new ProgressiveMediaSource.Factory(factory)
                .createMediaSource(MediaItem.fromUri(assetDataSource.getUri()));

        player = new ExoPlayer.Builder(SplashActivity.this).build();
        player.addListener(new Player.Listener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == Player.STATE_ENDED) {
                    //imageSplashScreen();
                    showLandingScreen();
                }
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                // imageSplashScreen();
            }
        });
        if (Utility.isTelevision()) {
            player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT);
        }
        player.prepare(mediaSource);
        player.setPlayWhenReady(true);

        /*
          Volume is set to 0 as per requirement
          Use as per the case
         */
        player.setVolume(1.0f);

        playerView.setPlayer(player);
    }

    /**
     * Redirect to login page if not logged in
     * else to Home page
     */
    private void showLandingScreen() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        String requiredVersionName = LocalStorage.getRequiredVersion(LocalStorage.KEY_APP_VERSION_NUMBER, getApplicationContext());
        String versionName = LocalStorage.getAppVersion();
        if (requiredVersionName != null) {
            if (!requiredVersionName.isEmpty() && !versionName.isEmpty()) {
                if (Utility.isAppUpdateRequired(requiredVersionName, versionName)) {
                    getApplicationContext().sendBroadcast(new Intent("mandatoryappupdate"));
                } else {
                    showScreen();
                }
                /*if (!requiredVersionName.equals(versionName)) {
                    getApplicationContext().sendBroadcast(new Intent("mandatoryappupdate"));
                }else{
                    showScreen();
                }*/
            } else {
                showScreen();
            }
        } else {
            showScreen();
        }
    }

    public void globalSettingApiCall() {
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        if (!BuildConfig.DEBUG) {
            mFirebaseAnalytics.setUserProperty("ct_objectId", Objects.requireNonNull(TrackierSDK.getTrackierId()));
        }
        // needs to add loader and after successful response showLandingPage
        APIMethods.getGlobalSetting(SplashActivity.this, (msg) -> {
            if (new APIRequest("", 0, null, null, SplashActivity.this).getHeader().equalsIgnoreCase(Constant.INDUSOS)) {
                if (LocalStorage.getUserData() != null && LocalStorage.getLocationData() != null) {
                    CampaingnTracking.getCampaignTrackingAPi(SplashActivity.this, LocalStorage.getCampaignId(), LocalStorage.getUserData().mobile, LocalStorage.getLocationData().getCity());
                }
            }
            //emptyState.hide();
        });
    }

    public void showScreen() {
        if (LocalStorage.isUserLoggedIn()) {

            if (!LocalStorage.getValue(LocalStorage.IS_PRESENTER_LOGGED_IN, false, Boolean.class)) {
                //if (LocalStorage.getValue(LocalStorage.KEY_LANG_PREFER, 0, Integer.class) == 0) {
                //    startActivity(new Intent(this, LanguagePrefActivity.class));
                //} else {
                // Analytics test code
                //if (!BuildConfig.DEBUG) {
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Test");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Splash Screen Analytics Test");
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                //}
                if (!LocalStorage.getUserData().mobile.isEmpty()) {
                    startActivity(new Intent(this, HomeActivity.class).setAction(getString(R.string.no_action)).putExtra(Constant.TYPE, type).putExtra(Constant.POSTID, postID));
                } else {
                    startActivity(new Intent(this, MobileVerificationActivity.class).putExtra(Constant.TYPE, type).putExtra(Constant.POSTID, postID));
                }
                // startActivity(new Intent(this, HomeActivity.class).setAction(getString(R.string.no_action)));
                //}
            } else {
                //startActivity(new Intent(this, PresenterLiveActivity.class));
            }
        } else {
            startActivity(new Intent(this, LoginActivity.class).setAction(getString(R.string.action_splash_login)).putExtra(Constant.TYPE, type).putExtra(Constant.POSTID, postID));
        }
        finish();
    }

    /**
     * Initialization of Empty State
     */
    void setUpEmptyState() {
        emptyState = EmptyStateManager.setUpInActivity(SplashActivity.this, new EmptyStateManager.ActionBtnCallBack() {
            @Override
            public void onBtnClick(String action) {
                if (action.equals(EmptyStateManager.ACTION_RETRY)) {
                    globalSettingApiCall();
                }
            }
        });
    }

    @Override
    public void Responses(boolean b, String s) {
        Log.i(TAG, s);
    }

    private void callAnalyticsApi() {
        Calendar cal = Calendar.getInstance();
        cal.clear(Calendar.HOUR);
        cal.clear(Calendar.HOUR_OF_DAY);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

        long now = cal.getTimeInMillis();
        Log.i("", String.valueOf(now));
        long diffMillis = now - LocalStorage.getValue(LocalStorage.KEY_TIME, 0, Long.class);
        if (diffMillis >= (3600000 * 24)) {
            // store now (i.e. in shared prefs)
            LocalStorage.putValue(now, LocalStorage.KEY_TIME);
            // do the check
            if (BuildConfig.BUILD_TYPE.equalsIgnoreCase("release")) {
                postAnalytics();
            }
        } else {
            // too early
            Log.i("", "Too Early");
        }
    }

    private void postAnalytics() {
        try {
            listRoomDatabase = ListRoomDatabase.getDatabase(this);
            List<AnalyticsData> analyticsData = listRoomDatabase.mediaDAO().getAnalytics();

            JsonObject jsonObj = new JsonObject();
            // array to JsonArray
            JsonArray jsonArray = new Gson().toJsonTree(analyticsData).getAsJsonArray();
            // ArrayList to JsonArray
            jsonObj.add("analytics", jsonArray);

            APIRequest apiRequest = new APIRequest(Url.ANALYTICS, Request.Method.POST, null, null, this);
            apiRequest.showLoader = false;
            APIManager.jsonRequest(apiRequest, null, jsonObj);

        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }
}
