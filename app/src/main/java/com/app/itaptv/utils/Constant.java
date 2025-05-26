package com.app.itaptv.utils;

import com.app.itaptv.BuildConfig;
import com.app.itaptv.MyApp;
import com.app.itaptv.R;

public class Constant {

    public static native String getSecretKey();

    public static native String getIvParameter();

    public static native String getSecretKeyDateTime();

    public static native String getIvParameterDateTime();

    public static native String getTestPublishKey();

    public static native String getLivePublishKey();

    static {
        System.loadLibrary("api-keys");
    }

    public static final String DEMO = "demo";
    public static final String DEBUG = "debug";
    public static final String RELEASE = "release";

    //App Information Constants
    public static final String KEY_TITLE = "info_title";
    public static final String KEY_DESCRIPTION = "info_desc";
    public static final String KEY_IMAGE = "info_image";
    public static final String KEY_INFO_TYPE = "info_type";
    public static final String KEY_APP_URL = "info_app_url";
    public static final String MAINTENANCE_APP_UPDATE = "app update";
    public static final String MAINTENANCE_SYSTEM_DOWN = "system down";
    public static final String MAINTENANCE_GENERAL = "general";
    public static final String APP_MAINTENANCE = "maintenance";
    public static final int GENERAL_RESULT_CODE = 89;

    public static int RV_HV_SPACING = 32;
    public static float RV_BACK_ALPHA = 0.0f;
    public static final String INVALID_DATE = "0000-00-00 00:00:00";
    public static final String colorTransparent = "#00000000";
    public static final String colorBlack = "#000000";
    public static final String SMS_RECIEVED = "android.provider.Telephony.SMS_RECEIVED";
    public static final String OTP_MESSAGE = "otpMessage";
    public static final String KEY_FCM_TOKEN = "fcmToken";
    // page
    public static final String LUCKY_WINNER = "live_winners_page";
    public static final String PLAYLIST = "playlist";
    public static final String GAME = "game";
    public static final String EPISODE = "episode";
    public static final String GAME_LIST = "game_listing";
    public static final String COUPONS = "coupon";
    public static final String ORIGINALS = "originals";
    public static final String COUPON_LISTING = "coupon_listing";
    public static final String CHANNEL = "channel";
    public static final String LEADERBOARD = "leaderboard";
    public static final String PLAYLIST_ID = "";
    public static final String TYPE_FEEDS = "feeds";
    public static final String TYPE_CATEGORIES = "categories";
    public static String TAB_WATCH = "listen";
    public static String TAB_PLAY = "play";
    public static String TAB_BUY = "buy";
    public static final String DOWNLOADS = "downloads";

    public static final String SUBSCRIPTION = "Subscription";
    public static final String PURCHASES = "Purchases";
    public static final String WATCHLIST = "Watchlist";
    public static final String DOWNLOAD = "Downloads";
    public static final String REDEEM_VOUCHER = "Redeem Voucher";
    public static final String REFER_EARN = "Refer & Earn";
    public static final String CONTACT_US = "Contact Us";
    public static final String TERMS_USE = "Terms of Use";
    public static final String PRIVACY_POLICY = "Privacy Policy";
    public static final String REFUND_CANCELLATION = "Refund & Cancellation";
    public static final String ABOUT = "About";

    public static final String SHOP = "Shop";
    public static final String AVATAR = "Avatar";
    public static final String PREMIUM = "Premium";
    public static final String LIVE = "Live";
    public static final String SEARCH = "Search";
    public static final String NOTIFICATION = "Notification";
    public static final String GAMES = "Games";
    public static final String EARN = "Earn";
    public static final String WALLET = "Wallet";

    public static String CONTENT_FREE = "free";
    public static String CONTENT_PAID = "paid";

    public static final String YES = "Yes";
    public static final String NO = "No";
    public static final String TEXT_TRUE = "True";
    public static final String TEXT_FALSE = "False";

    //REDEEM TAB TITLES
    public static final String SHOP_FRAG = "Shop";
    public static final String COUPONS_FRAG = "Coupons";
    public static final String VOUCHER_FRAG = "Voucher";
    public static final String WIN_CASH_FRAG = "Win Cash";

    //REDEEM TAB ID_TITLES
    public static final String SHOP_ID_FRAG = "0";
    public static final String COUPONS_ID_FRAG = "1";
    public static final String VOUCHER_ID_FRAG = "2";
    public static final String WIN_CASH_ID_FRAG = "3";

    public static String KEY_MOBILE = "mobile";
    public static String KEY_EMAIL = "email";
    private static String ERROR_MSG_EMPTY_NAME_FILED = "Please enter your name";

    public static String ACTION = "";
    public static String NOTIFICATION_DATA = "NotificationData";
    public static String NOTIFICATION_PAGE = "";

    //APP LINK
    public static String APP_SHARE_LINK = "https://play.google.com/store/apps/details?id=com.app.itap&hl=en";

    // Empty state messages
    public static final String SWIPE_DOWN_RETRY_MSG = "Swipe down to refresh or tap retry.";
    public static final String SWIPE_DOWN_MSG = "Swipe down to refresh.";
    public static final String RETRY_MSG = "Tap retry button.";

    //Error messages
    public static String ERROR_MSG_EMPTY_NUMBER_FILED = "Please enter your number";
    public static String ERROR_MSG_EMPTY_OTP_FILED = "Please enter otp";
    public static String ERROR_TITLE = MyApp.getAppContext().getString(R.string.label_error);
    public static String ALERT_TITLE = MyApp.getAppContext().getString(R.string.label_alert);

