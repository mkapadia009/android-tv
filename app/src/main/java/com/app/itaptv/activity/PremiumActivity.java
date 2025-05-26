package com.app.itaptv.activity;

import static com.app.itaptv.activity.SubscriptionSuccessActivity.REVENUE;
import static com.app.itaptv.utils.LocalStorage.STATUS_ACTIVE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.app.itaptv.API.APIManager;
import com.app.itaptv.API.APIMethods;
import com.app.itaptv.API.APIRequest;
import com.app.itaptv.API.APIResponse;
import com.app.itaptv.API.Url;
import com.app.itaptv.R;
import com.app.itaptv.custom_interface.SliderListener;
import com.app.itaptv.custom_widget.DotsIndicatorDecoration;
import com.app.itaptv.holder.CustomAdSliderHolder;
import com.app.itaptv.structure.AdFieldsData;
import com.app.itaptv.structure.AdMobData;
import com.app.itaptv.structure.FeedContentData;
import com.app.itaptv.structure.SubscriptionPlanData;
import com.app.itaptv.utils.AlertUtils;
import com.app.itaptv.utils.Constant;
import com.app.itaptv.utils.CustomClickableSpan;
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
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.kalpesh.krecycleradapter.Interface.KRecyclerItemClickListener;
import com.kalpesh.krecycleradapter.Interface.KRecyclerViewHolderCallBack;
import com.kalpesh.krecycleradapter.KRecyclerViewAdapter;
import com.kalpesh.krecycleradapter.KRecyclerViewHolder;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.paytm.pgsdk.TransactionManager;
import com.razorpay.Checkout;
import com.razorpay.ExternalWalletListener;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class PremiumActivity extends BaseActivity implements PaymentResultListener, ExternalWalletListener {

    public static int PREMIUM_REQUEST_CODE = 327;

    //Button manageSubscriptions;
    ProgressBar progressbar;
    AdView mAdView;
    AdRequest adRequest;
    ConstraintLayout clAdHolder;
    ImageView ivCustomAd;
    ImageView ivClose;
    ImageView ivVolumeUp;
    ImageView ivVolumeOff;
    ImageView ivQrcode;
    NestedScrollView nsvSubscriptionMobile;
    LinearLayout llSubscriptionTv;
    private boolean resumePlayer = false;

    private ExoPlayer playerPremium;
    private PlayerView playerView;

    HomeActivity homeActivity;
    // MediaPlayer mediaPlayer;

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

    private GridLayout glPlans;
    private RadioGroup rgPayment;
    private RadioGroup rgOtherCountryPayment;
    private RadioButton selectedRadioButton;
    private Button btPaySubscribe;
    private Button btCancelSubscription;
    private Button btGoToPremium;
    private LinearLayout llCoupon;
    private LinearLayout llPayment;
    private EditText etCouponCode;
    private TextView tvApply;
    private TextView tvActiveSubscription;

    private int selectedPlanPos;

    private LinearLayout[] llSubscriptionPlanList;
    private ArrayList<TextView> tvRupeesList;
    private ArrayList<TextView> tvDiscountList;
    private ArrayList<TextView> tvDurationList;
    private ArrayList<SubscriptionPlanData> subscriptionPlanDataArrayList = new ArrayList<>();

    String screenType;
    int selectedIndex = -1;
    String couponId = "";
    String subscriptionIdPayment = "";
    String couponIdPayment = "";
    int revenue = 0;
    boolean isFirstLoading = false;

    int PAYTM_REQUEST_CODE = 108;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium);
        Constant.NOTIFICATION_PAGE = "";
        isFirstLoading = true;
        init();
    }

    public void init() {
        setToolbar(true);
        showToolbar(true);
        showToolbarBackButton(R.drawable.white_arrow);
        showToolbarTitle(true);
        setCustomizedTitle(0f);
        setToolbarTitle(getString(R.string.label_premium));

        //manageSubscriptions = findViewById(R.id.btnManageSubsciptions);
        nsvSubscriptionMobile = findViewById(R.id.nsvSubscriptionMobile);
        llSubscriptionTv = findViewById(R.id.llSubscriptionTv);
        progressbar = findViewById(R.id.progressBar);
        glPlans = findViewById(R.id.glPlans);
        rgPayment = findViewById(R.id.rgPayment);
        rgOtherCountryPayment = findViewById(R.id.rgOtherCountryPayment);
        btPaySubscribe = findViewById(R.id.btPaySubscribe);
        btCancelSubscription = findViewById(R.id.btCancelSubscription);
        btGoToPremium = findViewById(R.id.btGoToPremium);
        llCoupon = findViewById(R.id.llCoupon);
        llPayment = findViewById(R.id.llPayment);
        etCouponCode = findViewById(R.id.etCouponCode);
        tvApply = findViewById(R.id.tvApply);
        tvActiveSubscription = findViewById(R.id.tvActiveSubscription);
        ivQrcode = findViewById(R.id.ivQrcode);

        mAdView = findViewById(R.id.adViewSubscribeFrag);
        clAdHolder = findViewById(R.id.cl_ad_holder);
        ivCustomAd = findViewById(R.id.ivCustomAd);
        ivClose = findViewById(R.id.ivClose);
        playerView = findViewById(R.id.playerView);
        ivVolumeUp = findViewById(R.id.ivVolumeUp);
        ivVolumeOff = findViewById(R.id.ivVolumeOff);
        rvSliderAd = findViewById(R.id.rvSliderAd);
        adRequest = new AdRequest.Builder().build();

        setTheme(R.style.Theme_Leanback);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        startManageSubscriptions();
        Utility.buttonFocusListener(btGoToPremium);

        /*manageSubscriptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = getResources().getResourceEntryName(manageSubscriptions.getId());
                Log.i("SubscribeFragment", String.valueOf(id));
                showAdPremium(String.valueOf(id));
                startManageSubscriptions();
            }
        });*/

        //setClickableTexts();

        btPaySubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etCouponCode.getText().toString().isEmpty()) {
                    couponId = "";
                    subscriptionIdPayment = "";
                    couponIdPayment = "";
                    if (LocalStorage.getUserData().countryCode.equalsIgnoreCase(Constant.INDIA)) {
                        selectedRadioButton = rgPayment.findViewById(rgPayment.getCheckedRadioButtonId());
                        if (selectedRadioButton.getText().toString().equalsIgnoreCase("Paytm")) {
                            paytmPrepayment(String.valueOf(subscriptionPlanDataArrayList.get(selectedPlanPos).ID));
                        } else {
                            razorpayPrepayment(String.valueOf(subscriptionPlanDataArrayList.get(selectedPlanPos).ID));
                        }
                    } else {
                        selectedRadioButton = rgOtherCountryPayment.findViewById(rgOtherCountryPayment.getCheckedRadioButtonId());
                        stripePrepayment(String.valueOf(subscriptionPlanDataArrayList.get(selectedPlanPos).ID));
                    }
                } else {
                    validateCouponCode(etCouponCode.getText().toString(), true);
                }
            }
        });

        btCancelSubscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCancelSubscriptionDialog();
            }
        });

        tvApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateCouponCode(etCouponCode.getText().toString(), false);
            }
        });

        if (!Utility.isTelevision()) {
            String id = getResources().getResourceEntryName(findViewById(R.id.adViewSubscribeFrag).getId());
            showBannerAd(id);
            showAd("bNavSubscribe");
        }
    }

    private void showCancelSubscriptionDialog() {
        new AlertDialog.Builder(PremiumActivity.this)
                .setTitle(getString(R.string.note))
                .setMessage(getString(R.string.cancel_subscription_message))
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    callCancelSubscriptionApi();
                }).setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> dialogInterface.dismiss())
                .setNegativeButton(android.R.string.no, null)
                .setIcon(PremiumActivity.this.getResources().getDrawable(R.drawable.ic_new_alert))
                .show();
    }

    private void setClickableTexts() {
        SpannableString spannableString =
                new SpannableString(this.getResources().getString(R.string.subject_to_tc));
        spannableString.setSpan(
                new CustomClickableSpan(this, 0),
                12,
                30,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        spannableString.setSpan(
                new CustomClickableSpan(this, 1),
                33,
                47,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        /*TextView tvLabelTermsNPolicyLine1 = findViewById(R.id.tvLabelTermsNPolicyLine1);
        tvLabelTermsNPolicyLine1.setText(spannableString);
        tvLabelTermsNPolicyLine1.setMovementMethod(LinkMovementMethod.getInstance());
        tvLabelTermsNPolicyLine1.setHighlightColor(Color.TRANSPARENT);*/
    }

    private void startManageSubscriptions() {
        progressbar.setVisibility(View.VISIBLE);
        String userId = LocalStorage.getUserId();
        try {
            Map<String, String> params = new HashMap<>();
            params.put("user_id", userId);
            APIRequest apiRequest = new APIRequest(Url.FETCH_SUBSCRIPTIONS_URL, Request.Method.POST, params, null, this);
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    // progressbar.setVisibility(View.GONE);
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
                                    ivQrcode.setImageBitmap(encodeAsBitmap(subscriptionsUrl));
                                }
                            }
                        } catch (JSONException | WriterException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (Exception e) {

        }
    }

    public void showAdPremium(String id) {
        /*if (getActivity() != null && getActivity() instanceof HomeActivity) {
            homeActivity = (HomeActivity) getActivity();
            if (homeActivity != null) {
                homeActivity.showAd(id);
            }
        }*/
        showAd(id);
    }

    private void checkActiveCoupons() {
        try {
            Map<String, String> params = new HashMap<>();
            APIRequest apiRequest = new APIRequest(Url.SUBSCRIPTION_ACTIVE_COUPON, Request.Method.GET, params, null, this);
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
                                    boolean isActive = jsonObjectResponse.getBoolean("msg");
                                    if (isActive) {
                                        llCoupon.setVisibility(View.VISIBLE);
                                    } else {
                                        llCoupon.setVisibility(View.GONE);
                                    }
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

    private void getSubscriptionPlans() {
        progressbar.setVisibility(View.VISIBLE);
        subscriptionPlanDataArrayList.clear();
        try {
            Map<String, String> params = new HashMap<>();
            APIRequest apiRequest = new APIRequest(Url.GET_SUBSCRIPTION_PLANS + "&calling_code=" + LocalStorage.getUserData().countryCode, Request.Method.GET, params, null, this);
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    progressbar.setVisibility(View.GONE);
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
                                    JSONArray jsonArrayMsg = jsonObjectResponse.getJSONArray("msg");
                                    for (int i = 0; i < jsonArrayMsg.length(); i++) {
                                        SubscriptionPlanData subscriptionPlanData = new SubscriptionPlanData(jsonArrayMsg.getJSONObject(i));
                                        subscriptionPlanDataArrayList.add(subscriptionPlanData);
                                    }
                                    if (subscriptionPlanDataArrayList.size() > 0) {
                                        setLayoutSubscriptionPlan();
                                    } else {
                                        tvActiveSubscription.setText(getString(R.string.no_plans_message));
                                        tvActiveSubscription.setVisibility(View.VISIBLE);
                                    }
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

    private void setLayoutSubscriptionPlan() {
        llPayment.setVisibility(View.VISIBLE);
        glPlans.removeAllViewsInLayout();
        llSubscriptionPlanList = new LinearLayout[subscriptionPlanDataArrayList.size()];
        tvRupeesList = new ArrayList<>();
        tvDiscountList = new ArrayList<>();
        tvDurationList = new ArrayList<>();
        for (int i = 0; i < subscriptionPlanDataArrayList.size(); i++) {
            llSubscriptionPlanList[i] = new LinearLayout(this);
            llSubscriptionPlanList[i].setOrientation(LinearLayout.VERTICAL);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams(
                    GridLayout.spec(GridLayout.UNDEFINED, 1f),
                    GridLayout.spec(GridLayout.UNDEFINED, 1f)
            );
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.width = 0;
            params.setMargins(20, 20, 20, 20);
            llSubscriptionPlanList[i].setLayoutParams(params);
            llSubscriptionPlanList[i].setPadding(0, 40, 0, 40);
            llSubscriptionPlanList[i].setBackground(getDrawable(R.drawable.round_unselected_payment));
            llSubscriptionPlanList[i].setGravity(Gravity.CENTER);

            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            TextView tvDiscount = new TextView(this);
            tvDiscount.setTextColor(getResources().getColor(R.color.colorWhite));
            tvDiscount.setTextAppearance(R.style.HeaderSemiBoldStyle);
            tvDiscount.setLayoutParams(param);
            tvDiscount.setGravity(Gravity.CENTER);
            tvDiscount.setVisibility(View.GONE);
            tvDiscountList.add(i, tvDiscount);

            TextView tvRupees = new TextView(this);
            tvRupees.setText(LocalStorage.getUserData().currency + " " + subscriptionPlanDataArrayList.get(i).subsAmount);
            tvRupees.setTextColor(getResources().getColor(R.color.colorWhite));
            tvRupees.setTextAppearance(R.style.HeaderSemiBoldStyle);
            tvRupees.setLayoutParams(param);
            tvRupees.setGravity(Gravity.CENTER);
            tvRupeesList.add(i, tvRupees);

            TextView tvDuration = new TextView(this);
            if (subscriptionPlanDataArrayList.get(i).durationType.equalsIgnoreCase("monthly")) {
                tvDuration.setText(subscriptionPlanDataArrayList.get(i).duration + " month");
            } else if (subscriptionPlanDataArrayList.get(i).durationType.equalsIgnoreCase("yearly")) {
                tvDuration.setText(subscriptionPlanDataArrayList.get(i).duration + " year");
            }

            tvDuration.setTextColor(getResources().getColor(R.color.colorWhite));
            tvDuration.setTextAppearance(R.style.HeaderRegularStyle);
            tvDuration.setLayoutParams(param);
            tvDuration.setGravity(Gravity.CENTER);
            tvDurationList.add(i, tvDuration);

            llSubscriptionPlanList[i].addView(tvDiscount);
            llSubscriptionPlanList[i].addView(tvRupees);
            llSubscriptionPlanList[i].addView(tvDuration);

            if (i == 0) {
                llSubscriptionPlanList[i].setBackground(getDrawable(R.drawable.round_selected_payment));
                tvRupees.setTextColor(getResources().getColor(R.color.colorAccent));
                tvDiscount.setTextColor(getResources().getColor(R.color.colorAccent));
                tvDuration.setTextColor(getResources().getColor(R.color.colorAccent));
                selectedPlanPos = i;
            }

            glPlans.addView(llSubscriptionPlanList[i]);

            int finalI = i;
            llSubscriptionPlanList[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int j = 0; j < 3; j++) {
                        if (j == finalI) {
                            llSubscriptionPlanList[j].setBackground(getDrawable(R.drawable.round_selected_payment));
                            tvRupeesList.get(j).setTextColor(getResources().getColor(R.color.colorAccent));
                            tvDiscountList.get(j).setTextColor(getResources().getColor(R.color.colorAccent));
                            tvDurationList.get(j).setTextColor(getResources().getColor(R.color.colorAccent));
                            selectedPlanPos = j;
                        } else {
                            llSubscriptionPlanList[j].setBackground(getDrawable(R.drawable.round_unselected_payment));
                            tvRupeesList.get(j).setTextColor(getResources().getColor(R.color.colorWhite));
                            tvDiscountList.get(j).setTextColor(getResources().getColor(R.color.colorWhite));
                            tvDurationList.get(j).setTextColor(getResources().getColor(R.color.colorWhite));
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (playerPremium != null && resumePlayer) {
            playerPremium.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
            playerPremium.setPlayWhenReady(true);
            playerView.setPlayer(playerPremium);
        }

        if (Utility.isTelevision()) {
            if (isFirstLoading) {
                callSubscriptionApi(LocalStorage.getUserId());
            }
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    if (LocalStorage.isUserLoggedIn()) {
                        callSubscriptionApi(LocalStorage.getUserId());
                    }
                }
            }, 15000, 15000);
        } else {
            callSubscriptionApi(LocalStorage.getUserId());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (playerPremium != null) {
            playerPremium.setPlayWhenReady(false);
            resumePlayer = true;
            playerView.setPlayer(null);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        closePlayer();
    }

    public void showBannerAd(String id) {
        String adType = Utility.getBannerAdType(id, this);
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
        List<AdMobData> list = LocalStorage.getBannerAdMobList(LocalStorage.KEY_BANNER_AD_MOB, this);
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).id.equals(id)) {
                    if (list.get(i).feedContentObjectData != null) {
                        int finalI = i;
                        if (list.get(i).feedContentObjectData.mediaType.equalsIgnoreCase(FeedContentData.MEDIA_TYPE_IMAGE)) {
                            String url = list.get(i).feedContentObjectData.adFieldsData.attachment;
                            Glide.with(this)
                                    .load(url)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(ivCustomAd);
                            ivCustomAd.setVisibility(View.VISIBLE);
                            playerView.setVisibility(View.GONE);
                            APIMethods.addEvent(this, Constant.VIEW, list.get(i).feedContentObjectData.postId, Constant.BANNER, id);

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
                            APIMethods.addEvent(this, Constant.VIEW, list.get(i).feedContentObjectData.postId, Constant.BANNER, id);

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
                                        if (playerPremium != null && playerPremium.isPlaying()) {
                                            ivVolumeUp.setVisibility(View.GONE);
                                            ivVolumeOff.setVisibility(View.VISIBLE);
                                            playerPremium.setVolume(0.0f);
                                        }
                                    } catch (Exception e) {

                                    }
                                }
                            });
                            ivVolumeOff.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {
                                        if (playerPremium != null && playerPremium.isPlaying()) {
                                            ivVolumeOff.setVisibility(View.GONE);
                                            ivVolumeUp.setVisibility(View.VISIBLE);
                                            playerPremium.setVolume(1.0f);
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
        APIMethods.addEvent(this, Constant.CLICK, adFieldsData.postId, location, subLocation);
        switch (adFieldsData.adType) {
            case FeedContentData.AD_TYPE_IN_APP:
                finish();
                if (adFieldsData.contentType.equalsIgnoreCase(Constant.PAGES)) {
                    HomeActivity.getInstance().redirectToPage(adFieldsData.pageType);
                } else {
                    HomeActivity.getInstance().getMediaData(String.valueOf(adFieldsData.contentId), adFieldsData.contentType);
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
                startActivity(new Intent(this, BrowserActivity.class).putExtra("title", "").putExtra("posturl", adFieldsData.externalUrl));
                break;
        }
    }

    private void playVideoAd(String url) {
        playerView.setUseController(false);
        playerView.requestFocus();

        Uri uri = Uri.parse(url);
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, getApplicationContext().getPackageName(), bandwidthMeter);
        MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(uri));

        playerPremium = new ExoPlayer.Builder(PremiumActivity.this).build();
        playerPremium.addListener(new Player.Listener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == Player.STATE_ENDED) {
                    playerPremium.seekTo(0);
                }
            }

            @Override
            public void onPlayerError(PlaybackException error) {

            }
        });
        playerPremium.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT);
        playerPremium.prepare(mediaSource);
        playerPremium.setPlayWhenReady(true);

        /*
          Volume is set to 0 as per requirement
          Use as per the case
         */
        playerPremium.setVolume(0.0f);

        playerView.setPlayer(playerPremium);

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
        if (playerPremium != null) {
            playerPremium.setPlayWhenReady(false);
            playerPremium.stop();
            playerPremium.release();
            playerPremium = null;
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
        adapterSliderAd = new KRecyclerViewAdapter(this, adSliderObjectArrayList, new KRecyclerViewHolderCallBack() {
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

        layoutManager = new CustomLinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvSliderAd.setLayoutManager(layoutManager);
        rvSliderAd.setNestedScrollingEnabled(false);
        rvSliderAd.setOnFlingListener(null);
        rvSliderAd.setAdapter(adapterSliderAd);

        // Dot indicator for banner
        final int radius = getResources().getDimensionPixelSize(R.dimen.dot_radius);
        final int dotsHeight = getResources().getDimensionPixelSize(R.dimen.dot_height);
        final int color = ContextCompat.getColor(this, R.color.colorAccent);
        final int inActiveColor = ContextCompat.getColor(this, R.color.game_gray);
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

    public void paytmPrepayment(String subscriptionId) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("subscription_id", subscriptionId);
            params.put("payment_mode", "paytm");
            params.put("coupon_id", couponId);

            APIRequest apiRequest = new APIRequest(Url.SUBSCRIPTION_PAYMENT, Request.Method.POST, params, null, this);
            apiRequest.showLoader = true;
            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    if (error == null) {
                        handlePaytmPrepaymentResponse(response, error);
                    } else {
                        Utility.showError(error.toString(), PremiumActivity.this);
                    }
                }
            });
        } catch (Exception e) {
            Utility.showError(getString(R.string.failed_to_initiate_payment), PremiumActivity.this);
        }
    }

    private void handlePaytmPrepaymentResponse(@Nullable String response, @Nullable Exception error) {
        try {
            if (error != null) {
                Utility.showError(error.getMessage(), PremiumActivity.this);
            } else {
                if (response != null) {
                    JSONObject jsonObjectResponse = new JSONObject(response);
                    String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                    if (type.equalsIgnoreCase("error")) {
                        String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                        Utility.showError(message, this);
                    } else if (type.equalsIgnoreCase("ok")) {
                        JSONObject object = jsonObjectResponse.getJSONObject("msg");
                        boolean status = object.has("status") ? object.getBoolean("status") : false;
                        if (status) {
                            revenue = 0;
                            startActivity(new Intent(PremiumActivity.this, SubscriptionSuccessActivity.class).putExtra(REVENUE, revenue));
                            finish();
                        } else {
                            this.initiatePaytm(object);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            Utility.showError(e.toString(), this);
        }
    }

    private void initiatePaytm(JSONObject object) {
        JSONObject paytmParams = null;
        try {
            if (!object.has("paytmParams")) {
                Utility.showError(getString(R.string.order_request_parameters_not_received), PremiumActivity.this);
            } else {
                paytmParams = object.getJSONObject("paytmParams");
                String ORDER_ID = paytmParams.getString("orderid");
                String MID = paytmParams.getString("mid");
                String TXN_TOKEN = paytmParams.getString("txnToken");
                String AMOUNT = paytmParams.getString("amount");
                revenue = Integer.parseInt(AMOUNT);
                String CALLBACK_URL = paytmParams.getString("CALLBACK_URL");
                subscriptionIdPayment = paytmParams.has("subscription_id") ? paytmParams.getString("subscription_id") : "";
                couponIdPayment = paytmParams.has("coupon_id") ? paytmParams.getString("coupon_id") : "";

                PaytmOrder paytmOrder = new PaytmOrder(ORDER_ID, MID, TXN_TOKEN, AMOUNT, CALLBACK_URL);

                TransactionManager transactionManager = new TransactionManager(paytmOrder, new PaytmPaymentTransactionCallback() {
                    @Override
                    public void onTransactionResponse(@Nullable Bundle bundle) {
                        if (bundle != null) {
                            if (bundle.getString("STATUS").equalsIgnoreCase("TXN_SUCCESS")) {
                                // TRANSACTION SUCCESS
                                paytmPostPaymentRequest(bundle.getString("ORDERID"), bundle.getString("TXNID"));
                            } else {
                                // TRANSACTION FAILED
                                subscriptionIdPayment = "";
                                couponIdPayment = "";
                                Log.d("avinash", "Paytm onTransactionResponse");
                                Utility.showError((bundle.containsKey("RESPMSG") ? bundle.getString("RESPMSG") : getString(R.string.transaction_failed)), PremiumActivity.this);
                            }
                        }
                    }

                    @Override
                    public void networkNotAvailable() {
                        subscriptionIdPayment = "";
                        couponIdPayment = "";
                        Utility.showError(getString(R.string.network_connection_error), PremiumActivity.this);
                    }

                    @Override
                    public void onErrorProceed(String s) {
                        subscriptionIdPayment = "";
                        couponIdPayment = "";
                        Utility.showError(getString(R.string.label_error) + " " + s, PremiumActivity.this);
                    }

                    @Override
                    public void clientAuthenticationFailed(String s) {
                        subscriptionIdPayment = "";
                        couponIdPayment = "";
                        Utility.showError(getString(R.string.client_authentication_failed) + s, PremiumActivity.this);
                    }

                    @Override
                    public void someUIErrorOccurred(String s) {
                        subscriptionIdPayment = "";
                        couponIdPayment = "";
                        Utility.showError(getString(R.string.ui_errror) + s, PremiumActivity.this);
                    }

                    @Override
                    public void onErrorLoadingWebPage(int i, String s, String s1) {
                        subscriptionIdPayment = "";
                        couponIdPayment = "";
                        Utility.showError(getString(R.string.unable_to_load_webpage) + s, PremiumActivity.this);
                    }

                    @Override
                    public void onBackPressedCancelTransaction() {
                        subscriptionIdPayment = "";
                        couponIdPayment = "";
                        Utility.showError(getString(R.string.transaction_cancelled), PremiumActivity.this);
                    }

                    @Override
                    public void onTransactionCancel(String s, Bundle bundle) {
                        subscriptionIdPayment = "";
                        couponIdPayment = "";
                        Utility.showError(getString(R.string.transaction_cancelled) + bundle.toString(), PremiumActivity.this);
                    }
                });

                transactionManager.startTransaction(this, PAYTM_REQUEST_CODE);
            }
        } catch (Exception e) {
            Log.d("avinash", "Exception " + e.getMessage());
        }
    }

    private void paytmPostPaymentRequest(String ORDERID, String PAYMENTID) {
        Log.d("avinash", "paytmPostPaymentRequest");
        progressbar.setVisibility(View.VISIBLE);
        try {
            Map<String, String> params = new HashMap<>();
            params.put("payment_mode", "paytm");
            params.put("order_id", ORDERID);
            params.put("payment_id", PAYMENTID);
            params.put("subscription_id", subscriptionIdPayment);
            params.put("coupon_id", couponIdPayment);

            APIRequest apiRequest = new APIRequest(Url.SUBSCRIPTION_STATUS_UPDATE, Request.Method.POST, params, null, this);
            apiRequest.showLoader = true;
            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    progressbar.setVisibility(View.GONE);
                    try {
                        JSONObject jsonObjectResponse = new JSONObject(response);
                        String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                        if (type.equalsIgnoreCase("error")) {

                            String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                            Utility.showError(message, PremiumActivity.this);

                        } else if (type.equalsIgnoreCase("ok") && jsonObjectResponse.has("msg")) {
                            JSONObject msg = jsonObjectResponse.getJSONObject("msg");
                            if (msg.getString("status").equalsIgnoreCase("success")) {
                                startActivity(new Intent(PremiumActivity.this, SubscriptionSuccessActivity.class).putExtra(REVENUE, revenue));
                                finish();
                            } else {
                                Utility.showError(msg.getString("display_message"), PremiumActivity.this);
                            }
                        }
                    } catch (Exception e) {

                    }
                }
            });
        } catch (Exception e) {
            Utility.showError(getString(R.string.failed_to_complete_payment), PremiumActivity.this);
        }
    }

    // RAZORPAY INIT ORDER
    public void razorpayPrepayment(String subscriptionId) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("subscription_id", subscriptionId);
            params.put("payment_mode", "razorpay");
            params.put("coupon_id", couponId);

            APIRequest apiRequest = new APIRequest(Url.SUBSCRIPTION_PAYMENT, Request.Method.POST, params, null, this);
            apiRequest.showLoader = true;
            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    if (error == null) {
                        handleRazorpayPrepaymentResponse(response, error);
                    } else {
                        Utility.showError(error.toString(), PremiumActivity.this);
                    }
                }
            });
        } catch (Exception e) {
            Utility.showError(getString(R.string.failed_to_initiate_payment), PremiumActivity.this);
        }
    }

    // RAZORPAY INIT ORDER RESPONSE
    private void handleRazorpayPrepaymentResponse(@Nullable String response, @Nullable Exception error) {
        try {
            if (error != null) {
                Utility.showError(error.getMessage(), PremiumActivity.this);
            } else {
                if (response != null) {
                    JSONObject jsonObjectResponse = new JSONObject(response);
                    String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                    if (type.equalsIgnoreCase("error")) {
                        String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                        //Utility.showError(message,this);
                    } else if (type.equalsIgnoreCase("ok")) {
                        /// new flow checking to be done here
                        JSONObject object = jsonObjectResponse.getJSONObject("msg");
                        boolean status = object.has("status") ? object.getBoolean("status") : false;
                        if (status) {
                            revenue = 0;
                            startActivity(new Intent(PremiumActivity.this, SubscriptionSuccessActivity.class).putExtra(REVENUE, revenue));
                            finish();
                        } else {
                            //flow for coins/rupees
                            this.initiateRazorpay(object);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            Utility.showError(e.toString(), this);
        }
    }

    private String itap_order_id = "";

    public void initiateRazorpay(JSONObject object) {
        final Activity activity = this;
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
            options.put("description", "");
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("currency", "INR");
            options.put("amount", object.getString("amount"));
            revenue = Integer.parseInt(object.getString("amount"));
            options.put("order_id", object.getString("razorpay_order_id"));
            this.itap_order_id = object.getString("razorpay_order_id");
            subscriptionIdPayment = object.has("subscription_id") ? object.getString("subscription_id") : "";
            couponIdPayment = object.has("coupon_id") ? object.getString("coupon_id") : "";

            instance.open(activity, options);
        } catch (Exception e) {
            Toast.makeText(activity, getString(R.string.error_in_payment) + e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
            e.printStackTrace();
        }
    }

    @Override
    public void onPaymentSuccess(String response) {
        if (response.isEmpty()) {
            Utility.showError(getString(R.string.razorpay_payment_id_is_null), this);
            this.itap_order_id = "";
            subscriptionIdPayment = "";
            couponIdPayment = "";
        } else {
            razorpayPostPayment(this.itap_order_id, response);
        }
    }

    private void razorpayPostPayment(String itap_order_id, String razorpay_order_id) {
        progressbar.setVisibility(View.VISIBLE);
        try {
            Map<String, String> params = new HashMap<>();
            params.put("payment_mode", "razorpay");
            params.put("order_id", itap_order_id);
            params.put("payment_id", razorpay_order_id);
            params.put("subscription_id", subscriptionIdPayment);
            params.put("coupon_id", couponIdPayment);

            APIRequest apiRequest = new APIRequest(Url.SUBSCRIPTION_STATUS_UPDATE, Request.Method.POST, params, null, this);
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    progressbar.setVisibility(View.GONE);
                    try {
                        if (error == null) {
                            JSONObject jsonObjectResponse = new JSONObject(response);
                            String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";

                            if (type.equalsIgnoreCase("error")) {

                                String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                                Utility.showError(message, PremiumActivity.this);

                            } else if (type.equalsIgnoreCase("ok") && jsonObjectResponse.has("msg")) {
                                JSONObject msg = jsonObjectResponse.getJSONObject("msg");
                                if (msg.getString("status").equalsIgnoreCase("success")) {
                                    startActivity(new Intent(PremiumActivity.this, SubscriptionSuccessActivity.class).putExtra(REVENUE, revenue));
                                    finish();
                                } else {
                                    Utility.showError(msg.getString("display_message"), PremiumActivity.this);
                                }
                            }
                        } else {
                            Utility.showError(error.toString(), PremiumActivity.this);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }
            });
        } catch (Exception e) {
            Utility.showError(getString(R.string.failed_to_complete_payment), PremiumActivity.this);
        }
    }

    // STRIPE INIT ORDER
    public void stripePrepayment(String subscriptionId) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("subscription_id", subscriptionId);
            params.put("payment_mode", "stripe");
            params.put("coupon_id", couponId);

            APIRequest apiRequest = new APIRequest(Url.SUBSCRIPTION_PAYMENT, Request.Method.POST, params, null, this);
            apiRequest.showLoader = true;
            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    if (error == null) {
                        handleStripePrepaymentResponse(response, error);
                    } else {
                        Utility.showError(error.toString(), PremiumActivity.this);
                    }
                }
            });
        } catch (Exception e) {
            Utility.showError(getString(R.string.failed_to_initiate_payment), PremiumActivity.this);
        }
    }

    // STRIPE INIT ORDER RESPONSE
    private void handleStripePrepaymentResponse(@Nullable String response, @Nullable Exception error) {
        try {
            if (error != null) {
                Utility.showError(error.getMessage(), PremiumActivity.this);
            } else {
                if (response != null) {
                    JSONObject jsonObjectResponse = new JSONObject(response);
                    String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                    if (type.equalsIgnoreCase("error")) {
                        String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                        //Utility.showError(message,this);
                    } else if (type.equalsIgnoreCase("ok")) {
                        /// new flow checking to be done here
                        JSONObject object = jsonObjectResponse.getJSONObject("msg");
                        boolean status = object.has("status") ? object.getBoolean("status") : false;
                        if (status) {
                            revenue = 0;
                            startActivity(new Intent(PremiumActivity.this, SubscriptionSuccessActivity.class).putExtra(REVENUE, revenue));
                            finish();
                        } else {
                            //flow for coins/rupees
                            String url = object.has("url") ? object.getString("url") : "";
                            if (!url.isEmpty()) {
                                startActivityForResult(new Intent(PremiumActivity.this, BrowserActivity.class).putExtra("title", getString(R.string.stripe)).putExtra("posturl", url), PREMIUM_REQUEST_CODE);
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            Utility.showError(e.toString(), this);
        }
    }

    @Override
    public void onPaymentError(int code, String response) {
        this.itap_order_id = "";
        subscriptionIdPayment = "";
        couponIdPayment = "";
        switch (code) {
            case Checkout.NETWORK_ERROR:
                Toast.makeText(getApplicationContext(), getString(R.string.there_was_a_network_error), Toast.LENGTH_LONG).show();
                break;

            case Checkout.PAYMENT_CANCELED:
            case Checkout.INVALID_OPTIONS:
                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                break;

            case Checkout.TLS_ERROR:
                Toast.makeText(getApplicationContext(), getString(R.string.device_does_not_have), Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    public void onExternalWalletSelected(String s, PaymentData paymentData) {
        if (s.equals("paytm")) {
            //handle paytm payment
            paytmPrepayment(String.valueOf(subscriptionPlanDataArrayList.get(selectedPlanPos).ID));
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    private void validateCouponCode(String couponCode, boolean isPayment) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("coupon_code", couponCode);

            APIRequest apiRequest = new APIRequest(Url.VALIDATE_COUPON_CODE, Request.Method.POST, params, null, this);
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    try {
                        if (error == null) {
                            JSONObject jsonObjectResponse = new JSONObject(response);
                            String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";

                            if (type.equalsIgnoreCase("error")) {

                                String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                                AlertUtils.showAlert(Constant.ALERT_TITLE, message, getString(R.string.ok), PremiumActivity.this, isPositiveAction -> {
                                    if (getIntent() != null) {
                                        finish();
                                        startActivity(getIntent());
                                    }
                                });

                            } else if (type.equalsIgnoreCase("ok") && jsonObjectResponse.has("msg")) {
                                JSONObject msg = jsonObjectResponse.getJSONObject("msg");
                                if (msg.has("_id")) {
                                    JSONObject _id = msg.getJSONObject("_id");
                                    couponId = _id.getString("$oid");
                                    if (isPayment) {
                                        if (LocalStorage.getUserData().countryCode.equalsIgnoreCase(Constant.INDIA)) {
                                            selectedRadioButton = rgPayment.findViewById(rgPayment.getCheckedRadioButtonId());
                                            if (selectedRadioButton.getText().toString().equalsIgnoreCase("Paytm")) {
                                                paytmPrepayment(String.valueOf(subscriptionPlanDataArrayList.get(selectedPlanPos).ID));
                                            } else {
                                                razorpayPrepayment(String.valueOf(subscriptionPlanDataArrayList.get(selectedPlanPos).ID));
                                            }
                                        } else {
                                            selectedRadioButton = rgOtherCountryPayment.findViewById(rgOtherCountryPayment.getCheckedRadioButtonId());
                                            stripePrepayment(String.valueOf(subscriptionPlanDataArrayList.get(selectedPlanPos).ID));
                                        }
                                    }
                                }
                                if (msg.has("discount")) {
                                    handleDiscountData(msg.getString("discount"));
                                }
                            }
                        } else {
                            AlertUtils.showAlert(Constant.ALERT_TITLE, error.toString(), getString(R.string.ok), PremiumActivity.this, isPositiveAction -> {
                                if (getIntent() != null) {
                                    finish();
                                    startActivity(getIntent());
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }
            });
        } catch (Exception e) {

        }
    }

    private void handleDiscountData(String discount) {
        for (int i = 0; i < subscriptionPlanDataArrayList.size(); i++) {
            for (int j = 0; j < subscriptionPlanDataArrayList.get(i).discountPlanDataArrayList.size(); j++) {
                if (subscriptionPlanDataArrayList.get(i).discountPlanDataArrayList.get(j).discount.equalsIgnoreCase(discount)) {
                    tvRupeesList.get(i).setPaintFlags(tvRupeesList.get(i).getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    tvRupeesList.get(i).setTextAppearance(R.style.Subtitle_2_RegularStyle);
                    tvDiscountList.get(i).setText(" Rs. " + subscriptionPlanDataArrayList.get(i).discountPlanDataArrayList.get(j).amount);
                    tvDiscountList.get(i).setVisibility(View.VISIBLE);
                }
            }
        }
    }

    public void callSubscriptionApi(String userId) {
        if (isFirstLoading && Utility.isTelevision()) {
            progressbar.setVisibility(View.VISIBLE);
            isFirstLoading = false;
        }
        try {
            Map<String, String> params = new HashMap<>();
            params.put("user_id", userId);
            APIRequest apiRequest = new APIRequest(Url.GET_SUBSCRIPTION_DETAILS, Request.Method.POST, params, null, this);
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, new APIResponse() {
                @SuppressLint("StringFormatInvalid")
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    progressbar.setVisibility(View.GONE);
                    JSONObject jsonObjectResponse = null;
                    try {
                        Log.i("subscriptionresponse", response);
                        jsonObjectResponse = new JSONObject(response);
                        String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                        if (type.equalsIgnoreCase("error")) {
                            LocalStorage.setUserPremium(false);
                            String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                            if (Utility.isTelevision()) {
                                nsvSubscriptionMobile.setVisibility(View.GONE);
                                llSubscriptionTv.setVisibility(View.VISIBLE);
                            } else {
                                nsvSubscriptionMobile.setVisibility(View.VISIBLE);
                                llSubscriptionTv.setVisibility(View.GONE);
                                getSubscriptionPlans();
                                btCancelSubscription.setVisibility(View.GONE);
                            }
                        } else if (type.equalsIgnoreCase("ok")) {
                            JSONArray okResponse = jsonObjectResponse.has("msg") ? jsonObjectResponse.getJSONArray("msg") : new JSONArray();
                            for (int i = 0; i < okResponse.length(); i++) {
                                JSONObject jsonObject = okResponse.getJSONObject(i);
                                if (jsonObject.getString("status").equals(STATUS_ACTIVE)) {
                                    LocalStorage.setUserPremium(true);
                                    LocalStorage.setSubStartDate(jsonObject.getString("start_date"));
                                    LocalStorage.setSubEndDate(jsonObject.getString("expiry_date"));
                                    String date = Utility.formatDate("yyyy-MM-dd hh:mm:ss", "dd MMM yyyy", jsonObject.getString("expiry_date"));
                                    tvActiveSubscription.setText(getString(R.string.active_subscription_until) + date);
                                    tvActiveSubscription.setVisibility(View.VISIBLE);
                                    nsvSubscriptionMobile.setVisibility(View.VISIBLE);
                                    llSubscriptionTv.setVisibility(View.GONE);
                                    if (Utility.isTelevision()) {
                                        btGoToPremium.setVisibility(View.VISIBLE);
                                        btGoToPremium.requestFocus();
                                    } else {
                                        btGoToPremium.setVisibility(View.GONE);
                                    }
                                    if (LocalStorage.getUserData().countryCode.equalsIgnoreCase(Constant.INDIA)) {
                                        btCancelSubscription.setVisibility(View.GONE);
                                        if (!Utility.isTelevision()) {
                                            getSubscriptionPlans();
                                        }
                                    } else {
                                        String stripeCancelled = jsonObject.has("stripe_cancelled") ? jsonObject.getString("stripe_cancelled") : "";
                                        if (stripeCancelled.equalsIgnoreCase("1")) {
                                            tvActiveSubscription.setText(tvActiveSubscription.getText() + "\n\n" + getString(R.string.can_not_resubscribe_message) + date);
                                            btCancelSubscription.setVisibility(View.GONE);
                                        } else {
                                            btCancelSubscription.setVisibility(View.VISIBLE);
                                        }
                                    }
                                } else {
                                    if (Utility.isTelevision()) {
                                        nsvSubscriptionMobile.setVisibility(View.GONE);
                                        llSubscriptionTv.setVisibility(View.VISIBLE);
                                    } else {
                                        nsvSubscriptionMobile.setVisibility(View.VISIBLE);
                                        llSubscriptionTv.setVisibility(View.GONE);
                                        getSubscriptionPlans();
                                    }
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void callCancelSubscriptionApi() {
        try {
            Map<String, String> params = new HashMap<>();
            APIRequest apiRequest = new APIRequest(Url.CANCEL_SUBSCRIPTION, Request.Method.POST, params, null, this);
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    JSONObject jsonObjectResponse = null;
                    try {
                        jsonObjectResponse = new JSONObject(response);
                        String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                        if (type.equalsIgnoreCase("error")) {
                            String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                        } else if (type.equalsIgnoreCase("ok")) {
                            String okResponse = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : "";
                            AlertUtils.showAlert("", okResponse, "OK", PremiumActivity.this, new AlertUtils.AlertClickHandler() {
                                @Override
                                public void alertButtonAction(boolean isPositiveAction) {
                                    if (isPositiveAction) {
                                        callSubscriptionApi(LocalStorage.getUserId());
                                    }
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYTM_REQUEST_CODE) {
            if (data != null && resultCode == RESULT_OK) {
                try {
                    JSONObject json = new JSONObject(data.getStringExtra("response"));
                    Log.i("", json.toString());
                    if (json.getString("STATUS").equalsIgnoreCase("TXN_SUCCESS")) {
                        // TRANSACTION SUCCESS
                        paytmPostPaymentRequest(json.getString("ORDERID"), json.getString("TXNID"));
                    } else {
                        // TRANSACTION FAILED
                        subscriptionIdPayment = "";
                        couponIdPayment = "";
                        Log.d("avinash", "Paytm onTransactionResponse");
                        Utility.showError((json.has("RESPMSG") ? json.getString("RESPMSG") : getString(R.string.transaction_failed)), PremiumActivity.this);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                subscriptionIdPayment = "";
                couponIdPayment = "";
                Utility.showError(getString(R.string.transaction_cancelled), PremiumActivity.this);
            }
        }

        if (requestCode == PREMIUM_REQUEST_CODE) {
            if (data != null && resultCode == RESULT_OK) {
                try {
                    boolean status = data.getBooleanExtra("status", false);
                    if (status) {
                        revenue = Integer.parseInt(subscriptionPlanDataArrayList.get(selectedPlanPos).subsAmount);
                        startActivity(new Intent(PremiumActivity.this, SubscriptionSuccessActivity.class));
                        finish();
                    } else {
                        Utility.showError(getString(R.string.transaction_failed), PremiumActivity.this);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Utility.showError(getString(R.string.transaction_failed), PremiumActivity.this);
            }
        }
    }

    Bitmap encodeAsBitmap(String str) throws WriterException {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix bitMatrix = writer.encode(str, BarcodeFormat.QR_CODE, 400, 400);

        int w = bitMatrix.getWidth();
        int h = bitMatrix.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                pixels[y * w + x] = bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
        return bitmap;
    }

    public void goToPremium(View view) {
        finish();
        if (Utility.isTelevision()) {
            HomeActivity.getInstance().goToPremium();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
