package com.app.itaptv.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class MyService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public class LocalBinder extends Binder{
        public MyService getService() {
            return MyService.this;
        }
    }
    private IBinder iBinder = new LocalBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //throw new UnsupportedOperationException("Not Yet Implemented");
        return iBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler.post(periodicUpdate);
        return START_STICKY;
    }

    final Handler handler = new Handler();
    private Runnable periodicUpdate=new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(periodicUpdate,2000);
            sendBroadcast(new Intent("checkinternetconnection"));
        }
    };
}
