/*
package com.app.itap.trillbit;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.app.itap.R;
import com.app.itap.utils.Log;
import com.google.gson.Gson;

import org.json.JSONObject;

import sdk.trill.com.AudioRecListener;
import sdk.trill.com.TrillSDK;


public class TrillbitJobService extends JobService {

    NotificationChannel channel;
    public static Boolean isRunning = false;

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d("avinash", "onStartJob");

        if (channel == null) {
            createNotificationChannel();
        }

        Log.d("avinash", "JobService not running created new thread.");
        TrillThread thread = new TrillThread(getApplicationContext());
        thread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        TrillbitJobService.isRunning = true;
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d("avinash", "onStopJob");
        return true;
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
        } else {
            Log.d("avinash", "OS < 26");
        }
    }
}

class TrillThread extends AsyncTask<Void, Void, Void> {
    TrillSDK trillInstance;
    Context context;
    Handler handler = new Handler(Looper.getMainLooper());

    public TrillThread(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Log.d("avinash", "doInBackground");
        if (trillInstance == null) {
            Log.d("avinash", "trillInstance null; created new instance");
            trillInstance = new TrillSDK(context);
        }
        trillInstance.addListener(new AudioRecListener() {
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
                        if (coupon.getTitle() != null) {
                            title = coupon.getTitle();
                        }
                        if (coupon.getDescription() != null) {
                            desc = coupon.getDescription();
                        }

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            Log.d("avinash", "OS 26 > ");
                            Notification notification = new Notification.Builder(context)
                                    .setContentTitle(title)
                                    .setContentText(desc)
                                    .setSmallIcon(R.drawable.trill_play_circle_outline_black_48dp)
                                    .setChannelId(String.valueOf(R.string.TRIL_Channel_ID))
                                    .build();
                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                            notificationManager.notify(1, notification);
                        } else {
                            Log.d("avinash", "OS < 26");
                            NotificationCompat.Builder b = new NotificationCompat.Builder(context);
                            b.setAutoCancel(true)
                                    .setWhen(System.currentTimeMillis())
                                    .setSmallIcon(R.drawable.trill_play_circle_outline_black_48dp)
                                    .setTicker("Hearty365")
                                    .setContentTitle(title)
                                    .setContentText(desc)
                                    .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                                    .setContentIntent(contentIntent)
                                    .setContentInfo("Info");
                            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.notify(1, b.build());
                        }
                        Intent newIntent = new Intent(context, CouponActivity.class);
                        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        newIntent.putExtra("coupon", coupon);
                        context.startActivity(newIntent);
                    }
                });
            }
        });
        trillInstance.start();
        return null;
    }
}
*/
