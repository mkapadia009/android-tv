package com.app.itaptv.notification;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.app.itaptv.R;
import com.app.itaptv.activity.HomeActivity;
import com.app.itaptv.utils.Constant;
import com.app.itaptv.utils.LocalStorage;
import com.app.itaptv.utils.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by poonam on 21/5/18.
 */

public class MyFireBaseMessagingService extends FirebaseMessagingService {

    public static int NOTIFICATION_ID = 0;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        sendNotification(remoteMessage);
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        LocalStorage.putValue(s, Constant.KEY_FCM_TOKEN);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void sendNotification(RemoteMessage remoteMessage) {
        try {
            Log.e("Notification", remoteMessage.getData().toString());

            NOTIFICATION_ID = (int) System.currentTimeMillis();

            Map<String, String> params = remoteMessage.getData();
            JSONObject jsonObject_data = new JSONObject(params);

            JSONObject data = null;

            String title = "";
            String message = "";
            String page = "";
            String imageUrl = "";
            Bitmap bitmap = null;
            if (jsonObject_data.has("data")) {
                data = new JSONObject(jsonObject_data.getString("data"));
                //  imageUrl = data.has("imgUrl") ? data.getString("imgUrl") : "";
                //  bitmap = Utility.getBitmapFromURL(imageUrl);
            }
            if (jsonObject_data.has("title")) {
                title = jsonObject_data.getString("title");
            }else {
                title=remoteMessage.getNotification().getTitle();
            }
            if (jsonObject_data.has("message")) {
                message = jsonObject_data.getString("message");
            }else {
                message=remoteMessage.getNotification().getBody();
            }
            if (jsonObject_data.has("page")) {
                page = jsonObject_data.getString("page");
            }
          /*  FutureTarget<Bitmap> futureTarget = Glide.with(this)
                    .asBitmap()
                    .load(imageUrl)
                    .submit();

            Bitmap bitmap = futureTarget.get();

            Glide.with(this).clear(futureTarget);*/
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            if (data != null) {
                intent.putExtra(Constant.NOTIFICATION_DATA, data.toString());
                intent.setAction(data.has("page") ? data.getString("page") : "");
            }else{
                intent.setAction(page);
                Constant.NOTIFICATION_PAGE=page;
            }
            PendingIntent pendingIntent = PendingIntent.getActivity(this, NOTIFICATION_ID, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_MUTABLE);
            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

            String CHANNEL_ID = "my_channel_01";
            NotificationCompat.Builder notificationBuilder;
            if (bitmap == null) {
                notificationBuilder = new NotificationCompat.Builder(this, title)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.notification))
                        .setSmallIcon(R.drawable.notification)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setChannelId(CHANNEL_ID)
                        .setGroup(title)
                        .setStyle(inboxStyle)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setContentIntent(pendingIntent);
            } else {
                notificationBuilder = new NotificationCompat.Builder(this, title)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.notification))
                        .setSmallIcon(R.drawable.notification)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setStyle(new NotificationCompat.BigPictureStyle()
                                .setSummaryText(message)
                                .bigPicture(bitmap))
                        .setGroup(title)
                        .setStyle(inboxStyle)
                        .setChannelId(CHANNEL_ID)
                        .setAutoCancel(true)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setContentIntent(pendingIntent);
            }

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            CharSequence name = "iTap";// The user-visible name of the channel.
            NotificationChannel mChannel = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                if (notificationManager != null) {
                    notificationManager.createNotificationChannel(mChannel);
                }
            }
            if (notificationManager != null) {
                notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
