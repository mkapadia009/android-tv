package com.app.itaptv.fragment

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.itaptv.R
import com.app.itaptv.activity.HomeActivity
import com.app.itaptv.activity.WalletActivity
import com.app.itaptv.holder.EarnHistoryHolder
import com.app.itaptv.structure.EarnHistoryData
import com.app.itaptv.structure.FeedContentData
import com.app.itaptv.utils.Log
import com.app.itaptv.utils.SeparatorDecoration
import com.app.itaptv.utils.Wallet
import com.kalpesh.krecycleradapter.Interface.KRecyclerItemClickListener
import com.kalpesh.krecycleradapter.Interface.KRecyclerViewHolderCallBack
import com.kalpesh.krecycleradapter.KRecyclerViewAdapter
import com.paginate.Paginate
import com.paginate.recycler.LoadingListItemCreator
import kotlinx.android.synthetic.main.fragment_earn_history.*
import kotlinx.android.synthetic.main.progress_bar.*
import org.json.JSONArray
import org.json.JSONException
import java.util.*

class EarnHistoryFrag : Fragment() {

    private lateinit var rootView: View

    private lateinit var tvLabelNoHistoryFound: TextView
    private lateinit var rvEarnHistory: RecyclerView
    private lateinit var adapter: KRecyclerViewAdapter

    private val arrayListEarnHistory = ArrayList<EarnHistoryData>()
    private var historyFlag = 0
    private var historyCnt = 0

    // Pagination
    private val GRID_SPAN = 1
    private var loading = false
    private var page = 0
    private var handler: Handler? = null
    private var paginate: Paginate? = null
    protected var networkDelay: Long = 2000
    protected var customLoadingListItem = false
    var offset = 0
    var maxRecord = 10
    var nextPageNo = 1

    var homeActivity: HomeActivity? = null

    companion object {
        @JvmStatic
        fun newInstance(): EarnHistoryFrag {
            return EarnHistoryFrag()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_earn_history, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {
        rvEarnHistory = rootView.findViewById(R.id.rvEarnHistory)
        tvLabelNoHistoryFound = rootView.findViewById(R.id.tvLabelNoHistoryFound)
        historyFlag = WalletActivity.FLAG_EARN_HISTORY

        //showHistory()
        setEarnHistoryRecyclerView()

        nsvTabFrag.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                Log.i(FeedContentData.TAG, "BOTTOM SCROLL")
                if (nextPageNo != 0) {
                    getTransactionHistory()
                }
            }
        })
        //getHistoryCount()
    }

    /**
     * Set recycler view to display earn history list
     */
    private fun setEarnHistoryRecyclerView() {
        rvEarnHistory.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        rvEarnHistory.isNestedScrollingEnabled = false
        val decoration =
            SeparatorDecoration(requireContext(), resources.getColor(R.color.tab_grey), 1.5f)
        rvEarnHistory.addItemDecoration(decoration)
        adapter = KRecyclerViewAdapter(
            requireContext(),
            arrayListEarnHistory,
            KRecyclerViewHolderCallBack { viewGroup, i ->
                val layoutView = LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.row_history, viewGroup, false)
                EarnHistoryHolder(layoutView)
            },
            KRecyclerItemClickListener { _, o, i -> })
        rvEarnHistory.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    private fun getTransactionHistory() {
        progressBar.visibility = View.VISIBLE
        Wallet.getTransactionHistory(
            activity,
            historyFlag,
            nextPageNo,
            maxRecord
        ) { success, error, coins, diamonds, creditedCoins, historyData, historyCount ->
            progressBar.visibility = View.GONE
            showHistory()
            nextPageNo = historyCount
            updateData(success, error, historyData)
            success
        }
    }

    private fun getHistoryCount() {
        //arrayListEarnHistory.clear()
        Wallet.getHistoryCount(historyFlag) { success, error, coins, diamonds, creditedCoins, historyData, historyCount -> //if (success) {
            historyCnt = historyCount
            initializeEarnPagination()
            /*} else {
                    noHistoryFound();
                }*/success
        }
    }

    private fun updateData(success: Boolean, error: String?, jsonArray: JSONArray?) {
        if (success) {
            if (jsonArray != null) {
                try {
                    offset = offset + maxRecord
                    if (offset > historyCnt) {
                        offset = -1
                    }
                    //arrayListEarnHistory.clear()
                    for (i in 0 until jsonArray.length()) {
                        val earnHistoryData =
                            EarnHistoryData(jsonArray.getJSONObject(i), historyFlag)
                        arrayListEarnHistory.add(earnHistoryData)
                    }
                    adapter.notifyDataSetChanged()
                    loading = false
                    page++
                } catch (e: JSONException) {
                    handleError(e.message)
                }
            }
        } else {
            handleError(error)
        }
        loading = false
    }

    override fun onResume() {
        super.onResume()
        arrayListEarnHistory.clear()
        offset = 0
        historyCnt = 0
        nextPageNo = 1
        if (nextPageNo != 0) {
            getTransactionHistory()
        }
        //getHistoryCount()
    }

    private fun handleError(error: String?) {
        loading = false
        //paginate!!.unbind()
        noHistoryFound()
    }

    private fun noHistoryFound() {
        rvEarnHistory.visibility = View.GONE
        tvLabelNoHistoryFound.visibility = View.VISIBLE
    }

    private fun showHistory() {
        rvEarnHistory.visibility = View.VISIBLE
        tvLabelNoHistoryFound.visibility = View.GONE
    }

    /************************ PAGINATION METHODS -- START  */
    fun initializeEarnPagination() {
        adapter.notifyDataSetChanged()
        customLoadingListItem = false
        paginate?.unbind()
        loading = false
        page = 0
        handler = Handler()
        handler!!.removeCallbacks(fakeCallback)
        paginate = Paginate.with(rvEarnHistory, callbacks)
            .setLoadingTriggerThreshold(0)
            .addLoadingListItem(true)
            .setLoadingListItemCreator(if (customLoadingListItem) CustomLoadingListItemCreator() else null)
            .setLoadingListItemSpanSizeLookup { GRID_SPAN }
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
            return offset == -1 // If all pages are loaded return true
        }
    }


    private val fakeCallback = Runnable {
        if (offset >= 0) {
            //getTransactionHistory()
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
        }
    }

    internal class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvLoading: TextView

        init {
            tvLoading = itemView.findViewById(R.id.tv_loading_text)
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            /*if (activity != null && activity is HomeActivity) {
                homeActivity = activity as HomeActivity?
                if (homeActivity != null) {
                    homeActivity!!.showAd()
                }
            }*/
        }
    }
}
