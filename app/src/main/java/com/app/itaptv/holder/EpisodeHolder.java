package com.app.itaptv.holder;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.itaptv.R;
import com.app.itaptv.custom_interface.NavigationMenuCallback;
import com.app.itaptv.structure.FeedContentData;
import com.app.itaptv.structure.SubscriptionDetails;
import com.app.itaptv.utils.Constant;
import com.app.itaptv.utils.LocalStorage;
import com.app.itaptv.utils.Utility;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.kalpesh.krecycleradapter.KRecyclerViewHolder;

public class EpisodeHolder extends KRecyclerViewHolder {

    View view;
    ImageView ivImage;
    ImageView ivEpisodeDownload;
    TextView txtEpisodeTitle;
    TextView txtEpisodeSubtitle;
    ImageView ivPlay;
    NavigationMenuCallback navigationMenuCallback;

    public EpisodeHolder(View itemView, NavigationMenuCallback navigationMenuCallback1) {
        super(itemView);
        view = itemView;
        navigationMenuCallback = navigationMenuCallback1;
        ivImage = itemView.findViewById(R.id.ivImage);
        ivEpisodeDownload = itemView.findViewById(R.id.ivEpisodeDownload);
        txtEpisodeTitle = itemView.findViewById(R.id.txtEpisodeTitle);
        txtEpisodeSubtitle = itemView.findViewById(R.id.txtEpisodeSubtitle);
        ivPlay = itemView.findViewById(R.id.ivPlay);
    }


    @Override
    protected void setData(Context context, Object itemObject) {
        super.setData(context, itemObject);

        if (itemObject instanceof FeedContentData) {
            FeedContentData feedContentData = (FeedContentData) itemObject;
            //String episodeNameWithIndex = String.format(context.getString(R.string.episode_name_with_index), feedContentData.episodeNumber, feedContentData.postTitle);
            if (Utility.isTelevision()) {
                if (feedContentData.isFocused) {
                    view.setBackground(view.getContext().getDrawable(R.drawable.highlight_text_field));
                    feedContentData.isFocused = true;
                    itemView.requestFocus();
                } else {
                    view.setBackground(null);
                    feedContentData.isFocused = false;
                }
                view.setPadding(10, 5, 10, 5);
                view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        if (view.hasFocus()) {
                            view.setBackground(view.getContext().getDrawable(R.drawable.highlight_text_field));
                            feedContentData.isFocused = true;
                        } else {
                            view.setBackground(null);
                            feedContentData.isFocused = false;
                        }
                    }
                });

                if (navigationMenuCallback != null) {
                    itemView.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View view, int i, KeyEvent keyEvent) {
                            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                                switch (i) {
                                    case KeyEvent.KEYCODE_DPAD_UP:
                                    case KeyEvent.KEYCODE_DPAD_RIGHT:
                                    case KeyEvent.KEYCODE_DPAD_DOWN:
                                        break;
                                    case KeyEvent.KEYCODE_DPAD_LEFT:
                                        navigationMenuCallback.navMenuToggle(true);
                                        break;
                                }
                            }
                            return false;
                        }
                    });
                }
            }
            txtEpisodeTitle.setText(feedContentData.postTitle);
            /*if (feedContentData.attachmentData != null) {
                tvEpisodeTime.setText(feedContentData.attachmentData.duration);
            } else {
                tvEpisodeTime.setText(feedContentData.duration);
            }*/
            SubscriptionDetails sd = LocalStorage.getUserSubscriptionDetails();
            if (sd != null && sd.allow_rental != null) {
                if (sd.allow_rental.equalsIgnoreCase(Constant.YES)) {
                    ivPlay.setVisibility(View.VISIBLE);
                } else {
                    if (feedContentData.can_i_use) {
                        ivPlay.setVisibility(View.VISIBLE);
                    } else {
                        ivPlay.setVisibility(View.INVISIBLE);
                    }
                }
            }
            Glide.with(context)
                    .load(feedContentData.imgUrl)
                    .thumbnail(0.1f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(200, 200)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(ivImage);
            //ivEpisodeDownload.setVisibility(feedContentData.feedTabType.equals(Constant.TAB_BUY) ? View.GONE : View.VISIBLE);
            /*txtEpisodeTitle.setText(((FeedContentData) itemObject).postTitle);
            txtEpisodeSubtitle.setText("");*/
            if (((FeedContentData) itemObject).isSelected) {
                txtEpisodeTitle.setTextColor(context.getResources().getColor(R.color.colorAccent));
            }

        }
    }


}
