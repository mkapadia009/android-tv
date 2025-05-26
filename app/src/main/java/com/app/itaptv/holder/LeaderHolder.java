package com.app.itaptv.holder;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.graphics.drawable.DrawableCompat;

import com.app.itaptv.R;
import com.app.itaptv.structure.LeaderBoardData;
import com.app.itaptv.utils.Utility;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kalpesh.krecycleradapter.KRecyclerViewHolder;

public class LeaderHolder extends KRecyclerViewHolder {
    TextView tvLeaderName;
    TextView tvLeaderCoins;
    TextView tvLeaderNo;
    ImageView ivCrown;
    ImageView ivLeaderUser;
    View view;

    public LeaderHolder(View itemView) {
        super(itemView);
        view = itemView;
        tvLeaderName = itemView.findViewById(R.id.tvLeaderName);
        tvLeaderCoins = itemView.findViewById(R.id.tvLeaderCoin);
        tvLeaderNo = itemView.findViewById(R.id.tvLeaderNo);
        ivCrown = itemView.findViewById(R.id.ivCrown);
        ivLeaderUser = itemView.findViewById(R.id.ivLeaderUser);
    }

    @Override
    protected void setData(Context context, Object itemObject) {
        super.setData(context, itemObject);

        if (itemObject instanceof LeaderBoardData) {
            //Set iCoins
            if (((LeaderBoardData) itemObject).getIndex() < 4) {
                tvLeaderCoins.setText(((LeaderBoardData) itemObject).value + context.getString(R.string.icoins));
                tvLeaderCoins.setTextColor(context.getResources().getColor(R.color.colorCoin));
                Drawable icon = context.getResources().getDrawable(R.drawable.ic_coin);
                if (icon != null) {
                    icon = DrawableCompat.wrap(icon);
                    int height = icon.getIntrinsicHeight();
                    int width = icon.getIntrinsicWidth();
                    icon.setBounds(0, 0, width, height);
                    tvLeaderCoins.setCompoundDrawables(icon, null, null, null);
                }
            } else {
                tvLeaderCoins.setText(((LeaderBoardData) itemObject).value + context.getString(R.string.icoins));
                tvLeaderCoins.setTextColor(context.getResources().getColor(R.color.colorGray));
                tvLeaderCoins.setCompoundDrawables(null, null, null, null);
            }

            //Set Rank Number
            String s = Utility.ordinal(((LeaderBoardData) itemObject).getIndex());
            SpannableString ss1 = new SpannableString(s);
            Utility.setFontSizeForPath(ss1, String.valueOf(((LeaderBoardData) itemObject).getIndex()), (int) tvLeaderNo.getTextSize() + 15);
            tvLeaderNo.setText(ss1);

            //Set Top Three Winner Crown
            if (((LeaderBoardData) itemObject).icon == 0) {
                ivCrown.setVisibility(View.GONE);
            } else {
                ivCrown.setImageResource(((LeaderBoardData) itemObject).getIcon());
            }

            //Set Name or Mobile Number
            if (((LeaderBoardData) itemObject).displayName.equalsIgnoreCase("")) {
                tvLeaderName.setText(Utility.getMobileStrikeOut(((LeaderBoardData) itemObject).mobileNo));
            } else {
                // check if name contains mobile no
                String displayName[] = ((LeaderBoardData) itemObject).displayName.split(" ");
                try {
                    Double.valueOf(displayName[0]);
                    if (displayName[0].length()>=10) {
                        tvLeaderName.setText(Utility.getMobileStrikeOut(displayName[0]));
                    }else{
                        tvLeaderName.setText(((LeaderBoardData) itemObject).displayName);
                    }
                } catch (NumberFormatException e) {
                    tvLeaderName.setText(displayName[0]);
                }
            }

            //Set User Image
            Glide.with(context)
                    .load(((LeaderBoardData) itemObject).img)
                    .thumbnail(0.1f)
                    .placeholder(context.getResources().getDrawable(R.drawable.user))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivLeaderUser);

        }
    }


}
