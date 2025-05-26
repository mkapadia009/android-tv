package com.app.itaptv.tv_fragment

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.app.itaptv.API.APIManager
import com.app.itaptv.API.APIRequest
import com.app.itaptv.API.Url
import com.app.itaptv.R
import com.app.itaptv.activity.HomeActivity
import com.app.itaptv.custom_interface.NavigationMenuCallback
import com.app.itaptv.fragment.ChannelCategoryFragment
import com.app.itaptv.structure.FeedContentData
import com.app.itaptv.tvControllers.ViewAllFeedTvAdapter
import com.app.itaptv.tv_fragment.ViewAllTvFragment.CenterSmoothScroller
import com.app.itaptv.utils.AlertUtils
import com.app.itaptv.utils.Analyticals
import com.app.itaptv.utils.Constant
import com.app.itaptv.utils.EmptyStateManager
import com.app.itaptv.utils.LocalStorage
import com.app.itaptv.utils.Log
import com.app.itaptv.utils.Utility
import kotlinx.android.synthetic.main.activity_home.nav_fragment
import org.json.JSONObject
import java.util.Objects

class SearchTvFragment : Fragment() {

    var v: View? = null
    var tvSearch: EditText? = null
    var tvA: TextView? = null
    var tvB: TextView? = null
    var tvC: TextView? = null
    var tvD: TextView? = null
    var tvE: TextView? = null
    var tvF: TextView? = null
    var tvG: TextView? = null
    var tvH: TextView? = null
    var tvI: TextView? = null
    var tvJ: TextView? = null
    var tvK: TextView? = null
    var tvL: TextView? = null
    var tvM: TextView? = null
    var tvN: TextView? = null
    var tvO: TextView? = null
    var tvP: TextView? = null
    var tvQ: TextView? = null
    var tvR: TextView? = null
    var tvS: TextView? = null
    var tvT: TextView? = null
    var tvU: TextView? = null
    var tvV: TextView? = null
    var tvW: TextView? = null
    var tvX: TextView? = null
    var tvY: TextView? = null
    var tvZ: TextView? = null
    var tvZero: TextView? = null
    var tvOne: TextView? = null
    var tvTwo: TextView? = null
    var tvThree: TextView? = null
    var tvFour: TextView? = null
    var tvFive: TextView? = null
    var tvSix: TextView? = null
    var tvSeven: TextView? = null
    var tvEight: TextView? = null
    var tvNine: TextView? = null
    var ivSpaceBar: ImageView? = null
    var ivBackspace: ImageView? = null
    var llprogressbar: RelativeLayout? = null
    var progressBar: ProgressBar? = null
    var emptyState: EmptyStateManager? = null
    var clKeyboard: ConstraintLayout? = null

