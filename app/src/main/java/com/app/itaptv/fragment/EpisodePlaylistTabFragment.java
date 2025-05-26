package com.app.itaptv.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.app.itaptv.API.APIManager;
import com.app.itaptv.API.APIMethods;
import com.app.itaptv.API.APIRequest;
import com.app.itaptv.API.APIResponse;
import com.app.itaptv.API.Url;
import com.app.itaptv.R;
import com.app.itaptv.activity.BrowserActivity;
import com.app.itaptv.activity.BuyDetailActivity;
import com.app.itaptv.activity.HomeActivity;
import com.app.itaptv.activity.PremiumActivity;
import com.app.itaptv.activity.SeasonsTvFragment;
import com.app.itaptv.activity.ViewMoreActivity;
import com.app.itaptv.custom_interface.NavigationMenuCallback;
import com.app.itaptv.holder.EpisodeHolder;
import com.app.itaptv.interfaces.KeyEventListener;
import com.app.itaptv.interfaces.NavigationStateListener;
import com.app.itaptv.structure.FeedContentData;
import com.app.itaptv.structure.PriceData;
import com.app.itaptv.structure.SubscriptionDetails;
import com.app.itaptv.tvControllers.EpisodeTvAdapter;
import com.app.itaptv.tv_fragment.ViewAllTvFragment;
import com.app.itaptv.utils.AlertUtils;
import com.app.itaptv.utils.Analyticals;
import com.app.itaptv.utils.Constant;
import com.app.itaptv.utils.EmptyStateManager;
import com.app.itaptv.utils.LocalStorage;
import com.app.itaptv.utils.Log;
import com.app.itaptv.utils.SpacingItemDecoration;
import com.app.itaptv.utils.Utility;
import com.kalpesh.krecycleradapter.Interface.KRecyclerItemClickListener;
import com.kalpesh.krecycleradapter.Interface.KRecyclerViewHolderCallBack;
import com.kalpesh.krecycleradapter.KRecyclerViewAdapter;
import com.kalpesh.krecycleradapter.KRecyclerViewHolder;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemCreator;
import com.paginate.recycler.LoadingListItemSpanLookup;
/*import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;*/

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.app.itaptv.activity.BuyDetailActivity.KEY_CONTENT_DATA;
import static com.app.itaptv.activity.BuyDetailActivity.KEY_EPISODE_ID;
import static com.app.itaptv.activity.BuyDetailActivity.PLAY_NOW;
import static com.app.itaptv.structure.FeedContentData.TAG;

/**
 * Created by poonam on 11/12/18.
 */

public class EpisodePlaylistTabFragment extends Fragment implements NavigationStateListener {
    public static String POST_TYPE = "postType";
    public static String SERIES_ID = "seriesId";
    public static String SEASON_ID = "seasonId";
    public static String EPISODE_ID = "episodeId";
    public static String PLAYLIST_ID = "playlistId";
    public static String ISWATCHED = "iswatched";
    public static String FROMSEASONTAB = "seasontab";

    View view;
    RecyclerView rvList;

    String contextType = "";
    String postType = "";
    String seriesId = "";
    String seasonId = "";
    String playlistId = "";
    String isWatched = "false";
    int selectedTabIndex;
    ArrayList<FeedContentData> arrayListFeedContent = new ArrayList<>();
    ArrayList<Integer> selectedDurationPosition = new ArrayList<>();
    ArrayList<Integer> selectedDurationPositionCoins = new ArrayList<>();

    EmptyStateManager emptyState;
    KRecyclerViewAdapter adapter;
    EpisodeTvAdapter episodeTvAdapter;
    public static LinearLayout buyNow;
    TextView tvRupees;
    TextView tvCoinsTotal;
    TextView tvDivider;
    TextView tvTeaserDescription;
    NestedScrollView nsvEpisodeFrag;
    ProgressBar progressBar;
    RelativeLayout llprogressbar;

    //payment
    LinearLayout paymentRoot;
    RelativeLayout rlPayTm, rlRazor, rlCoinsPay, rlSubscribe;
    TextView tvCoinsPayment;
    int priceDataPos = 0;
    int coinsDataPos = 1;

    // Pagination
    private static final int GRID_SPAN = 1;
    private boolean loading = false;
    private int page = 0;
    private Handler handler;
    private Paginate paginate;
    protected long networkDelay = 2000;
    protected boolean customLoadingListItem = false;
    int nextPageNo = 1;
    String feedUrl;
    NavigationMenuCallback navigationMenuCallback;
    int lastSelectedIndex = 0;
    RecyclerView.SmoothScroller smoothScroller;
    LinearLayoutManager linearLayoutManager;

