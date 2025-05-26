package com.app.itaptv.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PersistableBundle;
import android.os.StrictMode;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Request;
import com.app.itaptv.API.APIManager;
import com.app.itaptv.API.APIMethods;
import com.app.itaptv.API.APIRequest;
import com.app.itaptv.API.APIResponse;
import com.app.itaptv.API.Url;
import com.app.itaptv.BuildConfig;
import com.app.itaptv.MyApp;
import com.app.itaptv.NavigationMenu;
import com.app.itaptv.NestedScrollableViewHelper;
import com.app.itaptv.R;
import com.app.itaptv.adapter.ViewPagerAdapter;
import com.app.itaptv.custom_interface.NavigationMenuCallback;
import com.app.itaptv.custom_interface.OnFeedRefreshListener;
import com.app.itaptv.custom_interface.OnPlayerListener;
import com.app.itaptv.custom_interface.PlaybackStateListener;
import com.app.itaptv.custom_interface.WalletCallback;
import com.app.itaptv.fragment.AvatarFragment;
import com.app.itaptv.fragment.ChannelCategoryFragment;
import com.app.itaptv.fragment.CommentListFragment;
import com.app.itaptv.fragment.CouponFragment;
import com.app.itaptv.fragment.CreateFragment;
import com.app.itaptv.fragment.DiamondsFragment;
import com.app.itaptv.fragment.DownloadOptionsBottomSheetFrag;
import com.app.itaptv.fragment.EpisodesFragment;
import com.app.itaptv.fragment.HomeBuyTabFragment;
import com.app.itaptv.fragment.HomePlayTabFragment;
import com.app.itaptv.fragment.LiveJavaFrag;
import com.app.itaptv.fragment.MainHomeFragment;
import com.app.itaptv.fragment.MoreFragment;
import com.app.itaptv.tv_fragment.BuyDetailFragment;
import com.app.itaptv.tv_fragment.MoviesFragment;
import com.app.itaptv.fragment.NotificationFragment;
import com.app.itaptv.fragment.ProfileFragment;
import com.app.itaptv.fragment.ShopFrag;
import com.app.itaptv.fragment.SearchFragment;
import com.app.itaptv.holder.EpisodeHolder;
import com.app.itaptv.holder.FeedHolder;
import com.app.itaptv.offline.DownloadTracker;
import com.app.itaptv.roomDatabase.ListRoomDatabase;
import com.app.itaptv.roomDatabase.MediaDuration;
import com.app.itaptv.roomDatabase.Playlist;
import com.app.itaptv.service.AudioPlayerService;
import com.app.itaptv.structure.AdFieldsData;
import com.app.itaptv.structure.AdMobData;
import com.app.itaptv.structure.FeedContentData;
import com.app.itaptv.structure.GameData;
import com.app.itaptv.structure.PurchaseData;
import com.app.itaptv.structure.QuestionChoiceData;
import com.app.itaptv.structure.QuestionData;
import com.app.itaptv.structure.SeasonData;
import com.app.itaptv.structure.SeriesData;
import com.app.itaptv.structure.SubscriptionDetails;
import com.app.itaptv.structure.User;
import com.app.itaptv.trillbit.Constants;
import com.app.itaptv.tv_fragment.PremiumTvFragment;
import com.app.itaptv.tv_fragment.SearchTvFragment;
import com.app.itaptv.tv_fragment.ViewAllTvFragment;
import com.app.itaptv.utils.AlertUtils;
import com.app.itaptv.utils.Analyticals;
import com.app.itaptv.utils.AnalyticsTracker;
import com.app.itaptv.utils.Constant;
import com.app.itaptv.utils.ExoUtil;
import com.app.itaptv.utils.FirebaseAnalyticsLogs;
import com.app.itaptv.utils.GameDateValidation;
import com.app.itaptv.utils.KPlayerView;
import com.app.itaptv.utils.LicenseManager;
import com.app.itaptv.utils.LocalStorage;
import com.app.itaptv.utils.Log;
import com.app.itaptv.utils.SpacingItemDecoration;
import com.app.itaptv.utils.Utility;
import com.app.itaptv.utils.Wallet;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.drm.DrmSessionEventListener;
import com.google.android.exoplayer2.drm.OfflineLicenseHelper;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.dash.DashUtil;
import com.google.android.exoplayer2.source.dash.manifest.DashManifest;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.DefaultTimeBar;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.TimeBar;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.ActivityResult;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kalpesh.krecycleradapter.Interface.KRecyclerItemClickListener;
import com.kalpesh.krecycleradapter.Interface.KRecyclerViewHolderCallBack;
import com.kalpesh.krecycleradapter.KRecyclerViewAdapter;
import com.kalpesh.krecycleradapter.KRecyclerViewHolder;
import com.makeramen.roundedimageview.RoundedImageView;
import com.shephertz.app42.paas.sdk.android.App42API;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.user.UserService;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import static com.app.itaptv.structure.FeedContentData.POST_TYPE_AD;
import static com.app.itaptv.structure.FeedContentData.POST_TYPE_EPISODE;
import static com.app.itaptv.structure.FeedContentData.POST_TYPE_ORIGINALS;
import static com.app.itaptv.structure.FeedContentData.POST_TYPE_PLAYLIST;
import static com.app.itaptv.structure.FeedContentData.POST_TYPE_POST;
import static com.app.itaptv.structure.FeedContentData.POST_TYPE_STREAM;
import static com.app.itaptv.utils.Analyticals.ACTIVITY_TYPE_PLAY_AUDIO;
import static com.app.itaptv.utils.Analyticals.CONTEXT_APP_OPEN;
import static com.app.itaptv.utils.Constant.APP_MAINTENANCE;
import static com.app.itaptv.utils.Constant.GAME;
import static com.app.itaptv.utils.Constant.KEY_APP_URL;
import static com.app.itaptv.utils.Constant.KEY_DESCRIPTION;
import static com.app.itaptv.utils.Constant.KEY_FCM_TOKEN;
import static com.app.itaptv.utils.Constant.KEY_IMAGE;
import static com.app.itaptv.utils.Constant.KEY_INFO_TYPE;
import static com.app.itaptv.utils.Constant.KEY_TITLE;
import static com.app.itaptv.utils.Constant.MAINTENANCE_GENERAL;
import static com.app.itaptv.utils.Constant.MAINTENANCE_SYSTEM_DOWN;
import static com.app.itaptv.utils.Constant.nav_menu_home;

import com.app.itaptv.interfaces.NavigationStateListener;

import com.app.itaptv.interfaces.FragmentChangeListener;

import com.surveymonkey.surveymonkeyandroidsdk.SurveyMonkey;

