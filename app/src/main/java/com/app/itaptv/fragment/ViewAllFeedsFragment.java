package com.app.itaptv.fragment;

import static com.app.itaptv.structure.FeedContentData.POST_TYPE_AD;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.android.volley.Request;
import com.app.itaptv.API.APIManager;
import com.app.itaptv.API.APIRequest;
import com.app.itaptv.API.APIResponse;
import com.app.itaptv.API.Url;
import com.app.itaptv.R;
import com.app.itaptv.activity.BuyDetailActivity;
import com.app.itaptv.activity.HomeActivity;
import com.app.itaptv.activity.TournamentRegistrationFormActivity;
import com.app.itaptv.activity.TournamentSummaryActivity;
import com.app.itaptv.custom_interface.OnPlayerListener;
import com.app.itaptv.holder.FeedViewAllHolder;
import com.app.itaptv.structure.FeedContentData;
import com.app.itaptv.structure.SubscriptionDetails;
import com.app.itaptv.utils.AlertUtils;
import com.app.itaptv.utils.Analyticals;
import com.app.itaptv.utils.Constant;
import com.app.itaptv.utils.EmptyStateManager;
import com.app.itaptv.utils.LocalStorage;
import com.app.itaptv.utils.Log;
import com.app.itaptv.utils.Utility;
import com.kalpesh.krecycleradapter.KRecyclerViewAdapter;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemCreator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ViewAllFeedsFragment extends Fragment {
    View view;

    RecyclerView rvContent;
    int Next_Page_N0 = 1;
    ArrayList<FeedContentData> arrayListFeedContent;
    ArrayList<FeedContentData> arrayListPlayContent;
    ArrayList<FeedContentData> arrayListChannelFeedContentData = new ArrayList<>();
    KRecyclerViewAdapter adapter;
    OnPlayerListener onPlayerListener;

    String Feed_list_next_page_url;
    int Feed_ID;
    String FeedPage;

    private static final String Title = "title";
    private static final String PageName = "pagename";
    private static final String ID = "id";
    private static final String Subcategory = "subcategory";
    private static final String TileShape = "tileShape";
    private static final String FeedType = "feedType";
    private static final String ScreenType = "screenType";
    private String mtitle;
    private String mPageName;
    private int mID;
    private String mSubcategory;
    private String mTileShape;
    private String mFeedType;
    private String mScreenType;

    private static final int GRID_SPAN = 1;
    private boolean loading = false;
    private int page = 0;
    private Handler handler;
    private Paginate paginate;
    protected long networkDelay = 0000;
    protected boolean customLoadingListItem = false;
    EmptyStateManager emptyState;
    HomeActivity activity;
    RelativeLayout llprogressbar;
    ProgressBar progressBar;

    public ViewAllFeedsFragment() {
        arrayListFeedContent = new ArrayList<FeedContentData>();
        arrayListPlayContent = new ArrayList<FeedContentData>();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param id          :Dyanamic pass fragment
     * @param title       :Dyanamic pass page fragment like listen,read,channel,play
     * @param mPageName   :Dyanamic pass Fragment Page Name listen,read,channel
     * @param subcategory :Dyanamic pass if it from subcategory pass subcategory otherwise blank string
     * @return A new instance of fragment ViewAllFeedsFragment.
     */
    public static ViewAllFeedsFragment newInstance(int id, String title, String mPageName, String subcategory, String tileShape, String feedType, String screenType) {
        ViewAllFeedsFragment fragment = new ViewAllFeedsFragment();
        Bundle args = new Bundle();
        args.putInt(ID, id);
        args.putString(Title, title);
        args.putString(PageName, mPageName);
        args.putString(Subcategory, subcategory);
        args.putString(TileShape, tileShape);
        args.putString(FeedType, feedType);
        args.putString(ScreenType, screenType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mtitle = getArguments().getString(Title);
            mPageName = getArguments().getString(PageName);
            mID = getArguments().getInt(ID);
            mSubcategory = getArguments().getString(Subcategory);
            mTileShape = getArguments().getString(TileShape);
            mFeedType = getArguments().getString(FeedType);
            mScreenType = getArguments().getString(ScreenType);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_view_all_feeds, container, false);
            init();
        }
        return view;
    }

    /**
     * Initialization of all views
     */
    private void init() {
        onPlayerListener = (OnPlayerListener) getActivity();
        setHasOptionsMenu(false);
        setUpEmptyState();
        rvContent = view.findViewById(R.id.rvContent);
        llprogressbar = view.findViewById(R.id.llprogressbar);
        progressBar = view.findViewById(R.id.progressBar);
        Feed_ID = mID;
        FeedPage = mPageName;
        progressBar.setVisibility(View.VISIBLE);
        rvContent.setVisibility(View.INVISIBLE);
        //((HomeActivity) getActivity()).toolbarearncoins_button.setVisibility(View.GONE);
        adapter = new KRecyclerViewAdapter(getContext(), arrayListFeedContent, (viewGroup, i) -> {
            View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_feed_view_all, viewGroup, false);
            return new FeedViewAllHolder(layoutView, mTileShape, mFeedType, Constant.screen_type_viewall);
        }, (kRecyclerViewHolder, o, i) -> {

            if (o instanceof FeedContentData) {
                FeedContentData feedContentData = (FeedContentData) o;
                setPlaybackData(feedContentData, i, Feed_ID, FeedPage);
            }
        });
        rvContent.setAdapter(adapter);
        ((SimpleItemAnimator) Objects.requireNonNull(rvContent.getItemAnimator())).setSupportsChangeAnimations(false);
        int spanCount = 3;
        if (Utility.isTelevision()) {
            spanCount = 5;
        } else {
            if (mTileShape.equals("h_rectangle")) {
                spanCount = 2;
            }
        }
        GridLayoutManager manager = new GridLayoutManager(getContext(), spanCount, GridLayoutManager.VERTICAL, false);
        rvContent.setLayoutManager(manager);

        // rvContent.addItemDecoration(new SpacingItemDecoration(Constant.RV_HV_SPACING));

        initializePagination();
    }


    private void setChannelPlaybackData(FeedContentData feedContentData) {
        if (arrayListChannelFeedContentData != null && arrayListChannelFeedContentData.size() > 0) {
            if (getActivity() instanceof HomeActivity) {
                HomeActivity activity = (HomeActivity) getActivity();
                //  activity.playChannel(feedContentData, arrayListChannelFeedContentData, 0, feedContentData.postId);
            }
        }
    }

    private void setPlaybackData(FeedContentData feedContentData, int position, int feed_ID, String feedPage) {
        activity = (HomeActivity) getActivity();
        SubscriptionDetails sd = LocalStorage.getUserSubscriptionDetails();
        if (feedContentData.contentType.equalsIgnoreCase(Constant.eSportsTitle)) {
            getUserRegisteredTournamentInfo(feedContentData.postId);
        }
        if (arrayListFeedContent.get(position).contentOrientation.equalsIgnoreCase("vertical") && !Utility.isTelevision()) {
            activity.openVerticalPlayer(arrayListFeedContent, position);
        } else {
            if (feedContentData.feedTabType.equals(Constant.TAB_BUY) || feedContentData.postExcerpt.contains("paid")) {
                if (sd != null && sd.allow_rental != null) {
                    if (sd.allow_rental.equalsIgnoreCase(Constant.YES)) {
                        playMediaItems(feedContentData);
                    } else {
                        getActivity().startActivityForResult(new Intent(getActivity(), BuyDetailActivity.class).putExtra(BuyDetailActivity.CONTENT_DATA, feedContentData), BuyDetailActivity.REQUEST_CODE);
                    }
                }
            } else {
                if (getActivity() != null && getActivity() instanceof HomeActivity) {
                    if (!feedContentData.postType.equals("")) {
                        switch (feedContentData.postType) {
                            case FeedContentData.POST_TYPE_POST:
                                if (arrayListFeedContent.size() > 0) {
                                    if (arrayListFeedContent.get(feedContentData.feedPosition).feedContentType.equals(FeedContentData.CONTENT_TYPE_ALL) || mSubcategory.equals("categories")) {
                                        ArrayList<FeedContentData> arrayListAllTypeFeedContentData = new ArrayList<>();
                                        arrayListAllTypeFeedContentData.add(feedContentData);
                                        //activity.playItems(arrayListAllTypeFeedContentData, 0, String.valueOf(feed_ID), feedPage, String.valueOf(feedContentData.iswatchlisted));
                                        activity.showAudioSongExpandedView(arrayListAllTypeFeedContentData, 0, String.valueOf(feed_ID), feedPage, String.valueOf(feedContentData.iswatchlisted));
                                        return;
                                    }

                                    //activity.playItems(arrayListFeedContent, position, "", feedPage, "");

                                    activity.showAudioSongExpandedView(arrayListFeedContent, position, String.valueOf(feed_ID), feedPage, String.valueOf(arrayListFeedContent.get(position).iswatchlisted));
                                }
                                break;

                            case FeedContentData.POST_TYPE_ORIGINALS:
                                activity.showSeriesExpandedView(feedContentData.postId, "0", "0", String.valueOf(feedContentData.iswatchlisted));
                                break;

                            case FeedContentData.POST_TYPE_EPISODE:
                                activity.showSeriesExpandedView(feedContentData.seriesId, feedContentData.seasonId, feedContentData.episodeId, "");
                                break;

                            case FeedContentData.POST_TYPE_PLAYLIST:
                                activity.showPlaylistExpandedView(feedContentData.postId, String.valueOf(feedContentData.iswatchlisted));
                                break;

                            case FeedContentData.POST_TYPE_STREAM:
                                //playChannelAPI(feedContentData, 1);
                                activity.showChannelExpandedView(feedContentData, 1, String.valueOf(Feed_ID), Analyticals.CONTEXT_CHANNEL);
                                break;
                            case POST_TYPE_AD:
                                if (arrayListFeedContent.size() > 0) {
                                    if (arrayListFeedContent.get(feedContentData.feedPosition).feedContentType.equals(FeedContentData.CONTENT_TYPE_ALL) || mSubcategory.equals("categories")) {
                                        ArrayList<FeedContentData> arrayListAllTypeFeedContentData = new ArrayList<>();
                                        arrayListAllTypeFeedContentData.add(feedContentData);
                                        activity.showAudioSongExpandedView(arrayListAllTypeFeedContentData, 0, String.valueOf(feed_ID), feedPage, String.valueOf(feedContentData.iswatchlisted));
                                        return;
                                    }
                                    activity.showAudioSongExpandedView(arrayListFeedContent, position, String.valueOf(feed_ID), feedPage, String.valueOf(arrayListFeedContent.get(position).iswatchlisted));
                                }
                                break;
                        }
                    } else {
                        switch (feedContentData.taxonomy) {
                            case FeedContentData.TAXONOMY_SEASONS:
                                //activity.showSeriesExpandedView(feedContentData.seriesId, feedContentData.seasonId, feedContentData.episodeId);
                                activity.showSeriesExpandedView(feedContentData.seriesId, feedContentData.seasonId, feedContentData.episodeId, "");
                                break;

                            case FeedContentData.TAXONOMY_CATEGORY:
                                ChannelCategoryFragment channelcategoryFragment = ChannelCategoryFragment.newInstance("category", "listen", feedContentData.termId, feedContentData.name);
                                openFragment(channelcategoryFragment);
                                break;
                        }
                    }
                }
            }
        }
    }

    public void initializePagination() {
        Log.e("test", "initializePagination");
        arrayListFeedContent.clear();
        adapter.notifyDataSetChanged();
        if (mSubcategory.equalsIgnoreCase("feeds")) {
            Feed_list_next_page_url = Url.GET_FEEDS + FeedPage + "&ID=" + Feed_ID + "&tab_screen=" + mScreenType + "&page_no=";
        } else {
            Feed_list_next_page_url = Url.GET_CATEGORY + FeedPage + "&ID=" + Feed_ID + "&tab_screen=" + mScreenType + "&page_no=";
        }
        customLoadingListItem = false;
        if (paginate != null) {
            paginate.unbind();
        }
        loading = false;
        page = 0;
        handler = new Handler();
        handler.removeCallbacks(fakeCallback);
        paginate = Paginate.with(rvContent, callbacks)
                .setLoadingTriggerThreshold(0)
                .addLoadingListItem(true)
                .setLoadingListItemCreator(customLoadingListItem ? new CustomLoadingListItemCreator() : null)
                .setLoadingListItemSpanSizeLookup(() -> GRID_SPAN)
                .build();
    }

    //Pagination Code
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
            return Next_Page_N0 == 0; // If all pages are loaded return true
        }
    };

    private Runnable fakeCallback = new Runnable() {
        @Override
        public void run() {
            //  page++;
            // loading = false;
            if (page > 0) {
                Feed_list_next_page_url = Feed_list_next_page_url;
            }
            Log.e("test", "fakeCallback");
            if (Next_Page_N0 != 0) {
                getViewAllFeed(Next_Page_N0);
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

        @SuppressLint("DefaultLocale")
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            VH vh = (VH) holder;
            vh.tvLoading.setText(String.format(getString(R.string.total_items_loaded), adapter.getItemCount()));

            // This is how you can make full span if you are using StaggeredGridLayoutManager
            if (rvContent.getLayoutManager() instanceof StaggeredGridLayoutManager) {
                StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) vh.itemView.getLayoutParams();
                params.setFullSpan(true);
            }
        }
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvLoading;

        VH(View itemView) {
            super(itemView);
            tvLoading = itemView.findViewById(R.id.tv_loading_text);
        }
    }

    private void getViewAllFeed(int pageno) {
        try {
            Map<String, String> params = new HashMap<>();
            String mUrl;
            if (mSubcategory.equalsIgnoreCase("feeds")) {
                mUrl = Feed_list_next_page_url + pageno;
            } else {
                mUrl = Feed_list_next_page_url + pageno + "&onlyposts=true";
            }
            Log.i("Feed", mUrl);

            APIRequest apiRequest = new APIRequest(mUrl, Request.Method.GET, params, null, getContext());
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    llprogressbar.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    rvContent.setVisibility(View.VISIBLE);
                    handleFeedResponse(response, error);
                }
            });
        } catch (Exception e) {
            Log.e("", e.toString());
        }
    }

    private void handleFeedResponse(String response, Exception error) {
        //  arrayListFeedContent.clear();
        try {
            if (error != null) {
                // Utility.showError(error.getMessage(),getActivity());
                updateEmptyState(error.getMessage());
            } else {
                if (response != null) {
                    Log.e("viewallresponse", response);

                    JSONObject jsonObjectResponse = new JSONObject(response);
                    String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                    if (type.equalsIgnoreCase("error")) {
                        String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;

                        updateEmptyState(jsonObjectResponse.getString("msg"));
                    } else if (type.equalsIgnoreCase("ok")) {
                        JSONObject jsonObjectMsg = jsonObjectResponse.getJSONObject("msg");
                        String contentType = jsonObjectMsg.has("content_type") ? jsonObjectMsg.getString("content_type") : jsonObjectMsg.has("type") ? jsonObjectMsg.getString("type") : "";
                        String termForm = jsonObjectMsg.has("items_from") ? jsonObjectMsg.getString("items_from") : "";
                        String tabType = jsonObjectMsg.has("tab_type") ? jsonObjectMsg.getString("tab_type") : "";
                        String contentOrientation = jsonObjectMsg.has("content_orientation") ? jsonObjectMsg.getString("content_orientation") : "";
                        boolean isCategoryCoin = jsonObjectMsg.has("isCategoryCoin") ? jsonObjectMsg.getBoolean("isCategoryCoin") : false;
                        int categoryId = jsonObjectMsg.has("ID") ? jsonObjectMsg.getInt("ID") : 0;
                        JSONArray jsonArrayContent = jsonObjectMsg.getJSONArray("contents");
                        Log.i("viewallresponse", contentOrientation);
                        for (int i = 0; i < jsonArrayContent.length(); i++) {
                            FeedContentData feedContentData = new FeedContentData(tabType, contentType, termForm, jsonArrayContent.getJSONObject(i), 0);
                            if (feedContentData.contentOrientation.isEmpty()) {
                                feedContentData.contentOrientation = contentOrientation;
                            }
                            feedContentData.isCategoryCoin = isCategoryCoin;
                            feedContentData.categoryId = categoryId;
                            Log.i("viewallresponse", feedContentData.contentOrientation);
                            arrayListFeedContent.add(feedContentData);
                        }
                        /*if (Next_Page_N0==1) {
                            if (mID == 43415) {
                                FeedContentData feedContentData = new FeedContentData();
                               // feedContentData.imgUrl = Constant.eSportsBannerImage;
                                feedContentData.postTitle =Constant.eSportsTitle;
                                arrayListFeedContent.add(1, feedContentData);
                            }
                        }*/
                        Next_Page_N0 = jsonObjectMsg.getInt("next_page");
                        Collections.shuffle(arrayListFeedContent);

                        adapter.notifyDataSetChanged();
                        loading = false;
                        page++;
                        updateEmptyState(null);
                    }
                }
            }
        } catch (JSONException e) {
            loading = false;
            paginate.unbind();
        }
    }


    /**
     * Initialization of Empty State
     */
    void setUpEmptyState() {
        emptyState = EmptyStateManager.setUpInFragment(view, (AppCompatActivity) getActivity(), new EmptyStateManager.ActionBtnCallBack() {
            @Override
            public void onBtnClick(String action) {
                action.equals(EmptyStateManager.ACTION_RETRY);
            }
        });
    }

    /**
     * Update of Empty State
     */
    private void updateEmptyState(String error) {
        if (error == null) {
            if (arrayListFeedContent.isEmpty()) {
                emptyState.setImgAndMsg(getString(R.string.no_data_found), null);
                rvContent.setVisibility(View.INVISIBLE);
            } else {
                rvContent.setVisibility(View.VISIBLE);
                emptyState.hide();
            }
        } else {
            rvContent.setVisibility(View.INVISIBLE);
            if (Utility.isConnectingToInternet(getContext())) {
                emptyState.setMessage(APIManager.GENERIC_API_ERROR_MESSAGE);
            } else {
                emptyState.showNoConnectionState();
            }
        }

    }

    /**
     * Open specific fragment on BottomNavigationView item selected
     *
     * @param fragment Fragment object to display the Fragment
     */
    private void openFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        ((HomeActivity) getActivity()).toolbarWalletBalance.setVisibility(View.GONE);
        ((HomeActivity) getActivity()).toolbarlive_button.setVisibility(View.GONE);
        ((HomeActivity) getActivity()).toolbarsearch_button.setVisibility(View.VISIBLE);
        ((HomeActivity) getActivity()).toolbarNotificationButton.setVisibility(View.GONE);
        ((HomeActivity) getActivity()).toolbarearncoins_button.setVisibility(View.GONE);
    }

    private void playMediaItems(FeedContentData feedContentData) {
        if (feedContentData != null) {
            String id = feedContentData.postId;

            switch (feedContentData.postType) {
                case FeedContentData.POST_TYPE_ORIGINALS:
                    setSeriesData(id, "0", "0", "false");
                    break;
                case FeedContentData.POST_TYPE_SEASON:
                    setSeriesData(feedContentData.seriesId, feedContentData.seasonId, "0", "false");
                    break;
                case FeedContentData.POST_TYPE_PLAYLIST:
                    setPlaylistData(id);
                    break;
                case FeedContentData.POST_TYPE_POST:
                    //getSingleMediaItem(id);
                    arrayListPlayContent.clear();
                    arrayListPlayContent.add(feedContentData);
                    activity.showAudioSongExpandedView(arrayListPlayContent, feedContentData.feedPosition, feedContentData.postId, Analyticals.CONTEXT_FEED, String.valueOf(feedContentData.iswatchlisted));
                    break;
            }
        }
    }

    private void setSeriesData(String seriesId, String seasonId, String episodeId, String iswatched) {
        activity.showSeriesExpandedView(seriesId, seasonId, episodeId, iswatched);
    }

    private void setPlaylistData(String playlistId) {
        activity.showPlaylistExpandedView(playlistId, "false");
    }

    private void getSingleMediaItem(String id) {
        progressBar.setVisibility(View.VISIBLE);
        try {
            Map<String, String> params = new HashMap<>();
            APIRequest apiRequest = new APIRequest(Url.GET_SINGLE_MEDIA_ITEM + "&ID=" + id,
                    Request.Method.GET, params, null, getContext());
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, (response, error, headers, statusCode) -> {
                progressBar.setVisibility(View.GONE);
                handleSingleMediaItem(response, error);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleSingleMediaItem(@Nullable String response, @Nullable Exception error) {
        try {
            if (error != null) {
                //updateEmptyState(error.getMessage());
            } else {
                if (response != null) {
                    JSONObject jsonObjectResponse = new JSONObject(response);
                    String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                    if (type.equalsIgnoreCase("error")) {
                        String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                        showError(message);
                    } else if (type.equalsIgnoreCase("ok")) {
                        try {
                            JSONObject jsonObjectMsg = jsonObjectResponse.getJSONObject("msg");
                            FeedContentData feedContentData = new FeedContentData(jsonObjectMsg, 0);
                            setSinglePostData(feedContentData, Analyticals.CONTEXT_FEED);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            updateEmptyState(e.getMessage());
        }
    }

    private void setSinglePostData(FeedContentData feedContentData, String contextName) {
        arrayListFeedContent.clear();
        adapter.notifyDataSetChanged();
        arrayListFeedContent.add(feedContentData);
        String id = feedContentData.postId;
        activity.showAudioSongExpandedView(arrayListFeedContent, feedContentData.feedPosition, id, contextName, String.valueOf(feedContentData.iswatchlisted));
    }

    /**
     * This method shows error alert
     *
     * @param errorMessage message to be displayed in alert dialog
     */
    private void showError(@Nullable String errorMessage) {
        if (errorMessage == null) errorMessage = APIManager.GENERIC_API_ERROR_MESSAGE;
        AlertUtils.showAlert(getString(R.string.label_error), errorMessage, null, getContext(), null);
    }

    public void getUserRegisteredTournamentInfo(String postId) {
        try {
            Map<String, String> params = new HashMap<>();
            //params.put("tournament_id",feedContentData.postId);
            APIRequest apiRequest = new APIRequest(Url.GET_USER_REGISTERED_TOURNAMENT_INFO + "&tournament_id=" + postId,
                    Request.Method.GET, params, null, getContext());
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, (response, error, headers, statusCode) -> {
                handleUserRegisteredTournamentInfo(response, error, postId);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleUserRegisteredTournamentInfo(@Nullable String response, @Nullable Exception error, String postId) {
        try {
            if (error != null) {

            } else {
                if (response != null) {
                    JSONObject jsonObjectResponse = new JSONObject(response);
                    String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                    if (type.equalsIgnoreCase("error")) {
                        String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                    } else if (type.equalsIgnoreCase("ok")) {
                        JSONObject jsonArrayMsg = jsonObjectResponse.getJSONObject("msg");
                        JSONArray jsonArrayParticipant = jsonArrayMsg.getJSONArray("participant");
                        if (jsonArrayParticipant.length() > 0) {
                            Intent intent = new Intent(getActivity(), TournamentSummaryActivity.class);
                            //intent.putExtra(TournamentRegistrationFormActivity.CONTENT_DATA, postId);
                            LocalStorage.putValue(postId, LocalStorage.KEY_ESPORTS_ID);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(getActivity(), TournamentRegistrationFormActivity.class);
                            //intent.putExtra(TournamentRegistrationFormActivity.CONTENT_DATA, postId);
                            LocalStorage.putValue(postId, LocalStorage.KEY_ESPORTS_ID);
                            startActivity(intent);
                        }
                    }
                }
            }
        } catch (JSONException e) {

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((HomeActivity) getActivity()).setToolbar(true);
        ((HomeActivity) requireActivity()).showToolbarBackButton(R.drawable.white_arrow);
        ((HomeActivity) getActivity()).showToolbarTitle(true);
        ((HomeActivity) getActivity()).setToolbarTitle(mtitle);
        ((HomeActivity) getActivity()).setCustomizedTitle(0);
    }
}
