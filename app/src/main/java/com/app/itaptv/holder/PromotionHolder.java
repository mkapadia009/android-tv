package com.app.itaptv.holder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;

import com.app.itaptv.API.APIMethods;
import com.app.itaptv.R;
import com.app.itaptv.structure.FeedContentData;
import com.app.itaptv.structure.HomeSliderObject;
import com.app.itaptv.utils.Constant;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.kalpesh.krecycleradapter.KRecyclerViewHolder;

public class PromotionHolder extends KRecyclerViewHolder {

    View view;
    ImageView ivPromotionSliderImage;
    VideoView videoView;
    String image;
    String receivedImage;
    ImageView ivVolumeUp;
    ImageView ivVolumeOff;
    MediaPlayer mediaPlayer;

    public PromotionHolder(View itemView, String promotionImage) {
        super(itemView);
        view = itemView;
        receivedImage = promotionImage;
        ivPromotionSliderImage = view.findViewById(R.id.ivPromotionSliderImage);
        videoView = view.findViewById(R.id.videoView);
        ivVolumeUp = view.findViewById(R.id.ivVolumeUp);
        ivVolumeOff = view.findViewById(R.id.ivVolumeOff);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void setData(Context context, Object itemObject) {
        super.setData(context, itemObject);
        HomeSliderObject homeSliderObject = (HomeSliderObject) itemObject;

        if (homeSliderObject.feedContentData != null) {
            if (homeSliderObject.feedContentData.postType.equalsIgnoreCase(Constant.AD)) {
                if (homeSliderObject.feedContentData.mediaType.equalsIgnoreCase(FeedContentData.MEDIA_TYPE_IMAGE)) {
                    image = homeSliderObject.feedContentData.adFieldsData.attachment;

                    ivPromotionSliderImage.setVisibility(View.VISIBLE);
                    videoView.setVisibility(View.GONE);
                    ivVolumeOff.setVisibility(View.GONE);
                    ivVolumeUp.setVisibility(View.GONE);
                    APIMethods.addEvent(context,Constant.VIEW,homeSliderObject.feedContentData.postId,Constant.BANNER,homeSliderObject.feedContentData.postId);
                } else if (homeSliderObject.feedContentData.mediaType.equalsIgnoreCase(FeedContentData.MEDIA_TYPE_VIDEO)) {
                    videoView.setVideoURI(Uri.parse(homeSliderObject.feedContentData.adFieldsData.attachment));
                    //videoView.requestFocus();
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
                    ivPromotionSliderImage.setVisibility(View.GONE);
                    ivVolumeOff.setVisibility(View.VISIBLE);
                    ivVolumeUp.setVisibility(View.GONE);
                    APIMethods.addEvent(context,Constant.VIEW,homeSliderObject.feedContentData.postId,Constant.BANNER,homeSliderObject.feedContentData.postId);
                }
            } else {
                image = homeSliderObject.feedContentData.imgUrl;
                videoView.setVisibility(View.GONE);
                ivPromotionSliderImage.setVisibility(View.VISIBLE);
                ivVolumeOff.setVisibility(View.GONE);
                ivVolumeUp.setVisibility(View.GONE);
            }
        } else {
            if (homeSliderObject.gameData != null) {
                image = homeSliderObject.gameData.imageUrl;
                ivPromotionSliderImage.setVisibility(View.VISIBLE);
                ivVolumeOff.setVisibility(View.GONE);
                ivVolumeUp.setVisibility(View.GONE);
            }
        }

        Glide.with(context)
                .load(receivedImage)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(ivPromotionSliderImage);

        ivVolumeUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivVolumeUp.setVisibility(View.GONE);
                ivVolumeOff.setVisibility(View.VISIBLE);
                if (mediaPlayer != null) {
                    mediaPlayer.setVolume(0.0f,0.0f);
                }
            }
        });
        ivVolumeOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivVolumeOff.setVisibility(View.GONE);
                ivVolumeUp.setVisibility(View.VISIBLE);
                if (mediaPlayer != null) {
                    mediaPlayer.setVolume(1.0f,1.0f);
                }
            }
        });
    }
}
