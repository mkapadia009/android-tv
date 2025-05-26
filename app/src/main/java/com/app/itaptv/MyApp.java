package com.app.itaptv;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.app.itaptv.API.APIManager;
import com.app.itaptv.API.APIRequest;
import com.app.itaptv.API.Url;
import com.app.itaptv.offline.DownloadTracker;
import com.app.itaptv.roomDatabase.ListRoomDatabase;
import com.app.itaptv.structure.DeviceDetailsData;
import com.app.itaptv.structure.LocationData;
import com.app.itaptv.utils.AnalyticsTracker;
import com.app.itaptv.utils.Constant;
import com.app.itaptv.utils.FirebaseAnalyticsLogs;
import com.app.itaptv.utils.LocalStorage;
import com.app.itaptv.utils.Utility;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.database.DatabaseProvider;
import com.google.android.exoplayer2.database.ExoDatabaseProvider;
import com.google.android.exoplayer2.offline.DefaultDownloadIndex;
import com.google.android.exoplayer2.offline.DownloadManager;
import com.google.android.exoplayer2.ui.DownloadNotificationHelper;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.shephertz.app42.paas.sdk.android.App42API;
import com.shephertz.app42.paas.sdk.android.App42Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;

import com.bugfender.sdk.Bugfender;
import com.bugfender.android.BuildConfig;
import com.trackier.sdk.TrackierSDK;
import com.trackier.sdk.TrackierSDKConfig;

/**
 * Created by poonam on 22/8/18.
 */

public class MyApp extends Application {

    public static final String TAG = MyApp.class.getSimpleName();

    /// application context variable
    private static Context context;
    private static MyApp mInstance;
    private RequestQueue mRequestQueue;
    public boolean isShowNotification = true;
    public boolean isUpdateScreenVisible = false;


    private Timer playedSecondsTimer = null;
    private TimerTask secondsPlayedTask = null;
    private int secondsPlayed = 0;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private String latitude;
    private String longitude;
    private String city;
    private String state;
    private ListRoomDatabase listRoomDatabase;
    public boolean isFirstLoad;

    public static Context getAppContext() {
        return context;
    }

    @Override
    public void onCreate() {
        /*StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog()
                .build());*/
        super.onCreate();
        AnalyticsTracker.secondsPlayedUser = 0;
        AnalyticsTracker.locationData = null;
        AnalyticsTracker.deviceDetailsData = null;

        userAgent = Util.getUserAgent(this, getString(R.string.app_name));

        initAppWarp();
        context = getApplicationContext();
        mInstance = this;
        isFirstLoad = true;
        /*StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());*/

        //initExo();

        Bugfender.init(this, "TmueoREEvUB7yYIvTpQSV1v9jXrMYR8J", BuildConfig.DEBUG);
        Bugfender.enableCrashReporting();
        Bugfender.enableUIEventLogging(this);
        Bugfender.enableLogcatLogging(); // optional, if you want logs automatically collected from logcat

        FirebaseAnalyticsLogs.logEvent().activeUser();

        // VAV SDK
        // initiateVAVSdk();
        listRoomDatabase = ListRoomDatabase.getDatabase(context);
        if (LocalStorage.getPlayedGameData() != null) {
            listRoomDatabase.mediaDAO().saveAnalytics(LocalStorage.getPlayedGameData());
            LocalStorage.savePlayedGameData(null);
        }

        if (LocalStorage.getUserAnalytics() != null) {
            listRoomDatabase.mediaDAO().saveAnalytics(LocalStorage.getUserAnalytics());
            FirebaseAnalyticsLogs.logEvent().sessionDuration(LocalStorage.getUserAnalytics().getDuration());
            LocalStorage.saveUserAnalytics(null);
        }

        if (LocalStorage.getAvatarAnalyticsData() != null) {
            listRoomDatabase.mediaDAO().saveAnalytics(LocalStorage.getAvatarAnalyticsData());
            LocalStorage.saveAvatarAnalyticsData(null);
        }
        if (LocalStorage.getShopAnalyticsData() != null) {
            listRoomDatabase.mediaDAO().saveAnalytics(LocalStorage.getShopAnalyticsData());
            LocalStorage.saveShopAnalyticsData(null);
        }
        if (LocalStorage.getVideoAnalyticsData() != null) {
            listRoomDatabase.mediaDAO().saveAnalytics(LocalStorage.getVideoAnalyticsData());
            LocalStorage.saveVideoAnalyticsData(null);
        }
        Thread.setDefaultUncaughtExceptionHandler(handleAppCrash);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

        if (!Utility.isTelevision()) {
            if (ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            }
        }
        AnalyticsTracker.deviceDetailsData = Utility.getSystemDetail();
        AnalyticsTracker.resumeTimer(AnalyticsTracker.USER);

        final String TR_SDK_KEY = getString(R.string.trackier_app_id); // Please pass your SDK key here.

        /* While Initializing the SDK, You need to pass the three parameter in the TrackierSDKConfig.
         * In First argument, you need to pass context of the application
         * In second argument, you need to pass the Trackier SDK api key
         * In third argument, you need to pass the environment which can be either "development", "production" or "testing". */
        if (!com.app.itaptv.BuildConfig.DEBUG) {
            TrackierSDKConfig sdkConfig = new TrackierSDKConfig(this, TR_SDK_KEY, "development");
            TrackierSDK.initialize(sdkConfig);
        }
    }

