package com.app.itaptv.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.app.itaptv.API.APIManager;
import com.app.itaptv.API.APIMethods;
import com.app.itaptv.API.APIRequest;
import com.app.itaptv.API.APIResponse;
import com.app.itaptv.API.Url;
import com.app.itaptv.R;
import com.app.itaptv.activity.BrowserActivity;
import com.app.itaptv.activity.BuyDetailActivity;
import com.app.itaptv.activity.GameStartActivity;
import com.app.itaptv.activity.HomeActivity;
import com.app.itaptv.activity.LuckyWinnerActivity;
import com.app.itaptv.activity.TournamentRegistrationFormActivity;
import com.app.itaptv.activity.TournamentSummaryActivity;
import com.app.itaptv.activity.WebViewActivity;
import com.app.itaptv.custom_interface.OnFeedRefreshListener;
import com.app.itaptv.custom_interface.OnPlayerListener;
import com.app.itaptv.custom_interface.SliderListener;
import com.app.itaptv.custom_widget.DotsIndicatorDecoration;
import com.app.itaptv.holder.CustomAdSliderHolder;
import com.app.itaptv.holder.FeedCombinedGameHolder;
import com.app.itaptv.holder.FeedCombinedHolder;
import com.app.itaptv.holder.ListenHolder;
import com.app.itaptv.holder.PromotionHolder;
import com.app.itaptv.structure.AdFieldsData;
import com.app.itaptv.structure.AdMobData;
import com.app.itaptv.structure.FeedCombinedData;
import com.app.itaptv.structure.FeedContentData;
import com.app.itaptv.structure.GameData;
import com.app.itaptv.structure.HomeSliderObject;
import com.app.itaptv.structure.SubscriptionDetails;
import com.app.itaptv.utils.AlertUtils;
import com.app.itaptv.utils.Analyticals;
import com.app.itaptv.utils.Constant;
import com.app.itaptv.utils.CustomLinearLayoutManager;
import com.app.itaptv.utils.EmptyStateManager;
import com.app.itaptv.utils.GameDateValidation;
import com.app.itaptv.utils.LocalStorage;
import com.app.itaptv.utils.Log;
import com.app.itaptv.utils.SpacingItemDecoration;
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
import com.kalpesh.krecycleradapter.Interface.KRecyclerItemClickListener;
import com.kalpesh.krecycleradapter.Interface.KRecyclerViewHolderCallBack;
import com.kalpesh.krecycleradapter.KRecyclerViewAdapter;
import com.kalpesh.krecycleradapter.KRecyclerViewHolder;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemCreator;
import com.paginate.recycler.LoadingListItemSpanLookup;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.app.itaptv.structure.FeedCombinedData.FEED_TYPE_AD;
import static com.app.itaptv.structure.FeedCombinedData.FEED_TYPE_BUY;
import static com.app.itaptv.structure.FeedCombinedData.FEED_TYPE_CHANNEL;
import static com.app.itaptv.structure.FeedCombinedData.FEED_TYPE_CONTENT;
import static com.app.itaptv.structure.FeedCombinedData.FEED_TYPE_FIREWORKS;
import static com.app.itaptv.structure.FeedCombinedData.FEED_TYPE_GAME;
import static com.app.itaptv.structure.FeedCombinedData.FEED_TYPE_PROMOTION;
import static com.app.itaptv.structure.FeedContentData.POST_TYPE_AD;
import static com.app.itaptv.structure.FeedContentData.POST_TYPE_EPISODE;
import static com.app.itaptv.structure.FeedContentData.POST_TYPE_ORIGINALS;
import static com.app.itaptv.structure.FeedContentData.POST_TYPE_PLAYLIST;
import static com.app.itaptv.structure.FeedContentData.POST_TYPE_POST;
import static com.app.itaptv.structure.FeedContentData.POST_TYPE_STREAM;
import static com.app.itaptv.structure.FeedContentData.TAG;

public class HomeCombinedTabFragment extends Fragment {

    View view;
    RecyclerView rvListen;
    ProgressBar progressBar;
    RelativeLayout llprogressbar;
    OnFeedRefreshListener onFeedRefreshListener;
    OnPlayerListener onPlayerListener;
    KRecyclerViewAdapter adapterListen;
    SwipeRefreshLayout swipeRefreshLayout;
    AdView mAdView;
    AdRequest adRequest;
    ConstraintLayout clAdHolder;
    ImageView ivCustomAd;
    ImageView ivClose;
    PlayerView playerView;
    ImageView ivVolumeUp;
    ImageView ivVolumeOff;
    private boolean resumePlayer = false;
    List<View> playerViewList = new ArrayList<>();

    private ExoPlayer player;

    private String tabType = "listen";

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

    EmptyStateManager emptyState;
    private ArrayList<ViewGroup> arrayListListenData = new ArrayList<>();
    private ArrayList<FeedCombinedData> arrayListFeedData = new ArrayList<>();
    private ArrayList<FeedCombinedData> arrayListPageWiseFeedData = new ArrayList<>();
    private ArrayList<FeedContentData> arrayListFeedContent;
    private ArrayList<FeedContentData> arrayListChannelFeedContent;
    private ArrayList<HomeSliderObject> homeSliderObjectArrayList = new ArrayList<>();
    private ArrayList<FeedContentData> arrayListBuyFeedContent;
    private ArrayList<GameData> arrayListGameData;
    ArrayList<String> purchasedList = new ArrayList<>();
    HomeActivity homeActivity;
    LinearLayout llFireworks;
    NestedScrollView nsvCombinedTabFrag;

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
    boolean is_first_time_loading = true;

    private static int selectedDurationPositionCoins = 0;

    AlertDialog alertDialog;

    String sliderId;
    String feedId;
    boolean flag = false;

    public HomeCombinedTabFragment() {
    }

    public static HomeCombinedTabFragment newInstance() {
        return new HomeCombinedTabFragment();
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

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
    }

