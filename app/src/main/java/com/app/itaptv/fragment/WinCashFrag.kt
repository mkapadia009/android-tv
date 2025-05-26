package com.app.itaptv.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.app.itaptv.API.APIManager
import com.app.itaptv.API.APIMethods
import com.app.itaptv.API.APIRequest
import com.app.itaptv.API.Url
import com.app.itaptv.MyApp
import com.app.itaptv.R
import com.app.itaptv.activity.BrowserActivity
import com.app.itaptv.activity.HomeActivity
import com.app.itaptv.activity.LearnMoreActivity
import com.app.itaptv.activity.VerificationActivity
import com.app.itaptv.custom_widget.DotsIndicatorDecoration
import com.app.itaptv.holder.CustomAdSliderHolder
import com.app.itaptv.structure.AdFieldsData
import com.app.itaptv.structure.FeedContentData
import com.app.itaptv.structure.User
import com.app.itaptv.utils.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.kalpesh.krecycleradapter.KRecyclerViewAdapter
import org.json.JSONException
import org.json.JSONObject

class WinCashFrag : Fragment() {

    private lateinit var rootView: View

    private lateinit var winCashConstraintParent: ConstraintLayout
    private lateinit var noOfferDescText: TextView
    private lateinit var iCoinsTv: TextView
    private lateinit var rupeesTv: TextView
    private lateinit var tvCurrency: TextView
    private lateinit var winCashRedeemNowButton: ImageView
    private lateinit var winCashLearnMoreButton: ImageView
    private lateinit var winCashProgressBar: ProgressBar
    private lateinit var mAdView: AdView
    private lateinit var adRequest: AdRequest
    private lateinit var clAdHolder: ConstraintLayout
    private lateinit var ivCustomAd: ImageView
    private lateinit var ivClose: ImageView
    private lateinit var playerView: PlayerView
    private lateinit var ivVolumeUp: ImageView
    private lateinit var ivVolumeOff: ImageView
    private lateinit var learnMoreContent: String

    private lateinit var user: User
    var homeActivity: HomeActivity? = null
    private var resumePlayer = false

    private var playerWinCash: ExoPlayer? = null

    private var rvSliderAd: RecyclerView? = null
    private var layoutManager: CustomLinearLayoutManager? = null
    private var adapterSliderAd: KRecyclerViewAdapter? = null
    private var flagDecoration = false
    private val adSliderObjectArrayList = ArrayList<AdFieldsData>()

    private var sliderIdAd: String? = null

    // for slider
    private val runnable: Runnable? = null
    private val count = 0
    private var visibleItemPosition = 0
    private var sliderPosition = -1
    private var shouldSlide = true
    private var flagAd = false

