package com.app.itaptv.activity
//
//import android.annotation.SuppressLint
//import android.content.Context
//import android.content.Intent
//import android.os.Bundle
//import android.os.Handler
//import android.view.LayoutInflater
//import android.view.View
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.app.itap.MyApp
//import com.app.itap.R
//import com.app.itap.offline.DownloadTracker
//import com.app.itap.utils.AlertUtils
//import com.app.itap.utils.ExoUtil
//import com.app.itap.utils.Log
//import com.google.android.exoplayer2.offline.Download
//import com.google.android.exoplayer2.util.Util
//import com.kalpesh.krecycleradapter.Interface.KRecyclerItemClickListener
//import com.kalpesh.krecycleradapter.Interface.KRecyclerViewHolderCallBack
//import com.kalpesh.krecycleradapter.KRecyclerViewAdapter
//import com.kalpesh.krecycleradapter.KRecyclerViewHolder
//import kotlinx.android.synthetic.main.activity_downloads.*
//import kotlinx.android.synthetic.main.row_downloaded_content.view.*
//
//class DownloadsActivity : BaseActivity(), DownloadTracker.Listener {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_downloads)
//
//        setToolbar(true)
//        showToolbarBackButton(R.drawable.white_arrow)
//        showToolbarTitle(true)
//        title = "My Downloads"
//        setCustomizedTitle(0f)
//
//        displayDownloads()
//    }
//
//    override fun onStart() {
//        super.onStart()
//        MyApp.getInstance().downloadTracker.addListener(this)
//    }
//
//    override fun onStop() {
//        MyApp.getInstance().downloadTracker.removeListener(this)
//        super.onStop()
//    }
//
//    override fun onDownloadsChanged() {
//        displayDownloads()
//    }
//
//    private fun displayDownloads() {
//        val downloads = ArrayList<Download>()
//
//        val app = MyApp.getInstance()
//        val cursor = app.downloadManager.downloadIndex.getDownloads()
//        for (i in 0 until cursor.count) {
//            if (cursor.moveToPosition(i)) {
//                downloads.add(cursor.download)
//            }
//        }
//
//        Log.i("Downloads", "Total downloads: $downloads")
//
//        val adapter = KRecyclerViewAdapter(this, downloads, KRecyclerViewHolderCallBack { p0, p1 ->
//            val view = LayoutInflater.from(p0.context).inflate(R.layout.row_downloaded_content, p0, false)
//            DownloadedItemHolder(view, object : DownloadedItemHolder.DownloadedItemHolderListener {
//                override fun removeDownload(download: Download) {
//                    val name = Util.fromUtf8Bytes(download.request.data)
//                    val message = "Are you sure you want to remove $name? You won't be able to view this content offline until you download it again."
//                    AlertUtils.showAlert("Confirm Delete", message, "Delete", "Cancel", this@DownloadsActivity, object : AlertUtils.AlertClickHandler {
//                        override fun alertButtonAction(isPositiveAction: Boolean) {
//                            if (isPositiveAction) {
//                                ExoUtil.delete(download.request.id)
//                                Handler().postDelayed({
//                                    displayDownloads()
//                                }, 100)
//                            }
//                        }
//
//                    })
//                }
//
//            })
//        }, KRecyclerItemClickListener { p0, p1, p2 ->
//            if (p1 is Download && p1.state == Download.STATE_COMPLETED) {
//                val i = Intent(this, OfflinePlayerActivity::class.java)
//                i.putExtra(OFFLINE_CONTENT_ID, p1.request.id)
//                startActivity(i)
//            }
//        })
//
//        recyclerView.adapter = adapter
//        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL,
//                false)
//    }
//
//}
//
//class DownloadedItemHolder(private var view: View, private var listener: DownloadedItemHolderListener) : KRecyclerViewHolder(view) {
//
//    interface DownloadedItemHolderListener {
//        fun removeDownload(download: Download)
//    }
//
//    @SuppressLint("SetTextI18n")
//    override fun setData(context: Context, itemObject: Any) {
//        super.setData(context, itemObject)
//        if (itemObject is Download) {
//            view.tvTitle.text = Util.fromUtf8Bytes(itemObject.request.data)
//            setStatus(itemObject)
//            view.removeBtn.setOnClickListener {
//                listener.removeDownload(itemObject)
//            }
//        }
//    }
//
//    @SuppressLint("SetTextI18n")
//    private fun setStatus(download: Download) {
//        view.tvSubtitle.text = when (download.state) {
//            Download.STATE_COMPLETED -> {
//                "Downloaded"
//            }
//            Download.STATE_DOWNLOADING -> {
//                "Downloading..."
//            }
//            Download.STATE_FAILED -> {
//                "Download failed!"
//            }
//            Download.STATE_QUEUED -> {
//                "Download queued"
//            }
//            Download.STATE_REMOVING -> {
//                "Removing download..."
//            }
//            Download.STATE_RESTARTING -> {
//                "Restarting download..."
//            }
//            Download.STATE_STOPPED -> {
//                "Download stopped."
//            }
//            else -> {
//                "NA"
//            }
//        }
//    }
//
//}
