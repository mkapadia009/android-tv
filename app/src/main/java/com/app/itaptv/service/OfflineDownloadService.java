package com.app.itaptv.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.app.itaptv.MyApp;
import com.app.itaptv.R;
import com.google.android.exoplayer2.offline.Download;
import com.google.android.exoplayer2.offline.DownloadManager;
import com.google.android.exoplayer2.offline.DownloadService;
import com.google.android.exoplayer2.scheduler.PlatformScheduler;
import com.google.android.exoplayer2.scheduler.Scheduler;
import com.google.android.exoplayer2.ui.DownloadNotificationHelper;
import com.google.android.exoplayer2.util.NotificationUtil;
import com.google.android.exoplayer2.util.Util;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static com.app.itaptv.MyApp.DOWNLOAD_NOTIFICATION_CHANNEL_ID;
import static com.app.itaptv.MyApp.getAppContext;
import static com.app.itaptv.activity.HomeActivity.DOWNLOADING_BROADCAST_KEY;
import static com.app.itaptv.activity.HomeActivity.IS_DOWNLOADED;
import static com.app.itaptv.activity.HomeActivity.IS_DOWNLOADING;
import static com.app.itaptv.activity.HomeActivity.IS_DOWNLOAD_FAILED;

public class OfflineDownloadService extends DownloadService {

    private static final int JOB_ID = 1607;
    private static final int FOREGROUND_NOTIFICATION_ID = 1607;

    public OfflineDownloadService() {
        super(FOREGROUND_NOTIFICATION_ID, DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL,
                DOWNLOAD_NOTIFICATION_CHANNEL_ID,
                R.string.exo_download_notification_channel_name, 0);
    }

    @NotNull
    @Override
    protected DownloadManager getDownloadManager() {
        MyApp application = (MyApp) getApplication();
        DownloadManager downloadManager = application.getDownloadManager();
        DownloadNotificationHelper downloadNotificationHelper =
                application.getDownloadNotificationHelper();
        downloadManager.addListener(
                new TerminalStateNotificationHelper(
                        this, downloadNotificationHelper,
                        FOREGROUND_NOTIFICATION_ID + 1));
        return downloadManager;
    }

    @Nullable
    @Override
    protected Scheduler getScheduler() {
        return Util.SDK_INT >= 21 ? new PlatformScheduler(MyApp.sharedContext(), JOB_ID) : null;
    }

    @NotNull
    @Override
    protected Notification getForegroundNotification(List<Download> downloads, int notMetRequirements) {
        return ((MyApp) getApplication())
                .getDownloadNotificationHelper()
                .buildProgressNotification(getAppContext(),R.drawable.ic_download,null,null,downloads);
    }

    /**
     * Creates and displays notifications for downloads when they complete or fail.
     *
     * <p>This helper will outlive the lifespan of a single instance of {@link OfflineDownloadService}.
     * It is static to avoid leaking the first {@link OfflineDownloadService} instance.
     */
    private static final class TerminalStateNotificationHelper implements DownloadManager.Listener {

        private final Context context;
        private final DownloadNotificationHelper notificationHelper;

        private int nextNotificationId;

        public TerminalStateNotificationHelper(
                Context context, DownloadNotificationHelper notificationHelper, int firstNotificationId) {
            this.context = context.getApplicationContext();
            this.notificationHelper = notificationHelper;
            nextNotificationId = firstNotificationId;
        }

        @Override
        public void onDownloadChanged(DownloadManager downloadManager, Download download, @Nullable Exception finalException) {
            DownloadManager.Listener.super.onDownloadChanged(downloadManager, download, finalException);
            Notification notification;
            String title = null;
            try {
                JSONObject jsonObject = new JSONObject(Util.fromUtf8Bytes(download.request.data));
                title = jsonObject.getString("title");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (download.state == Download.STATE_QUEUED) {
                Toast.makeText(context, context.getString(R.string.downloading), Toast.LENGTH_LONG).show();
            }

            if (download.state == Download.STATE_DOWNLOADING) {
                sendLocalBroadcastStates(context, IS_DOWNLOADING);
            }

            if (download.state == Download.STATE_REMOVING || download.state == Download.FAILURE_REASON_NONE ||
                    download.state == Download.FAILURE_REASON_UNKNOWN) {
                sendLocalBroadcastStates(context, IS_DOWNLOAD_FAILED);
            }

            if (download.state == Download.STATE_COMPLETED) {

                Toast.makeText(context, context.getString(R.string.download_finished), Toast.LENGTH_SHORT).show();
                sendLocalBroadcastStates(context, IS_DOWNLOADED);

                Intent i = new Intent("myDownloadsBroadCast");
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, i, 0 | PendingIntent.FLAG_MUTABLE);

                notification = notificationHelper.buildDownloadCompletedNotification(getAppContext(), R.drawable.ic_download_done, pendingIntent, title);

                notification.flags |= Notification.FLAG_AUTO_CANCEL;
                notification.contentIntent = pendingIntent;
            } else if (download.state == Download.STATE_FAILED) {
                sendLocalBroadcastStates(context, IS_DOWNLOAD_FAILED);

                notification = notificationHelper.buildDownloadFailedNotification(getAppContext(),
                        R.drawable.ic_error, null, title);
            } else {
                return;
            }
            NotificationUtil.setNotification(context, nextNotificationId++, notification);
        }
    }

    /**
     * Send local broadcast to homeActivity for handling player download button
     *
     * @param context
     * @param extraString
     */
    private static void sendLocalBroadcastStates(Context context, String extraString) {
        Intent downloadInf = new Intent(DOWNLOADING_BROADCAST_KEY);
        downloadInf.putExtra(extraString, true);
        LocalBroadcastManager.getInstance(context).sendBroadcast(downloadInf);
    }
}
