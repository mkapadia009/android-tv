package com.app.itaptv.API;

import com.app.itaptv.BuildConfig;
import com.app.itaptv.utils.Constant;

/**
 * Created by poonam on 22/8/18.
 */
public class Url {

    /**
     * App environment enum.REF
     * (can be more like: UAT/STAGING/DEBUG...)
     */
    public enum Environment {
        DEVELOPMENT,
        TESTING,
        PRODUCTION,
        DEMO
    }

    public static native String getDevKeys();

    public static native String getProdKeys();

    static {
        System.loadLibrary("api-keys");
    }

    /**
     * This defines the environment or the server to which the app is pointing to.
     */
    private static Environment environment = Environment.DEVELOPMENT;

    /**
     * Check the current environment of the app.
     *
     * @return current environment value of the app.
     */
    public static Environment getEnvironment() {
        if (BuildConfig.BUILD_TYPE.equalsIgnoreCase(Constant.DEBUG)) {
            return Environment.DEVELOPMENT;
            //return Environment.PRODUCTION;
        } else if (BuildConfig.BUILD_TYPE.equalsIgnoreCase(Constant.RELEASE)) {
            return Environment.PRODUCTION;
        } else if (BuildConfig.BUILD_TYPE.equalsIgnoreCase(Constant.DEMO)) {
            return Environment.DEMO;
        }
        return environment;
    }

    /**
     * Get domain based on the environment of the app.
     *
     * @return domain based on the app environment. (e.g. development/production)
     */
    public static String getDomain() {
        if (BuildConfig.BUILD_TYPE.equalsIgnoreCase("debug")) {
            return DEVELOPMENT_DOMAIN;
            //return PRODUCTION_DOMAIN;
        } else if (BuildConfig.BUILD_TYPE.equalsIgnoreCase("release")) {
            return PRODUCTION_DOMAIN;
        } else if (BuildConfig.BUILD_TYPE.equalsIgnoreCase("demo")) {
            return DEMO_DOMAIN;
        } else {
            return DEVELOPMENT_DOMAIN;
        }
        /*switch (environment) {
            case DEVELOPMENT:
                return DEVELOPMENT_DOMAIN;
            case PRODUCTION:
                return PRODUCTION_DOMAIN;
            case TESTING:
                return TESTING_DOMAIN;
            default:
                return "";
        }*/
    }

    public static String getLinkDomain() {
        if (BuildConfig.BUILD_TYPE.equalsIgnoreCase("debug")) {
            return DEV_DOMAIN;
            //return PRODUCTION_DOMAIN;
        } else if (BuildConfig.BUILD_TYPE.equalsIgnoreCase("release")) {
            return PROD_DOMAIN;
        } else if (BuildConfig.BUILD_TYPE.equalsIgnoreCase("demo")) {
            return DEV_DOMAIN;
        } else {
            return DEV_DOMAIN;
        }
    }

    public static boolean isDev() {
        if (BuildConfig.BUILD_TYPE.equalsIgnoreCase("debug")) {
            return true;
            //return PRODUCTION_DOMAIN;
        } else if (BuildConfig.BUILD_TYPE.equalsIgnoreCase("release")) {
            return false;
        } else if (BuildConfig.BUILD_TYPE.equalsIgnoreCase("demo")) {
            return true;
        } else {
            return true;
        }
    }

    // Domain/Server
    /*private static final String DEVELOPMENT_DOMAIN = "https://itap.infini.work/";
    private static final String TESTING_DOMAIN = "http://192.168.2.123:8085/";
    private static final String PRODUCTION_DOMAIN = getKeys();*/

    private static final String DEVELOPMENT_DOMAIN = getDevKeys();
    private static final String TESTING_DOMAIN = "http://192.168.2.123:8085/";
    private static final String PRODUCTION_DOMAIN = getProdKeys();
    private static final String DEV_DOMAIN = "https://dev.itap.online";
    private static final String PROD_DOMAIN = "https://stream.itap.online";

    // Demo Domains
    private static final String DEMO_DOMAIN = "https://demo1.itap.online/";

    //Domain/Server for Terms and Use,About, Contact Us, Privacy Policy and Refunds & Cancellation
    private static String DOMAIN_PAGES = "https://app.itap.online/static/";

    /**
     * Get API prefix.
     *
     * @return string with combination of domain and method.
     */
    private static String getAPIPrefix() {
        return getDomain() + "api/" + "?method=";
    }

