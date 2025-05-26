package com.app.itaptv.activity;

import android.animation.Animator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.itaptv.R;
import com.app.itaptv.structure.AttachmentData;
import com.app.itaptv.structure.GameData;
import com.app.itaptv.structure.QuestionChoiceData;
import com.app.itaptv.structure.QuestionData;
import com.app.itaptv.utils.Analyticals;
import com.app.itaptv.utils.DownloadUtil;
import com.app.itaptv.utils.GameConstants;
import com.app.itaptv.utils.GameDateValidation;
import com.app.itaptv.utils.LocalStorage;
import com.app.itaptv.utils.Log;
import com.app.itaptv.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import nl.dionsegijn.konfetti.KonfettiView;

public class GameOngoingLiveActivity extends GameActivity {
    private static String TAG = GameOngoingLiveActivity.class.getSimpleName();
    public static String GAME_DATA = "gameData";


    private byte GAME_STATUS;
    GameData gameData;

    RelativeLayout rlParent;
    RelativeLayout rlQuestionParent;
    RelativeLayout rlRightAnswer;
    RelativeLayout rlWrongAnswer;
    RelativeLayout rlTimeOver;
    RelativeLayout rlTimer;
    RelativeLayout rlAudioPlayer;
    RelativeLayout rlQuestion;
    LinearLayout llQuestionOptions;
    LinearLayout llLoader;
    LinearLayout llAttachment;
    LinearLayout llEndsIn;
    LinearLayout llLeftEndsIn;
    //LinearLayout llBlockLayer;
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
    TextView tvGameTime;
    TextView tvQuestionCount;
    CardView cvQuestion;
    CardView cvFooterAd;
    ImageView ivQuestImage;
    ImageView ivTimeCircle;
    ImageView ivGrayCircle;
    Button btReplay;
    KonfettiView konfettiView;
    ProgressBar pbTimer;
    Handler h = new Handler();
    Runnable runnable;
    CountDownTimer countDownTimer;


