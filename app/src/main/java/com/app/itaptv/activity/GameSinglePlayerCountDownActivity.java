package com.app.itaptv.activity;

import android.animation.Animator;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.app.itaptv.API.APIManager;
import com.app.itaptv.API.APIRequest;
import com.app.itaptv.API.APIResponse;
import com.app.itaptv.API.Url;
import com.app.itaptv.R;
import com.app.itaptv.structure.GameData;
import com.app.itaptv.structure.QuestionData;
import com.app.itaptv.utils.AlertUtils;
import com.app.itaptv.utils.AnalyticsTracker;
import com.app.itaptv.utils.DownloadUtil;
import com.app.itaptv.utils.LocalStorage;
import com.app.itaptv.utils.Log;
import com.app.itaptv.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by poonam on 12/11/18.
 */

public class GameSinglePlayerCountDownActivity extends BaseActivity {
    public static String QUESTION_DATA = "questionData";
    public static String TIMER = "timer";
    public static String NO_OF_QUESTIONS = "noOfQuestions";

    LinearLayout llDownloadingData;
    LinearLayout llGameStarting;
    TextView tvCount;
    TextView tvLabelGameStartingIn;
    TextView tvLabelDownloadingData;
    ProgressBar downloadProgressBar;

    Animation animation;
    GameData gameData;
    int pageCount = 1;
    int countDown = 3;
    int timer;
    int noOfQuestions;
    String gameId;
    String userId;
    ArrayList<QuestionData> arrayListQuestionData = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_single_player_count_down);
        setTheme(R.style.Theme_Leanback);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        init();

    }

    private void init() {
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_zoom_out);
        gameData = (GameData) getIntent().getExtras().getBundle("Bundle").getParcelable(GameStartActivity.GAME_DATA);
        gameId = gameData.id;
        timer = gameData.singlePlayerTimer.equals("") || gameData.singlePlayerTimer.equals(null) || gameData.singlePlayerTimer.equals("null") ? 0 : Integer.parseInt(gameData.singlePlayerTimer);
        noOfQuestions = gameData.noOfQuestions.equals("") ? 0 : Integer.parseInt(gameData.noOfQuestions);
        userId = LocalStorage.getUserId();

        llDownloadingData = findViewById(R.id.llDownloadingData);
        llGameStarting = findViewById(R.id.llGameStarting);
        tvCount = findViewById(R.id.tvCount);
        tvLabelGameStartingIn = findViewById(R.id.tvLabelGameStartingIn);
        tvLabelDownloadingData = findViewById(R.id.tvLabelDownloadingData);
        downloadProgressBar = findViewById(R.id.downloadProgressBar);

        tvCount.startAnimation(animation);
        getQuestionsAPI();

    }

    private void setCountDownTimer() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (countDown > 1) {
                    tvCount.startAnimation(animation);
                    countDown--;
                    tvCount.setText(String.valueOf(countDown));
                    setCountDownTimer();
                } else {
                    if (arrayListQuestionData.size() > 0) {
                        Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList(QUESTION_DATA, arrayListQuestionData);
                        bundle.putParcelable(GameStartActivity.GAME_DATA, gameData);
                        bundle.putInt(TIMER, timer);
                        bundle.putInt(NO_OF_QUESTIONS, noOfQuestions);
                        startActivityForResult(
                                new Intent(GameSinglePlayerCountDownActivity.this, GameOngoingSinglePlayer.class)
                                        .putExtra("Bundle", bundle), WalletActivity.REQUEST_CODE
                        );
                        finish();
                    } else {
                        showError();
                    }
                }

            }
        }, 1000);
    }

    private void downloadData() {
        // Show game data downloading...
        DownloadUtil.getInstance().downloadItems(this, arrayListQuestionData, (success, error) -> {
            // show game starting in 3,2,1... and start the game
            delayDownloadingData();
        });
    }


    private void delayDownloadingData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hideDownloadingDataView();
                revealView(llGameStarting, 0);
                setCountDownTimer();
            }
        }, 1000);
    }

    private void hideDownloadingDataView() {
        llDownloadingData.setVisibility(View.VISIBLE);
        llDownloadingData.setAlpha(0);
    }


    private void getQuestionsAPI() {
        String url = Url.QUESTIONS + "&page_no=" + pageCount + "&game=" + gameId + "&user_ids=" + userId;
        try {
            Map<String, String> params = new HashMap<>();
            APIRequest apiRequest = new APIRequest(url, Request.Method.GET, params, null, this);
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    handleQuestionData(response, error);
                }
            });
        } catch (Exception e) {

        }
    }

    private void handleQuestionData(@Nullable String response, @Nullable Exception error) {

        try {

            if (error != null) {
                showError();
            } else {
                if (response != null) {
                    //Log.e("response", response);
                    JSONObject jsonObjectResponse = new JSONObject(response);
                    String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                    if (type.equalsIgnoreCase("error")) {
                        String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                        showError();
                    } else if (type.equalsIgnoreCase("ok")) {
                        JSONObject jsonObjectMsg = jsonObjectResponse.has("msg") ? jsonObjectResponse.getJSONObject("msg") : null;
                        if (jsonObjectMsg != null) {
                            JSONArray jsonArrayQuestions = jsonObjectMsg.has("questions") ? jsonObjectMsg.getJSONArray("questions") : null;
                            if (jsonArrayQuestions != null) {
                                int length = noOfQuestions < jsonArrayQuestions.length() ? noOfQuestions : jsonArrayQuestions.length();

                                for (int i = 0; i < length; i++) {
                                    QuestionData questionData = new QuestionData(jsonArrayQuestions.getJSONObject(i));
                                    arrayListQuestionData.add(questionData);
                                    if (noOfQuestions <= arrayListQuestionData.size()) {
                                        // TODO: call API with incremented page count
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            Log.e("Exception", e.getMessage());
            showError();
        }
        //delayDownloadingData();
        downloadData();
    }

    /**
     * This method shows error alert
     */
    private void showError() {
        String errorMessage = APIManager.GENERIC_API_ERROR_MESSAGE;
        AlertUtils.showAlert(getString(R.string.label_error), errorMessage, null, this, isPositiveAction -> finish());
    }


    /**
     * Reveal view with animation.
     *
     * @param view     View to be revealed with animation.
     * @param duration Time in milliseconds after which the view should hide.
     *                 Passing zero will use the default duration of 2.5 second.
     */
    private void revealView(@NonNull View view, int duration) {
        // Get previous visibility
        int previousVisibility = view.getVisibility();

        // Animation duration
        int animationDuration = 400;

        // Show for duration
        if (duration <= 0) {
            duration = 2500;
        }

        // Make view visible but alpha 0
        view.setVisibility(View.VISIBLE);
        view.setAlpha(0);

        // Reveal view by animating alpha change from 0 to 1
        view.animate()
                .alpha(1)
                .setDuration(animationDuration);

        /*// Hide question layout by animating alpha change from 1 to 0
        rlQuestionParent.animate()
                .alpha(0)
                .setDuration(animationDuration);*/

        // Hide view after specific duration
        new Handler().postDelayed(() -> {
            view.animate()
                    .alpha(0)
                    .setDuration(animationDuration)
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            view.setVisibility(previousVisibility);
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {
                        }
                    });
            /*// Reveal question layout by animating alpha change from 0 to 1
            rlQuestionParent.animate()
                    .alpha(1)
                    .setDuration(animationDuration);*/
        }, duration);
    }


    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        AnalyticsTracker.resumeTimer(AnalyticsTracker.GAME);
    }
}
