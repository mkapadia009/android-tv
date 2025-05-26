package com.app.itaptv.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.app.itaptv.API.APIMethods;
import com.app.itaptv.R;
import com.app.itaptv.custom_interface.SliderListener;
import com.app.itaptv.custom_widget.DotsIndicatorDecoration;
import com.app.itaptv.holder.CustomAdSliderHolder;
import com.app.itaptv.structure.AdFieldsData;
import com.app.itaptv.structure.AdMobData;
import com.app.itaptv.structure.FeedContentData;
import com.app.itaptv.structure.GameData;
import com.app.itaptv.utils.AlertUtils;
import com.app.itaptv.utils.Analyticals;
import com.app.itaptv.utils.AnalyticsTracker;
import com.app.itaptv.utils.Constant;
import com.app.itaptv.utils.CustomLinearLayoutManager;
import com.app.itaptv.utils.DownloadUtil;
import com.app.itaptv.utils.GameConstants;
import com.app.itaptv.utils.GameDateValidation;
import com.app.itaptv.utils.LocalStorage;
import com.app.itaptv.utils.Log;
import com.app.itaptv.utils.Utility;
import com.app.itaptv.utils.Wallet;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
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
import com.google.android.gms.ads.AdView;
import com.kalpesh.krecycleradapter.Interface.KRecyclerItemClickListener;
import com.kalpesh.krecycleradapter.Interface.KRecyclerViewHolderCallBack;
import com.kalpesh.krecycleradapter.KRecyclerViewAdapter;
import com.kalpesh.krecycleradapter.KRecyclerViewHolder;
import com.shephertz.app42.gaming.multiplayer.client.WarpClient;
import com.shephertz.app42.gaming.multiplayer.client.command.WarpResponseResultCode;
import com.shephertz.app42.gaming.multiplayer.client.events.AllRoomsEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.AllUsersEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.ConnectEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.LiveRoomInfoEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.LiveUserInfoEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.MatchedRoomsEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomEvent;
import com.shephertz.app42.gaming.multiplayer.client.listener.ConnectionRequestListener;
import com.shephertz.app42.gaming.multiplayer.client.listener.RoomRequestListener;
import com.shephertz.app42.gaming.multiplayer.client.listener.ZoneRequestListener;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.app.itaptv.utils.Analyticals.GAME_ACTIVITY_TYPE;
import static com.app.itaptv.utils.AnalyticsTracker.secondsPlayedGames;

/**
 * Created by poonam on 19/9/18.
 */

public class GameStartActivity extends BaseActivity implements ConnectionRequestListener, ZoneRequestListener, RoomRequestListener {

    static String TAG = "GameStartActivity";

    public static String GAME_DATA = "gameData";
    public static int GAME_TYPE_SINGLE_USER = 1;
    public static int GAME_TYPE_MULTI_USER = 2;

    LinearLayout llPlayer;
    LinearLayout llPlayNow;
    LinearLayout llAssociatedWith;
    LinearLayout llWinCoins;
    LinearLayout llWinCoupons;
    LinearLayout llTimer;
    LinearLayout llLoader;
    RelativeLayout rlDownloadingData;
    RelativeLayout rlParent;
    TextView tvLabelEndsIn;
    TextView tvGameTime;
    TextView tvWinnerTotalCoins;
    TextView tvGameTitle;
    TextView tvGameDescription;
    TextView tvCouponName;
    Button btSinglePlayer;
    Button btRandomOpponent;
    Button btHowToPlay;
    ImageView ivGameImage;
    ImageView ivCoinImage;
    ImageView ivTvLogo;
    ImageView ivCouponImage;
    CardView cvImage;
    TextView tvEntryFees;
    AdView mAdViewTop;
    AdRequest adRequest;
    ConstraintLayout clAdHolderTop;
    ImageView ivCustomAdTop;
    ImageView ivCloseTop;
    PlayerView playerViewTop;
    ImageView ivVolumeUpTop;
    ImageView ivVolumeOffTop;
    AdView mAdViewBottom;
    ConstraintLayout clAdHolderBottom;
    ImageView ivCustomAdBottom;
    ImageView ivCloseBottom;
    PlayerView playerViewBottom;
    ImageView ivVolumeUpBottom;
    ImageView ivVolumeOffBottom;

    private boolean resumePlayerTop = false;
    private ExoPlayer playerTopGame;
    private boolean resumePlayerBottom = false;
    private ExoPlayer playerBottomGame;

    private static final String TYPE_LAYOUT_SEARCH = "Search";
    private static final String TYPE_LAYOUT_DOWNLOAD_DATA = "Download Game Data";
    GameData gameData;
    private WarpClient warpClient;

    private String gameId = "";
    private String userId = "";
    private long walletBalance;
    private boolean success;

    private RecyclerView rvSliderAdTop;
    private CustomLinearLayoutManager layoutManagerTop;
    private KRecyclerViewAdapter adapterSliderAdTop;
    private boolean flagDecorationTop = false;
    private ArrayList<AdFieldsData> adSliderObjectArrayListTop = new ArrayList<>();

    private String sliderIdAdTop;
    // for slider
    private Runnable runnableTop = null;
    private int countTop = 0;
    private int visibleItemPositionTop;
    private int sliderPositionTop = -1;
    private boolean shouldSlideTop = true;
    private boolean flagAdTop = false;

    private RecyclerView rvSliderAdBottom;
    private CustomLinearLayoutManager layoutManagerBottom;
    private KRecyclerViewAdapter adapterSliderAdBottom;
    private boolean flagDecorationBottom = false;
    private ArrayList<AdFieldsData> adSliderObjectArrayListBottom = new ArrayList<>();

    private String sliderIdAdBottom;
    // for slider
    private Runnable runnableBottom = null;
    private int countBottom = 0;
    private int visibleItemPositionBottom;
    private int sliderPositionBottom = -1;
    private boolean shouldSlideBottom = true;
    private boolean flagAdBottom = false;

    /*
     * Lifecycle methods
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_start);
        setTheme(R.style.Theme_Leanback);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        init();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*if (requestCode == MultiplayerCoinAlertActivity.REQUEST_CODE) {
            if (data != null) {
                if (data.hasExtra(MultiplayerCoinAlertActivity.SUCCESS)) {
                }
            }
        }*/

