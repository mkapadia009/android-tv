package com.app.itaptv.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import com.app.itaptv.R
import com.app.itaptv.activity.OFFLINE_CONTENT_ID
import com.app.itaptv.activity.OFFLINE_CONTENT_IMAGE
import com.app.itaptv.activity.OfflinePlayerActivity
import com.app.itaptv.utils.AlertUtils
import com.app.itaptv.utils.ExoUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.download_options_sheet_layout.*

const val MEDIA_ID = "media_id"
const val MEDIA_TITLE = "media_title"
const val MEDIA_IMAGE = "media_image"
const val RELOAD_DOWNLOADS_LIST = "reload_downloads"

class DownloadOptionsBottomSheetFrag : BottomSheetDialogFragment() {

    private var mListener: ItemClickListener? = null
    private var mediaId: String? = null
    private var mediaTitle: String? = null
    private var mediaImage: String? = null

    companion object {
        const val TAG = "DownloadOptionsFrag"
        fun newInstance(): DownloadOptionsBottomSheetFrag {
            return DownloadOptionsBottomSheetFrag()
        }
    }

    @Nullable
    override fun onCreateView(inflater: LayoutInflater, @Nullable container: ViewGroup?,
                              @Nullable savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.download_options_sheet_layout, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener { dialog ->
            val d = dialog as BottomSheetDialog
            val bottomSheet: View? = d.findViewById(R.id.bottom_sheet_layout)
            BottomSheetBehavior.from(bottomSheet!!).state = BottomSheetBehavior.STATE_EXPANDED
        }

        return dialog
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    @SuppressLint("StringFormatInvalid")
    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = arguments
        if (arguments != null) {
            mediaId = args?.getString(MEDIA_ID)
            mediaTitle = args?.getString(MEDIA_TITLE)
            mediaImage = args?.getString(MEDIA_IMAGE)
            titleTv.text = mediaTitle

        }

        tvWatchNow.setOnClickListener {
            val i = Intent(requireContext(), OfflinePlayerActivity::class.java)
            i.putExtra(OFFLINE_CONTENT_ID, mediaId)
            i.putExtra(OFFLINE_CONTENT_IMAGE, mediaImage)
            startActivity(i)
            dialog?.dismiss()
        }

        tvDelete.setOnClickListener {
            dialog?.dismiss()
            val message = String.format(getString(R.string.sure_to_remove,mediaTitle))
            AlertUtils.showAlert(getString(R.string.confirm_delete), message, getString(R.string.delete), getString(R.string.cancel), requireContext()) { isPositiveAction ->
                if (isPositiveAction) {
                    ExoUtil.delete(mediaId)
                    Handler().postDelayed({
                        dialog?.cancel()
                        mListener?.sheetCallback(RELOAD_DOWNLOADS_LIST)
                    }, 100)
                }
            }

        }

    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        mListener?.sheetCallback("sheet_closed")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = if (context is ItemClickListener) {
            context
        } else {
            throw RuntimeException("$context must implement ItemClickListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    interface ItemClickListener {
        fun sheetCallback(item: String?)

    }
}

