package com.app.itaptv.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.app.itaptv.API.APIManager;
import com.app.itaptv.API.APIRequest;
import com.app.itaptv.API.Url;
import com.app.itaptv.R;
import com.app.itaptv.utils.AlertUtils;
import com.app.itaptv.utils.Constant;
import com.app.itaptv.utils.Log;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PastContestWinners extends AppCompatActivity implements View.OnClickListener {

    String TAG = String.valueOf(PastContestWinners.class);

    private ImageView ivWinners;
    private ImageButton goToAppBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_contest_winners);

        ivWinners = findViewById(R.id.iv_winners);
        goToAppBtn = findViewById(R.id.btn_go_to_app);
        goToAppBtn.setVisibility(View.INVISIBLE);
        goToAppBtn.setOnClickListener(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setupWinnerDataAPI();

    }

    private void setupWinnerDataAPI() {
        try {
            Map<String, String> params = new HashMap<>();
            APIRequest apiRequest = new APIRequest(Url.GET_CAMPAIGN_WINNERS, Request.Method.POST, params, null, this);
            apiRequest.showLoader = true;

            APIManager.request(apiRequest, (response, error, headers, statusCode) -> {
                handlePostWinnersResponse(response, error);

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handlePostWinnersResponse(String response, Exception error) {
        Log.d(TAG, response);
        try {
            if (error != null) {
                // showError(error.getMessage());
                Intent intent = new Intent();
                intent.putExtra("USER_REFER", 1);
                setResult(Activity.RESULT_OK, intent);
                finish();
            } else {
                if (response != null) {
                    //Log.e("response", response);
                    JSONObject jsonObjectResponse = new JSONObject(response);
                    String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                    if (type.equalsIgnoreCase("error")) {
                        String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                        showAlert(true, message);
                    } else if (type.equalsIgnoreCase("ok")) {
                        JSONObject jsonArrayMsg = jsonObjectResponse.getJSONObject("msg");
                        boolean status = jsonArrayMsg.getBoolean("status");
                        if (status) {
                            JSONObject jsonObjectContents = jsonArrayMsg.getJSONObject("data");
                            String url = jsonObjectContents.getString("winner_image");
                            RequestOptions options = new RequestOptions().centerCrop().placeholder(R.color.back_black);
                            Glide.with(this)
                                    .load(url)
                                    .apply(options)
                                    .into(ivWinners);
                            goToAppBtn.setVisibility(View.VISIBLE);
                        } else {
                            showAlert(false, null);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(boolean isAlert, @Nullable String errorMessage) {
        if (isAlert) {
            AlertUtils.showAlert(Constant.ALERT_TITLE, errorMessage, getString(R.string.ok), this, isPositiveAction -> finishFlow());
        } else {
            finishFlow();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_go_to_app:
                finishFlow();
        }
    }

    private void finishFlow() {
        Intent intent = new Intent();
        intent.putExtra("USER_REFER", 0);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