    companion object {
        @JvmStatic
        fun newInstance(): WinCashFrag {
            return WinCashFrag()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_win_cash, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    @SuppressLint("SetTextI18n")
    private fun init() {

        winCashRedeemNowButton = rootView.findViewById(R.id.winCashRedeemNowButton)
        winCashLearnMoreButton = rootView.findViewById(R.id.winCashLearnMoreButton)
        iCoinsTv = rootView.findViewById(R.id.iCoinsTv)
        rupeesTv = rootView.findViewById(R.id.rupeesTv)
        tvCurrency = rootView.findViewById(R.id.tvCurrency)
        winCashProgressBar = rootView.findViewById(R.id.progressBar)
        winCashConstraintParent = rootView.findViewById(R.id.winCashConstraintParent)
        noOfferDescText = rootView.findViewById(R.id.noOfferDescText)

        user = LocalStorage.getUserData()

        iCoinsTv.text = LocalStorage.getNumberOfCoins() + " = "
        tvCurrency.text = LocalStorage.getUserData().currency
        rupeesTv.text = LocalStorage.getRedeemCashValue()

        learnMoreContent = LocalStorage.getWinCashLearnMoreContent()
        winCashLearnMoreButton.setOnClickListener {
            val id = resources.getResourceEntryName(winCashLearnMoreButton.id)
            Log.i("WinCashFrag", id.toString())
            startActivity(
                Intent(
                    requireContext(),
                    LearnMoreActivity::class.java
                ).putExtra("fromWinCash", true)
                    .putExtra("winCashLearnMoreContent", learnMoreContent)
            )
            showAd(id.toString())
        }

        winCashRedeemNowButton.setOnClickListener {
            winCashRedeemNowButton.isClickable = false
            checkVerificationOffer()
        }
        if (LocalStorage.getIsBannerActive()) {
            winCashConstraintParent.visibility = View.VISIBLE
            noOfferDescText.visibility = View.GONE
        } else {
            noOfferDescText.visibility = View.VISIBLE
            winCashConstraintParent.visibility = View.GONE
        }

        mAdView = rootView.findViewById(R.id.adViewWinCashFrag)
        clAdHolder = rootView.findViewById(R.id.cl_ad_holder)
        ivCustomAd = rootView.findViewById(R.id.ivCustomAd)
        ivClose = rootView.findViewById(R.id.ivClose)
        playerView = rootView.findViewById(R.id.playerView)
        ivVolumeUp = rootView.findViewById(R.id.ivVolumeUp)
        ivVolumeOff = rootView.findViewById(R.id.ivVolumeOff)
        rvSliderAd = rootView.findViewById(R.id.rvSliderAd)
        adRequest = AdRequest.Builder().build()
        val id =
            resources.getResourceEntryName(rootView.findViewById<View>(R.id.adViewWinCashFrag).id)
        showBannerAd(id)
    }

    private fun checkVerificationOffer() {
        if (user.mobile == null || user.mobile == "") {
            startActivity(
                Intent(requireContext(), VerificationActivity::class.java).putExtra(
                    VerificationActivity.TYPE_KEY,
                    Constant.KEY_MOBILE
                )
            )
        }/* else if (user.userEmail == null || user.userEmail == "") {
            startActivity(Intent(requireContext(), VerificationActivity::class.java).putExtra(VerificationActivity.TYPE_KEY, Constant.KEY_EMAIL))
        } */ else if (user.displayName == null || user.displayName == "") {
            setupUserNameDialog(
                requireContext(),
                resources.getString(R.string.label_edit_profile_name_desc_redeem)
            )
        } else {
            startRedeemOfferFunctionalityAPI()
        }
        winCashRedeemNowButton.isClickable = true
    }

    private fun startRedeemOfferFunctionalityAPI() {
        winCashProgressBar.visibility = View.VISIBLE
        try {
            val params: Map<String, String> = HashMap()
            val apiRequest = APIRequest(
                Url.REDEEM_COINS_FOR_CASH + "&userId=" + user.userId,
                Request.Method.GET,
                params,
                null,
                requireContext()
            )
            apiRequest.showLoader = false
            APIManager.request(apiRequest) { response, error, _, _ ->
                winCashProgressBar.visibility = View.GONE
                handleCoinOfferResponse(response, error)
            }
        } catch (e: java.lang.Exception) {

        }
    }

    private fun handleCoinOfferResponse(response: String?, error: java.lang.Exception?) {
        try {
            if (error != null) {
                showError(getString(R.string.error_contact_us))
            } else {
                if (response != null) {
                    //Log.e("response", response);
                    val jsonObjectResponse = JSONObject(response)
                    val type =
                        if (jsonObjectResponse.has("type")) jsonObjectResponse.getString("type") else ""
                    if (type.equals("error", ignoreCase = true)) {
                        val message: String =
                            if (jsonObjectResponse.has("msg")) jsonObjectResponse.getString("msg") else APIManager.GENERIC_API_ERROR_MESSAGE
                        val result = message.replace("\\n", "\n")
                        showRedeemCoinsResponseDialog(requireContext(), result)
                    } else if (type.equals("ok", ignoreCase = true)) {
                        val jsonArrayMsg = jsonObjectResponse.getJSONObject("msg")
                        val status: Boolean = jsonArrayMsg.getBoolean("status")
                        if (status){
                            Utility.customEventsTracking(Constant.WalletRedemptions,"")
                        }
                        val message: String = jsonArrayMsg.getString("message")
                        val result = message.replace("\\n", "\n")
                        showRedeemCoinsResponseDialog(requireContext(), result)
                    }
                }
                winCashRedeemNowButton.isClickable = true
            }
        } catch (e: JSONException) {
            //showError(error!!.message)
        }
    }

    private fun showRedeemCoinsResponseDialog(context: Context, message: String?) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.dialog_redeem_coins_success, null)
        val iv_go = view.findViewById<ImageView>(R.id.iv_got_it)
        val tv_cash_won = view.findViewById<TextView>(R.id.tv_cash_won)
        val alertDialogBuilder = Dialog(context)
        alertDialogBuilder.setContentView(view)
        alertDialogBuilder.show()
        tv_cash_won.text = message
        val homeActivity: HomeActivity = activity as HomeActivity
        homeActivity.getWalletBalance()
        iv_go.setOnClickListener { view1: View? -> alertDialogBuilder.dismiss() }
    }

