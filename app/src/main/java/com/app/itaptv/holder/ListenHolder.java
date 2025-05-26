package com.app.itaptv.holder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.app.itaptv.R;
import com.kalpesh.krecycleradapter.KRecyclerViewHolder;

public class ListenHolder extends KRecyclerViewHolder {

    View views;
    FrameLayout llfeed;

    public ListenHolder(View itemView) {
        super(itemView);
        views = itemView;
        llfeed = views.findViewById(R.id.llfeed);
    }

    @Override
    protected void setData(Context context, Object itemObject) {
        super.setData(context, itemObject);

        if (itemObject instanceof ViewGroup) {
            ViewGroup layoutview = (ViewGroup) itemObject;
            if (((View) itemObject).getParent() != null)
                ((ViewGroup) ((View) itemObject).getParent()).removeView((View) itemObject);
            llfeed.addView(layoutview);
        }
    }
}
