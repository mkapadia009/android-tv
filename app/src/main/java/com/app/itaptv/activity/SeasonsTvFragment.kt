package com.app.itaptv.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.app.itaptv.API.APIManager
import com.app.itaptv.API.APIRequest
import com.app.itaptv.API.Url
import com.app.itaptv.R
import com.app.itaptv.custom_interface.NavigationMenuCallback
import com.app.itaptv.fragment.EpisodePlaylistTabFragment
import com.app.itaptv.holder.SeasonHolder
import com.app.itaptv.interfaces.FragmentChangeListener
import com.app.itaptv.interfaces.NavigationStateListener
import com.app.itaptv.structure.FeedContentData
import com.app.itaptv.structure.SeasonData
import com.app.itaptv.utils.Utility
import com.kalpesh.krecycleradapter.KRecyclerViewAdapter
import kotlinx.android.synthetic.main.activity_seasons_tv.*
import kotlinx.android.synthetic.main.custom_notification.view.*
import org.json.JSONException
import org.json.JSONObject

class SeasonsTvFragment : Fragment(), NavigationStateListener, FragmentChangeListener,
    NavigationMenuCallback {

    companion object {
        var POST_TYPE = "postType"
        var SERIES_ID = "seriesId"
        var SEASON_ID = "seasonId"
        var EPISODE_ID = "episodeId"
        var PLAYLIST_ID = "playlistId"
        var ISWATCHED = "iswatched"
        var FROMSEASONTAB = "seasontab"
        val viewsList = ArrayList<View>()
        var lastSelectedSeason = 0
        var lastSelectedSeasonAdapter = 0

        fun getInstance(
            contextType: String?,
            postType: String?,
            seriesId: String?,
            arrayListSeasonData: ArrayList<SeasonData>,
            isWatched: String?
        ): Fragment? {
            val bundle = Bundle()
            bundle.putString(ViewMoreActivity.CONTEXT_TYPE, contextType)
            bundle.putString(EpisodePlaylistTabFragment.POST_TYPE, postType)
            bundle.putString(EpisodePlaylistTabFragment.SERIES_ID, seriesId)
            bundle.putParcelableArrayList(EpisodePlaylistTabFragment.SEASON_ID, arrayListSeasonData)
            bundle.putString(EpisodePlaylistTabFragment.ISWATCHED, isWatched)
            val seasonsTvFragment = SeasonsTvFragment()
            seasonsTvFragment.arguments = bundle
            return seasonsTvFragment
        }

        fun getInstance(
            contextType: String?,
            postType: String?,
            playlistId: String?,
            isWatched: String?
        ): Fragment? {
            val bundle = Bundle()
            bundle.putString(ViewMoreActivity.CONTEXT_TYPE, contextType)
            bundle.putString(EpisodePlaylistTabFragment.POST_TYPE, postType)
            bundle.putString(EpisodePlaylistTabFragment.PLAYLIST_ID, playlistId)
            bundle.putString(EpisodePlaylistTabFragment.ISWATCHED, isWatched)
            val seasonsTvFragment = SeasonsTvFragment()
            seasonsTvFragment.arguments = bundle
            return seasonsTvFragment
        }
    }

    var rootView: View? = null
    var rvListSeason: RecyclerView? = null
    var container: FrameLayout? = null
    var contextType = ""
    var postType = ""
    var seriesId = ""
    var arrayListSeasonData = ArrayList<SeasonData>()
    var playlistId = ""
    var isWatched = "false"
    var screenType: String? = null
    var isSeasonFocused = false
    lateinit var adapter: KRecyclerViewAdapter
    lateinit var tabFragment: EpisodePlaylistTabFragment

    lateinit var episodePlaylistTabFragment: EpisodePlaylistTabFragment
    var alertDialog: AlertDialog? = null
    var navigationStateListener: NavigationStateListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.activity_seasons_tv, container, false)
        init()
        return rootView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            contextType = requireArguments().getString(ViewMoreActivity.CONTEXT_TYPE)!!
            postType = requireArguments().getString(EpisodePlaylistTabFragment.POST_TYPE)!!
            when (postType) {
                FeedContentData.POST_TYPE_ORIGINALS, FeedContentData.POST_TYPE_SEASON -> {
                    seriesId = requireArguments().getString(EpisodePlaylistTabFragment.SERIES_ID)!!
                    arrayListSeasonData =
                        requireArguments().getParcelableArrayList<SeasonData>(
                            EpisodePlaylistTabFragment.SEASON_ID
                        )!!
                    isWatched = requireArguments().getString(EpisodePlaylistTabFragment.ISWATCHED)!!
                }

                FeedContentData.POST_TYPE_PLAYLIST -> {
                    playlistId =
                        requireArguments().getString(EpisodePlaylistTabFragment.PLAYLIST_ID)!!
                    isWatched = requireArguments().getString(EpisodePlaylistTabFragment.ISWATCHED)!!
                }
            }
        }
    }

    private fun init() {
        rvListSeason = rootView!!.findViewById(R.id.rvListSeason)
        container = rootView!!.findViewById(R.id.container)
        lastSelectedSeason = 0
        lastSelectedSeasonAdapter=0
        setSeasonData()
        viewsList.clear()
    }

    private fun setSeasonData() {
        openEpisodesList(arrayListSeasonData[lastSelectedSeason])
        rvListSeason!!.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        setRecyclerView()
    }

    private fun setRecyclerView() {
        adapter = KRecyclerViewAdapter(context, arrayListSeasonData,
            { viewGroup, i ->
                val layoutView = LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.custom_season_tv_tab, viewGroup, false)
                SeasonHolder(layoutView, SeasonHolder.KeyCallback {
                    navMenuToggle(false)
                })
            }
        ) { _, o, i ->
            if (o is SeasonData) {
                viewsList[lastSelectedSeason].requestFocus()
                lastSelectedSeason = i
                lastSelectedSeasonAdapter= lastSelectedSeason
                openEpisodesList(o)
                viewsList[i].requestFocus()
            }
        }
        rvListSeason!!.adapter = adapter
        navMenuToggle(true)
    }

    private fun openFragment(fragment: Fragment) {
        val fragmentTransaction: FragmentTransaction =
            childFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.container, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }


    @SuppressLint("NotifyDataSetChanged")
    fun restoreFocus() {
        (context as Activity).runOnUiThread {
            try {
                if (isSeasonFocused) {
                    container!!.clearFocus();
                    container!!.descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS;
                    rvListSeason!!.requestFocus();
                    rvListSeason!!.descendantFocusability = ViewGroup.FOCUS_AFTER_DESCENDANTS;
                    viewsList[lastSelectedSeason].requestFocus();
                    lastSelectedSeasonAdapter= lastSelectedSeason
                } else {
                    rvListSeason!!.clearFocus();
                    rvListSeason!!.descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS;
                    container!!.requestFocus();
                    container!!.descendantFocusability = ViewGroup.FOCUS_AFTER_DESCENDANTS;
                    navigationStateListener!!.onStateChanged(true, "")
                }
            } catch (e: Exception) {
                e.printStackTrace();
            }
        }
    }

    override fun navMenuToggle(toShow: Boolean) {
        /* Toast.makeText(
             this,
             "Focused :" + toShow + " " + viewsList.size,
             Toast.LENGTH_SHORT
         ).show()*/
        try {
            if (toShow) {
                container!!.clearFocus();
                container!!.descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS;
                rvListSeason!!.requestFocus();
                rvListSeason!!.descendantFocusability = ViewGroup.FOCUS_AFTER_DESCENDANTS;
                viewsList[lastSelectedSeason].requestFocus()
                lastSelectedSeasonAdapter= lastSelectedSeason
                isSeasonFocused = true
            } else {
                rvListSeason!!.clearFocus();
                rvListSeason!!.descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS;
                container!!.requestFocus();
                container!!.descendantFocusability = ViewGroup.FOCUS_AFTER_DESCENDANTS;
                navigationStateListener!!.onStateChanged(true, "")
                isSeasonFocused = false
            }
        } catch (e: Exception) {
            e.printStackTrace();
        }
    }

    override fun switchFragment(fragmentName: String?) {
        TODO("Not yet implemented")
    }

    override fun onStateChanged(expanded: Boolean, lastSelected: String?) {
        if (!expanded) {
            rvListSeason!!.clearFocus()
            isSeasonFocused = false
        } else {
            //do
        }
    }

    private fun openEpisodesList(seasonData: SeasonData) {
        val tabFragment = EpisodePlaylistTabFragment.getInstance(
            "",
            postType,
            seriesId,
            seasonData.termId,
            0,
            isWatched
        )
        episodePlaylistTabFragment = tabFragment as EpisodePlaylistTabFragment
        navigationStateListener = episodePlaylistTabFragment
        openFragment(tabFragment)
        tabFragment.setNavigationMenuCallback(this)
    }

    /* override fun onBackPressed() {
         super.onBackPressed()
         finish()
     }*/

    fun coinspayPrepayment(productId: String, pricing_option: Int, screenType1: String) {
        try {
            screenType = screenType1
            val params: MutableMap<String, String> = HashMap()
            params["ID"] = productId
            params["payment_mode"] = "icoins"
            params["pricing_option"] = pricing_option.toString()
            val apiRequest =
                APIRequest(Url.INITIATE_ORDER, Request.Method.POST, params, null, context)
            apiRequest.showLoader = true
            APIManager.request(
                apiRequest
            ) { response, error, headers, statusCode ->
                if (error == null) {
                    handleCoinsPayPrepaymentResponse(response, error, productId)
                } else {
                    Utility.showError(error.toString(), context)
                }
            }
        } catch (e: java.lang.Exception) {
            Utility.showError(
                getString(R.string.failed_to_initiate_payment),
                context
            )
        }
    }

    private fun handleCoinsPayPrepaymentResponse(
        response: String?,
        error: java.lang.Exception?,
        productId: String
    ) {
        try {
            if (error != null) {
                Utility.showError(error.message, context)
            } else {
                if (response != null) {
                    val jsonObjectResponse = JSONObject(response)
                    val type =
                        if (jsonObjectResponse.has("type")) jsonObjectResponse.getString("type") else ""
                    if (type.equals("error", ignoreCase = true)) {
                        val message =
                            if (jsonObjectResponse.has("msg")) jsonObjectResponse.getString("msg") else APIManager.GENERIC_API_ERROR_MESSAGE
                        //Utility.showError(message,this);
                        if (message.contains("purchased")) {
                            showAlreadyPurchasedDialog(message)
                        } else {
                            showInsufficientCoinsDialog(message)
                        }
                    } else if (type.equals("ok", ignoreCase = true)) {
                        /// new flow checking to be done here
                        val `object` = jsonObjectResponse.getJSONObject("msg")
                        val status =
                            if (`object`.has("status")) `object`.getString("status") else ""
                        if (status.equals("SUCCESS", ignoreCase = true)) {
                            //flow for coin  only
                            if (screenType.equals(
                                    EpisodePlaylistTabFragment.FROMSEASONTAB,
                                    ignoreCase = true
                                )
                            ) {
                                showBuySuccessDialog()
                            }
                        }
                    }
                }
            }
        } catch (e: JSONException) {
            Utility.showError(e.toString(), context)
        }
    }

    fun showBuySuccessDialog() {
        val builder = context?.let {
            AlertDialog.Builder(it)
                .setView(R.layout.dialog_custom)
        }
        alertDialog = builder!!.create()
        alertDialog!!.setCancelable(true)
        alertDialog!!.show()
        val tvTitle: TextView? = alertDialog!!.findViewById<TextView>(R.id.tvStoreName)
        val btNeutral: TextView? = alertDialog!!.findViewById<TextView>(R.id.btNeutral)
        tvTitle!!.setText(R.string.msg_purchase_success)
        btNeutral!!.setText(R.string.ok)
        btNeutral!!.visibility = View.VISIBLE
        btNeutral.setOnClickListener {
            alertDialog!!.dismiss()
            refreshTabScreen()
        }
    }

    private fun showAlreadyPurchasedDialog(message: String) {
        val builder = context?.let {
            AlertDialog.Builder(it)
                .setView(R.layout.dialog_custom)
        }
        alertDialog = builder!!.create()
        alertDialog!!.setCancelable(true)
        alertDialog!!.show()
        val tvTitle: TextView? = alertDialog!!.findViewById<TextView>(R.id.tvStoreName)
        val btPositive: TextView? = alertDialog!!.findViewById<TextView>(R.id.btPositive)
        tvTitle!!.text = message
        btPositive!!.setText(R.string.ok)
        btPositive!!.visibility = View.VISIBLE
        btPositive!!.setOnClickListener { alertDialog!!.dismiss() }
    }

    private fun showInsufficientCoinsDialog(message: String) {
        val builder = context?.let {
            AlertDialog.Builder(it)
                .setView(R.layout.dialog_custom)
        }
        alertDialog = builder!!.create()
        alertDialog!!.setCancelable(true)
        alertDialog!!.show()
        val tvTitle: TextView? = alertDialog!!.findViewById<TextView>(R.id.tvStoreName)
        val btPositive: TextView? = alertDialog!!.findViewById<TextView>(R.id.btPositive)
        tvTitle!!.text = message
        btPositive!!.setText(R.string.ok)
        btPositive!!.visibility = View.VISIBLE
        btPositive!!.setOnClickListener { alertDialog!!.dismiss() }
    }

    fun refreshTabScreen() {
        //episodePlaylistTabFragment.refreshTab()
        val manager: FragmentManager = childFragmentManager
        val trans: FragmentTransaction = manager.beginTransaction()
        trans.remove(episodePlaylistTabFragment)
        trans.commit()
        manager.popBackStack()
        openEpisodesList(arrayListSeasonData[lastSelectedSeason])
        navMenuToggle(true)
    }
}