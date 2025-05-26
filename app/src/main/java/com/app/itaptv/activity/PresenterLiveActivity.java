/*
package com.app.itap.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.app.itap.API.APIManager;
import com.app.itap.API.APIRequest;
import com.app.itap.API.Url;
import com.app.itap.R;
import com.app.itap.holder.PresenterLiveHolder;
import com.app.itap.structure.LiveNowData;
import com.app.itap.utils.AlertUtils;
import com.app.itap.utils.Constant;
import com.app.itap.utils.EmptyStateManager;
import com.app.itap.utils.SpacingItemDecoration;
import com.app.itap.utils.Utility;
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

*/
/**
 * Created by poonam on 5/2/19.
 *//*


public class PresenterLiveActivity extends AppCompatActivity {
    RecyclerView rvLiveSession;
    KRecyclerViewAdapter adapter;
    LiveNowData liveNowData;
    EmptyStateManager emptyState;

    ArrayList<LiveNowData> arrayListLiveNowData = new ArrayList<>();

    // Pagination
    private static final int GRID_SPAN = 1;
    private boolean loading = false;
    private int page = 0;
    private Handler handler;
    private Paginate paginate;
    protected long networkDelay = 2000;
    protected boolean customLoadingListItem = false;
    int nextPageNo = 1;
    String liveSessionUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presenter_live);

        init();
    }

    public void init() {
        rvLiveSession = findViewById(R.id.rvLiveSession);
        rvLiveSession.addItemDecoration(new SpacingItemDecoration(Constant.RV_HV_SPACING));
        adapter = new KRecyclerViewAdapter(this, arrayListLiveNowData, (parent, i) -> {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_live_session, parent, false);
            return new PresenterLiveHolder(layoutView);
        }, (kRecyclerViewHolder, o, i) -> startLiveSession(o));
        rvLiveSession.setAdapter(adapter);
        setUpEmptyState();
        initializePagination();

    }

    private void callPresenterSessionAPI() {
        try {
            Map<String, String> params = new HashMap<>();
            APIRequest apiRequest = new APIRequest(liveSessionUrl + "" + nextPageNo, Request.Method.GET, params, null, this);
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, (response, error, headers, statusCode) -> {
                handlePresenterSessionAPIResponse(response, error);

            });
        } catch (Exception e) {

        }
    }

    private void handlePresenterSessionAPIResponse(@Nullable String response, @Nullable Exception error) {
        try {
            if (error != null) {
                updateEmptyState(error.getMessage());
            } else {
                if (response != null) {
                    JSONObject jsonObject = new JSONObject(response);
                    String type = jsonObject.has("type") ? jsonObject.getString("type") : "";
                    if (type.equalsIgnoreCase("error")) {
                        String message = jsonObject.has("msg") ? jsonObject.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                        showError(message);
                    } else if (type.equalsIgnoreCase("ok")) {
                        JSONObject jsonObjectMessage = jsonObject.getJSONObject("msg");
                        JSONArray jsonArrayContent = jsonObjectMessage.getJSONArray("contents");
                        nextPageNo = jsonObjectMessage.has("next_page") ? jsonObjectMessage.getInt("next_page") : 0;
                        if (jsonArrayContent.length() > 0) {
                            new Thread(() -> {
                                try {
                                    for (int i = 0; i < jsonArrayContent.length(); i++) {
                                        liveNowData = new LiveNowData(jsonArrayContent.getJSONObject(i));
                                        arrayListLiveNowData.add(liveNowData);
                                    }
                                    new Handler(Looper.getMainLooper()).post(() -> updateUI());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }).start();
                        } else {
                            updateEmptyState(null);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            updateEmptyState(e.getMessage());
        }
    }

    private void updateUI() {
        adapter.notifyDataSetChanged();
        loading = false;
        page++;
    }

    private void startLiveSession(Object object) {
        if (object instanceof LiveNowData) {
            LiveNowData liveNowData = (LiveNowData) object;
            int id = liveNowData.id;
            Bundle bundle = new Bundle();
            bundle.putParcelable(LiveNowStartActivity.LIVE_SESSION_DATA, liveNowData);
            startActivity(new Intent(this, LiveNowStartActivity.class).putExtra("Bundle", bundle));

            //startActivity(new Intent(this, PresenterLiveStartActivity.class).putExtra(PresenterLiveStartActivity.LIVE_ID, id));
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
        AlertUtils.showAlert(getString(R.string.label_error), errorMessage, null, this, null);
    }

    */
/**
     * Initialization of Empty State
     *//*

    void setUpEmptyState() {
        emptyState = EmptyStateManager.setUpInActivity(this, action -> {
            if (action.equals(EmptyStateManager.ACTION_RETRY)) {
                callPresenterSessionAPI();
            }
        });
        emptyState.hide();

    }

    */
/**
     * Update of Empty State
     *//*

    private void updateEmptyState(String error) {
        if (error == null) {
            if (arrayListLiveNowData.isEmpty()) {
                emptyState.setImgAndMsg("No Live Session found", null);
                rvLiveSession.setVisibility(View.GONE);
            } else {
                rvLiveSession.setVisibility(View.VISIBLE);
                emptyState.hide();
            }
        } else {
            rvLiveSession.setVisibility(View.GONE);
            if (Utility.isConnectingToInternet(this)) {
                emptyState.setMessage(APIManager.GENERIC_API_ERROR_MESSAGE);
            } else {
                emptyState.showNoConnectionState();
            }
        }

    }


    */
/************************ PAGINATION METHODS -- START **********************//*


    public void initializePagination() {
        arrayListLiveNowData.clear();
        adapter.notifyDataSetChanged();
        liveSessionUrl = Url.GET_PRESENTER_SESSION;

        customLoadingListItem = false;

        if (paginate != null) {
            paginate.unbind();
        }
        loading = false;
        page = 0;
        nextPageNo = 1;
        handler = new Handler();
        handler.removeCallbacks(fakeCallback);
        paginate = Paginate.with(rvLiveSession, callbacks)
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
                liveSessionUrl = liveSessionUrl;

            }
            if (nextPageNo != 0) {
                callPresenterSessionAPI();
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
            vh.tvLoading.setText(String.format("Total items loaded: %d.\nLoading more...", adapter.getItemCount()));

            // This is how you can make full span if you are using StaggeredGridLayoutManager
            if (rvLiveSession.getLayoutManager() instanceof StaggeredGridLayoutManager) {
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



    @Override
    protected void onDestroy() {
        super.onDestroy();
        APIManager.cancelAllRequests(APIManager.PAGE_NAVIGATION);
        if (paginate != null) {
            paginate.unbind();
        }
        if (handler != null) {
            handler.removeCallbacks(fakeCallback);
        }
    }
}
*/
