package com.app.itaptv.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.app.itaptv.API.APIManager;
import com.app.itaptv.API.APIRequest;
import com.app.itaptv.API.APIResponse;
import com.app.itaptv.API.Url;
import com.app.itaptv.R;
import com.app.itaptv.structure.User;
import com.app.itaptv.utils.AlertUtils;
import com.app.itaptv.utils.LocalStorage;
import com.app.itaptv.utils.Log;
import com.app.itaptv.utils.Utility;
import com.app.itaptv.utils.Wallet;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.app.itaptv.utils.Constant.KEY_MOBILE;

/**
 * Created by poonam on 25/9/18.
 */

public class WalletActivity extends BaseActivity {

    public static final int FLAG_EARN_HISTORY = 1;
    public static final int FLAG_REDEEM_HISTORY = 2;
    public static String KEY_WALLET_BALANCE = "wallet_balance";
    public static String KEY_SUCCESS = "success";
    public static String KEY_REGISTER = "register";
    public static String KEY_REFRESH = "refresh";

    public static int REQUEST_CODE = 1;
    public static int REQUEST_CODE1 = 23;
    private RelativeLayout rlParent;
    private TextView tvWalletBalance;
    private ProgressBar progressBar;
    private RelativeLayout rlLeaderBoard, rlRedeemCoins, rlEarnHistroy, rlRedeemHistroy, rlLearMore;
    private LinearLayout redeemCoinsLayout;
    private boolean isRedeemOfferActive = false;
    //View notificationButton;

    User user;

