package com.app.itaptv.activity.vouchers;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.app.itaptv.API.APIManager;
import com.app.itaptv.API.APIRequest;
import com.app.itaptv.API.APIResponse;
import com.app.itaptv.API.Url;
import com.app.itaptv.R;
import com.app.itaptv.activity.BaseActivity;
import com.app.itaptv.holder.CouponListingHolder;
import com.app.itaptv.structure.vouchers.AllCouponsFeedsData;
import com.app.itaptv.structure.vouchers.AllCouponsFeedsDataContents;
import com.app.itaptv.structure.vouchers.CouponsDetails;
import com.bumptech.glide.Glide;
import com.kalpesh.krecycleradapter.KRecyclerViewAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CouponsListingActivity extends BaseActivity {

    //UI
    ConstraintLayout clTop;
    ImageView ivLogo;
    TextView tvName, tvEmptyStates;
    ProgressBar progressBar;
    //Variables
    ArrayList<CouponsDetails> couponsDetailsArrayList = new ArrayList<>();
    public static String COUPON_OBJECT_KEY = "couponObject";
    public static String COUPON_PARENT_OBJECT_KEY = "couponParentObject";
    AllCouponsFeedsDataContents allCouponsFeedsDataContents;
    AllCouponsFeedsData allCouponsFeedsData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupons_listing);
        setToolbar();
        getExtras();
    }

    private void getExtras() {
        if (getIntent() != null) {
            if (getIntent().hasExtra(COUPON_OBJECT_KEY)) {
                allCouponsFeedsDataContents = getIntent().getParcelableExtra(COUPON_OBJECT_KEY);
                allCouponsFeedsData = getIntent().getParcelableExtra(COUPON_PARENT_OBJECT_KEY);
                setUI();
            }
        }
    }

    private void setUI() {
        progressBar = findViewById(R.id.progressBar);
        clTop = findViewById(R.id.clTop);
        ivLogo = findViewById(R.id.ivLogo);
        tvName = findViewById(R.id.tvName);
        tvEmptyStates = findViewById(R.id.tvEmptyState);
        if (allCouponsFeedsDataContents != null) {
            String url = allCouponsFeedsDataContents.rectImage;
            if (allCouponsFeedsDataContents.rectImage.isEmpty()) {
                url = allCouponsFeedsDataContents.image;
                ivLogo.setPadding(10, 10, 10, 10);
            }
            Glide.with(this).load(url).into(ivLogo);
            tvName.setText(allCouponsFeedsDataContents.name);
        }
    }

    private void getData() {
        if (allCouponsFeedsDataContents != null) {
            progressBar.setVisibility(View.VISIBLE);
            HashMap<String, String> params = new HashMap<>();
            params.put("id", String.valueOf(allCouponsFeedsDataContents.id));

            String url = Url.GET_COUPONS_STORE_OFFERS;
            if (allCouponsFeedsDataContents.type.equalsIgnoreCase(AllCouponsFeedsDataContents.COUPON_TYPE_CATEGORIES)) {
                url = Url.GET_COUPONS_CAT_OFFERS;
            }
            APIRequest apiRequest = new APIRequest(url + "&id=" + allCouponsFeedsDataContents.id + "&category=" + String.valueOf(allCouponsFeedsData.id), Request.Method.GET, null, null, this);
            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    progressBar.setVisibility(View.GONE);
                    if (response != null) {
                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.has("contents")) {
                                JSONArray array = object.getJSONArray("contents");
                                boolean isFromStore = false;
                                if (allCouponsFeedsDataContents.type.equalsIgnoreCase(AllCouponsFeedsDataContents.COUPON_TYPE_STORE)) {
                                    isFromStore = true;
                                }
                                couponsDetailsArrayList.clear();
                                for (int i = 0; i < array.length(); i++) {
                                    couponsDetailsArrayList.add(new CouponsDetails(array.getJSONObject(i), isFromStore));
                                }
                            }
                            setupRecylerView();
                            setEmptyStates();
                        } catch (Exception e) {
                            e.printStackTrace();
                            progressBar.setVisibility(View.GONE);
                            setEmptyStates();
                        }
                    }
                }
            });
        }
    }

    private void setEmptyStates() {
        if (couponsDetailsArrayList.size() == 0) {
            tvEmptyStates.setVisibility(View.VISIBLE);
        } else {
            tvEmptyStates.setVisibility(View.GONE);
        }
    }

    private void setToolbar() {
        setToolbar(true);
        showToolbarBackButton(R.drawable.white_arrow);
        showToolbarTitle(false);
    }

    private void setupRecylerView() {
        RecyclerView rvCoupon = findViewById(R.id.rvCouponListing);
        rvCoupon.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        KRecyclerViewAdapter adapter = new KRecyclerViewAdapter(this, couponsDetailsArrayList, (viewGroup, i) -> {
            View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_coupon_listing, viewGroup, false);
            return new CouponListingHolder(layoutView);
        }, (kRecyclerViewHolder, o, i) -> {
            // item clicked
            CouponsDetails couponsDetails = (CouponsDetails) o;
            Intent intent = new Intent(this, CouponsDetailsActivity.class);
            intent.putExtra(CouponsDetailsActivity.COUPON_DETAILS_OBJECT, couponsDetails);
            startActivity(intent);
        });

        rvCoupon.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }
}
