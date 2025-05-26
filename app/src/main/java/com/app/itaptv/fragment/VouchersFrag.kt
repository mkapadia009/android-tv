package com.app.itaptv.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.app.itaptv.API.APIManager
import com.app.itaptv.API.APIRequest
import com.app.itaptv.API.Url
import com.app.itaptv.R
import com.app.itaptv.activity.GiveAwayActivity
import com.app.itaptv.activity.HomeActivity
import com.app.itaptv.activity.VerificationActivity
import com.app.itaptv.dialog.RedeemCodeSuccessDialog
import com.app.itaptv.structure.User
import com.app.itaptv.utils.Constant
import com.app.itaptv.utils.LocalStorage
import com.app.itaptv.utils.Log
import kotlinx.android.synthetic.main.activity_coupon_redemption.*
import kotlinx.android.synthetic.main.dialog_success_coupon.*
import org.json.JSONObject
import java.util.*

class VouchersFrag : Fragment() {

    private lateinit var rootView: View
    var user: User? = null

    var homeActivity: HomeActivity? = null

    companion object {
        @JvmStatic
        fun newInstance(): VouchersFrag {
            return VouchersFrag()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_vouchers, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {
        user = LocalStorage.getUserData()
        btnRedeemNowBtn.setOnClickListener {
            if (user!!.mobile == null || user!!.mobile == "") {
                phoneCheck()
            } else {
                val code: String = etRedeemCode.text.toString()
                if (code.isNotEmpty()) {
                    if (user!!.mobile == null || user!!.mobile == "") {
                        startActivityForResult(
                            Intent(
                                context,
                                VerificationActivity::class.java
                            ).putExtra(VerificationActivity.TYPE_KEY, Constant.KEY_MOBILE),
                            GiveAwayActivity.GIVE_AWAY_CODE
                        )
                    } else {
                        couponRedemptionApi(code)
                        etRedeemCode.setText("")
                    }
                } else {
                    etRedeemCode.error = getString(R.string.please_enter_code)
                }
            }
        }
    }

    private fun phoneCheck() {
        if (user!!.mobile == null || user!!.mobile == "") {
            startActivityForResult(
                Intent(context, VerificationActivity::class.java).putExtra(
                    VerificationActivity.TYPE_KEY,
                    Constant.KEY_MOBILE
                ), GiveAwayActivity.GIVE_AWAY_CODE
            )
        }
    }

    private fun couponRedemptionApi(code: String) {
        try {
            val params = HashMap<String, String>()
            params["coupon"] = code
            Log.e(Url.APPLY_COUPON_CODE, Url.APPLY_COUPON_CODE)

            val apiRequest = APIRequest(
                Url.APPLY_COUPON_CODE,
                Request.Method.POST,
                params,
                null,
                requireContext()
            )
            APIManager.request(apiRequest) { response, _, _, _ ->
                if (response != null) {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.has("type")) {
                        if (jsonObject.getString("type") == "OK") {
                            val dialog = RedeemCodeSuccessDialog(requireContext())
                            dialog.show()
                            dialog.btGoToStore?.setOnClickListener {
                                dialog.dismiss()
                                context?.sendBroadcast(Intent(HomeActivity.STORE_BROADCAST_KEY))
                            }
                        } else {
                            var message = "Some Error Occurred"
                            jsonObject.getString("msg").let {
                                message = it
                            }
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(
                        context,
                        getString(R.string.some_error_occurred),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } catch (E: Exception) {
            E.printStackTrace()
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