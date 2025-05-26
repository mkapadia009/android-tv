package com.app.itaptv.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.*
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.app.NotificationCompat
import com.app.itaptv.API.APIManager
import com.app.itaptv.API.APIMethods
import com.app.itaptv.MyApp
import com.app.itaptv.R
import com.app.itaptv.activity.HomeActivity
import com.app.itaptv.custom_interface.PlaybackStateListener
import com.app.itaptv.roomDatabase.AnalyticsData
import com.app.itaptv.roomDatabase.ListRoomDatabase
import com.app.itaptv.structure.FeedContentData
import com.app.itaptv.utils.*
import com.app.itaptv.utils.Analyticals.ACTIVITY_TYPE_PLAY_AUDIO
import com.app.itaptv.utils.Wallet.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.NotificationTarget
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.drm.DrmSession
import com.google.android.exoplayer2.drm.DrmSessionManager
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import org.jetbrains.annotations.Nullable
import java.util.*
import kotlin.math.abs

class AudioPlayerService : Service() {

    private val tag = "AudioPlayerService"
    private val notification_id = 234

    var listener: PlaybackStateListener? = null

    var playAd = true

    var adPlayer: ExoPlayer? = null
    var player: ExoPlayer? = null
    private var playerNotificationManager: PlayerNotificationManager? = null
    private var mediaSession: MediaSessionCompat? = null
    private var mediaSessionConnector: MediaSessionConnector? = null

    val playList = ArrayList<FeedContentData>()

    private var secondsPlayed = 0
    var playedseconds = 0
    private var playerPaused = false
    private var resumedFromLast = false
    private var resumedFromLastAndPaused = false

    var isPlaying = false
        set(value) {
            field = value
            startTimer()
        }

    var isPlayingAd = false
    var isPlayingImageAd = false
    var isPlayingVideoAd = false
    var isPlayingTeaser = false
    var isPlayingCreditAd = false

    var adIndex = -1

    val REWIND_PLAYBACK = "1"
    val FORWARD_PLAYBACK = "2"
    val MILLISECONDS: Long = 30000 // 30 seconds
    val VERTICALMILLISECONDS: Long = 10000 // 10 seconds

