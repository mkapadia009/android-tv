package com.app.itaptv.tv_fragment

import android.annotation.SuppressLint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.leanback.app.RowsSupportFragment
import androidx.leanback.widget.*
import com.android.volley.Request
import com.app.itaptv.API.APIManager
import com.app.itaptv.API.APIRequest
import com.app.itaptv.API.Url
import com.app.itaptv.FeedClickHelper
import com.app.itaptv.R
import com.app.itaptv.activity.HomeActivity
import com.app.itaptv.custom_interface.NavigationMenuCallback
import com.app.itaptv.fragment.HomeSliderTabFragment
import com.app.itaptv.fragment.HomeSliderTabFragment.TAB_TYPE
import com.app.itaptv.structure.FeedCombinedData
import com.app.itaptv.structure.FeedCombinedData.*
import com.app.itaptv.structure.FeedContentData
import com.app.itaptv.structure.HomeSliderObject
import com.app.itaptv.tvControllers.CardListRow
import com.app.itaptv.tvControllers.CardPresenterSelector
import com.app.itaptv.tvControllers.RowPresenterSelector
import com.app.itaptv.utils.AlertUtils
import com.app.itaptv.utils.Constant
import com.app.itaptv.utils.ExoUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_coupon.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_teaser.*
import kotlinx.android.synthetic.main.card_subject.view.*
import kotlinx.android.synthetic.main.fragment_movie.*
import kotlinx.android.synthetic.main.fragment_movie.promoPlayer
import kotlinx.android.synthetic.main.fragment_movie.view.*
import kotlinx.android.synthetic.main.fragment_movie.view.tvTitle
import kotlinx.android.synthetic.main.fragment_nav_menu.*
import kotlinx.android.synthetic.main.layout_media_controller.view.*
import kotlinx.android.synthetic.main.sliding_layout.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class PremiumTvFragment : RowsSupportFragment() {

    private var tabType = ""
    var TAB_TYPE = "tabType"
    var mRowsAdapter: ArrayObjectAdapter = ArrayObjectAdapter(RowPresenterSelector())

    private var v: View? = null
    private var llProgressBar: RelativeLayout? = null
    private var progressBar: ProgressBar? = null
    private var btPlayNow: Button? = null
    private var clBanner: ConstraintLayout? = null
    private var clHeader: ConstraintLayout? = null
    private var promoPlayerBanner: PlayerView? = null
    private var ivBigBanner: ImageView? = null
    private var tvTitleBanner: TextView? = null
    private var tvTitle: TextView? = null
    private var tvDescTextBanner: TextView? = null
    private var tvDescText: TextView? = null
    private var flVideo: FrameLayout? = null
    private var flImage: FrameLayout? = null
    private var ivBanner: ImageView? = null
    private var rlList: RelativeLayout? = null
    private var promoPlayer: PlayerView? = null

    lateinit var navigationMenuCallback: NavigationMenuCallback
    var player: ExoPlayer? = null
    var playerBanner: ExoPlayer? = null
    var handler: Handler? = Handler()

    private val arrayListFeedData = ArrayList<FeedCombinedData>()
    private val arrayListRowData = ArrayList<FeedCombinedData>()
    private val arrayListPageWiseFeedData = ArrayList<FeedCombinedData>()
    private var arrayListFeedContent = ArrayList<FeedContentData>()
    private var bigSliderObjectArrayList = ArrayList<FeedCombinedData>()

    var nextPageNo = 1
    var currentFeedIndex = 0
    var selectedIndex: Int = 0;
    var selectedRowIndex: Int = 0;
    var isExpanded: Boolean = false

    companion object {
        @JvmStatic
        fun getInstance(tabType: String?): PremiumTvFragment? {
            val bundle = Bundle()
            bundle.putString(TAB_TYPE, tabType)
            val premiumTvFragment = PremiumTvFragment()
            premiumTvFragment.arguments = bundle
            return premiumTvFragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (v == null) {
            v = inflater.inflate(R.layout.fragment_movie, container, false);
            // calling super so that 'mVerticalGridView' get set in super class via 'findGridViewFromRoot'
            init()
        }
        super.onCreateView(inflater, container, savedInstanceState);
        return v;
    }

    override fun findGridViewFromRoot(view: View?): VerticalGridView? {
        return v!!.findViewById(R.id.container_list)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            tabType = requireArguments().getString(HomeSliderTabFragment.TAB_TYPE)!!
        }
    }

    private fun initAdapters() {
        adapter = mRowsAdapter
    }

    @SuppressLint("ResourceAsColor")
    private fun initListeners() {
        onItemViewSelectedListener =
            OnItemViewSelectedListener { itemViewHolder, item, rowViewHolder, row ->
                val indexOfRow = mRowsAdapter.indexOf(row)
                selectedRowIndex = indexOfRow
                val indexOfItem = ((row as CardListRow).adapter as ArrayObjectAdapter).indexOf(item)
                selectedIndex = indexOfItem
                val size = ((row as CardListRow).adapter as ArrayObjectAdapter).size()

                val rowSize = mRowsAdapter.size()
                //itemViewHolder?.view?.requestFocus()
                if (indexOfRow == rowSize - 3) {
                    if (nextPageNo != 0) {
                        getFeedsAPI()
                    }
                }

                if (item is FeedContentData) {
                    if (!item.feedContentType.equals("viewall") && !isExpanded) {
                        cleanupBanner()
                        cleanup()
                        /* if (item.tileShape.equals("lv_rectangle")) {
                             clHeader!!.visibility = View.GONE
                             val params = rlList!!.layoutParams as ConstraintLayout.LayoutParams
                             params.height = ConstraintLayout.LayoutParams.MATCH_PARENT
                             params.setMargins(0, 0, 0, 0)
                             if (clBanner!!.visibility == View.GONE) {
                                 itemViewHolder.view.requestFocus()
                             }

                             val scale = requireContext().resources.displayMetrics.density
                             val dpWidthInPx = (540 * scale).toInt()
                             val dpHeightInPx = (400 * scale).toInt()
                             val layoutParams =
                                 RelativeLayout.LayoutParams(dpWidthInPx, dpHeightInPx)
                             itemViewHolder?.view?.ivFeedImage!!.layoutParams = layoutParams
                             Handler().postDelayed({
                                 mRowsAdapter.notifyItemRangeChanged(indexOfItem, mRowsAdapter.size())
                             },10)
                         } else {*/
                        if (clBanner!!.visibility == View.GONE) {
                            clHeader!!.visibility = View.VISIBLE
                            setView(false)
                        }
                        tvDescText!!.text = item.teaserDescription
                        tvTitle!!.text = item.postTitle
                        flImage!!.visibility = View.VISIBLE
                        context?.let {
                            if (item.horizontalRectangleImg.isEmpty()) {
                                Glide.with(it)
                                    .load(item.imgUrl)
                                    .thumbnail(0.1f)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .transition(DrawableTransitionOptions.withCrossFade())
                                    .into(ivBanner!!)
                            } else {
                                Glide.with(it)
                                    .load(item.horizontalRectangleImg)
                                    .thumbnail(0.1f)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .transition(DrawableTransitionOptions.withCrossFade())
                                    .into(ivBanner!!)
                            }
                        }
                        flVideo!!.visibility = View.GONE
                        if (item.teaserUrl != null && item.teaserUrl.isNotEmpty()) {
                            handler!!.postDelayed({
                                flVideo!!.visibility = View.VISIBLE
                                flImage!!.visibility = View.GONE
                                play(item.teaserUrl)
                            }, 3000)
                        }
                    }
                    // }
                }

                itemViewHolder?.view?.setOnKeyListener { v, keyCode, event ->
                    if (event.action == KeyEvent.ACTION_DOWN) {
                        handler!!.removeCallbacksAndMessages(null)
                        when (keyCode) {
                            KeyEvent.KEYCODE_DPAD_LEFT -> {
                                if (indexOfItem == 0) {
                                    navigationMenuCallback.navMenuToggle(true)
                                }
                            }
                            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                            }
                            KeyEvent.KEYCODE_DPAD_UP -> {
                                if (indexOfRow == 0 && bigSliderObjectArrayList.isNotEmpty()) {
                                    setView(true)
                                    setBanner()
                                } else if (indexOfRow == 0 && bigSliderObjectArrayList.isEmpty()) {
                                    return@setOnKeyListener true
                                }
                            }
                            KeyEvent.KEYCODE_DPAD_DOWN -> {
                            }
                        }
                    }
                    false
                }
            }

        onItemViewClickedListener =
            OnItemViewClickedListener { itemViewHolder, item, rowViewHolder, row ->
                val indexOfRow = mRowsAdapter.indexOf(row)
                val indexOfItem = ((row as CardListRow).adapter as ArrayObjectAdapter).indexOf(item)
                FeedClickHelper().handleFeedItemClick(
                    context,
                    arrayListRowData[indexOfRow],
                    item,
                    indexOfItem
                )
            }
    }

    private fun init() {
        if (activity != null) {
            (activity as HomeActivity?)!!.setToolbar(false)
        }
        llProgressBar = v!!.findViewById(R.id.llprogressbar)
        progressBar = v!!.findViewById(R.id.progressBar)
        btPlayNow = v!!.findViewById(R.id.btPlayNow)
        clBanner = v!!.findViewById(R.id.clBanner)
        clHeader = v!!.findViewById(R.id.clHeader)
        promoPlayerBanner = v!!.findViewById(R.id.promoPlayerBanner)
        ivBigBanner = v!!.findViewById(R.id.ivBigBanner)
        tvTitleBanner = v!!.findViewById(R.id.tvTitleBanner)
        tvTitle = v!!.findViewById(R.id.tvTitle)
        tvDescTextBanner = v!!.findViewById(R.id.tvDescTextBanner)
        tvDescText = v!!.findViewById(R.id.tvDescText)
        flVideo = v!!.findViewById(R.id.flVideo)
        flImage = v!!.findViewById(R.id.flImage)
        ivBanner = v!!.findViewById(R.id.ivBanner)
        rlList = v!!.findViewById(R.id.rlList)
        promoPlayer = v!!.findViewById(R.id.promoPlayer)

        llProgressBar!!.visibility = View.VISIBLE
        progressBar!!.visibility = View.VISIBLE
        initAdapters()
        initListeners()
        getFeedsAPI()
        setView(false)
    }

    /**
     * Returns feeds data related to currently selected tab
     */
    private fun getFeedsAPI() {
        try {
            val params: Map<String, String> = HashMap()
            val apiRequest = APIRequest(
                Url.GET_FEEDS + tabType + "&f_page_no=" + nextPageNo + "&tab_screen=" + Constant.SCREEN_HOME,
                Request.Method.GET,
                params,
                null,
                context
            )
            apiRequest.showLoader = false
            APIManager.request(
                apiRequest
            ) { response, error, headers, statusCode ->
                progressBar!!.visibility = View.GONE
                llProgressBar!!.visibility = View.GONE
                handleFeedResponse(response, error)
            }
        } catch (e: Exception) {
        }
    }

    /**
     * This method handles feed response
     *
     * @param response api response. Can be null
     * @param error    error message in case of error. Can be null
     */
    private fun handleFeedResponse(response: String?, error: Exception?) {
        //arrayListFeedData.clear();
        try {
            if (error != null) {
                // showError(error.getMessage());
            } else {
                if (response != null) {
                    //Log.e("response", response);
                    val jsonObjectResponse = JSONObject(response)
                    val type =
                        if (jsonObjectResponse.has("type")) jsonObjectResponse.getString("type") else ""
                    if (type.equals("error", ignoreCase = true)) {
                        val message =
                            if (jsonObjectResponse.has("msg")) jsonObjectResponse.getString("msg") else APIManager.GENERIC_API_ERROR_MESSAGE
                    } else if (type.equals("ok", ignoreCase = true)) {
                        val jsonArrayMsg = jsonObjectResponse.getJSONObject("msg")
                        val jsonArrayContents = jsonArrayMsg.getJSONArray("contents")
                        nextPageNo =
                            if (jsonArrayMsg.has("next_page")) jsonArrayMsg.getInt("next_page") else 0
                        Thread {
                            try {
                                currentFeedIndex += arrayListPageWiseFeedData.size
                                val position: Int = arrayListPageWiseFeedData.size
                                arrayListPageWiseFeedData.clear()
                                for (i in 0 until jsonArrayContents.length()) {
                                    val feedCombinedData = FeedCombinedData(
                                        jsonArrayContents.getJSONObject(i),
                                        position + i
                                    )
                                    arrayListPageWiseFeedData.add(feedCombinedData)
                                }
                                arrayListFeedData.addAll(
                                    currentFeedIndex,
                                    arrayListPageWiseFeedData
                                )
                                Handler(Looper.getMainLooper()).post { updateUI() }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }.start()
                    }
                }
            }
        } catch (e: JSONException) {

        }
    }

    @SuppressLint("ResourceType")
    fun updateUI() {
        Thread {
            for (feedCombinedData in arrayListPageWiseFeedData) {
                when (feedCombinedData.viewType) {
                    VIEW_TYPE_H_LIST -> {
                        Handler(Looper.getMainLooper()).post {
                            arrayListFeedContent = feedCombinedData.arrayListFeedContent
                            createRows(feedCombinedData)
                        }
                    }
                    VIEW_TYPE_SLIDER -> {
                        Handler(Looper.getMainLooper()).post {

                        }
                    }
                    VIEW_TYPE_BIG_SLIDER -> {
                    }
                    VIEW_TYPE_TABS -> {
                        Handler(Looper.getMainLooper()).post {
                            handleTabsArray(feedCombinedData.bigSliderObjectArrayList)
                        }
                    }
                    VIEW_TYPE_AD -> {}
                }
            }
        }.start()

        /* Handler(Looper.getMainLooper()).post {
             vpHome.requestFocus()
         }*/
        //(requireView().context as Activity).runOnUiThread { if (vpHome.visibility == View.VISIBLE) vpHome.requestFocus() }

    }

    private fun createRows(feedCombinedData: FeedCombinedData) {
        mRowsAdapter.add(createNewRow(feedCombinedData))
    }

    private fun createNewRow(feedCombinedData: FeedCombinedData): Row {
        val presenterSelector = activity?.baseContext?.let {
            CardPresenterSelector(it, feedCombinedData)
        }
        val adapter = ArrayObjectAdapter(presenterSelector)
        var headerItem = HeaderItem("")
        arrayListRowData.add(feedCombinedData)
        when (feedCombinedData.feedType) {
            FEED_TYPE_BUY -> {
                //feedCombinedData.arrayListBuyFeedContent.shuffle()
                if (feedCombinedData.arrayListBuyFeedContent.size >= 10) {
                    for (i in 0..9) {
                        adapter.add(feedCombinedData.arrayListBuyFeedContent[i])
                        headerItem = HeaderItem(feedCombinedData.title)
                    }
                } else {
                    for (data in feedCombinedData.arrayListBuyFeedContent) {
                        adapter.add(data)
                        headerItem = HeaderItem(feedCombinedData.title)
                    }
                }
                val feedContentData = FeedContentData()
                feedContentData.feedContentType = "viewall"
                adapter.add(feedContentData)
            }
            FEED_TYPE_CONTENT -> {
                //feedCombinedData.arrayListFeedContent.shuffle()
                if (feedCombinedData.arrayListFeedContent.size >= 10) {
                    for (i in 0..9) {
                        adapter.add(feedCombinedData.arrayListFeedContent[i])
                        headerItem = HeaderItem(feedCombinedData.title)
                    }
                } else {
                    for (data in feedCombinedData.arrayListFeedContent) {
                        adapter.add(data)
                        headerItem = HeaderItem(feedCombinedData.title)
                    }
                }
                val feedContentData = FeedContentData()
                feedContentData.feedContentType = "viewall"
                adapter.add(feedContentData)
            }
        }
        return CardListRow(headerItem, adapter)
    }

    fun setNavigationMenuCallbackPremium(callback: NavigationMenuCallback) {
        this.navigationMenuCallback = callback
    }

    /**
     * this function can put focus or select a specific item in a specific row
     */
    fun restoreSelection() {
        Handler().postDelayed({
            if (!isExpanded) {
                if (clHeader!!.visibility == View.VISIBLE) {
                    setView(false)
                }
                setSelectedPosition(
                    selectedRowIndex,
                    true,
                    object : ListRowPresenter.SelectItemViewHolderTask(selectedIndex) {
                        override fun run(holder: Presenter.ViewHolder?) {
                            super.run(holder)
                            holder?.view?.postDelayed({
                                holder.view.requestFocus()
                            }, 10)
                        }
                    })
            } else {
                setBanner()
            }
        }, 10)
    }

    fun selectFirstItem() {
        if (!isExpanded) {
            setSelectedPosition(
                selectedRowIndex,
                true,
                object : ListRowPresenter.SelectItemViewHolderTask(selectedIndex) {
                    override fun run(holder: Presenter.ViewHolder?) {
                        super.run(holder)
                        holder?.view?.postDelayed({
                            holder.view.requestFocus()
                        }, 10)
                    }
                })
        } else {
            Handler().postDelayed({
                setBanner()
            }, 10)
        }
    }

    override fun onResume() {
        super.onResume()
        if (arrayListFeedContent.isNotEmpty()) {
            restoreSelection()
        }
        (activity as HomeActivity?)!!.showToolbar(false)
        (activity as HomeActivity?)!!.nav_fragment.visibility = View.VISIBLE
    }

    override fun onPause() {
        super.onPause()
        handler!!.removeCallbacksAndMessages(null)
        cleanupBanner()
        cleanup()
        if (isExpanded) {
            promoPlayerBanner!!.visibility = View.GONE
            ivBigBanner!!.visibility = View.VISIBLE
        } else {
            flVideo!!.visibility = View.GONE
            flImage!!.visibility = View.VISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cleanup()
        cleanupBanner()
    }

    private fun handleTabsArray(bigSliderList: ArrayList<FeedCombinedData>) {
        bigSliderObjectArrayList.clear()
        bigSliderObjectArrayList.addAll(bigSliderList)
        if (bigSliderList.isEmpty()) {
            setView(false)
        } else {
            setView(true)
            setBanner()
        }
    }

    private fun setBanner() {
        cleanupBanner()
        cleanup()
        ivBigBanner!!.visibility = View.VISIBLE
        promoPlayerBanner!!.visibility = View.GONE
        context?.let {
            Glide.with(it)
                .load(bigSliderObjectArrayList.get(0).homeSliderObjectArrayList.get(0).feedContentData.image)
                .thumbnail(0.1f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(object : SimpleTarget<Drawable?>() {
                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable?>?
                    ) {
                        val bitmap = (resource as BitmapDrawable).bitmap
                        val imageWidth = bitmap.width
                        val imageHeight = bitmap.height
                        ivBigBanner!!.setImageDrawable(resource)
                        val viewWidth: Int = ivBigBanner!!.width
                        val scale = viewWidth - imageWidth
                        val height = imageHeight + scale
                        ivBigBanner!!.layoutParams.height = height
                    }
                })
        }
        tvTitleBanner!!.text =
            bigSliderObjectArrayList.get(0).homeSliderObjectArrayList.get(0).feedContentData.postTitle
        tvDescTextBanner!!.text =
            bigSliderObjectArrayList.get(0).homeSliderObjectArrayList.get(0).feedContentData.description
        promoPlayerBanner!!.visibility = View.GONE
        val url: String? =
            bigSliderObjectArrayList.get(0).homeSliderObjectArrayList.get(0).feedContentData.teaserUrl
        if (url != null && url!!.isNotEmpty()) {
            // videoViewBanner!!.setVideoURI(Uri.parse(url))
            handler!!.postDelayed({
                /* videoViewBanner!!.visibility = View.VISIBLE
                 videoViewBanner!!.setOnPreparedListener { mp ->
                     videoViewBanner!!.start()
                     ivBigBanner!!.visibility = View.GONE
                 }
                 videoViewBanner!!.setOnCompletionListener {
                     ivBigBanner!!.visibility = View.VISIBLE
                 }
                 videoViewBanner!!.setOnErrorListener { mediaPlayer, i, i2 ->
                     ivBigBanner!!.visibility = View.VISIBLE
                     videoViewBanner!!.visibility = View.GONE
                     return@setOnErrorListener true
                 }*/
                playBanner(url)
            }, 2000)
        }
        /* btPlayNow!!.onFocusChangeListener = View.OnFocusChangeListener { view, b ->
             if (view.hasFocus()) {
                 view.background = requireContext().getDrawable(R.drawable.bg_white)
                 btPlayNow.setTextColor(resources.getColor(R.color.black))
                 clBanner.visibility = View.VISIBLE
                 clHeader.visibility = View.GONE
                 setMargin(true)
             } else {
                 view.background = requireContext().getDrawable(R.drawable.bg_button_grey)
                 btPlayNow.setTextColor(resources.getColor(R.color.white))
                 clBanner.visibility = View.GONE
                 clHeader.visibility = View.VISIBLE
                 setMargin(false)

             }
         }*/
        btPlayNow!!.setOnClickListener {
            setActionOnClick(
                homeSliderObject = bigSliderObjectArrayList[0].homeSliderObjectArrayList[0]
            )
        }
        btPlayNow!!.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_LEFT -> {
                        navigationMenuCallback.navMenuToggle(true)
                    }
                    KeyEvent.KEYCODE_DPAD_RIGHT -> {
                        return@setOnKeyListener true
                    }
                    KeyEvent.KEYCODE_DPAD_UP -> {
                        return@setOnKeyListener true
                    }
                    KeyEvent.KEYCODE_DPAD_DOWN -> {
                        promoPlayerBanner!!.visibility = View.GONE
                        ivBigBanner!!.visibility = View.VISIBLE
                        handler!!.removeCallbacksAndMessages(null)
                        setSelectedPosition(
                            0,
                            true,
                            object : ListRowPresenter.SelectItemViewHolderTask(0) {
                                override fun run(holder: Presenter.ViewHolder?) {
                                    super.run(holder)
                                    holder?.view?.postDelayed({
                                        holder.view.requestFocus()
                                    }, 0)
                                }
                            })
                        setView(false)
                    }
                }
            }
            false
        }
        btPlayNow!!.requestFocus()
    }

    fun setActionOnClick(homeSliderObject: HomeSliderObject) {
        if (homeSliderObject.feedContentData != null) {
            FeedClickHelper().setActionOnSlider(
                homeSliderObject.feedContentData, homeSliderObject.id.toString(), context
            )
        }
    }

    private fun setMargin(set: Boolean) {
        if (set) {
            val params = rlList!!.layoutParams as ConstraintLayout.LayoutParams
            params.height = ConstraintLayout.LayoutParams.WRAP_CONTENT
            val scale = requireContext().resources.displayMetrics.density
            val marginTop: Int = (400 * scale).toInt()
            params.setMargins(0, marginTop, 0, 0)
        } else {
            val scale = requireContext().resources.displayMetrics.density
            val marginTop: Int = (250 * scale).toInt()
            val params = rlList!!.layoutParams as ConstraintLayout.LayoutParams
            params.height = ConstraintLayout.LayoutParams.MATCH_PARENT
            params.setMargins(0, marginTop, 0, 0)
        }
    }

    private fun setView(expand: Boolean) {
        cleanup()
        cleanupBanner()
        if (expand) {
            isExpanded = true
            clBanner!!.visibility = View.VISIBLE
            clHeader!!.visibility = View.GONE
            setMargin(true)
            Handler().postDelayed({
                btPlayNow!!.requestFocus()
            }, 10)
        } else {
            isExpanded = false
            clBanner!!.visibility = View.GONE
            clHeader!!.visibility = View.VISIBLE
            setMargin(false)
            Handler().postDelayed({
                btPlayNow!!.clearFocus()
            }, 10)
        }
    }

    private fun play(url: String) {
        cleanupBanner()
        cleanup()
        // Data source
        val bandwidthMeter = DefaultBandwidthMeter()

        val adaptiveTrackSelection = DefaultTrackSelector(requireContext())
        val dataSourceFactory = DefaultDataSourceFactory(
            requireContext(),
            Util.getUserAgent(requireContext(), getString(R.string.app_name)), bandwidthMeter
        )
        val mediaSource = ExoUtil.buildMediaSource(Uri.parse(url), dataSourceFactory, null)

        // Player
        player =
            ExoPlayer.Builder(requireContext()).setTrackSelector(adaptiveTrackSelection).build()
        player!!.addListener(object : Player.Listener {

            override fun onLoadingChanged(isLoading: Boolean) {
                if (isLoading) {
                    // show loader
                } else {
                    // hide loader
                }
            }

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                super.onPlayerStateChanged(playWhenReady, playbackState)
                if (playWhenReady && playbackState == Player.STATE_READY) {
                    flVideo!!.visibility = View.VISIBLE
                    flImage!!.visibility = View.GONE
                } else if (playbackState == Player.STATE_ENDED) {
                    flVideo!!.visibility = View.GONE
                    flImage!!.visibility = View.VISIBLE
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                AlertUtils.showAlert(
                    getString(R.string.label_error),
                    getString(R.string.unable_to_play_the_promo) + "${error.message}",
                    null, requireContext()
                ) {
                    flVideo!!.visibility = View.GONE
                    flImage!!.visibility = View.VISIBLE
                }
            }
        })
        // Prepare player with media source
        if (mediaSource != null) player!!.prepare(mediaSource)
        // Start playing as the player is prepared
        player!!.playWhenReady = true

        // Set player
        if (promoPlayer != null) {
            promoPlayer!!.player = player
        }
    }

    private fun cleanup() {
        if (promoPlayer != null && player != null) {
            promoPlayer!!.player = null
            player!!.stop()
            player!!.clearVideoSurface()
            player!!.release()
            player = null
        }
    }

    private fun playBanner(url: String) {
        cleanupBanner()
        cleanup()
        // Data source
        val bandwidthMeter = DefaultBandwidthMeter()

        val adaptiveTrackSelection = DefaultTrackSelector(requireContext())
        val dataSourceFactory = DefaultDataSourceFactory(
            requireContext(),
            Util.getUserAgent(requireContext(), getString(R.string.app_name)), bandwidthMeter
        )
        val mediaSource = ExoUtil.buildMediaSource(Uri.parse(url), dataSourceFactory, null)

        // Player
        playerBanner =
            ExoPlayer.Builder(requireContext()).setTrackSelector(adaptiveTrackSelection).build()
        playerBanner!!.addListener(object : Player.Listener {

            override fun onLoadingChanged(isLoading: Boolean) {
                if (isLoading) {
                    // show loader
                } else {
                    // hide loader
                }
            }

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                super.onPlayerStateChanged(playWhenReady, playbackState)
                if (playWhenReady && playbackState == Player.STATE_READY) {
                    promoPlayerBanner!!.visibility = View.VISIBLE
                    ivBigBanner!!.visibility = View.GONE
                } else if (playbackState == Player.STATE_ENDED) {
                    promoPlayerBanner!!.visibility = View.GONE
                    ivBigBanner!!.visibility = View.VISIBLE
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                AlertUtils.showAlert(
                    getString(R.string.label_error),
                    getString(R.string.unable_to_play_the_promo) + "${error.message}",
                    null, requireContext()
                ) {
                    promoPlayerBanner!!.visibility = View.GONE
                    ivBigBanner!!.visibility = View.VISIBLE
                }
            }
        })
        // Prepare player with media source
        if (mediaSource != null) playerBanner!!.prepare(mediaSource)
        // Start playing as the player is prepared
        playerBanner!!.playWhenReady = true

        // Set player
        if (promoPlayerBanner != null) {
            promoPlayerBanner!!.player = playerBanner
        }
    }

    private fun cleanupBanner() {
        if (promoPlayerBanner != null && playerBanner != null) {
            promoPlayerBanner!!.player = null
            playerBanner!!.stop()
            playerBanner!!.clearVideoSurface()
            playerBanner!!.release()
            playerBanner = null
        }
    }
}