public class HomeActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener,
        OnFeedRefreshListener, OnPlayerListener, BottomNavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener, TabLayout.BaseOnTabSelectedListener, LifecycleObserver, DownloadTracker.Listener,
        DownloadOptionsBottomSheetFrag.ItemClickListener, CommentListFragment.CommentCount, NavigationStateListener, FragmentChangeListener,
        NavigationMenuCallback {

    public static final String IS_DOWNLOADING = "isDownloading";
    public static final String IS_DOWNLOADED = "isDownloaded";
    public static final String IS_DOWNLOAD_FAILED = "isDownloadFailed";
    public static final String IS_GO_TO_DOWNLOAD = "isGotoDownload";
    public static final int SM_REQUEST_CODE = 0;
    public static final String SM_RESPONDENT = "smRespondent";
    public static final String SM_ERROR = "smError";
    public static final String RESPONSES = "responses";
    public static final String SAMPLE_APP = "iTap App";
    public static final String SURVEY_HASH = "S3TDHCG";
    public static final int PTYPE = 3;
    private static final String TAG = "HomeActivity";
    public static boolean startProfileFragment = false;
    public static String VOUCHER_BROADCAST_KEY = "myVoucherBroadCast";
    public static String STORE_BROADCAST_KEY = "myStoreBroadCast";
    public static String DOWNLOADS_BROADCAST_KEY = "myDownloadsBroadCast";
    public static String DOWNLOADING_BROADCAST_KEY = "mydownloadingBroadCast";
    public static String AD_NOTIFICATION_BROADCAST_KEY = "adnotificationBroadCast";
    public static String EARN_COINS_KEY = "earncoins";
    public static TextView toolbarWalletBalance;
    public static TextView toolbarDiamonds;
    public static View toolbarNotificationButton;
    public static View toolbarsearch_button;
    public static View toolbarlive_button;
    public static TextView toolbarearncoins_button;
    public static int Request_code_for_comment = 123;
    public static String feedId = "";
    public static String contentOrientation = "";
    public static String POSITION = "position";
    static HomeActivity homeActivity;
    private static boolean isFirstTime = true; //for loading home fragment for 1st time
    private static int trackPosition;
    final Handler audioPlayDelayHandler = new Handler();
    // Full Screen Code
    private final String STATE_RESUME_WINDOW = "resumeWindow";
    private final String STATE_RESUME_POSITION = "resumePosition";
    private final String STATE_PLAYER_FULLSCREEN = "playerFullscreen";
    private final String STATE_CONTEXT_TYPE = "contextType";
    private final BroadcastReceiver downloadsBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (intent.getAction().equals(DOWNLOADS_BROADCAST_KEY)) {
                    openDownloads();
                }
            }
        }
    };
    public LinearLayout llMainBottomPlayerView;
    public CardView cvPlayer;
    public ProgressBar progressBar, downloadProgressBar;
    public ImageButton ibPlay;
    public ImageButton ibRewind;
    public ImageButton ibForward;
    public DefaultTimeBar exo_progress;
    public long walletBalance;
    public Boolean callSettings = true;
    //for nextSeries
    public Button btNextSeries;
    public ArrayList<FeedContentData> arrayListPlaylist = new ArrayList<>();
    //DrawerLayout drawerLayout;
    RelativeLayout llBottomPlayerView;
    RelativeLayout rlMediaControllerLayout;
    RelativeLayout playerHolder;
    //PlayerView playerView;
    KPlayerView playerView;
    BottomNavigationView bottomNavigationView;
    int purchaseNextPageNo = 1;
    ArrayList<FeedContentData> arrayListFeedContent = new ArrayList<>();
    ArrayList<String> arrayListPurchaseData = new ArrayList<>();
    FrameLayout container;
    FeedContentData feedContentPlayerData;
    FeedContentData feedContentDataSeries;
    SeasonData seasonData;
    LinearLayout llEpisodeData;
    LinearLayout llLoader;
    LinearLayout llExpandableViewLoader;
    NestedScrollView nsvNestedScrollView;
    ImageButton ibclose;
    ImageButton ibQuestionClose, ibShopClose;
    ImageView ivAudioImage;
    //ImageView exo_fullscreen_icon;
    TextView tvAudioTitle;
    TextView tvAudioTitlesiler;
    TextView tvAudioSubtitleSlider;
    TextView tvAudioSubtitle;
    TextView txtQuestion;
    TextView tvWonCoins;
    TextView tvWonCoin;
    TextView tvMenuWalletBalance;
    TextView tvQuestionPoints;
    TextView tvLikeMusic, tvAddtoWatchlist, btLike, tvCommentMusic, tvDownload, tvVisitUrl;
    TextView tvLike, tvComment;
    ImageView tvAddtoWatch;
    ImageView ivLike;
    WebView webView;
    AdView mAdView;
    AdRequest adRequest;
    ConstraintLayout clAdHolder;
    ImageView ivCustomAd;
    ImageView ivClose;
    PlayerView pvCustomAd;
    ImageView ivVolumeUp;
    ImageView ivVolumeOff;
    TextView tvSeason;
    Button btOption1;
    Button btOption2;
    Button btOption3;
    Button btOption4;
    Button btViewMore;
    MenuItem actionCoins;
    MenuItem actionNotification;
    // YouTubePlayerView youTubePlayerView;
    // YouTubePlayer.OnInitializedListener onInitializedListener;
    Thread thread;
    String action = "";
    FrameLayout mainMediaFrame;
    ImageView ivAd;
    LifecycleOwner lifecycleOwner;
    ListRoomDatabase listRoomDatabase;
    ArrayList<Button> ArrayOfButton;
    ArrayList<QuestionData> arrayListQuestionData = new ArrayList<>();
    RelativeLayout questionHolder;
    LinearLayout llQuestion, llProgressbar;
    RelativeLayout rlRightAnswer;
    RelativeLayout rlWrongAnswer;
    RelativeLayout rlShopNow;
    CardView cvMissedQuestions;
    ImageView ivNextQuestion, ivNextQuestionW, ivshopnow;
    ProgressBar circleProgressBar;
    TextView tvProgress;
    Dialog alertDialog;
    String contextType = "";
    boolean exoPlayerPrepared = false;
    boolean isPlayerVisible = true;
    boolean isPlayerClosed = true;
    boolean hasSongChanged = false;
    boolean isLoaded = false;
    int playbackPosition;
    int questionIndex;
    int questionNo;
    int totalNoOfQuestions;
    int selectedTabIndex;
    String questiontotalcoins;
    String currentSeasonId;
    int totalcoinsofquestion = 0;
    int firstIndex = 0;
    int lastIndex = 10;
    int totalNumberOfRecords;
    int progressCount = 0;
    //int iProgress = 0;
    Context mContext = HomeActivity.this;
    String n_json_object;
    //---url-------------- EPISODE ------------------//
    RecyclerView rvEpisode;
    LinearLayout llSimilarItems;
    TextView tvListTitle;
    KRecyclerViewAdapter adapterEpisode;
    ArrayList<FeedContentData> arrayListContentData = new ArrayList<>();
    ArrayList<FeedContentData> arrayListEpisodeData = new ArrayList<>();
    ArrayList<FeedContentData> arrayListPlaylistData = new ArrayList<>();
    ArrayList<FeedContentData> arrayListChannelFeedContentData;
    ArrayList<String> purchasedList = new ArrayList<>();
    String[] arrPermissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    ArrayList<MenuItem> bottomNavigationMenuList = new ArrayList<>();
    SeriesData seriesData;
    BroadcastReceiver trillBroadcastReceiver;
    //----------------- PLAYLIST ------------------//
    int currentFeedPosition;
    int prevFeedPosition;
    String currentSongId = "";
    String currentContextId = "";
    ViewPager2 videosViewPager;
    VideosAdapter videosAdapter;
    ArrayList<VideoView> videoViewArrayList = new ArrayList<>();
    ArrayList<TextView> textViewArrayList = new ArrayList<>();
    ArrayList<ImageView> imageViewArrayList = new ArrayList<>();
    ArrayList<String> videoId = new ArrayList<>();
    ArrayList<FeedContentData> verticalVideoList = new ArrayList<>();
    // for view more button
    int buttonViewMoreThreshold = 5;
    //Player Full Screen
    LayoutInflater liSeries;
    View viSeries;
    /*TabLayout tabSeries;
    ViewPager vpSeries;*/ ViewPagerAdapter viewPagerAdapter;
    String[] arrTabSeriesLabels;
    EpisodesFragment episodesFragment = new EpisodesFragment();
    EpisodesFragment episodesFragment1 = new EpisodesFragment();
    Fragment[] arrTabSeriesFragments = {episodesFragment, episodesFragment1};
    LinearLayout lldynamicViewInPlayer;
    BroadcastReceiver earnCoinsBroadCast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (intent.getAction().equals(EARN_COINS_KEY)) {
                    long coins = intent.getLongExtra(EARN_COINS_KEY, 0);
                    //getWalletBalance();
                    setWalletBalance(coins);
                }
            }
        }
    };
    boolean minimisePlayer = false;
    PlayerPagerAdapter playerPagerAdapter;
    Timer timer;
    TimerTask timerTask;
    MainHomeFragment homeFragment;
    MoviesFragment moviesFragment;
    PremiumTvFragment premiumTvFragment;
    ChannelCategoryFragment channelFragment;
    AvatarFragment avatarFragment;
    CreateFragment createFragment;
    // DownloadsFrag downloadsFragment;
    HomeBuyTabFragment premiumFragment;
    MoreFragment moreFragment;
    SearchFragment searchFragment;
    SearchTvFragment searchTvFragment;
    NotificationFragment notificationFragment;
    LiveJavaFrag liveJavaFrag;
    ProfileFragment profileFragment;
    CouponFragment couponFragment;
    ShopFrag shopFrag;
    int playerHeight;
    int previousTabId = R.id.bNavHome;

    private NavigationMenu navMenuFragment;
    FrameLayout nav_fragment;
    private String currentSelectedFragment = nav_menu_home;

    BroadcastReceiver voucherBroadCast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (intent.getAction().equals(VOUCHER_BROADCAST_KEY)) {
                    openVoucher();
                }
            }
        }
    };
    BroadcastReceiver storeBroadCast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (intent.getAction().equals(STORE_BROADCAST_KEY)) {
                    openStore();
                }
            }
        }
    };
    private AudioPlayerService service = null;
    private boolean serviceBound = false;
    private boolean isPausedPlayer = false;
    private AppUpdateManager appUpdateManager;
    private int REQUEST_CODE_UPDATE = 898;
    private int REQUEST_CODE_SURVEY = 899;
    private int REQUEST_CODE_DOWNLOAD = 897;
    private SlidingUpPanelLayout slidingUpPanelLayout;
    private boolean plaChannelAds;
    OrientationEventListener orientationEventListener;

    /*BroadcastReceiver adsNotificationBroadCast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (intent.getAction().equals(AD_NOTIFICATION_BROADCAST_KEY)) {
                    Log.i(TAG, "call in app notification");
                    if (serviceBound && service!=null){
                        showAdsInAppNotification();
                        service.pauseMediaPlayer();
                    }
                }
            }
        }
    };*/
    private DownloadTracker downloadTracker;

    /*BroadcastReceiver checkInternetConnectionBroadCast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (intent.getAction().equals(CHECK_INTERNET_CONNECTION)) {
                    if (!Utility.isConnectingToInternet(getApplicationContext())) {
                        if (serviceBound && service != null) {
                            if(!isPausedPlayer) {
                                isPausedPlayer=true;
                                service.pauseMediaPlayer();
                            }
                        }
                    }
                }
            }
        }
    };*/
    private ArrayList<FeedContentData> playlist = new ArrayList<>();
    // Initialize the SurveyMonkey SDK like so
    private SurveyMonkey s = new SurveyMonkey();
    private boolean resumePlayer = false;
    private ExoPlayer player;
    private boolean isAppBackground = false;
    private boolean mExoPlayerFullscreen = false;
    private boolean mQuestionFullscreen = false;
    private int mResumeWindow;
    private long mResumePosition;
    private Dialog mFullScreenDialog;
    ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinderService) {
            ImageButton playBtn = llMainBottomPlayerView.findViewById(R.id.ibPlay);
            if (iBinderService != null) {
                Log.i("Kalpesh", "service connected");
                AudioPlayerService.LocalBinder binder = (AudioPlayerService.LocalBinder) iBinderService;
                service = binder.getService();
                serviceBound = true;

                if (service.isPlaying() || service.isPlayingAd() || service.isPlayingCreditAd()) {
                    if (playerPagerAdapter != null) {
                        playerPagerAdapter.updatePlayers(false);
                    }
                    checkPlayerVisibility();
                    restartTimer();
                    int position = service.getCurrentWindowIndex();
                    if (position >= 0) {
                        hasSongChanged = false;
                        setExpandedPlayerLayout(0, position, contextType, "", "");
                    }
                    setMinimizedPlayer();
                    //setExpandedPlayer();
                    playBtn.setImageResource(R.drawable.player_icon_pause_small);
                }

                service.setListener(new PlaybackStateListener() {

                    int playedDuration = 0;

                    @Override
                    public void adStarted() {
                        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                        Log.d("Rohini", "adstarted");
                        FeedContentData currentItem = service.getCurrentItem();
                        if (!service.isPlayingCreditAd()) {
                            if (currentItem != null && currentItem.adsArray != null) {
                                int playedSeconds = (int) (service.getPlayer().getCurrentPosition() / 1000);
                                for (FeedContentData ad : currentItem.adsArray) {
                                    int adDiff = Math.abs(playedSeconds - ad.adFieldsData.play_at);
                                    if (adDiff < 2) {
                                        // api call
                                        //ad.id; // ad id
                                        //currentItem.id;
                                        callAddActivityAPI(String.valueOf(ad.postId), Analyticals.CONTEXT_AUDIO_SONG, currentSongId, ad.postTitle);
                                        break;
                                    }
                                }
                            }
                        }
                        restartTimer();
                        /*configureAdPlayer();
                        configureAdPlayer();*/
                        checkPlayerVisibility();
                        // setNoQuestionLayout();
                    }

                    @Override
                    public void adCompleted() {
                        Log.d("Rohini", "adCompleted");
                        checkPlayerVisibility();
                        // getSongQuestionAPI(feedContentPlayerData.postId);
                    }

                    @Override
                    public void songChanged(int position, FeedContentData media, String context_id, String context, String iswatched) {
                        Log.e("KalpeshPlayer", "Song Changed. Pos: " + position);
                        Log.d("Rohini", "songChanged");

                        currentSongId = media.postId;
                        currentContextId = context_id;
                        int prevPosition = playbackPosition;
                        playbackPosition = position;
                        prevPosition = currentFeedPosition;
                        currentFeedPosition = media.feedPosition;

                        //TODO: send media.postId too for saving position against specific id's
                        //UpdatelocalPlayList(position);
                        /*
                        Playlist playlist = new Playlist();
                        playlist.setPosition(position);
                        playlist.setPlayListItems(Collections.singletonList(media));
                        //TODO: check if collections list is being sent or not.
                        playlistRoomDatabase.mediaDAO().updatePlaylist(playlist);*/

                        // Update player UI with media data.
                        hasSongChanged = true;
                        questionNo = 0;
                        setExpandedPlayerLayout(prevPosition, position, context, context_id, iswatched);

                        callAddActivityAPI(media.postId, context, context_id, media.postTitle);
                        // Commented as new duration
                        /*if (!media.coins.equalsIgnoreCase("")) {
                            Wallet.earnCoins(HomeActivity.this, Integer.parseInt(media.postId), media.postTitle, Wallet.FLAG_PLAYBACK, Long.parseLong(media.coins), new WalletCallback() {
                                @Override
                                public boolean onResult(boolean success, @Nullable String error, long coins, JSONArray historyData, int historyCount) {
                                    handleApp42ResponseforSong(success, error, Integer.parseInt(media.postId), Long.parseLong(media.coins), media.postTitle);
                                    return success;
                                }
                            });
                        }*/

                        //restartTimer();
                        checkPlayerVisibility();
                        if (service.getCurrentItem() != null) {
                            updateAdBreakPoints(service.getCurrentItem().adsArray);
                        }
                    }

                    @Override
                    public void playerStateChanged(boolean playWhenReady, int state) {
                        Log.d("Rohini", "playerStateChanged");
                        Log.i("KalpeshPlayer", "State Changed. State: " + state);
                        ImageButton playBtn = llMainBottomPlayerView.findViewById(R.id.ibPlay);
                        if (service.isPlaying()) {

                            // Get Played duration
                            playedDuration = (int) (service.getPlayer().getCurrentPosition() / 1000);
                            // Audio is playing, show pause button.
                            if (playBtn != null) {
                                playBtn.post(() -> {
                                    playBtn.setImageResource(R.drawable.player_icon_pause_small);
                                });
                            }
                        } else {
                            // Audio is paused, show play button.
                            if (playBtn != null) {
                                playBtn.post(() -> {
                                    playBtn.setImageResource(R.drawable.ic_app_icon_play);
                                });
                            }
                        }
                        if (state == Player.STATE_ENDED) {
                            // Finished playing, hide player fragment if required.
                        } else {
                            if (!isPlayerClosed) {
                                setMinimizedPlayer();
                                Log.i("playerexpanded", "Player ");
                            } else {
                                closePlayer(null);
                            }
                        }
                        if (playerPagerAdapter != null) {
                            playerPagerAdapter.setVisibilityListener();
                        }

                        // Update seek bar
                        updateSeekBar();
                        restartTimer();
                        checkPlayerVisibility();
                        preventScreenOff();
                    }

                    @Override
                    public void playerError(PlaybackException error) {

                    }

                    @Override
                    public void playerError(ExoPlaybackException error) {
                        Log.e("KalpeshPlayer", "Error: " + error.getMessage());
                    }

                    @Override
                    public void playerClosed() {
                        closePlayer(null);
                    }

                });
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("Kalpesh", "service disconnected");
            serviceBound = false;
            service.setListener(null);
        }
    };
    private Dialog mFullScreenQuestionDialog;
    private BroadcastReceiver downloadingBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isDownloading = intent.getBooleanExtra(IS_DOWNLOADING, false);
            boolean isDownloaded = intent.getBooleanExtra(IS_DOWNLOADED, false);
            boolean isDownloadFailed = intent.getBooleanExtra(IS_DOWNLOAD_FAILED, false);
            boolean isGoToDownload = intent.getBooleanExtra(IS_GO_TO_DOWNLOAD, false);
            if (isDownloading) {
                setDownloadedImage(IS_DOWNLOADING);
            }
            if (isDownloaded) {
                setDownloadedImage(IS_DOWNLOADED);
            }
            if (isDownloadFailed) {
                setDownloadedImage(IS_DOWNLOAD_FAILED);
            }
            if (isGoToDownload) {
                openDownloads();
            }
        }
    };
    private View collapsedPlayer;
    private View expandedPlayer;

    /*
    DownloadTracker Listeners - Start
     */
    private FeedContentData channel = null;
    private int nextPageNo = 1;

    public static HomeActivity getInstance() {
        return homeActivity;
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    /*
    DownloadTracker Listeners - End
     */

    static public Uri getLocalBitmapUri(Bitmap bmp, Context context) {
        Uri bmpUri = null;
        try {
            File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            //bmpUri = Uri.fromFile(file);
            bmpUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".fileprovider", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    public static int booleanToInt(boolean value) {
        // Convert true to 1 and false to 0.
        return value ? 1 : 0;
    }

    public static boolean IntToboolean(int i) {
        // Convert true to 1 and false to 0.
        return i == 1 ? true : false;
    }

    /*private void configureAdPlayer() {
        if (playerPagerAdapter != null) {
            playerPagerAdapter.configureAdPlayer();
        }
    }*/

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putBoolean("service_state", serviceBound);
        outState.putInt("playback_position", playbackPosition);
        outState.putInt(STATE_RESUME_WINDOW, mResumeWindow);
        outState.putLong(STATE_RESUME_POSITION, mResumePosition);
        outState.putBoolean(STATE_PLAYER_FULLSCREEN, mExoPlayerFullscreen);
        outState.putString(STATE_CONTEXT_TYPE, contextType);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            serviceBound = savedInstanceState.getBoolean("service_state", false);
            playbackPosition = savedInstanceState.getInt("playback_position", 0);
        }
    }

    // Life cycle observers
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onAppBackgrounded() {
        Log.d("MyApp", "App in background");
        isAppBackground = true;
        if (serviceBound) {
            if (service != null && (service.isPlaying() || service.isPlayingAd())) {
                FeedContentData currentItem = service.getCurrentItem();
                if (currentItem != null) {
                    if (currentItem.attachmentData.type.equalsIgnoreCase("video")) {
                        if (service.getPlayer() != null) {
                            service.getPlayer().setPlayWhenReady(false);
                        }
                        if (service.getAdPlayer() != null) {
                            service.getAdPlayer().setPlayWhenReady(false);
                            service.cleanUpAdPlayer();
                        }
                        //playerView.showMediaPlayer(false);
                    } else if (currentItem.singleCredit) {
                        if (service.getPlayer() != null) {
                            service.getPlayer().setPlayWhenReady(false);
                        }
                    }
                }
            }
            if (playerPagerAdapter != null) {
                playerPagerAdapter.resetPlayers();
            }
        }
        AnalyticsTracker.pauseTimer(AnalyticsTracker.GAME);
        AnalyticsTracker.pauseTimer(AnalyticsTracker.USER);
        AnalyticsTracker.pauseTimer(AnalyticsTracker.AVATAR);
        AnalyticsTracker.pauseTimer(AnalyticsTracker.SHOP);
        AnalyticsTracker.pauseTimer(AnalyticsTracker.VIDEO);
        //MyApp application = (MyApp) getApplication();
        //application.isUpdateScreenVisible=false;
    }

    /*DefaultTimeBar mediaPlayerTimeBar;
    ProgressBar adProgressbar;*/

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onAppForegrounded() {
        Log.d("MyApp", "App in foreground");
        isAppBackground = false;
        AnalyticsTracker.resumeTimer(AnalyticsTracker.USER);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy =
                new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //getWindow().addFlags(LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_home);
        LocalStorage.setLoginScreenShown(false);
        homeActivity = this;
        isFirstTime = true;
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        downloadTracker = MyApp.getInstance().getDownloadTracker();

        if (savedInstanceState != null) {
            mResumeWindow = savedInstanceState.getInt(STATE_RESUME_WINDOW);
            mResumePosition = savedInstanceState.getLong(STATE_RESUME_POSITION);
            mExoPlayerFullscreen = savedInstanceState.getBoolean(STATE_PLAYER_FULLSCREEN);
            contextType = savedInstanceState.getString(STATE_CONTEXT_TYPE);
        }

        lifecycleOwner = this;
        listRoomDatabase = ListRoomDatabase.getDatabase(this);
        /*playlistRoomDatabase.mediaDAO().getAllWords().observe(lifecycleOwner, feedContentData -> {
            if (feedContentData != null) {
                Log.d("ItemData:", feedContentData.get(0).id + "");
            }
        });*/

        videosViewPager = findViewById(R.id.viewPagerVideos);

        init();
        //setDrawer();
        showToolbarTitle(false);
        setTheme(R.style.Theme_Leanback);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setTvNavigationView();
        bindService();


        callAddActivityAPI("", CONTEXT_APP_OPEN, "", "");
        // Restore last playlist
        //restorePlaylist();
        isPausedPlayer = false;

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(VOUCHER_BROADCAST_KEY);
        this.registerReceiver(voucherBroadCast, intentFilter);

        IntentFilter intentFilter1 = new IntentFilter();
        intentFilter1.addCategory(Intent.CATEGORY_DEFAULT);
        intentFilter1.addAction(DOWNLOADS_BROADCAST_KEY);
        registerReceiver(downloadsBroadcast, intentFilter);

        /*IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addCategory(Intent.CATEGORY_DEFAULT);
        intentFilter2.addAction(AD_NOTIFICATION_BROADCAST_KEY);
        registerReceiver(adsNotificationBroadCast, intentFilter2);*/

        IntentFilter intentFilter3 = new IntentFilter();
        intentFilter3.addCategory(Intent.CATEGORY_DEFAULT);
        intentFilter3.addAction(EARN_COINS_KEY);
        registerReceiver(earnCoinsBroadCast, intentFilter3);

        /*IntentFilter intentFilter4 = new IntentFilter();
        intentFilter4.addCategory(Intent.CATEGORY_DEFAULT);
        intentFilter4.addAction(CHECK_INTERNET_CONNECTION);
        registerReceiver(checkInternetConnectionBroadCast, intentFilter4);*/

        LocalBroadcastManager.getInstance(this).registerReceiver(downloadingBroadcast,
                new IntentFilter(DOWNLOADING_BROADCAST_KEY));

        IntentFilter intentFilterStore = new IntentFilter();
        intentFilterStore.addAction(STORE_BROADCAST_KEY);
        this.registerReceiver(storeBroadCast, intentFilterStore);

        if (LocalStorage.getSurveyHash() != null) {
            if (!LocalStorage.getSurveyHash().isEmpty()) {
                s.onStart(this, SAMPLE_APP, SM_REQUEST_CODE, LocalStorage.getSurveyHash());
            }
        }
        mainMediaFrame = findViewById(R.id.main_media_frame);

        MyApp application = (MyApp) getApplication();
        if (application.isShowNotification) {
            Log.i(TAG, "isShowing");
            application.isShowNotification = false;
            application.getNotification();
        }

        AnalyticsTracker.context = this;

        if (getIntent() != null) {
            String type = getIntent().getStringExtra(Constant.TYPE);
            String postID = getIntent().getStringExtra(Constant.POSTID);
            if (type != null && !type.isEmpty()) {
                handleData(type, postID);
            }
        }
    }

    public void callFirebaseInstanceId() {
        if (Utility.isConnectingToInternet(HomeActivity.this)) {
            try {
                FirebaseMessaging.getInstance().getToken().addOnCompleteListener(HomeActivity.this, instanceIdResult -> {
                    String newToken = instanceIdResult.getResult();
                    LocalStorage.putValue(KEY_FCM_TOKEN, newToken);
                    Log.e("newToken", newToken);
                    sendRegId(LocalStorage.getUserId(), newToken);
                });
            } catch (Exception e) {

            }
        }
    }

    public void callFirebaseDeepLink() {
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                        }

                        if (deepLink != null) {
                            String contentType = deepLink.getQueryParameter("contentType");
                            String contentId = deepLink.getQueryParameter("contentId");
                            switch (contentType) {
                                // logic for free item
                                case FeedContentData.CONTENT_TYPE_URL:
                                    String url = contentId;
                                    if (!url.startsWith("http://") && !url.startsWith("https://"))
                                        url = "https://" + url;
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                    startActivity(browserIntent);
                                    break;
                                case FeedContentData.CONTENT_TYPE_ESPORTS:
                                    getUserRegisteredTournamentInfo(contentId);
                                    break;
                                default:
                                    getMediaData(contentId, contentType);
                                    break;
                            }
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    private void sendRegId(String userId, String regId) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("user_id", userId);
            params.put("registration_id", regId);
            APIRequest apiRequest = new APIRequest(Url.UPDATE_REG_ID, Request.Method.POST, params, null, this);
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, (response, error, headers, statusCode) -> {
                try {
                    if (error != null) {
                        //AlertUtils.showToast(error.getMessage(), 1, this);
                    } else {
                        if (response != null) {
                            //Log.e("response", response);
                            JSONObject jsonObjectResponse = new JSONObject(response);
                            String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                            if (type.equalsIgnoreCase("error")) {
                                String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                                //AlertUtils.showToast(message, 1, this);
                            } else if (type.equalsIgnoreCase("ok")) {
                                // Registration id Was updated successfully
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public AudioPlayerService getServiceInstance() {
        return service;
    }

    // Restore last playlist Data
    private void restorePlaylist() {
        new Thread(() -> {
            Playlist playlist = listRoomDatabase.mediaDAO().getPlaylistlll();
            if (playlist != null) {
                minimisePlayer = true;
                feedId = playlist.feed_id;
                arrayListPlaylist.addAll(playlist.arrayListPlaylistData);
                arrayListEpisodeData.addAll(playlist.arrayListEpisodeData);
                arrayListContentData.addAll(playlist.arrayListContentData);
                feedContentDataSeries = playlist.getFeedContentDataSeries();
                if (playlist.getPage_type().equalsIgnoreCase(Analyticals.CONTEXT_PLAYLIST)) {
                    tvSeason.setVisibility(View.GONE);
                    btViewMore.setVisibility(playlist.getTotalNumberOfRecords() <= buttonViewMoreThreshold ? View.GONE : View.VISIBLE);
                    btViewMore.setTag(playlist.feed_id);
                    btViewMore.setId(booleanToInt(playlist.iswatchlisted));
                } else if (playlist.getPage_type().equalsIgnoreCase(Analyticals.CONTEXT_SERIES)) {
                    tvSeason.setVisibility(View.VISIBLE);
                    tvSeason.setText(feedContentDataSeries.seasonData.name);
                    btViewMore.setVisibility(totalNumberOfRecords <= buttonViewMoreThreshold ? View.GONE : View.VISIBLE);
                    btViewMore.setTag(feedContentDataSeries.postId);
                    currentSeasonId = playlist.currentSeasonId;
                }
                playMediaItems(new ArrayList<>(playlist.getPlayListItems()), playlist.getPosition(), playlist.getFeed_id(), playlist.getPage_type(), String.valueOf(playlist.iswatchlisted), false, true);
            }
        }).start();
    }

    private void bindService() {
        Log.i("Kalpesh", "bindService");
        AsyncTask.execute(() -> {
            if (!serviceBound) {
                Log.i("Kalpesh", "service not bound. binding...");
                Intent i = new Intent(HomeActivity.this, AudioPlayerService.class);
                //Util.startForegroundService(HomeActivity.this, i);
                startService(i);
                bindService(i, serviceConnection, Context.BIND_AUTO_CREATE);
                Log.i("Kalpesh", "service started");
            }
        });
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private void initFullscreenDialog() {

        mFullScreenDialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
            public void onBackPressed() {
                if (Utility.isTelevision()) {
                    closePlayer(null);
                    Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
                    if (fragment instanceof MoviesFragment) {
                        moviesFragment.restoreSelection();
                    } else if (fragment instanceof PremiumTvFragment) {
                        premiumTvFragment.restoreSelection();
                    } else if (fragment instanceof ViewAllTvFragment) {
                        ((ViewAllTvFragment) fragment).restorePosition();
                    } else if (fragment instanceof SearchTvFragment) {
                        ((SearchTvFragment) fragment).restorePosition();
                    } else if (fragment instanceof MoreFragment) {
                        ((MoreFragment) fragment).restorePosition();
                    } else if (fragment instanceof SeasonsTvFragment) {
                        ((SeasonsTvFragment) fragment).restoreFocus();
                    }
                } else {
                    if (contentOrientation.equalsIgnoreCase("vertical")) {
                        closeFullscreenDialog();
                    } else {
                        if (mExoPlayerFullscreen) {
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        }
                    }
                }
                super.onBackPressed();
            }
        };

        mFullScreenQuestionDialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
            public void onBackPressed() {
                if (mExoPlayerFullscreen) {
                    if (mQuestionFullscreen) {
                        closeFullscreenQuestionDialog(null);
                        return;
                    }
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
                super.onBackPressed();
            }
        };
        int offset = getResources().getDimensionPixelSize(R.dimen.dp_10) * 2;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels - offset;
        int width = displayMetrics.widthPixels;
        mFullScreenQuestionDialog.getWindow().setLayout((int) (height * 0.9), WindowManager.LayoutParams.WRAP_CONTENT);
        //mFullScreenQuestionDialog.getWindow().setDimAmount(20);
        mFullScreenQuestionDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        //mFullScreenDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, width);
        //mFullScreenDialog.getWindow().setLayout(width/2, height/2);
        //mFullScreenDialog.getWindow().setLayout(height, width);
        /*mFullScreenDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mFullScreenDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
    }

    /**
     * Api calls to get global settings to show and hide presenterLogin
     **/
    public void globalSettingApiCall() {
        APIMethods.getGlobalSetting(mContext, (msg) -> {
            Log.e("Result", msg.toString());
            //toolbarlive_button.setVisibility(LocalStorage.getLiveModuleStatus() ? View.VISIBLE : View.INVISIBLE);
            View notificationCountView = findViewById(R.id.view_notification_red_circle);
            if (LocalStorage.getNotificationCount() == 0) {
                if (notificationCountView != null) {
                    notificationCountView.setVisibility(View.GONE);
                }
            } else {
                if (notificationCountView != null) {
                    notificationCountView.setVisibility(View.VISIBLE);
                }
            }
            try {
                String title = msg.has("title") ? msg.getString("title") : null;
                String desc = msg.has("description") ? msg.getString("description") : null;
                String image = msg.has("image") ? msg.getString("image") : null;
                String type_of_maintenance = msg.has("type_of_maintenance") ? msg.getString("type_of_maintenance") : null;
                String url = msg.has("url") ? msg.getString("url") : null;
                String isMaintenance = msg.has("is_maintenance") ? msg.getString("is_maintenance") : null;
                if (msg.has("version")) {
                    int version = msg.getInt("version");
                    Intent intent = new Intent(this, AppInfoActivity.class);
                    intent.putExtra(KEY_TITLE, title);
                    intent.putExtra(KEY_DESCRIPTION, desc);
                    intent.putExtra(KEY_IMAGE, image);
                    intent.putExtra(KEY_INFO_TYPE, type_of_maintenance);
                    intent.putExtra(KEY_APP_URL, url);
                    if (version > BuildConfig.VERSION_CODE || type_of_maintenance.equals(MAINTENANCE_SYSTEM_DOWN)) {
                        startActivity(intent);
                        callSettings = true;
                    }
                    if (type_of_maintenance.equals(MAINTENANCE_GENERAL)) {
                        startActivity(intent);
                        callSettings = false;
                    }

                    if (type_of_maintenance.equalsIgnoreCase(APP_MAINTENANCE)) {
                        if (isMaintenance != null) {
                            if (isMaintenance.equalsIgnoreCase("yes")) {
                                startActivity(new Intent(this, MaintenanceActivity.class).putExtra("Message", desc));
                                finish();
                                callSettings = false;
                            }
                        }
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void getAdMobAPiCall() {
        APIMethods.getAdMob(mContext);
    }

    private void openFullscreenDialog() {
        if (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {

            // Player frame and holder
            FrameLayout frame = findViewById(R.id.main_media_frame);

            ViewGroup parent = (ViewGroup) playerHolder.getParent();

            // Remove player holder from parent
            parent.removeView(playerHolder);

            // Remove player holder from frame
            frame.removeView(playerHolder);

            // Add player holder to full screen dialog
            mFullScreenDialog.addContentView(playerHolder, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            /*((ViewGroup) playerView.getParent()).removeView(playerView);
            mFullScreenDialog.addContentView(playerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));*/
            //rlMediaControllerLayout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            ibclose.setVisibility(View.GONE);
            //exo_fullscreen_icon.setImageDrawable(ContextCompat.getDrawable(HomeActivity.this, R.drawable.ic_fullscreen_skrink));
            mExoPlayerFullscreen = true;
            mFullScreenDialog.show();
            //mFullScreenDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            //mFullScreenDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
            if (playerPagerAdapter != null) {
                playerPagerAdapter.setFullScreen();
            }
            if (service != null) {
                if (service.getCurrentItem() != null) {
                    updateAdBreakPoints(service.getCurrentItem().adsArray);
                }
            }
        }
    }

    private void closeFullscreenDialog() {

        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        // Player frame, holder and parent
        FrameLayout frame = findViewById(R.id.main_media_frame);
        ViewGroup parent = (ViewGroup) playerHolder.getParent();

        // Remove player holder from parent
        parent.removeView(playerHolder);

        // Add player holder to frame
        frame.addView(playerHolder);

        /*((ViewGroup) playerView.getParent()).removeView(playerView);
        ((FrameLayout) findViewById(R.id.main_media_frame)).addView(playerView);*/
        //rlMediaControllerLayout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ibclose.setVisibility(View.VISIBLE);

        mExoPlayerFullscreen = false;
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

        mFullScreenDialog.dismiss();
        //exo_fullscreen_icon.setImageDrawable(ContextCompat.getDrawable(HomeActivity.this, R.drawable.ic_fullscreen_expand));
        if (playerPagerAdapter != null) {
            playerPagerAdapter.removeFullScreen();
        }
    }

    public void openFullscreenQuestionDialog(View view) {
        if (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {

            // Player frame and holder
            FrameLayout flQuestionFrame = findViewById(R.id.flQuestionFrame);

            // Remove question holder from frame
            flQuestionFrame.removeView(questionHolder);

            // Add question holder to full screen dialog
            mFullScreenQuestionDialog.addContentView(questionHolder, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

           /* if(mQuestionFullscreen) {
                mQuestionFullscreen = false;
                ibQuestionClose.setVisibility(View.GONE);
                mFullScreenQuestionDialog.dismiss();
                ((Button) view).setText("Play Game");
            }else {*/
            mQuestionFullscreen = true;
            mFullScreenQuestionDialog.show();
            ibQuestionClose.setVisibility(View.VISIBLE);
            ibShopClose.setVisibility(View.VISIBLE);
               /* ((Button) view).setText("Hide Game");
            }*/
        }
    }

    public void closeFullscreenQuestionDialog(View view) {
        // Player frame, holder and parent
        FrameLayout flQuestionFrame = findViewById(R.id.flQuestionFrame);
        ViewGroup parent = (ViewGroup) questionHolder.getParent();

        // Remove question holder from parent
        parent.removeView(questionHolder);

        // Add question holder to frame
        flQuestionFrame.addView(questionHolder);
        mQuestionFullscreen = false;
        ibQuestionClose.setVisibility(View.GONE);
        ibShopClose.setVisibility(View.GONE);
        mFullScreenQuestionDialog.dismiss();
    }

    @SuppressLint("SourceLockedOrientationActivity")
    public void changeScreen(View view) {
        if (contentOrientation.equalsIgnoreCase("vertical") && !Utility.isTelevision()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            if (!mExoPlayerFullscreen) {
                openFullscreenDialog();
            } else {
                closeFullscreenDialog();
            }
        } else {
            if (mExoPlayerFullscreen) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }
    }

    @Override
    public void onDownloadsChanged() {
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }

    @Override
    public void sheetCallback(@org.jetbrains.annotations.Nullable String item) {

    }

    @Override
    public void getCommentCount(String commentCount, String songId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvCommentMusic.setText(String.format(getResources().getString(R.string.label_button_comment_no), commentCount));
                tvComment.setText(commentCount);
                LocalStorage.setCommented(Integer.parseInt(songId), commentCount, true);

                if (videosViewPager != null) {
                    textViewArrayList.get(videosViewPager.getCurrentItem()).setText(commentCount);
                }
            }
        });
    }

    private void configurePlayers() {
        playerPagerAdapter = new PlayerPagerAdapter(this);
        playerView.setAdapter(playerPagerAdapter);
    }

    private void restartTimer() {
        if (serviceBound && service != null) {
            if (timer != null) {
                timer.cancel();
                timer.purge();
                timer = null;
            }
            if (timerTask != null) {
                timerTask.cancel();
                timerTask = null;
            }

            timerTask = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(() -> updateSeekBar());
                }
            };
            timer = new Timer();
            timer.scheduleAtFixedRate(timerTask, new Date(), 1000);
        }
    }

    private void configureSeekBar() {
        /*if (playerPagerAdapter != null) {
            playerPagerAdapter.configureSeekBar();
        }*/
        /*if (adProgressbar == null) {
            adProgressbar = findViewById(R.id.adPlayerProgressBar);
            *//*if (adProgressbar == null) {
                adProgressbar = llMainBottomPlayerView.findViewById(R.id.adPlayerProgressBar);
            }*//*
        }
        if (mediaPlayerTimeBar == null) {
            mediaPlayerTimeBar = findViewById(R.id.mediaPlayerTimeBar);
            mediaPlayerTimeBar.addListener(new TimeBar.OnScrubListener() {
                @Override
                public void onScrubStart(TimeBar timeBar, long position) {
                }

                @Override
                public void onScrubMove(TimeBar timeBar, long position) {
                }

                @Override
                public void onScrubStop(TimeBar timeBar, long position, boolean canceled) {
                    if (!canceled && serviceBound && service != null && service.getPlayer() != null) {
                        service.getPlayer().seekTo(position);
                    }
                }
            });
        }*/
    }

    private void updateSeekBar() {
        configureSeekBar();
        if (playerPagerAdapter != null) {
            playerPagerAdapter.updateSeekBar();
        }
        /*if (serviceBound && service != null) {
            if (service.getAdPlayer() != null && (service.isPlayingAd() || isPlayingAd)) {
                int total = (int) service.getAdPlayer().getPlayedDuration();
                int buffered = (int) service.getAdPlayer().getBufferedPosition();
                int played = (int) service.getAdPlayer().getCurrentPosition();
                adProgressbar.setMax(total);
                adProgressbar.setSecondaryProgress(buffered);
                adProgressbar.setProgress(played);

            } else {
                if (service.getPlayer() != null) {
                    mediaPlayerTimeBar.setPlayedDuration(service.getPlayer().getPlayedDuration());
                    mediaPlayerTimeBar.setBufferedPosition(service.getPlayer().getBufferedPosition());
                    mediaPlayerTimeBar.setPosition(service.getPlayer().getCurrentPosition());
                    if (service.getCurrentItem() != null) {
                        updateAdBreakPoints(service.getCurrentItem().adsArray);
                    }
                }
            }
        }*/
    }

    private void updateAdBreakPoints(@NonNull ArrayList<FeedContentData> ads) {
        if (playerPagerAdapter != null) {
            playerPagerAdapter.updateAdsBreakPoints(ads);
        }
        /*if (mediaPlayerTimeBar == null) {
            configureSeekBar();
        }
        if (ads.size() > 0) {
            long[] adGroupTimesMs = new long[ads.size()];
            boolean[] playedAdGroups = new boolean[ads.size()];
            for (int i = 0; i < ads.size(); i++) {
                AttachmentData ad = ads.get(i);
                adGroupTimesMs[i] = ad.play_at * 1000L;
                playedAdGroups[i] = false;
                Log.i("Ads", "Ad time: " + adGroupTimesMs[i]);
            }
            mediaPlayerTimeBar.setAdGroupTimesMs(adGroupTimesMs, playedAdGroups, ads.size());
        }*/
    }

    private void checkPlayerVisibility() {
        runOnUiThread(() -> {
            if (playerView == null) {
                return;
            }
            if (serviceBound && service != null) {
                playerView.showMediaPlayer(false);
                playerPagerAdapter.updatePlayers(false);
                playerPagerAdapter.updatePlayerTitle();
                /*if (service.isPlayingAd()) {
                    playerView.showAdPlayer(false);
                } else {
                    playerView.showMediaPlayer(false);
                }
                if (playerPagerAdapter != null) {
                    playerPagerAdapter.updatePlayers();
                    playerPagerAdapter.updatePlayerTitle();
                }*/
            }
        });
    }

    private void preventScreenOff() {
        boolean preventScreenOff = false;
        // Check if service is bound.
        if (serviceBound && service != null) {
            // Prevent screen from dimming or sleeping when media is being played.
            preventScreenOff = (service.isPlaying() || service.isPlayingAd());
        }
        // Check player state.
        if (slidingUpPanelLayout.getPanelState() != SlidingUpPanelLayout.PanelState.EXPANDED) {
            // If player is not expanded, we do not want to prevent scree from dimming or sleeping.
            preventScreenOff = false;
        }
        // Apply window flags.
        if (preventScreenOff) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    private void prepareFragments() {
        homeFragment = MainHomeFragment.newInstance();
        moviesFragment = MoviesFragment.getInstance("listen");
        premiumTvFragment = PremiumTvFragment.getInstance("buy");
        channelFragment = ChannelCategoryFragment.newInstance("channel", "channel", 0, "");
        avatarFragment = AvatarFragment.newInstance();
        createFragment = CreateFragment.Companion.newInstance();
        //downloadsFragment = DownloadsFrag.newInstance();
        premiumFragment = HomeBuyTabFragment.newInstance();
        couponFragment = CouponFragment.newInstance();
        shopFrag = ShopFrag.newInstance();
        moreFragment = MoreFragment.newInstance();
        searchFragment = SearchFragment.newInstance();
        searchTvFragment = SearchTvFragment.newInstance();
        notificationFragment = NotificationFragment.newInstance();
        liveJavaFrag = LiveJavaFrag.newInstance();
        profileFragment = ProfileFragment.newInstance();
    }

    /**
     * Initialize data
     */
    private void init() {
        //getUserDetails();
        bottomNavigationMenuList.clear();

        playerHeight = getResources().getDimensionPixelSize(R.dimen.player_size);
        playerHeight += getResources().getDimensionPixelSize(R.dimen.dp_35);

        //drawerLayout = findViewById(R.id.drawerLayout);
        //drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED); // Hide the drawer
        if (getIntent() != null) {
            Intent intent = getIntent();
            Constant.ACTION = intent.getAction();
        }

        nav_fragment = findViewById(R.id.nav_fragment);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        llBottomPlayerView = findViewById(R.id.llBottomPlayerView);
        rlMediaControllerLayout = findViewById(R.id.rlMediaControllerLayout);
        playerHolder = findViewById(R.id.playerHolder);
        llQuestion = findViewById(R.id.llQuestion);
        llProgressbar = findViewById(R.id.llProgressbar);
        llEpisodeData = findViewById(R.id.llEpisodeData);
        circleProgressBar = findViewById(R.id.circle_progress_bar);
        tvProgress = findViewById(R.id.progress_text);
        llLoader = findViewById(R.id.llLoader);
        llExpandableViewLoader = findViewById(R.id.llExpandableViewLoader);
        nsvNestedScrollView = findViewById(R.id.nsvNestedScrollView);
        rlRightAnswer = findViewById(R.id.rlRightAnswer);
        rlWrongAnswer = findViewById(R.id.rlWrongAnswer);
        rlShopNow = findViewById(R.id.rlShopnow);
        ivNextQuestionW = findViewById(R.id.ivNextQuestionW);
        cvMissedQuestions = findViewById(R.id.cvMissedQuestions);
        cvPlayer = findViewById(R.id.cvPlayer);
        progressBar = findViewById(R.id.progressBar);
        downloadProgressBar = findViewById(R.id.downloadProgressBar);
        ibPlay = findViewById(R.id.ibPlay);
        ibRewind = findViewById(R.id.ibRewind);
        ibForward = findViewById(R.id.ibForward);
        ibQuestionClose = findViewById(R.id.ibQuestionClose);
        ibShopClose = findViewById(R.id.ibShopClose);
        slidingUpPanelLayout = findViewById(R.id.sliding_layout);
        llMainBottomPlayerView = findViewById(R.id.llMainBottomPlayerView);
        container = findViewById(R.id.container);
        exo_progress = findViewById(R.id.exo_progress);
        /*tabSeries = findViewById(R.id.tabseries);
        vpSeries = findViewById(R.id.vpseries);*/

        llMainBottomPlayerView.setOnClickListener(this);
        slidingUpPanelLayout.setScrollableViewHelper(new NestedScrollableViewHelper());
        ivAudioImage = findViewById(R.id.ivAudioImage);
        //exo_fullscreen_icon = findViewById(R.id.exo_fullscreen_icon);
        tvAudioTitle = findViewById(R.id.tvAudioTitle);
        tvAudioTitlesiler = findViewById(R.id.tvAudioTitleslider);
        tvLikeMusic = findViewById(R.id.tvLikeMusic);
        tvCommentMusic = findViewById(R.id.tvCommentMusic);
        tvDownload = findViewById(R.id.tvDownload);
        tvVisitUrl = findViewById(R.id.tvVisitUrl);
        webView = findViewById(R.id.webView);
        btLike = findViewById(R.id.btLike);
        tvAudioSubtitle = findViewById(R.id.tvAudioSubtitle);
        tvAudioSubtitleSlider = findViewById(R.id.tvAudioSubtitleSlider);
        tvWonCoins = findViewById(R.id.tvWonCoins);
        tvWonCoin = findViewById(R.id.tvWonCoin);
        tvSeason = findViewById(R.id.tvSeason);

        playerView = findViewById(R.id.playerViewPager);
        //playerView = findViewById(R.id.mediaPlayerView);

        //youTubePlayerView = findViewById(R.id.youtube_player_view);
        //getLifecycle().addObserver(youTubePlayerView);

        configurePlayers();
        //configureAdPlayer();
        questionHolder = findViewById(R.id.rlQuestions);
        btOption1 = findViewById(R.id.btOption1);
        btOption2 = findViewById(R.id.btOption2);
        btOption3 = findViewById(R.id.btOption3);
        btOption4 = findViewById(R.id.btOption4);
        btViewMore = findViewById(R.id.btViewMore);
        ivNextQuestion = findViewById(R.id.ivNextQuestion);
        ivshopnow = findViewById(R.id.ivshopnow);
        toolbarWalletBalance = findViewById(R.id.coins_text);
        toolbarDiamonds = findViewById(R.id.diamonds_text);
        toolbarNotificationButton = findViewById(R.id.notification_button);
        toolbarsearch_button = findViewById(R.id.search_button);
        toolbarlive_button = findViewById(R.id.live_button);
        toolbarearncoins_button = findViewById(R.id.earn_text);

        llExpandableViewLoader.setVisibility(View.VISIBLE);
        nsvNestedScrollView.setVisibility(View.GONE);

        toolbarlive_button.setVisibility(LocalStorage.getLiveListingModuleStatus() ? View.VISIBLE : View.INVISIBLE);

        //toolbarWalletBalance.setOnClickListener(view -> startActivityForResult(new Intent(HomeActivity.this, WalletActivity.class), WalletActivity.REQUEST_CODE1));

        toolbarWalletBalance.setOnClickListener(view -> {
                    String id = getResources().getResourceEntryName(toolbarWalletBalance.getId());
                    Log.i(TAG, String.valueOf(id));
                    showAd(String.valueOf(id));
                    // Transitioning to Wallet screen in Rewards page
                    MenuItem item = bottomNavigationView.getMenu().getItem(1);
                    bottomNavigationView.setSelectedItemId(item.getItemId());
                    if (ShopFrag.tabLayoutRewards != null) {
                        new Handler().postDelayed(() -> ShopFrag.tabLayoutRewards.getTabAt(2).select(), 100);
                    }
                }
        );

        toolbarDiamonds.setOnClickListener(view -> {
                    openFragment(DiamondsFragment.Companion.newInstance());
                }
        );

        toolbarNotificationButton.setOnClickListener(view -> {
            String id = getResources().getResourceEntryName(toolbarNotificationButton.getId());
            Log.i(TAG, String.valueOf(id));
            showAd(String.valueOf(id));
            openFragment(NotificationFragment.newInstance());
        });

        toolbarlive_button.setOnClickListener(v -> {
            /*Intent intent = new Intent(HomeActivity.this, LiveActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(new Intent(intent));*/
            //Toast.makeText(this, "Feature Disabled", Toast.LENGTH_SHORT).show();
            String id = getResources().getResourceEntryName(toolbarlive_button.getId());
            Log.i(TAG, String.valueOf(id));
            showAd(String.valueOf(id));
            openFragment(LiveJavaFrag.newInstance());
        });

        toolbarsearch_button.setOnClickListener(view -> {
            String id = getResources().getResourceEntryName(toolbarsearch_button.getId());
            Log.i(TAG, String.valueOf(id));
            showAd(String.valueOf(id));
            openFragment(searchFragment);
        });

        toolbarearncoins_button.setOnClickListener(view -> {
            int id = toolbarearncoins_button.getId();
            Log.i(TAG, String.valueOf(id));
            showAd(String.valueOf(id));
            checkUserValidity();
        });

        orientationEventListener = new OrientationEventListener(this) {
            @Override
            public void onOrientationChanged(int orientation) {
                int epsilon = 10;
                int leftLandscape = 90;
                int rightLandscape = 270;
                if (epsilonCheck(orientation, leftLandscape, epsilon) ||
                        epsilonCheck(orientation, rightLandscape, epsilon)) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                }
            }

            private boolean epsilonCheck(int a, int b, int epsilon) {
                return a > b - epsilon && a < b + epsilon;
            }
        };
        orientationEventListener.disable();

        ArrayOfButton = new ArrayList<>();
        ArrayOfButton.add(btOption1);
        ArrayOfButton.add(btOption2);
        ArrayOfButton.add(btOption3);
        ArrayOfButton.add(btOption4);
        //llBottomPlayerView.setVisibility(View.GONE);

        // Show on-boarding coins earned screen
        if (LocalStorage.getValue(LocalStorage.KEY_LANG_PREFER, 0, Integer.class) == 0) {
            //showCoinsActivity();
            LocalStorage.putValue(1, LocalStorage.KEY_LANG_PREFER);
        }

        initPlayerPannel();

        getWalletBalance();
        slidingUpPanelLayout.setTouchEnabled(false);

        //----------------- EPISODE ------------------//
        rvEpisode =

                findViewById(R.id.rvEpisode);

        llSimilarItems =

                findViewById(R.id.llSimilarItems);

        tvListTitle =

                findViewById(R.id.tvListTitle);

        // Trillbit BroadcastReceiver
        /*trillBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("avinash", "BroadcastReceiver onReceive");
                startTrillService();
            }
        };*/
        rvEpisode.setLayoutManager(new

                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvEpisode.setNestedScrollingEnabled(false);
        rvEpisode.addItemDecoration(new

                SpacingItemDecoration(Constant.RV_HV_SPACING));

        mAdView =

                findViewById(R.id.adViewPlayer);

        clAdHolder =

                findViewById(R.id.cl_ad_holder);

        ivCustomAd =

                findViewById(R.id.ivCustomAd);

        ivClose =

                findViewById(R.id.ivClose);

        pvCustomAd =

                findViewById(R.id.pvCustomAd);

        ivVolumeUp =

                findViewById(R.id.ivVolumeUp);

        ivVolumeOff =

                findViewById(R.id.ivVolumeOff);

        adRequest = new AdRequest.Builder().

                build();

        String id = getResources().getResourceEntryName(findViewById(R.id.adViewPlayer).getId());
        //showBannerAd(id);

        tvAddtoWatchlist =

                findViewById(R.id.tvAddtoWatchlist);
       /* tvAddtoWatchlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToWatchList();
            }
        });*/

        tvCommentMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("avinash", "CommentListActivity ");
                Intent intent = new Intent(HomeActivity.this, CommentListActivity.class);
                intent.putExtra("postId", feedContentPlayerData.postId);
                intent.putExtra("postTitle", feedContentPlayerData.postTitle);
                startActivityForResult(intent, CommentListActivity.REQUEST_CODE);
            }
        });

        Constant.ACTION =

                getIntent().

                        getAction();

        Bundle bundle = new Bundle();
        if (!action.equalsIgnoreCase(

                getString(R.string.no_action))) {
            if (getIntent().getExtras() != null) {
                n_json_object = getIntent().getExtras().getString(Constant.NOTIFICATION_DATA);
                bundle.putString(Constant.NOTIFICATION_DATA, n_json_object);
            }
        }

        // Prepare tab fragments
        prepareFragments();
        homeFragment.setArguments(bundle);

        if (Utility.isTelevision()) {
            openFragment(moviesFragment);
        } else {
            openFragment(homeFragment);
        }

        btNextSeries =

                findViewById(R.id.btNextSeries);

        updateCheck();

        getPurchaseHistory();

        if (!Utility.isTelevision()) {
            checkForPermission();
        }

        MyApp application = (MyApp) getApplication();
    }

    private void checkForPermission() {
        if (!Utility.checkPermission(HomeActivity.this, arrPermissions)) {
            Utility.requestPermission(HomeActivity.this, 100, arrPermissions);
        }
    }

    public void getUserDetails() {
        String userId = LocalStorage.getUserId();
        try {
            Map<String, String> params = new HashMap<>();
            params.put("user_id", userId);
            APIRequest apiRequest = new APIRequest(Url.GET_USER_DETAILS, Request.Method.POST, params, null, this);
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    try {
                        if (error != null) {
                            //AlertUtils.showToast(error.getMessage(), 1, context);
                        } else {
                            if (response != null) {
                                JSONObject jsonObjectResponse = new JSONObject(response);
                                String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                                if (type.equalsIgnoreCase("error")) {
                                    String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                                    //showError(message);
                                } else if (type.equalsIgnoreCase("ok")) {
                                    JSONObject jsonObject = jsonObjectResponse.has("msg") ? jsonObjectResponse.getJSONObject("msg") : new JSONObject();
                                    //User user = new User(jsonObject.has("user_details") ? jsonObject.getJSONObject("user_details") : new JSONObject());
                                    User user = new User(jsonObject.has("user_details") ? jsonObject.getJSONObject("user_details") : new JSONObject());
                                    LocalStorage.setUserData(user);
                                    SubscriptionDetails subscription = new SubscriptionDetails(jsonObject.has("subscription_details") ? jsonObject.getJSONObject("subscription_details") : new JSONObject());
                                    LocalStorage.setUserSubscriptionDetails(subscription);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateCheck() {
        appUpdateManager = AppUpdateManagerFactory.create(this);
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                try {
                    appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, this, REQUEST_CODE_UPDATE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void callAddActivityAPI(String entityId, String context, String contextId, String title) {
        String activityType = "";

        switch (context) {
            case Analyticals.CONTEXT_FEED:
                activityType = ACTIVITY_TYPE_PLAY_AUDIO;
                Utility.customEventsTracking(Constant.VideoViews, title);
                break;
            case Analyticals.CONTEXT_EPISODE:
                activityType = Analyticals.ACTIVITY_TYPE_PLAY_EPISODE;
                Utility.customEventsTracking(Constant.VideoViews, title);
                break;
            case Analyticals.CONTEXT_AUDIO_SONG:
                activityType = Analyticals.ACTIVITY_TYPE_VIEW_AD;
                Utility.customEventsTracking(Constant.VideoViews, title);
                break;
            case Analyticals.CONTEXT_PLAYLIST:
                activityType = Analyticals.ACTIVITY_TYPE_PLAY_PLAYLIST;
                Utility.customEventsTracking(Constant.VideoViews, title);
                break;
            case Analyticals.CONTEXT_APP_OPEN:
                activityType = Analyticals.ACTIVITY_TYPE_APP_OPEN;
                break;
        }
        Analyticals.CallPlayedActivity(activityType, entityId, contextId, context, this, "", new Analyticals.AnalyticsResult() {
            @Override
            public void onResult(boolean success, String acitivity_id, @Nullable String error) {

            }
        });
    }

    public void addToWatchList(View view) {
        String ID = view.getTag().toString();

        if (!feedContentPlayerData.postId.isEmpty()) {
            try {
                Map<String, String> params = new HashMap<>();
                Log.e("avinash", params.toString());
                APIRequest apiRequest = new APIRequest(Url.ADD_REMOVE_WATCHLIST + ID, Request.Method.GET, params, null, this);
                apiRequest.showLoader = false;
                APIManager.request(apiRequest, new APIResponse() {
                    @Override
                    public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                        if (response == null || response.isEmpty()) {
                            return;
                        }
                        JSONObject jsonObjectResponse = null;
                        try {
                            jsonObjectResponse = new JSONObject(response);
                            Log.e("avinash", String.valueOf(jsonObjectResponse));

                            String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                            if (type.equalsIgnoreCase("error")) {
                                String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                                Toast.makeText(HomeActivity.this, message, Toast.LENGTH_LONG).show();
                            } else if (type.equalsIgnoreCase("ok")) {
                                JSONObject msg = jsonObjectResponse.getJSONObject("msg");
                                setWishImage(msg.getBoolean("is_added"));
                                if (msg.getBoolean("is_added")) {
                                    Toast.makeText(HomeActivity.this, getString(R.string.added_to_watchlist), Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(HomeActivity.this, getString(R.string.removed_to_watchlist), Toast.LENGTH_LONG).show();
                                }
                                LocalStorage.setWishedItem(Integer.parseInt(ID), msg.getBoolean("is_added"), getApplicationContext());

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                Utility.showError(getString(R.string.failed_to_comment), HomeActivity.this);
            }
        }
    }

    private void setWishImage(boolean isWished) {
        if (isWished) {
            tvAddtoWatchlist.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.blue_tick, 0, 0);
            tvAddtoWatch.setImageResource(R.drawable.blue_tick);
        } else {
            tvAddtoWatchlist.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_add_black, 0, 0);
            tvAddtoWatch.setImageResource(R.drawable.ic_add_white);
        }
    }

    public void setDownloadedImage(String downloadState) {
        switch (downloadState) {
            case IS_DOWNLOADING:
                tvDownload.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_waiting, 0, 0);
                tvDownload.setText(getString(R.string.downloading));
                setTextViewDrawableColor(tvDownload, R.color.download_purple);
                tvDownload.setClickable(false);
                break;
            case IS_DOWNLOADED:
                tvDownload.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.blue_tick, 0, 0);
                tvDownload.setText(getString(R.string.downloaded));
                setTextViewDrawableColor(tvDownload, R.color.download_purple);
                tvDownload.setClickable(false);
                break;
            case IS_DOWNLOAD_FAILED:
            default:
                tvDownload.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_file_download_black, 0, 0);
                tvDownload.setText(getString(R.string.download_offline));
                tvDownload.setTextColor(ContextCompat.getColor(this, R.color.download_purple));
                setTextViewDrawableColor(tvDownload, R.color.download_purple);
                tvDownload.setClickable(true);
        }
    }

    private void setTextViewDrawableColor(TextView textView, int color) {
        for (Drawable drawable : textView.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(textView.getContext(), color), PorterDuff.Mode.SRC_IN));
            }
        }
    }

    //----------------- EPISODE ------------------//
    private void setEpisodeIndex(int prevPosition, int currentPosition) {
        if (prevPosition < currentPosition) {
            if (currentPosition == lastIndex) {
                firstIndex = lastIndex;
                lastIndex = firstIndex + 10;
                setEpisodeList("", 0);
            }
        } else if (prevPosition > currentPosition) {
            if (currentPosition < firstIndex) {
                firstIndex = firstIndex - 10;
                lastIndex = firstIndex + 10;
                setEpisodeList("", 0);
            }
        }
    }

    private void setEpisodeList(String iswatched, int position) {
        arrayListContentData.clear();
        adapterEpisode = null;
        tvListTitle.setText(getString(R.string.label_episode));
        for (int i = 0; i < arrayListEpisodeData.size(); i++) {
            if (position == i) {
                arrayListEpisodeData.get(i).isSelected = true;
            } else {
                arrayListEpisodeData.get(i).isSelected = false;
            }
            arrayListContentData.add(arrayListEpisodeData.get(i));
        }
        //arrayListEpisodeData.addAll(seasonData.arrayListFeedContentData);

        if (adapterEpisode == null) {

            adapterEpisode = new KRecyclerViewAdapter(this, arrayListContentData, new KRecyclerViewHolderCallBack() {
                @Override
                public KRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                    View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_episode, viewGroup, false);
                    return new EpisodeHolder(layoutView, null);
                }
            }, new KRecyclerItemClickListener() {
                @Override
                public void onRecyclerItemClicked(KRecyclerViewHolder kRecyclerViewHolder, Object o, int i) {
                    if (o instanceof FeedContentData) {
                        playEpisode(i, iswatched);
                    }
                }
            });
            rvEpisode.setAdapter(adapterEpisode);
            //isCreateViewCalled = false;
        } else {
            adapterEpisode.notifyDataSetChanged();
        }
        llExpandableViewLoader.setVisibility(View.GONE);
        nsvNestedScrollView.setVisibility(View.GONE);
    }

    private void setPlaylist(String iswatched, String PlayListID, int position) {
        arrayListContentData.clear();
        adapterEpisode = null;
        tvListTitle.setText(getString(R.string.playlist));
        for (int i = 0; i < arrayListPlaylist.size(); i++) {
            if (position == i) {
                arrayListPlaylist.get(i).isSelected = true;
            } else {
                arrayListPlaylist.get(i).isSelected = false;
            }
            arrayListContentData.add(arrayListPlaylist.get(i));
        }

        if (adapterEpisode == null) {
            adapterEpisode = new KRecyclerViewAdapter(this, arrayListContentData, new KRecyclerViewHolderCallBack() {
                @Override
                public KRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                    View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_episode, viewGroup, false);
                    return new EpisodeHolder(layoutView, null);
                }
            }, new KRecyclerItemClickListener() {
                @Override
                public void onRecyclerItemClicked(KRecyclerViewHolder kRecyclerViewHolder, Object o, int i) {
                    if (o instanceof FeedContentData) {
                        playPlaylist(i, iswatched, PlayListID);
                    }
                }
            });
            rvEpisode.setAdapter(adapterEpisode);
        } else {
            adapterEpisode.notifyDataSetChanged();
        }
        llExpandableViewLoader.setVisibility(View.GONE);
        nsvNestedScrollView.setVisibility(View.GONE);
    }

    public int getStartingIndex(String episodeNumber) {
        int startingIndex = 0;
        if (!episodeNumber.equals("")) {
            int episodeNo = Integer.parseInt(episodeNumber);
            episodeNo = (episodeNo / 10) * 10;
            startingIndex = episodeNo - 1 >= 0 ? episodeNo - 1 : 0;
        }
        return startingIndex;
    }

    /**
     * Initialize of SliderUpPannel
     */
    private void initPlayerPannel() {
        expandedPlayer = findViewById(R.id.expandedPlayerViewMain);

        ibclose = findViewById(R.id.ibclose);
        ibclose.setOnClickListener(this);
        slidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i(TAG, "onPanelSlide, offset " + (1 - slideOffset));
                float collapsedAlpha = 1 - slideOffset;
                if (collapsedAlpha < 0) {
                    collapsedAlpha *= -1;
                }
                if (collapsedAlpha > 1) {
                    collapsedAlpha = 1;
                }
                Log.i("SlideOffset", String.valueOf(slideOffset));
                if (slideOffset > 0) {
                    homeFragment.panelListener(true);
                } else {
                    homeFragment.panelListener(false);
                }
                if (llMainBottomPlayerView.getVisibility() == View.INVISIBLE || llMainBottomPlayerView.getVisibility() == View.GONE) {
                    llMainBottomPlayerView.setVisibility(View.VISIBLE);
                }
                if (bottomNavigationView.getVisibility() == View.INVISIBLE || bottomNavigationView.getVisibility() == View.GONE) {
                }
                if (expandedPlayer.getVisibility() == View.INVISIBLE || expandedPlayer.getVisibility() == View.GONE) {
                    expandedPlayer.setVisibility(View.VISIBLE);
                }
                llMainBottomPlayerView.setAlpha(collapsedAlpha);
                //bottomNavigationView.setAlpha(collapsedAlpha);
                //expandedPlayer.setAlpha(slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    llMainBottomPlayerView.setVisibility(View.INVISIBLE);
                    expandedPlayer.setVisibility(View.VISIBLE);
                    isPlayerClosed = false;
                    if (Utility.isTelevision()) {
                        orientationEventListener.disable();
                        openFullscreenDialog();
                    } else {
                        orientationEventListener.enable();
                    }
                    //expandedPlayer.setVisibility(View.INVISIBLE);
                    //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                    //mExoPlayerFullscreen = true;
                } else if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    llMainBottomPlayerView.setVisibility(View.VISIBLE);
                    expandedPlayer.setVisibility(View.INVISIBLE);
                    isPlayerClosed = true;
                    if (!Utility.isTelevision()) {
                        orientationEventListener.disable();
                        //mExoPlayerFullscreen = false;
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    }
                } else if (newState == SlidingUpPanelLayout.PanelState.ANCHORED) {
                    llMainBottomPlayerView.setVisibility(View.VISIBLE);
                    expandedPlayer.setVisibility(View.INVISIBLE);
                    if (!Utility.isTelevision()) {
                        orientationEventListener.disable();
                        //mExoPlayerFullscreen = false;
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    }
                } else if (newState == SlidingUpPanelLayout.PanelState.HIDDEN) {
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    llMainBottomPlayerView.setVisibility(View.VISIBLE);
                    expandedPlayer.setVisibility(View.INVISIBLE);
                    isPlayerClosed = true;
                    if (!Utility.isTelevision()) {
                        orientationEventListener.disable();
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    }
                    //mExoPlayerFullscreen = false;
                }
                checkPlayerVisibility();
            }
        });
        initFullscreenDialog();
    }

    public void playPausePlayback(View view) {
        if (serviceBound && service.getPlayer() != null) {
            if (service.isPlayingAd()) {
                return;
            }
            if (service.isPlaying()) {
                service.getPlayer().setPlayWhenReady(false);
            } else {
                service.getPlayer().setPlayWhenReady(true);
            }
        } else {
            // Error. Player is null. Hide the player fragment.
        }
    }

    public void pausePlayer() {
        if (serviceBound && service.getPlayer() != null) {
            if (service.isPlaying()) {
                service.getPlayer().setPlayWhenReady(false);
            }
        }
    }

    public void closePlayer(View view) {
        if (serviceBound && service != null) {
            if (service.getCurrentItem() != null) {
                service.logEvent();
                if (service.getPlayedseconds() != 0) {
                    int seconds = service.getPlayedseconds();
                  /*  AnalyticsData analyticsData = new AnalyticsData(ACTIVITY_TYPE_PLAY_AUDIO,
                            service.getCurrentItem().postId,
                            service.getCurrentItem().postTitle,
                            service.getCurrentItem().postType,
                            String.valueOf(seconds),
                            LocalStorage.getUserData().userId,
                            LocalStorage.getUserData().displayName,
                            LocalStorage.getUserData().mobile,
                            Utility.getCurrentDateTime());
                    listRoomDatabase.mediaDAO().saveAnalytics(analyticsData);*/
                }
            }
            service.stopStreaming();
            //deletePlaylist();
        }
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        configurePlayers();
        isPlayerClosed = true;
        container.setPadding(0, 0, 0, 0);
        getWindow().clearFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    //set side tv navigation view
    private void setTvNavigationView() {
        navMenuFragment = new NavigationMenu();
        fragmentReplacer(nav_fragment.getId(), navMenuFragment);
        nav_fragment.setVisibility(View.VISIBLE);
    }

    // Set bottom navigation view
    private void setCustomBottomNavigationView() {
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setBackgroundColor(getResources().getColor(R.color.tabBackground));
        bottomNavigationMenuList.add(bottomNavigationView.getMenu().getItem(0));
        bottomNavigationView.setVisibility(View.VISIBLE);
    }

    private void setExpandedPlayerLayout(int prevPosition, int position, String contextType, String contextId, String iswatched) {
        // getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        llExpandableViewLoader.setVisibility(View.VISIBLE);
        nsvNestedScrollView.setVisibility(View.GONE);
        this.contextType = contextType;

        // Initialize values
        arrayListQuestionData.clear();
        llSimilarItems.removeAllViews();
        questionIndex = 0;
        feedContentPlayerData = service.getPlayList().get(position);

        //---------------------- Set Title, Subtitle and Image -----------------------

        tvAudioSubtitle.setVisibility(View.GONE);
        tvAudioSubtitleSlider.setVisibility(View.GONE);
        tvLikeMusic.setTag(feedContentPlayerData.postId);
//        tvCommentMusic.setText(String.format(getResources().getString(R.string.label_button_comment_no), String.valueOf(feedContentPlayerData.commentCount)));
        tvDownload.setText(String.format(getResources().getString(R.string.label_button_download_name), ""));
        btLike.setTag(feedContentPlayerData.postId);
        if (Utility.isHtml(feedContentPlayerData.description)) {
            webView.loadDataWithBaseURL(null, feedContentPlayerData.description, "text/html", "utf-8", null);
            webView.setVisibility(View.VISIBLE);
        } else {
            webView.setVisibility(View.GONE);
        }
       /* boolean isliked = LocalStorage.isLiked(Integer.parseInt(feedContentPlayerData.postId));
        if (isliked) {
            LikeData likeData = LocalStorage.getLikes(Integer.parseInt(feedContentPlayerData.postId));
            tvLikeMusic.setText(String.format(getResources().getString(R.string.label_button_like_no), likeData.getCount()));
            setLikeMusicImage(likeData.isLike());
        } else {*/
           /* tvLikeMusic.setText(String.format(getResources().getString(R.string.label_button_like_no), feedContentPlayerData.likesCount));
            setLikeMusicImage(feedContentPlayerData.likeByMe);*/
        getLikeAndComment(feedContentPlayerData.postId);
        //}
       /* boolean isComented = LocalStorage.isCommented(Integer.parseInt(feedContentPlayerData.postId));
        if (isComented) {
            LikeData likeData = LocalStorage.getComments(Integer.parseInt(feedContentPlayerData.postId));
            tvCommentMusic.setText(String.format(getResources().getString(R.string.label_button_comment_no), likeData.getCount()));

        } else {
            tvCommentMusic.setText(String.format(getResources().getString(R.string.label_button_comment_no), String.valueOf(feedContentPlayerData.commentCount)));
        }*/

       /* if (ExoUtil.isDownloaded(feedContentPlayerData.postId)) {
            setDownloadedImage(IS_DOWNLOADED);
        } else {
            setDownloadedImage(IS_DOWNLOAD_FAILED);
        }*/

        if (contextId.equalsIgnoreCase("")) {
            tvAddtoWatchlist.setTag(feedContentPlayerData.postId);
            /*boolean isAddedToWatchList = LocalStorage.isAddedToWishList(Integer.parseInt(feedContentPlayerData.postId), getApplicationContext());

            if (isAddedToWatchList) {
                iswatched = String.valueOf(isAddedToWatchList);
                setWishImage(isAddedToWatchList);
            } else {
                setWishImage(feedContentPlayerData.iswatchlisted);
            }*/
        } else {
            tvAddtoWatchlist.setTag(contextId);
           /* boolean isAddedToWatchList = LocalStorage.isAddedToWishList(Integer.parseInt(contextId), getApplicationContext());
            if (isAddedToWatchList) {
                iswatched = String.valueOf(isAddedToWatchList);
                setWishImage(isAddedToWatchList);
            } else {
                setWishImage(Boolean.parseBoolean(iswatched));
            }*/
        }

        //---------------------- Set Questions -----------------------
        // setEmbeddedGameQuestion();
        setNoQuestionLayout();
        getSongQuestionAPI(feedContentPlayerData.postId);


        //---------------------- Set Thumbnail image on ExoPlayer if audio post -----------------------
        setThumbnail();
        if (hasSongChanged) {
            // Set first and last index to display only 10 data items at a time
            switch (contextType) {
                case Analyticals.CONTEXT_SERIES:
                case Analyticals.CONTEXT_EPISODE:
                    setEpisodeList(iswatched, position);
                    break;
                case Analyticals.CONTEXT_PLAYLIST:
                    setPlaylist(iswatched, contextId, position);
                    break;
            }
        }

        if (feedContentPlayerData.singleCredit) {
            tvAddtoWatchlist.setVisibility(View.GONE);
            tvDownload.setVisibility(View.GONE);
            tvVisitUrl.setVisibility(View.VISIBLE);
        } else {
            tvAddtoWatchlist.setVisibility(View.VISIBLE);
            tvDownload.setVisibility(View.VISIBLE);
            tvVisitUrl.setVisibility(View.GONE);
        }

        switch (contextType) {
            case Analyticals.CONTEXT_SERIES:
                StringBuilder sbSubtitle = new StringBuilder();
                sbSubtitle.append(feedContentPlayerData.seasonName).append(", ").append(feedContentPlayerData.postTitle);

                /*tabSeries.setVisibility(View.VISIBLE);
                nsvNestedScrollView.setVisibility(View.VISIBLE);*/
                llEpisodeData.setVisibility(View.VISIBLE);
                tvAudioSubtitle.setVisibility(View.VISIBLE);
                tvAudioSubtitleSlider.setVisibility(View.VISIBLE);
                tvAudioTitle.setText(feedContentPlayerData.seriesName);
                tvAudioTitlesiler.setText(feedContentPlayerData.seriesName);
                tvAudioSubtitle.setText(sbSubtitle.toString());
                tvAudioSubtitleSlider.setText(sbSubtitle.toString());
                Glide.with(HomeActivity.this).load(feedContentPlayerData.imgUrl).apply(new RequestOptions().centerCrop().dontAnimate().diskCacheStrategyOf(DiskCacheStrategy.ALL)).into(ivAudioImage);

                //------------ Similar to this -----------------------//
                getSimilarItemsAPI(feedContentPlayerData.postId);
                break;
            case Analyticals.CONTEXT_EPISODE:
                StringBuilder sbSubtitle1 = new StringBuilder();
                sbSubtitle1.append(feedContentPlayerData.seasonName).append(", ").append(feedContentPlayerData.postTitle);

                /*tabSeries.setVisibility(View.VISIBLE);
                nsvNestedScrollView.setVisibility(View.VISIBLE);*/
                llEpisodeData.setVisibility(View.VISIBLE);
                tvAudioSubtitle.setVisibility(View.VISIBLE);
                tvAudioSubtitleSlider.setVisibility(View.VISIBLE);
                tvAudioTitle.setText(feedContentPlayerData.seriesName);
                tvAudioTitlesiler.setText(feedContentPlayerData.seriesName);
                tvAudioSubtitle.setText(sbSubtitle1.toString());
                tvAudioSubtitleSlider.setText(sbSubtitle1.toString());
                Glide.with(HomeActivity.this).load(feedContentPlayerData.imgUrl).apply(new RequestOptions().centerCrop().dontAnimate().diskCacheStrategyOf(DiskCacheStrategy.ALL)).into(ivAudioImage);

                //------------ Similar to this -----------------------//
                getSimilarItemsAPI(feedContentPlayerData.postId);
                break;

            case Analyticals.CONTEXT_PLAYLIST:
                llEpisodeData.setVisibility(View.VISIBLE);
                tvAudioSubtitle.setVisibility(View.VISIBLE);
                tvAudioSubtitleSlider.setVisibility(View.VISIBLE);
                tvAudioTitle.setText(feedContentPlayerData.playlistName);
                tvAudioTitlesiler.setText(feedContentPlayerData.playlistName);
                tvAudioSubtitle.setText(feedContentPlayerData.postTitle);
                tvAudioSubtitleSlider.setText(feedContentPlayerData.postTitle);
                Glide.with(HomeActivity.this).load(feedContentPlayerData.playlistImageUrl).apply(new RequestOptions().centerCrop().dontAnimate().diskCacheStrategyOf(DiskCacheStrategy.ALL)).into(ivAudioImage);
                //------------ Similar to this -----------------------//
                getSimilarItemsAPI(feedContentPlayerData.postId);
                break;

            default:

                /*tabSeries.setVisibility(View.GONE);
                nsvNestedScrollView.setVisibility(View.GONE);*/
                llEpisodeData.setVisibility(View.GONE);
                tvAudioSubtitle.setVisibility(View.GONE);
                tvAudioSubtitleSlider.setVisibility(View.GONE);
                tvAudioTitle.setText(feedContentPlayerData.channelName.equals("") ? feedContentPlayerData.postTitle : feedContentPlayerData.channelName);
                tvAudioTitlesiler.setText(feedContentPlayerData.channelName.equals("") ? feedContentPlayerData.postTitle : feedContentPlayerData.channelName);
                Glide.with(HomeActivity.this).load(feedContentPlayerData.imgUrl).apply(new RequestOptions().centerCrop().dontAnimate().diskCacheStrategyOf(DiskCacheStrategy.ALL)).into(ivAudioImage);
                /*llExpandableViewLoader.setVisibility(View.GONE);
                nsvNestedScrollView.setVisibility(View.VISIBLE);*/
                //------------ Similar to this -----------------------//
                getSimilarItemsAPI(feedContentPlayerData.postId);
                break;
        }
    }

    private void showPlaylist() {
        if (currentFeedPosition != prevFeedPosition) {
            if (service != null) {
                arrayListPlaylist.clear();
                arrayListPlaylist.addAll(service.getPlayList());
                adapterEpisode = null;

                if (adapterEpisode == null) {
                    adapterEpisode = new KRecyclerViewAdapter(this, arrayListPlaylist, new KRecyclerViewHolderCallBack() {
                        @Override
                        public KRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                            View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_episode, viewGroup, false);
                            return new EpisodeHolder(layoutView, null);
                        }
                    }, new KRecyclerItemClickListener() {
                        @Override
                        public void onRecyclerItemClicked(KRecyclerViewHolder kRecyclerViewHolder, Object o, int i) {
                            if (o instanceof FeedContentData) {
                                //playItems(arrayListPlaylist, i, arrayListPlaylist.get(i).postId, pagetype);
                            }
                        }
                    });
                    rvEpisode.setAdapter(adapterEpisode);
                    //isCreateViewCalled = false;

                } else {
                    adapterEpisode.notifyDataSetChanged();
                }
            }
        }
    }

    private void getSongQuestionAPI(String ID) {
        try {
            Map<String, String> params = new HashMap<>();
            APIRequest apiRequest = new APIRequest(Url.GET_QUESTIONS + ID, Request.Method.GET, params, null, this);
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    handleResponse(response, error);
                }
            });
        } catch (Exception ignored) {

        }
    }

    private void handleResponse(String response, Exception error) {
        try {
            if (error != null) {
                setNoQuestionLayout();
            } else {
                JSONObject jsonObject = new JSONObject(response);
                String type = jsonObject.getString("type");
                if (type.equals("OK")) {
                    JSONObject msg = jsonObject.getJSONObject("msg");
                    JSONArray questions = msg.getJSONArray("questions");
                    questiontotalcoins = msg.getString("total_coins");
                    if (questions.length() == 0 && questiontotalcoins.equalsIgnoreCase("0")) {
                        setNoQuestionLayout();

                    } else if (questions.length() == 0 && !questiontotalcoins.equalsIgnoreCase("0")) {
                        setRightAnswerWonLayout(questiontotalcoins);
                    } else {
                        for (int i = 0; i < questions.length(); i++) {
                            JSONObject object = questions.getJSONObject(i);
                            QuestionData questionData = new QuestionData(object);
                            arrayListQuestionData.add(questionData);
                        }
                        setEmbeddedGameQuestion();
                    }
                }
            }
        } catch (Exception ignored) {
            Log.e(TAG, ignored.toString());
        }
    }

    private void setEmbeddedGameQuestion() {
        //arrayListQuestionData = feedContentData.arrayListQuestionData;
        totalNoOfQuestions = arrayListQuestionData.size();

        if (arrayListQuestionData.size() > 0) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    try {
                        QuestionData questionData = arrayListQuestionData.get(questionIndex);
                        //  boolean isAnswered = LocalStorage.isAnswered(questionData.id);

                        //   if (!isAnswered) {
                        if (txtQuestion == null) txtQuestion = findViewById(R.id.txtQuestion);

                        if (tvQuestionPoints == null)
                            tvQuestionPoints = findViewById(R.id.tvQuestionPoints);
                        txtQuestion.setText(questionData.postTitle);
                        tvQuestionPoints.setText(String.format(getString(R.string.text_coin), questionData.points));

                        for (int p = 0; p < questionData.choices.size(); p++) {
                            QuestionChoiceData questionChoiceData = questionData.choices.get(p);
                            ArrayOfButton.get(p).setText(questionChoiceData.text);
                            ArrayOfButton.get(p).setTag(questionChoiceData.correct);
                            ArrayOfButton.get(p).setId(questionData.id);
                            ArrayOfButton.get(p).setContentDescription(questionData.points);
                            LinearLayout linearLayout = (LinearLayout) ArrayOfButton.get(p).getParent();
                            linearLayout.setBackground(getDrawable(R.drawable.bg_lightgray));
                            ViewGroup view = (ViewGroup) ArrayOfButton.get(p).getParent();
                            view.setId(Integer.parseInt(feedContentPlayerData.postId));
                            view.setTag(feedContentPlayerData.postTitle);
                        }
                        //setQuestionLayout();
                        new Handler(Looper.getMainLooper()).post(() -> setQuestionLayout());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            new Thread(() -> {

            }).start();

        } else {
                /*if (totalNoOfQuestions > questionIndex) {
                    setEmbeddedGameQuestion();
                    return;
                }*/
            setNoQuestionLayout();
        }
        /*} else {
            setNoQuestionLayout();
        }*/
    }

    private void startNextQuestionUIThread() {
        thread = new Thread() {

            @Override
            public void run() {

                // Block this thread for 5 seconds.
             /*   try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
                llLoader.setVisibility(View.VISIBLE);*/
                // After sleep finished blocking, create a Runnable to run on the UI Thread.
                runOnUiThread(() -> {
                    llLoader.setVisibility(View.VISIBLE);
                    llProgressbar.setVisibility(View.VISIBLE);
                    circleProgressBar.setProgress(100);
                    tvProgress.setText("" + 10);
                    progressCount = 9;
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (progressCount > -1) {
                                tvProgress.setText("" + progressCount);
                                circleProgressBar.setProgress(progressCount * 10);
                                progressCount--;
                                handler.postDelayed(this::run, 1000);
                            } else {
                                llProgressbar.setVisibility(View.GONE);
                                handler.removeCallbacks(this::run);
                            }
                        }
                    }, 1000);
                    questionIndex++;
                    if (totalNoOfQuestions > questionIndex) {
                        setEmbeddedGameQuestion();
                    } else {
                        llLoader.setVisibility(View.GONE);
                        //setNoQuestionLayout();
                    }

                });
                /* llLoader.setVisibility(View.GONE);*/

            }

        };

        // Don't forget to start the thread.
        thread.start();
    }

    /**
     * Set Music Like unlike Image
     *
     * @param likeByMe :Ture or False
     */
    private void setLikeMusicImage(boolean likeByMe) {
        if (likeByMe) {
            tvLikeMusic.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_likes_fill_assent, 0, 0);
            btLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_likes_fill_assent, 0, 0, 0);
            ivLike.setImageResource(R.drawable.ic_likes_fill_assent);
        } else {
            tvLikeMusic.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_likes_fill, 0, 0);
            btLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_likes_fill_white, 0, 0, 0);
            ivLike.setImageResource(R.drawable.like);
        }
    }

    /**
     * Set and Update Minimized Player UI
     */
    private void setMinimizedPlayer() {
        if (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.HIDDEN) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            llMainBottomPlayerView.setVisibility(View.VISIBLE);
            if (minimisePlayer) {
                minimisePlayer = false;
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            } else {
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
            isPlayerVisible = true;
            container.setPadding(0, 0, 0, 0);
            container.setPadding(0, 0, 0, playerHeight);
            return;
        }
        if (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
            return;
        }
        if (!isPlayerVisible) {
            llMainBottomPlayerView.setVisibility(View.VISIBLE);
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            isPlayerVisible = true;
            container.setPadding(0, 0, 0, 0);
            container.setPadding(0, 0, 0, playerHeight);
        }
    }

    private void setExpandedPlayer(boolean play) {
        SlidingUpPanelLayout.PanelState state = play ? SlidingUpPanelLayout.PanelState.EXPANDED : SlidingUpPanelLayout.PanelState.COLLAPSED;
        llMainBottomPlayerView.setVisibility(View.VISIBLE);
        slidingUpPanelLayout.setPanelState(state);
        isPlayerVisible = true;
        container.setPadding(0, 0, 0, 0);
        container.setPadding(0, 0, 0, playerHeight);
    }

    public void rewindForward(View view) {
        if (service != null) {
            String tagActionType = String.valueOf(view.getTag());
            service.setRewindForward(tagActionType, service.getMILLISECONDS());
        }
    }

    /**
     * Open specific fragment on BottomNavigationView item selected
     *
     * @param fragment Fragment object to display the Fragment
     */
    public void openFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (fragment instanceof ChannelCategoryFragment || fragment instanceof HomeBuyTabFragment || fragment instanceof HomePlayTabFragment || fragment instanceof AvatarFragment ||
                fragment instanceof CreateFragment || fragment instanceof MoreFragment || fragment instanceof SearchFragment || fragment instanceof NotificationFragment ||
                fragment instanceof LiveJavaFrag || fragment instanceof ProfileFragment || fragment instanceof CouponFragment || fragment instanceof ShopFrag || fragment instanceof DiamondsFragment) {
            Fragment tempfragment = getSupportFragmentManager().findFragmentById(R.id.container);
            if (!fragment.getClass().getSimpleName().equalsIgnoreCase(tempfragment.getClass().getSimpleName())) {
                fragmentTransaction.replace(R.id.container, fragment);
                fragmentTransaction.addToBackStack(null);
            }
        } else if (fragment instanceof MainHomeFragment) {
            fragmentTransaction.replace(R.id.container, fragment);
            if (!isFirstTime) {
                fragmentTransaction.addToBackStack(null);
            }
            isFirstTime = false;
        } else if (fragment instanceof MoviesFragment || fragment instanceof PremiumTvFragment || fragment instanceof SearchTvFragment) {
            fragmentTransaction.replace(R.id.container, fragment);
            fragmentTransaction.addToBackStack(null);
            nav_fragment.setVisibility(View.VISIBLE);
        } else if (fragment instanceof BuyDetailFragment || fragment instanceof SeasonsTvFragment) {
            fragmentTransaction.replace(R.id.container, fragment);
            fragmentTransaction.addToBackStack(null);
            nav_fragment.setVisibility(View.GONE);
        }
        fragmentTransaction.commit();
    }

    public void showHome(String switchToSection) {
        switchToTabAtIndex(0, switchToSection);
    }

    public void showPremium() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).post(() -> bottomNavigationView.setSelectedItemId(R.id.bNavSubscribe));
            }
        }, 100);
    }

    void showChannels() {
        switchToTabAtIndex(1, Constant.TAB_WATCH);
    }

    private void switchToTabAtIndex(int index, String switchToSection) {
        MenuItem item = bottomNavigationView.getMenu().getItem(index);
        bottomNavigationView.setSelectedItemId(item.getItemId());
        if (switchToSection.equalsIgnoreCase(Constant.TAB_WATCH)) {
            showHomeTypeTab(0);
        } else if (switchToSection.equalsIgnoreCase(Constant.TAB_PLAY)) {
            showHomeTypeTab(1);
        } else if (switchToSection.equalsIgnoreCase(Constant.TAB_BUY)) {
            showHomeTypeTab(2);
        }
    }

    private void showHomeTypeTab(int index) {
       /* if (HomeFragment.tlHomeTabs != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    HomeFragment.tlHomeTabs.getTabAt(index).select();
                }
            }, 100);
        }*/
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onBackPressed() {
        if (slidingUpPanelLayout != null && (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED)) {
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else if (videosViewPager.getVisibility() == View.VISIBLE) {
            videosViewPager.setVisibility(View.GONE);
            videoId.clear();
            videoViewArrayList.clear();
            textViewArrayList.clear();
            imageViewArrayList.clear();
            verticalVideoList.clear();
            videosViewPager.setAdapter(null);
            AnalyticsTracker.stopDurationVideo();
        } else {
            hideKeyboard();
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
            try {
                if (Utility.isTelevision()) {
                    if (fragment instanceof MoviesFragment || fragment instanceof PremiumTvFragment || fragment instanceof SearchTvFragment || fragment instanceof MoreFragment) {
                        if (navMenuFragment.isNavigationOpen()) {
                            showExitAppDialog();
                        } else {
                            if (fragment instanceof MoviesFragment) {
                                moviesFragment.navigationMenuCallback.navMenuToggle(true);
                            } else if (fragment instanceof PremiumTvFragment) {
                                premiumTvFragment.navigationMenuCallback.navMenuToggle(true);
                            } else if (fragment instanceof SearchTvFragment) {
                                searchTvFragment.navigationMenuCallback.navMenuToggle(true);
                            } else if (fragment instanceof MoreFragment) {
                                moreFragment.navigationMenuCallback.navMenuToggle(true);
                            }
                        }
                    } else if (fragment instanceof LiveJavaFrag || fragment instanceof NotificationFragment) {
                        super.onBackPressed();
                        navMenuFragment.setLastSelectedMenu(Constant.nav_menu_home);
                        switchFragment(Constant.nav_menu_home);
                        navMenuFragment.closeNav(Constant.nav_menu_home);
                    } else {
                        super.onBackPressed();
                    }
                } else {
                    super.onBackPressed();
                    if (fragment instanceof MainHomeFragment || fragment instanceof ShopFrag || fragment instanceof AvatarFragment || fragment instanceof CreateFragment ||
                            fragment instanceof HomeBuyTabFragment || fragment instanceof MoreFragment) {
                        if (bottomNavigationMenuList.size() > 0) {
                            bottomNavigationMenuList.remove(bottomNavigationMenuList.size() - 1);
                            if (bottomNavigationMenuList.size() > 0) {
                                previousTabId = bottomNavigationMenuList.get(bottomNavigationMenuList.size() - 1).getItemId();
                                bottomNavigationMenuList.get(bottomNavigationMenuList.size() - 1).setChecked(true);
                                AnalyticsTracker.stopDurationShop();
                            }
                        }
                    }
                }

                if (fragment instanceof NotificationFragment) {
                    globalSettingApiCall();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Collapse panel if expanded
        if (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        /*if (id == R.id.bNavHome || id == R.id.bNavChannels || id == R.id.bNavMore
                || id == R.id.bNavPurchases || id == R.id.bNavVoucher) {*/
        if (id == R.id.bNavHome || id == R.id.bNavSubscribe || id == R.id.bNavMore || id == R.id.bNavAvatar || id == R.id.bNavShop) {

            bottomNavigationItemClicked(item);
            return true;
        }

        // Other menu item
        if (id == R.id.nav_camera) {

        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        //drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    /**
     * Download or  delete item.
     *
     * @param item Item to be downloaded or deleted. If null, checks for current playing item.
     */
    private void downloadOrDeleteItem(@Nullable FeedContentData item) {
        downloadProgressBar.setVisibility(View.VISIBLE);
        new Handler().postDelayed(() -> {
            downloadProgressBar.setVisibility(View.GONE);
        }, 3000);
        if (serviceBound && service != null) {
            if (item == null) item = service.getCurrentItem();
            if (item != null) {
                if (ExoUtil.isDownloaded(item.postId)) {
                    //ExoUtil.delete(item.postId);
                } else {
                    Uri uri = Uri.parse(item.getMediaUrl());
                    if (uri != null) {
                        JSONObject feedItems = new JSONObject();
                        try {
                            feedItems.put("title", item.postTitle);
                            feedItems.put("duration", item.duration);
                            feedItems.put("thumb", item.imgUrl);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String objectString = feedItems.toString();
                        if (ExoUtil.isDash(uri) && item.getIdForLicence() != null && !item.getIdForLicence().isEmpty() && item.postDrmProtected.equalsIgnoreCase(Constant.YES)) {
                            FeedContentData finalItem = item;
                            ExoUtil.getOfflineLicense(LocalStorage.getUserId(), item.getIdForLicence(), item.postId, url -> {
                                if (url != null && !url.isEmpty()) {
                                    //download(uri, url, finalItem);
                                    if ((isNetworkConnected())) {
                                        RenderersFactory renderersFactory = MyApp.getInstance()
                                                .buildRenderersFactory(false);
                                        downloadTracker.toggleDownload(getSupportFragmentManager(), MediaItem.fromUri(uri), renderersFactory);
                                        download(uri, url, finalItem);
                                    }
                                }
                            });
                        } else {
                            RenderersFactory renderersFactory = MyApp.getInstance()
                                    .buildRenderersFactory(false);
                            if (isNetworkConnected()) {
                                downloadTracker.toggleDownload(getSupportFragmentManager(), MediaItem.fromUri(uri), renderersFactory);
                            }
                            //ExoUtil.download(this, item.postId, Uri.parse(item.getMediaUrl()));
                        }
                    }
                }
            }
        }
    }

    void download(Uri uri, String licenceUrl, FeedContentData item) {
        try {
            if (isNetworkConnected()) {
                DefaultHttpDataSource.Factory httpDataSourceFactory = new DefaultHttpDataSource.Factory().setUserAgent(
                        MyApp.getInstance().userAgent);

                OfflineLicenseHelper offlineLicenseHelper = OfflineLicenseHelper
                        .newWidevineInstance(licenceUrl, httpDataSourceFactory, new DrmSessionEventListener.EventDispatcher());

                DataSource dataSource = httpDataSourceFactory.createDataSource();
                DashManifest dashManifest = DashUtil.loadManifest(dataSource,
                        uri);
                Format drmInitData = DashUtil.loadFormatWithDrmInitData(dataSource, dashManifest.getPeriod(0));
                if (drmInitData != null) {
                    byte[] offlineLicenseKeySetId = offlineLicenseHelper.downloadLicense(drmInitData);
                    String key = Base64.encodeToString(offlineLicenseKeySetId, Base64.DEFAULT);
                    LocalStorage.saveLicence(item.postId, key);
                    //AlertUtils.showToast("License found", 4, this);
                } else {
                    //AlertUtils.showToast("License unavailable", 4, this);
                    Log.e("Offline Licence Download", "No licence required. File is not DRM protected");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("RestrictedApi")
    private void bottomNavigationItemClicked(@NonNull MenuItem item) {
        try {
            for (int i = 0; i < bottomNavigationView.getChildCount(); i++) {
                View child = bottomNavigationView.getChildAt(i);
                if (child instanceof BottomNavigationItemView) {
                    ((BottomNavigationItemView) child).setChecked(false);
                }
            }
            int id = item.getItemId();
            String tabId = getResources().getResourceEntryName(id);
            Log.i(TAG, String.valueOf(tabId));
            showAd(String.valueOf(tabId));
            if (id != R.id.bNavShop) {
                AnalyticsTracker.stopDurationShop();
            }
            videosViewPager.setVisibility(View.GONE);
            videosViewPager.setAdapter(null);
            if (id == R.id.bNavHome) {
                if (bottomNavigationMenuList.size() > 0 && !bottomNavigationMenuList.get(bottomNavigationMenuList.size() - 1).equals(item)) {
                    bottomNavigationMenuList.add(item);
                }
                openFragment(homeFragment);
                showHomeTypeTab(0);
                previousTabId = id;
            }/* else if (id == R.id.bNavVoucher1) {
                if (bottomNavigationMenuList.size() > 0 && !bottomNavigationMenuList.get(bottomNavigationMenuList.size() - 1).equals(item)) {
                    bottomNavigationMenuList.add(item);
                }
                openFragment(couponFragment);
                previousTabId = id;
            }*/ else if (id == R.id.bNavShop) {
                if (bottomNavigationMenuList.size() > 0 && !bottomNavigationMenuList.get(bottomNavigationMenuList.size() - 1).equals(item)) {
                    bottomNavigationMenuList.add(item);
                }
                openFragment(shopFrag);
                previousTabId = id;
            } else if (id == R.id.bNavAvatar) {
                if (bottomNavigationMenuList.size() > 0 && !bottomNavigationMenuList.get(bottomNavigationMenuList.size() - 1).equals(item)) {
                    bottomNavigationMenuList.add(item);
                }
                openFragment(createFragment);
                previousTabId = id;
            } else if (id == R.id.bNavSubscribe) {
                if (bottomNavigationMenuList.size() > 0 && !bottomNavigationMenuList.get(bottomNavigationMenuList.size() - 1).equals(item)) {
                    bottomNavigationMenuList.add(item);
                }
                //openFragment(downloadsFragment);
                openFragment(premiumFragment);
                previousTabId = id;
            } else if (id == R.id.bNavMore) {
                if (bottomNavigationMenuList.size() > 0 && !bottomNavigationMenuList.get(bottomNavigationMenuList.size() - 1).equals(item)) {
                    bottomNavigationMenuList.add(item);
                }
                openFragment(moreFragment);
                previousTabId = id;
            }
            /*else if (id == R.id.bNavChannels) {
                if (bottomNavigationMenuList.size() > 0 && !bottomNavigationMenuList.get(bottomNavigationMenuList.size() - 1).equals(item)) {
                    bottomNavigationMenuList.add(item);
                }
                openFragment(channelFragment);
                previousTabId = id;
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void refreshData(ArrayList<?> arrayListFeed, int tabPosition) {
        try {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
            if (fragment != null) {
                //((HomeFragment) fragment).updateOtherSliders((ArrayList<FeedCombinedData>) arrayListFeed);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void playSelection(int position) {
        trackPosition = position;
    }

    void openVoucher() {
        MenuItem item = bottomNavigationView.getMenu().getItem(1);
        bottomNavigationItemClicked(item);
        bottomNavigationView.setSelectedItemId(item.getItemId());
    }

    /*private void requestPermissionsForTrill() {
        Log.d("avinash", "checkPermissionsForTrill");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, Constants.neededPermissions, 1);
        } else {
            startTrillService();
            Log.d("avinash", "start checkPermissionsForTrill");
            if (!TrillbitService.isRunning) {
                startTrillService();
            }
            Log.d("avinash", "start checkPermissionsForTrill");
        }
    }

    private void startTrillService() {
        Log.d("avinash", "JobScheduler scheduled");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("avinash", "API > 26");
            ComponentName componentName = new ComponentName(getApplicationContext(), TrillbitJobService.class);
            JobInfo jobInfo = new JobInfo.Builder(01, componentName)
                    .setPersisted(true)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .build();
            JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            scheduler.schedule(jobInfo);
        } else {
            Log.d("avinash", "API < 26");
            Intent intent = new Intent(this, TrillbitService.class);
            startService(intent);
        }
    }

    private void stopTrillService() {
        Intent intent = new Intent(this, TrillbitService.class);
        stopService(intent);
    }*/

    public void openDownloads() {
        /*MenuItem item = bottomNavigationView.getMenu().getItem(3);
        bottomNavigationItemClicked(item);
        bottomNavigationView.setSelectedItemId(item.getItemId());*/
        startActivity(new Intent(this, DownloadActivity.class).putExtra("title", getResources().getString(R.string.label_downloads)));
    }

    void openStore() {
        MenuItem item = bottomNavigationView.getMenu().getItem(2);
        bottomNavigationItemClicked(item);
        bottomNavigationView.setSelectedItemId(item.getItemId());
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, avatarFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isAppBackground = false;
        if (startProfileFragment && profileFragment != null) {
            startProfileFragment = false;
            moreFragment.setShouldOpenProfile(true);
            openFragment(moreFragment);
        }
        startProfileFragment = false;

        /*Fragment premiumFragment = getSupportFragmentManager().findFragmentById(R.id.container);
        if (premiumFragment instanceof HomeBuyTabFragment) {
            FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
            fragTransaction.detach(premiumFragment);
            fragTransaction.attach(premiumFragment);
            fragTransaction.commit();
        }*/

        // setDrawer();
        if (serviceBound) {
            if (playerPagerAdapter != null) {
                playerPagerAdapter.updatePlayers(true);
            }
            if (playerView != null) {
                playerView.showMediaPlayer(false);
            }
        } else {
            bindService();
        }

        checkPlayerVisibility();
        if (videosViewPager.getVisibility() == View.VISIBLE) {
            AnalyticsTracker.startDurationVideo();
        }
        //initTrillbit();

        if (Constant.NOTIFICATION_PAGE.equalsIgnoreCase(Constant.SUBSCRIPTION)) {
            startActivity(new Intent(this, PremiumActivity.class).putExtra("title", getResources().getString(R.string.label_premium)));
        }

        if (isLoaded) {
            getWalletBalance();
        } else {
            isLoaded = true;
            //addBonusPoints();
        }
        if (callSettings) {
            globalSettingApiCall();
            getAdMobAPiCall();
        }

        getUserDetails();
        // checkUserValidityForSurvey();

        if (player != null && resumePlayer) {
            player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
            player.setPlayWhenReady(true);
            pvCustomAd.setPlayer(player);
        }

        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                // If an in-app update is already running, resume the update.
                try {
                    appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, this, REQUEST_CODE_UPDATE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        });

        if (Constant.ACTION_RESTART.equalsIgnoreCase("restart")) {
            if (getIntent() != null) {
                Constant.ACTION_RESTART = "";
                finish();
                startActivity(getIntent());
            }
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    void initTrillbit() {
       /* if (hasPermissions(HomeActivity.this, Constants.neededPermissions)) {
            if (!TrillbitService.isRunning) {
                startTrillService();
            }
        } else {
            requestPermissionsForTrill();
        }*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        isAppBackground = true;
        if (serviceBound) {
            /*if (service != null && (service.isPlaying() || service.isPlayingAd())) {
                FeedContentData currentItem = service.getCurrentItem();
                if (currentItem != null) {
                    //if (currentItem.attachmentData.type.equalsIgnoreCase("video")) {
                    if (service.getPlayer() != null) {
                        service.getPlayer().setPlayWhenReady(false);
                    }
                    if (service.getAdPlayer() != null) {
                        service.getAdPlayer().setPlayWhenReady(false);
                    }
                    playerView.showMediaPlayer(false);
                    //}
                }
            }*/
            if (playerPagerAdapter != null) {
                playerPagerAdapter.resetPlayers();
            }
        }

        if (service != null) {
            service.pauseMediaPlayer();
        }

        if (player != null) {
            player.setPlayWhenReady(false);
            resumePlayer = true;
            pvCustomAd.setPlayer(null);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        downloadTracker.addListener(this);
    }

    @Override
    protected void onStop() {
        downloadTracker.removeListener(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (serviceBound) {
            service.stopStreaming();
            unbindService(serviceConnection);
        }
        LocalStorage.KEY_MUSIC_LIKE_LIST.clear();

        sendTrillBroadcastReceiver();
        this.unregisterReceiver(voucherBroadCast);
        this.unregisterReceiver(storeBroadCast);
        this.unregisterReceiver(downloadsBroadcast);
        //this.unregisterReceiver(adsNotificationBroadCast);
        this.unregisterReceiver(earnCoinsBroadCast);
        // this.unregisterReceiver(checkInternetConnectionBroadCast);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(downloadingBroadcast);

        closePlayer();
        super.onDestroy();
    }

    private void sendTrillBroadcastReceiver() {
        Log.d("avinash", "sendTrillBroadcastReceiver");
        Intent intent = new Intent(Constants.INTENT_FILTER_TRILL_BROADCASTER);
        getApplicationContext().sendBroadcast(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibclose:
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                break;
            case R.id.llMainBottomPlayerView:
                homeFragment.panelListener(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                        slidingUpPanelLayout.setOverlayed(true);
                        if (playerPagerAdapter != null) {
                            boolean forceMediaPlayer = !service.isPlayingAd() || service.getAdPlayer() == null;
                            playerPagerAdapter.updatePlayers(forceMediaPlayer);
                        }
                    }
                }, 700);
                break;
        }
    }

    public void checkOption(View view) throws JSONException {
        if (view instanceof Button) {
            Button button = (Button) view;
            questionNo++;
            LinearLayout linearLayout = (LinearLayout) button.getParent();
            Utility.setShadeBackground(linearLayout);
            boolean isCorrectAns = button.getTag().equals("") ? false : (boolean) button.getTag();
            ViewGroup viewGroup = (ViewGroup) button.getParent();
            JSONObject answer = new JSONObject().put("answer", button.getText().toString());
            String context_id = String.valueOf(viewGroup.getId());

            Analyticals.CallplayedQuestionApi(HomeActivity.this, Analyticals.AUDIO_SONG_QUESTION_ACTIVITY_TYPE, String.valueOf(button.getId()), Analyticals.CONTEXT_AUDIO_SONG, context_id, answer, (success, activity_id, error) -> {
                if (success) {
                    addWalletCoins(view);
                } else {
                    llLoader.setVisibility(View.GONE);
                    setWrongAnswerLayout();
                }
                // startNextQuestionUIThread();
            });
        }
    }

    public void tryAnotherQuestion(View view) {
        if (thread != null && thread.isAlive()) thread.interrupt();
        if (totalNoOfQuestions == questionNo) {
            setRightAnswerWonLayout(String.valueOf(totalcoinsofquestion));
        } else {
            startNextQuestionUIThread();
        }
        // setEmbeddedGameQuestion();
    }

    public void setLikeMusic(View view) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("post_id", view.getTag().toString());
            APIRequest apiRequest = new APIRequest(Url.LIKES, Request.Method.POST, params, null, this);
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    handleResponse(response, error, view.getTag().toString());
                }
            });
        } catch (Exception ignored) {
        }
    }

    public void getLikeAndComment(String id) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("post_id", id);
            APIRequest apiRequest = new APIRequest(Url.GET_LIKES_COMMENTS + "&post_id=" + id, Request.Method.GET, params, null, this);
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    handleLikeAndCommentResponse(response, error, id);
                }
            });
        } catch (Exception ignored) {
        }
    }

    public void setCommentMusic(View view) {
        Intent intent = new Intent(HomeActivity.this, CommentListActivity.class);
        intent.putExtra("postId", feedContentPlayerData.postId);
        intent.putExtra("postTitle", feedContentPlayerData.postTitle);
        startActivityForResult(intent, CommentListActivity.REQUEST_CODE);
    }

    public void openCommentDialog(FeedContentData feedContentData) {
        CommentListFragment commentListFragment = new CommentListFragment(feedContentData.postId, feedContentData.postTitle, this);
        commentListFragment.show(getSupportFragmentManager(), commentListFragment.getTag());
    }

    /**
     * Check if user is a premium user then only allow to download
     */
    public void download(View view) {
        if (LocalStorage.isUserPremium()) {
            if (isNetworkConnected()) {
                downloadOrDeleteItem(null);
            }
        } else {
            setMinimizedPlayer();
            openDownloads();
        }
        //downloadOrDeleteItem(null);
    }

    public void shareData(View view) {
        String feedImage = feedContentPlayerData.imgUrl.equals("") ? feedContentPlayerData.playlistImageUrl.equals("") ? "" : feedContentPlayerData.playlistImageUrl : feedContentPlayerData.imgUrl;
        if (!feedImage.equals("")) {
            Picasso.get().load(feedImage).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    try {
                        String appLink = Constant.APP_SHARE_LINK;
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.putExtra(Intent.EXTRA_TITLE, getString(R.string.label_itap));
                        i.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(bitmap, view.getContext()));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            i.putExtra(Intent.EXTRA_TEXT, getString(R.string.hey_watch) + feedContentPlayerData.postTitle + getString(R.string.check_it_out_now_on_iTap) + "\n" +
                                    "https://www.itap.online?" +
                                    Utility.encryptData("playstore=true&type=" + feedContentPlayerData.postType + "&postId=" + feedContentPlayerData.postId, Constant.getSecretKey())
                            );
                        }
                        i.setType("*/*");
                        startActivity(Intent.createChooser(i, getString(R.string.share_content)));
                    } catch (Exception e) {

                    }
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
        }
    }

    public void visitUrl(View view) {
        if (service != null && service.getCurrentItem() != null) {
            openWeb(service.getCurrentItem().adFieldsData);
        }
    }

    private void handleResponse(String response, Exception error, String s) {
        try {
            if (error != null) {
                //  TODO: Handle error state (Empty State)
            } else {
                JSONObject jsonObject = new JSONObject(response);
                String type = jsonObject.getString("type");
                if (type.equals("OK")) {
                    JSONObject msg = jsonObject.getJSONObject("msg");
                    tvLikeMusic.setText(String.format(getResources().getString(R.string.label_button_like_no), msg.getString("no_likes")));
                    tvLike.setText(msg.getString("no_likes"));
                    setLikeMusicImage(msg.getBoolean("liked"));
                    // LocalStorage.setLiked(Integer.parseInt(s), msg.getString("no_likes"), msg.getBoolean("liked"));
                }
            }
        } catch (Exception ignored) {

        }
    }

    private void handleLikeAndCommentResponse(String response, Exception error, String s) {
        try {
            if (error != null) {
                //  TODO: Handle error state (Empty State)
            } else {
                JSONObject jsonObject = new JSONObject(response);
                String type = jsonObject.getString("type");
                if (type.equals("OK")) {
                    JSONObject msg = jsonObject.getJSONObject("msg");
                    tvLikeMusic.setText(String.format(getResources().getString(R.string.label_button_like_no), msg.getString("like_count")));
                    tvLike.setText(msg.getString("like_count"));
                    setLikeMusicImage(msg.getBoolean("liked_by_me"));

                    tvCommentMusic.setText(String.format(getResources().getString(R.string.label_button_comment_no), msg.getString("comment_count")));
                    tvComment.setText(msg.getString("comment_count"));

                    setWishImage(msg.getBoolean("is_watch_listed"));
                }
            }
        } catch (Exception ignored) {

        }
    }

    private void setRightAnswerLayout(long coins) {

        tvWonCoins.setText(String.format(getString(R.string.text_coin), String.valueOf(coins)));
        totalcoinsofquestion = totalcoinsofquestion + (int) coins;
        llQuestion.setVisibility(View.GONE);
        llProgressbar.setVisibility(View.GONE);
        rlWrongAnswer.setVisibility(View.GONE);
        rlRightAnswer.setVisibility(View.VISIBLE);
        rlShopNow.setVisibility(View.GONE);
        //cvMissedQuestions.setVisibility(View.VISIBLE);
    }

    private void setRightAnswerWonLayout(String questiontotalcoins) {
        questiontotalcoins = questiontotalcoins.equalsIgnoreCase("") || questiontotalcoins.equalsIgnoreCase(null) || questiontotalcoins.equalsIgnoreCase("null") ? "0" : questiontotalcoins;
        tvWonCoin.setText(String.format(getString(R.string.text_coin), questiontotalcoins));
        questionHolder.setVisibility(View.VISIBLE);
        llQuestion.setVisibility(View.GONE);
        llProgressbar.setVisibility(View.GONE);
        rlWrongAnswer.setVisibility(View.GONE);
        rlRightAnswer.setVisibility(View.GONE);
        rlShopNow.setVisibility(View.VISIBLE);

        //cvMissedQuestions.setVisibility(View.VISIBLE);
    }

    private void setWrongAnswerLayout() {
        llQuestion.setVisibility(View.GONE);
        llProgressbar.setVisibility(View.GONE);
        rlWrongAnswer.setVisibility(View.VISIBLE);
        rlRightAnswer.setVisibility(View.GONE);
        rlShopNow.setVisibility(View.GONE);

        //cvMissedQuestions.setVisibility(View.VISIBLE);
    }

    private void setQuestionLayout() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                questionHolder.setVisibility(View.VISIBLE);
                llQuestion.setVisibility(View.VISIBLE);
                llProgressbar.setVisibility(View.GONE);
            }
        }, 11000);
        rlWrongAnswer.setVisibility(View.GONE);
        rlRightAnswer.setVisibility(View.GONE);
        rlShopNow.setVisibility(View.GONE);
        llLoader.setVisibility(View.GONE);

        //cvMissedQuestions.setVisibility(View.VISIBLE);
    }

    private void setNoQuestionLayout() {
        questionHolder.setVisibility(View.GONE);
        rlWrongAnswer.setVisibility(View.GONE);
        rlRightAnswer.setVisibility(View.GONE);
        rlShopNow.setVisibility(View.GONE);
        llProgressbar.setVisibility(View.GONE);
        //cvMissedQuestions.setVisibility(View.GONE);
    }

    public void shopNow(View view) throws JSONException {
        if (view instanceof ImageView) {
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            //HomeFragment.tlHomeTabs.getTabAt(2).select();
            //openFragment(HomeBuyTabFragment.newInstance());
            MenuItem item = bottomNavigationView.getMenu().getItem(1);
            bottomNavigationView.setSelectedItemId(item.getItemId());
            ShopFrag.selectedTab = 2;
            if (ShopFrag.tabLayoutRewards != null) {
                new Handler().postDelayed(() -> ShopFrag.tabLayoutRewards.getTabAt(0).select(), 100);
            }
        }
    }

    public void playChannel(@NonNull FeedContentData feedContentData, @NonNull ArrayList<FeedContentData> mediaList, int position, String channel_id, String pageType) {
        channel = feedContentData;
        plaChannelAds = LocalStorage.getChannelAdsEnabled();
        playMediaItems(mediaList, position, channel_id, pageType, String.valueOf(channel.iswatchlisted), true, plaChannelAds);
        if (playerPagerAdapter != null) {
            playerPagerAdapter.updatePlayers(false);
        }
    }

    public void playItems(@NonNull ArrayList<FeedContentData> mediaList, int position, String feed_id, String pageType, String iswatched) {
        channel = null;
        makePlaylist(mediaList, position, feed_id, pageType, iswatched, true, true);
        if (playerPagerAdapter != null) {
            playerPagerAdapter.updatePlayers(false);
        }
    }

    public void deletePlaylist() {
        new Thread(() -> {
            List<Playlist> list = listRoomDatabase.mediaDAO().getPlaylist();
            if (list != null && list.size() > 0) {
                listRoomDatabase.mediaDAO().deletePlaylist(list);
            }
        }).start();
    }

    private void playMediaItems(@NonNull ArrayList<FeedContentData> mediaList, int position,
                                String feed_id, String pageType, String iswatched,
                                boolean play, boolean playAd) {
        FirebaseAnalyticsLogs.startDurationStartUp();
        feedId = feed_id;
       /* if (feedId.equalsIgnoreCase(Constant.FEED_ID)) {
            Log.i(TAG, "Vertical video");
            homeActivity.setLayoutVideo(true);
        } else {
            Log.i(TAG, "Not Vertical video");
            homeActivity.setLayoutVideo(false);
        }*/
        LicenseManager.getInstance().downloadLicences(mediaList, licences -> {
            try {
                if (!Utility.isTelevision()) {
                    //savelocalPlayList(mediaList, position, feed_id, pageType, iswatched);
                }
            } catch (IndexOutOfBoundsException e) {
                Log.d("IndexOutOfBounds from playing media: ", e.getMessage());
            }
            Log.i("ADS", "playMediaItems");
            try {
                FeedContentData item = mediaList.get(position);
                String item1 = item.postType;
                if (!playAd) {
                    if (service != null) {
                        Log.i("ADS", "starting service");
                        service.logEvent();
                        service.setPlayAd(false);
                        if (!isPlayerClosed) {
                            service.play(mediaList, position, feed_id, pageType, iswatched, play);
                        }
                        //setExpandedPlayer(play);
                        //llMainBottomPlayerView.setVisibility(View.INVISIBLE);
                        if (mInterstitialAd != null) {
                            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    // Called when fullscreen content is dismissed.
                                    Log.d("TAG", "The ad was dismissed.");
                                    if (service.isPlaying()) {
                                        service.resumeMediaPlayer();
                                    }
                                }

                                @Override
                                public void onAdFailedToShowFullScreenContent(AdError adError) {
                                    // Called when fullscreen content failed to show.
                                    Log.d("TAG", "The ad failed to show.");
                                }

                                @Override
                                public void onAdShowedFullScreenContent() {
                                    // Called when fullscreen content is shown.
                                    // Make sure to set your reference to null so you don't
                                    // show it a second time.
                                    //application.mInterstitialAd = null;
                                    Log.d("TAG", "The ad was shown.");
                                    if (service.isPlaying()) {
                                        service.pauseMediaPlayer();
                                    }
                                }
                            });
                        }
                        if (!Utility.isTelevision()) {
                            showAd(item.feedTabType);
                        }
                        if (item.adsArray.size() > 0) {
                            if (item.adsArray.get(0).adFieldsData.play_at == 0) {
                                playerView.showAdPlayer(false);
                            }
                        }
                    }
                } else {
                    APIManager.getAdsForPost(item.postId, adsList -> {
                        Log.i("ADS", "ad loaded");
                        // hide loader
                        runOnUiThread(() -> {
                            item.isAdLoaded = true;
                            item.adsArray.addAll(adsList);
                            if (service != null) {
                                Log.i("ADS", "starting service");
                                service.logEvent();
                                service.setPlayAd(true);
                                if (!isPlayerClosed) {
                                    service.play(mediaList, position, feed_id, pageType, iswatched, play);
                                }
                                if (mInterstitialAd != null) {
                                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                        @Override
                                        public void onAdDismissedFullScreenContent() {
                                            // Called when fullscreen content is dismissed.
                                            Log.d("TAG", "The ad was dismissed.");
                                            service.resumeMediaPlayer();
                                            loadAd();
                                        }

                                        @Override
                                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                                            // Called when fullscreen content failed to show.
                                            Log.d("TAG", "The ad failed to show.");
                                            if (service.isPlaying()) {
                                                service.resumeMediaPlayer();
                                            }
                                            loadAd();
                                        }

                                        @Override
                                        public void onAdShowedFullScreenContent() {
                                            // Called when fullscreen content is shown.
                                            // Make sure to set your reference to null so you don't
                                            // show it a second time.
                                            //application.mInterstitialAd = null;
                                            Log.d("TAG", "The ad was shown.");
                                            service.pauseMediaPlayer();
                                        }
                                    });
                                }
                                if (!Utility.isTelevision()) {
                                    showAd(item.feedTabType);
                                }
                                //setExpandedPlayer(play);
                                //llMainBottomPlayerView.setVisibility(View.INVISIBLE);
                                if (item.adsArray.size() > 0) {
                                    if (item.adsArray.get(0).adFieldsData.play_at == 0) {
                                        playerView.showAdPlayer(false);
                                    }
                                }
                            }
                        });
                    });
                }
            } catch (IndexOutOfBoundsException e) {
                Log.d("IndexOutOfBounds from playing media: ", e.getMessage());
            }
        }, this);
    }

    /**
     * Save The Current Playing Song Data in Local
     *
     * @param mediaList: PlayList Data
     * @param position:  Position of PlayList
     * @param feed_id:   PlayList_ID or Series_ID
     * @param pageType:  PlayList or Series
     * @param iswatched: True or false
     */
    public void savelocalPlayList(@NonNull ArrayList<FeedContentData> mediaList, int position, String feed_id, String pageType, String iswatched) {
        new Thread(() -> {
            deletePlaylist();
            Playlist playlist = new Playlist();
            playlist.setPosition(position);
            playlist.setPlayListItems(mediaList);
            playlist.setFeed_id(feed_id);
            playlist.setPage_type(pageType);
            playlist.setIswatchlisted(Boolean.parseBoolean(iswatched));
            playlist.setArrayListPlaylistData(arrayListPlaylist);
            playlist.setArrayListEpisodeData(arrayListEpisodeData);
            playlist.setFeedContentDataSeries(feedContentDataSeries);
            playlist.setTotalNumberOfRecords(totalNumberOfRecords);
            playlist.setArrayListContentData(arrayListContentData);
            playlist.setCurrentSeasonId(currentSeasonId);
            listRoomDatabase.mediaDAO().savePlaylist(playlist);

            //playlistRoomDatabase.mediaDAO().insertPlayList(mediaList);
            //    Log.d("Average Rating: ", String.valueOf(playlistRoomDatabase.mediaDAO().getPlaylist()));
        }).start();
    }

