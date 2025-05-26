package com.app.itaptv.activity

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.app.itaptv.MyApp
import com.app.itaptv.R
import com.app.itaptv.roomDatabase.MediaDuration
import com.app.itaptv.roomDatabase.ListRoomDatabase
import com.app.itaptv.utils.AlertUtils
import com.app.itaptv.utils.ExoUtil
import com.app.itaptv.utils.LocalStorage
import com.app.itaptv.utils.Utility
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.drm.DrmSession.DrmSessionException
import com.google.android.exoplayer2.offline.Download
import com.google.android.exoplayer2.offline.DownloadHelper
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import kotlinx.android.synthetic.main.activity_offline_player.*
import java.net.CookieHandler
import java.net.CookieManager
import java.net.CookiePolicy

const val OFFLINE_CONTENT_ID = "id"
const val OFFLINE_CONTENT_IMAGE = "image"

class OfflinePlayerActivity : BaseActivity() {

    var player: SimpleExoPlayer? = null
    lateinit var contentId: String
    lateinit var contentImage: String
    var listRoomDatabase: ListRoomDatabase? = null

    lateinit var fullscreenButton: ImageView
    var fullscreen = false

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSystemUI()
        setContentView(R.layout.activity_offline_player)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)

        fullscreenButton = offlinePlayer.findViewById(R.id.exo_full_icon);

        listRoomDatabase = ListRoomDatabase.getDatabase(this)
        val cookieManager = CookieManager()
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER)
        if (CookieHandler.getDefault() !== cookieManager) {
            CookieHandler.setDefault(cookieManager)
        }

        getOfflineContent()

        fullscreenButton.setOnClickListener {
            if (fullscreen) {
                fullscreenButton.setImageDrawable(ContextCompat.getDrawable(this@OfflinePlayerActivity, R.drawable.exo_icon_fullscreen_enter))
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
                if (supportActionBar != null) {
                    supportActionBar!!.show()
                }
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                fullscreen = false
            } else {
                fullscreenButton.setImageDrawable(ContextCompat.getDrawable(this@OfflinePlayerActivity, R.drawable.exo_icon_fullscreen_exit))
                window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
                if (supportActionBar != null) {
                    supportActionBar!!.hide()
                }
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                fullscreen = true
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }

    private fun getOfflineContent() {
        if (intent.hasExtra(OFFLINE_CONTENT_ID)) {
            contentId = intent.getStringExtra(OFFLINE_CONTENT_ID).toString()
            contentImage = intent.getStringExtra(OFFLINE_CONTENT_IMAGE).toString()
            intent.getStringExtra(OFFLINE_CONTENT_ID)?.let { offlineContentId ->
                MyApp.getInstance().downloadManager.downloadIndex
                        .getDownload(offlineContentId)?.let { download ->
                            if (download.state == Download.STATE_COMPLETED) {
                                playContent(download)
                            }
                        }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        player?.playWhenReady = false
        offlinePlayer.onPause()
    }

    override fun onResume() {
        super.onResume()
        offlinePlayer.onResume()
    }

    private fun getMediaSource(download: Download): MediaSource? {
        val downloadRequest = MyApp.getInstance().downloadTracker
                .getDownloadRequest(download.request.uri)
        return if (downloadRequest != null) {
            val drmSessionManager = ExoUtil.getOfflineDrmSessionManager(download.request.id,
                    "", null, C.WIDEVINE_UUID, false)

            if (drmSessionManager != null) {
                DownloadHelper.createMediaSource(downloadRequest, MyApp.getInstance()
                        .buildDataSourceFactory(), drmSessionManager)
            } else {
                DownloadHelper.createMediaSource(downloadRequest, MyApp.getInstance()
                        .buildDataSourceFactory())
            }
        } else {
            null
        }
    }

    private fun playContent(download: Download) {
        HomeActivity.getInstance().pausePlayer()
        val hasLicence = LocalStorage.getLicence(download.request.id).isNotEmpty()
        if (ExoUtil.isDash(download.request.uri) && hasLicence && !ExoUtil.isValidLicence(download.request.id)) {
            AlertUtils.showAlert(getString(R.string.licence_expired),
                    getString(R.string.download_the_content_again),
                    null, this) {
                ExoUtil.delete(download.request.id)
                finish()
            }
            return
        }

        val app = MyApp.getInstance()

        val builder = DefaultTrackSelector.ParametersBuilder(this)
        val trackSelectionParameters = builder.build()

        val trackSelectionFactory = AdaptiveTrackSelection.Factory()
        val renderersFactory = app.buildRenderersFactory(false)

        val trackSelector = DefaultTrackSelector(this, trackSelectionFactory)
        trackSelector.parameters = trackSelectionParameters

        player = SimpleExoPlayer.Builder(this, renderersFactory)
                .setTrackSelector(trackSelector)
                .build()
        player!!.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
        player!!.addListener(object : Player.Listener {

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                Log.d("D: ", player!!.currentPosition.toString())
                if (player!!.currentPosition > 0) {
                    savePlayerMediaDuration(contentId, player!!.duration, player!!.currentPosition)
                    Log.d("D: ", player!!.duration.toString())
                }
            }

            override fun onLoadingChanged(isLoading: Boolean) {
            }

            override fun onPlayerError(error: PlaybackException) {
                //Log.e("Offline Playback", "Player error: ${error.message}")
                var message = getString(R.string.error_item_cannot_be_played)
                if (error.cause != null && error.cause is DrmSessionException) {
                    message = getString(R.string.offline_licence_expired)
                }
                ExoUtil.delete(contentId)
                AlertUtils.showAlert(getString(R.string.error_playback), message, null,
                        this@OfflinePlayerActivity) {
                    finish()
                }
            }
        })
        val mediaSource = getMediaSource(download)
        if (mediaSource != null) {
            player!!.prepare(mediaSource)
            resumeFromLastPlayed()
            player!!.volume = 0.4f
            player!!.playWhenReady = true
            offlinePlayer.player = player

            try {
                Glide.with(this).asBitmap().load(contentImage).into(object : SimpleTarget<Bitmap?>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap?>?) {
                        if (player != null) {
                            updateArtWork(resource)
                        }
                    }
                })
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        } else {
            AlertUtils.showAlert(getString(R.string.error_playback), getString(R.string.media_item_not_found),
                    null, this) { finish() }
        }
    }

    private fun releasePlayer() {
        if (player != null) {
            player!!.stop()
            player!!.clearVideoSurface()
            player!!.release()
            player = null
        }
        offlinePlayer.player = null
    }

    override fun onDestroy() {
        releasePlayer()
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        super.onDestroy()

    }

    fun savePlayerMediaDuration(itemId: String, totalDuration: Long, currentPosition: Long) {
        Thread(Runnable {
            val mediaDuration = MediaDuration()
            mediaDuration.id = itemId
            mediaDuration.duration = totalDuration
            mediaDuration.playedDuration = currentPosition
            listRoomDatabase?.mediaDAO()?.savePlayerDuration(mediaDuration)
        }).start()
    }

    private fun resumeFromLastPlayed() {
        val resumeDuration = getResumePosition()
        if (player != null) {
            player?.seekTo(resumeDuration)
        }
    }

    private fun getResumePosition(position: Int? = null): Long {
        var d: Long = 0
        val current = contentId
        if (listRoomDatabase == null) {
            listRoomDatabase = ListRoomDatabase.getDatabase(this)
        }
        val result = listRoomDatabase?.mediaDAO()?.getMediaDurationData(contentId)
        if (result != null && result.size > 0) {
            d = if (Utility.isMediaEnding(result[0].playedDuration, result[0].duration)) {
                0
            } else {
                result[0].playedDuration
            }
        }

        if (d < 0) {
            d = 0
        }
        return d
    }

    fun updateArtWork(resource: Bitmap) {
        if (offlinePlayer != null) {
            try {
                val d: Drawable = BitmapDrawable(resources, resource)
                offlinePlayer.defaultArtwork = d
            } catch (e: Exception) {
                //offlinePlayer.setDefaultArtwork(resource)
            }
        }
    }

}
