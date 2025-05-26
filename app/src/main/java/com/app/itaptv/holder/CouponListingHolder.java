package com.app.itaptv.holder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.itaptv.R;
import com.app.itaptv.structure.vouchers.CouponsDetails;
import com.app.itaptv.utils.Utility;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kalpesh.krecycleradapter.KRecyclerViewHolder;

/**
 * Created by poonam on 26/9/18.
 */
public class CouponListingHolder extends KRecyclerViewHolder {

    View view;

    ImageView ivLogo;
    TextView tvStoreName, tvCat, tvDescription, tvCoins, tvExp;

    public CouponListingHolder(View itemView) {
        super(itemView);
        view = itemView;
        ivLogo = view.findViewById(R.id.ivLogo);
        tvStoreName = view.findViewById(R.id.tvStoreName);
        tvCat = view.findViewById(R.id.tvCat);
        tvDescription = view.findViewById(R.id.tvDescription);
        tvCoins = view.findViewById(R.id.tvCoins);
        tvExp = view.findViewById(R.id.tvExpires);
    }

    @Override
    protected void setData(Context context, Object itemObject) {
        super.setData(context, itemObject);
        if (itemObject instanceof CouponsDetails) {
            CouponsDetails couponsDetails = (CouponsDetails) itemObject;
            String url = couponsDetails.imageUrl;
            if (couponsDetails.isFromStore) {
                url = couponsDetails.category.iconUrl;
            }
            Glide.with(context).load(url)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(200, 200).into(ivLogo);
            tvCat.setText(couponsDetails.category.catName);
            tvDescription.setText(couponsDetails.title);
            String expiresDate = Utility.formatDate("yyyy-MM-DD HH:mm:ss", "MMM dd", (couponsDetails.expiresOn));
            if (expiresDate.isEmpty()) {
                tvExp.setVisibility(View.GONE);
            } else {
                tvExp.setVisibility(View.VISIBLE);
            }
            tvExp.setText(context.getString(R.string.expires_on)+ expiresDate);
            tvCoins.setText(couponsDetails.coins + context.getString(R.string.icoins));
        }
    }
}
