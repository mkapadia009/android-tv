package com.app.itaptv.utils;

import android.os.Bundle;

import com.app.itaptv.MyApp;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class FirebaseAnalyticsLogs {

    private FirebaseAnalytics mFirebaseAnalytics;

    //Time for OTP
    public static Timer otpTimer = null;
    public static TimerTask otpTimerTask = null;
    public static int otpSec = 0;

    //Time for Register
    public static Timer registerTimer = null;
    public static TimerTask registerTask = null;
    public static int registerSec = 0;

    //Speed to startUp
    public static Timer startUpTimer = null;
    public static TimerTask startUpTask = null;
    public static int startUpSec = 0;


    public static FirebaseAnalyticsLogs logEvent() {
        return new FirebaseAnalyticsLogs();
    }

    public FirebaseAnalytics getInstance() {
        if (mFirebaseAnalytics == null) {
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(MyApp.getAppContext());
        }
        return mFirebaseAnalytics;
    }

    public void activeUser() {
        Bundle bundle = new Bundle();
        bundle.putString("userId", LocalStorage.getUserId());
        bundle.putString("source", Constant.ANDROIDTV);
        getInstance().logEvent("ActiveUsers", bundle);
    }

    public void registrationRate() {
        Bundle bundle = new Bundle();
        bundle.putString("userMobile", LocalStorage.getUserData().mobile);
        bundle.putString("source", Constant.ANDROIDTV);
        getInstance().logEvent("RegistrationRate", bundle);
    }

    public void completionRates(String title) {
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("userId", LocalStorage.getUserId());
        bundle.putString("source", Constant.ANDROIDTV);
        getInstance().logEvent("CompletionRates", bundle);
    }

    public static void startDurationOtp() {
        if (otpTimerTask == null && otpTimer == null) {
            otpTimerTask = new TimerTask() {
                @Override
                public void run() {
                    otpSec++;
                }
            };

            otpTimer = new Timer();
            otpTimer.schedule(otpTimerTask, new Date(), 1000);
        }
    }

    public static void stopDurationOtp(String mobile) {
        timeToOtp(mobile);
        otpSec = 0;
        if (otpTimer != null) {
            otpTimer.cancel();
            otpTimer.purge();
            otpTimer = null;
        }
        if (otpTimerTask != null) {
            otpTimerTask.cancel();
            otpTimerTask = null;
        }
    }

    public static void timeToOtp(String mobile) {
        Bundle bundle = new Bundle();
        bundle.putString("userMobile", mobile);
        bundle.putInt("duration", otpSec);
        bundle.putString("source", Constant.ANDROIDTV);
        FirebaseAnalytics.getInstance(MyApp.getAppContext()).logEvent("TimeToOpt", bundle);
    }

    public static void startDurationRegister() {
        if (registerTask == null && registerTimer == null) {
            registerTask = new TimerTask() {
                @Override
                public void run() {
                    registerSec++;
                }
            };

            registerTimer = new Timer();
            registerTimer.schedule(registerTask, new Date(), 1000);
        }
    }

    public static void stopDurationRegister() {
        timeToRegister();
        registerSec = 0;
        if (registerTimer != null) {
            registerTimer.cancel();
            registerTimer.purge();
            registerTimer = null;
        }
        if (registerTask != null) {
            registerTask.cancel();
            registerTask = null;
        }
    }

    public static void timeToRegister() {
        Bundle bundle = new Bundle();
        bundle.putString("userMobile", LocalStorage.getUserData().mobile);
        bundle.putInt("duration", registerSec);
        bundle.putString("source", Constant.ANDROIDTV);
        FirebaseAnalytics.getInstance(MyApp.getAppContext()).logEvent("TimeToRegister", bundle);
    }

    public void videoBitrate(String title, String bitrate) {
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("video_bitrate", bitrate);
        bundle.putString("source", Constant.ANDROIDTV);
        FirebaseAnalytics.getInstance(MyApp.getAppContext()).logEvent("VideoBitrate", bundle);
    }

    public void sessionDuration(String sec) {
        Log.i("firebaseevent", sec);
        Bundle bundle = new Bundle();
        bundle.putString("session_duration", sec);
        bundle.putString("source", Constant.ANDROIDTV);
        FirebaseAnalytics.getInstance(MyApp.getAppContext()).logEvent("SessionDuration", bundle);
    }


    public static void startDurationStartUp() {
        if (startUpTask == null && startUpTimer == null) {
            startUpTask = new TimerTask() {
                @Override
                public void run() {
                    startUpSec++;
                }
            };

            startUpTimer = new Timer();
            startUpTimer.schedule(startUpTask, new Date(), 1000);
        }
    }

    public static void stopDurationStartUp(String title) {
        speedToStartUp(title);
        startUpSec = 0;
        if (startUpTimer != null) {
            startUpTimer.cancel();
            startUpTimer.purge();
            startUpTimer = null;
        }
        if (startUpTask != null) {
            startUpTask.cancel();
            startUpTask = null;
        }
    }

    public static void speedToStartUp(String title) {
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putInt("duration", startUpSec);
        bundle.putString("source", Constant.ANDROIDTV);
        FirebaseAnalytics.getInstance(MyApp.getAppContext()).logEvent("SpeedToStartUp", bundle);
    }
}
