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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
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
import com.app.itaptv.activity.GameStartActivity;
import com.app.itaptv.activity.HomeActivity;
import com.app.itaptv.activity.LuckyWinnerActivity;
import com.app.itaptv.custom_interface.CustomRunnable;
import com.app.itaptv.custom_interface.OnFeedRefreshListener;
import com.app.itaptv.custom_interface.SliderListener;
import com.app.itaptv.custom_widget.DotsIndicatorDecoration;
import com.app.itaptv.holder.CustomAdSliderHolder;
import com.app.itaptv.holder.FeedGameHolder;
import com.app.itaptv.holder.ListenHolder;
import com.app.itaptv.structure.AdFieldsData;
import com.app.itaptv.structure.AdMobData;
import com.app.itaptv.structure.FeedContentData;
import com.app.itaptv.structure.FeedGameData;
import com.app.itaptv.structure.GameData;
import com.app.itaptv.utils.AlertUtils;
import com.app.itaptv.utils.Constant;
import com.app.itaptv.utils.CustomLinearLayoutManager;
import com.app.itaptv.utils.EmptyStateManager;
import com.app.itaptv.utils.GameDateValidation;
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
import com.kalpesh.krecycleradapter.Interface.KRecyclerItemClickListener;
import com.kalpesh.krecycleradapter.Interface.KRecyclerViewHolderCallBack;
import com.kalpesh.krecycleradapter.KRecyclerViewAdapter;
import com.kalpesh.krecycleradapter.KRecyclerViewHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomePlayTabFragment extends Fragment {

    View view;
    RecyclerView rvPlay;
    ProgressBar progressBar;

    EmptyStateManager emptyState;
    KRecyclerViewAdapter adapterViews;
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

    private ExoPlayer player;

    OnFeedRefreshListener onFeedRefreshListener;
    ArrayList<FeedGameData> arrayListFeedGameData = new ArrayList<>();
    ArrayList<GameData> arrayListSliderGameData = new ArrayList<>();
    ArrayList<ViewGroup> arrayListGameViews = new ArrayList<>();
    String TAG = HomePlayTabFragment.class.getName();
    boolean is_fragment_visible = false;
    boolean is_first_time_loading = true;

    HomeActivity homeActivity;

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

    //Game Activity Result Code

    public HomePlayTabFragment() {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GameData.REQUEST_CODE_GAME) {
            if (data != null && data.getBooleanExtra("GameStatus", false)) getGameListingAPI();
        }
    }

    public static HomePlayTabFragment newInstance() {
        return new HomePlayTabFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_home_play_tab, container, false);
            init();
        }
        return view;
    }

    /**
     * Initialize data
     */
    private void init() {
        onFeedRefreshListener = (OnFeedRefreshListener) getActivity();
        CustomRunnable.isCancelled = false;

        rvPlay = view.findViewById(R.id.rvPlay);
        progressBar = view.findViewById(R.id.progressBar);
        swipeRefreshLayout = view.findViewById(R.id.swipeToRefresh);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setGameListingRecyclerView();
                getGameListingAPI();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        setUpEmptyState();
        setGameListingRecyclerView();
        getGameListingAPI();
    }

    /**
     * Initialize recycler view
     */
    void setGameListingRecyclerView() {

        adapterViews = new KRecyclerViewAdapter(getContext(), arrayListGameViews, new KRecyclerViewHolderCallBack() {
            @Override
            public KRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_linear_item, viewGroup, false);
                return new ListenHolder(layoutView);
            }
        }, new KRecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClicked(KRecyclerViewHolder kRecyclerViewHolder, Object o, int i) {

            }
        });
        rvPlay.setAdapter(adapterViews);
        rvPlay.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        //rvPlay.addItemDecoration(new SpacingItemDecoration(Constant.RV_HV_SPACING));
        // getGameListingAPI();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.d("HomePlayTabFragment", "HomePlayTabFragment" + isVisibleToUser);
        if (isVisibleToUser) {
            /*if (getActivity() != null && getActivity() instanceof HomeActivity) {
                homeActivity = (HomeActivity) getActivity();
                if (homeActivity != null) {
                    homeActivity.showAd();
                }
            }*/
            is_fragment_visible = true;
            if (onFeedRefreshListener != null) updateSliders(1);
            if (is_first_time_loading) {
                is_first_time_loading = false;
                getGameListingAPI();
            }
        } else {
            is_fragment_visible = false;
        }
    }

    /**
     * Initialization of Empty State
     */
    void setUpEmptyState() {
        emptyState = EmptyStateManager.setUpInFragment(view, (AppCompatActivity) getActivity(), new EmptyStateManager.ActionBtnCallBack() {
            @Override
            public void onBtnClick(String action) {
                if (action.equals(EmptyStateManager.ACTION_RETRY)) {
                    getGameListingAPI();
                }
            }
        });
    }

    /**
     * Update of Empty State
     */
    private void updateEmptyState(String error) {
        if (error == null) {
            if (arrayListFeedGameData.isEmpty()) {
                emptyState.setImgAndMsg(getString(R.string.no_data_found), null);
                rvPlay.setVisibility(View.INVISIBLE);
            } else {
                rvPlay.setVisibility(View.VISIBLE);
                emptyState.hide();
            }
        } else {
            rvPlay.setVisibility(View.INVISIBLE);
            if (Utility.isConnectingToInternet(getContext())) {
                emptyState.setMessage(APIManager.GENERIC_API_ERROR_MESSAGE);
            } else {
                emptyState.showNoConnectionState();
            }
        }

    }

    /**
     * Returns Games data
     */
    private void getGameListingAPI() {
        try {
            Map<String, String> params = new HashMap<>();
            APIRequest apiRequest = new APIRequest(Url.GET_GAME_LISTING, Request.Method.GET, params, null, getContext());
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    progressBar.setVisibility(View.GONE);
                    rvPlay.setVisibility(View.VISIBLE);
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
        arrayListFeedGameData.clear();
        arrayListGameViews.clear();
        try {
            if (error != null) {
                // showError(error.getMessage());
                updateEmptyState(error.getMessage());
            } else {
                if (response != null) {
                    Log.e(";", response);

                    JSONObject jsonObjectResponse = new JSONObject(response);
                    String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                    if (type.equalsIgnoreCase("error")) {
                        String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                        showError(message);
                    } else if (type.equalsIgnoreCase("ok")) {
                        JSONObject jsonObjectMsg = jsonObjectResponse.getJSONObject("msg");
                        JSONArray jsonArrayBuckets = jsonObjectMsg.getJSONArray("buckets");
                        JSONArray jsonArraySlider = jsonObjectMsg.getJSONArray("slider");
                        for (int i = 0; i < jsonArrayBuckets.length(); i++) {
                            FeedGameData feedGameData = new FeedGameData(jsonArrayBuckets.getJSONObject(i));
                            arrayListFeedGameData.add(feedGameData);
                        }

                        for (int i = 0; i < jsonArraySlider.length(); i++) {
                            GameData gameData = new GameData(jsonArraySlider.getJSONObject(i), "");
                            arrayListSliderGameData.add(gameData);
                        }
                        new Handler(Looper.getMainLooper()).post(() -> updateUI());
                    }
                    updateEmptyState(null);
                }
            }
        } catch (JSONException e) {
            updateEmptyState(e.getMessage());
        }
    }

    void updateUI() {
/*
        if (is_fragment_visible) onFeedRefreshListener.refreshData(arrayListSliderGameData, 1);
*/
        new Thread(() -> {
            for (FeedGameData feedGameData : arrayListFeedGameData) {
                new Handler(Looper.getMainLooper()).post(() -> setHorizontalRecyclerView(feedGameData));
            }
            new Handler(Looper.getMainLooper()).post(() -> {
                adapterViews.notifyDataSetChanged();
            });

        }).start();

        /*   }*/
    }

    void updateSliders(int tabPosition) {
        // onFeedRefreshListener.refreshData(arrayListSliderGameData, tabPosition);

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

    FeedGameHolder feedGameHolder;

    private void setHorizontalRecyclerView(final FeedGameData feedGameData) {
        try {

            View viewContent = View.inflate(getContext(), R.layout.row_horizontal_list, null);

            LinearLayout llHzRow = viewContent.findViewById(R.id.llHzRow);
            TextView tvTitle = viewContent.findViewById(R.id.tvStoreName);
            RecyclerView rvContent = viewContent.findViewById(R.id.rvContent);
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
            adRequest = new AdRequest.Builder().build();
            if (feedGameData.id.equalsIgnoreCase("casual_games")) {
                String id = getResources().getResourceEntryName(viewContent.findViewById(R.id.adViewFeed).getId());
                showBannerAd(id);
            }

            ArrayList<GameData> arrayListGameData = feedGameData.arrayListGameData;

            if (arrayListGameData.size() == 0) {
                llHzRow.setVisibility(View.GONE);
                return;
            }

            llHzRow.setVisibility(View.VISIBLE);
            tvTitle.setText(feedGameData.postTitle);
            rvContent.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            rvContent.setNestedScrollingEnabled(false);
            // rvContent.addItemDecoration(new SpacingItemDecoration(Constant.RV_HV_SPACING, SpacingItemDecoration.LEFT));

            rlParentTitle.setPadding(Utility.getPercentPadding(getActivity(), 4), 20,
                    10, 30);
            rvContent.setPadding(Utility.getPercentPadding(getActivity(), 4), 0,
                    10, Utility.getPercentPadding(getActivity(), 4));

            KRecyclerViewAdapter adapter = new KRecyclerViewAdapter(getContext(), arrayListGameData, (viewGroup, i) -> {
                View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_feed_play_tab, viewGroup, false);
                feedGameHolder = new FeedGameHolder(layoutView);
                return feedGameHolder;
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
                                /*Intent intent = new Intent(getActivity(), WebViewActivity.class).putExtra(WebViewActivity.GAME_URL, gameData.webviewUrl);
                                startActivityForResult(intent, WebViewActivity.GAME_REDIRECT_REQUEST_CODE);*/

                                // initializing object for custom chrome tabs.
                                CustomTabsIntent.Builder customIntent = new CustomTabsIntent.Builder();
                                String token = LocalStorage.getToken();
                                String userId = LocalStorage.getUserId();
                                String url = null;
                                try {
                                    url = gameData.webviewUrl + "&authToken=" + token + "&userid=" + Utility.encryptData(userId, Constant.getSecretKeyDateTime(), Constant.getIvParameterDateTime());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                Utility.openCustomTab(getActivity(), customIntent.build(), Uri.parse(url));
                            }
                            break;
                        }
                        showAd(gameData.feedGameId);
                    }
                }
            });

            /*if (feedGameData.backgroundStatus.equalsIgnoreCase("Enable") && !feedGameData.backgroundImage.equalsIgnoreCase("false")) {
                Drawable drawable = new BitmapDrawable(getResources(), Utility.getBitmapFromURL(feedGameData.backgroundImage));
                rvContent.setBackground(drawable);
                rvContent.setPadding(Utility.getPercentPadding(getActivity(), 33), 5, 30, 5);
            }*/

            rvContent.setAdapter(adapter);
            // adapter.notifyDataSetChanged();
            rlParentTitle.setOnClickListener(v -> {
                ViewAllPlayFragment viewAllPlayFragment = ViewAllPlayFragment.newInstance(feedGameData.id, feedGameData.postTitle, Constant.SCREEN_HOME);
                openFragment(viewAllPlayFragment);
            });
            arrayListGameViews.add((ViewGroup) viewContent);
        } catch (Exception e) {
            e.printStackTrace();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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

    /*@Override
    public void onPause() {
        super.onPause();
        if(feedGameHolder != null){
            feedGameHolder.clearAll();
        }
    }*/

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
        if (feedGameHolder != null) {
            feedGameHolder.clearAll();
        }
        closePlayer();
    }

    /*public void onClick(View view){
        switch (view.getId()){
            case R.id.hunter_games_button:
        }
    }*/
/*
    public void openGame(View view){

    }*/

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
}