    public static Fragment getInstance(String contextType, String postType, String Id, String seasonId, int selectedTabIndex, String isWatched) {
        Bundle bundle = new Bundle();
        bundle.putString(ViewMoreActivity.CONTEXT_TYPE, contextType);
        bundle.putString(POST_TYPE, postType);
        bundle.putString(SERIES_ID, Id);
        bundle.putString(SEASON_ID, seasonId);
        bundle.putString(PLAYLIST_ID, Id);
        bundle.putString(ISWATCHED, isWatched);
        bundle.putInt(ViewMoreActivity.SELECTED_TAB_INDEX, selectedTabIndex);
        EpisodePlaylistTabFragment episodePlaylistTabFragment = new EpisodePlaylistTabFragment();
        episodePlaylistTabFragment.setArguments(bundle);
        return episodePlaylistTabFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contextType = getArguments().getString(ViewMoreActivity.CONTEXT_TYPE);
        postType = getArguments().getString(POST_TYPE);
        switch (postType) {
            case FeedContentData.POST_TYPE_ORIGINALS:
            case FeedContentData.POST_TYPE_SEASON:
                seriesId = getArguments().getString(SERIES_ID);
                seasonId = getArguments().getString(SEASON_ID);
                isWatched = getArguments().getString(ISWATCHED);
                selectedTabIndex = getArguments().getInt(ViewMoreActivity.SELECTED_TAB_INDEX);
                break;
            case FeedContentData.POST_TYPE_PLAYLIST:
                playlistId = getArguments().getString(PLAYLIST_ID);
                isWatched = getArguments().getString(ISWATCHED);
                break;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_episode_playlist_tab, container, false);
        init();
        return view;
    }

