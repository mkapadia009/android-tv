package com.app.itaptv.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.app.itaptv.API.APIManager
import com.app.itaptv.API.APIRequest
import com.app.itaptv.API.Url
import com.app.itaptv.R
import com.app.itaptv.activity.HomeActivity
import com.app.itaptv.activity.MyCouponsActivity
import com.app.itaptv.holder.CouponsHolder
import com.app.itaptv.structure.CouponsData
import com.app.itaptv.structure.FeedContentData
import com.app.itaptv.structure.vouchers.AllCoupons
import com.app.itaptv.structure.vouchers.AllCouponsFeedsData
import com.app.itaptv.utils.Log
import com.kalpesh.krecycleradapter.Interface.KRecyclerItemClickListener
import com.kalpesh.krecycleradapter.Interface.KRecyclerViewHolderCallBack
import com.kalpesh.krecycleradapter.KRecyclerViewAdapter
import com.paginate.Paginate
import com.paginate.recycler.LoadingListItemCreator
import kotlinx.android.synthetic.main.fragment_coupons.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class CouponFrag : Fragment() {

    private lateinit var rootView: View

    private lateinit var rvCoupon: RecyclerView

    //Variables
    var couponsDataArrayList = ArrayList<CouponsData>()
    var allCoupons: AllCoupons? = null
    var feedsDataArrayList = ArrayList<AllCouponsFeedsData>()
    var arrayListPageWiseData = ArrayList<AllCouponsFeedsData>()
    private lateinit var progressBar: ProgressBar
    private lateinit var nsvCombinedTabFrag: NestedScrollView;

    var GET_ALL_COUPONS_DATA = ""
    var adapterCoupons: KRecyclerViewAdapter? = null

    // Pagination
    private val GRID_SPAN = 1
    private var loading = false
    private var handler: Handler? = null
    private var paginate: Paginate? = null
    protected var networkDelay: Long = 2000
    protected var customLoadingListItem = false
    var nextPageNo = 1
    var currentIndex = 0

    var homeActivity: HomeActivity? = null

    companion object {
        @JvmStatic
        fun newInstance(): CouponFrag {
            return CouponFrag()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_coupons, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        //val myCouponsTabFragment = MyCouponsTabFragment()
        tabMyCoupons.setOnClickListener {
            val id = resources.getResourceEntryName(tabMyCoupons.id)
            Log.i("CouponFrag", id.toString())
            //openFragment(myCouponsTabFragment)
            val intent = Intent(context, MyCouponsActivity::class.java)
            startActivity(intent)
            if (activity != null && activity is HomeActivity) {
                homeActivity = activity as HomeActivity?
                if (homeActivity != null) {
                    homeActivity!!.showAd(id.toString())
                }
            }
        }
    }

    private fun init() {
        nsvCombinedTabFrag = rootView.findViewById(R.id.nsvCombinedTabFrag)

        nsvCombinedTabFrag.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, _, oldScrollY ->
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    Log.i(FeedContentData.TAG, "BOTTOM SCROLL")
                    if (nextPageNo != 0) {
                        getAllCouponsData()
                    }
                }
            })

        getAllCouponsData()
        setCouponRecyclerView()
        //initializeContentPagination()
    }

    /*fun openFragment(fragment: Fragment) {
        val fragmentTransaction: FragmentTransaction = activity?.supportFragmentManager!!.beginTransaction()
        fragmentTransaction.replace(R.id.couponsContainer, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }*/

    private fun setCouponRecyclerView() {
        progressBar = rootView.findViewById(R.id.progressBar)
        rvCoupon = rootView.findViewById(R.id.rvTabCoupons)
        rvCoupon.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        adapterCoupons = KRecyclerViewAdapter(
            activity,
            feedsDataArrayList,
            KRecyclerViewHolderCallBack { viewGroup, i ->
                val layoutView = LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.row_coupons, viewGroup, false)
                CouponsHolder(layoutView)
            },
            KRecyclerItemClickListener { _, o, i -> })
        rvCoupon.adapter = adapterCoupons
        adapterCoupons!!.notifyDataSetChanged()
    }

    /************************ PAGINATION METHODS -- START  */
    private fun initializeContentPagination() {
        customLoadingListItem = false
        paginate?.unbind()
        loading = false
        handler = Handler()
        handler!!.removeCallbacks(fakeCallback)
        paginate = Paginate.with(rvCoupon, callbacks)
            .setLoadingTriggerThreshold(0)
            .addLoadingListItem(true)
            .setLoadingListItemCreator(if (customLoadingListItem) CustomLoadingListItemCreator() else null) /* .setLoadingListItemSpanSizeLookup(new LoadingListItemSpanLookup() {
                     @Override
                     public int getSpanSize() {
                         return GRID_SPAN;
                     }
                 })*/
            .build()
    }

    var callbacks: Paginate.Callbacks = object : Paginate.Callbacks {
        override fun onLoadMore() {
            // Load next page of data (e.g. network or database)
            loading = true
            handler!!.postDelayed(fakeCallback, networkDelay)
        }

        override fun isLoading(): Boolean {
            // Indicate whether new page loading is in progress or not
            return loading // Return boolean weather data is already loading or not
        }

        override fun hasLoadedAllItems(): Boolean {
            // Indicate whether all data (pages) are loaded or not
            return nextPageNo == 0 // If all pages are loaded return true
        }
    }

    private val fakeCallback = Runnable {
        if (nextPageNo != 0) {
            //  getAllCouponsData()
        }
    }

    private class CustomLoadingListItemCreator : LoadingListItemCreator {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val view = inflater.inflate(R.layout.custom_loading_list_item, parent, false)
            return VH(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val vh = holder as VH
            //vh.tvLoading.text = String.format("Total items loaded: %d.\nLoading more...", adapterCoupons.getItemCount())

            // This is how you can make full span if you are using StaggeredGridLayoutManager
            /*if (rvCoupon.getLayoutManager() is StaggeredGridLayoutManager) {
                val params = vh.itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams
                params.isFullSpan = true
            }*/
        }
    }

    internal class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvLoading: TextView

        init {
            tvLoading = itemView.findViewById(R.id.tv_loading_text)
        }
    }

    /************************ PAGINATION METHODS -- END  */
    private fun getAllCouponsData() {
        progressBar = rootView.findViewById(R.id.progressBar)
        progressBar.visibility = View.VISIBLE
        val url = Url.GET_ALL_COUPONS_DATA + "&f_page_no=" + nextPageNo
        val apiRequest = APIRequest(url, Request.Method.GET, null, null, activity)
        APIManager.request(apiRequest) { response, error, headers, statusCode ->
            progressBar.visibility = View.GONE
            if (response != null) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.has("msg")) {
                        allCoupons = AllCoupons(jsonObject.getJSONObject("msg"))
                        val jsonObjectMessage = jsonObject.getJSONObject("msg")
                        nextPageNo =
                            if (jsonObjectMessage.has("next_page")) jsonObjectMessage.getInt("next_page") else 0
                        feedsDataArrayList.addAll(
                            adapterCoupons!!.itemCount,
                            allCoupons!!.feedsDataArrayList
                        )
                    }
                    adapterCoupons!!.notifyDataSetChanged()
                    loading = false
                } catch (e: JSONException) {
                    e.printStackTrace()
                    progressBar.visibility = View.GONE
                }
            }
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {

        }
    }

}
