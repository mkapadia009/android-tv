/*
package com.app.itap.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.app.itap.API.APIManager;
import com.app.itap.API.APIRequest;
import com.app.itap.API.APIResponse;
import com.app.itap.API.Url;
import com.app.itap.R;
import com.app.itap.activity.ViewAllLiveActivity;
import com.app.itap.holder.ListenHolder;
import com.app.itap.holder.LiveNowViewListHolder;
import com.app.itap.structure.LiveData;
import com.app.itap.structure.LiveNowData;
import com.app.itap.utils.AlertUtils;
import com.app.itap.utils.Constant;
import com.app.itap.utils.EmptyStateManager;
import com.app.itap.utils.Log;
import com.app.itap.utils.SeparatorDecoration;
import com.app.itap.utils.SpacingItemDecoration;
import com.app.itap.utils.Utility;
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

public class UpcomingLiveFragment extends Fragment {

    View view;
    ProgressBar progressBar;
    RecyclerView rvUpcomingLive;
    EmptyStateManager emptyState;
    KRecyclerViewAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;

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
    ArrayList<ViewGroup> arrayListupcomingData = new ArrayList<>();
    ArrayList<LiveNowData> arrayupcomingList = new ArrayList<>();
    ArrayList<LiveData> arrayListPageWiseLiveData = new ArrayList<>();
    ArrayList<LiveData> arrayListLiveData = new ArrayList<>();

    public UpcomingLiveFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_upcoming_live, container, false);
        init();
        return view;
    }

    */
/**
     * Initialization of ALL
     *//*

    private void init() {
        progressBar = view.findViewById(R.id.progressBar);
        rvUpcomingLive = view.findViewById(R.id.rvUpcomingLive);
        swipeRefreshLayout = view.findViewById(R.id.swipeToRefreshUpcomingLive);

        progressBar.setVisibility(View.VISIBLE);
        rvUpcomingLive.setVisibility(View.INVISIBLE);
        setUpEmptyState();
        setUpRecyclerView();

        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(true);
            //setColorSchemeResources();
            arrayListupcomingData.clear();
            getUpcomingLiveAPI(1);
        });
    }

    */
/**
     * Set Up the RecyclerView
     *//*

    private void setUpRecyclerView() {
        rvUpcomingLive.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        SeparatorDecoration decoration = new SeparatorDecoration(getContext(), getResources().getColor(R.color.tab_grey), 0.5f);
        rvUpcomingLive.addItemDecoration(decoration);
        adapter = new KRecyclerViewAdapter(getContext(), arrayListupcomingData, new KRecyclerViewHolderCallBack() {
            @Override
            public KRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_linear_item, viewGroup, false);
                return new ListenHolder(layoutView);
            }
        }, new KRecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClicked(KRecyclerViewHolder kRecyclerViewHolder, Object o, int i) {
                if (o != null) {
                }
            }
        });
        rvUpcomingLive.setAdapter(adapter);
        initializePagination();
    }

    */
/************************ PAGINATION METHODS -- START **********************//*


    public void initializePagination() {
        arrayListupcomingData.clear();
        adapter.notifyDataSetChanged();
        upcommingUrl = Url.GET_LIVE_UPCOMING + "&f_page_no=";
        customLoadingListItem = false;

        if (paginate != null) {
            paginate.unbind();
        }
        loading = false;
        page = 0;
        handler = new Handler();
        handler.removeCallbacks(fakeCallback);
        paginate = Paginate.with(rvUpcomingLive, callbacks)
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
                getUpcomingLiveAPI(nextPageNo);
            }

        }
    };

    private class CustomLoadingListItemCreator implements LoadingListItemCreator {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.custom_loading_list_item, parent, false);
            return new NotificationFragment.VH(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            NotificationFragment.VH vh = (NotificationFragment.VH) holder;
            vh.tvLoading.setText(String.format("Total items loaded: %d.\nLoading more...", adapter.getItemCount()));

            // This is how you can make full span if you are using StaggeredGridLayoutManager
            if (rvUpcomingLive.getLayoutManager() instanceof StaggeredGridLayoutManager) {
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

    */
/************************ PAGINATION METHODS -- END **********************//*



    */
/**
     * Initialization of Empty State
     *//*

    void setUpEmptyState() {
        emptyState = EmptyStateManager.setUpInFragment(view, (AppCompatActivity) getActivity(), new EmptyStateManager.ActionBtnCallBack() {
            @Override
            public void onBtnClick(String action) {
                if (action.equals(EmptyStateManager.ACTION_RETRY)) {
                    getUpcomingLiveAPI(nextPageNo);
                }
            }
        });
    }

    */
