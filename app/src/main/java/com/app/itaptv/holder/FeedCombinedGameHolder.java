package com.app.itaptv.holder;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.app.itaptv.R;
import com.app.itaptv.custom_interface.CustomRunnable;
import com.app.itaptv.structure.FeedCombinedData;
import com.app.itaptv.structure.GameData;
import com.app.itaptv.utils.GameDateValidation;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.kalpesh.krecycleradapter.KRecyclerViewHolder;

import static com.app.itaptv.utils.Constant.TAB_BUY;

/**
 * Created by poonam on 20/12/18.
 */

public class FeedCombinedGameHolder extends KRecyclerViewHolder {
    View view;
    RelativeLayout cvParent;
    CardView cvFeedImage;
    ImageView ivFeedImage;
    LinearLayout llLive;
    LinearLayout llTexts;
    TextView tvFeedTitle;
    TextView tvTvChannelName;
    TextView tvTimer;
    TextView tvTurnBasedSubtitle;
    TextView btViewWinners;
    FeedCombinedData fCombinedData;
    private Handler handler = new Handler();
    CustomRunnable customRunnable;
    String title = "";

    public FeedCombinedGameHolder(View itemView, @Nullable FeedCombinedData feedCombinedData) {
        super(itemView);
        view = itemView;
        fCombinedData = feedCombinedData;
        cvParent = view.findViewById(R.id.cvParent);
        cvFeedImage = view.findViewById(R.id.cvFeedImage);
        ivFeedImage = view.findViewById(R.id.ivFeedImage);
        llTexts = view.findViewById(R.id.ll_texts_parent);
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

            tvFeedTitle.setText(gameData.postTitle);
            tvTurnBasedSubtitle.setText(gameData.description);
            tvTvChannelName.setText(gameData.tvChannelName);

            switch (fCombinedData.tileShape) {
                case "circle":
                    RequestOptions requestOptions = new RequestOptions()
                            .centerCrop()
                            .circleCrop()
                            .diskCacheStrategy(DiskCacheStrategy.ALL);

                    Glide.with(context)
                            .load(gameData.imageUrl)
                            .apply(requestOptions)
                            .into(ivFeedImage);

                    setIvLayoutParams(context, 100, 100);

                    tvFeedTitle.setGravity(Gravity.CENTER);
                    tvFeedTitle.setPadding(2, 0, 0, 0);
                    cvFeedImage.setBackgroundColor(ContextCompat.getColor(context, R.color.full_transparent));
                    break;

                case "square":
                    Glide.with(context)
                            .load(gameData.imageUrl)
                            .thumbnail(0.1f)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(ivFeedImage);

                    setIvLayoutParams(context, 100, 100);

                    tvFeedTitle.setGravity(Gravity.START);
                    tvFeedTitle.setPadding(2, 0, 15, 0);
                    break;

                case "v_rectangle":
                    Glide.with(context)
                            .load(gameData.imageUrl)
                            .thumbnail(0.1f)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(ivFeedImage);

                    setIvLayoutParams(context, 110, 180);

                    tvFeedTitle.setGravity(Gravity.START);
                    tvFeedTitle.setPadding(2, 0, 15, 0);
                    break;

                case "h_rectangle":
                    Glide.with(context)
                            .load(gameData.imageUrl)
                            .thumbnail(0.1f)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(ivFeedImage);

                    setIvLayoutParams(context, 185, 120);

                    tvFeedTitle.setGravity(Gravity.START);
                    tvFeedTitle.setPadding(2, 0, 15, 0);
                    break;
            }
            llLive.setVisibility(gameData.quizType.equals(GameData.QUIZE_TYPE_LIVE) ? View.VISIBLE : View.GONE);
            if (gameData.quizType.equals(GameData.QUIZE_TYPE_TURN_BASED)){
                if (!gameData.description.isEmpty()){
                    tvTurnBasedSubtitle.setVisibility(View.VISIBLE);
                }else{
                    tvTurnBasedSubtitle.setVisibility(View.GONE);
                }
            }
            //tvTurnBasedSubtitle.setVisibility(gameData.quizType.equals(GameData.QUIZE_TYPE_TURN_BASED) ? View.VISIBLE : View.GONE);
            setData(gameData);
            setTimerData(context, gameData);
        }
    }

    private void setIvLayoutParams(Context context, int dpWidth, int dpHeight) {
        final float scale = context.getResources().getDisplayMetrics().density;
        int dpWidthInPx = (int) (dpWidth * scale);
        int dpHeightInPx = (int) (dpHeight * scale);
        if (fCombinedData.feedType.equalsIgnoreCase(TAB_BUY)) {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(dpWidthInPx, dpHeightInPx);
            ivFeedImage.setLayoutParams(layoutParams);
        } else {
            FrameLayout.LayoutParams layoutParams1 = new FrameLayout.LayoutParams(dpWidthInPx, dpHeightInPx);
            ivFeedImage.setLayoutParams(layoutParams1);
        }
    }

    private void setData(GameData gameData) {
        title = gameData.postTitle;
        if (fCombinedData.showText.equals("yes")) {
            llTexts.setVisibility(View.VISIBLE);
            tvFeedTitle.setText(title);
        } else if (fCombinedData.showText.equals("no")) {
            llTexts.setVisibility(View.GONE);
        } else if (fCombinedData == null) {
            llTexts.setVisibility(View.GONE);
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

    public void bind() {
        handler.removeCallbacks(customRunnable);
        customRunnable.holder = tvTimer;
        handler.postDelayed(customRunnable, 100);
    }

    public void clearAll() {
        CustomRunnable.isCancelled = true;
        handler.removeCallbacksAndMessages(null);
    }
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
