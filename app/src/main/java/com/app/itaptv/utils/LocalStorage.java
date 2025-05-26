package com.app.itaptv.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;

import com.app.itaptv.MyApp;
import com.app.itaptv.roomDatabase.ListRoomDatabase;
import com.app.itaptv.roomDatabase.MediaDuration;
import com.app.itaptv.structure.AdMobData;
import com.app.itaptv.roomDatabase.AnalyticsData;
import com.app.itaptv.structure.CountryData;
import com.app.itaptv.structure.LanguagesData;
import com.app.itaptv.structure.LikeData;
import com.app.itaptv.structure.LocationData;
import com.app.itaptv.structure.SubscriptionDetails;
import com.app.itaptv.structure.TournamentData;
import com.app.itaptv.structure.User;
import com.app.itaptv.structure.WishedItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * A wrapper/abstraction layer over SharedPreferences
 * which features storage/retrieving data from/to SharedPreferences
 */
public class LocalStorage {

    private static final String APP_PREFS = "AppPrefs";

    public static final String KEY_AUTH_TOKEN = "X-AUTH-TOKEN";
    public static final String KEY_APP_VERSION = "X-APP-VERSION";
    public static final String X_REQUEST_ID = "X-REQUEST-ID";
    public static final String DEVICE_ID = "DEVICE-ID";
    public static final String SOURCE = "Source";
    public static final String COUNTRY = "Country";
    public static final String LANGUAGEPREF = "LanguagePref";
    public static final String KEY_APP_UPDATE = "X-APP-UPDATE";
    public static final String IS_PRESENTER_LOGGED_IN = "isPresenterLoggedIn";
    //public static final String KEY_PRESENTER_AUTH_TOKEN = "X-PRESENTER-AUTH-TOKEN";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_IS_LOGIN_SCREEN_SHOWN = "loginScreenShown";
    private static final String IS_USER_LOGGED_IN_KEY = "isUserLoggedIn";
    public static final String KEY_LANG_PREFER = "lang_prefs";
    private static final String KEY_ANSWERED = "answered";
    public static final String KEY_ACTIVITY_ID = "activity_id";
    public static final String KEY_USER_DATA = "UserData";
    public static final String KEY_LOCATION_DATA = "LocationData";
    public static final String KEY_APP_VERSION_NUMBER = "appVersion";
    public static final String KEY_APP_LOGOUT = "logout";
    public static final String KEY_ESPORTS_FEEDDATA = "esportsFeedContentData";
    public static final String KEY_ESPORTS_ID = "esportsFeedId";
    public static final String KEY_PLAYED_GAME = "playedGame";
    public static final String KEY_USER_ANALYTICS = "user_analytics";
    public static final String KEY_AVATAR_ANALYTICS = "avatar_analytics";
    public static final String KEY_SHOP_ANALYTICS = "shop_analytics";
    public static final String KEY_VIDEO_ANALYTICS = "shop_analytics";
    public static final String KEY_TIME = "time";

    public static final ArrayList<LikeData> KEY_MUSIC_LIKE_LIST = new ArrayList<>();
    public static final ArrayList<LikeData> KEY_MUSIC_COMMENT_LIST = new ArrayList<>();
    public static final ArrayList<WishedItem> KEY_MUSIC_WISHED_LIST = new ArrayList<>();

    // global setting
    public static final String KEY_PRACTICE_QUESTION_POINTS = "PracticeQuestionPoints";
    public static final String KEY_REFERRER_POINTS = "referrerPoints";
    public static final String KEY_REFERRAL_POINTS = "referralPoints";
    public static final String KEY_REGISTER_BONUS = "registerBonus";
    public static final String KEY_LIVE_MODULE = "liveModule";
    public static final String KEY_NEW_LIST_LIVE_MODULE = "liveLiveModule";
    public static final String KEY_APP_LINK = "appLink";
    public static final String KEY_NOTIFICATION_COUNT = "notificationCount";

    //Channel Ads configuration
    public static final String KEY_CHANNEL_ADS_ENABLED = "channelAdsEnabled";

