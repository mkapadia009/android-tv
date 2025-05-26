package com.app.itaptv.holder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.app.itaptv.R;
import com.app.itaptv.structure.MessageData;
import com.app.itaptv.utils.Utility;
import com.kalpesh.krecycleradapter.KRecyclerViewHolder;

/**
 * Created by poonam on 8/2/19.
 */

public class MessageHolder extends KRecyclerViewHolder {
    View view;
    TextView tvUsername;
    TextView tvComment;

    public MessageHolder(View itemView) {
        super(itemView);
        view = itemView;
        tvUsername = view.findViewById(R.id.tvUsername);
        tvComment = view.findViewById(R.id.tvComment);
    }

    @Override
    protected void setData(Context context, Object itemObject) {
        super.setData(context, itemObject);

        if (itemObject instanceof MessageData) {
            MessageData messageData = (MessageData) itemObject;
            if (messageData.senderName.matches("[0-9]+") && messageData.senderName.length() > 5) {
                tvUsername.setText(Utility.getMobileStrikeOutMiddle(messageData.senderName));
            } else {
                tvUsername.setText(messageData.senderName);
            }
            tvComment.setText(messageData.message);
        }
    }
}
