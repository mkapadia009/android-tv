/*
package com.app.itap.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.app.itap.API.APIManager;
import com.app.itap.API.APIRequest;
import com.app.itap.API.APIResponse;
import com.app.itap.API.Url;
import com.app.itap.R;
import com.app.itap.activity.LiveActivity;
import com.app.itap.activity.LiveNowStartActivity;
import com.app.itap.activity.LiveStreamActivity;
import com.app.itap.holder.LiveNowViewHolder;
import com.app.itap.structure.LiveNowData;
import com.app.itap.structure.User;
import com.app.itap.utils.AlertUtils;
import com.app.itap.utils.Constant;
import com.app.itap.utils.EmptyStateManager;
import com.app.itap.utils.LocalStorage;
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

import static com.app.itap.utils.Constant.ERROR_MSG_EMPTY_NUMBER_FILED;

public class LiveNowFragment extends Fragment {

    View view;
    RecyclerView liveNowRecyclerView;
    KRecyclerViewAdapter adapter;
    ArrayList<LiveNowData> liveModuleDataList = new ArrayList<>();

    String liveNowUrl = "";

    ProgressBar progressBar;

    EmptyStateManager emptyState;
    SwipeRefreshLayout swipeRefreshLayout;

    Button seeUpcoming;

    //Pagination
    private static final int GRID_SPAN = 1;
    private boolean loading = false;
    private int page = 0;
    private Handler handler;
    private Paginate paginate;
    protected long networkDelay = 2000;
    protected boolean customLoadingListItem = false;
    int nextPageNo = 1;

    boolean isLive = true;

    public LiveNowFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_live_now, container, false);
        init();
        return view;

    }

    private void init() {
        ((LiveActivity) getActivity()).setToolbar(true);
        ((LiveActivity) getActivity()).showToolbarBackButton(R.drawable.white_arrow);
        ((LiveActivity) getActivity()).showToolbarTitle(true);
        //((LiveActivity) getActivity()).setToolbarTitle(getString(R.string.label_notification));
        ((LiveActivity) getActivity()).setCustomizedTitle(0);

        liveNowRecyclerView = view.findViewById(R.id.live_now_recycler_view);
        swipeRefreshLayout = view.findViewById(R.id.swipeToRefreshLiveNow);
        progressBar = view.findViewById(R.id.progressBar);
        seeUpcoming = view.findViewById(R.id.see_upcoming_button);

        progressBar.setVisibility(View.VISIBLE);
        liveNowRecyclerView.setVisibility(View.INVISIBLE);
        seeUpcoming.setVisibility(View.INVISIBLE);

        setUpEmptyState();
        setUpRecyclerView();

        //swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.dk_cyan));
        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(true);
            //setColorSchemeResources();
            liveModuleDataList.clear();
            getLiveNowAPI(1);
        });

        seeUpcoming.setOnClickListener(view1 -> {
            TabLayout tabhost = (TabLayout) getActivity().findViewById(R.id.tabs);
            tabhost.getTabAt(1).select();
        });
    }

    private void setUpRecyclerView() {
        liveNowRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        //SeparatorDecoration decoration = new SeparatorDecoration(getContext(), getResources().getColor(R.color.black), 0.5f);
        //liveNowRecyclerView.addItemDecoration(decoration);
        adapter = new KRecyclerViewAdapter(getContext(), liveModuleDataList, new KRecyclerViewHolderCallBack() {
            @Override
            public KRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_live_session, viewGroup, false);
                return new LiveNowViewHolder(view, isLive);
            }
        }, (kRecyclerViewHolder, o, i) -> {
            if (o != null) {
                User user = LocalStorage.getUserDisplayName();
                if (user.displayName.matches("[0-9]+") && user.displayName.length() > 5) {
                    setProfileNameDialog(getActivity());
                } else {
                    startLiveSession(o);
                }
            }
        });
        liveNowRecyclerView.setAdapter(adapter);
        initializePagination();
    }

    public void setProfileNameDialog(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_set_name, null);

        EditText edit_text_name = (EditText) view.findViewById(R.id.dialog_editText_name);
        Button btn_set = (Button) view.findViewById(R.id.btn_set);
        final Dialog alertDialogBuilder = new Dialog(context);
        alertDialogBuilder.setContentView(view);
        alertDialogBuilder.show();

        btn_set.setOnClickListener(view1 -> {
            String name = edit_text_name.getText().toString();
            if (name.equals("")) {
                showError(ERROR_MSG_EMPTY_NUMBER_FILED);
                return;
            }
            updateProfileAPI(name);
            alertDialogBuilder.dismiss();
        });
    }

    */
/**
     * Update Profile Using API call
     *
     * @param username
     *//*

    private void updateProfileAPI(String username) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("display_name", username);
            //params.put("image", fileBase64);
            Log.d("params", params.toString());
            APIRequest apiRequest = new APIRequest(Url.UPDATE_PROFILE, Request.Method.POST, params, null, getActivity());
            apiRequest.showLoader = true;
            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    handleUpdateProfileResponse(response, error);
                }
            });
        } catch (Exception e) {

        }
    }

    */
