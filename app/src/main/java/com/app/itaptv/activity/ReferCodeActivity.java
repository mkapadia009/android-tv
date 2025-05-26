package com.app.itaptv.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.app.itaptv.API.APIManager;
import com.app.itaptv.API.APIRequest;
import com.app.itaptv.API.APIResponse;
import com.app.itaptv.API.Url;
import com.app.itaptv.R;
import com.app.itaptv.utils.AlertUtils;
import com.app.itaptv.utils.Constant;
import com.app.itaptv.utils.LocalStorage;
import com.app.itaptv.utils.Utility;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ReferCodeActivity extends AppCompatActivity {

    Context mContext = ReferCodeActivity.this;
    Button btContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refer_code);
        setTheme(R.style.Theme_Leanback);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setupUI();
    }

    private void setupUI() {
        EditText etCode = findViewById(R.id.etCode);
        TextView tvSkip = findViewById(R.id.tvSkip);
        btContinue = findViewById(R.id.btContinue);

        if (Utility.isTelevision()) {
            etCode.requestFocus();
            Utility.showKeyboard(ReferCodeActivity.this);
        }

        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("USER_REFER", 0);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

        btContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btContinue.setEnabled(false);
                if (etCode.getText().length() > 0) {
                    if (etCode.getText().toString().equalsIgnoreCase(Constant.LUCKY_DRAW_REFERRAL_CODE)) {
                        LocalStorage.setWinner(true);
                    }
                    HashMap<String, String> params = new HashMap<>();
                    params.put("code", etCode.getText().toString());
                    APIRequest apiRequest = new APIRequest(Url.REFERRAL_CODE, Request.Method.POST, params, null, mContext);
                    APIManager.request(apiRequest, new APIResponse() {
                        @Override
                        public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                            if (response != null) {
                                try {
                                    JSONObject object = new JSONObject(response);
                                    String type = "";
                                    if (object.has("type")) {
                                        type = object.getString("type");
                                    }
                                    if (type.equals("OK")) {
                                        Intent intent = new Intent();
                                        intent.putExtra("USER_REFER", 1);
                                        /*setResult(Activity.RESULT_OK, intent);
                                        finish();*/
                                        setResult(LoginActivity.REQUEST_REFERRAL_COINS_RECEIVED);
                                        startActivity(new Intent(ReferCodeActivity.this, ReferralCoinsActivity.class));
                                        finish();
                                    } else {
                                        AlertUtils.showToast(getString(R.string.some_error_occurred), Toast.LENGTH_LONG, mContext);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            btContinue.setEnabled(true);
                        }
                    });
                } else {
                    AlertUtils.showToast(getString(R.string.error_message_referral_code), Toast.LENGTH_LONG, mContext);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        btContinue.setEnabled(true);
    }
}
