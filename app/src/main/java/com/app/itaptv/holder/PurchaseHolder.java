package com.app.itaptv.holder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.itaptv.R;
import com.app.itaptv.structure.PriceData;
import com.app.itaptv.structure.PurchaseData;
import com.app.itaptv.utils.Utility;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.kalpesh.krecycleradapter.KRecyclerViewHolder;

/**
 * Created by poonam on 13/12/18.
 */

public class PurchaseHolder extends KRecyclerViewHolder {
    ImageView ivImage;
    TextView tvTitle;
    TextView tvExpiry;
    TextView tvPaidAmount;
    TextView tvPaidLabel;
    TextView tvCoins;
    TextView tvPlus;

    public PurchaseHolder(View itemView) {
        super(itemView);
        Utility.textFocusListener(itemView);
        ivImage = itemView.findViewById(R.id.ivImage);
        tvTitle = itemView.findViewById(R.id.tvStoreName);
        tvExpiry = itemView.findViewById(R.id.tvExpiry);
        tvPaidLabel = itemView.findViewById(R.id.tvLabelPaid);
        tvPaidAmount = itemView.findViewById(R.id.tvPaidAmount);
        tvCoins = itemView.findViewById(R.id.tvCoins);
        tvPlus = itemView.findViewById(R.id.tvPlus);
    }

    @Override
    protected void setData(Context context, Object itemObject) {
        super.setData(context, itemObject);
        if (itemObject instanceof PurchaseData) {
            PurchaseData purchaseData = (PurchaseData) itemObject;
            String expiresIn = String.format(context.getString(R.string.expires_in), purchaseData.expiry);
            String paidAmount = String.format(context.getString(R.string.rs_offer_price), purchaseData.priceData.rupees);
            String coins = purchaseData.priceData.coins;
            tvTitle.setText(purchaseData.postTitle);
            //tvExpiry.setText(expiresIn);
            tvPaidAmount.setText(paidAmount);
            tvCoins.setText(coins);

            String costType = purchaseData.priceData.costType;
            boolean isVoucher = purchaseData.priceData.voucherRedeem;
            if (isVoucher) {
                tvPaidAmount.setVisibility(View.INVISIBLE);
                tvPlus.setVisibility(View.INVISIBLE);
                tvCoins.setVisibility(View.INVISIBLE);
                tvPaidLabel.setText(context.getResources().getString(R.string.label_voucher_redeemed));
            } else {
                if (costType.equalsIgnoreCase(PriceData.COST_TYPE_RUPEES)) {
                    tvPaidAmount.setVisibility(View.VISIBLE);
                    tvPlus.setVisibility(View.GONE);
                    tvCoins.setVisibility(View.GONE);
                } else if (costType.equalsIgnoreCase(PriceData.COST_TYPE_COINS)) {
                    tvPaidAmount.setVisibility(View.GONE);
                    tvPlus.setVisibility(View.GONE);
                    tvCoins.setVisibility(View.VISIBLE);
                } else if (costType.equalsIgnoreCase(PriceData.COST_TYPE_COINS_AND_RUPEES)) {
                    tvPaidAmount.setVisibility(View.VISIBLE);
                    tvPlus.setVisibility(View.VISIBLE);
                    tvCoins.setVisibility(View.VISIBLE);
                }
            }

            Glide.with(context)
                    .load(purchaseData.imgUrl)
                    .thumbnail(0.1f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(200,200)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(ivImage);
        }
    }
}
