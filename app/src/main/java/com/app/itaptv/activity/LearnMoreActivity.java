package com.app.itaptv.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.app.itaptv.R;
import com.app.itaptv.adapter.ViewPagerAdapter;
import com.app.itaptv.fragment.LearnMoreCoinTabFragment;
import com.app.itaptv.utils.LocalStorage;
import com.app.itaptv.utils.Utility;
import com.google.android.material.tabs.TabLayout;

public class LearnMoreActivity extends BaseActivity implements TabLayout.OnTabSelectedListener {
    String[] arrTabLabels;
    TabLayout tlCoinTabs;
    ViewPager vpCoin;
    ViewPagerAdapter viewPagerAdapter;
    LearnMoreCoinTabFragment tabFragment;
    RelativeLayout iconsRl;
    ImageView iTapLogo;
    TextView itapTv, taglineTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_learn_more);
            setTheme(R.style.Theme_Leanback);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setToolbar(false);
        showToolbarBackButton(R.drawable.white_arrow);
        showToolbarTitle(false);
        init();
        showAd("helpEarnTv");
    }

    /**
     * Initializes views and set tab
     */
    private void init() {
        arrTabLabels = getResources().getStringArray(R.array.arra_learn_more_tabs);
        tlCoinTabs = findViewById(R.id.tlCoinTabs);
        vpCoin = findViewById(R.id.vpCoin);
        //iconsRl = findViewById(R.id.rIconsParent);
        iTapLogo = findViewById(R.id.iTapLogoWithText);
        itapTv = findViewById(R.id.tv_itapcoin);
        taglineTv = findViewById(R.id.tv_itapcoindesc);

        //setTabsWithViewPager();
        TextView tvDescription = findViewById(R.id.tvDescription);

        if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean("fromDownloads")) {
            if (getIntent().getExtras().getString("learnMoreContent") != null && !getIntent().getExtras().getString("learnMoreContent").equalsIgnoreCase("null")
                    && !getIntent().getExtras().getString("learnMoreContent").isEmpty()) {
                tvDescription.setText(Html.fromHtml(getIntent().getExtras().getString("learnMoreContent")));
            } else {
                tvDescription.setText(R.string.offline_learn_more_content);
            }
            //iconsRl.setVisibility(View.GONE);
            itapTv.setVisibility(View.GONE);
            taglineTv.setVisibility(View.GONE);
        } else if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean("fromWinCash")) {
            if (getIntent().getExtras().getString("winCashLearnMoreContent") != null && !getIntent().getExtras().getString("winCashLearnMoreContent").equalsIgnoreCase("null")
                    && !getIntent().getExtras().getString("winCashLearnMoreContent").isEmpty()) {
                tvDescription.setText(Html.fromHtml(getIntent().getExtras().getString("winCashLearnMoreContent")));
            } else {
                tvDescription.setText(R.string.learn_more_description);
            }
            //iconsRl.setVisibility(View.GONE);
            itapTv.setVisibility(View.GONE);
            taglineTv.setVisibility(View.GONE);
        } else {
            tvDescription.setText(LocalStorage.getGeneralLearnMoreContent() != null ? Html.fromHtml(LocalStorage.getGeneralLearnMoreContent()) :
                    Html.fromHtml(getResources().getString(R.string.learn_more_description)));
        }
    }

    /**
     * Initializes Tabs with ViewPager
     */
    private void setTabsWithViewPager() {
        vpCoin.setOffscreenPageLimit(1);
        // Set ViewPager adapter
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        for (int i = 0; i < arrTabLabels.length; i++) {
            // Sets arguments to pass data to the fragment
            tabFragment = new LearnMoreCoinTabFragment();
            //tabFragment.setArguments(bundle);
            viewPagerAdapter.addFragment(tabFragment, arrTabLabels[i]);
        }
        vpCoin.setAdapter(viewPagerAdapter);
        tlCoinTabs.setupWithViewPager(vpCoin);

        tlCoinTabs.addOnTabSelectedListener(this);

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        tabFragment.callMethod(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
