package com.app.itaptv.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.app.itaptv.utils.Constant;
import com.app.itaptv.utils.Log;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

/**
 * BroadcastReceiver to wait for SMS messages. This can be registered either
 * in the AndroidManifest or at runtime.  Should filter Intents on
 * SmsRetriever.SMS_RETRIEVED_ACTION.
 */
public class SMSReceiver extends BroadcastReceiver {

    private static final String TAG = "SmsRetriever";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive");
        if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);
                if (status != null) {
                    switch (status.getStatusCode()) {
                        case CommonStatusCodes.SUCCESS:
                            // Get SMS message contents
                            String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
                            if (message != null) {
                                Log.i(TAG, "OTP Received: " + message);

                                // Clean up
                                message = message.replace("<#>", "");

                                // Extract one-time code from the message.
                                String otp = message.substring(13,19);

                                Intent smsIntent = new Intent();
                                smsIntent.putExtra("otpMessage", otp);
                                smsIntent.setAction(Constant.OTP_MESSAGE);
                                context.sendBroadcast(smsIntent);
                            }
                            break;

                        case CommonStatusCodes.TIMEOUT:
                            // Waiting for SMS timed out (5 minutes)
                            // Handle the error ...
                            Log.e(TAG, "Timed out. Retry sending SMS");
                            break;

                        default:
                            Log.i(TAG, "Failed to receive otp. Reason: Unknown");
                            break;
                    }
                } else {
                    Log.i(TAG, "Failed to receive otp. Reason: Status is null");
                }
            } else {
                Log.i(TAG, "Failed to receive otp. Reason: Bundle is null");
            }
        }
    }
}