/**
     * This method handles Update Profile
     *
     * @param response api response. Can be null
     * @param error    error message in case of error. Can be null
     *//*

    private void handleUpdateProfileResponse(String response, Exception error) {
        try {
            if (error != null) {
                showError(error.getMessage());
            } else {
                if (response != null) {
                    Log.e("response", response);
                    JSONObject jsonObjectResponse = new JSONObject(response);
                    String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                    if (type.equalsIgnoreCase("error")) {
                        String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                        showError(message);
                    } else if (type.equalsIgnoreCase("ok")) {
                        JSONObject jsonObject = jsonObjectResponse.has("msg") ? jsonObjectResponse.getJSONObject("msg") : new JSONObject();
                        if (jsonObject.length() > 0) {
                            JSONObject jsonObjectuser = jsonObject.has("user") ? jsonObject.getJSONObject("user") : new JSONObject();
                            if (jsonObjectuser.length() > 0) {
                                User user = new User(jsonObjectuser);
                                LocalStorage.setUserData(user);
                                getFragmentManager().popBackStack();
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {

        }
    }

    */
/************************ PAGINATION METHODS -- START **********************//*

    public void initializePagination() {
        liveModuleDataList.clear();
        adapter.notifyDataSetChanged();
        liveNowUrl = Url.GET_LIVE_NOW_FEED + "&page_no=";
        customLoadingListItem = false;
        if (paginate != null) {
            paginate.unbind();
        }
        loading = false;
        page = 0;
        handler = new Handler();
        handler.removeCallbacks(fakeCallback);
        paginate = Paginate.with(liveNowRecyclerView, callbacks)
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
                liveNowUrl = liveNowUrl;

            }
            if (nextPageNo != 0) {
                getLiveNowAPI(nextPageNo);
            }

        }
    };

    private class CustomLoadingListItemCreator implements LoadingListItemCreator {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.custom_loading_list_item, parent, false);
            return new LiveNowFragment.VH(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            LiveNowFragment.VH vh = (LiveNowFragment.VH) holder;
            vh.tvLoading.setText(String.format("Total items loaded: %d.\nLoading more...", adapter.getItemCount()));

            // This is how you can make full span if you are using StaggeredGridLayoutManager
            if (liveNowRecyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
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

    private void startLiveSession(Object object) {
        if (object instanceof LiveNowData) {
            LiveNowData liveNowData = (LiveNowData) object;

            Bundle bundle = new Bundle();
            bundle.putParcelable(LiveNowStartActivity.LIVE_SESSION_DATA, liveNowData);
            startActivity(new Intent(getActivity(), LiveStreamActivity.class).putExtra("Bundle", bundle));
        }
    }

    */
/**
     * Initialization of Empty State
     *//*

    void setUpEmptyState() {
        emptyState = EmptyStateManager.setUpInFragment(view, (AppCompatActivity) getActivity(), action -> {
            if (action.equals(EmptyStateManager.ACTION_RETRY)) {
                getLiveNowAPI(nextPageNo);
            }
        });
    }

    private void getLiveNowAPI(int nextPageNo) {
        Map<String, String> params = new HashMap<>();
        Log.d("LiveNowUrl", liveNowUrl);
        APIRequest apiRequest = new APIRequest(liveNowUrl + nextPageNo, Request.Method.GET, params, null, getContext());
        apiRequest.showLoader = false;
        APIManager.request(apiRequest, new APIResponse() {
            @Override
            public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                progressBar.setVisibility(View.GONE);
                liveNowRecyclerView.setVisibility(View.VISIBLE);
                handleLiveNowFeedResponse(response, error, statusCode);
            }
        });
    }

    private void handleLiveNowFeedResponse(String response, Exception error, int statusCode) {
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
                            LiveNowData liveNowData = new LiveNowData(jsonArrayContents.getJSONObject(i));
                            liveModuleDataList.add(liveNowData);
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
            if (liveModuleDataList.isEmpty()) {
                //emptyState.setImgAndMsg("No Live Sessions Found.", null);
                emptyState.setSecondaryMsg(Constant.SWIPE_DOWN_MSG);
                liveNowRecyclerView.setVisibility(View.INVISIBLE);
                seeUpcoming.setVisibility(View.VISIBLE);
            } else {
                liveNowRecyclerView.setVisibility(View.VISIBLE);
                emptyState.hide();
            }
        } else {
            liveNowRecyclerView.setVisibility(View.INVISIBLE);
            seeUpcoming.setVisibility(View.VISIBLE);
            if (Utility.isConnectingToInternet(getContext())) {
                emptyState.setMessage(APIManager.GENERIC_API_ERROR_MESSAGE);
                emptyState.setSecondaryMsg(Constant.SWIPE_DOWN_MSG);
            } else {
                emptyState.showNoConnectionState();
            }
        }
    }
}*/
