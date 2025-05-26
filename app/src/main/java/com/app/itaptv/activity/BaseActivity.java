package com.app.itaptv.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.app.itaptv.API.APIManager;
import com.app.itaptv.API.APIMethods;
import com.app.itaptv.API.APIRequest;
import com.app.itaptv.API.APIResponse;
import com.app.itaptv.API.Url;
import com.app.itaptv.LocaleHelper;
import com.app.itaptv.MyApp;
import com.app.itaptv.MyBroadcastReceiver;
import com.app.itaptv.R;
import com.app.itaptv.custom_interface.IOnBackPressed;
import com.app.itaptv.custom_interface.SliderListener;
import com.app.itaptv.custom_widget.DotsIndicatorDecoration;
import com.app.itaptv.holder.CustomAdSliderHolder;
import com.app.itaptv.permissionHandling.PermissionActivity;
import com.app.itaptv.service.MyService;
import com.app.itaptv.structure.AdFieldsData;
import com.app.itaptv.structure.AdMobData;
import com.app.itaptv.structure.FeedContentData;
import com.app.itaptv.utils.Constant;
import com.app.itaptv.utils.CustomLinearLayoutManager;
import com.app.itaptv.utils.LocalStorage;
import com.app.itaptv.utils.Log;
import com.app.itaptv.utils.Utility;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;
import com.kalpesh.krecycleradapter.Interface.KRecyclerItemClickListener;
import com.kalpesh.krecycleradapter.Interface.KRecyclerViewHolderCallBack;
import com.kalpesh.krecycleradapter.KRecyclerViewAdapter;
import com.kalpesh.krecycleradapter.KRecyclerViewHolder;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

/**
 * Created by poonam on 24/8/18.
 */

public class BaseActivity extends PermissionActivity implements NavigationView.OnNavigationItemSelectedListener {
    Toolbar toolbar;
    AppBarLayout appBarLayout;
    ActionBarDrawerToggle toggle;

    AlertDialog alertDialogOnClickAd;
    private ExoPlayer playerOnClickAd;
    private PlayerView playerViewOnClickAd;
    private RecyclerView rvSliderOnClickAd;
    private CustomLinearLayoutManager layoutManagerOnClickAd;
    private KRecyclerViewAdapter adapterSliderOnClickAd;
    public boolean isAdShowingOnClickAd = false;
    private boolean resumePlayerOnClickAd = false;
    private ArrayList<AdFieldsData> adSliderObjectArrayListOnClickAd = new ArrayList<>();

    private Dialog alertDialogBuilderInApp;
    private ExoPlayer playerInApp;
    private PlayerView playerViewInApp;
    private RecyclerView rvSliderInApp;
    private CustomLinearLayoutManager layoutManagerInApp;
    private KRecyclerViewAdapter adapterSliderInApp;
    public boolean isAdShowingInApp = false;
    private boolean resumePlayerInApp = false;
    private ArrayList<AdFieldsData> adSliderObjectArrayListInApp = new ArrayList<>();

    private String sliderId;
    // for slider
    final Handler handler = new Handler();
    private Runnable runnable = null;
    private int count = 0;
    private int sliderPosition = -1;
    private boolean shouldSlide = true;
    private boolean flag = false;

    public static int TOP = 1;
    public static int CENTER = 2;

    public static boolean isConnectionRestored = false;

    public static String TAG = "BaseActivity";
    public static final String URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";

    MyBroadcastReceiver myBroadcastReceiver = new MyBroadcastReceiver();
    public static String NOTIFICATION_BROADCAST_KEY = "inappnotification";
    public static String MANDATORY_APP_UPDATE = "mandatoryappupdate";
    public static String CHECK_INTERNET_CONNECTION = "checkinternetconnection";

    Dialog dialog;

    private boolean serviceBound = false;
    private MyService service = null;
    InterstitialAd mInterstitialAd;

    FusedLocationProviderClient fusedLocationProviderClient;

    String contentType = "";
    String contentId = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(BaseActivity.this);

