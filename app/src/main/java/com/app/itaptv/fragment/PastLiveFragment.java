/*
package com.app.itap.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.app.itap.API.APIManager;
import com.app.itap.API.APIRequest;
import com.app.itap.API.APIResponse;
import com.app.itap.API.Url;
import com.app.itap.R;
import com.app.itap.activity.LiveActivity;
import com.app.itap.activity.PastStreamActivity;
import com.app.itap.holder.PastLiveHolder;
import com.app.itap.structure.PastLiveData;
import com.app.itap.utils.AlertUtils;
import com.app.itap.utils.Constant;
import com.app.itap.utils.EmptyStateManager;
import com.app.itap.utils.Log;
import com.app.itap.utils.Utility;
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

public class PastLiveFragment extends Fragment {

    View view;
    RecyclerView previousLiveRecyclerView;
    KRecyclerViewAdapter adapter;
    ArrayList<PastLiveData> previousLiveDataList = new ArrayList<>();

    String previousLiveUrl = "";

    ProgressBar progressBar;

    EmptyStateManager emptyState;
    SwipeRefreshLayout swipeRefreshLayout;

    //Pagination
    private static final int GRID_SPAN = 1;
    protected boolean customLoadingListItem = false;
    private Paginate paginate;
    private boolean loading = false;
    private int page = 0;
    private Handler handler;
    protected long networkDelay = 2000;
    int nextPageNo = 1;

    boolean isLive = false;

    public PastLiveFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_previous_live, container, false);
        init();
        return view;

    }

    private void init() {
        ((LiveActivity) getActivity()).setToolbar(true);
        ((LiveActivity) getActivity()).showToolbarBackButton(R.drawable.white_arrow);
        ((LiveActivity) getActivity()).showToolbarTitle(true);
        //((LiveActivity) getActivity()).setToolbarTitle(getString(R.string.label_notification));
        ((LiveActivity) getActivity()).setCustomizedTitle(0);

        previousLiveRecyclerView = (RecyclerView) view.findViewById(R.id.previous_live_recycler_view);
        swipeRefreshLayout = view.findViewById(R.id.swipeToRefreshPreviousLive);
        progressBar = view.findViewById(R.id.progressBar);

        progressBar.setVisibility(View.VISIBLE);
        previousLiveRecyclerView.setVisibility(View.INVISIBLE);
        setUpEmptyState();
        setUpRecyclerView();

        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(true);
            //setColorSchemeResources();
            previousLiveDataList.clear();
            getPreviousLiveAPI(1);
        });
    }

    private void setUpRecyclerView() {
        previousLiveRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        //SeparatorDecoration decoration = new SeparatorDecoration(getContext(), getResources().getColor(R.color.black), 0.5f);
        //liveNowRecyclerView.addItemDecoration(decoration);
        adapter = new KRecyclerViewAdapter(getContext(), previousLiveDataList, new KRecyclerViewHolderCallBack() {
            @Override
            public KRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_live_session, viewGroup, false);
                return new PastLiveHolder(view, isLive);
            }
        }, (kRecyclerViewHolder, o, i) -> {
            if (o != null) {
                openPastSession(o);
            }
        });
        previousLiveRecyclerView.setAdapter(adapter);
        initializePagination();
    }

    public void openPastSession(Object object) {
        if (object instanceof PastLiveData) {
            PastLiveData pastLiveData = (PastLiveData) object;

            Bundle bundle = new Bundle();
            bundle.putParcelable(PastStreamActivity.PAST_SESSION_DATA, pastLiveData);
            startActivity(new Intent(getActivity(), PastStreamActivity.class).putExtra("Bundle", bundle));
        }
    }

    */
/************************ PAGINATION METHODS -- START **********************//*


    public void initializePagination() {
        previousLiveDataList.clear();
        adapter.notifyDataSetChanged();
        previousLiveUrl = Url.GET_LIVE_PREVIOUS_FEED + "&page_no=";
        customLoadingListItem = false;

        if (paginate != null) {
            paginate.unbind();
        }
        loading = false;
        page = 0;
        handler = new Handler();
        handler.removeCallbacks(fakeCallback);
        paginate = Paginate.with(previousLiveRecyclerView, callbacks)
                .setLoadingTriggerThreshold(0)
                .addLoadingListItem(true)
                .setLoadingListItemCreator(customLoadingListItem ? new PastLiveFragment.CustomLoadingListItemCreator() : null)
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
                previousLiveUrl = previousLiveUrl;

            }
            if (nextPageNo != 0) {
                getPreviousLiveAPI(nextPageNo);
            }

        }
    };

    private class CustomLoadingListItemCreator implements LoadingListItemCreator {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.custom_loading_list_item, parent, false);
            return new PastLiveFragment.VH(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            PastLiveFragment.VH vh = (PastLiveFragment.VH) holder;
            vh.tvLoading.setText(String.format("Total items loaded: %d.\nLoading more...", adapter.getItemCount()));

            // This is how you can make full span if you are using StaggeredGridLayoutManager
            if (previousLiveRecyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
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
                    getPreviousLiveAPI(nextPageNo);
                }
            }
        });
    }

    private void getPreviousLiveAPI(int nextPageNo) {
        Map<String, String> params = new HashMap<>();
        Log.d("PreviousLiveURL", previousLiveUrl);
        APIRequest apiRequest = new APIRequest(previousLiveUrl + nextPageNo, Request.Method.GET, params, null, getContext());
        apiRequest.showLoader = false;
        APIManager.request(apiRequest, new APIResponse() {
            @Override
            public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                progressBar.setVisibility(View.GONE);
                previousLiveRecyclerView.setVisibility(View.VISIBLE);
                handlePreviousLiveResponce(response, error, statusCode);
            }
        });
    }

    private void handlePreviousLiveResponce(String response, Exception error, int statusCode) {
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
                        JSONArray jsonArrayContents = jsonArrayMsg.getJSONArray("contents");
                        nextPageNo = jsonArrayMsg.has("next_page") ? jsonArrayMsg.getInt("next_page") : 0;
                        for (int i = 0; i < jsonArrayContents.length(); i++) {
                            PastLiveData previousLiveData = new PastLiveData(jsonArrayContents.getJSONObject(i));
                            previousLiveDataList.add(previousLiveData);
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        adapter.notifyDataSetChanged();
                        loading = false;
                        page++;
                    }
                    updateEmptyState(null);
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

        } catch (JSONException e) {
            updateEmptyState(e.getMessage());
            swipeRefreshLayout.setRefreshing(false);
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
            if (previousLiveDataList.isEmpty()) {
                emptyState.setImgAndMsg("No Data Found.", null);
                emptyState.setSecondaryMsg(Constant.SWIPE_DOWN_MSG);
                previousLiveRecyclerView.setVisibility(View.INVISIBLE);
            } else {
                previousLiveRecyclerView.setVisibility(View.VISIBLE);
                emptyState.hide();
            }
        } else {
            previousLiveRecyclerView.setVisibility(View.INVISIBLE);
            if (Utility.isConnectingToInternet(getContext())) {
                emptyState.setMessage(APIManager.GENERIC_API_ERROR_MESSAGE);
                emptyState.setSecondaryMsg(Constant.SWIPE_DOWN_MSG);
            } else {
                emptyState.showNoConnectionState();
            }
        }
    }
}*/
