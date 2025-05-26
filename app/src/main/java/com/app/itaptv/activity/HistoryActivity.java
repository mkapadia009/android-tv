package com.app.itaptv.activity;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.app.itaptv.R;
import com.app.itaptv.custom_interface.WalletCallback;
import com.app.itaptv.holder.EarnHistoryHolder;
import com.app.itaptv.structure.EarnHistoryData;
import com.app.itaptv.utils.Log;
import com.app.itaptv.utils.SeparatorDecoration;
import com.app.itaptv.utils.Wallet;
import com.kalpesh.krecycleradapter.Interface.KRecyclerItemClickListener;
import com.kalpesh.krecycleradapter.Interface.KRecyclerViewHolderCallBack;
import com.kalpesh.krecycleradapter.KRecyclerViewAdapter;
import com.kalpesh.krecycleradapter.KRecyclerViewHolder;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemCreator;
import com.paginate.recycler.LoadingListItemSpanLookup;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by poonam on 26/9/18.
 */

public class HistoryActivity extends BaseActivity {

    public static String HISTORY_FLAG = "historyFlag";

    LinearLayout llParent;
    ProgressBar progressBar;
    TextView tvLabelNoHistoryFound;
    RecyclerView rvEarnHistory;
    KRecyclerViewAdapter adapter;
    NestedScrollView nsvHistory;

    private ArrayList<EarnHistoryData> arrayListEarnHistory = new ArrayList<>();
    private int historyFlag;
    private int historyCnt;

    int nextPageNo = 1;


