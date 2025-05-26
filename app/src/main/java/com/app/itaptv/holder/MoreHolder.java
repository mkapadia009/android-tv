package com.app.itaptv.holder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.itaptv.R;
import com.app.itaptv.fragment.MoreFragment;
import com.app.itaptv.structure.MoreMenuData;
import com.kalpesh.krecycleradapter.KRecyclerViewHolder;

public class MoreHolder extends KRecyclerViewHolder {
    TextView tvMoreMenuName;
    ImageView ivMoreMenu;
    View view;

    public MoreHolder(View itemView, MoreFragment context) {
        super(itemView);
        view = itemView;
        tvMoreMenuName = itemView.findViewById(R.id.tvMoreMenu);
        ivMoreMenu = itemView.findViewById(R.id.ivMoreMenu);
    }

    @Override
    protected void setData(Context context, Object itemObject) {
        super.setData(context, itemObject);

        if (itemObject instanceof MoreMenuData) {
            tvMoreMenuName.setText(((MoreMenuData) itemObject).MenuName);
            ivMoreMenu.setImageResource(((MoreMenuData) itemObject).img);
        }
    }
}
