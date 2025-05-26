package com.app.itaptv.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.app.itaptv.API.APIManager;
import com.app.itaptv.API.APIRequest;
import com.app.itaptv.API.APIResponse;
import com.app.itaptv.API.Url;
import com.app.itaptv.R;
import com.app.itaptv.activity.BrowserActivity;
import com.app.itaptv.activity.BuyDetailActivity;
import com.app.itaptv.activity.CouponDetailsActivity;
import com.app.itaptv.activity.DownloadActivity;
import com.app.itaptv.activity.GameStartActivity;
import com.app.itaptv.activity.HomeActivity;
import com.app.itaptv.activity.LeaderBoardActivity;
import com.app.itaptv.activity.LuckyWinnerActivity;
import com.app.itaptv.activity.RedeemCoinsActivity;
import com.app.itaptv.activity.TournamentRegistrationFormActivity;
import com.app.itaptv.activity.TournamentSummaryActivity;
import com.app.itaptv.activity.WebViewActivity;
import com.app.itaptv.adapter.ViewPagerAdapter;
import com.app.itaptv.structure.CouponData;
import com.app.itaptv.structure.FeedContentData;
import com.app.itaptv.structure.FeedData;
import com.app.itaptv.structure.GameData;
import com.app.itaptv.structure.HomeTabsList;
import com.app.itaptv.utils.AlertUtils;
import com.app.itaptv.utils.Analyticals;
import com.app.itaptv.utils.Constant;
import com.app.itaptv.utils.CustomLinearLayoutManager;
import com.app.itaptv.utils.GameDateValidation;
import com.app.itaptv.utils.LocalStorage;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.app.itaptv.activity.BaseActivity.isConnectionRestored;
import static com.app.itaptv.activity.WalletActivity.KEY_WALLET_BALANCE;
import static com.app.itaptv.activity.WalletActivity.REQUEST_CODE;
import static com.app.itaptv.structure.FeedContentData.POST_TYPE_EPISODE;
import static com.app.itaptv.structure.FeedContentData.POST_TYPE_ORIGINALS;
import static com.app.itaptv.structure.FeedContentData.POST_TYPE_PLAYLIST;
import static com.app.itaptv.structure.FeedContentData.POST_TYPE_POST;
import static com.app.itaptv.structure.FeedContentData.POST_TYPE_STREAM;

public class HomeFragment extends Fragment implements TabLayout.OnTabSelectedListener {

    private View view;
    public static TabLayout tlHomeTabs;
    ViewPager vpHome;
    AppBarLayout appBarLayout;
    SwipeRefreshLayout swipeRefreshLayout;
    //RecyclerView rvSlider;
    CustomLinearLayoutManager layoutManager;

    // KRecyclerViewAdapter adapterSlider;
    ViewPagerAdapter viewPagerAdapter;

    //String[] arrTabLabels;
    ArrayList<FeedData> arrayListFeedData = new ArrayList<>();
    ArrayList<FeedContentData> arrayListFeedContentData = new ArrayList<>();
    //ArrayList<HomeSliderObject> homeSliderObjectArrayList = new ArrayList<>();
    ArrayList<GameData> arrayListGameData = new ArrayList<>();
    ArrayList<FeedContentData> arrayListChannelFeedContentData = new ArrayList<>();

    ArrayList<HomeTabsList> arrayListTabData = new ArrayList<>();
    ArrayList<HomeSliderTabFragment> ftList = new ArrayList<>();

