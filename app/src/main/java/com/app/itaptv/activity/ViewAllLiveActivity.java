package com.app.itaptv.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.android.volley.Request;
import com.app.itaptv.API.APIManager;
import com.app.itaptv.API.APIRequest;
import com.app.itaptv.API.APIResponse;
import com.app.itaptv.API.Url;
import com.app.itaptv.R;
import com.app.itaptv.holder.LiveNowViewListHolder;
import com.app.itaptv.structure.LiveNowData;
import com.app.itaptv.utils.AlertUtils;
import com.app.itaptv.utils.Constant;
import com.app.itaptv.utils.EmptyStateManager;
import com.app.itaptv.utils.Log;
import com.app.itaptv.utils.SpacingItemDecoration;
import com.app.itaptv.utils.Utility;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewAllLiveActivity extends BaseActivity {
    ProgressBar progressBar;
    RecyclerView rvAllQuestions;
    EmptyStateManager emptyState;
    KRecyclerViewAdapter adapter;

    // Pagination
    private static final int GRID_SPAN = 1;
    private boolean loading = false;
    private int page = 0;
    private Handler handler;
    private Paginate paginate;
    protected long networkDelay = 2000;
    protected boolean customLoadingListItem = false;
    int nextPageNo = 1;
    int currentFeedIndex = 0;

    String upcommingUrl = "";
    String LiveID = "";
    String title = "";
    ArrayList<LiveNowData> arrayListLiveContent = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_questions);
        title = getIntent().getBundleExtra("data").getString("title");
        LiveID = getIntent().getBundleExtra("data").getString("ID");
        init();
    }

    private void init() {
        setToolbar(true);
        showToolbarBackButton(R.drawable.white_arrow);
        showToolbarTitle(true);
        setCustomizedTitle(0);
        setToolbarTitle(title);

        progressBar = findViewById(R.id.progressBar);
        rvAllQuestions = findViewById(R.id.all_questions_recycler_view);

        progressBar.setVisibility(View.VISIBLE);
        rvAllQuestions.setVisibility(View.INVISIBLE);
        setUpEmptyState();
        setUpRecyclerView();

    }

    /**
     * Initialization of Empty State
     */
    void setUpEmptyState() {
        emptyState = EmptyStateManager.setUpInActivity(ViewAllLiveActivity.this, new EmptyStateManager.ActionBtnCallBack() {
            @Override
            public void onBtnClick(String action) {
                if (action.equals(EmptyStateManager.ACTION_RETRY)) {
                    getUpcommingLiveAPI(nextPageNo);
                }
            }
        });
    }

    /**
     * Set Up the RecyclerView
     */
    private void setUpRecyclerView() {
        rvAllQuestions.setLayoutManager(new LinearLayoutManager(ViewAllLiveActivity.this, LinearLayoutManager.VERTICAL, false));/*
        SeparatorDecoration decoration = new SeparatorDecoration(ViewAllLiveActivity.this, getResources().getColor(R.color.tab_grey), 1.5f);
        rvUpcommingLive.addItemDecoration(decoration);*/
        adapter = new KRecyclerViewAdapter(ViewAllLiveActivity.this, arrayListLiveContent, new KRecyclerViewHolderCallBack() {
            @Override
            public KRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_live_session_upcomming, viewGroup, false);
                return new LiveNowViewListHolder(layoutView, false);
            }
        }, new KRecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClicked(KRecyclerViewHolder kRecyclerViewHolder, Object o, int i) {
                if (o != null) {
                }
            }
        });
        rvAllQuestions.setAdapter(adapter);
        rvAllQuestions.addItemDecoration(new SpacingItemDecoration(Constant.RV_HV_SPACING));
        initializePagination();
    }

    /************************ PAGINATION METHODS -- START **********************/

    public void initializePagination() {
        arrayListLiveContent.clear();
        adapter.notifyDataSetChanged();
        upcommingUrl = Url.GET_LIVE_UPCOMING + "&ID=" + LiveID + "&page_no=";
        customLoadingListItem = false;

        if (paginate != null) {
            paginate.unbind();
        }
        loading = false;
        page = 0;
        handler = new Handler();
        handler.removeCallbacks(fakeCallback);
        paginate = Paginate.with(rvAllQuestions, callbacks)
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
                upcommingUrl = upcommingUrl;

            }
            if (nextPageNo != 0) {
                getUpcommingLiveAPI(nextPageNo);
            }

        }
    };

    private void getUpcommingLiveAPI(int nextPageNo) {
        Map<String, String> params = new HashMap<>();
        Log.d("notification", upcommingUrl);
        APIRequest apiRequest = new APIRequest(upcommingUrl + nextPageNo, Request.Method.GET, params, null, this);
        apiRequest.showLoader = false;
        APIManager.request(apiRequest, new APIResponse() {
            @Override
            public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                progressBar.setVisibility(View.GONE);
                rvAllQuestions.setVisibility(View.VISIBLE);
                handleUpcommingResponce(response, error, statusCode);
            }
        });
    }

    private void handleUpcommingResponce(String response, Exception error, int statusCode) {
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
                            LiveNowData liveData = new LiveNowData(jsonArrayContents.getJSONObject(i));
                            arrayListLiveContent.add(liveData);
                        }
                        adapter.notifyDataSetChanged();
                        loading = false;
                        page++;
                    }
                    updateEmptyState(null);

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
            if (rvAllQuestions.getLayoutManager() instanceof StaggeredGridLayoutManager) {
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
     * Update of Empty State
     */
    private void updateEmptyState(String error) {
        if (error == null) {
            if (arrayListLiveContent.isEmpty()) {
                emptyState.setImgAndMsg(getString(R.string.no_data_found), null);
                rvAllQuestions.setVisibility(View.INVISIBLE);
            } else {
                rvAllQuestions.setVisibility(View.VISIBLE);
                emptyState.hide();
            }
        } else {
            rvAllQuestions.setVisibility(View.INVISIBLE);
            if (Utility.isConnectingToInternet(this)) {
                emptyState.setMessage(APIManager.GENERIC_API_ERROR_MESSAGE);
            } else {
                emptyState.showNoConnectionState();
            }
        }

    }
}
