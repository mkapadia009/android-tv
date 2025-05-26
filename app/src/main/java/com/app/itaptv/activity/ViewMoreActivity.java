package com.app.itaptv.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.app.itaptv.API.APIManager;
import com.app.itaptv.API.APIRequest;
import com.app.itaptv.API.APIResponse;
import com.app.itaptv.API.Url;
import com.app.itaptv.R;
import com.app.itaptv.adapter.ViewPagerAdapter;
import com.app.itaptv.fragment.EpisodePlaylistTabFragment;
import com.app.itaptv.structure.SeasonData;
import com.app.itaptv.utils.AlertUtils;
import com.app.itaptv.utils.Analyticals;
import com.app.itaptv.utils.EmptyStateManager;
import com.app.itaptv.utils.Utility;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by poonam on 28/11/18.
 */

public class ViewMoreActivity extends BaseActivity implements TabLayout.BaseOnTabSelectedListener {
    public static String CONTEXT_TYPE = "contextType";
    public static String POST_TYPE = "postType";
    public static String SERIES_ID = "seriesId";
    public static String SEASON_ID = "seasonId";
    public static String EPISODE_ID = "episodeId";
    public static String SERIES_NAME = "seriesName";
    public static String SEASON_NAME = "seasonName";
    public static String PLAYLIST_ID = "playlistId";
    public static String PLAYLIST_NAME = "playlistName";
    public static String SELECTED_TAB_INDEX = "selectedTabIndex";
    public static String ISWATCHED = "iswatched";
    public static String ARRAY_FEED_CONTENTS = "arrayListFeedContent";
    public static final int REQUEST_CODE = 10;
    String postType = "";
    String contextType = "";
    String seriesId = "";
    String seasonId = "";
    String seriesName = "";
    String seasonName = "";
    String playlistId = "";
    String playlistName = "";
    String isWatched = "";
    int selectedTabIndex;


    TabLayout tlSeason;
    ViewPager vpSeason;


    EmptyStateManager emptyState;