    // Tab Fragments
    //HomeListenTabFragment homeListenTabFragment = new HomeListenTabFragment();
    //HomeCombinedTabFragment homeCombinedTabFragment = new HomeCombinedTabFragment();
    //MainHomeFragment mainHomeFragment=new MainHomeFragment();
    //HomeWatchPlayCustomTabFragment homeWatchPlayCustomTabFragment = new HomeWatchPlayCustomTabFragment();
    /*HomeBuyTabFragment homeBuyTabFragment = new HomeBuyTabFragment();
    HomePlayTabFragment homePlayTabFragment = new HomePlayTabFragment();*/
    //Fragment[] arrTabFragments = {homeListenTabFragment, homePlayTabFragment, homeBuyTabFragment};
    // Fragment[] arrTabFragments = {mainHomeFragment, homePlayTabFragment,homeBuyTabFragment};
    int[] arrTabIcons = {R.drawable.ic_listen, R.drawable.ic_play, R.drawable.ic_buy};
    //String sliderId;
    HomeActivity activity;
    boolean flag = false;
    boolean shouldSlide = true;
    // for slider
    final Handler handler = new Handler();
    Runnable runnable = null;
    int count = 0;
    int visibleItemPosition;
    private boolean flagDecoration = false;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getActivity() != null && getActivity().getActionBar() != null)
            getActivity().getActionBar().show();
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_home, container, false);
            init();
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //startSlidingRecyclerview();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            ((HomeActivity) getActivity()).setToolbar(true);
            ((HomeActivity) getActivity()).showToolbarIcon();
            ((HomeActivity) getActivity()).showToolbarTitle(false);
        }
        /*if (LocalStorage.getLiveModuleStatus()) {
            HomeActivity.toolbarlive_button.setVisibility(View.VISIBLE);
        } else {
            HomeActivity.toolbarlive_button.setVisibility(View.INVISIBLE);
        }*/
        /*startSliding();*/
        // startSlidingRecyclerview();
    }

    @Override
    public void onPause() {
        super.onPause();
        //stopSliding();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //stopSliding();
    }

    /**
     * Initialize data
     */
    private void init() {
        activity = (HomeActivity) getActivity();
        // arrTabLabels = getResources().getStringArray(R.array.arra_home_tabs);
        tlHomeTabs = view.findViewById(R.id.tlHomeTabs);
        vpHome = view.findViewById(R.id.vpHome);
        appBarLayout = view.findViewById(R.id.appBar);
        swipeRefreshLayout = view.findViewById(R.id.swipeToRefresh);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
               //activity.reloadFragment(HomeFragment.newInstance());
                swipeRefreshLayout.setRefreshing(false);
                view=null;
                getActivity().getSupportFragmentManager().beginTransaction().detach(HomeFragment.this).commit();
                getActivity().getSupportFragmentManager().beginTransaction().attach(HomeFragment.this).commit();
            }
        });

        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        FragmentManager childFragment=getChildFragmentManager();
        FragmentTransaction childFragmentTransaction=childFragment.beginTransaction();
        childFragmentTransaction.add(R.id.fragment_container,MainHomeFragment.newInstance());
        childFragmentTransaction.addToBackStack(null);
        childFragmentTransaction.commit();

        //rvSlider = view.findViewById(R.id.rvSlider);

        //setTabsWithViewPager();
        //setOtherSliderRecyclerView();
        redirectToPage();
        getActiveTabs();
    }

    //int sliderPosition = -1;

    /**
     * Initializes slider recycler view
     * Set the slider recyclerview of Watch and Buy Tab
     */
    /*private void setOtherSliderRecyclerView() {
        adapterSlider = new KRecyclerViewAdapter(getContext(), homeSliderObjectArrayList, new KRecyclerViewHolderCallBack() {
            @Override
            public KRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_slider, viewGroup, false);

                adapterSlider.getItemCount();
                adapterSlider.getSelectedIndexes();
                return new SliderHolder(layoutView, homeSliderObjectArrayList.size());
            }
        }, new KRecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClicked(KRecyclerViewHolder kRecyclerViewHolder, Object o, int i) {
                if (o instanceof HomeSliderObject) {
                    HomeSliderObject homeSliderObject = (HomeSliderObject) o;
                    if (homeSliderObject.feedContentData != null) {
                        if (homeSliderObject.feedContentData.contentType.equalsIgnoreCase(Constant.AD)) {
                            setActionOnSliderAd(homeSliderObject.feedContentData);
                        } else {
                            setActionOnSlider(homeSliderObject.feedContentData);
                        }
                    } else {
                        setActionOnPlaySlider(homeSliderObject.gameData);
                    }
                }
            }
        });

        layoutManager = new CustomLinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvSlider.setLayoutManager(layoutManager);
        rvSlider.setNestedScrollingEnabled(false);
        rvSlider.setOnFlingListener(null);
        rvSlider.setAdapter(adapterSlider);

        // Dot indicator for banner
        final int radius = getResources().getDimensionPixelSize(R.dimen.dot_radius);
        final int dotsHeight = getResources().getDimensionPixelSize(R.dimen.dot_height);
        final int color = ContextCompat.getColor(getContext(), R.color.colorAccent);
        final int inActiveColor = ContextCompat.getColor(getContext(), R.color.game_gray);
       *//* if (!flagDecoration) {
            rvSlider.addItemDecoration(new DotsIndicatorDecoration(radius, radius * 2, dotsHeight, inActiveColor, color));
            flagDecoration = true;
        }*//*
        new PagerSnapHelper().attachToRecyclerView(rvSlider);

        rvSlider.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
        *//*new PagerSnapHelper().attachToRecyclerView(rvSlider);*//*
    }*/

   /* private Handler sliderHandler;
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
    }*/

   /* @SuppressLint("SetTextI18n")
    private void changeSliderPage() {
        if (homeSliderObjectArrayList.size() <= 1) return;
        try {
            LinearLayoutManager layoutManager = (LinearLayoutManager) rvSlider.getLayoutManager();
            if (layoutManager != null) {
                visibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition();
                if (visibleItemPosition > -1 && visibleItemPosition < homeSliderObjectArrayList.size()) {
                    if (visibleItemPosition == homeSliderObjectArrayList.size() - 1) {
                        // Scroll to first item
                        rvSlider.smoothScrollToPosition(0);
                    } else {
                        // Scroll to next item
                        rvSlider.smoothScrollToPosition(visibleItemPosition + 1);
                    }
                }
            }
        } catch (Exception ignored) {

        }
    }*/

    // Set the slider recyclerview of Play Tab
    /*private void setPlaySliderRecyclerView() {
        adapterSlider = new KRecyclerViewAdapter(getContext(), arrayListGameData, new KRecyclerViewHolderCallBack() {
            @Override
            public KRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_slider, viewGroup, false);
                return new SliderHolder(layoutView, arrayListGameData.size());
            }
        }, new KRecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClicked(KRecyclerViewHolder kRecyclerViewHolder, Object o, int i) {
                if (o instanceof GameData) {
                    GameData gameData = (GameData) o;
                    setActionOnPlaySlider(gameData);
                }
            }
        });

        rvSlider.setAdapter(adapterSlider);
    }*/

    /**
     * Initializes Tabs with ViewPager
     */
   /* private void setTabsWithViewPager() {

        // Set ViewPager adapter
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        for (int i = 0; i < arrTabLabels.length; i++) {
            // Sets arguments to pass data to the fragment
            Fragment tabFragment = arrTabFragments[i];
            //tabFragment.setArguments(bundle);
            viewPagerAdapter.addFragment(tabFragment, arrTabLabels[i]);
        }
        vpHome.setAdapter(viewPagerAdapter);
        tlHomeTabs.setupWithViewPager(vpHome);
        //setTabTextNIcons();

        tlHomeTabs.addOnTabSelectedListener(this);

        //showDividerBwTabs();
    }*/

    /**
     * Sets Tab text label and icon
     * Also sets the selected tab text and icon color
     */
    @SuppressLint("ResourceType")
    private void setTabTextNIcons() {
    /*
    New Code for setting up custom tab icons
    */

        View view = LayoutInflater.from(getContext()).inflate(R.layout.custom_tab, null);
        TextView tvCustomTab = view.findViewById(R.id.tab1);
        TextView tvCustomTab1 = view.findViewById(R.id.tab2);
        TextView tvCustomTab2 = view.findViewById(R.id.tab3);

        tvCustomTab.setTextColor(getResources().getColorStateList(R.drawable.tab_selector));
        tvCustomTab1.setTextColor(getResources().getColorStateList(R.drawable.tab_selector));
        tvCustomTab2.setTextColor(getResources().getColorStateList(R.drawable.tab_selector));

        tvCustomTab.setTypeface(ResourcesCompat.getFont(view.getContext(), R.font.rubik_medium));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tvCustomTab.setCompoundDrawableTintList(getResources().getColorStateList(R.drawable.tab_selector));
            tvCustomTab1.setCompoundDrawableTintList(getResources().getColorStateList(R.drawable.tab_selector));
            tvCustomTab2.setCompoundDrawableTintList(getResources().getColorStateList(R.drawable.tab_selector));
        } else {
            ColorStateList colors = getResources().getColorStateList(R.drawable.tab_selector);
            Drawable icon, icon1, icon2;
            icon = getResources().getDrawable(R.drawable.ic_listen);
            icon1 = getResources().getDrawable(R.drawable.ic_games);
            icon2 = getResources().getDrawable(R.drawable.ic_buy);
            if (icon != null) {
                icon = DrawableCompat.wrap(icon);
                DrawableCompat.setTintList(icon, colors);
                int height = icon.getIntrinsicHeight();
                int width = icon.getIntrinsicWidth();
                icon.setBounds(0, 0, width, height);
                icon1.setBounds(0, 0, width, height);
                icon2.setBounds(0, 0, width, height);
                tvCustomTab.setCompoundDrawables(icon, null, null, null);
                tvCustomTab1.setCompoundDrawables(icon1, null, null, null);
                tvCustomTab2.setCompoundDrawables(icon2, null, null, null);
            }
        }

       /* tlHomeTabs.getTabAt(0).setCustomView(tvCustomTab);
        tlHomeTabs.getTabAt(1).setCustomView(tvCustomTab1);
        tlHomeTabs.getTabAt(2).setCustomView(tvCustomTab2);*/

       /*for (int i = 0; i < arrTabIcons.length; i++) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.custom_tab, null);
            TextView tvCustomTab = view.findViewById(R.id.tab);
            tvCustomTab.setText(arrTabLabels[i]);
            tvCustomTab.setTextColor(getResources().getColorStateList(R.drawable.tab_selector));
            tvCustomTab.setId(i);


            // If block to set icon to the selected tab for Marshmallow or height API version
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                tvCustomTab.setCompoundDrawableTintList(getResources().getColorStateList(R.drawable.tab_selector));
                tvCustomTab.setCompoundDrawablesWithIntrinsicBounds(arrTabIcons[i], 0, 0, 0);
                tvCustomTab.setGravity(Gravity.CENTER);
            }


            // Else block to set icon to the selected tab for API version lower than Marshmallow
            else {
                ColorStateList colors = getResources().getColorStateList(R.drawable.tab_selector);
                Drawable icon = getResources().getDrawable(arrTabIcons[i]);
                if (icon != null) {
                    icon = DrawableCompat.wrap(icon);
                    DrawableCompat.setTintList(icon, colors);
                    int height = icon.getIntrinsicHeight();
                    int width = icon.getIntrinsicWidth();
                    icon.setBounds(0, 0, width, height);
                    tvCustomTab.setCompoundDrawables(icon, null, null, null);
                }
            }
            tlHomeTabs.getTabAt(i).setCustomView(tvCustomTab);
        }*/

    }

    /**
     * Updates UI i.e Sliders and selected tab fragment data on onTabSelected (Watch and Buy Tab)
     */
    /*public void updateOtherSliders(ArrayList<FeedCombinedData> arrayListFeedData) {
        setOtherSliderRecyclerView();
        flag = true;
        arrayListFeedContentData.clear();
        homeSliderObjectArrayList.clear();
        new Thread(() -> {
            for (FeedCombinedData feedData : arrayListFeedData) {
                if (feedData.viewType.equals(FeedData.VIEW_TYPE_SLIDER)) {
                    sliderId = String.valueOf(feedData.id);
                    *//*arrayListFeedContentData.addAll(feedData.arrayListFeedContent);*//*
                    for (int i = 0; i < feedData.contentsJSONArray.length(); i++) {
                        try {
                            homeSliderObjectArrayList.add(new HomeSliderObject(feedData.contentsJSONArray.getJSONObject(i), i));
                            //tvBannerNumber.setText("1/" + homeSliderObjectArrayList.size());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                }
            }

            //ESports Banner
            *//*JSONObject jsonObject=new JSONObject();
            HomeSliderObject homeSliderObject=new HomeSliderObject(jsonObject,homeSliderObjectArrayList.size()+1);
            homeSliderObject.contentType="audio_post";
            homeSliderObject.feedContentData.postTitle=Constant.eSportsTitle;
            homeSliderObjectArrayList.add(homeSliderObject);*//*
            new Handler(Looper.getMainLooper()).post(() -> {
                if (adapterSlider != null)
                    adapterSlider.notifyDataSetChanged();
                //startSliding();
                startSliding();
            });
        }).start();
    }*/

    /**
     * Updates UI i.e Sliders and selected tab fragment data on onTabSelected (Play Tab)
     */
    /*public void updatePlaySliders(ArrayList<GameData> arrayListGameData) {
        setPlaySliderRecyclerView();
        this.arrayListGameData.clear();
        new Thread(() -> {

            this.arrayListGameData.addAll(arrayListGameData);
            new Handler(Looper.getMainLooper()).post(() -> {
                if (adapterSlider != null)
                    adapterSlider.notifyDataSetChanged();
            });
        }).start();

    }*/

    /**
     * Shows divider line between each tab
     */
    /*private void showDividerBwTabs() {
        LinearLayout linearLayout = (LinearLayout) tlHomeTabs.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.GRAY);
        drawable.setSize(1, 1);
        linearLayout.setDividerPadding(45);
        linearLayout.setDividerDrawable(drawable);
    }*/
    /*private void setActionOnSlider(FeedContentData feedContentData) {

     *//* if (feedContentData.feedTabType.equals(Constant.TAB_WATCH)) {*//*

        if (feedContentData.postExcerpt.equalsIgnoreCase("paid")) {
            // logic for buy/paid item
            SubscriptionDetails sd = LocalStorage.getUserSubscriptionDetails();
            //if (!feedContentData.isTrailer) {
            if (sd != null && sd.allow_rental != null && !sd.allow_rental.equals("")) {
                if (sd.allow_rental.equalsIgnoreCase(Constant.YES)) {
                    if (feedContentData.postType.equals(POST_TYPE_ORIGINALS)) {
                        Intent intent = new Intent(getActivity(), BuyDetailActivity.class);
                        intent.putExtra(BuyDetailActivity.CONTENT_DATA, feedContentData);
                        intent.putExtra(BuyDetailActivity.FROM_SLIDER, "sliderPageKey");
                        getActivity().startActivityForResult(intent, BuyDetailActivity.REQUEST_CODE);
                        //showAd(sliderId);
                    } else {
                        ArrayList<FeedContentData> arrayListFeedContentData = new ArrayList<>();
                        arrayListFeedContentData.add(feedContentData);
                        activity.showAudioSongExpandedView(arrayListFeedContentData, 0, sliderId, Analyticals.CONTEXT_SLIDER, "");
                    }
                }
            } else {
                Intent intent = new Intent(getActivity(), BuyDetailActivity.class);
                intent.putExtra(BuyDetailActivity.CONTENT_DATA, feedContentData);
                intent.putExtra(BuyDetailActivity.FROM_SLIDER, "sliderPageKey");
                getActivity().startActivityForResult(intent, BuyDetailActivity.REQUEST_CODE);
                showAd(sliderId);
            }
            *//*} else if (feedContentData.isTrailer) {
                Intent intent = new Intent(getActivity(), BuyDetailActivity.class);
                intent.putExtra(BuyDetailActivity.CONTENT_DATA, feedContentData);
                intent.putExtra(BuyDetailActivity.FROM_SLIDER, "sliderPageKey");
                getActivity().startActivityForResult(intent, BuyDetailActivity.REQUEST_CODE);
            }*//*
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
                    //HomeActivity activity = (HomeActivity) getActivity();
                    ArrayList<FeedContentData> arrayListFeedContentData = new ArrayList<>();
                    arrayListFeedContentData.add(feedContentData);
                    //activity.playItems(arrayListFeedContentData, 0, sliderId, Analyticals.CONTEXT_SLIDER, "");
                    activity.showAudioSongExpandedView(arrayListFeedContentData, 0, sliderId, Analyticals.CONTEXT_SLIDER, "");
                    break;
                case FeedContentData.CONTENT_TYPE_ALL_M_ORIGINALS:
                    activity.showSeriesExpandedView(feedContentData.seriesId, feedContentData.seasonId, feedContentData.episodeId, String.valueOf(feedContentData.iswatchlisted));
                    break;
                case FeedContentData.CONTENT_TYPE_SEASON:
                    activity.showSeriesExpandedView(feedContentData.seriesId, feedContentData.seasonId, feedContentData.episodeId, "");
                    break;
                case FeedContentData.CONTENT_TYPE_ALL_M_STREAM:
                    //playChannelAPI(feedContentData, 1, sliderId, Analyticals.CONTEXT_SLIDER);
                    activity.showChannelExpandedView(feedContentData, 1, sliderId, Analyticals.CONTEXT_CHANNEL);
                    break;
                case FeedContentData.CONTENT_TYPE_EPISODE:
                    activity.showSeriesExpandedView(feedContentData.seriesId, feedContentData.seasonId, feedContentData.episodeId, "");
                    break;
                case FeedContentData.CONTENT_TYPE_PLAYLIST:
                    activity.showPlaylistExpandedView(feedContentData.postId, String.valueOf(feedContentData.iswatchlisted));
                    break;
                case FeedContentData.CONTENT_TYPE_ESPORTS:
                    //startActivity(new Intent(getActivity(), TournamentRegistrationFormActivity.class));
                    getUserRegisteredTournamentInfo(feedContentData.postId);
                    break;
            }
        }
        *//*} else if (feedContentData.feedTabType.equals(Constant.TAB_BUY)) {
            Intent intent = new Intent(getActivity(), BuyDetailActivity.class);
            intent.putExtra(BuyDetailActivity.CONTENT_DATA, feedContentData);
            getActivity().startActivityForResult(intent, BuyDetailActivity.REQUEST_CODE);
        }*//*
    }*/
    private void getActiveTabs() {
        try {
            Map<String, String> params = new HashMap<>();
            APIRequest apiRequest = new APIRequest(Url.GET_HOME_TABS + Constant.SCREEN_HOME, Request.Method.GET, params, null, getContext());
            apiRequest.showLoader = true;
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
                        handleTabsJSONArray(jsonArrayMsg);
                    }
                }
            }
        } catch (JSONException e) {
        }
    }

    private void handleTabsJSONArray(JSONArray dataJSON) throws JSONException {
        arrayListTabData.clear();
        for (int i = 0; i < dataJSON.length(); i++) {
            HomeTabsList tabs =new HomeTabsList(dataJSON.getJSONObject(i));
            arrayListTabData.add(tabs);
        }
        if(arrayListTabData.size()>1) {
            setSliderData();
            tlHomeTabs.setVisibility(View.VISIBLE);
            appBarLayout.setVisibility(View.VISIBLE);
        }else{
            tlHomeTabs.setVisibility(View.GONE);
            appBarLayout.setVisibility(View.GONE);
        }
    }

    private void setSliderData() {
        // Set View Pager
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        for (int i = 0; i < arrayListTabData.size(); i++) {
            /*String tabType = arrayListTabData.get(i).title;
            Fragment tabFragment = HomeSliderTabFragment.getInstance(tabType,Constant.SCREEN_HOME);
            ftList.add((HomeSliderTabFragment) tabFragment);
            viewPagerAdapter.addFragment(tabFragment, "Position " + i);*/
        }
        vpHome.setAdapter(viewPagerAdapter);
        tlHomeTabs.setupWithViewPager(vpHome);
        setHomeTabText();
        tlHomeTabs.addOnTabSelectedListener(this);
    }

    @SuppressLint("ResourceType")
    private void setHomeTabText() {
        for (int i = 0; i < arrayListTabData.size(); i++) {
            TextView tvCustomTab = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.custom_home_tab, null);
            String title = arrayListTabData.get(i).title.substring(0, 1).toUpperCase() + arrayListTabData.get(i).title.substring(1).toLowerCase();
            tvCustomTab.setText(title);
            tvCustomTab.setTextColor(getResources().getColorStateList(R.drawable.tab_selector));
            if (i == 0)
                tvCustomTab.setTypeface(ResourcesCompat.getFont(getContext(), R.font.rubik_medium));
            tlHomeTabs.getTabAt(i).setCustomView(tvCustomTab);
        }
    }

    private void setActionOnSliderAd(FeedContentData feedContentData) {
        switch (feedContentData.adFieldsData.adType) {
            case FeedContentData.AD_TYPE_IN_APP:
                if (feedContentData.adFieldsData.contentType.equalsIgnoreCase(Constant.PAGES)) {
                    HomeActivity.getInstance().redirectToPage(feedContentData.adFieldsData.pageType);
                } else {
                    HomeActivity.getInstance().getMediaData(String.valueOf(feedContentData.adFieldsData.contentId),feedContentData.adFieldsData.contentType);
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

    private void playItems(ArrayList<FeedContentData> itemsToPlay, int position, String feedId, String pageType) {
        if (getActivity() != null && getActivity() instanceof HomeActivity) {
            activity = (HomeActivity) getActivity();
            if (activity != null) {
                switch (itemsToPlay.get(position).postType) {
                    case POST_TYPE_PLAYLIST:
                        activity.showPlaylistExpandedView(itemsToPlay.get(position).postId, String.valueOf(itemsToPlay.get(position).iswatchlisted));
                        break;
                    case POST_TYPE_ORIGINALS:
                        activity.showSeriesExpandedView(itemsToPlay.get(position).postId, "0", "0", String.valueOf(itemsToPlay.get(position).iswatchlisted));
                        break;
                    case POST_TYPE_EPISODE:
                        activity.showSeriesExpandedView(itemsToPlay.get(position).seriesId, itemsToPlay.get(position).seasonId, itemsToPlay.get(position).episodeId, "");
                        break;
                    case POST_TYPE_STREAM:
                        activity.showChannelExpandedView(itemsToPlay.get(position), 1, feedId, pageType);
                        break;
                    case POST_TYPE_POST:
                        activity.playItems(itemsToPlay, position, feedId, "feed", pageType);
                        break;
                }
            }
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

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        //rvSlider.setVisibility(tab.getPosition() == 1 ? View.GONE : View.VISIBLE);
        TextView tvTabTextView = (TextView) tab.getCustomView();
        if (tvTabTextView != null) {
            tvTabTextView.setTypeface(ResourcesCompat.getFont(view.getContext(), R.font.rubik_medium));
            for (Drawable drawable : tvTabTextView.getCompoundDrawables()) {
                if (drawable != null) {
                    drawable.mutate().setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(getActivity(), R.color.colorAccent), PorterDuff.Mode.SRC_IN));
                }
            }
        }
        appBarLayout.setExpanded(true);
        // updateSliderOnTabChanged(tab.getPosition());
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

    /*public void updateSliderOnTabChanged(int tabPosition) {

        switch (tabPosition) {
            // Watch
            case 0:
                homeCombinedTabFragment.updateSlider(tabPosition);
                break;
            // Play
            case 1:
                homePlayTabFragment.updateSliders(tabPosition);
                break;
            // Buy
            case 2:
                homeBuyTabFragment.updateSlider(tabPosition);
                break;
        }

    }*/

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (!isConnectionRestored) {
            APIManager.cancelAllRequests(APIManager.PAGE_NAVIGATION);
            isConnectionRestored = false;
        } else {
            isConnectionRestored = false;
        }
    }

    private void redirectToPage() {
        switch (Constant.ACTION) {
            case Constant.LUCKY_WINNER:
                JSONObject json_object = null;
                String gameId = "";
                try {
                    json_object = new JSONObject(getArguments().getString(Constant.NOTIFICATION_DATA));
                    gameId = json_object.has("gameId") ? json_object.getString("gameId") : "";
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(activity, LuckyWinnerActivity.class);
                intent.putExtra(LuckyWinnerActivity.KEY_GAME_ID, gameId);
                break;
            case Constant.PLAYLIST:
                JSONObject json_object1 = null;
                String ID = "";
                FeedContentData feedContentDataPlaylist;
                try {
                    json_object1 = new JSONObject(getArguments().getString(Constant.NOTIFICATION_DATA));
                    ID = json_object1.has("ID") ? json_object1.getString("ID") : "";
                    feedContentDataPlaylist = new FeedContentData(json_object1.getJSONObject("post"), -1);
                    if (feedContentDataPlaylist == null) {
                        activity.showPlaylistExpandedView(ID, "false");
                    } else {
                        if (feedContentDataPlaylist.postExcerpt.equalsIgnoreCase("free")) {
                            activity.showPlaylistExpandedView(ID, "false");
                        } else {
                            Intent intent1 = new Intent(getActivity(), BuyDetailActivity.class);
                            intent1.putExtra(BuyDetailActivity.CONTENT_DATA, feedContentDataPlaylist);
                            getActivity().startActivityForResult(intent1, BuyDetailActivity.REQUEST_CODE);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Constant.GAME:
                JSONObject json_object2 = null;
                String gameID = "";
                JSONObject gamedata = null;
                try {
                    json_object2 = new JSONObject(getArguments().getString(Constant.NOTIFICATION_DATA));
                    gameID = json_object2.has("ID") ? json_object2.getString("ID") : "";
                    gamedata = json_object2.has("game") ? json_object2.getJSONObject("game") : new JSONObject();
                    GameData gameData = new GameData(gamedata, gameID);
                    switch (gameData.quizType) {
                        case GameData.QUIZE_TYPE_LIVE:
                            activity.openFragment(HomePlayTabFragment.newInstance());
                            String invalidDateMessage = GameDateValidation.getInvalidDateMsg(getActivity(), gameData.start, gameData.end);
                            if (invalidDateMessage.equals("")) {
                                if (gameData.playedGame) {
                                    AlertUtils.showToast(getString(R.string.msg_game_played), Toast.LENGTH_SHORT, getActivity());
                                    return;
                                }
                                Bundle bundle = new Bundle();
                                bundle.putParcelable(GameStartActivity.GAME_DATA, gameData);
                                startActivityForResult(new Intent(getActivity(), GameStartActivity.class)
                                        .putExtra("Bundle", bundle), GameData.REQUEST_CODE_GAME);
                            } else {
                                if (gameData.winnersDeclared) {
                                    startActivity(
                                            new Intent(getContext(), LuckyWinnerActivity.class)
                                                    .putExtra(LuckyWinnerActivity.KEY_GAME_ID, gameData.id)
                                    );
                                    return;
                                }
                                AlertUtils.showToast(invalidDateMessage, Toast.LENGTH_SHORT, getActivity());
                            }
                            break;

                        case GameData.QUIZE_TYPE_TURN_BASED:
                            Bundle bundle = new Bundle();
                            bundle.putParcelable(GameStartActivity.GAME_DATA, gameData);
                            startActivityForResult(new Intent(getContext(), GameStartActivity.class)
                                    .putExtra("Bundle", bundle), GameData.REQUEST_CODE_GAME);
                            break;
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;
            case Constant.EPISODE:
                JSONObject json_object3 = null;
                String episodeid = "";
                JSONObject jsonObject = null;
                FeedContentData feedContentData = null;
                try {
                    json_object3 = new JSONObject(getArguments().getString(Constant.NOTIFICATION_DATA));
                    episodeid = json_object3.has("ID") ? json_object3.getString("ID") : "";
                    jsonObject = json_object3.has("post") ? json_object3.getJSONObject("post") : new JSONObject();
                    feedContentData = new FeedContentData(json_object3.getJSONObject("series"), -1);
                    if (feedContentData == null) {
                        String seriesId = jsonObject.has("series") ? jsonObject.getString("series") : String.valueOf(feedContentData.id);
                        String seasonId = jsonObject.has("season") ? jsonObject.getString("season") : "";
                        activity.showSeriesExpandedView(seriesId, seasonId, episodeid, "");
                    } else {
                        if (feedContentData.postExcerpt.equalsIgnoreCase("free")) {
                            String seriesId = jsonObject.has("series") ? jsonObject.getString("series") : String.valueOf(feedContentData.id);
                            String seasonId = jsonObject.has("season") ? jsonObject.getString("season") : "";
                            activity.showSeriesExpandedView(seriesId, seasonId, episodeid, "");
                        } else {
                            Intent intent1 = new Intent(getActivity(), BuyDetailActivity.class);
                            intent1.putExtra(BuyDetailActivity.CONTENT_DATA, feedContentData);
                            getActivity().startActivityForResult(intent1, BuyDetailActivity.REQUEST_CODE);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Constant.GAME_LIST:
                activity.openFragment(HomePlayTabFragment.newInstance());
                break;
            case Constant.COUPONS:
                JSONObject json_object4 = null;
                try {
                    json_object4 = new JSONObject(getArguments().getString(Constant.NOTIFICATION_DATA));
                    CouponData couponData = new CouponData(json_object4.getJSONObject("post"));
                    String walletBalance = "0";
                    if (HomeActivity.toolbarWalletBalance != null) {
                        walletBalance = HomeActivity.toolbarWalletBalance.getText().toString();
                    }
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("CoupanData", couponData);
                    bundle.putString("Balance", walletBalance);
                    startActivityForResult(
                            new Intent(getActivity(), CouponDetailsActivity.class).putExtra("CoupanDetail", bundle),
                            REQUEST_CODE
                    );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Constant.ORIGINALS:
                JSONObject json_object5 = null;
                String seriesid = "";
                FeedContentData feedContentData1 = null;
                try {
                    json_object5 = new JSONObject(getArguments().getString(Constant.NOTIFICATION_DATA));
                    seriesid = json_object5.has("ID") ? json_object5.getString("ID") : "";
                    feedContentData1 = new FeedContentData(json_object5.getJSONObject("post"), -1);
                    if (feedContentData1 == null) {
                    } else {
                        if (feedContentData1.postExcerpt.equalsIgnoreCase("free")) {
                            activity.showSeriesExpandedView(seriesid, feedContentData1.seasonId, feedContentData1.episodeId, "");
                        } else {
                            Intent intent1 = new Intent(getActivity(), BuyDetailActivity.class);
                            intent1.putExtra(BuyDetailActivity.CONTENT_DATA, feedContentData1);
                            getActivity().startActivityForResult(intent1, BuyDetailActivity.REQUEST_CODE);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;
            case Constant.COUPON_LISTING:
                startActivityForResult(
                        new Intent(getActivity(), RedeemCoinsActivity.class)
                                .putExtra(KEY_WALLET_BALANCE, "0"),
                        REQUEST_CODE
                );
                break;
            case Constant.CHANNEL:
                JSONObject json_object_channel = null;
                String channelid = "";
                FeedContentData feedContentDatachannel = null;
                try {
                    json_object_channel = new JSONObject(getArguments().getString(Constant.NOTIFICATION_DATA));
                    channelid = json_object_channel.has("ID") ? json_object_channel.getString("ID") : "";
                    feedContentDatachannel = new FeedContentData(json_object_channel.getJSONObject("post"), -1);
                    activity.showChannelExpandedView(feedContentDatachannel, 1, channelid, Analyticals.CONTEXT_CHANNEL);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Constant.LEADERBOARD:
                startActivity(new Intent(getActivity(), LeaderBoardActivity.class));
                break;
            case Constant.DOWNLOADS:
                //startActivity(new Intent(getActivity(), HomeActivity.class));
               /* if (tlHomeTabs != null) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tlHomeTabs.getTabAt(0).select();
                        }
                    }, 100);
                }*/
                startActivity(new Intent(getContext(), DownloadActivity.class).putExtra("title", getResources().getString(R.string.label_downloads)));
                break;
            default:
                Constant.ACTION = getResources().getString(R.string.no_action);
                break;
        }

    }

    public void getUserRegisteredTournamentInfo(String postId) {
        try {
            Map<String, String> params = new HashMap<>();
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

    public void showAd(String id) {
        if (getActivity() != null && getActivity() instanceof HomeActivity) {
            activity = (HomeActivity) getActivity();
            if (activity != null) {
                activity.showAd(id);
            }
        }
    }

    public void panelListener(boolean isPanelExpanded) {
        //homeCombinedTabFragment.panelListener(isPanelExpanded);
    }
}
