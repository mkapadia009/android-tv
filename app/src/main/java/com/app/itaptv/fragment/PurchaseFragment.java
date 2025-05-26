package com.app.itaptv.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
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
import com.app.itaptv.MyApp;
import com.app.itaptv.R;
import com.app.itaptv.activity.BrowserActivity;
import com.app.itaptv.activity.HomeActivity;
import com.app.itaptv.custom_interface.SliderListener;
import com.app.itaptv.custom_widget.DotsIndicatorDecoration;
import com.app.itaptv.holder.CustomAdSliderHolder;
import com.app.itaptv.holder.PurchaseHolder;
import com.app.itaptv.structure.AdFieldsData;
import com.app.itaptv.structure.AdMobData;
import com.app.itaptv.structure.FeedContentData;
import com.app.itaptv.structure.PurchaseData;
import com.app.itaptv.utils.AlertUtils;
import com.app.itaptv.utils.Analyticals;
import com.app.itaptv.utils.Constant;
import com.app.itaptv.utils.CustomLinearLayoutManager;
import com.app.itaptv.utils.EmptyStateManager;
import com.app.itaptv.utils.LocalStorage;
import com.app.itaptv.utils.SpacingItemDecoration;
import com.app.itaptv.utils.Utility;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by poonam on 13/12/18.
 */

public class PurchaseFragment extends Fragment {

    View view;
    RecyclerView rvList;
    ProgressBar progressBar;
    SwipeRefreshLayout srlRefresh;
    AdView mAdView;
    AdRequest adRequest;
    ConstraintLayout clAdHolder;
    ImageView ivCustomAd;
    ImageView ivClose;
    ImageView ivVolumeUp;
    ImageView ivVolumeOff;

    private boolean resumePlayer = false;

    private ExoPlayer player;
    private PlayerView playerView;

    HomeActivity activity;
    ArrayList<PurchaseData> arrayListPurchaseData = new ArrayList<>();
    ArrayList<FeedContentData> arrayListFeedContent = new ArrayList<>();

    EmptyStateManager emptyState;
    KRecyclerViewAdapter adapter;

    // Pagination
    private static final int GRID_SPAN = 1;
    private boolean loading = false;
    private int page = 0;
    private Handler handler;
    private Paginate paginate;
    protected long networkDelay = 2000;
    protected boolean customLoadingListItem = false;
    int nextPageNo = 1;
    String purchaseUrl;

    HomeActivity homeActivity;

    private RecyclerView rvSliderAd;
    private CustomLinearLayoutManager layoutManager;
    private KRecyclerViewAdapter adapterSliderAd;
    private boolean flagDecoration = false;
    ArrayList<AdFieldsData> adSliderObjectArrayList = new ArrayList<>();

