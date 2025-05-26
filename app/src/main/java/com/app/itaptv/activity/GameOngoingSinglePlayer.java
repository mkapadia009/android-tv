package com.app.itaptv.activity;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.app.itaptv.API.APIManager;
import com.app.itaptv.API.APIMethods;
import com.app.itaptv.API.APIRequest;
import com.app.itaptv.API.APIResponse;
import com.app.itaptv.API.Url;
import com.app.itaptv.R;
import com.app.itaptv.custom_interface.SliderListener;
import com.app.itaptv.custom_widget.DotsIndicatorDecoration;
import com.app.itaptv.holder.CustomAdSliderHolder;
import com.app.itaptv.structure.AdFieldsData;
import com.app.itaptv.structure.AdMobData;
import com.app.itaptv.structure.AttachmentData;
import com.app.itaptv.structure.FeedContentData;
import com.app.itaptv.structure.GameData;
import com.app.itaptv.structure.QuestionChoiceData;
import com.app.itaptv.structure.QuestionData;
import com.app.itaptv.utils.Analyticals;
import com.app.itaptv.utils.AnalyticsTracker;
import com.app.itaptv.utils.Constant;
import com.app.itaptv.utils.CustomLinearLayoutManager;
import com.app.itaptv.utils.DownloadUtil;
import com.app.itaptv.utils.LocalStorage;
import com.app.itaptv.utils.Log;
import com.app.itaptv.utils.Utility;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.kalpesh.krecycleradapter.Interface.KRecyclerItemClickListener;
import com.kalpesh.krecycleradapter.Interface.KRecyclerViewHolderCallBack;
import com.kalpesh.krecycleradapter.KRecyclerViewAdapter;
import com.kalpesh.krecycleradapter.KRecyclerViewHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.dionsegijn.konfetti.KonfettiView;

/**
 * Created by poonam on 12/11/18.
 */

public class GameOngoingSinglePlayer extends GameActivity {

    ImageView ivQuestImage;
    RelativeLayout rlScore;
    RelativeLayout rlTimer;
    RelativeLayout rlParent;
    RelativeLayout rlQuestionParent;
    RelativeLayout rlRightAnswer;
    RelativeLayout rlWrongAnswer;
    RelativeLayout rlTimeOver;
    RelativeLayout rlAudioPlayer;
    RelativeLayout rlQuestion;
    LinearLayout llQuestionOptions;
    LinearLayout llAttachment;
    CardView cvQuestion;
    TextView tvTimerCount;
    TextView tvQuestion;
    TextView tvWonCoins;
    TextView tvQuestionCount;
    ProgressBar pbTimer;
    AdView mAdView;
    AdRequest adRequest;
    ConstraintLayout clAdHolder;
    ImageView ivCustomAd;
    ImageView ivClose;
    PlayerView playerView;
    ImageView ivVolumeUp;
    ImageView ivVolumeOff;
    KonfettiView konfettiView;
    LinearLayout llAd;
    String userId;
    String gameId;
    int position;
    int currentQuestionCoins;
    int totalCoinsEarned;
    boolean isQuestionLoaded;
    boolean isTimerFinished;
    private boolean resumePlayer = false;

    private ExoPlayer player;
    /*
     * Timer
     */
    private static CountDownTimer countDownTimer;
    private long timeCountInMilliSeconds = 60000;

    private enum TimerStatus {
        STARTED,
        STOPPED
    }

    private TimerStatus timerStatus = TimerStatus.STOPPED;

    GameData gameData;
    ArrayList<QuestionData> arrayListQuestionData;
    int noOfQuestions;
    int timer;
    int totalQuestions;
    int pageCount = 2;
    int dataInterval = 0;

    private RecyclerView rvSliderAd;
    private CustomLinearLayoutManager layoutManager;
    private KRecyclerViewAdapter adapterSliderAd;
    private boolean flagDecoration = false;
    private ArrayList<AdFieldsData> adSliderObjectArrayList = new ArrayList<>();

    private String sliderIdAd;
    // for slider
    private Runnable runnable = null;
    private int count = 0;
    private int visibleItemPosition;
    private int sliderPosition = -1;
    private boolean shouldSlide = true;
    private boolean flagAd = false;
    private boolean isFirstTimeLoading = true;

