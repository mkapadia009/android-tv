package com.app.itaptv.API;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.itaptv.MyApp;
import com.app.itaptv.R;
import com.app.itaptv.activity.LoginActivity;
import com.app.itaptv.roomDatabase.AnalyticsData;
import com.app.itaptv.roomDatabase.ListRoomDatabase;
import com.app.itaptv.structure.FeedContentData;
import com.app.itaptv.utils.AlertUtils;
import com.app.itaptv.utils.LocalStorage;
import com.app.itaptv.utils.Log;
import com.app.itaptv.utils.Utility;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Deep on 8/24/2017.
 */

public class APIManager {

    private static String TAG = APIManager.class.getCanonicalName();

    public static String GENERIC_API_ERROR_MESSAGE = getGenericApiErrorMessage();
    public static String ALERT = "Alert";
    public static String TOKEN_EXPIRED = "token expired";
    public static String PAGE_NAVIGATION = "page_navigation";
    private static int statusCode = 0;
    private static Map<String, String> responseHeaders = new HashMap<>();
    private static ProgressDialog progressDialog;
    private static RequestQueue queue = null;
    ;
    private static boolean isFirstTime = true;
    private static Context mContext;

    /**
     * Make api request to server.
     *
     * @param apiRequest  - api request object.
     * @param apiResponse - api response completion with error and response received from server.
     */
    public static void request(final APIRequest apiRequest, final APIResponse apiResponse) {
        // return if url is invalid
        if (apiRequest.url == null || apiRequest.url.length() == 0) {
            // Show error and print log
            return;
        }
        // Show loader if required
        if (apiRequest.context != null && apiRequest.showLoader) {
            // progressDialog = new ProgressDialog(apiRequest.context);
            progressDialog = new ProgressDialog(apiRequest.context);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }
        if (apiRequest.context != null) {
            mContext = apiRequest.context;
            if (isFirstTime) {
                isFirstTime = false;
                queue = Volley.newRequestQueue(apiRequest.context);
            }
        }
        // API Call
        final StringRequest stringRequest = new StringRequest(apiRequest.method, apiRequest.url, response -> {
            // Check for token expiry
            if (response != null && !response.isEmpty()) {
                try {
                    JSONObject resp = new JSONObject(response);
                    if (resp.has("type")) {
                        String type = resp.getString("type");
                        if (resp.has("msg")) {
                            String msg = resp.getString("msg");
                            if (type.toLowerCase().equals("error") &&
                                    msg.toLowerCase().contains("token")) {
                                // Token expired.
                                // Cancel all requests.
                                APIManager.cancelAllRequests(TOKEN_EXPIRED);
                                // Ask user to login again.
                                if (apiRequest.context != null) {
                                    try {
                                        if (!LocalStorage.isLoginScreenShown()) {
                                            Intent i = new Intent(apiRequest.context, LoginActivity.class);
                                            i.setAction(TOKEN_EXPIRED);
                                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            apiRequest.context.startActivity(i);
                                        }
                                    } catch (Exception ignored) {

                                    }
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (apiResponse != null) {
                apiResponse.onResponse(response, null, responseHeaders, statusCode);
            }

            // Hide loader if shown
            if (apiRequest.context != null && apiRequest.showLoader) {
                if (progressDialog != null) {
                    try {
                        progressDialog.dismiss();
                    } catch (final IllegalArgumentException e) {
                        // Handle or log or ignore
                    } catch (final Exception e) {
                        // Handle or log or ignore
                    } finally {
                        progressDialog = null;
                    }

                }
            }
        }, error -> {
            if (apiRequest.context != null && apiRequest.showError && apiRequest.showLoader) {
                String errorText = apiRequest.context.getString(R.string.GENERIC_API_ERROR_MESSAGE);
                if (error instanceof NoConnectionError) {
                    errorText = apiRequest.context.getString(R.string.NO_CONNECTIVITY_ERROR);
                } else {
                    if (Url.getEnvironment() != Url.Environment.PRODUCTION) {
                        if (error.getLocalizedMessage() != null) {
                            errorText = error.getLocalizedMessage();
                        }
                    }
                }
                AlertUtils.showAlert("Error", errorText, null, apiRequest.context, null);
            }
            if (apiResponse != null) {
                apiResponse.onResponse(null, error, responseHeaders, statusCode);
            }
            // Hide loader if shown
            if (apiRequest.context != null && apiRequest.showLoader) {
                if (progressDialog != null) {
                    try {
                        progressDialog.dismiss();
                    } catch (final IllegalArgumentException e) {
                        // Handle or log or ignore
                    } catch (final Exception e) {
                        // Handle or log or ignore
                    } finally {
                        progressDialog = null;
                    }
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                if (apiRequest.params == null) {
                    apiRequest.params = new HashMap<>();
                }
                return apiRequest.params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (apiRequest.headers == null) {
                    apiRequest.headers = new HashMap<>();
                }
                return apiRequest.headers;
            }

           /* @Override
            public byte[] getBody() throws AuthFailureError {
                String str = LocalStorage.getValue("ENCRYPT","",String.class);
                return str.getBytes();
            }*/

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                statusCode = response.statusCode;
                responseHeaders = response.headers;
                if (response.headers.containsKey(LocalStorage.KEY_APP_UPDATE)) {
                    String appUpdateValue = responseHeaders.getOrDefault(LocalStorage.KEY_APP_UPDATE, "");
                    if (!appUpdateValue.isEmpty()) {
                        String[] values = appUpdateValue.split(",");
                        LocalStorage.saveRequiredVersion(values[0], LocalStorage.KEY_APP_VERSION_NUMBER, MyApp.getAppContext());
                        LocalStorage.saveIsNeedLogout(values[1], LocalStorage.KEY_APP_LOGOUT, MyApp.getAppContext());
                    }
                    //MyApp.getAppContext().sendBroadcast(new Intent("mandatoryappupdate"));
                    String requiredVersionName = LocalStorage.getRequiredVersion(LocalStorage.KEY_APP_VERSION_NUMBER, MyApp.getAppContext());
                    String versionName = LocalStorage.getAppVersion();
                    if (requiredVersionName != null) {
                        if (!requiredVersionName.isEmpty() && !versionName.isEmpty()) {
                            if (Utility.isAppUpdateRequired(requiredVersionName, versionName)) {
                                MyApp.getAppContext().sendBroadcast(new Intent("mandatoryappupdate"));
                            }
                        }
                    }
                }
                return super.parseNetworkResponse(response);
            }

            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError) {
                return super.parseNetworkError(volleyError);
            }
        };
       /* if (queue!=null){
            queue.add(stringRequest);
            queue.start();
        }*/
        stringRequest.setTag("itap_api");
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(40000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        APIRequestQueue.sharedinstance().addToRequestQueue(stringRequest);
    }

    /**
     * Checks if internet is available or not.
     *
     * @return true if internet available else false.
     */
    public static boolean isConnectedToInternet() {
        boolean isConnected = false;
        ConnectivityManager cm = (ConnectivityManager)
                MyApp.sharedContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
        return isConnected;
    }

    public static void cancelAllRequests(String action) {
        APIRequestQueue.sharedinstance().cancelAll();
        if (action.equals(TOKEN_EXPIRED)) {
            LocalStorage.logout();
        }
    }

    /**
     * Ads reponse interface.
     */
    public interface AdsResponse {
        /**
         * Called after getAdsForPost api is completed.
         *
         * @param adsList Ads list.
         */
        void ads(ArrayList<FeedContentData> adsList);
    }

    /**
     * Get ads for specific post by its id.
     *
     * @param postId      Id of the post.
     * @param adsResponse Ads response.
     */
    public static void getAdsForPost(@NonNull String postId, @Nullable AdsResponse adsResponse) {
        HashMap<String, String> params = new HashMap<>();
        APIRequest apiRequest = new APIRequest(Url.GET_ADS + postId,
                Request.Method.GET, params, null, null);
        apiRequest.showLoader = false;
        APIManager.request(apiRequest, (response, error, headers, statusCode) -> {
            //ArrayList<AttachmentData> adsList = new ArrayList<>();
            ArrayList<FeedContentData> adsList = new ArrayList<>();
            if (error == null && response != null) {
                try {
                    Log.i("ADS", "response: " + response);
                    // Get response
                    JSONObject resp = new JSONObject(response);
                    if (resp.has("msg")) {
                        // Post array
                        JSONArray msg = resp.getJSONArray("msg");
                        for (int i = 0; i < msg.length(); i++) {
                            // Post object
                            JSONObject obj = msg.getJSONObject(i);
                            // Ad attachment object
                           /* if (obj.has("attachment")) {
                                JSONObject attachment = obj.getJSONObject("attachment");
                                String mPostId = obj.getString("ID");
                                AttachmentData ad = new AttachmentData(attachment, mPostId);
                                // Ad position (in seconds)
                                if (obj.has("play_at")) {
                                    ad.play_at = obj.getInt("play_at");
                                }
                                adsList.add(ad);
                            }*/
                            FeedContentData ad = new FeedContentData(obj);
                            if (obj.has("play_at")) {
                                ad.adFieldsData.play_at = obj.getInt("play_at");
                            }
                            adsList.add(ad);
                            /*if (obj.has("ad_fields")) {
                                JSONObject adFields = obj.getJSONObject("ad_fields");
                                String mPostId = obj.getString("ID");
                                AdFieldsData ad = new AdFieldsData(adFields,mPostId);
                                // Ad position (in seconds)
                                if (obj.has("play_at")) {
                                    ad.play_at = obj.getInt("play_at");
                                }
                                adsList.add(ad);
                            }*/
                        }
                    }
                } catch (JSONException ignored) {

                }
            }
            if (adsResponse != null) {
                adsResponse.ads(adsList);
            }
        });
    }


    public static void request(final APIRequest apiRequest, String encryptedParams, final APIResponse apiResponse) {
        // return if url is invalid
        if (apiRequest.url == null || apiRequest.url.length() == 0) {
            // Show error and print log
            return;
        }
        // Show loader if required
        if (apiRequest.context != null && apiRequest.showLoader) {
            // progressDialog = new ProgressDialog(apiRequest.context);
            progressDialog = new ProgressDialog(apiRequest.context);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }
        if (apiRequest.context != null) {
            if (isFirstTime) {
                isFirstTime = false;
                queue = Volley.newRequestQueue(apiRequest.context);
            }
        }
        // API Call
        final StringRequest stringRequest = new StringRequest(apiRequest.method, apiRequest.url, response -> {
            // Check for token expiry
            if (response != null && !response.isEmpty()) {
                try {
                    JSONObject resp = new JSONObject(response);
                    if (resp.has("type")) {
                        String type = resp.getString("type");
                        if (resp.has("msg")) {
                            String msg = resp.getString("msg");
                            if (type.toLowerCase().equals("error") &&
                                    msg.toLowerCase().contains("token")) {
                                // Token expired.
                                // Cancel all requests.
                                APIManager.cancelAllRequests(TOKEN_EXPIRED);
                                // Ask user to login again.
                                if (apiRequest.context != null) {
                                    try {
                                        if (!LocalStorage.isLoginScreenShown()) {
                                            Intent i = new Intent(apiRequest.context, LoginActivity.class);
                                            i.setAction(TOKEN_EXPIRED);
                                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            apiRequest.context.startActivity(i);
                                        }
                                    } catch (Exception ignored) {

                                    }
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (apiResponse != null) {
                apiResponse.onResponse(response, null, responseHeaders, statusCode);
            }

            // Hide loader if shown
            if (apiRequest.context != null && apiRequest.showLoader) {
                if (progressDialog != null) {
                    try {
                        progressDialog.dismiss();
                    } catch (final IllegalArgumentException e) {
                        // Handle or log or ignore
                    } catch (final Exception e) {
                        // Handle or log or ignore
                    } finally {
                        progressDialog = null;
                    }

                }
            }
        }, error -> {
            if (apiRequest.context != null && apiRequest.showError && apiRequest.showLoader) {
                String errorText = apiRequest.context.getString(R.string.GENERIC_API_ERROR_MESSAGE);
                if (error instanceof NoConnectionError) {
                    errorText = apiRequest.context.getString(R.string.NO_CONNECTIVITY_ERROR);
                } else {
                    if (Url.getEnvironment() != Url.Environment.PRODUCTION) {
                        if (error.getLocalizedMessage() != null) {
                            errorText = error.getLocalizedMessage();
                        }
                    }
                }
                AlertUtils.showAlert("Error", errorText, null, apiRequest.context, null);
            }
            if (apiResponse != null) {
                apiResponse.onResponse(null, error, responseHeaders, statusCode);
            }
            // Hide loader if shown
            if (apiRequest.context != null && apiRequest.showLoader) {
                if (progressDialog != null) {
                    try {
                        progressDialog.dismiss();
                    } catch (final IllegalArgumentException e) {
                        // Handle or log or ignore
                    } catch (final Exception e) {
                        // Handle or log or ignore
                    } finally {
                        progressDialog = null;
                    }
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                if (apiRequest.params == null) {
                    apiRequest.params = new HashMap<>();
                }
                return apiRequest.params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (apiRequest.headers == null) {
                    apiRequest.headers = new HashMap<>();
                }
                return apiRequest.headers;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                String str = encryptedParams;
                return str.getBytes();
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                statusCode = response.statusCode;
                responseHeaders = response.headers;
                if (response.headers.containsKey(LocalStorage.KEY_APP_UPDATE)) {
                    String appUpdateValue = responseHeaders.getOrDefault(LocalStorage.KEY_APP_UPDATE, "");
                    if (!appUpdateValue.isEmpty()) {
                        String[] values = appUpdateValue.split(",");
                        LocalStorage.saveRequiredVersion(values[0], LocalStorage.KEY_APP_VERSION_NUMBER, MyApp.getAppContext());
                        LocalStorage.saveIsNeedLogout(values[1], LocalStorage.KEY_APP_LOGOUT, MyApp.getAppContext());
                    }
                    //MyApp.getAppContext().sendBroadcast(new Intent("mandatoryappupdate"));
                    String requiredVersionName = LocalStorage.getRequiredVersion(LocalStorage.KEY_APP_VERSION_NUMBER, MyApp.getAppContext());
                    String versionName = LocalStorage.getAppVersion();
                    if (requiredVersionName != null) {
                        if (!requiredVersionName.isEmpty() && !versionName.isEmpty()) {
                            if (Utility.isAppUpdateRequired(requiredVersionName, versionName)) {
                                MyApp.getAppContext().sendBroadcast(new Intent("mandatoryappupdate"));
                            }
                        }
                    }
                }
                return super.parseNetworkResponse(response);
            }

            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError) {
                return super.parseNetworkError(volleyError);
            }
        };
        stringRequest.setTag("itap_api");
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(40000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        APIRequestQueue.sharedinstance().addToRequestQueue(stringRequest);
    }

    public static void jsonRequest(final APIRequest apiRequest, final APIResponse apiResponse, JsonObject data) throws JSONException {
        // return if url is invalid
        if (apiRequest.url == null || apiRequest.url.length() == 0) {
            // Show error and print log
            return;
        }
        // Show loader if required
        if (apiRequest.context != null && apiRequest.showLoader) {
            // progressDialog = new ProgressDialog(apiRequest.context);
            progressDialog = new ProgressDialog(apiRequest.context);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }
        if (apiRequest.context != null) {
            if (isFirstTime) {
                isFirstTime = false;
                queue = Volley.newRequestQueue(apiRequest.context);
            }
        }
        JSONObject jsonObject = new JSONObject(String.valueOf(data));
        // API Call
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, apiRequest.url, new JSONObject(String.valueOf(data)),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("AnalyticsResponse", response.toString());
                        ListRoomDatabase listRoomDatabase = ListRoomDatabase.getDatabase(MyApp.getAppContext());
                        List<AnalyticsData> analyticsData = listRoomDatabase.mediaDAO().getAnalytics();
                        listRoomDatabase.mediaDAO().deleteAnalytics(analyticsData);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Log.i("AnalyticsResponse", error.toString());
                    }
                }
        ) {
            //here I want to post data to sever
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (apiRequest.headers == null) {
                    apiRequest.headers = new HashMap<>();
                }
                apiRequest.headers.put("Content-Type", "application/json; charset=utf-8");
                return apiRequest.headers;
            }
        };
        jsonObjectRequest.setTag("itap_api");
        queue.add(jsonObjectRequest);
    }

    public static String getGenericApiErrorMessage() {
        if (mContext != null) {
            return mContext.getString(R.string.GENERIC_API_ERROR_MESSAGE);
        }
        return "";
    }
}