    //Orientation Texts
    public static final String VIEW_PORTRAIT = "Portrait";
    public static final String VIEW_LANDSCAPE = "Landscape";
    public static final String VIEW_UNSPECIFIED = "Unspecified";

    public static final String LUCKY_DRAW_REFERRAL_CODE = "ITAPGIFT1";

    //dev best short forwards feed id
    public static final String FEED_ID = getFeedId();

    //prod best short forwards feed id
    //public static final String FEED_ID="57712";

    public static final String YOUTUBE_API_KEY = "AIzaSyBEXVugaB56hlupJFdyarTUQz94bC38cjk";

    public static final String eSportsTitle = "eSports";

    public static final String JOIN = "join";
    public static final String CREATE = "create";

    //Encryption keys for Credit and debit
    public static final String SECRET_KEY_ENCRYPT = getSecretKey();
    public static final String IV_PARAMETER_ENCRYPT = getIvParameter();

    //Encryption keys for Date and Time
    public static final String SECRET_KEY_ENCRYPT_DATE_TIME = getSecretKeyDateTime();
    public static final String IV_PARAMETER_ENCRYPT_DATE_TIME = getIvParameterDateTime();

    //Source keys
    public static final String GOOGLE_PLAYSTORE = "Google Playstore";
    public static final String INDUSOS = "IndusOS";
    public static final String ANDROIDTV = "AndroidTv";

    //Ad
    public static final String AD = "ad";
    public static final String BROWSER = "browser";
    public static final String WEBVIEW = "webview";
    public static final String ONCLICK = "onClick";
    public static final String BANNER = "Banner";
    public static final String ADMOB = "admob";
    public static final String CUSTOM = "custom";
    public static final String VIEW = "view";
    public static final String CLICK = "click";
    public static final String SKIP = "skip";
    public static final String INAPPNOTIFICATION = "inappnotification";
    public static final String INVIDEO = "invideo";
    public static final String EXTERNAL = "external";

    public static final String TYPE = "type";
    public static final String POSTID = "postId";

    public static final String ACTIVITY_AVATAR = "activity_avatar";
    public static final String ACTIVITY_SHOP = "activity_shop";
    public static final String ACTIVITY_PREMIUM = "activity_premium";
    public static final String ACTIVITY_SUBSCRIPTION = "activity_subscription";
    public static final String ACTIVITY_GAME = "activity_game";

    public static final String PAGES = "pages";
    public static final String SCREEN_HOME = "home";
    public static final String SCREEN_PREMIUM = "premium";

    public static String ACTION_RESTART = "";
    public static String INDIA = "+91";
    public static String DEFAULT_LANGUAGE = "en";

    public static String Registration = "o6WBbqtsre";
    public static String Subscription = "fD7nrLS75P";
    public static String VideoViews = "LbmY0jI4Np";
    public static String GamePlay = "G5nzDj7pi5";
    public static String AdClicks = "3XLaqX1IOd";
    public static String WalletRedemptions = "e4NTW3WMjX";
    public static String LiveEventHits = "xKwdoow2wo";

    public static String nav_menu_home = "home";
    public static String nav_menu_premium = "premium";
    public static String nav_menu_more = "more";
    public static String nav_menu_search = "search";
    public static String nav_menu_live = "live";
    public static String nav_menu_notification = "notification";

    public static String screen_type_search = "search";
    public static String screen_type_viewall = "viewall";

    public static String getFeedId() {
        if (BuildConfig.BUILD_TYPE.equalsIgnoreCase(Constant.DEBUG)) {
            return "48823";
        } else if (BuildConfig.BUILD_TYPE.equalsIgnoreCase(Constant.RELEASE)) {
            return "57712";
        } else if (BuildConfig.BUILD_TYPE.equalsIgnoreCase(Constant.DEMO)) {
            return "48823";
        }
        return "48823";
    }

    public static String getFreeFeedId() {
        if (BuildConfig.BUILD_TYPE.equalsIgnoreCase(Constant.DEBUG)) {
            return "12802";
        } else if (BuildConfig.BUILD_TYPE.equalsIgnoreCase(Constant.RELEASE)) {
            return "12802";
        } else if (BuildConfig.BUILD_TYPE.equalsIgnoreCase(Constant.DEMO)) {
            return "12802";
        }
        return "12802";
    }

    public static String getFreeFeedIdSecond() {
        if (BuildConfig.BUILD_TYPE.equalsIgnoreCase(Constant.DEBUG)) {
            return "45417";
        } else if (BuildConfig.BUILD_TYPE.equalsIgnoreCase(Constant.RELEASE)) {
            return "45417";
        } else if (BuildConfig.BUILD_TYPE.equalsIgnoreCase(Constant.DEMO)) {
            return "45417";
        }
        return "45417";
    }

    public static String getRentFeedId() {
        if (BuildConfig.BUILD_TYPE.equalsIgnoreCase(Constant.DEBUG)) {
            return "29776";
        } else if (BuildConfig.BUILD_TYPE.equalsIgnoreCase(Constant.RELEASE)) {
            return "29776";
        } else if (BuildConfig.BUILD_TYPE.equalsIgnoreCase(Constant.DEMO)) {
            return "29776";
        }
        return "29776";
    }

    public static String getStripePublishKey() {
        if (BuildConfig.BUILD_TYPE.equalsIgnoreCase(Constant.DEBUG)) {
            return getTestPublishKey();
        } else if (BuildConfig.BUILD_TYPE.equalsIgnoreCase(Constant.RELEASE)) {
            return getLivePublishKey();
        } else if (BuildConfig.BUILD_TYPE.equalsIgnoreCase(Constant.DEMO)) {
            return getLivePublishKey();
        }
        return getLivePublishKey();
    }
}
