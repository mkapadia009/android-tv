package com.app.itaptv.holder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;

import com.app.itaptv.API.APIMethods;
import com.app.itaptv.R;
import com.app.itaptv.custom_interface.SliderListener;
import com.app.itaptv.structure.AdFieldsData;
import com.app.itaptv.structure.FeedContentData;
import com.app.itaptv.utils.Constant;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.kalpesh.krecycleradapter.KRecyclerViewHolder;

public class CustomAdSliderHolder extends KRecyclerViewHolder {
    View view;
    ImageView ivSliderImage;
    VideoView videoView;
    ImageView ivVolumeUp;
    ImageView ivVolumeOff;

    String image;
    String location;
    String subLocation;
    int arrLength;
    MediaPlayer mediaPlayer;
    SliderListener sliderListener;

    public CustomAdSliderHolder(View itemView, int size, String loc, String subLoc, SliderListener sliderListener1) {
        super(itemView);
        view = itemView;
        arrLength = size;
        location = loc;
        subLocation = subLoc;
        sliderListener = sliderListener1;
        ivSliderImage = view.findViewById(R.id.ivSliderImage);
        videoView = view.findViewById(R.id.videoView);
        ivVolumeUp = view.findViewById(R.id.ivVolumeUp);
        ivVolumeOff = view.findViewById(R.id.ivVolumeOff);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void setData(Context context, Object itemObject) {
        super.setData(context, itemObject);

        AdFieldsData adFieldsData = (AdFieldsData) itemObject;

        if (adFieldsData != null) {
            if (adFieldsData.mediaType.equalsIgnoreCase(FeedContentData.MEDIA_TYPE_IMAGE)) {
                image = adFieldsData.attachment;
                ivSliderImage.setVisibility(View.VISIBLE);
                videoView.setVisibility(View.GONE);
                ivVolumeOff.setVisibility(View.GONE);
                ivVolumeUp.setVisibility(View.GONE);
                Glide.with(context)
                        .load(adFieldsData.attachment)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(ivSliderImage);
                APIMethods.addEvent(context, Constant.VIEW, adFieldsData.postId, location, subLocation);
            } else if (adFieldsData.mediaType.equalsIgnoreCase(FeedContentData.MEDIA_TYPE_VIDEO)) {
                videoView.setVideoURI(Uri.parse(adFieldsData.attachment));
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
                        videoView.seekTo(0);
                    }
                });
                videoView.setVisibility(View.VISIBLE);
                ivSliderImage.setVisibility(View.GONE);
                ivVolumeOff.setVisibility(View.VISIBLE);
                ivVolumeUp.setVisibility(View.GONE);
                APIMethods.addEvent(context, Constant.VIEW, adFieldsData.postId, location, subLocation);
            }
        }

        ivVolumeUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        ivVolumeUp.setVisibility(View.GONE);
                        ivVolumeOff.setVisibility(View.VISIBLE);
                        mediaPlayer.setVolume(0.0f, 0.0f);
                    }
                } catch (Exception e) {

                }
            }
        });
        ivVolumeOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        ivVolumeOff.setVisibility(View.GONE);
                        ivVolumeUp.setVisibility(View.VISIBLE);
                        mediaPlayer.setVolume(1.0f, 1.0f);
                    }
                } catch (Exception e) {

                }
            }
        });

      /*  view.setOnTouchListener(new View.OnTouchListener() {
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
