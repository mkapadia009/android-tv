package com.app.itaptv.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.android.volley.Request;
import com.app.itaptv.API.APIManager;
import com.app.itaptv.API.APIRequest;
import com.app.itaptv.API.APIResponse;
import com.app.itaptv.API.Url;
import com.app.itaptv.R;
import com.app.itaptv.activity.HomeActivity;
import com.app.itaptv.custom_interface.OnFeedRefreshListener;
import com.app.itaptv.custom_interface.OnPlayerListener;
import com.app.itaptv.holder.FeedHolder;
import com.app.itaptv.holder.ListenHolder;
import com.app.itaptv.structure.FeedContentData;
import com.app.itaptv.structure.FeedData;
import com.app.itaptv.utils.AlertUtils;
import com.app.itaptv.utils.Analyticals;
import com.app.itaptv.utils.Constant;
import com.app.itaptv.utils.EmptyStateManager;
import com.app.itaptv.utils.Log;
import com.app.itaptv.utils.SpacingItemDecoration;
import com.app.itaptv.utils.Utility;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
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

public class HomeListenTabFragment extends Fragment {

    View view;
    /* LinearLayout llFeed;*/
    RecyclerView rvListen;
    ProgressBar progressBar;
    RelativeLayout llprogressbar;
    OnFeedRefreshListener onFeedRefreshListener;
    OnPlayerListener onPlayerListener;
    KRecyclerViewAdapter adapterListen;

    private String tabType = "listen";

    ArrayList<ViewGroup> arrayListListenData = new ArrayList<>();
    EmptyStateManager emptyState;
    ArrayList<FeedData> arrayListFeedData = new ArrayList<>();
    ArrayList<FeedData> arrayListPageWiseFeedData = new ArrayList<>();
    HomeActivity activity;

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
    String feedUrl;
    boolean is_fragment_visible = false;

    public HomeListenTabFragment() {
    }