    // Pagination
    private static final int GRID_SPAN = 1;
    private boolean loading = false;
    private int page = 0;
    private Handler handler;
    private Paginate paginate;
    protected long networkDelay = 2000;
    protected boolean customLoadingListItem = false;
    int offset = 0;
    int maxRecord = 10;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earn_history);
        init();
    }

    private void init() {
        historyFlag = getIntent().getExtras().getInt(HISTORY_FLAG);
        Log.d("History Flag LOg:", String.valueOf(historyFlag));
        llParent = findViewById(R.id.llParent);
        progressBar = findViewById(R.id.progressBar);
        rvEarnHistory = findViewById(R.id.rvEarnHistory);
        tvLabelNoHistoryFound = findViewById(R.id.tvLabelNoHistoryFound);
        nsvHistory = findViewById(R.id.nsvHistory);

        setToolbar();
        showHistory();
        setEarnHistoryRecyclerView();
        //getHistoryCount();
        if (nextPageNo != 0) {
            getTransactionHistory();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            nsvHistory.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                    if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                        Log.i(TAG, "BOTTOM SCROLL");
                        if (nextPageNo != 0) {
                            getTransactionHistory();
                        }
                    }
                }
            });
        }
    }


    private void setToolbar() {
        setToolbar(true);
        showToolbarBackButton(R.drawable.white_arrow);
        showToolbarTitle(true);
        setCustomizedTitle(0);
    }

    /**
     * Set recycler view to display earn history list
     */
    private void setEarnHistoryRecyclerView() {
        rvEarnHistory.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvEarnHistory.setNestedScrollingEnabled(false);
        SeparatorDecoration decoration = new SeparatorDecoration(this, getResources().getColor(R.color.tab_grey), 1.5f);
        rvEarnHistory.addItemDecoration(decoration);

        adapter = new KRecyclerViewAdapter(this, arrayListEarnHistory, new KRecyclerViewHolderCallBack() {
            @Override
            public KRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_history, viewGroup, false);
                return new EarnHistoryHolder(layoutView);
            }
        }, new KRecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClicked(KRecyclerViewHolder kRecyclerViewHolder, Object o, int i) {

            }
        });

        rvEarnHistory.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void noHistoryFound() {
        rvEarnHistory.setVisibility(View.GONE);
        tvLabelNoHistoryFound.setVisibility(View.VISIBLE);
    }

    private void showHistory() {
        rvEarnHistory.setVisibility(View.VISIBLE);
        tvLabelNoHistoryFound.setVisibility(View.GONE);
    }

    private void getTransactionHistory() {
        Wallet.getTransactionHistory(this, historyFlag, offset, maxRecord, new WalletCallback() {
            @Override
            public boolean onResult(boolean success, @Nullable String error, long coins, long diamonds, long creditedCoins, JSONArray historyData, int historyCount) {
                nextPageNo = historyCount;
                updateData(success, error, historyData);
                return success;
            }
        });
    }

    private void getHistoryCount() {
        Wallet.getHistoryCount(historyFlag, new WalletCallback() {
            @Override
            public boolean onResult(boolean success, @Nullable String error, long coins, long diamonds, long creditedCoins, JSONArray historyData, int historyCount) {
                //if (success) {
                historyCnt = historyCount;
                initializePagination();
                /*} else {
                    noHistoryFound();
                }*/
                return success;
            }
        });
    }

    private void updateData(boolean success, String error, JSONArray jsonArray) {
        if (success) {
            if (jsonArray != null) {
                try {
                    offset = offset + maxRecord;
                    if (offset > historyCnt) {
                        offset = -1;
                    }

                    for (int i = 0; i < jsonArray.length(); i++) {
                        EarnHistoryData earnHistoryData = new EarnHistoryData(jsonArray.getJSONObject(i), historyFlag);
                        arrayListEarnHistory.add(earnHistoryData);
                    }

                    adapter.notifyDataSetChanged();
                    loading = false;
                    page++;

                } catch (JSONException e) {
                    handleError(e.getMessage());
                }
            }
        } else {
            handleError(error);
        }
    }

    private void handleError(String error) {
        loading = false;
        paginate.unbind();
        /*if (!error.contains(Wallet.ERROR_MESSAGE_NOT_FOUND)) {
            String errorMessage = error == null ? Wallet.GENERIC_ERROR_MESSAGE : error;
            AlertUtils.showAlert("Error", errorMessage, null, HistoryActivity.this, null);
            return;
        }*/

        noHistoryFound();

    }


    /************************ PAGINATION METHODS -- START **********************/

    public void initializePagination() {
        arrayListEarnHistory.clear();
        adapter.notifyDataSetChanged();
        customLoadingListItem = false;

        if (paginate != null) {
            paginate.unbind();
        }
        loading = false;
        page = 0;
        handler = new Handler();
        handler.removeCallbacks(fakeCallback);
        paginate = Paginate.with(rvEarnHistory, callbacks)
                .setLoadingTriggerThreshold(0)
                .addLoadingListItem(true)
                .setLoadingListItemCreator(customLoadingListItem ? new HistoryActivity.CustomLoadingListItemCreator() : null)
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
            return offset == -1; // If all pages are loaded return true
        }
    };


    private Runnable fakeCallback = new Runnable() {
        @Override
        public void run() {
            if (offset >= 0) {
                getTransactionHistory();
            }

        }
    };

    private class CustomLoadingListItemCreator implements LoadingListItemCreator {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.custom_loading_list_item, parent, false);
            return new HistoryActivity.VH(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            HistoryActivity.VH vh = (HistoryActivity.VH) holder;
            vh.tvLoading.setText(String.format(getString(R.string.total_items_loaded), adapter.getItemCount()));

            // This is how you can make full span if you are using StaggeredGridLayoutManager
            if (rvEarnHistory.getLayoutManager() instanceof StaggeredGridLayoutManager) {
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

    @Override
    protected void onResume() {
        super.onResume();
        switch (historyFlag) {
            case WalletActivity.FLAG_EARN_HISTORY:
                setTitle(getString(R.string.earn_history));
                break;
            case WalletActivity.FLAG_REDEEM_HISTORY:
                setTitle(getString(R.string.redeem_history));
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (paginate != null) {
            paginate.unbind();
        }

        if (handler != null) {
            handler.removeCallbacks(fakeCallback);
        }
    }
}
