package com.app.itaptv.trillbit;

import android.Manifest;

import androidx.appcompat.app.AppCompatActivity;

public class Constants extends AppCompatActivity {
    public static String[] neededPermissions = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE
    };

    public static String INTENT_FILTER_TRILL_BROADCASTER = "TRILL_BROADCASTER_RECEIVER";

}
