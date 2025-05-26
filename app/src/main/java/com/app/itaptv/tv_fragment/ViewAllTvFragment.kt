package com.app.itaptv.tv_fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.*
import com.android.volley.Request
import com.app.itaptv.API.APIManager
import com.app.itaptv.API.APIRequest
import com.app.itaptv.API.Url
import com.app.itaptv.R
import com.app.itaptv.activity.HomeActivity
import com.app.itaptv.fragment.ChannelCategoryFragment
import com.app.itaptv.interfaces.KeyEventListener
import com.app.itaptv.structure.FeedContentData
import com.app.itaptv.tvControllers.ViewAllFeedTvAdapter
import com.app.itaptv.utils.*
import com.paginate.Paginate
import com.paginate.recycler.LoadingListItemCreator
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class ViewAllTvFragment : Fragment() {

    companion object {
        private val Title = "title"
        private val PageName = "pagename"
        private val ID = "id"
        private val Subcategory = "subcategory"
        private val TileShape = "tileShape"
        private val FeedType = "feedType"
        private val ScreenType = "screenType"

        @JvmStatic
        open fun newInstance(
            id: Int,
            title: String?,
            mPageName: String?,
            subcategory: String?,
            tileShape: String?,
            feedType: String?,
            screenType: String?
        ): ViewAllTvFragment {
            val fragment = ViewAllTvFragment()
            val args = Bundle()
            args.putInt(ID, id)
            args.putString(Title, title)
            args.putString(PageName, mPageName)
            args.putString(Subcategory, subcategory)
            args.putString(TileShape, tileShape)
            args.putString(FeedType, feedType)
            args.putString(ScreenType, screenType)
            fragment.arguments = args
            return fragment
        }
    }

    private var v: View? = null
    private var mtitle: String? = null
    private var mPageName: String? = null
    private var mID = 0
    private var mSubcategory: String? = null
    private var mTileShape: String? = null
    private var mFeedType: String? = null
    private var mScreenType: String? = null
    private var Feed_list_next_page_url: String? = null
    private var Feed_ID = 0
    private var FeedPage: String? = null
    private var Next_Page_N0 = 1
    private var homeActivity: HomeActivity? = null
    var lastSelectedItem: Int = 0
    var lastViewSelected: View? = null

    private var GRID_SPAN = 1
    private var loading = false
    private var page = 0
    private var handler: Handler? = null
    private var paginate: Paginate? = null
    protected var networkDelay: Long = 0
    protected var customLoadingListItem = false

    private var rvContent: RecyclerView? = null
    private var llprogressbar: RelativeLayout? = null
    private var progressBar: ProgressBar? = null
    private var emptyState: EmptyStateManager? = null
    var smoothScroller: RecyclerView.SmoothScroller? = null
    var manager: GridLayoutManager? = null

    private var arrayListFeedContent: ArrayList<FeedContentData> = ArrayList()
    private var arrayListPlayContent: ArrayList<FeedContentData>? = ArrayList()
    private var adapter: ViewAllFeedTvAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mtitle = requireArguments().getString(Title)
            mPageName = requireArguments().getString(PageName)
            mID = requireArguments().getInt(ID)
            mSubcategory = requireArguments().getString(Subcategory)
            mTileShape = requireArguments().getString(TileShape)
            mFeedType = requireArguments().getString(FeedType)
            mScreenType = requireArguments().getString(ScreenType)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (v == null) {
            // Inflate the layout for this fragment
            v = inflater.inflate(R.layout.fragment_view_all_tv, container, false)
            init()
        }
        return v
    }

    fun init() {
        rvContent = v!!.findViewById(R.id.rvContent)
        llprogressbar = v!!.findViewById(R.id.llprogressbar)
        progressBar = v!!.findViewById(R.id.progressBar)

        Feed_ID = mID
        FeedPage = mPageName

        setUpEmptyState()
        setGridView()
        initializePagination()
    }

    private fun setGridView() {
        var spanCount = 0
        when (mTileShape) {
            "default", "v_rectangle" -> {
                spanCount = 6
            }

            "h_rectangle" -> {
                spanCount = 4
            }

            else -> {
                spanCount = 6
            }
        }
        GRID_SPAN = spanCount
        manager = GridLayoutManager(
            activity, spanCount, GridLayoutManager.VERTICAL, false
        )
        rvContent!!.layoutManager = manager
        smoothScroller =
            CenterSmoothScroller(rvContent!!.getContext())

        adapter = context?.let {
            ViewAllFeedTvAdapter(
                it, arrayListFeedContent,
                mTileShape!!,
                mFeedType!!,
                spanCount,
                KeyEventListener { pos, item, v, _ ->
                    smoothScroller!!.targetPosition = pos
                    manager!!.startSmoothScroll(smoothScroller)
                    lastSelectedItem = pos
                    lastViewSelected = v
                    if (item != null) {
                        setPlaybackData(item as FeedContentData, pos, mID, mPageName!!)
                        adapter!!.canAccess = false
                    }
                })
        }
        rvContent!!.adapter = adapter
    }

    fun initializePagination() {
        Log.e("test", "initializePagination")
        arrayListFeedContent.clear()
        adapter!!.notifyDataSetChanged()
        Feed_list_next_page_url = if (mSubcategory.equals("feeds", ignoreCase = true)) {
            Url.GET_FEEDS + FeedPage + "&ID=" + Feed_ID + "&tab_screen=" + mScreenType + "&page_no="
        } else {
            Url.GET_CATEGORY + FeedPage + "&ID=" + Feed_ID + "&tab_screen=" + mScreenType + "&page_no="
        }
        customLoadingListItem = false
        if (paginate != null) {
            paginate!!.unbind()
        }
        loading = false
        page = 0
        handler = Handler()
        handler!!.removeCallbacks(fakeCallback)
        paginate = Paginate.with(rvContent, callbacks)
            .setLoadingTriggerThreshold(0)
            .addLoadingListItem(true)
            .setLoadingListItemCreator(if (customLoadingListItem) CustomLoadingListItemCreator() else null)
            .setLoadingListItemSpanSizeLookup { GRID_SPAN }
            .build()
    }

    //Pagination Code
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
            return Next_Page_N0 == 0 // If all pages are loaded return true
        }
    }

    private val fakeCallback = Runnable { //  page++;
        // loading = false;
        if (page > 0) {
            Feed_list_next_page_url = Feed_list_next_page_url
        }
        Log.e("test", "fakeCallback")
        if (Next_Page_N0 != 0) {
            getViewAllFeed(Next_Page_N0)
        }
    }

    private class CustomLoadingListItemCreator : LoadingListItemCreator {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val view = inflater.inflate(R.layout.custom_loading_list_item, parent, false)
            return VH(view)
        }

        @SuppressLint("DefaultLocale")
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val vh = holder as VH
            vh.tvLoading.setText(
                kotlin.String.format(
                    holder.itemView.context.resources.getString(R.string.total_items_loaded)
                )
            )

            // This is how you can make full span if you are using StaggeredGridLayoutManager
            /* if (rvContent.getLayoutManager() is StaggeredGridLayoutManager) {
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


    private fun setPlaybackData(
        feedContentData: FeedContentData,
        position: Int,
        feed_ID: Int,
        feedPage: String
    ) {
        homeActivity = activity as HomeActivity?
        val sd = LocalStorage.getUserSubscriptionDetails()
        if (arrayListFeedContent[position].contentOrientation.equals(
                "vertical",
                ignoreCase = true
            ) && !Utility.isTelevision()
        ) {
            homeActivity!!.openVerticalPlayer(arrayListFeedContent, position)
        } else {
            if (feedContentData.feedTabType == Constant.TAB_BUY || feedContentData.postExcerpt.contains(
                    "paid"
                )
            ) {
                if (sd != null && sd.allow_rental != null) {
                    if (sd.allow_rental.equals(Constant.YES, ignoreCase = true)) {
                        playMediaItems(feedContentData)
                    } else {
                        /* requireActivity().startActivityForResult(
                             Intent(
                                 activity,
                                 BuyDetailActivity::class.java
                             ).putExtra(BuyDetailActivity.CONTENT_DATA, feedContentData),
                             BuyDetailActivity.REQUEST_CODE
                         )*/
                        homeActivity!!.openFragment(
                            BuyDetailFragment.getInstance(
                                feedContentData,
                                ""
                            )
                        );
                    }
                }
            } else {
                if (activity != null && activity is HomeActivity) {
                    if (feedContentData.postType != "") {
                        when (feedContentData.postType) {
                            FeedContentData.POST_TYPE_POST -> {
                                if (arrayListFeedContent.size > 0) {
                                    if (arrayListFeedContent[feedContentData.feedPosition].feedContentType == FeedContentData.CONTENT_TYPE_ALL || mSubcategory == "categories") {
                                        val arrayListAllTypeFeedContentData =
                                            ArrayList<FeedContentData>()
                                        arrayListAllTypeFeedContentData.add(feedContentData)
                                        //activity.playItems(arrayListAllTypeFeedContentData, 0, String.valueOf(feed_ID), feedPage, String.valueOf(feedContentData.iswatchlisted));
                                        homeActivity!!.showAudioSongExpandedView(
                                            arrayListAllTypeFeedContentData,
                                            0,
                                            feed_ID.toString(),
                                            feedPage,
                                            feedContentData.iswatchlisted.toString()
                                        )
                                        return
                                    }

                                    //activity.playItems(arrayListFeedContent, position, "", feedPage, "");
                                    homeActivity!!.showAudioSongExpandedView(
                                        arrayListFeedContent,
                                        position,
                                        feed_ID.toString(),
                                        feedPage,
                                        arrayListFeedContent[position].iswatchlisted.toString()
                                    )
                                }
                            }

                            FeedContentData.POST_TYPE_ORIGINALS -> {
                                /* homeActivity!!.showSeriesExpandedView(
                                     feedContentData.postId,
                                     "0",
                                     "0",
                                     feedContentData.iswatchlisted.toString()
                                 )*/
                                homeActivity!!.openFragment(
                                    BuyDetailFragment.getInstance(
                                        feedContentData,
                                        ""
                                    )
                                )
                            }

                            FeedContentData.POST_TYPE_EPISODE -> {
                                homeActivity!!.showSeriesExpandedView(
                                    feedContentData.seriesId,
                                    feedContentData.seasonId,
                                    feedContentData.episodeId,
                                    ""
                                )
                            }

                            FeedContentData.POST_TYPE_PLAYLIST -> {
                                homeActivity!!.showPlaylistExpandedView(
                                    feedContentData.postId,
                                    feedContentData.iswatchlisted.toString()
                                )
                            }

                            FeedContentData.POST_TYPE_STREAM -> {                              //playChannelAPI(feedContentData, 1);
                                homeActivity!!.showChannelExpandedView(
                                    feedContentData,
                                    1,
                                    Feed_ID.toString(),
                                    Analyticals.CONTEXT_CHANNEL
                                )
                            }

                            FeedContentData.POST_TYPE_AD -> {
                                if (arrayListFeedContent.size > 0) {
                                    if (arrayListFeedContent[feedContentData.feedPosition].feedContentType == FeedContentData.CONTENT_TYPE_ALL || mSubcategory == "categories") {
                                        val arrayListAllTypeFeedContentData =
                                            ArrayList<FeedContentData>()
                                        arrayListAllTypeFeedContentData.add(feedContentData)
                                        homeActivity!!.showAudioSongExpandedView(
                                            arrayListAllTypeFeedContentData,
                                            0,
                                            feed_ID.toString(),
                                            feedPage,
                                            feedContentData.iswatchlisted.toString()
                                        )
                                        return
                                    }
                                    homeActivity!!.showAudioSongExpandedView(
                                        arrayListFeedContent,
                                        position,
                                        feed_ID.toString(),
                                        feedPage,
                                        arrayListFeedContent[position].iswatchlisted.toString()
                                    )
                                }
                            }
                        }
                    } else {
                        when (feedContentData.taxonomy) {
                            FeedContentData.TAXONOMY_SEASONS ->
                                homeActivity!!.showSeriesExpandedView(
                                    feedContentData.seriesId,
                                    feedContentData.seasonId,
                                    feedContentData.episodeId,
                                    ""
                                )

                            FeedContentData.TAXONOMY_CATEGORY -> {
                                val channelcategoryFragment = ChannelCategoryFragment.newInstance(
                                    "category",
                                    "listen",
                                    feedContentData.termId,
                                    feedContentData.name
                                )
                                openFragment(channelcategoryFragment)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun playMediaItems(feedContentData: FeedContentData?) {
        if (feedContentData != null) {
            val id = feedContentData.postId
            when (feedContentData.postType) {
                FeedContentData.POST_TYPE_ORIGINALS -> { //setSeriesData(id, "0", "0", "false")
                    homeActivity!!.openFragment(
                        BuyDetailFragment.getInstance(
                            feedContentData,
                            ""
                        )
                    )
                }

                FeedContentData.POST_TYPE_SEASON -> setSeriesData(
                    feedContentData.seriesId,
                    feedContentData.seasonId,
                    "0",
                    "false"
                )

                FeedContentData.POST_TYPE_PLAYLIST -> setPlaylistData(id)
                FeedContentData.POST_TYPE_POST -> {
                    arrayListPlayContent!!.clear()
                    arrayListPlayContent!!.add(feedContentData)
                    homeActivity!!.showAudioSongExpandedView(
                        arrayListPlayContent!!,
                        feedContentData.feedPosition,
                        feedContentData.postId,
                        Analyticals.CONTEXT_FEED,
                        feedContentData.iswatchlisted.toString()
                    )
                }
            }
        }
    }

    private fun setSeriesData(
        seriesId: String,
        seasonId: String,
        episodeId: String,
        iswatched: String
    ) {
        homeActivity!!.showSeriesExpandedView(seriesId, seasonId, episodeId, iswatched)
    }

    private fun setPlaylistData(playlistId: String) {
        homeActivity!!.showPlaylistExpandedView(playlistId, "false")
    }


    private fun getViewAllFeed(pageno: Int) {
        try {
            val params: Map<String, String> = HashMap()
            val mUrl: String
            mUrl = if (mSubcategory.equals("feeds", ignoreCase = true)) {
                Feed_list_next_page_url + pageno
            } else {
                Feed_list_next_page_url + pageno + "&onlyposts=true"
            }
            Log.i("Feed", mUrl)
            val apiRequest = APIRequest(
                mUrl, Request.Method.GET, params, null,
                context
            )
            apiRequest.showLoader = false
            APIManager.request(
                apiRequest
            ) { response, error, headers, statusCode ->
                llprogressbar!!.visibility = View.GONE
                progressBar!!.visibility = View.GONE
                rvContent!!.visibility = View.VISIBLE
                handleFeedResponse(response, error)
            }
        } catch (e: Exception) {
            Log.e("", e.toString())
        }
    }

    private fun handleFeedResponse(response: String?, error: Exception?) {
        //  arrayListFeedContent.clear();
        try {
            if (error != null) {
                // Utility.showError(error.getMessage(),getActivity());
                updateEmptyState(error.message)
            } else {
                if (response != null) {
                    Log.e("viewallresponse", response)
                    val jsonObjectResponse = JSONObject(response)
                    val type =
                        if (jsonObjectResponse.has("type")) jsonObjectResponse.getString("type") else ""
                    if (type.equals("error", ignoreCase = true)) {
                        val message =
                            if (jsonObjectResponse.has("msg")) jsonObjectResponse.getString("msg") else APIManager.GENERIC_API_ERROR_MESSAGE
                        updateEmptyState(jsonObjectResponse.getString("msg"))
                    } else if (type.equals("ok", ignoreCase = true)) {
                        val jsonObjectMsg = jsonObjectResponse.getJSONObject("msg")
                        val contentType =
                            if (jsonObjectMsg.has("content_type")) jsonObjectMsg.getString("content_type") else if (jsonObjectMsg.has(
                                    "type"
                                )
                            ) jsonObjectMsg.getString("type") else ""
                        val termForm =
                            if (jsonObjectMsg.has("items_from")) jsonObjectMsg.getString("items_from") else ""
                        val tabType =
                            if (jsonObjectMsg.has("tab_type")) jsonObjectMsg.getString("tab_type") else ""
                        val contentOrientation =
                            if (jsonObjectMsg.has("content_orientation")) jsonObjectMsg.getString("content_orientation") else ""
                        val isCategoryCoin =
                            if (jsonObjectMsg.has("isCategoryCoin")) jsonObjectMsg.getBoolean("isCategoryCoin") else false
                        val categoryId =
                            if (jsonObjectMsg.has("ID")) jsonObjectMsg.getInt("ID") else 0
                        val jsonArrayContent = jsonObjectMsg.getJSONArray("contents")
                        Log.i("viewallresponse", contentOrientation)
                        for (i in 0 until jsonArrayContent.length()) {
                            val feedContentData = FeedContentData(
                                tabType,
                                contentType,
                                termForm,
                                jsonArrayContent.getJSONObject(i),
                                0
                            )
                            if (feedContentData.contentOrientation.isEmpty()) {
                                feedContentData.contentOrientation = contentOrientation
                            }
                            feedContentData.isCategoryCoin = isCategoryCoin
                            feedContentData.categoryId = categoryId
                            Log.i("viewallresponse", feedContentData.contentOrientation)
                            arrayListFeedContent.add(feedContentData)
                        }
                        Next_Page_N0 = jsonObjectMsg.getInt("next_page")
                        //Collections.shuffle(arrayListFeedContent)
                        if (arrayListFeedContent.isNotEmpty()) {
                            arrayListFeedContent[0].isFocused = true
                        }
                        adapter!!.notifyDataSetChanged()
                        loading = false
                        page++
                        updateEmptyState(null)
                    }
                }
            }
        } catch (e: JSONException) {
            loading = false
            paginate!!.unbind()
        }
    }

    /**
     * Initialization of Empty State
     */
    fun setUpEmptyState() {
        emptyState = EmptyStateManager.setUpInFragment(
            v, activity as AppCompatActivity?
        ) { action -> action == EmptyStateManager.ACTION_RETRY }
    }

    /**
     * Update of Empty State
     */
    private fun updateEmptyState(error: String?) {
        if (error == null) {
            if (arrayListFeedContent.isEmpty()) {
                emptyState!!.setImgAndMsg(getString(R.string.no_data_found), null)
                rvContent!!.visibility = View.INVISIBLE
            } else {
                rvContent!!.visibility = View.VISIBLE
                emptyState!!.hide()
            }
        } else {
            rvContent!!.visibility = View.INVISIBLE
            if (Utility.isConnectingToInternet(context)) {
                emptyState!!.setMessage(APIManager.GENERIC_API_ERROR_MESSAGE)
            } else {
                emptyState!!.showNoConnectionState()
            }
        }
    }

    private fun openFragment(fragment: Fragment) {
        val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.container, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    class CenterSmoothScroller(context: Context?) : LinearSmoothScroller(context) {
        override fun calculateDtToFit(
            viewStart: Int,
            viewEnd: Int,
            boxStart: Int,
            boxEnd: Int,
            snapPreference: Int
        ): Int {
            return boxStart + (boxEnd - boxStart) / 2 - (viewStart + (viewEnd - viewStart) / 2)
        }
    }

    fun restorePosition() {
        try {
            Handler().postDelayed({
                if (adapter != null) {
                    smoothScroller!!.targetPosition = lastSelectedItem
                    manager!!.startSmoothScroll(smoothScroller)
                    Objects.requireNonNull(arrayListFeedContent[lastSelectedItem].view)
                        .requestFocus()
                    adapter!!.canAccess = true
                }
            }, 10)
        } catch (e: java.lang.Exception) {
        }
    }

    override fun onResume() {
        super.onResume()
        if (arrayListFeedContent.isNotEmpty()) {
            restorePosition()
        }
    }
}