    /*
     *Live Game Variables
     */
    int totalQuestions;
    int questionNo = 0;
    int totalCoinsEarned;
    int currentQuestionCoins;
    private boolean isUserActionDone = false;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GameData.REQUEST_CODE_GAME) {
            if (data != null)
                setResult(GameData.REQUEST_CODE_GAME, data);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_ongoing_live);
        getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        init();
    }

    public void init() {
        GAME_STATUS = GameConstants.STOPPED;

        gameData = (GameData) getIntent().getExtras().getBundle("Bundle").getParcelable(GAME_DATA);
        totalQuestions = gameData.arrayquestionData != null ? gameData.arrayquestionData.size() : 0;

        rlQuestionParent = findViewById(R.id.rlQuestionParent);
        rlRightAnswer = findViewById(R.id.rlRightAnswer);
        rlWrongAnswer = findViewById(R.id.rlWrongAnswer);
        rlTimeOver = findViewById(R.id.rlTimeOver);
        rlTimer = findViewById(R.id.rlTimer);
        rlParent = findViewById(R.id.rlParent);
        rlAudioPlayer = findViewById(R.id.rlAudioPlayer);
        rlQuestion = findViewById(R.id.rlQuestion);
        llLoader = findViewById(R.id.llLoader);
        llQuestionOptions = findViewById(R.id.llQuestionOptions);
        llAttachment = findViewById(R.id.llAttachment);
        llEndsIn = findViewById(R.id.llEndsIn);
        llLeftEndsIn = findViewById(R.id.llLeftEndsIn);
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
        tvGameTime = findViewById(R.id.tvGameTime);
        tvQuestionCount = findViewById(R.id.tvQuestionCount);
        cvQuestion = findViewById(R.id.cvQuestion);
        cvFooterAd = findViewById(R.id.cvFooterAd);
        ivQuestImage = findViewById(R.id.ivQuestImage);
        ivTimeCircle = findViewById(R.id.ivTimeCircle);
        ivGrayCircle = findViewById(R.id.ivGrayCircle);
        btReplay = findViewById(R.id.btReplay);
        konfettiView = findViewById(R.id.konfettiView);
        pbTimer = findViewById(R.id.pbTimer);

        setQuizLayout();
        setTimer();
        initialGameSetup();
        //new Handler().postDelayed(this::setQuestionUIForLiveGame, 500);
        // startAlert();
    }

    private void initialGameSetup() {
        rlRightAnswer.setVisibility(View.VISIBLE);
        rlWrongAnswer.setVisibility(View.VISIBLE);
        rlTimeOver.setVisibility(View.VISIBLE);
        rlQuestionParent.setVisibility(View.VISIBLE);

        rlRightAnswer.setAlpha(0);
        rlWrongAnswer.setAlpha(0);
        rlTimeOver.setAlpha(0);
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

    @Nullable
    @Override
    KonfettiView getConfettiView() {
        return konfettiView;
    }

    private void setQuizLayout() {
        Utility.setShadeBackground(llEndsIn);
        rlParent.setVisibility(View.GONE);
        rlTimer.setVisibility(View.GONE);
        ivTimeCircle.setVisibility(View.GONE);
        ivGrayCircle.setVisibility(View.GONE);
        cvFooterAd.setVisibility(View.GONE);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) cvQuestion.getLayoutParams();
        params.setMargins(10, 0, 10, 20);
        cvQuestion.setLayoutParams(params);

        setTimerLayout();
    }

    private void setTimerLayout() {
        llEndsIn.post(() -> {
            int layoutWidth = llEndsIn.getWidth();
            llLeftEndsIn.getLayoutParams().width = layoutWidth / 2;
            tvGameTime.getLayoutParams().width = layoutWidth / 2;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        setQuestionUIForLiveGame();
    }

    private void setQuestionUIForLiveGame() {
        if (checkValidQuestionNo(questionNo)) {
            resetPlayer();
            // set alpha 0
            tvQuestion.setAlpha(0f);

            // ------------ Set attachment ------------
            boolean showOptions = true;
            AttachmentData attachmentData = gameData.arrayquestionData.get(questionNo).attachmentData;
            if (attachmentData != null) {
                if (attachmentData.hasAttachmantData) {
                    llAttachment.setVisibility(View.VISIBLE);
                    switch (attachmentData.type) {
                        case AttachmentData.TYPE_AUDIO:
                            setAudioPlayer(attachmentData.url);
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

            //--------------Set question number----------
            tvQuestionCount.setText(String.format(getString(R.string.question_count), questionNo + 1, totalQuestions));

            // ------------ Set question and choices ------------
            tvQuestion.setText(gameData.arrayquestionData.get(questionNo).postTitle);
            // showm options after 3 secs
            if (showOptions) {
                executeAfter(3000, () -> {
                    setQuestionOptions(gameData.arrayquestionData.get(questionNo).choices, gameData.arrayquestionData.get(questionNo).points, gameData.arrayquestionData.get(questionNo).id);
                });
            }
            rlParent.setVisibility(View.VISIBLE);
            // Reveal question layout
            revealQuestionView();

        } else {
            Bundle bundle = new Bundle();
            bundle.putParcelable(GameLiveResultActivity.GAME_DATA, gameData);
            bundle.putString(GameLiveResultActivity.TOTAL_GAME_COINS, String.valueOf(totalCoinsEarned));
            startActivityForResult(new Intent(this, GameLiveResultActivity.class)
                    .putExtra("Bundle", bundle), GameData.REQUEST_CODE_GAME);

        }
    }


    private boolean checkValidQuestionNo(int questionNo) {
        if (questionNo < gameData.arrayquestionData.size()) {
            return true;
        }
        return false;
    }

    /**
     * This method is called to set the height of question image dynamically by calculating image heightas per image height and width.
     */
    private void setQuestionImageHeight(String imageUrl) {

        ivQuestImage.setVisibility(View.VISIBLE);
        rlAudioPlayer.setVisibility(View.GONE);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int imageHeight = (int) (displaymetrics.widthPixels / 3);
        ivQuestImage.getLayoutParams().height = imageHeight;
        ivQuestImage.getLayoutParams().width = imageHeight;

        Bitmap bm = DownloadUtil.getInstance().getImage(imageUrl);
        if (bm != null && !bm.isRecycled()) {
            ivQuestImage.setImageBitmap(bm);
        }
    }

    private void setAudioPlayer(String audioUrl) {
        ivQuestImage.setVisibility(View.GONE);
        //rlAudioPlayer.setVisibility(View.VISIBLE);//comment
        revealView(rlAudioPlayer);
        initPlayer();
        String path = DownloadUtil.getInstance().getAudioPath(audioUrl);
        if (path != null) {
            playFile(path);
        }
    }


    /**
     * Sets button layout for question option
     *
     * @param arrayListChoices list of choices
     * @param id
     */
    private void setQuestionOptions(ArrayList<QuestionChoiceData> arrayListChoices, String coins, int id) {
        enableUserInteraction();

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
            optionButton.setTag(id);
            llQuestionOptions.addView(viewOption);

            total_duration = duration * i;
            executeAfter(total_duration, () -> revealView(viewOption));
        }
    }


    public void selectAnswer(View view) {
        disableUserInteraction();
        if (view instanceof Button) {
            Button button = (Button) view;
            LinearLayout llOption = (LinearLayout) button.getParent();
            currentQuestionCoins = Integer.parseInt(String.valueOf(button.getContentDescription()));
            String answer = button.getText().toString();
            int id = (int) button.getTag();
            Utility.setShadeBackground(llOption);
            button.setBackground(getDrawable(R.drawable.bg_black));
            isUserActionDone = true;


            JSONObject object = new JSONObject();
            try {
                object.put("answer", answer);
                String activity_id = LocalStorage.getValue(LocalStorage.KEY_ACTIVITY_ID, "", String.class);
                object.put("activity_id", activity_id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            sendAnswer(object, id);
            Log.i(TAG, "Coins Earned " + currentQuestionCoins);
            Log.i(TAG, "Coins Earned " + answer);

        }
    }


    private void sendAnswer(JSONObject answer, int id) {
        Analyticals.CallplayedQuestionApi(this, Analyticals.LIVE_GAME_QUESTION_ACTIVITY_TYPE, String.valueOf(id), Analyticals.LIVE_GAME_QUESTION_CONTEXT, gameData.id, answer, (success, activity_id, error) -> {
            if (success) {
                setQuestionResultForLive(answer);
            }
        });
    }

    private void setQuestionResultForLive(JSONObject result) {
        Log.i(TAG, "Coins Earned2 " + currentQuestionCoins);
        ArrayList<QuestionChoiceData> choiceDataArrayList = gameData.arrayquestionData.get(questionNo).choices;
        Log.d(TAG, choiceDataArrayList.toString());
        for (int i = 0; i < choiceDataArrayList.size(); i++) {
            Log.d(TAG, choiceDataArrayList.get(i).toString());
            QuestionChoiceData jsonObject = choiceDataArrayList.get(i);

            try {
                if (jsonObject.text.equalsIgnoreCase(result.getString("answer"))) {
                    boolean isCorrectAnswer = jsonObject.correct;
                    new Handler(Looper.getMainLooper()).post(() -> {
                        /*rlQuestionParent.setVisibility(View.GONE);
                        if (isCorrectAnswer) {
                            rlRightAnswer.setVisibility(View.VISIBLE);
                            tvWonCoins.setText(String.valueOf(currentQuestionCoins));
                            streamKonfetti(BaseActivity.TOP);
                            totalCoinsEarned = totalCoinsEarned + currentQuestionCoins;
                        } else {
                            rlWrongAnswer.setVisibility(View.VISIBLE);
                        }
                        startResultUIThreadForLive();*/
                        startSelectAnswerThread(isCorrectAnswer);
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }


    // ------------------ Delay Methods START ----------------------//
    private void startSelectAnswerThread(boolean isCorrectAnswer) {
        //isQuestionLoaded = false;
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
                        //rlQuestionParent.setVisibility(View.GONE);
                        if (isCorrectAnswer) {
                            revealView(rlRightAnswer, 0);
                            //rlRightAnswer.setVisibility(View.VISIBLE);
                            tvWonCoins.setText(String.valueOf(currentQuestionCoins));
                            streamKonfetti(BaseActivity.TOP);
                            // TODO: later for coins API
                            totalCoinsEarned = totalCoinsEarned + currentQuestionCoins;
                        } else {
                            //rlWrongAnswer.setVisibility(View.VISIBLE);
                            revealView(rlWrongAnswer, 0);
                        }
                        startResultUIThreadForLive();
                    }
                });

            }

        };

// Don't forget to start the thread.
        thread.start();
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

        // Hide question layout by animating alpha change from 1 to 0
        rlQuestionParent.animate()
                .alpha(0)
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


    private void startResultUIThreadForLive() {
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
                        /*rlRightAnswer.setVisibility(View.GONE);
                        rlWrongAnswer.setVisibility(View.GONE);
                        rlTimeOver.setVisibility(View.GONE);
                        rlQuestionParent.setVisibility(View.VISIBLE);*/
                        questionNo++;
                        setQuestionUIForLiveGame();
                    }
                });

            }

        };
        thread.start();
    }

    String timeLeft = "";

    private void setTimer() {
        SimpleDateFormat calendarDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date formattedGivenDateTime = calendarDateFormat.parse(gameData.end);
            long gameExpireMilliseconds = formattedGivenDateTime.getTime();
            long currentTimeMilliseconds = System.currentTimeMillis();
            long difference = gameExpireMilliseconds - currentTimeMilliseconds;


            countDownTimer = new CountDownTimer(difference, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    timeLeft = GameDateValidation.getTimeLeft(millisUntilFinished);
                    Log.e("Ongoing Time", timeLeft);
                    tvGameTime.setText(timeLeft);
                }

                @Override
                public void onFinish() {
                    showAlertDialog();

                }
            }.start();

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.your_time_is_over)).setCancelable(
                false).setPositiveButton(getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DownloadUtil.getInstance().clear();
                        dialog.cancel();
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void disableUserInteraction() {
      /*  getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);*/
    }

    private void enableUserInteraction() {
        //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        h.removeCallbacks(runnable);
    }

    @Override
    protected void playerStopped() {
        super.playerStopped();
        // show quest
        revealView(tvQuestion);
        // show options after 3 secs after quest
        QuestionData questionData = gameData.arrayquestionData.get(questionNo);
        executeAfter(3000, () -> {
            setQuestionOptions(questionData.choices, questionData.points, questionData.id);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelCountDown();
    }

    private void cancelCountDown() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    public void onBackPressed() {
        DownloadUtil.getInstance().clear();
        super.onBackPressed();
    }
}
