package com.app.itaptv.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.app.itaptv.API.APIManager;
import com.app.itaptv.API.APIMethods;
import com.app.itaptv.API.APIRequest;
import com.app.itaptv.API.APIResponse;
import com.app.itaptv.API.Url;
import com.app.itaptv.MyApp;
import com.app.itaptv.R;
import com.app.itaptv.activity.BrowserActivity;
import com.app.itaptv.activity.BuyDetailActivity;
import com.app.itaptv.activity.GameStartActivity;
import com.app.itaptv.activity.HomeActivity;
import com.app.itaptv.activity.LuckyWinnerActivity;
import com.app.itaptv.activity.PremiumActivity;
import com.app.itaptv.activity.WebViewActivity;
import com.app.itaptv.adapter.ViewStatePagerAdapter;
import com.app.itaptv.custom_interface.NavigationMenuCallback;
import com.app.itaptv.custom_interface.OnFeedRefreshListener;
import com.app.itaptv.custom_interface.OnPlayerListener;
import com.app.itaptv.custom_interface.SliderListener;
import com.app.itaptv.custom_widget.DotsIndicatorDecoration;
import com.app.itaptv.holder.BigSliderHolder;
import com.app.itaptv.holder.CustomAdSliderHolder;
import com.app.itaptv.holder.FeedCombinedHolder;
import com.app.itaptv.holder.ListenHolder;
import com.app.itaptv.holder.PromotionHolder;
import com.app.itaptv.holder.SliderHolder;
import com.app.itaptv.structure.AdFieldsData;
import com.app.itaptv.structure.AdMobData;
import com.app.itaptv.structure.FeedCombinedData;
import com.app.itaptv.structure.FeedContentData;
import com.app.itaptv.structure.GameData;
import com.app.itaptv.structure.HomeSliderObject;
import com.app.itaptv.structure.HomeTabsList;
import com.app.itaptv.structure.SubscriptionDetails;
import com.app.itaptv.utils.AlertUtils;
import com.app.itaptv.utils.Analyticals;
import com.app.itaptv.utils.Constant;
import com.app.itaptv.utils.CustomLinearLayoutManager;
import com.app.itaptv.utils.EmptyStateManager;
import com.app.itaptv.utils.GameDateValidation;
import com.app.itaptv.utils.LocalStorage;
import com.app.itaptv.utils.Log;
import com.app.itaptv.utils.Utility;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.tabs.TabLayout;
import com.kalpesh.krecycleradapter.Interface.KRecyclerItemClickListener;
import com.kalpesh.krecycleradapter.Interface.KRecyclerViewHolderCallBack;
import com.kalpesh.krecycleradapter.KRecyclerViewAdapter;
import com.kalpesh.krecycleradapter.KRecyclerViewHolder;
import com.paginate.Paginate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.app.itaptv.structure.FeedCombinedData.FEED_TYPE_AD;
import static com.app.itaptv.structure.FeedCombinedData.FEED_TYPE_BUY;
import static com.app.itaptv.structure.FeedCombinedData.FEED_TYPE_PROMOTION;
import static com.app.itaptv.structure.FeedCombinedData.GAME_TAB;
import static com.app.itaptv.structure.FeedCombinedData.NORMAL;
import static com.app.itaptv.structure.FeedCombinedData.PREMIUM_TAB;
import static com.app.itaptv.structure.FeedCombinedData.SUBSCRIPTION_TAB;

public class HomeBuyTabFragment extends Fragment implements BigSliderHolder.EventListener, TabLayout.OnTabSelectedListener {
    public static String TAG = "HomeBuyTabFragment";

    View view;
    ProgressBar progressBar;
    RecyclerView rvBuy;
    RelativeLayout llprogressbar;
    AdView mAdView;
    AdRequest adRequest;
    ConstraintLayout clAdHolder;
    ImageView ivCustomAd;
    ImageView ivClose;
    PlayerView playerView;
    ImageView ivVolumeUp;
    ImageView ivVolumeOff;
    NestedScrollView nsvBuyTabFrag;
    private boolean resumePlayer = false;

    private ExoPlayer player;

    OnFeedRefreshListener onFeedRefreshListener;
    OnPlayerListener onPlayerListener;
    KRecyclerViewAdapter adapter;//, internalAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<KRecyclerViewAdapter> internalAdapters = new ArrayList<>();

    private HomeActivity homeActivity;

    private String tabType = "buy";

    private RecyclerView rvSliderAd;
    private CustomLinearLayoutManager layoutManager;
    private KRecyclerViewAdapter adapterSliderAd;
    private boolean flagDecoration = false;
    private ArrayList<AdFieldsData> adSliderObjectArrayList = new ArrayList<>();

    private String sliderIdAd;
    // for slider
    private Runnable runnable = null;
    private int count = 0;
    private int visibleItemPosition;
    private int sliderPosition = -1;
    private boolean shouldSlide = true;
    private boolean flagAd = false;

    //ArrayList<FeedData> arrayListFeedData = new ArrayList<>();
    ArrayList<ViewGroup> arrayListViewGroup = new ArrayList<>();
    ArrayList<FeedContentData> arrayListFeedContent = new ArrayList<>();

    String sliderId;
    private ArrayList<FeedCombinedData> arrayListFeedData = new ArrayList<>();
    private ArrayList<FeedCombinedData> arrayListPageWiseFeedData = new ArrayList<>();
    private ArrayList<HomeSliderObject> homeSliderObjectArrayList = new ArrayList<>();
    private ArrayList<FeedContentData> arrayListBuyFeedContent = new ArrayList<>();
    private ArrayList<FeedCombinedData> bigSliderObjectArrayList = new ArrayList<>();
    EmptyStateManager emptyState;

    // Pagination
    private static final int GRID_SPAN = 1;
    private boolean loading = false;
    private int page = 0;
    private Handler handler;
    private Paginate paginate;
    protected long networkDelay = 2000;
    int currentFeedIndex = 0;
    protected boolean customLoadingListItem = false;
    int nextPageNo = 1;
    String feedUrl;
    boolean is_fragment_visible = false;
    boolean is_first_time_loading = true;

    public TabLayout tlPremiumTabs;
    ViewPager vPremium;
    ArrayList<HomeTabsList> arrayListTabData = new ArrayList<>();
    ArrayList<HomeSliderTabFragment> ftList = new ArrayList<>();

    NavigationMenuCallback navigationMenuCallback;

    public HomeBuyTabFragment() {
    }

    public static HomeBuyTabFragment newInstance() {
        return new HomeBuyTabFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_home_buy_tab, container, false);
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
        homeActivity = (HomeActivity) getActivity();

        swipeRefreshLayout = view.findViewById(R.id.swipeToRefresh);
        nsvBuyTabFrag = view.findViewById(R.id.nsvBuyTabFrag);

        tlPremiumTabs = view.findViewById(R.id.tlPremiumTabs);
        vPremium = view.findViewById(R.id.vpPremium);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                emptyState.hide();
                nextPageNo = 1;
                currentFeedIndex = 0;
                arrayListBuyFeedContent.clear();
                arrayListViewGroup.clear();
                arrayListFeedData.clear();
                arrayListFeedContent.clear();
                arrayListPageWiseFeedData.clear();
                homeSliderObjectArrayList.clear();
                ftList.clear();
                bigSliderObjectArrayList.clear();

                setListenListingRecyclerView();
                getFeedsAPI();
                //getActiveTabs();
                swipeRefreshLayout.setRefreshing(false);
                swipeRefreshLayout.setEnabled(false);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            nsvBuyTabFrag.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                    if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                        Log.i(TAG, "BOTTOM SCROLL");
                        if (nextPageNo != 0) {
                            getFeedsAPI();
                        }
                    }
                }
            });
        }


        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        adRequest = new AdRequest.Builder().build();

        setListenListingRecyclerView();
        //getFeedsAPI();
    }

    /**
     * Initialize recycler view
     */
    void setListenListingRecyclerView() {
        setUpEmptyState();
        llprogressbar.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        rvBuy = view.findViewById(R.id.rvFeed);
        rvBuy.setVisibility(View.INVISIBLE);
        vPremium.setVisibility(View.GONE);
        //swipeRefreshLayout.setEnabled(false);

        adapter = new KRecyclerViewAdapter(getContext(), arrayListViewGroup, (viewGroup, i) -> {
            View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_linear_item, viewGroup, false);
            return new ListenHolder(layoutView);
        }, new KRecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClicked(KRecyclerViewHolder kRecyclerViewHolder, Object o, int i) {

            }
        });
        rvBuy.setAdapter(adapter);
        rvBuy.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        //initializePagination();
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
                rvBuy.setVisibility(View.INVISIBLE);
            } else {
                rvBuy.setVisibility(View.VISIBLE);
                emptyState.hide();
            }
        } else {
            rvBuy.setVisibility(View.INVISIBLE);
            if (Utility.isConnectingToInternet(getContext())) {
                emptyState.setMessage(APIManager.GENERIC_API_ERROR_MESSAGE);
            } else {
                emptyState.showNoConnectionState();
            }
        }

    }

    /************************ PAGINATION METHODS -- START **********************/
