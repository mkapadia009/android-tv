package com.app.itaptv.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.android.volley.Request
import com.app.itaptv.API.APIManager
import com.app.itaptv.API.APIRequest
import com.app.itaptv.API.Url
import com.app.itaptv.R
import com.app.itaptv.activity.HomeActivity
import com.app.itaptv.adapter.ViewPagerAdapter
import com.app.itaptv.structure.RedeemTabsList
import com.app.itaptv.utils.AnalyticsTracker
import com.app.itaptv.utils.Constant
import com.app.itaptv.utils.Log
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_coupon_redemption.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class RedeemFrag : Fragment(), TabLayout.OnTabSelectedListener {

    private lateinit var rootView: View
    lateinit var tabLayoutRedeem: TabLayout
    lateinit var viewPagerRedeem: ViewPager
    lateinit var viewPagerRedeemAdapter: ViewPagerAdapter

    private var arrayListListOfTabs = ArrayList<RedeemTabsList>()

    // Tab Fragments
    var dealsFrag: DealsFrag = DealsFrag.newInstance()
    var couponFrag: CouponFrag = CouponFrag.newInstance()
    var vouchersFrag: VouchersFrag = VouchersFrag.newInstance()
    var winCashFrag: WinCashFrag = WinCashFrag.newInstance()

    var arrTabFragments = arrayOf(dealsFrag, couponFrag, vouchersFrag, winCashFrag)
    private var arrayRedeemTabLabels = ArrayList<String>()
    private var arrayListListStringOfTabs = ArrayList<Fragment>()

    var homeActivity: HomeActivity? = null

    companion object {
        @JvmStatic
        fun newInstance(): RedeemFrag {
            return RedeemFrag()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_redeem, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {

        homeActivity = HomeActivity()
        //arrayRedeemTabLabels = resources.getStringArray(R.array.array_redeem_tabs)
        tabLayoutRedeem = rootView.findViewById(R.id.tabLayoutRedeem)
        viewPagerRedeem = rootView.findViewById(R.id.viewPagerRedeem)

        getActiveTabs()
        //setTabsWithViewPager()
    }

    private fun getActiveTabs() {
        try {
            val params: Map<String, String> = HashMap()
            val apiRequest =
                APIRequest(Url.GET_REDEEM_TABS, Request.Method.GET, params, null, context)
            apiRequest.showLoader = false
            APIManager.request(apiRequest) { response, error, _, _ ->
                handleTabsResponse(response, error)
            }
        } catch (e: Exception) {

        }
    }

    private fun handleTabsResponse(response: String?, error: java.lang.Exception?) {
        //arrayListFeedData.clear();
        try {
            if (error != null) {

            } else {
                if (response != null) {
                    val jsonObjectResponse = JSONObject(response)
                    val type =
                        if (jsonObjectResponse.has("type")) jsonObjectResponse.getString("type") else ""
                    if (type.equals("error", ignoreCase = true)) {
                        val message =
                            if (jsonObjectResponse.has("msg")) jsonObjectResponse.getString("msg") else APIManager.GENERIC_API_ERROR_MESSAGE
                        showError(message)
                    } else if (type.equals("ok", ignoreCase = true)) {
                        val jsonArrayMsg = jsonObjectResponse.getJSONArray("msg")
                        handleTabsJSONArray(jsonArrayMsg)
                    }
                }
            }
        } catch (e: JSONException) {
        }
    }

    private fun showError(error: String?) {
        var errorMessage = error
        if (context != null) {
            if (errorMessage == null) errorMessage = APIManager.GENERIC_API_ERROR_MESSAGE
            //AlertUtils.showAlert(getString(R.string.label_error), errorMessage, null, getContext(), null);
        }
    }

    private fun handleTabsJSONArray(dataJSON: JSONArray) {
        arrayListListOfTabs.clear()
        arrayRedeemTabLabels.clear()
        arrayListListStringOfTabs.clear()
        for (i in 0 until dataJSON.length()) {
            val tabs = RedeemTabsList(dataJSON.getJSONObject(i))
            arrayListListOfTabs.add(tabs)
            if (tabs.status == "Enable") {
                when (tabs.id) {
                    Constant.SHOP_ID_FRAG -> {
                        arrayListListStringOfTabs.add(dealsFrag)
                        arrayRedeemTabLabels.add(tabs.title)
                    }
                    Constant.COUPONS_ID_FRAG -> {
                        arrayListListStringOfTabs.add(couponFrag)
                        arrayRedeemTabLabels.add(tabs.title)
                    }
                    Constant.VOUCHER_ID_FRAG -> {
                        arrayListListStringOfTabs.add(vouchersFrag)
                        arrayRedeemTabLabels.add(tabs.title)
                    }
                    Constant.WIN_CASH_ID_FRAG -> {
                        arrayListListStringOfTabs.add(winCashFrag)
                        arrayRedeemTabLabels.add(tabs.title)
                    }
                }
                /*when (tabs.title) {
                    Constant.SHOP_FRAG -> {
                        arrayListListStringOfTabs.add(shopFrag)
                        arrayRedeemTabLabels.add(resources.getString(R.string.label_shop))
                    }
                    Constant.COUPONS_FRAG -> {
                        arrayListListStringOfTabs.add(couponFrag)
                        arrayRedeemTabLabels.add(resources.getString(R.string.label_coupons))
                    }
                    Constant.VOUCHER_FRAG -> {
                        arrayListListStringOfTabs.add(vouchersFrag)
                        arrayRedeemTabLabels.add(resources.getString(R.string.label_voucher))
                    }
                    Constant.WIN_CASH_FRAG -> {
                        arrayListListStringOfTabs.add(winCashFrag)
                        arrayRedeemTabLabels.add(resources.getString(R.string.label_win_cash))
                    }
                }*/
            }
        }
        setTabsWithViewPager()
        viewPagerRedeemAdapter.notifyDataSetChanged()
    }

    /**
     * Initializes Tabs with ViewPager
     */
    private fun setTabsWithViewPager() {
        // Set ViewPager adapter
        viewPagerRedeemAdapter = ViewPagerAdapter(childFragmentManager)
        for (i in arrayRedeemTabLabels.indices) {
            // Sets arguments to pass data to the fragment
            val tabFragment: Fragment = arrayListListStringOfTabs[i]
            //tabFragment.setArguments(bundle);
            viewPagerRedeemAdapter.addFragment(tabFragment, arrayRedeemTabLabels[i])
        }
        viewPagerRedeem.offscreenPageLimit = 3
        viewPagerRedeem.adapter = viewPagerRedeemAdapter
        tabLayoutRedeem.setupWithViewPager(viewPagerRedeem)
        tabLayoutRedeem.addOnTabSelectedListener(this)
        tabLayoutRedeem.getTabAt(ShopFrag.selectedTab)!!.select()
        ShopFrag.selectedTab=0
    }

    override fun onTabReselected(tab: TabLayout.Tab) {

    }

    override fun onTabUnselected(tab: TabLayout.Tab) {

    }

    override fun onTabSelected(tab: TabLayout.Tab) {
        viewPagerRedeem.currentItem = tab.position;
        Log.i("RedeemFrag", arrayListListOfTabs[tab.position].id);
        showAd(arrayListListOfTabs[tab.position].id.toString());
        if (etRedeemCode != null) {
            etRedeemCode.error = null;
        }
        if (tab.position == 0) {
            AnalyticsTracker.startDurationShop()
        } else {
            AnalyticsTracker.stopDurationShop()
        }
    }

    override fun onResume() {
        super.onResume()
        if (viewPagerRedeem.currentItem == 0) {
            AnalyticsTracker.startDurationShop()
        } else {
            AnalyticsTracker.stopDurationShop()
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {

        }
    }

    fun showAd(id: String) {
        if (activity != null && activity is HomeActivity) {
            homeActivity = activity as HomeActivity?
            if (homeActivity != null) {
                homeActivity!!.showAd(id)
            }
        }
    }
}