    private Thread.UncaughtExceptionHandler handleAppCrash =
            new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread thread, Throwable ex) {
                    //send email here
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    ex.printStackTrace(pw);
                    String str = sw.toString();
                    LocalStorage.putValue(str, "Throwable");
                    DeviceDetailsData deviceDetailsData = Utility.getSystemDetail();
                    try {
                        Map<String, String> params = new HashMap<>();
                        params.put("crashDateTime", Utility.getCurrentDateTime());
                        params.put("crashDescription", str);
                        params.put("deviceModel", deviceDetailsData.getModel());
                        params.put("deviceVersion", deviceDetailsData.getVersionCode());
                        params.put("userId", LocalStorage.getUserId());
                        APIRequest apiRequest = new APIRequest(Url.STORE_CRASH_DETAILS, Request.Method.POST, params, null, context);
                        apiRequest.showLoader = false;
                        APIManager.request(apiRequest, (response, error, headers, statusCode) -> {
                            try {
                                if (error != null) {
                                    //AlertUtils.showToast(error.getMessage(), 1, this);
                                } else {
                                    if (response != null) {
                                        Log.e("response", response);
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
            };

    /// Get application context

    public static Context sharedContext() {
        return context;
    }

    public static synchronized MyApp getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    /**
     * AppWarp Initialization
     */
    private static final String APP_WARP_API_KEY_DEV = "4e9f008d4256c21f2ac0cc2aa07bf216170be8646340db951a68176acec728f7";
    private static final String APP_WARP_SECRET_KEY_DEV = "926c8e7e3686fc744535d57b85aab44e4156b14d0ea5ccaaecbd19cab1eb30c1";
    private static final String APP_WARP_API_KEY_PROD = "f9f0473565eda2585fd057ef9ff0fcd7a4b8f782356d04948df706807932a060"; // staging (UAT)
    private static final String APP_WARP_SECRET_KEY_PROD = "f92e64586614167865edb773e849165bafd33b97492dbc37e08a4d995a544c50"; // staging (UAT)
    private static final String APP_WARP_API_KEY_DEMO = "b430d78baf50bd27c3538b17d46209d1bdfb9d17e94ed754b18163cfa5a5683a"; // DEMO (UAT)
    private static final String APP_WARP_SECRET_KEY_DEMO = "b48d068feef8218737fea9a73837ebda44f8df23fe4b708498fbfaf430c27d9b"; // DEMO (UAT)

    public static String GAME_NAME = "WALLET";

    private void initAppWarp() {
        //boolean prod = Url.getEnvironment() == Url.Environment.PRODUCTION;

        String API_KEY = APP_WARP_API_KEY_DEV;
        String SECRET_KEY = APP_WARP_SECRET_KEY_DEV;

        switch (BuildConfig.BUILD_TYPE) {
            case Constant.DEBUG:
                API_KEY = APP_WARP_API_KEY_DEV;
                SECRET_KEY = APP_WARP_SECRET_KEY_DEV;
                /*API_KEY = APP_WARP_API_KEY_PROD;
                SECRET_KEY = APP_WARP_SECRET_KEY_PROD;*/
                break;
            case Constant.RELEASE:
                API_KEY = APP_WARP_API_KEY_PROD;
                SECRET_KEY = APP_WARP_SECRET_KEY_PROD;
                break;
            case Constant.DEMO:
                API_KEY = APP_WARP_API_KEY_DEMO;
                SECRET_KEY = APP_WARP_SECRET_KEY_DEMO;
                break;
        }
        App42API.initialize(this, API_KEY, SECRET_KEY);
        App42Log.setDebug(true);
        App42API.setDbName(GAME_NAME);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
    }

    // VAV SDK
    private static final String authToken = "d1e203fdc3c92698e8be";
    private static final String guid = "1ff3410cc50841938d49";//"b3fca1a92044a0139df7";
    private static final String referralCode = "0123150007";
    private static final String app_key = "64b636723e96d10ef0eb";

    /*private void initiateVAVSdk() {
        //if (VavDirectGenerator.getInstance().isUserLogin()) return;

        DeploymentType deploymentType = DeploymentType.DEVELOPMENT;
        ApplicationHelper.getInstance().setCurrentApplication(this);
        VavDirectGenerator.initialize(deploymentType, app_key);

        // auth token is the user's login id from the app.
        AuthData authData = new AuthData(authToken, guid, "");
        //AuthData authData = new AuthData(authToken, guid, referralCode);
        //AuthData authData = new AuthData("Triplecom", "8", "");

        LoginCallback loginCallback = new LoginCallback() {
            @Override
            public void onLoginSuccess() {
                Log.i("VAV SDK", "Login Successful");
            }

            @Override
            public void onLoginFailure(ErrorInfo errorInfo) {
                Log.e("VAV SDK", "Login Failed. Error: " + errorInfo.getMessage());
            }
        };
        VavDirectGenerator.getInstance().login(authData, loginCallback);
    }*/

    /**
     * ExoPlayer - Offline
     */

    public static final String DOWNLOAD_NOTIFICATION_CHANNEL_ID = "iTap_download_channel";

    private static final String DOWNLOAD_ACTION_FILE = "actions";
    private static final String DOWNLOAD_TRACKER_ACTION_FILE = "tracked_actions";
    private static final String DOWNLOAD_CONTENT_DIRECTORY = "downloads";

    public String userAgent;

    private DatabaseProvider databaseProvider;
    private File downloadDirectory;
    private Cache downloadCache;
    private DownloadManager downloadManager;
    private DownloadTracker downloadTracker;
    private DownloadNotificationHelper downloadNotificationHelper;

    /**
     * Returns a {@link DataSource.Factory}.
     */
    public DataSource.Factory buildDataSourceFactory() {
        DefaultDataSourceFactory upstreamFactory =
                new DefaultDataSourceFactory(this, buildHttpDataSourceFactory());
        return buildReadOnlyCacheDataSource(upstreamFactory, getDownloadCache());
    }

    /**
     * Returns a {@link HttpDataSource.Factory}.
     */
    public HttpDataSource.Factory buildHttpDataSourceFactory() {
        return new DefaultHttpDataSource.Factory().setUserAgent(userAgent);
    }

    /**
     * Returns whether extension renderers should be used.
     */
    public boolean useExtensionRenderers() {
        return false;
    }

    public RenderersFactory buildRenderersFactory(boolean preferExtensionRenderer) {
        @DefaultRenderersFactory.ExtensionRendererMode
        int extensionRendererMode =
                useExtensionRenderers()
                        ? (preferExtensionRenderer
                        ? DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER
                        : DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON)
                        : DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF;
        return new DefaultRenderersFactory(/* context= */ this)
                .setExtensionRendererMode(extensionRendererMode);
    }

    public DownloadNotificationHelper getDownloadNotificationHelper() {
        if (downloadNotificationHelper == null) {
            downloadNotificationHelper =
                    new DownloadNotificationHelper(this, DOWNLOAD_NOTIFICATION_CHANNEL_ID);
        }
        return downloadNotificationHelper;
    }

    public DownloadManager getDownloadManager() {
        initDownloadManager();
        return downloadManager;
    }

    public DownloadTracker getDownloadTracker() {
        initDownloadManager();
        return downloadTracker;
    }

    protected synchronized Cache getDownloadCache() {
        if (downloadCache == null) {
            File downloadContentDirectory = new File(getDownloadDirectory(), DOWNLOAD_CONTENT_DIRECTORY);
            downloadCache =
                    new SimpleCache(downloadContentDirectory, new NoOpCacheEvictor(), getDatabaseProvider());
        }
        return downloadCache;
    }

    private synchronized void initDownloadManager() {
        if (downloadManager == null) {
            DefaultDownloadIndex downloadIndex = new DefaultDownloadIndex(getDatabaseProvider());
            upgradeActionFile(
                    DOWNLOAD_ACTION_FILE, downloadIndex, /* addNewDownloadsAsCompleted= */ false);
            upgradeActionFile(
                    DOWNLOAD_TRACKER_ACTION_FILE, downloadIndex, /* addNewDownloadsAsCompleted= */ true);
           /* DownloaderConstructorHelper downloaderConstructorHelper =
                    new DownloaderConstructorHelper(getDownloadCache(), buildHttpDataSourceFactory());*/
            downloadManager =
                    new DownloadManager(
                            context,
                            getDatabaseProvider(),
                            getDownloadCache(),
                            buildHttpDataSourceFactory(),
                            Executors.newFixedThreadPool(6));
            downloadTracker =
                    new DownloadTracker(/* context= */ this, buildDataSourceFactory(), downloadManager);
        }
    }

    private void upgradeActionFile(
            String fileName, DefaultDownloadIndex downloadIndex, boolean addNewDownloadsAsCompleted) {
     /*   try {
            ActionFileUpgradeUtil.upgradeAndDelete(
                    new File(getDownloadDirectory(), fileName),
                    *//* downloadIdProvider= *//* null,
                    downloadIndex,
                    *//* deleteOnFailure= *//* true,
                    addNewDownloadsAsCompleted);
        } catch (IOException e) {
            Log.e(TAG, "Failed to upgrade action file: " + fileName, e);
        }*/
    }

    private DatabaseProvider getDatabaseProvider() {
        if (databaseProvider == null) {
            databaseProvider = new ExoDatabaseProvider(this);
        }
        return databaseProvider;
    }

    private File getDownloadDirectory() {
        if (downloadDirectory == null) {
            downloadDirectory = getExternalFilesDir(null);
            if (downloadDirectory == null) {
                downloadDirectory = getFilesDir();
            }
        }
        return downloadDirectory;
    }

    protected static CacheDataSource.Factory buildReadOnlyCacheDataSource(
            DataSource.Factory upstreamFactory, Cache cache) {
        return new CacheDataSource.Factory().setCache(cache).setUpstreamDataSourceFactory(upstreamFactory).setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);
    }

    public void getNotification() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 15 seconds
                context.sendBroadcast(new Intent("inappnotification"));
            }
        }, 15000);
    }

    private void getCurrentLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();

                        if (location != null) {
                            latitude = String.valueOf(location.getLatitude());
                            longitude = String.valueOf(location.getLongitude());
                            Geocoder geocoder;
                            List<Address> addresses;
                            geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                            try {
                                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                                city = addresses.get(0).getLocality();
                                state = addresses.get(0).getAdminArea();
                                AnalyticsTracker.locationData = new LocationData(latitude, longitude, city, state);
                                LocalStorage.setLocationData(AnalyticsTracker.locationData);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            LocationRequest locationRequest = new LocationRequest()
                                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                    .setFastestInterval(1000)
                                    .setNumUpdates(1);

                            LocationCallback locationCallback = new LocationCallback() {
                                @Override
                                public void onLocationResult(LocationResult locationResult) {
                                    Location location1 = locationResult.getLastLocation();
                                    latitude = String.valueOf(location1.getLatitude());
                                    longitude = String.valueOf(location1.getLongitude());
                                    Geocoder geocoder;
                                    List<Address> addresses;
                                    geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                                    try {
                                        addresses = geocoder.getFromLocation(location1.getLatitude(), location1.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                                        city = addresses.get(0).getLocality();
                                        state = addresses.get(0).getAdminArea();
                                        AnalyticsTracker.locationData = new LocationData(latitude, longitude, city, state);
                                        LocalStorage.setLocationData(AnalyticsTracker.locationData);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            };
                            if (ActivityCompat.checkSelfPermission(context,
                                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                    && ActivityCompat.checkSelfPermission(context,
                                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                fusedLocationProviderClient.requestLocationUpdates(locationRequest
                                        , locationCallback, Looper.myLooper());
                            }
                        }
                    }
                });
            }
        } else {
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }
}