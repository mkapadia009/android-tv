package com.app.itaptv.activity.vouchers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.app.itaptv.API.APIManager;
import com.app.itaptv.API.APIRequest;
import com.app.itaptv.API.APIResponse;
import com.app.itaptv.API.Url;
import com.app.itaptv.R;
import com.app.itaptv.activity.BaseActivity;
import com.app.itaptv.holder.CouponsRowHolder;
import com.app.itaptv.structure.vouchers.AllCouponsFeedsData;
import com.app.itaptv.structure.vouchers.AllCouponsFeedsDataContents;
import com.kalpesh.krecycleradapter.KRecyclerViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import static com.app.itaptv.activity.vouchers.CouponsListingActivity.COUPON_PARENT_OBJECT_KEY;

public class CouponsViewAllActivity extends BaseActivity {
    // UI
    // Variables
    public ArrayList<AllCouponsFeedsDataContents> contentsArrayList = new ArrayList<>();
    public static String ID_KEY = "idKey";
    public static String NAME = "couponName";
    String id = "";
    String name = "";
    Context mContext = CouponsViewAllActivity.this;
    ProgressBar progressBar;
    AllCouponsFeedsData allCouponsFeedsData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupons_view_all);
        setUI();
        setToolbar();
        getExtras();
        getData();
    }

    private void setUI() {
        progressBar = findViewById(R.id.progressBar);
    }

    void getExtras() {
        if (getIntent() != null) {
            if (getIntent().hasExtra(ID_KEY)) {
                id = getIntent().getStringExtra(ID_KEY);
            }
            if (getIntent().hasExtra(NAME)) {
                name = getIntent().getStringExtra(NAME);
                setToolbarTitle(name.toUpperCase());
                allCouponsFeedsData = getIntent().getParcelableExtra(COUPON_PARENT_OBJECT_KEY);
            }
        }
    }

    private void setToolbar() {
        setToolbar(true);
        showToolbarBackButton(R.drawable.white_arrow);
        showToolbarTitle(true);
        setCustomizedTitle(0);
    }

    void getData() {
        progressBar.setVisibility(View.VISIBLE);
        APIRequest apiRequest = new APIRequest(Url.COUPONS_VIEW_ALL + "&id=" + id, Request.Method.GET, null, null, mContext);
        APIManager.request(apiRequest, new APIResponse() {
            @Override
            public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                if (response != null) {
                    progressBar.setVisibility(View.GONE);
                    try {
                        JSONObject object = new JSONObject(response);
                        if (object.has("msg")) {
                            JSONObject objectMSG = object.getJSONObject("msg");
                            JSONArray arrayContents = objectMSG.getJSONArray("contents");
                            for (int i = 0; i < arrayContents.length(); i++) {
                                contentsArrayList.add(new AllCouponsFeedsDataContents(arrayContents.getJSONObject(i)));
                            }
                            setCouponDataRecyclerView();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private void setCouponDataRecyclerView() {
        RecyclerView rvCoupon = findViewById(R.id.rvViewAll);
        rvCoupon.setLayoutManager(new GridLayoutManager(mContext, 3));

        KRecyclerViewAdapter adapter = new KRecyclerViewAdapter(mContext, contentsArrayList, (viewGroup, i) -> {
            View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_coupons_data, viewGroup, false);
            return new CouponsRowHolder(layoutView);
        }, (kRecyclerViewHolder, o, i) -> {
            // item clicked
            AllCouponsFeedsDataContents couponsFeedsDataContents = (AllCouponsFeedsDataContents) o;
            Intent intent = new Intent(mContext, CouponsListingActivity.class);
            intent.putExtra(CouponsListingActivity.COUPON_OBJECT_KEY, couponsFeedsDataContents);
            intent.putExtra(CouponsListingActivity.COUPON_PARENT_OBJECT_KEY, allCouponsFeedsData);
            startActivity(intent);
        });

        rvCoupon.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
