package com.app.itaptv.fragment

import android.os.Bundle
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.app.itaptv.API.APIManager
import com.app.itaptv.API.APIRequest
import com.app.itaptv.API.Url
import com.app.itaptv.R
import com.app.itaptv.activity.HomeActivity
import com.app.itaptv.font_awesome.FontAwesome
import com.app.itaptv.holder.LeaderHolder
import com.app.itaptv.structure.LeaderBoardData
import com.app.itaptv.utils.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.kalpesh.krecycleradapter.Interface.KRecyclerItemClickListener
import com.kalpesh.krecycleradapter.Interface.KRecyclerViewHolderCallBack
import com.kalpesh.krecycleradapter.KRecyclerViewAdapter
import com.kalpesh.krecycleradapter.KRecyclerViewHolder
import kotlinx.android.synthetic.main.progress_bar.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class LeaderboardFrag : Fragment() {

    private lateinit var rootView: View

    private val winnersList = ArrayList<LeaderBoardData>()

    //  private RecyclerView topWinnersRecyclerView;
    private var winnersRecyclerView: RecyclerView? = null
    var collapsingToolbarLayout: CollapsingToolbarLayout? = null
    var llLeaderTop: LinearLayout? = null
    var emptyState: EmptyStateManager? = null
    var leaderData = ArrayList<LeaderBoardData>()
    var UserPosition = -1
    var UserId: String? = null
    var tvUserNo: TextView? = null
    var tvUserLeaderCoin: TextView? = null
    var tvYouUserNo: TextView? = null
    var ivUser: ImageView? = null

    var homeActivity: HomeActivity? = null

    companion object {
        @JvmStatic
        fun newInstance(): LeaderboardFrag {
            return LeaderboardFrag()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_leaderboard, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {

        winnersRecyclerView = rootView.findViewById(R.id.rvLeaderList)
        collapsingToolbarLayout = rootView.findViewById(R.id.collapsetoolbar)
        llLeaderTop = rootView.findViewById(R.id.llLeaderTop)
        tvUserNo = rootView.findViewById(R.id.tvUserNo)
        tvUserLeaderCoin = rootView.findViewById(R.id.tvUserLeaderCoin)
        tvYouUserNo = rootView.findViewById(R.id.tvYouUserNo)
        ivUser = rootView.findViewById(R.id.ivUser)

        UserId = LocalStorage.getUserId()

        getLeaderboardData()
    }


    /**
     * API Calling the LeaderBoard
     */
    private fun getLeaderboardData() {
        leaderData.clear()
        winnersList.clear()
        setUpEmptyState()
        progressBar.visibility = View.VISIBLE
        winnersRecyclerView!!.visibility = View.INVISIBLE
        val params: Map<String, String> = HashMap()
        val apiRequest = APIRequest(Url.GET_LEADERBOARD, Request.Method.GET, params,
                null, requireContext())
        apiRequest.showLoader = false
        APIManager.request(apiRequest) { response, error, _, _ ->
            progressBar.visibility = View.GONE
            winnersRecyclerView!!.visibility = View.VISIBLE
            handleLeaderResponse(response, error)
        }
    }

    private fun handleLeaderResponse(response: String?, error: Exception?) {
        if (error != null) {
            updateEmptyState(APIManager.GENERIC_API_ERROR_MESSAGE)
        } else if (response != null) {
            try {
                val leaderObject = JSONObject(response)
                val type = if (leaderObject.has("type")) leaderObject.getString("type") else ""
                if (type.equals("error", ignoreCase = true)) {
                    val message = if (leaderObject.has("msg")) leaderObject.getString("msg") else APIManager.GENERIC_API_ERROR_MESSAGE
                    //showError(message)
                    Log.d("Leaderboard Error: ", message)
                    updateEmptyState(null)
                } else if (type.equals("OK", ignoreCase = true)) {
                    val jsonObject = leaderObject.getJSONObject("msg")
                    val jsonArray = jsonObject.getJSONArray("top20")
                    val jsonObject1 = jsonObject.getJSONObject("me")
                    Log.d("LeaderBoard", jsonObject1.toString())
                    for (i in 0 until jsonArray.length()) {
                        val leaderBoardData = LeaderBoardData(jsonArray.getJSONObject(i))
                        leaderData.add(leaderBoardData)
                    }
                    setUpUserUI(jsonObject1)
                    setupLists(leaderData)
                }
            } catch (e: JSONException) {
                updateEmptyState(APIManager.GENERIC_API_ERROR_MESSAGE)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        //leaderData.clear()
        //winnersList.clear()
    }

    /**
     * This method shows error alert
     *
     * @param errorMessage message to be displayed in alert dialog
     */
    private fun showError(errorMessage: String?) {
        var errorMessage = errorMessage
        if (errorMessage == null) errorMessage = APIManager.GENERIC_API_ERROR_MESSAGE
        AlertUtils.showAlert(getString(R.string.label_error), errorMessage, null, requireContext(), null)
    }

    private fun setupLists(data: ArrayList<LeaderBoardData>) {
        if (data.size >= 3) {
            data[0].setIcon(R.drawable.gold_crown)
            data[0].setIndex(1)
            if (UserId.equals(data[0].ID, ignoreCase = true)) {
                UserPosition = 0
            }
            winnersList.add(data[0])
            data[1].setIcon(R.drawable.silver_crown)
            data[1].setIndex(2)
            if (UserId.equals(data[1].ID, ignoreCase = true)) {
                UserPosition = 1
            }
            winnersList.add(data[1])
            data[2].setIcon(R.drawable.bronze_crown)
            data[2].setIndex(3)
            if (UserId.equals(data[2].ID, ignoreCase = true)) {
                UserPosition = 2
            }
            winnersList.add(data[2])
            if (data.size > 3) {
                for (i in 3 until data.size) {
                    data[i].setIndex(i + 1)
                    data[i].setIcon(0)
                    if (UserId.equals(data[i].ID, ignoreCase = true)) {
                        UserPosition = i
                    }
                    winnersList.add(data[i])
                }
            }
        } else if (data.size == 2) {
            data[0].setIcon(R.drawable.gold_crown)
            if (UserId.equals(data[0].ID, ignoreCase = true)) {
                UserPosition = 0
            }
            winnersList.add(data[0])
            data[1].setIcon(R.drawable.silver_crown)
            if (UserId.equals(data[1].ID, ignoreCase = true)) {
                UserPosition = 1
            }
            winnersList.add(data[1])
        } else if (data.size == 1) {
            data[0].setIcon(R.drawable.gold_crown)
            if (UserId.equals(data[0].ID, ignoreCase = true)) {
                UserPosition = 0
            }
            winnersList.add(data[0])
        } else {
            // No items
        }
        setupAdapters()
    }

    /**
     * Initialization of Empty State
     */
    fun setUpEmptyState() {
        emptyState = EmptyStateManager.setUpInFragment(view, activity as AppCompatActivity?) { action ->
            if (action == EmptyStateManager.ACTION_RETRY) {
                getLeaderboardData()
            }
        }
    }

    /**
     * Update of Empty State
     */
    private fun updateEmptyState(error: String?) {
        if (error == null) {
            if (leaderData.isEmpty()) {
                emptyState!!.setImgAndMsg(getString(R.string.no_data_found), FontAwesome.FA_Exclamation)
                //   topWinnersRecyclerView.setVisibility(View.INVISIBLE);
                winnersRecyclerView!!.visibility = View.INVISIBLE
                ivUser!!.visibility = View.INVISIBLE
                tvUserNo!!.visibility = View.INVISIBLE
                tvUserLeaderCoin!!.visibility = View.INVISIBLE
                tvYouUserNo!!.visibility = View.INVISIBLE
            } else {
                //   topWinnersRecyclerView.setVisibility(View.INVISIBLE);
                winnersRecyclerView!!.visibility = View.INVISIBLE
                emptyState!!.hide()
            }
        } else {
            //  topWinnersRecyclerView.setVisibility(View.INVISIBLE);
            winnersRecyclerView!!.visibility = View.INVISIBLE
            ivUser!!.visibility = View.INVISIBLE
            tvUserNo!!.visibility = View.INVISIBLE
            tvUserLeaderCoin!!.visibility = View.INVISIBLE
            tvYouUserNo!!.visibility = View.INVISIBLE
            if (Utility.isConnectingToInternet(requireContext())) {
                emptyState!!.message = APIManager.GENERIC_API_ERROR_MESSAGE
            } else {
                emptyState!!.showNoConnectionState()
            }
        }
    }

    private fun setUpUserUI(jsonObject1: JSONObject) {
        // Set User Position and Image
        //  appBarLayout.setExpanded(true);
        try {
            val s = Utility.ordinal(jsonObject1.getInt("rank"))
            val ss1 = SpannableString(s)
            Utility.setFontSizeForPath(ss1, jsonObject1.getInt("rank").toString(), tvUserNo!!.textSize.toInt() + 15)
            tvUserNo!!.text = ss1
            tvUserLeaderCoin!!.text = jsonObject1.getJSONObject("user").getString("balance") + getString(R.string.icoins)
            var icon = resources.getDrawable(R.drawable.ic_coin)
            if (icon != null) {
                icon = DrawableCompat.wrap(icon)
                val height = icon.intrinsicHeight
                val width = icon.intrinsicWidth
                icon.setBounds(0, 0, width, height)
                tvUserLeaderCoin!!.setCompoundDrawables(icon, null, null, null)
            }
            val imgUrl = jsonObject1.getJSONObject("user").getString("img")
            Glide.with(this)
                    .load(imgUrl)
                    .thumbnail(0.1f)
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE).placeholder(R.drawable.user).skipMemoryCache(true))
                    .into(ivUser!!)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun setupAdapters() {
        // Other winners RecyclerView and its Adapter
        winnersRecyclerView!!.layoutManager = LinearLayoutManager(requireContext(),
                LinearLayoutManager.VERTICAL, false)
        val winnersAdapter = KRecyclerViewAdapter(requireContext(),
                winnersList, KRecyclerViewHolderCallBack { viewGroup, i ->
            val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.row_leaderboard, viewGroup, false)
            LeaderHolder(view)
        }, KRecyclerItemClickListener { _: KRecyclerViewHolder?, o: Any?, i: Int -> })
        winnersRecyclerView!!.adapter = winnersAdapter
        winnersRecyclerView!!.isNestedScrollingEnabled = false
        winnersAdapter.notifyDataSetChanged()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser){
            /*if (activity != null && activity is HomeActivity) {
                homeActivity = activity as HomeActivity?
                if (homeActivity != null) {
                    homeActivity!!.showAd()
                }
            }*/
        }
    }
}
