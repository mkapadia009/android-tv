package com.app.itaptv.holder;

import android.content.Context;
import android.graphics.Paint;
import android.util.DisplayMetrics;
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
import com.app.itaptv.structure.FeedCombinedData;
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

public class FeedCombinedHolder extends KRecyclerViewHolder {

    View view;
    LinearLayout llBuyInfo;
    //View layoutBuyInfo;
    LinearLayout layoutBuyInfo;
    ImageView ivFeedImage;
    CardView cvFeedImage;
    TextView tvFeedTitle;
    TextView tvFeedAuthor;
    TextView tvSellingPrice;
    TextView tvMrpPrice;
    TextView tvDivider;
    TextView tvSellingCoins;
    TextView tvFree;
    TextView tvCoins;
    TextView tvUseOrWin;
    TextView tvPercentOff;
    View buyInfoLayout;
    RelativeLayout rlrowfeed;
    FlowLayout flCoins;
    TextView tvReward;
    //LinearLayout llRewards;
    String title = "";
    static DisplayMetrics displaymetrics;
    FeedCombinedData fData;


    public FeedCombinedHolder(View itemView, @Nullable FeedCombinedData feedData) {
        super(itemView);
        view = itemView;
        fData = feedData;
        llBuyInfo = view.findViewById(R.id.llBuyInfo);
        layoutBuyInfo = view.findViewById(R.id.ll_buy_details);
        ivFeedImage = view.findViewById(R.id.ivFeedImage);
        tvFeedTitle = view.findViewById(R.id.tvFeedTitle);
        tvFeedAuthor = view.findViewById(R.id.tvFeedAuthorName);
        tvSellingPrice = view.findViewById(R.id.tvSellingPrice);
        tvMrpPrice = view.findViewById(R.id.tvMrpPrice);
        tvDivider = view.findViewById(R.id.tvDivider);
        tvSellingCoins = view.findViewById(R.id.tvSellingCoins);
        tvFree = view.findViewById(R.id.tvFree);
        tvReward = view.findViewById(R.id.tvReward);
        tvCoins = view.findViewById(R.id.tvCoins);
        tvUseOrWin = view.findViewById(R.id.tvUseOrWin);
        tvPercentOff = view.findViewById(R.id.tvPercentOff);
        rlrowfeed = view.findViewById(R.id.rlrowfeed);
        cvFeedImage = view.findViewById(R.id.cvFeedImage);
        flCoins = view.findViewById(R.id.flCoins);
        //llRewards = view.findViewById(R.id.llRewards);
        //buyInfoLayout = view.findViewById(R.id.buy_info_layout);
    }

    public View getView() {
        return view;
    }

