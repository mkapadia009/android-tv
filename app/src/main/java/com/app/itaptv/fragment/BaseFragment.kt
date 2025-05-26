package com.app.itaptv.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.app.itaptv.R
import com.app.itaptv.activity.BaseActivity
import kotlinx.android.synthetic.main.toolbar.*

open class BaseFragment : Fragment() {

    @LayoutRes
    protected var layoutId: Int? = null
    protected var rootView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        layoutId?.let {
            if (rootView == null) {
                rootView = inflater.inflate(it, container, false)
            }
            return rootView
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    protected fun showActivity(cls: Class<*>, finish: Boolean = false) {
        val intent = Intent(context, cls)
        startActivity(intent)
        if (finish) activity?.finish()
    }

    protected fun setToolbar() {
        toolbar?.let {
            (activity as? BaseActivity)?.let { act ->
                act.setSupportActionBar(toolbar)
                act.supportActionBar?.setDisplayHomeAsUpEnabled(true)
                act.supportActionBar?.setHomeAsUpIndicator(R.drawable.back_arrow)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            (activity as? BaseActivity)?.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}