    //User Premium Subscription
    public static final String KEY_USER_SUBSCRIPTION = "userSubscription";
    public static final String KEY_IS_USER_PREMIUM = "userPremium";
    public static final String STATUS_ACTIVE = "active";
    public static final String KEY_SUB_START_DATE = "subscriptionStartDate";
    public static final String KEY_SUB_EXPIRY_DATE = "subscriptionExpiryDate";
    public static final String KEY_MIN_SUB_AMOUNT = "subscriptionExpiryDate";
    public static final String KEY_SUB_IMAGE = "subscriptionOfferImage";
    public static final String KEY_GENERAL_LEARN_MORE = "generalLearnMoreContent";
    public static final String KEY_WIN_CASH_LEARN_MORE = "winCashLearnMoreContent";
    public static final String KEY_REDEEM_CASH_VALUE = "redeemCashValue";
    public static final String KEY_NUMBER_OF_COINS = "numberOfCoins";
    public static final String KEY_IS_BANNER_ACTIVE = "isBannerActive";

    public static final String KEY_WALLET_BALANCE = "walletBalance";
    public static final String KEY_DIAMONDS = "diamonds";

    public static final String KEY_SURVEY = "survey";
    public static final String KEY_ONCLICK_AD_MOB = "onclickadmob";
    public static final String KEY_BANNER_AD_MOB = "bannneradmob";
    public static final String KEY_AD_MOB_VIDEO = "admobs_video_hash";
    public static final String KEY_AD_MOB_BANNER = "admobs_image_hash";
    public static final String KEY_AVATAR_SCRIPT = "avatar_script";
    public static final String KEY_AVATAR_LOADER = "avatar_loader";
    public static final String KEY_AVATAR_VIEW_SCRIPT = "avatar_view_script";
    public static final String KEY_CAMPAIGNID = "campaignId";
    public static final String KEY_CAMPAIGN_PARAM_1 = "campaign_param_1";
    public static final String KEY_CAMPAIGN_PARAM_2 = "campaign_param_2";
    public static final String KEY_AVATAR_SHARE_MESSAGE = "avatar_share_text";
    public static final String KEY_ANALYTICS_LIST = "analyticsList";
    public static final String KEY_APP_LANGUAGE = "app_language";
    public static final String KEY_LANGUAGES_LIST = "languages_list";
    public static final String KEY_COUNTRIES_LIST = "countries_list";
    public static final String KEY_INSTALLATION = "install";
    public static final String KEY_TERMS_POLICIES = "termsNPolicies";

    public static boolean isWinner = false;

    static List<AnalyticsData> analyticsDataList = new ArrayList<>();

    // Get instance of shared preferences in private mode
    private static SharedPreferences getSharedPrefs() {
        return MyApp.sharedContext().getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
    }

    // Get instance of Editor
    private static SharedPreferences.Editor getEditor() {
        return getSharedPrefs().edit();
    }


    /**
     * Get locally stored value for specific key in the desired data type
     * The value can be null if it does not exist or cannot be casted to the desired data type
     *
     * @param key          name of key
     * @param defaultValue default value to map to the key in case not found
     * @param type         type of class (e.g. String, Boolean, Integer) used to store value
     * @param <T>          Generic type
     * @return returns value mapped to the key. Can be of any type
     */
    public static <T> T getValue(String key, Object defaultValue, Class<T> type) {
        Object value = null;
        SharedPreferences sharedPrefs = getSharedPrefs();
        if (type.equals(String.class)) {    // String
            String defVal = "";
            if (defaultValue instanceof String) {
                defVal = (String) defaultValue;
            }
            try {
                value = sharedPrefs.getString(key, defVal);
            } catch (Exception e) {
                android.util.Log.e("LocalStorage", "Error getting value for key: " + key + "\nError: " + e.getMessage());
            }

        } else if (type.equals(Integer.class)) {    // Integer
            int defVal = 0;
            if (defaultValue instanceof Integer) {
                defVal = (int) defaultValue;
            }
            try {
                value = sharedPrefs.getInt(key, defVal);
            } catch (Exception e) {
                android.util.Log.e("LocalStorage", "Error getting value for key: " + key + "\nError: " + e.getMessage());
            }

        } else if (type.equals(Float.class)) {  // Float
            float defVal = 0;
            if (defaultValue instanceof Float) {
                defVal = (float) defaultValue;
            }
            try {
                value = sharedPrefs.getFloat(key, defVal);
            } catch (Exception e) {
                android.util.Log.e("LocalStorage", "Error getting value for key: " + key + "\nError: " + e.getMessage());
            }

        } else if (type.equals(Boolean.class)) {    // Boolean
            boolean defVal = false;
            if (defaultValue instanceof Boolean) {
                defVal = (boolean) defaultValue;
            }
            try {
                value = sharedPrefs.getBoolean(key, defVal);
            } catch (Exception e) {
                android.util.Log.e("LocalStorage", "Error getting value for key: " + key + "\nError: " + e.getMessage());
            }

        } else if (type.equals(Long.class)) {   // Long
            long defVal = 0;
            if (defaultValue instanceof Long) {
                defVal = (long) defaultValue;
            }
            try {
                value = sharedPrefs.getLong(key, defVal);
            } catch (Exception e) {
                android.util.Log.e("LocalStorage", "Error getting value for key: " + key + "\nError: " + e.getMessage());
            }

        }
        return type.cast(value);
    }