    @Override
    protected void setData(Context context, Object itemObject) {
        super.setData(context, itemObject);
        if (itemObject instanceof FeedContentData) {
            FeedContentData feedContentData = (FeedContentData) itemObject;
            switch (fData.tileShape) {
                case "circle":
                    RequestOptions requestOptions = new RequestOptions()
                            .centerCrop()
                            .circleCrop()
                            .diskCacheStrategy(DiskCacheStrategy.ALL);

                    /*if (feedContentData.postTitle.equalsIgnoreCase(Constant.eSportsTitle)) {
                        ivFeedImage.setImageResource(R.drawable.esportsimage);
                        Glide.with(context)
                                .load(context.getResources().getIdentifier("esportsimage", "drawable", context.getPackageName()))
                                .apply(requestOptions)
                                .into(ivFeedImage);
                    } else {*/
                    Glide.with(context)
                            .load(feedContentData.imgUrl)
                            .apply(requestOptions)
                            .into(ivFeedImage);
                    //}

                    setIvLayoutParams(context, 100, 100);

                    tvFeedTitle.setGravity(Gravity.CENTER);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    params.gravity = Gravity.CENTER_HORIZONTAL;
                    layoutBuyInfo.setLayoutParams(params);
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
                    //setIvLayoutParams(context, 100, 50);

                    tvFeedTitle.setGravity(Gravity.START);
                    tvFeedTitle.setPadding(2, 0, 15, 0);
                    break;
            }

            if (tvFeedAuthor != null) {
                if (!feedContentData.displayName.equalsIgnoreCase("")) {
                    tvFeedAuthor.setText(String.format(context.getString(R.string.text_by), feedContentData.displayName));
                } else {
                    tvFeedAuthor.setVisibility(View.GONE);
                }
            }

            SubscriptionDetails sd = LocalStorage.getUserSubscriptionDetails();
            if (sd != null && sd.allow_rental != null) {
                if (sd.allow_rental.equalsIgnoreCase(Constant.YES)) {
                    if (feedContentData.singleCredit) {
                        layoutBuyInfo.setVisibility(View.VISIBLE);
                    } else {
                        layoutBuyInfo.setVisibility(View.GONE);
                    }
                } else if (sd.allow_rental.equalsIgnoreCase(Constant.NO)) {
                    //llBuyInfo.setVisibility(View.VISIBLE);
                    layoutBuyInfo.setVisibility(View.GONE);
                }
            }
            //llBuyInfo.setVisibility(feedContentData.feedTabType.equals(TAB_BUY) ? View.VISIBLE : View.GONE);
            checkData(feedContentData);
            setPaymentData(context, feedContentData);
        }
    }

