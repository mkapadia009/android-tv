package com.app.itaptv.custom_interface;

import androidx.annotation.Nullable;

import org.json.JSONArray;

/**
 * Created by poonam on 11/10/18.
 */

public interface WalletCallback {
    boolean onResult(boolean success, @Nullable String error, long coins,long diamonds,long creditedCoins, JSONArray historyData, int historyCount);
}
