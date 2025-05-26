package com.app.itaptv.holder;

import android.content.Context;
import android.graphics.Paint;
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
import com.app.itaptv.structure.FeedContentData;
import com.app.itaptv.structure.FeedData;
import com.app.itaptv.structure.PriceData;
import com.app.itaptv.structure.SubscriptionDetails;
import com.app.itaptv.utils.Constant;
import com.app.itaptv.utils.LocalStorage;
import com.app.itaptv.utils.Utility;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.internal.FlowLayout;
import com.kalpesh.krecycleradapter.KRecyclerViewHolder;

import static com.app.itaptv.utils.Constant.TAB_BUY;

/**
 * Created by poonam on 29/8/18.
 */

public class FeedHolder extends KRecyclerViewHolder {
    View view;
    LinearLayout llBuyInfo;
    ImageView ivFeedImage;
    TextView tvFeedTitle;
    TextView tvFeedAuthor;
    TextView tvSellingPrice;
    TextView tvMrpPrice;
    TextView tvCoins;
    TextView tvUseOrWin;
    TextView tvPercentOff;
    RelativeLayout rlrowfeed;
    CardView cvFeedImage;
    FlowLayout flCoins;
    String title = "";
    FeedData receivedFeedData;

    public FeedHolder(View itemView, @Nullable FeedData feedData) {
        super(itemView);
        view = itemView;
        llBuyInfo = view.findViewById(R.id.llBuyInfo);
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
        flCoins = view.findViewById(R.id.flCoins);
        receivedFeedData = feedData;
    }

    public View getView() {
        return view;
    }

    @Override
    protected void setData(Context context, Object itemObject) {
        super.setData(context, itemObject);
        if (itemObject instanceof FeedContentData) {
            FeedContentData feedContentData = (FeedContentData) itemObject;
            if (receivedFeedData != null) {
                switch (receivedFeedData.tileShape) {
                    case "circle":
                        RequestOptions requestOptions = new RequestOptions()
                                .centerCrop()
                                .circleCrop()
                                .diskCacheStrategy(DiskCacheStrategy.ALL);

                        Glide.with(context)
                                .load(feedContentData.imgUrl)
                                .apply(requestOptions)
                                .into(ivFeedImage);

                        setIvLayoutParams(context, 100, 100);

                        tvFeedTitle.setGravity(Gravity.CENTER);
                        tvFeedTitle.setPadding(2, 0, 0, 0);
                        cvFeedImage.setBackgroundColor(ContextCompat.getColor(context, R.color.full_transparent));
                        break;

                    case "square":
                        Glide.with(context)
                                .load(feedContentData.imgUrl)
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
                                .load(feedContentData.imgUrl)
                                .thumbnail(0.1f)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .into(ivFeedImage);

                        setIvLayoutParams(context, 100, 150);

                        tvFeedTitle.setGravity(Gravity.START);
                        tvFeedTitle.setPadding(2, 0, 15, 0);
                        break;

                    case "h_rectangle":
                        Glide.with(context)
                                .load(feedContentData.imgUrl)
                                .thumbnail(0.1f)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .into(ivFeedImage);

                        setIvLayoutParams(context, 185, 120);

                        tvFeedTitle.setGravity(Gravity.START);
                        tvFeedTitle.setPadding(2, 0, 15, 0);
                        break;
                }
            } else {
                Glide.with(context)
                        .load(feedContentData.imgUrl)
                        .thumbnail(0.1f)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(ivFeedImage);

                setIvLayoutParams(context, 100, 100);

                tvFeedTitle.setGravity(Gravity.START);
                tvFeedTitle.setPadding(2, 0, 15, 0);
            }


            if (tvFeedAuthor != null) {
                if (!feedContentData.displayName.equalsIgnoreCase("")) {
                    tvFeedAuthor.setText(String.format(context.getString(R.string.text_by), feedContentData.displayName));
                } else {
                    tvFeedAuthor.setVisibility(View.INVISIBLE);
                }
            }
            checkData(feedContentData);

            SubscriptionDetails sd = LocalStorage.getUserSubscriptionDetails();
            if (sd != null && sd.allow_rental != null) {
                if (sd.allow_rental.equalsIgnoreCase(Constant.YES)) {
                    llBuyInfo.setVisibility(View.GONE);
                } else if (sd.allow_rental.equalsIgnoreCase(Constant.NO)) {
                    //llBuyInfo.setVisibility(View.VISIBLE);
                    llBuyInfo.setVisibility(feedContentData.feedTabType.equals(Constant.TAB_BUY) ? View.VISIBLE : View.GONE);
                }
            }
            setPaymentData(context, feedContentData);
        }
    }