    private void setIvLayoutParams(Context context, int dpWidth, int dpHeight) {
        final float scale = context.getResources().getDisplayMetrics().density;
        int dpWidthInPx = (int) (dpWidth * scale);
        int dpHeightInPx = (int) (dpHeight * scale);
        if (fData.feedType.equalsIgnoreCase(TAB_BUY)) {
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
        if (fData.showText.equals("yes")) {
            tvFeedTitle.setVisibility(View.VISIBLE);
            tvFeedTitle.setText(title);
        } else if (fData.showText.equals("no")) {
            tvFeedTitle.setVisibility(View.GONE);
        } else if (fData == null) {
            tvFeedTitle.setVisibility(View.GONE);
        }
    }

    private void setPaymentData(Context context, FeedContentData feedContentData) {
        // if (feedContentData.feedTabType.equals(TAB_BUY)) {
        if (feedContentData.postExcerpt.equalsIgnoreCase("free")) {
            tvMrpPrice.setVisibility(View.GONE);
            tvPercentOff.setVisibility(View.GONE);
            tvSellingPrice.setVisibility(View.GONE);
            tvUseOrWin.setVisibility(View.GONE);
            tvCoins.setVisibility(View.GONE);
            tvDivider.setVisibility(View.GONE);
            tvSellingCoins.setVisibility(View.GONE);
            if (feedContentData.contentType.equalsIgnoreCase(Constant.eSportsTitle)) {
                tvFree.setVisibility(View.GONE);
            } else {
                //tvFree.setVisibility(View.VISIBLE);
                tvFree.setVisibility(View.GONE);
            }
        } else if (feedContentData.postExcerpt.equalsIgnoreCase("paid")) {
            if (feedContentData.arrayListPriceData.size() > 0) {
                if (feedContentData.arrayListPriceData.get(0).costType.equalsIgnoreCase("rupees")) {
                    tvFree.setVisibility(View.GONE);
                    PriceData priceData = feedContentData.arrayListPriceData.get(0);
                    PriceData coinsData;
                    String coins = "";

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
                        //tvSellingPrice.setVisibility(View.VISIBLE);
                        tvSellingPrice.setVisibility(View.GONE);
                        tvMrpPrice.setVisibility(View.VISIBLE);
                        tvCoins.setVisibility(View.VISIBLE);
                        tvPercentOff.setVisibility(View.VISIBLE);
                        tvUseOrWin.setText(context.getResources().getString(R.string.use));
                        if (flCoins != null) flCoins.setVisibility(View.VISIBLE);
                    } else if (feedContentData.arrayListPriceData.get(0).costType.equalsIgnoreCase(PriceData.COST_TYPE_RUPEES)) {
                        //tvSellingPrice.setVisibility(View.VISIBLE);
                        tvSellingPrice.setVisibility(View.GONE);
                        tvMrpPrice.setVisibility(View.GONE);
                        tvCoins.setVisibility(View.VISIBLE);
                        tvPercentOff.setVisibility(View.GONE);
                        if (priceData.getCoins == null || priceData.getCoins.isEmpty() || priceData.getCoins.equals("0")) {
                            tvUseOrWin.setVisibility(View.GONE);
                            tvCoins.setVisibility(View.GONE);
                            flCoins.setVisibility(View.GONE);
                        } else {
                            tvUseOrWin.setText(context.getResources().getString(R.string.win));
                            tvCoins.setText(priceData.getCoins.equals("") ? "0" : Utility.numberFormat(Long.parseLong(priceData.getCoins)));
                        }
                    } else if (feedContentData.arrayListPriceData.get(0).costType.equalsIgnoreCase(PriceData.COST_TYPE_COINS)) {
                        tvSellingPrice.setVisibility(View.GONE);
                        tvMrpPrice.setVisibility(View.VISIBLE);
                        tvCoins.setVisibility(View.VISIBLE);
                        tvPercentOff.setVisibility(View.VISIBLE);
                        if (flCoins != null) flCoins.setVisibility(View.VISIBLE);
                    }


                    if (feedContentData.arrayListPriceData.size() > 1) {
                        coinsData = feedContentData.arrayListPriceData.get(1);
                        coins = coinsData.coins.equals("") ? "0" : coinsData.finalCost;
                        tvSellingCoins.setText(coins);


                        if (coins.equalsIgnoreCase("0")) {
                            tvDivider.setVisibility(View.GONE);
                            tvSellingCoins.setVisibility(View.GONE);
                        } else {
                            //tvDivider.setVisibility(View.VISIBLE);
                            tvDivider.setVisibility(View.GONE);
                            tvSellingCoins.setVisibility(View.VISIBLE);
                        }
                    } else {
                        tvDivider.setVisibility(View.GONE);
                        tvSellingCoins.setVisibility(View.GONE);
                    }
                /*if (feedContentData.arrayListPriceData.get(0).costType.equalsIgnoreCase(PriceData.COST_TYPE_COINS_AND_RUPEES)) {
                    tvSellingPrice.setVisibility(View.VISIBLE);
                    tvMrpPrice.setVisibility(View.VISIBLE);
                    tvCoins.setVisibility(View.VISIBLE);
                    tvPercentOff.setVisibility(View.VISIBLE);
                    flCoins.setVisibility(View.VISIBLE);
                } else if (feedContentData.arrayListPriceData.get(0).costType.equalsIgnoreCase(PriceData.COST_TYPE_RUPEES)) {
                    tvSellingPrice.setVisibility(View.VISIBLE);
                    tvMrpPrice.setVisibility(View.VISIBLE);
                    tvCoins.setVisibility(View.GONE);
                    tvPercentOff.setVisibility(View.GONE);
                    flCoins.setVisibility(View.GONE);
                } else if (feedContentData.arrayListPriceData.get(0).costType.equalsIgnoreCase(PriceData.COST_TYPE_COINS)) {
                    tvSellingPrice.setVisibility(View.GONE);
                    tvMrpPrice.setVisibility(View.VISIBLE);
                    tvCoins.setVisibility(View.VISIBLE);
                    tvPercentOff.setVisibility(View.VISIBLE);
                    flCoins.setVisibility(View.VISIBLE);
                }*/
                } else if (feedContentData.arrayListPriceData.size() > 0) {
                    if (feedContentData.arrayListPriceData.get(0).costType.equalsIgnoreCase("coins")) {
                        PriceData coinsData;
                        String coins = "";

                        if (feedContentData.arrayListPriceData.size() > 1) {
                            PriceData priceData = feedContentData.arrayListPriceData.get(1);

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

                            coinsData = feedContentData.arrayListPriceData.get(0);
                            coins = coinsData.coins.equals("") ? "0" : coinsData.finalCost;
                            //tvDivider.setVisibility(View.VISIBLE);
                            tvDivider.setVisibility(View.GONE);
                            tvSellingCoins.setVisibility(View.VISIBLE);
                            tvSellingCoins.setText(coins);

                            if (feedContentData.arrayListPriceData.get(1).costType.equalsIgnoreCase(PriceData.COST_TYPE_COINS_AND_RUPEES)) {
                                //tvSellingPrice.setVisibility(View.VISIBLE);
                                tvSellingPrice.setVisibility(View.GONE);
                                tvMrpPrice.setVisibility(View.VISIBLE);
                                tvCoins.setVisibility(View.VISIBLE);
                                tvPercentOff.setVisibility(View.VISIBLE);
                                tvUseOrWin.setText(context.getResources().getString(R.string.use));
                                if (flCoins != null) flCoins.setVisibility(View.VISIBLE);
                            } else if (feedContentData.arrayListPriceData.get(1).costType.equalsIgnoreCase(PriceData.COST_TYPE_RUPEES)) {
                                //tvSellingPrice.setVisibility(View.VISIBLE);
                                tvSellingPrice.setVisibility(View.GONE);
                                tvMrpPrice.setVisibility(View.GONE);
                                tvCoins.setVisibility(View.VISIBLE);
                                tvPercentOff.setVisibility(View.GONE);
                                if (priceData.getCoins == null || priceData.getCoins.isEmpty() || priceData.getCoins.equals("0")) {
                                    tvUseOrWin.setVisibility(View.GONE);
                                    tvCoins.setVisibility(View.GONE);
                                    flCoins.setVisibility(View.GONE);
                                } else {
                                    tvUseOrWin.setText(context.getResources().getString(R.string.win));
                                    tvCoins.setText(priceData.getCoins.equals("") ? "0" : Utility.numberFormat(Long.parseLong(priceData.getCoins)));
                                }
                            } else if (feedContentData.arrayListPriceData.get(1).costType.equalsIgnoreCase(PriceData.COST_TYPE_COINS)) {
                                tvSellingPrice.setVisibility(View.GONE);
                                tvMrpPrice.setVisibility(View.VISIBLE);
                                tvCoins.setVisibility(View.VISIBLE);
                                tvPercentOff.setVisibility(View.VISIBLE);
                                if (flCoins != null) flCoins.setVisibility(View.VISIBLE);
                            }
                        }

                        if (feedContentData.arrayListPriceData.size() > 0) {
                            tvDivider.setVisibility(View.GONE);
                            tvUseOrWin.setVisibility(View.GONE);
                            tvMrpPrice.setVisibility(View.GONE);
                            tvPercentOff.setVisibility(View.GONE);
                            tvSellingPrice.setVisibility(View.GONE);
                            tvCoins.setVisibility(View.GONE);
                            coinsData = feedContentData.arrayListPriceData.get(0);
                            coins = coinsData.coins.equals("") ? "0" : coinsData.finalCost;
                            tvSellingCoins.setText(coins);

                            if (coins.equalsIgnoreCase("0")) {
                                tvDivider.setVisibility(View.GONE);
                                tvSellingCoins.setVisibility(View.GONE);
                                //tvFree.setVisibility(View.VISIBLE);
                                tvFree.setVisibility(View.GONE);
                            } else {
                                tvDivider.setVisibility(View.GONE);
                                tvSellingCoins.setVisibility(View.VISIBLE);
                                tvFree.setVisibility(View.GONE);
                            }
                        } else {
                            tvDivider.setVisibility(View.GONE);
                            tvSellingCoins.setVisibility(View.GONE);
                            //tvFree.setVisibility(View.VISIBLE);
                            tvFree.setVisibility(View.GONE);
                        }
                    }
                }
            } else {
                tvMrpPrice.setVisibility(View.GONE);
                tvPercentOff.setVisibility(View.GONE);
                tvSellingPrice.setVisibility(View.GONE);
                tvUseOrWin.setVisibility(View.GONE);
                tvCoins.setVisibility(View.GONE);
                tvDivider.setVisibility(View.GONE);
                tvSellingCoins.setVisibility(View.GONE);
                //tvFree.setVisibility(View.VISIBLE);
                tvFree.setVisibility(View.GONE);
            }
        } /*else if (){
            tvMrpPrice.setVisibility(View.GONE);
            tvPercentOff.setVisibility(View.GONE);
            tvSellingPrice.setVisibility(View.GONE);
            tvUseOrWin.setVisibility(View.GONE);
            tvCoins.setVisibility(View.GONE);
            tvDivider.setVisibility(View.GONE);
            tvSellingCoins.setVisibility(View.GONE);
            tvFree.setVisibility(View.GONE);
            llRewards.setVisibility(View.GONE);
        }*/
        /*} else {
            tvMrpPrice.setVisibility(View.GONE);
            tvPercentOff.setVisibility(View.GONE);
            tvSellingPrice.setVisibility(View.GONE);
            tvUseOrWin.setVisibility(View.GONE);
            tvCoins.setVisibility(View.GONE);
            tvDivider.setVisibility(View.GONE);
            tvSellingCoins.setVisibility(View.GONE);
            tvFree.setVisibility(View.GONE);
            if (feedContentData.arrayListPriceData.size() > 0) {
                if (feedContentData.arrayListPriceData.get(0).costType.equalsIgnoreCase("coins")) {
                    PriceData coinsData;
                    String coins = "";
                    if (feedContentData.arrayListPriceData.size() > 0) {
                        coinsData = feedContentData.arrayListPriceData.get(0);
                        coins = coinsData.coins.equals("") ? "0" : coinsData.finalCost;
                        tvSellingCoins.setText(coins);
                        if (coins.equalsIgnoreCase("0")) {
                            tvDivider.setVisibility(View.GONE);
                            tvSellingCoins.setVisibility(View.GONE);
                            tvFree.setVisibility(View.VISIBLE);
                        } else {
                            tvDivider.setVisibility(View.GONE);
                            tvSellingCoins.setVisibility(View.VISIBLE);
                        }
                    } else {
                        tvDivider.setVisibility(View.GONE);
                        tvSellingCoins.setVisibility(View.GONE);
                        tvFree.setVisibility(View.VISIBLE);
                    }
                } else {
                    tvDivider.setVisibility(View.GONE);
                    tvSellingCoins.setVisibility(View.GONE);
                    if (!feedContentData.arrayListPriceData.get(0).costType.equalsIgnoreCase("rupees")) {
                        tvFree.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                tvMrpPrice.setVisibility(View.GONE);
                tvPercentOff.setVisibility(View.GONE);
                tvSellingPrice.setVisibility(View.GONE);
                tvUseOrWin.setVisibility(View.GONE);
                tvCoins.setVisibility(View.GONE);
                tvDivider.setVisibility(View.GONE);
                tvSellingCoins.setVisibility(View.GONE);
                tvFree.setVisibility(View.VISIBLE);
            }
        }*/

        if (feedContentData.singleCredit) {
            tvMrpPrice.setVisibility(View.GONE);
            tvPercentOff.setVisibility(View.GONE);
            tvSellingPrice.setVisibility(View.GONE);
            tvUseOrWin.setVisibility(View.GONE);
            tvCoins.setVisibility(View.GONE);
            tvDivider.setVisibility(View.GONE);
            tvSellingCoins.setVisibility(View.GONE);
            tvFree.setVisibility(View.GONE);
            tvReward.setVisibility(View.VISIBLE);
            //llRewards.setVisibility(View.VISIBLE);
            tvReward.setText(feedContentData.reward);
        } else {
            tvReward.setVisibility(View.INVISIBLE);
        }
    }
}
