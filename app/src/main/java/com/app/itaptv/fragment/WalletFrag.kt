package com.app.itaptv.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.android.volley.Request
import com.app.itaptv.API.APIManager
import com.app.itaptv.API.APIRequest
import com.app.itaptv.API.Url
import com.app.itaptv.R
import com.app.itaptv.activity.HomeActivity
import com.app.itaptv.activity.LearnMoreActivity
import com.app.itaptv.adapter.ViewPagerAdapter
import com.app.itaptv.utils.LocalStorage
import com.app.itaptv.utils.Log
import com.app.itaptv.utils.Wallet
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_earn.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class WalletFrag : Fragment(), TabLayout.OnTabSelectedListener {

    private lateinit var rootView: View
    lateinit var tabLayoutWallet: TabLayout
    lateinit var viewPagerWallet: ViewPager
    lateinit var viewPagerWalletAdapter: ViewPagerAdapter

    lateinit var arrayWalletTabLabels: Array<String>
    lateinit var arrayWalletTabId: Array<String>
    private var walletBalance = 0L
    private lateinit var coinsBalanceTv: TextView
    private lateinit var coinsExpiryTv: TextView
    private lateinit var helpWalletTv: TextView
    private var isRedeemOfferActive = false
    var is_fragment_visible = false
    var is_first_time_loading = true

    // Tab Fragments
    var leaderboardFrag: LeaderboardFrag = LeaderboardFrag.newInstance()
    var earnHistoryFrag: EarnHistoryFrag = EarnHistoryFrag.newInstance()
    var redeemHistoryFrag: RedeemHistoryFrag = RedeemHistoryFrag.newInstance()

    var arrTabFragments = arrayOf(leaderboardFrag, earnHistoryFrag, redeemHistoryFrag)

    var homeActivity: HomeActivity? = null

    companion object {
        @JvmStatic
        fun newInstance(): WalletFrag? {
            return WalletFrag()
        }
    }

    var earnCoinsBroadCast: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent != null) {
                if (intent.action == HomeActivity.EARN_COINS_KEY) {
                    getWalletBalance();
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_wallet, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {

        homeActivity = HomeActivity()
        arrayWalletTabLabels = resources.getStringArray(R.array.array_wallet_tabs)
        arrayWalletTabId = resources.getStringArray(R.array.array_wallet_tabs_id)
        tabLayoutWallet = rootView.findViewById(R.id.tabLayoutWallet)
        viewPagerWallet = rootView.findViewById(R.id.viewPagerWallet)
        coinsBalanceTv = rootView.findViewById(R.id.coinsBalanceTv)
        coinsExpiryTv = rootView.findViewById(R.id.coinsExpiryTv)
        helpWalletTv = rootView.findViewById(R.id.helpWalletTv)

        getWalletBalance()
        //getWalletExp()
        setTabsWithViewPager()
        helpWalletTv.setOnClickListener {
            val id = resources.getResourceEntryName(helpWalletTv.id)
            Log.i("WalletFrag", id.toString())
            startActivity(Intent(requireContext(), LearnMoreActivity::class.java))
            showAd(id.toString())
        }
    }

    fun getWalletBalance() {
        Wallet.getWalletBalance(context,
            { success: Boolean, error: String?, coins: Long, diamonds: Long, creditedCoins: Long, historyData: JSONArray?, historyCount: Int ->
                handleApp42Response(success, error, coins)
                success
            })
    }

    private fun handleApp42Response(success: Boolean, error: String?, coins: Long) {
        if (success) {
            if (is_fragment_visible) {
                walletBalance = coins
                coinsBalanceTv.text = walletBalance.toString()
            }
        } else {
            if (is_fragment_visible) {
                coinsBalanceTv.text = walletBalance.toString()
            }
        }
    }

    private fun getWalletExp() {
        try {
            val apiRequest =
                APIRequest(Url.WALLET_EXPIRY, Request.Method.POST, null, null, requireContext())
            APIManager.request(apiRequest) { response: String?, _: Exception?, _: Map<String?, String?>?, _: Int ->
                if (response != null) {
                    try {
                        val `object` = JSONObject(response)
                        if (`object`.has("msg")) {
                            val objectMessage = `object`.getJSONObject("msg")
                            if (objectMessage.has("message")) {
                                coinsExpiryTv.text = objectMessage.getString("message")
                                isRedeemOfferActive = objectMessage.getBoolean("isBannerActive")
                                if (isRedeemOfferActive) {
                                    LocalStorage.setIsBannerActive(true)
                                } else {
                                    LocalStorage.setIsBannerActive(false)
                                }
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Initializes Tabs with ViewPager
     */
    private fun setTabsWithViewPager() {
        // Set ViewPager adapter
        viewPagerWalletAdapter = ViewPagerAdapter(childFragmentManager)
        for (i in arrayWalletTabLabels.indices) {
            // Sets arguments to pass data to the fragment
            val tabFragment: Fragment = arrTabFragments[i]
            //tabFragment.setArguments(bundle);
            viewPagerWalletAdapter.addFragment(tabFragment, arrayWalletTabLabels[i])
        }
        viewPagerWallet.offscreenPageLimit = 3
        viewPagerWallet.adapter = viewPagerWalletAdapter
        tabLayoutWallet.setupWithViewPager(viewPagerWallet)
        tabLayoutWallet.addOnTabSelectedListener(this)

    }

    override fun onTabReselected(tab: TabLayout.Tab) {

    }

    override fun onTabUnselected(tab: TabLayout.Tab) {

    }

    override fun onTabSelected(tab: TabLayout.Tab) {
        viewPagerWallet.currentItem = tab.position;
        Log.i("WalletFrag", viewPagerWallet.currentItem.toString());
        showAd(arrayWalletTabId[viewPagerWallet.currentItem]);
    }

    override fun onResume() {
        super.onResume()
        val intentFilter3 = IntentFilter()
        intentFilter3.addCategory(Intent.CATEGORY_DEFAULT)
        intentFilter3.addAction(HomeActivity.EARN_COINS_KEY)
        activity?.registerReceiver(earnCoinsBroadCast, intentFilter3)
    }

    override fun onPause() {
        super.onPause()
        activity?.unregisterReceiver(earnCoinsBroadCast)
    }

    fun showAd(id: String) {
        if (activity != null && activity is HomeActivity) {
            homeActivity = activity as HomeActivity?
            if (homeActivity != null) {
                homeActivity!!.showAd(id)
            }
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        Log.d("WalletFrag", "WalletFrag$isVisibleToUser")
        if (isVisibleToUser) {
            //showAd()
            is_fragment_visible = true
            if (is_first_time_loading) {
                is_first_time_loading = false
            }
            getWalletExp()
            getWalletBalance()
        } else {
            is_fragment_visible = false
        }
    }

}
