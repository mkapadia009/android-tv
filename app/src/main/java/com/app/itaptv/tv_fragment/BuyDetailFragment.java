package com.app.itaptv.tv_fragment;

import static com.app.itaptv.activity.BaseActivity.TAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.app.itaptv.API.APIManager;
import com.app.itaptv.API.APIRequest;
import com.app.itaptv.API.APIResponse;
import com.app.itaptv.API.Url;
import com.app.itaptv.R;
import com.app.itaptv.activity.BrowserActivity;
import com.app.itaptv.activity.DurationActivity;
import com.app.itaptv.activity.HomeActivity;
import com.app.itaptv.activity.PremiumActivity;
import com.app.itaptv.activity.PurchasesActivity;
import com.app.itaptv.activity.SeasonsTvFragment;
import com.app.itaptv.activity.TeaserActivity;
import com.app.itaptv.adapter.ViewPagerAdapter;
import com.app.itaptv.fragment.EpisodePlaylistTabFragment;
import com.app.itaptv.structure.FeedContentData;
import com.app.itaptv.structure.PriceData;
import com.app.itaptv.structure.SeasonData;
import com.app.itaptv.structure.SubscriptionDetails;
import com.app.itaptv.structure.User;
import com.app.itaptv.utils.AlertUtils;
import com.app.itaptv.utils.Constant;
import com.app.itaptv.utils.LocalStorage;
import com.app.itaptv.utils.Log;
import com.app.itaptv.utils.Utility;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.material.tabs.TabLayout;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.razorpay.Checkout;
import com.razorpay.ExternalWalletListener;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BuyDetailFragment extends Fragment implements TabLayout.BaseOnTabSelectedListener, PaymentResultListener, ExternalWalletListener {
    public static String CONTENT_DATA = "contentData";
    public static String FROM_SLIDER = "fromSlider";
    public static int REQUEST_CODE = 3;
    public static String KEY_ACTION_TYPE = "actionType";
    public static String KEY_CONTENT_DATA = "contentData";
    public static final String VIEW_PURCHASES = "viewPurchases";
    public static final String PLAY_NOW = "playNow";
    public static final String REQUEST_CODE_EXTRA = "requestCodeExtra";
    public static final String KEY_EPISODE_ID = "episodeId";
    public static final String SUBSCRIBE = "subscribe";


    private static int selectedDurationPosition = 0;
    private static int selectedDurationPositionCoins = 1;

    private static final int MY_PERMISSIONS_REQUEST_SMS = 100;

    View rootView;
    View viewContentLoader;
    TextView tvRupees;
    LinearLayout buyNow;
    LinearLayout llSeasons;
    NestedScrollView nsvBuydetail;
    Button playNow;
    TextView tvCoinsTotal;
    TextView tvDivider;
    RelativeLayout rlParent;
    ImageView ivTeaserImage, ivFeedImage, ivPlayBtn;
    TextView tvTitle;
    TextView tvSeasonCount;
    TextView tvEpisodeCount;
    TextView tvDuration;
    TextView duration;
    TextView tvMrpPrice;
    //TextView tvOr;
    ImageView ivCoins;
    //TextView tvSellingCoins;
    //TextView tvOfferPrice;
    TextView tvPercentOff;
    TextView tvCoins;
    TextView tvTeaserDescription;
    TextView tvSaving;
    TabLayout tlSeason;
    ViewPager vpSeason;
    LinearLayout llCoins, llTeaser;

    FeedContentData feedContentData;
    PriceData priceData;
    String postId = "";
    String postType = "";
    long walletCoins = 0L;
    ArrayList<PriceData> arrayListPriceData = new ArrayList<>();
    ArrayList<SeasonData> arrayListSeasonData = new ArrayList<>();
    ArrayList<String> purchasedList = new ArrayList<>();
    ArrayList<EpisodePlaylistTabFragment> ftList = new ArrayList<>();
    int tabPosition;

    AlertDialog alertDialog;

    public static int REQUEST_CODE_BUY = 478;
    public static int REQUEST_CODE_WATCH = 480;
    public static int REQUEST_CODE_PREMIUM = 494;
    public static String KEY_PRODUCT_ID = "productId";
    public static String KEY_BUY = "buyClicked";
    public static String KEY_WATCH = "watchClicked";

    User user;
    String screenType;
    boolean isRefreshRequired = false;

    //payment
    LinearLayout paymentRoot;
    RelativeLayout rlPayTm, rlRazor, rlCoinsPay, rlSubscribe;
    TextView tvCoinsPayment;
    int priceDataPos = 0;
    int coinsDataPos = 1;

    public static Fragment getInstance(FeedContentData feedContentData, String fromSlider) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(CONTENT_DATA, feedContentData);
        bundle.putString(FROM_SLIDER, fromSlider);
        BuyDetailFragment buyDetailFragment = new BuyDetailFragment();
        buyDetailFragment.setArguments(bundle);
        return buyDetailFragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            feedContentData = (FeedContentData) getArguments().getParcelable(CONTENT_DATA);
            if (feedContentData != null) {
                postId = feedContentData.postId;
                postType = feedContentData.postType;
                arrayListPriceData.clear();
                arrayListPriceData.addAll(feedContentData.arrayListPriceData);
                /*if (getArguments().getString(FROM_SLIDER)) {
                    if (feedContentData.isTrailer) {
                        // play trailer
                        //playTeaser();
                    } else {

                    }
                }*/
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_buy_detail, container, false);
        initialize();
        return rootView;
    }

    void initialize() {
        rlParent = rootView.findViewById(R.id.rlParent);

        setPageLayout();

     /*   if (feedContentData != null) {
            if (feedContentData.isTrailer) {
                // play trailer
                HomeActivity.getInstance().pausePlayer();
                startActivity(new Intent(this, TeaserActivity.class).putExtra(CONTENT_DATA, feedContentData));
            }
        }*/
        isRefreshRequired = false;
    }

    /**
     * Set layout depend on whether it is audio post or it contains Tabs (Either episode or playlist)
     */
    private void setPageLayout() {
        if (feedContentData != null) {
            if (Utility.isTelevision()) {
                switch (feedContentData.postType) {
                    case FeedContentData.POST_TYPE_POST:
                    case FeedContentData.POST_TYPE_ORIGINALS:
                    case FeedContentData.POST_TYPE_PLAYLIST:
                    case FeedContentData.POST_TYPE_SEASON:
                    case FeedContentData.POST_TYPE_EPISODE:
                        setTvPostLayout();
                        break;
                }
            } else {
                switch (feedContentData.postType) {
                    case FeedContentData.POST_TYPE_POST:
                        setAudioPostLayout();
                        break;
                    case FeedContentData.POST_TYPE_ORIGINALS:
                    case FeedContentData.POST_TYPE_PLAYLIST:
                    case FeedContentData.POST_TYPE_SEASON:
                        setTabListLayout();
                        break;
                }
            }
        }
    }

    private void setAudioPostLayout() {
        View viewContent = View.inflate(getContext(), R.layout.layout_buy_detail_without_coord, null);
        viewContentLoader = View.inflate(getContext(), R.layout.loader, null);
        viewContentLoader.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        viewContentLoader.setVisibility(View.GONE);
        rlParent.addView(viewContent);
        rlParent.addView(viewContentLoader);
        init();

    }

    private void setTabListLayout() {
        View viewContent = View.inflate(getContext(), R.layout.layout_buy_detail_with_coord, null);
        viewContentLoader = View.inflate(getContext(), R.layout.loader, null);
        viewContentLoader.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        viewContent.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        viewContentLoader.setVisibility(View.GONE);
        rlParent.addView(viewContent);
        rlParent.addView(viewContentLoader);
        tlSeason = rootView.findViewById(R.id.tlSeason);
        vpSeason = rootView.findViewById(R.id.vpSeason);
        init();
    }

    private void setTvPostLayout() {
        View viewContent = View.inflate(getContext(), R.layout.layout_buy_detail_tv, null);
        viewContentLoader = View.inflate(getContext(), R.layout.loader, null);
        viewContentLoader.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        viewContentLoader.setVisibility(View.GONE);
        rlParent.addView(viewContent);
        rlParent.addView(viewContentLoader);
        llSeasons = rootView.findViewById(R.id.llSeasons);
        nsvBuydetail = rootView.findViewById(R.id.nsvBuydetail);
        Utility.buttonFocusListener(llSeasons);
        llSeasons.setVisibility(View.VISIBLE);
        ivPlayBtn = rootView.findViewById(R.id.ivPlay);
        llTeaser = rootView.findViewById(R.id.llTeaser);
        ivPlayBtn.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (view.hasFocus()) {
                    llTeaser.setBackground(getContext().getDrawable(R.drawable.highlight_text_field));
                    nsvBuydetail.scrollTo(0, nsvBuydetail.getTop());
                } else {
                    llTeaser.setBackground(null);
                    nsvBuydetail.scrollTo(0, nsvBuydetail.getBottom());
                }
            }
        });
       /* new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ivPlayBtn.requestFocus();
            }
        }, 10);
*/

        ivPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playTeaserTv(view);
            }
        });

        if (feedContentData.getTeaserUrl() == null || feedContentData.getTeaserUrl().isEmpty() || feedContentData.getTeaserUrl().equals("false")) {
            ivPlayBtn.setVisibility(View.GONE);
            llTeaser.setClickable(false);
        } else {
            ivPlayBtn.requestFocus();
        }

        llSeasons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String seriesId = String.valueOf(feedContentData.seriesId);
             /*   startActivityForResult(new Intent(getContext(), SeasonsTvActivity.class).putExtra(ViewMoreActivity.CONTEXT_TYPE, "").putExtra(SeasonsTvActivity.Companion.getPOST_TYPE(), postType)
                        .putExtra(SeasonsTvActivity.Companion.getSERIES_ID(), seriesId).putParcelableArrayListExtra(SeasonsTvActivity.Companion.getSEASON_ID(), feedContentData.arrayListSeasonData).putExtra(SeasonsTvActivity.Companion.getISWATCHED(), String.valueOf(feedContentData.iswatchlisted)), 101);*/
                ((HomeActivity) getActivity()).openFragment(SeasonsTvFragment.Companion.getInstance("", postType, seriesId, feedContentData.arrayListSeasonData, String.valueOf(feedContentData.iswatchlisted)));
            }
        });

        llSeasons.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (i) {
                        case KeyEvent.KEYCODE_DPAD_UP:
                            if (buyNow != null && buyNow.getVisibility() == View.GONE) {
                                ivPlayBtn.requestFocus();
                            } else if (buyNow != null && buyNow.getVisibility() == View.VISIBLE) {
                                buyNow.requestFocus();
                            }
                        case KeyEvent.KEYCODE_DPAD_RIGHT:
                        case KeyEvent.KEYCODE_DPAD_DOWN:
                        case KeyEvent.KEYCODE_DPAD_LEFT:
                            return true;
                    }
                }

                return false;
            }
        });

        init();

    }

    private void init() {

        if (!Utility.isTelevision()) {
            ((HomeActivity) getActivity()).setToolbar(false);
            ((HomeActivity) getActivity()).showToolbarBackButton(R.drawable.back_arrow_white);
            ((HomeActivity) getActivity()).showToolbarTitle(false);
        }

        buyNow = rootView.findViewById(R.id.buyNow);
        playNow = rootView.findViewById(R.id.btPlayNow);
        tvRupees = rootView.findViewById(R.id.tvRupees);
        tvCoinsTotal = rootView.findViewById(R.id.tvCoinsTotal);
        tvDivider = rootView.findViewById(R.id.tvDivider);
        ivTeaserImage = rootView.findViewById(R.id.ivTeaserImage);
        ivFeedImage = rootView.findViewById(R.id.ivFeedImage);
        tvTitle = rootView.findViewById(R.id.tvStoreName);
        tvSeasonCount = rootView.findViewById(R.id.tvSeasonCount);
        tvEpisodeCount = rootView.findViewById(R.id.tvEpisodeCount);
        tvDuration = rootView.findViewById(R.id.tvDuration);
        duration = rootView.findViewById(R.id.duration);
        tvMrpPrice = rootView.findViewById(R.id.tvMrpPrice);
        //tvOr = findViewById(R.id.tvOr);
        //tvSellingCoins = findViewById(R.id.tvSellingCoins);
        //tvOfferPrice = findViewById(R.id.tvOfferPrice);
        tvPercentOff = rootView.findViewById(R.id.tvPercentOff);
        tvCoins = rootView.findViewById(R.id.tvCoins);
        tvTeaserDescription = rootView.findViewById(R.id.tvTeaserDescription);
        tvSaving = rootView.findViewById(R.id.tvSaving);
        llCoins = rootView.findViewById(R.id.llCoins);
        //llOfferPrice = findViewById(R.id.llOfferPrice);
        Utility.buttonFocusListener(buyNow);

        user = LocalStorage.getUserData();

        paymentRoot = rootView.findViewById(R.id.llPayment);
        rlRazor = rootView.findViewById(R.id.rlRazorPay);
        rlPayTm = rootView.findViewById(R.id.rlPayTm);
        rlCoinsPay = rootView.findViewById(R.id.rlCoinsPay);
        tvCoinsPayment = rootView.findViewById(R.id.tvCoinsPayment);
        rlSubscribe = rootView.findViewById(R.id.rlSubscribe);

        buyNow.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (i) {
                        case KeyEvent.KEYCODE_DPAD_DOWN:
                            nsvBuydetail.scrollTo(0, nsvBuydetail.getBottom());
                            if (llSeasons != null && llSeasons.getVisibility() == View.GONE) {
                                return true;
                            }
                    }
                }
                return false;
            }
        });

        rlPayTm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //handle paytm payment
                String productId = "";
                if (feedContentData != null) {
                    productId = feedContentData.postId;
                    paytmPrepayment(productId, selectedDurationPosition, "");
                } else {
                    Utility.showError(getString(R.string.product_id_missing), getContext());
                }
            }
        });
        rlRazor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callOrderAPI();
            }
        });

        rlCoinsPay.setOnClickListener(v -> {
            callOrderByCoinsAPI();
        });

        rlCoinsPay.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (i) {
                        case KeyEvent.KEYCODE_DPAD_UP:
                            if (paymentRoot.getVisibility() == View.VISIBLE && ivPlayBtn.getVisibility() == View.VISIBLE) {
                                buyNow.setVisibility(View.VISIBLE);
                                paymentRoot.setVisibility(View.GONE);
                                ivPlayBtn.requestFocus();
                            }
                            break;
                        case KeyEvent.KEYCODE_DPAD_RIGHT:
                        case KeyEvent.KEYCODE_DPAD_LEFT:
                            return true;
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
                // startManageSubscriptions();
                startActivityForResult(new Intent(getContext(), PremiumActivity.class).putExtra("title", getResources().getString(R.string.label_premium)), REQUEST_CODE);
            }
        });

        rlSubscribe.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (i) {
                        case KeyEvent.KEYCODE_DPAD_RIGHT:
                        case KeyEvent.KEYCODE_DPAD_LEFT:
                            return true;
                        case KeyEvent.KEYCODE_DPAD_DOWN:
                            nsvBuydetail.scrollTo(0, nsvBuydetail.getBottom());
                            if (paymentRoot.getVisibility() == View.VISIBLE) {
                                buyNow.setVisibility(View.VISIBLE);
                                paymentRoot.setVisibility(View.GONE);
                            }
                            if (llSeasons != null && llSeasons.getVisibility() == View.VISIBLE) {
                                llSeasons.post(new Runnable() {
                                    public void run() {
                                        llSeasons.requestFocus();
                                    }
                                });
                            }
                            break;
                    }
                }
                return false;
            }
        });


        playNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //finish();
                HomeActivity.getInstance().playMedia(feedContentData, "0");
            }
        });

        purchasedList = LocalStorage.getPurchasedArrayList("savedPurchased", getContext());

        setBasicInfo();
        configureBuyNow();
        configureBuyNowCoins();
        getPostData();
    }

    private void setBasicInfo() {
        if (feedContentData != null) {
            String episodeCount;
            // Check post type
            switch (feedContentData.postType) {
                case FeedContentData.POST_TYPE_POST:
                case FeedContentData.POST_TYPE_EPISODE:
                    tvSeasonCount.setVisibility(View.GONE);
                    tvEpisodeCount.setVisibility(View.GONE);
                    if (llSeasons != null) {
                        llSeasons.setVisibility(View.GONE);
                    }
                    break;
                case FeedContentData.POST_TYPE_ORIGINALS:
                    String seasonCount = String.format(getString(R.string.season_count), feedContentData.seasonCount);
                    episodeCount = String.format(getString(R.string.episode_count), feedContentData.totalEpisodes);
                    if (!feedContentData.customText.isEmpty()) {
                        tvSeasonCount.setText(feedContentData.customText);
                        tvEpisodeCount.setVisibility(View.GONE);
                    } else {
                        tvSeasonCount.setText(seasonCount);
                        tvEpisodeCount.setText(episodeCount);
                    }
                    if (!Utility.isTelevision()) {
                        setSeasonData();
                    }
                    break;
                case FeedContentData.POST_TYPE_SEASON:
                    String seasonName = feedContentData.name;
                    episodeCount = String.format(getString(R.string.episode_count), feedContentData.totalEpisodes);
                    if (!feedContentData.customText.isEmpty()) {
                        tvSeasonCount.setText(feedContentData.customText);
                        tvEpisodeCount.setVisibility(View.GONE);
                    } else {
                        tvSeasonCount.setText(seasonName);
                        tvEpisodeCount.setText(episodeCount);
                    }
                    if (!Utility.isTelevision()) {
                        setSeasonData();
                    }
                    break;
                case FeedContentData.POST_TYPE_PLAYLIST:
                    String playlistCount = String.format(getString(R.string.song_count), feedContentData.totalItems);
                    tvSeasonCount.setText(playlistCount);
                    tvEpisodeCount.setVisibility(View.GONE);
                    if (!Utility.isTelevision()) {
                        setPlaylistData();
                    }
                    if (llSeasons != null) {
                        llSeasons.setVisibility(View.GONE);
                    }
                    break;
            }

            Glide.with(this).load(feedContentData.teaserImage).diskCacheStrategy(DiskCacheStrategy.ALL).transition(DrawableTransitionOptions.withCrossFade()).into(ivTeaserImage);

            ivFeedImage.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.full_transparent));
            Glide.with(this).load(feedContentData.imgUrl).diskCacheStrategy(DiskCacheStrategy.ALL).transition(DrawableTransitionOptions.withCrossFade()).into(ivFeedImage);
            tvTitle.setText(feedContentData.postTitle);

            tvTeaserDescription.setText(feedContentData.teaserDescription);

            if (feedContentData.arrayListPriceData.size() > 1) {
                if (feedContentData.arrayListPriceData.get(0).costType.equalsIgnoreCase("rupees")) {
                    setPriceData(0);
                    priceDataPos = 0;
                    selectedDurationPosition = 0;
                    tvRupees.setText(" ".concat(String.format(getString(R.string.rs_offer_price), feedContentData.arrayListPriceData.get(0).finalCost)));

                    setCoinsData(1);
                    coinsDataPos = 1;
                    selectedDurationPositionCoins = 1;
                    tvCoinsTotal.setText(feedContentData.arrayListPriceData.get(1).finalCost);

                } else if (feedContentData.arrayListPriceData.get(0).costType.equalsIgnoreCase("coins")) {
                    setPriceData(1);
                    priceDataPos = 1;
                    selectedDurationPosition = 1;
                    tvRupees.setText(" ".concat(String.format(getString(R.string.rs_offer_price), feedContentData.arrayListPriceData.get(1).finalCost)));

                    setCoinsData(0);
                    coinsDataPos = 0;
                    selectedDurationPositionCoins = 0;
                    tvCoinsTotal.setText(feedContentData.arrayListPriceData.get(0).finalCost);
                }

            } else if (feedContentData.arrayListPriceData.size() > 0) {
                if (feedContentData.arrayListPriceData.get(0).costType.equalsIgnoreCase("rupees")) {
                    setPriceData(0);
                    priceDataPos = 0;
                    selectedDurationPosition = 0;
                    tvRupees.setText(" ".concat(String.format(getString(R.string.rs_offer_price), feedContentData.arrayListPriceData.get(0).finalCost)));
                } else if (feedContentData.arrayListPriceData.get(0).costType.equalsIgnoreCase("coins")) {
                    setCoinsData(0);
                    coinsDataPos = 0;
                    selectedDurationPositionCoins = 0;
                    tvCoinsTotal.setText(feedContentData.arrayListPriceData.get(0).finalCost);
                    tvRupees.setVisibility(View.GONE);
                    //tvDivider.setVisibility(View.GONE);
                    //tvOr.setVisibility(View.GONE);
                }
            }
        }

    }

    private void setPriceData(int position) {
        if (feedContentData.arrayListPriceData != null) {
            //rlPayTm.setVisibility(View.VISIBLE);
            //rlRazor.setVisibility(View.VISIBLE);
            selectedDurationPosition = position;
            PriceData priceData = arrayListPriceData.get(position);
            priceData = feedContentData.arrayListPriceData.get(position);

            String mrpPrice = priceData.finalCost.equals("") ? "0" : priceData.finalCost;
            mrpPrice = String.format(getString(R.string.mrp_price), mrpPrice);

            String offerPrice = priceData.rupees.equals("") ? "0" : priceData.rupees;
            offerPrice = String.format(getString(R.string.rs_offer_price), offerPrice);

            String percentOff = priceData.percentDiscount.equals("") ? "0" : priceData.percentDiscount;
            percentOff = String.format(getString(R.string.percent_off), percentOff);

            String coins = priceData.coins.equals("") ? "0" : priceData.coins;
            walletCoins = Long.parseLong(coins);
            coins = String.format(getString(R.string.coins), coins);

            String duration = priceData.duration;
            String durationType = priceData.durationType;
            durationType = durationType.substring(0, 1).toUpperCase() + durationType.substring(1);

            String savings = priceData.savings.equals("") ? "0" : priceData.savings;
            if (savings.equalsIgnoreCase("0")) {
                tvSaving.setVisibility(View.GONE);
            } else {
                tvSaving.setVisibility(View.VISIBLE);
            }
            savings = String.format(getString(R.string.saving), savings);

            tvSaving.setText(savings);
            tvMrpPrice.setVisibility(View.VISIBLE);
            tvMrpPrice.setText(mrpPrice, TextView.BufferType.SPANNABLE);
            Spannable spannable = (Spannable) tvMrpPrice.getText();
            spannable.setSpan(new StrikethroughSpan(), 5, mrpPrice.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            if (priceData.costType.equals(PriceData.COST_TYPE_RUPEES)) {
                tvMrpPrice.setVisibility(View.GONE);
                if (priceData.getCoins == null || priceData.getCoins.isEmpty() || priceData.getCoins.equals("0")) {
                    tvSaving.setVisibility(View.GONE);
                    tvCoins.setVisibility(View.GONE);
                } else {
                    tvSaving.setVisibility(View.VISIBLE);
                    tvCoins.setVisibility(View.VISIBLE);
                    tvSaving.setText(getResources().getString(R.string.win));
                    tvCoins.setText(priceData.getCoins);
                }
            } else if (priceData.costType.equals(PriceData.COST_TYPE_COINS)) {
                tvMrpPrice.setVisibility(View.VISIBLE);
                tvCoins.setVisibility(View.VISIBLE);
                tvCoins.setText(coins);
            } else if (priceData.costType.equals(PriceData.COST_TYPE_COINS_AND_RUPEES)) {
                tvMrpPrice.setVisibility(View.VISIBLE);
                tvCoins.setVisibility(View.VISIBLE);
                tvCoins.setText(coins);
            }
            // tvOfferPrice.setText(offerPrice);
            tvPercentOff.setText(percentOff);
            tvDuration.setText(String.format(getString(R.string.total_duration), duration, durationType));


            // tinting drawables
            for (Drawable drawable : tvDuration.getCompoundDrawables()) {
                if (drawable != null) {
                    drawable.mutate().setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(getContext(), R.color.colorAccent), PorterDuff.Mode.SRC_IN));
                }
            }

            String costType = feedContentData.arrayListPriceData.get(position).costType;
            if (costType.equalsIgnoreCase(PriceData.COST_TYPE_RUPEES)) {
                tvPercentOff.setVisibility(View.GONE);
                llCoins.setVisibility(View.VISIBLE);
                //llOfferPrice.setVisibility(View.VISIBLE);
            } else if (costType.equalsIgnoreCase(PriceData.COST_TYPE_COINS)) {
                //llOfferPrice.setVisibility(View.GONE);
                tvPercentOff.setVisibility(View.VISIBLE);
                llCoins.setVisibility(View.VISIBLE);
            } else if (costType.equalsIgnoreCase(PriceData.COST_TYPE_COINS_AND_RUPEES)) {
                //llOfferPrice.setVisibility(View.GONE);
                tvPercentOff.setVisibility(View.VISIBLE);
                llCoins.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setCoinsData(int position) {
        PriceData priceData = arrayListPriceData.get(position);
        priceData = feedContentData.arrayListPriceData.get(position);
        String duration = priceData.duration;
        String durationType = priceData.durationType;
        durationType = durationType.substring(0, 1).toUpperCase() + durationType.substring(1);
        tvDuration.setText(String.format(getString(R.string.total_duration), duration, durationType));


        // tinting drawables
        for (Drawable drawable : tvDuration.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.mutate().setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(getContext(), R.color.colorAccent), PorterDuff.Mode.SRC_IN));
            }
        }
        //tvOr.setVisibility(View.VISIBLE);
        //tvSellingCoins.setVisibility(View.VISIBLE);
        tvCoinsTotal.setVisibility(View.VISIBLE);
        tvDivider.setVisibility(View.VISIBLE);
        //tvSellingCoins.setText(feedContentData.arrayListPriceData.get(position).finalCost);

        long walletBalance = 0;
        if (HomeActivity.toolbarWalletBalance != null) {
            walletBalance = LocalStorage.getValue(LocalStorage.KEY_WALLET_BALANCE, 0, Long.class);
        }
        long coinsForEntry = Integer.parseInt(feedContentData.arrayListPriceData.get(position).finalCost);
        if (coinsForEntry <= walletBalance) {
            rlCoinsPay.setVisibility(View.VISIBLE);
            tvCoinsPayment.setText(feedContentData.arrayListPriceData.get(position).finalCost + " " + getString(R.string.icoins));
        } else {
            rlCoinsPay.setVisibility(View.GONE);
        }
    }

    private void setSeasonData() {
        arrayListSeasonData.clear();
        arrayListSeasonData.addAll(feedContentData.arrayListSeasonData);

        // Set View Pager
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        for (int i = 0; i < arrayListSeasonData.size(); i++) {
            String seriesId = String.valueOf(feedContentData.seriesId);
            String seasonId = arrayListSeasonData.get(i).termId;
            Fragment tabFragment = EpisodePlaylistTabFragment.getInstance("", postType, seriesId, seasonId, 0, String.valueOf(feedContentData.iswatchlisted));
            ftList.add((EpisodePlaylistTabFragment) tabFragment);
            viewPagerAdapter.addFragment(tabFragment, "Position " + i);
        }
        vpSeason.setOffscreenPageLimit(3);
        vpSeason.setAdapter(viewPagerAdapter);
        tlSeason.setupWithViewPager(vpSeason);
        setSeasonTabText();
        tlSeason.addOnTabSelectedListener(this);
    }

    private void setPlaylistData() {
        // Set View Pager
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        for (int i = 0; i < 1; i++) {
            String playlistId = feedContentData.postId;
            Fragment tabFragment = EpisodePlaylistTabFragment.getInstance("", postType, playlistId, "", 0, String.valueOf(feedContentData.iswatchlisted));
            viewPagerAdapter.addFragment(tabFragment, "Position " + i);
        }
        vpSeason.setOffscreenPageLimit(1);
        vpSeason.setAdapter(viewPagerAdapter);
        tlSeason.setupWithViewPager(vpSeason);
        setPlaylistTabText();
        tlSeason.addOnTabSelectedListener(this);
    }

    @SuppressLint("ResourceType")
    private void setSeasonTabText() {
        for (int i = 0; i < arrayListSeasonData.size(); i++) {
            TextView tvCustomTab = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.custom_season_tab, null);
            tvCustomTab.setText(arrayListSeasonData.get(i).name);
            tvCustomTab.setTextColor(getResources().getColorStateList(R.drawable.tab_selector));
            if (i == 0)
                tvCustomTab.setTypeface(ResourcesCompat.getFont(getContext(), R.font.rubik_medium));
            tlSeason.getTabAt(i).setCustomView(tvCustomTab);
        }
    }

    @SuppressLint("ResourceType")
    private void setPlaylistTabText() {
        for (int i = 0; i < 1; i++) {
            TextView tvCustomTab = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.custom_season_tab, null);
            tvCustomTab.setText(R.string.playlist);
            tvCustomTab.setTextColor(getResources().getColorStateList(R.drawable.tab_selector));
            if (i == 0)
                tvCustomTab.setTypeface(ResourcesCompat.getFont(getContext(), R.font.rubik_medium));
            tlSeason.getTabAt(i).setCustomView(tvCustomTab);
        }
    }

    public void showDuration(View view) {
        TextView textView = (TextView) view;
        String selectedDuration = textView.getText().toString();
        startActivityForResult(new Intent(getContext(), DurationActivity.class).putExtra(DurationActivity.PRICE_DATA, arrayListPriceData).putExtra(DurationActivity.SELECTED_DURATION, selectedDuration), REQUEST_CODE);
    }

    public void showBuySuccessDialog() {
        viewContentLoader.setVisibility(View.GONE);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext()).setView(R.layout.dialog_custom);
        alertDialog = builder.create();
        alertDialog.setCancelable(true);
        alertDialog.show();

        TextView tvTitle = alertDialog.findViewById(R.id.tvStoreName);
        TextView btPositive = alertDialog.findViewById(R.id.btPositive);
        TextView btNegative = alertDialog.findViewById(R.id.btNegative);
        TextView btNeutral = alertDialog.findViewById(R.id.btNeutral);
        tvTitle.setText(R.string.msg_purchase_success);
        btPositive.setText(R.string.play_now);
        btNegative.setText(R.string.view_purchases);
        btNeutral.setText(R.string.play_now);
        if (Utility.isTelevision()) {
            btPositive.setVisibility(View.GONE);
            btNegative.setVisibility(View.GONE);
            btNeutral.setVisibility(View.VISIBLE);
        } else {
            btPositive.setVisibility(View.VISIBLE);
            btNeutral.setVisibility(View.VISIBLE);
        }


        btNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPurchases();
                refreshScreen();
            }
        });

        btNeutral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshScreen();
                playNow();
            }
        });

        btPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNow();
            }
        });
    }

    private void showAlreadyPurchasedDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext()).setView(R.layout.dialog_custom);
        alertDialog = builder.create();
        alertDialog.setCancelable(true);
        alertDialog.show();

        TextView tvTitle = alertDialog.findViewById(R.id.tvStoreName);
        TextView btPositive = alertDialog.findViewById(R.id.btPositive);
        TextView btNegative = alertDialog.findViewById(R.id.btNegative);
        tvTitle.setText(message);
        btPositive.setText(R.string.play_now);
        btNegative.setText(R.string.ok);
        btPositive.setVisibility(View.VISIBLE);
        btNegative.setVisibility(View.VISIBLE);


        btNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        btPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNow();
            }
        });

    }

    private void showInsufficientCoinsDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext()).setView(R.layout.dialog_custom);
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

    private void showPremiumDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext()).setView(R.layout.dialog_custom);
        alertDialog = builder.create();
        alertDialog.setCancelable(true);
        alertDialog.show();

        TextView tvTitle = alertDialog.findViewById(R.id.tvStoreName);
        TextView btPositive = alertDialog.findViewById(R.id.btPositive);
        TextView btNegative = alertDialog.findViewById(R.id.btNegative);
        tvTitle.setText(getString(R.string.subscribed));
        btPositive.setText(R.string.play_now);
        btNegative.setText(R.string.ok);
        btPositive.setVisibility(View.VISIBLE);
        btNegative.setVisibility(View.VISIBLE);


        btNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                refreshScreen();
            }
        });

        btPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNow();
            }
        });

    }

    private void viewPurchases() {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
            /*Intent returnIntent = new Intent();
            returnIntent.putExtra(KEY_ACTION_TYPE, VIEW_PURCHASES);
            setResult(Activity.RESULT_OK, returnIntent);*/
            startActivity(new Intent(getContext(), PurchasesActivity.class).putExtra("title", getResources().getString(R.string.label_purchases)));
            //finish();
        }
    }

    private void playNow() {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
            Intent returnIntent = new Intent();
            feedContentData.feedPosition = 0;
            returnIntent.putExtra(KEY_ACTION_TYPE, PLAY_NOW);
            returnIntent.putExtra(KEY_CONTENT_DATA, feedContentData);
            ((HomeActivity) requireActivity()).playPremiumContent(feedContentData, null);
            getParentFragmentManager().popBackStack();
            //finish();
        }
    }

    private void playContent() {
        Intent returnIntent = new Intent();
        feedContentData.feedPosition = 0;
        returnIntent.putExtra(KEY_ACTION_TYPE, PLAY_NOW);
        returnIntent.putExtra(KEY_CONTENT_DATA, feedContentData);
        ((HomeActivity) getActivity()).playPremiumContent(feedContentData, null);
        getParentFragmentManager().popBackStack();
        //getActivity().setResult(Activity.RESULT_OK, returnIntent);
        //finish();
    }

   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (data != null) {
                int position = data.getIntExtra(DurationActivity.POSITION, 0);
                priceDataPos = position;
                setPriceData(position);
            }
        }
        if (requestCode == REQUEST_CODE_BUY) {
            if (data != null) {
                if (data.hasExtra(KEY_BUY)) {
                    //callOrderAPI();
                    //paytmPrepayment(data.getStringExtra(KEY_PRODUCT_ID), selectedDurationPosition, "");
                }
            }
        }
        if (requestCode == REQUEST_CODE_WATCH) {
            if (data != null) {
                if (data.hasExtra(KEY_WATCH)) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(KEY_ACTION_TYPE, PLAY_NOW);
                    returnIntent.putExtra(REQUEST_CODE_EXTRA, REQUEST_CODE_WATCH);
                    returnIntent.putExtra(KEY_CONTENT_DATA, feedContentData);
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            }
        }
        if (requestCode == REQUEST_CODE_PREMIUM) {
            if (data != null) {
                boolean status = data.getBooleanExtra("status", false);
                if (status) {
                    APIMethods.getUserDetails(BuyDetailActivity.this);
                    showPremiumDialog();
                }
            }
        }

        if (requestCode == 101) {
            if (data != null) {
                Intent intent = new Intent();
                intent.putExtras(data);
                setResult(this.RESULT_OK, intent);
                this.finish();
            }
        }
    }*/

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        tabPosition = tab.getPosition();
        TextView tvTabTextView = (TextView) tab.getCustomView();
        if (tvTabTextView != null) {
            tvTabTextView.setTypeface(ResourcesCompat.getFont(getContext(), R.font.rubik_medium));
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        TextView tvTabTextView = (TextView) tab.getCustomView();
        if (tvTabTextView != null) {
            tvTabTextView.setTypeface(ResourcesCompat.getFont(getContext(), R.font.rubik_regular));
        }
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    /**
     * RAZORPAY INTERFACE IMPLEMENTATION
     **/

    private void configureBuyNow() {
        buyNow.setOnClickListener(v -> {
            if (feedContentData != null) {
                //String costType = feedContentData.arrayListPriceData.get(priceDataPos).costType;
               /* if (costType.equalsIgnoreCase(PriceData.COST_TYPE_COINS)) {
                    callOrderAPI();
                } else {*/
                    /*if (user.mobile == null || user.mobile.equals("")) {
                        //System.out.println("Mobile: " + user.mobile);
                        startActivity(new Intent(this, VerificationActivity.class).putExtra(VerificationActivity.TYPE_KEY, KEY_MOBILE));
                    } else if (user.userEmail == null || user.userEmail.equals("")) {
                        //System.out.println("Email: " + user.userEmail);
                        startActivity(new Intent(this, VerificationActivity.class).putExtra(VerificationActivity.TYPE_KEY, KEY_EMAIL));
                    } else {*/
                if (rlCoinsPay.getVisibility() == View.VISIBLE) {
                    long walletBalance = 0;
                    if (HomeActivity.toolbarWalletBalance != null) {
                        walletBalance = LocalStorage.getValue(LocalStorage.KEY_WALLET_BALANCE, 0, Long.class);
                    }
                    if (feedContentData.arrayListPriceData.size() > coinsDataPos) {
                        long coinsForEntry = Integer.parseInt(feedContentData.arrayListPriceData.get(coinsDataPos).finalCost);
                        if (coinsForEntry <= walletBalance) {
                            buyNow.setVisibility(View.GONE);
                            paymentRoot.setVisibility(View.VISIBLE);
                            rlCoinsPay.requestFocus();
                            nsvBuydetail.post(new Runnable() {
                                public void run() {
                                    nsvBuydetail.scrollTo(0, nsvBuydetail.getBottom());
                                }
                            });
                        } else {
                            startActivity(new Intent(getContext(), PremiumActivity.class).putExtra("title", getResources().getString(R.string.label_premium)));
                        }
                    } else {
                        startActivity(new Intent(getContext(), PremiumActivity.class).putExtra("title", getResources().getString(R.string.label_premium)));
                    }
                } else {
                    startActivity(new Intent(getContext(), PremiumActivity.class).putExtra("title", getResources().getString(R.string.label_premium)));
                }
                    /*    }
                    }
                    callOrderAPI();
                    if (!hasRequiredPermissions()) {
                        askPermission(new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, new PermissionCallback() {
                            @Override
                            public void onRequestPermissionGranted(String[] permission) {
                                callOrderAPI();
                            }

                            @Override
                            public void onRequestPermissionDenied(String[] permission) {

                            }

                            @Override
                            public void onRequestPermissionDeniedPermanently(String[] permission) {

                            }
                        });
                        //ActivityCompat.requestPermissions(BuyDetailActivity.this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, 101);
                    } else {
                        callOrderAPI();
                    }*/

                // }
            }
        });
    }

    private void configureBuyNowCoins() {
       /* buyNowCoins.setOnClickListener(v -> {
            if (feedContentData != null) {
                String costType = feedContentData.arrayListPriceData.get(coinsDataPos).costType;
                if (costType.equalsIgnoreCase(PriceData.COST_TYPE_COINS)) {
                    callOrderByCoinsAPI();
                } else {
                    buyNowCoins.setVisibility(View.GONE);
                }
            }
        });*/
    }

    /*private boolean hasRequiredPermissions() {
        return super.isGrantedPermission(this, Manifest.permission.READ_SMS);
    }*/

    private void getPostData() {
        try {
            Map<String, String> params = new HashMap<>();
            APIRequest apiRequest = new APIRequest(Url.GET_POST_BY_ID + "&ID=" + feedContentData.postId, Request.Method.GET, params, null, getContext());
            apiRequest.showLoader = true;
            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    handlePostResponse(response, error);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void handlePostResponse(@Nullable String response, @Nullable Exception error) {
        try {
            if (error != null) {
                // showError(error.getMessage());
            } else {
                if (response != null) {
                    Log.e("response", response);

                    JSONObject jsonObjectResponse = new JSONObject(response);
                    String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                    if (type.equalsIgnoreCase("error")) {
                        String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                        showError(message);
                    } else if (type.equalsIgnoreCase("ok")) {
                        try {
                            JSONObject jsonArrayMsg = jsonObjectResponse.getJSONObject("msg");
                            boolean can_i_use = jsonArrayMsg.has("can_i_use") ? jsonArrayMsg.getBoolean("can_i_use") : false;
                            feedContentData.can_i_use = can_i_use;
                            SubscriptionDetails sd = LocalStorage.getUserSubscriptionDetails();
                            if (sd != null && sd.allow_rental != null) {
                                if (sd.allow_rental.equalsIgnoreCase(Constant.YES)) {
                                    buyNow.setVisibility(View.GONE);
                                    duration.setVisibility(View.GONE);
                                    tvDuration.setVisibility(View.GONE);
                                    if (ivPlayBtn.getVisibility() == View.GONE && llSeasons != null) {
                                        llSeasons.requestFocus();
                                    }

                                    if (feedContentData.postType.equalsIgnoreCase(FeedContentData.POST_TYPE_POST)) {
                                        playNow.setVisibility(View.VISIBLE);
                                    } else {
                                        playNow.setVisibility(View.GONE);
                                    }
                                } else {
                                    if (feedContentData.postType.equalsIgnoreCase(FeedContentData.TYPE_ORIGINALS)) {
                                        if (feedContentData.can_i_use) {
                                            buyNow.setVisibility(View.GONE);
                                            duration.setVisibility(View.GONE);
                                            tvDuration.setVisibility(View.GONE);
                                            playNow.setVisibility(View.GONE);
                                            if (ivPlayBtn.getVisibility() == View.GONE && llSeasons != null && llSeasons.getVisibility() == View.VISIBLE) {
                                                llSeasons.requestFocus();
                                            }
                                        } else {
                                            buyNow.setVisibility(View.VISIBLE);
                                            //duration.setVisibility(View.VISIBLE);
                                            //tvDuration.setVisibility(View.VISIBLE);
                                            duration.setVisibility(View.GONE);
                                            tvDuration.setVisibility(View.GONE);
                                            playNow.setVisibility(View.GONE);
                                            if (feedContentData.getTeaserUrl() == null || feedContentData.getTeaserUrl().isEmpty() || feedContentData.getTeaserUrl().equals("false")) {
                                                buyNow.requestFocus();
                                            }
                                        }
                                    } else {
                                        if (feedContentData.can_i_use) {
                                            playContent();
                                        } else {
                                            buyNow.setVisibility(View.VISIBLE);
                                            //duration.setVisibility(View.VISIBLE);
                                            //tvDuration.setVisibility(View.VISIBLE);
                                            duration.setVisibility(View.GONE);
                                            tvDuration.setVisibility(View.GONE);
                                            playNow.setVisibility(View.GONE);
                                            if (feedContentData.getTeaserUrl() == null || feedContentData.getTeaserUrl().isEmpty() || feedContentData.getTeaserUrl().equals("false")) {
                                                buyNow.requestFocus();
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (JSONException e) {

        }
    }

    private void callOrderAPI() {
        // CHECK USE HAS ENOUGH COIN BALANCE?
        String productId = "";
        if (feedContentData != null) {
            if (feedContentData.postType.equals(FeedContentData.POST_TYPE_SEASON)) {
                productId = feedContentData.seriesId + ":" + feedContentData.seasonId;

            } else {
                productId = feedContentData.postId;
            }
            String name = feedContentData.name;
            razorpayPrepayment(productId, selectedDurationPosition, "");
        } else {
            Utility.showError(getString(R.string.product_id_missing), getContext());
        }
    }

    private void callOrderByCoinsAPI() {
        // CHECK USE HAS ENOUGH COIN BALANCE?
        String productId = "";
        if (feedContentData != null) {
            if (feedContentData.postType.equals(FeedContentData.POST_TYPE_SEASON)) {
                productId = feedContentData.seriesId + ":" + feedContentData.seasonId;

            } else {
                productId = feedContentData.postId;
            }
            String name = feedContentData.name;
            coinspayPrepayment(productId, selectedDurationPositionCoins, "");
        } else {
            Utility.showError(getString(R.string.product_id_missing), getContext());
        }
    }

    // RAZORPAY INIT ORDER
    public void razorpayPrepayment(String productId, int pricing_option, String screenType1) {
        try {
            screenType = screenType1;
            Map<String, String> params = new HashMap<>();
            params.put("ID", productId);
            params.put("payment_mode", "razorpay");
            params.put("pricing_option", String.valueOf(pricing_option));

            // Getge user details like name, phone & email
            //params.put("user_name", LocalStorage.getUserDisplayName().toString());
            //params.put("user_phone", );
            //params.put("user_email", );

            APIRequest apiRequest = new APIRequest(Url.INITIATE_ORDER, Request.Method.POST, params, null, getContext());
            apiRequest.showLoader = true;
            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    if (error == null) {
                        handleRazorpayPrepaymentResponse(response, error);
                    } else {
                        Utility.showError(error.toString(), getContext());
                    }
                }
            });
        } catch (Exception e) {
            Utility.showError(getString(R.string.failed_to_initiate_payment), getContext());
        }
    }

    public void coinspayPrepayment(String productId, int pricing_option, String screenType1) {
        try {
            screenType = screenType1;
            Map<String, String> params = new HashMap<>();
            params.put("ID", productId);
            params.put("payment_mode", "icoins");
            params.put("pricing_option", String.valueOf(pricing_option));

            APIRequest apiRequest = new APIRequest(Url.INITIATE_ORDER, Request.Method.POST, params, null, getContext());
            apiRequest.showLoader = true;
            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    if (error == null) {
                        handleCoinsPayPrepaymentResponse(response, error, productId);
                    } else {
                        Utility.showError(error.toString(), getContext());
                    }
                }
            });
        } catch (Exception e) {
            Utility.showError(getString(R.string.failed_to_initiate_payment), getContext());
        }
    }

    // RAZORPAY INIT ORDER RESPONSE
    private void handleRazorpayPrepaymentResponse(@Nullable String response, @Nullable Exception error) {
        try {
            if (error != null) {
                Utility.showError(error.getMessage(), getContext());
            } else {
                if (response != null) {
                    JSONObject jsonObjectResponse = new JSONObject(response);
                    String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                    if (type.equalsIgnoreCase("error")) {
                        String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                        //Utility.showError(message,this);
                        showAlreadyPurchasedDialog(message);
                    } else if (type.equalsIgnoreCase("ok")) {
                        /// new flow checking to be done here
                        JSONObject object = jsonObjectResponse.getJSONObject("msg");
                        String status = object.has("status") ? object.getString("status") : "";
                        if (status.equalsIgnoreCase("SUCCESS")) {
                            //flow for coin  only
                            if (screenType.equalsIgnoreCase(EpisodePlaylistTabFragment.FROMSEASONTAB)) {
                                refreshTabScreen();
                            } else {
                                showBuySuccessDialog();
                            }
                        } else {
                            //flow for coins/rupees
                            this.initiateRazorpay(object);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            Utility.showError(e.toString(), getContext());
        }
    }

    private void handleCoinsPayPrepaymentResponse(@Nullable String response, @Nullable Exception error, String productId) {
        try {
            if (error != null) {
                Utility.showError(error.getMessage(), getContext());
            } else {
                if (response != null) {
                    JSONObject jsonObjectResponse = new JSONObject(response);
                    String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                    if (type.equalsIgnoreCase("error")) {
                        String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                        //Utility.showError(message,this);
                        if (message.contains("purchased")) {
                            showAlreadyPurchasedDialog(message);
                        } else {
                            showInsufficientCoinsDialog(message);
                        }
                    } else if (type.equalsIgnoreCase("ok")) {
                        /// new flow checking to be done here
                        JSONObject object = jsonObjectResponse.getJSONObject("msg");
                        String status = object.has("status") ? object.getString("status") : "";
                        if (status.equalsIgnoreCase("SUCCESS")) {
                            //flow for coin  only
                            if (screenType.equalsIgnoreCase(EpisodePlaylistTabFragment.FROMSEASONTAB)) {
                                refreshTabScreen();
                            } else {
                                purchasedList.add(productId);
                                LocalStorage.savePurchasedArrayList(purchasedList, "savedPurchased", getContext());
                                showBuySuccessDialog();
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            Utility.showError(e.toString(), getContext());
        }
    }

    private String itap_order_id = "";

    public void initiateRazorpay(JSONObject object) {
        final Activity activity = HomeActivity.getInstance();
        final Checkout instance = new Checkout();
        try {
            /*   MERCHANT LOGO
             *   MERCHANT NAME
             */
            instance.setImage(R.mipmap.itap_logo);
            JSONArray wallets = new JSONArray();
            wallets.put("paytm");
            JSONObject externals = new JSONObject();
            externals.put("wallets", wallets);
            JSONObject options = new JSONObject();
            options.put("external", externals);
            options.put("name", "iTap");
            options.put("description", object.getString("post_title"));
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("currency", "INR");
            options.put("amount", object.getString("amount"));
            options.put("order_id", object.getString("razorpay_order_id"));
            this.itap_order_id = object.getString("order_id");

            instance.open(activity, options);
        } catch (Exception e) {
            Toast.makeText(activity, getString(R.string.error_in_payment) + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onPaymentSuccess(String response) {
        if (response.isEmpty()) {
            Utility.showError(getString(R.string.razorpay_payment_id_is_null), getContext());
            this.itap_order_id = "";
        } else {
            razorpayPostPayment(this.itap_order_id, response);
        }
    }

    private void razorpayPostPayment(String itap_order_id, String razorpay_order_id) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("payment_mode", "razorpay");
            params.put("order_id", itap_order_id);
            params.put("razorpay_payment_id", razorpay_order_id);

            APIRequest apiRequest = new APIRequest(Url.COMPLETE_ORDER, Request.Method.POST, params, null, getContext());
            apiRequest.showLoader = false;
            viewContentLoader.setVisibility(View.VISIBLE);
            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    try {
                        if (error == null) {
                            JSONObject jsonObjectResponse = new JSONObject(response);
                            String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";

                            if (type.equalsIgnoreCase("error")) {

                                String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                                Utility.showError(message, getContext());

                            } else if (type.equalsIgnoreCase("ok") && jsonObjectResponse.has("msg")) {

                                JSONObject msg = jsonObjectResponse.getJSONObject("msg");
                                if (msg.getString("status").equalsIgnoreCase("success")) {
                                    if (screenType.equalsIgnoreCase(EpisodePlaylistTabFragment.FROMSEASONTAB)) {
                                        viewContentLoader.setVisibility(View.GONE);
                                        refreshTabScreen();
                                    } else {
                                        showBuySuccessDialog();
                                    }
                                    /*AlertUtils.showAlert("Success", msg.getString("display_message"), "OK", BuyDetailActivity.this, new AlertUtils.AlertClickHandler() {
                                        @Override
                                        public void alertButtonAction(boolean isPositiveAction) {
                                            Intent returnIntent = new Intent();
                                            returnIntent.putExtra(WalletActivity.KEY_SUCCESS,true);
                                            setResult(Activity.RESULT_OK,returnIntent);
                                            finish();
                                        }
                                    });*/
                                } else {
                                    Utility.showError(msg.getString("display_message"), getContext());
                                }
                            }
                        } else {
                            Utility.showError(error.toString(), getContext());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }
            });
        } catch (Exception e) {
            Utility.showError(getString(R.string.failed_to_complete_payment), getContext());
        }
    }

    @Override
    public void onPaymentError(int code, String response) {
        this.itap_order_id = "";
        switch (code) {
            case Checkout.NETWORK_ERROR:
                Toast.makeText(getContext(), getString(R.string.there_was_a_network_error), Toast.LENGTH_LONG).show();
                break;

            case Checkout.PAYMENT_CANCELED:
            case Checkout.INVALID_OPTIONS:
                Toast.makeText(getContext(), response, Toast.LENGTH_LONG).show();
                break;

            case Checkout.TLS_ERROR:
                Toast.makeText(getContext(), getString(R.string.device_does_not_have), Toast.LENGTH_LONG).show();
                break;
        }
    }

    /*@Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }*/

    @Override
    public void onExternalWalletSelected(String walletName, PaymentData paymentData) {

        if (walletName.equals("paytm")) {
            //handle paytm payment
            String productId = "";
            if (feedContentData != null) {
                productId = feedContentData.postId;
                paytmPrepayment(productId, selectedDurationPosition, "");
            } else {
                Utility.showError(getString(R.string.product_id_missing), getContext());
            }
        }
    }

    public void paytmPrepayment(String productId, int pricing_option, String screenType1) {
        try {
            screenType = screenType1;
            Map<String, String> params = new HashMap<>();
            params.put("ID", productId);
            params.put("payment_mode", "paytm");
            params.put("pricing_option", String.valueOf(pricing_option));

            APIRequest apiRequest = new APIRequest(Url.INITIATE_ORDER, Request.Method.POST, params, null, getContext());
            apiRequest.showLoader = true;
            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    if (error == null) {
                        handlePaytmPrepaymentResponse(response, error);
                    } else {
                        Utility.showError(error.toString(), getContext());
                    }
                }
            });
        } catch (Exception e) {
            Utility.showError(getString(R.string.failed_to_initiate_payment), getContext());
        }
    }

    private void handlePaytmPrepaymentResponse(@Nullable String response, @Nullable Exception error) {
        try {
            if (error != null) {
                Utility.showError(error.getMessage(), getContext());
            } else {
                if (response != null) {
                    JSONObject jsonObjectResponse = new JSONObject(response);
                    String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                    if (type.equalsIgnoreCase("error")) {
                        String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                        Utility.showError(message, getContext());
                    } else if (type.equalsIgnoreCase("ok")) {
                        JSONObject object = jsonObjectResponse.getJSONObject("msg");
                        this.initiatePaytm(object);
                    }
                }
            }
        } catch (JSONException e) {
            Utility.showError(e.toString(), getContext());
        }
    }

    private void initiatePaytm(JSONObject object) {
        Log.d("avinash", "initiatePaytm");
        // Staging environment:
        // PaytmPGService Service = PaytmPGService.getStagingService();
        // Production environment:
        PaytmPGService Service = PaytmPGService.getProductionService();

        /**
         *  ORDER PAYLOAD
         */

        HashMap<String, String> paramMap = new HashMap<>();
        JSONObject paytmParams = null;
        String orderId = "";

        try {
            if (!object.has("paytmParams")) {
                Utility.showError(getString(R.string.order_request_parameters_not_received), getContext());
            } else {
                paytmParams = object.getJSONObject("paytmParams");

                paramMap.put("MID", paytmParams.getString("MID"));
                paramMap.put("ORDER_ID", paytmParams.getString("ORDER_ID"));
                paramMap.put("CUST_ID", paytmParams.getString("CUST_ID"));
                paramMap.put("MOBILE_NO", paytmParams.getString("MOBILE_NO"));
                paramMap.put("EMAIL", paytmParams.getString("EMAIL"));
                paramMap.put("CHANNEL_ID", paytmParams.getString("CHANNEL_ID"));
                paramMap.put("TXN_AMOUNT", paytmParams.getString("TXN_AMOUNT"));
                paramMap.put("WEBSITE", paytmParams.getString("WEBSITE"));
                paramMap.put("INDUSTRY_TYPE_ID", paytmParams.getString("INDUSTRY_TYPE_ID"));
                paramMap.put("CALLBACK_URL", paytmParams.getString("CALLBACK_URL"));
                paramMap.put("CHECKSUMHASH", paytmParams.getString("checksum"));
                PaytmOrder Order = new PaytmOrder(paramMap);

                /*Log.e("avinash", "Request RESPONE = " + paytmParams.toString());
                Iterator<String> iter = paytmParams.keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    paramMap.put(key, paytmParams.getString(key));
                }
                Log.e("avinash", "Request parameter");
                Log.d("avinash", "Keys = " + String.valueOf(iter));
                PaytmOrder Order = new PaytmOrder(paramMap); */


                Log.d("avinash", String.valueOf(paramMap));
                /**  CLIENT SIDE SSL CERTIFICATE. (OPTIONAL)
                 *    PaytmClientCertificate Certificate = new PaytmClientCertificate(String inPassword, String inFileName);
                 *    Service.initialize(Order, Certificate);
                 */

                Service.initialize(Order, null);

                Service.startPaymentTransaction(getContext(), true, true, new PaytmPaymentTransactionCallback() {

                    @Override
                    public void onTransactionResponse(Bundle inResponse) {
                        Log.d("avinash", "onTransactionResponse " + inResponse.getString("ORDERID"));
                        if (inResponse.getString("STATUS").equalsIgnoreCase("TXN_SUCCESS")) {
                            // TRANSACTION SUCCESS
                            paytmPostPaymentRequest(inResponse);
                        } else {
                            // TRANSACTION FAILED
                            Log.d("avinash", "Paytm onTransactionResponse");
                            Utility.showError((inResponse.containsKey("RESPMSG") ? inResponse.getString("RESPMSG") : getString(R.string.transaction_failed)), getContext());
                        }
                    }

                    @Override
                    public void networkNotAvailable() {
                        Toast.makeText(getContext(), getString(R.string.network_connection_error), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onErrorProceed(String s) {

                    }

                    @Override
                    public void clientAuthenticationFailed(String inErrorMessage) {
                        Toast.makeText(getContext(), getString(R.string.client_authentication_failed) + inErrorMessage, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void someUIErrorOccurred(String inErrorMessage) {
                        Toast.makeText(getContext(), getString(R.string.ui_errror) + inErrorMessage, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
                        Toast.makeText(getContext(), getString(R.string.unable_to_load_webpage) + String.valueOf(iniErrorCode) + inErrorMessage, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onBackPressedCancelTransaction() {
                        Toast.makeText(getContext(), getString(R.string.transaction_cancelled), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                        Toast.makeText(getContext(), getString(R.string.transaction_cancelled) + inResponse.toString(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        } catch (Exception e) {
            Log.d("avinash", "Exception " + e.getMessage());
        }
    }


    private void paytmPostPaymentRequest(Bundle inResponse) {
        Log.d("avinash", "paytmPostPaymentRequest");
        try {
            Map<String, String> params = new HashMap<>();
            params.put("payment_mode", "paytm");
            params.put("order_id", inResponse.getString("ORDERID"));

            APIRequest apiRequest = new APIRequest(Url.COMPLETE_ORDER, Request.Method.POST, params, null, getContext());
            apiRequest.showLoader = true;
            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    try {
                        JSONObject jsonObjectResponse = new JSONObject(response);
                        String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                        if (type.equalsIgnoreCase("error")) {

                            String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                            Utility.showError(message, getContext());

                        } else if (type.equalsIgnoreCase("ok") && jsonObjectResponse.has("msg")) {
                            JSONObject msg = jsonObjectResponse.getJSONObject("msg");
                            if (msg.getString("status").equalsIgnoreCase("success")) {
                                if (screenType.equalsIgnoreCase(EpisodePlaylistTabFragment.FROMSEASONTAB)) {
                                    refreshTabScreen();
                                } else {
                                    showBuySuccessDialog();
                                }
                                /*AlertUtils.showAlert("Success", msg.getString("display_message"), "OK", BuyDetailActivity.this, new AlertUtils.AlertClickHandler() {
                                    @Override
                                    public void alertButtonAction(boolean isPositiveAction) {
                                        Intent returnIntent = new Intent();
                                        returnIntent.putExtra(WalletActivity.KEY_SUCCESS, true);
                                        setResult(Activity.RESULT_OK, returnIntent);
                                        finish();
                                    }
                                });*/
                            } else {
                                Utility.showError(msg.getString("display_message"), getContext());
                            }
                        }
                    } catch (Exception e) {

                    }
                }
            });
        } catch (Exception e) {
            Utility.showError(getString(R.string.failed_to_complete_payment), getContext());
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

    public void refreshScreen() {
        paymentRoot.setVisibility(View.GONE);
        duration.setVisibility(View.GONE);
        tvDuration.setVisibility(View.GONE);
        buyNow.setVisibility(View.GONE);
        playNow.setVisibility(View.GONE);
        if (feedContentData.postType.equalsIgnoreCase(FeedContentData.POST_TYPE_ORIGINALS) || feedContentData.postType.equalsIgnoreCase(FeedContentData.POST_TYPE_SEASON) || feedContentData.postType.equalsIgnoreCase(FeedContentData.POST_TYPE_EPISODE)) {
            if (!Utility.isTelevision()) {
                setSeasonData();
            }
        }
    }

    public void refreshTabScreen() {
        ftList.get(tabPosition).refreshTab();
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
                                    startActivityForResult(new Intent(getContext(), BrowserActivity.class).putExtra("title", "Premium").putExtra("posturl", subscriptionsUrl), REQUEST_CODE_PREMIUM);
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
    public void onResume() {
        super.onResume();
        if (getArguments() != null && isRefreshRequired) {
            isRefreshRequired = false;
            //finish();
            //startActivity(getContext());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isRefreshRequired = true;
    }

    public void playTeaserTv(View view) {
        HomeActivity.getInstance().pausePlayer();
        /*startActivity(new Intent(this, TeaserActivity.class).putExtra(CONTENT_DATA, feedContentData));*/
        SubscriptionDetails sd = LocalStorage.getUserSubscriptionDetails();
        if (sd != null && sd.allow_rental != null) {
            if (sd.allow_rental.equalsIgnoreCase(Constant.YES)) {
                startActivity(new Intent(getContext(), TeaserActivity.class).putExtra(CONTENT_DATA, feedContentData).putExtra(REQUEST_CODE_EXTRA, REQUEST_CODE_WATCH));
            } else {
                startActivity(new Intent(getContext(), TeaserActivity.class).putExtra(CONTENT_DATA, feedContentData).putExtra(REQUEST_CODE_EXTRA, REQUEST_CODE_BUY));
            }
        }
    }
}