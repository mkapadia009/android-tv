package com.app.itaptv.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.app.itaptv.R;
import com.app.itaptv.activity.CouponRedemptionActivity;
import com.app.itaptv.adapter.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;

public class CouponFragment extends Fragment implements TabLayout.OnTabSelectedListener, View.OnClickListener {

    //UI
    TabLayout couponTab;
    ViewPager vpCoupons;
    ViewPagerAdapter viewPagerAdapter;
    //Variables
    String[] arrTabLabels;
    Button btRedeemCoupon;
    View view;

    public CouponFragment() {
        // Required empty public constructor
    }

    public static CouponFragment newInstance() {
        return new CouponFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_coupon, container, false);
            init();
        }
        return view;
    }

    /**
     * Initializes views and set tab
     */
    private void init() {
        arrTabLabels = getResources().getStringArray(R.array.arra_coupons_tabs);
        couponTab = view.findViewById(R.id.couponsTabs);
        vpCoupons = view.findViewById(R.id.vpCoupon);
        btRedeemCoupon = view.findViewById(R.id.btRedeemCoupon);
        setTabsWithViewPager();
        btRedeemCoupon.setOnClickListener(this);

    }

    /**
     * Initializes Tabs with ViewPager
     */
    private void setTabsWithViewPager() {
        vpCoupons.setOffscreenPageLimit(1);
        // Set ViewPager adapter
        viewPagerAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        AllCouponsTabFragment allCouponsTabFragment = new AllCouponsTabFragment();
        MyCouponsTabFragment myCouponsTabFragment = new MyCouponsTabFragment();
        myCouponsTabFragment.vpCoupons = vpCoupons;
        viewPagerAdapter.addFragment(allCouponsTabFragment, arrTabLabels[0]);
        viewPagerAdapter.addFragment(myCouponsTabFragment, arrTabLabels[1]);
        vpCoupons.setAdapter(viewPagerAdapter);
        couponTab.setupWithViewPager(vpCoupons);
        couponTab.addOnTabSelectedListener(this);

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btRedeemCoupon:
                startActivity(new Intent(getContext(), CouponRedemptionActivity.class).putExtra("title", getResources().getString(R.string.label_redeem_coupon)));
                break;
        }
    }
}
