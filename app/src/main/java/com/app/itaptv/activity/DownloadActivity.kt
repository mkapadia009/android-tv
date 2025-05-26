package com.app.itaptv.activity

import android.os.Bundle
import com.app.itaptv.R
import com.app.itaptv.fragment.*

class DownloadActivity : BaseActivity(), DownloadOptionsBottomSheetFrag.ItemClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download)
        init()
    }

    private fun init() {
        setToolbar(true)
        showToolbarBackButton(R.drawable.white_arrow)
        showToolbarTitle(true)
        setCustomizedTitle(0f)
        setToolbarTitle(getString(R.string.label_downloads))
    }

    override fun sheetCallback(item: String?) {

    }
}