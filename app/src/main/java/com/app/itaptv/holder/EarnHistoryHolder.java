package com.app.itaptv.holder;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.app.itaptv.R;
import com.app.itaptv.structure.EarnHistoryData;
import com.app.itaptv.utils.Wallet;
import com.kalpesh.krecycleradapter.KRecyclerViewHolder;

/**
 * Created by poonam on 26/9/18.
 */

public class EarnHistoryHolder extends KRecyclerViewHolder {
    public static final String TRIVIA = "TRIVIA";
    public static final String LIVE = "LIVE";
    View view;
    TextView tvTitle;
    TextView tvDescription;

    public EarnHistoryHolder(View itemView) {
        super(itemView);
        view = itemView;
        tvTitle = view.findViewById(R.id.tvStoreName);
        tvDescription = view.findViewById(R.id.tvDescription);

    }

    @Override
    protected void setData(Context context, Object itemObject) {
        super.setData(context, itemObject);

        if (itemObject instanceof EarnHistoryData) {
            EarnHistoryData earnHistoryData = (EarnHistoryData) itemObject;
            tvTitle.setText(String.format(context.getResources().getString(R.string.text_coins), earnHistoryData.points));
            String description = String.format(context.getString(R.string.label_coupon_desc), earnHistoryData.description);

            switch (earnHistoryData.flag) {
                case Wallet.FLAG_PLAYBACK:
                    description = String.format(context.getString(R.string.label_playing_des), earnHistoryData.description);
                    break;
                case Wallet.FLAG_QUESTION:
                    description = String.format(context.getString(R.string.label_Answer_question_des), earnHistoryData.description);
                    break;
                case Wallet.FLAG_TRIVIA_GAME:
                    description = String.format(context.getString(R.string.label_game_desc), TRIVIA, earnHistoryData.description);
                    break;
                case Wallet.FLAG_LIVE_GAME:
                    description = String.format(context.getString(R.string.label_game_desc), LIVE, earnHistoryData.description);
                    break;
                case Wallet.FLAG_COUPON:
                    description = String.format(context.getString(R.string.label_coupon_desc), earnHistoryData.description);
                    break;
                case Wallet.FLAG_REGISTER:
                    description = String.format(context.getString(R.string.label_registration_desc), earnHistoryData.description);
                    break;
                default:
                    description = String.format(context.getString(R.string.label_coupon_desc), earnHistoryData.description);
                    break;
            }


            tvDescription.setText(Html.fromHtml(description));
            // tvDescription.setAllCaps(earnHistoryData.historyFlag == WalletActivity.FLAG_EARN_HISTORY ? true : false);
        }
    }
}
