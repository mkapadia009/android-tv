package com.app.itaptv.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.android.volley.Request
import com.app.itaptv.API.APIManager
import com.app.itaptv.API.APIRequest
import com.app.itaptv.API.Url
import com.app.itaptv.R
import com.app.itaptv.activity.vouchers.CouponsDetailsActivity
import com.app.itaptv.holder.CouponListingHolder
import com.app.itaptv.structure.vouchers.CouponsDetails
import com.kalpesh.krecycleradapter.Interface.KRecyclerItemClickListener
import com.kalpesh.krecycleradapter.Interface.KRecyclerViewHolderCallBack
import com.kalpesh.krecycleradapter.KRecyclerViewAdapter
import com.kalpesh.krecycleradapter.KRecyclerViewHolder
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class MyCouponsActivity : BaseActivity() {

    //UI
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmpty: TextView
    private lateinit var btBuyNow: Button

    //Variables
    var view: View? = null
    var couponsDetailsArrayList = ArrayList<CouponsDetails>()
    var vpCoupons: ViewPager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_coupons)
        setUI()
        setToolbar(true)
        showToolbarBackButton(R.drawable.back_arrow_white)
        showToolbarTitle(true)
        setToolbarTitle(resources.getString(R.string.label_mycoupon))
    }

    private fun setUI() {
        progressBar = findViewById(R.id.progressBar)
        tvEmpty = findViewById(R.id.tvEmpty)
        btBuyNow = findViewById(R.id.btBuyNow)
        btBuyNow.setOnClickListener(View.OnClickListener {
            finish()
        })
    }

    override fun onResume() {
        super.onResume()
        getData()
    }

    private fun getData() {
        progressBar.visibility = View.VISIBLE
        val apiRequest = APIRequest(Url.MY_COUPONS, Request.Method.GET, null, null, this)
        APIManager.request(apiRequest) { response, error, headers, statusCode ->
            progressBar.visibility = View.GONE
            if (response != null) {
                try {
                    val `object` = JSONObject(response)
                    if (`object`.has("msg")) {
                        val object1 = `object`.getJSONObject("msg")
                        val array = object1.getJSONArray("items")
                        couponsDetailsArrayList.clear()
                        for (i in 0 until array.length()) {
                            couponsDetailsArrayList.add(CouponsDetails(array.getJSONObject(i), false))
                        }
                        emptyState()
                        setupRecyclerView()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    progressBar.visibility = View.GONE
                    emptyState()
                }
            }
        }
    }

    private fun emptyState() {
        if (couponsDetailsArrayList.isEmpty()) {
            tvEmpty.visibility = View.VISIBLE
            btBuyNow.visibility = View.VISIBLE
        } else {
            tvEmpty.visibility = View.GONE
            btBuyNow.visibility = View.GONE
        }
    }

    private fun setupRecyclerView() {
        val rvCoupon: RecyclerView = findViewById(R.id.rvMyCoupons)
        rvCoupon.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val adapter = KRecyclerViewAdapter(this, couponsDetailsArrayList, KRecyclerViewHolderCallBack { viewGroup: ViewGroup, i: Int ->
            val layoutView = LayoutInflater.from(viewGroup.context).inflate(R.layout.row_coupon_listing, viewGroup, false)
            CouponListingHolder(layoutView)
        }, KRecyclerItemClickListener { _: KRecyclerViewHolder?, o: Any?, i: Int ->
            // item clicked
            val couponsDetails = o as CouponsDetails?
            val intent = Intent(this, CouponsDetailsActivity::class.java)
            intent.putExtra(CouponsDetailsActivity.COUPON_DETAILS_OBJECT, couponsDetails)
            startActivity(intent)
        })
        rvCoupon.adapter = adapter
        adapter.notifyDataSetChanged()
    }

}