package com.app.itaptv.fragment;

import static com.app.itaptv.structure.FeedContentData.POST_TYPE_EPISODE;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.MenuItemCompat;
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
import com.app.itaptv.holder.FeedViewAllHolder;
import com.app.itaptv.interfaces.KeyEventListener;
import com.app.itaptv.structure.FeedContentData;
import com.app.itaptv.structure.SubscriptionDetails;
import com.app.itaptv.tvControllers.ViewAllFeedTvAdapter;
import com.app.itaptv.tv_fragment.ViewAllTvFragment;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SearchFragment extends Fragment {

    private final int GRID_SPAN = 1;
    private boolean loading = false;
    private int page = 0;
    private Handler handler;
    private Paginate paginate;
    protected long networkDelay = 2000;
    protected boolean customLoadingListItem = false;
    EmptyStateManager emptyState;
    HomeActivity activity;
    RelativeLayout llprogressbar;
    ProgressBar progressBar;

    RecyclerView rvSearch;
    KRecyclerViewAdapter adapter;
    ViewAllFeedTvAdapter adapterTv;
    RecyclerView.SmoothScroller smoothScroller = null;
    GridLayoutManager manager = null;
    String SearchUrl;
    int Next_Page_N0 = 1;
    String searchtext = "";
    SearchView searchView;
    View view;
    String TAG = String.valueOf(SearchFragment.class);
    public int lastSelectedItem = 0;
    public View lastViewSelected;

    ArrayList<FeedContentData> arrayListSearchContent = new ArrayList<>();
    ArrayList<FeedContentData> arrayListSearchPlayContent = new ArrayList<>();
    ArrayList<FeedContentData> arrayListChannelFeedContentData = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, container, false);
        setHasOptionsMenu(true);
        ((HomeActivity) getActivity()).setToolbar(true);
        ((HomeActivity) getActivity()).showToolbar(true);
        ((HomeActivity) Objects.requireNonNull(requireActivity())).showToolbarBackButton(R.drawable.white_arrow);
        ((HomeActivity) getActivity()).showToolbarTitle(false);
        ((HomeActivity) getActivity()).setToolbarTitle("");
        init();
        return view;
    }

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    private void init() {
        rvSearch = view.findViewById(R.id.rvsearch);
        llprogressbar = view.findViewById(R.id.llprogressbar);
        progressBar = view.findViewById(R.id.progressBar);

        setUpEmptyState();
        progressBar.setVisibility(View.GONE);
        rvSearch.setVisibility(View.INVISIBLE);
        lastSelectedItem = 0;
        if (Utility.isTelevision()) {
            setRecyclerViewTv();
        } else {
            setRecyclerView();
        }
    }

    private void setRecyclerView() {
        adapter = new KRecyclerViewAdapter(getActivity(), arrayListSearchContent, (viewGroup, i) -> {
            View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_feed_view_all, viewGroup, false);
            return new FeedViewAllHolder(layoutView, null, null, Constant.screen_type_search);
        }, (kRecyclerViewHolder, o, i) -> {
            if (o instanceof FeedContentData) {
                FeedContentData feedContentData = (FeedContentData) o;
                /*if (feedContentData.originalExcerpt.equalsIgnoreCase(Constant.CONTENT_PAID)) {
                    Intent intent = new Intent(getActivity(), BuyDetailActivity.class);
                    intent.putExtra(BuyDetailActivity.CONTENT_DATA, feedContentData);
                    getActivity().startActivityForResult(intent, BuyDetailActivity.REQUEST_CODE);
                } else {*/
                setPlaybackData(feedContentData, i, Integer.parseInt(feedContentData.postId), Analyticals.CONTEXT_FEED);
                //}
            }

        });
        ((SimpleItemAnimator) Objects.requireNonNull(rvSearch.getItemAnimator())).setSupportsChangeAnimations(false);
        rvSearch.setAdapter(adapter);
        int spanCount = 3;
        if (Utility.isTelevision()) {
            spanCount = 5;
        }
        GridLayoutManager manager = new GridLayoutManager(getActivity(), spanCount, GridLayoutManager.VERTICAL, false);
        rvSearch.setLayoutManager(manager);

    }

    private void setRecyclerViewTv() {
        int spanCount = 6;
        manager = new GridLayoutManager(getActivity(), spanCount, GridLayoutManager.VERTICAL, false);
        rvSearch.setLayoutManager(manager);
        smoothScroller =
                new ViewAllTvFragment.CenterSmoothScroller(rvSearch.getContext());
        adapterTv = new ViewAllFeedTvAdapter(getContext(), arrayListSearchContent,
                "",
                "",
                spanCount, new KeyEventListener() {
            @Override
            public void onKeyEvent(int position, Object item, View view, boolean isFirstRow) {
                if (isFirstRow) {
                    searchView.setFocusable(true);
                    searchView.requestFocus();
                } else {
                    smoothScroller.setTargetPosition(position);
                    manager.startSmoothScroll(smoothScroller);
                    lastSelectedItem = position;
                    lastViewSelected = view;
                }
                if (item instanceof FeedContentData) {
                    FeedContentData feedContentData = (FeedContentData) item;
                    if (feedContentData != null) {
                        setPlaybackData(feedContentData, position, Integer.parseInt(feedContentData.postId), Analyticals.CONTEXT_FEED);
                    }
                }
            }
        });
        rvSearch.setAdapter(adapterTv);
    }

    private void setPlaybackData(FeedContentData feedContentData, int position, int feed_ID, String feedPage) {

        if (getActivity() != null && getActivity() instanceof HomeActivity) {
            activity = (HomeActivity) getActivity();

            SubscriptionDetails sd = LocalStorage.getUserSubscriptionDetails();
            if (!feedContentData.postType.equals("")) {
                switch (feedContentData.postType) {
                    case FeedContentData.POST_TYPE_POST:
                        if (arrayListSearchContent.size() > 0) {
                            if (arrayListSearchContent.get(feedContentData.feedPosition).feedContentType.equals(FeedContentData.CONTENT_TYPE_ALL)) {
                                ArrayList<FeedContentData> arrayListAllTypeFeedContentData = new ArrayList<>();
                                arrayListAllTypeFeedContentData.add(feedContentData);
                                //activity.playItems(arrayListAllTypeFeedContentData, 0, String.valueOf(feed_ID), feedPage, String.valueOf(feedContentData.iswatchlisted));
                                if (feedContentData.postExcerpt.equalsIgnoreCase("free")) {
                                    activity.showAudioSongExpandedView(arrayListAllTypeFeedContentData, 0, String.valueOf(feed_ID), feedPage, String.valueOf(feedContentData.iswatchlisted));
                                } else if (feedContentData.postExcerpt.equalsIgnoreCase("paid")) {
                                    if (sd != null && sd.allow_rental != null) {
                                        if (sd.allow_rental.equalsIgnoreCase(Constant.YES)) {
                                            playMediaItems(feedContentData);
                                        } else {
                                            Intent intent1 = new Intent(getActivity(), BuyDetailActivity.class);
                                            intent1.putExtra(BuyDetailActivity.CONTENT_DATA, feedContentData);
                                            getActivity().startActivityForResult(intent1, BuyDetailActivity.REQUEST_CODE);
                                        }
                                    }
                                }
                                return;
                            }
                            ArrayList<FeedContentData> arrayList = new ArrayList<>();
                            arrayList.add(feedContentData);
                            if (feedContentData.postExcerpt.equalsIgnoreCase("free")) {
                                activity.showAudioSongExpandedView(arrayList, 0, "", feedPage, String.valueOf(arrayList.get(0).iswatchlisted));
                            } else if (feedContentData.postExcerpt.equalsIgnoreCase("paid")) {
                                if (sd != null && sd.allow_rental != null) {
                                    if (sd.allow_rental.equalsIgnoreCase(Constant.YES)) {
                                        playMediaItems(feedContentData);
                                    } else {
                                        Intent intent1 = new Intent(getActivity(), BuyDetailActivity.class);
                                        intent1.putExtra(BuyDetailActivity.CONTENT_DATA, feedContentData);
                                        getActivity().startActivityForResult(intent1, BuyDetailActivity.REQUEST_CODE);
                                    }
                                }
                            }
                        }
                        break;

                    case FeedContentData.POST_TYPE_ORIGINALS:
                        if (feedContentData.postExcerpt.equalsIgnoreCase("free")) {
                            activity.showSeriesExpandedView(feedContentData.postId, "0", "0", String.valueOf(feedContentData.iswatchlisted));
                        } else if (feedContentData.postExcerpt.equalsIgnoreCase("paid")) {
                            if (sd != null && sd.allow_rental != null) {
                                if (sd.allow_rental.equalsIgnoreCase(Constant.YES)) {
                                    playMediaItems(feedContentData);
                                } else {
                                    Intent intent1 = new Intent(getActivity(), BuyDetailActivity.class);
                                    intent1.putExtra(BuyDetailActivity.CONTENT_DATA, feedContentData);
                                    getActivity().startActivityForResult(intent1, BuyDetailActivity.REQUEST_CODE);
                                }
                            }
                        }
                        break;

                    case FeedContentData.POST_TYPE_EPISODE:
                        if (feedContentData.postExcerpt.equalsIgnoreCase("free")) {
                            activity.showSeriesExpandedView(feedContentData.seriesId, feedContentData.seasonId, feedContentData.episodeId, "");
                        } else if (feedContentData.postExcerpt.equalsIgnoreCase("paid")) {
                            if (sd != null && sd.allow_rental != null) {
                                if (sd.allow_rental.equalsIgnoreCase(Constant.YES)) {
                                    playMediaItems(feedContentData);
                                } else {
                                    Intent intent1 = new Intent(getActivity(), BuyDetailActivity.class);
                                    intent1.putExtra(BuyDetailActivity.CONTENT_DATA, feedContentData);
                                    getActivity().startActivityForResult(intent1, BuyDetailActivity.REQUEST_CODE);
                                }
                            }
                        }
                        break;

                    case FeedContentData.POST_TYPE_PLAYLIST:
                        if (feedContentData.postExcerpt.equalsIgnoreCase("free")) {
                            activity.showPlaylistExpandedView(feedContentData.postId, String.valueOf(feedContentData.iswatchlisted));
                        } else if (feedContentData.postExcerpt.equalsIgnoreCase("paid")) {
                            if (sd != null && sd.allow_rental != null) {
                                if (sd.allow_rental.equalsIgnoreCase(Constant.YES)) {
                                    playMediaItems(feedContentData);
                                } else {
                                    Intent intent1 = new Intent(getActivity(), BuyDetailActivity.class);
                                    intent1.putExtra(BuyDetailActivity.CONTENT_DATA, feedContentData);
                                    getActivity().startActivityForResult(intent1, BuyDetailActivity.REQUEST_CODE);
                                }
                            }
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


    public void initializePagination(String newText) {
        Log.e("test", "initializePagination");
        progressBar.setVisibility(View.VISIBLE);
        arrayListSearchContent.clear();
        if (Utility.isTelevision()) {
            adapterTv.notifyDataSetChanged();
        } else {
            adapter.notifyDataSetChanged();
        }
        SearchUrl = Url.GET_SEARCH + newText + "&page_no=";
        customLoadingListItem = false;

        if (paginate != null) {
            paginate.unbind();
        }
        loading = false;
        searchtext = newText;
        page = 0;
        Next_Page_N0 = 1;
        handler = new Handler();
        handler.removeCallbacks(fakeCallback);
        paginate = Paginate.with(rvSearch, callbacks)
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
                SearchUrl = SearchUrl;

            }
            Log.e("test", "fakeCallback");
            if (Next_Page_N0 != 0) {

                getSearchData(Next_Page_N0);

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
            if (rvSearch.getLayoutManager() instanceof StaggeredGridLayoutManager) {
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

    private void getSearchData(int next_page_n0) {
        try {
            Map<String, String> params = new HashMap<>();
            APIRequest apiRequest = new APIRequest(SearchUrl + next_page_n0, Request.Method.GET, params, null, getActivity());
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    progressBar.setVisibility(View.GONE);
                    rvSearch.setVisibility(View.VISIBLE);
                    handleSearchResponse(response, error);
                }
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
                        Next_Page_N0 = jsonArrayMsg.has("next_page") ? jsonArrayMsg.getInt("next_page") : 0;
                        for (int i = 0; i < jsonArrayContents.length(); i++) {
                            FeedContentData feedContentData = new FeedContentData(jsonArrayContents.getJSONObject(i), i);
                            arrayListSearchContent.add(feedContentData);
                        }
                        if (Utility.isTelevision()) {
                            adapterTv.notifyDataSetChanged();
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                        if (!searchView.hasFocus()) {
                            restorePosition();
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
    void setUpEmptyState() {
        emptyState = EmptyStateManager.setUpInFragment(view, (AppCompatActivity) getActivity(), new EmptyStateManager.ActionBtnCallBack() {
            @Override
            public void onBtnClick(String action) {
                if (action.equals(EmptyStateManager.ACTION_RETRY)) {
                    getSearchData(0);
                }
            }
        });
    }

    /**
     * Update of Empty State
     */
    private void updateEmptyState(String error) {
        if (error == null) {
            if (arrayListSearchContent.isEmpty()) {
                emptyState.setImgAndMsg(getString(R.string.no_item_found), null);
                rvSearch.setVisibility(View.INVISIBLE);
            } else {
                rvSearch.setVisibility(View.VISIBLE);
                emptyState.hide();
            }
        } else {
            rvSearch.setVisibility(View.INVISIBLE);
            if (Utility.isConnectingToInternet(getContext())) {
                emptyState.setMessage(APIManager.GENERIC_API_ERROR_MESSAGE);
            } else {
                emptyState.showNoConnectionState();
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchItem.expandActionView();
        SearchView searchViewAction = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchViewAction.onActionViewExpanded();
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchableInfo searchableInfo = searchManager.getSearchableInfo(getActivity().getComponentName());

        searchViewAction.setSearchableInfo(searchableInfo);


        EditText searchEditText = (EditText) searchViewAction.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setHint(R.string.search_hint);
        searchEditText.setTextSize(17);
        searchEditText.setTypeface(ResourcesCompat.getFont(view.getContext(), R.font.rubik_regular));
        searchEditText.setTextColor(getResources().getColor(R.color.white));
        searchEditText.setHintTextColor(getResources().getColor(R.color.white));

        searchEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_DPAD_CENTER) {
                    Utility.showKeyboard(getActivity());
                }
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    if (i == KeyEvent.KEYCODE_DPAD_DOWN) {
                        if (adapterTv != null) {
                            restorePosition();
                            searchView.setFocusable(false);
                        }
                    }
                }
                return false;
            }
        });

        ImageView searchCloseIcon = (ImageView) searchViewAction.findViewById(androidx.appcompat.R.id.search_close_btn);
        searchCloseIcon.setImageResource(R.drawable.ic_close);

        searchCloseIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchEditText.setText("");
                rvSearch.setVisibility(View.INVISIBLE);
                lastSelectedItem = 0;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (paginate != null) {
                    paginate.unbind();
                }
                if (handler != null) {
                    handler.removeCallbacks(fakeCallback);
                }
                initializePagination(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                emptyState.hide();
                progressBar.setVisibility(View.GONE);
                if (paginate != null) {
                    paginate.unbind();
                }
                if (handler != null) {
                    handler.removeCallbacks(fakeCallback);
                }
                rvSearch.setVisibility(View.INVISIBLE);
                // in searchViewFunction(newText); i am doing my things
                if (newText.length() > 2) {
                    initializePagination(newText);
                }
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
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
                case POST_TYPE_EPISODE:
                    activity.showSeriesExpandedView(feedContentData.seriesId, feedContentData.seasonId, feedContentData.episodeId, "");
                    break;
                case FeedContentData.POST_TYPE_PLAYLIST:
                    setPlaylistData(id);
                    break;
                case FeedContentData.POST_TYPE_POST:
                    //getSingleMediaItem(id);
                    arrayListSearchPlayContent.clear();
                    arrayListSearchPlayContent.add(feedContentData);
                    activity.showAudioSongExpandedView(arrayListSearchPlayContent, feedContentData.feedPosition, feedContentData.postId, Analyticals.CONTEXT_FEED, String.valueOf(feedContentData.iswatchlisted));
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
        arrayListSearchContent.clear();
        arrayListSearchContent.add(feedContentData);
        String id = feedContentData.postId;
        activity.showAudioSongExpandedView(arrayListSearchContent, feedContentData.feedPosition, id, contextName, String.valueOf(feedContentData.iswatchlisted));
    }

    public void restorePosition() {
        try {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (adapterTv != null) {
                        smoothScroller.setTargetPosition(lastSelectedItem);
                        manager.startSmoothScroll(smoothScroller);
                     //   Objects.requireNonNull(arrayListSearchContent.get(lastSelectedItem).view).requestFocus();
                    }
                }
            }, 10);
        }catch (Exception e){

        }
    }
}
