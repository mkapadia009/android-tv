package com.app.itaptv.holder;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.app.itaptv.R;
import com.app.itaptv.structure.FeedContentData;
import com.app.itaptv.structure.FeedData;
import com.app.itaptv.utils.Utility;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.internal.FlowLayout;
import com.kalpesh.krecycleradapter.KRecyclerViewHolder;


public class FeedNewLiveHolder extends KRecyclerViewHolder {
    View view;
    ImageView ivFeedImage;
    ImageView iv_premium_logo;
    TextView tvFeedTitle;
    TextView tvFeedAuthor;
    TextView tvSellingPrice;
    TextView tvMrpPrice;
    TextView tvCoins;
    TextView tvUseOrWin;
    TextView tvPercentOff;
    RelativeLayout rlrowfeed;
    CardView cvFeedImage;
    LinearLayout llBuyInfo;
    FlowLayout flCoins;
    String title = "";

    public FeedNewLiveHolder(View itemView) {
        super(itemView);
        view = itemView;
        ivFeedImage = view.findViewById(R.id.ivFeedImage);
        tvFeedTitle = view.findViewById(R.id.tvFeedTitle);
        tvFeedAuthor = view.findViewById(R.id.tvFeedAuthorName);
        tvSellingPrice = view.findViewById(R.id.tvSellingPrice);
        tvMrpPrice = view.findViewById(R.id.tvMrpPrice);
        tvCoins = view.findViewById(R.id.tvCoins);
        tvUseOrWin = view.findViewById(R.id.tvUseOrWin);
        tvPercentOff = view.findViewById(R.id.tvPercentOff);
        rlrowfeed = view.findViewById(R.id.rlrowfeed);
        cvFeedImage = view.findViewById(R.id.cvFeedImage);
    }

    public View getView() {

        return view;
    }

    @Override
    protected void setData(Context context, Object itemObject) {
        super.setData(context, itemObject);
        if (itemObject instanceof FeedContentData) {
            DisplayMetrics displaymetrics = new DisplayMetrics();
            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            FeedContentData feedContentData = (FeedContentData) itemObject;

            if (Utility.isTelevision()) {
                Utility.textFocusListener(view);
                Glide.with(context)
                        .load(feedContentData.imgUrl)
                        .thumbnail(0.1f)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(ivFeedImage);
            } else {
                Glide.with(context)
                        .load(feedContentData.imgUrl)
                        .thumbnail(0.1f)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(new SimpleTarget<Drawable>() {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();
                                int imageWidth = bitmap.getWidth();
                                int imageHeight = bitmap.getHeight();
                                ivFeedImage.setImageDrawable(resource);

                                int viewWidth = ivFeedImage.getWidth();

                                int scale = viewWidth - imageWidth;
                                int height = imageHeight + scale;
                                ivFeedImage.getLayoutParams().height = height;
                            }
                        });
            }
            //.into(ivFeedImage);
            if (tvFeedAuthor != null) {
                if (!feedContentData.displayName.equalsIgnoreCase("")) {
                    tvFeedAuthor.setText(String.format(context.getString(R.string.text_by), feedContentData.displayName));
                } else {
                    tvFeedAuthor.setVisibility(View.INVISIBLE);
                }
            }

            checkData(feedContentData);
        }
    }

    private void checkData(FeedContentData feedContentData) {
        switch (feedContentData.feedContentType) {
            case FeedContentData.CONTENT_TYPE_CATEGORIES:
            case FeedContentData.CONTENT_TYPE_LIVE:
                title = feedContentData.name;
                break;
            case FeedContentData.CONTENT_TYPE_ALL:
                if (feedContentData.feedItemFrom.equals(FeedData.ITEMS_FROM_AUTO)) {
                    title = feedContentData.postTitle;
                } else if (feedContentData.feedItemFrom.equals(FeedData.ITEMS_FROM_MANUAL)) {
                    checkAllManualData(feedContentData);
                }
                break;
            default:
                title = feedContentData.postTitle;
                break;
        }

        setData();
    }

    private void checkAllManualData(FeedContentData feedContentData) {
        switch (feedContentData.contentType) {
            case FeedContentData.CONTENT_TYPE_SEASON:
                title = feedContentData.name;
                break;
            default:
                title = feedContentData.postTitle;
                break;
        }

        setData();
    }

    private void setData() {
        tvFeedTitle.setText(title);

    }
}
