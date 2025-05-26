package com.app.itaptv.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.app.itaptv.API.APIManager
import com.app.itaptv.API.APIRequest
import com.app.itaptv.API.Url
import com.app.itaptv.R
import com.app.itaptv.activity.HomeActivity
import com.app.itaptv.activity.LearnMoreActivity
import com.app.itaptv.holder.EarnPointsDataHolder
import com.app.itaptv.holder.ListenHolder
import com.app.itaptv.structure.EarnData
import com.app.itaptv.structure.EarnPointsData
import com.app.itaptv.utils.EmptyStateManager
import com.app.itaptv.utils.Log
import com.app.itaptv.utils.Utility
import com.kalpesh.krecycleradapter.Interface.KRecyclerItemClickListener
import com.kalpesh.krecycleradapter.Interface.KRecyclerViewHolderCallBack
import com.kalpesh.krecycleradapter.KRecyclerViewAdapter
import com.kalpesh.krecycleradapter.KRecyclerViewHolder
import com.paginate.Paginate
import com.paginate.recycler.LoadingListItemCreator
import kotlinx.android.synthetic.main.fragment_earn.*
import kotlinx.android.synthetic.main.progress_bar.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class EarnFrag : Fragment() {

    private lateinit var rootView: View

    private lateinit var rvEarnPoints: RecyclerView
    private lateinit var emptyState: EmptyStateManager
    private lateinit var adapetEarn: KRecyclerViewAdapter
    private val arrayListListenData = ArrayList<ViewGroup>()
    private val arrayFeedEarnData = ArrayList<EarnData>()
    private val arrayListPageWiseFeedData = ArrayList<EarnData>()
    private var arrayListEarnPointsData = ArrayList<EarnPointsData>()

    // Pagination
    private val GRID_SPAN = 1
    private var loading = false
    private var page = 0
    private var handler: Handler? = null
    private var paginate: Paginate? = null
    protected var networkDelay: Long = 2000
    protected var customLoadingListItem = false
    var nextPageNo = 1
    var currentFeedIndex = 0
    var feedUrl: String? = null
    var is_fragment_visible = false
    var is_first_time_loading = true

    var homeActivity: HomeActivity? = null

    companion object {
        @JvmStatic
        fun newInstance(): EarnFrag? {
            return EarnFrag()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_earn, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {

        setEarnRecyclerView()
        helpEarnTv.setOnClickListener {
            startActivity(Intent(requireContext(), LearnMoreActivity::class.java))
        }
    }

    /**
     * Initialize recycler view
     */
    private fun setEarnRecyclerView() {
        rvEarnPoints = rootView.findViewById(R.id.earningPointsRv)
        setUpEmptyState()
        showLoader()
        rvEarnPoints.visibility = View.INVISIBLE
        adapetEarn = KRecyclerViewAdapter(
            context,
            arrayListListenData,
            KRecyclerViewHolderCallBack { viewGroup, _ ->
                val layoutView = LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.row_linear_item, viewGroup, false)
                ListenHolder(layoutView)
            },
            KRecyclerItemClickListener { _: KRecyclerViewHolder?, _: Any?, _: Int -> })
        rvEarnPoints.adapter = adapetEarn
        rvEarnPoints.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        //initializePagination()
        //getEarnPointsAPI()
    }


    fun initializePagination() {
        arrayListListenData.clear()
        adapetEarn.notifyDataSetChanged()
        feedUrl = Url.GET_COIN_RULES
        customLoadingListItem = false
        paginate?.unbind()
        loading = false
        page = 0
        handler = Handler()
        handler!!.removeCallbacks(fakeCallback)
        /*paginate = Paginate.with(rvEarnPoints, callbacks)
                .setLoadingTriggerThreshold(0)
                .addLoadingListItem(true)
                .setLoadingListItemCreator(if (customLoadingListItem) CustomLoadingListItemCreator() else null)
                .setLoadingListItemSpanSizeLookup(LoadingListItemSpanLookup { GRID_SPAN })
                .build()*/
    }

    private class CustomLoadingListItemCreator : LoadingListItemCreator {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val view = inflater.inflate(R.layout.custom_loading_list_item, parent, false)
            return VH(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val vh = holder as VH
            //vh.tvLoading.text = String.format("Total items loaded: %d.\nLoading more...", adapterListen.getItemCount())

        }
    }

    internal class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvLoading: TextView = itemView.findViewById(R.id.tv_loading_text)

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
        if (page > 0) {
            feedUrl = feedUrl
        }
        if (nextPageNo != 0) {
            //getEarnPointsAPI()
        }
    }

    /**
     * Initialization of Empty State
     */
    fun setUpEmptyState() {
        emptyState =
            EmptyStateManager.setUpInFragment(view, activity as AppCompatActivity?) { action ->
                if (action == EmptyStateManager.ACTION_RETRY) {
                    //getEarnPointsAPI()
                }
            }
    }

    private fun getEarnPointsAPI() {
        arrayFeedEarnData.clear()
        arrayListListenData.clear()
        arrayListPageWiseFeedData.clear()
        try {
            val params: Map<String, String> = HashMap()
            val apiRequest =
                APIRequest(Url.GET_COIN_RULES, Request.Method.GET, params, null, context)
            apiRequest.showLoader = false
            APIManager.request(apiRequest) { response, error, _, _ ->
                dismissLoader()
                rvEarnPoints.visibility = View.VISIBLE
                handleFeedResponse(response, error)
            }
        } catch (e: Exception) {

        }
    }

    override fun onResume() {
        super.onResume()
        //arrayFeedEarnData.clear()
        //arrayListListenData.clear()
        //arrayListPageWiseFeedData.clear()
    }

    private fun handleFeedResponse(response: String?, error: java.lang.Exception?) {
        //arrayListEarnPointsData.clear();
        try {
            if (error != null) {
                updateEmptyState(error.message)
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
                        Thread(Runnable {
                            try {
                                currentFeedIndex += arrayListPageWiseFeedData.size
                                val position: Int = arrayListPageWiseFeedData.size
                                arrayListPageWiseFeedData.clear()
                                for (i in 0 until jsonArrayMsg.length()) {
                                    val earnData =
                                        EarnData(jsonArrayMsg.getJSONObject(i), position + i)
                                    arrayListPageWiseFeedData.add(earnData)
                                }
                                arrayFeedEarnData.addAll(
                                    currentFeedIndex,
                                    arrayListPageWiseFeedData
                                )
                                Handler(Looper.getMainLooper()).post { updateUI() }
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                            }
                        }).start()
                        /*arrayListEarnPointsData.clear()
                        arrayFeedEarnData.clear()
                        for (i in 0 until jsonArrayMsg.length()) {
                            val feedEarnData = EarnData(jsonArrayMsg.getJSONObject(i))
                            arrayFeedEarnData.add(feedEarnData)
                        }
                        updateUI()*/
                    }
                }
            }
        } catch (e: JSONException) {
            updateEmptyState(e.message)
        }
    }

    private fun updateUI() {
        /*for (earnData in arrayFeedEarnData) {
            Handler(Looper.getMainLooper()).post { setHorRecyclerView(earnData) }
        }*/
        /*if (page == 0) {
            if (is_fragment_visible) onFeedRefreshListener.refreshData(arra, 0)
        }*/
        Thread(Runnable {
            for (earnData in arrayListPageWiseFeedData) {
                Handler(Looper.getMainLooper()).post { setHorRecyclerView(earnData) }
            }
            Handler(Looper.getMainLooper()).post {
                adapetEarn.notifyDataSetChanged()
                loading = false
                page++
            }
        }).start()
    }

    private fun setHorRecyclerView(earnData: EarnData) {
        try {
            val viewContent = View.inflate(context, R.layout.row_earn_points, null)
            val constraintPointsParent =
                viewContent.findViewById<ConstraintLayout>(R.id.constraintPointsParent)
            val tvTitle = viewContent.findViewById<TextView>(R.id.pointHeaderTv)
            val rvPointsDeclaration: RecyclerView =
                viewContent.findViewById(R.id.pointsDeclarationRv)
            constraintPointsParent.visibility = View.VISIBLE
            tvTitle.text = earnData.title
            arrayListEarnPointsData = earnData.earnPointsDataArrayList

            var adapter: KRecyclerViewAdapter? = null
            if (arrayListEarnPointsData.size == 0) {
                constraintPointsParent.visibility = View.GONE
                return
            }
            adapter = KRecyclerViewAdapter(
                context,
                arrayListEarnPointsData,
                KRecyclerViewHolderCallBack { viewGroup: ViewGroup, i: Int ->
                    val layoutView = LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.row_points_declaration, viewGroup, false)
                    EarnPointsDataHolder(layoutView)
                },
                KRecyclerItemClickListener { _: KRecyclerViewHolder?, o: Any?, i: Int ->

                }
            )
            val linearLayoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            rvPointsDeclaration.layoutManager = linearLayoutManager
            rvPointsDeclaration.isNestedScrollingEnabled = false
            rvPointsDeclaration.adapter = adapter
            adapter.notifyDataSetChanged()
            arrayListListenData.add((viewContent as ViewGroup))
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun showError(error: String?) {
        var errorMessage = error
        if (context != null) {
            if (errorMessage == null) errorMessage = APIManager.GENERIC_API_ERROR_MESSAGE
            //AlertUtils.showAlert(getString(R.string.label_error), errorMessage, null, getContext(), null);
        }
    }

    private fun updateEmptyState(error: String?) {
        if (error == null) {
            if (arrayFeedEarnData.isEmpty()) {
                emptyState.setImgAndMsg(getString(R.string.no_data_found), null)
                rvEarnPoints.visibility = View.INVISIBLE
            } else {
                rvEarnPoints.visibility = View.VISIBLE
                emptyState.hide()
            }
        } else {
            rvEarnPoints.visibility = View.INVISIBLE
            if (Utility.isConnectingToInternet(context)) {
                emptyState.message = APIManager.GENERIC_API_ERROR_MESSAGE
            } else {
                emptyState.showNoConnectionState()
            }
        }
    }

    fun showLoader() {
        if (progressBar != null && progressBar.visibility == View.GONE) progressBar.visibility =
            View.VISIBLE
        rvEarnPoints.visibility = View.GONE
    }

    fun dismissLoader() {
        if (progressBar != null && progressBar.visibility == View.VISIBLE) progressBar.visibility =
            View.GONE
        rvEarnPoints.visibility = View.VISIBLE
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
        Log.d("EarnFrag", "EarnFrag$isVisibleToUser")
        if (isVisibleToUser) {
            //showAd()
            is_fragment_visible = true
            if (is_first_time_loading) {
                is_first_time_loading = false
                getEarnPointsAPI()
            }
        } else {
            is_fragment_visible = false
        }
    }
}