    ArrayList<SeasonData> arrayListSeasonData = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_more);

        setToolbar(true);
        showToolbarBackButton(R.drawable.back_arrow_white);
        showToolbarTitle(true);
        setCustomizedTitle(14);
        init();
        //new retrieveDataAsyncTask().execute();
    }

    private void init() {
        tlSeason = findViewById(R.id.tlSeason);
        vpSeason = findViewById(R.id.vpSeason);

        // Get extras
        Intent ext = getIntent();
        if (ext != null) {
            setData(ext);
        }


        //setUpEmptyState();
    }

    private void setData(Intent ext) {
        String title = "";
        postType = ext.hasExtra(POST_TYPE) ? ext.getStringExtra(POST_TYPE) : "";
        contextType = ext.hasExtra(CONTEXT_TYPE) ? ext.getStringExtra(CONTEXT_TYPE) : "";
        switch (contextType) {
            case Analyticals.CONTEXT_SERIES:
            case Analyticals.CONTEXT_EPISODE:
                selectedTabIndex = ext.hasExtra(SELECTED_TAB_INDEX) ? ext.getIntExtra(SELECTED_TAB_INDEX, 0) : 0;
                seriesId = ext.hasExtra(SERIES_ID) ? ext.getStringExtra(SERIES_ID) : "";
                seasonId = ext.hasExtra(SEASON_ID) ? ext.getStringExtra(SEASON_ID) : "";
                seriesName = ext.hasExtra(SERIES_NAME) ? ext.getStringExtra(SERIES_NAME) : "";
                seasonName = ext.hasExtra(SEASON_NAME) ? ext.getStringExtra(SEASON_NAME) : "";
                isWatched = ext.hasExtra(ViewMoreActivity.ISWATCHED) ? ext.getStringExtra(ViewMoreActivity.ISWATCHED) : "";
                title = String.format(getString(R.string.view_more_title), seriesName, seasonName);
                getAllSeasons();
                break;
            case Analyticals.CONTEXT_PLAYLIST:
                playlistId = ext.hasExtra(PLAYLIST_ID) ? ext.getStringExtra(PLAYLIST_ID) : "";
                playlistName = ext.hasExtra(PLAYLIST_NAME) ? ext.getStringExtra(PLAYLIST_NAME) : "";
                isWatched = ext.hasExtra(ViewMoreActivity.ISWATCHED) ? ext.getStringExtra(ViewMoreActivity.ISWATCHED) : "";
                title = playlistName;
                setPlaylistData();
                break;
        }
        setPageTitle(title);
    }

    // Set page title
    private void setPageTitle(String title) {
        setTitle(title);
    }

    /**
     * Initialization of Empty State
     */
    void setUpEmptyState() {
        emptyState = EmptyStateManager.setUpInActivity(this, new EmptyStateManager.ActionBtnCallBack() {
            @Override
            public void onBtnClick(String action) {
                if (action.equals(EmptyStateManager.ACTION_RETRY)) {
                    //getEpisodeListing();
                }
            }
        });
    }


    /**
     * Returns season data
     */
    private void getAllSeasons() {
        try {
            Map<String, String> params = new HashMap<>();
            APIRequest apiRequest = new APIRequest(Url.GET_SEASONS
                    + "&series=" + seriesId,
                    Request.Method.GET, params, null, this);
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, new APIResponse() {

                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    handleAllSeasonsResponse(response, error);

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleAllSeasonsResponse(@Nullable String response, @Nullable Exception error) {
        try {
            if (error == null) {
                //updateEmptyState(error.getMessage());
                if (response != null) {
                    JSONObject jsonObjectResponse = new JSONObject(response);
                    String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                    if (type.equalsIgnoreCase("error")) {
                        String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                        showError(message);
                    } else if (type.equalsIgnoreCase("ok")) {
                        try {
                            JSONArray jsonArrayMsg = jsonObjectResponse.getJSONArray("msg");
                            for (int i = 0; i < jsonArrayMsg.length(); i++) {

                                SeasonData seasonData = new SeasonData(jsonArrayMsg.getJSONObject(i));
                                arrayListSeasonData.add(seasonData);
                            }
                            setSeasonData();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            //updateEmptyState(e.getMessage());
        }
    }

    private void setSeasonData() {
        // Set View Pager
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        for (int i = 0; i < arrayListSeasonData.size(); i++) {
            seasonId = String.valueOf(arrayListSeasonData.get(i).termId);
            Fragment tabFragment = EpisodePlaylistTabFragment.getInstance(contextType, postType, seriesId, seasonId, 0, isWatched);
            viewPagerAdapter.addFragment(tabFragment, "Position " + i);
        }
        vpSeason.setOffscreenPageLimit(3);
        vpSeason.setAdapter(viewPagerAdapter);
        tlSeason.setupWithViewPager(vpSeason);
        setSeasonTabText();
        tlSeason.addOnTabSelectedListener(this);
        tlSeason.getTabAt(selectedTabIndex).select();
    }


    private void setPlaylistData() {

        // Set View Pager
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        for (int i = 0; i < 1; i++) {
            Fragment tabFragment = EpisodePlaylistTabFragment.getInstance(contextType, postType, playlistId, "", 0, isWatched);
            viewPagerAdapter.addFragment(tabFragment, "Position " + i);
        }
        vpSeason.setOffscreenPageLimit(1);
        vpSeason.setAdapter(viewPagerAdapter);
        tlSeason.setupWithViewPager(vpSeason);
        setPlaylistTabText();
        tlSeason.addOnTabSelectedListener(this);
    }


    @SuppressLint("ResourceType")
    private void setSeasonTabText() {
        for (int i = 0; i < arrayListSeasonData.size(); i++) {
            TextView tvCustomTab = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_season_tab, null);
            tvCustomTab.setText(arrayListSeasonData.get(i).name);
            tvCustomTab.setTextColor(getResources().getColorStateList(R.drawable.tab_selector));
            if (i == 0)
                tvCustomTab.setTypeface(ResourcesCompat.getFont(this, R.font.rubik_medium));
            tlSeason.getTabAt(i).setCustomView(tvCustomTab);
        }
    }

    @SuppressLint("ResourceType")
    private void setPlaylistTabText() {
        for (int i = 0; i < 1; i++) {
            TextView tvCustomTab = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_season_tab, null);
            tvCustomTab.setText(R.string.playlist);
            tvCustomTab.setTextColor(getResources().getColorStateList(R.drawable.tab_selector));
            if (i == 0)
                tvCustomTab.setTypeface(ResourcesCompat.getFont(this, R.font.rubik_medium));
            tlSeason.getTabAt(i).setCustomView(tvCustomTab);
        }
    }

    /**
     * Update of Empty State
     */
    private void updateEmptyState(String error) {
        if (error == null) {
            if (arrayListSeasonData.isEmpty()) {
                emptyState.setImgAndMsg(getString(R.string.no_data_found), null);
                //llData.setVisibility(View.INVISIBLE);
            } else {
                //llData.setVisibility(View.VISIBLE);
                emptyState.hide();
            }
        } else {
            //llData.setVisibility(View.INVISIBLE);
            if (Utility.isConnectingToInternet(this)) {
                emptyState.setMessage(APIManager.GENERIC_API_ERROR_MESSAGE);
            } else {
                emptyState.showNoConnectionState();
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


    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        TextView tvTabTextView = (TextView) tab.getCustomView();
        if (tvTabTextView != null) {
            tvTabTextView.setTypeface(ResourcesCompat.getFont(this, R.font.rubik_medium));
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        TextView tvTabTextView = (TextView) tab.getCustomView();
        if (tvTabTextView != null) {
            tvTabTextView.setTypeface(ResourcesCompat.getFont(this, R.font.rubik_regular));
        }
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //APIManager.cancelAllRequests(APIManager.PAGE_NAVIGATION);
    }
}
