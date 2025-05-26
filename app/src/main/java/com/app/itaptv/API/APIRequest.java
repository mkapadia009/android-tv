package com.app.itaptv.API;

import android.content.Context;
import android.provider.Settings;

import com.app.itaptv.MyApp;
import com.app.itaptv.utils.Constant;
import com.app.itaptv.utils.LocalStorage;
import com.app.itaptv.utils.Log;
import com.app.itaptv.utils.Utility;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Deep on 8/24/2017.
 */

public class APIRequest {

    public String url = "";
    public int method;
    //public JSONObject objectParams = new JSONObject();
    public Map<String, String> params = new HashMap<>();
    public Map<String, String> headers = new HashMap<>();
    public Context context;
    public boolean showLoader;
    public boolean showError; // Set default value as per the app environment or do it in constructor
    // timeOutInterval
    // retry policy

    public APIRequest() {
    }

    public APIRequest(String url, int method, Map<String, String> params, Map<String, String> headers, Context context) {
        this.url = url;
        this.method = method;
        this.params = params;
        //Log.e("response", url);
        // If authToken in headers is mandatory,
        // Create new if headers is null and pass authToken here
        if (headers == null) {
            headers = new HashMap<>();
        }
        headers.put(LocalStorage.KEY_AUTH_TOKEN, LocalStorage.getToken());
        headers.put(LocalStorage.KEY_APP_VERSION, LocalStorage.getAppVersion());
        try {
            String time = Utility.getCurrentDateTimeInMillis();
            String android_id = Settings.Secure.getString(MyApp.getAppContext().getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            String uniqueId = android_id + "." + time;
            headers.put(LocalStorage.X_REQUEST_ID, Utility.openssl_encrypt(uniqueId, Constant.getSecretKeyDateTime(), Constant.getIvParameterDateTime()));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        if (Utility.isTelevision()){
            headers.put(LocalStorage.SOURCE, Constant.ANDROIDTV);
        }else {
            headers.put(LocalStorage.SOURCE, Constant.GOOGLE_PLAYSTORE);
        }
        headers.put(LocalStorage.LANGUAGEPREF, Utility.getLanguagePref(context));
        if (LocalStorage.getUserData()!=null) {
            headers.put(LocalStorage.COUNTRY, LocalStorage.getUserData().countryId);
        }
        headers.put(LocalStorage.DEVICE_ID, Utility.getSystemDetail().getDeviceID());
        this.headers = headers;
        if (context != null) {
            this.context = context;
            /*showLoader = true;
            showError = true;*/
        }
        Log.d("URL..:", url);
        Log.d("Headers..:", headers.toString());
    }

    public String getHeader() {
        if (headers != null) {
            if (headers.containsValue(Constant.INDUSOS)) {
                return Constant.INDUSOS;
            }
        }
        return "";
    }
}