    private void setIvLayoutParams(Context context, int dpWidth, int dpHeight) {
        final float scale = context.getResources().getDisplayMetrics().density;
        int dpWidthInPx = (int) (dpWidth * scale);
        int dpHeightInPx = (int) (dpHeight * scale);
        if (receivedFeedData.feedType.equalsIgnoreCase(TAB_BUY)) {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(dpWidthInPx, dpHeightInPx);
            ivFeedImage.setLayoutParams(layoutParams);
        } else {
            FrameLayout.LayoutParams layoutParams1 = new FrameLayout.LayoutParams(dpWidthInPx, dpHeightInPx);
            ivFeedImage.setLayoutParams(layoutParams1);
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
        //if (fData.showText.equals("yes")) {
        tvFeedTitle.setText(title);
        /*} else if (fData.showText.equals("no")) {
            tvFeedTitle.setVisibility(View.GONE);
        } else if (fData == null) {
            tvFeedTitle.setVisibility(View.GONE);
        }*/

    }

    private void setPaymentData(Context context, FeedContentData feedContentData) {
        if (feedContentData.feedTabType.equals(Constant.TAB_BUY)) {
            if (feedContentData.arrayListPriceData.size() > 0) {
                PriceData priceData = feedContentData.arrayListPriceData.get(0);

                String sellingPrice = priceData.rupees.equals("") ? "0" : priceData.rupees;
                String mrpPrice = priceData.finalCost.equals("") ? "0" : priceData.finalCost;
                String percentOff = priceData.percentDiscount.equals("") ? "0" : priceData.percentDiscount;

                if (percentOff.equalsIgnoreCase("0")) {
                    tvMrpPrice.setVisibility(View.GONE);
                    tvPercentOff.setVisibility(View.GONE);
                } else {
                    tvMrpPrice.setVisibility(View.VISIBLE);
                    tvPercentOff.setVisibility(View.VISIBLE);
                }
                sellingPrice = String.format(context.getString(R.string.rs_offer_price), sellingPrice);
                mrpPrice = String.format(context.getString(R.string.rs_mrp), mrpPrice);
                percentOff = String.format(context.getString(R.string.percent_off), percentOff);

                tvSellingPrice.setText(sellingPrice);
                tvMrpPrice.setText(mrpPrice);
                tvMrpPrice.setPaintFlags(tvMrpPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                tvCoins.setText(priceData.coins.equals("") ? "0" : priceData.coins);
                tvPercentOff.setText(percentOff);

                if (feedContentData.arrayListPriceData.get(0).costType.equalsIgnoreCase(PriceData.COST_TYPE_COINS_AND_RUPEES)) {
                    tvSellingPrice.setVisibility(View.VISIBLE);
                    tvMrpPrice.setVisibility(View.VISIBLE);
                    tvCoins.setVisibility(View.VISIBLE);
                    tvPercentOff.setVisibility(View.VISIBLE);
                    tvUseOrWin.setText(context.getResources().getString(R.string.use));
                    if (flCoins != null) flCoins.setVisibility(View.VISIBLE);
                } else if (feedContentData.arrayListPriceData.get(0).costType.equalsIgnoreCase(PriceData.COST_TYPE_RUPEES)) {
                    tvSellingPrice.setVisibility(View.VISIBLE);
                    tvMrpPrice.setVisibility(View.GONE);
                    tvCoins.setVisibility(View.VISIBLE);
                    tvUseOrWin.setText(context.getResources().getString(R.string.win));
                    tvCoins.setText(priceData.getCoins.equals("") ? "0" : Utility.numberFormat(Long.parseLong(priceData.getCoins)));
                    tvPercentOff.setVisibility(View.GONE);
                } else if (feedContentData.arrayListPriceData.get(0).costType.equalsIgnoreCase(PriceData.COST_TYPE_COINS)) {
                    tvSellingPrice.setVisibility(View.GONE);
                    tvMrpPrice.setVisibility(View.VISIBLE);
                    tvCoins.setVisibility(View.VISIBLE);
                    tvPercentOff.setVisibility(View.VISIBLE);
                    if (flCoins != null) flCoins.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}
