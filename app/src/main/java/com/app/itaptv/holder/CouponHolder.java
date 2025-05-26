package com.app.itaptv.holder;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.itaptv.R;
import com.app.itaptv.structure.CouponData;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kalpesh.krecycleradapter.KRecyclerViewHolder;

/**
 * Created by poonam on 26/9/18.
 */
public class CouponHolder extends KRecyclerViewHolder {
    View view;
    ImageView ivCoupanImage;
    TextView tvCoupanTitle;
    TextView tvCoupanDetail;

    public CouponHolder(View itemView) {
        super(itemView);
        view = itemView;
        ivCoupanImage = view.findViewById(R.id.ivCouponImage);
        tvCoupanTitle = view.findViewById(R.id.tvCoupanName);
        tvCoupanDetail = view.findViewById(R.id.tvCoupanValue);

    }

    @Override
    protected void setData(Context context, Object itemObject) {
        super.setData(context, itemObject);
        if (itemObject instanceof CouponData) {
            CouponData couponData = (CouponData) itemObject;

            Glide.with(context)
                    .load(couponData.imgUrl)
                    .thumbnail(0.1f)
                    /*.apply(new RequestOptions().centerCrop().dontAnimate())*/
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(200,200)
                    .into(ivCoupanImage);
            tvCoupanTitle.setText(couponData.postTitle);
            String coins = String.format(context.getString(R.string.text_for_coupon), couponData.rewardText, couponData.rewardCoins);
            tvCoupanDetail.setText(Html.fromHtml(coins));
        }
    }
}
