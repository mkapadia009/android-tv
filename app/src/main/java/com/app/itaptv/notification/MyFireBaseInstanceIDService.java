package com.app.itaptv.notification;

import androidx.annotation.NonNull;

import com.app.itaptv.utils.Constant;
import com.app.itaptv.utils.LocalStorage;
import com.google.firebase.messaging.FirebaseMessagingService;

/**
 * Created by Avinash on 4/09/18.
 */

public class MyFireBaseInstanceIDService extends FirebaseMessagingService {//FirebaseInstanceIdService {

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        LocalStorage.putValue(s, Constant.KEY_FCM_TOKEN);
    }
}
