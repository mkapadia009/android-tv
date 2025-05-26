package com.app.itaptv.holder;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.app.itaptv.R;
import com.app.itaptv.custom_interface.CustomRunnable;
import com.app.itaptv.structure.GameData;
import com.app.itaptv.utils.GameDateValidation;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.kalpesh.krecycleradapter.KRecyclerViewHolder;

/**
 * Created by poonam on 20/12/18.
 */

public class FeedGameHolder extends KRecyclerViewHolder {
    View view;
    CardView cvParent;
    CardView cvFeedImage;
    ImageView ivFeedImage;
    LinearLayout llLive;
    TextView tvFeedTitle;
    TextView tvTvChannelName;
    TextView tvTimer;
    TextView tvTurnBasedSubtitle;
    TextView btViewWinners;
    private Handler handler = new Handler();
    CustomRunnable customRunnable;

    public FeedGameHolder(View itemView) {
        super(itemView);
        view = itemView;
        cvParent = view.findViewById(R.id.cvParent);
        cvFeedImage = view.findViewById(R.id.cvFeedImage);
        ivFeedImage = view.findViewById(R.id.ivFeedImage);
        llLive = view.findViewById(R.id.llLive);
        tvFeedTitle = view.findViewById(R.id.tvFeedTitle);
        tvTvChannelName = view.findViewById(R.id.tvTvChannelName);
        tvTimer = view.findViewById(R.id.tvTimer);
        tvTurnBasedSubtitle = view.findViewById(R.id.tvTurnBasedSubtitle);
        btViewWinners = view.findViewById(R.id.btViewWinners);
    }

    @Override
    protected void setData(Context context, Object itemObject) {
        super.setData(context, itemObject);

        if (itemObject instanceof GameData) {
            GameData gameData = (GameData) itemObject;

            DisplayMetrics displaymetrics = new DisplayMetrics();
            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            //if you need one fix imageview in width
            int devicewidth = (int) (displaymetrics.widthPixels / 1.5);
            cvParent.getLayoutParams().width = devicewidth;
            cvFeedImage.getLayoutParams().height = (int) (devicewidth / 1.8);

            Glide.with(context)
                    .load(gameData.imageUrl)
                    .thumbnail(0.1f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(ivFeedImage);

            llLive.setVisibility(gameData.quizType.equals(GameData.QUIZE_TYPE_LIVE) ? View.VISIBLE : View.GONE);
            tvTurnBasedSubtitle.setVisibility(gameData.quizType.equals(GameData.QUIZE_TYPE_TURN_BASED) ? View.VISIBLE : View.GONE);
            tvFeedTitle.setText(gameData.postTitle);
            tvTurnBasedSubtitle.setText(gameData.description);
            tvTvChannelName.setText(gameData.tvChannelName);
            setTimerData(context, gameData);
        }
    }

    private void setTimerData(Context context, GameData gameData) {
        switch (gameData.feedGameId) {
            case GameData.GAME_ID_ONGOING_LIVE:
                /*String timeLeft = GameDateValidation.getTimeLeft(gameData.end);
                tvTimer.setText(timeLeft);*/
                customRunnable = new CustomRunnable(handler, tvTimer, gameData.end);
                bind();

                //setTimer(gameData);
                break;

            case GameData.GAME_ID_TRIVIA:
                break;

            case GameData.GAME_ID_UPCOMING_LIVE:
                String startsIn = GameDateValidation.getDateTime(context, gameData.start);
                tvTimer.setText(startsIn);
                break;

            case GameData.GAME_ID_COMPLETED_LIVE:
                tvFeedTitle.setVisibility(View.GONE);
                llLive.setVisibility(View.GONE);
                tvTurnBasedSubtitle.setVisibility(View.GONE);
                btViewWinners.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void bind(){
        handler.removeCallbacks(customRunnable);
        customRunnable.holder = tvTimer;
        handler.postDelayed(customRunnable,100);
    }

    public void clearAll(){
        CustomRunnable.isCancelled = true;
        handler.removeCallbacksAndMessages(null);
    }



    /*CountDownTimer countDownTimer;
    String timeLeft = "";
    private void setTimer(GameData gameData) {
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
                    tvTimer.setText(timeLeft);
                }

                @Override
                public void onFinish() {
                    //showAlertDialog();

                }
            }.start();

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }*/
}
