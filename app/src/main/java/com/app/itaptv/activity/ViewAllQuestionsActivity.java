package com.app.itaptv.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.app.itaptv.API.APIManager;
import com.app.itaptv.API.APIRequest;
import com.app.itaptv.API.APIResponse;
import com.app.itaptv.API.Url;
import com.app.itaptv.R;
import com.app.itaptv.holder.QuestionsHolder;
import com.app.itaptv.structure.QuestionsLiveNowData;
import com.app.itaptv.utils.AlertUtils;
import com.app.itaptv.utils.EmptyStateManager;
import com.app.itaptv.utils.Log;
import com.app.itaptv.utils.Utility;
import com.kalpesh.krecycleradapter.KRecyclerViewAdapter;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemCreator;
import com.paginate.recycler.LoadingListItemSpanLookup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewAllQuestionsActivity extends BaseActivity {

    String feedId;

    ProgressBar progressBar;

    EmptyStateManager emptyState;
    SwipeRefreshLayout swipeRefreshLayout;

    RecyclerView recyclerQuestions;
    LinearLayoutManager mLinearLayoutManager;
    KRecyclerViewAdapter adapter;

    String getQuestionsUrl = "";

    ArrayList<QuestionsLiveNowData> arrayListQuestions = new ArrayList<>();

    //Pagination
    private static final int GRID_SPAN = 1;
    private boolean loading = false;
    private int page = 0;
    private Handler handler;
    private Paginate paginate;
    protected long networkDelay = 2000;
    protected boolean customLoadingListItem = false;
    int nextPageNo = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_questions);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.all_questions_activity_title));

        /*setToolbar(true);
        showToolbarBackButton(R.drawable.back_arrow_white);
        showToolbarTitle(true);
        setCustomizedTitle(14);
        setToolbarTitle(getResources().getString(R.string.all_questions_activity_title));*/

        init();

    }

    private void init() {
        feedId = getIntent().getExtras().getBundle("LiveStreamId").getString("StreamId");
        recyclerQuestions = findViewById(R.id.all_questions_recycler_view);
        swipeRefreshLayout = findViewById(R.id.swipeToRefresh);
        progressBar = findViewById(R.id.progressBar);

        progressBar.setVisibility(View.VISIBLE);
        recyclerQuestions.setVisibility(View.INVISIBLE);
        setUpEmptyState();
        initializeRecyclerView();
        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(true);
            //setColorSchemeResources();
            arrayListQuestions.clear();
            getQuestionsLiveNowAPI(1);
        });
    }

    private void initializeRecyclerView() {
        mLinearLayoutManager = new LinearLayoutManager(this);
        //mLinearLayoutManager.setReverseLayout(true);
        //mLinearLayoutManager.setStackFromEnd(true);
        recyclerQuestions.setLayoutManager(mLinearLayoutManager);
        adapter = new KRecyclerViewAdapter(this, arrayListQuestions, (parent, i) -> {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_presenter_questions_recycler_view, parent, false);
            return new QuestionsHolder(layoutView);
        }, (kRecyclerViewHolder, o, i) -> {
            //No click handler needed for all questions
        });
        recyclerQuestions.setAdapter(adapter);
        initializePagination();
    }

    /**
     * Initialization of Empty State
     */
    void setUpEmptyState() {
        emptyState = EmptyStateManager.setUpInActivity(this, new EmptyStateManager.ActionBtnCallBack() {
            @Override
            public void onBtnClick(String action) {
                if (action.equals(EmptyStateManager.ACTION_RETRY)) {
                    getQuestionsLiveNowAPI(nextPageNo);
                }
            }
        });
    }

    /************************ PAGINATION METHODS -- START **********************/

    public void initializePagination() {
        arrayListQuestions.clear();
        adapter.notifyDataSetChanged();
        getQuestionsUrl = Url.GET_QUESTIONS_LIVE_SESSION + feedId + "&page_no=";
        customLoadingListItem = false;

        if (paginate != null) {
            paginate.unbind();
        }
        loading = false;
        page = 0;
        handler = new Handler();
        handler.removeCallbacks(fakeCallback);
        paginate = Paginate.with(recyclerQuestions, callbacks)
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
                getQuestionsUrl = getQuestionsUrl;

            }
            if (nextPageNo != 0) {
                getQuestionsLiveNowAPI(nextPageNo);
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
            if (recyclerQuestions.getLayoutManager() instanceof StaggeredGridLayoutManager) {
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

    private void getQuestionsLiveNowAPI(int nextPageNo) {
        Map<String, String> params = new HashMap<>();
        Log.d("QuestionsLiveNowUrl", getQuestionsUrl);
        APIRequest apiRequest = new APIRequest(getQuestionsUrl + nextPageNo, Request.Method.GET, params, null, this);
        apiRequest.showLoader = false;
        APIManager.request(apiRequest, new APIResponse() {
            @Override
            public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                progressBar.setVisibility(View.GONE);
                recyclerQuestions.setVisibility(View.VISIBLE);
                handleNotificationResponce(response, error, statusCode);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void handleNotificationResponce(String response, Exception error, int statusCode) {
        try {
            if (error != null) {
                // showError(error.getMessage());
                updateEmptyState(error.getMessage());
                swipeRefreshLayout.setRefreshing(false);
            } else {
                if (response != null) {
                    //Log.e("response", response);

                    JSONObject jsonObjectResponse = new JSONObject(response);
                    String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                    if (type.equalsIgnoreCase("error")) {
                        String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                        showError(message);
                        swipeRefreshLayout.setRefreshing(false);
                    } else if (type.equalsIgnoreCase("ok")) {
                        JSONObject jsonArrayMsg = jsonObjectResponse.getJSONObject("msg");
                        JSONArray jsonArrayContents = jsonArrayMsg.getJSONArray("contents");
                        nextPageNo = jsonArrayMsg.has("next_page") ? jsonArrayMsg.getInt("next_page") : 0;
                        for (int i = 0; i < jsonArrayContents.length(); i++) {
                            QuestionsLiveNowData questionsLiveNowData = new QuestionsLiveNowData(jsonArrayContents.getJSONObject(i));
                            arrayListQuestions.add(questionsLiveNowData);
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        adapter.notifyDataSetChanged();
                        loading = false;
                        page++;
                    }
                    swipeRefreshLayout.setRefreshing(false);
                    updateEmptyState(null);
                }
            }

        } catch (JSONException e) {
            updateEmptyState(e.getMessage());
            swipeRefreshLayout.setRefreshing(false);
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
        swipeRefreshLayout.setRefreshing(false);
    }

    /**
     * Update of Empty State
     */
    private void updateEmptyState(String error) {
        if (error == null) {
            if (arrayListQuestions.isEmpty()) {
                emptyState.setImgAndMsg(getString(R.string.no_data_found), null);
                recyclerQuestions.setVisibility(View.INVISIBLE);
            } else {
                recyclerQuestions.setVisibility(View.VISIBLE);
                emptyState.hide();
            }
        } else {
            recyclerQuestions.setVisibility(View.INVISIBLE);
            if (Utility.isConnectingToInternet(this)) {
                emptyState.setMessage(APIManager.GENERIC_API_ERROR_MESSAGE);
            } else {
                emptyState.showNoConnectionState();
            }
        }
    }
}