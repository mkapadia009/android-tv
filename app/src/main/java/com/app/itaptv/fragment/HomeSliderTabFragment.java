package com.app.itaptv.fragment;

import static com.app.itaptv.structure.FeedCombinedData.GAME_TAB;
import static com.app.itaptv.structure.FeedCombinedData.NORMAL;
import static com.app.itaptv.structure.FeedCombinedData.PREMIUM_TAB;
import static com.app.itaptv.structure.FeedCombinedData.SUBSCRIPTION_TAB;
import static com.app.itaptv.structure.FeedContentData.POST_TYPE_ORIGINALS;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.app.itaptv.R;
import com.app.itaptv.activity.BrowserActivity;
import com.app.itaptv.activity.BuyDetailActivity;
import com.app.itaptv.activity.GameStartActivity;
import com.app.itaptv.activity.HomeActivity;
import com.app.itaptv.activity.LuckyWinnerActivity;
import com.app.itaptv.activity.PremiumActivity;
import com.app.itaptv.activity.WebViewActivity;
import com.app.itaptv.custom_interface.SliderListener;
import com.app.itaptv.custom_widget.DotsIndicatorDecoration;
import com.app.itaptv.holder.BigSliderHolder;
import com.app.itaptv.holder.SliderHolder;
import com.app.itaptv.structure.FeedCombinedData;
import com.app.itaptv.structure.FeedContentData;
import com.app.itaptv.structure.GameData;
import com.app.itaptv.structure.HomeSliderObject;
import com.app.itaptv.structure.SubscriptionDetails;
import com.app.itaptv.utils.AlertUtils;
import com.app.itaptv.utils.Analyticals;
import com.app.itaptv.utils.Constant;
import com.app.itaptv.utils.GameDateValidation;
import com.app.itaptv.utils.LocalStorage;
import com.app.itaptv.utils.Utility;
import com.kalpesh.krecycleradapter.KRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class HomeSliderTabFragment extends Fragment implements BigSliderHolder.EventListener {

    public static String TAB_TYPE = "tabType";
    public static String SLIDER_DATA = "sliderData";
    View view;
    RecyclerView rvContent;
    KRecyclerViewAdapter adapter;
    boolean flagDecoration = false;
    String sliderId;
    HomeActivity homeActivity;
    Activity activity;

    String tabType = "";
    String screenType = "";
    //private ArrayList<FeedCombinedData> arrayListPageWiseFeedData = new ArrayList<>();
    FeedCombinedData feedCombinedData;
    private ArrayList<HomeSliderObject> homeSliderObjectArrayList = new ArrayList<>();

    public static Fragment getInstance(String tabType, FeedCombinedData feedCombinedData) {
        Bundle bundle = new Bundle();
        bundle.putString(TAB_TYPE, tabType);
        bundle.putParcelable(SLIDER_DATA, feedCombinedData);
        HomeSliderTabFragment homeSliderTabFragment = new HomeSliderTabFragment();
        homeSliderTabFragment.setArguments(bundle);
        return homeSliderTabFragment;
    }

    public static Fragment getInstance() {
        return new HomeSliderTabFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tabType = getArguments().getString(TAB_TYPE);
            feedCombinedData = getArguments().getParcelable(SLIDER_DATA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home_slider_tab, container, false);
        init();
        return view;
    }

    private void init() {
        flagDecoration = false;
        rvContent = view.findViewById(R.id.rvContent);

        homeActivity = (HomeActivity) getActivity();
        activity = ((Activity) getContext());

        //callAPI();
        updateUI();
    }

    /**
     * Returns feeds data related to currently selected tab
     */
/*
    private void callAPI() {
        arrayListPageWiseFeedData.clear();
        homeSliderObjectArrayList.clear();

        try {
            Map<String, String> params = new HashMap<>();
            APIRequest apiRequest = new APIRequest(Url.GET_HOME_TABS + screenType + "&type=" + tabType, Request.Method.GET, params, null, getContext());
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    handleFeedResponse(response, error);
                }
            });
        } catch (Exception e) {

        }
    }
*/

    /**
     * This method handles feed response
     */
/*
    private void handleFeedResponse(@Nullable String response, @Nullable Exception error) {
        try {
            if (error != null) {

            } else {
                if (response != null) {
                    //Log.e("response", response);

                    JSONObject jsonObjectResponse = new JSONObject(response);
                    String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                    if (type.equalsIgnoreCase("error")) {
                        String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                    } else if (type.equalsIgnoreCase("ok")) {
                        JSONArray jsonArrayMsg = jsonObjectResponse.getJSONArray("msg");
                        new Thread(() -> {
                            try {
                                for (int j = 0; j < jsonArrayMsg.length(); j++) {
                                    FeedCombinedData feedCombinedData = new FeedCombinedData(jsonArrayMsg.getJSONObject(j), j);
                                    arrayListPageWiseFeedData.add(feedCombinedData);
                                    */
/*JSONArray jsonArrayContents = jsonObject.getJSONArray("contents");
                                    for (int i = 0; i < jsonArrayContents.length(); i++) {
                                        FeedContentData feedContentData=new FeedContentData(jsonArrayContents.getJSONObject(i));
                                        Log.i("",feedContentData.toString());
                                    }*//*

                                }
                                new Handler(Looper.getMainLooper()).post(() -> updateUI());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }).start();
                    }
                }
            }
        } catch (JSONException e) {

        }
    }
*/
    private void updateUI() {
        if (feedCombinedData != null) {
            switch (feedCombinedData.viewType) {
                case FeedCombinedData.VIEW_TYPE_SLIDER:
                    new Handler(Looper.getMainLooper()).post(() -> setFeedSliderRecyclerView(feedCombinedData));
                    ;
                    break;
                case FeedCombinedData.VIEW_TYPE_BIG_SLIDER:
                    new Handler(Looper.getMainLooper()).post(() -> setFeedBigSliderRecyclerView(feedCombinedData));
                    ;
                    break;
            }
        }
    }

    private void setFeedSliderRecyclerView(FeedCombinedData feedCombinedData) {
        try {
            LinearLayoutManager linearLayoutManager;
            sliderId = String.valueOf(feedCombinedData.id);
            homeSliderObjectArrayList = feedCombinedData.homeSliderObjectArrayList;
            if (homeSliderObjectArrayList.size() == 0) {
                rvContent.setVisibility(View.GONE);
                return;
            }
            Timer[] timer = {new Timer()}; // new timer
            boolean[] isScrolling = {true};
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

            });

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
                            try {
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
                        }
                    }
                }
            });
            rvContent.setAdapter(adapter);
            adapter.notifyDataSetChanged();

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

            try {
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


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setFeedBigSliderRecyclerView(FeedCombinedData feedCombinedData) {
        try {
            LinearLayoutManager linearLayoutManager;
            final BigSliderHolder[] bigSliderHolder = new BigSliderHolder[1];
            sliderId = String.valueOf(feedCombinedData.id);
            homeSliderObjectArrayList = feedCombinedData.homeSliderObjectArrayList;
            if (homeSliderObjectArrayList.size() == 0) {
                rvContent.setVisibility(View.GONE);
                return;
            }
            linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            rvContent.setLayoutManager(linearLayoutManager);
            Timer[] timer = {new Timer()}; // new timer
            boolean[] isScrolling = {true};
            adapter = new KRecyclerViewAdapter(getContext(), homeSliderObjectArrayList, (viewGroup, i) -> {
                View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_big_slider, viewGroup, false);
                bigSliderHolder[0] = new BigSliderHolder(layoutView, homeSliderObjectArrayList.size(), this, new SliderListener() {
                    @Override
                    public void stopScrolling() {
                        timer[0].cancel();
                        timer[0].purge();
                        isScrolling[0] = false;
                    }
                });
                return bigSliderHolder[0];
            }, (kRecyclerViewHolder, o, i) -> {
                if (o instanceof HomeSliderObject) {
                    HomeSliderObject homeSliderObject = (HomeSliderObject) o;
                    setActionOnClick(homeSliderObject);
                }

            });

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
                            try {
                                timer[0].schedule(new TimerTask() {
                                    @SuppressLint("NotifyDataSetChanged")
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
                                }, 10000, 10000);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
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
                        }
                    }
                }
            });
            rvContent.setAdapter(adapter);
            adapter.notifyDataSetChanged();

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

            try {
                timer[0].schedule(new TimerTask() {
                    @SuppressLint("NotifyDataSetChanged")
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
                }, 10000, 10000);

            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (
                Exception e) {
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
                    if (feedContentData.postType.equals(POST_TYPE_ORIGINALS)) {
                        Intent intent = new Intent(getActivity(), BuyDetailActivity.class);
                        intent.putExtra(BuyDetailActivity.CONTENT_DATA, feedContentData);
                        intent.putExtra(BuyDetailActivity.FROM_SLIDER, "sliderPageKey");
                        getActivity().startActivityForResult(intent, BuyDetailActivity.REQUEST_CODE);
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
                case FeedContentData.CONTENT_TYPE_ESPORTS:
                    homeActivity.getUserRegisteredTournamentInfo(feedContentData.postId);
                    break;
            }
        }
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

    public interface keyEvent {
        void setSelection();
    }
}