package com.app.itaptv.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.app.itaptv.API.APIManager;
import com.app.itaptv.API.APIRequest;
import com.app.itaptv.API.APIResponse;
import com.app.itaptv.API.Url;
import com.app.itaptv.R;
import com.app.itaptv.adapter.WatchListAdaptor;
import com.app.itaptv.structure.FeedContentData;
import com.app.itaptv.utils.EmptyStateManager;
import com.app.itaptv.utils.LocalStorage;
import com.app.itaptv.utils.Log;
import com.app.itaptv.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyWatchList extends BaseActivity {

    WatchListAdaptor watchListAdaptor = null;
    public static String CONTENT_DATA = "contentData";
    public static int REQUEST_CODE = 2;
    public static String KEY_ACTION_TYPE = "actionType";
    public static String KEY_CONTENT_DATA = "contentData";
    public static final String VIEW_PURCHASES = "viewPurchases";
    public static final String PLAY_NOW = "playNow";

    ArrayList<FeedContentData> watchList = new ArrayList<FeedContentData>();

    private RecyclerView rvWatchList;
    static private int currentPageIndex = 1;
    private boolean isNetworkBussy = false;
    private boolean noMorePages = false;
    private int lastVisibleIndex = 0;
    LinearLayoutManager layoutManager;

    ProgressBar progressBar;
    EmptyStateManager emptyState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_watch_list);
        setTheme(R.style.Theme_Leanback);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        Log.e("avinash", "TOKEN = " + LocalStorage.getToken());

        init();
        loadWatchList();
    }

    private void loadWatchList() {
        Log.e("avinash", "loadWatchList postId = ");
        try {
            Map<String, String> params = new HashMap<>();
            String url = Url.GET_WATCHLIST + "&page_no=" + String.valueOf(currentPageIndex);
            Log.e("avinash", "loadWatchList URL = " + url);
            APIRequest apiRequest = new APIRequest(url, Request.Method.GET, null, null, this);
            apiRequest.showLoader = false;
            isNetworkBussy = true;
            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    isNetworkBussy = false;
                    JSONObject jsonObjectResponse = null;
                    progressBar.setVisibility(View.GONE);
                    rvWatchList.setVisibility(View.VISIBLE);
                    try {
                        if (error != null) {
                            // showError(error.getMessage());
                            updateEmptyState(error.getMessage());
                        } else {
                            if (response != null) {
                                jsonObjectResponse = new JSONObject(response);
                                String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                                if (type.equalsIgnoreCase("error")) {
                                    String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                                    Utility.showError(message, MyWatchList.this);
                                } else if (type.equalsIgnoreCase("ok")) {

                                    JSONArray itemList = jsonObjectResponse.getJSONObject("msg").getJSONArray("contents");
                                    if (itemList.length() > 0) {
                                        for (int i = 0; i < itemList.length(); i++) {
                                            FeedContentData feedContentData = new FeedContentData(itemList.getJSONObject(i), -1);
                                            watchList.add(feedContentData);
                                            Log.e("avinash", "RESPONSE " + i + " = " + String.valueOf(itemList.get(i)));
                                        }
                                        Log.e("avinash", "loadWatchList Response count = " + String.valueOf(watchList.size()));
                                        rvWatchList.setLayoutManager(layoutManager);
                                        if (watchListAdaptor == null) {
                                            watchListAdaptor = new WatchListAdaptor(watchList, MyWatchList.this);
                                            rvWatchList.setAdapter(watchListAdaptor);
                                        } else {
                                            watchListAdaptor.updateWatchListAdaptor(watchList);
                                            watchListAdaptor.notifyDataSetChanged();
                                            rvWatchList.scrollToPosition(lastVisibleIndex + 1);
                                        }
                                        if (jsonObjectResponse.getJSONObject("msg").getInt("next_page") != 0) {
                                            currentPageIndex = currentPageIndex + 1;
                                        } else {
                                            noMorePages = true;
                                        }
                                        updateEmptyState(null);
                                    } else {
                                        updateEmptyState(null);
                                    }
                                }
                            }
                        }
                    } catch (JSONException e) {
                        if (error != null) {
                            updateEmptyState(error.getMessage());
                        }
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            isNetworkBussy = false;
            Utility.showError(getString(R.string.failed_to_comment), MyWatchList.this);
        }
    }

    private void init() {
        setToolbar(true);
        showToolbarBackButton(R.drawable.white_arrow);
        showToolbarTitle(true);
        setCustomizedTitle(0);
        rvWatchList = findViewById(R.id.rvMyWatchList);
        layoutManager = new LinearLayoutManager(MyWatchList.this);
        setToolbarTitle(getString(R.string.label_my_watchlist));
        setUpEmptyState();
        progressBar = findViewById(R.id.progressBar);

        progressBar.setVisibility(View.VISIBLE);
        rvWatchList.setVisibility(View.INVISIBLE);

        rvWatchList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                lastVisibleIndex = layoutManager.findLastVisibleItemPosition();
                int totalItemCount = layoutManager.getItemCount();
                if (lastVisibleIndex - 2 == watchList.size() - 3 && !isNetworkBussy) {
                    isNetworkBussy = true;
                    if (!noMorePages) {
                        loadWatchList();
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == MyWatchList.REQUEST_CODE) {
                Log.e("avinash", "MyWatchList.REQUEST_CODE");
                if (data != null) {
                    Log.e("avinash", "MyWatchList data != null");
                    String actionType = data.getStringExtra(MyWatchList.KEY_ACTION_TYPE);
                    switch (actionType) {
                        case MyWatchList.PLAY_NOW:
                            Log.e("avinash", "actionType actionType PLAY_NOW");
                            FeedContentData feedContentData = data.getParcelableExtra(MyWatchList.KEY_CONTENT_DATA);
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra(KEY_ACTION_TYPE, PLAY_NOW);
                            returnIntent.putExtra(KEY_CONTENT_DATA, feedContentData);
                            setResult(Activity.RESULT_OK, returnIntent);
                            finish();
                            break;
                    }
                }
            }
        }

    }

    /**
     * Initialization of Empty State
     */
    void setUpEmptyState() {
        emptyState = EmptyStateManager.setUpInActivity(this, new EmptyStateManager.ActionBtnCallBack() {
            @Override
            public void onBtnClick(String action) {
                if (action.equals(EmptyStateManager.ACTION_RETRY)) {
                    loadWatchList();
                }
            }
        });
    }

    /**
     * Update of Empty State
     */
    private void updateEmptyState(String error) {
        if (error == null) {
            if (watchList.isEmpty()) {
                emptyState.setImgAndMsg(getString(R.string.no_data_found), null);
                rvWatchList.setVisibility(View.INVISIBLE);
            } else {
                rvWatchList.setVisibility(View.VISIBLE);
                emptyState.hide();
            }
        } else {
            rvWatchList.setVisibility(View.INVISIBLE);
            if (Utility.isConnectingToInternet(this)) {
                emptyState.setMessage(APIManager.GENERIC_API_ERROR_MESSAGE);
            } else {
                emptyState.showNoConnectionState();
            }
        }
    }
}