    // Sign in
    public static String INITIATE_OTP_SIGNIN = getAPIPrefix() + "initiateOTPSignin";
    public static String OTP_SIGNIN = getAPIPrefix() + "otpSignin";

    public static String SOCIAL_SIGNIN = getAPIPrefix() + "socialSignin";
    public static String UPDATE_REG_ID = getAPIPrefix() + "updateRegisterIdByUserId";
    public static String PRESENTER_SIGNIN = getAPIPrefix() + "signin";

    public static String VERIFY_MOBILE_NUMBER = getAPIPrefix() + "verifyMobileNumber";
    public static String INITIATE_OTP_VERIFICATION = getAPIPrefix() + "initiateOTPVerification";

    // Language preference
    public static String GET_LANGUAGES = getAPIPrefix() + "getTerms&term=language";
    public static String SET_LANG_PREF = getAPIPrefix() + "setLangPref";

    // Survey Feedback
    public static String GET_SURVEY_QUESTIONS = getAPIPrefix() + "get_survey_lists";
    public static String SET_ANSWERS = getAPIPrefix() + "survey_preference";

    //GiveAway Offer Event
    public static String GET_CAMPAIGN = getAPIPrefix() + "getCampaign";
    public static String GET_USER_ENROLL_CAMPAIGN = getAPIPrefix() + "userEnrollCampaign";
    public static String GET_CAMPAIGN_WINNERS = getAPIPrefix() + "checkWinners";

    // User & User Subscription Details
    public static String GET_USER_DETAILS = getAPIPrefix() + "UserDetailsByIDv2";

    // Get All Coin Rules
    public static String GET_COIN_RULES = getAPIPrefix() + "getAllCoinsRules";

    // Manage Redeem
    public static String GET_REDEEM_TABS = getAPIPrefix() + "manageRedeem";

    //Manage Home
    public static String GET_HOME_TABS = getAPIPrefix() + "getTabDetails&tab_screen=";

    // Feeds
    public static String GET_FEEDS = getAPIPrefix() + "feeds&type=";

    // Live Feeds
    public static String GET_LIVE_FEEDS = getAPIPrefix() + "getAllPlaylistsData";

    //Live Now
    public static String GET_LIVE_NOW_FEED = getAPIPrefix() + "livesessions&type=livenow";

    //Live Upcoming
    public static String GET_LIVE_UPCOMING = getAPIPrefix() + "livesessions&type=upcoming";

    //Get Live Session details
    public static String GET_LIVE_SESSION_DETAILS = getAPIPrefix() + "preparelive&ID=";

    //Go Live with session Id
    public static String GET_GO_LIVE_SESSION = getAPIPrefix() + "golive&ID=";

    //Pause Live with session Id
    public static String GET_PAUSE_LIVE_SESSION = getAPIPrefix() + "pauselive&ID=";

    //Stop Live session
    public static String GET_STOP_LIVE_SESSION = getAPIPrefix() + "stoplive&ID=";

    //Get Live Session Questions
    public static String GET_QUESTIONS_LIVE_SESSION = getAPIPrefix() + "getsessionquestions&ID=";

    //Live Previous
    public static String GET_LIVE_PREVIOUS_FEED = getAPIPrefix() + "livesessions&type=past";

    //Send Pusher Message URL
    public static String SEND_PUSHER_MESSAGE = getAPIPrefix() + "comment";

    //Pusher Authentication
    public static String AUTHENTICATE_SUBSCRIBER = getAPIPrefix() + "authpushersubscriber";

    // Category
    public static String GET_CATEGORY = getAPIPrefix() + "categories&feed_type=";

    // Games
    public static String GET_GAME_LISTING = getAPIPrefix() + "games1&page_no=";

    //Games View ALL
    public static String GET_GAME_VIEW_ALL_LISTING = getAPIPrefix() + "games1&bucket=";

    // Channel
    public static String GET_CHANNEL = getAPIPrefix() + "channel";

    // Played Question
    public static String POST_PLAYED_QUESTION = getAPIPrefix() + "addActivity";

    // CouponFragment
    public static String GET_COUPAN = getAPIPrefix() + "coupons";
    public static String GET_REDEEN_COUPON = getAPIPrefix() + "redeem";

    // LuckyWinner
    public static String GET_LUCKY_WINNERS = getAPIPrefix() + "luckyWinners";