    @Nullable
    @Override
    KonfettiView getConfettiView() {
        return konfettiView;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_ongoing);
        getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setTheme(R.style.Theme_Leanback);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        init();
    }

    private void init() {
        Bundle bundle = this.getIntent().getExtras().getBundle("Bundle");
        arrayListQuestionData = bundle.getParcelableArrayList(GameSinglePlayerCountDownActivity.QUESTION_DATA);
        totalQuestions = arrayListQuestionData != null ? arrayListQuestionData.size() : 0;
        dataInterval = totalQuestions / 2;
        gameData = bundle.getParcelable(GameStartActivity.GAME_DATA);
        timer = bundle.getInt(GameSinglePlayerCountDownActivity.TIMER);
        noOfQuestions = bundle.getInt(GameSinglePlayerCountDownActivity.NO_OF_QUESTIONS);
        userId = LocalStorage.getUserId();
        gameId = gameData.id;

        ivQuestImage = findViewById(R.id.ivQuestImage);
        rlScore = findViewById(R.id.rlScore);
        rlTimer = findViewById(R.id.rlTimer);
        rlParent = findViewById(R.id.rlParent);
        rlQuestionParent = findViewById(R.id.rlQuestionParent);
        rlRightAnswer = findViewById(R.id.rlRightAnswer);
        rlWrongAnswer = findViewById(R.id.rlWrongAnswer);
        rlTimeOver = findViewById(R.id.rlTimeOver);
        rlAudioPlayer = findViewById(R.id.rlAudioPlayer);
        rlQuestion = findViewById(R.id.rlQuestion);
        llQuestionOptions = findViewById(R.id.llQuestionOptions);
        llAttachment = findViewById(R.id.llAttachment);
        cvQuestion = findViewById(R.id.cvQuestion);
        tvTimerCount = findViewById(R.id.tvTimerCount);
        tvQuestion = findViewById(R.id.tvQuestion);
        tvWonCoins = findViewById(R.id.tvWonCoins);
        tvQuestionCount = findViewById(R.id.tvQuestionCount);
        pbTimer = findViewById(R.id.pbTimer);
        konfettiView = findViewById(R.id.konfettiView);

        rlParent.setVisibility(View.GONE);
        rlScore.setVisibility(View.GONE);
        rlTimer.setVisibility(View.VISIBLE);
        tvTimerCount.setText(String.valueOf(timer));

        mAdView = findViewById(R.id.adViewGameQuestion);
        clAdHolder = findViewById(R.id.cl_ad_holder);
        ivCustomAd = findViewById(R.id.ivCustomAd);
        ivClose = findViewById(R.id.ivClose);
        playerView = findViewById(R.id.playerView);
        ivVolumeUp = findViewById(R.id.ivVolumeUp);
        ivVolumeOff = findViewById(R.id.ivVolumeOff);
        rvSliderAd = findViewById(R.id.rvSliderAd);
        adRequest = new AdRequest.Builder().build();

        llAd = findViewById(R.id.llAd);

        initialGameSetup();
        //setBackgroundImage();

        new Handler().postDelayed(this::setQuestion, 500);
        String id = getResources().getResourceEntryName(findViewById(R.id.adViewGameQuestion).getId());
        showBannerAd(id);
        //new Handler().postDelayed(this::setHeightLayout, 3500);
    }

    // ------------------------------ TIMER METHODS START ---------------------------------------

    /**
     * method to start count down timer
     */
    private void startCountDownTimer() {
        countDownTimer = new CountDownTimer(timeCountInMilliSeconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int timeLeft = (int) (millisUntilFinished / 1000);
                tvTimerCount.setText(String.valueOf(timeLeft));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    pbTimer.setProgress(timeLeft, true);
                } else {
                    pbTimer.setProgress(timeLeft);
                }
                if (timeLeft == 0) {
                    disableUserInteraction();
                }
                Log.i("Timer", "millis: " + millisUntilFinished);
                Log.i("Timer", "Tick: " + timeLeft);
            }

            @Override
            public void onFinish() {
                if (!isTimerFinished) {
                    showTimeOver();
                    isTimerFinished = true;
                }
                tvTimerCount.setText("0");
                pbTimer.setProgress(0);
                Log.i("Timer", "Finished");
            }

        }.start();
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


    /**
     * method to stop count down timer
     */
    private void stopCountDownTimer() {
        timerStatus = TimerStatus.STOPPED;
        if (countDownTimer != null) {
            Log.e("TEMP", "stopCountDownTimer");
            countDownTimer.cancel();
        }
    }


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
     * method to set circular progress bar values
     */
    private void setProgressBarValues() {
        pbTimer.setMax((int) timeCountInMilliSeconds / 1000);
        pbTimer.setProgress((int) timeCountInMilliSeconds / 1000);
    }

    // ------------------------------ TIMER METHODS END ---------------------------------------


    @Override
    protected void playerStopped() {
        super.playerStopped();
        // show quest
        //revealView(tvQuestion);
        // show options after 3 secs after quest
        if (arrayListQuestionData.size() > position) {
            QuestionData questionData = arrayListQuestionData.get(position);
            executeAfter(3000, () -> {
                setQuestionOptions(questionData.choices, questionData.points, questionData.id);
            });
        }
        // start timer after displaying options: done

    }

    private void setQuestion() {
        Log.e("TEMP", "setQuestion position " + position);
        resetPlayer();

        // set alpha 0
        tvQuestion.setAlpha(0f);
        tvTimerCount.setText(String.valueOf(timer));
        pbTimer.setMax(timer);
        pbTimer.setProgress(timer);

        if (arrayListQuestionData.size() > position) {
            QuestionData questionData = arrayListQuestionData.get(position);
            int questionTextSize = 0;

            // ------------ Set attachment ------------
            boolean showOptions = true;
            AttachmentData attachmentData = questionData.attachmentData;
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
        /*if (countDownTimer != null && isTimerFinished) {
            stopCountDownTimer();
        }
        startStop(timer);*/

            // ------------ Set question and choices ------------

            tvQuestionCount.setText(String.format(getString(R.string.question_count), position + 1, noOfQuestions));
            tvQuestion.setText(questionData.postTitle);
            // showm options after 3 secs
            if (showOptions) {
                executeAfter(3000, () -> {
                    setQuestionOptions(questionData.choices, questionData.points, questionData.id);
                });
            }
            rlParent.setVisibility(View.VISIBLE);

            isQuestionLoaded = true;
            isTimerFinished = false;

            // Reveal question layout
            revealQuestionView();
        }

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
        //rlAudioPlayer.setVisibility(View.VISIBLE);
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
     */
    private void setQuestionOptions(ArrayList<QuestionChoiceData> arrayListChoices, String coins, int questionId) {
        disableUserInteraction();

        llQuestionOptions.removeAllViews();
        llQuestionOptions.setVisibility(View.GONE);
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
            optionButton.setTag(arrayListChoices.get(i).correct);
            optionButton.setId(questionId);
            llQuestionOptions.addView(viewOption);
            if (i == 0) {
                optionButton.requestFocus();
            }
            Utility.textFocusListener(optionButton);

            total_duration = duration * i;
            executeAfter(total_duration, () -> revealView(viewOption));
            executeAfter(total_duration, () -> llQuestionOptions.setVisibility(View.VISIBLE));
        }

        if (isFirstTimeLoading) {
            isFirstTimeLoading = false;
            setHeightLayout();
        }

        // start timer afer 2 secs / 1800 milli secs (0+300+600+900)

        executeAfter(total_duration + 500, () -> {
            if (countDownTimer != null && isTimerFinished) {
                stopCountDownTimer();
            }
            enableUserInteraction();
            startStop(timer);
        });

        position++;
    }

    public void selectAnswer(View view) {
        if (view instanceof Button) {

            if (countDownTimer != null) {
                stopCountDownTimer();
            }
            disableUserInteraction();

            Button button = (Button) view;
            LinearLayout llOption = (LinearLayout) button.getParent();
            String coins = LocalStorage.getPractiseQuesPoints(); // using global coins value as per new requirements
            int temp = Integer.parseInt(String.valueOf(button.getContentDescription()));
            if (coins != null && !coins.equals("0") && !coins.equals("")) {
                currentQuestionCoins = Integer.parseInt(String.valueOf(coins));

            } else {
                currentQuestionCoins = Integer.parseInt(String.valueOf(button.getContentDescription()));
            }
            String answer = button.getText().toString();
            int id = button.getId();
            Utility.setShadeBackground(llOption);
            button.setBackground(getDrawable(R.drawable.bg_black));

            boolean isCorrectAnswer = (boolean) button.getTag();
            startSelectAnswerThread(isCorrectAnswer);

            JSONObject object = new JSONObject();
            try {
                object.put("answer", answer);
                String activity_id = LocalStorage.getValue(LocalStorage.KEY_ACTIVITY_ID, "", String.class);
                object.put("activity_id", activity_id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            sendAnswer(object, id);
        }
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
        startResultUIThread();
    }

    Thread thread;

    // ------------------ Delay Methods START ----------------------//
    private void startSelectAnswerThread(boolean isCorrectAnswer) {
        isQuestionLoaded = false;
        thread = new Thread() {

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
                        startResultUIThread();
                    }
                });

            }

        };

