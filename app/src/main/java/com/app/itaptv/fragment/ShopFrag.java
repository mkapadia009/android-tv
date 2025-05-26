package com.app.itaptv.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.app.itaptv.API.APIManager;
import com.app.itaptv.API.APIMethods;
import com.app.itaptv.MyApp;
import com.app.itaptv.R;
import com.app.itaptv.activity.BrowserActivity;
import com.app.itaptv.activity.HomeActivity;
import com.app.itaptv.adapter.ViewPagerAdapter;
import com.app.itaptv.custom_interface.SliderListener;
import com.app.itaptv.custom_widget.DotsIndicatorDecoration;
import com.app.itaptv.holder.CustomAdSliderHolder;
import com.app.itaptv.structure.AdFieldsData;
import com.app.itaptv.structure.AdMobData;
import com.app.itaptv.structure.FeedContentData;
import com.app.itaptv.utils.AnalyticsTracker;
import com.app.itaptv.utils.Constant;
import com.app.itaptv.utils.CustomLinearLayoutManager;
import com.app.itaptv.utils.LocalStorage;
import com.app.itaptv.utils.Log;
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
import com.google.android.material.tabs.TabLayout;
import com.kalpesh.krecycleradapter.Interface.KRecyclerItemClickListener;
import com.kalpesh.krecycleradapter.Interface.KRecyclerViewHolderCallBack;
import com.kalpesh.krecycleradapter.KRecyclerViewAdapter;
import com.kalpesh.krecycleradapter.KRecyclerViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShopFrag extends Fragment implements TabLayout.OnTabSelectedListener {

    private View view;
    public static TabLayout tabLayoutRewards;
    ViewPager viewPagerRewards;
    ViewPagerAdapter viewPagerAdapter;
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

    String[] arrayRewardTabLabels;

    // Tab Fragments
    RedeemFrag redeemFrag = new RedeemFrag();
    EarnFrag earnFrag = new EarnFrag();
    WalletFrag walletFrag = new WalletFrag();
    Fragment[] arrTabFragments = {redeemFrag, earnFrag, walletFrag};
    int[] arrTabIcons = {R.drawable.ic_action_vouchers, R.drawable.ic_earn_tab, R.drawable.ic_wallet_tab};
    HomeActivity activity;

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

    public static ShopFrag newInstance() {
        return new ShopFrag();
    }

    public static int selectedTab = 0;

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
            view = inflater.inflate(R.layout.fragment_rewards, container, false);
        }
        init();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            ((HomeActivity) getActivity()).showToolbarIcon();
            ((HomeActivity) getActivity()).showToolbarTitle(false);
        }

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
    public void onDestroy() {
        super.onDestroy();
        closePlayer();
    }

    private void init() {
        activity = (HomeActivity) getActivity();
        arrayRewardTabLabels = getResources().getStringArray(R.array.array_rewards_tabs);
        tabLayoutRewards = view.findViewById(R.id.tabLayoutRewards);
        viewPagerRewards = view.findViewById(R.id.viewPagerRewards);

        mAdView = view.findViewById(R.id.adViewRewardsFrag);
        clAdHolder = view.findViewById(R.id.cl_ad_holder);
        ivCustomAd = view.findViewById(R.id.ivCustomAd);
        ivClose = view.findViewById(R.id.ivClose);
        playerView = view.findViewById(R.id.playerView);
        ivVolumeUp = view.findViewById(R.id.ivVolumeUp);
        ivVolumeOff = view.findViewById(R.id.ivVolumeOff);
        rvSliderAd = view.findViewById(R.id.rvSliderAd);
        adRequest = new AdRequest.Builder().build();

        String id = getResources().getResourceEntryName(view.findViewById(R.id.adViewRewardsFrag).getId());
        showBannerAd(id);

        setTabsWithViewPager();
    }

    /**
     * Initializes Tabs with ViewPager
     */
    private void setTabsWithViewPager() {
        // Set ViewPager adapter
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        for (int i = 0; i < arrayRewardTabLabels.length; i++) {
            // Sets arguments to pass data to the fragment
            Fragment tabFragment = arrTabFragments[i];
            //tabFragment.setArguments(bundle);
            viewPagerAdapter.addFragment(tabFragment, arrayRewardTabLabels[i]);
        }
        viewPagerRewards.setOffscreenPageLimit(3);
        viewPagerRewards.setAdapter(viewPagerAdapter);
        tabLayoutRewards.setupWithViewPager(viewPagerRewards);
        setTabTextNIcons();
        tabLayoutRewards.addOnTabSelectedListener(this);
    }

    /**
     * Sets Tab text label and icon
     * Also sets the selected tab text and icon color
     */
    @SuppressLint("ResourceType")
    private void setTabTextNIcons() {

        /*
        New Code for setting up custom tab icons
        */
        View view = LayoutInflater.from(getContext()).inflate(R.layout.custom_reward_tab, null);
        TextView tvCustomTab = view.findViewById(R.id.redeemTab);
        TextView tvCustomTab1 = view.findViewById(R.id.earnTab);
        TextView tvCustomTab2 = view.findViewById(R.id.walletTab);

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
            icon = getResources().getDrawable(R.drawable.ic_action_vouchers);
            icon1 = getResources().getDrawable(R.drawable.ic_earn_tab);
            icon2 = getResources().getDrawable(R.drawable.ic_wallet_tab);
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

        tabLayoutRewards.getTabAt(0).setCustomView(tvCustomTab);
        tabLayoutRewards.getTabAt(1).setCustomView(tvCustomTab1);
        tabLayoutRewards.getTabAt(2).setCustomView(tvCustomTab2);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        TextView tvTabTextView = (TextView) tab.getCustomView();
        if (tvTabTextView != null) {
            String id = getResources().getResourceEntryName(tvTabTextView.getId());
            Log.i("RewardsFrag", String.valueOf(id));
            showAd(String.valueOf(id));
            tvTabTextView.setTypeface(ResourcesCompat.getFont(view.getContext(), R.font.rubik_medium));
            for (Drawable drawable : tvTabTextView.getCompoundDrawables()) {
                if (drawable != null) {
                    drawable.mutate().setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(getActivity(), R.color.colorAccent), PorterDuff.Mode.SRC_IN));
                }
            }
        }
        if (tab.getPosition() == 0) {
            AnalyticsTracker.startDurationShop();
        } else {
            AnalyticsTracker.stopDurationShop();
        }
    }

    private void showAd(String id) {
        if (activity != null && activity instanceof HomeActivity) {
            activity.showAd(id);
        }
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        APIManager.cancelAllRequests(APIManager.PAGE_NAVIGATION);
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
                                    try {
                                        if (player != null && player.isPlaying()) {
                                            player.setVolume(0.0f);
                                            ivVolumeUp.setVisibility(View.GONE);
                                            ivVolumeOff.setVisibility(View.VISIBLE);
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
                                            player.setVolume(1.0f);
                                            ivVolumeOff.setVisibility(View.GONE);
                                            ivVolumeUp.setVisibility(View.VISIBLE);
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

    private void setActionCustomAd(AdFieldsData adFieldsData, String location, String subLocation) {
        APIMethods.addEvent(getContext(), Constant.CLICK, adFieldsData.postId, location, subLocation);
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
                            HomeActivity.getInstance().getMediaData(String.valueOf(adFieldsData.contentId), adFieldsData.contentType);
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
                startActivity(new Intent(getContext(), BrowserActivity.class).putExtra("title", "").putExtra("posturl", adFieldsData.externalUrl));
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
                } catch (Exception e) {
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
}
