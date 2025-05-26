package com.app.itaptv.holder;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.app.itaptv.R;
import com.app.itaptv.structure.FeedContentData;
import com.app.itaptv.structure.MediaData;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.kalpesh.krecycleradapter.KRecyclerViewHolder;

public class ProductMediaHolder extends KRecyclerViewHolder {

    ImageView ivMedia;
    ConstraintLayout clVideo;
    VideoView videoView;
    ImageView ivVolumeUp;
    ImageView ivVolumeOff;
    MediaPlayer mediaPlayer;

    public ProductMediaHolder(View itemView) {
        super(itemView);
        ivMedia = itemView.findViewById(R.id.ivMedia);
        clVideo = itemView.findViewById(R.id.clVideo);
        videoView = itemView.findViewById(R.id.videoView);
        ivVolumeUp = itemView.findViewById(R.id.ivVolumeUp);
        ivVolumeOff = itemView.findViewById(R.id.ivVolumeOff);
    }

    @Override
    protected void setData(Context context, Object itemObject) {
        super.setData(context, itemObject);

        if (itemObject instanceof MediaData) {

            MediaData mediaData = (MediaData) itemObject;
            if (mediaData.type.equalsIgnoreCase(FeedContentData.MEDIA_TYPE_IMAGE)) {
                ivMedia.setVisibility(View.VISIBLE);
                clVideo.setVisibility(View.GONE);
                Glide.with(context)
                    .load(mediaData.file)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(ivMedia);
            }else if (mediaData.type.equalsIgnoreCase(FeedContentData.MEDIA_TYPE_VIDEO)){
                ivMedia.setVisibility(View.GONE);
                clVideo.setVisibility(View.VISIBLE);
                videoView.setVideoURI(Uri.parse(mediaData.file));
                videoView.requestFocus();
                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        videoView.start();
                        mp.setVolume(0.0f,0.0f);
                        mediaPlayer=mp;
                    }
                });
                videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        videoView.start();
                    }
                });
            }

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
}
