/*
package com.app.itap.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import androidx.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.app.itap.API.APIManager;
import com.app.itap.API.APIRequest;
import com.app.itap.API.Url;
import com.app.itap.R;
import com.app.itap.structure.LiveNowData;
import com.app.itap.utils.AlertUtils;
import com.app.itap.utils.EmptyStateManager;
import com.app.itap.utils.GameDateValidation;
import com.app.itap.utils.Log;
import com.app.itap.utils.Utility;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

*/
/**
 * Created by poonam on 8/2/19.
 *//*


public class PresenterLiveStartActivity extends BaseActivity {
    public static final String LIVE_ID = "id";
    private String sourceDate = "yyyy-MM-dd HH:mm:ss";
    private String destinationDate = "EEEE MMM dd, hh:mm a";
    String timeLeft;
    LiveNowData liveNowData;

    LinearLayout llParent;
    ImageView ivImage;
    TextView tvTitle;
    TextView tvScheduledTime;
    TextView tvCountDownTime;
    Button btStartNow;
    ProgressBar progressBar;
    EmptyStateManager emptyState;
    CountDownTimer countDownTimer;

    SimpleDateFormat sourceDateFormat = new SimpleDateFormat(sourceDate);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_start);
        init();
    }

    private void init() {
        int id = getIntent().getExtras().getInt(LIVE_ID);

        llParent = findViewById(R.id.llParent);
        ivImage = findViewById(R.id.ivImage);
        tvTitle = findViewById(R.id.tvStoreName);
        tvScheduledTime = findViewById(R.id.tvScheduledTime);
        tvCountDownTime = findViewById(R.id.tvCountDownTime);
        btStartNow = findViewById(R.id.btStartNow);
        progressBar = findViewById(R.id.progressBar);
        setUpEmptyState();
        callGetLiveSessionDetailsAPI(id);
        //setData();
    }

    */
/**
     * Initialization of Empty State
     *//*

    void setUpEmptyState() {
        emptyState = EmptyStateManager.setUpInActivity(this, action -> {
            if (action.equals(EmptyStateManager.ACTION_RETRY)) {
            }
        });
        emptyState.hide();

    }

    private void setData() {

        */
/*boolean isValid = isValidDate(liveNowData.time);
        String scheduledTime;
        if (!isValid) {
            scheduledTime = "Live session will start at " + liveNowData.time;
        } else {
            scheduledTime = "Live session started";
        }
        tvScheduledTime.setText(scheduledTime);*//*

    }


    private boolean isValidDate(String date) {
        Calendar calendar = Calendar.getInstance();
        String currentDate = sourceDateFormat.format(calendar.getTime());

        try {
            Date dtScheduledDate = sourceDateFormat.parse(date);
            Date dtCurrentDate = sourceDateFormat.parse(currentDate);
            if (dtScheduledDate.equals(dtCurrentDate) || dtScheduledDate.before(dtCurrentDate)) {
                return true;
            } else {
                return false;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;

    }

    public void startNow(View view) {
        startActivity(new Intent(this, LiveNowStartActivity.class));
    }

    private void callGetLiveSessionDetailsAPI(int id) {
        showLoader();
        try {
            Map<String, String> params = new HashMap<>();
            APIRequest apiRequest = new APIRequest(Url.GET_PRESENTER_SESSION + "" + id, Request.Method.GET, params, null, this);
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, (response, error, headers, statusCode) -> handleGetLiveSessionDetailsAPIResponse(response, error));
        } catch (Exception e) {

        }
    }

    private void handleGetLiveSessionDetailsAPIResponse(@Nullable String response, @Nullable Exception error) {
        hideLoader();
        try {
            if (error != null) {
                updateEmptyState(error.getMessage());
            } else {
                if (response != null) {
                    JSONObject jsonObject = new JSONObject(response);
                    String type = jsonObject.has("type") ? jsonObject.getString("type") : "";
                    if (type.equalsIgnoreCase("error")) {
                        String message = jsonObject.has("msg") ? jsonObject.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                        showError(message);
                    } else {
                        JSONObject jsonObjectMessage = jsonObject.getJSONObject("msg");
                        liveNowData = new LiveNowData(jsonObjectMessage);
                        if (liveNowData != null) {
                            updateUI();
                        } else {
                            updateEmptyState(null);
                        }
                    }

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            updateEmptyState(e.getMessage());
        }
    }

    private void updateUI() {

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        //if you need fix imageview height
        int devicewidth = (int) (displaymetrics.widthPixels);
        int deviceheight = (int) (devicewidth / 2);
        ivImage.getLayoutParams().height = deviceheight;

        String formattedDateTime = Utility.formatDate(sourceDate, destinationDate, liveNowData.time)
                .replace("AM", "am")
                .replace("PM", "pm");
        String scheduledTime = String.format(getString(R.string.scheduled_time), formattedDateTime, liveNowData.duration);
        boolean isValidDate = isValidDate(liveNowData.time);

        Glide.with(this)
                .load(liveNowData.imageUrl)
                .thumbnail(0.1f)
                .apply(new RequestOptions().centerCrop().dontAnimate().diskCacheStrategyOf(DiskCacheStrategy.ALL).override(devicewidth, deviceheight))
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(ivImage);
        tvTitle.setText(liveNowData.title);
        tvScheduledTime.setText(scheduledTime);

        */
/*if (isValidDate) {
            btStartNow.setVisibility(View.VISIBLE);
            tvCountDownTime.setVisibility(View.GONE);
        } else {
            btStartNow.setVisibility(View.GONE);
            tvCountDownTime.setVisibility(View.VISIBLE);
            startCountDown();
        }*//*

    }

    private void showLoader() {
        progressBar.setVisibility(View.VISIBLE);
        llParent.setVisibility(View.GONE);
    }

    private void hideLoader() {
        progressBar.setVisibility(View.GONE);
        llParent.setVisibility(View.VISIBLE);
    }


    private void startCountDown() {
        try {
            Date formattedGivenDateTime = sourceDateFormat.parse(liveNowData.time);
            long gameExpireMilliseconds = formattedGivenDateTime.getTime();
            long currentTimeMilliseconds = System.currentTimeMillis();
            long difference = gameExpireMilliseconds - currentTimeMilliseconds;


            countDownTimer = new CountDownTimer(difference, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    timeLeft = GameDateValidation.getTimeLeft(millisUntilFinished);
                    Log.e("Start Time", timeLeft);
                    tvCountDownTime.setText(timeLeft);
                }

                @Override
                public void onFinish() {
                    tvCountDownTime.setVisibility(View.INVISIBLE);
                    btStartNow.setVisibility(View.VISIBLE);

                }
            }.start();

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void cancelCountDown() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
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

    */
/**
     * Update of Empty State
     *//*

    private void updateEmptyState(String error) {
        if (error == null) {
            if (liveNowData == null) {
                emptyState.setImgAndMsg("No details found", null);
                llParent.setVisibility(View.GONE);
            } else {
                llParent.setVisibility(View.VISIBLE);
                emptyState.hide();
            }
        } else {
            llParent.setVisibility(View.GONE);
            if (Utility.isConnectingToInternet(this)) {
                emptyState.setMessage(APIManager.GENERIC_API_ERROR_MESSAGE);
            } else {
                emptyState.showNoConnectionState();
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelCountDown();
    }
}
*/
