package com.app.itaptv.holder;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.itaptv.R;
import com.app.itaptv.structure.LuckyWinner;
import com.app.itaptv.structure.User;
import com.app.itaptv.utils.Utility;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kalpesh.krecycleradapter.KRecyclerViewHolder;


public class LuckyWinnerHolder extends KRecyclerViewHolder {

    // UI
    private TextView tvCount, tvWinner;
    private ImageView ivWinnerImage;
    // Variables
    private View view, bac;
    private LuckyWinner luckyWinner;

    public LuckyWinnerHolder(View itemView, LuckyWinner luckyWinner) {
        super(itemView);
        view = itemView;
        this.luckyWinner = luckyWinner;
        init();
    }

    private void init() {
        // tvCount = view.findViewById(R.id.tvCount);
        tvWinner = view.findViewById(R.id.tvWinner);
        ivWinnerImage = view.findViewById(R.id.ivWinnerImage);
        // bac = view.findViewById(R.id.vBac);
    }

    @Override
    protected void setData(final Context context, final Object itemObject) {
        super.setData(context, itemObject);
        if (itemObject instanceof User) {
            final User user = (User) itemObject;
            // tvCount.setText(String.valueOf(getAdapterPosition() + 1));
            Glide.with(context)
                    .load(((User) itemObject).img)
                    .thumbnail(0.1f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.user)
                    .into(ivWinnerImage);
            if (!user.displayName.isEmpty()) {
                tvWinner.setText(user.displayName);
            } else {
                tvWinner.setText(Utility.getMobileStrikeOut(user.mobile));
            }
            //setupColor();
        }
    }

    private void setupColor() {
        if (luckyWinner != null) {
            bac.setBackgroundColor(Color.parseColor(luckyWinner.gameData.colorBox));
            tvWinner.setTextColor(Color.parseColor(luckyWinner.gameData.colorSecondary));
        }
    }

}