    private long walletBalance = 0L;
    private boolean success;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        init();
        setToolbar(true);
        showToolbarBackButton(R.drawable.white_arrow);
        showToolbarTitle(false);
        setCustomizedTitle(0);
        getWalletBalance();
        getWalletExp();
    }

    private void init() {
        rlParent = findViewById(R.id.rlParent);
        tvWalletBalance = findViewById(R.id.tvWalletBalance);
        //tvWalletBalance = findViewById(R.id.coins_text);
        progressBar = findViewById(R.id.progressBar);
        redeemCoinsLayout = findViewById(R.id.layout_redeem_coins);
        redeemCoinsLayout.setClickable(true);
        redeemCoinsLayout.setVisibility(View.GONE);
        redeemCoinsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user = LocalStorage.getUserData();
                if (isRedeemOfferActive) {
                    redeemCoinsLayout.setClickable(false);
                    checkVerificationOffer();
                }
            }
        });
        //notificationButton = findViewById(R.id.notification_button);
        rlLeaderBoard = findViewById(R.id.rlLeaderBoard);
        rlLeaderBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WalletActivity.this, LeaderBoardActivity.class));
            }
        });
        rlRedeemCoins = findViewById(R.id.rlRedeemCoins);
        rlRedeemCoins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult();
                finish();
            }
        });
        rlEarnHistroy = findViewById(R.id.rlEarnHistroy);
        rlEarnHistroy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WalletActivity.this, HistoryActivity.class)
                        .putExtra(HistoryActivity.HISTORY_FLAG, FLAG_EARN_HISTORY));
            }
        });
        rlRedeemHistroy = findViewById(R.id.rlRedeemHistroy);
        rlRedeemHistroy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WalletActivity.this, HistoryActivity.class)
                        .putExtra(HistoryActivity.HISTORY_FLAG, FLAG_REDEEM_HISTORY));
            }
        });
        rlLearMore = findViewById(R.id.rlLearMore);
        rlLearMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WalletActivity.this, LearnMoreActivity.class));

            }
        });
        showLoader();

    }

    public void checkVerificationOffer() {
        if (user.mobile == null || user.mobile.equals("")) {
            startActivity(new Intent(this, VerificationActivity.class).putExtra(VerificationActivity.TYPE_KEY, KEY_MOBILE));
        }/* else if (user.userEmail == null || user.userEmail.equals("")) {
            startActivity(new Intent(this, VerificationActivity.class).putExtra(VerificationActivity.TYPE_KEY, KEY_EMAIL));
        } */ else if (user.displayName == null || user.displayName.equals("")) {
            setupUserNameDialog(this, getResources().getString(R.string.label_edit_profile_name_desc_redeem));
        } else {
            startRedeemOfferFunctionalityAPI();
        }
        redeemCoinsLayout.setClickable(true);
    }

    public void startRedeemOfferFunctionalityAPI() {
        progressBar.setVisibility(View.VISIBLE);
        try {
            Map<String, String> params = new HashMap<>();
            APIRequest apiRequest = new APIRequest(Url.REDEEM_COINS_FOR_CASH + "&userId=" + user.userId, Request.Method.GET, params, null, this);
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    progressBar.setVisibility(View.GONE);
                    handleCoinOfferResponse(response, error);
                }
            });
        } catch (Exception e) {

        }
    }

    /**
     * This method handles offer response
     *
     * @param response api response. Can be null
     * @param error    error message in case of error. Can be null
     */
    private void handleCoinOfferResponse(@Nullable String response, @Nullable Exception error) {
        try {
            if (error != null) {
                showError(error.getMessage());
            } else {
                if (response != null) {
                    //Log.e("response", response);
                    JSONObject jsonObjectResponse = new JSONObject(response);
                    String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                    if (type.equalsIgnoreCase("error")) {
                        String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                        showRedeemCoinsResponseDialog(this, message);
                    } else if (type.equalsIgnoreCase("ok")) {
                        JSONObject jsonArrayMsg = jsonObjectResponse.getJSONObject("msg");
                        String message = jsonArrayMsg.getString("message");
                        //int cash = jsonArrayMsg.getInt("cash");
                        showRedeemCoinsResponseDialog(this, message);

                    }
                }
                redeemCoinsLayout.setClickable(true);
            }
        } catch (JSONException e) {
            showError(error.getMessage());
        }
    }

    public void showRedeemCoinsResponseDialog(Context context, String message) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_redeem_coins_success, null);

        ImageView iv_go = view.findViewById(R.id.iv_got_it);
        TextView tv_cash_won = view.findViewById(R.id.tv_cash_won);
        final Dialog alertDialogBuilder = new Dialog(context);
        alertDialogBuilder.setContentView(view);
        alertDialogBuilder.show();
        tv_cash_won.setText(message);


        iv_go.setOnClickListener(view1 -> alertDialogBuilder.dismiss());
    }

    public void setupUserNameDialog(Context context, String dialogDesc) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_set_name, null);

        EditText edit_text_name = view.findViewById(R.id.dialog_editText_name);
        TextView text_view_desc = view.findViewById(R.id.textView3);
        Button btn_set = view.findViewById(R.id.btn_set);
        text_view_desc.setText(dialogDesc);
        final Dialog alertDialogBuilder = new Dialog(context);
        alertDialogBuilder.setContentView(view);
        alertDialogBuilder.show();

        btn_set.setOnClickListener(view1 -> {
            String name = edit_text_name.getText().toString();
            if (name.equals("")) {
                showError(getString(R.string.error_msg_enter_mobile_no));
                return;
            }
            updateProfileAPI(name);
            alertDialogBuilder.dismiss();
        });
    }

    /**
     * Update Profile Using API call
     *
     * @param username
     */
    private void updateProfileAPI(String username) {
        progressBar.setVisibility(View.VISIBLE);
        try {
            Map<String, String> params = new HashMap<>();
            params.put("display_name", username);
            Log.d("params", params.toString());
            APIRequest apiRequest = new APIRequest(Url.UPDATE_PROFILE, Request.Method.POST, params, null, this);
            apiRequest.showLoader = true;
            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    progressBar.setVisibility(View.GONE);
                    handleUpdateProfileResponse(response, error);
                }
            });
        } catch (Exception e) {

        }
    }

    /**
     * This method handles Update Profile
     *
     * @param response api response. Can be null
     * @param error    error message in case of error. Can be null
     */
    private void handleUpdateProfileResponse(String response, Exception error) {
        try {
            if (error != null) {
                showError(error.getMessage());
            } else {
                if (response != null) {
                    //Log.e("response", response);
                    JSONObject jsonObjectResponse = new JSONObject(response);
                    String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                    if (type.equalsIgnoreCase("error")) {
                        //String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                        String message = jsonObjectResponse.has("msg") ? Utility.getMessage(WalletActivity.this, jsonObjectResponse.get("msg").toString()) : APIManager.GENERIC_API_ERROR_MESSAGE;
                        showError(message);
                    } else if (type.equalsIgnoreCase("ok")) {
                        JSONObject jsonObject = jsonObjectResponse.has("msg") ? jsonObjectResponse.getJSONObject("msg") : new JSONObject();
                        if (jsonObject.length() > 0) {
                            JSONObject jsonObjectuser = jsonObject.has("user") ? jsonObject.getJSONObject("user") : new JSONObject();
                            if (jsonObjectuser.length() > 0) {
                                User user = new User(jsonObjectuser);
                                LocalStorage.setUserData(user);
                                getFragmentManager().popBackStack();
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {

        }
    }

    /**
     * This method shows error alert
     *
     * @param errorMessage message to be displayed in alert dialog
     */
    private void showError(@Nullable String errorMessage) {
        if (errorMessage == null) errorMessage = APIManager.GENERIC_API_ERROR_MESSAGE;
        AlertUtils.showAlert(getString(R.string.label_error), errorMessage, null, this, null);
    }

    public void showRedeemCoins(View view) {
        setResult();
        finish();
        /*Intent intent = new Intent();
        intent.setAction(HomeActivity.VOUCHER_BROADCAST_KEY);
        sendBroadcast(intent);*/
    }

    public void showHistory(View view) {
        int pageFlag = 0;
        switch (view.getId()) {
            case R.id.rlEarnHistroy:
                pageFlag = FLAG_EARN_HISTORY;
                break;
            case R.id.rlRedeemHistroy:
                pageFlag = FLAG_REDEEM_HISTORY;
                break;
        }
        startActivity(new Intent(this, HistoryActivity.class)
                .putExtra(HistoryActivity.HISTORY_FLAG, pageFlag));
    }

    public void showLeaderBoard(View view) {
        startActivity(new Intent(WalletActivity.this, LeaderBoardActivity.class));
    }

    public void showLearnMore(View view) {
        startActivity(new Intent(this, LearnMoreActivity.class));
    }

    private void getWalletBalance() {
        Wallet.getWalletBalance(this, (success, error, coins, diamonds, creditedCoins, historyData, historyCount) -> {
            handleApp42Response(success, error, coins);
            return success;
        });
    }

    private void handleApp42Response(boolean success, @Nullable String error, long coins) {

        if (success) {
            hideLoaderOnSuccess();
            walletBalance = coins;
            setWalletBalance();
        } else {
            /*if (!error.contains(Wallet.ERROR_MESSAGE_NOT_FOUND)) {
                progressBar.setVisibility(View.GONE);
                String errorMessage = error == null ? Wallet.GENERIC_ERROR_MESSAGE : error;
                AlertUtils.showAlert("Error", errorMessage, null, WalletActivity.this, null);
                return;
            }*/
            hideLoaderOnSuccess();
            tvWalletBalance.setText(String.valueOf(walletBalance));
        }
    }

    private void showLoader() {
        progressBar.setVisibility(View.VISIBLE);
        rlParent.setVisibility(View.GONE);
    }

    private void hideLoaderOnSuccess() {
        progressBar.setVisibility(View.GONE);
        rlParent.setVisibility(View.VISIBLE);
    }

    private void setWalletBalance() {
        tvWalletBalance.setText(String.valueOf(walletBalance));
    }

    private void setResult() {
        Intent intent = getIntent();
        intent.putExtra(WalletActivity.KEY_WALLET_BALANCE, walletBalance);
        intent.putExtra(WalletActivity.KEY_SUCCESS, success);
        setResult(RESULT_OK, intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTitle(getString(R.string.wallet));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            if (data != null) {
                success = data.getBooleanExtra(WalletActivity.KEY_SUCCESS, false);
                if (success) {
                    walletBalance = data.getLongExtra(KEY_WALLET_BALANCE, 0L);
                    setWalletBalance();
                    setResult();
                }
            }
        }
    }

    void getWalletExp() {
        try {
            APIRequest apiRequest = new APIRequest(Url.WALLET_EXPIRY, Request.Method.POST, null, null, WalletActivity.this);
            APIManager.request(apiRequest, (response, error, headers, statusCode) -> {
                if (response != null) {
                    try {
                        JSONObject object = new JSONObject(response);
                        if (object.has("msg")) {
                            JSONObject objectMessage = object.getJSONObject("msg");
                            if (objectMessage.has("message")) {
                                TextView tvWallet = findViewById(R.id.tvWalletExpires);
                                tvWallet.setText(objectMessage.getString("message"));
                                isRedeemOfferActive = objectMessage.getBoolean("isBannerActive");
                                if (isRedeemOfferActive) {
                                    redeemCoinsLayout.setVisibility(View.VISIBLE);
                                    LocalStorage.setIsBannerActive(true);
                                } else {
                                    LocalStorage.setIsBannerActive(false);
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
}
