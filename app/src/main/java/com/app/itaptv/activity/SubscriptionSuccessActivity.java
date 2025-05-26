package com.app.itaptv.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;

import com.app.itaptv.API.APIMethods;
import com.app.itaptv.R;
import com.app.itaptv.utils.Constant;
import com.app.itaptv.utils.Utility;

public class SubscriptionSuccessActivity extends AppCompatActivity {

    public static String REVENUE = "revenue";
    int revenue = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_success);
        setTheme(R.style.Theme_Leanback);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        APIMethods.getUserDetails(SubscriptionSuccessActivity.this);
        if (getIntent() != null) {
            revenue = getIntent().getIntExtra(REVENUE, 0);
        }
        Utility.customEventsTracking(Constant.Subscription, String.valueOf(revenue));
    }

    public void goToPremium(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        if (Utility.isTelevision()) {
            HomeActivity.getInstance().goToPremium();
        } else {
            HomeActivity.getInstance().showPremium();
        }
    }
}