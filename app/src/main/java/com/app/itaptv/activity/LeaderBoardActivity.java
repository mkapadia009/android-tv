package com.app.itaptv.activity;

import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.app.itaptv.API.APIManager;
import com.app.itaptv.API.APIRequest;
import com.app.itaptv.API.APIResponse;
import com.app.itaptv.API.Url;
import com.app.itaptv.R;
import com.app.itaptv.font_awesome.FontAwesome;
import com.app.itaptv.holder.LeaderHolder;
import com.app.itaptv.structure.LeaderBoardData;
import com.app.itaptv.utils.AlertUtils;
import com.app.itaptv.utils.AnalyticsTracker;
import com.app.itaptv.utils.Constant;
import com.app.itaptv.utils.EmptyStateManager;
import com.app.itaptv.utils.LocalStorage;
import com.app.itaptv.utils.Log;
import com.app.itaptv.utils.SpacingItemDecoration;
import com.app.itaptv.utils.Utility;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.kalpesh.krecycleradapter.Interface.KRecyclerViewHolderCallBack;
import com.kalpesh.krecycleradapter.KRecyclerViewAdapter;
import com.kalpesh.krecycleradapter.KRecyclerViewHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LeaderBoardActivity extends BaseActivity {

    // private ArrayList<LeaderBoardData> topWinnersList = new ArrayList<LeaderBoardData>();
    private ArrayList<LeaderBoardData> winnersList = new ArrayList<LeaderBoardData>();
    //  private RecyclerView topWinnersRecyclerView;
    private RecyclerView winnersRecyclerView;
    ProgressBar progressBar;
    CollapsingToolbarLayout collapsingToolbarLayout;
    LinearLayout llLeaderTop;
    EmptyStateManager emptyState;
    ArrayList<LeaderBoardData> leaderData = new ArrayList<>();
    int UserPosition = -1;
    String UserId;
    TextView tvUserNo, tvUserLeaderCoin;
    ImageView ivUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);
        setTheme(R.style.Theme_Leanback);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        init();
    }

    private void init() {
        setToolbar(true);
        showToolbarBackButton(R.drawable.white_arrow);
        showToolbarTitle(true);
        setCustomizedTitle(0);
        //  topWinnersRecyclerView = findViewById(R.id.rvLeaderThreelist);
        winnersRecyclerView = findViewById(R.id.rvLeaderList);
        collapsingToolbarLayout = findViewById(R.id.collapsetoolbar);
        progressBar = findViewById(R.id.progressBar);
        llLeaderTop = findViewById(R.id.llLeaderTop);
        tvUserNo = findViewById(R.id.tvUserNo);
        tvUserLeaderCoin = findViewById(R.id.tvUserLeaderCoin);
        ivUser = findViewById(R.id.ivUser);

        UserId = LocalStorage.getUserId();
        AppBarLayout appBarLayout = findViewById(R.id.appBarLayout);

        // appBarLayout.setExpanded(false);
        collapsingToolbarLayout.setCollapsedTitleTypeface(ResourcesCompat.getFont(this, R.font.rubik_medium));

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    llLeaderTop.setVisibility(View.INVISIBLE);
                    collapsingToolbarLayout.setTitle(getString(R.string.leaderboard));
                    isShow = true;
                } else if (isShow) {
                    llLeaderTop.setVisibility(View.VISIBLE);
                    collapsingToolbarLayout.setTitle("");
                    isShow = true;
                }
            }
        });


        getLeaderboardData();
    }

    /**
     * API Calling the LeaderBoard
     */
    private void getLeaderboardData() {
        setUpEmptyState();
        progressBar.setVisibility(View.VISIBLE);
        winnersRecyclerView.setVisibility(View.INVISIBLE);
        Map<String, String> params = new HashMap<>();
        APIRequest apiRequest = new APIRequest(Url.GET_LEADERBOARD, Request.Method.GET, params,
                null, LeaderBoardActivity.this);
        apiRequest.showLoader = false;
        APIManager.request(apiRequest, new APIResponse() {
            @Override
            public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                progressBar.setVisibility(View.GONE);
                winnersRecyclerView.setVisibility(View.VISIBLE);
                handleleaderResponce(response, error);
            }
        });
    }

    private void handleleaderResponce(String response, Exception error) {
        if (error != null) {
            updateEmptyState(error.getMessage());
        } else if (response != null) {
            try {
                JSONObject leaderObject = new JSONObject(response);
                String type = leaderObject.has("type") ? leaderObject.getString("type") : "";
                if (type.equalsIgnoreCase("error")) {
                    String message = leaderObject.has("msg") ? leaderObject.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                    showError(message);
                } else if (type.equalsIgnoreCase("OK")) {
                    JSONObject jsonObject = leaderObject.getJSONObject("msg");
                    JSONArray jsonArray = jsonObject.getJSONArray("top20");
                    JSONObject jsonObject1 = jsonObject.getJSONObject("me");
                    Log.d("LeaderBoard", jsonObject1.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        LeaderBoardData leaderBoardData = new LeaderBoardData(jsonArray.getJSONObject(i));
                        leaderData.add(leaderBoardData);
                    }
                    setUpUserUI(jsonObject1);
                    setupLists(leaderData);
                }
            } catch (JSONException e) {
                updateEmptyState(e.getMessage());
            }
        }

    }

    /**
     * This method shows error alert
     *
     * @param errorMessage message to be displayed in alert dialog
     */
    private void showError(@Nullable String errorMessage) {
        if (errorMessage == null) errorMessage = APIManager.GENERIC_API_ERROR_MESSAGE;
        AlertUtils.showAlert(getString(R.string.label_error), errorMessage, null, this, null);
    }

    private void setupLists(@NonNull ArrayList<LeaderBoardData> data) {
        if (data.size() >= 3) {
            data.get(0).setIcon(R.drawable.gold_crown);
            data.get(0).setIndex(1);
            if (UserId.equalsIgnoreCase(data.get(0).ID)) {
                UserPosition = 0;
            }
            winnersList.add(data.get(0));
            data.get(1).setIcon(R.drawable.silver_crown);
            data.get(1).setIndex(2);
            if (UserId.equalsIgnoreCase(data.get(1).ID)) {
                UserPosition = 1;
            }
            winnersList.add(data.get(1));
            data.get(2).setIcon(R.drawable.bronze_crown);
            data.get(2).setIndex(3);
            if (UserId.equalsIgnoreCase(data.get(2).ID)) {
                UserPosition = 2;
            }
            winnersList.add(data.get(2));
            if (data.size() > 3) {
                for (int i = 3; i < data.size(); i++) {
                    data.get(i).setIndex(i + 1);
                    data.get(i).setIcon(0);
                    if (UserId.equalsIgnoreCase(data.get(i).ID)) {
                        UserPosition = i;
                    }
                    winnersList.add(data.get(i));
                }
            }

        } else if (data.size() == 2) {
            data.get(0).setIcon(R.drawable.gold_crown);
            if (UserId.equalsIgnoreCase(data.get(0).ID)) {
                UserPosition = 0;
            }
            winnersList.add(data.get(0));
            data.get(1).setIcon(R.drawable.silver_crown);
            if (UserId.equalsIgnoreCase(data.get(1).ID)) {
                UserPosition = 1;
            }
            winnersList.add(data.get(1));

        } else if (data.size() == 1) {
            data.get(0).setIcon(R.drawable.gold_crown);
            if (UserId.equalsIgnoreCase(data.get(0).ID)) {
                UserPosition = 0;
            }
            winnersList.add(data.get(0));

        } else {
            // No items
        }
        setupAdapters();
    }

    /**
     * Initialization of Empty State
     */
    void setUpEmptyState() {
        emptyState = EmptyStateManager.setUpInActivity(LeaderBoardActivity.this, new EmptyStateManager.ActionBtnCallBack() {
            @Override
            public void onBtnClick(String action) {
                if (action.equals(EmptyStateManager.ACTION_RETRY)) {
                    getLeaderboardData();
                }
            }
        });
    }

    /**
     * Update of Empty State
     */
    private void updateEmptyState(String error) {
        if (error == null) {
            if (leaderData.isEmpty()) {
                emptyState.setImgAndMsg(getString(R.string.no_data_found), FontAwesome.FA_Exclamation);
                //   topWinnersRecyclerView.setVisibility(View.INVISIBLE);
                winnersRecyclerView.setVisibility(View.INVISIBLE);
            } else {
                //   topWinnersRecyclerView.setVisibility(View.INVISIBLE);
                winnersRecyclerView.setVisibility(View.INVISIBLE);
                emptyState.hide();
            }
        } else {
            //  topWinnersRecyclerView.setVisibility(View.INVISIBLE);
            winnersRecyclerView.setVisibility(View.INVISIBLE);
            if (Utility.isConnectingToInternet(LeaderBoardActivity.this)) {
                emptyState.setMessage(APIManager.GENERIC_API_ERROR_MESSAGE);
            } else {
                emptyState.showNoConnectionState();
            }
        }

    }

    private void setUpUserUI(JSONObject jsonObject1) {
        // Set User Position and Image
        //  appBarLayout.setExpanded(true);
        try {
            String s = Utility.ordinal((jsonObject1.getInt("rank")));
            SpannableString ss1 = new SpannableString(s);
            Utility.setFontSizeForPath(ss1, String.valueOf(jsonObject1.getInt("rank")), (int) tvUserNo.getTextSize() + 15);
            tvUserNo.setText(ss1);

            tvUserLeaderCoin.setText(jsonObject1.getString("value") + getString(R.string.icoins));
            Drawable icon = getResources().getDrawable(R.drawable.ic_coin);
            if (icon != null) {
                icon = DrawableCompat.wrap(icon);
                int height = icon.getIntrinsicHeight();
                int width = icon.getIntrinsicWidth();
                icon.setBounds(0, 0, width, height);
                tvUserLeaderCoin.setCompoundDrawables(icon, null, null, null);
            }

            String imgUrl = jsonObject1.getJSONObject("user").getString("img");
            Glide.with(getApplicationContext())
                    .load(imgUrl)
                    .thumbnail(0.1f)
                    .apply(new RequestOptions().dontAnimate().diskCacheStrategyOf(DiskCacheStrategy.NONE).placeholder(R.drawable.user).skipMemoryCache(true))
                    .into(ivUser);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setupAdapters() {
        // Other winners RecyclerView and its Adapter
        winnersRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        KRecyclerViewAdapter winnersAdapter = new KRecyclerViewAdapter(this,
                winnersList, new KRecyclerViewHolderCallBack() {
            @Override
            public KRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_leaderboard, viewGroup, false);
                return new LeaderHolder(view);
            }
        }, (kRecyclerViewHolder, o, i) -> {
        });
        winnersRecyclerView.setAdapter(winnersAdapter);
        winnersRecyclerView.addItemDecoration(new SpacingItemDecoration(Constant.RV_HV_SPACING));
        winnersAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AnalyticsTracker.isGameOngoing) {
            AnalyticsTracker.resumeTimer(AnalyticsTracker.GAME);
        }
    }
}
