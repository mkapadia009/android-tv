package com.app.itaptv.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import com.app.itaptv.R
import com.app.itaptv.structure.FeedContentData
import com.app.itaptv.utils.AlertUtils
import com.app.itaptv.utils.ExoUtil
import com.app.itaptv.utils.Log
import com.app.itaptv.utils.Utility
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_teaser.*
import kotlinx.android.synthetic.main.layout_media_controller.view.*

/**
 * Created by poonam on 28/1/19.
 */
class TeaserActivity : BaseActivity() {

    var player: ExoPlayer? = null
    var title = ""
    private var requestCode: Int = 0
    private lateinit var productId: String
    val REQUEST_CODE_ESPORTS = 500
    val CONTENT_DATA = "contentData"

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        setContentView(R.layout.activity_teaser)
        if (intent != null) {
            requestCode = intent.extras!!.getInt(BuyDetailActivity.REQUEST_CODE_EXTRA)
            showHideButton(requestCode)
            if (requestCode == REQUEST_CODE_ESPORTS) {
                var attachment = intent.getStringExtra(BuyDetailActivity.CONTENT_DATA)
                if (attachment != null) {
                    if (attachment.isNotEmpty()) play(Utility.urlFix(attachment))
                    return
                }
            } else {
                intent.getParcelableExtra<FeedContentData>(BuyDetailActivity.CONTENT_DATA).let {
                    title = it!!.postTitle
                    productId = it.postId
                    it.attachmentTeaserPromoData.let { attachment ->
                        it.teaserUrl.let { url ->
                            if (url.isNotEmpty()) play(Utility.urlFix(url))
                            return
                        }
                    }
                }
            }
        }
        AlertUtils.showAlert(
            getString(R.string.label_error), getString(R.string.invalid_attachment_url), null,
            this, null
        )
    }


    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }

    /*override fun onBackPressed() {
        val currentOrientation = resources.configuration.orientation
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            return
        }
        super.onBackPressed()
    }*/

    override fun onDestroy() {
        cleanup()
        super.onDestroy()
    }

    private fun showHideButton(requestCode: Int) {
        if (Utility.isTelevision()) {
            llPlayerBuyButton.visibility = View.GONE
            llPlayerWatchButton.visibility = View.GONE
        } else {
            if (requestCode == BuyDetailActivity.REQUEST_CODE_WATCH) {
                llPlayerBuyButton.visibility = View.GONE
                llPlayerWatchButton.visibility = View.VISIBLE
            } else if (requestCode == REQUEST_CODE_ESPORTS) {
                llPlayerBuyButton.visibility = View.GONE
                llPlayerWatchButton.visibility = View.GONE
            } else {
                llPlayerBuyButton.visibility = View.GONE
                llPlayerWatchButton.visibility = View.GONE
            }
        }
    }

    private fun play(url: String) {
        promoPlayer.exo_progress.visibility = View.VISIBLE
        promoPlayer.exo_fullscreen_icon.visibility = View.GONE
        promoPlayer.tvTitle.visibility = View.VISIBLE
        promoPlayer.ivClose.visibility = View.VISIBLE
        if (Utility.isTelevision()) {
            promoPlayer.llBuy.visibility = View.GONE
            promoPlayer.llWatch.visibility = View.GONE
        } else {
            if (requestCode == BuyDetailActivity.REQUEST_CODE_WATCH) {
                promoPlayer.llBuy.visibility = View.GONE
                promoPlayer.llWatch.visibility = View.VISIBLE
            } else if (requestCode == REQUEST_CODE_ESPORTS) {
                promoPlayer.llBuy.visibility = View.GONE
                promoPlayer.llWatch.visibility = View.GONE
            } else {
                promoPlayer.llBuy.visibility = View.GONE
                promoPlayer.llWatch.visibility = View.GONE
            }
        }

        promoPlayer.ivClose.setOnClickListener {
            cleanup()
            finish()
        }
        promoPlayer.tvTitle.text = title + getString(R.string.trailer)

        promoPlayer.llBuy.setOnClickListener {
            val intentData = Intent()
            intentData.putExtra(BuyDetailActivity.KEY_BUY, "BUY")
            setResult(BuyDetailActivity.REQUEST_CODE_BUY, intentData)
            cleanup()
            finish()
        }

        promoPlayer.setOnKeyListener(View.OnKeyListener { view, i, keyEvent ->
            Log.i("", "")
            if (i == KeyEvent.KEYCODE_DPAD_CENTER) {
                if (player!!.isPlaying) {
                    promoPlayer!!.showController()
                }
            }
            false
        })
        promoPlayer.setOnClickListener {
        }

        promoPlayer.setControllerVisibilityListener {
            if (it == 0) {
                llPlayerBuyButton.visibility = View.GONE
                llPlayerWatchButton.visibility = View.GONE
            } else {
                showHideButton(requestCode)
                /*if (requestCode == BuyDetailActivity.REQUEST_CODE_WATCH) {
                    llPlayerBuyButton.visibility = View.GONE
                    llPlayerWatchButton.visibility = View.VISIBLE
                } else {
                    llPlayerBuyButton.visibility = View.VISIBLE
                    llPlayerWatchButton.visibility = View.GONE
                }*/
            }
        }

        llPlayerBuyButton.setOnClickListener {
            val intentData = Intent()
            intentData.putExtra(BuyDetailActivity.KEY_BUY, "BUY")
            intentData.putExtra(BuyDetailActivity.KEY_PRODUCT_ID, productId)
            setResult(BuyDetailActivity.REQUEST_CODE_BUY, intentData)
            cleanup()
            finish()
        }
        llPlayerWatchButton.setOnClickListener {
            val intentData = Intent()
            intentData.putExtra(BuyDetailActivity.KEY_WATCH, "WATCH")
            setResult(BuyDetailActivity.REQUEST_CODE_WATCH, intentData)
            cleanup()
            finish()
        }

        // Data source
        val bandwidthMeter = DefaultBandwidthMeter()

        val adaptiveTrackSelection = DefaultTrackSelector(this)
        val dataSourceFactory = DefaultDataSourceFactory(
            this,
            Util.getUserAgent(this, getString(R.string.app_name)), bandwidthMeter
        )
        // TODO: - Build drm session manager here if Teaser/Trailer requires DRM build
        /*var drmSessionManager: DrmSessionManager<*>? = null
        if (ExoUtil.isDash(Uri.parse(url))) {
            val licUrl = LicenseManager.getInstance().licences[intent.getParcelableExtra<FeedContentData>(BuyDetailActivity.CONTENT_DATA).postId]
                    ?: ""
            val uuid = C.WIDEVINE_UUID *//*UUID.randomUUID()*//* //UUID.fromString("edef8ba9-79d6-4ace-a3c8-27dcd51d21ed")
            drmSessionManager = ExoUtil.getDrmSessionManager(licUrl, null,
                    uuid, false)
        }*/

        val mediaSource = ExoUtil.buildMediaSource(Uri.parse(url), dataSourceFactory, null)

        // Player
        player =
            ExoPlayer.Builder(this).setTrackSelector(adaptiveTrackSelection).build()
        player!!.addListener(object : Player.Listener {

            override fun onLoadingChanged(isLoading: Boolean) {
                if (isLoading) {
                    // show loader
                } else {
                    // hide loader
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                AlertUtils.showAlert(
                    getString(R.string.label_error),
                    getString(R.string.unable_to_play_the_promo) + "${error.message}",
                    null, this@TeaserActivity
                ) { finish() }
            }
        })

        try {
            Glide.with(this).asBitmap()
                .load(intent.getParcelableExtra<FeedContentData>(BuyDetailActivity.CONTENT_DATA)!!.teaserImage)
                .into(object : SimpleTarget<Bitmap?>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap?>?
                    ) {
                        if (player != null) {
                            updateArtWork(resource)
                        }
                    }
                })
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        // Prepare player with media source
        if (mediaSource != null) player!!.prepare(mediaSource)
        // Start playing as the player is prepared
        player!!.playWhenReady = true

        // Set player
        promoPlayer.player = player
    }

    private fun cleanup() {
        promoPlayer.player = null
        if (player != null) {
            player!!.stop()
            player!!.clearVideoSurface()
            player!!.release()
            player = null
        }
    }


    fun updateArtWork(resource: Bitmap) {
        if (promoPlayer != null) {
            try {
                val d: Drawable = BitmapDrawable(resources, resource)
                promoPlayer.defaultArtwork = d
            } catch (e: Exception) {
                //promoPlayer.setDefaultArtwork(resource)
            }
        }
    }
}