    private fun setupUserNameDialog(context: Context, dialogDesc: String?) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.dialog_set_name, null)
        val edit_text_name = view.findViewById<EditText>(R.id.dialog_editText_name)
        val text_view_desc = view.findViewById<TextView>(R.id.textView3)
        val btn_set = view.findViewById<Button>(R.id.btn_set)
        text_view_desc.text = dialogDesc
        val alertDialogBuilder = Dialog(context)
        alertDialogBuilder.setContentView(view)
        alertDialogBuilder.show()
        btn_set.setOnClickListener { view1: View? ->
            val name = edit_text_name.text.toString()
            if (name == "") {
                showError(getString(R.string.error_msg_enter_mobile_no))
                return@setOnClickListener
            }
            updateProfileAPI(name)
            alertDialogBuilder.dismiss()
        }
    }

    private fun updateProfileAPI(username: String) {
        winCashProgressBar.visibility = View.VISIBLE
        try {
            val params: MutableMap<String, String> = HashMap()
            params["display_name"] = username
            Log.d("params", params.toString())
            val apiRequest =
                APIRequest(Url.UPDATE_PROFILE, Request.Method.POST, params, null, requireContext())
            apiRequest.showLoader = true
            APIManager.request(apiRequest) { response, error, headers, statusCode ->
                winCashProgressBar.visibility = View.GONE
                handleUpdateProfileResponse(response, error)
            }
        } catch (e: Exception) {
        }
    }

    private fun handleUpdateProfileResponse(response: String?, error: java.lang.Exception?) {
        try {
            if (error != null) {
                showError(error.message)
            } else {
                if (response != null) {
                    //Log.e("response", response);
                    val jsonObjectResponse = JSONObject(response)
                    val type =
                        if (jsonObjectResponse.has("type")) jsonObjectResponse.getString("type") else ""
                    if (type.equals("error", ignoreCase = true)) {
                        val message =
                            if (jsonObjectResponse.has("msg")) Utility.getMessage(
                                activity,
                                jsonObjectResponse.get("msg").toString()
                            ) else APIManager.GENERIC_API_ERROR_MESSAGE
                        showError(message)
                    } else if (type.equals("ok", ignoreCase = true)) {
                        val jsonObject =
                            if (jsonObjectResponse.has("msg")) jsonObjectResponse.getJSONObject("msg") else JSONObject()
                        if (jsonObject.length() > 0) {
                            val jsonObjectuser =
                                if (jsonObject.has("user")) jsonObject.getJSONObject("user") else JSONObject()
                            if (jsonObjectuser.length() > 0) {
                                val user = User(jsonObjectuser)
                                LocalStorage.setUserData(user)
                                requireActivity().supportFragmentManager.popBackStack()
                            }
                        }
                    }
                }
            }
        } catch (e: JSONException) {

        }
    }

    private fun showError(error: String?) {
        var errorMessage = error
        if (errorMessage == null) errorMessage = APIManager.GENERIC_API_ERROR_MESSAGE
        AlertUtils.showAlert(
            getString(R.string.label_error),
            errorMessage,
            null,
            requireContext(),
            null
        )
    }

    override fun onResume() {
        super.onResume()
        if (playerWinCash != null && resumePlayer) {
            playerWinCash!!.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
            playerWinCash!!.playWhenReady = true
            playerView.player = playerWinCash
        }
    }

    override fun onPause() {
        super.onPause()
        if (playerWinCash != null) {
            playerWinCash!!.playWhenReady = false
            resumePlayer = true
            playerView.player = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        closePlayer()
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
        if (isVisibleToUser) {
            // showAd()
        }
    }

    fun showBannerAd(id: String) {
        val adType = Utility.getBannerAdType(id, context)
        if (!adType.isEmpty()) {
            if (adType.equals(Constant.ADMOB, ignoreCase = true)) {
                mAdView.loadAd(adRequest)
                mAdView.visibility = View.VISIBLE
                clAdHolder.setVisibility(View.GONE)
            } else if (adType.equals(Constant.CUSTOM, ignoreCase = true)) {
                showCustomAd(id)
                mAdView.visibility = View.GONE
            }
        }
    }

    fun showCustomAd(id: String) {
        clAdHolder.visibility = View.VISIBLE
        val list = LocalStorage.getBannerAdMobList(
            LocalStorage.KEY_BANNER_AD_MOB,
            requireContext()
        )
        if (list != null) {
            for (i in list.indices) {
                if (list[i].id == id) {
                    if (list[i].feedContentObjectData != null) {
                        if (list[i].feedContentObjectData.mediaType.equals(
                                FeedContentData.MEDIA_TYPE_IMAGE,
                                ignoreCase = true
                            )
                        ) {
                            val url = list[i].feedContentObjectData.adFieldsData.attachment
                            Glide.with(requireActivity())
                                .load(url)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(ivCustomAd)
                            ivCustomAd.visibility = View.VISIBLE
                            playerView.visibility = View.GONE
                            APIMethods.addEvent(
                                activity,
                                Constant.VIEW,
                                list[i].feedContentObjectData.postId, Constant.BANNER, id
                            )
                            ivCustomAd.setOnClickListener {
                                setActionCustomAd(
                                    list[i].feedContentObjectData.adFieldsData,
                                    Constant.BANNER,
                                    id
                                )
                            }
                        } else if (list[i].feedContentObjectData.mediaType.equals(
                                FeedContentData.MEDIA_TYPE_VIDEO,
                                ignoreCase = true
                            )
                        ) {
                            playVideoAd(list[i].feedContentObjectData.adFieldsData.attachment)
                            playerView.visibility = View.VISIBLE
                            ivVolumeOff.visibility = View.VISIBLE
                            ivCustomAd.visibility = View.GONE
                            APIMethods.addEvent(
                                activity,
                                Constant.VIEW,
                                list[i].feedContentObjectData.postId, Constant.BANNER, id
                            )
                            playerView.videoSurfaceView!!.setOnClickListener {
                                setActionCustomAd(
                                    list[i].feedContentObjectData.adFieldsData, Constant.BANNER, id
                                )
                            }
                            ivVolumeUp.setOnClickListener {
                                try {
                                    if (playerWinCash != null && playerWinCash!!.isPlaying) {
                                        ivVolumeUp.visibility = View.GONE
                                        ivVolumeOff.visibility = View.VISIBLE
                                        playerWinCash!!.volume = 0.0f
                                    }
                                } catch (e: Exception) {

                                }
                            }
                            ivVolumeOff.setOnClickListener {
                                try {
                                    if (playerWinCash != null && playerWinCash!!.isPlaying) {
                                        ivVolumeOff.visibility = View.GONE
                                        ivVolumeUp.visibility = View.VISIBLE
                                        playerWinCash!!.volume = 1.0f
                                    }
                                } catch (e: Exception) {

                                }
                            }
                        } else if (list[i].feedContentObjectData.mediaType.equals(
                                FeedContentData.MEDIA_TYPE_SLIDER,
                                ignoreCase = true
                            )
                        ) {
                            updateOtherSliders(
                                list[i].feedContentObjectData.arrayListAdFieldsData,
                                id
                            )
                            playerView.visibility = View.GONE
                            ivVolumeOff.visibility = View.GONE
                            ivCustomAd.visibility = View.GONE
                            rvSliderAd!!.visibility = View.VISIBLE
                        }
                    }
                    ivClose.setOnClickListener { closePlayer() }
                }
            }
        }
    }

    private fun setActionCustomAd(
        adFieldsData: AdFieldsData,
        location: String,
        subLocation: String
    ) {
        APIMethods.addEvent(context, Constant.CLICK, adFieldsData.postId, location, subLocation)
        when (adFieldsData.adType) {
            FeedContentData.AD_TYPE_IN_APP ->
                if (adFieldsData.contentType.equals(Constant.PAGES, ignoreCase = true)) {
                    HomeActivity.getInstance().redirectToPage(adFieldsData.pageType)
                } else {
                    when (adFieldsData.contentType) {
                        FeedContentData.CONTENT_TYPE_ESPORTS -> HomeActivity.getInstance()
                            .getUserRegisteredTournamentInfo(adFieldsData.contentId.toString())
                        else -> HomeActivity.getInstance()
                            .getMediaData(
                                adFieldsData.contentId.toString(),
                                adFieldsData.contentType
                            );
                    }
                }

            FeedContentData.AD_TYPE_EXTERNAL -> setActionOnAd(adFieldsData)
        }
    }

    private fun setActionOnAd(adFieldsData: AdFieldsData) {
        when (adFieldsData.urlType) {
            Constant.BROWSER -> {
                val browserIntent =
                    Intent(Intent.ACTION_VIEW, Uri.parse(adFieldsData.externalUrl))
                startActivity(browserIntent)
            }
            Constant.WEBVIEW -> startActivity(
                Intent(context, BrowserActivity::class.java).putExtra(
                    "title",
                    ""
                ).putExtra("posturl", adFieldsData.externalUrl)
            )
        }
    }

    private fun playVideoAd(url: String) {
        playerView.useController = false
        playerView.requestFocus()
        val uri = Uri.parse(url)
        val bandwidthMeter = DefaultBandwidthMeter()
        val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(
            requireContext(), MyApp.getAppContext().packageName, bandwidthMeter
        )
        val mediaSource: MediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(uri));
        playerWinCash = ExoPlayer.Builder(requireContext()).build()
        playerWinCash!!.addListener(object : Player.Listener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    playerWinCash!!.seekTo(0)
                }
            }

            override fun onPlayerError(error: PlaybackException) {}
        })
        playerWinCash!!.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT)
        playerWinCash!!.prepare(mediaSource)
        playerWinCash!!.setPlayWhenReady(true)

        /*
          Volume is set to 0 as per requirement
          Use as per the case
         */playerWinCash!!.setVolume(0.0f)
        playerView.player = playerWinCash

        val vto = playerView.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val retriever = MediaMetadataRetriever()
                if (Build.VERSION.SDK_INT >= 14) {
                    try {
                        retriever.setDataSource(url, java.util.HashMap())
                    } catch (ex: RuntimeException) {
                        // something went wrong with the file, ignore it and continue
                    }
                } else {
                    retriever.setDataSource(url)
                }
                val videoWidth =
                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)!!
                        .toInt()
                val videoHeight =
                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)!!
                        .toInt()
                retriever.release()
                playerView.viewTreeObserver.removeGlobalOnLayoutListener(this)
                val playerViewWidth = playerView.width
                if (playerViewWidth < videoWidth) {
                    val scale = Math.abs(playerViewWidth - videoWidth) / 2
                    val height = videoHeight - scale
                    playerView.layoutParams.height = height
                } else {
                    val scale = (Math.abs(playerViewWidth - videoWidth) * 1.5).toInt()
                    val height = Math.abs(videoHeight + scale)
                    playerView.layoutParams.height = height
                }
            }
        })
    }

    private fun closePlayer() {
        if (playerWinCash != null) {
            playerWinCash!!.setPlayWhenReady(false)
            playerWinCash!!.stop()
            playerWinCash!!.release()
            playerWinCash = null
            playerView.player = null
        }
    }

    /**
     * Initializes slider recycler view
     * Set the slider recyclerview of Custom ads
     */
    private fun setOtherSliderRecyclerView(id: String) {
        adapterSliderAd = KRecyclerViewAdapter(activity, adSliderObjectArrayList,
            { viewGroup, i ->
                val layoutView = LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.row_slider, viewGroup, false)
                adapterSliderAd!!.getItemCount()
                adapterSliderAd!!.getSelectedIndexes()
                CustomAdSliderHolder(
                    layoutView, adSliderObjectArrayList.size, Constant.BANNER, id
                ) {
                    stopSliding()
                }
            }
        ) { kRecyclerViewHolder, o, i ->
            if (o is AdFieldsData) {
                setActionCustomAd(o, Constant.BANNER, id)
            }
        }
        layoutManager = CustomLinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        rvSliderAd!!.setLayoutManager(layoutManager)
        rvSliderAd!!.setNestedScrollingEnabled(false)
        rvSliderAd!!.setOnFlingListener(null)
        rvSliderAd!!.setAdapter(adapterSliderAd)

        // Dot indicator for banner
        val radius = resources.getDimensionPixelSize(R.dimen.dot_radius)
        val dotsHeight = resources.getDimensionPixelSize(R.dimen.dot_height)
        val color = ContextCompat.getColor(requireContext(), R.color.colorAccent)
        val inActiveColor = ContextCompat.getColor(requireContext(), R.color.game_gray)
        if (!flagDecoration) {
            rvSliderAd!!.addItemDecoration(
                DotsIndicatorDecoration(
                    radius,
                    radius * 2,
                    dotsHeight,
                    inActiveColor,
                    color
                )
            )
            flagDecoration = true
        }
        PagerSnapHelper().attachToRecyclerView(rvSliderAd)
        rvSliderAd!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            @SuppressLint("SetTextI18n")
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                shouldSlide = newState == RecyclerView.SCROLL_STATE_IDLE
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    sliderPosition = layoutManager!!.findFirstCompletelyVisibleItemPosition()
                    startSliding()
                } else {
                    stopSliding()
                }
            }
        })
    }

    private var sliderHandler: Handler? = null
    private var sliderRunnable: Runnable? = null
    private val secondsToWait: Long = 4000

    private fun startSliding() {
        if (sliderHandler == null) {
            sliderHandler = Handler()
        }
        if (sliderRunnable == null) {
            sliderRunnable = Runnable { changeSliderPage() }
        }
        sliderHandler!!.postDelayed(sliderRunnable!!, secondsToWait)
    }

    private fun stopSliding() {
        if (sliderHandler != null && sliderRunnable != null) {
            sliderHandler!!.removeCallbacks(sliderRunnable!!)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun changeSliderPage() {
        if (adSliderObjectArrayList.size <= 1) return
        try {
            val layoutManager = rvSliderAd!!.getLayoutManager() as LinearLayoutManager
            if (layoutManager != null) {
                visibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition()
                if (visibleItemPosition > -1 && visibleItemPosition < adSliderObjectArrayList.size) {
                    if (visibleItemPosition == adSliderObjectArrayList.size - 1) {
                        // Scroll to first item
                        rvSliderAd!!.smoothScrollToPosition(0)
                    } else {
                        // Scroll to next item
                        rvSliderAd!!.smoothScrollToPosition(visibleItemPosition + 1)
                    }
                }
            }
        } catch (ignored: java.lang.Exception) {
        }
    }

    private fun updateOtherSliders(adFieldsDataArrayList: ArrayList<AdFieldsData>, id: String) {
        setOtherSliderRecyclerView(id)
        flagAd = true
        flagDecoration = false
        adSliderObjectArrayList.clear()
        Thread {
            for (adFieldsData in adFieldsDataArrayList) {
                sliderIdAd = adFieldsData.postId.toString()
                adSliderObjectArrayList.addAll(adFieldsDataArrayList)
                break
            }
            Handler(Looper.getMainLooper()).post {
                if (adapterSliderAd != null) adapterSliderAd!!.notifyDataSetChanged()
                startSliding()
            }
        }.start()
    }
}