        if (requestCode == GameData.REQUEST_CODE_GAME) {
            if (data != null)
                setResult(GameData.REQUEST_CODE_GAME, data);
            finish();
        } else if (requestCode == GameOngoingActivity.REQUEST_CODE_OPPONENT_NOT_FOUND) {
            if (data != null) {
                boolean playSinglePlayerGame = data.getBooleanExtra(GameOngoingActivity.SINGLE_PLAYER_GAME, false);
                if (playSinglePlayerGame) {
                    singlePlayer(null);
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (gameData.quizType.equalsIgnoreCase(GameData.QUIZE_TYPE_TURN_BASED)) {
            if (warpClient == null) {
                initWarpClient();
            }
            warpClient.addConnectionRequestListener(this);
            warpClient.addZoneRequestListener(this);
            warpClient.addRoomRequestListener(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (gameData.quizType.equalsIgnoreCase(GameData.QUIZE_TYPE_TURN_BASED)) {
            if (warpClient != null) {
                warpClient.removeConnectionRequestListener(this);
                warpClient.removeZoneRequestListener(this);
                warpClient.removeRoomRequestListener(this);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (!isPlayNowClicked) {
            super.onBackPressed();
        }
        if (gameData.quizType.equalsIgnoreCase(GameData.QUIZE_TYPE_TURN_BASED)) {
            warpClient.disconnect();
        }
    }

    private void initWarpClient() {
        try {
            WarpClient.initialize(GameConstants.GAME_KEY, GameConstants.getGameHost());
            WarpClient.setRecoveryAllowance(GameConstants.RECCOVERY_ALLOWANCE_TIME);
            warpClient = WarpClient.getInstance();
        } catch (Exception e) {
            AlertUtils.showAlert(getString(R.string.label_error), e.getMessage(), null, this,
                    isPositiveAction -> finish());
        }
    }

    private void init() {
        gameData = (GameData) getIntent().getExtras().getBundle("Bundle").getParcelable(GAME_DATA);
        gameId = gameData.id;
        userId = LocalStorage.getUserId();
        Log.d("gameData", gameData.toString());

        if (gameData.quizType.equalsIgnoreCase(GameData.QUIZE_TYPE_TURN_BASED))
            initWarpClient();
        llPlayer = findViewById(R.id.llPlayer);
        llPlayNow = findViewById(R.id.llPlayNow);
        llAssociatedWith = findViewById(R.id.llAssociatedWith);
        llWinCoins = findViewById(R.id.llWinCoins);
        llWinCoupons = findViewById(R.id.llWinCoupons);
        llTimer = findViewById(R.id.llTimer);
        llLoader = findViewById(R.id.llLoader);
        rlDownloadingData = findViewById(R.id.rlDownloadingData);
        rlParent = findViewById(R.id.rlParent);
        tvLabelEndsIn = findViewById(R.id.tvLabelEndsIn);
        tvGameTime = findViewById(R.id.tvGameTime);
        tvWinnerTotalCoins = findViewById(R.id.tvWinnerTotalCoins);
        tvGameTitle = findViewById(R.id.tvGameTitle);
        tvGameDescription = findViewById(R.id.tvGameDescription);
        tvCouponName = findViewById(R.id.tvCouponName);
        btSinglePlayer = findViewById(R.id.btSinglePlayer);
        btRandomOpponent = findViewById(R.id.btRandomOpponent);
        btHowToPlay = findViewById(R.id.btHowToPlay);
        ivGameImage = findViewById(R.id.ivGameImage);
        ivCoinImage = findViewById(R.id.ivCoinImage);
        ivTvLogo = findViewById(R.id.ivTvLogo);
        ivCouponImage = findViewById(R.id.ivCouponImage);
        cvImage = findViewById(R.id.cvImage);
        //Log.e("Density",""+getResources().getDisplayMetrics().density);

        tvWinnerTotalCoins.setText(String.format(getString(R.string.win_upto_coins), gameData.bonusPoints));
        tvEntryFees = findViewById(R.id.tvEntryFees);

        mAdViewTop = findViewById(R.id.adViewGameStartTop);
        clAdHolderTop = findViewById(R.id.cl_ad_holderTop);
        ivCustomAdTop = findViewById(R.id.ivCustomAdTop);
        ivCloseTop = findViewById(R.id.ivCloseTop);
        playerViewTop = findViewById(R.id.playerViewTop);
        ivVolumeUpTop = findViewById(R.id.ivVolumeUpTop);
        ivVolumeOffTop = findViewById(R.id.ivVolumeOffTop);
        rvSliderAdTop = findViewById(R.id.rvSliderAdTop);
        rvSliderAdBottom = findViewById(R.id.rvSliderAdBottom);
        adRequest = new AdRequest.Builder().build();

        String id = getResources().getResourceEntryName(findViewById(R.id.adViewGameStartTop).getId());
        showBannerAdTop(id);

        mAdViewBottom = findViewById(R.id.adViewGameStartBottom);
        clAdHolderBottom = findViewById(R.id.cl_ad_holderBottom);
        ivCustomAdBottom = findViewById(R.id.ivCustomAdBottom);
        ivCloseBottom = findViewById(R.id.ivCloseBottom);
        playerViewBottom = findViewById(R.id.playerViewBottom);
        ivVolumeUpBottom = findViewById(R.id.ivVolumeUpBottom);
        ivVolumeOffBottom = findViewById(R.id.ivVolumeOffBottom);
        buttonFocusListener(llPlayNow);
        buttonFocusListener(btHowToPlay);
        buttonFocusListener(findViewById(R.id.btExit));
        llPlayNow.requestFocus();

        String id1 = getResources().getResourceEntryName(findViewById(R.id.adViewGameStartBottom).getId());
        showBannerAdBottom(id1);

        setData();
        secondsPlayedGames = 0;
        AnalyticsTracker.gamedata = null;
        AnalyticsTracker.gamedata = gameData;
        //startAnimation();
    }

    private void startAnimation() {
        Animation animation;
        animation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.rotate_coin);
        ivCoinImage.startAnimation(animation);
    }

    private void setData() {
        tvGameTitle.setText(gameData.postTitle);
        tvGameDescription.setText(gameData.description);
        tvCouponName.setText(gameData.coupon);
        Utility.setShadeBackground(llPlayNow);
        Glide.with(this)
                .load(gameData.rectangle_image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(ivGameImage);

        Glide.with(this)
                .load(gameData.tvLogo)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(ivTvLogo);

        Glide.with(this)
                .load(gameData.couponImg)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(ivCouponImage);
        setQuizTypeData();


        if (gameData != null) {
            tvEntryFees.setText(gameData.entryFees + getString(R.string.icoins));
        }

    }

    private void setQuizTypeData() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int imageHeight = 0;
        if (Utility.isTelevision()) {
            llAssociatedWith.setVisibility(View.GONE);
            llWinCoupons.setVisibility(View.GONE);
            llWinCoins.setVisibility(View.GONE);
            llTimer.setVisibility(View.GONE);
            tvGameDescription.setVisibility(View.VISIBLE);
            final float scale = this.getResources().getDisplayMetrics().density;
            int dpWidthInPx = (int) (185 * scale);
            int dpHeightInPx = (int) (120 * scale);
            ViewGroup.LayoutParams params = cvImage.getLayoutParams();
            // Changes the height and width to the specified *pixels*
            params.height = dpHeightInPx;
            params.width = dpWidthInPx;
            cvImage.setLayoutParams(params);
        } else {
            switch (gameData.quizType) {
                case GameData.QUIZE_TYPE_LIVE:
                    setTimerLayout();
                    setTimer();
                    llAssociatedWith.setVisibility(View.VISIBLE);
                    llWinCoupons.setVisibility(View.VISIBLE);
                    llWinCoins.setVisibility(View.GONE);
                    tvGameDescription.setVisibility(View.GONE);
                    llTimer.setVisibility(View.VISIBLE);
                    String timeLeft = GameDateValidation.getTimeLeft(gameData.end);
                    tvGameTime.setText(timeLeft);
                    imageHeight = (int) (displaymetrics.widthPixels / 3);
                    cvImage.getLayoutParams().height = imageHeight;
                    break;
                case GameData.QUIZE_TYPE_TURN_BASED:
                    llAssociatedWith.setVisibility(View.GONE);
                    llWinCoupons.setVisibility(View.GONE);
                    llWinCoins.setVisibility(View.GONE);
                    llTimer.setVisibility(View.GONE);
                    tvGameDescription.setVisibility(View.VISIBLE);
                    imageHeight = (int) (displaymetrics.widthPixels / 2);
                    cvImage.getLayoutParams().height = imageHeight;
                    break;
            }
        }
    }

    private void setTimerLayout() {
        llTimer.post(() -> {
            int layoutWidth = llTimer.getWidth();
            tvLabelEndsIn.getLayoutParams().width = layoutWidth / 2;
            tvGameTime.getLayoutParams().width = layoutWidth / 2;
        });
    }


    CountDownTimer countDownTimer;
    String timeLeft = "";

    private void setTimer() {
        SimpleDateFormat calendarDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date formattedGivenDateTime = calendarDateFormat.parse(gameData.end);
            long gameExpireMilliseconds = formattedGivenDateTime.getTime();
            long currentTimeMilliseconds = System.currentTimeMillis();
            long difference = gameExpireMilliseconds - currentTimeMilliseconds;


            countDownTimer = new CountDownTimer(difference, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    timeLeft = GameDateValidation.getTimeLeft(millisUntilFinished);
                    Log.e("Start Time", timeLeft);
                    tvGameTime.setText(timeLeft);
                }

                @Override
                public void onFinish() {
                    //showAlertDialog();

                }
            }.start();

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    boolean isPlayNowClicked = false;

    public void navigateTo(View view) {
        String id = getResources().getResourceEntryName(findViewById(R.id.playNow).getId());
        Log.i(TAG, String.valueOf(id));
        showAd(String.valueOf(id));
        switch (gameData.quizType) {
            case GameData.QUIZE_TYPE_LIVE:
                rlParent.setVisibility(View.GONE);
                rlDownloadingData.setVisibility(View.VISIBLE);
                isPlayNowClicked = true;
                setDownloadingDataUI();
                break;
            case GameData.QUIZE_TYPE_TURN_BASED:
                //setSearchOpponentUI();
                /*Bundle bundle = new Bundle();
                bundle.putParcelable(GameStartActivity.GAME_DATA, gameData);
                startActivity(new Intent(this, GamePlayerActivity.class)
                        .putExtra("Bundle", bundle));*/
                llPlayer.setVisibility(View.VISIBLE);
                llPlayNow.setVisibility(View.GONE);
                break;
        }
    }

    public void exitGame(View view) {
        onBackPressed();
    }

    public void howToPlay(View view) {
        Bundle bundle1 = new Bundle();
        bundle1.putParcelable(GAME_DATA, gameData);
        startActivity(new Intent(GameStartActivity.this, HowToPlayActivity.class)
                .putExtra("Bundle", bundle1));

    }

    // Set text and icon for game data downloading
    private void setDownloadingDataUI() {
        Analyticals.CallPlayedActivity(GAME_ACTIVITY_TYPE, gameData.id, "", "", this, "", new Analyticals.AnalyticsResult() {
            @Override
            public void onResult(boolean success, String activity_id, @Nullable String error) {
                if (success) {
                    LocalStorage.putValue(activity_id, LocalStorage.KEY_ACTIVITY_ID);
                    downloadData();
                } else {

                }
            }
        });
    }

    private void downloadData() {
        // Show game data downloading...
        DownloadUtil.getInstance().downloadItems(this, gameData.arrayquestionData, (success, error) -> {
            //  Analyticals.ACTIVITY_ID=activity_id;
            cancelCountDown();
            Bundle bundle1 = new Bundle();
            bundle1.putParcelable(GAME_DATA, gameData);
            startActivityForResult(new Intent(GameStartActivity.this, GameOngoingLiveActivity.class)
                    .putExtra("TempQuestionType", "")
                    .putExtra("Bundle", bundle1), GameData.REQUEST_CODE_GAME);
        });
    }

    // Set text and icon for search opponent
    private void setSearchOpponentUI() {
        // Connect to WarpClient
        connectWithUsername();
    }

    /*
     * App42
     */
    private void connectWithUsername() {
        llLoader.setVisibility(View.VISIBLE);

        //showToast("Connecting with username: " + LocalStorage.getUserId());
        Log.e(TAG, "Connect with username");
        warpClient.connectWithUserName(LocalStorage.getUserId(), "");
    }

    /*
     * ConnectionRequestListener Methods
     */

    @Override
    public void onConnectDone(ConnectEvent event, String desc) {
        Log.i(TAG, "onConnectDone");
        runOnUiThread(() -> {
            if (event.getResult() == WarpResponseResultCode.SUCCESS) {
                join2UserRoom();
            } else if (event.getResult() == WarpResponseResultCode.BAD_REQUEST) {
                //showToast("Bad Request. Disconnecting...");
                warpClient.disconnect();
            } else {
                llLoader.setVisibility(View.GONE);
                /*AlertUtils.showAlert("Connection Failed", "Result: "
                                + event.getResult(), null, this,
                        isPositiveAction -> finish());*/
                showErrorDialog(event.getResult());
            }
        });
    }

    @Override
    public void onDisconnectDone(ConnectEvent connectEvent) {
        Log.e(TAG, "onDisconnectDone");
    }

    @Override
    public void onInitUDPDone(byte b) {
    }

    /*
     * Join Room / Create Room
     */

    private void join2UserRoom() {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("twoUser", true);
        properties.put("gameId", gameId);
        warpClient.joinRoomWithProperties(properties);
        //showToast("Connected. Joining room...");
    }

    private void createRoom() {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("twoUser", true);
        properties.put("gameId", gameId);
        warpClient.createTurnRoom(String.valueOf(System.currentTimeMillis()), "shephertz",
                2, properties, GameConstants.TURN_TIME);
        //showToast("Creating room...");
    }

    /*
     * ZoneRequestListener Methods
     */

    @Override
    public void onDeleteRoomDone(RoomEvent roomEvent, String s) {
    }

    @Override
    public void onGetAllRoomsDone(AllRoomsEvent allRoomsEvent) {
    }

    @Override
    public void onCreateRoomDone(RoomEvent event, String desc) {
        runOnUiThread(() -> {
            if (event.getResult() == WarpResponseResultCode.SUCCESS) {
                //showToast("Room created. Joining...");
                warpClient.joinRoom(event.getData().getId());
            } else {
                llLoader.setVisibility(View.GONE);
                showErrorDialog(event.getResult());
                /*AlertUtils.showAlert("Room creation failed", "Result" +
                                event.getResult(), "Retry", "Cancel",
                        this, isPositiveAction -> {
                            if (isPositiveAction) {
                                join2UserRoom();
                            } else {
                                finish();
                            }
                        });*/
            }
        });
    }

    @Override
    public void onGetOnlineUsersDone(AllUsersEvent allUsersEvent) {
    }

    @Override
    public void onGetLiveUserInfoDone(LiveUserInfoEvent liveUserInfoEvent) {
    }

    @Override
    public void onSetCustomUserDataDone(LiveUserInfoEvent liveUserInfoEvent) {
    }

    @Override
    public void onGetMatchedRoomsDone(MatchedRoomsEvent matchedRoomsEvent) {
    }

    @Override
    public void onRPCDone(byte b, String s, Object o) {
    }

    /*
     * RoomRequestListener Methods
     */

    @Override
    public void onSubscribeRoomDone(RoomEvent event) {
        Log.e(TAG, "onSubscribeRoomDone-------");
        new Handler(Looper.getMainLooper()).post(() -> llLoader.setVisibility(View.GONE));

        if (event.getResult() == WarpResponseResultCode.SUCCESS) {
            //showToast("Room subscribed. Starting game...");
            long coins = Long.parseLong(gameData.entryFees);
            String des = getString(R.string.played) + gameData.postTitle;
            Wallet.redeemCoins(this, 1, des, Wallet.FLAG_TRIVIA_GAME, coins, (success, error, coins1, diamonds, creditedCoins, historyData, historyCount) -> {
                if (success) {
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("username", userId);
                        Log.e(TAG, "setUserReady");
                        WarpClient.getInstance().invokeRoomRPC(event.getData().getId(), "setUserSubscribed", jsonObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    startGame(event.getData().getId(), event.getData().getMaxUsers());
                } else {
                    AlertUtils.showAlert(getString(R.string.failed_to_subscribe), getString(R.string.result) + event.getResult(),
                            null, GameStartActivity.this, null);
                }
                return success;
            });

        } else {
            AlertUtils.showAlert(getString(R.string.failed_to_subscribe), getString(R.string.result) + event.getResult(),
                    null, this, null);
        }
    }

    @Override
    public void onUnSubscribeRoomDone(RoomEvent roomEvent) {
        Log.e(TAG, "onUnSubscribeRoomDone");

    }

    @Override
    public void onJoinRoomDone(RoomEvent event, String desc) {
        Log.e(TAG, "onJoinRoomDone");

        runOnUiThread(() -> {
            if (event.getResult() == WarpResponseResultCode.SUCCESS) {
                //showToast("Room joined. Subscribing...");
                warpClient.subscribeRoom(event.getData().getId());
            } else {
                createRoom();
            }
        });
    }

    @Override
    public void onLeaveRoomDone(RoomEvent roomEvent) {
    }

    @Override
    public void onGetLiveRoomInfoDone(LiveRoomInfoEvent liveRoomInfoEvent) {
    }

    @Override
    public void onSetCustomRoomDataDone(LiveRoomInfoEvent liveRoomInfoEvent) {
    }

    @Override
    public void onUpdatePropertyDone(LiveRoomInfoEvent liveRoomInfoEvent, String s) {
    }

    @Override
    public void onLockPropertiesDone(byte b) {
    }

    @Override
    public void onUnlockPropertiesDone(byte b) {
    }

    /*
     * Start Game
     */

    private void startGame(@NonNull String roomId, int maxUsers) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(GAME_DATA, gameData);
        Intent intent = new Intent(GameStartActivity.this, GameOngoingActivity.class);
        intent.putExtra("roomId", roomId);
        intent.putExtra("TempQuestionType", "");
        intent.putExtra("Bundle", bundle);
        startActivityForResult(intent, GameOngoingActivity.REQUEST_CODE_OPPONENT_NOT_FOUND);
        //finish();
    }

    private void showToast(@NonNull String message) {
        runOnUiThread(() -> AlertUtils.showToast(message, 0, this));
        Log.d(TAG, message);
    }

    public void singlePlayer(View view) {
        String id = getResources().getResourceEntryName(findViewById(R.id.playNow).getId());
        Log.i(TAG, id);
        Analyticals.CallPlayedActivity(GAME_ACTIVITY_TYPE, gameData.id, "", "", this, "", new Analyticals.AnalyticsResult() {
            @Override
            public void onResult(boolean success, String activity_id, @Nullable String error) {
                if (success) {
                    LocalStorage.putValue(activity_id, LocalStorage.KEY_ACTIVITY_ID);
                    //  Analyticals.ACTIVITY_ID=activity_id;
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(GameStartActivity.GAME_DATA, gameData);
                    startActivityForResult(new Intent(GameStartActivity.this, GameSinglePlayerCountDownActivity.class)
                            .putExtra("Bundle", bundle), WalletActivity.REQUEST_CODE);
                    showAd(String.valueOf(id));
                    Utility.customEventsTracking(Constant.GamePlay, gameData.postTitle);
                } else {

                }
            }
        });
    }

    public void randomOpponent(View view) {
        String id = getResources().getResourceEntryName(findViewById(R.id.btRandomOpponent).getId());
        Log.i(TAG, String.valueOf(id));
        showAd(String.valueOf(id));
        if (gameData != null) {
            if (!gameData.entryFees.equals("0")) {
            /*    Intent intent = new Intent(GameStartActivity.this, MultiplayerCoinAlertActivity.class);
                intent.putExtra(MultiplayerCoinAlertActivity.KEY_WINNER_COINS, String.valueOf(gameData.bonusPoints));
                intent.putExtra(MultiplayerCoinAlertActivity.KEY_USING_COINS, String.valueOf(gameData.entryFees));
                startActivityForResult(intent, MultiplayerCoinAlertActivity.REQUEST_CODE);*/
                connectWithUsername();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (playerTopGame != null && resumePlayerTop) {
            playerTopGame.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
            playerTopGame.setPlayWhenReady(true);
            playerViewTop.setPlayer(playerTopGame);
        }
        if (playerBottomGame != null && resumePlayerBottom) {
            playerBottomGame.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
            playerBottomGame.setPlayWhenReady(true);
            playerViewBottom.setPlayer(playerBottomGame);
        }
        AnalyticsTracker.resumeTimer(AnalyticsTracker.GAME);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (playerTopGame != null) {
            playerTopGame.setPlayWhenReady(false);
            resumePlayerTop = true;
            playerViewTop.setPlayer(null);
        }
        if (playerBottomGame != null) {
            playerBottomGame.setPlayWhenReady(false);
            resumePlayerBottom = true;
            playerViewBottom.setPlayer(null);
        }
    }

    @Override
    protected void onDestroy() {
        AnalyticsTracker.stopDurationGames();
        super.onDestroy();
        cancelCountDown();
        closePlayerTop();
        closePlayerBottom();
    }

    private void cancelCountDown() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private void showErrorDialog(int result) {
        if (!isFinishing()) {
            String message = "";

            switch (result) {
                case 5:
                    message = GameConstants.ALERT_CONN_FAIL;
                    break;
                case 6:
                default:
                    message = GameConstants.GENERIC_ERR_MESSAGE;
                    break;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setView(R.layout.dialog_custom);
            AlertDialog alertDialog = builder.create();
            alertDialog.setCancelable(true);
            alertDialog.show();

            TextView tvTitle = alertDialog.findViewById(R.id.tvStoreName);
            TextView btPositive = alertDialog.findViewById(R.id.btPositive);

            tvTitle.setText(message);
            btPositive.setText(R.string.ok);
            btPositive.setVisibility(View.VISIBLE);
            btPositive.setOnClickListener(v -> {
                finish();
            });
        }
    }

    public void showBannerAdTop(String id) {
        String adType = Utility.getBannerAdType(id, GameStartActivity.this);
        if (!adType.isEmpty()) {
            if (adType.equalsIgnoreCase(Constant.ADMOB)) {
                mAdViewTop.loadAd(adRequest);
                mAdViewTop.setVisibility(View.VISIBLE);
                clAdHolderTop.setVisibility(View.GONE);
            } else if (adType.equalsIgnoreCase(Constant.CUSTOM)) {
                showCustomAdTop(id);
                mAdViewTop.setVisibility(View.GONE);
            }
        }
    }

    public void showCustomAdTop(String id) {
        clAdHolderTop.setVisibility(View.VISIBLE);
        List<AdMobData> list = LocalStorage.getBannerAdMobList(LocalStorage.KEY_BANNER_AD_MOB, GameStartActivity.this);
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).id.equals(id)) {
                    if (list.get(i).feedContentObjectData != null) {
                        int finalI = i;
                        if (list.get(i).feedContentObjectData.mediaType.equalsIgnoreCase(FeedContentData.MEDIA_TYPE_IMAGE)) {
                            String url = list.get(i).feedContentObjectData.adFieldsData.attachment;
                            Glide.with(GameStartActivity.this)
                                    .load(url)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(ivCustomAdTop);
                            ivCustomAdTop.setVisibility(View.VISIBLE);
                            playerViewTop.setVisibility(View.GONE);
                            APIMethods.addEvent(GameStartActivity.this, Constant.VIEW, list.get(i).feedContentObjectData.postId, Constant.BANNER, id);

                            ivCustomAdTop.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    setActionCustomAd(list.get(finalI).feedContentObjectData.adFieldsData, Constant.BANNER, id);
                                }
                            });

                        } else if (list.get(i).feedContentObjectData.mediaType.equalsIgnoreCase(FeedContentData.MEDIA_TYPE_VIDEO)) {
                            playVideoAdTop(list.get(i).feedContentObjectData.adFieldsData.attachment);
                            playerViewTop.setVisibility(View.VISIBLE);
                            ivVolumeOffTop.setVisibility(View.VISIBLE);
                            ivCustomAdTop.setVisibility(View.GONE);
                            APIMethods.addEvent(GameStartActivity.this, Constant.VIEW, list.get(i).feedContentObjectData.postId, Constant.BANNER, id);

                            playerViewTop.getVideoSurfaceView().setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    setActionCustomAd(list.get(finalI).feedContentObjectData.adFieldsData, Constant.BANNER, id);
                                }
                            });
                            ivVolumeUpTop.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {
                                        if (playerTopGame != null && playerTopGame.isPlaying()) {
                                            ivVolumeUpTop.setVisibility(View.GONE);
                                            ivVolumeOffTop.setVisibility(View.VISIBLE);
                                            playerTopGame.setVolume(0.0f);
                                        }
                                    } catch (Exception e) {

                                    }
                                }
                            });
                            ivVolumeOffTop.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {
                                        if (playerTopGame != null && playerTopGame.isPlaying()) {
                                            ivVolumeOffTop.setVisibility(View.GONE);
                                            ivVolumeUpTop.setVisibility(View.VISIBLE);
                                            playerTopGame.setVolume(1.0f);
                                        }
                                    } catch (Exception e) {

                                    }
                                }
                            });
                        } else if (list.get(i).feedContentObjectData.mediaType.equalsIgnoreCase(FeedContentData.MEDIA_TYPE_SLIDER)) {
                            updateOtherSlidersTop(list.get(i).feedContentObjectData.arrayListAdFieldsData, id);
                            playerViewTop.setVisibility(View.GONE);
                            ivVolumeOffTop.setVisibility(View.GONE);
                            ivCustomAdTop.setVisibility(View.GONE);
                            rvSliderAdTop.setVisibility(View.VISIBLE);
                        }
                    }
                    ivCloseTop.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            closePlayerTop();
                        }
                    });
                }
            }
        }
    }

    public void showBannerAdBottom(String id) {
        String adType = Utility.getBannerAdType(id, GameStartActivity.this);
        if (!adType.isEmpty()) {
            if (adType.equalsIgnoreCase(Constant.ADMOB)) {
                mAdViewBottom.loadAd(adRequest);
                mAdViewBottom.setVisibility(View.VISIBLE);
                clAdHolderBottom.setVisibility(View.GONE);
            } else if (adType.equalsIgnoreCase(Constant.CUSTOM)) {
                showCustomAdBottom(id);
                mAdViewBottom.setVisibility(View.GONE);
            }
        }
    }

    public void showCustomAdBottom(String id) {
        clAdHolderBottom.setVisibility(View.VISIBLE);
        List<AdMobData> list = LocalStorage.getBannerAdMobList(LocalStorage.KEY_BANNER_AD_MOB, GameStartActivity.this);
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).id.equals(id)) {
                    if (list.get(i).feedContentObjectData != null) {
                        int finalI = i;
                        if (list.get(i).feedContentObjectData.mediaType.equalsIgnoreCase(FeedContentData.MEDIA_TYPE_IMAGE)) {
                            String url = list.get(i).feedContentObjectData.adFieldsData.attachment;
                            Glide.with(GameStartActivity.this)
                                    .load(url)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(ivCustomAdBottom);
                            ivCustomAdBottom.setVisibility(View.VISIBLE);
                            playerViewBottom.setVisibility(View.GONE);
                            APIMethods.addEvent(GameStartActivity.this, Constant.VIEW, list.get(i).feedContentObjectData.postId, Constant.BANNER, id);

                            ivCustomAdBottom.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    setActionCustomAd(list.get(finalI).feedContentObjectData.adFieldsData, Constant.BANNER, id);
                                }
                            });

                        } else if (list.get(i).feedContentObjectData.mediaType.equalsIgnoreCase(FeedContentData.MEDIA_TYPE_VIDEO)) {
                            playVideoAdBottom(list.get(i).feedContentObjectData.adFieldsData.attachment);
                            playerViewBottom.setVisibility(View.VISIBLE);
                            ivVolumeOffBottom.setVisibility(View.VISIBLE);
                            ivCustomAdBottom.setVisibility(View.GONE);
                            APIMethods.addEvent(GameStartActivity.this, Constant.VIEW, list.get(i).feedContentObjectData.postId, Constant.BANNER, id);

                            playerViewBottom.getVideoSurfaceView().setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    setActionCustomAd(list.get(finalI).feedContentObjectData.adFieldsData, Constant.BANNER, id);
                                }
                            });
                            ivVolumeUpBottom.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {
                                        if (playerBottomGame != null && playerBottomGame.isPlaying()) {
                                            ivVolumeUpBottom.setVisibility(View.GONE);
                                            ivVolumeOffBottom.setVisibility(View.VISIBLE);
                                            playerBottomGame.setVolume(0.0f);
                                        }
                                    } catch (Exception e) {

                                    }
                                }
                            });
                            ivVolumeOffBottom.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {
                                        if (playerBottomGame != null && playerBottomGame.isPlaying()) {
                                            ivVolumeOffBottom.setVisibility(View.GONE);
                                            ivVolumeUpBottom.setVisibility(View.VISIBLE);
                                            playerBottomGame.setVolume(1.0f);
                                        }
                                    } catch (Exception e) {

                                    }
                                }
                            });
                        } else if (list.get(i).feedContentObjectData.mediaType.equalsIgnoreCase(FeedContentData.MEDIA_TYPE_SLIDER)) {
                            updateOtherSlidersBottom(list.get(i).feedContentObjectData.arrayListAdFieldsData, id);
                            playerViewBottom.setVisibility(View.GONE);
                            ivVolumeOffBottom.setVisibility(View.GONE);
                            ivCustomAdBottom.setVisibility(View.GONE);
                            rvSliderAdBottom.setVisibility(View.VISIBLE);
                        }
                    }
                    ivCloseBottom.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            closePlayerBottom();
                        }
                    });
                }
            }
        }
    }

    private void setActionCustomAd(AdFieldsData adFieldsData, String location, String subLocation) {
        APIMethods.addEvent(GameStartActivity.this, Constant.CLICK, adFieldsData.postId, location, subLocation);
        switch (adFieldsData.adType) {
            case FeedContentData.AD_TYPE_IN_APP:
                finish();
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
                startActivity(new Intent(GameStartActivity.this, BrowserActivity.class).putExtra("title", "").putExtra("posturl", adFieldsData.externalUrl));
                break;
        }
    }

    private void playVideoAdTop(String url) {
        playerViewTop.setUseController(false);
        playerViewTop.requestFocus();

        Uri uri = Uri.parse(url);
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(GameStartActivity.this, getApplicationContext().getPackageName(), bandwidthMeter);
        MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(uri));

        playerTopGame = new ExoPlayer.Builder(GameStartActivity.this).build();
        playerTopGame.addListener(new Player.Listener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == Player.STATE_ENDED) {
                    playerTopGame.seekTo(0);
                }
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                playVideoAdTop(url);
            }
        });
        playerTopGame.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT);
        playerTopGame.prepare(mediaSource);
        playerTopGame.setPlayWhenReady(true);

        /*
          Volume is set to 0 as per requirement
          Use as per the case
         */
        playerTopGame.setVolume(0.0f);

        playerViewTop.setPlayer(playerTopGame);

        ViewTreeObserver vto = playerViewTop.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                try {
                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                    if (Build.VERSION.SDK_INT >= 14) {
                        try {
                            retriever.setDataSource(url, new HashMap<String, String>());
                        } catch (RuntimeException ex) {
                            // something went wrong with the file, ignore it and continue
                        }
                    } else {
                        retriever.setDataSource(url);
                    }
                    int videoWidth = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
                    int videoHeight = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
                    retriever.release();

                    playerViewTop.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    int playerViewWidth = playerViewTop.getWidth();
                    if (playerViewWidth < videoWidth) {
                        int scale = (Math.abs(playerViewWidth - videoWidth)) / 2;
                        int height = videoHeight - scale;
                        playerViewTop.getLayoutParams().height = height;
                    } else {
                        int scale = (int) (Math.abs(playerViewWidth - videoWidth) * 1.5);
                        int height = Math.abs(videoHeight + scale);
                        playerViewTop.getLayoutParams().height = height;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void closePlayerTop() {
        if (playerTopGame != null) {
            playerTopGame.setPlayWhenReady(false);
            playerTopGame.stop();
            playerTopGame.release();
            playerTopGame = null;
        }
        if (playerViewTop != null) {
            playerViewTop.setPlayer(null);
        }
    }

    private void playVideoAdBottom(String url) {
        playerViewBottom.setUseController(false);
        playerViewBottom.requestFocus();

        Uri uri = Uri.parse(url);
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(GameStartActivity.this, getApplicationContext().getPackageName(), bandwidthMeter);
        MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(uri));

        playerBottomGame = new ExoPlayer.Builder(GameStartActivity.this).build();
        playerBottomGame.addListener(new Player.Listener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == Player.STATE_ENDED) {
                    playerBottomGame.seekTo(0);
                }
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                playVideoAdBottom(url);
            }
        });
        playerBottomGame.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT);
        playerBottomGame.prepare(mediaSource);
        playerBottomGame.setPlayWhenReady(true);

        /*
          Volume is set to 0 as per requirement
          Use as per the case
         */
        playerBottomGame.setVolume(0.0f);

        playerViewBottom.setPlayer(playerBottomGame);

        ViewTreeObserver vto = playerViewBottom.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                try {
                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                    if (Build.VERSION.SDK_INT >= 14) {
                        try {
                            retriever.setDataSource(url, new HashMap<String, String>());
                        } catch (RuntimeException ex) {
                            // something went wrong with the file, ignore it and continue
                        }
                    } else {
                        retriever.setDataSource(url);
                    }
                    int videoWidth = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
                    int videoHeight = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
                    retriever.release();

                    playerViewBottom.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    int playerViewWidth = playerViewBottom.getWidth();
                    if (playerViewWidth < videoWidth) {
                        int scale = (Math.abs(playerViewWidth - videoWidth)) / 2;
                        int height = videoHeight - scale;
                        playerViewBottom.getLayoutParams().height = height;
                    } else {
                        int scale = (int) (Math.abs(playerViewWidth - videoWidth) * 1.5);
                        int height = Math.abs(videoHeight + scale);
                        playerViewBottom.getLayoutParams().height = height;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void closePlayerBottom() {
        if (playerBottomGame != null) {
            playerBottomGame.setPlayWhenReady(false);
            playerBottomGame.stop();
            playerBottomGame.release();
            playerBottomGame = null;
        }
        if (playerViewBottom != null) {
            playerViewBottom.setPlayer(null);
        }
    }

    /**
     * Initializes slider recycler view
     * Set the slider recyclerview of Custom ads
     */
    private void setOtherSliderRecyclerViewTop(String id) {
        adapterSliderAdTop = new KRecyclerViewAdapter(GameStartActivity.this, adSliderObjectArrayListTop, new KRecyclerViewHolderCallBack() {
            @Override
            public KRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_slider, viewGroup, false);

                adapterSliderAdTop.getItemCount();
                adapterSliderAdTop.getSelectedIndexes();
                return new CustomAdSliderHolder(layoutView, adSliderObjectArrayListTop.size(), Constant.BANNER, id, new SliderListener() {
                    @Override
                    public void stopScrolling() {
                        stopSlidingTop();
                    }
                });
            }
        }, new KRecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClicked(KRecyclerViewHolder kRecyclerViewHolder, Object o, int i) {
                if (o instanceof AdFieldsData) {
                    AdFieldsData adFieldsData = (AdFieldsData) o;
                    setActionCustomAd(adFieldsData, Constant.BANNER, id);
                }
            }
        });

        layoutManagerTop = new CustomLinearLayoutManager(GameStartActivity.this, LinearLayoutManager.HORIZONTAL, false);
        rvSliderAdTop.setLayoutManager(layoutManagerTop);
        rvSliderAdTop.setNestedScrollingEnabled(false);
        rvSliderAdTop.setOnFlingListener(null);
        rvSliderAdTop.setAdapter(adapterSliderAdTop);

        // Dot indicator for banner
        final int radius = getResources().getDimensionPixelSize(R.dimen.dot_radius);
        final int dotsHeight = getResources().getDimensionPixelSize(R.dimen.dot_height);
        final int color = ContextCompat.getColor(GameStartActivity.this, R.color.colorAccent);
        final int inActiveColor = ContextCompat.getColor(GameStartActivity.this, R.color.game_gray);
        if (!flagDecorationTop) {
            rvSliderAdTop.addItemDecoration(new DotsIndicatorDecoration(radius, radius * 2, dotsHeight, inActiveColor, color));
            flagDecorationTop = true;
        }

        new PagerSnapHelper().attachToRecyclerView(rvSliderAdTop);

        rvSliderAdTop.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                shouldSlideTop = (newState == RecyclerView.SCROLL_STATE_IDLE);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    sliderPositionTop = layoutManagerTop.findFirstCompletelyVisibleItemPosition();
                    startSlidingTop();
                } else {
                    stopSlidingTop();
                }
            }
        });
    }

    private Handler sliderHandlerTop;
    private Runnable sliderRunnableTop;
    private long secondsToWait = 4000;

    private void startSlidingTop() {
        if (sliderHandlerTop == null) {
            sliderHandlerTop = new Handler();
        }
        if (sliderRunnableTop == null) {
            sliderRunnableTop = this::changeSliderPageTop;
        }
        sliderHandlerTop.postDelayed(sliderRunnableTop, secondsToWait);
    }

    private void stopSlidingTop() {
        if (sliderHandlerTop != null && sliderRunnableTop != null) {
            sliderHandlerTop.removeCallbacks(sliderRunnableTop);
        }
    }

    @SuppressLint("SetTextI18n")
    private void changeSliderPageTop() {
        if (adSliderObjectArrayListTop.size() <= 1) return;
        try {
            LinearLayoutManager layoutManager = (LinearLayoutManager) rvSliderAdTop.getLayoutManager();
            if (layoutManager != null) {
                visibleItemPositionTop = layoutManager.findLastCompletelyVisibleItemPosition();
                if (visibleItemPositionTop > -1 && visibleItemPositionTop < adSliderObjectArrayListTop.size()) {
                    if (visibleItemPositionTop == adSliderObjectArrayListTop.size() - 1) {
                        // Scroll to first item
                        rvSliderAdTop.smoothScrollToPosition(0);
                    } else {
                        // Scroll to next item
                        rvSliderAdTop.smoothScrollToPosition(visibleItemPositionTop + 1);
                    }
                }
            }
        } catch (Exception ignored) {

        }
    }

    private void updateOtherSlidersTop(ArrayList<AdFieldsData> adFieldsDataArrayList, String id) {
        setOtherSliderRecyclerViewTop(id);
        flagAdTop = true;
        flagDecorationTop = false;
        adSliderObjectArrayListTop.clear();
        new Thread(() -> {
            for (AdFieldsData adFieldsData : adFieldsDataArrayList) {
                sliderIdAdTop = String.valueOf(adFieldsData.postId);
                adSliderObjectArrayListTop.addAll(adFieldsDataArrayList);
                break;
            }

            new Handler(Looper.getMainLooper()).post(() -> {
                if (adapterSliderAdTop != null)
                    adapterSliderAdTop.notifyDataSetChanged();
                startSlidingTop();
            });
        }).start();
    }

    /**
     * Initializes slider recycler view
     * Set the slider recyclerview of Custom ads
     */
    private void setOtherSliderRecyclerViewBottom(String id) {
        adapterSliderAdBottom = new KRecyclerViewAdapter(GameStartActivity.this, adSliderObjectArrayListBottom, new KRecyclerViewHolderCallBack() {
            @Override
            public KRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_slider, viewGroup, false);

                adapterSliderAdBottom.getItemCount();
                adapterSliderAdBottom.getSelectedIndexes();
                return new CustomAdSliderHolder(layoutView, adSliderObjectArrayListBottom.size(), Constant.BANNER, id, new SliderListener() {
                    @Override
                    public void stopScrolling() {
                        stopSlidingBottom();
                    }
                });
            }
        }, new KRecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClicked(KRecyclerViewHolder kRecyclerViewHolder, Object o, int i) {
                if (o instanceof AdFieldsData) {
                    AdFieldsData adFieldsData = (AdFieldsData) o;
                    setActionCustomAd(adFieldsData, Constant.BANNER, id);
                }
            }
        });

        layoutManagerBottom = new CustomLinearLayoutManager(GameStartActivity.this, LinearLayoutManager.HORIZONTAL, false);
        rvSliderAdBottom.setLayoutManager(layoutManagerBottom);
        rvSliderAdBottom.setNestedScrollingEnabled(false);
        rvSliderAdBottom.setOnFlingListener(null);
        rvSliderAdBottom.setAdapter(adapterSliderAdBottom);

        // Dot indicator for banner
        final int radius = getResources().getDimensionPixelSize(R.dimen.dot_radius);
        final int dotsHeight = getResources().getDimensionPixelSize(R.dimen.dot_height);
        final int color = ContextCompat.getColor(GameStartActivity.this, R.color.colorAccent);
        final int inActiveColor = ContextCompat.getColor(GameStartActivity.this, R.color.game_gray);
        if (!flagDecorationBottom) {
            rvSliderAdBottom.addItemDecoration(new DotsIndicatorDecoration(radius, radius * 2, dotsHeight, inActiveColor, color));
            flagDecorationBottom = true;
        }

        new PagerSnapHelper().attachToRecyclerView(rvSliderAdBottom);

        rvSliderAdBottom.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                shouldSlideBottom = (newState == RecyclerView.SCROLL_STATE_IDLE);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    sliderPositionBottom = layoutManagerBottom.findFirstCompletelyVisibleItemPosition();
                    startSlidingBottom();
                } else {
                    stopSlidingBottom();
                }
            }
        });
    }

    private Handler sliderHandlerBottom;
    private Runnable sliderRunnableBottom;

    private void startSlidingBottom() {
        if (sliderHandlerBottom == null) {
            sliderHandlerBottom = new Handler();
        }
        if (sliderRunnableBottom == null) {
            sliderRunnableBottom = this::changeSliderPageBottom;
        }
        sliderHandlerBottom.postDelayed(sliderRunnableBottom, secondsToWait);
    }

    private void stopSlidingBottom() {
        if (sliderHandlerBottom != null && sliderRunnableBottom != null) {
            sliderHandlerBottom.removeCallbacks(sliderRunnableBottom);
        }
    }

    @SuppressLint("SetTextI18n")
    private void changeSliderPageBottom() {
        if (adSliderObjectArrayListBottom.size() <= 1) return;
        try {
            LinearLayoutManager layoutManager = (LinearLayoutManager) rvSliderAdBottom.getLayoutManager();
            if (layoutManager != null) {
                visibleItemPositionBottom = layoutManager.findLastCompletelyVisibleItemPosition();
                if (visibleItemPositionBottom > -1 && visibleItemPositionBottom < adSliderObjectArrayListBottom.size()) {
                    if (visibleItemPositionBottom == adSliderObjectArrayListBottom.size() - 1) {
                        // Scroll to first item
                        rvSliderAdBottom.smoothScrollToPosition(0);
                    } else {
                        // Scroll to next item
                        rvSliderAdBottom.smoothScrollToPosition(visibleItemPositionBottom + 1);
                    }
                }
            }
        } catch (Exception ignored) {

        }
    }

    private void updateOtherSlidersBottom(ArrayList<AdFieldsData> adFieldsDataArrayList, String id) {
        setOtherSliderRecyclerViewBottom(id);
        flagAdBottom = true;
        flagDecorationBottom = false;
        adSliderObjectArrayListBottom.clear();
        new Thread(() -> {
            for (AdFieldsData adFieldsData : adFieldsDataArrayList) {
                sliderIdAdBottom = String.valueOf(adFieldsData.postId);
                adSliderObjectArrayListBottom.addAll(adFieldsDataArrayList);
                break;
            }

            new Handler(Looper.getMainLooper()).post(() -> {
                if (adapterSliderAdBottom != null)
                    adapterSliderAdBottom.notifyDataSetChanged();
                startSlidingBottom();
            });
        }).start();
    }

    private void buttonFocusListener(View view) {
        if (Utility.isTelevision()) {
            view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (view.hasFocus()) {
                        view.setBackground(view.getContext().getDrawable(R.drawable.bg_accent));
                    } else {
                        view.setBackground(view.getContext().getDrawable(R.drawable.bg_lightgray));
                    }
                }
            });
        }
    }
}
