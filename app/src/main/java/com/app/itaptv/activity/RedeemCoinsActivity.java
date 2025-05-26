package com.app.itaptv.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.android.volley.Request;
import com.app.itaptv.API.APIManager;
import com.app.itaptv.API.APIRequest;
import com.app.itaptv.API.APIResponse;
import com.app.itaptv.API.Url;
import com.app.itaptv.R;
import com.app.itaptv.custom_interface.WalletCallback;
import com.app.itaptv.holder.CouponHolder;
import com.app.itaptv.structure.CouponData;
import com.app.itaptv.utils.AlertUtils;
import com.app.itaptv.utils.AnalyticsTracker;
import com.app.itaptv.utils.EmptyStateManager;
import com.app.itaptv.utils.Utility;
import com.app.itaptv.utils.Wallet;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.kalpesh.krecycleradapter.Interface.KRecyclerItemClickListener;
import com.kalpesh.krecycleradapter.Interface.KRecyclerViewHolderCallBack;
import com.kalpesh.krecycleradapter.KRecyclerViewAdapter;
import com.kalpesh.krecycleradapter.KRecyclerViewHolder;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemCreator;
import com.paginate.recycler.LoadingListItemSpanLookup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by poonam on 26/9/18.
 */

public class RedeemCoinsActivity extends BaseActivity {

    private TextView tvBalanceCoins;

    private ArrayList<CouponData> arrayListCouponData = new ArrayList<>();
    private long walletBalance = 0L;
    private boolean success;

    // Pagination
    private static final int GRID_SPAN = 1;
    private boolean loading = false;
    private int page = 0;
    private Handler handler;
    private Paginate paginate;
    protected long networkDelay = 2000;
    protected boolean customLoadingListItem = false;
    int nextPageNo = 1;

    String CoupanUrl;
    RecyclerView rvCoupon;
    EmptyStateManager emptyState;
    ProgressBar progress_bar;
    RelativeLayout llprogressbar;