    private String sliderId;
    // for slider
    private Runnable runnable = null;
    private int count = 0;
    private int visibleItemPosition;
    private int sliderPosition = -1;
    private boolean shouldSlide = true;
    private boolean flag = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //if (view == null) {
        view = inflater.inflate(R.layout.fragment_purchase, container, false);
        init();
        //}
        return view;
    }

    public static PurchaseFragment newInstance() {
        return new PurchaseFragment();
    }

    private void init() {
        progressBar = view.findViewById(R.id.progressBar);
        srlRefresh = view.findViewById(R.id.srlRefresh);
        rvList = view.findViewById(R.id.rvList);
        rvList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvList.addItemDecoration(new SpacingItemDecoration(Constant.RV_HV_SPACING));
        srlRefresh.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        activity = (HomeActivity) getActivity();

        mAdView = view.findViewById(R.id.adViewPurchaseFrag);
        clAdHolder = view.findViewById(R.id.cl_ad_holder);
        ivCustomAd = view.findViewById(R.id.ivCustomAd);
        ivClose = view.findViewById(R.id.ivClose);
        playerView = view.findViewById(R.id.playerView);
        ivVolumeUp = view.findViewById(R.id.ivVolumeUp);
        ivVolumeOff = view.findViewById(R.id.ivVolumeOff);
        rvSliderAd = view.findViewById(R.id.rvSliderAd);
        adRequest = new AdRequest.Builder().build();

        String id = getResources().getResourceEntryName(view.findViewById(R.id.adViewPurchaseFrag).getId());
        showBannerAd(id);

        srlRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (paginate != null) {
                    paginate.unbind();
                }
                if (handler != null) {
                    handler.removeCallbacks(fakeCallback);
                }
                initializePagination();
                srlRefresh.setRefreshing(false);
            }
        });

        setUpEmptyState();
        setRecyclerView();
    }

    private void setRecyclerView() {
        adapter = new KRecyclerViewAdapter(getActivity(), arrayListPurchaseData, new KRecyclerViewHolderCallBack() {
            @Override
            public KRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_purchase, viewGroup, false);
                return new PurchaseHolder(layoutView);
            }
        }, new KRecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClicked(KRecyclerViewHolder kRecyclerViewHolder, Object o, int i) {
                if (o instanceof PurchaseData) {
                    PurchaseData purchaseData = (PurchaseData) o;
                    if (purchaseData.isExpired) {
                        showExpiredDialog();
                        return;
                    }
                    playMediaItems(purchaseData);
                }

            }
        });
        rvList.setAdapter(adapter);
        initializePagination();
    }

    private void getPurchaseHistory() {
        try {
            Map<String, String> params = new HashMap<>();
            APIRequest apiRequest = new APIRequest(purchaseUrl + "&page_no=" + nextPageNo,
                    Request.Method.GET, params, null, getContext());
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, new APIResponse() {

                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    progressBar.setVisibility(View.GONE);
                    srlRefresh.setVisibility(View.VISIBLE);
                    emptyState.hide();
                    handlePurchaseHistory(response, error);

                }
            });
        } catch (Exception e) {
            progressBar.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }

    private void handlePurchaseHistory(@Nullable String response, @Nullable Exception error) {
        try {
            if (error != null) {
                updateEmptyState(error.getMessage());
            } else {
                if (response != null) {
                    JSONObject jsonObjectResponse = new JSONObject(response);
                    String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                    if (type.equalsIgnoreCase("error")) {
                        String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                        showError(message);
                    } else if (type.equalsIgnoreCase("ok")) {
                        try {
                            JSONObject jsonArrayMsg = jsonObjectResponse.getJSONObject("msg");
                            JSONArray jsonArrayOrders = jsonArrayMsg.getJSONArray("orders");
                            nextPageNo = jsonArrayMsg.has("next_page") ? jsonArrayMsg.getInt("next_page") : 0;

                            if (jsonArrayOrders.length() > 0) {
                                new Thread(() -> {
                                    try {
                                        for (int i = 0; i < jsonArrayOrders.length(); i++) {
                                            PurchaseData purchaseData = new PurchaseData(jsonArrayOrders.getJSONObject(i));
                                            arrayListPurchaseData.add(purchaseData);
                                        }
                                        new Handler(Looper.getMainLooper()).post(() -> updateUI());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }).start();
                            } else {
                                updateEmptyState(null);
                            }

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

    private void updateUI() {
        adapter.notifyDataSetChanged();
        loading = false;
        page++;
    }

    private void playMediaItems(PurchaseData purchaseData) {
        if (purchaseData != null) {
            activity = (HomeActivity) getActivity();
            String id = purchaseData.itemId;

            switch (purchaseData.postType) {
                case FeedContentData.POST_TYPE_ORIGINALS:
                    setSeriesData(purchaseData.itemId, "0", "0", "false");
                    break;
                case FeedContentData.POST_TYPE_SEASON:
                    setSeriesData(purchaseData.seriesId, purchaseData.seasonId, "0", "false");
                    break;
                case FeedContentData.POST_TYPE_PLAYLIST:
                    setPlaylistData(purchaseData.itemId, "false");
                    break;
                case FeedContentData.POST_TYPE_POST:
                    getSingleMediaItem(id);
                    break;
            }
        }
    }

    //***************************** AUDIO POST API ***********************************************//

    private void getSingleMediaItem(String id) {
        progressBar.setVisibility(View.VISIBLE);
        try {
            Map<String, String> params = new HashMap<>();
            APIRequest apiRequest = new APIRequest(Url.GET_SINGLE_MEDIA_ITEM + "&ID=" + id,
                    Request.Method.GET, params, null, getContext());
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, new APIResponse() {

                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    progressBar.setVisibility(View.GONE);
                    handleSingleMediaItem(response, error);

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setSeriesData(String seriesId, String seasonId, String episodeId, String iswatched) {
        activity.showSeriesExpandedView(seriesId, seasonId, episodeId, iswatched);
    }

    private void setPlaylistData(String playlistId, String iswatched) {
        activity.showPlaylistExpandedView(playlistId, iswatched);
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
        //activity.playItems(arrayListFeedContent, 0, id, contextName,"");
        activity.showAudioSongExpandedView(arrayListFeedContent, feedContentData.feedPosition, id, contextName, String.valueOf(feedContentData.iswatchlisted));
    }

    /**
     * Initialization of Empty State
     */
    void setUpEmptyState() {
        emptyState = EmptyStateManager.setUpInFragment(view, (AppCompatActivity) getActivity(), new EmptyStateManager.ActionBtnCallBack() {
            @Override
            public void onBtnClick(String action) {
                if (action.equals(EmptyStateManager.ACTION_RETRY)) {
                    getPurchaseHistory();
                }
            }
        });
        emptyState.hide();

    }

    /**
     * Update of Empty State
     */
    private void updateEmptyState(String error) {
        if (error == null) {
            if (arrayListPurchaseData.isEmpty()) {
                emptyState.setImgAndMsg(getString(R.string.no_purchases_found), null);
                srlRefresh.setVisibility(View.INVISIBLE);
                emptyState.setActionButton(EmptyStateManager.GO_TO_BUY);
                emptyState = EmptyStateManager.setUpInFragment(view, (AppCompatActivity) getActivity(), action -> {
                    goToBuySection();
                });
            } else {
                srlRefresh.setVisibility(View.VISIBLE);
                emptyState.hide();
            }
        } else {
            srlRefresh.setVisibility(View.INVISIBLE);
            if (Utility.isConnectingToInternet(getContext())) {
                emptyState.setMessage(APIManager.GENERIC_API_ERROR_MESSAGE);
            } else {
                emptyState.showNoConnectionState();
            }
        }
    }

    public void goToBuySection() {
        if (activity != null) {
            activity.showHome(Constant.TAB_BUY);
        }
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

    private void showExpiredDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(R.layout.dialog_custom);
        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();

        TextView tvTitle = alertDialog.findViewById(R.id.tvStoreName);
        TextView btNeutral = alertDialog.findViewById(R.id.btNeutral);
        tvTitle.setText(R.string.msg_item_expired);
        btNeutral.setText(R.string.ok);
        btNeutral.setVisibility(View.VISIBLE);

        btNeutral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    /************************ PAGINATION METHODS -- START **********************/

    public void initializePagination() {
        arrayListPurchaseData.clear();
        adapter.notifyDataSetChanged();
        purchaseUrl = Url.GET_PURCHASE_HISTORY;

        customLoadingListItem = false;

        if (paginate != null) {
            paginate.unbind();
        }
        loading = false;
        page = 0;
        nextPageNo = 1;
        handler = new Handler();
        handler.removeCallbacks(fakeCallback);
        paginate = Paginate.with(rvList, callbacks)
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
                purchaseUrl = purchaseUrl;

            }
            if (nextPageNo != 0) {
                getPurchaseHistory();
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
            vh.tvLoading.setText(String.format(getString(R.string.total_items_loaded), adapter.getItemCount()));

            // This is how you can make full span if you are using StaggeredGridLayoutManager
            if (rvList.getLayoutManager() instanceof StaggeredGridLayoutManager) {
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        APIManager.cancelAllRequests(APIManager.PAGE_NAVIGATION);
        if (paginate != null) {
            paginate.unbind();
        }
        if (handler != null) {
            handler.removeCallbacks(fakeCallback);
        }

        closePlayer();
    }

    @Override
    public void onResume() {
        super.onResume();
        /*if ((HomeActivity) getActivity() != null) {
            ((HomeActivity) getActivity()).showToolbarIcon();
            ((HomeActivity) getActivity()).showToolbarTitle(false);
        }*/
        // startSliding();
        // startSlidingRecyclerview();
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

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            /*if (getActivity() != null && getActivity() instanceof HomeActivity) {
                homeActivity = (HomeActivity) getActivity();
                if (homeActivity != null) {
                    homeActivity.showAd();
                }
            }*/
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
                mAdView.setVisibility(View.GONE);
                showCustomAd(id);
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
                                    setActionCustomAd(list.get(finalI).feedContentObjectData.adFieldsData, Constant.BANNER, id);
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
                                    setActionCustomAd(list.get(finalI).feedContentObjectData.adFieldsData, Constant.BANNER, id);
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

    private void setActionCustomAd(AdFieldsData adFieldsData, String location, String subLocation) {
        APIMethods.addEvent(getActivity(), Constant.CLICK, adFieldsData.postId, location, subLocation);
        switch (adFieldsData.adType) {
            case FeedContentData.AD_TYPE_IN_APP:
                if (adFieldsData.contentType.equalsIgnoreCase(Constant.PAGES)) {
                    HomeActivity.getInstance().redirectToPage(adFieldsData.pageType);
                } else {
                    switch (adFieldsData.contentType) {
                        case FeedContentData.CONTENT_TYPE_ESPORTS:
                            HomeActivity.getInstance().getUserRegisteredTournamentInfo(String.valueOf(adFieldsData.contentId));
                            break;
                        default:
                            HomeActivity.getInstance().getMediaData(String.valueOf(adFieldsData.contentId),adFieldsData.contentType);
                    }
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
                startActivity(new Intent(getActivity(), BrowserActivity.class).putExtra("title", "").putExtra("posturl", adFieldsData.externalUrl));
                break;
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
                    setActionCustomAd(adFieldsData, Constant.BANNER, id);
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
        flag = true;
        flagDecoration = false;
        adSliderObjectArrayList.clear();
        new Thread(() -> {
            for (AdFieldsData adFieldsData : adFieldsDataArrayList) {
                sliderId = String.valueOf(adFieldsData.postId);
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
}
