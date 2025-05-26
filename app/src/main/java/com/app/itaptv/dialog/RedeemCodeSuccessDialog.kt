package com.app.itaptv.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.RelativeLayout
import com.app.itaptv.R
import kotlinx.android.synthetic.main.dialog_success_coupon.*

class RedeemCodeSuccessDialog(context: Context) : Dialog(context) {

    init {
        init()
    }

    private fun init() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCancelable(false)
        setContentView(R.layout.dialog_success_coupon)
        val window = this.window
        if (window != null) {
            window.setLayout(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        ivClose?.setOnClickListener {
            dismiss()
        }
    }
}