    //LeaderBoard
    public static String GET_LEADERBOARD = getAPIPrefix() + "leaderboard";

    //Notification
    public static String GET_NOTIFICATION = getAPIPrefix() + "notifications";
    public static String READ_NOTIFICATION = getAPIPrefix() + "notification_read";

    //Search
    public static String GET_SEARCH = getAPIPrefix() + "search&search=";

    // Fetch Series
    public static String FETCH_SERIES = getAPIPrefix() + "series&series=";

    // Single Player Game
    public static String QUESTIONS = getAPIPrefix() + "questions";

    //Like
    public static String LIKES = getAPIPrefix() + "saveLikes";
    public static String GET_LIKES_COMMENTS = getAPIPrefix() + "getLikesAndCommentsCount";

    //Insert Comment
    public static String INSERT_COMMENT = getAPIPrefix() + "insertComment";

    //Get Comment
    public static String GET_COMMENTS = getAPIPrefix() + "getPostComments";

    // Ads
    public static String GET_ADS = getAPIPrefix() + "ads&post_id=";

    // Similar Items
    public static String GET_SIMILAR_ITEMS = getAPIPrefix() + "similaritems&post_id=";

    // Episode listing
    public static String GET_EPISODE_LISTING = getAPIPrefix() + "episodes";

    // Playlist listing
    public static String GET_PLAYLIST_LISTING = getAPIPrefix() + "playlists";

    // Purchase History
    public static String GET_PURCHASE_HISTORY = getAPIPrefix() + "purchases&page_no=";

    // Play Single Media
    public static String GET_SINGLE_MEDIA_ITEM = getAPIPrefix() + "media";

    //Get Questions
    public static String GET_QUESTIONS = getAPIPrefix() + "postQuestions&ID=";

    //Get Seasons
    public static String GET_SEASONS = getAPIPrefix() + "seasons";

    // PAYMENT
    public static String INITIATE_ORDER = getAPIPrefix() + "order";
    public static String COMPLETE_ORDER = getAPIPrefix() + "completeOrder";

    // SUBSCRIPTIONS FOR PREMIUM MEMBERSHIP
    public static String FETCH_SUBSCRIPTIONS_URL = getAPIPrefix() + "get_subscription_url";
    public static String CHECK_ACTIVE_SUBSCRIPTION = getAPIPrefix() + "active_subscription";
    public static String GET_SUBSCRIPTION_PLANS = getAPIPrefix() + "getSubscriptionPlans";
    public static String SUBSCRIPTION_PAYMENT = getAPIPrefix() + "subscriptionPayment";
    public static String SUBSCRIPTION_STATUS_UPDATE = getAPIPrefix() + "subscriptionStatusUpdate";
    public static String SUBSCRIPTION_ACTIVE_COUPON = getAPIPrefix() + "subscriptionCouponsExist";
    public static String VALIDATE_COUPON_CODE = getAPIPrefix() + "validateCouponCode";
    public static String GET_SUBSCRIPTION_DETAILS = getAPIPrefix() + "getSubscriptionDetails";
    public static String CANCEL_SUBSCRIPTION = getAPIPrefix() + "cancelSubscription";

    //REDEEM ICOINS FOR CASH
    public static String REDEEM_COINS_FOR_CASH = getAPIPrefix() + "getCashByCoins";

    //CREDIT AND DEBIT COINS
    public static String CREDIT_COINS = getAPIPrefix() + "creditCoins";
    public static String DEBIT_COINS = getAPIPrefix() + "debitCoins";

    //SEND OTP
    public static String SEND_VERFICATION_OTP = getAPIPrefix() + "sendVerificationOTP";
    public static String ADD_REMOVE_WATCHLIST = getAPIPrefix() + "addToWatchlist&ID=";

    //VERIFY OTP
    public static String VERFICATION_OTP = getAPIPrefix() + "verifyAndSavev2";
    public static String GET_WATCHLIST = getAPIPrefix() + "watchlist";

    //Update Profile Name and Image
    public static String UPDATE_PROFILE = getAPIPrefix() + "saveProfile";
    public static String UPDATE_PROFILE_IMAGE = getAPIPrefix() + "saveProfileImagev2";

    //App Launcher Bonus
    public static String OPEN_APP_BONUS = getAPIPrefix() + "openapp";

