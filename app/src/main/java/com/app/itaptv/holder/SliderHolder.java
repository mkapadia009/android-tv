package com.app.itaptv.holder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.app.itaptv.API.APIMethods;
import com.app.itaptv.R;
import com.app.itaptv.custom_interface.SliderListener;
import com.app.itaptv.structure.FeedContentData;
import com.app.itaptv.structure.HomeSliderObject;
import com.app.itaptv.utils.Constant;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.kalpesh.krecycleradapter.KRecyclerViewHolder;

/**
 * Created by poonam on 28/8/18.
 */

public class SliderHolder extends KRecyclerViewHolder {

    View view;
    ImageView ivSliderImage;
    TextView tvBannerNumber;
    VideoView videoView;
    ImageView ivVolumeUp;
    ImageView ivVolumeOff;

    String image;
    int arrLength;
    MediaPlayer mediaPlayer;
    SliderListener sliderListener;

    public SliderHolder(View itemView, int size, SliderListener scrollingStateBehaviour) {
        super(itemView);
        view = itemView;
        arrLength = size;
        sliderListener = scrollingStateBehaviour;
        ivSliderImage = view.findViewById(R.id.ivSliderImage);
        tvBannerNumber = view.findViewById(R.id.tvBannerNumber);
        videoView = view.findViewById(R.id.videoView);
        ivVolumeUp = view.findViewById(R.id.ivVolumeUp);
        ivVolumeOff = view.findViewById(R.id.ivVolumeOff);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void setData(Context context, Object itemObject) {
        super.setData(context, itemObject);

        HomeSliderObject homeSliderObject = (HomeSliderObject) itemObject;
       /*if (itemObject instanceof FeedContentData) {
            FeedContentData feedContentData = (FeedContentData) itemObject;
            image = feedContentData.image;
        } else if (itemObject instanceof GameData) {
            GameData gameData = (GameData) itemObject;
            image = gameData.image;
        }*/

        tvBannerNumber.setText(getAdapterPosition() + 1 + "/" + arrLength);

        if (homeSliderObject.feedContentData != null) {
            if (homeSliderObject.feedContentData.contentType.equalsIgnoreCase(Constant.AD)) {
                if (homeSliderObject.feedContentData.adFieldsData != null) {
                    if (homeSliderObject.feedContentData.mediaType.equalsIgnoreCase(FeedContentData.MEDIA_TYPE_IMAGE)) {
                        image = homeSliderObject.feedContentData.adFieldsData.attachment;
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
                                if (ivVolumeUp.getVisibility() != View.VISIBLE) {
                                    mp.setVolume(0.0f, 0.0f);
                                }
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
                image = homeSliderObject.feedContentData.image;
                ivSliderImage.setVisibility(View.VISIBLE);
                videoView.setVisibility(View.GONE);
                ivVolumeOff.setVisibility(View.GONE);
                ivVolumeUp.setVisibility(View.GONE);
            }
        } else {
            if (homeSliderObject.gameData != null) {
                image = homeSliderObject.gameData.image;
                ivSliderImage.setVisibility(View.VISIBLE);
                videoView.setVisibility(View.GONE);
                ivVolumeOff.setVisibility(View.GONE);
                ivVolumeUp.setVisibility(View.GONE);
            }
        }

        Glide.with(context)
                .load(image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(ivSliderImage);

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

       /* view.setOnTouchListener(new View.OnTouchListener() {
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
