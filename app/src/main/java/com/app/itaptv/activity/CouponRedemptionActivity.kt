package com.app.itaptv.activity

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.app.itaptv.API.APIManager
import com.app.itaptv.API.APIRequest
import com.app.itaptv.API.Url
import com.app.itaptv.R
import com.app.itaptv.dialog.RedeemCodeSuccessDialog
import com.app.itaptv.structure.User
import com.app.itaptv.utils.Constant
import com.app.itaptv.utils.LocalStorage
import com.app.itaptv.utils.Log
import com.app.itaptv.utils.Utility
import kotlinx.android.synthetic.main.activity_coupon_redemption.*
import kotlinx.android.synthetic.main.dialog_success_coupon.*
import org.json.JSONObject
import java.util.*

class CouponRedemptionActivity : BaseActivity() {

    var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coupon_redemption)

        user = LocalStorage.getUserData()
        phoneCheck()
        setToolbar(true)
        showToolbarBackButton(R.drawable.back_arrow)
        showToolbarTitle(true)

        val toolTitle = intent.getStringExtra("title")
        toolbar.title = toolTitle

        etRedeemCode.setOnEditorActionListener { _: TextView?, actionId: Int, event: KeyEvent? ->
            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                if (etRedeemCode.text.toString() != "") {
                    couponRedemptionApi(etRedeemCode.text.toString())
                } else {
                    etRedeemCode.error = getString(R.string.please_enter_a_code)
                }
            }
            false
        }
    }

    private fun phoneCheck() {
        if (user!!.mobile == null || user!!.mobile == "") {
            startActivityForResult(
                Intent(this, VerificationActivity::class.java).putExtra(
                    VerificationActivity.TYPE_KEY,
                    Constant.KEY_MOBILE
                ), GiveAwayActivity.GIVE_AWAY_CODE
            )
        }
    }

    fun redeemCouponClick(v: View) {
        when (v.id) {
            R.id.btnRedeemNowBtn -> {
                val code: String = etRedeemCode.text.toString()
                if (code.isNotEmpty()) {
                    if (user!!.mobile == null || user!!.mobile == "") {
                        startActivityForResult(
                            Intent(
                                this,
                                VerificationActivity::class.java
                            ).putExtra(VerificationActivity.TYPE_KEY, Constant.KEY_MOBILE),
                            GiveAwayActivity.GIVE_AWAY_CODE
                        )
                    } else {
                        couponRedemptionApi(code)
                    }
                } else {
                    etRedeemCode.error = getString(R.string.please_enter_a_code)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GiveAwayActivity.GIVE_AWAY_CODE) {
            user = LocalStorage.getUserData()
            if (user!!.mobile == null || user!!.mobile == "") {
                finish()
            }
        }
    }

    private fun couponRedemptionApi(code: String) {
        try {
            val params = HashMap<String, String>()
            params["coupon"] = code
            Log.e(Url.APPLY_COUPON_CODE, Url.APPLY_COUPON_CODE)

            val apiRequest =
                APIRequest(Url.APPLY_COUPON_CODE, Request.Method.POST, params, null, this)
            APIManager.request(apiRequest) { response, _, _, _ ->
                if (response != null) {
                    Log.i(TAG, response.toString());
                    val jsonObject = JSONObject(response)
                    if (jsonObject.has("type")) {
                        if (jsonObject.getString("type") == "OK") {
                            val dialog = RedeemCodeSuccessDialog(this)
                            dialog.show()
                            dialog.btGoToStore?.setOnClickListener {
                                dialog.dismiss()
                                sendBroadcast(Intent(HomeActivity.STORE_BROADCAST_KEY))
                                finish()
                            }
                        } else {
                            var message = getString(R.string.some_error_occurred)
                            /*jsonObject.getString("msg").let {
                                message = it
                            }*/
                            Log.i("Coupon msg", jsonObject.get("msg").toString());
                            message = Utility.getMessage(this, jsonObject.get("msg").toString());
                            if (message.isEmpty()){
                                message = getString(R.string.some_error_occurred)
                            }
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.some_error_occurred),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } catch (E: Exception) {
            E.printStackTrace()
        }
    }
}
