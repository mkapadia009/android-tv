package com.app.itaptv.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.app.itaptv.R
import com.app.itaptv.adapter.ViewPagerAdapter
import com.app.itaptv.utils.Constant
import com.app.itaptv.utils.LocalStorage
import com.app.itaptv.utils.Log
import com.app.itaptv.utils.Utility
import com.google.android.material.tabs.TabLayout

class DiamondsFragment : Fragment(), TabLayout.OnTabSelectedListener {

    var rootView: View? = null
    lateinit var tabLayoutDiamonds: TabLayout
    lateinit var viewPagerDiamonds: ViewPager
    lateinit var viewPagerDiamondsAdapter: ViewPagerAdapter
    lateinit var arrayDiamondsTabLabels: Array<String>

    // Tab Fragments
    var buyDiamondsFragment: BuyDiamondsFragment = BuyDiamondsFragment.newInstance()
    var diamondWalletFragment: DiamondWalletFragment = DiamondWalletFragment.newInstance()
    var diamondRedeemFragment: DiamondRedeemFragment = DiamondRedeemFragment()

    var arrTabFragments = arrayOf(buyDiamondsFragment, diamondWalletFragment, diamondRedeemFragment)

    lateinit var bt_buyNow: Button

    companion object {
        fun newInstance(): DiamondsFragment {
            return DiamondsFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_diamonds, container, false)
        init()
        return rootView
    }

    fun init() {
        arrayDiamondsTabLabels = resources.getStringArray(R.array.array_diamonds_tabs)
        tabLayoutDiamonds = rootView!!.findViewById(R.id.tabLayoutDiamonds)
        viewPagerDiamonds = rootView!!.findViewById(R.id.viewPagerDiamonds)
        bt_buyNow = rootView!!.findViewById(R.id.bt_buyNow)

        bt_buyNow.setOnClickListener {
            val userId = LocalStorage.getUserId()
            val time = Utility.getCurrentDateTimeInMillis()
            val diamondsUrl: String =
                "https://itap.inspeero.com/diamond/?id=" + Utility.encryptData(
                    userId,
                    Constant.getSecretKeyDateTime(),
                    Constant.getIvParameterDateTime()
                ) + "&date=" + Utility.encryptData(
                    time,
                    Constant.getSecretKeyDateTime(),
                    Constant.getIvParameterDateTime()
                )
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(diamondsUrl))
            startActivity(browserIntent)
        }

        setTabsWithViewPager()
    }

    private fun setTabsWithViewPager() {
        // Set ViewPager adapter
        viewPagerDiamondsAdapter = ViewPagerAdapter(childFragmentManager)
        for (i in arrayDiamondsTabLabels.indices) {
            // Sets arguments to pass data to the fragment
            val tabFragment: Fragment = arrTabFragments[i]
            //tabFragment.setArguments(bundle);
            viewPagerDiamondsAdapter.addFragment(tabFragment, arrayDiamondsTabLabels[i])
        }
        viewPagerDiamonds.offscreenPageLimit = 3
        viewPagerDiamonds.adapter = viewPagerDiamondsAdapter
        tabLayoutDiamonds.setupWithViewPager(viewPagerDiamonds)
        tabLayoutDiamonds.addOnTabSelectedListener(this)
    }

    override fun onTabReselected(tab: TabLayout.Tab) {

    }

    override fun onTabUnselected(tab: TabLayout.Tab) {

    }

    override fun onTabSelected(tab: TabLayout.Tab) {
        viewPagerDiamonds.currentItem = tab.position;
        Log.i("WalletFrag", viewPagerDiamonds.currentItem.toString());
    }

}