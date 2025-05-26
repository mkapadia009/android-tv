package com.app.itaptv.activity;

import android.animation.Animator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.app.itaptv.API.APIManager;
import com.app.itaptv.API.APIRequest;
import com.app.itaptv.API.APIResponse;
import com.app.itaptv.API.Url;
import com.app.itaptv.R;
import com.app.itaptv.custom_widget.GameStateIndicatorDialog;
import com.app.itaptv.structure.AttachmentData;
import com.app.itaptv.structure.GameChatData;
import com.app.itaptv.structure.GameChatScoreData;
import com.app.itaptv.structure.GameData;
import com.app.itaptv.structure.QuestionChoiceData;
import com.app.itaptv.structure.QuestionData;
import com.app.itaptv.utils.AlertUtils;
import com.app.itaptv.utils.DownloadUtil;
import com.app.itaptv.utils.GameConstants;
import com.app.itaptv.utils.LocalStorage;
import com.app.itaptv.utils.Log;
import com.app.itaptv.utils.MyAnimation;
import com.app.itaptv.utils.Utility;
import com.shephertz.app42.gaming.multiplayer.client.WarpClient;
import com.shephertz.app42.gaming.multiplayer.client.command.WarpResponseResultCode;
import com.shephertz.app42.gaming.multiplayer.client.events.ChatEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.ConnectEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.LiveRoomInfoEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.LobbyData;
import com.shephertz.app42.gaming.multiplayer.client.events.MoveEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomData;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.UpdateEvent;
import com.shephertz.app42.gaming.multiplayer.client.listener.ConnectionRequestListener;
import com.shephertz.app42.gaming.multiplayer.client.listener.NotifyListener;
import com.shephertz.app42.gaming.multiplayer.client.listener.RoomRequestListener;
import com.shephertz.app42.gaming.multiplayer.client.listener.TurnBasedRoomListener;
import com.shephertz.app42.gaming.multiplayer.client.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import nl.dionsegijn.konfetti.KonfettiView;

/**
 * Created by poonam on 20/9/18.
 */