    /**
     * Initialize data
     */
    private void init() {
        onFeedRefreshListener = (OnFeedRefreshListener) getActivity();
        onPlayerListener = (OnPlayerListener) getActivity();
        progressBar = view.findViewById(R.id.progressBar);
        llprogressbar = view.findViewById(R.id.llprogressbar);
        llFireworks = view.findViewById(R.id.ll_fireworks);
        nsvCombinedTabFrag = view.findViewById(R.id.nsvCombinedTabFrag);
        swipeRefreshLayout = view.findViewById(R.id.swipeToRefresh);

        if (is_fragment_visible) {
            if (is_first_time_loading) {
                is_first_time_loading = false;
                getFeedsAPI();
            }
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                emptyState.hide();
                nextPageNo = 1;
                setListenListingRecyclerView();
                getFeedsAPI();
                swipeRefreshLayout.setRefreshing(false);
                swipeRefreshLayout.setEnabled(false);
            }
        });

        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            nsvCombinedTabFrag.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
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

        adRequest = new AdRequest.Builder().build();

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
        llFireworks.setVisibility(View.INVISIBLE);
        swipeRefreshLayout.setEnabled(false);

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
                llFireworks.setVisibility(View.INVISIBLE);
            } else {
                rvListen.setVisibility(View.VISIBLE);
                llFireworks.setVisibility(View.VISIBLE);
                emptyState.hide();
            }
        } else {
            rvListen.setVisibility(View.INVISIBLE);
            llFireworks.setVisibility(View.INVISIBLE);
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
            if (nextPageNo != 0 && nextPageNo < 3) {
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
        swipeRefreshLayout.setEnabled(true);
        try {
            if (error != null) {
                // showError(error.getMessage());
                updateEmptyState(error.getMessage());
            } else {
                if (response != null) {
                    //Log.e("response", response);

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
                                    FeedCombinedData feedCombinedData = new FeedCombinedData(jsonArrayContents.getJSONObject(i), position + i);
                                    arrayListPageWiseFeedData.add(feedCombinedData);
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
            //AlertUtils.showAlert(getString(R.string.label_error), errorMessage, null, getContext(), null);
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
        swipeRefreshLayout.setEnabled(true);
        if (page == 0) {
            if (is_fragment_visible)
                onFeedRefreshListener.refreshData(arrayListFeedData, 0);
        }
        new Thread(() -> {
            for (FeedCombinedData feedCombinedData : arrayListPageWiseFeedData) {
                switch (feedCombinedData.viewType) {
                    case FeedCombinedData.VIEW_TYPE_H_LIST:
                        new Handler(Looper.getMainLooper()).post(() -> setHorRecyclerView(feedCombinedData));
                        ;
                        break;
                    case FeedCombinedData.VIEW_TYPE_AD:
                        //new Handler(Looper.getMainLooper()).post(() -> setAdLayout(feedCombinedData.feedContentObjectData));
                        break;
                }
            }
            new Handler(Looper.getMainLooper()).post(() -> {
                adapterListen.notifyDataSetChanged();
                loading = false;
                page++;
            });
        }).start();
    }

    FeedCombinedGameHolder feedCombinedGameHolder;

    void updateSlider(int tabPosition) {
        //  onFeedRefreshListener.refreshData(arrayListFeedData, tabPosition);
    }

    private void setHorRecyclerView(final FeedCombinedData feedCombinedData) {
        try {
            View viewContent = View.inflate(requireContext(), R.layout.row_horizontal_list, null);

            LinearLayout llHzRow = viewContent.findViewById(R.id.llHzRow);
            TextView tvTitle = viewContent.findViewById(R.id.tvStoreName);
            RecyclerView rvContent = viewContent.findViewById(R.id.rvContent);
            ImageView rvBackImage = viewContent.findViewById(R.id.rvBackImage);
            TextView tvViewAll = viewContent.findViewById(R.id.tvViewAll);
            RelativeLayout rlParentTitle = viewContent.findViewById(R.id.rlParentTitle);

            LinearLayoutManager linearLayoutManager;
            tvViewAll.setVisibility(View.VISIBLE);
            llFireworks.setVisibility(View.VISIBLE);
            llHzRow.setVisibility(View.VISIBLE);
            tvTitle.setText(feedCombinedData.title);
            Log.d("TAG", feedCombinedData.title);
            Log.d("TAG", String.valueOf(feedCombinedData.id));

            if (String.valueOf(feedCombinedData.id).equals(Constant.getFeedId())) {
                String id = getResources().getResourceEntryName(viewContent.findViewById(R.id.adViewFeed).getId());
                showBannerAd(id, viewContent);
            } else if (String.valueOf(feedCombinedData.id).equals(Constant.getFreeFeedId())) {
                String id = getResources().getResourceEntryName(viewContent.findViewById(R.id.adViewFeed).getId());
                showBannerAd(id, viewContent);
            } else if (String.valueOf(feedCombinedData.id).equals(Constant.getFreeFeedIdSecond())) {
                String id = getResources().getResourceEntryName(viewContent.findViewById(R.id.adViewFeed).getId());
                showBannerAd(id, viewContent);
            }

            arrayListFeedContent = feedCombinedData.arrayListFeedContent;
            /*if (feedCombinedData.id==43415){
                FeedContentData feedContentData=new FeedContentData();
                //feedContentData.imgUrl=Constant.eSports;
                feedContentData.postTitle=Constant.eSportsTitle;
                arrayListFeedContent.add(1,feedContentData);
            }*/
            arrayListChannelFeedContent = feedCombinedData.arrayListChannelFeedContent;
            arrayListBuyFeedContent = feedCombinedData.arrayListBuyFeedContent;
            homeSliderObjectArrayList = feedCombinedData.homeSliderObjectArrayList;

            rlParentTitle.setPadding(Utility.getPercentPadding(getActivity(), 4), 20,
                    10, 30);
            rvContent.setPadding(Utility.getPercentPadding(getActivity(), 4), 0,
                    10, Utility.getPercentPadding(getActivity(), 4));

            KRecyclerViewAdapter adapter = null;
            feedId = String.valueOf(feedCombinedData.id);
            switch (feedCombinedData.feedType) {

                case FEED_TYPE_BUY:
                    if (arrayListBuyFeedContent.size() == 0) {
                        llHzRow.setVisibility(View.GONE);
                        return;
                    }
                    adapter = new KRecyclerViewAdapter(getContext(), arrayListBuyFeedContent, (viewGroup, i) -> {
                        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_feed_buy, viewGroup, false);
                        return new FeedCombinedHolder(layoutView, feedCombinedData);
                    }, (kRecyclerViewHolder, o, i) -> {
                        if (o instanceof FeedContentData) {
                            FeedContentData feedContentData = (FeedContentData) o;
                            //showAd(feedContentData.feedTabType);
                            SubscriptionDetails sd = LocalStorage.getUserSubscriptionDetails();
                            if (sd != null && sd.allow_rental != null) {
                                if (sd.allow_rental.equalsIgnoreCase(Constant.YES)) {
                                    if (feedContentData.postType.equals(POST_TYPE_ORIGINALS)) {
                                        Intent intent = new Intent(getActivity(), BuyDetailActivity.class);
                                        intent.putExtra(BuyDetailActivity.CONTENT_DATA, feedContentData);
                                        intent.putExtra(BuyDetailActivity.FROM_SLIDER, "sliderPageKey");
                                        getActivity().startActivityForResult(intent, BuyDetailActivity.REQUEST_CODE);
                                        showAd(sliderId);
                                    } else {
                                        playMediaItems(feedContentData);
                                        //playItems(feedCombinedData.arrayListBuyFeedContent, i, String.valueOf(feedCombinedData.id), Analyticals.CONTEXT_FEED);

                                    }
                                } else {
                                    Intent intent = new Intent(getActivity(), BuyDetailActivity.class);
                                    intent.putExtra(BuyDetailActivity.CONTENT_DATA, feedContentData);
                                    getActivity().startActivityForResult(intent, BuyDetailActivity.REQUEST_CODE);
                                }
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

                            /*Log.d("scrollOffset", String.valueOf(scrollOffset));
                            Log.d("xOffset", String.valueOf(xOffset));*/

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
                    break;

                case FEED_TYPE_CHANNEL:
                    if (arrayListChannelFeedContent.size() == 0) {
                        llHzRow.setVisibility(View.GONE);
                        return;
                    }
                    adapter = new KRecyclerViewAdapter(getContext(), arrayListChannelFeedContent, (viewGroup, i) -> {
                        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_feed, viewGroup, false);
                        return new FeedCombinedHolder(layoutView, feedCombinedData);
                    }, (kRecyclerViewHolder, o, i) -> {
                        FeedContentData feedContentData = (FeedContentData) o;
                        //showAd(feedContentData.feedTabType);
                        setPlaybackData(feedContentData, i, String.valueOf(feedCombinedData.id), Analyticals.CONTEXT_FEED);
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
                    rvContent.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    break;

                case FEED_TYPE_CONTENT:
                    if (arrayListFeedContent.size() == 0) {
                        llHzRow.setVisibility(View.GONE);
                        return;
                    }
                    adapter = new KRecyclerViewAdapter(getContext(), arrayListFeedContent, (viewGroup, i) -> {
                        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_feed, viewGroup, false);
                        return new FeedCombinedHolder(layoutView, feedCombinedData);
                    }, (kRecyclerViewHolder, o, i) -> {
                        FeedContentData feedContentData = (FeedContentData) o;
                        // showAd(feedContentData.feedTabType);
                        homeActivity = (HomeActivity) getActivity();
                        checkValidity(feedCombinedData, i);
                        //coinspayPrepayment(feedCombinedData,i);
                        //playItems(feedCombinedData.arrayListFeedContent, i, String.valueOf(feedCombinedData.id), Analyticals.CONTEXT_FEED);
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
                    adapter = new KRecyclerViewAdapter(getContext(), homeSliderObjectArrayList, (viewGroup, i) -> {
                        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_promotion_slider, viewGroup, false);
                        return new PromotionHolder(layoutView, feedCombinedData.promotionImage);
                    }, (kRecyclerViewHolder, o, i) -> {
                        if (o instanceof HomeSliderObject) {
                            HomeSliderObject homeSliderObject = (HomeSliderObject) o;
                            if (homeSliderObject.feedContentData != null) {
                                setActionOnSlider(homeSliderObject.feedContentData, String.valueOf(homeSliderObject.id));
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

                case FEED_TYPE_FIREWORKS:
                    /*tvViewAll.setVisibility(View.GONE);
                    IntegratedAdapter integratedAdapter = new IntegratedAdapter(getActivity().getSupportFragmentManager(), true);
                    IntegratedViewModel integratedViewModel;
                    integratedViewModel = ViewModelProviders.of(this).get(IntegratedViewModel.class);
                    integratedViewModel.getContent().observe(this, demoContents -> {
                        integratedAdapter.getContentFeed().clear();
                        integratedAdapter.getContentFeed().addAll(demoContents);
                        rvContent.setLayoutManager(new LinearLayoutManager(view.getContext()));
                        //rvContent.addItemDecoration(new SpacingItemDecoration(Constant.RV_HV_SPACING, 1, SpacingItemDecoration.LEFT));
                        rvContent.setAdapter(integratedAdapter);
                        integratedAdapter.notifyDataSetChanged();
                    });
                    FireworkSDK.addOnItemClickListener(i -> {
                        HomeActivity.getInstance().pausePlayer();
                    });*/
                    break;

                case FEED_TYPE_GAME:
                    arrayListGameData = feedCombinedData.arrayListGameData;
                    if (arrayListGameData.size() == 0) {
                        llHzRow.setVisibility(View.GONE);
                        return;
                    }
                    adapter = new KRecyclerViewAdapter(getContext(), arrayListGameData, (viewGroup, i) -> {
                        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_feed_play, viewGroup, false);
                        feedCombinedGameHolder = new FeedCombinedGameHolder(layoutView, feedCombinedData);
                        return feedCombinedGameHolder;
                    }, (kRecyclerViewHolder, o, i) -> {
                        if (o instanceof GameData) {
                            GameData gameData = (GameData) o;
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
                                        startActivityForResult(new Intent(getContext(), GameStartActivity.class).putExtra("Bundle", bundle), GameData.REQUEST_CODE_GAME);
                                    } else {
                                        if (gameData.winnersDeclared) {
                                            startActivity(new Intent(getActivity(), LuckyWinnerActivity.class).putExtra(LuckyWinnerActivity.KEY_GAME_ID, gameData.id));
                                            return;
                                        }
                                        AlertUtils.showToast(invalidDateMessage, Toast.LENGTH_SHORT, getContext());
                                    }
                                    break;

                                case GameData.QUIZE_TYPE_TURN_BASED: {
                                    Bundle bundle = new Bundle();
                                    bundle.putParcelable(GameStartActivity.GAME_DATA, gameData);
                                    startActivityForResult(new Intent(getContext(), GameStartActivity.class).putExtra("Bundle", bundle), GameData.REQUEST_CODE_GAME);
                                }
                                break;
                                case GameData.QUIZE_TYPE_HUNTER_GAMES: {
                                    Intent intent = new Intent(getActivity(), WebViewActivity.class).putExtra(WebViewActivity.GAME_URL, gameData.webviewUrl).putExtra(WebViewActivity.GAME_TITLE, gameData.postTitle);
                                    startActivityForResult(intent, WebViewActivity.GAME_REDIRECT_REQUEST_CODE);
                                }
                                break;
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
                switch (feedCombinedData.feedType) {
                    case FEED_TYPE_BUY:
                    case FEED_TYPE_CHANNEL:
                    case FEED_TYPE_CONTENT:
                        ViewAllFeedsFragment viewAllFeedsFragment1 = ViewAllFeedsFragment.newInstance(feedCombinedData.id, feedCombinedData.title, feedCombinedData.tabType, "feeds", feedCombinedData.tileShape,
                                feedCombinedData.feedType, Constant.SCREEN_HOME);
                        openFragment(viewAllFeedsFragment1);
                        break;
                    case FEED_TYPE_GAME:
                        ViewAllPlayFragment viewAllPlayFragment = ViewAllPlayFragment.newInstance(String.valueOf(feedCombinedData.id), feedCombinedData.postTitle, Constant.SCREEN_HOME);
                        viewAllPlayFragment.fromCombinedTab = true;
                        openFragment(viewAllPlayFragment);
                        break;
                }
            });
            arrayListListenData.add((ViewGroup) viewContent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // FireworkSDK.addOnItemClickListener(null);
        closePlayer();
    }

    private void setActionOnSlider(FeedContentData feedContentData, String feedId) {
        /* if (feedContentData.feedTabType.equals(Constant.TAB_WATCH)) {*/
        homeActivity = (HomeActivity) getActivity();
        if (feedContentData.postExcerpt.equalsIgnoreCase("paid")) {
            // logic for buy/paid item
            SubscriptionDetails sd = LocalStorage.getUserSubscriptionDetails();
            if (sd != null && sd.allow_rental != null && !sd.allow_rental.equals("") && !feedContentData.isTrailer) {
                if (sd.allow_rental.equalsIgnoreCase(Constant.YES)) {
                    if (feedContentData.postType.equals(POST_TYPE_ORIGINALS)) {
                        Intent intent = new Intent(getActivity(), BuyDetailActivity.class);
                        intent.putExtra(BuyDetailActivity.CONTENT_DATA, feedContentData);
                        intent.putExtra(BuyDetailActivity.FROM_SLIDER, "sliderPageKey");
                        getActivity().startActivityForResult(intent, BuyDetailActivity.REQUEST_CODE);
                        showAd(sliderId);
                    } else {
                        ArrayList<FeedContentData> arrayListFeedContentData = new ArrayList<>();
                        arrayListFeedContentData.add(feedContentData);
                        homeActivity.showAudioSongExpandedView(arrayListFeedContentData, 0, feedId, Analyticals.CONTEXT_SLIDER, "");

                    }
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
            }
        }
        /*} else if (feedContentData.feedTabType.equals(Constant.TAB_BUY)) {
            Intent intent = new Intent(getActivity(), BuyDetailActivity.class);
            intent.putExtra(BuyDetailActivity.CONTENT_DATA, feedContentData);
            getActivity().startActivityForResult(intent, BuyDetailActivity.REQUEST_CODE);
        }*/
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

    private void playItems(ArrayList<FeedContentData> itemsToPlay, int position, String feedId, String pageType) {
        if (getActivity() != null && getActivity() instanceof HomeActivity) {
            homeActivity = (HomeActivity) getActivity();
            if (homeActivity != null) {
                if (itemsToPlay.get(position).contentOrientation.equalsIgnoreCase("vertical")) {
                    homeActivity.openVerticalPlayer(itemsToPlay, position);
                } else {
                    switch (itemsToPlay.get(position).postType) {
                        case POST_TYPE_PLAYLIST:
                            homeActivity.showPlaylistExpandedView(itemsToPlay.get(position).postId, String.valueOf(itemsToPlay.get(position).iswatchlisted));
                            break;
                        case POST_TYPE_ORIGINALS:
                            homeActivity.showSeriesExpandedView(itemsToPlay.get(position).postId, "0", "0", String.valueOf(itemsToPlay.get(position).iswatchlisted));
                            break;
                        case POST_TYPE_EPISODE:
                            homeActivity.showSeriesExpandedView(itemsToPlay.get(position).seriesId, itemsToPlay.get(position).seasonId, itemsToPlay.get(position).episodeId, "");
                            break;
                        case POST_TYPE_STREAM:
                            homeActivity.showChannelExpandedView(itemsToPlay.get(position), 1, feedId, pageType);
                            break;
                        case POST_TYPE_POST:
                            //homeActivity.makePlaylist(itemsToPlay, position, feedId, pageType, "");
                            homeActivity.playItems(itemsToPlay, position, feedId, pageType, "");
                            break;
                        case POST_TYPE_AD:
                            //homeActivity.makePlaylist(itemsToPlay, position, feedId, pageType, "");
                            homeActivity.playItems(itemsToPlay, position, feedId, pageType, "");
                            break;
                    }
                }
            }
        }
    }

    private void setPlaybackData(FeedContentData feedContentData, int position, String feed_id, String pagetype) {
        if (arrayListFeedData.size() > 0) {
            if (getActivity() != null && getActivity() instanceof HomeActivity) {
                homeActivity = (HomeActivity) getActivity();
                if (!feedContentData.postType.equals("")) {
                    switch (feedContentData.postType) {
                        case FeedContentData.POST_TYPE_POST:
                            if (feedContentData.feedPosition >= arrayListFeedData.size()) return;
                            if (arrayListFeedData.get(feedContentData.feedPosition).contentType.equals(FeedContentData.CONTENT_TYPE_ALL)) {
                                ArrayList<FeedContentData> arrayListAllTypeFeedContentData = new ArrayList<>();
                                arrayListAllTypeFeedContentData.add(feedContentData);
                                homeActivity.showAudioSongExpandedView(arrayListAllTypeFeedContentData, 0, feed_id, pagetype, String.valueOf(feedContentData.iswatchlisted));
                                //homeActivity.playItems(arrayListAllTypeFeedContentData, 0, feed_id, pagetype, String.valueOf(feedContentData.iswatchlisted));
                                return;
                            }
                            homeActivity.showAudioSongExpandedView(arrayListFeedData.get(feedContentData.feedPosition).arrayListFeedContent, position, feed_id, pagetype, "");
                            //homeActivity.showAudioSongExpandedView(arrayListFeedData.get(feedContentData.feedPosition).arrayListFeedContent, position, feed_id, pagetype, "");
                            break;

                        case POST_TYPE_ORIGINALS:
                            homeActivity.showSeriesExpandedView(feedContentData.postId, "0", "0", String.valueOf(feedContentData.iswatchlisted));
                            break;

                        case POST_TYPE_EPISODE:
                            homeActivity.showSeriesExpandedView(feedContentData.seriesId, feedContentData.seasonId, feedContentData.episodeId, "");
                            break;

                        case POST_TYPE_PLAYLIST:
                            homeActivity.showPlaylistExpandedView(feedContentData.postId, String.valueOf(feedContentData.iswatchlisted));
                            break;

                        case POST_TYPE_STREAM:
                            //playChannelAPI(feedContentData, 1, feed_id, pagetype);
                            homeActivity.showChannelExpandedView(feedContentData, 1, feed_id, pagetype);
                            break;
                    }
                } else {
                    switch (feedContentData.taxonomy) {
                        case FeedContentData.TAXONOMY_SEASONS:
                            //homeActivity.showSeriesExpandedView(feedContentData.seriesId, feedContentData.seasonId, feedContentData.episodeId);
                            homeActivity.showSeriesExpandedView(feedContentData.seriesId, feedContentData.seasonId, feedContentData.episodeId, "");
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

    private void setChannelsPlaybackData(FeedContentData feedContentData, int position) {
        if (getActivity() != null && getActivity() instanceof HomeActivity) {
            homeActivity = (HomeActivity) getActivity();
            homeActivity.showChannelExpandedView(feedContentData, 1, String.valueOf(arrayListChannelFeedContent.get(feedContentData.feedPosition).id), Analyticals.CONTEXT_CHANNEL);
            /*switch ("channel") {
                case "channel":
            playChannelAPI(feedContentData, 1);
            homeActivity.showChannelExpandedView(feedContentData, 1, String.valueOf(arrayListChannelFeedContent.get(feedContentData.feedPosition).id), Analyticals.CONTEXT_CHANNEL);
            break;*/
                /*case "category":
                    /*if (arrayListChannelOrCatData.size() > 0) {
                        homeActivity.playItems(arrayListChannelOrCatData.get(feedContentData.feedPosition).arrayListFeedContent, position, String.valueOf(arrayListChannelOrCatData.get(feedContentData.feedPosition).id), arrayListChannelOrCatData.get(feedContentData.feedPosition).tabType);
                    }*/

            /*if (arrayListChannelFeedContent.size() > 0) {
                if (feedContentData.postType.equalsIgnoreCase(FeedContentData.POST_TYPE_ORIGINALS)) {
                    homeActivity.showSeriesExpandedView(feedContentData.postId, feedContentData.seasonId, feedContentData.episodeId, String.valueOf(feedContentData.iswatchlisted));
                } else if (feedContentData.postType.equalsIgnoreCase(FeedContentData.POST_TYPE_PLAYLIST)) {
                    homeActivity.showPlaylistExpandedView(feedContentData.postId, String.valueOf(feedContentData.iswatchlisted));
                } else {
                    ArrayList<FeedContentData> arrayList = new ArrayList<>();
                    arrayList.add(feedContentData);
                    //homeActivity.playItems(arrayListChannelOrCatData.get(feedContentData.feedPosition).arrayListFeedContent, position, String.valueOf(arrayListChannelOrCatData.get(feedContentData.feedPosition).id), arrayListChannelOrCatData.get(feedContentData.feedPosition).tabType,"");
                    homeActivity.showAudioSongExpandedView(arrayList, 0, String.valueOf(arrayListChannelFeedContent.get(feedContentData.feedPosition).id), Analyticals.CONTEXT_FEED, String.valueOf(feedContentData.iswatchlisted));

                }
            }*/

            //break;

        }
    }


    /**
     * This method adds the horizontal recycler view dynamically to the parent layout of the page
     *
     * @param feedCombinedData Feed Data
     */
    private void setHorizontalBuyRecyclerView(final FeedCombinedData feedCombinedData) {
        try {

            View viewContent = View.inflate(getContext(), R.layout.row_horizontal_list, null);

            LinearLayout llHzRow = viewContent.findViewById(R.id.llHzRow);
            TextView tvTitle = viewContent.findViewById(R.id.tvStoreName);
            RecyclerView rvContent = viewContent.findViewById(R.id.rvContent);
            TextView tvViewAll = viewContent.findViewById(R.id.tvViewAll);
            RelativeLayout rlParentTitle = viewContent.findViewById(R.id.rlParentTitle);

            ArrayList<FeedContentData> arrayListFeedContent = feedCombinedData.arrayListFeedContent;

            if (arrayListFeedContent.size() == 0) {
                llHzRow.setVisibility(View.GONE);
                return;
            }

            llHzRow.setVisibility(View.VISIBLE);
            tvTitle.setText(feedCombinedData.title);
            rvContent.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            rvContent.setNestedScrollingEnabled(false);
            rvContent.addItemDecoration(new SpacingItemDecoration(Constant.RV_HV_SPACING, 1, SpacingItemDecoration.LEFT));

            KRecyclerViewAdapter adapter = new KRecyclerViewAdapter(getContext(), arrayListFeedContent, new KRecyclerViewHolderCallBack() {
                @Override
                public KRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                    View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_feed_buy, viewGroup, false);
                    return new FeedCombinedHolder(layoutView, feedCombinedData);
                }
            }, new KRecyclerItemClickListener() {
                @Override
                public void onRecyclerItemClicked(KRecyclerViewHolder kRecyclerViewHolder, Object o, int i) {
                    if (o instanceof FeedContentData) {
                        FeedContentData feedContentData = (FeedContentData) o;
                        Intent intent = new Intent(getActivity(), BuyDetailActivity.class);
                        intent.putExtra(BuyDetailActivity.CONTENT_DATA, feedContentData);
                        getActivity().startActivityForResult(intent, BuyDetailActivity.REQUEST_CODE);
                    }
                }
            });

            rvContent.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            // View more click disabled until further action
           /* rlParentTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewAllFeedsFragment viewAllFeedsFragment = ViewAllFeedsFragment.newInstance(feedCombinedData.id, feedCombinedData.title, feedCombinedData.tabType, "feeds");
                    openFragment(viewAllFeedsFragment);
                }
            });*/
            arrayListListenData.add((ViewGroup) viewContent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setHorizontalRecyclerView(final FeedCombinedData feedCombinedData) {
        try {

            View viewContent = View.inflate(getContext(), R.layout.row_horizontal_list, null);

            LinearLayout llHzRow = viewContent.findViewById(R.id.llHzRow);
            TextView tvTitle = viewContent.findViewById(R.id.tvStoreName);
            RecyclerView rvContent = viewContent.findViewById(R.id.rvContent);
            TextView tvViewAll = viewContent.findViewById(R.id.tvViewAll);
            RelativeLayout rlParentTitle = viewContent.findViewById(R.id.rlParentTitle);
            ImageView ivPremiumLogo = viewContent.findViewById(R.id.iv_premium_logo);

            ArrayList<FeedContentData> arrayListFeedContent = feedCombinedData.arrayListFeedContent;

            if (arrayListFeedContent.size() == 0) {
                llHzRow.setVisibility(View.GONE);
                return;
            }

            llHzRow.setVisibility(View.VISIBLE);
            tvTitle.setText(feedCombinedData.title);
            rvContent.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            rvContent.setNestedScrollingEnabled(false);
            rvContent.addItemDecoration(new SpacingItemDecoration(Constant.RV_HV_SPACING, SpacingItemDecoration.LEFT));
            // rvContent.setId(position);

            KRecyclerViewAdapter adapter = new KRecyclerViewAdapter(getContext(), arrayListFeedContent, (viewGroup, i) -> {
                View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_feed, viewGroup, false);
                return new FeedCombinedHolder(layoutView, feedCombinedData);
            }, (kRecyclerViewHolder, o, i) -> {
                FeedContentData feedContentData = (FeedContentData) o;
                setPlaybackData(feedContentData, i, String.valueOf(feedCombinedData.id), Analyticals.CONTEXT_FEED);
            });
            rvContent.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            /*rlParentTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewAllFeedsFragment viewAllFeedsFragment = ViewAllFeedsFragment.newInstance(feedCombinedData.id, feedCombinedData.title, feedCombinedData.tabType, "feeds");
                    openFragment(viewAllFeedsFragment);
                }
            });*/
            arrayListListenData.add((ViewGroup) viewContent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setHorizontalGameRecyclerView(final FeedCombinedData feedGameData) {
        try {

            View viewContent = View.inflate(getContext(), R.layout.row_horizontal_list, null);

            LinearLayout llHzRow = viewContent.findViewById(R.id.llHzRow);
            TextView tvTitle = viewContent.findViewById(R.id.tvStoreName);
            RecyclerView rvContent = viewContent.findViewById(R.id.rvContent);
            TextView tvViewAll = viewContent.findViewById(R.id.tvViewAll);
            RelativeLayout rlParentTitle = viewContent.findViewById(R.id.rlParentTitle);
            ImageView ivPremiumLogo = viewContent.findViewById(R.id.iv_premium_logo);

            ArrayList<GameData> arrayListGameData = feedGameData.arrayListGameData;

            if (arrayListGameData.size() == 0) {
                llHzRow.setVisibility(View.GONE);
                return;
            }

            llHzRow.setVisibility(View.VISIBLE);
            tvTitle.setText(feedGameData.title);
            rvContent.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            rvContent.setNestedScrollingEnabled(false);
            rvContent.addItemDecoration(new SpacingItemDecoration(Constant.RV_HV_SPACING, SpacingItemDecoration.LEFT));
            // rvContent.setId(position);

            KRecyclerViewAdapter adapter = new KRecyclerViewAdapter(getContext(), arrayListGameData, new KRecyclerViewHolderCallBack() {
                @Override
                public KRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                    View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_feed_play, viewGroup, false);
                    feedCombinedGameHolder = new FeedCombinedGameHolder(layoutView, feedGameData);
                    return feedCombinedGameHolder;
                }
            }, new KRecyclerItemClickListener() {
                @Override
                public void onRecyclerItemClicked(KRecyclerViewHolder kRecyclerViewHolder, Object o, int i) {
                    if (o instanceof GameData) {
                        GameData gameData = (GameData) o;
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
                                    startActivityForResult(new Intent(getContext(), GameStartActivity.class).putExtra("Bundle", bundle), GameData.REQUEST_CODE_GAME);
                                } else {
                                    if (gameData.winnersDeclared) {
                                        startActivity(new Intent(getActivity(), LuckyWinnerActivity.class).putExtra(LuckyWinnerActivity.KEY_GAME_ID, gameData.id));
                                        return;
                                    }
                                    AlertUtils.showToast(invalidDateMessage, Toast.LENGTH_SHORT, getContext());
                                }
                                break;

                            case GameData.QUIZE_TYPE_TURN_BASED: {
                                Bundle bundle = new Bundle();
                                bundle.putParcelable(GameStartActivity.GAME_DATA, gameData);
                                startActivityForResult(new Intent(getContext(), GameStartActivity.class).putExtra("Bundle", bundle), GameData.REQUEST_CODE_GAME);
                            }
                            break;
                            case GameData.QUIZE_TYPE_HUNTER_GAMES: {
                                Intent intent = new Intent(getActivity(), WebViewActivity.class).putExtra(WebViewActivity.GAME_URL, gameData.webviewUrl).putExtra(WebViewActivity.GAME_TITLE, gameData.postTitle);
                                startActivityForResult(intent, WebViewActivity.GAME_REDIRECT_REQUEST_CODE);
                            }
                            break;
                        }
                    }

                }
            });

            rvContent.setAdapter(adapter);
            // adapter.notifyDataSetChanged();
            // View more click disabled until further action
            /*rlParentTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewAllPlayFragment viewAllPlayFragment = ViewAllPlayFragment.newInstance(String.valueOf(feedGameData.id), feedGameData.postTitle);
                    openFragment(viewAllPlayFragment);
                }
            });*/
            arrayListListenData.add((ViewGroup) viewContent);
        } catch (Exception e) {
            e.printStackTrace();
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

    private void playMediaItems(FeedContentData feedContentData) {
        homeActivity = (HomeActivity) getActivity();
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

    private void checkValidity(FeedCombinedData feedCombinedData, int position) {
        SubscriptionDetails sd = LocalStorage.getUserSubscriptionDetails();
        purchasedList = LocalStorage.getPurchasedArrayList("savedPurchased", getContext());
        if (feedCombinedData.arrayListFeedContent.get(position).contentType.equalsIgnoreCase(Constant.eSportsTitle)) {
            getUserRegisteredTournamentInfo(feedCombinedData.arrayListFeedContent.get(position).postId);
        } else if (sd != null && sd.allow_rental != null) {
            if (sd.allow_rental.equalsIgnoreCase(Constant.YES)) {
                if (feedCombinedData.arrayListFeedContent.get(position).postType.equals(POST_TYPE_ORIGINALS)) {
                    Intent intent = new Intent(getActivity(), BuyDetailActivity.class);
                    intent.putExtra(BuyDetailActivity.CONTENT_DATA, feedCombinedData.arrayListFeedContent.get(position));
                    intent.putExtra(BuyDetailActivity.FROM_SLIDER, "sliderPageKey");
                    getActivity().startActivityForResult(intent, BuyDetailActivity.REQUEST_CODE);
                    showAd(sliderId);
                } else {
                    playItems(feedCombinedData.arrayListFeedContent, position, String.valueOf(feedCombinedData.id), Analyticals.CONTEXT_FEED);
                }
            } else {
                if (feedCombinedData.arrayListFeedContent.get(position).postExcerpt.equalsIgnoreCase("free")) {
                    playItems(feedCombinedData.arrayListFeedContent, position, String.valueOf(feedCombinedData.id), Analyticals.CONTEXT_FEED);
                } else if (feedCombinedData.arrayListFeedContent.get(position).postExcerpt.equalsIgnoreCase("paid")) {
                    if (feedCombinedData.arrayListFeedContent.get(position).arrayListPriceData.size() > 0) {
                        if (feedCombinedData.arrayListFeedContent.get(position).arrayListPriceData.get(0).costType.equalsIgnoreCase("coins")) {
                            if (purchasedList != null && !purchasedList.isEmpty()) {
                                //for (int i = 0; i < purchasedList.size(); i++) {
                                if (purchasedList.contains(feedCombinedData.arrayListFeedContent.get(position).postId)) {
                                    homeActivity = (HomeActivity) getActivity();
                                    //homeActivity.playMedia(feedCombinedData.arrayListFeedContent.get(position));
                                    homeActivity.updatePurchasedList(feedCombinedData.arrayListFeedContent, position, String.valueOf(feedCombinedData.id), Analyticals.CONTEXT_FEED);
                                } else {
                                    selectedDurationPositionCoins = 0;
                                    String coins = feedCombinedData.arrayListFeedContent.get(position).arrayListPriceData.get(0).finalCost;
                                    showPurchaseDialog(getContext(), coins, feedCombinedData, position);
                                }
                                // }
                            } else {
                                selectedDurationPositionCoins = 0;
                                String coins = feedCombinedData.arrayListFeedContent.get(position).arrayListPriceData.get(0).finalCost;
                                showPurchaseDialog(getContext(), coins, feedCombinedData, position);
                            }
                        } else {
                            //homeActivity.playMedia(feedCombinedData.arrayListFeedContent.get(position));
                            playItems(feedCombinedData.arrayListFeedContent, position, String.valueOf(feedCombinedData.id), Analyticals.CONTEXT_FEED);
                        }
                    } else {
                        //homeActivity.playMedia(feedCombinedData.arrayListFeedContent.get(position));
                        playItems(feedCombinedData.arrayListFeedContent, position, String.valueOf(feedCombinedData.id), Analyticals.CONTEXT_FEED);
                    }
                }
            }
        }
    }

    private void showPurchaseDialog(Context context, String message, FeedCombinedData feedCombinedData, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_purchase_confirmation, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setView(view);
        alertDialog = builder.create();
        alertDialog.setCancelable(true);
        alertDialog.show();

        TextView tvTitle = alertDialog.findViewById(R.id.tv_message);
        ImageView btPositive = alertDialog.findViewById(R.id.iv_yes);
        ImageView btNegative = alertDialog.findViewById(R.id.iv_no);
        tvTitle.setText(message + getString(R.string.icoins_deducted));

        btPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                coinspayPrepayment(feedCombinedData, position);
                alertDialog.dismiss();
            }
        });

        btNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

    }

    private void coinspayPrepayment(FeedCombinedData feedCombinedData, int position) {
        try {
            String id = String.valueOf(feedCombinedData.arrayListFeedContent.get(position).postId);
            Map<String, String> params = new HashMap<>();
            params.put("ID", id);
            params.put("payment_mode", "icoins");
            params.put("pricing_option", String.valueOf(selectedDurationPositionCoins));

            APIRequest apiRequest = new APIRequest(Url.INITIATE_ORDER, Request.Method.POST, params, null, getContext());
            apiRequest.showLoader = true;
            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    if (error == null) {
                        handleCoinsPayPrepaymentResponse(response, error, feedCombinedData, position);
                    } else {
                        Utility.showError(error.toString(), getContext());
                    }
                }
            });
        } catch (Exception e) {
            Utility.showError(getString(R.string.failed_to_initiate_payment), getContext());
        }
    }

    private void handleCoinsPayPrepaymentResponse(@Nullable String response, @Nullable Exception error, FeedCombinedData feedCombinedData, int position) {
        try {
            if (error != null) {
                Utility.showError(error.getMessage(), getContext());
            } else {
                if (response != null) {
                    JSONObject jsonObjectResponse = new JSONObject(response);
                    String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                    if (type.equalsIgnoreCase("error")) {
                        String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                        //Utility.showError(message,getContext());
                        if (message.contains("purchased")) {
                            //homeActivity.playMedia(feedCombinedData.arrayListFeedContent.get(position));
                            playItems(feedCombinedData.arrayListFeedContent, position, String.valueOf(feedCombinedData.id), Analyticals.CONTEXT_FEED);
                        } else {
                            showInsufficientCoinsDialog(message);
                        }
                    } else if (type.equalsIgnoreCase("ok")) {
                        /// new flow checking to be done here
                        JSONObject object = jsonObjectResponse.getJSONObject("msg");
                        String status = object.has("status") ? object.getString("status") : "";
                        if (status.equalsIgnoreCase("SUCCESS")) {
                            //flow for coin  only
                            homeActivity = (HomeActivity) getActivity();
                            //homeActivity.getPurchaseHistory();
                            //HomeActivity.purchaseNextPageNo = 1;
                            //homeActivity.getPurchaseHistory();
                            homeActivity.getWalletBalance();
                            purchasedList.add(feedCombinedData.arrayListFeedContent.get(position).postId);
                            LocalStorage.savePurchasedArrayList(purchasedList, "savedPurchased", getContext());
                            //homeActivity.playMedia(feedCombinedData.arrayListFeedContent.get(position));
                            //playItems(feedCombinedData.arrayListFeedContent, position, String.valueOf(feedCombinedData.id), Analyticals.CONTEXT_FEED);
                            homeActivity.updatePurchasedList(feedCombinedData.arrayListFeedContent, position, String.valueOf(feedCombinedData.id), Analyticals.CONTEXT_FEED);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            Utility.showError(e.toString(), getContext());
        }
    }


    private void showInsufficientCoinsDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setView(R.layout.dialog_custom);
        alertDialog = builder.create();
        alertDialog.setCancelable(true);
        alertDialog.show();

        TextView tvTitle = alertDialog.findViewById(R.id.tvStoreName);
        TextView btPositive = alertDialog.findViewById(R.id.btPositive);
        tvTitle.setText(message);
        btPositive.setText(R.string.ok);
        btPositive.setVisibility(View.VISIBLE);


        btPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.d("HomeListenTabFragment", "HomeListenTabFragment" + isVisibleToUser);
        if (isVisibleToUser) {
            /*if (getActivity() != null && getActivity() instanceof HomeActivity) {
                homeActivity = (HomeActivity) getActivity();
                if (homeActivity != null) {
                    homeActivity.showAd();
                }
            }*/
            is_fragment_visible = true;
            if (onFeedRefreshListener != null)
                updateSlider(0);
        } else {
            is_fragment_visible = false;
        }

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
                    HomeActivity.getInstance().getMediaData(String.valueOf(adFieldsData.contentId),adFieldsData.contentType);
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

    public void showBannerAd(String id, View viewContent) {
        mAdView = viewContent.findViewById(R.id.adViewFeed);
        clAdHolder = viewContent.findViewById(R.id.cl_ad_holder);
        ivCustomAd = viewContent.findViewById(R.id.ivCustomAd);
        ivClose = viewContent.findViewById(R.id.ivClose);
        playerView = viewContent.findViewById(R.id.playerView);
        ivVolumeUp = viewContent.findViewById(R.id.ivVolumeUp);
        ivVolumeOff = viewContent.findViewById(R.id.ivVolumeOff);
        rvSliderAd = viewContent.findViewById(R.id.rvSliderAd);

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
                                    if (player != null && player.isPlaying()) {
                                        ivVolumeUp.setVisibility(View.GONE);
                                        ivVolumeOff.setVisibility(View.VISIBLE);
                                        player.setVolume(0.0f);
                                    }
                                }
                            });
                            ivVolumeOff.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (player != null && player.isPlaying()) {
                                        ivVolumeOff.setVisibility(View.GONE);
                                        ivVolumeUp.setVisibility(View.VISIBLE);
                                        player.setVolume(1.0f);
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
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getContext(), getContext().getPackageName(), bandwidthMeter);
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
                Log.i(TAG, String.valueOf(error));
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
        playerViewList.add(playerView);
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

    @Override
    public void onResume() {
        super.onResume();
        if (player != null && resumePlayer) {
            player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
            player.setPlayWhenReady(true);
            playerView.setPlayer(player);
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

    public void panelListener(boolean isPanelExpanded) {
        if (isPanelExpanded) {
            if (playerViewList != null) {
                for (int i = 0; i < playerViewList.size(); i++) {
                    playerViewList.get(i).setVisibility(View.GONE);
                }
            }
        } else {
            if (playerViewList != null) {
                for (int i = 0; i < playerViewList.size(); i++) {
                    playerViewList.get(i).setVisibility(View.VISIBLE);
                }
            }
        }
    }
}
