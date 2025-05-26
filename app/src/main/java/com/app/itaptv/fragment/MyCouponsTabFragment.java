package com.app.itaptv.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.app.itaptv.API.APIManager;
import com.app.itaptv.API.APIRequest;
import com.app.itaptv.API.APIResponse;
import com.app.itaptv.API.Url;
import com.app.itaptv.R;
import com.app.itaptv.activity.vouchers.CouponsDetailsActivity;
import com.app.itaptv.holder.CouponListingHolder;
import com.app.itaptv.structure.vouchers.CouponsDetails;
import com.kalpesh.krecycleradapter.KRecyclerViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyCouponsTabFragment extends Fragment {

    //UI
    ProgressBar progressBar;
    TextView tvEmpty;
    Button btBuyNow;
    //Variables
    View view;
    ArrayList<CouponsDetails> couponsDetailsArrayList = new ArrayList<>();
    public ViewPager vpCoupons;

    public MyCouponsTabFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_my_coupons_tab, container, false);
        setUI();
        // emptyState();
        return view;
    }


    private void setUI() {
        progressBar = view.findViewById(R.id.progressBar);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        btBuyNow = view.findViewById(R.id.btBuyNow);
        btBuyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (vpCoupons != null) {
                    vpCoupons.setCurrentItem(0);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    private void getData() {
        progressBar.setVisibility(View.VISIBLE);

        APIRequest apiRequest = new APIRequest(Url.MY_COUPONS, Request.Method.GET, null, null, getActivity());
        APIManager.request(apiRequest, new APIResponse() {
            @Override
            public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                progressBar.setVisibility(View.GONE);
                if (response != null) {
                    try {
                        JSONObject object = new JSONObject(response);
                        if (object.has("msg")) {
                            JSONObject object1 = object.getJSONObject("msg");
                            JSONArray array = object1.getJSONArray("items");
                            couponsDetailsArrayList.clear();
                            for (int i = 0; i < array.length(); i++) {
                                couponsDetailsArrayList.add(new CouponsDetails(array.getJSONObject(i), false));
                            }
                            emptyState();
                            setupRecylerView();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressBar.setVisibility(View.GONE);
                        emptyState();
                    }
                }
            }
        });
    }

    private void emptyState() {
        if (couponsDetailsArrayList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            btBuyNow.setVisibility(View.VISIBLE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            btBuyNow.setVisibility(View.GONE);
        }
    }

    private void setupRecylerView() {
        RecyclerView rvCoupon = view.findViewById(R.id.rvMyCoupons);
        rvCoupon.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        KRecyclerViewAdapter adapter = new KRecyclerViewAdapter(getActivity(), couponsDetailsArrayList, (viewGroup, i) -> {
            View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_coupon_listing, viewGroup, false);
            return new CouponListingHolder(layoutView);
        }, (kRecyclerViewHolder, o, i) -> {
            // item clicked
            CouponsDetails couponsDetails = (CouponsDetails) o;
            Intent intent = new Intent(getActivity(), CouponsDetailsActivity.class);
            intent.putExtra(CouponsDetailsActivity.COUPON_DETAILS_OBJECT, couponsDetails);
            startActivity(intent);
        });
        rvCoupon.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
