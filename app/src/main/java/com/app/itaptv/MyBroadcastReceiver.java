package com.app.itaptv;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.app.itaptv.utils.Utility;

import static com.app.itaptv.activity.BaseActivity.CHECK_INTERNET_CONNECTION;
import static com.app.itaptv.activity.BaseActivity.MANDATORY_APP_UPDATE;
import static com.app.itaptv.activity.HomeActivity.AD_NOTIFICATION_BROADCAST_KEY;
import static com.app.itaptv.activity.HomeActivity.DOWNLOADS_BROADCAST_KEY;
import static com.app.itaptv.activity.HomeActivity.EARN_COINS_KEY;
import static com.app.itaptv.activity.HomeActivity.NOTIFICATION_BROADCAST_KEY;

public class MyBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action) {
            case "myDownloadsBroadCast":
                Intent downIntent = new Intent();
                downIntent.setAction(DOWNLOADS_BROADCAST_KEY);
                context.sendBroadcast(downIntent);
                break;

            case "CRASH":
                Utility.showError(context.getString(R.string.unable_to_complete_operation),context);
                break;

            case "inappnotification":
                Intent notificationIntent = new Intent();
                notificationIntent.setAction(NOTIFICATION_BROADCAST_KEY);
                context.sendBroadcast(notificationIntent);
                break;

            case "adnotificationBroadCast":
                Intent adsNotificationIntent = new Intent();
                adsNotificationIntent.setAction(AD_NOTIFICATION_BROADCAST_KEY);
                context.sendBroadcast(adsNotificationIntent);
                break;

            case "mandatoryappupdate":
                Intent mandatoryAppUpdateIntent = new Intent();
                mandatoryAppUpdateIntent.setAction(MANDATORY_APP_UPDATE);
                context.sendBroadcast(mandatoryAppUpdateIntent);
                break;

            case "checkinternetconnection":
                Intent checkInternetConnectionIntent = new Intent();
                checkInternetConnectionIntent.setAction(CHECK_INTERNET_CONNECTION);
                context.sendBroadcast(checkInternetConnectionIntent);
                break;

            case "earncoins":
                Intent earnCoinsIntent = new Intent();
                earnCoinsIntent.setAction(EARN_COINS_KEY);
                context.sendBroadcast(earnCoinsIntent);
                break;
        }
    }

}