public class GameOngoingActivity extends GameActivity implements ConnectionRequestListener,
        RoomRequestListener, TurnBasedRoomListener, NotifyListener {

    private static String TAG = GameOngoingActivity.class.getSimpleName();

    public static String GAME_DATA = "gameData";
    public static final int REQUEST_CODE_OPPONENT_NOT_FOUND = 201;
    public static final String SINGLE_PLAYER_GAME = "singlePlayerGame";

    RelativeLayout rlParent;
    RelativeLayout rlFindingOpponents;
    RelativeLayout rlDownloadingData;
    RelativeLayout rlQuestionParent;
    RelativeLayout rlRightAnswer;
    RelativeLayout rlWrongAnswer;
    RelativeLayout rlTimeOver;
    RelativeLayout rlYourTurn;
    RelativeLayout rlTimer;
    RelativeLayout rlScore;
    RelativeLayout rlOpponentTurn;
    RelativeLayout rlAudioPlayer;
    RelativeLayout rlQuestion;
    RelativeLayout rlNoOpponentFound;
    RelativeLayout rlRecoverConnection;
    LinearLayout llQuestionOptions;
    LinearLayout llAttachment;
    LinearLayout llPlayer1;
    LinearLayout llPlayer2;
    LinearLayout llChildPlayer1;
    LinearLayout llChildPlayer2;
    TextView tvPlayer1;
    TextView tvPlayer2;
    TextView tvPlayer1Points;
    TextView tvPlayer2Points;
    TextView tvTimerCount;
    TextView tvQuestion;
    TextView tvLabelBingo;
    TextView tvLabelOops;
    TextView tvLabelSorry;
    TextView tvWonCoins;
    TextView tvWon;
    TextView tvOpponentTurn;
    TextView tvQuestionCount;
    TextView tvLabelRecovering;
    TextView tvLabelWeakConnection;
    CardView cvQuestion;
    CardView cvFooterAd;
    ImageView ivQuestImage;
    Button btReplay;
    Button btCancel;
    KonfettiView konfettiView;
    ProgressBar pbTimer;
    GameStateIndicatorDialog gameStateIndicatorDialog;

    GameData gameData;
    GameChatData gameChatData;
    String gameId;
    String tempQuestionType;
    String userIds;
    int totalCoinsEarned;
    int currentQuestionCoins;
    int pageCount = 1;
    int pausedTime;
    boolean isFirstQuestion = true;
    boolean isTimeOut;
    boolean isQuestionLoaded;
    boolean isTimerFinished = true;

    /*
     * WarpClient
     */
    private WarpClient warpClient;
    private String roomId = null;
    private String userId = "";
    private Handler handler = new Handler();
    private int recoveryCount = 0;
    private int questionCount = 0;
    private boolean isUserTurn = false;
    private boolean isUserActionDone = false;
    private boolean nextQuestDownloaded = false;
    private byte GAME_STATUS;
    private JSONObject jsonObjectQuestion = null;

    ArrayList<QuestionData> arrayListQuestionData = new ArrayList<>();

    /*
     * Timer
     */
    private long timeCountInMilliSeconds = 1 * 60000;

    private enum TimerStatus {
        STARTED,
        STOPPED
    }

    private CountDownTimer countDownTimer;
    private TimerStatus timerStatus = TimerStatus.STOPPED;

    /*
     * Lifecycle methods
     */

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_ongoing);
/*      //Screenshot Not Allowed code
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);*/
        //Screen On Code
        getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (warpClient == null) {
            initWarpClient();
        }
        warpClient.addConnectionRequestListener(this);
        warpClient.addRoomRequestListener(this);
        warpClient.addTurnBasedRoomListener(this);
        warpClient.addNotificationListener(this);
        // TODO: show loader here


    }

    @Override
    protected void onStop() {
        super.onStop();
        if (warpClient != null) {
            warpClient.removeTurnBasedRoomListener(this);
            warpClient.removeRoomRequestListener(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (warpClient != null) {
            warpClient.removeConnectionRequestListener(this);
            warpClient.removeNotificationListener(this);
        }
    }

    @Override
    public void onBackPressed() {
        handleLeave();
        super.onBackPressed();
    }

    @Nullable
    @Override
    KonfettiView getConfettiView() {
        return konfettiView;
    }

    /**
     * Initialize data
     */

    private void initWarpClient() {
        try {
            warpClient = WarpClient.getInstance();
            warpClient.addConnectionRequestListener(this);
            warpClient.addRoomRequestListener(this);
            warpClient.addTurnBasedRoomListener(this);
            warpClient.addNotificationListener(this);
        } catch (Exception ex) {
            AlertUtils.showAlert(getString(R.string.label_error), ex.getMessage(), null, this,
                    (isPositiveAction) -> {
                        DownloadUtil.getInstance().clear();
                        finish();
                    });
        }
    }

    private void init() {
        roomId = getIntent().getStringExtra("roomId");
        userId = LocalStorage.getUserId();
        initWarpClient();

        GAME_STATUS = GameConstants.STOPPED;

        gameData = (GameData) getIntent().getExtras().getBundle("Bundle").getParcelable(GAME_DATA);
        gameId = gameData.id;


        rlQuestionParent = findViewById(R.id.rlQuestionParent);
        rlRightAnswer = findViewById(R.id.rlRightAnswer);
        rlWrongAnswer = findViewById(R.id.rlWrongAnswer);
        rlTimeOver = findViewById(R.id.rlTimeOver);
        rlYourTurn = findViewById(R.id.rlYourTurn);
        rlTimer = findViewById(R.id.rlTimer);
        rlScore = findViewById(R.id.rlScore);
        rlParent = findViewById(R.id.rlParent);
        rlFindingOpponents = findViewById(R.id.rlFindingOpponents);
        rlDownloadingData = findViewById(R.id.rlDownloadingData);
        rlOpponentTurn = findViewById(R.id.rlOpponentTurn);
        rlAudioPlayer = findViewById(R.id.rlAudioPlayer);
        rlQuestion = findViewById(R.id.rlQuestion);
        rlNoOpponentFound = findViewById(R.id.rlNoOpponentFound);
        rlRecoverConnection = findViewById(R.id.rlRecoverConnection);
        //llLoader = findViewById(R.id.llLoader);
        llQuestionOptions = findViewById(R.id.llQuestionOptions);
        llAttachment = findViewById(R.id.llAttachment);
        llPlayer1 = findViewById(R.id.llPlayer1);
        llPlayer2 = findViewById(R.id.llPlayer2);
        llChildPlayer1 = findViewById(R.id.llChildPlayer1);
        llChildPlayer2 = findViewById(R.id.llChildPlayer2);
        //llBlockLayer = findViewById(R.id.llBlockLayer);
        tvPlayer1 = findViewById(R.id.tvPlayer1);
        tvPlayer2 = findViewById(R.id.tvPlayer2);
        tvPlayer1Points = findViewById(R.id.tvPlayer1Points);
        tvPlayer2Points = findViewById(R.id.tvPlayer2Points);
        tvTimerCount = findViewById(R.id.tvTimerCount);
        tvQuestion = findViewById(R.id.tvQuestion);
        tvLabelBingo = findViewById(R.id.tvLabelBingo);
        tvLabelOops = findViewById(R.id.tvLabelOops);
        tvLabelSorry = findViewById(R.id.tvLabelSorry);
        tvWonCoins = findViewById(R.id.tvWonCoins);
        tvWon = findViewById(R.id.tvWon);
        tvOpponentTurn = findViewById(R.id.tvOpponentTurn);
        tvQuestionCount = findViewById(R.id.tvQuestionCount);
        tvLabelRecovering = findViewById(R.id.tvLabelRecovering);
        tvLabelWeakConnection = findViewById(R.id.tvLabelWeakConnection);
        cvQuestion = findViewById(R.id.cvQuestion);
        cvFooterAd = findViewById(R.id.cvFooterAd);
        ivQuestImage = findViewById(R.id.ivQuestImage);
        btReplay = findViewById(R.id.btReplay);
        btCancel = findViewById(R.id.btCancel);
        konfettiView = findViewById(R.id.konfettiView);
        pbTimer = findViewById(R.id.pbTimer);
        gameStateIndicatorDialog = new GameStateIndicatorDialog(this);

        rlTimer.setVisibility(View.VISIBLE);
        rlScore.setVisibility(View.VISIBLE);
        cvFooterAd.setVisibility(View.GONE);
        initialGameSetup();
        //new Handler().postDelayed(this::setQuestionUI, 500);

        showFindingOpponent();

        /*setQuestionTypeHeader();
        setQuestionOptions(4);*/
    }

    private void initialGameSetup() {
        tvQuestionCount.setVisibility(View.GONE);
        rlRightAnswer.setVisibility(View.VISIBLE);
        rlWrongAnswer.setVisibility(View.VISIBLE);
        rlTimeOver.setVisibility(View.VISIBLE);
        rlYourTurn.setVisibility(View.VISIBLE);
        rlQuestionParent.setVisibility(View.VISIBLE);

        rlRightAnswer.setAlpha(0);
        rlWrongAnswer.setAlpha(0);
        rlTimeOver.setAlpha(0);
        rlYourTurn.setAlpha(0);
        rlQuestionParent.setAlpha(0);
        rlQuestionParent.setAlpha(0);

        revealQuestionView();
    }

    private void revealQuestionView() {
        rlQuestionParent.setVisibility(View.VISIBLE);
        rlQuestionParent.setAlpha(0);
        rlQuestionParent.animate()
                .alpha(1)
                .setDuration(400)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });
    }

    private void revealOpponentView() {
        llQuestionOptions.setVisibility(View.VISIBLE);
        rlOpponentTurn.setVisibility(View.VISIBLE);
        llQuestionOptions.setAlpha(0);
        rlOpponentTurn.setAlpha(0);

        /*rlOpponentTurn.animate()
                .alpha(1)
                .setDuration(400)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });*/

        //buildDialog(R.style.DialogAnimation, getString(R.string.opponent_s_turn));
    }

    private void revealUserView() {
        llQuestionOptions.setVisibility(View.VISIBLE);
        rlOpponentTurn.setVisibility(View.VISIBLE);
        llQuestionOptions.setAlpha(0);
        rlOpponentTurn.setAlpha(0);
        buildDialog(R.style.DialogAnimation, getString(R.string.your_turn));
        llQuestionOptions.animate()
                .alpha(1)
                .setDuration(400)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });
    }

    private void revealNoOpponentFound() {
        revealView(rlNoOpponentFound);
    }

    /**
     * Reveal view with animation.
     *
     * @param view     View to be revealed with animation.
     * @param duration Time in milliseconds after which the view should hide.
     *                 Passing zero will use the default duration of 2.5 second.
     */
    private void revealView(@NonNull View view, int duration) {
        llQuestionOptions.removeAllViews();
        int animationDuration = 400;
        // Hide question layout by animating alpha change from 1 to 0
        rlQuestionParent.animate()
                .alpha(0)
                .setDuration(animationDuration);
        if (view != null) {
            // Get previous visibility
            int previousVisibility = view.getVisibility();

            // Animation duration


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
    }


    private void showFindingOpponent() {
        new Handler(Looper.getMainLooper()).post(() -> {
            rlFindingOpponents.setVisibility(View.VISIBLE);
            //rlParent.setVisibility(View.GONE);
            searchAnimation();
        });
    }

    private void hideFindingOpponent() {
        new Handler(Looper.getMainLooper()).post(() -> {
            rlFindingOpponents.setVisibility(View.GONE);
            //rlParent.setVisibility(View.VISIBLE);
        });
    }

    private void showDownloadingData() {
        new Handler(Looper.getMainLooper()).post(() -> {
            rlDownloadingData.setVisibility(View.VISIBLE);
            rlParent.setVisibility(View.GONE);
            searchAnimation();
        });
    }

    private void hideDownloadingData() {
        new Handler(Looper.getMainLooper()).post(() -> {
            rlDownloadingData.setVisibility(View.GONE);
            rlParent.setVisibility(View.VISIBLE);
        });
    }

    private void searchAnimation() {
        /*ImageView ivSearch_Download = findViewById(R.id.ivSearch_Download);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.RESTART);
        ivSearch_Download.startAnimation(animation);*/
        ImageView ivSearch_Download = findViewById(R.id.ivSearch_Download);
        Animation anim = new MyAnimation(ivSearch_Download, 180);
        anim.setDuration(3000);
        anim.setRepeatCount(Animation.INFINITE);
        ivSearch_Download.startAnimation(anim);
    }

    private void setQuestionTypeHeader() {
        switch (tempQuestionType) {
            case "audio":
                btReplay.setVisibility(View.VISIBLE);
                break;
            default:
                btReplay.setVisibility(View.GONE);
                //setQuestionImageHeight();
        }
    }

    private void setFooterAdHeight() {
        int footerAdHeight = Utility.getDeviceWidthHeight(this, "h");
        footerAdHeight = footerAdHeight / 12;
        cvFooterAd.getLayoutParams().height = footerAdHeight;
    }


    /**
     * This method is called to set the height of question image dynamically by calculating image heightas per image height and width.
     */
    private void setQuestionImageHeight(String imageUrl) {

        ivQuestImage.setVisibility(View.VISIBLE);
        rlAudioPlayer.setVisibility(View.GONE);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int imageHeight = (int) (displaymetrics.widthPixels / 4.5);
        ivQuestImage.getLayoutParams().height = imageHeight;
        ivQuestImage.getLayoutParams().width = imageHeight;

        Bitmap bm = DownloadUtil.getInstance().getImage(imageUrl);
        if (bm != null && !bm.isRecycled()) {
            ivQuestImage.setImageBitmap(bm);
        }
    }


    /**
     * Sets button layout for question option
     *
     * @param arrayListChoices list of choices
     */
    private void setQuestionOptions(ArrayList<QuestionChoiceData> arrayListChoices, String coins) {

        disableUserInteraction();
        llQuestionOptions.removeAllViews();
        long duration = 300;
        long total_duration = 0;
        for (int i = 0; i < arrayListChoices.size(); i++) {
            View viewOption = getLayoutInflater().inflate(R.layout.row_question_option_layout, null);
            viewOption.setAlpha(0f);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 30, 0, 0);
            viewOption.setLayoutParams(layoutParams);

            Button optionButton = viewOption.findViewById(R.id.btOption);
            optionButton.setId(i);
            optionButton.setContentDescription(coins.equals("") ? "0" : coins);
            optionButton.setText(arrayListChoices.get(i).text);
            llQuestionOptions.addView(viewOption);

            total_duration = duration * i;
            executeAfter(total_duration, () -> revealView(viewOption));
        }

        // start timer afer 2 secs / 1800 milli secs (0+300+600+900)
        executeAfter(total_duration + 500, () -> {
            /*if (countDownTimer != null && isTimerFinished) {
                stopCountDownTimer();
            }
            enableUserInteraction();
            startStop(timer);*/

            if (!isUserTurn)
                revealView(rlOpponentTurn);

            Log.e(TAG, "startTimer");
            if (isUserTurn) {
                try {

                    WarpClient.getInstance().invokeRoomRPC(roomId, "startTimer");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        });
    }

    public void selectAnswer(View view) {
        if (view instanceof Button) {

            if (countDownTimer != null) {
                stopCountDownTimer();
            }
            disableUserInteraction();

            Button button = (Button) view;
            LinearLayout llOption = (LinearLayout) button.getParent();
            currentQuestionCoins = Integer.parseInt(String.valueOf(button.getContentDescription()));
            /*Utility.setBackgroundColor(button, gameData.colorPrimary);
            Utility.setTextColor(button, Constant.colorBlack);*/
            isUserActionDone = true;
            String answer = button.getText().toString();
            Utility.setShadeBackground(llOption);


            startSelectAnswerThread(answer);
            Log.i(TAG, "Coins Earned " + currentQuestionCoins);
        }
    }


    /**
     * Calls on selection of option. Changes selected button style (Background, text color)
     *
     * @param view
     */

    public void selectedOption(View view) {
        if (view instanceof Button) {
            Button button = ((Button) view);
            /*Utility.setBackgroundColor(button, gameData.colorPrimary);
            Utility.setTextColor(button, Constant.colorBlack);*/
            rlQuestionParent.setVisibility(View.GONE);

            switch (button.getId()) {
                case 0:
                    rlRightAnswer.setVisibility(View.VISIBLE);
                    streamKonfetti(BaseActivity.TOP);
                    break;

                case 1:
                    rlWrongAnswer.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    rlTimeOver.setVisibility(View.VISIBLE);
                    break;
                case 3:

                    break;
            }
        }
    }

    public void cancelGame(View view) {
        onBackPressed();
    }

    /*
     * ConnectionRequestListener Methods
     */

    @Override
    public void onConnectDone(ConnectEvent event, String desc) {
        Log.e(TAG, "onConnectDone");
        Log.e(TAG, "Event " + event.getResult());
        runOnUiThread(() -> {
            // TODO: hide loader dialog here...
            //hideFindingOpponent();
            if (event.getResult() == WarpResponseResultCode.SUCCESS) {
                showToast(GameConstants.ALERT_CONN_SUCC);
            } else if (event.getResult() == WarpResponseResultCode.SUCCESS_RECOVERED) {
                // showToast(GameConstants.ALERT_CONN_RECOVERED);

            }
            if (event.getResult() == WarpResponseResultCode.CONNECTION_ERROR_RECOVERABLE) {
                if (recoveryCount == 0) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        showRecoveringConnection();
                        stopCountDownTimer();
                    });
                }

                if (recoveryCount >= GameConstants.MAX_RECOVERY_ATTEMPT) {
                    Log.e(TAG, GameConstants.ALERT_CONN_ERR_NON_RECOVABLE);
                    hideRecoveringConnection();
                    showErrorDialog(GameConstants.ALERT_CONN_FAIL);
                    return;
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG, GameConstants.RECOVER_TEXT);

                        recoveryCount++;
                        // TODO: show loader for recovering connection
                        //showFindingOpponent();
                        //showToast(GameConstants.RECOVER_TEXT);
                        warpClient.RecoverConnection();
                    }
                }, 5000);
            } else {
                Log.e(TAG, GameConstants.ALERT_CONN_RECOVERED);
                hideRecoveringConnection();
                /*AlertUtils.showAlert("Connection Failed",
                        GameConstants.ALERT_CONN_ERR_NON_RECOVABLE + event.getResult(),
                        null, this,
                        isPositiveAction -> {
                            onBackPressed();
                        });*/
            }
        });
    }

    @Override
    public void onDisconnectDone(ConnectEvent connectEvent) {
        Log.i(TAG, "onDisconnectDone");
    }

    @Override
    public void onInitUDPDone(byte b) {
        Log.e(TAG, "onInitUDPDone");
    }

    /*
     * RoomRequestListener Methods
     */

    @Override
    public void onSubscribeRoomDone(RoomEvent event) {
        Log.e(TAG, "onSubscribeRoomDone");
    }

    @Override
    public void onUnSubscribeRoomDone(RoomEvent roomEvent) {
        Log.e(TAG, "onUnSubscribeRoomDone");
    }

    @Override
    public void onJoinRoomDone(RoomEvent event, String desc) {
        Log.e(TAG, "onJoinRoomDone");
    }

    @Override
    public void onLeaveRoomDone(RoomEvent event) {
        Log.i(TAG, "onLeaveRoomDone: " + event.getResult());
        if (event.getData() != null && event.getData().getId().equals(roomId)) {
            roomId = null;
        }
    }

    @Override
    public void onGetLiveRoomInfoDone(LiveRoomInfoEvent liveRoomInfoEvent) {
        Log.e(TAG, "onGetLiveRoomInfoDone");
    }

    @Override
    public void onSetCustomRoomDataDone(LiveRoomInfoEvent liveRoomInfoEvent) {
        Log.e(TAG, "onSetCustomRoomDataDone");
    }

    @Override
    public void onUpdatePropertyDone(LiveRoomInfoEvent liveRoomInfoEvent, String s) {
        Log.e(TAG, "onUpdatePropertyDone");
    }

    @Override
    public void onLockPropertiesDone(byte b) {
        Log.e(TAG, "onLockPropertiesDone");
    }

    @Override
    public void onUnlockPropertiesDone(byte b) {
        Log.e(TAG, "onUnlockPropertiesDone");
    }

    @Override
    public void onRPCDone(byte b, String s, Object o) {
        Log.e("---------", "OnRPCDone");

    }

    /*
     * TurnBasedRoomListener
     */

    @Override
    public void onSendMoveDone(byte result, String desc) {
        isTimerFinished = false;
        Log.e(TAG, "onSendMoveDone");
        // Response will be like below:
        // boolean - right/wrong answer
        // score - 1-0 (both users)
        if (result == WarpResponseResultCode.SUCCESS) {
            isUserActionDone = false;
        } else {
            //showToast(GameConstants.ALERT_SEND_FAIL + result);
        }
        setQuestionResult(desc);
    }

    @Override
    public void onStartGameDone(byte b, String s) {
        Log.e(TAG, "onStartGameDone");
    }

    @Override
    public void onStopGameDone(byte b, String s) {
        Log.e(TAG, "onStopGameDone");
    }

    @Override
    public void onGetMoveHistoryDone(byte b, MoveEvent[] moveEvents) {
        Log.e(TAG, "onGetMoveHistoryDone");
    }

    /*
     * NotifyListener
     */

    @Override
    public void onRoomCreated(RoomData roomData) {
        Log.e(TAG, "onRoomCreated");
    }

    @Override
    public void onRoomDestroyed(RoomData roomData) {
        Log.e(TAG, "onRoomDestroyed");
    }

    @Override
    public void onUserLeftRoom(RoomData roomData, String s) {
        Log.e(TAG, "onUserLeftRoom");
    }

    @Override
    public void onUserJoinedRoom(RoomData roomData, String s) {
        Log.e(TAG, "onUserJoinedRoom");
    }

    @Override
    public void onUserLeftLobby(LobbyData lobbyData, String s) {
        Log.e(TAG, "onUserLeftLobby");
    }

    @Override
    public void onUserJoinedLobby(LobbyData lobbyData, String s) {
        Log.e(TAG, "onUserJoinedLobby");
    }

    @Override
    public void onChatReceived(ChatEvent event) {
        Log.e(TAG, "Chat received. Sender: " + event.getSender() + ". Message: " + event.getMessage());
        runOnUiThread(() -> {
            if (event.getSender().equals(GameConstants.SERVER_NAME)) {
                if (event.getMessage().indexOf('#') != -1) {
                    int hashIndex = event.getMessage().indexOf('#');
                    final String code = event.getMessage().substring(0, hashIndex);
                    Log.e(TAG, "onChatReceived Code" + code);
                    final String message = event.getMessage().substring(hashIndex + 1, event.getMessage().length());
                    try {
                        final int CODE = Integer.parseInt(code);
                        Log.e("CODE", "" + CODE);

                        if (CODE == GameConstants.CODE_OPPONENT_NOT_FOUND) {
                            revealNoOpponentFound();

                        } else if (CODE == GameConstants.CODE_QUESTION) {
                            questionCount++;
                            // Question received
                            try {
                                JSONObject object = new JSONObject(message);
                                Log.i(TAG, "Question received: " + object);
                                jsonObjectQuestion = object;
                            } catch (JSONException e) {
                                Log.e(TAG, "Error: " + e.getMessage());
                            }
                            // TODO: add question to UI
                            Log.e(TAG, "onChatReceived");
                            //if (isFirstQuestion || isUserTurn || isTimeOut) {
                            revealView(null, 0);
                            restartOpponentTime();
                            if (isFirstQuestion) {
                                disableUserInteraction();

                                isFirstQuestion = false;

                                startResultUIThread();
                            }


                            if (questionCount == 5) {
                                getQuestionsAPI();
                            }

                            // }
                        } else if (CODE == GameConstants.CODE_ATTACHMENT_DATA) {
                            userIds = message;
                            hideFindingOpponent();
                            showDownloadingData();
                            getQuestionsAPI();

                        } else if (CODE == GameConstants.CODE_TIMER) {
                            if (countDownTimer != null && isTimerFinished) {

                                stopCountDownTimer();
                            }
                            enableUserInteraction();
                            startStop(timer);
                        } else if (CODE == GameConstants.CODE_RESUMED_TIME) {
                            pausedTime = Integer.parseInt(message);
                            new Handler(Looper.getMainLooper()).post(() -> {
                                hideGamePaused();
                                recoveryCount = 0;
                                Log.e("onUserResumed", "" + pausedTime);

                                startStop(pausedTime);
                            });

                        } else if (CODE == GameConstants.RESULT_GAME_OVER || CODE == GameConstants.RESULT_USER_LEFT) {
                            android.util.Log.i(TAG, "Game over or user left");
                            GAME_STATUS = GameConstants.STOPPED;
                            handleMessage(CODE, message);
                        }
                    } catch (NumberFormatException e) {
                        Log.e(TAG, "onChatReceived:NumberFormatException: " + e.getMessage());
                    }
                }
            }
        });
    }

    @Override
    public void onPrivateChatReceived(String s, String s1) {
        Log.e(TAG, "onPrivateChatReceived");
    }

    @Override
    public void onUpdatePeersReceived(UpdateEvent updateEvent) {
        Log.e(TAG, "onUpdatePeersReceived");

    }

    @Override
    public void onUserChangeRoomProperty(RoomData roomData, String s, HashMap<String, Object> hashMap,
                                         HashMap<String, String> hashMap1) {
        Log.e(TAG, "onUserChangeRoomProperty");

    }

    @Override
    public void onMoveCompleted(MoveEvent me) {
        Log.e(TAG, "onMoveCompleted");

        runOnUiThread(() -> {
            if (me.getNextTurn().equals(LocalStorage.getUserId())) {
                isUserTurn = true;
                isUserActionDone = false;
                // TODO: set turn indicator here...
                setUserTurnIndicator();
            } else {
                isUserTurn = false;
                // TODO: set opponents turn indicator here...
                setOpponentTurnIndicator();
            }
            try {
                if (me.getMoveData().trim().length() > 0) {
                    JSONObject object = new JSONObject(me.getMoveData());
                    /*TOP_CARD = object.getInt("top");
                    REQUESTED_CARD = -1;
                    setCardInImageView(topCardView, TOP_CARD);*/
                    Log.i(TAG, "Move data: " + object);
                } else {
                    Log.i("onMoveCompleted:me.getMoveData()", me.getMoveData());
                }
            } catch (JSONException e) {
                Log.e(TAG, "onMoveCompleted:JSONException: " + e.getMessage());
            }
        });

        if (isUserTurn) {
            try {
                WarpClient.getInstance().invokeRoomRPC(roomId, "serveQuestion");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        new Handler(Looper.getMainLooper()).post(() -> {

            if (isUserTurn) {
                showYourTurn();
                tvTimerCount.setText("0");
                pbTimer.setProgress(0);
                isTimerFinished = true;
            } else {
                if (isTimerFinished) {
                    showTimeOver();
                    tvTimerCount.setText("0");
                    pbTimer.setProgress(0);
                    isTimerFinished = true;
                }
            }
        });
    }

    @Override
    public void onGameStarted(String sender, String roomId, String nextTurn) {
        Log.e(TAG, "onGameStarted");

        // TODO: hide loader here...
        isFirstQuestion = true;

        //hideLoader();
        runOnUiThread(() -> {
            if (GAME_STATUS == GameConstants.STOPPED) {
                GAME_STATUS = GameConstants.RUNNING;
            } else if (GAME_STATUS == GameConstants.PAUSED) {
                GAME_STATUS = GameConstants.RUNNING;
            }
            isUserTurn = nextTurn.equals(Util.userName);
            //tvPlayer1.setText(Util.userName);
            //tvPlayer2.setText(nextTurn);
            if (isUserTurn) {
                // TODO: set turn indicator here...
                setUserTurnIndicator();
            } else {
                // TODO: set opponents turn indicator here...
                setOpponentTurnIndicator();
            }
        });

        /*try {
            Log.e(TAG,"serveQuestion");
            WarpClient.getInstance().invokeRoomRPC(roomId, "serveQuestion");
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public void onGameStopped(String sender, String data) {
        Log.e(TAG, "onGameStopped");

        if (sender.equals(GameConstants.SERVER_NAME)) {
            runOnUiThread(() -> {
                if (GAME_STATUS == GameConstants.RUNNING) {
                    GAME_STATUS = GameConstants.PAUSED;
                    handleGamePause(sender);
                }
            });
        }
    }

    @Override
    public void onUserPaused(String s, boolean b, String s1) {
        Log.e(TAG, "onUserPaused");
        new Handler(Looper.getMainLooper()).post(() -> {
            showGamePaused();
            stopCountDownTimer();
        });


    }

    @Override
    public void onUserResumed(String s, boolean b, String s1) {
        Log.e(TAG, "onUserResumed");
        /*new Handler(Looper.getMainLooper()).post(() -> {
            Log.e("onUserResumed", "" + pausedTime);
            startStop(pausedTime + 1);
        });*/

    }

    /*
     *
     */

    private void sendAnswer(String answer) {
        if (!isUserTurn) {
            //AlertUtils.showToast("Not your turn", 0, this);
        } else {
            if (isUserActionDone) {
                try {
                    JSONObject object = new JSONObject();
                    // TODO: Update here (coordinate with Siddhesh)...
                    object.put("answer", answer);
                    Log.e("onSendMoveClicked", object.toString());
                    warpClient.sendMove(object.toString());


                } catch (JSONException e) {
                    Log.e("onSendMoveClicked", e.toString());
                }
            } else {
                //AlertUtils.showToast("Please complete your turn", 0, this);
            }
        }
    }

    private void restartOpponentTime() {
        if (countDownTimer != null) {
            stopCountDownTimer();
        }
        disableUserInteraction();
    }

    private void handleMessage(int code, String data) {
        if (code == GameConstants.RESULT_USER_LEFT || code == GameConstants.RESULT_GAME_OVER) {
            roomId = null;
            try {
                //showToast("Game result: " + data);
                JSONObject message = new JSONObject(data);
                if (message.has("win")) {
                    GameChatScoreData gameChatScoreData =
                            new GameChatScoreData(message.has("scores") ? message.getJSONObject("scores") : null);
                    String userWin = message.getString("win");
                    //showToast("Winner is: " + userWin);
                    navigateToGameResult(userWin, gameChatScoreData);
                }
            } catch (JSONException e) {
                Log.e(TAG, "Handel message error: " + e.getMessage());
            }
        }
    }

    private void handleGamePause(String username) {
        /*showToast("This Game is stopped by " + username + " because all user's are" +
                " not online this time.");*/
    }

    private void handleLeave() {
        if (roomId != null) {
            DownloadUtil.getInstance().clear();
            warpClient.leaveRoom(roomId);
            roomId = null;
        }
        warpClient.disconnect();
    }

    private void showToast(@NonNull String message) {
        runOnUiThread(() -> AlertUtils.showToast(message, 0, this));
        Log.d(TAG, message);
    }

    int timer = 0;

    private void setQuestionUI() {

        if (jsonObjectQuestion != null) {
            gameChatData = new GameChatData(jsonObjectQuestion);
            if (gameChatData.questionData != null) {
                timer = gameChatData.timer;

                // ------------ Set scores------------
                HashMap<String, Integer> mapPlayers = gameChatData.gameChatScoreData.mapPlayers;
                if (mapPlayers.size() == 2) {
                    for (String userId : mapPlayers.keySet()) {
                        String score = String.valueOf(mapPlayers.get(userId));
                        if (userId.equals(LocalStorage.getUserId())) {
                            tvPlayer1Points.setText(score);
                        } else {
                            tvPlayer2Points.setText(score);
                        }

                    }
                }

                resetPlayer();
                // set alpha 0
                tvQuestion.setAlpha(0f);
                tvTimerCount.setText(String.valueOf(timer));
                pbTimer.setMax(timer);
                pbTimer.setProgress(timer);

                // ------------ Set attachment ------------
                boolean showOptions = true;
                AttachmentData attachmentData = gameChatData.questionData.attachmentData;
                if (attachmentData != null) {
                    if (attachmentData.hasAttachmantData) {
                        llAttachment.setVisibility(View.VISIBLE);
                        switch (attachmentData.type) {
                            case AttachmentData.TYPE_AUDIO:
                                setAudioPlayer(attachmentData.url);
                                // show quest after 1 sec: done
                                executeAfter(1000, () -> {
                                    revealView(tvQuestion);
                                });
                                // show quest after audio stops
                                // show options after 3 secs after quest
                                // start timer after displaying options
                                showOptions = false;
                                break;
                            case AttachmentData.TYPE_IMAGE:
                                setQuestionImageHeight(attachmentData.url);
                                // show quest after 1 sec: done
                                executeAfter(1000, () -> {
                                    revealView(tvQuestion);
                                });
                                // show options after 3 secs after quest: done
                                // start timer after displaying options: done
                                break;
                        }

                        //------------- Set Top Margin --------------------//
                        RelativeLayout.LayoutParams paramsQuestLayout = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        paramsQuestLayout.setMargins(0, 50, 0, 0);
                        paramsQuestLayout.addRule(RelativeLayout.BELOW, R.id.llAttachment);
                        rlQuestion.setLayoutParams(paramsQuestLayout);

                        //------------- Set Top Margin --------------------//
                        RelativeLayout.LayoutParams paramsQuestOpt = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        paramsQuestOpt.setMargins(0, 0, 0, 0);
                        paramsQuestOpt.addRule(RelativeLayout.BELOW, R.id.tvQuestion);
                        llQuestionOptions.setLayoutParams(paramsQuestOpt);

                        tvQuestion.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                        rlQuestionParent.setPadding(0, 0, 0, 0);

                    } else {
                        llAttachment.setVisibility(View.GONE);

                        //------------- Set Top Margin --------------------//
                        RelativeLayout.LayoutParams paramsQuestLayout = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        paramsQuestLayout.setMargins(0, 130, 0, 0);
                        paramsQuestLayout.addRule(RelativeLayout.BELOW, R.id.llAttachment);
                        rlQuestion.setLayoutParams(paramsQuestLayout);

                        //------------- Set Top Margin --------------------//
                        RelativeLayout.LayoutParams paramsQuestOpt = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        paramsQuestOpt.setMargins(0, 20, 0, 0);
                        paramsQuestOpt.addRule(RelativeLayout.BELOW, R.id.tvQuestion);
                        llQuestionOptions.setLayoutParams(paramsQuestOpt);


                        tvQuestion.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
                        rlQuestionParent.setPadding(0, 0, 0, 60);
                        // show quest
                        revealView(tvQuestion);
                        // show options after 3 secs after quest: done below
                        // start timer after displaying options: done below
                    }
                }


                // ------------ Set timer ------------

                //tvTimerCount.setText(String.valueOf(timer));
                /*if (countDownTimer != null) {
                    stopCountDownTimer();
                }
                startStop(timer);*/


                // ------------ Set question and choices ------------
                tvQuestion.setText(gameChatData.questionData.postTitle);
                // showm options after 2 secs
                if (showOptions) {
                    executeAfter(2000, () -> {
                        Log.e(TAG, "setQuestionOptions");
                        setQuestionOptions(gameChatData.questionData.choices, gameChatData.questionData.points);
                    });
                }
                rlParent.setVisibility(View.VISIBLE);

                isQuestionLoaded = true;
                //isTimerFinished = false;

                // Reveal question layout
                revealQuestionView();

            }
        }

        revealQuestionView();
    }

    private void setAudioPlayer(String audioUrl) {
        ivQuestImage.setVisibility(View.GONE);
        //rlAudioPlayer.setVisibility(View.VISIBLE);
        revealView(rlAudioPlayer);
        initPlayer();
        String path = DownloadUtil.getInstance().getAudioPath(audioUrl);
        if (path != null) {
            playFile(path);
        }
    }

    private void navigateToGameResult(String winUser, GameChatScoreData gameChatScoreData) {
        DownloadUtil.getInstance().clear();
        String yourScore = "";
        String opponentScore = "";
        HashMap<String, Integer> mapPlayers = gameChatScoreData.mapPlayers;
        if (mapPlayers.size() == 2) {
            for (String userId : mapPlayers.keySet()) {
                String score = String.valueOf(mapPlayers.get(userId));
                if (userId.equals(LocalStorage.getUserId())) {
                    yourScore = score;
                } else {
                    opponentScore = score;
                }

            }
        }

        int gameStatus = winUser.equals(LocalStorage.getUserId()) ? GameResultActivity.GAME_WIN : GameResultActivity.GAME_LOST;
        Bundle bundle = new Bundle();
        bundle.putParcelable(GameResultActivity.GAME_DATA, gameData);
        bundle.putInt(GameResultActivity.GAME_TYPE, GameStartActivity.GAME_TYPE_MULTI_USER);
        bundle.putInt(GameResultActivity.GAME_STATUS, gameStatus);
        bundle.putString(GameResultActivity.YOUR_SCORE, yourScore);
        bundle.putString(GameResultActivity.OPPONENT_SCORE, opponentScore);
        bundle.putString(GameResultActivity.TOTAL_GAME_COINS, gameData.bonusPoints);
        startActivity(new Intent(this, GameResultActivity.class)
                .putExtra("Bundle", bundle));
        finish();
    }

    private void setQuestionResult(String result) {
        if (result != null) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                boolean isCorrectAnswer = jsonObject.has("correct") ? jsonObject.getBoolean("correct") : false;
                new Handler(Looper.getMainLooper()).post(() -> {
                    //rlQuestionParent.setVisibility(View.GONE);

                    if (isCorrectAnswer) {
                        //rlRightAnswer.setVisibility(View.VISIBLE);
                        revealView(rlRightAnswer, 0);
                        tvWonCoins.setText(String.valueOf(currentQuestionCoins));
                        tvWonCoins.setVisibility(View.GONE);// as per new requirement
                        tvWon.setVisibility(View.GONE);// as per new requirement
                        streamKonfetti(BaseActivity.TOP);
                        totalCoinsEarned = totalCoinsEarned + currentQuestionCoins;
                    } else {
                        //rlWrongAnswer.setVisibility(View.VISIBLE);
                        revealView(rlWrongAnswer, 0);
                    }
                    //setQuestionUI();

                    startResultUIThread();
                });


            } catch (JSONException e) {

            }
        }

    }

    private void startResultUIThread() {

        Log.e(TAG, "startResultUIThread");
        Thread thread = new Thread() {

            @Override
            public void run() {

                // Block this thread for 3 seconds.
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                }

                // After sleep finished blocking, create a Runnable to run on the UI Thread.
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideDownloadingData();
                        enableUserInteraction();

                        setQuestionUI();
                    }
                });

            }

        };

