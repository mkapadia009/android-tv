package com.app.itaptv.holder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.app.itaptv.R;
import com.app.itaptv.structure.EarnPointsData;
import com.kalpesh.krecycleradapter.KRecyclerViewHolder;

public class EarnPointsDataHolder extends KRecyclerViewHolder {

    View view;
    TextView title, coins;

    public EarnPointsDataHolder(View itemView) {
        super(itemView);
        view = itemView;
        title = view.findViewById(R.id.pointsTitleTv);
        coins = view.findViewById(R.id.iCoinsTv);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void setData(Context context, Object itemObject) {
        super.setData(context, itemObject);
        if (itemObject instanceof EarnPointsData) {
            EarnPointsData earnPointsData = (EarnPointsData) itemObject;
            title.setText(earnPointsData.title);
            coins.setText(earnPointsData.coins + context.getString(R.string.icoins));
        }
    }
}
