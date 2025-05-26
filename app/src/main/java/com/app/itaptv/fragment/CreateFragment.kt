package com.app.itaptv.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.app.itaptv.R
import com.app.itaptv.adapter.ViewPagerAdapter
import com.google.android.material.tabs.TabLayout

class CreateFragment : Fragment(), TabLayout.OnTabSelectedListener {

    private lateinit var rootView: View
    lateinit var tabLayoutCreate: TabLayout
    lateinit var viewPagerCreate: ViewPager
    lateinit var viewPagerCreateAdapter: ViewPagerAdapter

    companion object {
        fun newInstance(): CreateFragment {
            return CreateFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_create, container, false)
        init()
        return rootView;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private fun init() {
        tabLayoutCreate = rootView.findViewById(R.id.tabLayoutCreate)
        viewPagerCreate = rootView.findViewById(R.id.viewPagerCreate)
        setTabsWithViewPager()
    }

    private fun setTabsWithViewPager() {
        // Set ViewPager adapter
        viewPagerCreateAdapter = ViewPagerAdapter(childFragmentManager)
        viewPagerCreateAdapter.addFragment(WowTalkiesFragment.newInstance(), "WowTalkies")
        viewPagerCreateAdapter.addFragment(AvatarFragment.newInstance(), "Avatar")
        viewPagerCreate.adapter = viewPagerCreateAdapter
        tabLayoutCreate.setupWithViewPager(viewPagerCreate)
        tabLayoutCreate.addOnTabSelectedListener(this)
        tabLayoutCreate.getTabAt(ShopFrag.selectedTab)!!.select()
        ShopFrag.selectedTab = 0
    }


    override fun onTabSelected(tab: TabLayout.Tab?) {
        viewPagerCreate.currentItem = tab!!.position;
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
    }
}