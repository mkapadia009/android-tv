/*
package com.app.itap.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.app.itap.API.APIManager;
import com.app.itap.API.APIRequest;
import com.app.itap.API.Url;
import com.app.itap.R;
import com.app.itap.utils.AlertUtils;
import com.app.itap.utils.LocalStorage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

*/
/**
 * Created by poonam on 5/2/19.
 *//*


public class LoginPresenterActivity extends AppCompatActivity {
    LinearLayout llLoader;
    EditText etUsername;
    EditText etPassword;

    private static String ERROR_MSG_EMPTY_USERNAME_OR_PASSWORD = "Please enter Username and Password";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login_presenter);
        init();
    }

    private void init() {
        llLoader = findViewById(R.id.llLoader);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
    }


    public void presenterSignIn(View view) {
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            showError(ERROR_MSG_EMPTY_USERNAME_OR_PASSWORD);
            return;
        }
        llLoader.setVisibility(View.VISIBLE);
        callPresenterSignInAPI(username, password);
    }

    private void callPresenterSignInAPI(String username, String password) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("user_login", username);
            params.put("user_password", password);
            APIRequest apiRequest = new APIRequest(Url.PRESENTER_SIGNIN, Request.Method.POST, params, null, this);
            apiRequest.showLoader = false;

            APIManager.request(apiRequest, (response, error, headers, statusCode) -> {
                handlePresenterSignInAPIResponse(response, error);

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handlePresenterSignInAPIResponse(@Nullable String response, @Nullable Exception error) {
        llLoader.setVisibility(View.GONE);
        try {
            if (error != null) {
                showError(error.getMessage());
            } else {
                if (response != null) {
                    JSONObject jsonObject = new JSONObject(response);
                    String type = jsonObject.has("type") ? jsonObject.getString("type") : "";
                    if (type.equalsIgnoreCase("error")) {
                        String message = jsonObject.has("msg") ? jsonObject.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                        showError(message);
                    } else if (type.equalsIgnoreCase("ok")) {
                        JSONObject jsonObjectMessage = jsonObject.getJSONObject("msg");
                        String authToken = jsonObjectMessage.has("auth_token") ? jsonObjectMessage.getString("auth_token") : "";
                        LocalStorage.putValue(authToken, LocalStorage.KEY_AUTH_TOKEN);
                        LocalStorage.setLoggedInStatus(true, true);

                        startActivity(new Intent(this, PresenterLiveActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

                    }

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    */
/**
     * This method shows error alert
     *
     * @param errorMessage message to be displayed in alert dialog
     *//*

    private void showError(@Nullable String errorMessage) {
        if (errorMessage == null) errorMessage = APIManager.GENERIC_API_ERROR_MESSAGE;
        AlertUtils.showAlert(getString(R.string.label_error), errorMessage, null, this, null);
    }
}
*/