// Don't forget to start the thread.
        thread.start();
    }

    private void startResultUIThread() {
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
                        if (position < noOfQuestions) {
                            enableUserInteraction();
                            setQuestion();
                            if (totalQuestions < noOfQuestions) {
                                if (position == totalQuestions - dataInterval) {
                                    getQuestionsAPI();
                                }
                            }
                            /*rlRightAnswer.setVisibility(View.GONE);
                            rlWrongAnswer.setVisibility(View.GONE);
                            rlTimeOver.setVisibility(View.GONE);
                            rlQuestionParent.setVisibility(View.VISIBLE);*/
                        } else {
                            navigateToGameResult();
                        }
                    }
                });

            }

        };

        // Don't forget to start the thread.
        thread.start();
    }

    private void navigateToGameResult() {
        DownloadUtil.getInstance().clear();
        Bundle bundle = new Bundle();
        bundle.putParcelable(GameResultActivity.GAME_DATA, gameData);
        bundle.putInt(GameResultActivity.GAME_TYPE, GameStartActivity.GAME_TYPE_SINGLE_USER);
        bundle.putInt(GameResultActivity.GAME_STATUS, GameResultActivity.GAME_WIN);
        bundle.putString(GameResultActivity.TOTAL_GAME_COINS, String.valueOf(totalCoinsEarned));
        bundle.putBoolean(GameResultActivity.GAME_TYPE_PRACTICE, true);
        startActivityForResult(new Intent(GameOngoingSinglePlayer.this, GameResultActivity.class)
                .putExtra("Bundle", bundle), WalletActivity.REQUEST_CODE);
        finish();
        /*startActivity(new Intent(this, GameResultNewActivity.class));
        finish();*/
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

    // ------------------ Delay Methods END ----------------------//

    private void disableUserInteraction() {
       /* getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);*/
    }

    private void enableUserInteraction() {
       // getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }


    private void sendAnswer(JSONObject answer, int id) {
        Analyticals.CallplayedQuestionApi(this, Analyticals.LIVE_GAME_QUESTION_ACTIVITY_TYPE, String.valueOf(id), Analyticals.LIVE_GAME_QUESTION_CONTEXT, gameData.id, answer, (success, activity_id, error) -> {

        });
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
                // TODO: Exit game
            } else {
                if (response != null) {
                    Log.e("response", response);
                    JSONObject jsonObjectResponse = new JSONObject(response);
                    String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                    if (type.equalsIgnoreCase("error")) {
                        // TODO: Exit game
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

                                totalQuestions = arrayListQuestionData.size();
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            Log.e("Exception", e.getMessage());
            // TODO: Exit game
        }

        downloadData();
    }

    private void downloadData() {
        // Show game data downloading...
        try {
            DownloadUtil.getInstance().downloadItems(this, arrayListQuestionData, (success, error) -> {
            });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (isQuestionLoaded) {
            DownloadUtil.getInstance().clear();
            super.onBackPressed();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (player != null && resumePlayer) {
            player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
            player.setPlayWhenReady(true);
            playerView.setPlayer(player);
        }
        AnalyticsTracker.resumeTimer(AnalyticsTracker.GAME);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (player != null) {
            player.setPlayWhenReady(false);
            resumePlayer = true;
            playerView.setPlayer(null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopCountDownTimer();
        isActivityDestroyed = true;
        closePlayer();
    }

    public void showBannerAd(String id) {
        String adType = Utility.getBannerAdType(id, GameOngoingSinglePlayer.this);
        if (!adType.isEmpty()) {
            if (adType.equalsIgnoreCase(Constant.ADMOB)) {
                mAdView.loadAd(adRequest);
                mAdView.setVisibility(View.VISIBLE);
                clAdHolder.setVisibility(View.GONE);
            } else if (adType.equalsIgnoreCase(Constant.CUSTOM)) {
                showCustomAd(id);
                mAdView.setVisibility(View.GONE);
            }
        }
    }

    public void showCustomAd(String id) {
        clAdHolder.setVisibility(View.VISIBLE);
        List<AdMobData> list = LocalStorage.getBannerAdMobList(LocalStorage.KEY_BANNER_AD_MOB, GameOngoingSinglePlayer.this);
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).id.equals(id)) {
                    if (list.get(i).feedContentObjectData != null) {
                        int finalI = i;
                        if (list.get(i).feedContentObjectData.mediaType.equalsIgnoreCase(FeedContentData.MEDIA_TYPE_IMAGE)) {
                            String url = list.get(i).feedContentObjectData.adFieldsData.attachment;
                            Glide.with(GameOngoingSinglePlayer.this)
                                    .load(url)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(ivCustomAd);
                            ivCustomAd.setVisibility(View.VISIBLE);
                            playerView.setVisibility(View.GONE);
                            APIMethods.addEvent(GameOngoingSinglePlayer.this, Constant.VIEW, list.get(i).feedContentObjectData.postId, Constant.BANNER, id);

                            ivCustomAd.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    setActionCustomAd(list.get(finalI).feedContentObjectData.adFieldsData, Constant.BANNER, id);
                                }
                            });

                        } else if (list.get(i).feedContentObjectData.mediaType.equalsIgnoreCase(FeedContentData.MEDIA_TYPE_VIDEO)) {
                            playVideoAd(list.get(i).feedContentObjectData.adFieldsData.attachment);
                            playerView.setVisibility(View.VISIBLE);
                            ivVolumeOff.setVisibility(View.VISIBLE);
                            ivCustomAd.setVisibility(View.GONE);
                            APIMethods.addEvent(GameOngoingSinglePlayer.this, Constant.VIEW, list.get(i).feedContentObjectData.postId, Constant.BANNER, id);

                            playerView.getVideoSurfaceView().setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    setActionCustomAd(list.get(finalI).feedContentObjectData.adFieldsData, Constant.BANNER, id);
                                }
                            });
                            ivVolumeUp.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {
                                        if (player != null && player.isPlaying()) {
                                            ivVolumeUp.setVisibility(View.GONE);
                                            ivVolumeOff.setVisibility(View.VISIBLE);
                                            player.setVolume(0.0f);
                                        }
                                    } catch (Exception e) {

                                    }
                                }
                            });
                            ivVolumeOff.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {
                                        if (player != null && player.isPlaying()) {
                                            ivVolumeOff.setVisibility(View.GONE);
                                            ivVolumeUp.setVisibility(View.VISIBLE);
                                            player.setVolume(1.0f);
                                        }
                                    } catch (Exception e) {

                                    }
                                }
                            });
                        } else if (list.get(i).feedContentObjectData.mediaType.equalsIgnoreCase(FeedContentData.MEDIA_TYPE_SLIDER)) {
                            updateOtherSliders(list.get(i).feedContentObjectData.arrayListAdFieldsData, id);
                            playerView.setVisibility(View.GONE);
                            ivVolumeOff.setVisibility(View.GONE);
                            ivCustomAd.setVisibility(View.GONE);
                            rvSliderAd.setVisibility(View.VISIBLE);
                        }
                    }
                    ivClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            closePlayer();
                        }
                    });
                }
            }
        }
    }

    private void setActionCustomAd(AdFieldsData adFieldsData, String location, String subLocation) {
        APIMethods.addEvent(GameOngoingSinglePlayer.this, Constant.CLICK, adFieldsData.postId, location, subLocation);
        switch (adFieldsData.adType) {
            case FeedContentData.AD_TYPE_IN_APP:
                finish();
                if (adFieldsData.contentType.equalsIgnoreCase(Constant.PAGES)) {
                    HomeActivity.getInstance().redirectToPage(adFieldsData.pageType);
                } else {
                    switch (adFieldsData.contentType) {
                        case FeedContentData.CONTENT_TYPE_ESPORTS:
                            HomeActivity.getInstance().getUserRegisteredTournamentInfo(String.valueOf(adFieldsData.contentId));
                            break;
                        default:
                            HomeActivity.getInstance().getMediaData(String.valueOf(adFieldsData.contentId), adFieldsData.contentType);
                    }
                }
                break;
            case FeedContentData.AD_TYPE_EXTERNAL:
                setActionOnAd(adFieldsData);
                break;
        }
    }

    private void setActionOnAd(AdFieldsData adFieldsData) {
        switch (adFieldsData.urlType) {
            case Constant.BROWSER:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(adFieldsData.externalUrl));
                startActivity(browserIntent);
                break;
            case Constant.WEBVIEW:
                startActivity(new Intent(GameOngoingSinglePlayer.this, BrowserActivity.class).putExtra("title", "").putExtra("posturl", adFieldsData.externalUrl));
                break;
        }
    }

    private void playVideoAd(String url) {
        playerView.setUseController(false);
        playerView.requestFocus();

        Uri uri = Uri.parse(url);
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(GameOngoingSinglePlayer.this, getApplicationContext().getPackageName(), bandwidthMeter);
        MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(uri));

        player = new ExoPlayer.Builder(GameOngoingSinglePlayer.this).build();
        player.addListener(new Player.Listener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == Player.STATE_ENDED) {
                    player.seekTo(0);
                }
            }

            @Override
            public void onPlayerError(PlaybackException error) {

            }
        });
        player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT);
        player.prepare(mediaSource);
        player.setPlayWhenReady(true);

        /*
          Volume is set to 0 as per requirement
          Use as per the case
         */
        player.setVolume(0.0f);

        playerView.setPlayer(player);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ViewTreeObserver vto = playerView.getViewTreeObserver();
                vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        try {
                            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                            if (Build.VERSION.SDK_INT >= 14) {
                                try {
                                    retriever.setDataSource(url, new HashMap<String, String>());
                                } catch (RuntimeException ex) {
                                    // something went wrong with the file, ignore it and continue
                                }
                            } else {
                                retriever.setDataSource(url);
                            }
                            int videoWidth = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
                            int videoHeight = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
                            retriever.release();

                            playerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                            int playerViewWidth = playerView.getWidth();
                            if (playerViewWidth < videoWidth) {
                                int scale = (Math.abs(playerViewWidth - videoWidth)) / 2;
                                int height = videoHeight - scale;
                                playerView.getLayoutParams().height = height;
                            } else {
                                int scale = (int) (Math.abs(playerViewWidth - videoWidth) * 1.5);
                                int height = Math.abs(videoHeight + scale);
                                playerView.getLayoutParams().height = height;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }, 2000);
    }

    private void closePlayer() {
        if (player != null) {
            player.setPlayWhenReady(false);
            player.stop();
            player.release();
            player = null;
        }
        if (playerView != null) {
            playerView.setPlayer(null);
        }
    }

    /**
     * Initializes slider recycler view
     * Set the slider recyclerview of Custom ads
     */
    private void setOtherSliderRecyclerView(String id) {
        adapterSliderAd = new KRecyclerViewAdapter(GameOngoingSinglePlayer.this, adSliderObjectArrayList, new KRecyclerViewHolderCallBack() {
            @Override
            public KRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_slider, viewGroup, false);

                adapterSliderAd.getItemCount();
                adapterSliderAd.getSelectedIndexes();
                return new CustomAdSliderHolder(layoutView, adSliderObjectArrayList.size(), Constant.BANNER, id, new SliderListener() {
                    @Override
                    public void stopScrolling() {
                        stopSliding();
                    }
                });
            }
        }, new KRecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClicked(KRecyclerViewHolder kRecyclerViewHolder, Object o, int i) {
                if (o instanceof AdFieldsData) {
                    AdFieldsData adFieldsData = (AdFieldsData) o;
                    setActionCustomAd(adFieldsData, Constant.BANNER, id);
                }
            }
        });

        layoutManager = new CustomLinearLayoutManager(GameOngoingSinglePlayer.this, LinearLayoutManager.HORIZONTAL, false);
        rvSliderAd.setLayoutManager(layoutManager);
        rvSliderAd.setNestedScrollingEnabled(false);
        rvSliderAd.setOnFlingListener(null);
        rvSliderAd.setAdapter(adapterSliderAd);

        // Dot indicator for banner
        final int radius = getResources().getDimensionPixelSize(R.dimen.dot_radius);
        final int dotsHeight = getResources().getDimensionPixelSize(R.dimen.dot_height);
        final int color = ContextCompat.getColor(GameOngoingSinglePlayer.this, R.color.colorAccent);
        final int inActiveColor = ContextCompat.getColor(GameOngoingSinglePlayer.this, R.color.game_gray);
        if (!flagDecoration) {
            rvSliderAd.addItemDecoration(new DotsIndicatorDecoration(radius, radius * 2, dotsHeight, inActiveColor, color));
            flagDecoration = true;
        }

        new PagerSnapHelper().attachToRecyclerView(rvSliderAd);

        rvSliderAd.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                shouldSlide = (newState == RecyclerView.SCROLL_STATE_IDLE);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    sliderPosition = layoutManager.findFirstCompletelyVisibleItemPosition();
                    startSliding();
                } else {
                    stopSliding();
                }
            }
        });
    }

    private Handler sliderHandler;
    private Runnable sliderRunnable;
    private long secondsToWait = 4000;

    private void startSliding() {
        if (sliderHandler == null) {
            sliderHandler = new Handler();
        }
        if (sliderRunnable == null) {
            sliderRunnable = this::changeSliderPage;
        }
        sliderHandler.postDelayed(sliderRunnable, secondsToWait);
    }

    private void stopSliding() {
        if (sliderHandler != null && sliderRunnable != null) {
            sliderHandler.removeCallbacks(sliderRunnable);
        }
    }

    @SuppressLint("SetTextI18n")
    private void changeSliderPage() {
        if (adSliderObjectArrayList.size() <= 1) return;
        try {
            LinearLayoutManager layoutManager = (LinearLayoutManager) rvSliderAd.getLayoutManager();
            if (layoutManager != null) {
                visibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition();
                if (visibleItemPosition > -1 && visibleItemPosition < adSliderObjectArrayList.size()) {
                    if (visibleItemPosition == adSliderObjectArrayList.size() - 1) {
                        // Scroll to first item
                        rvSliderAd.smoothScrollToPosition(0);
                    } else {
                        // Scroll to next item
                        rvSliderAd.smoothScrollToPosition(visibleItemPosition + 1);
                    }
                }
            }
        } catch (Exception ignored) {

        }
    }

    private void updateOtherSliders(ArrayList<AdFieldsData> adFieldsDataArrayList, String id) {
        setOtherSliderRecyclerView(id);
        flagAd = true;
        flagDecoration = false;
        adSliderObjectArrayList.clear();
        new Thread(() -> {
            for (AdFieldsData adFieldsData : adFieldsDataArrayList) {
                sliderIdAd = String.valueOf(adFieldsData.postId);
                adSliderObjectArrayList.addAll(adFieldsDataArrayList);
                break;
            }

            new Handler(Looper.getMainLooper()).post(() -> {
                if (adapterSliderAd != null)
                    adapterSliderAd.notifyDataSetChanged();
                startSliding();
            });
        }).start();
    }

    public void setHeightLayout() {
        ViewTreeObserver vto = llAd.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                llAd.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                llAd.getLayoutParams().height = llAd.getHeight();
            }
        });
    }
}