    /**
     * Store data locally for specific key
     *
     * @param value store value
     * @param key   name of key to map value against
     */
    public static void putValue(Object value, String key) {
        SharedPreferences.Editor editor = getEditor();
        if (value instanceof String) {    // String
            editor.putString(key, (String) value);

        } else if (value instanceof Integer) {    // Integer
            editor.putInt(key, (int) value);

        } else if (value instanceof Float) {  // Float
            editor.putFloat(key, (float) value);

        } else if (value instanceof Boolean) {    // Boolean
            editor.putBoolean(key, (boolean) value);

        } else if (value instanceof Long) {   // Long
            editor.putLong(key, (long) value);
        }
        editor.commit();
    }

    public static void setLoginScreenShown(boolean shown) {
        putValue(shown, KEY_IS_LOGIN_SCREEN_SHOWN);
    }

    public static boolean isLoginScreenShown() {
        return getValue(KEY_IS_LOGIN_SCREEN_SHOWN, false, Boolean.class);
    }

    public static String getUserId() {
        return getValue(KEY_USER_ID, "", String.class);
    }

    public static void setUserId(@NonNull String userId) {
        putValue(userId, KEY_USER_ID);
    }

    public static String getToken() {
        return getValue(KEY_AUTH_TOKEN, "", String.class);
    }

