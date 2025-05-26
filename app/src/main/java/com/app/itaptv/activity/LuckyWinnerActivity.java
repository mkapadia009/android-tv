package com.app.itaptv.activity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.app.itaptv.API.APIManager;
import com.app.itaptv.API.APIRequest;
import com.app.itaptv.API.Url;
import com.app.itaptv.R;
import com.app.itaptv.holder.LuckyWinnerHolder;
import com.app.itaptv.structure.LuckyWinner;
import com.app.itaptv.utils.Constant;
import com.app.itaptv.utils.Log;
import com.app.itaptv.utils.SpacingItemDecoration;
import com.app.itaptv.utils.Utility;
import com.kalpesh.krecycleradapter.KRecyclerViewAdapter;

import org.json.JSONObject;

public class LuckyWinnerActivity extends BaseActivity {

    // Variables
    Context mContext = LuckyWinnerActivity.this;
    KRecyclerViewAdapter adapterLuckyWinnerListing;
    LuckyWinner luckyWinner;
    public static String KEY_GAME_ID = "gameId";
    // UI
    ImageView ivBackground, ivGame;
    TextView tvCongo, tvLuckyWinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lucky_winner);
        setTheme(R.style.Theme_Leanback);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setupUI();
        getData();
        setToolbar(false);
        showToolbarBackButton(R.drawable.white_arrow);
        showToolbarTitle(false);
    }

    private void setupUI() {
        tvCongo = findViewById(R.id.tvCong);
        tvLuckyWinner = findViewById(R.id.tvLuckyWinner);
    }

    private void getData() {
        try {
            String gameId = "";
            if (getIntent() != null) {
                if (getIntent().hasExtra(KEY_GAME_ID)) {
                    gameId = getIntent().getStringExtra(KEY_GAME_ID);
                }
            }
            APIRequest apiRequest = new APIRequest(Url.GET_LUCKY_WINNERS + "&game_id=" + gameId, Request.Method.GET, null, null, mContext);
            apiRequest.showLoader = true;
            APIManager.request(apiRequest, (response, error, headers, statusCode) -> {
                if (response != null) {
                    try {
                        Log.d("LuckyWinners", response);
                        JSONObject object = new JSONObject(response);
                        if (object.has("msg")) {
                            JSONObject messageObject = object.getJSONObject("msg");
                            luckyWinner = new LuckyWinner(messageObject);
                            setupRecyclerView();
                            setUpView();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void setUpView() {
        if (luckyWinner != null) {
            try {
                String winner = getString(R.string.to_our) + luckyWinner.winnersList.size() + getString(R.string.lucky_winners);
                tvLuckyWinner.setText(winner);
               /* Glide.with(mContext)
                        .load(luckyWinner.gameData.backgroundImage)
                        .thumbnail(0.1f)
                        .apply(new RequestOptions().error(R.drawable.no_image_avail).placeholder(R.drawable.no_image_avail).dontAnimate().diskCacheStrategyOf(DiskCacheStrategy.ALL))
                        .into(ivBackground);

                Glide.with(mContext)
                        .load(luckyWinner.gameData.imageUrl)
                        .apply(new RequestOptions().error(R.drawable.no_image_avail).placeholder(R.drawable.no_image_avail).dontAnimate().diskCacheStrategyOf(DiskCacheStrategy.ALL))
                        .into(ivGame);*/
                tvCongo.setTextColor(Color.parseColor(luckyWinner.gameData.colorPrimary));
                tvLuckyWinner.setTextColor(Color.parseColor(luckyWinner.gameData.colorSecondary));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    // RecyclerView
    void setupRecyclerView() {

        adapterLuckyWinnerListing = new KRecyclerViewAdapter(mContext, luckyWinner.winnersList, (viewGroup, i) -> {
            View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_lucky_winner, viewGroup, false);
            return new LuckyWinnerHolder(layoutView, luckyWinner);
        }, (kRecyclerViewHolder, o, i) -> {

        });
        // LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);

        RecyclerView recyclerView = findViewById(R.id.rvLuckyWinner);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapterLuckyWinnerListing);
        recyclerView.addItemDecoration(new SpacingItemDecoration(Constant.RV_HV_SPACING));
    }
}