//------------------------- App42 APIs START -------------------------------------------------------

    public void savePlayerMediaDuration(FeedContentData item, long currentPosition) {
        try {
            // new Thread(() ->).start();
            new Handler(Looper.getMainLooper()).post(() -> {
                MediaDuration mediaDuration = new MediaDuration();
                mediaDuration.setId(item.postId);
                if (service != null && service.getPlayer() != null) {
                    mediaDuration.setDuration(service.getPlayer().getDuration());
                }
                mediaDuration.setPlayedDuration(currentPosition);
                listRoomDatabase.mediaDAO().savePlayerDuration(mediaDuration);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long getResumePosition(FeedContentData data) {
        long d = 0;
        List<MediaDuration> result = listRoomDatabase.mediaDAO().getMediaDurationData(String.valueOf(data.id));
        if (result != null && result.size() > 0) {
            d = result.get(0).getPlayedDuration();
        }
        if (d < 0) {
            d = 0;
        }
        return d;
    }

    /**
     * Update The Current Playing Song Data in Local
     *
     * @param position:Position of PlayList
     */
    public void UpdatelocalPlayList(int position) {
        new Thread(() -> {
            List<Playlist> playlist = listRoomDatabase.mediaDAO().getPlaylist();
            if (playlist != null && playlist.size() > 0) {
                playlist.get(0).setPosition(position);
                listRoomDatabase.mediaDAO().updatePlaylist(playlist.get(0));

                //playlistRoomDatabase.mediaDAO().insertPlayList(mediaList);
                Log.d("Average Rating: ", String.valueOf(listRoomDatabase.mediaDAO().getPlaylist()));
            }
        }).start();
    }

    public void addWalletCoins(View view) {
        if (view instanceof Button) {
            Button button = (Button) view;
            boolean isCorrectAns = (boolean) button.getTag();
            int questionId = button.getId();

            if (isCorrectAns) {
                llLoader.setVisibility(View.VISIBLE);
                ViewGroup viewGroup = (ViewGroup) button.getParent();
                String contentDescription = button.getContentDescription().toString();
                String contentId = String.valueOf(viewGroup.getId());
                String description = viewGroup.getTag().toString();
                long earnCoins = contentDescription.equals("") || contentDescription.equals("null") ? 0 : Long.parseLong(contentDescription);

                Wallet.earnCoins(this, questionId, description, Wallet.FLAG_QUESTION, earnCoins, new WalletCallback() {
                    @Override
                    public boolean onResult(boolean success, @Nullable String error, long coins, long diamonds, long creditedCoins, JSONArray historyData, int historyCount) {
                        llLoader.setVisibility(View.GONE);
                        handleApp42Response(success, error, questionId, earnCoins);
                        return success;
                    }
                });

            } else {
                llLoader.setVisibility(View.GONE);
                setWrongAnswerLayout();
            }
        }
    }

    public void getWalletBalance() {
        Wallet.getWalletBalance(this, (success, error, coins, diamonds, creditedCoins, historyData, historyCount) -> {
            handleApp42Response(success, error, coins);
            if (toolbarDiamonds != null) {
                LocalStorage.putValue(diamonds, LocalStorage.KEY_DIAMONDS);
                toolbarDiamonds.setText(Utility.numberFormat(LocalStorage.getValue(LocalStorage.KEY_DIAMONDS, 0, Long.class)));
            }
            return success;
        });
    }

    public void app42Logout() {
        UserService userService = App42API.buildUserService();
        String appWarpUserSessionId = App42API.getUserSessionId();
        if (appWarpUserSessionId != null && !appWarpUserSessionId.isEmpty()) {
            Log.i("App42", "User is logged in. Session id: " + appWarpUserSessionId);
            userService.logout(appWarpUserSessionId, new App42CallBack() {
                @Override
                public void onSuccess(Object o) {
                    Log.i("App42", "Successfully logged out");
                }

                @Override
                public void onException(Exception e) {
                    Log.e("App42", "Error while performing log out: " + e.getMessage());
                }
            });
        }
    }

    /**
     * Handle earn coin response
     *
     * @param success    True if coins added successfully else false
     * @param error      Error message in case of error
     * @param questionId Question or content id
     * @param coins      Number of coins earned
     */
    private void handleApp42Response(boolean success, @Nullable String error, int questionId, long coins) {
        if (success) {
            setRightAnswerLayout(coins);
            getWalletBalance();

        } else {
            String errorMessage = error == null ? getString(R.string.GENERIC_API_ERROR_MESSAGE) : error;
            AlertUtils.showAlert(getString(R.string.label_error), errorMessage, null, HomeActivity.this, null);
        }
    }

    /**
     * Handle wallet balance
     *
     * @param success True if got response from App42 API else false
     * @param error   Error message if any
     * @param coins   Total number of coins earned
     */
    private void handleApp42Response(boolean success, @Nullable String error, long coins) {
        if (error != null) {
            if (error.equalsIgnoreCase("Not Found")) {
                Log.i(TAG, "Create Wallet");
                createWallet(0);
            }
        } else if (success) {
            walletBalance = success ? coins : 0;
            setWalletBalance(walletBalance);
        }
    }

    private void handleApp42ResponseforSong(boolean success, String error, int postId, long coins, String s) {
        if (success) {
            String textdescribtion = String.format(getString(R.string.label_notificatin_for_won_coin), coins, s);
            Utility.CustomNotification(this, textdescribtion);
            getWalletBalance();
        }
    }

    //------------------------- App42 APIs END -------------------------------------------------------
    private void setWalletBalance(long coins) {
        if (toolbarWalletBalance != null) {
            LocalStorage.putValue(coins, LocalStorage.KEY_WALLET_BALANCE);
            toolbarWalletBalance.setText(Utility.numberFormat(coins));
        }
    }

    private void createWallet(long coins) {
        Wallet.earnCoins(this, Integer.parseInt(LocalStorage.getUserId()), "iTap", Wallet.FLAG_REGISTER, coins, new WalletCallback() {
            @Override
            public boolean onResult(boolean success, @Nullable String error, long coins, long diamonds, long creditedCoins, JSONArray historyData, int historyCount) {
                handleCreateWalletResponse(success, error);
                return success;
            }
        });
    }

    private void handleCreateWalletResponse(boolean success, @Nullable String error) {
        if (success) {
            getWalletBalance();
        } else {
            createWallet(0);
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    public void showAudioSongExpandedView(@NonNull ArrayList<FeedContentData> arrayListFeedContentData, int position, String feed_id, String pageType, String isWatched) {
        isPlayerVisible = true;
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        llExpandableViewLoader.setVisibility(View.VISIBLE);
        nsvNestedScrollView.setVisibility(View.GONE);
        tvSeason.setVisibility(View.GONE);
        audioPlayDelayHandler.postDelayed(() -> playItems(arrayListFeedContentData, position, feed_id, pageType, isWatched), 1200);
    }

    public void showChannelExpandedView(FeedContentData feedContentData, int pageCount, String feedId, String pageType) {
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        llExpandableViewLoader.setVisibility(View.VISIBLE);
        nsvNestedScrollView.setVisibility(View.GONE);
        tvSeason.setVisibility(View.VISIBLE);
        playChannelAPI(feedContentData, pageCount, feedId, pageType);
    }

    public void showSeriesExpandedView(String seriesId, String seasonId, String episodeId, String iswatched) {
        nextPageNo = 1;
        selectedTabIndex = 0;
        //feedContentDataSeries = feedContentData;
        llExpandableViewLoader.setVisibility(View.VISIBLE);
        nsvNestedScrollView.setVisibility(View.GONE);
        tvSeason.setVisibility(View.VISIBLE);
        fetchSeriesAPI(seriesId, seasonId, episodeId, iswatched);
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
    }

    public void showPlaylistExpandedView(String playlistId, String iswatched) {
        nextPageNo = 1;
        selectedTabIndex = 0;
        fetchPlaylist(playlistId, 0, iswatched);
        tvSeason.setVisibility(View.GONE);
        llExpandableViewLoader.setVisibility(View.VISIBLE);
        nsvNestedScrollView.setVisibility(View.GONE);
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
    }

    public void setSeasonData(String seasonId, String episodeId, String iswatched) {

        if (feedContentDataSeries.seasonData != null) {
            seasonId = feedContentDataSeries.seasonData.termId;
            tvSeason.setText(feedContentDataSeries.seasonData.name);
        }
        String seriesId = feedContentDataSeries.postId;
        fetchEpisodeAPI(seriesId, seasonId, episodeId, iswatched);
    }

    private void updateSeriesUI(String seriesId, String seasonId, String episodeId, String seasonName, String iswatched) {
        llExpandableViewLoader.setVisibility(View.VISIBLE);
        nsvNestedScrollView.setVisibility(View.GONE);
        tvSeason.setText(seasonName);
        fetchSeriesAPI(seriesId, seasonId, episodeId, iswatched);

    }

    private void updatePlaylistUI(String playlistId, int position, String iswatched) {
        llExpandableViewLoader.setVisibility(View.VISIBLE);
        nsvNestedScrollView.setVisibility(View.GONE);
        fetchPlaylist(playlistId, position, iswatched);

    }

    private void playChannelAPI(FeedContentData feedContentData, int pageCount, String feedId, String pageType) {
        try {
            Map<String, String> params = new HashMap<>();
            APIRequest apiRequest = new APIRequest(Url.GET_CHANNEL + "&ID=" + feedContentData.postId + "&paged=" + pageCount, Request.Method.GET, params, null, this);
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    handlePlayChannelResponse(response, feedContentData, error, feedId, pageType);
                }
            });
        } catch (Exception e) {

        }
    }

    private void handlePlayChannelResponse(@Nullable String response, FeedContentData feedContentData, @Nullable Exception error, String feedId, String pageType) {
        try {

            if (error != null) {
                // TODO: Handle error condition
            } else {
                if (response != null) {
                    JSONObject jsonObjectResponse = new JSONObject(response);
                    String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                    if (type.equalsIgnoreCase("error")) {
                        String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                        // TODO: Handle error condition
                    } else if (type.equalsIgnoreCase("ok")) {
                        JSONArray jsonArrayContent = jsonObjectResponse.getJSONObject("msg").getJSONArray("contents");
                        JSONObject jsonObjectChannel = jsonObjectResponse.getJSONObject("msg").getJSONObject("channel");
                        String channelName = jsonObjectChannel.has("post_title") ? jsonObjectChannel.getString("post_title") : "";

                        new Thread(() -> {
                            try {
                                arrayListChannelFeedContentData = new ArrayList<>();
                                for (int i = 0; i < jsonArrayContent.length(); i++) {
                                    FeedContentData feedContent = new FeedContentData(jsonArrayContent.getJSONObject(i), feedContentData.imgUrl, channelName, false);
                                    arrayListChannelFeedContentData.add(feedContent);
                                }

                                ArrayList<FeedContentData> arrayListPlaylist = new ArrayList<>();
                                for (FeedContentData feedContent : arrayListChannelFeedContentData) {
                                    if (feedContent.attachmentData == null) {
                                        continue;
                                    }
                                    arrayListPlaylist.add(feedContent);
                                }
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (arrayListPlaylist.size() == 0) {
                                            Toast.makeText(HomeActivity.this, getString(R.string.error_msg_playback), Toast.LENGTH_LONG).show();
                                            llExpandableViewLoader.setVisibility(View.GONE);
                                            return;
                                        }
                                        updateChannelData(feedContentData, arrayListPlaylist, feedId, pageType);
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }).start();
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateChannelData(FeedContentData feedContentData, ArrayList<FeedContentData> arrayListPlaylist, String feedId, String context) {
        playChannel(feedContentData, arrayListPlaylist, 0, feedId, context);
    }

    private void fetchSeriesAPI(String seriesId, String seasonId, String episodeId, String iswatched) {
        currentSeasonId = seasonId;
        try {
            Map<String, String> params = new HashMap<>();
            APIRequest apiRequest = new APIRequest(Url.FETCH_SERIES + "" + seriesId + "&season=" + seasonId, Request.Method.GET, params, null, this);
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    handleSeriesResponse(response, error, seasonId, episodeId, iswatched);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchEpisodeAPI(String seriesId, String seasonId, String episodeId, String iswatched) {
        try {
            Map<String, String> params = new HashMap<>();
            APIRequest apiRequest = new APIRequest(Url.GET_EPISODE_LISTING + "&series=" + seriesId + "&season=" + seasonId + "&page_no=" + nextPageNo, Request.Method.GET, params, null, this);
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    handleEpisodeListResponse(response, error, episodeId, iswatched);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns playlist
     */
    private void fetchPlaylist(String playlistId, int position, String iswatched) {
        try {
            Map<String, String> params = new HashMap<>();
            APIRequest apiRequest = new APIRequest(Url.GET_PLAYLIST_LISTING + "&ID=" + playlistId + "&page_no=" + nextPageNo, Request.Method.GET, params, null, this);
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    handlePlaylistResponse(response, error, playlistId, position, iswatched);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleSeriesResponse(@Nullable String response, @Nullable Exception error, String seasonId, String episodeId, String iswatched) {
        try {
            if (error == null) {
                if (response != null) {
                    JSONObject jsonObjectResponse = new JSONObject(response);
                    String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                    if (type.equalsIgnoreCase("ok")) {
                        try {
                            JSONObject jsonObjectMsg = jsonObjectResponse.getJSONObject("msg");
                            feedContentDataSeries = new FeedContentData(jsonObjectMsg, -1);
                            if (iswatched == "")
                                iswatched = String.valueOf(feedContentDataSeries.iswatchlisted);
                            setSeasonData(seasonId, episodeId, iswatched);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void handleEpisodeListResponse(@Nullable String response, @Nullable Exception error, String episodeId, String iswatched) {
        try {
            if (error != null) {
                //  TODO: Handle error state (Empty State)
                //updateEmptyState(error.getMessage());
            } else {
                if (response != null) {
                    JSONObject jsonObjectResponse = new JSONObject(response);
                    String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                    if (type.equalsIgnoreCase("error")) {
                        String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                        //  TODO: Call showError method
                        //showError(message);
                    } else if (type.equalsIgnoreCase("ok")) {
                        try {
                            JSONObject jsonArrayMsg = jsonObjectResponse.getJSONObject("msg");
                            JSONArray jsonArrayEpisodes = jsonArrayMsg.getJSONArray("episodes");
                            nextPageNo = jsonArrayMsg.has("next_page") ? jsonArrayMsg.getInt("next_page") : 0;
                            totalNumberOfRecords = jsonArrayMsg.has("total") ? jsonArrayMsg.getInt("total") : 0;

                            new Thread(() -> {
                                try {
                                    arrayListEpisodeData = new ArrayList<>();
                                    for (int i = 0; i < jsonArrayEpisodes.length(); i++) {
                                        FeedContentData feedContentData = new FeedContentData(jsonArrayEpisodes.getJSONObject(i), -1);
                                        arrayListEpisodeData.add(feedContentData);
                                    }
                                    new Handler(Looper.getMainLooper()).post(() -> setEpisodeData(arrayListEpisodeData, feedContentDataSeries.postId, episodeId, iswatched));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }).start();
                        } catch (Exception e) {
                            e.printStackTrace();
                            llExpandableViewLoader.setVisibility(View.GONE);
                            nsvNestedScrollView.setVisibility(View.GONE);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            //  TODO: Handle error state (Empty State)
            //updateEmptyState(e.getMessage());
            llExpandableViewLoader.setVisibility(View.GONE);
            nsvNestedScrollView.setVisibility(View.GONE);
        }
    }

    private void handlePlaylistResponse(@Nullable String response, @Nullable Exception error, String playlistId, int position, String iswatched) {
        // arrayListPlaylist.clear();
        try {
            if (error != null) {
                //  TODO: Handle error state (Empty State)
                //updateEmptyState(error.getMessage());
            } else {
                if (response != null) {
                    JSONObject jsonObjectResponse = new JSONObject(response);
                    String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                    if (type.equalsIgnoreCase("error")) {
                        String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                        //  TODO: Call showError method
                        //showError(message);
                    } else if (type.equalsIgnoreCase("ok")) {
                        try {
                            JSONObject jsonArrayMsg = jsonObjectResponse.getJSONObject("msg");
                            JSONArray jsonArrayContents = jsonArrayMsg.getJSONArray("contents");
                            JSONObject jsonObjectPlaylist = jsonArrayMsg.getJSONObject("playlist");
                            String playlistName = jsonObjectPlaylist.has("post_title") ? jsonObjectPlaylist.getString("post_title") : "";
                            String playlistImageUrl = jsonObjectPlaylist.has("imgUrl") ? jsonObjectPlaylist.getString("imgUrl") : "";
                            nextPageNo = jsonArrayMsg.has("next_page") ? jsonArrayMsg.getInt("next_page") : 0;
                            totalNumberOfRecords = jsonArrayMsg.has("total") ? jsonArrayMsg.getInt("total") : 0;
                            //new Thread(() -> {
                            try {
                                arrayListPlaylist = new ArrayList<>();
                                for (int i = 0; i < jsonArrayContents.length(); i++) {
                                    FeedContentData feedContentData = new FeedContentData(jsonArrayContents.getJSONObject(i), playlistImageUrl, playlistName, true);
                                    arrayListPlaylist.add(feedContentData);
                                }
                                //setPlaylistData(arrayListPlaylist, playlistId, position, iswatched);
                                if (arrayListPlaylist.get(0).ptype == PTYPE) {
                                    Log.d(TAG, "YouTube");
                                    closePlayer(null);
                                    Intent intent = new Intent(HomeActivity.this, YouTubePlayerViewActivity.class);
                                    intent.putExtra(YouTubePlayerViewActivity.CONTENT_DATA, arrayListPlaylist.get(0));
                                    startActivity(intent);
                                    /*llBottomPlayerView.setVisibility(View.VISIBLE);
                                    llExpandableViewLoader.setVisibility(View.GONE);
                                    nsvNestedScrollView.setVisibility(View.GONE);
                                    llMainBottomPlayerView.setVisibility(View.GONE);
                                    mainMediaFrame.setVisibility(View.GONE);
                                    //llMainBottomPlayerView.setVisibility(View.GONE);
                                    expandedPlayer.setVisibility(View.GONE);
                                    //closePlayer(null);
                                    youTubePlayerView.setVisibility(View.VISIBLE);

                                    youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                                        @Override
                                        public void onReady(@NotNull YouTubePlayer youTubePlayer) {
                                            super.onReady(youTubePlayer);
                                            String videoId = arrayListPlaylist.get(0).url;
                                            youTubePlayer.loadVideo("qzcGfN9S_QY", 0);
                                        }
                                    });*/
                                    /*youTubePlayerView.initialize(Constant.YOUTUBE_API_KEY,onInitializedListener);
                                    onInitializedListener=new YouTubePlayer.OnInitializedListener() {
                                        @Override
                                        public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                                            youTubePlayer.loadVideo("HzeK7g8cD0Y");
                                            //youTubePlayer.play();
                                        }

                                        @Override
                                        public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                                            Toast.makeText(getApplicationContext(), "Video player Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    };*/

                                    /*Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(arrayListPlaylist.get(0).url));
                                    startActivity(intent);*/
                                } else {
                                    //isPlayerClosed=false;
                                    Log.i("foo", "isplayer playlist" + isPlayerClosed);
                                    setPlaylistData(arrayListPlaylist, playlistId, position, iswatched);
                                }
                                //new Handler(Looper.getMainLooper()).post(() -> setPlaylistData(arrayListPlaylist, playlistId, position, iswatched));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //}).start();
                        } catch (Exception e) {
                            e.printStackTrace();
                            llExpandableViewLoader.setVisibility(View.GONE);
                            nsvNestedScrollView.setVisibility(View.GONE);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            //  TODO: Handle error state (Empty State)
            //updateEmptyState(e.getMessage());
            llExpandableViewLoader.setVisibility(View.GONE);
            nsvNestedScrollView.setVisibility(View.GONE);
        }
    }

    /**
     * In Expanded Player Screen Display Season,Episode of Series
     */
    /*private void setEpisodeData(ArrayList<SeasonData> arrayListSeasonData, int seriesId, int seasonId, int episodeId) {
        setSeriesUI(arrayListSeasonData, seriesId, seasonId, episodeId);
    }*/
    private void setEpisodeData(ArrayList<FeedContentData> arrayListEpisodeData, String seriesId, String episodeId, String iswatched) {
        btViewMore.setVisibility(totalNumberOfRecords <= buttonViewMoreThreshold ? View.GONE : View.VISIBLE);
        btViewMore.setTag(seriesId);

        if (arrayListEpisodeData.size() == 0) {
            AlertUtils.showToast(getString(R.string.no_media_added_to_the_queue), Toast.LENGTH_LONG, this);
            llExpandableViewLoader.setVisibility(View.GONE);
            nsvNestedScrollView.setVisibility(View.GONE);
            return;
        }

        int selectedEpisodePosition = 0;
        for (int i = 0; i < arrayListEpisodeData.size(); i++) {
            FeedContentData episodeData = arrayListEpisodeData.get(i);
            if (episodeData.postId.equals(episodeId)) {
                selectedEpisodePosition = i;
                break;
            }
        }

        String contextType = !episodeId.equals("0") ? Analyticals.CONTEXT_EPISODE : Analyticals.CONTEXT_SERIES;

        playItems(arrayListEpisodeData, selectedEpisodePosition, seriesId, contextType, iswatched);
    }

    private void setPlaylistData(ArrayList<FeedContentData> arrayListPlaylist, String playlistId, int position, String iswatched) {
        btViewMore.setVisibility(totalNumberOfRecords <= buttonViewMoreThreshold ? View.GONE : View.VISIBLE);
        btViewMore.setTag(playlistId);
        btViewMore.setId(booleanToInt(Boolean.parseBoolean(iswatched)));

        if (arrayListPlaylist.size() == 0) {
            //AlertUtils.showToast("No media added to the queue.", Toast.LENGTH_LONG, this);
            llExpandableViewLoader.setVisibility(View.GONE);
            nsvNestedScrollView.setVisibility(View.GONE);
            closePlayer(null);
            return;
        }
        playItems(arrayListPlaylist, position, playlistId, Analyticals.CONTEXT_PLAYLIST, iswatched);
    }

    /*@SuppressLint("ResourceType")
    public void setTabText() {
        for (int i = 0; i < arrTabSeriesLabels.length; i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
            TextView tvCustomTab = view.findViewById(R.id.tab);
            tvCustomTab.setText(arrTabSeriesLabels[i]);
            tvCustomTab.setId(i);
            tvCustomTab.setTextColor(getResources().getColorStateList(R.drawable.tab_selector));
            tabSeries.getTabAt(i).setCustomView(tvCustomTab);
        }
    }*/

    public void playEpisode(int position, String iswatched) {
        llExpandableViewLoader.setVisibility(View.VISIBLE);
        nsvNestedScrollView.setVisibility(View.GONE);
        playItems(arrayListContentData, position, feedContentDataSeries.postId, Analyticals.CONTEXT_SERIES, iswatched);
    }

    public void playPlaylist(int position, String iswatched, String PlayListID) {
        llExpandableViewLoader.setVisibility(View.VISIBLE);
        nsvNestedScrollView.setVisibility(View.GONE);
        playItems(arrayListContentData, position, PlayListID, Analyticals.CONTEXT_PLAYLIST, iswatched);

    }


    private void getSimilarItemsAPI(String postId) {
      /*  llExpandableViewLoader.setVisibility(View.GONE);
        llLoader.setVisibility(View.GONE);*/
        // TODO: - Testing
        /*try {
            Map<String, String> params = new HashMap<>();
            APIRequest apiRequest = new APIRequest(Url.GET_SIMILAR_ITEMS + "" + postId, Request.Method.GET, params, null, this);
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, (response, error, headers, statusCode) -> {
                Log.e("RESPONSE", response);
                handleSimilarItemResponse(response, error);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }


    private void handleSimilarItemResponse(@Nullable String response, @Nullable Exception error) {
        try {
            if (error != null) {
                //  TODO: Handle error state (Empty State)

            } else {
                ArrayList<FeedContentData> arrayListSimilarItems = new ArrayList<>();
                JSONObject jsonObject = new JSONObject(response);
                String type = jsonObject.getString("type");
                if (type.equals("OK")) {
                    JSONObject jsonObjectMsg = jsonObject.getJSONObject("msg");
                    JSONArray jsonArrayContents = jsonObjectMsg.getJSONArray("contents");


                    new Thread(() -> {
                        try {
                            int songCount = -1;
                            for (int i = 0; i < jsonArrayContents.length(); i++) {
                                JSONObject jsonObjectContent = jsonArrayContents.getJSONObject(i);
                                String postType = jsonObjectContent.has("post_type") ? jsonObjectContent.getString("post_type") : "";
                                if (!postType.equals(FeedContentData.POST_TYPE_ORIGINALS) && !postType.equals(FeedContentData.POST_TYPE_EPISODE)) {
                                    songCount++;
                                } else {
                                    songCount = -1;
                                }
                                FeedContentData feedContentData = new FeedContentData(jsonArrayContents.getJSONObject(i), songCount);
                                //if (!feedContentData.postType.equals(FeedContentData.POST_TYPE_ORIGINALS) &&
                                //       !feedContentData.postType.equals(FeedContentData.POST_TYPE_EPISODE)) {
                                arrayListSimilarItems.add(feedContentData);
                                // }
                            }
                            new Handler(Looper.getMainLooper()).post(() -> updateSimilarItemsUI(arrayListSimilarItems));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }).start();


                    // TODO: update UI (Show horizontal list using RecyclerView)
                }

            }
        } catch (JSONException ignored) {
            llExpandableViewLoader.setVisibility(View.GONE);
        }
    }

    private void updateSimilarItemsUI(ArrayList<FeedContentData> arrayListSimilarItems) {

        llSimilarItems.removeAllViews();
        View viewContent = View.inflate(this, R.layout.row_horizontal_list, null);
        llSimilarItems.addView(viewContent);
        TextView tvTitle = viewContent.findViewById(R.id.tvStoreName);
        RecyclerView rvContent = viewContent.findViewById(R.id.rvContent);
        TextView tvViewAll = viewContent.findViewById(R.id.tvViewAll);
        if (arrayListSimilarItems.size() == 0) {
            llSimilarItems.setVisibility(View.GONE);
            return;
        }

        llSimilarItems.setVisibility(View.VISIBLE);
        tvViewAll.setVisibility(View.GONE);
        tvTitle.setText(getString(R.string.label_similar));
        rvContent.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvContent.setNestedScrollingEnabled(false);
        rvContent.addItemDecoration(new SpacingItemDecoration(Constant.RV_HV_SPACING, SpacingItemDecoration.LEFT));

        KRecyclerViewAdapter adapter = new KRecyclerViewAdapter(this, arrayListSimilarItems, new KRecyclerViewHolderCallBack() {
            @Override
            public KRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_feed, viewGroup, false);
                return new FeedHolder(layoutView, null);
            }
        }, new KRecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClicked(KRecyclerViewHolder kRecyclerViewHolder, Object o, int i) {

                if (o instanceof FeedContentData) {
                    FeedContentData feedContentData = (FeedContentData) o;

                    switch (feedContentData.postType) {
                        case FeedContentData.POST_TYPE_ORIGINALS:
                            //showSeriesExpandedView(Integer.parseInt(feedContentData.postId), feedContentData.seasonId, feedContentData.episodeId);
                            showSeriesExpandedView(feedContentData.postId, feedContentData.seasonId, feedContentData.episodeId, String.valueOf(feedContentData.iswatchlisted));
                            break;
                        case FeedContentData.POST_TYPE_PLAYLIST:
                            showPlaylistExpandedView(feedContentData.postId, String.valueOf(feedContentData.iswatchlisted));
                            break;
                        case FeedContentData.POST_TYPE_POST:
                            int position = feedContentData.songPosition >= 0 ? feedContentData.songPosition : i;
                            //playItems(arrayListSimilarItems, position, arrayListSimilarItems.get(position).postId, Analyticals.CONTEXT_FEED, "");
                            showAudioSongExpandedView(arrayListSimilarItems, position, arrayListSimilarItems.get(position).postId, Analyticals.CONTEXT_FEED, "");
                            break;
                    }
                }
            }
        });

        rvContent.setAdapter(adapter);
        //adapter.notifyDataSetChanged();
    }

    public void showViewMore(View view) {
        if (serviceBound && service != null) {
            service.pauseMediaPlayer();
        }
        Intent i = new Intent(HomeActivity.this, ViewMoreActivity.class);
        i.putExtra(ViewMoreActivity.CONTEXT_TYPE, contextType);

        switch (contextType) {
            case Analyticals.CONTEXT_SERIES:
            case Analyticals.CONTEXT_EPISODE:
                i.putExtra(ViewMoreActivity.POST_TYPE, feedContentDataSeries.postType);
                i.putExtra(ViewMoreActivity.SERIES_ID, feedContentDataSeries.postId);
                i.putExtra(ViewMoreActivity.SEASON_ID, feedContentPlayerData.seasonId);
                i.putExtra(ViewMoreActivity.SERIES_NAME, feedContentDataSeries.postTitle);
                i.putExtra(ViewMoreActivity.SEASON_NAME, feedContentPlayerData.seasonName);
                i.putExtra(ViewMoreActivity.SELECTED_TAB_INDEX, selectedTabIndex);
                i.putExtra(ViewMoreActivity.ISWATCHED, String.valueOf(feedContentDataSeries.iswatchlisted));
                Log.d("MoreData", String.valueOf(feedContentDataSeries.iswatchlisted));
                break;
            case Analyticals.CONTEXT_PLAYLIST:
                String playlistId = (String) view.getTag();
                String iswatched = String.valueOf(IntToboolean(view.getId()));
                i.putExtra(ViewMoreActivity.POST_TYPE, FeedContentData.POST_TYPE_PLAYLIST);
                i.putExtra(ViewMoreActivity.PLAYLIST_ID, playlistId);
                i.putExtra(ViewMoreActivity.ISWATCHED, iswatched);
                i.putExtra(ViewMoreActivity.PLAYLIST_NAME, arrayListContentData.size() > 0 ? arrayListContentData.get(0).playlistName : "");
                break;

        }
        startActivityForResult(i, ViewMoreActivity.REQUEST_CODE);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d("HomeActivity", "config changed");
        super.onConfigurationChanged(newConfig);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        switch (newConfig.orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                closeFullscreenDialog();
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                Log.d("ORIENTATION", "Landscape");
                openFullscreenDialog();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            //System.out.println("@#@");
            fragment.onActivityResult(requestCode, resultCode, data);
        }

        if (requestCode == REQUEST_CODE_UPDATE) {
            if (resultCode != RESULT_OK) {
                FirebaseCrashlytics.getInstance().log("Update flow failed! Result code: " + resultCode);
                // If the update is cancelled
            } else if (resultCode == RESULT_CANCELED) {
                FirebaseCrashlytics.getInstance().log("Update cancelled! Result code: " + resultCode);
            } else if (resultCode == ActivityResult.RESULT_IN_APP_UPDATE_FAILED) {
                FirebaseCrashlytics.getInstance().log("Update failed! Result code: " + resultCode);
            }
        }

        if (resultCode == RESULT_OK) {
            if (requestCode == CommentListActivity.REQUEST_CODE) {
                if (data != null) {
                    tvCommentMusic.setText(String.format(getResources().getString(R.string.label_button_comment_no), data.getStringExtra("comment_count")));
                    tvComment.setText(data.getStringExtra("comment_count"));
                    LocalStorage.setCommented(Integer.parseInt(data.getStringExtra("song_id")), data.getStringExtra("comment_count"), true);
                }
            }
            if (requestCode == WalletActivity.REQUEST_CODE1) {
                if (data != null) {
                    openVoucher();
                }
            }
            if (requestCode == WalletActivity.REQUEST_CODE) {
                if (previousTabId != R.id.bNavHome) {
                    bottomNavigationView.setSelectedItemId(previousTabId);//
                } else {
                    bottomNavigationView.getMenu().getItem(0).setChecked(true);//Home Fragment Add Not Replace
                }
            }
            if (requestCode == BuyDetailActivity.REQUEST_CODE) {
                if (data != null) {
                    try {
                        Bundle bundle = data.getExtras();
                        String actionType = bundle.getString(BuyDetailActivity.KEY_ACTION_TYPE);
                        switch (actionType) {
                            case BuyDetailActivity.VIEW_PURCHASES:
                                bottomNavigationView.setSelectedItemId(R.id.bNavAvatar);
                                break;
                            case BuyDetailActivity.PLAY_NOW:
                                FeedContentData feedContentData = data.getParcelableExtra(BuyDetailActivity.KEY_CONTENT_DATA);
                                String episodeId = data.getStringExtra(BuyDetailActivity.KEY_EPISODE_ID);
                                if (episodeId == null) {
                                    episodeId = "0";
                                }
                                //playPurchasedItem(feedContentData);
                                arrayListFeedContent.clear();
                                arrayListFeedContent.add(feedContentData);
                                Log.e("avinash", "playNowHome");
                                if (feedContentData.contentOrientation.equalsIgnoreCase("vertical") && !Utility.isTelevision()) {
                                    ArrayList<FeedContentData> feedList = new ArrayList<>();
                                    feedList.add(feedContentData);
                                    openVerticalPlayer(feedList, 0);
                                } else {
                                    playMedia(feedContentData, episodeId);
                                }
                                //showAudioSongExpandedView(arrayListFeedContent, feedContentData.feedPosition, feedContentData.postId, Analyticals.CONTEXT_FEED, String.valueOf(feedContentData.iswatchlisted));
                                break;
                            case BuyDetailActivity.SUBSCRIBE:
                                MenuItem item = bottomNavigationView.getMenu().getItem(3);
                                bottomNavigationView.setSelectedItemId(item.getItemId());
                                break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            if (requestCode == MyWatchList.REQUEST_CODE) {
                Log.e("avinash", "MyWatchList.REQUEST_CODE");
                if (data != null) {
                    try {
                        Log.e("avinash", "MyWatchList data != null");
                        Bundle bundle = data.getExtras();
                        String actionType = bundle.getString(MyWatchList.KEY_ACTION_TYPE);
                        String subscribedUser = bundle.getString(MyWatchList.KEY_ACTION_TYPE);
                        switch (actionType) {
                            case MyWatchList.VIEW_PURCHASES:
                                Log.e("Purchases", "actionType actionType VIEW_PURCHASES");
                                bottomNavigationView.setSelectedItemId(R.id.bNavAvatar);
                                break;
                            case MyWatchList.PLAY_NOW:
                                Log.e("Player", "actionType actionType PLAY_NOW");
                                FeedContentData feedContentData = data.getParcelableExtra(MyWatchList.KEY_CONTENT_DATA);
                                playPurchasedItem(feedContentData);
                                break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            if (requestCode == ViewMoreActivity.REQUEST_CODE) {
                if (data != null) {
                    String contextType = data.getStringExtra(ViewMoreActivity.CONTEXT_TYPE);
                    String seasonId = data.getStringExtra(ViewMoreActivity.SEASON_ID);
                    String iswatched = data.getStringExtra(ViewMoreActivity.ISWATCHED);
                    selectedTabIndex = data.getIntExtra(ViewMoreActivity.SELECTED_TAB_INDEX, 0);
                    int position = data.getIntExtra(POSITION, 0);
                    //ArrayList<FeedContentData> arrayList = data.getParcelableExtra(ViewMoreActivity.ARRAY_FEED_CONTENTS);
                    if (data.getExtras() != null) {
                        ArrayList<FeedContentData> arrayList = (ArrayList<FeedContentData>) data.getExtras().get((ViewMoreActivity.ARRAY_FEED_CONTENTS));
                        if (arrayList != null) {
                            arrayListContentData.clear();
                            arrayListContentData.addAll(arrayList);
                            arrayListPlaylist.clear();
                            arrayListPlaylist.addAll(arrayList);
                            adapterEpisode.notifyDataSetChanged();
                        }
                    }

                    switch (contextType) {
                        case Analyticals.CONTEXT_SERIES:
                        case Analyticals.CONTEXT_EPISODE:
                            if (arrayListContentData.size() > position && currentSeasonId.equals(seasonId)) {
                                playEpisode(position, iswatched);
                            } else {
                                currentSeasonId = seasonId;
                                String seriesId = data.getStringExtra(ViewMoreActivity.SERIES_ID);
                                String episodeId = data.getStringExtra(ViewMoreActivity.EPISODE_ID);
                                String seasonName = data.getStringExtra(ViewMoreActivity.SEASON_NAME);
                                int div = position / 10;
                                nextPageNo = div + 1;
                                Log.e("Next Page Number", "" + nextPageNo);
                                //position = position - 10;
                                updateSeriesUI(seriesId, seasonId, episodeId, seasonName, iswatched);
                            }
                            break;
                        case Analyticals.CONTEXT_PLAYLIST:
                            if (arrayListContentData.size() > position) {
                                String playlistId = data.getStringExtra(ViewMoreActivity.PLAYLIST_ID);
                                playPlaylist(position, iswatched, playlistId);
                            } else {
                                String playlistId = data.getStringExtra(ViewMoreActivity.PLAYLIST_ID);
                                int div = position / 10;
                                nextPageNo = div + 1;
                                Log.e("Next Page Number", "" + nextPageNo);
                                position = position - 10;
                                updatePlaylistUI(playlistId, position, iswatched);
                            }
                            break;
                    }
                }
            }
            if (data != null) {
                if (data.hasExtra(WebViewActivity.GAME_REDIRECT_KEY)) {
                    /*if (HomeFragment.tlHomeTabs != null) {
                        HomeFragment.tlHomeTabs.getTabAt(1).select();
                    }*/
                    openFragment(HomePlayTabFragment.newInstance());
                }
            }


            if (requestCode == SM_REQUEST_CODE) {
                boolean isPromoter = false;
                try {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        for (String key : bundle.keySet()) {
                            Log.e(TAG, key + " : " + (bundle.get(key) != null ? bundle.get(key) : "NULL"));
                        }
                    }
                    String respondent = data.getStringExtra(SM_RESPONDENT);

                    if (respondent != null) {
                        Log.d("SM", respondent);
                        JSONObject surveyResponse = new JSONObject(respondent);
                        String completion_status = surveyResponse.getString("completion_status");
                        Log.i("Status", "" + completion_status);
                        addUserSurvey();
                    }
                } catch (JSONException e) {

                }
            } else {
                // Toast t = Toast.makeText(this, getString(R.string.error_prompt), Toast.LENGTH_LONG);
                // t.show();
                //  SMError e = (SMError) data.getSerializableExtra(SM_ERROR);
                //Log.d("SM-ERROR", e.getDescription());
            }
        }
    }

    private void playPurchasedItem(FeedContentData feedContentData) {
        if (!feedContentData.postType.equals("")) {
            switch (feedContentData.postType) {
                case FeedContentData.POST_TYPE_ORIGINALS:
                    showSeriesExpandedView(feedContentData.postId, "0", "0", String.valueOf(feedContentData.iswatchlisted));
                    break;
                case FeedContentData.POST_TYPE_PLAYLIST:
                    showPlaylistExpandedView(feedContentData.postId, String.valueOf(feedContentData.iswatchlisted));
                    break;
                case FeedContentData.POST_TYPE_POST:
                    //getSingleMediaItem(feedContentData.postId);
                    arrayListFeedContent.clear();
                    arrayListFeedContent.add(feedContentData);
                    homeActivity.showAudioSongExpandedView(arrayListFeedContent, feedContentData.feedPosition, feedContentData.postId, Analyticals.CONTEXT_FEED, String.valueOf(feedContentData.iswatchlisted));
                    break;
            }
        } else {
            switch (feedContentData.taxonomy) {
                case FeedContentData.TAXONOMY_SEASONS:
                    showSeriesExpandedView(feedContentData.seriesId, feedContentData.seasonId, feedContentData.episodeId, "");
                    break;
            }
        }
    }

    public void getSingleMediaItem(String id) {
        progressBar.setVisibility(View.VISIBLE);
        try {
            Map<String, String> params = new HashMap<>();
            APIRequest apiRequest = new APIRequest(Url.GET_SINGLE_MEDIA_ITEM + "&ID=" + id, Request.Method.GET, params, null, this);
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, new APIResponse() {

                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    progressBar.setVisibility(View.GONE);
                    handleSingleMediaItem(response, error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleSingleMediaItem(@Nullable String response, @Nullable Exception error) {
        try {
            if (error != null) {
            } else {
                if (response != null) {
                    JSONObject jsonObjectResponse = new JSONObject(response);
                    String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                    if (type.equalsIgnoreCase("error")) {
                        String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                        showError(message);
                    } else if (type.equalsIgnoreCase("ok")) {
                        try {
                            JSONObject jsonObjectMsg = jsonObjectResponse.getJSONObject("msg");
                            FeedContentData feedContentData = new FeedContentData(jsonObjectMsg, 0);

                            setSinglePostData(feedContentData, Analyticals.CONTEXT_FEED);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setSinglePostData(FeedContentData feedContentData, String contextName) {
        ArrayList<FeedContentData> arrayListFeedContent = new ArrayList<>();
        arrayListFeedContent.add(feedContentData);
        String id = feedContentData.postId;
        //playItems(arrayListFeedContent, 0, id, contextName, String.valueOf(feedContentData.iswatchlisted));
        showAudioSongExpandedView(arrayListFeedContent, 0, id, contextName, String.valueOf(feedContentData.iswatchlisted));
    }

    private void addBonusPoints() {
        Log.i("HomeActivity", "Openapp");
        try {
            Map<String, String> params = new HashMap<>();
            APIRequest apiRequest = new APIRequest(Url.OPEN_APP_BONUS, Request.Method.GET, params, null, this);
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, (response, error, headers, statusCode) -> {
//                Log.e("RESPONSE", response);
                handleBonusResponse(response, error);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleBonusResponse(@Nullable String response, @Nullable Exception error) {
        try {
            if (error != null) {
                getWalletBalance();
            } else {
                if (response != null) {
                    JSONObject jsonObjectResponse = new JSONObject(response);
                    String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                    if (type.equalsIgnoreCase("error")) {
                        getWalletBalance();
                    } else if (type.equalsIgnoreCase("ok")) {
                        JSONObject jsonObjectMsg = jsonObjectResponse.has("msg") ? jsonObjectResponse.getJSONObject("msg") : null;
                        if (jsonObjectMsg != null) {
                            Long walletBalance = jsonObjectMsg.has("coins") ? jsonObjectMsg.getLong("coins") : 0;
                            if (walletBalance != 0) {
                                setWalletBalance(walletBalance);
                                return;
                            }
                        }
                        getWalletBalance();
                    }
                }

            }
        } catch (JSONException e) {

        }
    }

    /**
     * This method shows error alert
     *
     * @param errorMessage message to be displayed in alert dialog
     */
    private void showError(@Nullable String errorMessage) {
        if (errorMessage == null) errorMessage = APIManager.GENERIC_API_ERROR_MESSAGE;
        AlertUtils.showAlert(getString(R.string.label_alert), errorMessage, null, this, null);
    }

    private void setThumbnail() {
        try {
            try {
                Glide.with(mContext).asBitmap().load(feedContentPlayerData.imgUrl).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        if (playerPagerAdapter != null) {
                            playerPagerAdapter.updateArtWork(resource);
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
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
                            toolbarearncoins_button.setVisibility(View.GONE);
                        } else {
                            JSONObject jsonObject = new JSONObject(response);
                            String type = jsonObject.getString("type");
                            if (type.equals("OK")) {
                                JSONObject msg = jsonObject.getJSONObject("msg");
                                boolean status = msg.has("status") ? msg.getBoolean("status") : false;
                                if (status) {
                                    if (LocalStorage.getSurveyHash() != null) {
                                        if (!LocalStorage.getSurveyHash().isEmpty()) {
                                            toolbarearncoins_button.setVisibility(View.VISIBLE);
                                        } else {
                                            toolbarearncoins_button.setVisibility(View.GONE);
                                        }
                                    } else {
                                        toolbarearncoins_button.setVisibility(View.GONE);
                                    }
                                } else {
                                    toolbarearncoins_button.setVisibility(View.GONE);
                                }
                            } else if (type.equals("ERROR")) {
                                toolbarearncoins_button.setVisibility(View.GONE);
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

    public void checkUserValidity() {
        String userId = LocalStorage.getUserId();
        try {
            Map<String, String> params = new HashMap<>();
            params.put("userId", userId);
            APIRequest apiRequest = new APIRequest(Url.EARN_COINS_CODE, Request.Method.POST, params, null, this);
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    handleSurveyStatusResponse(response, error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleSurveyStatusResponse(String response, Exception error) {
        try {
            if (error != null) {
                //  TODO: Handle error state (Empty State)
            } else {
                JSONObject jsonObject = new JSONObject(response);
                String type = jsonObject.getString("type");
                if (type.equals("OK")) {
                    JSONObject msg = jsonObject.getJSONObject("msg");
                    Log.i("msg", "" + msg);
                    String title = msg.has("title") ? msg.getString("title") : null;
                    boolean status = msg.has("status") ? msg.getBoolean("status") : false;
                    if (status) {
                        s.startSMFeedbackActivityForResult(this, SM_REQUEST_CODE, LocalStorage.getSurveyHash());
                    } else {

                    }
                } else if (type.equals("ERROR")) {
                    String msg = jsonObject.getString("msg");
                    Log.i("msg", "" + msg);
                    showSurveyErrorDialog(HomeActivity.this, msg);
                }
            }
        } catch (Exception ignored) {

        }
    }

    private void addUserSurvey() {
        String userId = LocalStorage.getUserId();
        try {
            Map<String, String> params = new HashMap<>();
            params.put("userId", userId);
            params.put("status", "" + 1);
            APIRequest apiRequest = new APIRequest(Url.ADD_USER_SURVEY, Request.Method.POST, params, null, this);
            apiRequest.showLoader = true;
            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    getWalletBalance();
                    handleAddSurveyResponse(response, error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleAddSurveyResponse(String response, Exception error) {
        try {
            if (error != null) {
                //  TODO: Handle error state (Empty State)
            } else {
                JSONObject jsonObject = new JSONObject(response);
                String type = jsonObject.getString("type");
                if (type.equals("OK")) {
                    JSONObject msg = jsonObject.getJSONObject("msg");
                    Log.i("msg", "" + msg);
                    String message = msg.has("message") ? msg.getString("message") : null;
                    toolbarearncoins_button.setVisibility(View.GONE);
                    checkUserValidityForSurvey();
                    showSurveySuccessDialog(HomeActivity.this, message);
                }
            }
        } catch (Exception ignored) {

        }
    }

    private void showSurveySuccessDialog(Context context, String message) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_survey_success, null);
        ImageView iv_go = view.findViewById(R.id.iv_got_it);
        TextView tv_success_message = view.findViewById(R.id.tv_success_message);
        Dialog alertDialogBuilder = new Dialog(context);
        alertDialogBuilder.setContentView(view);
        alertDialogBuilder.show();
        tv_success_message.setText(message);
        iv_go.setOnClickListener(v -> {
            alertDialogBuilder.dismiss();
        });
    }

    private void showSurveyErrorDialog(Context context, String message) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_survey_error, null);
        ImageView iv_go = view.findViewById(R.id.iv_got_it);
        TextView tv_error_message = view.findViewById(R.id.tv_error_message);
        Dialog alertDialogBuilder = new Dialog(context);
        alertDialogBuilder.setContentView(view);
        alertDialogBuilder.show();
        tv_error_message.setText(message);
        iv_go.setOnClickListener(v -> {
            alertDialogBuilder.dismiss();
        });
    }

    public void setLayoutVideo(boolean vertical) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (vertical) {
                    final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
                    int pixels = (int) (600 * scale + 0.5f);
                    // Gets the layout params that will allow you to resize the layout
                    ViewGroup.LayoutParams params = mainMediaFrame.getLayoutParams();
                    ViewGroup.LayoutParams param = ivAd.getLayoutParams();
                    // Changes the height and width to the specified *pixels*
                    params.height = pixels;
                    params.width = LayoutParams.MATCH_PARENT;
                    param.height = pixels;
                    param.width = LayoutParams.MATCH_PARENT;
                    mainMediaFrame.setLayoutParams(params);
                    ivAd.setLayoutParams(param);
                } else {
                    final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
                    int pixels = (int) (250 * scale + 0.5f);
                    // Gets the layout params that will allow you to resize the layout
                    ViewGroup.LayoutParams params = mainMediaFrame.getLayoutParams();
                    ViewGroup.LayoutParams param = ivAd.getLayoutParams();
                    // Changes the height and width to the specified *pixels*
                    params.height = pixels;
                    params.width = LayoutParams.MATCH_PARENT;
                    param.height = pixels;
                    param.width = LayoutParams.MATCH_PARENT;
                    mainMediaFrame.setLayoutParams(params);
                    ivAd.setLayoutParams(param);
                }
            }
        });
    }

    public void playMedia(FeedContentData feedContentData, String episodeId) {
        if (feedContentData != null) {
            String id = feedContentData.postId;

            switch (feedContentData.postType) {
                case FeedContentData.POST_TYPE_ORIGINALS:
                    //showSeriesExpandedView(feedContentData.postId, "0", episodeId, "false");
                    openFragment(BuyDetailFragment.getInstance(feedContentData, "sliderPageKey"));
                    break;
                case FeedContentData.POST_TYPE_SEASON:
                    showSeriesExpandedView(feedContentData.seriesId, feedContentData.seasonId, episodeId, "false");
                    break;
                case POST_TYPE_EPISODE:
                    showSeriesExpandedView(feedContentData.seriesId, feedContentData.seasonId, episodeId, "false");
                    break;
                case FeedContentData.POST_TYPE_PLAYLIST:
                    showPlaylistExpandedView(feedContentData.postId, "false");
                    break;
                case FeedContentData.POST_TYPE_POST:
                    getSingleMediaItem(id);
                    break;
            }
        }
    }

    public void getPurchaseHistory() {
        try {
            Map<String, String> params = new HashMap<>();
            APIRequest apiRequest = new APIRequest(Url.GET_PURCHASE_HISTORY + purchaseNextPageNo,
                    Request.Method.GET, params, null, this);
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, new APIResponse() {

                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    progressBar.setVisibility(View.GONE);
                    handlePurchaseHistory(response, error);

                }
            });
        } catch (Exception e) {
            progressBar.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }

    private void handlePurchaseHistory(@Nullable String response, @Nullable Exception error) {
        try {
            if (error != null) {
                // updateEmptyState(error.getMessage());
            } else {
                if (response != null) {
                    JSONObject jsonObjectResponse = new JSONObject(response);
                    String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                    if (type.equalsIgnoreCase("error")) {
                        String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                        // showError(message);
                    } else if (type.equalsIgnoreCase("ok")) {
                        try {
                            JSONObject jsonArrayMsg = jsonObjectResponse.getJSONObject("msg");
                            JSONArray jsonArrayOrders = jsonArrayMsg.getJSONArray("orders");
                            purchaseNextPageNo = jsonArrayMsg.has("next_page") ? jsonArrayMsg.getInt("next_page") : 0;
                            arrayListPurchaseData.clear();
                            if (jsonArrayOrders.length() > 0) {
                                try {
                                    for (int i = 0; i < jsonArrayOrders.length(); i++) {
                                        PurchaseData purchaseData = new PurchaseData(jsonArrayOrders.getJSONObject(i));
                                        arrayListPurchaseData.add(purchaseData.itemId);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            LocalStorage.savePurchasedArrayList(arrayListPurchaseData, "savedPurchased", this);
                            arrayListPlaylistData.clear();
                            for (int i = 0; i < arrayListPurchaseData.size(); i++) {
                                // getPostData(arrayListPurchaseData.get(i));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getPostData(String id) {
        progressBar.setVisibility(View.VISIBLE);
        try {
            Map<String, String> params = new HashMap<>();
            APIRequest apiRequest = new APIRequest(Url.GET_SINGLE_MEDIA_ITEM + "&ID=" + id, Request.Method.GET, params, null, this);
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, new APIResponse() {

                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    progressBar.setVisibility(View.GONE);
                    try {
                        if (error != null) {
                        } else {
                            if (response != null) {
                                JSONObject jsonObjectResponse = new JSONObject(response);
                                String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                                if (type.equalsIgnoreCase("error")) {
                                    String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                                    showError(message);
                                } else if (type.equalsIgnoreCase("ok")) {
                                    try {
                                        JSONObject jsonObjectMsg = jsonObjectResponse.getJSONObject("msg");
                                        FeedContentData feedContentData = new FeedContentData(jsonObjectMsg, 0);
                                        arrayListPlaylistData.add(feedContentData);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void makePlaylist(@NonNull ArrayList<FeedContentData> mediaList, int position,
                             String feed_id, String pageType, String iswatched,
                             boolean play, boolean playAd) {
        playlist.clear();
        purchasedList = LocalStorage.getPurchasedArrayList("savedPurchased", getApplicationContext());
        Log.i(TAG, "" + purchasedList);
        for (int i = 0; i < mediaList.size(); i++) {
            if (!purchasedList.isEmpty()) {
                if (purchasedList.contains(mediaList.get(i).postId) && mediaList.get(i).attachmentData == null) {
                    for (int j = 0; j < arrayListPlaylistData.size(); j++) {
                        if (arrayListPlaylistData.get(j).postId.equals(mediaList.get(i).postId)) {
                            playlist.add(arrayListPlaylistData.get(j));
                        }
                    }
                } else if (mediaList.get(i).attachmentData != null) {
                    playlist.add(mediaList.get(i));
                }
            } else if (mediaList.get(i).attachmentData != null) {
                playlist.add(mediaList.get(i));
            }
        }

        Log.i(TAG, "" + playlist);
        if (!playlist.isEmpty()) {
            //playItems(playlist, position, feed_id, pageType, iswatched);
            for (int i = 0; i < playlist.size(); i++) {
                if (playlist.get(i).contentType.equalsIgnoreCase(Constant.eSportsTitle)) {
                    playlist.remove(i);
                    i--;
                }
            }

            for (int i = 0; i < playlist.size(); i++) {
                if (playlist.get(i).postId.equals(mediaList.get(position).postId)) {
                    position = i;
                }
            }
            playMediaItems(playlist, position, feed_id, pageType, iswatched, true, true);
        }
    }

    public void updatePurchasedList(ArrayList<FeedContentData> itemsToPlay, int position, String feedId, String pageType) {
        purchasedList.clear();
        arrayListPlaylistData.clear();
        purchasedList = LocalStorage.getPurchasedArrayList("savedPurchased", getApplicationContext());
        for (int i = 0; i < purchasedList.size(); i++) {
            getPostData(purchasedList.get(i), itemsToPlay, position, feedId, Analyticals.CONTEXT_FEED);
        }
    }

    public void updatePlayItems(ArrayList<FeedContentData> itemsToPlay, int position, String feedId, String pageType) {
        arrayListPlaylistData.clear();
        for (int i = 0; i < itemsToPlay.size(); i++) {
            if (itemsToPlay.get(i).attachmentData == null) {
                getPostData(itemsToPlay.get(i).postId, itemsToPlay, position, feedId, Analyticals.CONTEXT_FEED);
            } else {
                arrayListPlaylistData.add(itemsToPlay.get(i));
            }
        }
    }

    private void getPostData(String id, ArrayList<FeedContentData> itemsToPlay, int position, String feedId, String pageType) {
        progressBar.setVisibility(View.VISIBLE);
        try {
            Map<String, String> params = new HashMap<>();
            APIRequest apiRequest = new APIRequest(Url.GET_SINGLE_MEDIA_ITEM + "&ID=" + id, Request.Method.GET, params, null, this);
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, new APIResponse() {

                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    progressBar.setVisibility(View.GONE);
                    try {
                        if (error != null) {
                        } else {
                            if (response != null) {
                                JSONObject jsonObjectResponse = new JSONObject(response);
                                String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                                if (type.equalsIgnoreCase("error")) {
                                    String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                                    showError(message);
                                } else if (type.equalsIgnoreCase("ok")) {
                                    try {
                                        JSONObject jsonObjectMsg = jsonObjectResponse.getJSONObject("msg");
                                        FeedContentData feedContentData = new FeedContentData(jsonObjectMsg, 0);
                                        arrayListPlaylistData.add(feedContentData);
                                        playPurchasedItems(itemsToPlay, position, feedId, Analyticals.CONTEXT_FEED);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playPurchasedItems(ArrayList<FeedContentData> itemsToPlay, int position, String feedId, String pageType) {
        switch (itemsToPlay.get(position).postType) {
            case POST_TYPE_PLAYLIST:
                showPlaylistExpandedView(itemsToPlay.get(position).postId, String.valueOf(itemsToPlay.get(position).iswatchlisted));
                break;
            case POST_TYPE_ORIGINALS:
                showSeriesExpandedView(itemsToPlay.get(position).postId, "0", "0", String.valueOf(itemsToPlay.get(position).iswatchlisted));
                break;
            case POST_TYPE_EPISODE:
                showSeriesExpandedView(itemsToPlay.get(position).seriesId, itemsToPlay.get(position).seasonId, itemsToPlay.get(position).episodeId, "");
                break;
            case POST_TYPE_STREAM:
                showChannelExpandedView(itemsToPlay.get(position), 1, feedId, pageType);
                break;
            case POST_TYPE_POST:
                //homeActivity.makePlaylist(itemsToPlay, position, feedId, pageType, "");
                playItems(itemsToPlay, position, feedId, pageType, "");
                break;
        }
    }

    public void showAdsInAppNotification() {
        String userId = LocalStorage.getUserId();
        Map<String, String> params = new HashMap<>();
        params.put("userId", userId);
        APIRequest apiRequest = new APIRequest(Url.GET_IN_APP_NOTIFICATION, Request.Method.POST, params, null, this);
        apiRequest.showLoader = false;
        APIManager.request(apiRequest, (response, error, headers, statusCode) -> {
            if (error == null && response != null) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int id = 0;
                    String title = "", offerMessage = "", link = "", imageurl = "", buttonText = "";
                    String type = jsonObject.getString("type");
                    if (type.equals("OK")) {
                        JSONObject msg = jsonObject.getJSONObject("msg");
                        com.app.itaptv.utils.Log.i("msg", "" + msg);
                        boolean status = msg.has("status") ? msg.getBoolean("status") : false;
                        if (status) {
                            JSONObject message = msg.has("message") ? msg.getJSONObject("message") : null;
                            id = message.has("user_notification_id") ? message.getInt("user_notification_id") : 0;
                            title = message.has("title") ? message.getString("title") : null;
                            offerMessage = message.has("message") ? message.getString("message") : null;
                            link = message.has("link") ? message.getString("link") : null;
                            imageurl = message.has("image") ? message.getString("image") : null;
                            buttonText = message.has("button_text") ? message.getString("button_text") : null;
                        }

                        if (!offerMessage.isEmpty() && !link.isEmpty() && !imageurl.isEmpty() && !buttonText.isEmpty()) {
                            showInAppNotificationDialog(id, offerMessage, link, imageurl, buttonText, title);
                            //callReadNotificationAPi(id);
                        } else {
                            if (serviceBound && service != null) {
                                service.resumeMediaPlayer();
                            }
                        }
                    }
                } catch (JSONException ignored) {

                }
            } else {
                if (serviceBound && service != null) {
                    service.resumeMediaPlayer();
                }
            }
        });
    }

    public void showInAppNotificationDialog(int id, String offerMessage, String link, String imageurl, String buttonText, String title) {
        LayoutInflater inflater = (LayoutInflater) HomeActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_inapp_notification, null);

        RelativeLayout relativeLayout = view.findViewById(R.id.rl_ad_holder);
        RoundedImageView iv_offer_image = view.findViewById(R.id.iv_offer_image);
        ImageView iv_close = view.findViewById(R.id.iv_close);
        TextView tv_offer_message = view.findViewById(R.id.tv_offer_message);
        Button bt_offer = view.findViewById(R.id.bt_offer);

        Glide.with(HomeActivity.this)
                .load(imageurl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(iv_offer_image);
        tv_offer_message.setText(offerMessage);
        bt_offer.setText(buttonText);

        Dialog alertDialogBuilder = new Dialog(HomeActivity.this);
        alertDialogBuilder.setContentView(view);
        alertDialogBuilder.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialogBuilder.show();

        ViewTreeObserver vto = relativeLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                relativeLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                int width = relativeLayout.getMeasuredWidth();
                relativeLayout.getLayoutParams().height = width;
                relativeLayout.requestLayout();

            }
        });

        iv_close.setOnClickListener(v -> {
            alertDialogBuilder.dismiss();
            if (serviceBound && service != null) {
                service.resumeMediaPlayer();
            }
        });

        bt_offer.setOnClickListener(v -> {
            alertDialogBuilder.dismiss();
            //Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            //startActivity(browserIntent);
            if (link.startsWith("http")) {
                callUpdateClickedNotificationApi(id);
                startActivity(new Intent(HomeActivity.this, BrowserActivity.class).putExtra("title", title).putExtra("posturl", link));
            }
        });
    }

    public void resumePlayer() {
        if (serviceBound && service != null) {
            service.resumeMediaPlayer();
        }
    }

    public void hideKeyboard() {
        // Check if no view has focus:
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void showBannerAd(String id) {
        String adType = Utility.getBannerAdType(id, HomeActivity.this);
        if (!adType.isEmpty()) {
            if (adType.equalsIgnoreCase(Constant.ADMOB)) {
                mAdView.loadAd(adRequest);
                mAdView.setVisibility(View.VISIBLE);
                clAdHolder.setVisibility(View.GONE);
            } else if (adType.equalsIgnoreCase(Constant.CUSTOM)) {
                mAdView.setVisibility(View.GONE);
                showCustomAd(id);
            }
        }
    }

    public void showCustomAd(String id) {
        List<AdMobData> list = LocalStorage.getBannerAdMobList(LocalStorage.KEY_BANNER_AD_MOB, HomeActivity.this);
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).id.equals(id)) {
                    if (list.get(i).feedContentObjectData != null) {
                        long sec = (Long.parseLong(list.get(i).feedContentObjectData.skippableInSecs)) * 1000;
                        if (list.get(i).feedContentObjectData.mediaType.equalsIgnoreCase(FeedContentData.MEDIA_TYPE_IMAGE)) {
                            if (ivCustomAd != null && pvCustomAd != null) {
                                clAdHolder.setVisibility(View.VISIBLE);
                                String url = list.get(i).feedContentObjectData.adFieldsData.attachment;
                                Glide.with(HomeActivity.this)
                                        .load(url)
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .into(ivCustomAd);
                                ivCustomAd.setVisibility(View.VISIBLE);
                                pvCustomAd.setVisibility(View.GONE);
                                ivVolumeOff.setVisibility(View.GONE);
                                ivVolumeUp.setVisibility(View.GONE);
                                APIMethods.addEvent(this, Constant.VIEW, list.get(i).feedContentObjectData.postId, Constant.BANNER, id);

                              /*  if (list.get(i).feedContentObjectData.skippable) {
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            ivClose.setVisibility(View.VISIBLE);
                                        }
                                    }, sec);
                                } else {
                                    ivClose.setVisibility(View.INVISIBLE);
                                }*/

                            }
                        } /*else if (list.get(i).feedContentObjectData.mediaType.equalsIgnoreCase(FeedContentData.MEDIA_TYPE_VIDEO)) {
                            playVideoAd(list.get(i).feedContentObjectData.adFieldsData.attachment);
                            pvCustomAd.setVisibility(View.VISIBLE);
                            ivVolumeOff.setVisibility(View.VISIBLE);
                            ivVolumeUp.setVisibility(View.GONE);
                            ivCustomAd.setVisibility(View.GONE);
                            APIMethods.addEvent(getContext(),Constant.VIEW,list.get(i).feedContentObjectData.postId);*/
                    }
                }
                ivClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clAdHolder.setVisibility(View.GONE);
                        closePlayer();

                    }
                });
                int finalI = i;
                ivCustomAd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setActionCustomAd(list.get(finalI).feedContentObjectData, Constant.BANNER, id);
                    }
                });
                pvCustomAd.getVideoSurfaceView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setActionCustomAd(list.get(finalI).feedContentObjectData, Constant.BANNER, id);
                    }
                });
                ivVolumeUp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ivVolumeUp.setVisibility(View.GONE);
                        ivVolumeOff.setVisibility(View.VISIBLE);
                        if (player != null) {
                            player.setVolume(0.0f);
                        }
                    }
                });
                ivVolumeOff.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ivVolumeOff.setVisibility(View.GONE);
                        ivVolumeUp.setVisibility(View.VISIBLE);
                        if (player != null) {
                            player.setVolume(1.0f);
                        }
                    }
                });
            }
        }
    }

    private void setActionCustomAd(FeedContentData feedContentData, String location, String subLocation) {
        APIMethods.addEvent(this, Constant.CLICK, feedContentData.postId, location, subLocation);
        switch (feedContentData.adFieldsData.adType) {
            case FeedContentData.AD_TYPE_IN_APP:
                if (feedContentData.adFieldsData.contentType.equalsIgnoreCase(Constant.PAGES)) {
                    redirectToPage(feedContentData.adFieldsData.pageType);
                } else {
                    switch (feedContentData.adFieldsData.contentType) {
                        case FeedContentData.CONTENT_TYPE_ESPORTS:
                            getUserRegisteredTournamentInfo(String.valueOf(feedContentData.adFieldsData.contentId));
                            break;
                        default:
                            getMediaData(String.valueOf(feedContentData.adFieldsData.contentId), feedContentData.adFieldsData.contentType);
                    }
                }
                break;
            case FeedContentData.AD_TYPE_EXTERNAL:
                setActionOnAd(feedContentData);
                break;
        }
    }

    private void setActionOnAd(FeedContentData feedContentData) {
        switch (feedContentData.adFieldsData.urlType) {
            case Constant.BROWSER:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(feedContentData.adFieldsData.externalUrl));
                startActivity(browserIntent);
                break;
            case Constant.WEBVIEW:
                startActivity(new Intent(HomeActivity.this, BrowserActivity.class).putExtra("title", "").putExtra("posturl", feedContentData.adFieldsData.externalUrl));
                break;
        }
    }

    private void playVideoAd(String url) {
        pvCustomAd.setUseController(false);
        pvCustomAd.requestFocus();

        Uri uri = Uri.parse(url);
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(HomeActivity.this, getApplicationContext().getPackageName(), bandwidthMeter);
        MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(uri));

        player = new ExoPlayer.Builder(HomeActivity.this).build();
        player.addListener(new Player.Listener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == Player.STATE_ENDED) {
                    player.seekTo(0);
                }
            }

            @Override
            public void onPlayerError(PlaybackException error) {

            }
        });
        player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT);
        player.prepare(mediaSource);
        player.setPlayWhenReady(true);

        /*
          Volume is set to 0 as per requirement
          Use as per the case
         */
        player.setVolume(0.0f);

        pvCustomAd.setPlayer(player);
    }

    private void closePlayer() {
        if (player != null) {
            player.setPlayWhenReady(false);
            player.stop();
            player.release();
            player = null;
        }
        if (pvCustomAd != null) {
            pvCustomAd.setPlayer(null);
        }
    }

    public void openWeb(@NonNull AdFieldsData adFieldsData) {
        switch (adFieldsData.urlType) {
            case Constant.EXTERNAL:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(adFieldsData.externalUrl));
                mContext.startActivity(browserIntent);
                break;
            case Constant.WEBVIEW:
                mContext.startActivity(new Intent(mContext, BrowserActivity.class).putExtra("title", "").putExtra("posturl", adFieldsData.externalUrl));
                break;
        }
    }

    public void openVerticalPlayer(ArrayList<FeedContentData> videoItems, int position) {
        videoId.clear();
        videoViewArrayList.clear();
        textViewArrayList.clear();
        imageViewArrayList.clear();
        verticalVideoList.clear();
        closePlayer(null);

        verticalVideoList.addAll(videoItems);
        if (position > 0) {
            for (int i = position - 1; i >= 0; i--) {
                verticalVideoList.remove(i);
            }
        }
        LicenseManager.getInstance().downloadLicences(videoItems, licences -> {
            try {
                videosViewPager.setVisibility(View.VISIBLE);
                videosAdapter = new VideosAdapter(verticalVideoList, this, position);
                videosViewPager.setAdapter(videosAdapter);
                videosViewPager.setOffscreenPageLimit(5);

                videosViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                        super.onPageScrolled(position, positionOffset, positionOffsetPixels);

                    }

                    @Override
                    public void onPageSelected(int position) {
                        super.onPageSelected(position);
                        AnalyticsTracker.stopDurationVideo();
                        for (int i = 0; i < videoViewArrayList.size(); i++) {
                            if (i != position) {
                                videoViewArrayList.get(i).pause();
                                imageViewArrayList.get(i).setVisibility(View.VISIBLE);
                            }
                        }
                        if (position < videoViewArrayList.size()) {
                            videoViewArrayList.get(position).start();
                            videoViewArrayList.get(position).seekTo(0);
                            imageViewArrayList.get(position).setVisibility(View.GONE);
                            callAddActivityAPI(verticalVideoList.get(position).postId, Analyticals.CONTEXT_FEED, String.valueOf(verticalVideoList.get(position).id), verticalVideoList.get(position).postTitle);
                            AnalyticsTracker.feedContentData = verticalVideoList.get(position);
                            AnalyticsTracker.secondsPlayedVideo = 0;
                            AnalyticsTracker.startDurationVideo();
                        }
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                        super.onPageScrollStateChanged(state);
                    }
                });
            } catch (IndexOutOfBoundsException e) {
                Log.d("IndexOutOfBounds from playing media: ", e.getMessage());
            }
        }, this);

        // new Handler(Looper.getMainLooper()).post(() -> videosViewPager.setCurrentItem(position));
        ;
    }

    void handleData(String type, String postId) {
        if (type.equalsIgnoreCase(POST_TYPE_PLAYLIST) || type.equalsIgnoreCase(POST_TYPE_ORIGINALS) ||
                type.equalsIgnoreCase(POST_TYPE_EPISODE) || type.equalsIgnoreCase(POST_TYPE_STREAM) ||
                type.equalsIgnoreCase(POST_TYPE_POST) || type.equalsIgnoreCase(POST_TYPE_AD) || type.equalsIgnoreCase(GAME)) {
            if (postId != null && !postId.isEmpty()) {
                getMediaData(postId, type);
            }
        } else if (type.equalsIgnoreCase(Constant.ACTIVITY_SHOP)) {
            new Handler(Looper.getMainLooper()).post(() -> bottomNavigationView.setSelectedItemId(R.id.bNavShop));
        } else if (type.equalsIgnoreCase(Constant.ACTIVITY_AVATAR)) {
            new Handler(Looper.getMainLooper()).post(() -> bottomNavigationView.setSelectedItemId(R.id.bNavAvatar));
        } else if (type.equalsIgnoreCase(Constant.ACTIVITY_PREMIUM)) {
            new Handler(Looper.getMainLooper()).post(() -> bottomNavigationView.setSelectedItemId(R.id.bNavSubscribe));
        } else if (type.equalsIgnoreCase(Constant.ACTIVITY_SUBSCRIPTION)) {
            startActivity(new Intent(this, PremiumActivity.class).putExtra("title", getResources().getString(R.string.label_premium)));
        } else if (type.equalsIgnoreCase(Constant.ACTIVITY_GAME)) {
            new Handler(Looper.getMainLooper()).post(() -> openFragment(HomePlayTabFragment.newInstance()));
        }
    }

    public void getMediaData(String id, String type) {
        try {
            Map<String, String> params = new HashMap<>();
            APIRequest apiRequest = new APIRequest(Url.GET_POST_BY_ID + "&ID=" + id, Request.Method.GET, params, null, this);
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    handlePostResponse(response, error, type);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void handlePostResponse(@Nullable String response, @Nullable Exception error, String feedType) {
        try {
            if (error != null) {
                // showError(error.getMessage());
            } else {
                if (response != null) {
                    JSONObject jsonObjectResponse = new JSONObject(response);
                    String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                    if (type.equalsIgnoreCase("error")) {
                        String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                        showError(message);
                    } else if (type.equalsIgnoreCase("ok")) {
                        try {
                            JSONObject jsonObjectMsg = jsonObjectResponse.getJSONObject("msg");
                            if (feedType.equalsIgnoreCase(GAME)) {
                                GameData gameData = new GameData(jsonObjectMsg, "");
                                handleGameData(gameData);
                            } else {
                                FeedContentData feedContentData = new FeedContentData(jsonObjectMsg, 0);
                                playItems(feedContentData, String.valueOf(feedContentData.id), Analyticals.CONTEXT_FEED);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (JSONException e) {

        }
    }

    private void playItems(FeedContentData itemToPlay, String feedId, String pageType) {
        SubscriptionDetails sd = LocalStorage.getUserSubscriptionDetails();
        if (sd != null && sd.allow_rental != null) {
            if (sd.allow_rental.equalsIgnoreCase(Constant.YES)) {
                if (itemToPlay.postType.equals(POST_TYPE_ORIGINALS)) {
                   /* Intent intent = new Intent(this, BuyDetailActivity.class);
                    intent.putExtra(BuyDetailActivity.CONTENT_DATA, itemToPlay);
                    intent.putExtra(BuyDetailActivity.FROM_SLIDER, "sliderPageKey");
                    startActivityForResult(intent, BuyDetailActivity.REQUEST_CODE);*/
                    openFragment(BuyDetailFragment.getInstance(itemToPlay, "sliderPageKey"));
                } else {
                    playContent(itemToPlay, feedId, pageType);
                }
            } else {
                if (itemToPlay.postExcerpt.equalsIgnoreCase("free")) {
                    playContent(itemToPlay, feedId, pageType);
                } else if (itemToPlay.postExcerpt.equalsIgnoreCase("paid")) {
                    if (itemToPlay.can_i_use) {
                        playContent(itemToPlay, feedId, pageType);
                    } else {
                        if (itemToPlay.postType.equalsIgnoreCase(POST_TYPE_EPISODE)) {
                            LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View view = inflater.inflate(R.layout.dialog_subscribe, null);
                            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this)
                                    .setView(view);
                            alertDialog = builder.create();
                            alertDialog.setCancelable(false);
                            alertDialog.show();

                            TextView tvTitle = alertDialog.findViewById(R.id.tv_message);
                            Button btPositive = alertDialog.findViewById(R.id.btSubscribe);
                            tvTitle.setText(getString(R.string.this_content_is_exclusive_to_premium_members));

                            btPositive.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivity(new Intent(HomeActivity.this, PremiumActivity.class).putExtra("title", getResources().getString(R.string.label_premium)));
                                    alertDialog.dismiss();
                                }
                            });
                        } else {
                           /* Intent intent = new Intent(this, BuyDetailActivity.class);
                            intent.putExtra(BuyDetailActivity.CONTENT_DATA, itemToPlay);
                            startActivityForResult(intent, BuyDetailActivity.REQUEST_CODE);*/
                            openFragment(BuyDetailFragment.getInstance(itemToPlay, "sliderPageKey"));
                        }
                    }
                }
            }
        } else {
            if (itemToPlay.postExcerpt.equalsIgnoreCase("free")) {
                playContent(itemToPlay, feedId, pageType);
            } else if (itemToPlay.postExcerpt.equalsIgnoreCase("paid")) {
                if (itemToPlay.can_i_use) {
                    playContent(itemToPlay, feedId, pageType);
                } else {
                    if (itemToPlay.postType.equalsIgnoreCase(POST_TYPE_EPISODE)) {
                        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View view = inflater.inflate(R.layout.dialog_subscribe, null);
                        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this)
                                .setView(view);
                        alertDialog = builder.create();
                        alertDialog.setCancelable(false);
                        alertDialog.show();

                        TextView tvTitle = alertDialog.findViewById(R.id.tv_message);
                        Button btPositive = alertDialog.findViewById(R.id.btSubscribe);
                        tvTitle.setText(getString(R.string.this_content_is_exclusive_to_premium_members));

                        btPositive.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(HomeActivity.this, PremiumActivity.class).putExtra("title", getResources().getString(R.string.label_premium)));
                                alertDialog.dismiss();
                            }
                        });
                    } else {
                       /* Intent intent = new Intent(this, BuyDetailActivity.class);
                        intent.putExtra(BuyDetailActivity.CONTENT_DATA, itemToPlay);
                        startActivityForResult(intent, BuyDetailActivity.REQUEST_CODE);*/
                        openFragment(BuyDetailFragment.getInstance(itemToPlay, "sliderPageKey"));
                    }
                }
            }
        }
    }

    void handleGameData(GameData gameData) {
        switch (gameData.quizType) {
            case GameData.QUIZE_TYPE_LIVE:
                String invalidDateMessage = GameDateValidation.getInvalidDateMsg(this, gameData.start, gameData.end);
                if (invalidDateMessage.equals("")) {
                    if (gameData.playedGame) {
                        AlertUtils.showToast(getString(R.string.msg_game_played), Toast.LENGTH_SHORT, this);
                        return;
                    }
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(GameStartActivity.GAME_DATA, gameData);
                    startActivityForResult(new Intent(this, GameStartActivity.class).putExtra("Bundle", bundle), GameData.REQUEST_CODE_GAME);
                } else {
                    if (gameData.winnersDeclared) {
                        startActivity(new Intent(this, LuckyWinnerActivity.class).putExtra(LuckyWinnerActivity.KEY_GAME_ID, gameData.id));
                        return;
                    }
                    AlertUtils.showToast(invalidDateMessage, Toast.LENGTH_SHORT, this);
                }
                break;

            case GameData.QUIZE_TYPE_TURN_BASED: {
                Bundle bundle = new Bundle();
                bundle.putParcelable(GameStartActivity.GAME_DATA, gameData);
                startActivityForResult(new Intent(this, GameStartActivity.class).putExtra("Bundle", bundle), GameData.REQUEST_CODE_GAME);
            }
            break;
            case GameData.QUIZE_TYPE_HUNTER_GAMES: {
                Intent intent = new Intent(this, WebViewActivity.class).putExtra(WebViewActivity.GAME_URL, gameData.webviewUrl).putExtra(WebViewActivity.GAME_TITLE, gameData.postTitle);
                startActivityForResult(intent, WebViewActivity.GAME_REDIRECT_REQUEST_CODE);
            }
            break;
        }
    }

    void playContent(FeedContentData itemToPlay, String feedId, String pageType) {
        if (itemToPlay.contentOrientation.equalsIgnoreCase("vertical") && !Utility.isTelevision()) {
            ArrayList<FeedContentData> list = new ArrayList<>();
            list.add(itemToPlay);
            openVerticalPlayer(list, 0);
        } else {
            switch (itemToPlay.postType) {
                case POST_TYPE_PLAYLIST:
                    showPlaylistExpandedView(itemToPlay.postId, String.valueOf(itemToPlay.iswatchlisted));
                    break;
                case POST_TYPE_ORIGINALS:
                    //showSeriesExpandedView(itemToPlay.postId, "0", "0", String.valueOf(itemToPlay.iswatchlisted));
                    openFragment(BuyDetailFragment.getInstance(itemToPlay, "sliderPageKey"));
                    break;
                case POST_TYPE_EPISODE:
                    showSeriesExpandedView(itemToPlay.seriesId, itemToPlay.seasonId, itemToPlay.episodeId, "");
                    break;
                case POST_TYPE_STREAM:
                    showChannelExpandedView(itemToPlay, 1, feedId, pageType);
                    break;
                case POST_TYPE_POST:
                    setSinglePostData(itemToPlay, Analyticals.CONTEXT_FEED);
                    break;
                case POST_TYPE_AD:
                    setSinglePostData(itemToPlay, Analyticals.CONTEXT_FEED);
                    break;
            }
        }
    }

    public class PlayerPagerAdapter extends PagerAdapter {

        public View mediaPlayerLayout/*, adPlayerLayout*/;
        public View mediaControls, adControls;
        public PlayerView mediaPlayer/*, adPlayer*/, verticalMediaPlayer;
        public DefaultTimeBar mediaPlayerTimeBar, mediaPlayerTimeBar2;
        public ProgressBar adProgressBar, verticalPlayerProgressBar;
        public ImageView exo_fullscreen_icon, exo_play, exo_pause;
        public Button btPlayGame;
        TextView durationTV, positionTV;
        TextView playerItemTitleLbl;
        TextView adInfoLbl, tvSkipAd;
        TextView tvVideoTitle, tvVideoDescription;
        RelativeLayout rlMediaPlayerLayout;
        ConstraintLayout clVerticalMediaPlayerLayout;
        Button rewind, fForward;
        private Context mContext;

        PlayerPagerAdapter(Context context) {
            mContext = context;
        }

        @SuppressLint("ClickableViewAccessibility")
        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup collection, int position) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            //if (position == 0) {
            mediaPlayerLayout = inflater.inflate(R.layout.layout_media_player, collection, false);
            mediaPlayer = mediaPlayerLayout.findViewById(R.id.mediaPlayerView);
            verticalMediaPlayer = mediaPlayerLayout.findViewById(R.id.verticalMediaPlayerView);
            mediaPlayer.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
            verticalMediaPlayer.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);

            ivAd = mediaPlayerLayout.findViewById(R.id.ivAd);
            adInfoLbl = mediaPlayerLayout.findViewById(R.id.adInfoLbl);
            tvSkipAd = mediaPlayerLayout.findViewById(R.id.tvSkipAd);

            mediaPlayerTimeBar = mediaPlayerLayout.findViewById(R.id.mediaPlayerTimeBar);

            View playerControllerLayout = mediaPlayerLayout.findViewById(R.id.playerControllerLayout);
            mediaPlayerTimeBar2 = playerControllerLayout.findViewById(R.id.exo_progress);
            if (mediaPlayerTimeBar2 == null)
                mediaPlayerTimeBar2 = mediaPlayerLayout.findViewById(R.id.exo_progress);

            exo_fullscreen_icon = mediaPlayerLayout.findViewById(R.id.exo_fullscreen_icon);
            exo_play = mediaPlayerLayout.findViewById(R.id.exo_play);
            exo_pause = mediaPlayerLayout.findViewById(R.id.exo_pause);
            btPlayGame = mediaPlayerLayout.findViewById(R.id.btPlayGame);
            playerItemTitleLbl = mediaPlayerLayout.findViewById(R.id.tvTitle);

            mediaControls = mediaPlayerLayout.findViewById(R.id.rlMediaControllerLayout);
            adControls = mediaPlayerLayout.findViewById(R.id.adPlayerControlsLayout);

            durationTV = mediaPlayerLayout.findViewById(R.id.exo_duration);
            positionTV = mediaPlayerLayout.findViewById(R.id.exo_position);

            adProgressBar = mediaPlayerLayout.findViewById(R.id.adPlayerProgressBar);

            rlMediaPlayerLayout = mediaPlayerLayout.findViewById(R.id.mediaPlayerLayout);
            clVerticalMediaPlayerLayout = mediaPlayerLayout.findViewById(R.id.verticalMediaPlayerLayout);

            tvVideoTitle = mediaPlayerLayout.findViewById(R.id.tvVideoTitle);
            tvVideoDescription = mediaPlayerLayout.findViewById(R.id.tvVideoDescription);
            tvLike = mediaPlayerLayout.findViewById(R.id.tvLike);
            tvComment = mediaPlayerLayout.findViewById(R.id.tvComment);
            ivLike = mediaPlayerLayout.findViewById(R.id.ivLike);
            tvAddtoWatch = mediaPlayerLayout.findViewById(R.id.tvAddtoWatchlist);
            rewind = mediaPlayerLayout.findViewById(R.id.mediaPlayerRewind);
            fForward = mediaPlayerLayout.findViewById(R.id.mediaPlayerFForward);
            verticalPlayerProgressBar = mediaPlayerLayout.findViewById(R.id.verticalPlayerProgressBar);

            tvSkipAd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkPlayerVisibility();
                    resumePlayer();
                }
            });

           /* rewind.setOnTouchListener(new View.OnTouchListener() {
                GestureDetector gestureDetector = new GestureDetector(HomeActivity.this, new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        if (service != null) {
                            service.setRewindForward(service.getREWIND_PLAYBACK(), service.getVERTICALMILLISECONDS());
                        }
                        return super.onDoubleTap(e);
                    }
                });

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    gestureDetector.onTouchEvent(event);
                    return false;
                }
            });*/

          /*  fForward.setOnTouchListener(new View.OnTouchListener() {
                GestureDetector gestureDetector = new GestureDetector(HomeActivity.this, new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        if (service != null) {
                            service.setRewindForward(service.getFORWARD_PLAYBACK(), service.getVERTICALMILLISECONDS());
                        }
                        return super.onDoubleTap(e);
                    }
                });

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    gestureDetector.onTouchEvent(event);
                    return false;
                }
            });*/

          /*  verticalMediaPlayer.setOnTouchListener(new View.OnTouchListener() {
                // Minimal x and y axis swipe distance.
                private int MIN_SWIPE_DISTANCE_Y = 100;
                // Maximal x and y axis swipe distance.
                private int MAX_SWIPE_DISTANCE_Y = 1000;
                GestureDetector gestureDetector = new GestureDetector(HomeActivity.this, new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                        // Get swipe delta value in y axis.
                        float deltaY = e1.getY() - e2.getY();
                        // Get absolute value.
                        float deltaYAbs = Math.abs(deltaY);
                        // Only when swipe distance between minimal and maximal distance value then we treat it as effective swipe
                        if ((deltaYAbs >= MIN_SWIPE_DISTANCE_Y) && (deltaYAbs <= MAX_SWIPE_DISTANCE_Y)) {
                            if (deltaY > 0) {
                                if (service != null) {
                                    service.setPreviousNext(service.PLAYER_ACTION_NEXT);
                                }
                            } else {
                                if (service != null) {
                                    service.setPreviousNext(service.PLAYER_ACTION_PREVIOUS);
                                }
                            }
                        }
                        return super.onFling(e1, e2, velocityX, velocityY);
                    }
                });

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    gestureDetector.onTouchEvent(event);
                    return false;
                }
            });*/

            mediaPlayer.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View view, int i, KeyEvent keyEvent) {
                    Log.i("", "");
                    if (i == KeyEvent.KEYCODE_DPAD_CENTER) {
                        if (service != null) {
                            if (service.isPlaying()) {
                                mediaPlayer.showController();
                                if (exo_play.getVisibility() == View.VISIBLE) {
                                    exo_play.requestFocus();
                                } else {
                                    exo_pause.requestFocus();
                                }
                            }
                        }
                    }
                    return false;
                }
            });

            mediaPlayer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i("", "");
                }
            });

            mediaPlayer.findViewById(R.id.exo_prev).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (service != null) {
                        if (service.getPlayer() != null) {
                            service.logEvent();
                            service.getPlayer().seekToPreviousMediaItem();
                        }
                    }
                }
            });
            mediaPlayer.findViewById(R.id.exo_next).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (service != null) {
                        if (service.getPlayer() != null) {
                            service.logEvent();
                            service.getPlayer().seekToNextMediaItem();
                        }
                    }
                }
            });

            configureSeekBar();

            collection.addView(mediaPlayerLayout);
            return mediaPlayerLayout;
            /*} else {
                adPlayerLayout = inflater.inflate(R.layout.layout_ad_player, collection, false);
                //adPlayer = adPlayerLayout.findViewById(R.id.adPlayerView);
                adProgressBar = adPlayerLayout.findViewById(R.id.adPlayerProgressBar);
                collection.addView(adPlayerLayout);
                return adPlayerLayout;
            }*/
        }

        @Override
        public void destroyItem(@NonNull ViewGroup collection, int position, @NonNull Object view) {
            collection.removeView((View) view);
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "";
        }

        /*void configureAdPlayer() {
            if (adPlayer != null) {
                adPlayer.setControllerShowTimeoutMs(0);
                adPlayer.setControllerHideOnTouch(false);
                adPlayer.showController();
            }
        }*/

        void setVisibilityListener() {
            if (mediaPlayer == null) return;
            mediaPlayer.setControllerVisibilityListener(visibility -> {
                if (ibclose != null) {
                    if (mFullScreenDialog.isShowing()) {
                        ibclose.setVisibility(View.INVISIBLE);
                    } else {
                        ibclose.setVisibility(visibility);
                    }
                }
                if (visibility == View.VISIBLE) {
                    // Show thumb
                    if (mediaPlayerTimeBar != null) {
                        mediaPlayerTimeBar.setScrubberColor(ContextCompat.getColor(mContext, R.color.colorAccent));
                    }
                } else {
                    // hide thumb
                    if (mediaPlayerTimeBar != null) {
                        mediaPlayerTimeBar.setScrubberColor(Color.TRANSPARENT);
                    }
                }
            });
        }

        void updatePlayers(boolean forceMediaPlayer) {
            if (serviceBound && service != null) {
                if (mediaPlayer != null && service.getCurrentItem() != null) {
                    if (service.getCurrentItem() != null) {
                        if (service.getCurrentItem().singleCredit) {
                            mediaControls.setVisibility(View.GONE);
                            if (!isAppBackground) {
                                service.resumeMediaPlayer();
                            } else {
                                service.pauseMediaPlayer();
                            }
                        } else {
                            mediaControls.setVisibility(View.VISIBLE);
                        }
                    }
                    if (forceMediaPlayer) {
                        llExpandableViewLoader.setVisibility(View.GONE);
                        nsvNestedScrollView.setVisibility(View.GONE);
                        if (service.isPlayingCreditAd() && service.isPlaying()) {
                            showCreditAdPlayerUI();
                        } else {
                            showMediaPlayerUI();
                        }
                        return;
                    }
                    if (service.isPlayingCreditAd() && service.isPlaying()) {
                        llExpandableViewLoader.setVisibility(View.GONE);
                        nsvNestedScrollView.setVisibility(View.GONE);
                        showCreditAdPlayerUI();
                    }
                    if (!service.isPlayingCreditAd() && service.isPlaying()) {
                        llExpandableViewLoader.setVisibility(View.GONE);
                        nsvNestedScrollView.setVisibility(View.GONE);
                        showMediaPlayerUI();
                    }
                    if (service.isPlayingVideoAd()) {
                        llExpandableViewLoader.setVisibility(View.GONE);
                        nsvNestedScrollView.setVisibility(View.GONE);
                        showVideoAdPlayerUI();
                    }
                    if (service.isPlayingImageAd()) {
                        llExpandableViewLoader.setVisibility(View.GONE);
                        nsvNestedScrollView.setVisibility(View.GONE);
                        showImageAdUI();
                    }
                }
                /*if (adPlayer != null) {
                    adPlayer.setPlayer(service.getAdPlayer());
                }*/
            }
        }

        private void showMediaPlayerUI() {
            if (service.getCurrentItem().contentOrientation.equalsIgnoreCase("vertical") && !Utility.isTelevision()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
                        // Gets the layout params that will allow you to resize the layout
                        ViewGroup.LayoutParams params = mainMediaFrame.getLayoutParams();
                        // Changes the height and width to the specified *pixels*
                        params.height = LayoutParams.MATCH_PARENT;
                        params.width = LayoutParams.MATCH_PARENT;
                        mainMediaFrame.setLayoutParams(params);

                    }
                });

                rlMediaPlayerLayout.setVisibility(View.GONE);
                clVerticalMediaPlayerLayout.setVisibility(View.VISIBLE);

                verticalMediaPlayer.setPlayer(service.getPlayer());
                updateSeekBar();

                tvVideoTitle.setText(service.getCurrentItem().postTitle);
                tvVideoDescription.setText(service.getCurrentItem().description);
                ivLike.setTag(service.getCurrentItem().postId);
                tvAddtoWatch.setTag(service.getCurrentItem().postId);
            } else {
                // Set player
                rlMediaPlayerLayout.setVisibility(View.VISIBLE);
                clVerticalMediaPlayerLayout.setVisibility(View.GONE);

                mediaPlayer.setPlayer(service.getPlayer());

                // Stop ad
                service.cleanUpAdPlayer();

                ivAd.setVisibility(View.GONE);
                adInfoLbl.setVisibility(View.GONE);
                tvSkipAd.setVisibility(View.GONE);
                ibPlay.setVisibility(View.VISIBLE);

                // Set media controls
                mediaControls.setVisibility(View.VISIBLE);
                adControls.setVisibility(View.GONE);
                adProgressBar.setVisibility(View.GONE);
                if (!mFullScreenDialog.isShowing()) {
                    mediaPlayerTimeBar.setVisibility(View.VISIBLE);
                    mediaPlayerTimeBar2.setVisibility(View.INVISIBLE);

                } else {
                    mediaPlayerTimeBar.setVisibility(View.GONE);
                    mediaPlayerTimeBar2.setVisibility(View.VISIBLE);

                    if (btPlayGame != null) {
                        if (arrayListQuestionData != null && !arrayListQuestionData.isEmpty()) {
                            if (!Utility.isTelevision()) {
                                btPlayGame.setVisibility(View.VISIBLE);
                            }
                        } else {
                            btPlayGame.setVisibility(View.GONE);
                        }
                    }
                }
                if (!Utility.isTelevision()) {
                    exo_fullscreen_icon.setVisibility(View.VISIBLE);
                } else {
                    exo_fullscreen_icon.setVisibility(View.GONE);
                }

                // Set controls configuration
                mediaPlayer.setControllerShowTimeoutMs(5000);
                mediaPlayer.setControllerHideOnTouch(true);
                //mediaPlayer.showController();

                // Change duration and position bg
                durationTV.setBackgroundColor(Color.TRANSPARENT);
                positionTV.setBackgroundColor(Color.TRANSPARENT);

                if (service.getCurrentItem().contentOrientation.equalsIgnoreCase("vertical") && !Utility.isTelevision()) {
                    Log.i(TAG, "Vertical video");
                    contentOrientation = service.getCurrentItem().contentOrientation;
                    homeActivity.setLayoutVideo(true);
                } else {
                    Log.i(TAG, "Not Vertical video");
                    contentOrientation = service.getCurrentItem().contentOrientation;
                    homeActivity.setLayoutVideo(false);
                }
            }
        }

        private void showCreditAdPlayerUI() {
            // Set ad player
            mediaPlayer.setPlayer(service.getPlayer());

            ivAd.setVisibility(View.GONE);
            adInfoLbl.setVisibility(View.VISIBLE);
            tvSkipAd.setVisibility(View.GONE);
            ibPlay.setVisibility(View.GONE);

            // Set media controls
            mediaControls.setVisibility(View.GONE);
            adControls.setVisibility(View.VISIBLE);
            adProgressBar.setVisibility(View.VISIBLE);
            if (!mFullScreenDialog.isShowing()) {
                mediaPlayerTimeBar.setVisibility(View.GONE);
                mediaPlayerTimeBar2.setVisibility(View.GONE);

            } else {
                mediaPlayerTimeBar.setVisibility(View.GONE);
                mediaPlayerTimeBar2.setVisibility(View.GONE);

                if (btPlayGame != null) {
                    if (Utility.isTelevision()) {
                        btPlayGame.setVisibility(View.GONE);
                    } else {
                        if (arrayListQuestionData != null && !arrayListQuestionData.isEmpty()) {
                            if (!Utility.isTelevision()) {
                                btPlayGame.setVisibility(View.VISIBLE);
                            }
                        } else {
                            btPlayGame.setVisibility(View.GONE);
                        }
                    }
                }
            }
            if (!Utility.isTelevision()) {
                exo_fullscreen_icon.setVisibility(View.VISIBLE);
            } else {
                exo_fullscreen_icon.setVisibility(View.GONE);
            }
            //updateAdProgressBarMargin();

            // Set controls configuration
            mediaPlayer.setControllerShowTimeoutMs(0);
            mediaPlayer.setControllerHideOnTouch(false);

            // Change duration and position bg
            durationTV.setBackgroundColor(ContextCompat
                    .getColor(HomeActivity.this, R.color.black_transparent));
            positionTV.setBackgroundColor(ContextCompat
                    .getColor(HomeActivity.this, R.color.black_transparent));

            if (service.getCurrentItem().contentOrientation.equalsIgnoreCase("vertical") && !Utility.isTelevision()) {
                Log.i(TAG, "Vertical video");
                contentOrientation = service.getCurrentItem().contentOrientation;
                homeActivity.setLayoutVideo(true);
            } else {
                Log.i(TAG, "Not Vertical video");
                contentOrientation = service.getCurrentItem().contentOrientation;
                homeActivity.setLayoutVideo(false);
            }
        }

        private void showVideoAdPlayerUI() {
            // Set ad player
            mediaPlayer.setPlayer(service.getAdPlayer());

            // Stop playing
            service.pauseMediaPlayer();

            ivAd.setVisibility(View.GONE);
            adInfoLbl.setVisibility(View.VISIBLE);
            tvSkipAd.setVisibility(View.GONE);
            ibPlay.setVisibility(View.GONE);

            mediaPlayer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (service != null) {
                        if (service.isPlayingVideoAd()) {
                            if (service.getCurrentItem() != null && service.getAdIndex() >= 0) {
                                setActionCustomAd(service.getCurrentItem().adsArray.get(service.getAdIndex()), Constant.INVIDEO, service.getCurrentItem().postTitle);
                            }
                        }
                    }
                }
            });

            if (service.getCurrentItem() != null && service.getAdIndex() >= 0) {
                if (service.getCurrentItem().adsArray.get(service.getAdIndex()).skippable) {
                    long sec = (Long.parseLong(service.getCurrentItem().adsArray.get(service.getAdIndex()).skippableInSecs)) * 1000;
                    tvSkipAd.setVisibility(View.VISIBLE);
                    new CountDownTimer(sec, 1000) {

                        @Override
                        public void onTick(long millisUntilFinished) {
                            int progress = (int) (millisUntilFinished / 1000);
                            if (progress > 0) {
                                tvSkipAd.setText(Integer.toString(progress));
                            } else {
                                tvSkipAd.setText(getString(R.string.skip_ad));
                            }
                        }

                        @Override
                        public void onFinish() {
                            tvSkipAd.setText(getString(R.string.skip_ad));
                        }
                    }.start();
                    /*new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tvSkipAd.setVisibility(View.VISIBLE);
                        }
                    }, sec);*/
                }

                if (service.getCurrentItem().adsArray.get(service.getAdIndex()).contentOrientation.equalsIgnoreCase("vertical") && !Utility.isTelevision()) {
                    Log.i(TAG, "Vertical video");
                    contentOrientation = service.getCurrentItem().contentOrientation;
                    homeActivity.setLayoutVideo(true);
                } else {
                    Log.i(TAG, "Not Vertical video");
                    contentOrientation = service.getCurrentItem().contentOrientation;
                    homeActivity.setLayoutVideo(false);
                }
            }

            // Set media controls
            mediaControls.setVisibility(View.GONE);
            adControls.setVisibility(View.VISIBLE);
            adProgressBar.setVisibility(View.VISIBLE);
            mediaPlayerTimeBar.setVisibility(mFullScreenDialog.isShowing() ? View.GONE : View.INVISIBLE);
            mediaPlayerTimeBar2.setVisibility(View.INVISIBLE);
            exo_fullscreen_icon.setVisibility(View.GONE);
            if (btPlayGame != null) {
                btPlayGame.setVisibility(View.GONE);
            }
            updateAdProgressBarMargin();

            // Set controls configuration
            mediaPlayer.setControllerShowTimeoutMs(0);
            mediaPlayer.setControllerHideOnTouch(false);
            mediaPlayer.showController();

            // Change duration and position bg
            durationTV.setBackgroundColor(ContextCompat
                    .getColor(HomeActivity.this, R.color.black_transparent));
            positionTV.setBackgroundColor(ContextCompat
                    .getColor(HomeActivity.this, R.color.black_transparent));
        }

        private void showImageAdUI() {
            // Stop playing
            service.pauseMediaPlayer();
            //ivAd=service.getAdImage();
            //mediaPlayer.setVisibility(View.GONE);
            ivAd.setVisibility(View.VISIBLE);
            adInfoLbl.setVisibility(View.VISIBLE);
            ibPlay.setVisibility(View.GONE);

            ivAd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (service.getCurrentItem() != null && service.getAdIndex() >= 0) {
                        setActionCustomAd(service.getCurrentItem().adsArray.get(service.getAdIndex()), Constant.INVIDEO, service.getCurrentItem().postTitle);
                    }
                }
            });

            if (service.getCurrentItem() != null) {
                if (service.getCurrentItem().adFieldsData != null) {
                    Glide.with(HomeActivity.this)
                            .load(service.getCurrentItem().adFieldsData.attachment)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(ivAd);
                }
            }

            tvSkipAd.setVisibility(View.GONE);

            if (service.getCurrentItem() != null && service.getAdIndex() >= 0) {
                if (service.getCurrentItem().adsArray.get(service.getAdIndex()).skippable) {
                    tvSkipAd.setVisibility(View.VISIBLE);
                    long sec = (Long.parseLong(service.getCurrentItem().adsArray.get(service.getAdIndex()).skippableInSecs)) * 1000;
                    new CountDownTimer(sec, 1000) {

                        @Override
                        public void onTick(long millisUntilFinished) {
                            int progress = (int) (millisUntilFinished / 1000);
                            if (progress > 0) {
                                tvSkipAd.setText(Integer.toString(progress));
                            } else {
                                tvSkipAd.setText(getString(R.string.skip_ad));
                            }
                        }

                        @Override
                        public void onFinish() {
                            tvSkipAd.setText(getString(R.string.skip_ad));
                        }
                    }.start();
                   /* new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tvSkipAd.setVisibility(View.VISIBLE);
                        }
                    }, sec);*/
                } else {
                    if (service.getCurrentItem().mediaType.equalsIgnoreCase(FeedContentData.MEDIA_TYPE_IMAGE)) {
                        tvSkipAd.setVisibility(View.VISIBLE);
                    }
                }

                if (service.getCurrentItem().adsArray.get(service.getAdIndex()).contentOrientation.equalsIgnoreCase("vertical") && !Utility.isTelevision()) {
                    Log.i(TAG, "Vertical video");
                    contentOrientation = service.getCurrentItem().contentOrientation;
                    homeActivity.setLayoutVideo(true);
                } else {
                    Log.i(TAG, "Not Vertical video");
                    contentOrientation = service.getCurrentItem().contentOrientation;
                    homeActivity.setLayoutVideo(false);
                }
            }

            // Set media controls
            mediaControls.setVisibility(View.GONE);
            adControls.setVisibility(View.VISIBLE);
            adProgressBar.setVisibility(View.INVISIBLE);
            mediaPlayerTimeBar.setVisibility(View.INVISIBLE);
            mediaPlayerTimeBar2.setVisibility(View.INVISIBLE);
            exo_fullscreen_icon.setVisibility(View.GONE);
            if (btPlayGame != null) {
                btPlayGame.setVisibility(View.GONE);
            }
        }

        void resetPlayers() {
            if (mediaPlayer != null) {
                mediaPlayer.setPlayer(null);
            }
            /*if (adPlayer != null) {
                adPlayer.setPlayer(null);
            }*/
        }

        void updateArtWork(@NonNull Bitmap resource) {
            if (mediaPlayer != null) {
                try {
                    Drawable d = new BitmapDrawable(getResources(), resource);
                    mediaPlayer.setDefaultArtwork(d);
                } catch (Exception e) {
                    //mediaPlayer.setDefaultArtwork(resource);
                }
            }
        }

        void updateSeekBar() {
            if (serviceBound && service != null) {
                if (service.getAdPlayer() != null && (service.isPlayingAd())) {
                    if (adProgressBar == null) return;
                    int total = (int) service.getAdPlayer().getDuration();
                    int buffered = (int) service.getAdPlayer().getBufferedPosition();
                    int played = (int) service.getAdPlayer().getCurrentPosition();
                    adProgressBar.setMax(total);
                    adProgressBar.setSecondaryProgress(buffered);
                    adProgressBar.setProgress(played);
                } else {
                    if (service.getPlayer() != null) {
                        if (service.isPlayingCreditAd()) {
                            if (adProgressBar == null) return;
                            int total = (int) service.getPlayer().getDuration();
                            int buffered = (int) service.getPlayer().getBufferedPosition();
                            int played = (int) service.getPlayer().getCurrentPosition();
                            adProgressBar.setMax(total);
                            adProgressBar.setSecondaryProgress(buffered);
                            adProgressBar.setProgress(played);
                        } else {
                            if (service.getCurrentItem().contentOrientation.equalsIgnoreCase("vertical") && !Utility.isTelevision()) {
                                if (verticalPlayerProgressBar == null) return;
                                int total = (int) service.getPlayer().getDuration();
                                int buffered = (int) service.getPlayer().getBufferedPosition();
                                int played = (int) service.getPlayer().getCurrentPosition();
                                verticalPlayerProgressBar.setMax(total);
                                verticalPlayerProgressBar.setSecondaryProgress(buffered);
                                verticalPlayerProgressBar.setProgress(played);
                            } else {
                                if (mediaPlayerTimeBar == null) return;
                                mediaPlayerTimeBar.setDuration(service.getPlayer().getDuration());
                                mediaPlayerTimeBar.setBufferedPosition(service.getPlayer().getBufferedPosition());
                                mediaPlayerTimeBar.setPosition(service.getPlayer().getCurrentPosition());
                            }
                        }

                        // Update in db
                        FeedContentData data = service.getCurrentItem();
                        if (data != null && service.isPlaying() && !service.isPlayingCreditAd()) {
                            savePlayerMediaDuration(data, service.getPlayer().getCurrentPosition());
                        }
                    }
                }
            }
        }

        void configureSeekBar() {
            if (adProgressBar == null) {
                /*if (adPlayerLayout != null) {
                    adProgressBar = adPlayerLayout.findViewById(R.id.adPlayerProgressBar);
                }*/
                if (mediaPlayerLayout != null) {
                    adProgressBar = mediaPlayerLayout.findViewById(R.id.adPlayerProgressBar);
                }
            }
            if (mediaPlayerTimeBar == null) {
                if (mediaPlayerLayout != null) {
                    mediaPlayerTimeBar = mediaPlayerLayout.findViewById(R.id.mediaPlayerTimeBar);
                }
            }
            if (verticalPlayerProgressBar == null) {
                if (mediaPlayerLayout != null) {
                    verticalPlayerProgressBar = mediaPlayerLayout.findViewById(R.id.verticalPlayerProgressBar);
                }
            }
            if (mediaPlayerTimeBar != null) {
                mediaPlayerTimeBar.addListener(new TimeBar.OnScrubListener() {
                    @Override
                    public void onScrubStart(TimeBar timeBar, long position) {
                    }

                    @Override
                    public void onScrubMove(TimeBar timeBar, long position) {
                    }

                    @Override
                    public void onScrubStop(TimeBar timeBar, long position, boolean canceled) {
                        if (!canceled && serviceBound && service != null && service.getPlayer() != null) {
                            service.getPlayer().seekTo(position);
                        }
                    }
                });
            }
        }

        void updateAdsBreakPoints(@NonNull ArrayList<FeedContentData> ads) {
            if (mediaPlayerTimeBar == null) {
                configureSeekBar();
            }
            long[] adGroupTimesMs = new long[ads.size()];
            boolean[] playedAdGroups = new boolean[ads.size()];
            if (ads.size() > 0) {
                for (int i = 0; i < ads.size(); i++) {
                    FeedContentData ad = ads.get(i);
                    adGroupTimesMs[i] = ad.adFieldsData.play_at * 1000L;
                    playedAdGroups[i] = false;
                    Log.i("Ads", "Ad time: " + adGroupTimesMs[i]);
                }
            }
            try {
                DefaultTimeBar timeBar1 = mediaControls.findViewById(R.id.exo_progress);
                timeBar1.setAdGroupTimesMs(adGroupTimesMs, playedAdGroups, ads.size());
            } catch (Exception ignored) {
            }
            mediaPlayerTimeBar.setAdGroupTimesMs(adGroupTimesMs, playedAdGroups, ads.size());
            if (mediaPlayerTimeBar2 != null) {
                mediaPlayerTimeBar2.setAdGroupTimesMs(adGroupTimesMs, playedAdGroups, ads.size());
            }
        }

        void setFullScreen() {

            Log.i("HomeActivity", "setFullscreen");
            if (exo_play.getVisibility() == View.VISIBLE) {
                exo_play.requestFocus();
            } else if (exo_pause.getVisibility() == View.VISIBLE) {
                exo_pause.requestFocus();
            }
            if (exo_fullscreen_icon != null) {
                exo_fullscreen_icon.setImageDrawable(ContextCompat.
                        getDrawable(HomeActivity.this, R.drawable.fullscreen_exit));
            }
            if (mediaPlayerTimeBar != null) {
                mediaPlayerTimeBar.setVisibility(View.GONE);
            }
            if (service != null && !service.isPlayingCreditAd() && !service.isPlayingAd()) {
                if (mediaPlayerTimeBar2 != null) {
                    mediaPlayerTimeBar2.setVisibility(View.VISIBLE);
                }
            } else {
                if (mediaPlayerTimeBar2 != null) {
                    mediaPlayerTimeBar2.setVisibility(View.GONE);
                }
            }

            if (btPlayGame != null) {
                if (arrayListQuestionData.size() > 0) {
                    if (!Utility.isTelevision()) {
                        btPlayGame.setVisibility(View.VISIBLE);
                    }
                } else {
                    btPlayGame.setVisibility(View.GONE);
                }
            }
            if (playerItemTitleLbl != null) playerItemTitleLbl.setVisibility(View.VISIBLE);
        }

        void removeFullScreen() {
            //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            //getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
            if (exo_fullscreen_icon != null) {
                exo_fullscreen_icon.setImageDrawable(ContextCompat.getDrawable(HomeActivity.this, R.drawable.fullscreen));
            }
            if (service != null && mediaPlayerTimeBar != null && !service.isPlayingAd() && !service.isPlayingCreditAd()) {
                mediaPlayerTimeBar.setVisibility(View.VISIBLE);
            } else {
                if (mediaPlayerTimeBar != null) {
                    mediaPlayerTimeBar.setVisibility(View.GONE);
                }
                // updateAdProgressBarMargin();
            }
            if (mediaPlayerTimeBar2 != null) {
                mediaPlayerTimeBar2.setVisibility(View.GONE);
            }
            if (service != null && service.isPlayingAd()) {
                if (mediaPlayerTimeBar != null) {
                    mediaPlayerTimeBar.setVisibility(View.GONE);
                }
                updateAdProgressBarMargin();
            }

            if (btPlayGame != null) {
                btPlayGame.setVisibility(View.GONE);
            }
            if (playerItemTitleLbl != null) playerItemTitleLbl.setVisibility(View.GONE);
        }

        private void updateAdProgressBarMargin() {
            try {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)
                        adProgressBar.getLayoutParams();
                float scale = getResources().getDisplayMetrics().density;
                int pixels = (int) (14 * scale + 0.5f);
                params.bottomMargin = mFullScreenDialog.isShowing() ? 0 : pixels;
            } catch (Exception ignored) {
            }
        }

        private void updatePlayerTitle() {
            if (serviceBound && service != null) {
                if (service.getCurrentItem() != null && playerItemTitleLbl != null) {
                    playerItemTitleLbl.setText(service.getCurrentItem().postTitle);
                }
            }
        }

    }

    public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.VideoViewHolder> {
        private List<FeedContentData> mVideoItems;
        private Context context;
        private int index;

        public VideosAdapter(List<FeedContentData> videoItems, Context mContext, int position) {
            this.mVideoItems = videoItems;
            this.context = mContext;
            this.index = position;
        }

        @NonNull
        @Override
        public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new VideoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_vertical_player, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
            holder.setVideoData(mVideoItems.get(position), position, context);
        }

        @Override
        public int getItemCount() {
            return mVideoItems.size();
        }

        class VideoViewHolder extends RecyclerView.ViewHolder {
            VideoView mVideoView;
            TextView txtTitle, txtDesc;
            ProgressBar verticalPlayerProgressBar, progressBar;
            ImageView ivLike, ivComment, ivShare, ivAddToWatchlist, ivPause;
            TextView tvLike, tvComment;
            Button rewind, fForward;
            long mDeBounce = 0;

            public VideoViewHolder(@NonNull View itemView) {
                super(itemView);
                mVideoView = itemView.findViewById(R.id.videoView);
                txtTitle = itemView.findViewById(R.id.tvVideoTitle);
                txtDesc = itemView.findViewById(R.id.tvVideoDescription);
                verticalPlayerProgressBar = itemView.findViewById(R.id.verticalPlayerProgressBar);
                progressBar = itemView.findViewById(R.id.progressBar);
                ivPause = itemView.findViewById(R.id.ivPause);
                ivLike = itemView.findViewById(R.id.ivLike);
                ivComment = itemView.findViewById(R.id.ivComment);
                ivShare = itemView.findViewById(R.id.ivShare);
                ivAddToWatchlist = itemView.findViewById(R.id.ivAddtoWatchlist);
                tvLike = itemView.findViewById(R.id.tvLike);
                tvComment = itemView.findViewById(R.id.tvComment);
                rewind = itemView.findViewById(R.id.btRewind);
                fForward = itemView.findViewById(R.id.btFForward);
            }

            void setVideoData(FeedContentData videoItem, int position, Context mContext) {
                ivPause.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);

                txtTitle.setText(videoItem.postTitle);
                txtDesc.setText(videoItem.description);
                getLikeCommentWishList(videoItem.postId);
                if (!videoId.contains(videoItem.postId)) {
                    videoId.add(position, videoItem.postId);
                    videoViewArrayList.add(position, mVideoView);
                    textViewArrayList.add(position, tvComment);
                    imageViewArrayList.add(position, ivPause);
                }

                mVideoView.setVideoPath(videoItem.attachmentData.url);
                mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        progressBar.setVisibility(View.GONE);
                        ivPause.setVisibility(View.GONE);
                        mp.start();
                        //updateSeekBar(mp);
                        if (videoViewArrayList.get(videosViewPager.getCurrentItem()) != mVideoView) {
                            mp.pause();
                        }
                    }
                });
                mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if (videoItem.isCategoryCoin) {
                            APIMethods.addEvent(
                                    HomeActivity.this, "category_coin", String.valueOf(videoItem.categoryId), "", "");
                        }
                        AnalyticsTracker.stopDurationVideo();
                        videosViewPager.setCurrentItem(videosViewPager.getCurrentItem() + 1);
                    }
                });
                mVideoView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mVideoView.isPlaying()) {
                            AnalyticsTracker.pauseTimer(AnalyticsTracker.VIDEO);
                            mVideoView.pause();
                            ivPause.setVisibility(View.VISIBLE);
                        } else {
                            AnalyticsTracker.startDurationVideo();
                            mVideoView.start();
                            ivPause.setVisibility(View.GONE);
                        }
                    }
                });
               /* mVideoView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent motionEvent) {
                        if (Math.abs(mDeBounce - motionEvent.getEventTime()) < 250) {
                            //Ignore if it's been less then 250ms since
                            //the item was last clicked
                            return true;
                        }

                        int intCurrentY = Math.round(motionEvent.getY());
                        int intCurrentX = Math.round(motionEvent.getX());
                        int intStartY = motionEvent.getHistorySize() > 0 ? Math.round(motionEvent.getHistoricalY(0)) : intCurrentY;
                        int intStartX = motionEvent.getHistorySize() > 0 ? Math.round(motionEvent.getHistoricalX(0)) : intCurrentX;

                        if ((motionEvent.getAction() == MotionEvent.ACTION_UP) && (Math.abs(intCurrentX - intStartX) < 3) && (Math.abs(intCurrentY - intStartY) < 3)) {
                            if (mDeBounce > motionEvent.getDownTime()) {
                                //Still got occasional duplicates without this
                                return true;
                            }

                            //Handle the click
                            if (mVideoView.isPlaying()) {
                                AnalyticsTracker.pauseTimer(AnalyticsTracker.VIDEO);
                                mVideoView.pause();
                                ivPause.setVisibility(View.VISIBLE);
                            } else {
                                AnalyticsTracker.startDurationVideo();
                                mVideoView.start();
                                ivPause.setVisibility(View.GONE);
                            }

                            mDeBounce = motionEvent.getEventTime();
                            return true;
                        }
                        return false;
                    }
                });*/

                ivLike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setLike(videoItem.postId);
                    }
                });

                ivComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openCommentDialog(videoItem);
                    }
                });

                ivShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shareData(videoItem);
                    }
                });

                ivAddToWatchlist.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addToWatchList(videoItem.postId);
                    }
                });

              /*  rewind.setOnTouchListener(new View.OnTouchListener() {
                    GestureDetector gestureDetector = new GestureDetector(itemView.getContext(), new GestureDetector.SimpleOnGestureListener() {
                        @Override
                        public boolean onDoubleTap(MotionEvent e) {
                            int currentPosition = mVideoView.getCurrentPosition();
                            int duration = mVideoView.getDuration();
                            int newPosition;
                            newPosition = (currentPosition - 10000);
                            mVideoView.seekTo(newPosition >= 0 ? newPosition : 0);
                            return super.onDoubleTap(e);
                        }
                    });

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        gestureDetector.onTouchEvent(event);
                        return false;
                    }
                });

                fForward.setOnTouchListener(new View.OnTouchListener() {
                    GestureDetector gestureDetector = new GestureDetector(itemView.getContext(), new GestureDetector.SimpleOnGestureListener() {
                        @Override
                        public boolean onDoubleTap(MotionEvent e) {
                            int currentPosition = mVideoView.getCurrentPosition();
                            int duration = mVideoView.getDuration();
                            int newPosition;
                            newPosition = (currentPosition + 10000);
                            mVideoView.seekTo(newPosition <= duration ? newPosition : duration);
                            return super.onDoubleTap(e);
                        }
                    });

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        gestureDetector.onTouchEvent(event);
                        return false;
                    }
                });*/
            }

            private void getLikeCommentWishList(String id) {
                try {
                    Map<String, String> params = new HashMap<>();
                    params.put("post_id", id);
                    APIRequest apiRequest = new APIRequest(Url.GET_LIKES_COMMENTS + "&post_id=" + id, Request.Method.GET, params, null, itemView.getContext());
                    apiRequest.showLoader = false;
                    APIManager.request(apiRequest, new APIResponse() {
                        @Override
                        public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                            try {
                                if (error != null) {
                                    //  TODO: Handle error state (Empty State)
                                } else {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String type = jsonObject.getString("type");
                                    if (type.equals("OK")) {
                                        JSONObject msg = jsonObject.getJSONObject("msg");
                                        tvLike.setText(msg.getString("like_count"));
                                        tvComment.setText(msg.getString("comment_count"));

                                        setLikeMusicImage(msg.getBoolean("liked_by_me"));

                                        setWishImage(msg.getBoolean("is_watch_listed"));
                                    }
                                }
                            } catch (Exception ignored) {

                            }
                        }
                    });
                } catch (Exception ignored) {
                }
            }

            private void setLike(String id) {
                try {
                    Map<String, String> params = new HashMap<>();
                    params.put("post_id", id);
                    APIRequest apiRequest = new APIRequest(Url.LIKES, Request.Method.POST, params, null, itemView.getContext());
                    apiRequest.showLoader = false;
                    APIManager.request(apiRequest, new APIResponse() {
                        @Override
                        public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                            try {
                                if (error != null) {
                                    //  TODO: Handle error state (Empty State)
                                } else {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String type = jsonObject.getString("type");
                                    if (type.equals("OK")) {
                                        JSONObject msg = jsonObject.getJSONObject("msg");
                                        tvLike.setText(msg.getString("no_likes"));
                                        setLikeMusicImage(msg.getBoolean("liked"));
                                    }
                                }
                            } catch (Exception ignored) {

                            }
                        }
                    });
                } catch (Exception ignored) {
                }
            }

            private void addToWatchList(String id) {
                try {
                    Map<String, String> params = new HashMap<>();
                    Log.e("avinash", params.toString());
                    APIRequest apiRequest = new APIRequest(Url.ADD_REMOVE_WATCHLIST + id, Request.Method.GET, params, null, itemView.getContext());
                    apiRequest.showLoader = false;
                    APIManager.request(apiRequest, new APIResponse() {
                        @Override
                        public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                            if (response == null || response.isEmpty()) {
                                return;
                            }
                            JSONObject jsonObjectResponse = null;
                            try {
                                jsonObjectResponse = new JSONObject(response);
                                Log.e("avinash", String.valueOf(jsonObjectResponse));

                                String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                                if (type.equalsIgnoreCase("error")) {
                                    String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                                    Toast.makeText(HomeActivity.this, message, Toast.LENGTH_LONG).show();
                                } else if (type.equalsIgnoreCase("ok")) {
                                    JSONObject msg = jsonObjectResponse.getJSONObject("msg");
                                    setWishImage(msg.getBoolean("is_added"));
                                    if (msg.getBoolean("is_added")) {
                                        Toast.makeText(HomeActivity.this, getString(R.string.added_to_watchlist), Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(HomeActivity.this, getString(R.string.removed_to_watchlist), Toast.LENGTH_LONG).show();
                                    }
                                    LocalStorage.setWishedItem(Integer.parseInt(id), msg.getBoolean("is_added"), getApplicationContext());

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    Utility.showError(getString(R.string.failed_to_comment), HomeActivity.this);
                }
            }

            private void setLikeMusicImage(boolean likeByMe) {
                if (likeByMe) {
                    ivLike.setImageResource(R.drawable.ic_likes_fill_assent);
                } else {
                    ivLike.setImageResource(R.drawable.like);
                }
            }

            private void setWishImage(boolean isWished) {
                if (isWished) {
                    ivAddToWatchlist.setImageResource(R.drawable.ic_added_to_watchlist);
                } else {
                    ivAddToWatchlist.setImageResource(R.drawable.ic_add_white);
                }
            }

            private void shareData(FeedContentData feedContentData) {
                String feedImage = feedContentData.imgUrl.equals("") ? feedContentData.playlistImageUrl.equals("") ? "" : feedContentData.playlistImageUrl : feedContentData.imgUrl;
                if (!feedImage.equals("")) {
                    Picasso.get().load(feedImage).into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            try {
                                String appLink = Constant.APP_SHARE_LINK;
                                Intent i = new Intent(Intent.ACTION_SEND);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                i.putExtra(Intent.EXTRA_TITLE, "iTap");
                                i.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(bitmap, itemView.getContext()));
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    i.putExtra(Intent.EXTRA_TEXT, getString(R.string.hey_watch) + feedContentData.postTitle + getString(R.string.check_it_out_now_on_iTap) + "\n" +
                                            "https://www.itap.online?" +
                                            Utility.encryptData("playstore=true&type=" + feedContentData.postType + "&postId=" + feedContentData.postId, Constant.getSecretKey())
                                    );
                                }
                                i.setType("*/*");
                                startActivity(Intent.createChooser(i, getString(R.string.share_content)));
                            } catch (Exception e) {

                            }
                        }

                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    });


                }
            }

           /* private void updateSeekBar(MediaPlayer mp) {
                Timer timer = new Timer();
                TimerTask doAsynchronousTask = new TimerTask() {
                    @Override
                    public void run() {
                        handler.post(new Runnable() {
                            public void run() {
                                try {
                                    //your method here

                                    int total = mp.getDuration();
                                    int played = mp.getCurrentPosition();
                                    verticalPlayerProgressBar.setMax(total);
                                    //verticalPlayerProgressBar.setSecondaryProgress(buffered);
                                    verticalPlayerProgressBar.setProgress(played);
                                } catch (Exception e) {
                                }
                            }
                        });
                    }
                };
                timer.schedule(doAsynchronousTask, 0, 10);
            }*/

        }

    }

    public void redirectToPage(String page) {
        switch (page) {
            case Constant.SUBSCRIPTION:
                startActivity(new Intent(HomeActivity.this, PremiumActivity.class).putExtra("title", getResources().getString(R.string.label_premium)));
                break;
            case Constant.PURCHASES:
                startActivity(new Intent(HomeActivity.this, PurchasesActivity.class).putExtra("title", getResources().getString(R.string.label_purchases)));
                break;
            case Constant.WATCHLIST:
                startActivityForResult(new Intent(HomeActivity.this, MyWatchList.class), BuyDetailActivity.REQUEST_CODE);
                break;
            case Constant.DOWNLOAD:
                startActivity(new Intent(HomeActivity.this, DownloadActivity.class).putExtra("title", getResources().getString(R.string.label_downloads)));
                break;
            case Constant.REDEEM_VOUCHER:
                startActivity(new Intent(HomeActivity.this, CouponRedemptionActivity.class).putExtra("title", getResources().getString(R.string.label_redeem_coupon)));
                break;
            case Constant.REFER_EARN:
                startActivity(new Intent(HomeActivity.this, ReferAndEarnActivity.class));
                break;
            case Constant.CONTACT_US:
                startActivity(new Intent(HomeActivity.this, BrowserActivity.class).putExtra("title", getResources().getString(R.string.label_contact_us)).putExtra("posturl", Url.CONTACT_US));
                break;
            case Constant.TERMS_USE:
                startActivity(new Intent(HomeActivity.this, BrowserActivity.class).putExtra("title", getResources().getString(R.string.label_terms_of_uses)).putExtra("posturl", Url.TERMS_OF_USE));
                break;
            case Constant.PRIVACY_POLICY:
                startActivity(new Intent(HomeActivity.this, BrowserActivity.class).putExtra("title", getResources().getString(R.string.label_privacy_policy)).putExtra("posturl", Url.PRIVACY_POLICY));
                break;
            case Constant.REFUND_CANCELLATION:
                startActivity(new Intent(HomeActivity.this, BrowserActivity.class).putExtra("title", getResources().getString(R.string.label_refund_cancellation)).putExtra("posturl", Url.REFUNDS_AND_CANCELLATION));
                break;
            case Constant.ABOUT:
                startActivity(new Intent(HomeActivity.this, BrowserActivity.class).putExtra("title", getResources().getString(R.string.label_about)).putExtra("posturl", Url.ABOUT));
                break;
            case Constant.PREMIUM:
                new Handler(Looper.getMainLooper()).post(() -> bottomNavigationView.setSelectedItemId(R.id.bNavSubscribe));
                break;
            case Constant.AVATAR:
                new Handler(Looper.getMainLooper()).post(() -> bottomNavigationView.setSelectedItemId(R.id.bNavAvatar));
                break;
            case Constant.SHOP:
                new Handler(Looper.getMainLooper()).post(() -> bottomNavigationView.setSelectedItemId(R.id.bNavShop));
                break;
            case Constant.LIVE:
                new Handler(Looper.getMainLooper()).post(() -> openFragment(LiveJavaFrag.newInstance()));
                break;
            case Constant.SEARCH:
                new Handler(Looper.getMainLooper()).post(() -> openFragment(SearchFragment.newInstance()));
                break;
            case Constant.NOTIFICATION:
                new Handler(Looper.getMainLooper()).post(() -> openFragment(NotificationFragment.newInstance()));
                break;
            case Constant.GAMES:
                new Handler(Looper.getMainLooper()).post(() -> openFragment(HomePlayTabFragment.newInstance()));
                break;
            case Constant.EARN:
                new Handler(Looper.getMainLooper()).post(() -> {
                    MenuItem item = bottomNavigationView.getMenu().getItem(1);
                    bottomNavigationView.setSelectedItemId(item.getItemId());
                    if (ShopFrag.tabLayoutRewards != null) {
                        new Handler().postDelayed(() -> ShopFrag.tabLayoutRewards.getTabAt(1).select(), 100);
                    }
                });
                break;
            case Constant.WALLET:
                new Handler(Looper.getMainLooper()).post(() -> {
                    MenuItem item = bottomNavigationView.getMenu().getItem(1);
                    bottomNavigationView.setSelectedItemId(item.getItemId());
                    if (ShopFrag.tabLayoutRewards != null) {
                        new Handler().postDelayed(() -> ShopFrag.tabLayoutRewards.getTabAt(2).select(), 100);
                    }
                });
                break;
        }
    }

    public void getUserRegisteredTournamentInfo(String postId) {
        try {
            Map<String, String> params = new HashMap<>();
            //params.put("tournament_id",feedContentData.postId);
            APIRequest apiRequest = new APIRequest(Url.GET_USER_REGISTERED_TOURNAMENT_INFO + "&tournament_id=" + postId,
                    Request.Method.GET, params, null, HomeActivity.this);
            apiRequest.showLoader = true;
            APIManager.request(apiRequest, (response, error, headers, statusCode) -> {
                handleUserRegisteredTournamentInfo(response, error, postId);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleUserRegisteredTournamentInfo(@Nullable String response, @Nullable Exception error, String postId) {
        try {
            if (error != null) {

            } else {
                if (response != null) {
                    JSONObject jsonObjectResponse = new JSONObject(response);
                    String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                    if (type.equalsIgnoreCase("error")) {
                        String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                    } else if (type.equalsIgnoreCase("ok")) {
                        JSONObject jsonArrayMsg = jsonObjectResponse.getJSONObject("msg");
                        JSONArray jsonArrayParticipant = jsonArrayMsg.getJSONArray("participant");
                        if (jsonArrayParticipant.length() > 0) {
                            Intent intent = new Intent(HomeActivity.this, TournamentSummaryActivity.class);
                            //intent.putExtra(TournamentRegistrationFormActivity.CONTENT_DATA, postId);
                            LocalStorage.putValue(postId, LocalStorage.KEY_ESPORTS_ID);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(HomeActivity.this, TournamentRegistrationFormActivity.class);
                            //intent.putExtra(TournamentRegistrationFormActivity.CONTENT_DATA, postId);
                            LocalStorage.putValue(postId, LocalStorage.KEY_ESPORTS_ID);
                            startActivity(intent);
                        }
                    }
                }
            }
        } catch (JSONException e) {

        }
    }

    @Override
    public void navMenuToggle(boolean toShow) {
        try {
            if (toShow) {
                nav_fragment.setBackgroundResource(R.drawable.ic_nav_bg_closed);
                container.clearFocus();
                nav_fragment.requestFocus();
                navEnterAnimation();
                navMenuFragment.openNav();
            } else {
                nav_fragment.setBackgroundResource(R.drawable.ic_nav_bg_open);
                nav_fragment.clearFocus();
                container.requestFocus();
                navMenuFragment.closeNav("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void switchFragment(@Nullable String fragmentName) {
        nav_fragment.setBackgroundResource(R.drawable.ic_nav_bg_open);
        switch (fragmentName) {
            case "home":
                openFragment(moviesFragment);
                moviesFragment.restoreSelection();
                break;
            case "premium":
                openFragment(premiumTvFragment);
                premiumTvFragment.restoreSelection();
                break;
            case "more":
                openFragment(moreFragment);
                break;
            case "search":
                searchTvFragment = SearchTvFragment.newInstance();
                openFragment(searchTvFragment);
                searchTvFragment.selectFirstChar();
                break;
            case "live":
                openFragment(liveJavaFrag);
                nav_fragment.setVisibility(View.GONE);
                break;
            case "notification":
                openFragment(notificationFragment);
                nav_fragment.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onStateChanged(boolean expanded, @Nullable String lastSelected) {
        if (!expanded) {
            nav_fragment.setBackgroundResource(R.drawable.ic_nav_bg_open);
            nav_fragment.clearFocus();

            switch (Objects.requireNonNull(lastSelected)) {
                case "home":
                    currentSelectedFragment = Constant.nav_menu_home;
                    moviesFragment.selectFirstItem();
                    break;
                case "more":
                    currentSelectedFragment = Constant.nav_menu_more;
                    moreFragment.restorePosition();
                    break;
                case "premium":
                    currentSelectedFragment = Constant.nav_menu_premium;
                    premiumTvFragment.selectFirstItem();
                    break;
                case "search":
                    currentSelectedFragment = Constant.nav_menu_search;
                    searchTvFragment.selectFirstChar();
                    break;
            }
        } else {
            //do
        }
    }

    public void fragmentReplacer(int containerId, Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(containerId, fragment).addToBackStack(null).commit();
        if (fragment instanceof ViewAllTvFragment) {
            nav_fragment.setVisibility(View.GONE);
        }
    }

    private void navEnterAnimation() {
        Animation animate = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
        nav_fragment.startAnimation(animate);
    }

    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment instanceof MoviesFragment) {
            moviesFragment.setNavigationMenuCallbackMovie(HomeActivity.this);
        } else if (fragment instanceof PremiumTvFragment) {
            premiumTvFragment.setNavigationMenuCallbackPremium(HomeActivity.this);
        } else if (fragment instanceof MoreFragment) {
            moreFragment.setNavigationMenuCallbackMore(HomeActivity.this);
        } else if (fragment instanceof SearchTvFragment) {
            searchTvFragment.setNavigationMenuCallbackSearch(HomeActivity.this);
        } else if (fragment instanceof NavigationMenu) {
            navMenuFragment.setFragmentChangeListener(this);
            navMenuFragment.setNavigationStateListener(this);
        }
    }

    public void goToPremium() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        navMenuFragment.setLastSelectedMenu(Constant.nav_menu_premium);
                        switchFragment(Constant.nav_menu_premium);
                        navMenuFragment.closeNav(Constant.nav_menu_premium);
                    }
                });
            }
        }, 100);
    }

    public void showExitAppDialog() {
        LayoutInflater inflater = (LayoutInflater) HomeActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_app_exit, null);

        Button bt_yes = view.findViewById(R.id.bt_yes);
        Button bt_no = view.findViewById(R.id.bt_no);

        buttonFocusListener(bt_yes);
        buttonFocusListener(bt_no);
        bt_yes.requestFocus();

        Dialog alertDialogBuilder = new Dialog(HomeActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        alertDialogBuilder.setContentView(view);
        alertDialogBuilder.show();

        bt_yes.setOnClickListener(v -> {
            alertDialogBuilder.dismiss();
            this.finish();
            System.exit(0);
        });

        bt_no.setOnClickListener(v -> {
            alertDialogBuilder.dismiss();
        });
    }

    void buttonFocusListener(View view) {
        view.setBackground(getResources().getDrawable(R.drawable.bg_button_grey));
        view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (view.hasFocus()) {
                    view.setBackground(getResources().getDrawable(R.drawable.bg_accent));
                } else {
                    view.setBackground(getResources().getDrawable(R.drawable.bg_button_grey));
                }
            }
        });
    }

    public void playPremiumContent(FeedContentData feedContentData, String episodeId) {
        if (episodeId == null) {
            episodeId = "0";
        }
        //playPurchasedItem(feedContentData);
        arrayListFeedContent.clear();
        arrayListFeedContent.add(feedContentData);
        Log.e("avinash", "playNowHome");
        if (feedContentData.contentOrientation.equalsIgnoreCase("vertical") && !Utility.isTelevision()) {
            ArrayList<FeedContentData> feedList = new ArrayList<>();
            feedList.add(feedContentData);
            openVerticalPlayer(feedList, 0);
        } else {
            playMedia(feedContentData, episodeId);
        }
    }

}