    fun getCurrentItem(): FeedContentData? {
        try {
            if (player != null) {
                if (playList.size > player!!.currentWindowIndex) {
                    return playList[player!!.currentWindowIndex]
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun getItem(index: Int): FeedContentData? {
        if (playList.size > index) {
            return playList[index]
        }
        return null
    }

    fun getCurrentWindowIndex(): Int? {
        if (isPlaying && player != null) {
            return player!!.currentWindowIndex
        }
        return -1
    }

    override fun onCreate() {
        super.onCreate()
    }

    fun play(
        items: ArrayList<FeedContentData>,
        index: Int,
        feed_id: String,
        pageType: String,
        iswatched: String,
        play: Boolean
    ) {
        cleanUp()

        // Data source
        val bandwidthMeter = DefaultBandwidthMeter.getSingletonInstance(this)
        val adaptiveTrackSelection = DefaultTrackSelector(this)

        val dataSourceFactory = DefaultDataSourceFactory(
            this, Util.getUserAgent(this, getString(R.string.app_name)), bandwidthMeter
        )

        val concatenatingMediaSource = ConcatenatingMediaSource()
        playList.clear()
        items.forEach {
            var mediaSource: MediaSource? = null
            if (it.singleCredit) {
                if (it.adFieldsData.attachment == null) return@forEach
                if (it.adFieldsData.attachment != null && it.adFieldsData.attachment.isNotEmpty()) {

                    var drmSessionManager: DrmSessionManager? = null
                    if (it.postDrmProtected.equals(Constant.YES, ignoreCase = true)) {
                        val licUrl = LicenseManager.getInstance().licences[it.postId] ?: ""
                        val uuid =
                            C.WIDEVINE_UUID /*UUID.randomUUID()*/ //UUID.fromString("edef8ba9-79d6-4ace-a3c8-27dcd51d21ed")
                        drmSessionManager = ExoUtil.getDrmSessionManager(
                            licUrl, null, uuid, false
                        )
                    }

                    // Create media source
                    mediaSource = ExoUtil.buildMediaSource(
                        Uri.parse(it.adFieldsData.attachment), dataSourceFactory, drmSessionManager
                    )
                }
            } else {
                if (it.attachmentData == null) return@forEach
                if (it.mediaUrl != null && it.mediaUrl.isNotEmpty()) {

                    var drmSessionManager: DrmSessionManager? = null
                    if (it.postDrmProtected.equals(Constant.YES, ignoreCase = true)) {
                        val licUrl = LicenseManager.getInstance().licences[it.postId] ?: ""
                        val uuid =
                            C.WIDEVINE_UUID /*UUID.randomUUID()*/ //UUID.fromString("edef8ba9-79d6-4ace-a3c8-27dcd51d21ed")
                        drmSessionManager = ExoUtil.getDrmSessionManager(
                            licUrl, null, uuid, false
                        )
                    }

                    // Create media source
                    mediaSource = ExoUtil.buildMediaSource(
                        Uri.parse(it.mediaUrl), dataSourceFactory, drmSessionManager
                    )
                }
            }

            // If media is already downloaded, create media source from download request
            /*if (ExoUtil.isDownloaded(it.postId)) {
                val downloadRequest = DownloadRequest(it.postId, DownloadRequest.TYPE_DASH,
                        Uri.parse(it.mediaUrl), Collections.emptyList(), null, null)
                mediaSource = if (drmSessionManager != null) {
                    DownloadHelper.createMediaSource(downloadRequest,
                            MyApp.getInstance().buildDataSourceFactory(), drmSessionManager)
                } else {
                    DownloadHelper.createMediaSource(downloadRequest,
                            MyApp.getInstance().buildDataSourceFactory())
                }
            }*/

            // Concatenate media source
            if (mediaSource != null) {/*val adsLoader = ImaAdsLoader(this, Uri.parse(AD_URI))
            val adsMediaSource = AdsMediaSource(mediaSource, cacheDataSourceFactory,
                    adsLoader, null)
            concatenatingMediaSource.addMediaSource(adsMediaSource)*/
                concatenatingMediaSource.addMediaSource(mediaSource)
                playList.add(it)
            }
        }

        if (playList.isEmpty()) {
            Toast.makeText(
                this,
                applicationContext.getString(R.string.no_media_added_to_the_queue),
                Toast.LENGTH_LONG
            ).show()
        }
// Player
        player =
            ExoPlayer.Builder(this).setSeekBackIncrementMs(10000).setSeekForwardIncrementMs(10000)
                .setTrackSelector(adaptiveTrackSelection).build()

        player!!.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
        player!!.addListener(object : Player.Listener {

            override fun onTracksChanged(tracks: Tracks) {
                super.onTracksChanged(tracks)

            }

            override fun onLoadingChanged(isLoading: Boolean) {
                super.onLoadingChanged(isLoading)
                Log.i(
                    tag, "Loading changed. isLoading: $isLoading" + player!!.contentDuration
                )
                // Notify via custom listener
            }

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                super.onPlayerStateChanged(playWhenReady, playbackState)
                if (getCurrentItem()!!.singleCredit) {
                    isPlayingCreditAd = playWhenReady && playbackState == Player.STATE_READY
                    isPlaying = isPlayingCreditAd;
                } else {
                    isPlaying = playWhenReady && playbackState == Player.STATE_READY
                    isPlayingCreditAd = false;
                }
                if (listener != null) {
                    listener!!.playerStateChanged(playWhenReady, playbackState)
                }
                attachNotification()
                if (isPlaying) {
                    //Log.d("Play/pause Played", "Playing $secondsPlayed")
                    startDuration()
                    if (playerPaused) {
                        startDurationTimer()
                    } else if (!playerPaused && abs(player!!.currentPosition / 1000).toInt() >= 5) {
                        secondsPlayed = 0
                        playedseconds = 0
                        startDurationTimer()
                    } else {
                        startDurationTimer()
                    }
                    if (resumedFromLast && playerPaused) {
                        resumedFromLast = false
                        resumedFromLastAndPaused = true
                        //secondsPlayed = (player!!.currentPosition / 1000).toInt()
                    }
                } else if (playbackState == Player.STATE_ENDED) {
                    //Log.d("Playback:", "Ended!!!!")
                    if (playList[player!!.currentMediaItemIndex].singleCredit) {
                        Log.i("Player", "Ended")
                        APIMethods.addEvent(
                            applicationContext,
                            "watched",
                            playList[player!!.currentMediaItemIndex].postId,
                            "",
                            ""
                        )
                    }

                    if (playList[player!!.currentMediaItemIndex].isCategoryCoin) {
                        Log.i("Player", "Ended")
                        APIMethods.addEvent(
                            applicationContext,
                            "category_coin",
                            playList[player!!.previousMediaItemIndex].categoryId.toString(),
                            "",
                            ""
                        )
                    }
                    stopDurationTimer()
                    stopDuration(true)
                    playedseconds = 0
                    playerPaused = false
                } else {
                    //Log.d("Playback:", "Paused!!")
                    playerPaused = true
                    if (resumedFromLast) {
                        stopDurationTimer()
                        secondsPlayed = 0
                    } else if (resumedFromLastAndPaused) {
                        secondsPlayed = secondsPlayed
                        stopDurationTimer()

                        playedseconds = playedseconds
                        stopDuration(false)
                    } else {
                        stopDurationTimer()
                        secondsPlayed = (player!!.currentPosition / 1000).toInt()

                        stopDuration(false)
                        playedseconds = 0
                    }
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                stopDurationTimer()
                playerPaused = false
                if (listener != null) {
                    listener!!.playerError(error)
                    error.message?.let {
                        var message = applicationContext.getString(R.string.unable_to_play)
                        if (it.contains("drm") || error.cause is DrmSession.DrmSessionException) {
                            message = applicationContext.getString(R.string.item_skipped)
                        }
                        AlertUtils.showToast(message, 1, this@AudioPlayerService)
                    }
                    if (concatenatingMediaSource.size > 1) {
                        if (player!!.currentMediaItemIndex != C.INDEX_UNSET) {
                            playList.removeAt(player!!.currentMediaItemIndex)
                            concatenatingMediaSource.removeMediaSource(player!!.currentMediaItemIndex)
                            player!!.prepare(concatenatingMediaSource)
                            if (player!!.nextMediaItemIndex != C.INDEX_UNSET) {
                                player!!.seekTo(player!!.nextMediaItemIndex, 0)
                            }
                            player!!.playWhenReady = true
                        }
                    }
                }
            }
        })
        if (concatenatingMediaSource.size > 0) {
            val mediaSource: MediaSource = concatenatingMediaSource
            player!!.setMediaSource(mediaSource)
            player!!.prepare()
            if (index > -1) {
                player!!.seekTo(index, getResumePosition(index))
            }
            resumeFromLastPlayed()
            player!!.playWhenReady = play
            attachNotification()
            createSession()
        }
        FirebaseAnalyticsLogs.stopDurationStartUp(getCurrentItem()!!.postTitle)
    }
    private fun resumeFromLastPlayed() {
        val resumeDuration = getResumePosition()
        if (player != null) {
            player?.seekTo(resumeDuration)
            secondsPlayed = 0
            resumedFromLast = true
        }
    }

    var listRoomDatabase: ListRoomDatabase? = null

    private fun getResumePosition(position: Int? = null): Long {
        var d: Long = 0
        val current = if (position != null) getItem(position) else getCurrentItem()
        /*if (current?.postId == "7226") {
            return 0
        }*/
        if (current != null) {
            if (listRoomDatabase == null) {
                listRoomDatabase = ListRoomDatabase.getDatabase(this)
            }
            val result = listRoomDatabase?.mediaDAO()?.getMediaDurationData(current.postId)
            if (result != null && result.size > 0) {
                d = if (Utility.isMediaEnding(result[0].playedDuration, result[0].duration)) {
                    0
                } else {
                    result[0].playedDuration
                }
            }
        }
        if (d < 0) {
            d = 0
        }
        return d
    }

    fun setRewindForward(type: String, millis: Long) {
        val currentPosition = player!!.currentPosition
        val duration = player!!.duration
        val newPosition: Int
        when (type) {
            REWIND_PLAYBACK -> {
                newPosition = (currentPosition - millis).toInt()
                player!!.seekTo(if (newPosition >= 0) newPosition.toLong() else 0)
            }

            FORWARD_PLAYBACK -> {
                newPosition = (currentPosition + millis).toInt()
                player!!.seekTo(if (newPosition <= duration) newPosition.toLong() else duration)
            }
        }

    }

    private fun attachNotification() {
        /*playerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(
                this, getString(R.string.app_name), R.string.app_name, notification_id,
                MediaDescAdapter(this))
        playerNotificationManager!!.setNotificationListener(object : PlayerNotificationManager.NotificationListener {

            override fun onNotificationStarted(notificationId: Int, notification: Notification) {
                startForeground(notificationId, notification)
            }

            override fun onNotificationCancelled(notificationId: Int) {
                stopSelf()
            }

        })
        playerNotificationManager!!.setPlayer(player)*/
        Handler().postDelayed({
            if (player == null || playList.size == 0) {
                removePlayerNotification()
                return@postDelayed
            }
        }, 1000)


        // Custom notification
        // Get the layouts to use in the custom notification
        val notificationLayout = RemoteViews(packageName, R.layout.player_notification)

        // Actions
        setNotificationListeners(notificationLayout)
        val clickAction = Intent(this, HomeActivity::class.java)
        clickAction.action = PLAYER_ACTION_PREVIOUS
        val clickIntent = PendingIntent.getActivity(
            this, 0,
            clickAction, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        // you must create a notification channel for API 26 and Above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.app_name)
            val description = applicationContext.getString(R.string.iTap_media_streaming_channel)
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(name, name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        // Apply the layouts to the notification
        val notification = NotificationCompat.Builder(
            this,
            getString(R.string.app_name)
        )
            .setSmallIcon(R.drawable.play_icon_large)
            .setCustomContentView(notificationLayout)
            .setCustomBigContentView(notificationLayout)
            .setContentIntent(clickIntent)
            .setOngoing(true)
            .build()

        // Update notification
        getCurrentItem().let { feedContentData ->
            if (feedContentData != null) {
                notificationLayout.setTextViewText(
                    R.id.player_noti_title_lbl,
                    feedContentData.postTitle
                )
                notificationLayout.setTextViewText(
                    R.id.player_noti_sub_title_lbl,
                    feedContentData.attachmentData.description
                )

                val notificationTarget = NotificationTarget(
                    this, R.id.thumbnailImageView,
                    notificationLayout, notification, notification_id
                )
                val options = RequestOptions().placeholder(R.drawable.play_icon_large)
                Glide.with(applicationContext).asBitmap()
                    .apply(options)
                    .load(Uri.parse(feedContentData.imgUrl))
                    .into(notificationTarget)
            }
        }

        val res =
            if (isPlaying) R.drawable.exo_notification_pause else R.drawable.exo_notification_play
        notificationLayout.setImageViewResource(R.id.player_noti_play_btn, res)

        // Show notification
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notification_id, notification)
    }

    private fun setNotificationListeners(remoteView: RemoteViews) {
        // Previous action
        val previousActionIntent = Intent(this, AudioPlayerService::class.java)
        previousActionIntent.action = PLAYER_ACTION_PREVIOUS
        remoteView.setOnClickPendingIntent(
            R.id.player_noti_prev_btn,
            PendingIntent.getService(
                this, 0, previousActionIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
        )

        // Next action
        val nextActionIntent = Intent(this, AudioPlayerService::class.java)
        nextActionIntent.action = PLAYER_ACTION_NEXT
        remoteView.setOnClickPendingIntent(
            R.id.player_noti_next_btn,
            PendingIntent.getService(
                this, 0, nextActionIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
        )

        // Play/Pause action
        val playPauseActionIntent = Intent(this, AudioPlayerService::class.java)
        playPauseActionIntent.action = PLAYER_ACTION_PLAY_PAUSE
        remoteView.setOnClickPendingIntent(
            R.id.player_noti_play_btn,
            PendingIntent.getService(
                this, 0, playPauseActionIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
        )

        // Stop action
        val stopActionIntent = Intent(this, AudioPlayerService::class.java)
        stopActionIntent.action = PLAYER_ACTION_STOP
        remoteView.setOnClickPendingIntent(
            R.id.player_noti_close_btn,
            PendingIntent.getService(
                this, 0, stopActionIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
        )
    }

    private fun createSession() {
        mediaSession = MediaSessionCompat(this, "iTapMediaSession")
        mediaSession!!.isActive = true
        //playerNotificationManager!!.setMediaSessionToken(mediaSession!!.sessionToken)
        mediaSessionConnector = MediaSessionConnector(mediaSession!!)
        mediaSessionConnector!!.setQueueNavigator(object :
            TimelineQueueNavigator(mediaSession!!) {

            override fun getMediaDescription(
                player: Player,
                windowIndex: Int
            ): MediaDescriptionCompat {
                val mediaItem = playList[windowIndex]
                return MediaDescriptionCompat.Builder()
                    .setMediaId(windowIndex.toString())
                    .setTitle(mediaItem.mediaTitle)
                    .setDescription(mediaItem.mediaDesc)
                    .setIconUri(Uri.parse(mediaItem.mediaImageUrl))
                    .setMediaUri(Uri.parse(mediaItem.mediaUrl))
                    .build()
            }

        })
        mediaSessionConnector!!.setPlayer(player)
    }

    inner class MediaDescAdapter(context: Context) :
        PlayerNotificationManager.MediaDescriptionAdapter {

        private var mContext = context

        override fun createCurrentContentIntent(player: Player): PendingIntent? {
            val i = Intent(mContext, HomeActivity::class.java)
            return PendingIntent.getActivity(
                mContext,
                0,
                i,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
        }

        override fun getCurrentContentText(player: Player): String? {
            if (player.currentWindowIndex < playList.size) {
                return playList[player.currentWindowIndex].mediaDesc
            }
            return null
        }

        override fun getCurrentContentTitle(player: Player): String {
            if (player.currentWindowIndex < playList.size) {
                return playList[player.currentWindowIndex].mediaTitle
            }
            return ""
        }

        override fun getCurrentLargeIcon(
            player: Player,
            callback: PlayerNotificationManager.BitmapCallback
        ): Bitmap? {
            return BitmapFactory.decodeResource(mContext.resources, R.mipmap.ic_launcher_round)
        }

    }

    inner class LocalBinder : Binder() {

        val service: AudioPlayerService
            get() {
                return this@AudioPlayerService
            }

    }

    private val iBinder = LocalBinder()

    @Nullable
    override fun onBind(intent: Intent): IBinder? {
        return iBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Check for notification actions
        if (intent != null) {
            // Close player if stop action
            if (intent.action == PLAYER_ACTION_STOP) {
                cleanUp()
                removePlayerNotification()
                stopSelf()
                if (listener != null) {
                    listener!!.playerClosed()
                }
            }
            // Control player if player is not null and ad is not playing.
            if (!isPlayingAd && player != null) {
                if (intent.action == PLAYER_ACTION_PREVIOUS) {
                    if (player!!.previousWindowIndex != C.INDEX_UNSET) {
                        player!!.seekTo(player!!.previousWindowIndex, 0)
                    }
                } else if (intent.action == PLAYER_ACTION_NEXT) {
                    if (player!!.nextWindowIndex != C.INDEX_UNSET) {
                        player!!.seekTo(player!!.nextWindowIndex, 0)
                    }
                } else if (intent.action == PLAYER_ACTION_PLAY_PAUSE) {
                    player!!.playWhenReady = !isPlaying
                }
            }
        }
        // Service is not immediately destroyed and they can explicitly terminate our service when
        // we are finished with prepareAndPlay back
        return START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }

    override fun onDestroy() {
        cleanUp()
        super.onDestroy()
    }

    fun stopStreaming() {
        cleanUp()
    }

    private fun removePlayerNotification() {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notification_id)
    }

    fun cleanUp() {
        if (playerNotificationManager != null) {
            playerNotificationManager!!.setPlayer(null)
            playerNotificationManager = null
        }
        if (mediaSession != null) {
            mediaSession!!.release()
        }
        if (mediaSessionConnector != null) {
            mediaSessionConnector!!.setPlayer(null)
        }
        if (player != null) {
            player!!.stop()
            try {
                player!!.clearVideoSurface()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            player!!.release()
            player = null
        }
        isPlaying = false
        secondsPlayed = 0
        isPlayingCreditAd
        playList.clear()
        cleanUpAdPlayer()
        stopTimer()
        stopDuration(true)
        stopForeground(true)
        removePlayerNotification()
    }

// Ads handling

    fun startAd(@NonNull attachment: FeedContentData) {
        if (!playAd) return
        Log.i("ADS", "startAd")
        pauseMediaPlayer()
        APIMethods.addEvent(
            applicationContext,
            Constant.VIEW,
            attachment.postId,
            Constant.INVIDEO, attachment.postTitle
        )
        isPlayingCreditAd = false
        if (attachment.mediaType.equals(FeedContentData.MEDIA_TYPE_IMAGE)) {
            isPlayingImageAd = true
            isPlayingAd = true
            isPlayingVideoAd = false
        } else if (attachment.mediaType.equals(FeedContentData.MEDIA_TYPE_VIDEO)) {
            // Ad player
            val bandwidthMeter = DefaultBandwidthMeter.getSingletonInstance(this)

            val adaptiveTrackSelection = DefaultTrackSelector(this)
            val adDataSourceFactory = DefaultDataSourceFactory(
                this,
                Util.getUserAgent(this, getString(R.string.app_name)), bandwidthMeter
            )
            //val cacheDataSourceFactory = CacheDataSourceFactory(DownloadUtil.getCache(this), adDataSourceFactory)

            adPlayer =
                ExoPlayer.Builder(this).setTrackSelector(adaptiveTrackSelection).build()
            adPlayer!!.addListener(object : Player.Listener {

                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    super.onPlayerStateChanged(playWhenReady, playbackState)
                    isPlayingAd = playWhenReady && playbackState == Player.STATE_READY
                    isPlayingVideoAd = isPlayingAd
                    isPlayingImageAd = false
                    if (isPlayingAd) {
                        if (listener != null) {
                            listener!!.adStarted()
                        }
                    }
                    //removeNotification()
                    if (playbackState == Player.STATE_ENDED) {
                        Log.i("ADS", "ad played")
                        // Ad completed, resume playing
                        if (listener != null) {
                            listener!!.adCompleted()
                        }
                        resumeMediaPlayer()
                        //applicationContext.sendBroadcast(Intent(HomeActivity.AD_NOTIFICATION_BROADCAST_KEY))
                    }
                }

                override fun onPlayerError(error: PlaybackException) {
                    super.onPlayerError(error)
                    Log.i("ADS", "ad error: " + error?.localizedMessage)
                    if (listener != null) {
                        listener!!.adCompleted()
                    }
                    resumeMediaPlayer()
                }
            })

            // Data source
            val url =
                if (attachment.adFieldsData.attachment != null && attachment.adFieldsData.attachment.isNotEmpty()) {
                    attachment.adFieldsData.attachment
                } else {
                    attachment.externalUrl
                }
            if (url.isNotEmpty()) {
                Utility.urlFix(url)
                // TODO: - Pending drm implementation
                ExoUtil.buildMediaSource(
                    Uri.parse(url),
                    adDataSourceFactory, null
                )?.let { adMediaSource ->
                    adPlayer!!.prepare(adMediaSource)
                    adPlayer!!.playWhenReady = true
                }
            }
        }
    }

    fun pauseMediaPlayer() {
        Log.i("ADS", "pauseMedia")
        if (player != null) {
            player!!.playWhenReady = false
        }
    }

    fun resumeMediaPlayer() {
        Log.i("ADS", "resumeMedia")
        if (player != null) {
            player!!.playWhenReady = true
            //attachNotification()
        }
        cleanUpAdPlayer()
    }

    fun cleanUpAdPlayer() {
        if (adPlayer != null) {
            adPlayer!!.stop()
            adPlayer!!.clearVideoSurface()
            adPlayer!!.release()
            adPlayer = null
            isPlayingAd = false
            isPlayingVideoAd = false
        }
        isPlayingImageAd = false
    }

    // Timer
    private var timer: Timer? = null
    private var timerTask: TimerTask? = null


    private fun startTimer() {
        stopTimer()
        if (!isPlaying) return
        timerTask = object : TimerTask() {
            override fun run() {
                Log.i("ADS", "timerTick")
                Handler(Looper.getMainLooper()).post {
                    checkForAd()
                    getNextAd()
                }
            }
        }
        timer = Timer()
        timer!!.schedule(timerTask!!, Date(), 1000)
    }

    private fun stopTimer() {
        if (timer != null) {
            timer!!.cancel()
            timer!!.purge()
            timer = null
        }
        if (timerTask != null) {
            timerTask!!.cancel()
            timerTask = null
        }
    }

    private var secondsPlayedTimer: Timer? = null
    private var secondsPlayedTimerTask: TimerTask? = null
    private var playedSecondsTimer: Timer? = null
    private var secondsPlayedTask: TimerTask? = null

    private fun startDurationTimer() {
        secondsPlayedTimerTask = object : TimerTask() {
            override fun run() {
                secondsPlayed++
                // Log.i("Timer", "Seconds: $secondsPlayed")
                //if (secondsPlayed == 10) {
                //Change in durations and coins by Pathik on 17th June 2021
                if (getCurrentItem() != null && !getCurrentItem()!!.singleCredit && !getCurrentItem()!!.isCategoryCoin) {
                    if (secondsPlayed == 300) {
                        earnCoins(
                            MyApp.getAppContext(),
                            Integer.parseInt(getCurrentItem()!!.postId),
                            PLAYBACK_CREDIT_DESC,
                            FLAG_5_MINUTES,
                            COINS_5_MINUTES
                        )
                        { _, _, coins, _, creditedCoins, _, _ ->
                            Utility.CustomNotification(
                                MyApp.getAppContext(),
                                WON_COINS_DESC + creditedCoins + getString(R.string.label_for) + getCurrentItem()?.postTitle
                            )
                            false
                        }
                    } else if (secondsPlayed == 600) {
                        earnCoins(
                            MyApp.getAppContext(),
                            Integer.parseInt(getCurrentItem()!!.postId),
                            PLAYBACK_CREDIT_DESC,
                            FLAG_10_MINUTES,
                            COINS_10_MINUTES
                        )
                        { _, _, coins, _, creditedCoins, _, _ ->
                            Utility.CustomNotification(
                                MyApp.getAppContext(),
                                WON_COINS_DESC + creditedCoins + getString(R.string.label_for) + getCurrentItem()?.postTitle
                            )
                            false
                        }
                    } else if (secondsPlayed == 1200) {
                        //Log.i("Wohooo", "5 seconds completed and registered!!")
                        earnCoins(
                            MyApp.getAppContext(),
                            Integer.parseInt(getCurrentItem()!!.postId),
                            PLAYBACK_CREDIT_DESC,
                            FLAG_20_MINUTES,
                            COINS_20_MINUTES
                        )
                        { _, _, coins, _, creditedCoins, _, _ ->
                            Utility.CustomNotification(
                                MyApp.getAppContext(),
                                WON_COINS_DESC + creditedCoins + getString(R.string.label_for) + getCurrentItem()?.postTitle
                            )
                            false
                        }
                    } else if (secondsPlayed == 1800) {
                        earnCoins(
                            MyApp.getAppContext(),
                            Integer.parseInt(getCurrentItem()!!.postId),
                            PLAYBACK_CREDIT_DESC,
                            FLAG_30_MINUTES,
                            COINS_30_MINUTES
                        )
                        { _, _, coins, _, creditedCoins, _, _ ->
                            Utility.CustomNotification(
                                MyApp.getAppContext(),
                                WON_COINS_DESC + creditedCoins + getString(R.string.label_for) + getCurrentItem()?.postTitle
                            )
                            false
                        }
                    } else if (secondsPlayed == 2700) {
                        earnCoins(
                            MyApp.getAppContext(),
                            Integer.parseInt(getCurrentItem()!!.postId),
                            PLAYBACK_CREDIT_DESC,
                            FLAG_45_MINUTES,
                            COINS_45_MINUTES
                        )
                        { _, _, coins, _, creditedCoins, _, _ ->
                            Utility.CustomNotification(
                                MyApp.getAppContext(),
                                WON_COINS_DESC + creditedCoins + getString(R.string.label_for) + getCurrentItem()?.postTitle
                            )
                            false
                        }
                    } else if (secondsPlayed == 3600) {
                        earnCoins(
                            MyApp.getAppContext(),
                            Integer.parseInt(getCurrentItem()!!.postId),
                            PLAYBACK_CREDIT_DESC,
                            FLAG_60_MINUTES,
                            COINS_60_MINUTES
                        )
                        { _, _, coins, _, creditedCoins, _, _ ->
                            Utility.CustomNotification(
                                MyApp.getAppContext(),
                                WON_COINS_DESC + creditedCoins + getString(R.string.label_for) + getCurrentItem()?.postTitle
                            )
                            false
                        }
                    }
                }
            }
        }

        secondsPlayedTimer = Timer()
        secondsPlayedTimer!!.schedule(secondsPlayedTimerTask!!, Date(), 1000)
    }

    private fun stopDurationTimer() {
        if (secondsPlayedTimer != null) {
            secondsPlayedTimer!!.cancel()
            secondsPlayedTimer!!.purge()
            secondsPlayedTimer = null
        }
        if (secondsPlayedTimerTask != null) {
            secondsPlayedTimerTask!!.cancel()
            secondsPlayedTimerTask = null
        }
    }

    fun startDuration() {
        secondsPlayedTask = object : TimerTask() {
            override fun run() {
                if (isPlaying) {
                    if (playerPaused) {
                        playedseconds++
                        if (LocalStorage.getUserData() != null && AnalyticsTracker.locationData != null && AnalyticsTracker.deviceDetailsData != null) {
                            var analyticsData = AnalyticsData(
                                ACTIVITY_TYPE_PLAY_AUDIO,
                                getCurrentItem()!!.postId,
                                getCurrentItem()!!.postTitle,
                                getCurrentItem()!!.postType,
                                playedseconds.toString(),
                                AnalyticsTracker.locationData.latitude,
                                AnalyticsTracker.locationData.longitude,
                                AnalyticsTracker.locationData.city,
                                AnalyticsTracker.locationData.state,
                                AnalyticsTracker.deviceDetailsData.brand,
                                AnalyticsTracker.deviceDetailsData.deviceID,
                                AnalyticsTracker.deviceDetailsData.model,
                                AnalyticsTracker.deviceDetailsData.sdk,
                                AnalyticsTracker.deviceDetailsData.manufacture,
                                AnalyticsTracker.deviceDetailsData.user,
                                AnalyticsTracker.deviceDetailsData.type,
                                AnalyticsTracker.deviceDetailsData.base,
                                AnalyticsTracker.deviceDetailsData.incremental,
                                AnalyticsTracker.deviceDetailsData.board,
                                AnalyticsTracker.deviceDetailsData.host,
                                AnalyticsTracker.deviceDetailsData.fingerPrint,
                                AnalyticsTracker.deviceDetailsData.versionCode,
                                LocalStorage.getUserData().userId,
                                LocalStorage.getUserData().displayName,
                                LocalStorage.getUserData().mobile,
                                Utility.getCurrentDateTime()
                            )
                            LocalStorage.saveVideoAnalyticsData(analyticsData)
                        }
                    }
                }
            }
        }
        playedSecondsTimer = Timer()
        playedSecondsTimer!!.schedule(secondsPlayedTask!!, Date(), 1000)
    }

    fun stopDuration(record: Boolean) {
        var seconds: Int = playedseconds
        Log.i("Duration", seconds.toString())
        Log.i("Duration", record.toString())
        if (playerPaused) {
            Log.i("Duration", seconds.toString())
        } else if (!isPlaying) {
            Log.i("Duration", seconds.toString())
        }
        if (record) {
            logEvent()
            if (seconds != 0 && LocalStorage.getVideoAnalyticsData() != null) {
                var analyticsData = LocalStorage.getVideoAnalyticsData()
                listRoomDatabase!!.mediaDAO().saveAnalytics(analyticsData)
                LocalStorage.saveVideoAnalyticsData(null)
            }
        }
        if (playedSecondsTimer != null) {
            playedSecondsTimer!!.cancel()
            playedSecondsTimer!!.purge()
            playedSecondsTimer = null
        }
        if (secondsPlayedTask != null) {
            secondsPlayedTask!!.cancel()
            secondsPlayedTask = null
        }
    }

    private fun checkForAd() {
        //return
        if (!playAd) return
        if (player != null) {
            val index = player!!.currentWindowIndex
            val item = playList[index]
            val playedSeconds = (player!!.currentPosition / 1000).toInt()
            //for (attachmentData in item.adsArray) {
            item.adsArray.forEachIndexed { i, attachmentData ->
                if (attachmentData.adFieldsData.play_at == 0 && !attachmentData.adFieldsData.adPlayed) {
                    if (playedSeconds <= 5) {
                        Log.i(
                            "ADS",
                            "Starting Ad not played earlier, playing ad at pos: $playedSeconds"
                        )
                        attachmentData.adFieldsData.adPlayed = true
                        adIndex = i
                        startAd(attachmentData)
                        return
                    }
                }
                if (attachmentData.adFieldsData.play_at == playedSeconds) {
                    if (attachmentData.adFieldsData.adPlayed) return
                    Log.i("ADS", "Ad not played earlier, playing ad...")
                    attachmentData.adFieldsData.adPlayed = true
                    startAd(attachmentData)
                    return
                }
            }
        }
    }

    private fun getNextAd() {
        if (!playAd) return
        if (player != null) {
            val nextIndex = player!!.nextWindowIndex
            if (nextIndex != C.INDEX_UNSET) {
                if (nextIndex < playList.size) {
                    val nextItem = playList[nextIndex]
                    if (!nextItem.isAdLoaded) {
                        // Load next ad if the current item is about to end
                        val played = player!!.currentPosition / 1000
                        val duration = player!!.duration / 1000
                        if ((duration - played) == 10L) {
                            // load ad
                            Log.i("ADS", "loading next ad")
                            APIManager.getAdsForPost(nextItem.postId) {
                                Log.i("ADS", "next ad loaded")
                                nextItem.isAdLoaded = true
                                nextItem.adsArray.addAll(it)
                            }
                        }
                    }
                }
            }
        }
    }

    fun setPreviousNext(type: String) {
        when (type) {
            PLAYER_ACTION_PREVIOUS -> {
                if (player!!.previousWindowIndex != C.INDEX_UNSET) {
                    player!!.seekTo(player!!.previousWindowIndex, 0)
                }
            }

            PLAYER_ACTION_NEXT -> {
                if (player!!.nextWindowIndex != C.INDEX_UNSET) {
                    player!!.seekTo(player!!.nextWindowIndex, 0)
                }
            }
        }

    }

    fun logEvent() {
        if (player != null) {
            val duration = player!!.duration;
            val position = player!!.currentPosition;
            val percentageWatched = (100 * position / duration);
            if (percentageWatched > 75) {
                FirebaseAnalyticsLogs.logEvent()
                    .completionRates(getCurrentItem()!!.postTitle)
            }
        }
    }

    companion object {
        const val PLAYER_ACTION_PREVIOUS = "player_action_previous"
        const val PLAYER_ACTION_NEXT = "player_action_next"
        const val PLAYER_ACTION_PLAY_PAUSE = "player_action_play_pause"
        const val PLAYER_ACTION_STOP = "player_action_stop"
    }

}
