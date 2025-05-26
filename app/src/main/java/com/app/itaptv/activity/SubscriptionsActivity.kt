package com.app.itaptv.activity

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.WindowManager
import android.widget.TextView
import com.app.itaptv.R
import com.app.itaptv.utils.CustomClickableSpan

class SubscriptionsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subscriptions)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)

        init()
    }

    private fun init() {

        setClickableText()
    }

    private fun setClickableText() {
        val spannableString = SpannableString(getString(R.string.subject_to_tc))
        spannableString.setSpan(CustomClickableSpan(this, 0), 11, 29, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(CustomClickableSpan(this, 1), 32, 45, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val tvLabelTermsNPolicyLine1 = findViewById<TextView>(R.id.tvLabelTermsNPolicyLine1)
        tvLabelTermsNPolicyLine1.text = spannableString
        tvLabelTermsNPolicyLine1.movementMethod = LinkMovementMethod.getInstance()
        tvLabelTermsNPolicyLine1.highlightColor = Color.TRANSPARENT
    }

}