    var rvSearch: RecyclerView? = null
    var adapterTv: ViewAllFeedTvAdapter? = null
    var smoothScroller: RecyclerView.SmoothScroller? = null
    var manager: GridLayoutManager? = null
    var arrayListSearchContent = ArrayList<FeedContentData>()
    var arrayListSearchPlayContent = ArrayList<FeedContentData>()
    var lastSelectedItem = 0
    var lastSelectedChar: View? = null
    var homeActivity: HomeActivity? = null
    var SearchUrl: String? = null
    var Next_Page_N0 = 1
    var searchtext = ""
    var handler: Handler = Handler()
    lateinit var navigationMenuCallback: NavigationMenuCallback
    var isRefreshRequired = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if (v == null) {
            v = inflater.inflate(R.layout.fragment_search_tv, container, false)
            init()
        }
        return v
    }

    companion object {
        @JvmStatic
        fun newInstance(): SearchTvFragment {
            return SearchTvFragment()
        }
    }

    fun init() {
        tvSearch = v!!.findViewById(R.id.tvSearch)
        tvA = v!!.findViewById(R.id.tvA)
        tvB = v!!.findViewById(R.id.tvB)
        tvC = v!!.findViewById(R.id.tvC)
        tvD = v!!.findViewById(R.id.tvD)
        tvE = v!!.findViewById(R.id.tvE)
        tvF = v!!.findViewById(R.id.tvF)
        tvG = v!!.findViewById(R.id.tvG)
        tvH = v!!.findViewById(R.id.tvH)
        tvI = v!!.findViewById(R.id.tvI)
        tvJ = v!!.findViewById(R.id.tvJ)
        tvK = v!!.findViewById(R.id.tvK)
        tvL = v!!.findViewById(R.id.tvL)
        tvM = v!!.findViewById(R.id.tvM)
        tvN = v!!.findViewById(R.id.tvN)
        tvO = v!!.findViewById(R.id.tvO)
        tvP = v!!.findViewById(R.id.tvP)
        tvQ = v!!.findViewById(R.id.tvQ)
        tvR = v!!.findViewById(R.id.tvR)
        tvS = v!!.findViewById(R.id.tvS)
        tvT = v!!.findViewById(R.id.tvT)
        tvU = v!!.findViewById(R.id.tvU)
        tvV = v!!.findViewById(R.id.tvV)
        tvW = v!!.findViewById(R.id.tvW)
        tvX = v!!.findViewById(R.id.tvX)
        tvY = v!!.findViewById(R.id.tvY)
        tvZ = v!!.findViewById(R.id.tvZ)
        tvZero = v!!.findViewById(R.id.tvZero)
        tvOne = v!!.findViewById(R.id.tvOne)
        tvTwo = v!!.findViewById(R.id.tvTwo)
        tvThree = v!!.findViewById(R.id.tvThree)
        tvFour = v!!.findViewById(R.id.tvFour)
        tvFive = v!!.findViewById(R.id.tvFive)
        tvSix = v!!.findViewById(R.id.tvSix)
        tvSeven = v!!.findViewById(R.id.tvSeven)
        tvEight = v!!.findViewById(R.id.tvEight)
        tvNine = v!!.findViewById(R.id.tvNine)
        ivSpaceBar = v!!.findViewById(R.id.ivSpaceBar)
        ivBackspace = v!!.findViewById(R.id.ivBackSpace)
        rvSearch = v!!.findViewById(R.id.rvsearch)
        llprogressbar = v!!.findViewById(R.id.llprogressbar)
        progressBar = v!!.findViewById(R.id.progressBar)
        clKeyboard = v!!.findViewById(R.id.clKeyboard)
        setUpEmptyState()
        navMenuToggle(false)
        progressBar!!.setVisibility(View.GONE)
        rvSearch!!.setVisibility(View.INVISIBLE)

        homeActivity = activity as HomeActivity?
        lastSelectedChar = ivSpaceBar

        setClickListener()
        setKeyListener()
        setRecyclerViewTv()
    }

    private fun setClickListener() {
        tvA!!.setOnClickListener { tvSearch!!.setText(tvSearch!!.text.toString() + "a") }
        tvB!!.setOnClickListener { tvSearch!!.setText(tvSearch!!.text.toString() + "b") }
        tvC!!.setOnClickListener { tvSearch!!.setText(tvSearch!!.text.toString() + "c") }
        tvD!!.setOnClickListener { tvSearch!!.setText(tvSearch!!.text.toString() + "d") }
        tvE!!.setOnClickListener { tvSearch!!.setText(tvSearch!!.text.toString() + "e") }
        tvF!!.setOnClickListener { tvSearch!!.setText(tvSearch!!.text.toString() + "f") }
        tvG!!.setOnClickListener { tvSearch!!.setText(tvSearch!!.text.toString() + "g") }
        tvH!!.setOnClickListener { tvSearch!!.setText(tvSearch!!.text.toString() + "h") }
        tvI!!.setOnClickListener { tvSearch!!.setText(tvSearch!!.text.toString() + "i") }
        tvJ!!.setOnClickListener { tvSearch!!.setText(tvSearch!!.text.toString() + "j") }
        tvK!!.setOnClickListener { tvSearch!!.setText(tvSearch!!.text.toString() + "k") }
        tvL!!.setOnClickListener { tvSearch!!.setText(tvSearch!!.text.toString() + "l") }
        tvM!!.setOnClickListener { tvSearch!!.setText(tvSearch!!.text.toString() + "m") }
        tvN!!.setOnClickListener { tvSearch!!.setText(tvSearch!!.text.toString() + "n") }
        tvO!!.setOnClickListener { tvSearch!!.setText(tvSearch!!.text.toString() + "o") }
        tvP!!.setOnClickListener { tvSearch!!.setText(tvSearch!!.text.toString() + "p") }
        tvQ!!.setOnClickListener { tvSearch!!.setText(tvSearch!!.text.toString() + "q") }
        tvR!!.setOnClickListener { tvSearch!!.setText(tvSearch!!.text.toString() + "r") }
        tvS!!.setOnClickListener { tvSearch!!.setText(tvSearch!!.text.toString() + "s") }
        tvT!!.setOnClickListener { tvSearch!!.setText(tvSearch!!.text.toString() + "t") }
        tvU!!.setOnClickListener { tvSearch!!.setText(tvSearch!!.text.toString() + "u") }
        tvV!!.setOnClickListener { tvSearch!!.setText(tvSearch!!.text.toString() + "v") }
        tvW!!.setOnClickListener { tvSearch!!.setText(tvSearch!!.text.toString() + "w") }
        tvX!!.setOnClickListener { tvSearch!!.setText(tvSearch!!.text.toString() + "x") }
        tvY!!.setOnClickListener { tvSearch!!.setText(tvSearch!!.text.toString() + "y") }
        tvZ!!.setOnClickListener { tvSearch!!.setText(tvSearch!!.text.toString() + "z") }
        tvZero!!.setOnClickListener { tvSearch!!.setText(tvSearch!!.text.toString() + "0") }
        tvOne!!.setOnClickListener { tvSearch!!.setText(tvSearch!!.text.toString() + "1") }
        tvTwo!!.setOnClickListener { tvSearch!!.setText(tvSearch!!.text.toString() + "2") }
        tvThree!!.setOnClickListener { tvSearch!!.setText(tvSearch!!.text.toString() + "3") }
        tvFour!!.setOnClickListener { tvSearch!!.setText(tvSearch!!.text.toString() + "4") }
        tvFive!!.setOnClickListener { tvSearch!!.setText(tvSearch!!.text.toString() + "5") }
        tvSix!!.setOnClickListener { tvSearch!!.setText(tvSearch!!.text.toString() + "6") }
        tvSeven!!.setOnClickListener { tvSearch!!.setText(tvSearch!!.text.toString() + "7") }
        tvEight!!.setOnClickListener { tvSearch!!.setText(tvSearch!!.text.toString() + "8") }
        tvNine!!.setOnClickListener { tvSearch!!.setText(tvSearch!!.text.toString() + "9") }
        ivSpaceBar!!.setOnClickListener { tvSearch!!.setText(tvSearch!!.text.toString() + " ") }
        ivBackspace!!.setOnClickListener {
            val str = tvSearch!!.text.toString()
            if (str.isNotEmpty()) {
                tvSearch!!.setText(str.substring(0, str.length - 1))
            }
        }

        tvSearch!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (isRefreshRequired) {
                    emptyState!!.hide()
                    progressBar!!.visibility = View.GONE
                    rvSearch!!.visibility = View.INVISIBLE
                    initializePagination(s.toString())
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }

    private fun setKeyListener() {
        ivBackspace!!.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_RIGHT -> {
                        if (arrayListSearchContent.isNotEmpty()) {
                            lastSelectedChar = ivBackspace
                            navMenuToggle(true)
                        }
                    }
                }
            }
            false
        }
        tvF!!.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_RIGHT -> {
                        if (arrayListSearchContent.isNotEmpty()) {
                            lastSelectedChar = tvF
                            navMenuToggle(true)
                        }
                    }
                }
            }
            false
        }
        tvL!!.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_RIGHT -> {
                        if (arrayListSearchContent.isNotEmpty()) {
                            lastSelectedChar = tvL
                            navMenuToggle(true)
                        }
                    }
                }
            }
            false
        }
        tvR!!.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_RIGHT -> {
                        if (arrayListSearchContent.isNotEmpty()) {
                            lastSelectedChar = tvR
                            navMenuToggle(true)
                        }
                    }
                }
            }
            false
        }
        tvX!!.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_RIGHT -> {
                        if (arrayListSearchContent.isNotEmpty()) {
                            lastSelectedChar = tvX
                            navMenuToggle(true)
                        }
                    }
                }
            }
            false
        }
        tvThree!!.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_RIGHT -> {
                        if (arrayListSearchContent.isNotEmpty()) {
                            lastSelectedChar = tvThree
                            navMenuToggle(true)
                        }
                    }
                }
            }
            false
        }
        tvNine!!.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_RIGHT -> {
                        if (arrayListSearchContent.isNotEmpty()) {
                            lastSelectedChar = tvNine
                            navMenuToggle(true)
                        }
                    }
                }
            }
            false
        }


        ivSpaceBar!!.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_LEFT -> {
                        lastSelectedChar = ivSpaceBar
                        navigationMenuCallback.navMenuToggle(true)
                    }
                }
            }
            false
        }
        tvA!!.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_LEFT -> {
                        lastSelectedChar = tvA
                        navigationMenuCallback.navMenuToggle(true)
                    }
                }
            }
            false
        }
        tvG!!.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_LEFT -> {
                        lastSelectedChar = tvG
                        navigationMenuCallback.navMenuToggle(true)
                    }
                }
            }
            false
        }
        tvM!!.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_LEFT -> {
                        lastSelectedChar = tvM
                        navigationMenuCallback.navMenuToggle(true)
                    }
                }
            }
            false
        }
        tvS!!.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_LEFT -> {
                        lastSelectedChar = tvS
                        navigationMenuCallback.navMenuToggle(true)
                    }
                }
            }
            false
        }
        tvY!!.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_LEFT -> {
                        lastSelectedChar = tvY
                        navigationMenuCallback.navMenuToggle(true)
                    }
                }
            }
            false
        }
        tvFour!!.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_LEFT -> {
                        lastSelectedChar = tvFour
                        navigationMenuCallback.navMenuToggle(true)
                    }
                }
            }
            false
        }
    }

    private fun setRecyclerViewTv() {
        val spanCount = 4
        manager = GridLayoutManager(activity, spanCount, GridLayoutManager.VERTICAL, false)
        rvSearch!!.layoutManager = manager
        smoothScroller = CenterSmoothScroller(rvSearch!!.context)
        adapterTv = ViewAllFeedTvAdapter(
            requireContext(), arrayListSearchContent,
            "",
            "",
            spanCount
        ) { position, item, view, isFirstRow ->
            if (isFirstRow) {
                navMenuToggle(false)
            } else {
                smoothScroller!!.targetPosition = position
                manager!!.startSmoothScroll(smoothScroller)
                lastSelectedItem = position
            }
            if (item is FeedContentData) {
                val feedContentData = item
                if (feedContentData != null) {
                    setPlaybackData(
                        feedContentData,
                        position,
                        feedContentData.postId.toInt(),
                        Analyticals.CONTEXT_FEED
                    )
                    adapterTv!!.canAccess = false
                }
            }
        }
        rvSearch!!.adapter = adapterTv
    }

    fun initializePagination(newText: String) {
        Log.e("test", "initializePagination")
        progressBar!!.setVisibility(View.VISIBLE)
        arrayListSearchContent.clear()
        adapterTv!!.notifyDataSetChanged()
        SearchUrl = Url.GET_SEARCH + newText + "&page_no="
        searchtext = newText
        Next_Page_N0 = 1
        lastSelectedItem = 0
        handler.removeCallbacksAndMessages(null)
        getSearchData(Next_Page_N0)
    }

    private fun getSearchData(next_page_n0: Int) {
        try {
            val params: Map<String, String> = HashMap()
            val apiRequest = APIRequest(
                SearchUrl + next_page_n0, Request.Method.GET, params, null,
                activity
            )
            apiRequest.showLoader = false
            APIManager.request(
                apiRequest
            ) { response, error, headers, statusCode ->
                progressBar!!.setVisibility(View.GONE)
                rvSearch!!.visibility = View.VISIBLE
                handleSearchResponse(response, error)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleSearchResponse(response: String?, error: Exception?) {
        //Log.d(TAG, response);
        try {
            if (error != null) {
                // showError(error.getMessage());
                updateEmptyState(error.message)
            } else {
                if (response != null) {
                    Log.e("response", response)
                    val jsonObjectResponse = JSONObject(response)
                    val type =
                        if (jsonObjectResponse.has("type")) jsonObjectResponse.getString("type") else ""
                    if (type.equals("error", ignoreCase = true)) {
                        val message =
                            if (jsonObjectResponse.has("msg")) jsonObjectResponse.getString("msg") else APIManager.GENERIC_API_ERROR_MESSAGE
                        //showError(message)
                        updateEmptyState(message)
                    } else if (type.equals("ok", ignoreCase = true)) {
                        val jsonArrayMsg = jsonObjectResponse.getJSONObject("msg")
                        val jsonArrayContents = jsonArrayMsg.getJSONArray("contents")
                        Next_Page_N0 =
                            if (jsonArrayMsg.has("next_page")) jsonArrayMsg.getInt("next_page") else 0
                        handler.postDelayed({
                            if (Next_Page_N0 != 0) {
                                getSearchData(Next_Page_N0)
                            }
                        }, 1000)

                        for (i in 0 until jsonArrayContents.length()) {
                            val feedContentData =
                                FeedContentData(jsonArrayContents.getJSONObject(i), i)
                            arrayListSearchContent.add(feedContentData)
                        }
                        adapterTv!!.notifyDataSetChanged()
                        if (rvSearch!!.hasFocus()) {
                            restorePosition()
                        }
                        updateEmptyState(null)
                    }
                }
            }
        } catch (e: Exception) {

        }
    }

    /**
     * This method shows error alert
     *
     * @param errorMessage message to be displayed in alert dialog
     */
    private fun showError(errorMessage: String?) {
        var errorMessage = errorMessage
        if (errorMessage == null) errorMessage = APIManager.GENERIC_API_ERROR_MESSAGE
        AlertUtils.showAlert(getString(R.string.label_error), errorMessage, null, context, null)
    }


    /**
     * Initialization of Empty State
     */
    fun setUpEmptyState() {
        emptyState = EmptyStateManager.setUpInFragment(
            v, activity as AppCompatActivity?
        ) { action ->
            if (action == EmptyStateManager.ACTION_RETRY) {
                getSearchData(0)
            }
        }
    }

    /**
     * Update of Empty State
     */
    private fun updateEmptyState(error: String?) {
        if (error == null) {
            if (arrayListSearchContent.isEmpty()) {
                emptyState!!.setImgAndMsg(getString(R.string.no_item_found), null)
                rvSearch!!.visibility = View.INVISIBLE
            } else {
                rvSearch!!.visibility = View.VISIBLE
                emptyState!!.hide()
            }
        } else {
            rvSearch!!.visibility = View.INVISIBLE
            if (Utility.isConnectingToInternet(context)) {
                emptyState!!.setMessage(APIManager.GENERIC_API_ERROR_MESSAGE)
            } else {
                emptyState!!.showNoConnectionState()
            }
        }
    }

    private fun setPlaybackData(
        feedContentData: FeedContentData,
        position: Int,
        feed_ID: Int,
        feedPage: String
    ) {
        if (activity != null && activity is HomeActivity) {
            homeActivity = activity as HomeActivity?
            val sd = LocalStorage.getUserSubscriptionDetails()
            if (feedContentData.postType != "") {
                when (feedContentData.postType) {
                    FeedContentData.POST_TYPE_POST -> {
                        if (arrayListSearchContent.size > 0) {
                            if (arrayListSearchContent[feedContentData.feedPosition].feedContentType == FeedContentData.CONTENT_TYPE_ALL) {
                                val arrayListAllTypeFeedContentData =
                                    java.util.ArrayList<FeedContentData>()
                                arrayListAllTypeFeedContentData.add(feedContentData)
                                //activity.playItems(arrayListAllTypeFeedContentData, 0, String.valueOf(feed_ID), feedPage, String.valueOf(feedContentData.iswatchlisted));
                                if (feedContentData.postExcerpt.equals("free", ignoreCase = true)) {
                                    homeActivity!!.showAudioSongExpandedView(
                                        arrayListAllTypeFeedContentData,
                                        0,
                                        feed_ID.toString(),
                                        feedPage,
                                        feedContentData.iswatchlisted.toString()
                                    )
                                } else if (feedContentData.postExcerpt.equals(
                                        "paid",
                                        ignoreCase = true
                                    )
                                ) {
                                    if (sd != null && sd.allow_rental != null) {
                                        if (sd.allow_rental.equals(
                                                Constant.YES,
                                                ignoreCase = true
                                            )
                                        ) {
                                            playMediaItems(feedContentData)
                                        } else {
                                            /*  val intent1 =
                                            Intent(activity, BuyDetailActivity::class.java)
                                        intent1.putExtra(
                                            BuyDetailActivity.CONTENT_DATA,
                                            feedContentData
                                        )
                                        homeActivity!!.startActivityForResult(
                                            intent1,
                                            BuyDetailActivity.REQUEST_CODE
                                        )*/

                                            /*Intent intent = new Intent(context, BuyDetailActivity.class);
                                    intent.putExtra(BuyDetailActivity.CONTENT_DATA, feedContentData);
                                    intent.putExtra(BuyDetailActivity.FROM_SLIDER, "sliderPageKey");
                                    ((Activity) context).startActivityForResult(intent, BuyDetailActivity.REQUEST_CODE);*/
                                            homeActivity!!.openFragment(
                                                BuyDetailFragment.getInstance(
                                                    feedContentData,
                                                    ""
                                                )
                                            )
                                        }
                                    }
                                }
                                return
                            }
                            val arrayList = java.util.ArrayList<FeedContentData>()
                            arrayList.add(feedContentData)
                            if (feedContentData.postExcerpt.equals("free", ignoreCase = true)) {
                                homeActivity!!.showAudioSongExpandedView(
                                    arrayList,
                                    0,
                                    "",
                                    feedPage,
                                    arrayList[0].iswatchlisted.toString()
                                )
                            } else if (feedContentData.postExcerpt.equals(
                                    "paid",
                                    ignoreCase = true
                                )
                            ) {
                                if (sd != null && sd.allow_rental != null) {
                                    if (sd.allow_rental.equals(Constant.YES, ignoreCase = true)) {
                                        playMediaItems(feedContentData)
                                    } else {
                                        /* val intent1 = Intent(activity, BuyDetailActivity::class.java)
                                    intent1.putExtra(
                                        BuyDetailActivity.CONTENT_DATA,
                                        feedContentData
                                    )
                                    requireActivity().startActivityForResult(
                                        intent1,
                                        BuyDetailActivity.REQUEST_CODE
                                    )*/
                                        homeActivity!!.openFragment(
                                            BuyDetailFragment.getInstance(
                                                feedContentData,
                                                ""
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    FeedContentData.POST_TYPE_ORIGINALS -> {
                        if (feedContentData.postExcerpt.equals(
                                "free",
                                ignoreCase = true
                            )
                        ) {
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
                        } else if (feedContentData.postExcerpt.equals("paid", ignoreCase = true)) {
                            if (sd != null && sd.allow_rental != null) {
                                if (sd.allow_rental.equals(Constant.YES, ignoreCase = true)) {
                                    playMediaItems(feedContentData)
                                } else {
                                    /* val intent1 = Intent(activity, BuyDetailActivity::class.java)
                                 intent1.putExtra(BuyDetailActivity.CONTENT_DATA, feedContentData)
                                 requireActivity().startActivityForResult(
                                     intent1,
                                     BuyDetailActivity.REQUEST_CODE
                                 )*/
                                    homeActivity!!.openFragment(
                                        BuyDetailFragment.getInstance(
                                            feedContentData,
                                            ""
                                        )
                                    )
                                }
                            }
                        }
                    }

                    FeedContentData.POST_TYPE_EPISODE -> {
                        if (feedContentData.postExcerpt.equals(
                                "free",
                                ignoreCase = true
                            )
                        ) {
                            homeActivity!!.showSeriesExpandedView(
                                feedContentData.seriesId,
                                feedContentData.seasonId,
                                feedContentData.episodeId,
                                ""
                            )
                        } else if (feedContentData.postExcerpt.equals("paid", ignoreCase = true)) {
                            if (sd != null && sd.allow_rental != null) {
                                if (sd.allow_rental.equals(Constant.YES, ignoreCase = true)) {
                                    playMediaItems(feedContentData)
                                } else {
                                    /* val intent1 = Intent(activity, BuyDetailActivity::class.java)
                                 intent1.putExtra(BuyDetailActivity.CONTENT_DATA, feedContentData)
                                 requireActivity().startActivityForResult(
                                     intent1,
                                     BuyDetailActivity.REQUEST_CODE
                                 )*/
                                    homeActivity!!.openFragment(
                                        BuyDetailFragment.getInstance(
                                            feedContentData,
                                            ""
                                        )
                                    )
                                }
                            }
                        }
                    }

                    FeedContentData.POST_TYPE_PLAYLIST -> {
                        if (feedContentData.postExcerpt.equals(
                                "free",
                                ignoreCase = true
                            )
                        ) {
                            homeActivity!!.showPlaylistExpandedView(
                                feedContentData.postId,
                                feedContentData.iswatchlisted.toString()
                            )
                        } else if (feedContentData.postExcerpt.equals("paid", ignoreCase = true)) {
                            if (sd != null && sd.allow_rental != null) {
                                if (sd.allow_rental.equals(Constant.YES, ignoreCase = true)) {
                                    playMediaItems(feedContentData)
                                } else {
                                    /*  val intent1 = Intent(activity, BuyDetailActivity::class.java)
                                  intent1.putExtra(BuyDetailActivity.CONTENT_DATA, feedContentData)
                                  requireActivity().startActivityForResult(
                                      intent1,
                                      BuyDetailActivity.REQUEST_CODE
                                  )*/
                                    homeActivity!!.openFragment(
                                        BuyDetailFragment.getInstance(
                                            feedContentData,
                                            ""
                                        )
                                    )
                                }
                            }
                        }
                    }

                    FeedContentData.POST_TYPE_STREAM -> {                 //playChannelAPI(feedContentData, 1);
                        homeActivity!!.showChannelExpandedView(
                            feedContentData,
                            1,
                            feed_ID.toString(),
                            Analyticals.CONTEXT_CHANNEL
                        )
                    }
                }
            } else {
                when (feedContentData.taxonomy) {
                    FeedContentData.TAXONOMY_SEASONS -> {                     //activity.showSeriesExpandedView(feedContentData.seriesId, feedContentData.seasonId, feedContentData.episodeId);
                        homeActivity!!.showSeriesExpandedView(
                            feedContentData.seriesId,
                            feedContentData.seasonId,
                            feedContentData.episodeId,
                            ""
                        )
                    }

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

    private fun playMediaItems(feedContentData: FeedContentData?) {
        if (feedContentData != null) {
            val id = feedContentData.postId
            when (feedContentData.postType) {
                FeedContentData.POST_TYPE_ORIGINALS -> {
                    //setSeriesData(id, "0", "0", "false")
                    homeActivity!!.openFragment(
                        BuyDetailFragment.getInstance(
                            feedContentData,
                            ""
                        )
                    )
                }

                FeedContentData.POST_TYPE_SEASON -> {
                    setSeriesData(
                        feedContentData.seriesId,
                        feedContentData.seasonId,
                        "0",
                        "false"
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
                    setPlaylistData(id)
                }

                FeedContentData.POST_TYPE_POST -> {
                    //getSingleMediaItem(id);
                    arrayListSearchPlayContent.clear()
                    arrayListSearchPlayContent.add(feedContentData)
                    homeActivity!!.showAudioSongExpandedView(
                        arrayListSearchPlayContent,
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

    private fun openFragment(fragment: Fragment) {
        val fragmentTransaction =
            Objects.requireNonNull(requireActivity()).supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.container, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    fun navMenuToggle(toShow: Boolean) {
        try {
            if (toShow) {
                clKeyboard!!.clearFocus();
                clKeyboard!!.descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS;
                rvSearch!!.requestFocus();
                rvSearch!!.descendantFocusability = ViewGroup.FOCUS_AFTER_DESCENDANTS;
                restorePosition()
            } else {
                rvSearch!!.clearFocus();
                rvSearch!!.descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS;
                clKeyboard!!.requestFocus();
                clKeyboard!!.descendantFocusability = ViewGroup.FOCUS_AFTER_DESCENDANTS;
                lastSelectedChar!!.requestFocus()
            }
        } catch (e: Exception) {
            e.printStackTrace();
        }
    }

    fun restorePosition() {
        try {
            Handler().postDelayed({
                if (adapterTv != null && arrayListSearchContent.isNotEmpty()) {
                    smoothScroller!!.targetPosition = lastSelectedItem
                    manager!!.startSmoothScroll(smoothScroller)
                    Objects.requireNonNull(arrayListSearchContent[lastSelectedItem].view)
                        .requestFocus()
                    adapterTv!!.canAccess = true
                }
            }, 10)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun setNavigationMenuCallbackSearch(callback: NavigationMenuCallback) {
        this.navigationMenuCallback = callback
    }

    fun selectFirstChar() {
        try {
            Handler().postDelayed({
                lastSelectedChar!!.requestFocus()
            }, 0)
        } catch (e: java.lang.Exception) {
        }
    }

    override fun onResume() {
        super.onResume()
        if (arrayListSearchContent.isNotEmpty()) {
            restorePosition()
        }
        (activity as HomeActivity?)!!.showToolbar(false)
        (activity as HomeActivity?)!!.nav_fragment.visibility = View.VISIBLE
        isRefreshRequired = true
    }

    override fun onPause() {
        super.onPause()
        isRefreshRequired = false
    }

}