    public static String getAppVersion() {
        String version = "";
        try {
            PackageInfo pInfo = MyApp.getAppContext().getPackageManager().getPackageInfo(MyApp.getAppContext().getPackageName(), 0);
            if (Utility.isTelevision()) {
                version = "300.0.0";
            } else {
                version = pInfo.versionName;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    public static void setLoggedInStatus(boolean isLoggedIn, boolean isPresenterLoggedIn) {
        putValue(isLoggedIn, IS_USER_LOGGED_IN_KEY);
        putValue(isPresenterLoggedIn, IS_PRESENTER_LOGGED_IN);
    }

    public static String getFcmToken() {
        return getValue(Constant.KEY_FCM_TOKEN, "", String.class);
    }

    public static boolean isUserLoggedIn() {
        return getValue(IS_USER_LOGGED_IN_KEY, false, Boolean.class);
    }

    public static void clearData() {
        SharedPreferences.Editor sharesPrefEdit = getEditor();
        sharesPrefEdit.clear();
        sharesPrefEdit.commit();
    }

    public static void setAnswered(int id) {
        String ans = getAnswered();
        try {
            JSONArray a = ans.equals("") ? new JSONArray() : new JSONArray(ans);
            a.put(id);
            LocalStorage.putValue(a.toString(), KEY_ANSWERED);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static boolean isAnswered(int id) {
        String ans = getAnswered();
        try {
            JSONArray a = ans.equals("") ? new JSONArray() : new JSONArray(ans);
            for (int i = 0; i < a.length(); i++) {
                if (id == a.getInt(i)) {
                    return true;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static String getAnswered() {
        return getValue(KEY_ANSWERED, "", String.class);
    }

    public static void setLiked(int id, String likecount, boolean like) {
        ArrayList<LikeData> arrlike = KEY_MUSIC_LIKE_LIST;
        ArrayList<LikeData> a = arrlike.size() == 0 ? new ArrayList<>() : arrlike;
        LikeData likeData = new LikeData();
        likeData.setId(String.valueOf(id));
        likeData.setCount(likecount);
        likeData.setLike(like);
        for (int i = 0; i < a.size(); i++) {
            if (a.get(i).getId().equalsIgnoreCase(String.valueOf(id))) {
                a.remove(i);
                i--;
            }
        }
        a.add(likeData);
        KEY_MUSIC_LIKE_LIST.addAll(a);
    }

    public static boolean isLiked(int id) {
        ArrayList<LikeData> arrlike = KEY_MUSIC_LIKE_LIST;
        ArrayList<LikeData> a = arrlike.size() == 0 ? new ArrayList<>() : arrlike;
        for (int i = 0; i < arrlike.size(); i++) {
            if (arrlike.get(i).getId().equalsIgnoreCase(String.valueOf(id))) {
                return true;
            }
        }
        return false;
    }

    public static void setWishedItem(int id, boolean isWished, Context mContext) {
        ArrayList<WishedItem> arrWish = KEY_MUSIC_WISHED_LIST;
        arrWish = arrWish.size() == 0 ? new ArrayList<>() : arrWish;
        if (arrWish.size() == 0 && getArrayList("wishList", mContext) != null) {
            arrWish = getArrayList("wishList", mContext);
        }

        if (isWished) {
            WishedItem item = new WishedItem();
            item.setId(String.valueOf(id));
            for (int i = 0; i < arrWish.size(); i++) {
                if (Integer.parseInt(arrWish.get(i).getId()) == Integer.parseInt(String.valueOf(id))) {
                    KEY_MUSIC_WISHED_LIST.remove(i);
                    saveArrayList(KEY_MUSIC_WISHED_LIST, "wishList", mContext);
                    break;
                }
            }
            KEY_MUSIC_WISHED_LIST.add(item);
        } else {
            for (int i = 0; i < arrWish.size(); i++) {
                if (Integer.parseInt(arrWish.get(i).getId()) == Integer.parseInt(String.valueOf(id))) {
                    KEY_MUSIC_WISHED_LIST.remove(i);
                    saveArrayList(KEY_MUSIC_WISHED_LIST, "wishList", mContext);
                    break;
                }
            }
        }
        Log.e("avinash", "List count = " + KEY_MUSIC_WISHED_LIST.size());
    }

    public static void saveArrayList(ArrayList<WishedItem> list, String key, Context mContext) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.commit();
        editor.apply();     // This line is IMPORTANT !!!
    }

    public static ArrayList<WishedItem> getArrayList(String key, Context mContext) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<WishedItem>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public static void saveLanguageArrayList(ArrayList<Integer> list, String key, Context mContext) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.commit();
        editor.apply();     // This line is IMPORTANT !!!
    }

    public static ArrayList<Integer> getLanguageArrayList(String key, Context mContext) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<Integer>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public static boolean isAddedToWishList(int id, Context mContext) {
        Log.e("avinash", "isAddedToWishList");
        Log.e("avinash", "id = " + id);
        ArrayList<WishedItem> arrWish = KEY_MUSIC_WISHED_LIST;
        ArrayList<WishedItem> a = arrWish.size() == 0 ? new ArrayList<>() : arrWish;

        if (arrWish.size() == 0 && getArrayList("wishList", mContext) != null) {
            arrWish = getArrayList("wishList", mContext);
        }
        for (int i = 0; i < arrWish.size(); i++) {
            if (Integer.parseInt(arrWish.get(i).getId()) == Integer.parseInt(String.valueOf(id))) {
                return true;
            }
            Log.e("avinash", "List id = " + arrWish.get(i).getId());
        }
        return false;
    }


    public static LikeData getLikes(int id) {
        LikeData likeData = null;
        ArrayList<LikeData> arrlike = KEY_MUSIC_LIKE_LIST;
        ArrayList<LikeData> a = arrlike.size() == 0 ? new ArrayList<>() : arrlike;
        for (int i = 0; i < arrlike.size(); i++) {
            if (arrlike.get(i).getId().equalsIgnoreCase(String.valueOf(id))) {
                likeData = arrlike.get(i);
                return likeData;
            }
        }
        return likeData;
    }

    public static void logout() {
        // Clear user and login status flags and preferences
        LocalStorage.putValue("", LocalStorage.KEY_AUTH_TOKEN);
        LocalStorage.putValue("", LocalStorage.KEY_USER_ID);
        LocalStorage.putValue(false, LocalStorage.IS_USER_LOGGED_IN_KEY);
        LocalStorage.putValue(false, LocalStorage.IS_PRESENTER_LOGGED_IN);
        LocalStorage.putValue("", Constant.KEY_FCM_TOKEN);
        LocalStorage.putValue("", LocalStorage.KEY_USER_DATA);
        KEY_MUSIC_LIKE_LIST.clear();
        KEY_MUSIC_COMMENT_LIST.clear();
        LocalStorage.setLoggedInStatus(false, false);
        LocalStorage.clearData();
        ListRoomDatabase listRoomDatabase = ListRoomDatabase.getDatabase(MyApp.getAppContext());
        List<MediaDuration> mediaDurationList = listRoomDatabase.mediaDAO().getAllMediaDurationData();
        listRoomDatabase.mediaDAO().deletePlayerDuration(mediaDurationList);
    }

    public static void setUserData(User user) {
        Gson gson = new Gson();
        String json = gson.toJson(user);
        putValue(json, KEY_USER_DATA);
    }

    public static User getUserData() {
        Gson gson = new Gson();
        String userdata = LocalStorage.getValue(LocalStorage.KEY_USER_DATA, "", String.class);
        return gson.fromJson(userdata, User.class);
    }

    public static void setUserSubscriptionDetails(SubscriptionDetails subscription) {
        Gson gson = new Gson();
        String json = gson.toJson(subscription);
        putValue(json, KEY_USER_SUBSCRIPTION);
    }

    public static SubscriptionDetails getUserSubscriptionDetails() {
        Gson gson = new Gson();
        String subscription = LocalStorage.getValue(LocalStorage.KEY_USER_SUBSCRIPTION, "", String.class);
        return gson.fromJson(subscription, SubscriptionDetails.class);
    }

    public static void setUserPremium(boolean isPremium) {
        putValue(isPremium, KEY_IS_USER_PREMIUM);
    }

    public static boolean isUserPremium() {
        return getValue(KEY_IS_USER_PREMIUM, false, Boolean.class);
    }

    public static void setSubStartDate(String startDate) {
        putValue(startDate, KEY_SUB_START_DATE);
    }

    public static String getSubStartDate() {
        return getValue(KEY_SUB_START_DATE, null, String.class);
    }

    public static void setSubEndDate(String expDate) {
        putValue(expDate, KEY_SUB_EXPIRY_DATE);
    }

    public static String getSubEndDate() {
        return getValue(KEY_SUB_EXPIRY_DATE, null, String.class);
    }

    public static void setCommented(int id, String likecount, boolean like) {
        ArrayList<LikeData> arrlike = KEY_MUSIC_COMMENT_LIST;
        ArrayList<LikeData> a = arrlike.size() == 0 ? new ArrayList<>() : arrlike;
        LikeData likeData = new LikeData();
        likeData.setId(String.valueOf(id));
        likeData.setCount(likecount);
        likeData.setLike(like);
        for (int i = 0; i < arrlike.size(); i++) {
            if (arrlike.get(i).getId().equalsIgnoreCase(String.valueOf(id))) {
                arrlike.remove(i);
                break;
            }
        }
        a.add(likeData);
        KEY_MUSIC_COMMENT_LIST.addAll(a);
    }

    public static boolean isCommented(int id) {
        ArrayList<LikeData> arrlike = KEY_MUSIC_COMMENT_LIST;
        ArrayList<LikeData> a = arrlike.size() == 0 ? new ArrayList<>() : arrlike;
        for (int i = 0; i < arrlike.size(); i++) {
            if (arrlike.get(i).getId().equalsIgnoreCase(String.valueOf(id))) {
                return true;
            }
        }
        return false;
    }

    public static LikeData getComments(int id) {
        LikeData likeData = null;
        ArrayList<LikeData> arrlike = KEY_MUSIC_COMMENT_LIST;
        ArrayList<LikeData> a = arrlike.size() == 0 ? new ArrayList<>() : arrlike;
        for (int i = 0; i < arrlike.size(); i++) {
            if (arrlike.get(i).getId().equalsIgnoreCase(String.valueOf(id))) {
                likeData = arrlike.get(i);
                return likeData;
            }
        }

        return likeData;
    }

    // global setting
    public static void setPractiseQuesPoints(String points) {
        putValue(points, KEY_PRACTICE_QUESTION_POINTS);
    }

    public static String getPractiseQuesPoints() {
        return getValue(KEY_PRACTICE_QUESTION_POINTS, null, String.class);
    }

    public static void setLiveModuleStatus(boolean status) {
        putValue(status, KEY_LIVE_MODULE);
    }

    public static boolean getLiveModuleStatus() {
        return getValue(KEY_LIVE_MODULE, false, Boolean.class);
    }

    public static void setLiveListingModuleStatus(boolean status) {
        putValue(status, KEY_NEW_LIST_LIVE_MODULE);
    }

    public static boolean getLiveListingModuleStatus() {
        return getValue(KEY_NEW_LIST_LIVE_MODULE, false, Boolean.class);
    }

    public static void setRegisterBonus(long bonus) {
        putValue(bonus, KEY_REGISTER_BONUS);
    }

    public static Long getRegisterBonus() {
        return getValue(KEY_REGISTER_BONUS, "0", Long.class);
    }

    public static void setReferrerPoints(String bonus) {
        putValue(bonus, KEY_REFERRER_POINTS);
    }

    public static String getReferrerPoints() {
        return getValue(KEY_REFERRER_POINTS, null, String.class);
    }

    public static void setReferralPoints(String bonus) {
        putValue(bonus, KEY_REFERRAL_POINTS);
    }

    public static String getReferralPoints() {
        return getValue(KEY_REFERRAL_POINTS, null, String.class);
    }

    public static void setAppLink(String link) {
        putValue(link, KEY_APP_LINK);
    }

    public static String getAppLink() {
        return getValue(KEY_APP_LINK, null, String.class);
    }

    public static void setNotificationCount(int count) {
        putValue(count, KEY_NOTIFICATION_COUNT);
    }

    public static int getNotificationCount() {
        return getValue(KEY_NOTIFICATION_COUNT, 0, Integer.class);
    }

    public static void setChannelAdsEnabled(boolean isChannelPlayAd) {
        putValue(isChannelPlayAd, KEY_CHANNEL_ADS_ENABLED);
    }

    public static boolean getChannelAdsEnabled() {
        return getValue(KEY_CHANNEL_ADS_ENABLED, true, Boolean.class);
    }

    public static void setIsBannerActive(boolean isBannerActive) {
        putValue(isBannerActive, KEY_IS_BANNER_ACTIVE);
    }

    public static boolean getIsBannerActive() {
        return getValue(KEY_IS_BANNER_ACTIVE, true, Boolean.class);
    }

    public static void setMinSubAmount(String minSubAmount) {
        putValue(minSubAmount, KEY_MIN_SUB_AMOUNT);
    }

    public static String getMinSubAmount() {
        return getValue(KEY_MIN_SUB_AMOUNT, null, String.class);
    }

    public static void setSubsOfferImage(String subOfferImage) {
        putValue(subOfferImage, KEY_SUB_IMAGE);
    }

    public static String getSubsOfferImage() {
        return getValue(KEY_SUB_IMAGE, null, String.class);
    }

    public static void setGeneralLearnMoreContent(String generalLearnMoreContent) {
        putValue(generalLearnMoreContent, KEY_GENERAL_LEARN_MORE);
    }

    public static String getGeneralLearnMoreContent() {
        return getValue(KEY_GENERAL_LEARN_MORE, null, String.class);
    }

    public static void setWinCashLearnMoreContent(String winCashLearnMoreContent) {
        putValue(winCashLearnMoreContent, KEY_WIN_CASH_LEARN_MORE);
    }

    public static String getWinCashLearnMoreContent() {
        return getValue(KEY_WIN_CASH_LEARN_MORE, null, String.class);
    }

    public static void setRedeemCashValue(String redeemCashValue) {
        putValue(redeemCashValue, KEY_REDEEM_CASH_VALUE);
    }

    public static String getRedeemCashValue() {
        return getValue(KEY_REDEEM_CASH_VALUE, null, String.class);
    }

    public static void setNumberOfCoins(String numberOfCoins) {
        putValue(numberOfCoins, KEY_NUMBER_OF_COINS);
    }

    public static String getNumberOfCoins() {
        return getValue(KEY_NUMBER_OF_COINS, null, String.class);
    }

    public static void saveLicence(String postId, String licence) {
        putValue(licence, postId);
    }

    public static String getLicence(String postId) {
        return getValue(postId, "", String.class);
    }

    public static boolean isWinner() {
        return isWinner;
    }

    public static void setWinner(boolean winner) {
        isWinner = winner;
    }

    public static void setSurveyHash(String surveyHash) {
        putValue(surveyHash, KEY_SURVEY);
    }

    public static void setKeyAdMobVideo(String surveyHash) {
        putValue(surveyHash, KEY_AD_MOB_VIDEO);
    }

    public static void setKeyAdMobBanner(String surveyHash) {
        putValue(surveyHash, KEY_AD_MOB_BANNER);
    }

    public static String getSurveyHash() {
        return getValue(KEY_SURVEY, "", String.class);
    }

    public static String getKeyAdMobVideo() {
        return getValue(KEY_AD_MOB_VIDEO, "", String.class);
    }

    public static String getKeyAdMobBanner() {
        return getValue(KEY_AD_MOB_BANNER, "", String.class);
    }

    public static void setAvatarScript(String avatarScript) {
        putValue(avatarScript, KEY_AVATAR_SCRIPT);
    }

    public static String getAvatarScript() {
        return getValue(KEY_AVATAR_SCRIPT, "", String.class);
    }

    public static void setAvatarViewScript(String avatarViewScript) {
        putValue(avatarViewScript, KEY_AVATAR_VIEW_SCRIPT);
    }

    public static String getAvatarViewScript() {
        return getValue(KEY_AVATAR_VIEW_SCRIPT, "", String.class);
    }

    public static void setAvatarLoader(String avatarLoader) {
        putValue(avatarLoader, KEY_AVATAR_LOADER);
    }

    public static String getAvatarLoader() {
        return getValue(KEY_AVATAR_LOADER, "", String.class);
    }

    public static void setCampaignId(String campaignId) {
        putValue(campaignId, KEY_CAMPAIGNID);
    }

    public static String getCampaignId() {
        return getValue(KEY_CAMPAIGNID, "", String.class);
    }

    public static void setCampaignParam1(String campaignId) {
        putValue(campaignId, KEY_CAMPAIGN_PARAM_1);
    }

    public static String getCampaignParam1() {
        return getValue(KEY_CAMPAIGN_PARAM_1, "", String.class);
    }

    public static void setCampaignParam2(String campaignId) {
        putValue(campaignId, KEY_CAMPAIGN_PARAM_2);
    }

    public static String getCampaignParam2() {
        return getValue(KEY_CAMPAIGN_PARAM_2, "", String.class);
    }

    public static void setAvatarShareMessage(String avatarShareMessage) {
        putValue(avatarShareMessage, KEY_AVATAR_SHARE_MESSAGE);
    }

    public static String getAvatarShareMessage() {
        return getValue(KEY_AVATAR_SHARE_MESSAGE, "", String.class);
    }

    public static void setTermsNPoliciesText(String message) {
        putValue(message, KEY_TERMS_POLICIES);
    }

    public static String getTermsNPoliciesText() {
        return getValue(KEY_TERMS_POLICIES, "", String.class);
    }


    public static void savePurchasedArrayList(ArrayList<String> list, String key, Context mContext) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.commit();
        editor.apply();     // This line is IMPORTANT !!!
    }

    public static ArrayList<String> getPurchasedArrayList(String key, Context mContext) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public static void saveRequiredVersion(String version, String key, Context mContext) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, version);
        editor.commit();
        editor.apply();
    }

    public static String getRequiredVersion(String key, Context mContext) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        String version = prefs.getString(key, null);
        return version;
    }

    public static void saveIsNeedLogout(String isNeed, String key, Context mContext) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, isNeed);
        editor.commit();
        editor.apply();
    }

    public static String getIsNeedLogout(String key, Context mContext) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        String version = prefs.getString(key, null);
        return version;
    }

