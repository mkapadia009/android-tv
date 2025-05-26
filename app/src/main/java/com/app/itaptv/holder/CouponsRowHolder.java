package com.app.itaptv.holder;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.app.itaptv.R;
import com.app.itaptv.structure.vouchers.AllCouponsFeedsDataContents;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.kalpesh.krecycleradapter.KRecyclerViewHolder;

/**
 * Created by poonam on 28/8/18.
 */

public class CouponsRowHolder extends KRecyclerViewHolder {

    View views;
    ImageView imageView;
    TextView tvName;
    CardView cvFeedImage;
    RelativeLayout rlCoupons;
    static DisplayMetrics displaymetrics;

    public CouponsRowHolder(View itemView) {
        super(itemView);
        views = itemView;
        imageView = views.findViewById(R.id.ivImage);
        tvName = views.findViewById(R.id.tvStoreName);
        cvFeedImage = views.findViewById(R.id.cvFeedImage);
        rlCoupons = views.findViewById(R.id.rlCoupons);
    }

    @Override
    protected void setData(Context context, Object itemObject) {
        super.setData(context, itemObject);
        if (itemObject instanceof AllCouponsFeedsDataContents) {
            AllCouponsFeedsDataContents rowCouponsData = (AllCouponsFeedsDataContents) itemObject;

            if (rowCouponsData.type.equalsIgnoreCase(AllCouponsFeedsDataContents.COUPON_TYPE_STORE)) {

            } else {
                //imageView.setPadding(50, 50, 50, 50);
                imageView.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }
            if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                displaymetrics = new DisplayMetrics();
                ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            }
            if (displaymetrics != null) {
                int deviceWidth;
                deviceWidth = (int) (displaymetrics.widthPixels / 3.4);
                rlCoupons.getLayoutParams().width = deviceWidth;
                cvFeedImage.getLayoutParams().width = deviceWidth;
                cvFeedImage.getLayoutParams().height = deviceWidth;
                imageView.getLayoutParams().width = deviceWidth;
                imageView.getLayoutParams().height = deviceWidth;
            }
            Glide.with(context)
                    .load(rowCouponsData.image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(200, 200)
                    .thumbnail(0.1f)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(imageView);
            /*Glide.with(context)
                    .load(rowCouponsData.image)
                    .into(imageView);*/
            tvName.setText(rowCouponsData.name);
        }
    }
}