// Don't forget to start the thread.
        thread.start();
    }

    private void startSelectAnswerThread(String answer) {
        Thread thread = new Thread() {

            @Override
            public void run() {

                // Block this thread for 1 seconds.
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {

                }

                // After sleep finished blocking, create a Runnable to run on the UI Thread.
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        sendAnswer(answer);
                    }
                });

            }

        };

// Don't forget to start the thread.
        thread.start();
    }

    private void setUserTurnIndicator() {
        setUserLayout();
        showQuestionOption();
        enableUserInteraction();
        //llBlockLayer.setVisibility(View.GONE);
    }

    private void setOpponentTurnIndicator() {
        setOpponentLayout();
        showOpponentTurn();
        disableUserInteraction();
        // llBlockLayer.setVisibility(View.VISIBLE);
    }

    private void setUserLayout() {
        Utility.setShadeBackground(llPlayer1);
        llChildPlayer1.setBackground(getDrawable(R.drawable.bg_black));
        llPlayer2.setBackground(getDrawable(R.drawable.bg_lightgray));
        llChildPlayer2.setBackground(getDrawable(R.drawable.bg_lightgray));
        tvPlayer1.setTextColor(getResources().getColor(R.color.colorAccent));
        tvPlayer1Points.setTextColor(getResources().getColor(R.color.colorAccent));
        tvPlayer2.setTextColor(getResources().getColor(R.color.tab_text_grey));
        tvPlayer2Points.setTextColor(getResources().getColor(R.color.tab_text_grey));
    }

    private void setOpponentLayout() {
        Utility.setShadeBackground(llPlayer2);
        llChildPlayer2.setBackground(getDrawable(R.drawable.bg_black));
        llPlayer1.setBackground(getDrawable(R.drawable.bg_lightgray));
        llChildPlayer1.setBackground(getDrawable(R.drawable.bg_lightgray));
        tvPlayer2.setTextColor(getResources().getColor(R.color.colorAccent));
        tvPlayer2Points.setTextColor(getResources().getColor(R.color.colorAccent));
        tvPlayer1.setTextColor(getResources().getColor(R.color.tab_text_grey));
        tvPlayer1Points.setTextColor(getResources().getColor(R.color.tab_text_grey));
    }

    private void disableUserInteraction() {
       /* getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);*/
    }

    private void enableUserInteraction() {
       // getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    // ------------------------ TIMER --------------------------------------------//

    /**
     * method to start and stop count down timer
     *
     * @param i
     */
    private void startStop(int i) {
        if (timerStatus == TimerStatus.STOPPED) {
            tvTimerCount.setText(String.valueOf(i));
            // call to initialize the timer values
            setTimerValues(i);
            // call to initialize the progress bar values
            setProgressBarValues();
            // making edit text not editable
            tvTimerCount.setEnabled(false);
            // changing the timer status to started
            timerStatus = TimerStatus.STARTED;
            // call to start the count down timer
            startCountDownTimer();

        } else {
            // making edit text editable
            tvTimerCount.setEnabled(true);
            // changing the timer status to stopped
            timerStatus = TimerStatus.STOPPED;
            stopCountDownTimer();

        }

    }

    /**
     * method to reset count down timer
     */
    private void reset() {
        stopCountDownTimer();
        startCountDownTimer();
    }

    /**
     * method to initialize the values for count down timer
     *
     * @param i
     */
    private void setTimerValues(int i) {
        int time = i;
        if (!tvTimerCount.getText().toString().isEmpty()) {
            // fetching value from edit text and type cast to integer
            time = Integer.parseInt(tvTimerCount.getText().toString().trim());
        } else {

        }
        // assigning values after converting to milliseconds
        timeCountInMilliSeconds = time * 1000;
    }

    /**
     * method to start count down timer
     */
    private void startCountDownTimer() {

        countDownTimer = new CountDownTimer(timeCountInMilliSeconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int timeLeft = (int) (millisUntilFinished / 1000);
                pausedTime = timeLeft;
                tvTimerCount.setText(String.valueOf(timeLeft));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    pbTimer.setProgress(timeLeft, true);
                } else {
                    pbTimer.setProgress(timeLeft);
                }
                if (timeLeft == 0) {
                    disableUserInteraction();
                }

            }


            @Override
            public void onFinish() {
                /*if (!isTimerFinished) {
                    showTimeOver();
                    isTimerFinished = true;
                }
                tvTimerCount.setText("0");
                pbTimer.setProgress(0);*/
            }

        }.start();
        //countDownTimer.start();
    }

    private void showTimeOver() {

        disableUserInteraction();
        isQuestionLoaded = false;
        //rlQuestionParent.setVisibility(View.GONE);
        revealView(rlTimeOver, 0);
        //rlTimeOver.setVisibility(View.VISIBLE);

        // call to initialize the progress bar values
        setProgressBarValues();
        // changing the timer status to stopped
        timerStatus = TimerStatus.STOPPED;
        //startResultUIThread();
        //sendAnswer("");

        startResultUIThread();
    }

    private void showRecoveringConnection() {
        revealView(rlRecoverConnection);
    }

    private void hideRecoveringConnection() {
        rlRecoverConnection.setVisibility(View.VISIBLE);
        rlRecoverConnection.setAlpha(0);
    }

    private void showGamePaused() {
        tvLabelWeakConnection.setVisibility(View.VISIBLE);
        tvLabelRecovering.setText(getString(R.string.game_paused));
        revealView(rlRecoverConnection);
    }

    private void hideGamePaused() {
        tvLabelWeakConnection.setVisibility(View.GONE);
        tvLabelRecovering.setText(getString(R.string.recovering_connection));
        rlRecoverConnection.setVisibility(View.VISIBLE);
        rlRecoverConnection.setAlpha(0);
    }

    private void showYourTurn() {
        disableUserInteraction();
        isQuestionLoaded = false;
        //rlQuestionParent.setVisibility(View.GONE);
        revealView(rlYourTurn, 0);
        //rlTimeOver.setVisibility(View.VISIBLE);

        // call to initialize the progress bar values
        setProgressBarValues();
        // changing the timer status to stopped
        timerStatus = TimerStatus.STOPPED;
        //startResultUIThread();
        //sendAnswer("");

        startResultUIThread();
    }

    /**
     * method to stop count down timer
     */
    private void stopCountDownTimer() {
        timerStatus = TimerStatus.STOPPED;
        if (countDownTimer != null)
            countDownTimer.cancel();
    }

    /**
     * method to set circular progress bar values
     */
    private void setProgressBarValues() {
        int maxTimerInMilliSeconds = timer * 1000;
        pbTimer.setMax((int) maxTimerInMilliSeconds / 1000);
        pbTimer.setProgress((int) timeCountInMilliSeconds / 1000);
    }

    private void showOpponentTurn() {
        revealOpponentView();
    }

    private void showQuestionOption() {
        revealUserView();
    }

    private void buildDialog(int animationSource, String turnLabel) {
        /*TextView tvLabelTurn = gameStateIndicatorDialog.findViewById(R.id.tvLabelTurn);
        tvLabelTurn.setText(turnLabel);
        gameStateIndicatorDialog.getWindow().getAttributes().windowAnimations = animationSource;
        gameStateIndicatorDialog.show();*/
    }

    @Override
    protected void playerStopped() {
        super.playerStopped();
        // show quest
        //revealView(tvQuestion);
        // show options after 2 secs after quest
        executeAfter(2000, () -> {
            if (gameChatData != null) {
                setQuestionOptions(gameChatData.questionData.choices, gameChatData.questionData.points);
            }
        });
        // start timer after displaying options: done
    }


    private void getQuestionsAPI() {
        nextQuestDownloaded = false;
        //pageCount++;
        arrayListQuestionData.clear();
        String url = Url.QUESTIONS + "&page_no=" + pageCount + "&game=" + gameId + "&user_ids=" + userIds;
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
                //showError();
            } else {
                if (response != null) {
                    Log.e("response", response);
                    JSONObject jsonObjectResponse = new JSONObject(response);
                    String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                    if (type.equalsIgnoreCase("error")) {
                        String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                        //showError();
                    } else if (type.equalsIgnoreCase("ok")) {

                        JSONObject jsonObjectMsg = jsonObjectResponse.has("msg") ? jsonObjectResponse.getJSONObject("msg") : null;
                        if (jsonObjectMsg != null) {
                            JSONArray jsonArrayQuestions = jsonObjectMsg.has("questions") ? jsonObjectMsg.getJSONArray("questions") : null;
                            pageCount = jsonObjectMsg.has("next_page") ? jsonObjectMsg.getInt("next_page") : 0;
                            if (jsonArrayQuestions != null) {

                                for (int i = 0; i < jsonArrayQuestions.length(); i++) {
                                    QuestionData questionData = new QuestionData(jsonArrayQuestions.getJSONObject(i));
                                    arrayListQuestionData.add(questionData);
                                }
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            Log.e("Exception", e.getMessage());
            //showError();
        }
        //delayDownloadingData();
        downloadData();
    }

    private void downloadData() {
        DownloadUtil.getInstance().downloadItems(this, arrayListQuestionData, (success, error) -> {
            Log.e(TAG, "Download completed");
            // show game starting in 3,2,1... and start the game
            nextQuestDownloaded = true;
            if (isFirstQuestion) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("username", userId);
                    Log.e(TAG, "setUserReady");
                    WarpClient.getInstance().invokeRoomRPC(roomId, "setUserReady", jsonObject);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /*private void noOpponentFound() {
        revealNoOpponentFound();
    }*/

    public void singlePlayerGame(View view) {
        Intent intent = getIntent();
        intent.putExtra(SINGLE_PLAYER_GAME, true);
        setResult(RESULT_OK, intent);
        onBackPressed();
    }

    private void showErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setView(R.layout.dialog_custom);
        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(true);
        alertDialog.show();

        TextView tvTitle = alertDialog.findViewById(R.id.tvStoreName);
        TextView btPositive = alertDialog.findViewById(R.id.btPositive);

        tvTitle.setText(message);
        btPositive.setText(R.string.ok);
        btPositive.setVisibility(View.VISIBLE);
        btPositive.setOnClickListener(v -> {
            onBackPressed();
        });
    }
}