/**
     * Call Notification API
     *
     * @param nextPageNo
     *//*

    private void getUpcomingLiveAPI(int nextPageNo) {
        Map<String, String> params = new HashMap<>();
        Log.d("notification", upcommingUrl);
        APIRequest apiRequest = new APIRequest(upcommingUrl + nextPageNo, Request.Method.GET, params, null, getContext());
        apiRequest.showLoader = false;
        APIManager.request(apiRequest, new APIResponse() {
            @Override
            public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                progressBar.setVisibility(View.GONE);
                rvUpcomingLive.setVisibility(View.VISIBLE);
                handleUpcomingResponce(response, error, statusCode);
            }
        });
    }

    private void handleUpcomingResponce(String response, Exception error, int statusCode) {
        try {
            if (error != null) {
                // showError(error.getMessage());
                updateEmptyState(error.getMessage());
                swipeRefreshLayout.setRefreshing(false);
            } else {
                if (response != null) {
                    Log.e("response", response);

                    JSONObject jsonObjectResponse = new JSONObject(response);
                    String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                    if (type.equalsIgnoreCase("error")) {
                        String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                        showError(message);
                    } else if (type.equalsIgnoreCase("ok")) {
                        JSONObject jsonArrayMsg = jsonObjectResponse.getJSONObject("msg");
                        JSONArray jsonArrayContents = jsonArrayMsg.getJSONArray("feeds");
                        nextPageNo = jsonArrayMsg.has("next_page") ? jsonArrayMsg.getInt("next_page") : 0;
                        new Thread(() -> {
                            try {
                                currentFeedIndex = currentFeedIndex + arrayListPageWiseLiveData.size();
                                arrayListPageWiseLiveData.clear();
                                for (int i = 0; i < jsonArrayContents.length(); i++) {
                                    LiveData liveData = new LiveData(jsonArrayContents.getJSONObject(i));
                                    arrayListPageWiseLiveData.add(liveData);
                                    swipeRefreshLayout.setRefreshing(false);
                                }
                                arrayListLiveData.addAll(currentFeedIndex, arrayListPageWiseLiveData);
                                new Handler(Looper.getMainLooper()).post(() -> updateUI());
                            } catch (Exception e) {
                                e.printStackTrace();
                                swipeRefreshLayout.setRefreshing(false);
                                updateEmptyState(null);
                            }
                        }).start();
                    }
                }
            }
            progressBar.setVisibility(View.GONE);
        } catch (JSONException e) {
            updateEmptyState(e.getMessage());
            swipeRefreshLayout.setRefreshing(false);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void updateUI() {
        new Thread(() -> {
            for (LiveData liveData : arrayListPageWiseLiveData) {
                new Handler(Looper.getMainLooper()).post(() -> setHorizontalRecyclerView(liveData));

            }
            new Handler(Looper.getMainLooper()).post(() -> {
                adapter.notifyDataSetChanged();
                loading = false;
                page++;
            });

        }).start();
    }

    */
/**
     * This method adds the horizontal recycler view dynamically to the parent layout of the page
     *
     * @param liveData Live Data
     *//*

    private void setHorizontalRecyclerView(final LiveData liveData) {
        try {

            View viewContent = View.inflate(getContext(), R.layout.row_horizontal_list, null);


            LinearLayout llHzRow = viewContent.findViewById(R.id.llHzRow);
            TextView tvTitle = viewContent.findViewById(R.id.tvStoreName);
            RecyclerView rvContent = viewContent.findViewById(R.id.rvContent);
            TextView tvViewAll = viewContent.findViewById(R.id.tvViewAll);

            ArrayList<LiveNowData> arrayListLiveContent = liveData.arrayListLiveNowData;

            if (arrayListLiveContent.size() == 0) {
                llHzRow.setVisibility(View.GONE);
                return;
            }

            llHzRow.setVisibility(View.VISIBLE);
            tvTitle.setText(liveData.title);
            rvContent.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            rvContent.setNestedScrollingEnabled(false);
            rvContent.addItemDecoration(new SpacingItemDecoration(Constant.RV_HV_SPACING, SpacingItemDecoration.LEFT));
            // rvContent.setId(position);

            KRecyclerViewAdapter adapterhorizantal = new KRecyclerViewAdapter(getContext(), arrayListLiveContent, new KRecyclerViewHolderCallBack() {
                @Override
                public KRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                    View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_live_session_upcomming, viewGroup, false);
                    return new LiveNowViewListHolder(layoutView, true);
                }
            }, new KRecyclerItemClickListener() {
                @Override
                public void onRecyclerItemClicked(KRecyclerViewHolder kRecyclerViewHolder, Object o, int i) {


                }
            });

            rvContent.setAdapter(adapterhorizantal);
            adapterhorizantal.notifyDataSetChanged();
            tvViewAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("title", liveData.title);
                    bundle.putString("ID", String.valueOf(liveData.id));
                    startActivity(new Intent(getActivity(), ViewAllLiveActivity.class).putExtra("data", bundle));
                    // ViewAllFeedsFragment viewAllFeedsFragment = ViewAllFeedsFragment.newInstance(feedData.id, feedData.title, feedData.tabType, "feeds");
                    //openFragment(viewAllFeedsFragment);
                }
            });
            arrayListupcomingData.add((ViewGroup) viewContent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    */
/**
     * This method shows error alert
     *
     * @param errorMessage message to be displayed in alert dialog
     *//*

    private void showError(@Nullable String errorMessage) {
        if (errorMessage == null) errorMessage = APIManager.GENERIC_API_ERROR_MESSAGE;
        AlertUtils.showAlert(getString(R.string.label_error), errorMessage, null, getContext(), null);
        swipeRefreshLayout.setRefreshing(false);
    }

    */
/**
     * Update of Empty State
     *//*

    private void updateEmptyState(String error) {
        if (error == null) {
            if (arrayListupcomingData.isEmpty()) {
                emptyState.setImgAndMsg("No Data Found.", null);
                emptyState.setSecondaryMsg(Constant.SWIPE_DOWN_MSG);
                rvUpcomingLive.setVisibility(View.INVISIBLE);
            } else {
                rvUpcomingLive.setVisibility(View.VISIBLE);
                emptyState.hide();
            }
        } else {
            rvUpcomingLive.setVisibility(View.INVISIBLE);
            if (Utility.isConnectingToInternet(getContext())) {
                emptyState.setMessage(APIManager.GENERIC_API_ERROR_MESSAGE);
                emptyState.setSecondaryMsg(Constant.SWIPE_DOWN_MSG);
            } else {
                emptyState.showNoConnectionState();
            }
        }
    }
}*/
