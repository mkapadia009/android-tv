package com.app.itaptv.holder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.itaptv.R;
import com.app.itaptv.structure.GameData;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kalpesh.krecycleradapter.KRecyclerViewHolder;

/**
 * Created by poonam on 18/9/18.
 */

// Unused Class
public class GameHolder extends KRecyclerViewHolder {
    View view;
    LinearLayout llDeclareWinners;
    ImageView ivGameBgImage;
    ImageView ivGameImage;
    TextView tvGameTime;
    TextView tvDeclareWinners;

    GameData gameData;

    public GameHolder(View itemView) {
        super(itemView);
        view = itemView;
        llDeclareWinners = view.findViewById(R.id.llDeclareWinners);
        ivGameBgImage = view.findViewById(R.id.ivGameBgImage);
        ivGameImage = view.findViewById(R.id.ivGameImage);
        tvGameTime = view.findViewById(R.id.tvGameTime);
        tvDeclareWinners = view.findViewById(R.id.tvDeclareWinners);
    }

    @Override
    protected void setData(Context context, Object itemObject) {
        super.setData(context, itemObject);
        if (itemObject instanceof GameData) {
            gameData = (GameData) itemObject;

            Glide.with(context)
                    .load(gameData.backgroundImage)
                    .thumbnail(0.1f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(200,200)
                    .into(ivGameBgImage);
            Glide.with(context)
                    .load(gameData.imageUrl)
                    .thumbnail(0.1f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(200,200)
                    .into(ivGameImage);

            setGameTime(context);
        }
    }


    private void setGameTime(Context context) {
        switch (gameData.quizType) {
            case GameData.QUIZE_TYPE_LIVE:

                /*if (gameData.playedGame || !GameDateValidation.isValidDate(gameData.start, gameData.end)) {
                    llDeclareWinners.setVisibility(View.VISIBLE);
                    tvGameTime.setVisibility(View.GONE);
                    String winnersAnnounceAt = GameDateValidation.getDateTime(context, gameData.announceWinnerAt);
                    tvDeclareWinners.setText(String.format(context.getString(R.string.winners_to_be_declared_by), winnersAnnounceAt));

                } else {
                    llDeclareWinners.setVisibility(View.GONE);
                    tvGameTime.setVisibility(View.VISIBLE);
                    String time = GameDateValidation.getDateTime(context, gameData.end);
                    tvGameTime.setText(String.format(context.getString(R.string.game_ends_by_time), time));
                }*/

                break;
            case GameData.QUIZE_TYPE_TURN_BASED:
                llDeclareWinners.setVisibility(View.GONE);
                tvGameTime.setVisibility(View.GONE);
                break;
        }
    }
}
