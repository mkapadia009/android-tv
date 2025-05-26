package com.app.itaptv.holder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.itaptv.R;
import com.app.itaptv.structure.LeaderBoardData;
import com.app.itaptv.utils.LocalStorage;
import com.app.itaptv.utils.Utility;
import com.kalpesh.krecycleradapter.KRecyclerViewHolder;

public class LeaderTopHolder extends KRecyclerViewHolder {
    TextView tvLeaderName;
    TextView tvLeaderCoins;
    ImageView ivLeaderNo;
    View view;

    public LeaderTopHolder(View itemView) {
        super(itemView);
        view = itemView;
        tvLeaderName = itemView.findViewById(R.id.tvLeaderName);
        tvLeaderCoins = itemView.findViewById(R.id.tvLeaderCoin);
        ivLeaderNo = itemView.findViewById(R.id.ivLeaderNo);

    }

    @Override
    protected void setData(Context context, Object itemObject) {
        super.setData(context, itemObject);

        if (itemObject instanceof LeaderBoardData) {
            String id = LocalStorage.getUserId();
            if (((LeaderBoardData) itemObject).displayName.equalsIgnoreCase("")) {
                tvLeaderName.setText(id.equalsIgnoreCase(((LeaderBoardData) itemObject).ID) ? Utility.getMobileStrikeOut(((LeaderBoardData) itemObject).mobileNo) + "  YOU " : Utility.getMobileStrikeOut(((LeaderBoardData) itemObject).mobileNo));
            } else {
                String displayName[] = ((LeaderBoardData) itemObject).displayName.split(" ");
                tvLeaderName.setText(id.equalsIgnoreCase(((LeaderBoardData) itemObject).ID) ? displayName[0] + " " + context.getString(R.string.you) : displayName[0]);
            }

            tvLeaderCoins.setText(((LeaderBoardData) itemObject).value);
            ivLeaderNo.setImageResource(((LeaderBoardData) itemObject).getIcon());
        }
    }


}
