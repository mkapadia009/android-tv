package com.app.itaptv.utils;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.app.itaptv.API.APIManager;
import com.app.itaptv.API.APIRequest;
import com.app.itaptv.API.Url;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Analyticals {
    public static String GAME_ACTIVITY_TYPE = "play_game";
    public static String LIVE_GAME_QUESTION_CONTEXT = "trivia";
    public static String SINGLE_PLAYER_GAME_QUESTION_CONTEXT = "game";
    public static String LIVE_GAME_QUESTION_ACTIVITY_TYPE = "play_question";
    public static String ACTIVITY_TYPE_PLAY_AUDIO = "play_audio";
    public static String ACTIVITY_TYPE_PLAY_PLAYLIST = "play_playlist";
    public static String AUDIO_SONG_QUESTION_ACTIVITY_TYPE = "play_question";
    public static String ACTIVITY_TYPE_PLAY_EPISODE = "play_episode";
    public static String ACTIVITY_TYPE_AVATAR = "activity_avatar";
    public static String ACTIVITY_TYPE_SHOP = "activity_shop";
    public static String ACTIVITY_TYPE_VIEW_AD = "view_ad";
    public static String ACTIVITY_TYPE_APP_OPEN = "app_open";
    public static final String CONTEXT_AUDIO_SONG = "audio_song";
    public static final String CONTEXT_SERIES = "series";
    public static final String CONTEXT_EPISODE = "episode";
    public static final String CONTEXT_FEED = "feed";
    public static final String CONTEXT_PLAYLIST = "playlist";
    public static final String CONTEXT_CHANNEL = "channel";
    public static final String CONTEXT_APP_OPEN = "app_open";


    //public static String CONTEXT_SLIDER = "slider";
    public static String CONTEXT_SLIDER = "slider";


    static String TAG = Analyticals.class.getName();

    public interface AnalyticsResult {
        void onResult(boolean success, String acitivity_id, @Nullable String error);
    }

    public static void CallPlayedActivity(@NonNull String activity_type, @NonNull String entity_id, String contextId, String pageName, Context context, String meta, @Nullable AnalyticsResult results) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("activity_type", activity_type);
            params.put("entity_id", entity_id);
            if (!pageName.equalsIgnoreCase(""))
                params.put("context", pageName);
            if (!contextId.equalsIgnoreCase(""))
                params.put("context_id", contextId);
            if (!meta.equalsIgnoreCase(""))
                params.put("meta", meta);
            Log.d(TAG, params.toString());
            APIRequest apiRequest = new APIRequest(Url.POST_PLAYED_QUESTION, Request.Method.POST, params, null, context);
            APIManager.request(apiRequest, (response, error, headers, statusCode) -> {
                try {
                    if (response != null) {
                        Log.d(TAG, "response: " + response);
                        JSONObject result = new JSONObject(response);
                        if (result.getString("type").equalsIgnoreCase("OK")) {
                            if (results != null) {
                                results.onResult(true, result.getJSONObject("msg").getString("activity_id"), null);
                            }
                        }
                    }
                } catch (JSONException e) {
                    if (results != null) {
                        results.onResult(false, "", e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            if (results != null) {
                results.onResult(false, "", e.getMessage());
            }
        }
    }

    public static void CallplayedQuestionApi(Context context, String activityType, String questionId, String contextType, String contextId, JSONObject answer, @Nullable AnalyticsResult results) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("activity_type", activityType);
            params.put("entity_id", questionId);
            params.put("context", contextType);
            params.put("context_id", contextId);
            params.put("meta", answer.toString());
            Log.d(TAG, params.toString());

            APIRequest apiRequest = new APIRequest(Url.POST_PLAYED_QUESTION, Request.Method.POST, params, null, context);
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, (response, error, headers, statusCode) -> {
                try {
                    if (response != null) {
                        Log.d(TAG, "response: " + response);
                        JSONObject result = new JSONObject(response);
                        if (result.getString("type").equalsIgnoreCase("OK")) {
                            if (results != null) {
                                results.onResult(true, result.getJSONObject("msg").getString("activity_id"), null);
                            }
                        }
                    }
                } catch (JSONException e) {
                    if (results != null) {
                        results.onResult(false, "", e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            if (results != null) {
                results.onResult(false, "", e.getMessage());
            }
        }
    }
}
