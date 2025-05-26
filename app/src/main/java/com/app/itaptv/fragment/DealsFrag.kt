package com.app.itaptv.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.app.itaptv.API.APIManager
import com.app.itaptv.API.APIRequest
import com.app.itaptv.API.Url
import com.app.itaptv.R
import com.app.itaptv.activity.HomeActivity
import com.app.itaptv.holder.ProductHolder
import com.app.itaptv.structure.CommerceProductData
import com.kalpesh.krecycleradapter.KRecyclerViewAdapter
import com.paginate.Paginate
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class DealsFrag : Fragment() {

    private lateinit var rootView: View

    private lateinit var rvProduct: RecyclerView

    //Variables
    private var arrayListCommerceProduct = ArrayList<CommerceProductData>()
    var allProducts: CommerceProductData? = null
    var arrayListPageWiseData = ArrayList<CommerceProductData>()

    var adapterProducts: KRecyclerViewAdapter? = null

    // Pagination
    private var loading = false
    private var handler: Handler? = null
    private var paginate: Paginate? = null
    protected var networkDelay: Long = 2000
    protected var customLoadingListItem = false
    var nextPageNo = 1
    var currentIndex = 0

    var homeActivity: HomeActivity? = null

    private var isVisibleToUser = false

    companion object {
        @JvmStatic
        fun newInstance(): DealsFrag {
            return DealsFrag()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_shop, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {
        setCouponRecyclerView()
        initializeContentPagination()
    }

    /************************ PAGINATION METHODS -- START  */
    private fun initializeContentPagination() {
        nextPageNo = 1
        arrayListCommerceProduct.clear()
        customLoadingListItem = false
        paginate?.unbind()
        loading = false
        handler = Handler()
        handler!!.removeCallbacks(fakeCallback)
        paginate = Paginate.with(rvProduct, callbacks)
            .setLoadingTriggerThreshold(0)
            .addLoadingListItem(true)
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
            getCommerceProducts()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setCouponRecyclerView() {
        //progressBar = rootView.findViewById(R.id.progressBar)
        rvProduct = rootView.findViewById(R.id.rvCommissionProduct)
        rvProduct.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        adapterProducts = KRecyclerViewAdapter(
            activity,
            arrayListCommerceProduct,
            { viewGroup, i ->
                val layoutView = LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.row_product, viewGroup, false)
                ProductHolder(layoutView)
            },
            { _, o, i -> })
        rvProduct.adapter = adapterProducts
        adapterProducts!!.notifyDataSetChanged()
    }

    /************************ PAGINATION METHODS -- END  */
    @SuppressLint("NotifyDataSetChanged")
    private fun getCommerceProducts() {
        val url = Url.GET_COMMERCE_PRODUCTS + "&page_no=" + nextPageNo
        val apiRequest = APIRequest(url, Request.Method.GET, null, null, activity)
        APIManager.request(apiRequest) { response, error, headers, statusCode ->
            if (response != null) {
                try {
                    val jsonObjectResponse = JSONObject(response)
                    val type =
                        if (jsonObjectResponse.has("type")) jsonObjectResponse.getString("type") else ""
                    if (type.equals("error", ignoreCase = true)) {
                        val message =
                            if (jsonObjectResponse.has("msg")) jsonObjectResponse.getString("msg") else APIManager.GENERIC_API_ERROR_MESSAGE;
                    } else if (type.equals("ok", ignoreCase = true)) {
                        val jsonObjectMsg = jsonObjectResponse.getJSONObject("msg")
                        nextPageNo =
                            if (jsonObjectMsg.has("next_page")) jsonObjectMsg.getInt("next_page") else 0
                        val jsonArrayData = jsonObjectMsg.getJSONArray("data")
                        arrayListPageWiseData.clear()
                        for (i in 0 until jsonArrayData.length()) {
                            allProducts =
                                CommerceProductData(jsonArrayData.getJSONObject(i))
                            arrayListPageWiseData.add(allProducts!!)
                        }
                        arrayListCommerceProduct.addAll(
                            adapterProducts!!.itemCount,
                            arrayListPageWiseData
                        )
                    }
                    adapterProducts!!.notifyDataSetChanged()
                    loading = false
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }
    }
}
