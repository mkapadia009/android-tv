package com.app.itaptv.API;

import android.content.Context;
import android.content.Intent;

import com.android.volley.Request;
import com.app.itaptv.MyApp;
import com.app.itaptv.R;
import com.app.itaptv.activity.HomeActivity;
import com.app.itaptv.structure.AdMobData;
import com.app.itaptv.structure.CountryData;
import com.app.itaptv.structure.LanguagesData;
import com.app.itaptv.structure.SubscriptionDetails;
import com.app.itaptv.structure.User;
import com.app.itaptv.utils.AlertUtils;
import com.app.itaptv.utils.Constant;
import com.app.itaptv.utils.LocalStorage;
import com.app.itaptv.utils.Log;
import com.app.itaptv.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class APIMethods {

    public static void getGlobalSetting(Context mContext, GlobalSettingCallBack globalSettingCallBack) {
        APIRequest request = new APIRequest(Url.GET_GLOBAL_SETTING, Request.Method.GET, null, null, mContext);
        request.showLoader = false;
        APIManager.request(request, (response, error, headers, statusCode) -> {
            if (response != null) {
                try {
                    Log.i("response..",response.toString());
                    JSONObject rootObject = new JSONObject(response);
                    String type = rootObject.has("type") ? rootObject.getString("type") : "";
                    if (type.equals("OK")) {
                        JSONObject messageObject = rootObject.has("msg") ? rootObject.getJSONObject("msg") : null;
                        if (messageObject != null) {

                            boolean liveModule = messageObject.has("live_module") ? messageObject.getBoolean("live_module") : false;
                            LocalStorage.setLiveModuleStatus(liveModule);

                            boolean liveNewModule = messageObject.has("live_channel_playlist") ? messageObject.getBoolean("live_channel_playlist") : false;
                            LocalStorage.setLiveListingModuleStatus(liveNewModule);

                            String points = messageObject.has("practice_question_points") ? messageObject.getString("practice_question_points") : null;

                            if (points != null) {
                                LocalStorage.setPractiseQuesPoints(points);
                            }

                            String registerBonus = messageObject.has("register_bonus") ? messageObject.getString("register_bonus") : null;
                            if (registerBonus != null) {
                                LocalStorage.setRegisterBonus(Long.parseLong(registerBonus));
                            }

                            String referrerPoints = messageObject.has("referrer_points") ? messageObject.getString("referrer_points") : null;
                            if (referrerPoints != null) {
                                LocalStorage.setReferrerPoints(referrerPoints);
                            }

                            String referralPoints = messageObject.has("referee_points") ? messageObject.getString("referee_points") : null;
                            if (referralPoints != null) {
                                LocalStorage.setReferralPoints(referralPoints);
                            }

                            String appLink = messageObject.has("app_link") ? messageObject.getString("app_link") : null;
                            if (appLink != null) {
                                LocalStorage.setAppLink(appLink);
                            }

                            int count = messageObject.has("notification_count") ? messageObject.getInt("notification_count") : 0;
                            LocalStorage.setNotificationCount(count);

                            if (messageObject.has("channelAdsEnabled")) {
                                boolean channelAd = messageObject.getBoolean("channelAdsEnabled");
                                LocalStorage.setChannelAdsEnabled(channelAd);
                            }

                            String minSubAmount = messageObject.has("min_subscription_amount") ? messageObject.getString("min_subscription_amount")
                                    : mContext.getResources().getString(R.string.min_sub_amount_default);
                            if (minSubAmount != null) {
                                LocalStorage.setMinSubAmount(minSubAmount);
                            }

                            String subsOfferImage = messageObject.has("subscription_offer_image") ? messageObject.getString("subscription_offer_image")
                                    : null;
                            if (subsOfferImage != null) {
                                LocalStorage.setSubsOfferImage(subsOfferImage);
                            }

                            String generalLearnMoreContent = messageObject.has("general_learn_more") ? messageObject.getString("general_learn_more")
                                    : mContext.getResources().getString(R.string.learn_more_description);
                            if (generalLearnMoreContent != null) {
                                LocalStorage.setGeneralLearnMoreContent(generalLearnMoreContent);
                            }

                            String winCashLearnMoreContent = messageObject.has("win_cash_learn_more") ? messageObject.getString("win_cash_learn_more")
                                    : mContext.getResources().getString(R.string.learn_more_description);
                            if (winCashLearnMoreContent != null) {
                                LocalStorage.setWinCashLearnMoreContent(winCashLearnMoreContent);
                            }

                            String redeem_cash = messageObject.has("redeem_cash") ? messageObject.getString("redeem_cash")
                                    : "N/A";
                            if (redeem_cash != null) {
                                LocalStorage.setRedeemCashValue(redeem_cash);
                            }

                            String numberOfCoins = messageObject.has("rate_of_coins") ? messageObject.getString("rate_of_coins")
                                    : "N/A";
                            if (numberOfCoins != null) {
                                LocalStorage.setNumberOfCoins(numberOfCoins);
                            }

                            String appVersion = messageObject.has("app_version") ? messageObject.getString("app_version")
                                    : null;
                            if (appVersion != null) {
                                LocalStorage.saveRequiredVersion(appVersion, LocalStorage.KEY_APP_VERSION_NUMBER, MyApp.getAppContext());
                                if (!appVersion.isEmpty()) {
                                    String versionName = LocalStorage.getAppVersion();
                                    if (Utility.isAppUpdateRequired(appVersion, versionName)) {
                                        MyApp.getAppContext().sendBroadcast(new Intent("mandatoryappupdate"));
                                    }
                                    /*if (!appVersion.equals(versionName)) {
                                        MyApp.getAppContext().sendBroadcast(new Intent("mandatoryappupdate"));
                                    }*/
                                }
                            }

                            String surveyHash = messageObject.has("live_survey_id") ? messageObject.getString("live_survey_id") : null;
                            if (surveyHash != null) {
                                LocalStorage.setSurveyHash(surveyHash);
                            }

                            String adMobVideoKey = messageObject.has("admobs_video_hash") ? messageObject.getString("admobs_video_hash") : null;
                            if (surveyHash != null) {
                                LocalStorage.setKeyAdMobVideo(adMobVideoKey);
                            }

                            String adMobBannerKey = messageObject.has("admobs_image_hash") ? messageObject.getString("admobs_image_hash") : null;
                            if (surveyHash != null) {
                                LocalStorage.setKeyAdMobBanner(adMobBannerKey);
                            }

                            String avatarScript = messageObject.has("avatar_script") ? messageObject.getString("avatar_script") : null;
                            if (avatarScript != null) {
                                LocalStorage.setAvatarScript(avatarScript);
                            }

                            String avatarViewScript = messageObject.has("model_viewer_and_face_script") ? messageObject.getString("model_viewer_and_face_script") : null;
                            if (avatarViewScript != null) {
                                LocalStorage.setAvatarViewScript(avatarViewScript);
                            }

                            String avatarLoader = messageObject.has("avatar_loader") ? messageObject.getString("avatar_loader") : null;
                            if (avatarLoader != null) {
                                LocalStorage.setAvatarLoader(avatarLoader);
                            }

                            String campaignId = messageObject.has("campaign_id") ? messageObject.getString("campaign_id") : null;
                            if (campaignId != null) {
                                LocalStorage.setCampaignId(campaignId);
                            }
                            String campaignParam1 = messageObject.has("campaign_param_1") ? messageObject.getString("campaign_param_1") : null;
                            if (campaignParam1 != null) {
                                LocalStorage.setCampaignParam1(campaignParam1);
                            }
                            String campaignParam2 = messageObject.has("campaign_param_2") ? messageObject.getString("campaign_param_2") : null;
                            if (campaignParam2 != null) {
                                LocalStorage.setCampaignParam2(campaignParam2);
                            }
                            String avatarShareMessage = messageObject.has("avatar_share_text") ? messageObject.getString("avatar_share_text") : null;
                            if (avatarShareMessage != null) {
                                LocalStorage.setAvatarShareMessage(avatarShareMessage);
                            }

                            if (messageObject.has("countries")) {
                                ArrayList<CountryData> countryDataList = new ArrayList<>();
                                JSONArray countryJSONArray = messageObject.has("countries") ? messageObject.getJSONArray("countries") : new JSONArray();
                                for (int i = 0; i < countryJSONArray.length(); i++) {
                                    CountryData countryData = new CountryData(countryJSONArray.getJSONObject(i));
                                    countryDataList.add(countryData);
                                }
                                LocalStorage.saveCountriesList(countryDataList, LocalStorage.KEY_COUNTRIES_LIST, mContext);
                            }

                            if (messageObject.has("languages")) {
                                ArrayList<LanguagesData> languagesDataList = new ArrayList<>();
                                JSONArray languagesJSONArray = messageObject.has("languages") ? messageObject.getJSONArray("languages") : new JSONArray();
                                for (int i = 0; i < languagesJSONArray.length(); i++) {
                                    LanguagesData languagesData = new LanguagesData(languagesJSONArray.getJSONObject(i));
                                    languagesDataList.add(languagesData);
                                }
                                LocalStorage.saveLanguagesList(languagesDataList, LocalStorage.KEY_LANGUAGES_LIST, mContext);
                            }

                            String termsAndPolicies = messageObject.has("privacy_policy") ? messageObject.getString("privacy_policy")
                                    : null;
                            if (termsAndPolicies != null) {
                                LocalStorage.setTermsNPoliciesText(termsAndPolicies);
                            }

                            if (globalSettingCallBack != null) {
                                globalSettingCallBack.settingResponse(messageObject);
                            }
                        }
                    } else {
                        AlertUtils.showErrorDialog(mContext, mContext.getString(R.string.some_error_occurred));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    AlertUtils.showErrorDialog(mContext, mContext.getString(R.string.some_error_occurred));
                }
            } else {
                AlertUtils.showErrorDialog(mContext, mContext.getString(R.string.some_error_occurred));
            }
        });
    }

    public static void getAdMob(Context mContext) {
        List<AdMobData> bannerIdList = new ArrayList<>();
        List<AdMobData> onClickIdList = new ArrayList<>();
        APIRequest request = new APIRequest(Url.GET_AD_MOB, Request.Method.GET, null, null, mContext);
        request.showLoader = false;
        APIManager.request(request, (response, error, headers, statusCode) -> {
            if (response != null) {
                try {
                    JSONObject rootObject = new JSONObject(response);
                    String type = rootObject.has("type") ? rootObject.getString("type") : "";
                    if (type.equals("OK")) {
                        JSONArray messageArray = rootObject.has("msg") ? rootObject.getJSONArray("msg") : null;
                        if (messageArray != null) {
                            for (int i = 0; i < messageArray.length(); i++) {
                                JSONObject jsonObject = (JSONObject) messageArray.get(i);
                                JSONArray jsonArray = jsonObject.has("ads_on") ? jsonObject.getJSONArray("ads_on") : null;
                                String postType = jsonObject.has("post_title") ? jsonObject.getString("post_title") : "";
                                if (jsonArray != null) {
                                    for (int j = 0; j < jsonArray.length(); j++) {
                                        JSONObject jsonObject1 = (JSONObject) jsonArray.get(j);
                                        if (postType.equalsIgnoreCase(Constant.BANNER)) {
                                            AdMobData adMobData = new AdMobData(jsonObject1);
                                            bannerIdList.add(adMobData);
                                        } else if (postType.equalsIgnoreCase(Constant.ONCLICK)) {
                                            AdMobData adMobData = new AdMobData(jsonObject1);
                                            onClickIdList.add(adMobData);
                                        }
                                    }
                                }
                            }
                            LocalStorage.saveOnClickAdMobList(onClickIdList, LocalStorage.KEY_ONCLICK_AD_MOB, mContext);
                            LocalStorage.saveBannerAdMobList(bannerIdList, LocalStorage.KEY_BANNER_AD_MOB, mContext);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public interface GlobalSettingCallBack {
        void settingResponse(JSONObject msg);
    }

    public static void addEvent(Context mContext, String eventType, String id, String location, String subLocation) {
        if (location.equals(Constant.BANNER) || location.equals(Constant.ONCLICK)) {
            if (eventType.equals(Constant.CLICK)) {
                Utility.customEventsTracking(Constant.AdClicks,"");
            }
        }
        Map<String, String> params = new HashMap<>();
        params.put("event_type", eventType);
        params.put("ID", id);
        params.put("location", location);
        params.put("sub_location", subLocation);
        Log.i("APIMETHODS", id);
        APIRequest request = new APIRequest(Url.ADD_EVENT, Request.Method.POST, params, null, mContext);
        request.showLoader = false;
        APIManager.request(request, (response, error, headers, statusCode) -> {
            if (response != null) {
                Log.i("APIMETHODS", response);
                try {
                    JSONObject rootObject = new JSONObject(response);
                    String type = rootObject.has("type") ? rootObject.getString("type") : "";
                    if (type.equals("OK")) {
                        HomeActivity.getInstance().getWalletBalance();
                        JSONObject messageObject = rootObject.has("msg") ? rootObject.getJSONObject("msg") : null;
                        if (messageObject != null) {

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void getUserDetails(Context mContext) {
        String userId = LocalStorage.getUserId();
        try {
            Map<String, String> params = new HashMap<>();
            params.put("user_id", userId);
            APIRequest apiRequest = new APIRequest(Url.GET_USER_DETAILS, Request.Method.POST, params, null, mContext);
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

    public static void addCommerceEvent(Context mContext, String eventType, String id) {
        Map<String, String> params = new HashMap<>();
        params.put("event_type", eventType);
        params.put("ID", id);
        Log.i("APIMETHODS", id);
        APIRequest request = new APIRequest(Url.COMMERCE_EVENT, Request.Method.POST, params, null, mContext);
        request.showLoader = false;
        APIManager.request(request, (response, error, headers, statusCode) -> {
            if (response != null) {
                Log.i("APIMETHODS", response);
                try {
                    JSONObject rootObject = new JSONObject(response);
                    String type = rootObject.has("type") ? rootObject.getString("type") : "";
                    if (type.equals("OK")) {
                        JSONObject messageObject = rootObject.has("msg") ? rootObject.getJSONObject("msg") : null;
                        if (messageObject != null) {

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

   /* public interface CountryDataCallBack {
        void countryDataList(ArrayList<CountryData> countryDataArrayList);
    }

    public static void getCountryData(Context mContext, CountryDataCallBack countryDataCallBack) {
        ArrayList<CountryData> countryDataList = new ArrayList<>();
        try {
            Map<String, String> params = new HashMap<>();
            APIRequest apiRequest = new APIRequest(Url.GET_COUNTRY_DETAILS, Request.Method.GET, params,
                    null, mContext);
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    JSONObject jsonObjectResponse = null;
                    try {
                        jsonObjectResponse = new JSONObject(response);
                        String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                        if (type.equalsIgnoreCase("error")) {
                            LocalStorage.setUserPremium(false);
                            String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                        } else if (type.equalsIgnoreCase("ok")) {
                            JSONArray okResponse = jsonObjectResponse.has("msg") ? jsonObjectResponse.getJSONArray("msg") : new JSONArray();
                            for (int i = 0; i < okResponse.length(); i++) {
                                CountryData countryData = new CountryData(okResponse.getJSONObject(i));
                                countryDataList.add(countryData);
                            }
                        }
                        if (countryDataCallBack != null) {
                            countryDataCallBack.countryDataList(countryDataList);
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

    public interface LanguagesDataCallBack {
        void languagesDataList(ArrayList<LanguagesData> languagesDataArrayList);
    }

    public static void getLanguageData(Context mContext, LanguagesDataCallBack languagesDataCallBack) {
        ArrayList<LanguagesData> languagesDataList = new ArrayList<>();
        try {
            Map<String, String> params = new HashMap<>();
            APIRequest apiRequest = new APIRequest(Url.GET_LANGUAGE_DATA, Request.Method.GET, params,
                    null, mContext);
            apiRequest.showLoader = true;
            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    JSONObject jsonObjectResponse = null;
                    try {
                        jsonObjectResponse = new JSONObject(response);
                        String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                        if (type.equalsIgnoreCase("error")) {
                            String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                        } else if (type.equalsIgnoreCase("ok")) {
                            JSONArray okResponse = jsonObjectResponse.has("msg") ? jsonObjectResponse.getJSONArray("msg") : new JSONArray();
                            for (int i = 0; i < okResponse.length(); i++) {
                                LanguagesData languagesData = new LanguagesData(okResponse.getJSONObject(i));
                                languagesDataList.add(languagesData);
                            }
                            LocalStorage.saveLanguagesList(languagesDataList, LocalStorage.KEY_LANGUAGES_LIST, mContext);
                        }
                        if (languagesDataCallBack != null) {
                            languagesDataCallBack.languagesDataList(languagesDataList);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/


}
