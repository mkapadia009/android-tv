package com.app.itaptv.holder;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.itaptv.R;
import com.app.itaptv.activity.vouchers.CouponsListingActivity;
import com.app.itaptv.activity.vouchers.CouponsViewAllActivity;
import com.app.itaptv.structure.vouchers.AllCouponsFeedsData;
import com.app.itaptv.structure.vouchers.AllCouponsFeedsDataContents;
import com.app.itaptv.utils.Log;
import com.kalpesh.krecycleradapter.KRecyclerViewAdapter;
import com.kalpesh.krecycleradapter.KRecyclerViewHolder;

/**
 * Created by poonam on 28/8/18.
 */

public class CouponsHolder extends KRecyclerViewHolder {

    TextView tvTitle, tvViewAll;
    View views;
    RelativeLayout rlParentTitle;
    ImageView ivFeedImage;
    CardView cvFeedImage;

    public CouponsHolder(View itemView) {
        super(itemView);
        views = itemView;
        tvTitle = views.findViewById(R.id.tvStoreName);
        tvViewAll = views.findViewById(R.id.tvViewAll);
        rlParentTitle = views.findViewById(R.id.rlParentTitle);
    }

    @Override
    protected void setData(Context context, Object itemObject) {
        super.setData(context, itemObject);
        if (itemObject instanceof AllCouponsFeedsData) {
            AllCouponsFeedsData couponsData = (AllCouponsFeedsData) itemObject;
            setCouponDataRecyclerView(context, couponsData);
            tvTitle.setText(couponsData.name);
            rlParentTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, CouponsViewAllActivity.class);
                    intent.putExtra(CouponsViewAllActivity.ID_KEY, couponsData.id);
                    intent.putExtra(CouponsViewAllActivity.NAME, couponsData.name);
                    intent.putExtra(CouponsListingActivity.COUPON_PARENT_OBJECT_KEY, couponsData);
                    context.startActivity(intent);
                }
            });
        }
    }

    private void setCouponDataRecyclerView(Context context, AllCouponsFeedsData data) {
        RecyclerView rvCoupon = views.findViewById(R.id.rvRowCoupons);
        rvCoupon.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        KRecyclerViewAdapter adapter = new KRecyclerViewAdapter(context, data.contentsArrayList, (viewGroup, i) -> {
            View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_coupons_data, viewGroup, false);
            return new CouponsRowHolder(layoutView);
        }, (kRecyclerViewHolder, o, i) -> {
            // item clicked
            AllCouponsFeedsDataContents couponsFeedsDataContents = (AllCouponsFeedsDataContents) o;
            Intent intent = new Intent(context, CouponsListingActivity.class);
            intent.putExtra(CouponsListingActivity.COUPON_OBJECT_KEY, couponsFeedsDataContents);
            intent.putExtra(CouponsListingActivity.COUPON_PARENT_OBJECT_KEY, data);
            context.startActivity(intent);
            Log.d("Initial Listing :", String.valueOf(couponsFeedsDataContents));
        });

        rvCoupon.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

}
