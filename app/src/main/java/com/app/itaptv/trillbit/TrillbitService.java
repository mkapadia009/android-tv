/*
package com.app.itap.trillbit;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import com.app.itap.R;
import com.app.itap.utils.Log;
import com.google.gson.Gson;

import org.json.JSONObject;

import sdk.trill.com.AudioRecListener;
import sdk.trill.com.TrillSDK;


public class TrillbitService extends Service {
    TrillSDK trillInstance;
    NotificationChannel channel;
    public static Boolean isRunning = false;
    final class TrillThread implements Runnable{
        int service_id;
        Context context;
        Handler handler = new Handler(Looper.getMainLooper());
        TrillThread(int id, Context cnt){
            this.service_id = id;
            this.context = cnt;
            createNotificationChannel();
        }

        @Override
        public void run() {
            initTrillbit();
        }

        private void initTrillbit() {
            Log.d("avinash","initTrillbit");

            if(trillInstance == null){
                trillInstance = new TrillSDK(context);
            }
            trillInstance.addListener( new AudioRecListener() {
                @Override
                public void onError(int STATUS) {
                    super.onError(STATUS);
                    Log.d("avinash", "Trill onError: STATUS = " + String.valueOf(STATUS));
                }

                @Override
                public void onStartReceiving(int STATUS) {
                    super.onStartReceiving(STATUS);
                    Log.e("avinash", "Trill onStartReceiving" + String.valueOf(STATUS));
                }

                @Override
                public void onReceived(int STATUS, JSONObject info) {
                    super.onReceived(STATUS, info);
                    Log.e("avinash", String.valueOf(STATUS) + " " + info.toString());
                      handler.post(new Runnable() {
                        @Override
                        public void run() {
//                            Toast.makeText(context,"Trill onReceived: STATUS = " + String.valueOf(info),Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(context, CouponActivity.class);
                            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                            CouponFragment coupon = new Gson().fromJson(String.valueOf(info), CouponFragment.class);
                            Log.d("avinash", String.valueOf(info));
                            String title = "iTap";
                            String desc = "Trillbit Content.";
                            if(coupon.getTitle() != null) {
                                title = coupon.getTitle();
                            }
                            if(coupon.getDescription() != null) {
                                desc = coupon.getDescription();
                            }

                           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                Log.d("avinash", "OS 26 > ");
                                Notification notification = new Notification.Builder(context)
                                        .setContentTitle(title)
                                        .setContentText(desc)
                                        .setSmallIcon(R.drawable.facebook)
                                        .setChannelId(String.valueOf(R.string.TRIL_Channel_ID))
                                        .build();
                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                                notificationManager.notify(1, notification);
                            }else {
                                Log.d("avinash", "OS < 26");
                                NotificationCompat.Builder b = new NotificationCompat.Builder(context);
                                b.setAutoCancel(true)
                                        .setWhen(System.currentTimeMillis())
                                        .setSmallIcon(R.drawable.trill_play_circle_outline_black_48dp)
                                        .setTicker("Hearty365")
                                        .setContentTitle(title)
                                        .setContentText(desc)
                                        .setDefaults(Notification.DEFAULT_LIGHTS| Notification.DEFAULT_SOUND)
                                        .setContentIntent(contentIntent)
                                        .setContentInfo("Info");
                                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                                notificationManager.notify(1, b.build());
                            }

                            Intent newIntent= new Intent(getBaseContext(), CouponActivity.class);
                            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            newIntent.putExtra("coupon", coupon);
                            getApplication().startActivity(newIntent);

                        }
                    });
                }
            });
            //trillInstance.start();
        }

        private void createNotificationChannel() {
            Log.d("avinash", "createNotificationChannel");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.d("avinash", "OS 26 > ");
                CharSequence name = getString(R.string.TRIL_Channel_Name);
                String description = getString(R.string.TRIL_Channel_Desc);
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                channel = new NotificationChannel(String.valueOf(R.string.TRIL_Channel_ID), name, importance);
                channel.setDescription(description);
                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }else{
                Log.d("avinash", "OS < 26");
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if(trillInstance == null){
            trillInstance = new TrillSDK(getApplicationContext());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("avinash", "onStartCommand");
        if(!TrillbitService.isRunning){
            Log.d("avinash", "TrillbitService not running created new thread.");
            Thread thread = new Thread(new TrillThread(startId, this));
            thread.setPriority(thread.getThreadGroup().getMaxPriority());
            thread.start();
            TrillbitService.isRunning = true;
//            Toast.makeText(getApplicationContext(), "Thread started...", Toast.LENGTH_LONG).show();
        }else{
//            Toast.makeText(getApplicationContext(), "Thread already started...", Toast.LENGTH_LONG).show();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("avinash", "onDestroy");
    }
}
*/
