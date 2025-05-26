package com.app.itaptv.holder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.app.itaptv.R;
import com.app.itaptv.structure.NotificationData;
import com.kalpesh.krecycleradapter.KRecyclerViewHolder;

public class NotificationViewHolder extends KRecyclerViewHolder {
    View view;
    TextView tvNotificationTitle, tvNotificationSubTitle, tvDate, tvTime;

    public NotificationViewHolder(View itemView) {
        super(itemView);
        view = itemView;
        tvNotificationTitle = itemView.findViewById(R.id.tvNotificationTitle);
        tvNotificationSubTitle = itemView.findViewById(R.id.tvNotificationSubtitle);
        tvDate = itemView.findViewById(R.id.tvnotifdate);
        tvTime = itemView.findViewById(R.id.tvnotifTime);
    }

    @Override
    protected void setData(Context context, Object itemObject) {
        super.setData(context, itemObject);
        if (itemObject instanceof NotificationData) {
            tvNotificationTitle.setText(((NotificationData) itemObject).title);
            tvNotificationSubTitle.setText(((NotificationData) itemObject).meassage);
            tvTime.setText(((NotificationData) itemObject).time);
            tvDate.setText(((NotificationData) itemObject).date);
        }
    }
}
