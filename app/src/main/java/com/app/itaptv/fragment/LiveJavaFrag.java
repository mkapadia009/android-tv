package com.app.itaptv.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.android.volley.Request;
import com.app.itaptv.API.APIManager;
import com.app.itaptv.API.APIRequest;
import com.app.itaptv.API.Url;
import com.app.itaptv.R;
import com.app.itaptv.activity.HomeActivity;
import com.app.itaptv.holder.FeedNewLiveHolder;
import com.app.itaptv.interfaces.KeyEventListener;
import com.app.itaptv.structure.FeedContentData;
import com.app.itaptv.tvControllers.ViewAllFeedTvAdapter;
import com.app.itaptv.tv_fragment.BuyDetailFragment;
import com.app.itaptv.tv_fragment.ViewAllTvFragment;
import com.app.itaptv.utils.AlertUtils;
import com.app.itaptv.utils.Analyticals;
import com.app.itaptv.utils.Constant;
import com.app.itaptv.utils.EmptyStateManager;
import com.app.itaptv.utils.Log;
import com.app.itaptv.utils.Utility;
import com.kalpesh.krecycleradapter.KRecyclerViewAdapter;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemCreator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LiveJavaFrag extends Fragment {

    private static final int GRID_SPAN = 1;
    private boolean loading = false;
    private int page = 0;
    private Handler handler;
    private Paginate paginate;
    protected long networkDelay = 2000;
    protected boolean customLoadingListItem = false;
    private EmptyStateManager emptyState;
    private HomeActivity activity;
    private RelativeLayout llprogressbar;
    private ProgressBar progressBar;

    private RecyclerView rvLiveRecycler;
    private KRecyclerViewAdapter adapter;
    private ViewAllFeedTvAdapter adapterTv;
    RecyclerView.SmoothScroller smoothScroller = null;
    GridLayoutManager manager = null;
    public int lastSelectedItem = 0;
    public View lastViewSelected;
    private String liveUrl;
    private int Next_Page_N0 = 1;
    private View view;
    String TAG = String.valueOf(SearchFragment.class);

    private ArrayList<FeedContentData> arrayListLiveContent = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_live, container, false);
        init();
        return view;
    }

    public static LiveJavaFrag newInstance() {
        return new LiveJavaFrag();
    }

    private void init() {
        rvLiveRecycler = view.findViewById(R.id.liveRecyclerView);
        llprogressbar = view.findViewById(R.id.llprogressbar);
        progressBar = view.findViewById(R.id.progressBar);

        ((HomeActivity) getActivity()).setToolbar(true);
        ((HomeActivity) getActivity()).showToolbarBackButton(R.drawable.white_arrow);
        ((HomeActivity) getActivity()).showToolbarTitle(true);
        ((HomeActivity) getActivity()).setToolbarTitle(getString(R.string.live));
        ((HomeActivity) getActivity()).setCustomizedTitle(0);

        setUpEmptyState();
        progressBar.setVisibility(View.GONE);
        rvLiveRecycler.setVisibility(View.INVISIBLE);

        if (Utility.isTelevision()) {
            setRecyclerViewTv();
        } else {
            setRecyclerView();
        }
    }

    private void setRecyclerView() {
        adapter = new KRecyclerViewAdapter(getActivity(), arrayListLiveContent, (viewGroup, i) -> {
            View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_new_live_feed, viewGroup, false);
            return new FeedNewLiveHolder(layoutView);
        }, (kRecyclerViewHolder, o, i) -> {
            if (o instanceof FeedContentData) {
                FeedContentData feedContentData = (FeedContentData) o;
                Utility.customEventsTracking(Constant.LiveEventHits, feedContentData.postTitle);
                /*if (feedContentData.originalExcerpt.equalsIgnoreCase(Constant.CONTENT_PAID)) {
                    Intent intent = new Intent(getActivity(), BuyDetailActivity.class);
                    intent.putExtra(BuyDetailActivity.CONTENT_DATA, feedContentData);
                    getActivity().startActivityForResult(intent, BuyDetailActivity.REQUEST_CODE);
                } else {*/
                if (!feedContentData.url.isEmpty()) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(feedContentData.url));
                    startActivity(intent);
                } else
                    setPlaybackData(feedContentData, i, Integer.parseInt(feedContentData.postId), Analyticals.CONTEXT_FEED);
                //}
            }
        });
        rvLiveRecycler.setAdapter(adapter);
        int spanCount = 1;
        if (Utility.isTelevision()) {
            spanCount = 3;
        }
        GridLayoutManager manager = new GridLayoutManager(getActivity(), spanCount, GridLayoutManager.VERTICAL, false);
        rvLiveRecycler.setLayoutManager(manager);
        initializePagination();
    }

    private void setRecyclerViewTv() {
        int spanCount = 6;
        manager = new GridLayoutManager(getActivity(), spanCount, GridLayoutManager.VERTICAL, false);
        rvLiveRecycler.setLayoutManager(manager);
        smoothScroller =
                new ViewAllTvFragment.CenterSmoothScroller(rvLiveRecycler.getContext());
        adapterTv = new ViewAllFeedTvAdapter(getContext(), arrayListLiveContent,
                "",
                "",
                spanCount, new KeyEventListener() {
            @Override
            public void onKeyEvent(int position, Object item, View view, boolean isFirstRow) {
                if (isFirstRow) {

                } else {
                    smoothScroller.setTargetPosition(position);
                    manager.startSmoothScroll(smoothScroller);
                    lastSelectedItem = position;
                    lastViewSelected = view;
                }
                if (item instanceof FeedContentData) {
                    FeedContentData feedContentData = (FeedContentData) item;
                    Utility.customEventsTracking(Constant.LiveEventHits, feedContentData.postTitle);
                    if (!feedContentData.url.isEmpty()) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(feedContentData.url));
                        startActivity(intent);
                    } else
                        setPlaybackData(feedContentData, position, Integer.parseInt(feedContentData.postId), Analyticals.CONTEXT_FEED);
                }
            }
        });
        rvLiveRecycler.setAdapter(adapterTv);
        initializePagination();
    }


    private void setPlaybackData(FeedContentData feedContentData, int position, int feed_ID, String feedPage) {

        if (getActivity() != null && getActivity() instanceof HomeActivity) {
            activity = (HomeActivity) getActivity();

            if (!feedContentData.postType.equals("")) {
                switch (feedContentData.postType) {
                    case FeedContentData.POST_TYPE_POST:
                        if (arrayListLiveContent.size() > 0) {
                            if (arrayListLiveContent.get(feedContentData.feedPosition).feedContentType.equals(FeedContentData.CONTENT_TYPE_ALL)) {
                                ArrayList<FeedContentData> arrayListAllTypeFeedContentData = new ArrayList<>();
                                arrayListAllTypeFeedContentData.add(feedContentData);
                                //activity.playItems(arrayListAllTypeFeedContentData, 0, String.valueOf(feed_ID), feedPage, String.valueOf(feedContentData.iswatchlisted));
                                if (feedContentData.postExcerpt.equalsIgnoreCase("free")) {
                                    activity.showAudioSongExpandedView(arrayListAllTypeFeedContentData, 0, String.valueOf(feed_ID), feedPage, String.valueOf(feedContentData.iswatchlisted));
                                } else if (feedContentData.postExcerpt.equalsIgnoreCase("paid")) {
                                  /*  Intent intent1 = new Intent(getActivity(), BuyDetailActivity.class);
                                    intent1.putExtra(BuyDetailActivity.CONTENT_DATA, feedContentData);
                                    getActivity().startActivityForResult(intent1, BuyDetailActivity.REQUEST_CODE);*/
                                    activity.openFragment(BuyDetailFragment.getInstance(feedContentData,""));
                                }
                                return;
                            }
                            ArrayList<FeedContentData> arrayList = new ArrayList<>();
                            arrayList.add(feedContentData);
                            if (feedContentData.postExcerpt.equalsIgnoreCase("free")) {
                                activity.showAudioSongExpandedView(arrayList, 0, "", feedPage, String.valueOf(arrayList.get(0).iswatchlisted));
                            } else if (feedContentData.postExcerpt.equalsIgnoreCase("paid")) {
                              /*  Intent intent1 = new Intent(getActivity(), BuyDetailActivity.class);
                                intent1.putExtra(BuyDetailActivity.CONTENT_DATA, feedContentData);
                                getActivity().startActivityForResult(intent1, BuyDetailActivity.REQUEST_CODE);*/
                                activity.openFragment(BuyDetailFragment.getInstance(feedContentData,""));
                            }
                        }
                        break;

                    case FeedContentData.POST_TYPE_ORIGINALS:
                        if (feedContentData.postExcerpt.equalsIgnoreCase("free")) {
                            activity.showSeriesExpandedView(feedContentData.postId, "0", "0", String.valueOf(feedContentData.iswatchlisted));
                        } else if (feedContentData.postExcerpt.equalsIgnoreCase("paid")) {
                           /* Intent intent1 = new Intent(getActivity(), BuyDetailActivity.class);
                            intent1.putExtra(BuyDetailActivity.CONTENT_DATA, feedContentData);
                            getActivity().startActivityForResult(intent1, BuyDetailActivity.REQUEST_CODE);*/
                            activity.openFragment(BuyDetailFragment.getInstance(feedContentData,""));
                        }
                        break;

                    case FeedContentData.POST_TYPE_EPISODE:
                        if (feedContentData.postExcerpt.equalsIgnoreCase("free")) {
                            activity.showSeriesExpandedView(feedContentData.seriesId, feedContentData.seasonId, feedContentData.episodeId, "");
                        } else if (feedContentData.postExcerpt.equalsIgnoreCase("paid")) {
                           /* Intent intent1 = new Intent(getActivity(), BuyDetailActivity.class);
                            intent1.putExtra(BuyDetailActivity.CONTENT_DATA, feedContentData);
                            getActivity().startActivityForResult(intent1, BuyDetailActivity.REQUEST_CODE);*/
                            activity.openFragment(BuyDetailFragment.getInstance(feedContentData,""));
                        }
                        break;

                    case FeedContentData.POST_TYPE_PLAYLIST:
                        if (feedContentData.postExcerpt.equalsIgnoreCase("free")) {
                            activity.showPlaylistExpandedView(feedContentData.postId, String.valueOf(feedContentData.iswatchlisted));
                        } else if (feedContentData.postExcerpt.equalsIgnoreCase("paid")) {
                           /* Intent intent1 = new Intent(getActivity(), BuyDetailActivity.class);
                            intent1.putExtra(BuyDetailActivity.CONTENT_DATA, feedContentData);
                            getActivity().startActivityForResult(intent1, BuyDetailActivity.REQUEST_CODE);*/
                            activity.openFragment(BuyDetailFragment.getInstance(feedContentData,""));
                        }
                        break;

                    case FeedContentData.POST_TYPE_STREAM:
                        //playChannelAPI(feedContentData, 1);
                        activity.showChannelExpandedView(feedContentData, 1, String.valueOf(feed_ID), Analyticals.CONTEXT_CHANNEL);
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

    /**
     * Open specific fragment on BottomNavigationView item selected
     *
     * @param fragment Fragment object to display the Fragment
     */
    private void openFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = Objects.requireNonNull(requireActivity()).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void initializePagination() {
        Log.e("test", "initializePagination");
        progressBar.setVisibility(View.VISIBLE);
        arrayListLiveContent.clear();
        if (Utility.isTelevision()) {
            adapterTv.notifyDataSetChanged();
        } else {
            adapter.notifyDataSetChanged();
        }
        liveUrl = Url.GET_LIVE_FEEDS + "&page_no=";
        customLoadingListItem = false;

        if (paginate != null) {
            paginate.unbind();
        }
        loading = false;
        page = 0;
        Next_Page_N0 = 1;
        handler = new Handler();
        handler.removeCallbacks(fakeCallback);
        paginate = Paginate.with(rvLiveRecycler, callbacks)
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
                liveUrl = liveUrl;
            }
            Log.e("test", "fakeCallback");
            if (Next_Page_N0 != 0) {
                getLiveListingData(Next_Page_N0);
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
            if (Utility.isTelevision()) {
                vh.tvLoading.setText(String.format(getString(R.string.total_items_loaded), adapterTv.getItemCount()));
            } else {
                vh.tvLoading.setText(String.format(getString(R.string.total_items_loaded), adapter.getItemCount()));
            }

            // This is how you can make full span if you are using StaggeredGridLayoutManager
            if (rvLiveRecycler.getLayoutManager() instanceof StaggeredGridLayoutManager) {
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

    private void getLiveListingData(int next_page_n0) {
        try {
            Map<String, String> params = new HashMap<>();
            APIRequest apiRequest = new APIRequest(liveUrl + next_page_n0, Request.Method.GET, params, null, getActivity());
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, (response, error, headers, statusCode) -> {
                progressBar.setVisibility(View.GONE);
                rvLiveRecycler.setVisibility(View.VISIBLE);
                handleSearchResponse(response, error);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleSearchResponse(String response, Exception error) {
        //Log.d(TAG, response);
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
                        JSONObject theObject = jsonArrayContents.getJSONObject(0);
                        JSONArray theObjContents = theObject.getJSONArray("contents");
                        Next_Page_N0 = theObject.has("next_page") ? theObject.getInt("next_page") : 0;
                        for (int i = 0; i < theObjContents.length(); i++) {
                            FeedContentData feedContentData = new FeedContentData(theObjContents.getJSONObject(i), i);
                            arrayListLiveContent.add(feedContentData);
                        }
                        if (Utility.isTelevision()) {
                            adapterTv.notifyDataSetChanged();
                            restorePosition();
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                        loading = false;
                        page++;
                        updateEmptyState(null);
                    }
                }
            }
        } catch (Exception e) {
            if (paginate != null) {
                paginate.unbind();
            }
            if (handler != null) {
                handler.removeCallbacks(fakeCallback);
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
        AlertUtils.showAlert(getString(R.string.label_error), errorMessage, null, getContext(), null);
    }

    /**
     * Initialization of Empty State
     */
    private void setUpEmptyState() {
        emptyState = EmptyStateManager.setUpInFragment(view, activity, new EmptyStateManager.ActionBtnCallBack() {
            @Override
            public void onBtnClick(String action) {
                if (action.equals(EmptyStateManager.ACTION_RETRY)) {
                    getLiveListingData(0);
                }
            }
        });
    }

    /**
     * Update of Empty State
     */
    private void updateEmptyState(String error) {
        if (error == null) {
            if (arrayListLiveContent.isEmpty()) {
                emptyState.setImgAndMsg(getString(R.string.no_content_available), null);
                rvLiveRecycler.setVisibility(View.INVISIBLE);
            } else {
                rvLiveRecycler.setVisibility(View.VISIBLE);
                emptyState.hide();
            }
        } else {
            rvLiveRecycler.setVisibility(View.INVISIBLE);
            if (Utility.isConnectingToInternet(getContext())) {
                emptyState.setMessage(APIManager.GENERIC_API_ERROR_MESSAGE);
            } else {
                emptyState.showNoConnectionState();
            }
        }
    }

    public void restorePosition() {
        try {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (adapterTv != null) {
                        smoothScroller.setTargetPosition(lastSelectedItem);
                        manager.startSmoothScroll(smoothScroller);
                        // Objects.requireNonNull(arrayListLiveContent.get(lastSelectedItem).view).requestFocus();
                    }
                }
            }, 10);
        } catch (Exception e) {

        }
    }

}