        LayoutInflater inflater = (LayoutInflater) BaseActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.connection_dialog, null);
        //show dialog
        dialog = new Dialog(BaseActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(view);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        bindService();

        loadAd();
        //getLocationPermission();

    }

    BroadcastReceiver notificationBroadCast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (intent.getAction().equals(NOTIFICATION_BROADCAST_KEY)) {
                    Log.d(TAG, "Calling in app notification api");
                    if (!Utility.isTelevision()) {
                        callInAppNotificationApi();
                    }
                }
            }
        }
    };

    BroadcastReceiver mandatoryAppUpdateBroadCast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!Utility.isTelevision()) {
                if (intent != null) {
                    if (intent.getAction().equals(MANDATORY_APP_UPDATE)) {
                        MyApp application = (MyApp) getApplication();
                        if (!application.isUpdateScreenVisible) {
                            String isNeedLogout = LocalStorage.getIsNeedLogout(LocalStorage.KEY_APP_LOGOUT, getApplicationContext());
                            if (isNeedLogout != null) {
                                if (isNeedLogout.equalsIgnoreCase("yes")) {
                                    LocalStorage.logout();
                                }
                            }
                            application.isUpdateScreenVisible = true;
                            startActivity(new Intent(BaseActivity.this, AppUpdateActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            finish();
                        }
                    }
                }
            }
        }
    };

    BroadcastReceiver checkInternetConnectionBroadCast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (intent.getAction().equals(CHECK_INTERNET_CONNECTION)) {
                    if (Utility.isConnectingToInternet(getApplicationContext())) {
                        showInternetDialog(false);
                    } else {
                        showInternetDialog(true);
                    }
                }
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    public void setToolbar(boolean showBottomShadow) {
        toolbar = findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.appBarLayout);
        setSupportActionBar(toolbar);
        if (appBarLayout != null) {
            if (!showBottomShadow) {
                appBarLayout.setStateListAnimator(null);
            }
        }
    }

    /*public void setDrawer() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawerLayout);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        setCustomizedTitle();

    }*/


    public void setCustomizedTitle(float textSize) {
        if (toolbar == null) {
            return;
        }

        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View view = toolbar.getChildAt(i);
            if (view instanceof TextView && ((TextView) view).getText().toString().equals(toolbar.getTitle().toString())) {
                ((TextView) view).setTextSize(17);
                ((TextView) view).setTypeface(ResourcesCompat.getFont(view.getContext(), R.font.rubik_medium));
                ((TextView) view).setTextColor(getResources().getColor(R.color.colorWhite));

                if (textSize > 0) {
                    ((TextView) view).setTextSize(textSize);
                }
            }
        }
    }

    public void showToolbarBackButton(int resourceId) {
        if (toolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            if (findViewById(R.id.live_button) != null)
                findViewById(R.id.live_button).setVisibility(View.GONE);
            if (findViewById(R.id.coins_text) != null)
                findViewById(R.id.coins_text).setVisibility(View.GONE);
            if (findViewById(R.id.diamonds_text) != null)
                findViewById(R.id.diamonds_text).setVisibility(View.GONE);
            if (findViewById(R.id.llCoins) != null)
                findViewById(R.id.llCoins).setVisibility(View.GONE);
            if (findViewById(R.id.notification_button) != null)
                findViewById(R.id.notification_button).setVisibility(View.GONE);
            if (findViewById(R.id.search_button) != null)
                findViewById(R.id.search_button).setVisibility(View.GONE);
            if (findViewById(R.id.earn_text) != null)
                findViewById(R.id.earn_text).setVisibility(View.GONE);
            if (resourceId != 0) {
                getSupportActionBar().setHomeAsUpIndicator(resourceId);
                if (resourceId == R.drawable.back_arrow_white) {
                    toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
                }
            }
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
        if (!(fragment instanceof IOnBackPressed) || !((IOnBackPressed) fragment).onBackPressed()) {
            super.onBackPressed();
            isAdShowingOnClickAd = false;
            isAdShowingInApp = false;
        }
        //super.onBackPressed();
    }

    public void showToolbarDrawerIcon() {
        if (toolbar != null) {
            // Remove hamburger
            toggle.setDrawerIndicatorEnabled(false);
            // Show back button
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            toggle.setToolbarNavigationClickListener(null);
            toolbar.setNavigationOnClickListener(null);
            toggle.syncState();
        }
    }

    public void showToolbarIcon() {
        if (toolbar != null) {
            // Show back button
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            findViewById(R.id.live_button).setVisibility(View.VISIBLE);
            findViewById(R.id.coins_text).setVisibility(View.VISIBLE);
            findViewById(R.id.diamonds_text).setVisibility(View.VISIBLE);
            findViewById(R.id.llCoins).setVisibility(View.VISIBLE);
            findViewById(R.id.notification_button).setVisibility(View.VISIBLE);
            findViewById(R.id.search_button).setVisibility(View.VISIBLE);
            //findViewById(R.id.earn_text).setVisibility(View.VISIBLE);
            checkUserValidityForSurvey();
        }
    }

    /**
     * Hide/Show toolbar title
     *
     * @param displayTitle If true shows toolbar title else hide
     */
    public void showToolbarTitle(boolean displayTitle) {
        if (toolbar != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(displayTitle);
        }
    }

    public void showToolbar(boolean displayToolBar) {
        if (appBarLayout != null) {
            if (Utility.isTelevision()) {
                if (displayToolBar) {
                    appBarLayout.setVisibility(View.VISIBLE);
                } else {
                    appBarLayout.setVisibility(View.GONE);
                }
            }
        }
    }

    public void setToolbarTitle(String toolbarTitle) {
        setTitle(toolbarTitle);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    /**
     * Shows coins activity.ase
     */
    void showCoinsActivity() {
        Intent i = new Intent(this, CoinsActivity.class);
        startActivityForResult(i, WalletActivity.REQUEST_CODE);
    }

    @Nullable
    KonfettiView getConfettiView() {
        return null;
    }

    void streamKonfetti(int position) {
        final KonfettiView konfettiView = getConfettiView();
        if (konfettiView != null) {
            new Handler().post(() -> {
                konfettiView.build()
                        .addColors(ContextCompat.getColor(BaseActivity.this, R.color.lt_yellow),
                                ContextCompat.getColor(BaseActivity.this, R.color.lt_orange),
                                ContextCompat.getColor(BaseActivity.this, R.color.lt_purple),
                                ContextCompat.getColor(BaseActivity.this, R.color.lt_pink))
                        .setDirection(0.0, 359.0)
                        .setSpeed(1f, 5f)
                        .setFadeOutEnabled(true)
                        .setTimeToLive(3000L)
                        .addShapes(Shape.RECT, Shape.CIRCLE)
                        .addSizes(new Size(12, 5f), new Size(16, 6f))
                        .setPosition(konfettiView.getX() + konfettiView.getWidth() / 2, konfettiView.getY() + konfettiView.getHeight() / 3)
                        .burst(100);
                /*if (position == 1) {
                    konfettiView.build()
                            .addColors(ContextCompat.getColor(BaseActivity.this, R.color.lt_yellow),
                                    ContextCompat.getColor(BaseActivity.this, R.color.lt_orange),
                                    ContextCompat.getColor(BaseActivity.this, R.color.lt_purple),
                                    ContextCompat.getColor(BaseActivity.this, R.color.lt_pink))
                            .setDirection(0.0, 359.0)
                            .setSpeed(1f, 5f)
                            .setFadeOutEnabled(true)
                            .setTimeToLive(5000L)
                            .addShapes(Shape.RECT, Shape.CIRCLE)
                            .addSizes(new Size(12, 5f))
                            .setPosition(-50f, konfettiView.getWidth() + 50f, -50f, -50f)
                            .streamFor(300, 6000L);
                } else if (position == 2) {
                    konfettiView.build()
                            .addColors(ContextCompat.getColor(BaseActivity.this, R.color.lt_yellow),
                                    ContextCompat.getColor(BaseActivity.this, R.color.lt_orange),
                                    ContextCompat.getColor(BaseActivity.this, R.color.lt_purple),
                                    ContextCompat.getColor(BaseActivity.this, R.color.lt_pink))
                            .setDirection(0.0, 359.0)
                            .setSpeed(1f, 5f)
                            .setFadeOutEnabled(true)
                            .setTimeToLive(5000L)
                            .addShapes(Shape.RECT, Shape.CIRCLE)
                            .addSizes(new Size(12, 5f))
                            .setPosition(konfettiView.getX() + konfettiView.getWidth() / 2, konfettiView.getY() + konfettiView.getHeight() / 3)
                            .streamFor(300, 6000L);
                }*/
            });
        }
    }

    // This snippet hides the system bars.
    void hideSystemUI() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    // This snippet shows the system bars. It does this by removing all the flags
    // except for the ones that make the content appear under the system bars.
    void showSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter("CRASH");
        this.registerReceiver(myBroadcastReceiver, filter);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        intentFilter.addAction(NOTIFICATION_BROADCAST_KEY);
        this.registerReceiver(notificationBroadCast, intentFilter);

        IntentFilter intentFilter1 = new IntentFilter();
        intentFilter1.addCategory(Intent.CATEGORY_DEFAULT);
        intentFilter1.addAction(MANDATORY_APP_UPDATE);
        this.registerReceiver(mandatoryAppUpdateBroadCast, intentFilter1);

        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addCategory(Intent.CATEGORY_DEFAULT);
        intentFilter2.addAction(CHECK_INTERNET_CONNECTION);
        this.registerReceiver(checkInternetConnectionBroadCast, intentFilter2);

        /*Intent serviceIntent = new Intent(this, MyService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }*/

        //checkUserValidityForSurvey();
        isAdShowingOnClickAd = false;
        isAdShowingInApp = false;
        if (playerOnClickAd != null && resumePlayerOnClickAd) {
            playerOnClickAd.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
            playerOnClickAd.setPlayWhenReady(true);
            playerViewOnClickAd.setPlayer(playerOnClickAd);
        }
        if (playerInApp != null && resumePlayerInApp) {
            playerInApp.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
            playerInApp.setPlayWhenReady(true);
            playerViewInApp.setPlayer(playerInApp);
        }

        if (getIntent() != null) {
            Uri uri = getIntent().getData();
            if (uri != null) {
                try {
                    String parameters = Utility.decryptData((uri.toString().replace("https://www.itap.online?", "")), Constant.getSecretKey());
                    Uri data = Uri.parse("https://www.itap.online?" + parameters);
                    contentType = data.getQueryParameter(Constant.TYPE);
                    contentId = data.getQueryParameter(Constant.POSTID);

                    if (contentType != null && !contentType.isEmpty()) {
                        if (LocalStorage.isUserLoggedIn()) {
                            finish();
                            startActivity(new Intent(BaseActivity.this, HomeActivity.class).setAction(getString(R.string.no_action)).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK).putExtra(Constant.TYPE, contentType).putExtra(Constant.POSTID, contentId));
                        } else {
                            finish();
                            startActivity(new Intent(BaseActivity.this, LoginActivity.class).setAction(getString(R.string.action_splash_login)).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK).putExtra(Constant.TYPE, contentType).putExtra(Constant.POSTID, contentId));
                        }
                    }
                } catch (Exception e) {

                }
            }
        }

        LocaleHelper.setLocale(BaseActivity.this, LocalStorage.getSelectedLanguage(BaseActivity.this));
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(notificationBroadCast);
        this.unregisterReceiver(mandatoryAppUpdateBroadCast);
        this.unregisterReceiver(myBroadcastReceiver);
        this.unregisterReceiver(checkInternetConnectionBroadCast);
        isAdShowingOnClickAd = false;
        isAdShowingInApp = false;
        if (playerOnClickAd != null) {
            playerOnClickAd.setPlayWhenReady(false);
            resumePlayerOnClickAd = true;
            playerViewOnClickAd.setPlayer(null);
        }
        if (playerInApp != null) {
            playerInApp.setPlayWhenReady(false);
            resumePlayerInApp = true;
            playerViewInApp.setPlayer(null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceBound) {
            unbindService(serviceConnection);
        }
        closePlayerOnClikAd();
        closePlayerInApp();
    }

    public void callInAppNotificationApi() {
        String userId = LocalStorage.getUserId();
        try {
            Map<String, String> params = new HashMap<>();
            params.put("userId", userId);
            APIRequest apiRequest = new APIRequest(Url.GET_IN_APP_NOTIFICATION, Request.Method.POST, params, null, this);
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    handleInAppNotificationResponse(response, error);
                    Log.d(TAG, "" + response);
                }
            });
        } catch (Exception ignored) {

        }
    }

    public void handleInAppNotificationResponse(String response, Exception error) {
        try {
            if (error != null) {
                //  TODO: Handle error state (Empty State)
            } else {
                JSONObject jsonObject = new JSONObject(response);
                String type = jsonObject.getString("type");
                if (type.equals("OK")) {
                    JSONObject msg = jsonObject.getJSONObject("msg");
                    com.app.itaptv.utils.Log.i("msg", "" + msg);
                    boolean status = msg.has("status") ? msg.getBoolean("status") : false;
                    if (status) {
                        JSONObject message = msg.has("message") ? msg.getJSONObject("message") : null;
                        int id = message.has("user_notification_id") ? message.getInt("user_notification_id") : 0;
                        String title = message.has("title") ? message.getString("title") : null;
                        String link = message.has("link") ? message.getString("link") : null;
                        String imageurl = message.has("image") ? message.getString("image") : null;
                        String buttonText = message.has("button_text") ? message.getString("button_text") : null;

                        JSONObject ad = message.has("ad") ? message.getJSONObject("ad") : null;
                        FeedContentData feedContentData = null;
                        if (ad != null) {
                            feedContentData = new FeedContentData(ad);
                        }


                        if (id != 0 && feedContentData != null && buttonText != null) {
                            if (!buttonText.isEmpty()) {
                                showInAppNotification(buttonText, title, feedContentData);
                                //callReadNotificationAPi(id);
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {

        }
    }

    //@SuppressWarnings("deprecation")
    public void showInAppNotification(String buttonText, String title, FeedContentData feedContentData) {
        LayoutInflater inflater = (LayoutInflater) BaseActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_inapp_notification, null);

        RelativeLayout relativeLayout = view.findViewById(R.id.rl_ad_holder);
        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        RoundedImageView iv_offer_image = view.findViewById(R.id.iv_offer_image);
        ImageView iv_close = view.findViewById(R.id.iv_close);
        TextView tv_offer_message = view.findViewById(R.id.tv_offer_message);
        TextView tvTimer = view.findViewById(R.id.tvTimer);
        Button bt_offer = view.findViewById(R.id.bt_offer);
        ImageView ivVolumeUp = view.findViewById(R.id.ivVolumeUp);
        ImageView ivVolumeOff = view.findViewById(R.id.ivVolumeOff);
        playerViewInApp = view.findViewById(R.id.playerView);
        rvSliderInApp = view.findViewById(R.id.rvSliderAd);

        alertDialogBuilderInApp = new Dialog(BaseActivity.this);
        alertDialogBuilderInApp.setContentView(view);
        alertDialogBuilderInApp.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialogBuilderInApp.show();
        alertDialogBuilderInApp.setCancelable(false);

        new Handler(Looper.getMainLooper()).post(() -> {
            progressBar.setVisibility(View.GONE);
            tv_offer_message.setText(feedContentData.description);
            bt_offer.setText(buttonText);
        });

        if (feedContentData != null) {
            if (feedContentData.skippable) {
                long sec = (Long.parseLong(feedContentData.skippableInSecs)) * 1000;
                tvTimer.setVisibility(View.VISIBLE);
                iv_close.setVisibility(View.GONE);
                new CountDownTimer(sec, 1000) {

                    @Override
                    public void onTick(long millisUntilFinished) {
                        int progress = (int) (millisUntilFinished / 1000);
                        if (progress > 0) {
                            tvTimer.setText(Integer.toString(progress));
                        } else {
                            tvTimer.setVisibility(View.GONE);
                            iv_close.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFinish() {
                        tvTimer.setVisibility(View.GONE);
                        iv_close.setVisibility(View.VISIBLE);
                        alertDialogBuilderInApp.setCancelable(true);
                    }
                }.start();
                /*new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        iv_close.setVisibility(View.VISIBLE);
                        alertDialogBuilder.setCancelable(true);
                    }
                }, sec);*/
            } else {
                alertDialogBuilderInApp.setCancelable(false);
            }
            if (feedContentData.mediaType.equalsIgnoreCase(FeedContentData.MEDIA_TYPE_IMAGE)) {
                String url = feedContentData.adFieldsData.attachment;
                Glide.with(BaseActivity.this)
                        .load(url)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(iv_offer_image);
                iv_offer_image.setVisibility(View.VISIBLE);
                playerViewInApp.setVisibility(View.GONE);
                bt_offer.setVisibility(View.VISIBLE);
                APIMethods.addEvent(BaseActivity.this, Constant.VIEW, feedContentData.postId, Constant.INAPPNOTIFICATION, Constant.INAPPNOTIFICATION);

                iv_offer_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialogBuilderInApp.dismiss();
                        setActionCustomAd(feedContentData.adFieldsData, Constant.INAPPNOTIFICATION, Constant.INAPPNOTIFICATION);
                    }
                });

            } else if (feedContentData.mediaType.equalsIgnoreCase(FeedContentData.MEDIA_TYPE_VIDEO)) {
                playVideoInApp(feedContentData.adFieldsData.attachment);
                playerViewInApp.setVisibility(View.VISIBLE);
                ivVolumeOff.setVisibility(View.VISIBLE);
                iv_offer_image.setVisibility(View.GONE);
                bt_offer.setVisibility(View.VISIBLE);
                APIMethods.addEvent(BaseActivity.this, Constant.VIEW, feedContentData.postId, Constant.INAPPNOTIFICATION, Constant.INAPPNOTIFICATION);

                playerViewInApp.getVideoSurfaceView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialogBuilderInApp.dismiss();
                        setActionCustomAd(feedContentData.adFieldsData, Constant.INAPPNOTIFICATION, Constant.INAPPNOTIFICATION);
                    }
                });
                ivVolumeUp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if (playerInApp != null && playerInApp.isPlaying()) {
                                ivVolumeUp.setVisibility(View.GONE);
                                ivVolumeOff.setVisibility(View.VISIBLE);
                                playerInApp.setVolume(0.0f);
                            }
                        } catch (Exception e) {

                        }
                    }
                });
                ivVolumeOff.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if (playerInApp != null && playerInApp.isPlaying()) {
                                ivVolumeOff.setVisibility(View.GONE);
                                ivVolumeUp.setVisibility(View.VISIBLE);
                                playerInApp.setVolume(1.0f);
                            }
                        } catch (Exception e) {

                        }
                    }
                });
            } else if (feedContentData.mediaType.equalsIgnoreCase(FeedContentData.MEDIA_TYPE_SLIDER)) {
                final float scale = this.getResources().getDisplayMetrics().density;
                int dpWidthInPx = (int) (400 * scale);
                if (Utility.isTelevision()) {
                    alertDialogBuilderInApp.getWindow().setLayout(dpWidthInPx, LinearLayout.LayoutParams.MATCH_PARENT);
                } else {
                    alertDialogBuilderInApp.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                }
                updateInAppSliders(feedContentData.arrayListAdFieldsData);
                playerViewInApp.setVisibility(View.GONE);
                ivVolumeOff.setVisibility(View.GONE);
                iv_offer_image.setVisibility(View.GONE);
                rvSliderInApp.setVisibility(View.VISIBLE);
                bt_offer.setVisibility(View.GONE);
            }
        }
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogBuilderInApp.dismiss();
                APIMethods.addEvent(BaseActivity.this, Constant.SKIP, feedContentData.postId, Constant.INAPPNOTIFICATION, Constant.INAPPNOTIFICATION);
                closePlayerInApp();
            }
        });

        /*Glide.with(BaseActivity.this)
                .load(imageurl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(iv_offer_image);*/

        /*int width = iv_offer_image.getWidth();

        iv_offer_image.getLayoutParams().height = width;
        iv_offer_image.requestLayout();*/

       /* ViewTreeObserver vto = relativeLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    relativeLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    //noinspection deprecation
                    relativeLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                //relativeLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                int width = relativeLayout.getMeasuredWidth();
                relativeLayout.getLayoutParams().height = width;
                relativeLayout.requestLayout();

            }
        });*/

        bt_offer.setOnClickListener(v -> {
            alertDialogBuilderInApp.dismiss();
            //Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            //startActivity(browserIntent);
           /* if (link.startsWith("http")) {
                callUpdateClickedNotificationApi(id);
                startActivity(new Intent(BaseActivity.this, BrowserActivity.class).putExtra("title", title).putExtra("posturl", link));
            }*/
            setActionCustomAd(feedContentData.adFieldsData, Constant.INAPPNOTIFICATION, Constant.INAPPNOTIFICATION);
        });
        /*iv_offer_image.setOnClickListener(v -> {
            alertDialogBuilder.dismiss();
            //Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            //startActivity(browserIntent);
            if (link.startsWith("http")) {
                callUpdateClickedNotificationApi(id);
                startActivity(new Intent(BaseActivity.this, BrowserActivity.class).putExtra("title", title).putExtra("posturl", link));
            }
        });*/
    }

    /*public void callReadNotificationAPi(int id) {
        String userId = LocalStorage.getUserId();
        try {
            Map<String, String> params = new HashMap<>();
            params.put("userId", userId);
            params.put("user_notification_id", String.valueOf(id));
            APIRequest apiRequest = new APIRequest(Url.UPDATE_READ_NOTIFICATION, Request.Method.POST, params, null, this);
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    handleReadNotificationResponse(response, error);
                }
            });
        } catch (Exception ignored) {

        }
    }*/

    public void callUpdateClickedNotificationApi(int id) {
        String userId = LocalStorage.getUserId();
        try {
            Map<String, String> params = new HashMap<>();
            params.put("userId", userId);
            params.put("user_notification_id", String.valueOf(id));
            APIRequest apiRequest = new APIRequest(Url.UPDATE_CLICKED_NOTIFICATION, Request.Method.POST, params, null, this);
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    handleClickedNotificationResponse(response, error);
                }
            });
        } catch (Exception ignored) {

        }
    }

    /*public void handleReadNotificationResponse(String response, Exception error) {
        try {
            if (error != null) {
                //  TODO: Handle error state (Empty State)
            } else {
                JSONObject jsonObject = new JSONObject(response);
                String type = jsonObject.getString("type");
                if (type.equals("OK")) {

                } else if (type.equals("ERROR")) {

                }
            }
        } catch (Exception ignored) {

        }
    }*/

    public void handleClickedNotificationResponse(String response, Exception error) {
        try {
            if (error != null) {
                //  TODO: Handle error state (Empty State)
            } else {
                JSONObject jsonObject = new JSONObject(response);
                String type = jsonObject.getString("type");
                if (type.equals("OK")) {

                } else if (type.equals("ERROR")) {

                }
            }
        } catch (Exception ignored) {

        }
    }

    public void showInternetDialog(boolean show) {
        if (show) {
            if (dialog != null) {
                if (!dialog.isShowing()) {
                    isConnectionRestored = false;
                    dialog.show();
                }
            }
        } else {
            if (dialog != null) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                    if (getIntent() != null) {
                        isConnectionRestored = true;
                        finish();
                        startActivity(getIntent());
                    }
                }
            }
        }
    }

    public void checkUserValidityForSurvey() {
        String userId = LocalStorage.getUserId();
        try {
            Map<String, String> params = new HashMap<>();
            params.put("userId", userId);
            APIRequest apiRequest = new APIRequest(Url.EARN_COINS_CODE, Request.Method.POST, params, null, this);
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    try {
                        if (error != null) {
                            if (findViewById(R.id.earn_text) != null)
                                findViewById(R.id.earn_text).setVisibility(View.GONE);
                        } else {
                            JSONObject jsonObject = new JSONObject(response);
                            String type = jsonObject.getString("type");
                            if (type.equals("OK")) {
                                JSONObject msg = jsonObject.getJSONObject("msg");
                                boolean status = msg.has("status") ? msg.getBoolean("status") : false;
                                if (status) {
                                    if (LocalStorage.getSurveyHash() != null) {
                                        if (!LocalStorage.getSurveyHash().isEmpty()) {
                                            if (findViewById(R.id.earn_text) != null)
                                                findViewById(R.id.earn_text).setVisibility(View.GONE);
                                        } else {
                                        }
                                    } else {
                                        if (findViewById(R.id.earn_text) != null)
                                            findViewById(R.id.earn_text).setVisibility(View.GONE);
                                    }
                                } else {
                                    if (findViewById(R.id.earn_text) != null)
                                        findViewById(R.id.earn_text).setVisibility(View.GONE);
                                }
                            } else if (type.equals("ERROR")) {
                                if (findViewById(R.id.earn_text) != null)
                                    findViewById(R.id.earn_text).setVisibility(View.GONE);
                            }
                        }
                    } catch (Exception ignored) {

                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinderService) {
            if (iBinderService != null) {
                MyService.LocalBinder binder = (MyService.LocalBinder) iBinderService;
                service = binder.getService();
                serviceBound = true;
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    private void bindService() {
        try {
            AsyncTask.execute(() -> {
                if (!serviceBound) {
                    Intent i = new Intent(BaseActivity.this, MyService.class);
                    startService(i);
                    bindService(i, serviceConnection, Context.BIND_AUTO_CREATE);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        if (LocalStorage.getKeyAdMobVideo() != null) {
            InterstitialAd.load(this, LocalStorage.getKeyAdMobVideo(), adRequest,
                    new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            // The mInterstitialAd reference will be null until
                            // an ad is loaded.
                            mInterstitialAd = interstitialAd;
                            Log.i(TAG, "onAdLoaded");
                            // show();
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error
                            Log.i(TAG, loadAdError.getMessage());
                            //mInterstitialAd = null;
                        }
                    });
        }

    }

    public void showAd(String id) {
        String adType = Utility.getOnClickAdType(id, BaseActivity.this);
        if (!adType.isEmpty()) {
            if (!isAdShowingOnClickAd) {
                if (adType.equalsIgnoreCase(Constant.ADMOB)) {
                    if (mInterstitialAd != null) {
                        isAdShowingOnClickAd = true;
                        mInterstitialAd.show(BaseActivity.this);
                    } else {
                        isAdShowingOnClickAd = false;
                        Log.d("TAG", "The interstitial ad wasn't ready yet.");
                    }
                } else if (adType.equalsIgnoreCase(Constant.CUSTOM)) {
                    showCustomDialogAd(id);
                }
            }
        }
    }

    public void showCustomDialogAd(String id) {
        List<AdMobData> list = LocalStorage.getOnClickAdMobList(LocalStorage.KEY_ONCLICK_AD_MOB, BaseActivity.this);
        int position = 0;
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                position = i;
                if (list.get(i).id.equals(id)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
                            .setView(R.layout.dialog_custom_ad);
                    alertDialogOnClickAd = builder.create();
                    alertDialogOnClickAd.getWindow().setBackgroundDrawableResource(R.color.back_black_transparent);
                    alertDialogOnClickAd.getWindow().setGravity(Gravity.CENTER_VERTICAL);
                    alertDialogOnClickAd.setCancelable(false);
                    alertDialogOnClickAd.show();

                    ImageView ivCustomAd = alertDialogOnClickAd.findViewById(R.id.ivCustomAd);
                    ImageView ivClose = alertDialogOnClickAd.findViewById(R.id.ivClose);
                    TextView tvTimer = alertDialogOnClickAd.findViewById(R.id.tvTimer);
                    ImageView ivVolumeUp = alertDialogOnClickAd.findViewById(R.id.ivVolumeUp);
                    ImageView ivVolumeOff = alertDialogOnClickAd.findViewById(R.id.ivVolumeOff);
                    playerViewOnClickAd = alertDialogOnClickAd.findViewById(R.id.playerView);
                    rvSliderOnClickAd = alertDialogOnClickAd.findViewById(R.id.rvSliderAd);


                    if (list.get(i).feedContentObjectData != null) {
                        if (list.get(i).feedContentObjectData.skippable) {
                            long sec = (Long.parseLong(list.get(i).feedContentObjectData.skippableInSecs)) * 1000;
                            tvTimer.setVisibility(View.VISIBLE);
                            ivClose.setVisibility(View.GONE);
                            new CountDownTimer(sec, 1000) {

                                @Override
                                public void onTick(long millisUntilFinished) {
                                    int progress = (int) (millisUntilFinished / 1000);
                                    if (progress > 0) {
                                        tvTimer.setText(Integer.toString(progress));
                                    } else {
                                        tvTimer.setVisibility(View.GONE);
                                        ivClose.setVisibility(View.VISIBLE);
                                    }
                                }

                                @Override
                                public void onFinish() {
                                    tvTimer.setVisibility(View.GONE);
                                    ivClose.setVisibility(View.VISIBLE);
                                    alertDialogOnClickAd.setCancelable(true);
                                }
                            }.start();
                           /* new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    ivClose.setVisibility(View.VISIBLE);
                                    alertDialog.setCancelable(true);
                                }
                            }, sec);*/
                        } else {
                            alertDialogOnClickAd.setCancelable(false);
                        }
                        int finalI = i;
                        if (list.get(i).feedContentObjectData.mediaType.equalsIgnoreCase(FeedContentData.MEDIA_TYPE_IMAGE)) {
                            String url = list.get(i).feedContentObjectData.adFieldsData.attachment;
                            Glide.with(BaseActivity.this)
                                    .load(url)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(ivCustomAd);
                            ivCustomAd.setVisibility(View.VISIBLE);
                            playerViewOnClickAd.setVisibility(View.GONE);
                            isAdShowingOnClickAd = true;
                            APIMethods.addEvent(BaseActivity.this, Constant.VIEW, list.get(i).feedContentObjectData.postId, Constant.ONCLICK, id);

                            ivCustomAd.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    alertDialogOnClickAd.dismiss();
                                    isAdShowingOnClickAd = false;
                                    setActionCustomAd(list.get(finalI).feedContentObjectData.adFieldsData, Constant.ONCLICK, id);
                                }
                            });

                        } else if (list.get(i).feedContentObjectData.mediaType.equalsIgnoreCase(FeedContentData.MEDIA_TYPE_VIDEO)) {
                            playVideoOnClickAd(list.get(i).feedContentObjectData.adFieldsData.attachment);
                            playerViewOnClickAd.setVisibility(View.VISIBLE);
                            ivVolumeOff.setVisibility(View.VISIBLE);
                            ivCustomAd.setVisibility(View.GONE);
                            isAdShowingOnClickAd = true;
                            APIMethods.addEvent(BaseActivity.this, Constant.VIEW, list.get(i).feedContentObjectData.postId, Constant.ONCLICK, id);

                            playerViewOnClickAd.getVideoSurfaceView().setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    alertDialogOnClickAd.dismiss();
                                    isAdShowingOnClickAd = false;
                                    setActionCustomAd(list.get(finalI).feedContentObjectData.adFieldsData, Constant.ONCLICK, id);
                                }
                            });
                            ivVolumeUp.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {
                                        if (playerOnClickAd != null && playerOnClickAd.isPlaying()) {
                                            ivVolumeUp.setVisibility(View.GONE);
                                            ivVolumeOff.setVisibility(View.VISIBLE);
                                            playerOnClickAd.setVolume(0.0f);
                                        }
                                    } catch (Exception e) {

                                    }
                                }
                            });
                            ivVolumeOff.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {
                                        if (playerOnClickAd != null && playerOnClickAd.isPlaying()) {
                                            ivVolumeOff.setVisibility(View.GONE);
                                            ivVolumeUp.setVisibility(View.VISIBLE);
                                            playerOnClickAd.setVolume(1.0f);
                                        }
                                    } catch (Exception e) {

                                    }
                                }
                            });
                        } else if (list.get(i).feedContentObjectData.mediaType.equalsIgnoreCase(FeedContentData.MEDIA_TYPE_SLIDER)) {
                            updateOtherSliders(list.get(i).feedContentObjectData.arrayListAdFieldsData, id);
                            playerViewOnClickAd.setVisibility(View.GONE);
                            ivVolumeOff.setVisibility(View.GONE);
                            ivCustomAd.setVisibility(View.GONE);
                            rvSliderOnClickAd.setVisibility(View.VISIBLE);
                            isAdShowingOnClickAd = true;
                        }
                    }
                    int finalI1 = i;
                    ivClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialogOnClickAd.dismiss();
                            isAdShowingOnClickAd = false;
                            APIMethods.addEvent(BaseActivity.this, Constant.SKIP, list.get(finalI1).feedContentObjectData.postId, Constant.ONCLICK, id);
                            closePlayerOnClikAd();
                            closePlayerOnClikAd();
                        }
                    });

                }
            }
        }
    }

    private void setActionCustomAd(AdFieldsData adFieldsData, String location, String subLocation) {
        APIMethods.addEvent(BaseActivity.this, Constant.CLICK, adFieldsData.postId, location, subLocation);
        switch (adFieldsData.adType) {
            case FeedContentData.AD_TYPE_IN_APP:
                if (adFieldsData.contentType.equalsIgnoreCase(Constant.PAGES)) {
                    HomeActivity.getInstance().redirectToPage(adFieldsData.pageType);
                } else {
                    switch (adFieldsData.contentType) {
                        case FeedContentData.CONTENT_TYPE_ESPORTS:
                            HomeActivity.getInstance().getUserRegisteredTournamentInfo(String.valueOf(adFieldsData.contentId));
                            break;
                        default:
                            HomeActivity.getInstance().getMediaData(String.valueOf(adFieldsData.contentId), adFieldsData.contentType);
                    }
                }
                break;
            case FeedContentData.AD_TYPE_EXTERNAL:
                setActionOnAd(adFieldsData);
                break;
        }
    }

    private void setActionOnAd(AdFieldsData adFieldsData) {
        switch (adFieldsData.urlType) {
            case Constant.BROWSER:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(adFieldsData.externalUrl));
                startActivity(browserIntent);
                break;
            case Constant.WEBVIEW:
                startActivity(new Intent(BaseActivity.this, BrowserActivity.class).putExtra("title", "").putExtra("posturl", adFieldsData.externalUrl));
                break;
        }
    }


    private void playVideoOnClickAd(String url) {
        playerViewOnClickAd.setUseController(false);
        playerViewOnClickAd.requestFocus();

        Uri uri = Uri.parse(url);
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(BaseActivity.this, getApplicationContext().getPackageName(), bandwidthMeter);
        MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(uri));

        playerOnClickAd = new ExoPlayer.Builder(BaseActivity.this).build();
        playerOnClickAd.addListener(new Player.Listener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == Player.STATE_ENDED) {
                    playerOnClickAd.seekTo(0);
                }
            }

            @Override
            public void onPlayerError(PlaybackException error) {

            }
        });
        playerOnClickAd.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT);
        playerOnClickAd.prepare(mediaSource);
        playerOnClickAd.setPlayWhenReady(true);

        /*
          Volume is set to 0 as per requirement
          Use as per the case
         */
        playerOnClickAd.setVolume(0.0f);

        playerViewOnClickAd.setPlayer(playerOnClickAd);
    }

    private void playVideoInApp(String url) {
        playerViewInApp.setUseController(false);
        playerViewInApp.requestFocus();

        Uri uri = Uri.parse(url);
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(BaseActivity.this, getApplicationContext().getPackageName(), bandwidthMeter);
        MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(uri));

        playerInApp = new ExoPlayer.Builder(BaseActivity.this).build();
        playerInApp.addListener(new Player.Listener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == Player.STATE_ENDED) {
                    playerInApp.seekTo(0);
                }
            }

            @Override
            public void onPlayerError(PlaybackException error) {

            }
        });
        playerInApp.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT);
        playerInApp.prepare(mediaSource);
        playerInApp.setPlayWhenReady(true);

        /*
          Volume is set to 0 as per requirement
          Use as per the case
         */
        playerInApp.setVolume(0.0f);

        playerViewInApp.setPlayer(playerInApp);
    }


    private void closePlayerInApp() {
        if (playerInApp != null) {
            playerInApp.setPlayWhenReady(false);
            playerInApp.stop();
            playerInApp.release();
            playerInApp = null;
        }
        if (playerViewInApp != null) {
            playerViewInApp.setPlayer(null);
        }
    }

    private void closePlayerOnClikAd() {
        if (playerOnClickAd != null) {
            playerOnClickAd.setPlayWhenReady(false);
            playerOnClickAd.stop();
            playerOnClickAd.release();
            playerOnClickAd = null;
        }
        if (playerViewOnClickAd != null) {
            playerViewOnClickAd.setPlayer(null);
        }
    }


    /**
     * Initializes slider recycler view
     * Set the slider recyclerview of Custom ads
     */
    private void setOtherSliderRecyclerView(String id) {
        adapterSliderOnClickAd = new KRecyclerViewAdapter(BaseActivity.this, adSliderObjectArrayListOnClickAd, new KRecyclerViewHolderCallBack() {
            @Override
            public KRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_slider_ad, viewGroup, false);

                adapterSliderOnClickAd.getItemCount();
                adapterSliderOnClickAd.getSelectedIndexes();
                return new CustomAdSliderHolder(layoutView, adSliderObjectArrayListOnClickAd.size(), Constant.BANNER, id, new SliderListener() {
                    @Override
                    public void stopScrolling() {
                        stopSliding();
                    }
                });
            }
        }, new KRecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClicked(KRecyclerViewHolder kRecyclerViewHolder, Object o, int i) {
                if (o instanceof AdFieldsData) {
                    AdFieldsData adFieldsData = (AdFieldsData) o;
                    setActionCustomAd(adFieldsData, Constant.ONCLICK, id);
                    if (alertDialogOnClickAd != null) {
                        alertDialogOnClickAd.dismiss();
                    }
                }
            }
        });

        layoutManagerOnClickAd = new CustomLinearLayoutManager(BaseActivity.this, LinearLayoutManager.HORIZONTAL, false);
        rvSliderOnClickAd.setLayoutManager(layoutManagerOnClickAd);
        rvSliderOnClickAd.setNestedScrollingEnabled(false);
        rvSliderOnClickAd.setOnFlingListener(null);
        rvSliderOnClickAd.setAdapter(adapterSliderOnClickAd);

        // Dot indicator for banner
        final int radius = getResources().getDimensionPixelSize(R.dimen.dot_radius);
        final int dotsHeight = getResources().getDimensionPixelSize(R.dimen.dot_height);
        final int color = ContextCompat.getColor(BaseActivity.this, R.color.colorAccent);
        final int inActiveColor = ContextCompat.getColor(BaseActivity.this, R.color.game_gray);
        rvSliderOnClickAd.addItemDecoration(new DotsIndicatorDecoration(radius, radius * 2, dotsHeight, inActiveColor, color));

        new PagerSnapHelper().attachToRecyclerView(rvSliderOnClickAd);

        rvSliderOnClickAd.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                shouldSlide = (newState == RecyclerView.SCROLL_STATE_IDLE);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    sliderPosition = layoutManagerOnClickAd.findFirstCompletelyVisibleItemPosition();
                    startSliding();
                } else {
                    stopSliding();
                }
            }
        });
    }

    private void setInAppSliderRecyclerView() {
        adapterSliderInApp = new KRecyclerViewAdapter(BaseActivity.this, adSliderObjectArrayListInApp, new KRecyclerViewHolderCallBack() {
            @Override
            public KRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_slider_inapp_notification, viewGroup, false);

                adapterSliderInApp.getItemCount();
                adapterSliderInApp.getSelectedIndexes();
                return new CustomAdSliderHolder(layoutView, adSliderObjectArrayListInApp.size(), Constant.INAPPNOTIFICATION, Constant.INAPPNOTIFICATION, new SliderListener() {
                    @Override
                    public void stopScrolling() {
                        stopSlidingInApp();
                    }
                });
            }
        }, new KRecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClicked(KRecyclerViewHolder kRecyclerViewHolder, Object o, int i) {
                if (o instanceof AdFieldsData) {
                    AdFieldsData adFieldsData = (AdFieldsData) o;
                    setActionCustomAd(adFieldsData, Constant.INAPPNOTIFICATION, Constant.INAPPNOTIFICATION);
                    if (alertDialogBuilderInApp != null) {
                        alertDialogBuilderInApp.dismiss();
                    }
                }
            }
        });

        layoutManagerInApp = new CustomLinearLayoutManager(BaseActivity.this, LinearLayoutManager.HORIZONTAL, false);
        rvSliderInApp.setLayoutManager(layoutManagerInApp);
        rvSliderInApp.setNestedScrollingEnabled(false);
        rvSliderInApp.setOnFlingListener(null);
        rvSliderInApp.setAdapter(adapterSliderInApp);

        // Dot indicator for banner
        final int radius = getResources().getDimensionPixelSize(R.dimen.dot_radius);
        final int dotsHeight = getResources().getDimensionPixelSize(R.dimen.dot_height);
        final int color = ContextCompat.getColor(BaseActivity.this, R.color.colorAccent);
        final int inActiveColor = ContextCompat.getColor(BaseActivity.this, R.color.game_gray);
        rvSliderInApp.addItemDecoration(new DotsIndicatorDecoration(radius, radius * 2, dotsHeight, inActiveColor, color));

        new PagerSnapHelper().attachToRecyclerView(rvSliderInApp);

        rvSliderInApp.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                shouldSlide = (newState == RecyclerView.SCROLL_STATE_IDLE);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    sliderPosition = layoutManagerInApp.findFirstCompletelyVisibleItemPosition();
                    startSlidingInApp();
                } else {
                    stopSlidingInApp();
                }
            }
        });
    }

    private Handler sliderHandler;
    private Runnable sliderRunnable;
    private long secondsToWait = 4000;

    private void startSliding() {
        if (sliderHandler == null) {
            sliderHandler = new Handler();
        }
        if (sliderRunnable == null) {
            sliderRunnable = this::changeSliderPage;
        }
        sliderHandler.postDelayed(sliderRunnable, secondsToWait);
    }

    private void stopSliding() {
        if (sliderHandler != null && sliderRunnable != null) {
            sliderHandler.removeCallbacks(sliderRunnable);
        }
    }

    private Handler sliderHandlerInApp;
    private Runnable sliderRunnableInApp;
    private long secondsToWaitInApp = 4000;

    private void startSlidingInApp() {
        if (sliderHandlerInApp == null) {
            sliderHandlerInApp = new Handler();
        }
        if (sliderRunnableInApp == null) {
            sliderRunnableInApp = new Runnable() {
                @Override
                public void run() {
                    if (adSliderObjectArrayListInApp.size() <= 1) return;
                    try {
                        LinearLayoutManager layoutManager = (LinearLayoutManager) rvSliderInApp.getLayoutManager();
                        if (layoutManager != null) {
                            int visibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition();
                            if (visibleItemPosition > -1 && visibleItemPosition < adSliderObjectArrayListInApp.size()) {
                                if (visibleItemPosition == adSliderObjectArrayListInApp.size() - 1) {
                                    // Scroll to first item
                                    rvSliderInApp.smoothScrollToPosition(0);
                                } else {
                                    // Scroll to next item
                                    rvSliderInApp.smoothScrollToPosition(visibleItemPosition + 1);
                                }
                            }
                        }
                    } catch (Exception ignored) {

                    }
                }
            };

        }
        sliderHandlerInApp.postDelayed(sliderRunnableInApp, secondsToWaitInApp);
    }

    private void stopSlidingInApp() {
        if (sliderHandlerInApp != null && sliderRunnableInApp != null) {
            sliderHandlerInApp.removeCallbacks(sliderRunnableInApp);
        }
    }

    @SuppressLint("SetTextI18n")
    private void changeSliderPage() {
        if (adSliderObjectArrayListOnClickAd.size() <= 1) return;
        try {
            LinearLayoutManager layoutManager = (LinearLayoutManager) rvSliderOnClickAd.getLayoutManager();
            if (layoutManager != null) {
                int visibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition();
                if (visibleItemPosition > -1 && visibleItemPosition < adSliderObjectArrayListOnClickAd.size()) {
                    if (visibleItemPosition == adSliderObjectArrayListOnClickAd.size() - 1) {
                        // Scroll to first item
                        rvSliderOnClickAd.smoothScrollToPosition(0);
                    } else {
                        // Scroll to next item
                        rvSliderOnClickAd.smoothScrollToPosition(visibleItemPosition + 1);
                    }
                }
            }
        } catch (Exception ignored) {

        }
    }

    private void updateOtherSliders(ArrayList<AdFieldsData> adFieldsDataArrayList, String id) {
        setOtherSliderRecyclerView(id);
        flag = true;
        adSliderObjectArrayListOnClickAd.clear();
        new Thread(() -> {
            for (AdFieldsData adFieldsData : adFieldsDataArrayList) {
                sliderId = String.valueOf(adFieldsData.postId);
                adSliderObjectArrayListOnClickAd.addAll(adFieldsDataArrayList);
                break;
            }

            new Handler(Looper.getMainLooper()).post(() -> {
                if (adapterSliderOnClickAd != null)
                    adapterSliderOnClickAd.notifyDataSetChanged();
                startSliding();
            });
        }).start();
    }

    private void updateInAppSliders(ArrayList<AdFieldsData> adFieldsDataArrayList) {
        setInAppSliderRecyclerView();
        flag = true;
        adSliderObjectArrayListInApp.clear();
        new Thread(() -> {
            for (AdFieldsData adFieldsData : adFieldsDataArrayList) {
                sliderId = String.valueOf(adFieldsData.postId);
                adSliderObjectArrayListInApp.addAll(adFieldsDataArrayList);
                break;
            }

            new Handler(Looper.getMainLooper()).post(() -> {
                if (adapterSliderInApp != null)
                    adapterSliderInApp.notifyDataSetChanged();
                startSlidingInApp();
            });
        }).start();
    }

    public void getLocationPermission() {
        if (ActivityCompat.checkSelfPermission(BaseActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(BaseActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

        } else {
            ActivityCompat.requestPermissions(BaseActivity.this
                    , new String[]{Manifest.permission.ACCESS_FINE_LOCATION
                            , Manifest.permission.ACCESS_COARSE_LOCATION}
                    , 234);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 1 && (grantResults[0] + grantResults[1]
                == PackageManager.PERMISSION_GRANTED)) {

        } else {
            //Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }
}