    KRecyclerViewAdapter adapter;
    CollapsingToolbarLayout collapsingToolbarLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redeem_coins);
        setTheme(R.style.Theme_Leanback);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        init();
        setCouponRecyclerView();
    }

    private void init() {
        setToolbar(true);
        showToolbarBackButton(R.drawable.white_arrow);
        showToolbarTitle(true);
        setCustomizedTitle(0);
        setUpEmptyState();
        //walletBalance = getIntent().getLongExtra(WalletActivity.KEY_WALLET_BALANCE, 0L);
        collapsingToolbarLayout = findViewById(R.id.collapsetoolbar);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        rvCoupon = findViewById(R.id.rvCoupon);
        progress_bar = findViewById(R.id.progressBar);
        llprogressbar = findViewById(R.id.llprogressbar);
        llprogressbar.setVisibility(View.VISIBLE);
        progress_bar.setVisibility(View.VISIBLE);
        rvCoupon.setVisibility(View.INVISIBLE);

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
                    collapsingToolbarLayout.setTitle(String.format(getString(R.string.text_your_balance_coins), walletBalance));
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbarLayout.setTitle(String.format(getString(R.string.text_your_balance_coins), walletBalance));//carefull there should a space between double quote otherwise it wont work
                    isShow = true;
                }
            }
        });

        getWalletBalance();
    }

    private void getWalletBalance() {
        Wallet.getWalletBalance(this, new WalletCallback() {
            @Override
            public boolean onResult(boolean success, @Nullable String error, long coins, long diamonds, long creditedCoins, JSONArray historyData, int historyCount) {
                handleAppBalance42Response(success, error, coins);
                return success;
            }
        });
    }

    private void handleAppBalance42Response(boolean success, String error, long coins) {
        walletBalance = success ? coins : 0;
    }

    /************************ PAGINATION METHODS -- START **********************/

    public void initializePagination() {
        arrayListCouponData.clear();
        adapter.notifyDataSetChanged();
        CoupanUrl = Url.GET_FEEDS;
        customLoadingListItem = false;

        if (paginate != null) {
            paginate.unbind();
        }
        loading = false;
        page = 0;
        handler = new Handler();
        handler.removeCallbacks(fakeCallback);
        paginate = Paginate.with(rvCoupon, callbacks)
                .setLoadingTriggerThreshold(0)
                .addLoadingListItem(true)
                .setLoadingListItemCreator(customLoadingListItem ? new CustomLoadingListItemCreator() : null)
                .setLoadingListItemSpanSizeLookup(new LoadingListItemSpanLookup() {
                    @Override
                    public int getSpanSize() {
                        return GRID_SPAN;
                    }
                })
                .build();
    }

    Paginate.Callbacks callbacks = new Paginate.Callbacks() {
        @Override
        public void onLoadMore() {
            // Load next page of data (e.g. network or database)
            loading = true;
            handler.postDelayed(fakeCallback, networkDelay);

        }

        @Override
        public boolean isLoading() {
            // Indicate whether new page loading is in progress or not
            return loading; // Return boolean weather data is already loading or not
        }

        @Override
        public boolean hasLoadedAllItems() {
            // Indicate whether all data (pages) are loaded or not
            return nextPageNo == 0; // If all pages are loaded return true
        }
    };


    private Runnable fakeCallback = new Runnable() {
        @Override
        public void run() {

            if (page > 0) {
                CoupanUrl = CoupanUrl;

            }
            if (nextPageNo != 0) {
                getCoupanAPI();
            }

        }
    };


    private class CustomLoadingListItemCreator implements LoadingListItemCreator {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.custom_loading_list_item, parent, false);
            return new VH(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            VH vh = (VH) holder;
            vh.tvLoading.setText(String.format(getString(R.string.total_items_loaded), adapter.getItemCount()));

            // This is how you can make full span if you are using StaggeredGridLayoutManager
            if (rvCoupon.getLayoutManager() instanceof StaggeredGridLayoutManager) {
                StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) vh.itemView.getLayoutParams();
                params.setFullSpan(true);
            }
        }
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvLoading;

        public VH(View itemView) {
            super(itemView);
            tvLoading = itemView.findViewById(R.id.tv_loading_text);
        }
    }

    /************************ PAGINATION METHODS -- END **********************/

    /**
     * Set recycler view to display coupon list
     */
    private void setCouponRecyclerView() {

        rvCoupon.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvCoupon.setNestedScrollingEnabled(false);
      /*  SeparatorDecoration decoration = new SeparatorDecoration(this, getResources().getColor(R.color.tab_grey), 1f);
        rvCoupon.addItemDecoration(decoration);*/


        adapter = new KRecyclerViewAdapter(this, arrayListCouponData, new KRecyclerViewHolderCallBack() {
            @Override
            public KRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_redeem_coin_coupons, viewGroup, false);
                return new CouponHolder(layoutView);
            }
        }, new KRecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClicked(KRecyclerViewHolder kRecyclerViewHolder, Object o, int i) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("CoupanData", (Serializable) o);
                bundle.putString("Balance", String.valueOf(walletBalance));
                startActivityForResult(
                        new Intent(RedeemCoinsActivity.this, CouponDetailsActivity.class).putExtra("CoupanDetail", bundle),
                        WalletActivity.REQUEST_CODE
                );

            }
        });

        rvCoupon.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        initializePagination();
    }

    private void getCoupanAPI() {
        try {
            Map<String, String> params = new HashMap<>();
            APIRequest apiRequest = new APIRequest(Url.GET_COUPAN + "&page_no=" + nextPageNo, Request.Method.GET, params, null, RedeemCoinsActivity.this);
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    progress_bar.setVisibility(View.GONE);
                    llprogressbar.setVisibility(View.GONE);
                    rvCoupon.setVisibility(View.VISIBLE);
                    handleCoupanResponse(response, error);
                }
            });
        } catch (Exception e) {

        }
    }

    private void handleCoupanResponse(String response, Exception error) {
        try {

            if (error != null) {
                // showError(error.getMessage());
                updateEmptyState(error.getMessage());
            } else {
                if (response != null) {
                    //Log.e("response", response);

                    JSONObject jsonObjectResponse = new JSONObject(response);
                    String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                    if (type.equalsIgnoreCase("error")) {
                        String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                        showError(message);
                    } else if (type.equalsIgnoreCase("ok")) {
                        JSONObject jsonArrayMsg = jsonObjectResponse.getJSONObject("msg");
                        JSONArray jsonArrayContents = jsonArrayMsg.getJSONArray("contents");
                        nextPageNo = jsonArrayMsg.has("next_page") ? jsonArrayMsg.getInt("next_page") : 0;
                        for (int i = 0; i < jsonArrayContents.length(); i++) {
                            CouponData couponData = new CouponData(jsonArrayContents.getJSONObject(i));
                            arrayListCouponData.add(couponData);

                        }
                        adapter.notifyDataSetChanged();
                        loading = false;
                        page++;
                    }

                }
            }


        } catch (JSONException e) {
            updateEmptyState(e.getMessage());
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

    /**
     * Initialization of Empty State
     */
    void setUpEmptyState() {
        emptyState = EmptyStateManager.setUpInActivity(this, new EmptyStateManager.ActionBtnCallBack() {
            @Override
            public void onBtnClick(String action) {
                if (action.equals(EmptyStateManager.ACTION_RETRY)) {
                    getCoupanAPI();
                }
            }
        });
    }

    /**
     * Update of Empty State
     */
    private void updateEmptyState(String error) {
        if (error == null) {
            if (arrayListCouponData.isEmpty()) {
                emptyState.setImgAndMsg(getString(R.string.no_data_found), null);
                rvCoupon.setVisibility(View.GONE);
            } else {
                rvCoupon.setVisibility(View.VISIBLE);
                emptyState.hide();
            }
        } else {
            rvCoupon.setVisibility(View.GONE);
            if (Utility.isConnectingToInternet(this)) {
                emptyState.setMessage(APIManager.GENERIC_API_ERROR_MESSAGE);
            } else {
                emptyState.showNoConnectionState();
            }
        }

    }

    private void setWalletBalance() {
        collapsingToolbarLayout.setTitle(String.format(getString(R.string.text_your_balance_coins), walletBalance));

        //   collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.colorAccent));
        //  collapsingToolbarLayout.setExpandedTitleGravity(Gravity.BOTTOM);

        //tvBalanceCoins.setText(String.format(getString(R.string.text_your_balance_coins), walletBalance));
    }

    private void setResult() {
        setWalletBalance();
        Intent intent = getIntent();
        intent.putExtra(WalletActivity.KEY_WALLET_BALANCE, walletBalance);
        intent.putExtra(WalletActivity.KEY_SUCCESS, success);
        setResult(RESULT_OK, intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == WalletActivity.REQUEST_CODE) {
            if (data != null) {
                success = data.getBooleanExtra(WalletActivity.KEY_SUCCESS, false);
                if (success) {
                    walletBalance = data.getLongExtra(WalletActivity.KEY_WALLET_BALANCE, 0L);
                    setResult();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AnalyticsTracker.isGameOngoing) {
            AnalyticsTracker.resumeTimer(AnalyticsTracker.GAME);
        }
    }
}