    // Presenter Sessions
    public static String GET_PRESENTER_SESSION = getAPIPrefix() + "mysessions&page_no=";

    //Contact Us
    public static String CONTACT_US = DOMAIN_PAGES + "contact-us.html";

    //Terms of Use
    public static String TERMS_OF_USE = DOMAIN_PAGES + "terms-and-conditions.html";

    //Privacy Policy
    public static String PRIVACY_POLICY = DOMAIN_PAGES + "privacy-policy.html";

    //Refunds & Cancellation
    public static String REFUNDS_AND_CANCELLATION = DOMAIN_PAGES + "refund-and-cancellation-policy.html";

    //About
    public static String ABOUT = DOMAIN_PAGES + "about-us.html";

    //Ask Question
    public static String ASK_QUESTION = getAPIPrefix() + "askquestion";

    //Ask Question
    public static String REMIND_ME = getAPIPrefix() + "remindme";

    //Get Session Messages
    public static String GET_SESSION_MESSAGES = getAPIPrefix() + "getsessionmessages";

    //Get Global Setting
    public static String GET_GLOBAL_SETTING = getAPIPrefix() + "settings&allCountries=true";
    public static String GET_AD_MOB = getAPIPrefix() + "getAdmobs";

    //Coupons/vouchers
    public static String GET_ALL_COUPONS_DATA = getAPIPrefix() + "thridparty_coupons";
    public static String GET_COUPONS_STORE_OFFERS = getAPIPrefix() + "storecoupons";
    public static String GET_COUPONS_CAT_OFFERS = getAPIPrefix() + "categorycoupons";
    public static String REDEEM_COUPON = getAPIPrefix() + "redeemcoupon";
    public static String MY_COUPONS = getAPIPrefix() + "mycoupons";
    public static String TermsCondition = "https://www.coupondunia.in/terms-service";
    public static String COUPONS_VIEW_ALL = getAPIPrefix() + "couponfeed";

    // social profile connect
    public static String SOCIAL_CONNECT = getAPIPrefix() + "connect";

    // referral code
    public static String REFERRAL_CODE = getAPIPrefix() + "usereferralcodev2";

    // wallet
    public static String WALLET_EXPIRY = getAPIPrefix() + "coin_expiry";

    //couponCode
    public static String APPLY_COUPON_CODE = getAPIPrefix() + "apply_coupon";

    //earnCoins
    public static String EARN_COINS_CODE = getAPIPrefix() + "survey_status";
    public static String ADD_USER_SURVEY = getAPIPrefix() + "add_user_survay";

    //in-app notification
    public static String GET_IN_APP_NOTIFICATION = getAPIPrefix() + "in_app_notifications";
    public static String UPDATE_READ_NOTIFICATION = getAPIPrefix() + "read_in_app_notifications";
    public static String UPDATE_CLICKED_NOTIFICATION = getAPIPrefix() + "click_in_app_notifications";

    //eSports
    public static String PARTICIPATING_IN_TOURNAMENT = getAPIPrefix() + "participatingInTournament";
    public static String GET_USER_REGISTERED_TOURNAMENT_INFO = getAPIPrefix() + "getUserRegisteredTournamentInfo";
    public static String ESPORTS_PAYMEMT = getAPIPrefix() + "eSportsPayment";
    public static String ESPORTS_PAYMEMT_COMPLETE = getAPIPrefix() + "eSportsPaymentComplete";

    //Wallet
    public static String GET_WALLET_BALANCE = getAPIPrefix() + "userCoins";
    public static String GET_ICOINS_HISTORY = getAPIPrefix() + "getIcoinsHistory";

    public static String GET_POST_BY_ID = getAPIPrefix() + "getPostByID";

    public static String ADD_EVENT = getAPIPrefix() + "adEvent";

    public static String READY_PLAYER = "https://itap.readyplayer.me/avatar";

    public static String GET_COMMERCE_PRODUCTS = getAPIPrefix() + "getCommerceProducts";
    public static String COMMERCE_EVENT = getAPIPrefix() + "commerceEvent";

    public static String STORE_CRASH_DETAILS = getAPIPrefix() + "storeCrashDetails";

    public static String ANALYTICS = "https://9o7m8a8vbb.execute-api.ap-south-1.amazonaws.com/analytics";

    public static String STORE_DOWNLOAD_DETAILS = getAPIPrefix() + "storeDownloadDetails";

}
