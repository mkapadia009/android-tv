package com.app.itaptv.holder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.app.itaptv.API.APIMethods;
import com.app.itaptv.R;
import com.app.itaptv.custom_interface.SliderListener;
import com.app.itaptv.structure.FeedContentData;
import com.app.itaptv.structure.HomeSliderObject;
import com.app.itaptv.utils.Constant;
import com.app.itaptv.utils.Utility;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.kalpesh.krecycleradapter.KRecyclerViewHolder;

public class BigSliderHolder extends KRecyclerViewHolder {

    View view;
    ImageView ivSliderImage;
    TextView tvBannerNumber;
    VideoView videoView;
    ImageView ivVolumeUp;
    ImageView ivVolumeOff;
    Button btPlayNow;

    //String image;
    int arrLength;
    MediaPlayer mediaPlayer;

    EventListener listener;
    SliderListener sliderListener;

    public interface EventListener {
        void onEvent(HomeSliderObject data);
    }

    public BigSliderHolder(View itemView, int size, EventListener listener1, SliderListener scrollingStateBehaviour) {
        super(itemView);
        view = itemView;
        arrLength = size;
        listener = listener1;
        sliderListener = scrollingStateBehaviour;
        ivSliderImage = view.findViewById(R.id.ivSliderImage);
        tvBannerNumber = view.findViewById(R.id.tvBannerNumber);
        videoView = view.findViewById(R.id.videoView);
        ivVolumeUp = view.findViewById(R.id.ivVolumeUp);
        ivVolumeOff = view.findViewById(R.id.ivVolumeOff);
        btPlayNow = view.findViewById(R.id.btPlayNow);
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    protected void setData(Context context, Object itemObject) {
        super.setData(context, itemObject);

        HomeSliderObject homeSliderObject = (HomeSliderObject) itemObject;
        tvBannerNumber.setText(getAdapterPosition() + 1 + "/" + arrLength);

        if (homeSliderObject.feedContentData != null) {
            if (homeSliderObject.feedContentData.contentType.equalsIgnoreCase(Constant.AD)) {
                if (homeSliderObject.feedContentData.adFieldsData != null) {
                    //btPlayNow.setVisibility(View.GONE);
                    if (homeSliderObject.feedContentData.mediaType.equalsIgnoreCase(FeedContentData.MEDIA_TYPE_IMAGE)) {
                        Glide.with(context)
                                .load(homeSliderObject.feedContentData.adFieldsData.attachment)
                                .thumbnail(0.05f)
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(ivSliderImage);
                        ivSliderImage.setVisibility(View.VISIBLE);
                        videoView.setVisibility(View.GONE);
                        ivVolumeOff.setVisibility(View.GONE);
                        ivVolumeUp.setVisibility(View.GONE);
                        APIMethods.addEvent(context, Constant.VIEW, homeSliderObject.feedContentData.postId, Constant.BANNER, "HomeSlider");
                    } else if (homeSliderObject.feedContentData.mediaType.equalsIgnoreCase(FeedContentData.MEDIA_TYPE_VIDEO)) {
                        videoView.setVideoURI(Uri.parse(homeSliderObject.feedContentData.adFieldsData.attachment));
                        videoView.requestFocus();

                        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                videoView.start();
                                mp.setVolume(0.0f, 0.0f);
                                mediaPlayer = mp;
                            }
                        });
                        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                videoView.start();
                            }
                        });
                        videoView.setVisibility(View.VISIBLE);
                        ivSliderImage.setVisibility(View.GONE);
                        ivVolumeOff.setVisibility(View.VISIBLE);
                        ivVolumeUp.setVisibility(View.GONE);
                        APIMethods.addEvent(context, Constant.VIEW, homeSliderObject.feedContentData.postId, Constant.BANNER, "HomeSlider");
                    }
                }
            } else {
                String img = homeSliderObject.feedContentData.image;
                if (Utility.isTelevision()) {
                    if (!homeSliderObject.feedContentData.tvImage.isEmpty()) {
                        img = homeSliderObject.feedContentData.tvImage;
                    }
                }
                Glide.with(context)
                        .load(img)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .thumbnail(0.05f)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(ivSliderImage);
                ivSliderImage.setVisibility(View.VISIBLE);
                videoView.setVisibility(View.GONE);
                ivVolumeOff.setVisibility(View.GONE);
                ivVolumeUp.setVisibility(View.GONE);
            }
            if (homeSliderObject.feedContentData.buttonText.isEmpty()) {
                btPlayNow.setVisibility(View.GONE);
            } else {
                btPlayNow.setVisibility(View.VISIBLE);
                btPlayNow.setText(homeSliderObject.feedContentData.buttonText);
            }
        } else {
            if (homeSliderObject.gameData != null) {
                Glide.with(context)
                        .load(homeSliderObject.gameData.image)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .thumbnail(0.05f)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(ivSliderImage);
                ivSliderImage.setVisibility(View.VISIBLE);
                videoView.setVisibility(View.GONE);
                ivVolumeOff.setVisibility(View.GONE);
                ivVolumeUp.setVisibility(View.GONE);
                //btPlayNow.setVisibility(View.GONE);
                if (homeSliderObject.buttonText.isEmpty()) {
                    btPlayNow.setVisibility(View.GONE);
                } else {
                    btPlayNow.setVisibility(View.VISIBLE);
                    btPlayNow.setText(homeSliderObject.buttonText);
                }
            }
        }

        btPlayNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onEvent(homeSliderObject);
            }
        });

        ivVolumeUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivVolumeUp.setVisibility(View.GONE);
                ivVolumeOff.setVisibility(View.VISIBLE);
                if (mediaPlayer != null) {
                    mediaPlayer.setVolume(0.0f, 0.0f);
                }
            }
        });
        ivVolumeOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivVolumeOff.setVisibility(View.GONE);
                ivVolumeUp.setVisibility(View.VISIBLE);
                if (mediaPlayer != null) {
                    mediaPlayer.setVolume(1.0f, 1.0f);
                }
            }
        });

        /*view.setOnTouchListener(new View.OnTouchListener() {
            final GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public void onLongPress(MotionEvent e) {
                    sliderListener.stopScrolling();
                    super.onLongPress(e);
                }
            });

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return false;
            }
        });*/
    }
}
