package com.app.itaptv.activity;

import androidx.annotation.Nullable;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.app.itaptv.API.APIManager;
import com.app.itaptv.API.APIRequest;
import com.app.itaptv.API.APIResponse;
import com.app.itaptv.API.Url;
import com.app.itaptv.R;
import com.app.itaptv.structure.ParticipantData;
import com.app.itaptv.structure.TournamentData;
import com.app.itaptv.utils.AlertUtils;
import com.app.itaptv.utils.LocalStorage;
import com.app.itaptv.utils.Log;
import com.app.itaptv.utils.Utility;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TournamentSummaryActivity extends BaseActivity implements PaymentResultListener {

    public static String CONTENT_DATA = "contentData";
    public static String MESSAGE = "message";
    public static String TAG = "TournamentSummaryActivity";
    public static final String REQUEST_CODE_EXTRA = "requestCodeExtra";
    public static int REQUEST_CODE_ESPORTS = 500;

    //FeedContentData feedContentData;
    String postId = "";
    String message = "";

    ProgressBar progressBar;
    View llprogressbar;
    LinearLayout llTeaser;
    ImageView ivTeaserImage;
    TextView tvTournamentName;
    TextView tvTournamentStartDate;
    TextView tvTournamentEndDate;
    TextView tvRegistrationStartDate;
    TextView tvRegistrationEndDate;
    TextView tvEntryFees;
    TextView tvOr;
    TextView tvCoinsForEntry;
    TextView tvFree;
    TextView tvPrize;
    TextView tvRegistrationReward;
    TextView tvTeamName;
    LinearLayout llTelegramLink;
    RelativeLayout rlRules;
    LinearLayout llRules;
    LinearLayout llParticipants;
    ImageView ivCollapsed;
    ImageView ivExpand;
    Button btnPayNow;
    TextView tvPaymentStatus;
    AppBarLayout appBarLayoutTeaser;
    TournamentData tournamentData;
    List<ParticipantData> participantDataList = new ArrayList<>();

    //payment
    LinearLayout paymentRoot;
    RelativeLayout rlPayTm, rlRazor, rlCoins;
    TextView tvCoins;

    String participantId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournament_summary);
        if (getIntent() != null) {
            String paymentMode = getIntent().getStringExtra("paymentMode");
            String participantId = getIntent().getStringExtra("participantId");
            if (paymentMode != null) {
                if (paymentMode.equalsIgnoreCase("razorpay")) {
                    if (participantId != null) {
                        razorpayPrepayment(participantId);
                    }
                } else if (paymentMode.equalsIgnoreCase("paytm")) {
                    if (participantId != null) {
                        paytmPrepayment(participantId);
                    }
                } else if (paymentMode.equalsIgnoreCase("coins")) {
                    if (participantId != null) {
                        coinsPrepayment(participantId);
                    }
                }
            }
            message = getIntent().getStringExtra(MESSAGE);
        }
        //feedContentData = LocalStorage.getEsportsFeedData();
        postId = LocalStorage.getValue(LocalStorage.KEY_ESPORTS_ID, "", String.class);
        init();
    }

    public void init() {
        progressBar = findViewById(R.id.progressBar);
        llprogressbar = findViewById(R.id.llprogressbar);
        progressBar.setVisibility(View.VISIBLE);
        llprogressbar.setVisibility(View.VISIBLE);
        llTeaser = findViewById(R.id.llTeaser);
        ivTeaserImage = findViewById(R.id.ivTeaserImage);
        tvTournamentName = findViewById(R.id.tvTournamentName);
        tvTournamentStartDate = findViewById(R.id.tvTournamentStartDate);
        tvTournamentEndDate = findViewById(R.id.tvTournamentEndDate);
        tvRegistrationStartDate = findViewById(R.id.tvRegistrationStartDate);
        tvRegistrationEndDate = findViewById(R.id.tvRegistrationEndDate);
        tvEntryFees = findViewById(R.id.tvEntryFees);
        tvOr = findViewById(R.id.tvOr);
        tvCoinsForEntry = findViewById(R.id.tvCoinsForEntry);
        tvFree = findViewById(R.id.tvFree);
        tvPrize = findViewById(R.id.tvPrize);
        tvRegistrationReward = findViewById(R.id.tvRegistrationReward);
        tvTeamName = findViewById(R.id.tvTeamName);
        llTelegramLink = findViewById(R.id.llTelegramLink);
        rlRules = findViewById(R.id.rlRules);
        llRules = findViewById(R.id.llRules);
        llParticipants = findViewById(R.id.llParticipants);
        ivCollapsed = findViewById(R.id.ivCollapsed);
        ivExpand = findViewById(R.id.ivExpand);
        btnPayNow = findViewById(R.id.btnPaynow);
        tvPaymentStatus = findViewById(R.id.tvPaymentStatus);
        appBarLayoutTeaser = findViewById(R.id.appBarLayout);

        llTeaser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playTeaser();
            }
        });

        llTelegramLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tournamentData != null) {
                    if (!tournamentData.telegram_link.isEmpty()) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tournamentData.telegram_link));
                        startActivity(intent);
                    }
                }
            }
        });

        rlRules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int visibility = llRules.getVisibility();
                if (visibility == 0) {
                    llRules.setVisibility(View.GONE);
                    ivExpand.setVisibility(View.GONE);
                    ivCollapsed.setVisibility(View.VISIBLE);
                } else {
                    llRules.setVisibility(View.VISIBLE);
                    ivExpand.setVisibility(View.VISIBLE);
                    ivCollapsed.setVisibility(View.GONE);
                }
            }
        });

        btnPayNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnPayNow.setVisibility(View.GONE);
                paymentRoot.setVisibility(View.VISIBLE);
            }
        });

        paymentRoot = findViewById(R.id.llPayment);
        rlRazor = findViewById(R.id.rlRazorPay);
        rlPayTm = findViewById(R.id.rlPayTm);
        rlCoins = findViewById(R.id.rlCoinsPay);
        tvCoins = findViewById(R.id.tvCoins);

        rlRazor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!participantDataList.isEmpty()) {
                    for (int i = 0; i < participantDataList.size(); i++) {
                        if (participantDataList.get(i).user_id.equals(LocalStorage.getUserId())) {
                            razorpayPrepayment(participantDataList.get(i).id);
                        }
                    }
                }
            }
        });

        rlPayTm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!participantDataList.isEmpty()) {
                    for (int i = 0; i < participantDataList.size(); i++) {
                        if (participantDataList.get(i).user_id.equals(LocalStorage.getUserId())) {
                            paytmPrepayment(participantDataList.get(i).id);
                        }
                    }
                }
            }
        });

        rlCoins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!participantDataList.isEmpty()) {
                    for (int i = 0; i < participantDataList.size(); i++) {
                        if (participantDataList.get(i).user_id.equals(LocalStorage.getUserId())) {
                            coinsPrepayment(participantDataList.get(i).id);
                        }
                    }
                }
            }
        });

        getUserRegisteredTournamentInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //getUserRegisteredTournamentInfo();
    }

    public void playTeaser() {
        HomeActivity.getInstance().pausePlayer();
        if (tournamentData != null) {
            if (!tournamentData.attachment.isEmpty()) {
                //startActivity(new Intent(this, ESportsPlayerActivity.class).putExtra(CONTENT_DATA, feedContentData));
                startActivityForResult(new Intent(this, TeaserActivity.class).putExtra(CONTENT_DATA, tournamentData.attachment).putExtra(REQUEST_CODE_EXTRA, REQUEST_CODE_ESPORTS), REQUEST_CODE_ESPORTS);
            }
        }
    }

    public void getUserRegisteredTournamentInfo() {
        try {
            progressBar.setVisibility(View.VISIBLE);
            llprogressbar.setVisibility(View.VISIBLE);
            Map<String, String> params = new HashMap<>();
            APIRequest apiRequest = new APIRequest(Url.GET_USER_REGISTERED_TOURNAMENT_INFO + "&tournament_id=" + postId,
                    Request.Method.GET, params, null, this);
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, (response, error, headers, statusCode) -> {
                progressBar.setVisibility(View.GONE);
                llprogressbar.setVisibility(View.GONE);
                handleUserRegisteredTournamentInfo(response, error);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleUserRegisteredTournamentInfo(@Nullable String response, @Nullable Exception error) {
        try {
            if (error != null) {
                showError(error.getMessage());
            } else {
                if (response != null) {
                    JSONObject jsonObjectResponse = new JSONObject(response);
                    String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                    if (type.equalsIgnoreCase("error")) {
                        String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                        showError(message);
                    } else if (type.equalsIgnoreCase("ok")) {
                        JSONObject jsonArrayMsg = jsonObjectResponse.getJSONObject("msg");
                        JSONObject jsonArrayTournament = jsonArrayMsg.getJSONObject("tournament");
                        tournamentData = new TournamentData(jsonArrayTournament);
                        Log.i("TAG", "" + tournamentData);
                        participantDataList.clear();
                        JSONArray jsonArrayParticipant = jsonArrayMsg.getJSONArray("participant");
                        for (int i = 0; i < jsonArrayParticipant.length(); i++) {
                            JSONObject jsonObjectParticipant = (JSONObject) jsonArrayParticipant.get(i);
                            ParticipantData participantData = new ParticipantData(jsonObjectParticipant);
                            participantDataList.add(participantData);

                            JSONArray jsonArrayTeamMates = jsonObjectParticipant.getJSONArray("teammates");
                            for (int j = 0; j < jsonArrayTeamMates.length(); j++) {
                                JSONObject jsonObjectTeamMates = (JSONObject) jsonArrayTeamMates.get(j);
                                ParticipantData teamMatesData = new ParticipantData(jsonObjectTeamMates);
                                participantDataList.add(teamMatesData);
                            }
                        }
                        updateUI(tournamentData, participantDataList);
                    }
                }
            }
        } catch (JSONException e) {

        }
    }

    public void updateUI(TournamentData tournamentData, List<ParticipantData> participantDataList) {
        if (tournamentData.attachment != null && !tournamentData.attachment.isEmpty() && !tournamentData.attachment.equalsIgnoreCase("null")) {
            appBarLayoutTeaser.setVisibility(View.VISIBLE);
        } else {
            appBarLayoutTeaser.setVisibility(View.GONE);
        }
        if (tournamentData.tournament_type.equalsIgnoreCase("paid")) {
            tvFree.setVisibility(View.GONE);
            tvCoinsForEntry.setVisibility(View.VISIBLE);
            tvEntryFees.setVisibility(View.VISIBLE);
            tvOr.setVisibility(View.VISIBLE);

            tvCoinsForEntry.setText(tournamentData.coin_amount);
            tvEntryFees.setText("₹" + tournamentData.entry_price);
        } else if (tournamentData.tournament_type.equalsIgnoreCase("free")) {
            tvFree.setVisibility(View.VISIBLE);
            tvCoinsForEntry.setVisibility(View.GONE);
            tvEntryFees.setVisibility(View.GONE);
            tvOr.setVisibility(View.GONE);
        }

        if (!tournamentData.horizontal_rectangle.isEmpty()) {
            Glide.with(getApplicationContext())
                    .load(tournamentData.horizontal_rectangle)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(ivTeaserImage);
        }
        tvPrize.setText(tournamentData.coin_earnings);
        tvRegistrationReward.setText(tournamentData.registration_reward);
        tvTournamentName.setText(tournamentData.name);
        tvTournamentStartDate.setText(Utility.formatDate("yyyy-MM-dd hh:mm:ss", "dd MMM yyyy, hh:mm aa", tournamentData.tournament_start_date));
        tvTournamentEndDate.setText(Utility.formatDate("yyyy-MM-dd hh:mm:ss", "dd MMM yyyy, hh:mm aa", tournamentData.tournament_end_date));
        tvRegistrationStartDate.setText(Utility.formatDate("yyyy-MM-dd hh:mm:ss", "dd MMM yyyy, hh:mm aa", tournamentData.start_date));
        tvRegistrationEndDate.setText(Utility.formatDate("yyyy-MM-dd hh:mm:ss", "dd MMM yyyy, hh:mm aa", tournamentData.end_date));


        if (!participantDataList.isEmpty()) {
            /*for (int i = 0; i < participantDataList.size(); i++) {
                if (i == 0) {
                    tvTeamName.setText(participantDataList.get(i).team_name);
                    tvTeamMemberFirst.setText(participantDataList.get(i).username);
                }
                if (i == 1) {
                    tvTeamMemberFirst.setText(participantDataList.get(i).username);
                }
                if (i == 2) {
                    tvTeamMemberFirst.setText(participantDataList.get(i).username);
                }
                if (i == 3) {
                    tvTeamMemberFirst.setText(participantDataList.get(i).username);
                }
                if (participantDataList.get(i).user_id.equals(LocalStorage.getUserId())) {
                    checkPaymentStatus(participantDataList.get(i).isPaid);
                    participantId = participantDataList.get(i).id;
                }
            }*/
            if (tournamentData.tournament_type.equalsIgnoreCase("paid")) {
                long walletBalance = 0;
                if (HomeActivity.toolbarWalletBalance != null) {
                    walletBalance = LocalStorage.getValue(LocalStorage.KEY_WALLET_BALANCE, 0, Long.class);
                }
                long coinsForEntry = Integer.parseInt(tournamentData.coin_amount);
                if (coinsForEntry <= walletBalance) {
                    rlCoins.setVisibility(View.VISIBLE);
                    tvCoins.setText(tournamentData.coin_amount + getString(R.string.icoins));
                } else {
                    rlCoins.setVisibility(View.GONE);
                }
            }

            int count = 1;
            llParticipants.removeAllViews();
            llRules.removeAllViews();
            for (int i = 0; i < participantDataList.size(); i++) {
                if (participantDataList.get(i).user_id.equals(LocalStorage.getUserId())) {
                    tvTeamName.setText(participantDataList.get(i).team_name);
                    checkPaymentStatus(participantDataList.get(i).isPaid);
                    participantId = participantDataList.get(i).id;
                }

                TextView tv = new TextView(this);
                tv.setGravity(CENTER);
                tv.setTextAppearance(R.style.HeaderRegularStyleRent);
                tv.setText(count + ". " + participantDataList.get(i).username);
                if (participantDataList.get(i).is_leader.equalsIgnoreCase("true") && !participantDataList.get(i).isPaid) {
                    tv.setText(tv.getText() + " " + getString(R.string.leader_payment_pending));
                } else if (participantDataList.get(i).is_leader.equalsIgnoreCase("false") && !participantDataList.get(i).isPaid) {
                    tv.setText(count + ". " + participantDataList.get(i).username + " " + getString(R.string.payment_pending));
                } else if (participantDataList.get(i).is_leader.equalsIgnoreCase("true")) {
                    tv.setText(tv.getText() + " " + getString(R.string.leader));
                }
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 20, 0, 0);
                tv.setLayoutParams(params);
                tv.setTextColor(Color.WHITE);
                count++;
                llParticipants.addView(tv);
            }
        }

        for (int i = 0; i < tournamentData.rules.size(); i++) {
            TextView tv = new TextView(this);
            tv.setGravity(CENTER);
            tv.setTextAppearance(R.style.HeaderRegularStyleRent);
            tv.setText(tournamentData.rules.get(i));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 30, 0, 0);
            tv.setLayoutParams(params);
            tv.setTextColor(Color.WHITE);

            llRules.addView(tv);
        }

        if (message != null && !message.isEmpty()) {
            showRegistrationSuccessDialog(TournamentSummaryActivity.this, message);
        }
    }

    private void showError(@Nullable String errorMessage) {
        if (errorMessage == null) errorMessage = APIManager.GENERIC_API_ERROR_MESSAGE;
        AlertUtils.showAlert(getString(R.string.label_error), errorMessage, null, this, null);
    }

    public void checkPaymentStatus(boolean isPaid) {
        if (isPaid) {
            tvPaymentStatus.setText(getString(R.string.payment_paid));
            btnPayNow.setVisibility(View.GONE);
            paymentRoot.setVisibility(View.GONE);
        } else {
            tvPaymentStatus.setText(getString(R.string.pending));
            btnPayNow.setVisibility(View.VISIBLE);
        }
    }

    private void paytmPrepayment(String id) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("id", id);
            params.put("payment_mode", "paytm");

            APIRequest apiRequest = new APIRequest(Url.ESPORTS_PAYMEMT, Request.Method.POST, params, null, this);
            apiRequest.showLoader = true;
            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    if (error == null) {
                        handlePaytmPrepaymentResponse(response, error);
                    } else {
                        Utility.showError(error.toString(), TournamentSummaryActivity.this);
                    }
                }
            });
        } catch (Exception e) {
            Utility.showError(getString(R.string.failed_to_initiate_payment), TournamentSummaryActivity.this);
        }
    }

    private void handlePaytmPrepaymentResponse(@Nullable String response, @Nullable Exception error) {
        try {
            if (error != null) {
                Utility.showError(error.getMessage(), TournamentSummaryActivity.this);
            } else {
                if (response != null) {
                    JSONObject jsonObjectResponse = new JSONObject(response);
                    String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                    if (type.equalsIgnoreCase("error")) {
                        String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                        Utility.showError(message, this);
                    } else if (type.equalsIgnoreCase("ok")) {
                        JSONObject object = jsonObjectResponse.getJSONObject("msg");
                        this.initiatePaytm(object);
                    }
                }
            }
        } catch (JSONException e) {
            Utility.showError(e.toString(), this);
        }
    }

    private void initiatePaytm(JSONObject object) {
        PaytmPGService Service = PaytmPGService.getProductionService();

        HashMap<String, String> paramMap = new HashMap<>();
        JSONObject paytmParams = null;

        try {
            if (!object.has("paytmParams")) {
                Utility.showError(getString(R.string.order_request_parameters_not_received), TournamentSummaryActivity.this);
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

                Service.initialize(Order, null);

                Service.startPaymentTransaction(TournamentSummaryActivity.this, true, true, new PaytmPaymentTransactionCallback() {

                    @Override
                    public void onTransactionResponse(Bundle inResponse) {
                        Log.d(TAG, "onTransactionResponse " + inResponse.getString("ORDERID"));
                        if (inResponse.getString("STATUS").equalsIgnoreCase("TXN_SUCCESS")) {
                            // TRANSACTION SUCCESS
                            paytmPostPaymentRequest(inResponse);
                        } else {
                            // TRANSACTION FAILED
                            Log.d(TAG, "Paytm onTransactionResponse");
                            Utility.showError((inResponse.containsKey("RESPMSG") ? inResponse.getString("RESPMSG") : getString(R.string.transaction_failed)), TournamentSummaryActivity.this);
                        }
                    }

                    @Override
                    public void networkNotAvailable() {
                        Toast.makeText(getApplicationContext(), getString(R.string.network_connection_error), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onErrorProceed(String s) {

                    }

                    @Override
                    public void clientAuthenticationFailed(String inErrorMessage) {
                        Toast.makeText(getApplicationContext(), getString(R.string.client_authentication_failed) + inErrorMessage, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void someUIErrorOccurred(String inErrorMessage) {
                        Toast.makeText(getApplicationContext(), getString(R.string.ui_errror) + inErrorMessage, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
                        Toast.makeText(getApplicationContext(), getString(R.string.unable_to_load_webpage) + String.valueOf(iniErrorCode) + inErrorMessage, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onBackPressedCancelTransaction() {
                        Toast.makeText(getApplicationContext(), getString(R.string.transaction_cancelled), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                        Toast.makeText(getApplicationContext(), getString(R.string.transaction_cancelled) + inResponse.toString(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        } catch (Exception e) {
            Log.d(TAG, "Exception " + e.getMessage());
        }
    }

    private void paytmPostPaymentRequest(Bundle inResponse) {
        Log.d(TAG, "paytmPostPaymentRequest");
        try {
            Map<String, String> params = new HashMap<>();
            params.put("id", participantId);
            params.put("payment_mode", "paytm");
            params.put("order_id", inResponse.getString("ORDERID"));

            APIRequest apiRequest = new APIRequest(Url.ESPORTS_PAYMEMT_COMPLETE, Request.Method.POST, params, null, this);
            apiRequest.showLoader = true;
            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    try {
                        JSONObject jsonObjectResponse = new JSONObject(response);
                        String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                        if (type.equalsIgnoreCase("error")) {

                            String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                            Utility.showError(message, TournamentSummaryActivity.this);

                        } else if (type.equalsIgnoreCase("ok") && jsonObjectResponse.has("msg")) {
                            JSONObject msg = jsonObjectResponse.getJSONObject("msg");
                            getUserRegisteredTournamentInfo();
                            message = msg.getString("message");
                            if (msg.getString("status").equalsIgnoreCase("success")) {
                                // showRegistrationSuccessDialog(TournamentSummaryActivity.this,msg.getString("message"));
                            } else {
                                Utility.showError(msg.getString("display_message"), TournamentSummaryActivity.this);
                            }
                        }
                    } catch (Exception e) {

                    }
                }
            });
        } catch (Exception e) {
            Utility.showError(getString(R.string.failed_to_complete_payment), TournamentSummaryActivity.this);
        }
    }

    // RAZORPAY INIT ORDER
    private void razorpayPrepayment(String id) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("id", id);
            params.put("payment_mode", "razorpay");

            APIRequest apiRequest = new APIRequest(Url.ESPORTS_PAYMEMT, Request.Method.POST, params, null, this);
            apiRequest.showLoader = true;
            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    if (error == null) {
                        handleRazorpayPrepaymentResponse(response, error);
                    } else {
                        Utility.showError(error.toString(), TournamentSummaryActivity.this);
                    }
                }
            });
        } catch (Exception e) {
            Utility.showError(getString(R.string.failed_to_initiate_payment), TournamentSummaryActivity.this);
        }
    }

    // RAZORPAY INIT ORDER RESPONSE
    private void handleRazorpayPrepaymentResponse(@Nullable String response, @Nullable Exception error) {
        try {
            if (error != null) {
                Utility.showError(error.getMessage(), TournamentSummaryActivity.this);
            } else {
                if (response != null) {
                    JSONObject jsonObjectResponse = new JSONObject(response);
                    String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                    if (type.equalsIgnoreCase("error")) {
                        String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                        //Utility.showError(message,this);
                        //showAlreadyPurchasedDialog(message);
                    } else if (type.equalsIgnoreCase("ok")) {
                        /// new flow checking to be done here
                        JSONObject object = jsonObjectResponse.getJSONObject("msg");
                        String status = object.has("status") ? object.getString("status") : "";
                        if (status.equalsIgnoreCase("SUCCESS")) {
                            //flow for coin  only
                            //showBuySuccessDialog();
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

    private void initiateRazorpay(JSONObject object) {
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
            // options.put("description", object.getString("post_title"));
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("currency", "INR");
            options.put("amount", object.getString("amount"));
            options.put("order_id", object.getString("razorpay_order_id"));
            // this.itap_order_id = object.getString("order_id");

            instance.open(activity, options);
        } catch (Exception e) {
            Toast.makeText(activity, getString(R.string.error_in_payment) + e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
            e.printStackTrace();
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        if (s.isEmpty()) {
            Utility.showError(getString(R.string.razorpay_payment_id_is_null), this);
            this.itap_order_id = "";
        } else {
            razorpayPostPayment(this.itap_order_id, s);
        }
    }

    @Override
    public void onPaymentError(int i, String s) {
        this.itap_order_id = "";
        switch (i) {
            case Checkout.NETWORK_ERROR:
                Toast.makeText(getApplicationContext(), getString(R.string.there_was_a_network_error), Toast.LENGTH_LONG).show();
                break;

            case Checkout.PAYMENT_CANCELED:
            case Checkout.INVALID_OPTIONS:
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
                break;

            case Checkout.TLS_ERROR:
                Toast.makeText(getApplicationContext(), getString(R.string.device_does_not_have), Toast.LENGTH_LONG).show();
                break;
        }
    }

    private void razorpayPostPayment(String itap_order_id, String razorpay_order_id) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("id", participantId);
            params.put("payment_mode", "razorpay");
            //params.put("order_id", itap_order_id);
            params.put("razorpay_payment_id", razorpay_order_id);

            APIRequest apiRequest = new APIRequest(Url.ESPORTS_PAYMEMT_COMPLETE, Request.Method.POST, params, null, this);
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
                                Utility.showError(message, TournamentSummaryActivity.this);

                            } else if (type.equalsIgnoreCase("ok") && jsonObjectResponse.has("msg")) {

                                JSONObject msg = jsonObjectResponse.getJSONObject("msg");
                                getUserRegisteredTournamentInfo();
                                message = msg.getString("message");
                                if (msg.getString("message").equalsIgnoreCase("message")) {
                                    // message=msg.getString("message");
                                    //showRegistrationSuccessDialog(TournamentSummaryActivity.this,msg.getString("message"));
                                } else {
                                    Utility.showError(msg.getString("display_message"), TournamentSummaryActivity.this);
                                }
                            }
                        } else {
                            Utility.showError(error.toString(), TournamentSummaryActivity.this);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }
            });
        } catch (Exception e) {
            Utility.showError(getString(R.string.failed_to_complete_payment), TournamentSummaryActivity.this);
        }
    }

    private void coinsPrepayment(String id) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("id", id);
            params.put("payment_mode", "coins");

            APIRequest apiRequest = new APIRequest(Url.ESPORTS_PAYMEMT, Request.Method.POST, params, null, this);
            apiRequest.showLoader = true;
            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    if (error == null) {
                        handleCoinsPrepaymentResponse(response, error);
                    } else {
                        Utility.showError(error.toString(), TournamentSummaryActivity.this);
                    }
                }
            });
        } catch (Exception e) {
            Utility.showError(getString(R.string.failed_to_initiate_payment), TournamentSummaryActivity.this);
        }
    }

    private void handleCoinsPrepaymentResponse(@Nullable String response, @Nullable Exception error) {
        try {
            if (error != null) {
                Utility.showError(error.getMessage(), TournamentSummaryActivity.this);
            } else {
                if (response != null) {
                    JSONObject jsonObjectResponse = new JSONObject(response);
                    String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                    if (type.equalsIgnoreCase("error")) {
                        String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                        //Utility.showError(message,this);
                        //showAlreadyPurchasedDialog(message);
                    } else if (type.equalsIgnoreCase("ok")) {
                        /// new flow checking to be done here
                        JSONObject object = jsonObjectResponse.getJSONObject("msg");
                        String status = object.has("status") ? object.getString("status") : "";
                        if (status.equalsIgnoreCase("SUCCESS")) {
                            //flow for coin  only
                            //showBuySuccessDialog();
                        } else {
                            //flow for coins/rupees
                            getUserRegisteredTournamentInfo();
                            message = object.getString("message");
                            //showRegistrationSuccessDialog(TournamentSummaryActivity.this,object.getString("message"));
                        }
                    }
                }
            }
        } catch (JSONException e) {
            Utility.showError(e.toString(), this);
        }
    }

    private void showRegistrationSuccessDialog(Context context, String msg) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_survey_success, null);
        ImageView iv_go = view.findViewById(R.id.iv_got_it);
        TextView tv_success_message = view.findViewById(R.id.tv_success_message);
        Dialog alertDialogBuilder = new Dialog(context);
        alertDialogBuilder.setContentView(view);
        alertDialogBuilder.show();
        tv_success_message.setText(msg);
        message = "";
        iv_go.setOnClickListener(v -> {
            message = "";
            alertDialogBuilder.dismiss();
        });
    }
}