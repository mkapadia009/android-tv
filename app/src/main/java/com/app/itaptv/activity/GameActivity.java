package com.app.itaptv.activity;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.itaptv.R;
import com.app.itaptv.utils.AlertUtils;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends BaseActivity {

    protected Timer timer;
    protected MediaPlayer mediaPlayer;
    protected ProgressBar progressBar;
    protected TextView elapsedLbl, durationLbl;
    protected boolean isActivityDestroyed = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        initPlayer();
    }

    /**
     * Initialise player and player layouts.
     */
    @SuppressLint("StringFormatMatches")
    protected void initPlayer() {
        progressBar = findViewById(R.id.playerProgressBar);
        progressBar.setProgress(0);
        progressBar.setMax(0);

        elapsedLbl = findViewById(R.id.playerElapsedLbl);
        elapsedLbl.setText("--:--");
        durationLbl = findViewById(R.id.playerDurationLbl);
        durationLbl.setText("--:--");

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(mediaPlayer -> {
            int totalDuration = mediaPlayer.getDuration();
            progressBar.setMax(totalDuration);
            durationLbl.setText(getFormattedMilliSeconds(totalDuration));
            mediaPlayer.start();
            startObservingPlayer();
        });
        mediaPlayer.setOnErrorListener((mediaPlayer, what, extra) -> {
            AlertUtils.showAlert(getString(R.string.label_error),
                    String.format(getString(R.string.playback_error), what, extra),
                    null, this, null);
            stopObservingPlayer();
            return false;
        });
        mediaPlayer.setOnCompletionListener(mediaPlayer -> {
            progressBar.setProgress(progressBar.getMax());
            elapsedLbl.setText(durationLbl.getText().toString());
            stopObservingPlayer();
            playerStopped();
        });
    }

    /**
     * Called when player stops playing.
     */
    protected void playerStopped() {
    }

    /**
     * Play media file.
     *
     * @param filePath path of the media file.
     */
    protected void playFile(@NonNull String filePath) {
        if (mediaPlayer == null) initPlayer();
        try {
            mediaPlayer.setDataSource(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.showAlert(getString(R.string.playback_error), e.getMessage(), null, this,
                    null);
            return;
        }
        mediaPlayer.prepareAsync();
    }

    public void replay(View view) {
        if (mediaPlayer != null) {
            stopObservingPlayer();
            mediaPlayer.seekTo(0);
            if (!mediaPlayer.isPlaying()) mediaPlayer.start();
            startObservingPlayer();
        }
    }

    /**
     * Start observing player.
     */
    private void startObservingPlayer() {
        try {
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(() -> {
                        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                            int position = mediaPlayer.getCurrentPosition();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                progressBar.setProgress(position, true);
                            } else {
                                progressBar.setProgress(position);
                            }
                            elapsedLbl.setText(getFormattedMilliSeconds(position));
                        }
                    });
                }
            };
            timer = new Timer(); // new timer
            timer.scheduleAtFixedRate(timerTask, 0, 1000); // execute every 1 second
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Stop observing player.
     */
    private void stopObservingPlayer() {
        if (timer == null) return;
        timer.cancel(); // cancel timer
        timer.purge();  // remove all callbacks
    }

    /**
     * Get milliseconds formatted in mm:ss format.
     *
     * @param timeInMillis time in milli seconds.
     * @return formatted time in mm:ss.
     */
    @SuppressLint("DefaultLocale")
    private @NonNull
    String getFormattedMilliSeconds(int timeInMillis) {
        int timeInSeconds = timeInMillis / 1000;
        int minutes = timeInSeconds / 60;
        int seconds = timeInSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * Reset player and timer.
     */
    protected void resetPlayer() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
            progressBar.setProgress(0);
            progressBar.setMax(0);
        }
        stopObservingPlayer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null) {
            try {
                resetPlayer();
                //mediaPlayer.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        resetPlayer();
    }

    /**
     * Create spring animation.
     *
     * @param view          View to animate.
     * @param property      Property to animate.
     * @param finalPosition Final position/property value after animation.
     * @param stiffness     Stiffness value.
     * @param dampingRatio  Damping ratio.
     * @return SpringAnimation object.
     */
    protected SpringAnimation createSpringAnimation(@NonNull View view,
                                                    @NonNull DynamicAnimation.ViewProperty property,
                                                    float finalPosition, float stiffness,
                                                    float dampingRatio) {
        SpringForce spring = new SpringForce(finalPosition);
        spring.setStiffness(stiffness);
        spring.setDampingRatio(dampingRatio);
        SpringAnimation animation = new SpringAnimation(view, property);
        animation.setSpring(spring);
        return animation;
    }

    /**
     * Reveal view with spring animation.
     *
     * @param view View to be revealed.
     */
    protected void revealView(@NonNull View view) {
        // Initial properties
        view.setAlpha(0f);
        view.setScaleX(0f);
        view.setScaleY(0f);
        view.setVisibility(View.VISIBLE);

        // Animation properties
        float stiffness = SpringForce.STIFFNESS_LOW;
        float damping = SpringForce.DAMPING_RATIO_LOW_BOUNCY;
        float finalPosition = 1f;

        // Alpha
        createSpringAnimation(view, SpringAnimation.ALPHA, finalPosition, stiffness, damping)
                .start();

        // Scale x
        createSpringAnimation(view, SpringAnimation.SCALE_X, finalPosition, stiffness, damping)
                .start();

        // Scale y
        createSpringAnimation(view, SpringAnimation.SCALE_Y, finalPosition, stiffness, damping)
                .start();
    }

    /**
     * Execute task after delay.
     *
     * @param delay Delay in milliseconds.
     * @param block Block to be executed.
     */
    protected void executeAfter(long delay, @NonNull ExecutionBlock block) {
        if(!isActivityDestroyed){
        new Handler().postDelayed(block::executeBlock, delay);}
    }

    /**
     * ExecutionBlock interface used to execute code after delay.
     */
    protected interface ExecutionBlock {
        /**
         * This method is called by @method executeAfter after a delay.
         * Add your code to be executed in this block. The block runs on main thread.
         */
        void executeBlock();
    }

}