    public static HomeListenTabFragment newInstance() {
        return new HomeListenTabFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_home_listen_n_buy_tab, container, false);
            init();
        }
        return view;
    }

    /**
     * Initialize data
     */
    private void init() {
        onFeedRefreshListener = (OnFeedRefreshListener) getActivity();
        onPlayerListener = (OnPlayerListener) getActivity();
        progressBar = view.findViewById(R.id.progressBar);
        llprogressbar = view.findViewById(R.id.llprogressbar);
        setListenListingRecyclerView();
    }

    /**
     * Initialize recycler view
     */
    void setListenListingRecyclerView() {
        setUpEmptyState();
        llprogressbar.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        rvListen = view.findViewById(R.id.rvFeed);
        rvListen.setVisibility(View.INVISIBLE);
        adapterListen = new KRecyclerViewAdapter(getContext(), arrayListListenData, new KRecyclerViewHolderCallBack() {
            @Override
            public KRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_linear_item, viewGroup, false);
                return new ListenHolder(layoutView);
            }
        }, (kRecyclerViewHolder, o, i) -> {
        });
        rvListen.setAdapter(adapterListen);
        rvListen.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        initializePagination();
    }

    /**
     * Initialization of Empty State
     */
    void setUpEmptyState() {
        emptyState = EmptyStateManager.setUpInFragment(view, (AppCompatActivity) getActivity(), new EmptyStateManager.ActionBtnCallBack() {
            @Override
            public void onBtnClick(String action) {
                if (action.equals(EmptyStateManager.ACTION_RETRY)) {
                    getFeedsAPI();
                }
            }
        });
    }

    /**
     * Update of Empty State
     */
    private void updateEmptyState(String error) {
        if (error == null) {
            if (arrayListFeedData.isEmpty()) {
                emptyState.setImgAndMsg(getString(R.string.no_data_found), null);
                rvListen.setVisibility(View.INVISIBLE);
            } else {
                rvListen.setVisibility(View.VISIBLE);
                emptyState.hide();
            }
        } else {
            rvListen.setVisibility(View.INVISIBLE);
            if (Utility.isConnectingToInternet(getContext())) {
                emptyState.setMessage(APIManager.GENERIC_API_ERROR_MESSAGE);
            } else {
                emptyState.showNoConnectionState();
            }
        }

    }

    /************************ PAGINATION METHODS -- START **********************/

    public void initializePagination() {
        arrayListListenData.clear();
        adapterListen.notifyDataSetChanged();
        feedUrl = Url.GET_FEEDS;
        customLoadingListItem = false;

        if (paginate != null) {
            paginate.unbind();
        }
        loading = false;
        page = 0;
        handler = new Handler();
        handler.removeCallbacks(fakeCallback);
        paginate = Paginate.with(rvListen, callbacks)
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
                feedUrl = feedUrl;

            }
            if (nextPageNo != 0) {
                getFeedsAPI();
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
            vh.tvLoading.setText(String.format(getString(R.string.total_items_loaded), adapterListen.getItemCount()));

            // This is how you can make full span if you are using StaggeredGridLayoutManager
            if (rvListen.getLayoutManager() instanceof StaggeredGridLayoutManager) {
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
     * Returns feeds data related to currently selected tab
     */
    private void getFeedsAPI() {
        try {
            Map<String, String> params = new HashMap<>();
            APIRequest apiRequest = new APIRequest(Url.GET_FEEDS + tabType + "&f_page_no=" + nextPageNo, Request.Method.GET, params, null, getContext());
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    llprogressbar.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    rvListen.setVisibility(View.VISIBLE);
                    handleFeedResponse(response, error);
                }
            });
        } catch (Exception e) {

        }
    }


    /**
     * This method handles feed response
     *
     * @param response api response. Can be null
     * @param error    error message in case of error. Can be null
     */

    private void handleFeedResponse(@Nullable String response, @Nullable Exception error) {
        //arrayListFeedData.clear();
        try {
            if (error != null) {
                // showError(error.getMessage());
                updateEmptyState(error.getMessage());
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
                        new Thread(() -> {
                            try {
                                currentFeedIndex = currentFeedIndex + arrayListPageWiseFeedData.size();
                                int position = arrayListPageWiseFeedData.size();
                                arrayListPageWiseFeedData.clear();
                                for (int i = 0; i < jsonArrayContents.length(); i++) {
                                    FeedData feedData = new FeedData(jsonArrayContents.getJSONObject(i), position + i);
                                    if (feedData.feedType.equals("buy")) {
                                        arrayListPageWiseFeedData.add(feedData);
                                    }
                                }
                                arrayListFeedData.addAll(currentFeedIndex, arrayListPageWiseFeedData);
                                new Handler(Looper.getMainLooper()).post(() -> updateUI());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }).start();
                    }
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
        if (getContext() != null) {
            if (errorMessage == null) errorMessage = APIManager.GENERIC_API_ERROR_MESSAGE;
            AlertUtils.showAlert(getString(R.string.label_error), errorMessage, null, getContext(), null);
        }
    }

    void showLoader() {
        if (progressBar != null && progressBar.getVisibility() == View.GONE)
            progressBar.setVisibility(View.VISIBLE);
        if (rvListen != null) rvListen.setVisibility(View.GONE);
    }

    void dismissLoader() {
        if (progressBar != null && progressBar.getVisibility() == View.VISIBLE)
            progressBar.setVisibility(View.GONE);
        if (rvListen != null) rvListen.setVisibility(View.VISIBLE);
    }

    /**
     * This method is called from HomeFragment and is called to update the data on tab selection action.
     * It calls the particular method to add a view dynamically depending on the view type
     */
    void updateUI() {
        if (page == 0) {
            if (is_fragment_visible)
                onFeedRefreshListener.refreshData(arrayListFeedData, 0);
        }
        new Thread(() -> {
            for (FeedData feedData1 : arrayListPageWiseFeedData) {
                switch (feedData1.viewType) {
                    case FeedData.VIEW_TYPE_H_LIST:
                        // setHorizontalRecyclerView(position, feedData.title, feedData.arrayListFeedContent, String.valueOf(feedData.id));
                        new Handler(Looper.getMainLooper()).post(() -> setHorizontalRecyclerView(feedData1));

                        break;
                    case FeedData.VIEW_TYPE_AD:
                        new Handler(Looper.getMainLooper()).post(() -> setAdLayout(feedData1.feedContentObjectData));
                        break;
                }

            }
            new Handler(Looper.getMainLooper()).post(() -> {
                adapterListen.notifyDataSetChanged();
                loading = false;
                page++;
            });
        }).start();
        /*   }*/
    }

    void updateSlider(int tabPosition) {
        //  onFeedRefreshListener.refreshData(arrayListFeedData, tabPosition);
    }

    /**
     * This method adds the horizontal recycler view dynamically to the parent layout of the page
     *
     * @param feedData Feed Data
     */
    private void setHorizontalRecyclerView(final FeedData feedData) {
        try {

            View viewContent = View.inflate(getContext(), R.layout.row_horizontal_list, null);

            LinearLayout llHzRow = viewContent.findViewById(R.id.llHzRow);
            TextView tvTitle = viewContent.findViewById(R.id.tvStoreName);
            RecyclerView rvContent = viewContent.findViewById(R.id.rvContent);
            TextView tvViewAll = viewContent.findViewById(R.id.tvViewAll);

            ArrayList<FeedContentData> arrayListFeedContent = feedData.arrayListFeedContent;

            if (arrayListFeedContent.size() == 0) {
                llHzRow.setVisibility(View.GONE);
                return;
            }

            llHzRow.setVisibility(View.VISIBLE);
            tvTitle.setText(feedData.title);
            rvContent.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            rvContent.setNestedScrollingEnabled(false);
            rvContent.addItemDecoration(new SpacingItemDecoration(Constant.RV_HV_SPACING, SpacingItemDecoration.LEFT));
            // rvContent.setId(position);

            KRecyclerViewAdapter adapter = new KRecyclerViewAdapter(getContext(), arrayListFeedContent, (viewGroup, i) -> {
                View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_feed, viewGroup, false);
                return new FeedHolder(layoutView, feedData);
            }, (kRecyclerViewHolder, o, i) -> {
                FeedContentData feedContentData = (FeedContentData) o;
                setPlaybackData(feedContentData, i, String.valueOf(feedData.id), Analyticals.CONTEXT_FEED);
               /* if (feedContentData.ptype == 3) {
                    YouTubeDataSource.getInstance().feedData = feedData;
                    Intent intent = new Intent(getActivity(), YoutubePlayerActivity.class);
                    intent.putExtra(YoutubePlayerActivity.KEY_YOUTUBE_START_INDEX, i);
                    getActivity().startActivity(intent);
                } else {
                    setPlaybackData(feedContentData, i, String.valueOf(feedData.id), Analyticals.CONTEXT_FEED);
                }*/

               /* if (feedContentData != null) {
                    HomeActivity activity = (HomeActivity) getActivity();
                    if (feedContentData.nextSeries != null) {
                        if (activity != null) {
                            if (activity.btNextSeries != null) {
                                activity.btNextSeries.setVisibility(View.VISIBLE);
                                activity.btNextSeries.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        // next series call
                                        if (feedContentData.nextSeries.nextSeriesData.postExcerpt.equals("paid")) {
                                            *//*Intent intent = new Intent(getActivity(), BuyDetailActivity.class);
                                            intent.putExtra(BuyDetailActivity.CONTENT_DATA, feedContentData);
                                            getActivity().startActivity(intent);*//*
                                        } else {
                                            activity.showSeriesExpandedView(
                                                    String.valueOf(feedContentData
                                                            .nextSeries.nextSeriesData.id),
                                                    "0", "0", "");
                                        }
                                    }
                                });
                            }
                        }
                    } else {
                        if (activity != null) {
                            if (activity.btNextSeries != null) {
                                activity.btNextSeries.setVisibility(View.GONE);
                            }
                        }
                    }
                }*/
            });
            rvContent.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            tvViewAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewAllFeedsFragment viewAllFeedsFragment = ViewAllFeedsFragment.newInstance(feedData.id, feedData.title, feedData.tabType, "feeds", feedData.tileShape, feedData.feedType,Constant.SCREEN_HOME);
                    openFragment(viewAllFeedsFragment);
                }
            });
            arrayListListenData.add((ViewGroup) viewContent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setPlaybackData(FeedContentData feedContentData, int position, String feed_id, String pagetype) {
        if (arrayListFeedData.size() > 0) {
            if (getActivity() != null && getActivity() instanceof HomeActivity) {
                activity = (HomeActivity) getActivity();

                if (!feedContentData.postType.equals("")) {
                    switch (feedContentData.postType) {
                        case FeedContentData.POST_TYPE_POST:
                            if (feedContentData.feedPosition >= arrayListFeedData.size()) return;
                            if (arrayListFeedData.get(feedContentData.feedPosition).contentType.equals(FeedContentData.CONTENT_TYPE_ALL)) {
                                ArrayList<FeedContentData> arrayListAllTypeFeedContentData = new ArrayList<>();
                                arrayListAllTypeFeedContentData.add(feedContentData);
                                activity.showAudioSongExpandedView(arrayListAllTypeFeedContentData, 0, feed_id, pagetype, String.valueOf(feedContentData.iswatchlisted));
                                //activity.playItems(arrayListAllTypeFeedContentData, 0, feed_id, pagetype, String.valueOf(feedContentData.iswatchlisted));
                                return;
                            }
                            activity.showAudioSongExpandedView(arrayListFeedData.get(feedContentData.feedPosition).arrayListFeedContent, position, feed_id, pagetype, "");
                            //activity.playItems(arrayListFeedData.get(feedContentData.feedPosition).arrayListFeedContent, position, "", pagetype, "");
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
                            //playChannelAPI(feedContentData, 1, feed_id, pagetype);
                            activity.showChannelExpandedView(feedContentData, 1, feed_id, pagetype);
                            break;

                    }
                } else {
                    switch (feedContentData.taxonomy) {
                        case FeedContentData.TAXONOMY_SEASONS:
                            //activity.showSeriesExpandedView(feedContentData.seriesId, feedContentData.seasonId, feedContentData.episodeId);
                            activity.showSeriesExpandedView(feedContentData.seriesId, feedContentData.seasonId, feedContentData.episodeId, "");
                            break;

                        case FeedContentData.TAXONOMY_CATEGORY:
                            ChannelCategoryFragment channelcategoryFragment = ChannelCategoryFragment.newInstance("category", tabType, feedContentData.termId, feedContentData.name);
                            openFragment(channelcategoryFragment);
                            break;
                    }
                }
            }
        }
    }

    /**
     * This method adds the specific layout of the Ad depending on the ad type dynamically to the parent layout of the page
     *
     * @param feedContentData Ad content data object
     */
    private void setAdLayout(FeedContentData feedContentData) {

        switch (feedContentData.adType) {
            case FeedContentData.AD_TYPE_IN_APP:
                setAdLayoutAudio(feedContentData);
                break;
            case FeedContentData.AD_TYPE_EXTERNAL:
                setAdLayoutImage(feedContentData);
                break;
        }
    }


    /**
     * Sets layout for the Audio Ad to play the audio file
     *
     * @param feedContentData Ad content data object
     */
    private void setAdLayoutAudio(FeedContentData feedContentData) {
        View viewContent = View.inflate(getContext(), R.layout.row_audio, null);


        TextView tvTitle = viewContent.findViewById(R.id.tvStoreName);
        tvTitle.setText(feedContentData.postTitle);

        RelativeLayout rlAudio = viewContent.findViewById(R.id.rlAudio);
        rlAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertUtils.showToast(getString(R.string.play_audio), Toast.LENGTH_SHORT, getContext());
            }
        });
        arrayListListenData.add((ViewGroup) viewContent);
    }


    /**
     * Sets layout for the Ad to display image
     *
     * @param feedContentData Ad content data object
     */
    private void setAdLayoutImage(FeedContentData feedContentData) {

        String imageUrl = feedContentData.imgUrl;

        View viewContent = View.inflate(getContext(), R.layout.row_image, null);


        ImageView ivImage = viewContent.findViewById(R.id.ivImage);
        Glide.with(getContext())
                .load(imageUrl)
                .apply(new RequestOptions().error(R.drawable.no_image_avail).placeholder(R.drawable.no_image_avail).dontAnimate().diskCacheStrategyOf(DiskCacheStrategy.ALL))
                .into(ivImage);
        arrayListListenData.add((ViewGroup) viewContent);
    }


    /**
     * Sets layout for the Ad of the type question
     */
    private void setAdLayoutQuestion() {
        View viewContent = View.inflate(getContext(), R.layout.row_question, null);


        Button btOption4 = viewContent.findViewById(R.id.btOption4);
        btOption4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertUtils.showToast(getString(R.string.option_selected), Toast.LENGTH_SHORT, getContext());
            }
        });
        arrayListListenData.add((ViewGroup) viewContent);
    }


    /**
     * Open specific fragment on BottomNavigationView item selected
     *
     * @param fragment Fragment object to display the Fragment
     */
    private void openFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.d("HomeListenTabFragment", "HomeListenTabFragment" + isVisibleToUser);
        if (isVisibleToUser) {
            is_fragment_visible = true;
            if (onFeedRefreshListener != null)
                updateSlider(0);
        } else {
            is_fragment_visible = false;
        }
    }
}
