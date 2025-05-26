package com.app.itaptv.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.android.volley.Request;
import com.app.itaptv.API.APIManager;
import com.app.itaptv.API.APIRequest;
import com.app.itaptv.API.APIResponse;
import com.app.itaptv.API.Url;
import com.app.itaptv.R;
import com.app.itaptv.custom_interface.WalletCallback;
import com.app.itaptv.structure.CouponData;
import com.app.itaptv.utils.AlertUtils;
import com.app.itaptv.utils.Log;
import com.app.itaptv.utils.Wallet;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by poonam on 27/9/18.
 */

public class CouponDetailsActivity extends BaseActivity implements View.OnClickListener {
    TextView tvCouponSuccessMessage;
    TextView tvCouponTitle;
    TextView tvCouponCoins;
    TextView tvCoupanTermsCondition;
    ImageView ivCoupanImg;
    Button btBuyCoupon;
    LinearLayout llLoader;

    private long walletBalance = 0L;
    private boolean success;
    CouponData couponData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_details);

        setToolbar();
        init();
    }

    private void init() {
        tvCouponSuccessMessage = findViewById(R.id.tvCouponSuccessMessage);
        tvCouponTitle = findViewById(R.id.tvCoupanNameDetail);
        tvCouponCoins = findViewById(R.id.tvCoupanValueDetail);
        ivCoupanImg = findViewById(R.id.ivCouponImage);
        btBuyCoupon = findViewById(R.id.btBuyCoupon);
        llLoader = findViewById(R.id.llLoader);
        tvCoupanTermsCondition = findViewById(R.id.tvCoupanTermsCondition);

        btBuyCoupon.setOnClickListener(this);

        couponData = (CouponData) getIntent().getBundleExtra("CoupanDetail").getSerializable("CoupanData");
        // walletBalance = Long.parseLong(getIntent().getBundleExtra("CoupanDetail").getString("Balance"));

        tvCouponTitle.setText(couponData.postTitle);
        String coins = String.format(getString(R.string.text_for_coupon), couponData.rewardText, couponData.rewardCoins);
        tvCouponCoins.setText(Html.fromHtml(coins));
        tvCoupanTermsCondition.setText(Html.fromHtml(couponData.termsAndConditions));
        Glide.with(this)
                .load(couponData.imgUrl)
                .apply(new RequestOptions().centerCrop().dontAnimate().diskCacheStrategyOf(DiskCacheStrategy.ALL))
                .into(ivCoupanImg);
        getWalletBalance();
    }

    private void getWalletBalance() {
        Wallet.getWalletBalance(this, new WalletCallback() {
            @Override
            public boolean onResult(boolean success, @Nullable String error, long coins, long diamonds, long creditedCoins, JSONArray historyData, int historyCount) {
                handleAppBalance42Response(success, error, coins);
                return success;
            }
        });
    }

    private void handleAppBalance42Response(boolean success, String error, long coins) {
        walletBalance = success ? coins : 0;
    }

    private void setToolbar() {
        setToolbar(false);
        showToolbarBackButton(R.drawable.white_arrow);
        showToolbarTitle(false);
    }

    /**
     * Alert dialog to confirm coins to redeem for coupon
     * Set data on custom layout of alert dialog
     */
    public void confirmRedeemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setView(R.layout.layout_confirm_dialog);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        Button btPositive = alertDialog.findViewById(R.id.btPositive);
        Button btNegative = alertDialog.findViewById(R.id.btNegative);
        ((TextView) alertDialog.findViewById(R.id.tvStoreName)).setText(R.string.buy_coupon);
        ((TextView) alertDialog.findViewById(R.id.tvMessage)).setText(R.string.confirm_msg_redeem_coins);
        btPositive.setText(R.string.yes);
        btNegative.setText(R.string.label_cancel);

        btPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                redeemCoupon();
            }
        });
        btNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    private void redeemCoupon() {
        llLoader.setVisibility(View.VISIBLE);
        try {
            Map<String, String> params = new HashMap<>();
            APIRequest apiRequest = new APIRequest(Url.GET_REDEEN_COUPON + "&ID=" + couponData.Id, Request.Method.GET, params, null, CouponDetailsActivity.this);
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    handleCouponResponse(response, error);
                }
            });
        } catch (Exception e) {
            llLoader.setVisibility(View.GONE);
        }
    }

    private void handleCouponResponse(String response, Exception error) {
        try {

            if (error != null) {
                String errorMessage = error.getMessage() == null ? APIManager.GENERIC_API_ERROR_MESSAGE : error.getMessage();
                llLoader.setVisibility(View.GONE);
                AlertUtils.showAlert(getString(R.string.label_error), errorMessage, null, this, null);
            } else {
                if (response != null) {
                    Log.e("response", response);

                    JSONObject jsonObjectResponse = new JSONObject(response);
                    String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                    if (type.equalsIgnoreCase("error")) {
                        String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                        showError(message);
                    } else if (type.equalsIgnoreCase("ok")) {
                        app42RedeemCoins(Long.parseLong(couponData.rewardCoins));

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

    private void couponSuccess(long coins) {
        tvCouponSuccessMessage.setVisibility(View.VISIBLE);
        // btBuyCoupon.setText(R.string.resend);
        btBuyCoupon.setVisibility(View.GONE);
        walletBalance = coins;

        Intent intent = getIntent();
        intent.putExtra(WalletActivity.KEY_WALLET_BALANCE, walletBalance);
        intent.putExtra(WalletActivity.KEY_SUCCESS, success);
        setResult(RESULT_OK, intent);
    }

    private void app42RedeemCoins(long coins) {
        Wallet.redeemCoins(this, 1, couponData.postTitle + " " + couponData.rewardText, Wallet.FLAG_COUPON, coins, new WalletCallback() {
            @Override
            public boolean onResult(boolean success, @Nullable String error, long coins, long diamonds, long creditedCoins, JSONArray historyData, int historyCount) {
                llLoader.setVisibility(View.GONE);
                handleApp42Response(success, error, coins);
                return success;
            }
        });
    }

    private void handleApp42Response(boolean success, @Nullable String error, long coins) {
        this.success = success;
        if (success) {
            couponSuccess(coins);
        } else {
            String errorMessage = error == null ? getString(R.string.GENERIC_API_ERROR_MESSAGE) : error;
            AlertUtils.showAlert(getString(R.string.label_error), errorMessage, null, CouponDetailsActivity.this, null);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btBuyCoupon:
                if (Long.parseLong(couponData.rewardCoins) <= walletBalance) {
                    confirmRedeemDialog();
                } else {
                    showError(getString(R.string.error_msg_balance));
                }
                break;
        }
    }
}
