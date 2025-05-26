package com.app.itaptv.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.app.itaptv.API.APIManager
import com.app.itaptv.API.APIRequest
import com.app.itaptv.API.Url
import com.app.itaptv.MyApp
import com.app.itaptv.R
import com.app.itaptv.activity.DownloadActivity
import com.app.itaptv.activity.HomeActivity
import com.app.itaptv.activity.LearnMoreActivity
import com.app.itaptv.offline.DownloadTracker
import com.app.itaptv.structure.SubscriptionDetails
import com.app.itaptv.structure.User
import com.app.itaptv.utils.*
import com.app.itaptv.utils.LocalStorage.STATUS_ACTIVE
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.exoplayer2.offline.Download
import com.google.android.exoplayer2.util.Util
import com.kalpesh.krecycleradapter.Interface.KRecyclerItemClickListener
import com.kalpesh.krecycleradapter.Interface.KRecyclerViewHolderCallBack
import com.kalpesh.krecycleradapter.KRecyclerViewAdapter
import com.kalpesh.krecycleradapter.KRecyclerViewHolder
import kotlinx.android.synthetic.main.activity_subscriptions.*
import kotlinx.android.synthetic.main.row_downloaded_content.view.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class DownloadsFrag : Fragment(), DownloadTracker.Listener,
    DownloadOptionsBottomSheetFrag.ItemClickListener {

    private val downloads = ArrayList<Download>()
    private lateinit var downloadActivity: DownloadActivity
    private lateinit var user: User
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyState: EmptyStateManager
    private lateinit var downloadsRecycler: RecyclerView

    private lateinit var tvMsg: TextView
    private lateinit var tvMsg2: TextView
    private lateinit var ivEmpt: ImageView
    private lateinit var subAmount: TextView
    private lateinit var subsParent: ConstraintLayout
    private lateinit var pointersConstraint: ConstraintLayout
    private lateinit var offersIv: ImageView
    public var isFromBrowserFlag: Boolean = false

    private var learnMoreContent: String? = null

    private lateinit var rootView: View

    companion object {
        @JvmStatic
        fun newInstance(): DownloadsFrag? {
            return DownloadsFrag()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_downloads, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {

        progressBar = rootView.findViewById(R.id.progressBar)
        downloadsRecycler = rootView.findViewById(R.id.downloadsRecyclerView)
        tvMsg = rootView.findViewById(R.id.tvMsg)
        tvMsg2 = rootView.findViewById(R.id.tvMsg2)
        ivEmpt = rootView.findViewById(R.id.ivIcon)
        subAmount = rootView.findViewById(R.id.perMonthTv)
        subAmount = rootView.findViewById(R.id.perMonthTv)
        subsParent = rootView.findViewById(R.id.subscriptionsParent)
        pointersConstraint = rootView.findViewById(R.id.pointersConstraint)
        offersIv = rootView.findViewById(R.id.offersIv)

        progressBar.visibility = View.VISIBLE
        user = LocalStorage.getUserData()
        downloadActivity = activity as DownloadActivity
        setUpEmptyState()
        emptyState.hide()


        if (!LocalStorage.isUserPremium()) {
            showUnsubscribedState(true)
        } else {
            updateEmptyState(null)
            showUnsubscribedState(false)
            displayDownloads()
            progressBar.visibility = View.GONE
        }

        buyNowBtn.setOnClickListener {
            /*if (user.mobile == null || user.mobile == "") {
                startActivity(Intent(activity, VerificationActivity::class.java).putExtra(VerificationActivity.TYPE_KEY, Constant.KEY_MOBILE))
            } else if (user.userEmail == null || user.userEmail == "") {
                startActivity(Intent(activity, VerificationActivity::class.java).putExtra(VerificationActivity.TYPE_KEY, Constant.KEY_EMAIL))
            } else {
                startSubscriptionFlow()
            }*/

            startSubscriptionFlow()

            /*val jsonObj = JSONObject()
            val jsonAdd = JSONObject()

            jsonAdd.put("status", "active")
            jsonAdd.put("start_date", "12 - march")
            jsonAdd.put("expiry_date", "13 - march")
            jsonAdd.put("allow_rental", "Yes")

            jsonObj.put("subscription_details", jsonAdd);

            val subscription = SubscriptionDetails(if (jsonObj.has("subscription_details")) jsonObj.getJSONObject("subscription_details") else JSONObject())
            LocalStorage.setUserSubscriptionDetails(subscription)
            AlertUtils.showToast("Rentals Allowed", 3, requireContext())*/
        }

        learnMoreBtn.setOnClickListener {
            startActivity(
                Intent(activity, LearnMoreActivity::class.java).putExtra(
                    "fromDownloads",
                    true
                ).putExtra("learnMoreContent", learnMoreContent)
            )
        }
    }

    override fun onStart() {
        super.onStart()
        MyApp.getInstance().downloadTracker.addListener(this)
    }

    override fun onStop() {
        MyApp.getInstance().downloadTracker.removeListener(this)
        super.onStop()
    }

    override fun onDownloadsChanged() {
        displayDownloads()
    }

    private fun displayDownloads() {
        downloads.clear()
        val app = MyApp.getInstance()
        val cursor = app.downloadManager.downloadIndex.getDownloads()
        for (i in 0 until cursor.count) {
            if (cursor.moveToPosition(i)) {
                downloads.add(cursor.download)
            }
        }

        Log.i("Downloads", "Total downloads: $downloads")

        val adapter =
            KRecyclerViewAdapter(activity, downloads, KRecyclerViewHolderCallBack { p0, p1 ->
                val view = LayoutInflater.from(p0.context)
                    .inflate(R.layout.row_downloaded_content, p0, false)
                DownloadsItemHolder(
                    view,
                    object : DownloadsItemHolder.DownloadedItemHolderListener {
                        override fun removeDownload(download: Download) {
                            val name = Util.fromUtf8Bytes(download.request.data)
                            val message =
                                String.format(getString(R.string.sure_to_remove,name))
                            AlertUtils.showAlert(
                                getString(R.string.confirm_delete),
                                message,
                                getString(R.string.delete),
                                getString(R.string.cancel),
                                requireContext()
                            ) { isPositiveAction ->
                                if (isPositiveAction) {
                                    ExoUtil.delete(download.request.id)
                                    Handler().postDelayed({
                                        displayDownloads()
                                    }, 300)
                                }
                            }
                        }
                    })
            }, KRecyclerItemClickListener { p0, p1, p2 ->
                if (p1 is Download && p1.state == Download.STATE_COMPLETED) {
                    try {
                        val obj = JSONObject(Util.fromUtf8Bytes(downloads[p2].request.data))
                        val name = obj.getString("title")
                        val img = obj.getString("thumb")
                        openBottomSheetDialog(downloads[p2].request.id, name, img)
                    } catch (t: Throwable) {
                        Log.e(
                            "My App",
                            "Could not parse malformed JSON: \"" + Util.fromUtf8Bytes(downloads[p2].request.data) + "\""
                        )
                    }
                }
            })

        downloadsRecycler.adapter = adapter
        val mLayoutManager = LinearLayoutManager(activity)
        mLayoutManager.reverseLayout = true
        mLayoutManager.stackFromEnd = true
        downloadsRecycler.layoutManager = mLayoutManager
        updateEmptyState(null)
    }

    private fun openBottomSheetDialog(id: String, title: String, imageUrl: String) {
        val bundle = Bundle()
        val addBottomFrag = DownloadOptionsBottomSheetFrag()
        bundle.putString(MEDIA_ID, id)
        bundle.putString(MEDIA_TITLE, title)
        bundle.putString(MEDIA_IMAGE, imageUrl)
        addBottomFrag.arguments = bundle
        addBottomFrag.show(
            requireActivity().supportFragmentManager,
            DownloadOptionsBottomSheetFrag.TAG
        )
    }

    /**
     * Initialization of Empty State
     */
    fun setUpEmptyState() {
        emptyState =
            EmptyStateManager.setUpInFragment(rootView, activity as AppCompatActivity?) { action ->
                if (action == EmptyStateManager.ACTION_RETRY) {
                    displayDownloads()
                }
            }
        emptyState.hide()
    }

    /**
     * Show/Hide Unsubscribed Layout
     */
    private fun showUnsubscribedState(show: Boolean) {
        if (show) {
            subsParent.visibility = View.VISIBLE
            subAmount.text = LocalStorage.getMinSubAmount()
            setClickableTexts()
            if (LocalStorage.getSubsOfferImage().equals(
                    Constant.TEXT_FALSE,
                    ignoreCase = true
                ) || LocalStorage.getSubsOfferImage() == null || LocalStorage.getSubsOfferImage() == "null"
            ) {
                pointersConstraint.visibility = View.VISIBLE
                offersIv.visibility = View.GONE
            } else {
                pointersConstraint.visibility = View.GONE
                offersIv.visibility = View.VISIBLE
                Glide.with(requireActivity())
                    .load(LocalStorage.getSubsOfferImage())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(offersIv)
            }
        } else {
            displayDownloads()
            subsParent.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        checkUserSubscription()
    }

    private fun startSubscriptionFlow() {
        progressBar.visibility = View.VISIBLE
        val userId = LocalStorage.getUserId()
        try {
            val params: MutableMap<String, String> = HashMap()
            params["user_id"] = userId
            val apiRequest =
                APIRequest(Url.FETCH_SUBSCRIPTIONS_URL, Request.Method.POST, params, null, context)
            apiRequest.showLoader = false
            APIManager.request(apiRequest) { response, error, _, _ ->
                progressBar.visibility = View.GONE
                try {
                    if (error != null) {
                        //AlertUtils.showToast(error.message, 1, context)
                    } else {
                        if (response != null) {
                            val jsonObjectResponse = JSONObject(response)
                            val type =
                                if (jsonObjectResponse.has("type")) jsonObjectResponse.getString("type") else ""
                            if (type.equals("error", ignoreCase = true)) {
                                val message =
                                    if (jsonObjectResponse.has("msg")) jsonObjectResponse.getString(
                                        "msg"
                                    ) else APIManager.GENERIC_API_ERROR_MESSAGE
                                //AlertUtils.showToast(message, 1, context)
                            } else if (type.equals("ok", ignoreCase = true)) {
                                val jsonObject =
                                    if (jsonObjectResponse.has("msg")) jsonObjectResponse.getJSONObject(
                                        "msg"
                                    ) else JSONObject()
                                val subscriptionsUrl = jsonObject.getString("url")
                                if (subscriptionsUrl.startsWith("http")) {
                                    val browserIntent =
                                        Intent(Intent.ACTION_VIEW, Uri.parse(subscriptionsUrl))
                                    startActivity(browserIntent)
                                    isFromBrowserFlag = true
                                }
                            }
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkUserSubscription() {
        progressBar.visibility = View.VISIBLE
        val userId = LocalStorage.getUserId()
        try {
            val params: MutableMap<String, String> = HashMap()
            params["user_id"] = userId
            val apiRequest = APIRequest(
                Url.CHECK_ACTIVE_SUBSCRIPTION,
                Request.Method.POST,
                params,
                null,
                context
            )
            apiRequest.showLoader = false
            APIManager.request(apiRequest) { response, error, _, _ ->
                progressBar.visibility = View.INVISIBLE
                try {
                    if (error != null) {
                        //AlertUtils.showToast(error.message, 1, context)
                    } else {
                        if (response != null) {
                            val jsonObjectResponse = JSONObject(response)
                            val type =
                                if (jsonObjectResponse.has("type")) jsonObjectResponse.getString("type") else ""
                            if (type.equals("error", ignoreCase = true)) {
                                LocalStorage.setUserPremium(false)
                                showUnsubscribedState(true)
                                learnMoreContent =
                                    if (jsonObjectResponse.has("msg")) jsonObjectResponse.getString(
                                        "msg"
                                    ) else null
                            } else if (type.equals("ok", ignoreCase = true)) {
                                val jsonObject =
                                    if (jsonObjectResponse.has("msg")) jsonObjectResponse.getJSONObject(
                                        "msg"
                                    ) else JSONObject()
                                if (jsonObject.getString("status") == STATUS_ACTIVE) {
                                    LocalStorage.setUserPremium(true)
                                    LocalStorage.setSubStartDate(jsonObject.getString("start_date"))
                                    LocalStorage.setSubEndDate(jsonObject.getString("expiry_date"))
                                    showUnsubscribedState(false)
                                    if (isFromBrowserFlag) {
                                        AlertUtils.showAlert(
                                            getString(R.string.congratulations),
                                            getString(R.string.you_have_subscribed_successfully),
                                            null,
                                            activity,
                                            null
                                        )
                                        getUserDetails()
                                    }
                                } else {
                                    LocalStorage.setUserPremium(false)
                                    showUnsubscribedState(true)
                                }
                            }
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Update of Empty State
     */
    private fun updateEmptyState(error: String?) {
        if (error == null) {
            if (downloads.isEmpty()) {
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 30, 0, 0)
                emptyState.setImgAndMsg(
                    activity?.resources?.getString(R.string.downloads_primary_text_empty_state),
                    R.drawable.ic_downloads_empty
                )
                val tf = ResourcesCompat.getFont(MyApp.getAppContext(), R.font.rifficfree_bold)

                tvMsg.typeface = tf
                tvMsg.textSize = 24f
                tvMsg.layoutParams = params

                tvMsg.setTextColor(ContextCompat.getColor(rootView.context, R.color.colorAccent))
                emptyState.setSecondaryMsg(activity?.resources?.getString(R.string.downloads_secondary_text_empty_state))
                ivEmpt.setImageDrawable(
                    ContextCompat.getDrawable(
                        rootView.context,
                        R.drawable.ic_downloads_empty
                    )
                )
                val tf1 = ResourcesCompat.getFont(rootView.context, R.font.rubik_regular)
                tvMsg2.typeface = tf1
                tvMsg2.textSize = 16f
                tvMsg2.layoutParams = params
                emptyState.actionButton = EmptyStateManager.GO_TO_FEEDS_LISTING
                emptyState = EmptyStateManager.setUpInFragment(
                    rootView,
                    activity as AppCompatActivity?
                ) { action: String? -> goToWatchSection() }
            } else {
                emptyState.hide()
            }
        } else {
            if (Utility.isConnectingToInternet(context)) {
                emptyState.message = APIManager.GENERIC_API_ERROR_MESSAGE
            } else {
                emptyState.showNoConnectionState()
            }
        }
    }

    private fun goToWatchSection() {
        //HomeActivity.showHome(Constant.TAB_WATCH)
        val intent = Intent(activity, HomeActivity::class.java)
        //startActivity(intent)
        intent.action = Constant.DOWNLOADS
        requireActivity().startActivityForResult(intent,879)
        requireActivity().finish()
    }

    override fun sheetCallback(item: String?) {
        when (item) {
            RELOAD_DOWNLOADS_LIST -> {
                displayDownloads()
            }
        }
    }

    private fun setClickableTexts() {
        val activity: Activity? = activity
        if (activity != null) {
            val spannableString =
                SpannableString(activity.resources.getString(R.string.subject_to_tc))
            spannableString.setSpan(
                CustomClickableSpan(activity, 0),
                12,
                30,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spannableString.setSpan(
                CustomClickableSpan(activity, 1),
                33,
                47,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            val tvLabelTermsNPolicyLine1 =
                rootView.findViewById<TextView>(R.id.tvLabelTermsNPolicyLine1)
            tvLabelTermsNPolicyLine1?.text = spannableString
            tvLabelTermsNPolicyLine1?.movementMethod = LinkMovementMethod.getInstance()
            tvLabelTermsNPolicyLine1?.highlightColor = Color.TRANSPARENT
        }
    }

    private fun getUserDetails() {
        val userId = LocalStorage.getUserId()
        try {
            val params: MutableMap<String, String> = HashMap()
            params["user_id"] = userId
            val apiRequest = APIRequest(
                Url.GET_USER_DETAILS,
                Request.Method.POST,
                params,
                null,
                requireContext()
            )
            apiRequest.showLoader = false
            APIManager.request(apiRequest) { response, error, headers, statusCode ->
                try {
                    if (error != null) {
                        //AlertUtils.showToast(error.getMessage(), 1, context);
                    } else {
                        if (response != null) {
                            val jsonObjectResponse = JSONObject(response)
                            val type =
                                if (jsonObjectResponse.has("type")) jsonObjectResponse.getString("type") else ""
                            if (type.equals("error", ignoreCase = true)) {
                                val message =
                                    if (jsonObjectResponse.has("msg")) jsonObjectResponse.getString(
                                        "msg"
                                    ) else APIManager.GENERIC_API_ERROR_MESSAGE
                                //showError(message);
                            } else if (type.equals("ok", ignoreCase = true)) {
                                val jsonObject =
                                    if (jsonObjectResponse.has("msg")) jsonObjectResponse.getJSONObject(
                                        "msg"
                                    ) else JSONObject()
                                val user = User(
                                    if (jsonObject.has("user_details")) jsonObject.getJSONObject("user_details") else JSONObject()
                                )
                                LocalStorage.setUserData(user)
                                val subscription = SubscriptionDetails(
                                    if (jsonObject.has("subscription_details")) jsonObject.getJSONObject(
                                        "subscription_details"
                                    ) else JSONObject()
                                )
                                LocalStorage.setUserSubscriptionDetails(subscription)
                            }
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
}

class DownloadsItemHolder(
    private var view: View,
    private var listener: DownloadedItemHolderListener
) : KRecyclerViewHolder(view) {

    interface DownloadedItemHolderListener {
        fun removeDownload(download: Download)
    }

    @SuppressLint("SetTextI18n")
    override fun setData(context: Context, itemObject: Any) {
        super.setData(context, itemObject)
        if (itemObject is Download) {
            try {
                val obj = JSONObject(Util.fromUtf8Bytes(itemObject.request.data))
                view.tvTitle.text = obj.getString("title")
                if (itemObject.state == Download.STATE_COMPLETED) {
                    view.tvSubtitle.text = obj.getString("duration")
                } else {
                    setStatus(itemObject)
                }
                Glide.with(context)
                    .load(obj.getString("thumb"))
                    .override(100, 100)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(view.ivContentImage)
            } catch (t: Throwable) {
                Log.e(
                    "My App",
                    "Could not parse malformed JSON: \"" + Util.fromUtf8Bytes(itemObject.request.data) + "\""
                )
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setStatus(download: Download) {
        view.tvSubtitle.text = when (download.state) {
            Download.STATE_COMPLETED -> {
                "Downloaded"
            }
            Download.STATE_DOWNLOADING -> {
                "Downloading..."
            }
            Download.STATE_FAILED -> {
                "Download failed!"
            }
            Download.STATE_QUEUED -> {
                "Download queued"
            }
            Download.STATE_REMOVING -> {
                "Removing download..."
            }
            Download.STATE_RESTARTING -> {
                "Restarting download..."
            }
            Download.STATE_STOPPED -> {
                "Download stopped."
            }
            else -> {
                "NA"
            }
        }
    }
}