    private void init() {
        nextPageNo = 1;
        rvList = view.findViewById(R.id.rvList);
        buyNow = view.findViewById(R.id.buyNow);
        tvRupees = view.findViewById(R.id.tvRupees);
        tvCoinsTotal = view.findViewById(R.id.tvCoinsTotal);
        tvDivider = view.findViewById(R.id.tvDivider);
        tvTeaserDescription = view.findViewById(R.id.tvTeaserDescription);
        nsvEpisodeFrag = view.findViewById(R.id.nsvEpisodeFrag);
        progressBar = view.findViewById(R.id.progressBar);
        llprogressbar = view.findViewById(R.id.llprogressbar);
        llprogressbar.setVisibility(View.VISIBLE);
        Utility.buttonFocusListener(buyNow);
        keyListener(buyNow);
        buyNow.requestFocus();

        rvList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvList.addItemDecoration(new SpacingItemDecoration(Constant.RV_HV_SPACING));
        //rvList.setNestedScrollingEnabled(false);
        setUpEmptyState();
        setRecyclerView();

        nsvEpisodeFrag.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                    Log.i(TAG, "BOTTOM SCROLL");
                    if (nextPageNo != 0) {
                        if (postType.equals(FeedContentData.POST_TYPE_ORIGINALS) || postType.equals(FeedContentData.POST_TYPE_SEASON)) {
                            getEpisodeListing();
                        } else if (postType.equals(FeedContentData.POST_TYPE_PLAYLIST)) {
                            getPlaylist();
                        }
                    }
                }
            }
        });

        paymentRoot = view.findViewById(R.id.llPayment);
        rlRazor = view.findViewById(R.id.rlRazorPay);
        rlPayTm = view.findViewById(R.id.rlPayTm);
        rlCoinsPay = view.findViewById(R.id.rlCoinsPay);
        rlSubscribe = view.findViewById(R.id.rlSubscribe);
        tvCoinsPayment = view.findViewById(R.id.tvCoinsPayment);

        rlPayTm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //handle paytm payment
                String productId = "";
                if (arrayListFeedContent != null) {
                    productId = arrayListFeedContent.get(selectedTabIndex).seriesId.concat(":").concat(seasonId);
                    Log.i("", productId);
                    if (getActivity() != null) {
                        if (getActivity() instanceof BuyDetailActivity) {
                            ((BuyDetailActivity) getActivity()).paytmPrepayment(productId, selectedDurationPosition.get(selectedTabIndex), FROMSEASONTAB);
                        }
                    }
                } else {
                    Utility.showError(getString(R.string.product_id_missing), getContext());
                }
            }
        });
        rlRazor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String productId = "";
                if (arrayListFeedContent != null) {
                    productId = arrayListFeedContent.get(selectedTabIndex).seriesId.concat(":").concat(seasonId);
                    Log.i("", productId);
                    if (getActivity() != null) {
                        if (getActivity() instanceof BuyDetailActivity) {
                            ((BuyDetailActivity) getActivity()).razorpayPrepayment(productId, selectedDurationPosition.get(selectedTabIndex), FROMSEASONTAB);
                        }
                    }
                } else {
                    Utility.showError(getString(R.string.product_id_missing), getContext());
                }
            }
        });

        rlCoinsPay.setOnClickListener(v -> {
            String productId = "";
            if (arrayListFeedContent != null) {
                productId = arrayListFeedContent.get(selectedTabIndex).seriesId.concat(":").concat(seasonId);
                Log.i("", productId);
                SeasonsTvFragment seasonsTvFragment = ((SeasonsTvFragment) EpisodePlaylistTabFragment.this.getParentFragment());
                if (seasonsTvFragment != null) {
                    seasonsTvFragment.coinspayPrepayment(productId, selectedDurationPositionCoins.get(selectedTabIndex), FROMSEASONTAB);
                }
               /* if (getActivity() != null) {
                    if (getActivity() instanceof BuyDetailActivity) {
                        ((BuyDetailActivity) getActivity()).coinspayPrepayment(productId, selectedDurationPositionCoins.get(selectedTabIndex), FROMSEASONTAB);
                    } *//*else if (getActivity() instanceof SeasonsTvActivity) {
                        ((SeasonsTvActivity) getActivity()).coinspayPrepayment(productId, selectedDurationPositionCoins.get(selectedTabIndex), FROMSEASONTAB);
                    }*//*
                }*/
            } else {
                Utility.showError(getString(R.string.product_id_missing), getContext());
            }
        });

        rlCoinsPay.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (i) {
                        case KeyEvent.KEYCODE_DPAD_UP:
                        case KeyEvent.KEYCODE_DPAD_RIGHT:
                            return true;
                        case KeyEvent.KEYCODE_DPAD_LEFT:
                            navigationMenuCallback.navMenuToggle(true);
                            if (paymentRoot.getVisibility() == View.VISIBLE) {
                                buyNow.setVisibility(View.VISIBLE);
                                paymentRoot.setVisibility(View.GONE);
                            }
                            break;
                    }
                }
                return false;
            }
        });

        rlSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent returnIntent = new Intent();
                returnIntent.putExtra(KEY_ACTION_TYPE, SUBSCRIBE);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();*/
                //startManageSubscriptions();
                startActivity(new Intent(getContext(), PremiumActivity.class).putExtra("title", getResources().getString(R.string.label_premium)));
            }
        });

        rlSubscribe.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (i) {
                        case KeyEvent.KEYCODE_DPAD_RIGHT:
                            return true;
                        case KeyEvent.KEYCODE_DPAD_DOWN:
                            if (!arrayListFeedContent.isEmpty() && episodeTvAdapter != null) {
                                arrayListFeedContent.get(0).isFocused = true;
                                episodeTvAdapter.notifyItemChanged(0);
                                episodeTvAdapter.setLastSelectedIndex(0);
                                if (paymentRoot.getVisibility() == View.VISIBLE) {
                                    buyNow.setVisibility(View.VISIBLE);
                                    paymentRoot.setVisibility(View.GONE);
                                }
                            }
                            break;
                        case KeyEvent.KEYCODE_DPAD_LEFT:
                            navigationMenuCallback.navMenuToggle(true);
                            if (paymentRoot.getVisibility() == View.VISIBLE) {
                                buyNow.setVisibility(View.VISIBLE);
                                paymentRoot.setVisibility(View.GONE);
                            }
                            break;
                    }
                }
                return false;
            }
        });

        configureBuyNow();
    }

    private void setBasicInfo() {
        if (getActivity() != null) {
            if (!arrayListFeedContent.isEmpty()) {
                for (int i = 0; i < arrayListFeedContent.size(); i++) {
                    tvTeaserDescription.setText(arrayListFeedContent.get(i).description);
                    if (arrayListFeedContent.get(i).arrayListPriceData.size() > 1) {
                        if (arrayListFeedContent.get(i).arrayListPriceData.get(0).costType.equalsIgnoreCase("rupees")) {
                            setPriceData(0);
                            priceDataPos = 0;
                            selectedDurationPosition.add(0);
                            tvRupees.setText(" ".concat(String.format(getActivity().getString(R.string.rs_offer_price), arrayListFeedContent.get(i).arrayListPriceData.get(0).finalCost)));

                            setCoinsData(1);
                            coinsDataPos = 1;
                            selectedDurationPositionCoins.add(1);
                            tvCoinsTotal.setText(arrayListFeedContent.get(i).arrayListPriceData.get(1).finalCost);
                        } else if (arrayListFeedContent.get(i).arrayListPriceData.get(0).costType.equalsIgnoreCase("coins")) {
                            setPriceData(1);
                            priceDataPos = 1;
                            selectedDurationPosition.add(1);
                            tvRupees.setText(" ".concat(String.format(getActivity().getString(R.string.rs_offer_price), arrayListFeedContent.get(i).arrayListPriceData.get(1).finalCost)));

                            setCoinsData(0);
                            coinsDataPos = 0;
                            selectedDurationPositionCoins.add(0);
                            tvCoinsTotal.setText(arrayListFeedContent.get(i).arrayListPriceData.get(0).finalCost);
                        }
                    } else if (arrayListFeedContent.get(i).arrayListPriceData.size() > 0) {
                        if (arrayListFeedContent.get(i).arrayListPriceData.get(0).costType.equalsIgnoreCase("rupees")) {
                            setPriceData(0);
                            priceDataPos = 0;
                            selectedDurationPosition.add(0);
                            tvRupees.setText(" ".concat(String.format(getActivity().getString(R.string.rs_offer_price), arrayListFeedContent.get(i).arrayListPriceData.get(0).finalCost)));
                        } else if (arrayListFeedContent.get(i).arrayListPriceData.get(0).costType.equalsIgnoreCase("coins")) {
                            setCoinsData(0);
                            coinsDataPos = 0;
                            selectedDurationPositionCoins.add(0);
                            tvCoinsTotal.setText(arrayListFeedContent.get(i).arrayListPriceData.get(0).finalCost);
                            tvRupees.setVisibility(View.GONE);
                            //tvDivider.setVisibility(View.GONE);
                        }
                    }

                    if (!arrayListFeedContent.get(i).arrayListPriceData.isEmpty()) {
                        for (int j = 0; j < arrayListFeedContent.size(); j++) {
                            if (arrayListFeedContent.size() > selectedTabIndex) {
                                if (arrayListFeedContent.get(selectedTabIndex).can_i_use) {
                                    buyNow.setVisibility(View.GONE);
                                } else {
                                    buyNow.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    } else {
                        buyNow.setVisibility(View.GONE);
                        tvTeaserDescription.setVisibility(View.GONE);
                    }
                }
            }

            if (getActivity() instanceof ViewMoreActivity) {
                buyNow.setVisibility(View.GONE);
            }
        }
    }

    private void setSubscribedInfo() {
        if (!arrayListFeedContent.isEmpty()) {
            for (int i = 0; i < arrayListFeedContent.size(); i++) {
                tvTeaserDescription.setText(arrayListFeedContent.get(i).description);
            }
        }
        buyNow.setVisibility(View.GONE);
    }

    private void setPriceData(int position) {
        //rlPayTm.setVisibility(View.VISIBLE);
        //rlRazor.setVisibility(View.VISIBLE);
    }

    private void setCoinsData(int position) {
        tvCoinsTotal.setVisibility(View.VISIBLE);
        tvDivider.setVisibility(View.VISIBLE);
        if (arrayListFeedContent != null) {
            for (int i = 0; i < arrayListFeedContent.size(); i++) {
                long walletBalance = 0;
                if (HomeActivity.toolbarWalletBalance != null) {
                    walletBalance = LocalStorage.getValue(LocalStorage.KEY_WALLET_BALANCE, 0, Long.class);
                }
                long coinsForEntry = Integer.parseInt(arrayListFeedContent.get(i).arrayListPriceData.get(position).finalCost);
                if (coinsForEntry <= walletBalance) {
                    if (coinsForEntry != 0) {
                        rlCoinsPay.setVisibility(View.VISIBLE);
                        tvCoinsPayment.setText(arrayListFeedContent.get(i).arrayListPriceData.get(position).finalCost + " " + getString(R.string.icoins));
                    } else {
                        rlCoinsPay.setVisibility(View.GONE);
                    }
                } else {
                    rlCoinsPay.setVisibility(View.GONE);
                }
            }
        }
    }

    private void setRecyclerView() {
        if (Utility.isTelevision()) {
            linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            smoothScroller =
                    new ViewAllTvFragment.CenterSmoothScroller(rvList.getContext());
            rvList.setLayoutManager(linearLayoutManager);
            episodeTvAdapter = new EpisodeTvAdapter(requireContext(), arrayListFeedContent, new KeyEventListener() {
                @Override
                public void onKeyEvent(int i, Object item, View view, boolean isFirstRow) {
                    if (isFirstRow) {
                        navigationMenuCallback.navMenuToggle(true);
                        if (paymentRoot.getVisibility() == View.VISIBLE) {
                            buyNow.setVisibility(View.VISIBLE);
                            paymentRoot.setVisibility(View.GONE);
                        }
                    } else {
                       /* arrayListFeedContent.get(lastSelectedIndex).isFocused = false;
                        episodeTvAdapter.notifyItemChanged(lastSelectedIndex);
                        arrayListFeedContent.get(i).isFocused = true;
                        episodeTvAdapter.notifyItemChanged(i);*/
                        lastSelectedIndex = i;
                        smoothScroller.setTargetPosition(i);
                        linearLayoutManager.startSmoothScroll(smoothScroller);
                    }
                    if (item instanceof FeedContentData) {
                        episodeTvAdapter.setCanAccess(false);
                        if (!contextType.equals("")) {
                            if (arrayListFeedContent.get(selectedTabIndex).can_i_use) {
                                playSelection(i);
                            } else {
                                SubscriptionDetails sd = LocalStorage.getUserSubscriptionDetails();
                                if (sd != null && sd.allow_rental != null) {
                                    if (sd.allow_rental.equalsIgnoreCase(Constant.YES)) {
                                        playSelection(i);
                                    } else {
                                        //Utility.showError(getString(R.string.please_buy), getContext());
                                        AlertUtils.showAlert(getString(R.string.label_alert), getString(R.string.please_buy), null, getContext(), new AlertUtils.AlertClickHandler() {
                                            @Override
                                            public void alertButtonAction(boolean isPositiveAction) {
                                                onStateChanged(true,"");
                                            }
                                        });
                                    }
                                }
                            }
                        } else {
                            if (arrayListFeedContent.get(selectedTabIndex).can_i_use) {
                                playNow(i);
                            } else {
                                SubscriptionDetails sd = LocalStorage.getUserSubscriptionDetails();
                                if (sd != null && sd.allow_rental != null) {
                                    if (sd.allow_rental.equalsIgnoreCase(Constant.YES)) {
                                        playNow(i);
                                    } else {
                                        //Utility.showError(getString(R.string.please_buy), getContext());
                                        AlertUtils.showAlert(getString(R.string.label_alert), getString(R.string.please_buy), null, getContext(), new AlertUtils.AlertClickHandler() {
                                            @Override
                                            public void alertButtonAction(boolean isPositiveAction) {
                                                onStateChanged(true,"");
                                            }
                                        });
                                    }
                                }
                            }
                        }
                    }
                }
            });
            rvList.setAdapter(episodeTvAdapter);
            episodeTvAdapter.notifyDataSetChanged();
        } else {
            adapter = new KRecyclerViewAdapter(getActivity(), arrayListFeedContent, new KRecyclerViewHolderCallBack() {
                @Override
                public KRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                    View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_episode, viewGroup, false);
                    return new EpisodeHolder(layoutView, navigationMenuCallback);
                }
            }, new KRecyclerItemClickListener() {
                @Override
                public void onRecyclerItemClicked(KRecyclerViewHolder kRecyclerViewHolder, Object o, int i) {

                    if (!contextType.equals("")) {
                        if (arrayListFeedContent.get(selectedTabIndex).can_i_use) {
                            playSelection(i);
                        } else {
                            SubscriptionDetails sd = LocalStorage.getUserSubscriptionDetails();
                            if (sd != null && sd.allow_rental != null) {
                                if (sd.allow_rental.equalsIgnoreCase(Constant.YES)) {
                                    playSelection(i);
                                } else {
                                    //Utility.showError(getString(R.string.please_buy), getContext());
                                    AlertUtils.showAlert(getString(R.string.label_alert), getString(R.string.please_buy), null, getContext(), new AlertUtils.AlertClickHandler() {
                                        @Override
                                        public void alertButtonAction(boolean isPositiveAction) {
                                            onStateChanged(true,"");
                                        }
                                    });
                                }
                            }
                        }
                    } else {
                        if (arrayListFeedContent.get(selectedTabIndex).can_i_use) {
                            playNow(i);
                        } else {
                            SubscriptionDetails sd = LocalStorage.getUserSubscriptionDetails();
                            if (sd != null && sd.allow_rental != null) {
                                if (sd.allow_rental.equalsIgnoreCase(Constant.YES)) {
                                    playNow(i);
                                } else {
                                    // Utility.showError(getString(R.string.please_buy), getContext());
                                    AlertUtils.showAlert(getString(R.string.label_alert), getString(R.string.please_buy), null, getContext(), new AlertUtils.AlertClickHandler() {
                                        @Override
                                        public void alertButtonAction(boolean isPositiveAction) {
                                            onStateChanged(true,"");
                                        }
                                    });
                                }
                            }
                        }
                    }

                }
            });
            rvList.setAdapter(adapter);
        }
        //initializePagination();
        callAPI();
    }

    private void callAPI() {
        arrayListFeedContent.clear();
        if (Utility.isTelevision()) {
            episodeTvAdapter.notifyDataSetChanged();
        } else {
            adapter.notifyDataSetChanged();
        }


        if (postType.equals(FeedContentData.POST_TYPE_ORIGINALS) || postType.equals(FeedContentData.POST_TYPE_SEASON)) {
            feedUrl = Url.GET_EPISODE_LISTING;
        } else if (postType.equals(FeedContentData.POST_TYPE_PLAYLIST)) {
            feedUrl = Url.GET_PLAYLIST_LISTING;
        }

        if (nextPageNo != 0) {
            if (postType.equals(FeedContentData.POST_TYPE_ORIGINALS) || postType.equals(FeedContentData.POST_TYPE_SEASON)) {
                getEpisodeListing();
            } else if (postType.equals(FeedContentData.POST_TYPE_PLAYLIST)) {
                getPlaylist();
            }
        }
    }

    private void playNow(int position) {
        Intent returnIntent = new Intent();
        arrayListFeedContent.get(selectedTabIndex).feedPosition = 0;
        returnIntent.putExtra(BuyDetailActivity.KEY_ACTION_TYPE, PLAY_NOW);
        returnIntent.putExtra(KEY_CONTENT_DATA, arrayListFeedContent.get(selectedTabIndex));
        returnIntent.putExtra(KEY_EPISODE_ID, arrayListFeedContent.get(position).episodeId);
        ((HomeActivity) getActivity()).playPremiumContent(arrayListFeedContent.get(selectedTabIndex), arrayListFeedContent.get(position).episodeId);
        //getActivity().setResult(Activity.RESULT_OK, returnIntent);
        //getActivity().finish();
    }


    /**
     * Initialization of Empty State
     */
    void setUpEmptyState() {
        emptyState = EmptyStateManager.setUpInFragment(view, (AppCompatActivity) getActivity(), new EmptyStateManager.ActionBtnCallBack() {
            @Override
            public void onBtnClick(String action) {
                if (action.equals(EmptyStateManager.ACTION_RETRY)) {
                    getEpisodeListing();
                }
            }
        });
    }


    /**
     * Returns episode list of specific season of specific series
     */
    private void getEpisodeListing() {
        try {
            Map<String, String> params = new HashMap<>();
            APIRequest apiRequest = new APIRequest(feedUrl
                    + "&series=" + seriesId + "&season=" + seasonId + "&page_no=" + nextPageNo,
                    Request.Method.GET, params, null, getContext());
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, new APIResponse() {

                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    progressBar.setVisibility(View.GONE);
                    llprogressbar.setVisibility(View.GONE);
                    handleEpisodeListResponse(response, error);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns playlist
     */
    private void getPlaylist() {
        try {
            Map<String, String> params = new HashMap<>();
            APIRequest apiRequest = new APIRequest(feedUrl
                    + "&ID=" + playlistId + "&page_no=" + nextPageNo,
                    Request.Method.GET, params, null, getContext());
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, new APIResponse() {

                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    progressBar.setVisibility(View.GONE);
                    llprogressbar.setVisibility(View.GONE);
                    handlePlaylistResponse(response, error);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void handleEpisodeListResponse(@Nullable String response, @Nullable Exception error) {
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
                            JSONArray jsonArrayEpisodes = jsonArrayMsg.getJSONArray("episodes");
                            String description = jsonArrayMsg.has("description") ? jsonArrayMsg.getString("description") : "";
                            nextPageNo = jsonArrayMsg.has("next_page") ? jsonArrayMsg.getInt("next_page") : 0;
                            new Thread(() -> {
                                try {
                                    for (int i = 0; i < jsonArrayEpisodes.length(); i++) {
                                        FeedContentData feedContentData = new FeedContentData(jsonArrayEpisodes.getJSONObject(i), Constant.TAB_BUY);
                                        if (jsonArrayMsg.has("pricing")) {
                                            JSONArray jsonArrayPricing = jsonArrayMsg.getJSONArray("pricing");
                                            for (int j = 0; j < jsonArrayPricing.length(); j++) {
                                                PriceData priceData = new PriceData(jsonArrayPricing.getJSONObject(j));
                                                feedContentData.arrayListPriceData.add(priceData);
                                            }
                                        }
                                        feedContentData.description = description;
                                        /*if (i == 0) {
                                            feedContentData.isFocused = true;
                                        }*/
                                        arrayListFeedContent.add(feedContentData);
                                    }
                                    new Handler(Looper.getMainLooper()).post(() -> updateUI());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }).start();
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


    private void handlePlaylistResponse(@Nullable String response, @Nullable Exception error) {
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
                            JSONArray jsonArrayContents = jsonArrayMsg.getJSONArray("contents");
                            nextPageNo = jsonArrayMsg.has("next_page") ? jsonArrayMsg.getInt("next_page") : 0;
                            JSONObject jsonObjectPlaylist = jsonArrayMsg.getJSONObject("playlist");
                            String playlistName = jsonObjectPlaylist.has("post_title") ? jsonObjectPlaylist.getString("post_title") : "";
                            String playlistImageUrl = jsonObjectPlaylist.has("imgUrl") ? jsonObjectPlaylist.getString("imgUrl") : "";

                            new Thread(() -> {
                                try {
                                    for (int i = 0; i < jsonArrayContents.length(); i++) {
                                        FeedContentData feedContentData = new FeedContentData(jsonArrayContents.getJSONObject(i), Constant.TAB_BUY);
                                        feedContentData.playlistImageUrl = playlistImageUrl;
                                        feedContentData.playlistName = playlistName;
                                        arrayListFeedContent.add(feedContentData);
                                    }
                                    new Handler(Looper.getMainLooper()).post(() -> updateUI());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }).start();


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
        if (Utility.isTelevision()) {
            episodeTvAdapter.notifyDataSetChanged();
        } else {
            adapter.notifyDataSetChanged();
        }
        loading = false;
        page++;
        SubscriptionDetails sd = LocalStorage.getUserSubscriptionDetails();
        if (sd != null && sd.allow_rental != null) {
            if (sd.allow_rental.equalsIgnoreCase(Constant.YES)) {
                setSubscribedInfo();
            } else {
                setBasicInfo();
            }
        }
    }


    /**
     * Update of Empty State
     */
    private void updateEmptyState(String error) {
        if (error == null) {
            if (arrayListFeedContent.isEmpty()) {
                emptyState.setImgAndMsg(getString(R.string.no_data_found), null);
                rvList.setVisibility(View.INVISIBLE);
            } else {
                rvList.setVisibility(View.VISIBLE);
                emptyState.hide();
            }
        } else {
            rvList.setVisibility(View.INVISIBLE);
            if (Utility.isConnectingToInternet(getContext())) {
                emptyState.setMessage(APIManager.GENERIC_API_ERROR_MESSAGE);
            } else {
                emptyState.showNoConnectionState();
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


    /************************ PAGINATION METHODS -- START **********************/

    public void initializePagination() {
        arrayListFeedContent.clear();
        if (Utility.isTelevision()) {
            episodeTvAdapter.notifyDataSetChanged();
        } else {
            adapter.notifyDataSetChanged();
        }


        if (postType.equals(FeedContentData.POST_TYPE_ORIGINALS) || postType.equals(FeedContentData.POST_TYPE_SEASON)) {
            feedUrl = Url.GET_EPISODE_LISTING;
        } else if (postType.equals(FeedContentData.POST_TYPE_PLAYLIST)) {
            feedUrl = Url.GET_PLAYLIST_LISTING;
        }


        customLoadingListItem = false;

        if (paginate != null) {
            paginate.unbind();
        }
        loading = false;
        page = 0;
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
                feedUrl = feedUrl;

            }
            if (nextPageNo != 0) {
                if (postType.equals(FeedContentData.POST_TYPE_ORIGINALS) || postType.equals(FeedContentData.POST_TYPE_SEASON)) {
                    getEpisodeListing();
                } else if (postType.equals(FeedContentData.POST_TYPE_PLAYLIST)) {
                    getPlaylist();
                }
            }

        }
    };

    @Override
    public void onStateChanged(boolean expanded, @Nullable String lastSelected) {
        //Toast.makeText(getContext(), "Episode Focused", Toast.LENGTH_SHORT).show();
        if (expanded) {
            if (!arrayListFeedContent.isEmpty()) {
                arrayListFeedContent.get(lastSelectedIndex).isFocused = true;
                if (Utility.isTelevision()) {
                    episodeTvAdapter.notifyItemChanged(lastSelectedIndex);
                    episodeTvAdapter.setCanAccess(true);
                } else {
                    adapter.notifyItemChanged(lastSelectedIndex);
                }
            }
        }
    }

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


    private void playSelection(int position) {
        Intent intent = getActivity().getIntent();
        intent.putExtra(ViewMoreActivity.CONTEXT_TYPE, contextType);
        intent.putExtra(HomeActivity.POSITION, position);
        intent.putParcelableArrayListExtra(ViewMoreActivity.ARRAY_FEED_CONTENTS, arrayListFeedContent);
        //intent.putExtra(ViewMoreActivity.ARRAY_FEED_CONTENTS,arrayListFeedContent);

        switch (contextType) {
            case Analyticals.CONTEXT_SERIES:
            case Analyticals.CONTEXT_EPISODE:
                String episodeId = arrayListFeedContent.get(position).postId;
                String seasonName = arrayListFeedContent.get(position).seasonName;
                intent.putExtra(SERIES_ID, seriesId);
                intent.putExtra(SEASON_ID, seasonId);
                intent.putExtra(EPISODE_ID, episodeId);
                intent.putExtra(ViewMoreActivity.SEASON_NAME, seasonName);
                intent.putExtra(ViewMoreActivity.SELECTED_TAB_INDEX, selectedTabIndex);
                intent.putExtra(ViewMoreActivity.ISWATCHED, isWatched);
                break;
            case Analyticals.CONTEXT_PLAYLIST:
                intent.putExtra(PLAYLIST_ID, playlistId);
                intent.putExtra(ViewMoreActivity.ISWATCHED, isWatched);
                break;
        }

        getActivity().setResult(getActivity().RESULT_OK, intent);
        // getActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //APIManager.cancelAllRequests(APIManager.PAGE_NAVIGATION);
    }

    private void configureBuyNow() {
        buyNow.setOnClickListener(v -> {
            /*if (rlPayTm.getVisibility() == View.GONE && rlRazor.getVisibility() == View.GONE && rlCoinsPay.getVisibility() == View.GONE) {
                Utility.showError("You don't have sufficient balance", getContext());
            } else {*/
            long walletBalance = 0;
            if (HomeActivity.toolbarWalletBalance != null) {
                walletBalance = LocalStorage.getValue(LocalStorage.KEY_WALLET_BALANCE, 0, Long.class);
            }
            if (rlCoinsPay.getVisibility() == View.VISIBLE) {
                long coinsForEntry = Integer.parseInt(arrayListFeedContent.get(selectedTabIndex).arrayListPriceData.get(coinsDataPos).finalCost);
                if (coinsForEntry <= walletBalance) {
                    buyNow.setVisibility(View.GONE);
                    paymentRoot.setVisibility(View.VISIBLE);
                    rlCoinsPay.requestFocus();
                } else {
                    startActivity(new Intent(getContext(), PremiumActivity.class).putExtra("title", getResources().getString(R.string.label_premium)));
                }
            } else {
                startActivity(new Intent(getContext(), PremiumActivity.class).putExtra("title", getResources().getString(R.string.label_premium)));
            }
            // }
        });
    }

    public void refreshTab() {
        // Reload current fragment
       /* if( getFragmentManager() != null){
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        if (Build.VERSION.SDK_INT >= 26) {
            ft.setReorderingAllowed(false);
        }
        ft.detach(this).attach(this).commit();
        }*/
        if (getActivity() != null) {
            if (getActivity().getSupportFragmentManager() != null) {
                getActivity().getSupportFragmentManager().beginTransaction().detach(EpisodePlaylistTabFragment.this).commit();
                getActivity().getSupportFragmentManager().beginTransaction().attach(EpisodePlaylistTabFragment.this).commit();
            }
        }
    }

    private void startManageSubscriptions() {
        String userId = LocalStorage.getUserId();
        try {
            Map<String, String> params = new HashMap<>();
            params.put("user_id", userId);
            APIRequest apiRequest = new APIRequest(Url.FETCH_SUBSCRIPTIONS_URL, Request.Method.POST, params, null, getContext());
            apiRequest.showLoader = true;
            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    if (error != null) {
                        //showError(error.getMessage());
                    } else {
                        try {
                            if (response != null) {
                                Log.e("response", response);
                                JSONObject jsonObjectResponse = new JSONObject(response);
                                String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                                if (type.equalsIgnoreCase("error")) {
                                    String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                                    //AlertUtils.showToast(message, 1, getActivity());
                                } else if (type.equalsIgnoreCase("ok")) {
                                    JSONObject jsonObjMsg = jsonObjectResponse.getJSONObject("msg");
                                    String subscriptionsUrl = jsonObjMsg.getString("url");
                                    //Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(subscriptionsUrl));
                                    //startActivity(browserIntent);
                                    startActivityForResult(new Intent(getContext(), BrowserActivity.class).putExtra("title", "Premium").putExtra("posturl", subscriptionsUrl), BuyDetailActivity.REQUEST_CODE_PREMIUM);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (Exception e) {

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BuyDetailActivity.REQUEST_CODE_PREMIUM) {
            if (data != null) {
                boolean status = data.getBooleanExtra("status", false);
                if (status) {
                    APIMethods.getUserDetails(getContext());
                    ((BuyDetailActivity) getActivity()).refreshScreen();
                }
            }
        }
    }

    public void setNavigationMenuCallback(NavigationMenuCallback callback) {
        this.navigationMenuCallback = callback;
    }

    public void keyListener(View view) {
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (i) {
                        case KeyEvent.KEYCODE_DPAD_UP:
                        case KeyEvent.KEYCODE_DPAD_RIGHT:
                            return true;
                        case KeyEvent.KEYCODE_DPAD_DOWN:
                            if (!arrayListFeedContent.isEmpty() && episodeTvAdapter != null) {
                                arrayListFeedContent.get(0).isFocused = true;
                                episodeTvAdapter.notifyItemChanged(0);
                                episodeTvAdapter.setLastSelectedIndex(0);
                                if (paymentRoot.getVisibility() == View.VISIBLE) {
                                    buyNow.setVisibility(View.VISIBLE);
                                    paymentRoot.setVisibility(View.GONE);
                                }
                            }
                            break;
                        case KeyEvent.KEYCODE_DPAD_LEFT:
                            navigationMenuCallback.navMenuToggle(true);
                            if (paymentRoot.getVisibility() == View.VISIBLE) {
                                buyNow.setVisibility(View.VISIBLE);
                                paymentRoot.setVisibility(View.GONE);
                            }
                            break;
                    }
                }
                return false;
            }
        });
    }

    public static void restoreFocus() {
        ((Activity) buyNow.getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (buyNow != null && buyNow.getVisibility() == View.VISIBLE) {
                            buyNow.requestFocus();
                        }
                    }
                }, 10);
            }
        });
    }
}