    public static void saveEsportsData(TournamentData tournamentData) {
        Gson gson = new Gson();
        String json = gson.toJson(tournamentData);
        putValue(json, KEY_ESPORTS_FEEDDATA);
    }

    public static TournamentData getEsportsData() {
        Gson gson = new Gson();
        String tournamentData = LocalStorage.getValue(LocalStorage.KEY_ESPORTS_FEEDDATA, "", String.class);
        return gson.fromJson(tournamentData, TournamentData.class);
    }


    public static void saveOnClickAdMobList(List<AdMobData> list, String key, Context mContext) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.commit();
        editor.apply();     // This line is IMPORTANT !!!
    }

    public static List<AdMobData> getOnClickAdMobList(String key, Context mContext) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<List<AdMobData>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public static void saveBannerAdMobList(List<AdMobData> list, String key, Context mContext) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.commit();
        editor.apply();     // This line is IMPORTANT !!!
    }

    public static List<AdMobData> getBannerAdMobList(String key, Context mContext) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<List<AdMobData>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public static void savePlayedGameData(AnalyticsData analyticsData) {
        Gson gson = new Gson();
        String json = gson.toJson(analyticsData);
        putValue(json, KEY_PLAYED_GAME);
    }

    public static AnalyticsData getPlayedGameData() {
        Gson gson = new Gson();
        String analyticsData = LocalStorage.getValue(LocalStorage.KEY_PLAYED_GAME, "", String.class);
        return gson.fromJson(analyticsData, AnalyticsData.class);
    }

    public static void saveUserAnalytics(AnalyticsData analyticsData) {
        Gson gson = new Gson();
        String json = gson.toJson(analyticsData);
        putValue(json, KEY_USER_ANALYTICS);
    }

    public static AnalyticsData getUserAnalytics() {
        Gson gson = new Gson();
        String analyticsData = LocalStorage.getValue(LocalStorage.KEY_USER_ANALYTICS, "", String.class);
        return gson.fromJson(analyticsData, AnalyticsData.class);
    }

    public static void saveAvatarAnalyticsData(AnalyticsData analyticsData) {
        Gson gson = new Gson();
        String json = gson.toJson(analyticsData);
        putValue(json, KEY_AVATAR_ANALYTICS);
    }

    public static AnalyticsData getAvatarAnalyticsData() {
        Gson gson = new Gson();
        String analyticsData = LocalStorage.getValue(LocalStorage.KEY_AVATAR_ANALYTICS, "", String.class);
        return gson.fromJson(analyticsData, AnalyticsData.class);
    }

    public static void saveShopAnalyticsData(AnalyticsData analyticsData) {
        Gson gson = new Gson();
        String json = gson.toJson(analyticsData);
        putValue(json, KEY_SHOP_ANALYTICS);
    }

    public static AnalyticsData getShopAnalyticsData() {
        Gson gson = new Gson();
        String analyticsData = LocalStorage.getValue(LocalStorage.KEY_SHOP_ANALYTICS, "", String.class);
        return gson.fromJson(analyticsData, AnalyticsData.class);
    }

    public static void saveVideoAnalyticsData(AnalyticsData analyticsData) {
        Gson gson = new Gson();
        String json = gson.toJson(analyticsData);
        putValue(json, KEY_VIDEO_ANALYTICS);
    }

    public static AnalyticsData getVideoAnalyticsData() {
        Gson gson = new Gson();
        String analyticsData = LocalStorage.getValue(LocalStorage.KEY_VIDEO_ANALYTICS, "", String.class);
        return gson.fromJson(analyticsData, AnalyticsData.class);
    }

    public static void setLocationData(LocationData locationData) {
        Gson gson = new Gson();
        String json = gson.toJson(locationData);
        putValue(json, KEY_LOCATION_DATA);
    }

    public static LocationData getLocationData() {
        Gson gson = new Gson();
        String locationData = LocalStorage.getValue(LocalStorage.KEY_LOCATION_DATA, "", String.class);
        return gson.fromJson(locationData, LocationData.class);
    }

    public static void saveLanguagesList(List<LanguagesData> list, String key, Context mContext) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.commit();
        editor.apply();     // This line is IMPORTANT !!!
    }

    public static List<LanguagesData> getLanguagesList(String key, Context mContext) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<List<LanguagesData>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public static void saveCountriesList(List<CountryData> list, String key, Context mContext) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.commit();
        editor.apply();     // This line is IMPORTANT !!!
    }

    public static List<CountryData> getCountriesList(String key, Context mContext) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<List<CountryData>>() {
        }.getType();
        return gson.fromJson(json, type);
    }


    public static String getSelectedLanguage(Context mContext) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        String version = prefs.getString(KEY_APP_LANGUAGE, "");
        return version;
    }

    public static void setSelectedLanguage(@NonNull String selectedLanguage, Context mContext) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_APP_LANGUAGE, selectedLanguage);
        editor.commit();
        editor.apply();
    }

    public static boolean getIsFirstInstall(Context mContext) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        boolean isFirstInstall = prefs.getBoolean(KEY_INSTALLATION, true);
        return isFirstInstall;
    }

    public static void setIsFirstInstall(@NonNull boolean isFirstInstall, Context mContext) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_INSTALLATION, isFirstInstall);
        editor.commit();
        editor.apply();
    }

}