/*
    public void initializePagination() {
        arrayListFeedData.clear();
        adapter.notifyDataSetChanged();
        feedUrl = Url.GET_FEEDS;
        customLoadingListItem = false;

        if (paginate != null) {
            paginate.unbind();
        }
        loading = false;
        page = 0;
        handler = new Handler();
        handler.removeCallbacks(fakeCallback);
        paginate = Paginate.with(rvBuy, callbacks)
                .setLoadingTriggerThreshold(0)
                .addLoadingListItem(true)
                //.setLoadingListItemCreator(customLoadingListItem ? new CustomLoadingListItemCreator() : null)
                *//*.setLoadingListItemSpanSizeLookup(new LoadingListItemSpanLookup() {
                    @Override
                    public int getSpanSize() {
                        return GRID_SPAN;
                    }
                })*//*
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
                //getFeedsAPI();
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
            if (rvBuy.getLayoutManager() instanceof StaggeredGridLayoutManager) {
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
    }*/

    /************************ PAGINATION METHODS -- END **********************/
    /**
     * Returns feeds data related to currently selected tab
     */
    private void getFeedsAPI() {
        //arrayListFeedData.clear();
        try {
            Map<String, String> params = new HashMap<>();
            APIRequest apiRequest = new APIRequest(Url.GET_FEEDS + tabType + "&f_page_no=" + nextPageNo + "&tab_screen=" + Constant.SCREEN_PREMIUM, Request.Method.GET, params, null, getContext());
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    llprogressbar.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    rvBuy.setVisibility(View.VISIBLE);
                    handleFeedResponse(response, error);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
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
        swipeRefreshLayout.setEnabled(true);
        try {
            if (error != null) {
                // showError(error.getMessage());
                updateEmptyState(error.getMessage());
            } else {
                if (response != null) {
                    Log.i(TAG, response);

                    JSONObject jsonObjectResponse = new JSONObject(response);
                    String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                    if (type.equalsIgnoreCase("error")) {
                        String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                        showError(message);
                    } else if (type.equalsIgnoreCase("ok")) {
                        try {
                            JSONObject jsonArrayMsg = jsonObjectResponse.getJSONObject("msg");
                            JSONArray jsonArrayContents = jsonArrayMsg.getJSONArray("contents");
                            nextPageNo = jsonArrayMsg.has("next_page") ? jsonArrayMsg.getInt("next_page") : 0;
                            new Thread(() -> {
                                try {
                                    currentFeedIndex = currentFeedIndex + arrayListPageWiseFeedData.size();
                                    int position = arrayListPageWiseFeedData.size();
                                    arrayListPageWiseFeedData.clear();
                                    for (int i = 0; i < jsonArrayContents.length(); i++) {
                                        FeedCombinedData feedCombinedData = new FeedCombinedData(jsonArrayContents.getJSONObject(i), position + i);
                                        arrayListPageWiseFeedData.add(feedCombinedData);
                                    }
                                    arrayListFeedData.addAll(currentFeedIndex, arrayListPageWiseFeedData);
                                    new Handler(Looper.getMainLooper()).post(() -> updateUI());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                /*try {
                                    for (int i = 0; i < jsonArrayContents.length(); i++) {
                                        FeedData feedData = new FeedData(jsonArrayContents.getJSONObject(i), i);
                                        arrayListFeedData.add(feedData);
                                    }
                                    new Handler(Looper.getMainLooper()).post(() -> updateUI());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }*/
                            }).start();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
        if (errorMessage == null) errorMessage = APIManager.GENERIC_API_ERROR_MESSAGE;
        try {
            AlertUtils.showAlert(getString(R.string.label_error), errorMessage, null, getContext(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void showLoader() {
        if (progressBar != null && progressBar.getVisibility() == View.GONE)
            progressBar.setVisibility(View.VISIBLE);
    }

    void dismissLoader() {
        if (progressBar != null && progressBar.getVisibility() == View.VISIBLE)
            progressBar.setVisibility(View.GONE);
    }

    /**
     * This method is called from HomeFragment and is called to update the data on tab selection action.
     * It calls the particular method to add a view dynamically depending on the view type
     */
    void updateUI() {
        swipeRefreshLayout.setEnabled(true);
        internalAdapters.clear();
       /* if(is_fragment_visible)
            onFeedRefreshListener.refreshData(arrayListFeedData, 2);*/
        new Thread(() -> {
            /*for (FeedData feedData : arrayListFeedData) {
                switch (feedData.viewType) {
                    case FeedData.VIEW_TYPE_H_LIST:
                        new Handler(Looper.getMainLooper()).post(() -> setHorizontalRecyclerView(feedData));
                        break;

                    case FeedData.VIEW_TYPE_AD:
                        new Handler(Looper.getMainLooper()).post(() -> setAdLayout(feedData.feedContentObjectData));
                        break;
                }
            }*/
            for (FeedCombinedData feedCombinedData : arrayListPageWiseFeedData) {
                switch (feedCombinedData.viewType) {
                    case FeedCombinedData.VIEW_TYPE_H_LIST:
                        new Handler(Looper.getMainLooper()).post(() -> setHorizontalRecyclerView(feedCombinedData));
                        ;
                        break;
                    case FeedCombinedData.VIEW_TYPE_SLIDER:
                        new Handler(Looper.getMainLooper()).post(() -> setFeedSliderRecyclerView(feedCombinedData));
                        ;
                        break;
                    case FeedCombinedData.VIEW_TYPE_BIG_SLIDER:
                        new Handler(Looper.getMainLooper()).post(() -> setFeedBigSliderRecyclerView(feedCombinedData));
                        ;
                        break;
                    case FeedCombinedData.VIEW_TYPE_TABS:
                        new Handler(Looper.getMainLooper()).post(() -> handleTabsArray(feedCombinedData.bigSliderObjectArrayList));

                    case FeedCombinedData.VIEW_TYPE_AD:
                        //new Handler(Looper.getMainLooper()).post(() -> setAdLayout(feedCombinedData.feedContentObjectData));
                        break;
                }
            }
            new Handler(Looper.getMainLooper()).post(() -> {
                adapter.notifyDataSetChanged();
                loading = false;
                page++;
            });
        }).start();
    }

    void updateSlider(int tabPosition) {
        //onFeedRefreshListener.refreshData(arrayListFeedData, tabPosition);
    }

    /**
     * This method adds the horizontal recycler view dynamically to the parent layout of the page
     *
     * @param feedCombinedData Feed Combined Data
     */
    private void setHorizontalRecyclerView(final FeedCombinedData feedCombinedData) {
        try {
            View viewContent = View.inflate(getContext(), R.layout.row_horizontal_list, null);
            LinearLayout llHzRow = viewContent.findViewById(R.id.llHzRow);
            TextView tvTitle = viewContent.findViewById(R.id.tvStoreName);
            RecyclerView rvContent = viewContent.findViewById(R.id.rvContent);
            ImageView rvBackImage = viewContent.findViewById(R.id.rvBackImage);
            TextView tvViewAll = viewContent.findViewById(R.id.tvViewAll);
            RelativeLayout rlParentTitle = viewContent.findViewById(R.id.rlParentTitle);
            mAdView = viewContent.findViewById(R.id.adViewFeed);
            clAdHolder = viewContent.findViewById(R.id.cl_ad_holder);
            ivCustomAd = viewContent.findViewById(R.id.ivCustomAd);
            ivClose = viewContent.findViewById(R.id.ivClose);
            playerView = viewContent.findViewById(R.id.playerView);
            ivVolumeUp = viewContent.findViewById(R.id.ivVolumeUp);
            ivVolumeOff = viewContent.findViewById(R.id.ivVolumeOff);
            rvSliderAd = viewContent.findViewById(R.id.rvSliderAd);
            LinearLayoutManager linearLayoutManager;

            Log.d("TAG", feedCombinedData.title);
            Log.d("TAG", String.valueOf(feedCombinedData.id));
            if (String.valueOf(feedCombinedData.id).equals(Constant.getRentFeedId())) {
                String id = getResources().getResourceEntryName(viewContent.findViewById(R.id.adViewFeed).getId());
                //showBannerAd(id);
            }
            //ArrayList<FeedContentData> arrayListFeedContent = feedData.arrayListFeedContent;

            arrayListBuyFeedContent = feedCombinedData.arrayListBuyFeedContent;
            homeSliderObjectArrayList = feedCombinedData.homeSliderObjectArrayList;

            SubscriptionDetails subscriptionDetails = LocalStorage.getUserSubscriptionDetails();
            if (subscriptionDetails != null && subscriptionDetails.allow_rental != null) {
                if (subscriptionDetails.allow_rental.equalsIgnoreCase(Constant.YES)) {
                    for (int i = 0; i < homeSliderObjectArrayList.size(); i++) {
                        if (homeSliderObjectArrayList.get(i).feedContentData != null) {
                            if (homeSliderObjectArrayList.get(i).feedContentData.arrowLinking != null && !homeSliderObjectArrayList.get(i).feedContentData.arrowLinking.isEmpty()) {
                                if (homeSliderObjectArrayList.get(i).feedContentData.arrowLinking.equalsIgnoreCase(SUBSCRIPTION_TAB)) {
                                    homeSliderObjectArrayList.remove(i);
                                }
                            }
                        }
                    }
                }
            }

            rlParentTitle.setPadding(Utility.getPercentPadding(getActivity(), 4), 30,
                    10, 30);
            rvContent.setPadding(Utility.getPercentPadding(getActivity(), 4), 0,
                    10, Utility.getPercentPadding(getActivity(), 1));

            KRecyclerViewAdapter adapter = null;
            switch (feedCombinedData.feedType) {
                case FEED_TYPE_BUY:
                    if (arrayListBuyFeedContent.size() == 0) {
                        llHzRow.setVisibility(View.GONE);
                        return;
                    }

                    llHzRow.setVisibility(View.VISIBLE);
                    tvTitle.setText(feedCombinedData.title);
                    linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                    rvContent.setLayoutManager(linearLayoutManager);
                    rvContent.setNestedScrollingEnabled(false);
                    rvContent.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                            super.onScrollStateChanged(recyclerView, newState);
                        }

                        @Override
                        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);
                            // Get the left offset
                            int leftOffset = Utility.getPercentPadding(getActivity(), 33);

                            // Calculate the x offset
                            final int scrollOffset = recyclerView.computeHorizontalScrollOffset();
                            int xOffset = Math.abs(scrollOffset - leftOffset);

                            if (feedCombinedData.tileShape.equalsIgnoreCase("h_rectangle")) {
                                if (linearLayoutManager.findFirstVisibleItemPosition() == 0 &&
                                        xOffset >= 0 && xOffset <= leftOffset) {
                                    float minAlpha = Constant.RV_BACK_ALPHA;
                                    float alpha = (float) xOffset / (float) leftOffset;
                                    if (alpha > 1) alpha = 1f;
                                    if (alpha < minAlpha) alpha = minAlpha;

                                    int alphaIn255 = (int) (alpha * 255);
                                    if (rvBackImage.getDrawable() != null) {
                                        rvBackImage.setImageAlpha(alphaIn255);
                                    }
                                }
                            } else {
                                // Calculate and set alpha only when the first item is completely visible.
                                if (linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 1 &&
                                        xOffset >= 0 && xOffset <= leftOffset) {
                                    float minAlpha = Constant.RV_BACK_ALPHA;
                                    float alpha = (float) xOffset / (float) leftOffset;
                                    if (alpha > 1) alpha = 1f;
                                    if (alpha < minAlpha) alpha = minAlpha;

                                    int alphaIn255 = (int) (alpha * 255);
                                    if (rvBackImage.getDrawable() != null) {
                                        rvBackImage.setImageAlpha(alphaIn255);
                                    }
                                }
                            }
                        }
                    });
                    Collections.shuffle(arrayListBuyFeedContent);
                    adapter = new KRecyclerViewAdapter(getContext(), arrayListBuyFeedContent, (viewGroup, i) -> {
                        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_feed_buy, viewGroup, false);
                        return new FeedCombinedHolder(layoutView, feedCombinedData);
                        //return new FeedHolder(layoutView, feedCombinedData);
                    }, (kRecyclerViewHolder, o, i) -> {
                        //Content Click handles based upon if the user is Yearly subscribed or not.
                        if (o instanceof FeedContentData) {
                            FeedContentData feedContentData = (FeedContentData) o;
                            SubscriptionDetails sd = LocalStorage.getUserSubscriptionDetails();
                            if (sd != null && sd.allow_rental != null) {
                                if (sd.allow_rental.equalsIgnoreCase(Constant.YES)) {
                                    if (feedContentData.postType.equals(FeedContentData.POST_TYPE_ORIGINALS)) {
                                        Intent intent = new Intent(getActivity(), BuyDetailActivity.class);
                                        intent.putExtra(BuyDetailActivity.CONTENT_DATA, feedContentData);
                                        getActivity().startActivityForResult(intent, BuyDetailActivity.REQUEST_CODE);
                                        showAd(feedContentData.feedTabType);
                                    } else {
                                        playMediaItems(feedContentData);
                                    }
                                } else {
                                    Intent intent = new Intent(getActivity(), BuyDetailActivity.class);
                                    intent.putExtra(BuyDetailActivity.CONTENT_DATA, feedContentData);
                                    getActivity().startActivityForResult(intent, BuyDetailActivity.REQUEST_CODE);
                                    showAd(feedContentData.feedTabType);
                                }
                            }
                        }
                    });
                    //if (feedData.backgroundStatus.equalsIgnoreCase("Enable") && !feedData.backgroundImage.equalsIgnoreCase("false")) {
                    //    Drawable drawable = new BitmapDrawable(getResources(), Utility.getBitmapFromURL(feedData.backgroundImage));
                    if (feedCombinedData.backgroundStatus.equalsIgnoreCase("Enable") && !feedCombinedData.backgroundImage.equalsIgnoreCase("false")) {
                        Drawable drawable = new BitmapDrawable(getResources(), Utility.getBitmapFromURL(feedCombinedData.backgroundImage));
                        Glide.with(this)
                                .load(drawable)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .into(rvBackImage);
                        rvContent.setPadding(Utility.getPercentPadding(getActivity(), 33), 5, 30, 5);
                    }
                    internalAdapters.add(adapter);
                    rvContent.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    break;

                case FEED_TYPE_PROMOTION:
                    rlParentTitle.setVisibility(View.GONE);
                    rvContent.setPadding(Utility.getPercentPadding(getActivity(), 4), Utility.getPercentPadding(getActivity(), 4),
                            10, Utility.getPercentPadding(getActivity(), 4));

                    if (homeSliderObjectArrayList.size() == 0) {
                        llHzRow.setVisibility(View.GONE);
                        return;
                    }
                    adapter = new KRecyclerViewAdapter(getContext(), homeSliderObjectArrayList, (KRecyclerViewHolderCallBack) (viewGroup, i) -> {
                        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_promotion_slider, viewGroup, false);
                        return new PromotionHolder(layoutView, feedCombinedData.promotionImage);
                    }, (kRecyclerViewHolder, o, i) -> {
                        if (o instanceof HomeSliderObject) {
                            showAd(feedCombinedData.feedType);
                            HomeSliderObject homeSliderObject = (HomeSliderObject) o;
                            if (homeSliderObject.feedContentData != null) {
                                if (homeSliderObject.feedContentData.arrowLinking != null && !homeSliderObject.feedContentData.arrowLinking.isEmpty()) {
                                    switch (homeSliderObject.feedContentData.arrowLinking) {
                                        case GAME_TAB:
                                            openFragment(HomePlayTabFragment.newInstance());
                                            break;
                                        case PREMIUM_TAB:
                                            homeActivity = (HomeActivity) getActivity();
                                            if (homeActivity != null) {
                                                homeActivity.showPremium();
                                            }
                                            break;
                                        case SUBSCRIPTION_TAB:
                                            startActivity(new Intent(getContext(), PremiumActivity.class).putExtra("title", getResources().getString(R.string.label_premium)));
                                            break;
                                        case NORMAL:
                                            setActionOnSlider(homeSliderObject.feedContentData, String.valueOf(homeSliderObject.id));
                                            break;
                                        default:
                                            setActionOnSlider(homeSliderObject.feedContentData, String.valueOf(homeSliderObject.id));
                                            break;
                                    }
                                } else {
                                    setActionOnSlider(homeSliderObject.feedContentData, String.valueOf(homeSliderObject.id));
                                }
                            } else {
                                setActionOnPlaySlider(homeSliderObject.gameData);
                            }
                        }
                    });
                    rvContent.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                    rvContent.setNestedScrollingEnabled(false);
                    rvContent.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    break;

                case FEED_TYPE_AD:
                    rlParentTitle.setVisibility(View.GONE);
                    rvContent.setPadding(Utility.getPercentPadding(getActivity(), 4), Utility.getPercentPadding(getActivity(), 4),
                            10, Utility.getPercentPadding(getActivity(), 4));

                    if (homeSliderObjectArrayList.size() == 0) {
                        llHzRow.setVisibility(View.GONE);
                        return;
                    }

                    adapter = new KRecyclerViewAdapter(getContext(), homeSliderObjectArrayList, (viewGroup, i) -> {
                        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_promotion_slider, viewGroup, false);
                        return new PromotionHolder(layoutView, feedCombinedData.promotionImage);
                    }, (kRecyclerViewHolder, o, i) -> {
                        if (o instanceof HomeSliderObject) {
                            HomeSliderObject homeSliderObject = (HomeSliderObject) o;
                            setActionOnSliderAd(homeSliderObject.feedContentData.adFieldsData, Constant.BANNER, String.valueOf(homeSliderObject.id));
                        }
                    });
                    rvContent.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                    rvContent.setNestedScrollingEnabled(false);
                    rvContent.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    break;
            }

            rlParentTitle.setOnClickListener(v -> {
                if (feedCombinedData.arrowLinking != null && !feedCombinedData.arrowLinking.isEmpty()) {
                    switch (feedCombinedData.arrowLinking) {
                        case GAME_TAB:
                            openFragment(HomePlayTabFragment.newInstance());
                            break;
                        case PREMIUM_TAB:
                            homeActivity = (HomeActivity) getActivity();
                            if (homeActivity != null) {
                                homeActivity.showPremium();
                            }
                            break;
                        case SUBSCRIPTION_TAB:
                            if (subscriptionDetails != null && subscriptionDetails.allow_rental != null) {
                                if (subscriptionDetails.allow_rental.equalsIgnoreCase(Constant.YES)) {
                                    homeActivity = (HomeActivity) getActivity();
                                    if (homeActivity != null) {
                                        homeActivity.showPremium();
                                    }
                                } else {
                                    startActivity(new Intent(getContext(), PremiumActivity.class).putExtra("title", getResources().getString(R.string.label_premium)));
                                }
                            } else {
                                startActivity(new Intent(getContext(), PremiumActivity.class).putExtra("title", getResources().getString(R.string.label_premium)));
                            }
                            break;
                        case NORMAL:
                            ViewAllFeedsFragment viewAllFeedsFragment = ViewAllFeedsFragment.newInstance(feedCombinedData.id, feedCombinedData.title, feedCombinedData.tabType, "feeds", feedCombinedData.tileShape, feedCombinedData.feedType, Constant.SCREEN_PREMIUM);
                            openFragment(viewAllFeedsFragment);
                            break;
                        default:
                            ViewAllFeedsFragment viewAllFeedsFragment1 = ViewAllFeedsFragment.newInstance(feedCombinedData.id, feedCombinedData.title, feedCombinedData.tabType, "feeds", feedCombinedData.tileShape, feedCombinedData.feedType, Constant.SCREEN_PREMIUM);
                            openFragment(viewAllFeedsFragment1);
                            break;
                    }
                } else {
                    ViewAllFeedsFragment viewAllFeedsFragment = ViewAllFeedsFragment.newInstance(feedCombinedData.id, feedCombinedData.title, feedCombinedData.tabType, "feeds", feedCombinedData.tileShape, feedCombinedData.feedType, Constant.SCREEN_PREMIUM);
                    openFragment(viewAllFeedsFragment);
                }
            });
            arrayListViewGroup.add((ViewGroup) viewContent);
        } catch (
                Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Initializes slider recycler view
     * Set the slider recyclerview of Watch and Buy Tab
     */
    private void setFeedSliderRecyclerView(FeedCombinedData feedCombinedData) {
        try {
            View viewContent = View.inflate(requireContext(), R.layout.row_horizontal_list, null);

            LinearLayout llHzRow = viewContent.findViewById(R.id.llHzRow);
            TextView tvTitle = viewContent.findViewById(R.id.tvStoreName);
            RecyclerView rvContent = viewContent.findViewById(R.id.rvContent);
            ImageView rvBackImage = viewContent.findViewById(R.id.rvBackImage);
            TextView tvViewAll = viewContent.findViewById(R.id.tvViewAll);
            RelativeLayout rlParentTitle = viewContent.findViewById(R.id.rlParentTitle);

            LinearLayoutManager linearLayoutManager;
            tvViewAll.setVisibility(View.GONE);
            llHzRow.setVisibility(View.VISIBLE);
            tvTitle.setVisibility(View.GONE);

            arrayListFeedContent = feedCombinedData.arrayListFeedContent;
            arrayListBuyFeedContent = feedCombinedData.arrayListBuyFeedContent;
            homeSliderObjectArrayList = feedCombinedData.homeSliderObjectArrayList;

            SubscriptionDetails subscriptionDetails = LocalStorage.getUserSubscriptionDetails();
            if (subscriptionDetails != null && subscriptionDetails.allow_rental != null) {
                if (subscriptionDetails.allow_rental.equalsIgnoreCase(Constant.YES)) {
                    for (int i = 0; i < homeSliderObjectArrayList.size(); i++) {
                        if (homeSliderObjectArrayList.get(i).feedContentData != null) {
                            if (homeSliderObjectArrayList.get(i).feedContentData.arrowLinking != null && !homeSliderObjectArrayList.get(i).feedContentData.arrowLinking.isEmpty()) {
                                if (homeSliderObjectArrayList.get(i).feedContentData.arrowLinking.equalsIgnoreCase(SUBSCRIPTION_TAB)) {
                                    homeSliderObjectArrayList.remove(i);
                                }
                            }
                        }
                    }
                }
            }

            rlParentTitle.setPadding(Utility.getPercentPadding(getActivity(), 4), 20,
                    10, 30);
            rvContent.setPadding(Utility.getPercentPadding(getActivity(), 4), 0,
                    10, Utility.getPercentPadding(getActivity(), 4));

            KRecyclerViewAdapter adapter = null;
            Timer[] timer = {new Timer()}; // new timer
            boolean[] isScrolling = {true};
            String feedId = String.valueOf(feedCombinedData.id);
            if (homeSliderObjectArrayList.size() == 0) {
                llHzRow.setVisibility(View.GONE);
                return;
            }
            adapter = new KRecyclerViewAdapter(getContext(), homeSliderObjectArrayList, (viewGroup, i) -> {
                View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_slider, viewGroup, false);
                return new SliderHolder(layoutView, homeSliderObjectArrayList.size(), new SliderListener() {
                    @Override
                    public void stopScrolling() {
                        timer[0].cancel();
                        timer[0].purge();
                        isScrolling[0] = false;
                    }
                });

            }, (kRecyclerViewHolder, o, i) -> {
                if (o instanceof HomeSliderObject) {
                    HomeSliderObject homeSliderObject = (HomeSliderObject) o;
                    if (homeSliderObject.feedContentData != null) {
                        if (feedCombinedData.arrowLinking != null && !feedCombinedData.arrowLinking.isEmpty()) {
                            switch (feedCombinedData.arrowLinking) {
                                case GAME_TAB:
                                    openFragment(HomePlayTabFragment.newInstance());
                                    break;
                                case PREMIUM_TAB:
                                    homeActivity = (HomeActivity) getActivity();
                                    if (homeActivity != null) {
                                        homeActivity.showPremium();
                                    }
                                    break;
                                case SUBSCRIPTION_TAB:
                                    startActivity(new Intent(getContext(), PremiumActivity.class).putExtra("title", getResources().getString(R.string.label_premium)));
                                    break;
                                case NORMAL:
                                    if (homeSliderObject.feedContentData.contentType.equalsIgnoreCase(Constant.AD)) {
                                        setActionOnSliderAd(homeSliderObject.feedContentData.adFieldsData, Constant.BANNER, String.valueOf(homeSliderObject.id));
                                    } else {
                                        setActionOnSlider(homeSliderObject.feedContentData, feedId);
                                    }
                                    break;
                                default:
                                    if (homeSliderObject.feedContentData.contentType.equalsIgnoreCase(Constant.AD)) {
                                        setActionOnSliderAd(homeSliderObject.feedContentData.adFieldsData, Constant.BANNER, String.valueOf(homeSliderObject.id));
                                    } else {
                                        setActionOnSlider(homeSliderObject.feedContentData, feedId);
                                    }
                                    break;
                            }
                        } else if (homeSliderObject.feedContentData.contentType.equalsIgnoreCase(Constant.AD)) {
                            setActionOnSliderAd(homeSliderObject.feedContentData.adFieldsData, Constant.BANNER, String.valueOf(homeSliderObject.id));
                        } else {
                            setActionOnSlider(homeSliderObject.feedContentData, feedId);
                        }
                    } else {
                        setActionOnPlaySlider(homeSliderObject.gameData);
                    }
                }

            });
            if (feedCombinedData.backgroundStatus.equalsIgnoreCase("Enable") && !feedCombinedData.backgroundImage.equalsIgnoreCase("false")) {
                Drawable drawable = new BitmapDrawable(getResources(), Utility.getBitmapFromURL(feedCombinedData.backgroundImage));
                Glide.with(this)
                        .load(drawable)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(rvBackImage);
                rvContent.setPadding(Utility.getPercentPadding(getActivity(), 33), 5, 30, 5);
            }

            linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            rvContent.setLayoutManager(linearLayoutManager);
            rvContent.setNestedScrollingEnabled(false);
            KRecyclerViewAdapter finalAdapter = adapter;
            rvContent.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        if (!isScrolling[0]) {
                            timer[0].cancel();
                            timer[0].purge();
                            timer[0] = new Timer();
                            timer[0].schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    isScrolling[0] = true;
                                    int visibleItemPosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                                    if (visibleItemPosition < (finalAdapter.getItemCount() - 1)) {
                                        rvContent.smoothScrollToPosition(visibleItemPosition + 1);
                                    } else if (visibleItemPosition == (finalAdapter.getItemCount() - 1)) {
                                        rvContent.smoothScrollToPosition(0);
                                    }
                                }
                            }, 4000, 8000);
                        }
                    } else {
                        timer[0].cancel();
                        timer[0].purge();
                        isScrolling[0] = false;
                    }
                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    // Get the left offset
                    int leftOffset = Utility.getPercentPadding(getActivity(), 33);

                    // Calculate the x offset
                    final int scrollOffset = recyclerView.computeHorizontalScrollOffset();
                    int xOffset = Math.abs(scrollOffset - leftOffset);

                    if (feedCombinedData.tileShape.equalsIgnoreCase("h_rectangle")) {
                        if (linearLayoutManager.findFirstVisibleItemPosition() == 0 &&
                                xOffset >= 0 && xOffset <= leftOffset) {
                            float minAlpha = Constant.RV_BACK_ALPHA;
                            float alpha = (float) xOffset / (float) leftOffset;
                            if (alpha > 1) alpha = 1f;
                            if (alpha < minAlpha) alpha = minAlpha;

                            int alphaIn255 = (int) (alpha * 255);
                            if (rvBackImage.getDrawable() != null) {
                                rvBackImage.setImageAlpha(alphaIn255);
                            }
                        }
                    } else {
                        // Calculate and set alpha only when the first item is completely visible.
                        if (linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 1 &&
                                xOffset >= 0 && xOffset <= leftOffset) {
                            float minAlpha = Constant.RV_BACK_ALPHA;
                            float alpha = (float) xOffset / (float) leftOffset;
                            if (alpha > 1) alpha = 1f;
                            if (alpha < minAlpha) alpha = minAlpha;

                            int alphaIn255 = (int) (alpha * 255);
                            if (rvBackImage.getDrawable() != null) {
                                rvBackImage.setImageAlpha(alphaIn255);
                            }
                        }
                    }
                }
            });
            rvContent.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            arrayListViewGroup.add((ViewGroup) viewContent);

            LinearSnapHelper snapHelper = new LinearSnapHelper();
            snapHelper.attachToRecyclerView(rvContent);

            timer[0].schedule(new TimerTask() {
                @Override
                public void run() {
                    isScrolling[0] = true;
                    int visibleItemPosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                    if (visibleItemPosition < (finalAdapter.getItemCount() - 1)) {
                        rvContent.smoothScrollToPosition(visibleItemPosition + 1);
                    } else if (visibleItemPosition == (finalAdapter.getItemCount() - 1)) {
                        rvContent.smoothScrollToPosition(0);
                    }
                }
            }, 4000, 8000);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setFeedBigSliderRecyclerView(FeedCombinedData feedCombinedData) {
        try {
            View viewContent = View.inflate(requireContext(), R.layout.row_big_slider_horizontal_list, null);

            boolean flagDecoration = false;
            LinearLayout llHzRow = viewContent.findViewById(R.id.llHzRow);
            TextView tvTitle = viewContent.findViewById(R.id.tvStoreName);
            RecyclerView rvContent = viewContent.findViewById(R.id.rvContent);
            ImageView rvBackImage = viewContent.findViewById(R.id.rvBackImage);
            TextView tvViewAll = viewContent.findViewById(R.id.tvViewAll);
            RelativeLayout rlParentTitle = viewContent.findViewById(R.id.rlParentTitle);

            LinearLayoutManager linearLayoutManager;
            tvViewAll.setVisibility(View.GONE);
            llHzRow.setVisibility(View.VISIBLE);
            tvTitle.setVisibility(View.GONE);

            arrayListFeedContent = feedCombinedData.arrayListFeedContent;
            arrayListBuyFeedContent = feedCombinedData.arrayListBuyFeedContent;
            homeSliderObjectArrayList = feedCombinedData.homeSliderObjectArrayList;

            SubscriptionDetails subscriptionDetails = LocalStorage.getUserSubscriptionDetails();
            if (subscriptionDetails != null && subscriptionDetails.allow_rental != null) {
                if (subscriptionDetails.allow_rental.equalsIgnoreCase(Constant.YES)) {
                    for (int i = 0; i < homeSliderObjectArrayList.size(); i++) {
                        if (homeSliderObjectArrayList.get(i).feedContentData != null) {
                            if (homeSliderObjectArrayList.get(i).feedContentData.arrowLinking != null && !homeSliderObjectArrayList.get(i).feedContentData.arrowLinking.isEmpty()) {
                                if (homeSliderObjectArrayList.get(i).feedContentData.arrowLinking.equalsIgnoreCase(SUBSCRIPTION_TAB)) {
                                    homeSliderObjectArrayList.remove(i);
                                }
                            }
                        }
                    }
                }
            }

            KRecyclerViewAdapter adapter = null;
            Timer[] timer = {new Timer()}; // new timer
            boolean[] isScrolling = {true};
            String feedId = String.valueOf(feedCombinedData.id);
            if (homeSliderObjectArrayList.size() == 0) {
                llHzRow.setVisibility(View.GONE);
                return;
            }
            adapter = new KRecyclerViewAdapter(getContext(), homeSliderObjectArrayList, (viewGroup, i) -> {
                View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_big_slider, viewGroup, false);
                return new BigSliderHolder(layoutView, homeSliderObjectArrayList.size(), this, new SliderListener() {
                    @Override
                    public void stopScrolling() {
                        timer[0].cancel();
                        timer[0].purge();
                        isScrolling[0] = false;
                    }
                });

            }, (kRecyclerViewHolder, o, i) -> {
                if (o instanceof HomeSliderObject) {
                    HomeSliderObject homeSliderObject = (HomeSliderObject) o;
                    setActionOnClick(homeSliderObject);
                }

            });
            if (feedCombinedData.backgroundStatus.equalsIgnoreCase("Enable") && !feedCombinedData.backgroundImage.equalsIgnoreCase("false")) {
                Drawable drawable = new BitmapDrawable(getResources(), Utility.getBitmapFromURL(feedCombinedData.backgroundImage));
                Glide.with(this)
                        .load(drawable)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(rvBackImage);
                rvContent.setPadding(Utility.getPercentPadding(getActivity(), 33), 5, 30, 5);
            }

            linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            rvContent.setLayoutManager(linearLayoutManager);
            rvContent.setNestedScrollingEnabled(false);
            KRecyclerViewAdapter finalAdapter = adapter;

            rvContent.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        if (!isScrolling[0]) {
                            timer[0].cancel();
                            timer[0].purge();
                            timer[0] = new Timer();
                            timer[0].schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    isScrolling[0] = true;
                                    int visibleItemPosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                                    if (visibleItemPosition < (finalAdapter.getItemCount() - 1)) {
                                        rvContent.smoothScrollToPosition(visibleItemPosition + 1);
                                    } else if (visibleItemPosition == (finalAdapter.getItemCount() - 1)) {
                                        rvContent.smoothScrollToPosition(0);
                                    }
                                }
                            }, 4000, 8000);
                        }
                    } else {
                        timer[0].cancel();
                        timer[0].purge();
                        isScrolling[0] = false;
                    }
                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    // Get the left offset
                    int leftOffset = Utility.getPercentPadding(getActivity(), 33);

                    // Calculate the x offset
                    final int scrollOffset = recyclerView.computeHorizontalScrollOffset();
                    int xOffset = Math.abs(scrollOffset - leftOffset);

                    if (feedCombinedData.tileShape.equalsIgnoreCase("h_rectangle")) {
                        if (linearLayoutManager.findFirstVisibleItemPosition() == 0 &&
                                xOffset >= 0 && xOffset <= leftOffset) {
                            float minAlpha = Constant.RV_BACK_ALPHA;
                            float alpha = (float) xOffset / (float) leftOffset;
                            if (alpha > 1) alpha = 1f;
                            if (alpha < minAlpha) alpha = minAlpha;

                            int alphaIn255 = (int) (alpha * 255);
                            if (rvBackImage.getDrawable() != null) {
                                rvBackImage.setImageAlpha(alphaIn255);
                            }
                        }
                    } else {
                        // Calculate and set alpha only when the first item is completely visible.
                        if (linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 1 &&
                                xOffset >= 0 && xOffset <= leftOffset) {
                            float minAlpha = Constant.RV_BACK_ALPHA;
                            float alpha = (float) xOffset / (float) leftOffset;
                            if (alpha > 1) alpha = 1f;
                            if (alpha < minAlpha) alpha = minAlpha;

                            int alphaIn255 = (int) (alpha * 255);
                            if (rvBackImage.getDrawable() != null) {
                                rvBackImage.setImageAlpha(alphaIn255);
                            }
                        }
                    }
                }
            });
            rvContent.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            arrayListViewGroup.add((ViewGroup) viewContent);

            // Dot indicator for banner
            final int radius = getResources().getDimensionPixelSize(R.dimen.dot_radius);
            final int dotsHeight = getResources().getDimensionPixelSize(R.dimen.dot_height);
            final int color = ContextCompat.getColor(getContext(), R.color.colorAccent);
            final int inActiveColor = ContextCompat.getColor(getContext(), R.color.white);
            if (!flagDecoration) {
                rvContent.addItemDecoration(new DotsIndicatorDecoration(radius, radius * 2, dotsHeight, inActiveColor, color));
                flagDecoration = true;
            }

            LinearSnapHelper snapHelper = new LinearSnapHelper();
            snapHelper.attachToRecyclerView(rvContent);

            timer[0].schedule(new TimerTask() {
                @Override
                public void run() {
                    isScrolling[0] = true;
                    int visibleItemPosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                    if (visibleItemPosition < (finalAdapter.getItemCount() - 1)) {
                        rvContent.smoothScrollToPosition(visibleItemPosition + 1);
                    } else if (visibleItemPosition == (finalAdapter.getItemCount() - 1)) {
                        rvContent.smoothScrollToPosition(0);
                    }
                }
            }, 4000, 8000);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setActionOnSlider(FeedContentData feedContentData, String feedId) {
        /* if (feedContentData.feedTabType.equals(Constant.TAB_WATCH)) {*/
        homeActivity = (HomeActivity) getActivity();
        if (feedContentData.postExcerpt.equalsIgnoreCase("paid")) {
            // logic for buy/paid item
            SubscriptionDetails sd = LocalStorage.getUserSubscriptionDetails();
            if (sd != null && sd.allow_rental != null && !sd.allow_rental.equals("") && !feedContentData.isTrailer) {
                if (sd.allow_rental.equalsIgnoreCase(Constant.YES)) {
                    if (feedContentData.postType.equals(FeedContentData.POST_TYPE_ORIGINALS)) {
                        Intent intent = new Intent(getActivity(), BuyDetailActivity.class);
                        intent.putExtra(BuyDetailActivity.CONTENT_DATA, feedContentData);
                        intent.putExtra(BuyDetailActivity.FROM_SLIDER, "sliderPageKey");
                        getActivity().startActivityForResult(intent, BuyDetailActivity.REQUEST_CODE);
                    } else {
                        ArrayList<FeedContentData> arrayListFeedContentData = new ArrayList<>();
                        arrayListFeedContentData.add(feedContentData);
                        //homeActivity.showAudioSongExpandedView(arrayListFeedContentData, 0, feedId, Analyticals.CONTEXT_SLIDER, "");
                        switch (feedContentData.contentType) {
                            // logic for free item
                            case FeedContentData.CONTENT_TYPE_URL:
                                String url = feedContentData.url;
                                if (!url.startsWith("http://") && !url.startsWith("https://"))
                                    url = "https://" + url;
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                startActivity(browserIntent);
                                break;

                            case FeedContentData.CONTENT_TYPE_AUDIO_POST:
                                ArrayList<FeedContentData> arrayListFeedContentData1 = new ArrayList<>();
                                arrayListFeedContentData1.add(feedContentData);
                                //activity.playItems(arrayListFeedContentData, 0, sliderId, Analyticals.CONTEXT_SLIDER, "");
                                homeActivity.showAudioSongExpandedView(arrayListFeedContentData1, 0, feedId, Analyticals.CONTEXT_SLIDER, "");
                                break;
                            case FeedContentData.CONTENT_TYPE_ALL_M_ORIGINALS:
                                homeActivity.showSeriesExpandedView(feedContentData.seriesId, feedContentData.seasonId, feedContentData.episodeId, String.valueOf(feedContentData.iswatchlisted));
                                break;
                            case FeedContentData.CONTENT_TYPE_SEASON:
                            case FeedContentData.CONTENT_TYPE_EPISODE:
                                homeActivity.showSeriesExpandedView(feedContentData.seriesId, feedContentData.seasonId, feedContentData.episodeId, "");
                                break;
                            case FeedContentData.CONTENT_TYPE_ALL_M_STREAM:
                                //playChannelAPI(feedContentData, 1, sliderId, Analyticals.CONTEXT_SLIDER);
                                homeActivity.showChannelExpandedView(feedContentData, 1, sliderId, Analyticals.CONTEXT_CHANNEL);
                                break;
                            case FeedContentData.CONTENT_TYPE_PLAYLIST:
                                homeActivity.showPlaylistExpandedView(feedContentData.postId, String.valueOf(feedContentData.iswatchlisted));
                                break;
                        }
                    }
                } else {
                    Intent intent = new Intent(getActivity(), BuyDetailActivity.class);
                    intent.putExtra(BuyDetailActivity.CONTENT_DATA, feedContentData);
                    intent.putExtra(BuyDetailActivity.FROM_SLIDER, "sliderPageKey");
                    getActivity().startActivityForResult(intent, BuyDetailActivity.REQUEST_CODE);
                }
            } else {
                // logic for buy/paid item
                Intent intent = new Intent(getActivity(), BuyDetailActivity.class);
                intent.putExtra(BuyDetailActivity.CONTENT_DATA, feedContentData);
                intent.putExtra(BuyDetailActivity.FROM_SLIDER, "sliderPageKey");
                getActivity().startActivityForResult(intent, BuyDetailActivity.REQUEST_CODE);
            }
        } else {
            switch (feedContentData.contentType) {
                // logic for free item
                case FeedContentData.CONTENT_TYPE_URL:
                    String url = feedContentData.url;
                    if (!url.startsWith("http://") && !url.startsWith("https://"))
                        url = "https://" + url;
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(browserIntent);
                    break;

                case FeedContentData.CONTENT_TYPE_AUDIO_POST:
                    ArrayList<FeedContentData> arrayListFeedContentData = new ArrayList<>();
                    arrayListFeedContentData.add(feedContentData);
                    //activity.playItems(arrayListFeedContentData, 0, sliderId, Analyticals.CONTEXT_SLIDER, "");
                    homeActivity.showAudioSongExpandedView(arrayListFeedContentData, 0, feedId, Analyticals.CONTEXT_SLIDER, "");
                    break;
                case FeedContentData.CONTENT_TYPE_ALL_M_ORIGINALS:
                    homeActivity.showSeriesExpandedView(feedContentData.seriesId, feedContentData.seasonId, feedContentData.episodeId, String.valueOf(feedContentData.iswatchlisted));
                    break;
                case FeedContentData.CONTENT_TYPE_SEASON:
                case FeedContentData.CONTENT_TYPE_EPISODE:
                    homeActivity.showSeriesExpandedView(feedContentData.seriesId, feedContentData.seasonId, feedContentData.episodeId, "");
                    break;
                case FeedContentData.CONTENT_TYPE_ALL_M_STREAM:
                    //playChannelAPI(feedContentData, 1, sliderId, Analyticals.CONTEXT_SLIDER);
                    homeActivity.showChannelExpandedView(feedContentData, 1, sliderId, Analyticals.CONTEXT_CHANNEL);
                    break;
                case FeedContentData.CONTENT_TYPE_PLAYLIST:
                    homeActivity.showPlaylistExpandedView(feedContentData.postId, String.valueOf(feedContentData.iswatchlisted));
                    break;
                case FeedContentData.CONTENT_TYPE_ESPORTS:
                    homeActivity.getUserRegisteredTournamentInfo(feedContentData.postId);
                    break;
            }
        }
        /*} else if (feedContentData.feedTabType.equals(Constant.TAB_BUY)) {
            Intent intent = new Intent(getActivity(), BuyDetailActivity.class);
            intent.putExtra(BuyDetailActivity.CONTENT_DATA, feedContentData);
            getActivity().startActivityForResult(intent, BuyDetailActivity.REQUEST_CODE);
        }*/
    }


    private void setActionOnSliderAd(FeedContentData feedContentData) {
        switch (feedContentData.adFieldsData.adType) {
            case FeedContentData.AD_TYPE_IN_APP:
                if (feedContentData.adFieldsData.contentType.equalsIgnoreCase(Constant.PAGES)) {
                    HomeActivity.getInstance().redirectToPage(feedContentData.adFieldsData.pageType);
                } else {
                    HomeActivity.getInstance().getMediaData(String.valueOf(feedContentData.adFieldsData.contentId), feedContentData.adFieldsData.contentType);
                }
                break;
            case FeedContentData.AD_TYPE_EXTERNAL:
                setActionOnAd(feedContentData);
                break;
        }
    }

    private void setActionOnAd(FeedContentData feedContentData) {
        switch (feedContentData.adFieldsData.urlType) {
            case Constant.BROWSER:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(feedContentData.adFieldsData.externalUrl));
                startActivity(browserIntent);
                break;
            case Constant.WEBVIEW:
                startActivity(new Intent(getContext(), BrowserActivity.class).putExtra("title", "").putExtra("posturl", feedContentData.adFieldsData.externalUrl));
                break;
        }
    }

    private void setActionOnPlaySlider(GameData gameData) {

        switch (gameData.quizType) {
            case GameData.QUIZE_TYPE_LIVE:
                String invalidDateMessage = GameDateValidation.getInvalidDateMsg(getActivity(), gameData.start, gameData.end);
                if (invalidDateMessage.equals("")) {
                    if (gameData.playedGame) {
                        AlertUtils.showToast(getString(R.string.msg_game_played), Toast.LENGTH_SHORT, getContext());
                        return;
                    }
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(GameStartActivity.GAME_DATA, gameData);
                    startActivityForResult(new Intent(getContext(), GameStartActivity.class)
                            .putExtra("Bundle", bundle), GameData.REQUEST_CODE_GAME);
                } else {
                    if (gameData.winnersDeclared) {
                        startActivity(
                                new Intent(getActivity(), LuckyWinnerActivity.class)
                                        .putExtra(LuckyWinnerActivity.KEY_GAME_ID, gameData.id)
                        );
                        return;
                    }
                    AlertUtils.showToast(invalidDateMessage, Toast.LENGTH_SHORT, getContext());
                }
                break;

            case GameData.QUIZE_TYPE_TURN_BASED:
                Bundle bundle = new Bundle();
                bundle.putParcelable(GameStartActivity.GAME_DATA, gameData);
                startActivityForResult(new Intent(getContext(), GameStartActivity.class)
                        .putExtra("Bundle", bundle), GameData.REQUEST_CODE_GAME);
                break;

            case GameData.QUIZE_TYPE_HUNTER_GAMES:
                Intent intent = new Intent(getActivity(), WebViewActivity.class).putExtra(WebViewActivity.GAME_URL, gameData.webviewUrl).putExtra(WebViewActivity.GAME_TITLE, gameData.postTitle);
                startActivityForResult(intent, WebViewActivity.GAME_REDIRECT_REQUEST_CODE);
                break;
        }
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
        arrayListViewGroup.add((ViewGroup) viewContent);
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
        arrayListViewGroup.add((ViewGroup) viewContent);
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
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.d("HomeBuyTabFragment", "HomeBuyTabFragment" + isVisibleToUser);
        if (isVisibleToUser) {
            /*if (getActivity() != null && getActivity() instanceof HomeActivity) {
                homeActivity = (HomeActivity) getActivity();
                if (homeActivity != null) {
                    homeActivity.showAd();
                }
            }*/
            is_fragment_visible = true;
            if (onFeedRefreshListener != null)
                updateSlider(2);
        } else {
            is_fragment_visible = false;
        }
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
                    getSingleMediaItem(id);
                    break;
            }
        }
    }

    private void setSeriesData(String seriesId, String seasonId, String episodeId, String iswatched) {
        homeActivity.showSeriesExpandedView(seriesId, seasonId, episodeId, iswatched);
    }

    private void setPlaylistData(String playlistId) {
        homeActivity.showPlaylistExpandedView(playlistId, "false");
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
        arrayListFeedContent.add(feedContentData);
        String id = feedContentData.postId;
        homeActivity.showAudioSongExpandedView(arrayListFeedContent, feedContentData.feedPosition, id, contextName, String.valueOf(feedContentData.iswatchlisted));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            ((HomeActivity) getActivity()).showToolbarIcon();
            ((HomeActivity) getActivity()).showToolbarTitle(false);
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        for (KRecyclerViewAdapter adapter : internalAdapters) {
            adapter.notifyDataSetChanged();
        }
        if (player != null && resumePlayer) {
            player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
            player.setPlayWhenReady(true);
            playerView.setPlayer(player);
        }
        if (is_first_time_loading) {
            is_first_time_loading = false;
            getFeedsAPI();
            //getActiveTabs();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (player != null) {
            player.setPlayWhenReady(false);
            resumePlayer = true;
            playerView.setPlayer(null);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        closePlayer();
    }

    private void showAd(String id) {
        if (getActivity() != null && getActivity() instanceof HomeActivity) {
            homeActivity = (HomeActivity) getActivity();
            if (homeActivity != null) {
                homeActivity.showAd(id);
            }
        }
    }

    private void setActionOnSliderAd(AdFieldsData adFieldsData, String location, String subLocation) {
        APIMethods.addEvent(getContext(), Constant.CLICK, adFieldsData.postId, location, subLocation);
        switch (adFieldsData.adType) {
            case FeedContentData.AD_TYPE_IN_APP:
                if (adFieldsData.contentType.equalsIgnoreCase(Constant.PAGES)) {
                    HomeActivity.getInstance().redirectToPage(adFieldsData.pageType);
                } else {
                    HomeActivity.getInstance().getMediaData(String.valueOf(adFieldsData.contentId), adFieldsData.contentType);
                }
                break;
            case FeedContentData.AD_TYPE_EXTERNAL:
                setActionOnAd(adFieldsData);
                break;
        }
    }

    private void setActionOnAd(AdFieldsData adFieldsData) {
        switch (adFieldsData.urlType) {
            case Constant.BROWSER:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(adFieldsData.externalUrl));
                startActivity(browserIntent);
                break;
            case Constant.WEBVIEW:
                startActivity(new Intent(getContext(), BrowserActivity.class).putExtra("title", "").putExtra("posturl", adFieldsData.externalUrl));
                break;
        }
    }

    public void showBannerAd(String id) {
        String adType = Utility.getBannerAdType(id, getContext());
        if (!adType.isEmpty()) {
            if (adType.equalsIgnoreCase(Constant.ADMOB)) {
                mAdView.loadAd(adRequest);
                mAdView.setVisibility(View.VISIBLE);
                clAdHolder.setVisibility(View.GONE);
            } else if (adType.equalsIgnoreCase(Constant.CUSTOM)) {
                showCustomAd(id);
                mAdView.setVisibility(View.GONE);
            }
        }
    }

    public void showCustomAd(String id) {
        clAdHolder.setVisibility(View.VISIBLE);
        List<AdMobData> list = LocalStorage.getBannerAdMobList(LocalStorage.KEY_BANNER_AD_MOB, getContext());
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).id.equals(id)) {
                    if (list.get(i).feedContentObjectData != null) {
                        int finalI = i;
                        if (list.get(i).feedContentObjectData.mediaType.equalsIgnoreCase(FeedContentData.MEDIA_TYPE_IMAGE)) {
                            String url = list.get(i).feedContentObjectData.adFieldsData.attachment;
                            Glide.with(getActivity())
                                    .load(url)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(ivCustomAd);
                            ivCustomAd.setVisibility(View.VISIBLE);
                            playerView.setVisibility(View.GONE);
                            APIMethods.addEvent(getActivity(), Constant.VIEW, list.get(i).feedContentObjectData.postId, Constant.BANNER, id);

                            ivCustomAd.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    setActionOnSliderAd(list.get(finalI).feedContentObjectData.adFieldsData, Constant.BANNER, id);
                                }
                            });

                        } else if (list.get(i).feedContentObjectData.mediaType.equalsIgnoreCase(FeedContentData.MEDIA_TYPE_VIDEO)) {
                            playVideoAd(list.get(i).feedContentObjectData.adFieldsData.attachment);
                            playerView.setVisibility(View.VISIBLE);
                            ivVolumeOff.setVisibility(View.VISIBLE);
                            ivCustomAd.setVisibility(View.GONE);
                            APIMethods.addEvent(getActivity(), Constant.VIEW, list.get(i).feedContentObjectData.postId, Constant.BANNER, id);

                            playerView.getVideoSurfaceView().setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    setActionOnSliderAd(list.get(finalI).feedContentObjectData.adFieldsData, Constant.BANNER, id);
                                }
                            });
                            ivVolumeUp.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {
                                        if (player != null && player.isPlaying()) {
                                            ivVolumeUp.setVisibility(View.GONE);
                                            ivVolumeOff.setVisibility(View.VISIBLE);
                                            player.setVolume(0.0f);
                                        }
                                    } catch (Exception e) {

                                    }
                                }
                            });
                            ivVolumeOff.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {
                                        if (player != null && player.isPlaying()) {
                                            ivVolumeOff.setVisibility(View.GONE);
                                            ivVolumeUp.setVisibility(View.VISIBLE);
                                            player.setVolume(1.0f);
                                        }
                                    } catch (Exception e) {

                                    }
                                }
                            });
                        } else if (list.get(i).feedContentObjectData.mediaType.equalsIgnoreCase(FeedContentData.MEDIA_TYPE_SLIDER)) {
                            updateOtherSliders(list.get(i).feedContentObjectData.arrayListAdFieldsData, id);
                            playerView.setVisibility(View.GONE);
                            ivVolumeOff.setVisibility(View.GONE);
                            ivCustomAd.setVisibility(View.GONE);
                            rvSliderAd.setVisibility(View.VISIBLE);
                        }
                    }
                    ivClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            closePlayer();
                        }
                    });
                }
            }
        }
    }

    private void playVideoAd(String url) {
        playerView.setUseController(false);
        playerView.requestFocus();

        Uri uri = Uri.parse(url);
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getContext(), MyApp.getAppContext().getPackageName(), bandwidthMeter);
        MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(uri));

        player = new ExoPlayer.Builder(requireContext()).build();
        player.addListener(new Player.Listener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == Player.STATE_ENDED) {
                    player.seekTo(0);
                }
            }

            @Override
            public void onPlayerError(PlaybackException error) {

            }
        });
        player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT);
        player.prepare(mediaSource);
        player.setPlayWhenReady(true);

        /*
          Volume is set to 0 as per requirement
          Use as per the case
         */
        player.setVolume(0.0f);

        playerView.setPlayer(player);

        ViewTreeObserver vto = playerView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                try {
                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                    if (Build.VERSION.SDK_INT >= 14) {
                        try {
                            retriever.setDataSource(url, new HashMap<String, String>());
                        } catch (RuntimeException ex) {
                            // something went wrong with the file, ignore it and continue
                        }
                    } else {
                        retriever.setDataSource(url);
                    }
                    int videoWidth = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
                    int videoHeight = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
                    retriever.release();

                    playerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    int playerViewWidth = playerView.getWidth();
                    if (playerViewWidth < videoWidth) {
                        int scale = (Math.abs(playerViewWidth - videoWidth)) / 2;
                        int height = videoHeight - scale;
                        playerView.getLayoutParams().height = height;
                    } else {
                        int scale = (int) (Math.abs(playerViewWidth - videoWidth) * 1.5);
                        int height = Math.abs(videoHeight + scale);
                        playerView.getLayoutParams().height = height;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void closePlayer() {
        if (player != null) {
            player.setPlayWhenReady(false);
            player.stop();
            player.release();
            player = null;
        }
        if (playerView != null) {
            playerView.setPlayer(null);
        }
    }

    /**
     * Initializes slider recycler view
     * Set the slider recyclerview of Custom ads
     */
    private void setOtherSliderRecyclerView(String id) {
        adapterSliderAd = new KRecyclerViewAdapter(getActivity(), adSliderObjectArrayList, new KRecyclerViewHolderCallBack() {
            @Override
            public KRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_slider, viewGroup, false);

                adapterSliderAd.getItemCount();
                adapterSliderAd.getSelectedIndexes();
                return new CustomAdSliderHolder(layoutView, adSliderObjectArrayList.size(), Constant.BANNER, id, new SliderListener() {
                    @Override
                    public void stopScrolling() {
                        stopSliding();
                    }
                });
            }
        }, new KRecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClicked(KRecyclerViewHolder kRecyclerViewHolder, Object o, int i) {
                if (o instanceof AdFieldsData) {
                    AdFieldsData adFieldsData = (AdFieldsData) o;
                    setActionOnSliderAd(adFieldsData, Constant.BANNER, id);
                }
            }
        });

        layoutManager = new CustomLinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rvSliderAd.setLayoutManager(layoutManager);
        rvSliderAd.setNestedScrollingEnabled(false);
        rvSliderAd.setOnFlingListener(null);
        rvSliderAd.setAdapter(adapterSliderAd);

        // Dot indicator for banner
        final int radius = getResources().getDimensionPixelSize(R.dimen.dot_radius);
        final int dotsHeight = getResources().getDimensionPixelSize(R.dimen.dot_height);
        final int color = ContextCompat.getColor(getContext(), R.color.colorAccent);
        final int inActiveColor = ContextCompat.getColor(getContext(), R.color.game_gray);
        if (!flagDecoration) {
            rvSliderAd.addItemDecoration(new DotsIndicatorDecoration(radius, radius * 2, dotsHeight, inActiveColor, color));
            flagDecoration = true;
        }

        new PagerSnapHelper().attachToRecyclerView(rvSliderAd);

        rvSliderAd.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                shouldSlide = (newState == RecyclerView.SCROLL_STATE_IDLE);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    sliderPosition = layoutManager.findFirstCompletelyVisibleItemPosition();
                    startSliding();
                } else {
                    stopSliding();
                }
            }
        });
    }

    private Handler sliderHandler;
    private Runnable sliderRunnable;
    private long secondsToWait = 4000;

    private void startSliding() {
        if (sliderHandler == null) {
            sliderHandler = new Handler();
        }
        if (sliderRunnable == null) {
            sliderRunnable = this::changeSliderPage;
        }
        sliderHandler.postDelayed(sliderRunnable, secondsToWait);
    }

    private void stopSliding() {
        if (sliderHandler != null && sliderRunnable != null) {
            sliderHandler.removeCallbacks(sliderRunnable);
        }
    }

    @SuppressLint("SetTextI18n")
    private void changeSliderPage() {
        if (adSliderObjectArrayList.size() <= 1) return;
        try {
            LinearLayoutManager layoutManager = (LinearLayoutManager) rvSliderAd.getLayoutManager();
            if (layoutManager != null) {
                visibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition();
                if (visibleItemPosition > -1 && visibleItemPosition < adSliderObjectArrayList.size()) {
                    if (visibleItemPosition == adSliderObjectArrayList.size() - 1) {
                        // Scroll to first item
                        rvSliderAd.smoothScrollToPosition(0);
                    } else {
                        // Scroll to next item
                        rvSliderAd.smoothScrollToPosition(visibleItemPosition + 1);
                    }
                }
            }
        } catch (Exception ignored) {

        }
    }

    private void updateOtherSliders(ArrayList<AdFieldsData> adFieldsDataArrayList, String id) {
        setOtherSliderRecyclerView(id);
        flagAd = true;
        flagDecoration = false;
        adSliderObjectArrayList.clear();
        new Thread(() -> {
            for (AdFieldsData adFieldsData : adFieldsDataArrayList) {
                sliderIdAd = String.valueOf(adFieldsData.postId);
                adSliderObjectArrayList.addAll(adFieldsDataArrayList);
                break;
            }

            new Handler(Looper.getMainLooper()).post(() -> {
                if (adapterSliderAd != null)
                    adapterSliderAd.notifyDataSetChanged();
                startSliding();
            });
        }).start();
    }

    public void setActionOnClick(HomeSliderObject homeSliderObject) {
        if (homeSliderObject.feedContentData != null) {
            if (homeSliderObject.feedContentData.arrowLinking != null && !homeSliderObject.feedContentData.arrowLinking.isEmpty()) {
                switch (homeSliderObject.feedContentData.arrowLinking) {
                    case GAME_TAB:
                        homeActivity.openFragment(HomePlayTabFragment.newInstance());
                        break;
                    case PREMIUM_TAB:
                        homeActivity = (HomeActivity) getActivity();
                        if (homeActivity != null) {
                            homeActivity.showPremium();
                        }
                        break;
                    case SUBSCRIPTION_TAB:
                        startActivity(new Intent(getContext(), PremiumActivity.class).putExtra("title", getResources().getString(R.string.label_premium)));
                        break;
                    case NORMAL:
                        if (homeSliderObject.feedContentData.contentType.equalsIgnoreCase(Constant.AD)) {
                            setActionOnSliderAd(homeSliderObject.feedContentData);
                        } else {
                            setActionOnSlider(homeSliderObject.feedContentData, String.valueOf(homeSliderObject.id));
                        }
                        break;
                    default:
                        if (homeSliderObject.feedContentData.contentType.equalsIgnoreCase(Constant.AD)) {
                            setActionOnSliderAd(homeSliderObject.feedContentData);
                        } else {
                            setActionOnSlider(homeSliderObject.feedContentData, String.valueOf(homeSliderObject.id));
                        }
                        break;
                }
            } else if (homeSliderObject.feedContentData.contentType.equalsIgnoreCase(Constant.AD)) {
                setActionOnSliderAd(homeSliderObject.feedContentData);
            } else {
                setActionOnSlider(homeSliderObject.feedContentData, String.valueOf(homeSliderObject.id));
            }
        } else {
            setActionOnPlaySlider(homeSliderObject.gameData);
        }

    }

    @Override
    public void onEvent(HomeSliderObject data) {
        setActionOnClick(data);
    }

    private void getActiveTabs() {
        try {
            Map<String, String> params = new HashMap<>();
            APIRequest apiRequest = new APIRequest(Url.GET_HOME_TABS + Constant.PREMIUM, Request.Method.GET, params, null, getContext());
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    handleTabsResponse(response, error);
                }
            });
        } catch (Exception e) {

        }
    }

    private void handleTabsResponse(String response, Exception error) {
        try {
            if (error != null) {

            } else {
                if (response != null) {
                    JSONObject jsonObjectResponse = new JSONObject(response);
                    String type =
                            jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                    if (type.equalsIgnoreCase("error")) {
                        String message =
                                jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                    } else if (type.equalsIgnoreCase("ok")) {
                        JSONArray jsonArrayMsg = jsonObjectResponse.getJSONArray("msg");
                        //handleTabsJSONArray(jsonArrayMsg);
                    }
                }
            }
        } catch (JSONException e) {
        }
    }

    private void handleTabsArray(ArrayList<FeedCombinedData> bigSliderList) {
        bigSliderObjectArrayList.clear();
        /*for (int i = 0; i < dataJSON.length(); i++) {
            HomeTabsList tabs = new HomeTabsList(dataJSON.getJSONObject(i));
            arrayListTabData.add(tabs);
        }*/
        bigSliderObjectArrayList.addAll(bigSliderList);
        if (bigSliderList.isEmpty()) {
            tlPremiumTabs.setVisibility(View.GONE);
            vPremium.setVisibility(View.GONE);
        } else if (bigSliderObjectArrayList.size() > 1) {
            tlPremiumTabs.setVisibility(View.VISIBLE);
            vPremium.setVisibility(View.VISIBLE);
        } else {
            tlPremiumTabs.setVisibility(View.GONE);
            vPremium.setVisibility(View.VISIBLE);
        }
        new Handler(Looper.getMainLooper()).post(this::setSliderData);
    }

    private void setSliderData() {
        // Set View Pager
        if (getActivity() != null) {
            ViewStatePagerAdapter viewPagerAdapter = new ViewStatePagerAdapter(getActivity().getSupportFragmentManager());
            for (int i = 0; i < bigSliderObjectArrayList.size(); i++) {
                String tabType = bigSliderObjectArrayList.get(i).title;
                Fragment tabFragment = HomeSliderTabFragment.getInstance(tabType, bigSliderObjectArrayList.get(i));
                ftList.add((HomeSliderTabFragment) tabFragment);
                viewPagerAdapter.addFragment(tabFragment, "Position " + i);
            }
            vPremium.setAdapter(viewPagerAdapter);
            tlPremiumTabs.setupWithViewPager(vPremium);
            setHomeTabText();
            tlPremiumTabs.addOnTabSelectedListener(this);
        }
    }

    @SuppressLint("ResourceType")
    private void setHomeTabText() {
        for (int i = 0; i < bigSliderObjectArrayList.size(); i++) {
            TextView tvCustomTab = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.custom_home_tab, null);
            String title = bigSliderObjectArrayList.get(i).title;
            tvCustomTab.setText(title);
            tvCustomTab.setTextColor(getResources().getColorStateList(R.drawable.tab_selector));
            if (i == 0)
                tvCustomTab.setTypeface(ResourcesCompat.getFont(getContext(), R.font.rubik_medium));
            tlPremiumTabs.getTabAt(i).setCustomView(tvCustomTab);
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        TextView tvTabTextView = (TextView) tab.getCustomView();
        if (tvTabTextView != null) {
            tvTabTextView.setTypeface(ResourcesCompat.getFont(view.getContext(), R.font.rubik_medium));
            for (Drawable drawable : tvTabTextView.getCompoundDrawables()) {
                if (drawable != null) {
                    drawable.mutate().setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(getActivity(), R.color.colorAccent), PorterDuff.Mode.SRC_IN));
                }
            }
        }
        nsvBuyTabFrag.fullScroll(View.FOCUS_UP);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        TextView tvTabTextView = (TextView) tab.getCustomView();
        if (tvTabTextView != null) {
            tvTabTextView.setTypeface(ResourcesCompat.getFont(view.getContext(), R.font.rubik_regular));
            for (Drawable drawable : tvTabTextView.getCompoundDrawables()) {
                if (drawable != null) {
                    drawable.mutate().setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(getActivity(), R.color.tab_text_grey), PorterDuff.Mode.SRC_IN));
                }
            }
        }
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    public void setNavigationMenuCallback(NavigationMenuCallback callback) {
        this.navigationMenuCallback = callback;
    }
}
