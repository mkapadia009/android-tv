package com.app.itaptv.activity.vouchers;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.app.itaptv.API.APIManager;
import com.app.itaptv.API.APIRequest;
import com.app.itaptv.API.APIResponse;
import com.app.itaptv.API.Url;
import com.app.itaptv.R;
import com.app.itaptv.activity.BaseActivity;
import com.app.itaptv.structure.vouchers.CouponsDetails;
import com.app.itaptv.utils.AlertUtils;
import com.app.itaptv.utils.Log;
import com.app.itaptv.utils.Utility;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CouponsDetailsActivity extends BaseActivity {

    //UI
    ConstraintLayout clCoupon;
    ImageView ivLogo, ivCoins;
    TextView tvTitle, tvExpires, tvCat, tvDescription, tvCoins, tvName, tvCode, tvRedeemLb, tvTerms;
    Button btRedeem, btCopy, btGoToStore;
    ProgressBar progressBar;

    //Variables
    public static String COUPON_DETAILS_OBJECT = "couponsDetailsObject";
    CouponsDetails couponsDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupons_details);
        setToolbar();
        getExtras();
    }

    private void setToolbar() {
        setToolbar(true);
        showToolbarBackButton(R.drawable.white_arrow);
        showToolbarTitle(false);
    }

    private void getExtras() {
        if (getIntent() != null) {
            if (getIntent().hasExtra(COUPON_DETAILS_OBJECT)) {
                couponsDetails = getIntent().getParcelableExtra(COUPON_DETAILS_OBJECT);
                setUpUI();
            }
        }
    }

    private void setUpUI() {
        clCoupon = findViewById(R.id.clCoupon);
        ivLogo = findViewById(R.id.ivLogo);
        tvName = findViewById(R.id.tvName);
        tvCoins = findViewById(R.id.tvCoins);
        tvTitle = findViewById(R.id.tvTitle);
        tvCat = findViewById(R.id.tvCat);
        tvDescription = findViewById(R.id.tvDescription);
        tvExpires = findViewById(R.id.tvExpires);
        btRedeem = findViewById(R.id.btRedeem);
        tvCode = findViewById(R.id.etCode);
        btCopy = findViewById(R.id.btContinue);
        tvRedeemLb = findViewById(R.id.tvRedeemLbl);
        ivCoins = findViewById(R.id.ivCoins);
        tvTerms = findViewById(R.id.tvTerms);
        progressBar = findViewById(R.id.progressBar);
        btGoToStore = findViewById(R.id.btGoToStore);
        btGoToStore.setOnClickListener(view -> {
            if (couponsDetails != null) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(getString(R.string.copied), couponsDetails.couponCode);
                if (clipboard != null) {
                    if (!couponsDetails.couponCode.equals("null")) {
                        clipboard.setPrimaryClip(clip);
                        AlertUtils.showToast(getString(R.string.copied_to_clipboard), Toast.LENGTH_LONG, CouponsDetailsActivity.this);
                    }
                }
                Intent viewIntent =
                        new Intent("android.intent.action.VIEW",
                                Uri.parse(couponsDetails.outLinkUrl));
                startActivity(viewIntent);
            }
        });
        tvTerms.setOnClickListener(v -> {
            Intent viewIntent =
                    new Intent("android.intent.action.VIEW",
                            Uri.parse(Url.TermsCondition));
            startActivity(viewIntent);
        });
        if (couponsDetails != null) {
            Glide.with(this).load(couponsDetails.imageUrl).into(ivLogo);
            tvName.setText(couponsDetails.storeName);
            tvCat.setText(couponsDetails.category.catName);
            tvTitle.setText(couponsDetails.title);
            String des = "<p>" + couponsDetails.description.replaceAll("(\\\\r\\\\n|\\\\n)", "<br />") + "</p>";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                tvDescription.setText(Html.fromHtml(des, Html.FROM_HTML_MODE_LEGACY));
            } else {
                tvDescription.setText(Html.fromHtml(des));
            }
            String expiresDate = Utility.formatDate("yyyy-MM-DD HH:mm:ss", "MMM dd", (couponsDetails.expiresOn));
            if (expiresDate.isEmpty()) {
                tvExpires.setVisibility(View.GONE);
            }
            tvExpires.setText(getString(R.string.expires_on) + expiresDate);
            tvCoins.setText(couponsDetails.coins + getString(R.string.icoins));
            tvCode.setText(couponsDetails.couponCode);

            if (couponsDetails.isRedeem) {
                couponFlow();
                changeRedeemText();
            }
        }
        btCopy.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText(getString(R.string.copied), couponsDetails.couponCode);
            if (clipboard != null) {
                clipboard.setPrimaryClip(clip);
                AlertUtils.showToast(getString(R.string.copied_Coupon) + couponsDetails.couponCode, Toast.LENGTH_LONG, CouponsDetailsActivity.this);
            }
        });

        btRedeem.setOnClickListener(v -> {
            if (couponsDetails != null) {
                if (couponsDetails.isRedeem) {
                    if (couponsDetails.isDeal) {
                        Intent viewIntent =
                                new Intent("android.intent.action.VIEW",
                                        Uri.parse(couponsDetails.outLinkUrl));
                        startActivity(viewIntent);
                    }
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    btRedeem.setVisibility(View.GONE);
                    HashMap<String, String> param = new HashMap<>();
                    couponsDetails.isRedeem = true;
                    param.put("coupon", prepareJsonString());
                    APIRequest apiRequest = new APIRequest(Url.REDEEM_COUPON, Request.Method.POST, param, null, CouponsDetailsActivity.this);
                    APIManager.request(apiRequest, new APIResponse() {
                        @Override
                        public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                            progressBar.setVisibility(View.GONE);
                            if (response != null) {
                                try {
                                    Log.d("Coupons Response", "[" + response + "]");
                                    Log.i("Couponsasdad", "[" + response + "]");
                                    JSONObject object = new JSONObject(response);
                                    if (object.has("type")) {
                                        String type = object.getString("type");
                                        String msg = "";
                                        if (object.has("msg")) {
                                            //msg = object.getString("msg");
                                            msg = Utility.getMessage(CouponsDetailsActivity.this, object.get("msg").toString());
                                        }
                                        if (type.equalsIgnoreCase("ok")) {
                                            couponsDetails.isRedeem = true;
                                            couponFlow();
                                            changeRedeemText();
                                            if (couponsDetails.couponCode != null) {
                                                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                                ClipData clip = ClipData.newPlainText(getString(R.string.copied), couponsDetails.couponCode);
                                                if (clipboard != null) {
                                                    if (!couponsDetails.couponCode.equals("null")) {
                                                        clipboard.setPrimaryClip(clip);
                                                        AlertUtils.showToast(getString(R.string.copied_to_clipboard), Toast.LENGTH_LONG, CouponsDetailsActivity.this);
                                                    }
                                                }
                                            }
                                            Intent viewIntent =
                                                    new Intent("android.intent.action.VIEW",
                                                            Uri.parse(couponsDetails.outLinkUrl));
                                            startActivity(viewIntent);
                                        } else {
                                            AlertUtils.showToast(msg, Toast.LENGTH_LONG, CouponsDetailsActivity.this);
                                        }
                                    }
                                    /*response.replaceFirst("<font>.*?</font>", "");
                                    int jsonStart = response.indexOf("{");
                                    int jsonEnd = response.indexOf("}");
                                    if (jsonStart >= 0 && jsonEnd >= 0 && jsonEnd > jsonStart) {
                                        response = response.substring(jsonStart, jsonEnd + 1);
                                        JSONObject object1 = new JSONObject(response);
                                        if (object1.has("type")) {
                                            String type = object1.getString("type");
                                            String msg = "";
                                            if (object1.has("msg")) {
                                                msg = object1.getString("msg");
                                            }
                                            if (type.equalsIgnoreCase("ok")) {
                                                couponFlow();
                                                couponsDetails.isRedeem = true;
                                                changeRedeemText();
                                                Intent viewIntent =
                                                        new Intent("android.intent.action.VIEW",
                                                                Uri.parse(couponsDetails.outLinkUrl));
                                                startActivity(viewIntent);
                                            } else {
                                                AlertUtils.showToast(msg, Toast.LENGTH_LONG, CouponsDetailsActivity.this);
                                            }
                                        } else {
                                            AlertUtils.showToast("Some Error Occurred", 3, getApplicationContext());
                                        }
                                    }*/
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Log.d("Coupon Exception", e.getLocalizedMessage());
                                    Log.d("Coupon Exception", e.getMessage());
                                    progressBar.setVisibility(View.GONE);
                                    btRedeem.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    private void couponFlow() {
        if (couponsDetails.isDeal) {
            // for get deal flow
            btRedeem.setVisibility(View.VISIBLE);
            clCoupon.setVisibility(View.GONE);
            btRedeem.setText(getString(R.string.use_offer));
            btGoToStore.setVisibility(View.GONE);
        } else {
            // for coupon flow
            clCoupon.setVisibility(View.VISIBLE);
            btRedeem.setVisibility(View.GONE);
            btGoToStore.setVisibility(View.VISIBLE);
        }
    }

    private void changeRedeemText() {
        tvRedeemLb.setText(getString(R.string.redeem_on));
        ivCoins.setVisibility(View.GONE);
        String redeemDate = Utility.formatDate("yyyy-MM-dd HH:mm:ss", "MMM dd", (couponsDetails.redeemDate));

        if (redeemDate.isEmpty()) {
            Date c = Calendar.getInstance().getTime();
            System.out.println(getString(R.string.current_time) + c);
            SimpleDateFormat df = new SimpleDateFormat("MMM dd", Locale.ENGLISH);
            redeemDate = df.format(c);
        }

        tvCoins.setText(redeemDate);
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 0);
        tvCoins.setLayoutParams(params);
        tvCoins.setTextColor(ContextCompat.getColor(CouponsDetailsActivity.this, R.color.white));
    }

    private String prepareJsonString() {
        return couponsDetails.toString()
                .replace("expiresOn", "expires_on")
                .replace("couponCode", "coupon_code")
                .replace("isDeal", "is_deal")
                .replace("outLinkUrl", "outlink_url")
                .replace("imageUrl", "image")
                .replace("rectImage", "image_rectangle")
                .replace("storeName", "store_name")
                .replace("catId", "category_id")
                .replace("catName", "category_name")
                .replace("iconUrl", "icon_url")
                .replace("noOfOffers", "num_offers")
                .replace("isRedeem", "isPurchased");
    }
}
