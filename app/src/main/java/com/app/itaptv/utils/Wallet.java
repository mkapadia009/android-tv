package com.app.itaptv.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.android.volley.Request;
import com.app.itaptv.API.APIManager;
import com.app.itaptv.API.APIRequest;
import com.app.itaptv.API.APIResponse;
import com.app.itaptv.API.Url;
import com.app.itaptv.MyApp;
import com.app.itaptv.R;
import com.app.itaptv.activity.HomeActivity;
import com.app.itaptv.custom_interface.WalletCallback;
import com.app.itaptv.roomDatabase.Converters;
import com.shephertz.app42.paas.sdk.android.App42API;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.storage.Query;
import com.shephertz.app42.paas.sdk.android.storage.QueryBuilder;
import com.shephertz.app42.paas.sdk.android.storage.Storage;
import com.shephertz.app42.paas.sdk.android.storage.StorageService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by poonam on 10/10/18.
 */

public class Wallet {

    public static String ERROR_MESSAGE_NOT_FOUND = "Not Found";
    public static String TAG = "WALLET";
    public static final int ACTION_ADD = 1;
    public static final int ACTION_REDEEM = 2;
    public static final String COLLECTION_NAME = "TransactionHistory";
    public static final String FLAG_QUESTION = "question";
    public static final String FLAG_PLAYBACK = "playback";
    public static final String FLAG_TRIVIA_GAME = "trivia";
    public static final String FLAG_LIVE_GAME = "live";
    public static final String FLAG_LIVE_PRACTICE_GAME = "trivia_practice";
    public static final String FLAG_MEDIA_QUESTION = "playback_question";
    public static final String FLAG_COUPON = "coupon";
    public static final String FLAG_BUY = "buy";
    public static final String FLAG_REGISTER = "register";
    public static final String FLAG_DAILY_CHECK_IN = "daily_check_in";
    public static final String FLAG_SOCIAL_SHARE = "social_share";
    public static final String FLAG_REFER = "refer";
    public static final String FLAG_WIN_CASH_TIMEOUT = "win_cash_timeout";
    public static final long FLAG_WIN_CASH_REFUND_DEFAULT_COINS = 1000;
    public static final String FLAG_5_MINUTES = "5_minutes";
    public static final long COINS_5_MINUTES = 5;
    public static final String FLAG_10_MINUTES = "10_minutes";
    public static final long COINS_10_MINUTES = 10;
    public static final String FLAG_20_MINUTES = "20_minutes";
    public static final long COINS_20_MINUTES = 30;
    public static final String FLAG_30_MINUTES = "30_minutes";
    public static final long COINS_30_MINUTES = 60;
    public static final String FLAG_45_MINUTES = "45_minutes";
    public static final long COINS_45_MINUTES = 100;
    public static final String FLAG_60_MINUTES = "60_minutes";
    public static final long COINS_60_MINUTES = 150;
    public static final String PLAYBACK_CREDIT_DESC = "Playback crossed a checkpoint";
    public static final String WON_COINS_DESC = MyApp.getAppContext().getString(R.string.you_have_won);

    public static void earnCoins(Context context, int postId, String description, String flag, long coins, WalletCallback walletCallback) {
        String userId = LocalStorage.getUserId();
        String randomOrderId = UUID.randomUUID().toString();
        try {
            Map<String, String> param = new HashMap<>();
            Map<String, String> params = new HashMap<>();
            params.put("user_id", userId);
            params.put("order_id", randomOrderId);
            params.put("entity_id", String.valueOf(postId));
            params.put("desc", description);
            params.put("entity_type", flag);
            params.put("coins", String.valueOf(coins));

            String data = Converters.jsonformat(params);
            String encryptedParams = Utility.openssl_encrypt(data, Constant.SECRET_KEY_ENCRYPT, Constant.IV_PARAMETER_ENCRYPT);

            APIRequest apiRequest = new APIRequest(Url.CREDIT_COINS, Request.Method.POST, param, null, context);
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, encryptedParams, (response, error, headers, statusCode) -> {
                try {
                    if (error != null) {
                        //AlertUtils.showToast(error.getMessage(), 1, context);
                    } else {
                        if (response != null) {
                            Log.e("response", response);
                            JSONObject jsonObjectResponse = new JSONObject(response);
                            String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                            if (type.equalsIgnoreCase("error")) {
                                String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                                //AlertUtils.showToast(message, 1, context);
                            } else if (type.equalsIgnoreCase("ok")) {
                                JSONObject jsonObject = jsonObjectResponse.has("msg") ? jsonObjectResponse.getJSONObject("msg") : new JSONObject();
                                //String message = jsonObject.has("message") ? jsonObject.getString("message") : APIManager.GENERIC_API_ERROR_MESSAGE;
                                long coinBalance = jsonObject.has("balance") ? (long) jsonObject.getInt("balance") : 0;
                                long creditedCoins = jsonObject.has("coins") ? (long) jsonObject.getInt("coins") : 0;
                                long diamonds = jsonObject.has("diamonds") ? (long) jsonObject.getInt("diamonds") : 0;
                                new Handler(Looper.getMainLooper()).post(() -> {
                                    if (walletCallback != null) {
                                        walletCallback.onResult(true, null, coinBalance, diamonds, creditedCoins, null, -1);
                                    }
                                    context.sendBroadcast(new Intent(HomeActivity.EARN_COINS_KEY).putExtra(HomeActivity.EARN_COINS_KEY, coinBalance));
                                });
                                //AlertUtils.showToast(message, 1, context);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void redeemCoins(Context context, int couponId, String description, String flag, long coins, WalletCallback walletCallback) {
        String userId = LocalStorage.getUserId();
        String randomOrderId = UUID.randomUUID().toString();
        try {
            Map<String, String> param = new HashMap<>();
            Map<String, String> params = new HashMap<>();
            params.put("user_id", userId);
            params.put("coins", String.valueOf(coins));
            params.put("desc", description);
            params.put("order_id", randomOrderId);
            params.put("entity_id", String.valueOf(couponId));
            params.put("entity_type", flag);

            String data = Converters.jsonformat(params);
            String encryptedParams = Utility.openssl_encrypt(data, Constant.SECRET_KEY_ENCRYPT, Constant.IV_PARAMETER_ENCRYPT);
            APIRequest apiRequest = new APIRequest(Url.DEBIT_COINS, Request.Method.POST, param, null, context);
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, encryptedParams, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    try {
                        if (error != null) {
                            //AlertUtils.showToast(error.getMessage(), 1, context);
                        } else {
                            if (response != null) {
                                Log.e("response", response);
                                JSONObject jsonObjectResponse = new JSONObject(response);
                                String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                                if (type.equalsIgnoreCase("error")) {
                                    String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                                    //AlertUtils.showToast(message, 1, context);
                                } else if (type.equalsIgnoreCase("ok")) {
                                    JSONObject jsonObject = jsonObjectResponse.has("msg") ? jsonObjectResponse.getJSONObject("msg") : new JSONObject();
                                    //String message = jsonObject.has("message") ? jsonObject.getString("message") : APIManager.GENERIC_API_ERROR_MESSAGE;
                                    long coinBalance = jsonObject.has("balance") ? (long) jsonObject.getInt("balance") : 0;
                                    long diamonds = jsonObject.has("diamonds") ? (long) jsonObject.getInt("diamonds") : 0;
                                    new Handler(Looper.getMainLooper()).post(() -> {
                                        if (walletCallback != null) {
                                            walletCallback.onResult(true, null, coinBalance, diamonds, 0, null, -1);
                                        }
                                    });
                                    context.sendBroadcast(new Intent(HomeActivity.EARN_COINS_KEY).putExtra(HomeActivity.EARN_COINS_KEY, coinBalance));
                                    //AlertUtils.showToast(message, 1, context);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*public static void redeemCoins(int couponId, String description, String flag, long coins, WalletCallback walletCallback) {
        Long currentBalance = LocalStorage.getValue("finalWalletBalance", 0, Long.class);
        if (currentBalance > 0 || !(currentBalance >= coins)) {
            try {
                String encodedRewardText = URLEncoder.encode(description, "UTF-8");
                String userName = LocalStorage.getUserId();
                JSONObject metaData = generateCoinJSON(ACTION_REDEEM, userName, couponId, encodedRewardText, flag, coins);
                BigDecimal redeemCoins = new BigDecimal(coins);

                ScoreBoardService scoreBoardService = App42API.buildScoreBoardService();
                scoreBoardService.addJSONObject(COLLECTION_NAME, metaData);
                scoreBoardService.deductScore(MyApp.GAME_NAME, userName, "Coins redeemed", redeemCoins, new App42CallBack() {
                    @Override
                    public void onSuccess(Object o) {
                        Log.i(TAG, "Success " + o.toString());
                        boolean success;
                        long walletBalance = 0L;
                        try {
                            JSONObject jsonObject = new JSONObject(o.toString());
                            JSONObject jsonObjectResponse = jsonObject.getJSONObject("app42").getJSONObject("response");
                            success = jsonObjectResponse.getBoolean("success");
                            if (success) {
                                walletBalance = jsonObjectResponse.getJSONObject("games")
                                        .getJSONObject("game")
                                        .getJSONObject("scores")
                                        .getJSONObject("score")
                                        .getLong("value");
                            }

                            boolean finalSuccess = success;
                            long finalWalletBalance = walletBalance;
                            new Handler(Looper.getMainLooper()).post(() -> {
                                if (walletCallback != null) {
                                    walletCallback.onResult(finalSuccess, null, finalWalletBalance, null, -1);
                                }
                            });
                        } catch (JSONException e) {
                            handleError(false, e, walletCallback, walletBalance);
                        }
                    }

                    @Override
                    public void onException(Exception e) {
                        Log.e(TAG, "Exception " + e.getMessage());
                        handleError(true, e, walletCallback, 0L);
                    }
                });
            } catch (UnsupportedEncodingException e) {

            }
        }
    }*/

/*
    public static void earnCoins(int questionId, String description, String flag, long coins, WalletCallback walletCallback) {

    /*
     * Wallet Interaction from AppSide Old Functionality...
     *//*
        String userName = LocalStorage.getUserId();
        JSONObject metaData = generateCoinJSON(ACTION_ADD, userName, questionId, description, flag, coins);
        BigDecimal addCoins = new BigDecimal(coins);

        ScoreBoardService scoreBoardService = App42API.buildScoreBoardService();
        scoreBoardService.addJSONObject(COLLECTION_NAME, metaData);
        scoreBoardService.addScore(MyApp.GAME_NAME, userName, "Coins added", addCoins, new App42CallBack() {
            @Override
            public void onSuccess(Object o) {
                Log.i(TAG, "Success " + o.toString());
                boolean success;
                try {
                    JSONObject jsonObject = new JSONObject(o.toString());
                    success = jsonObject.getJSONObject("app42").getJSONObject("response").getBoolean("success");
                    boolean finalSuccess = success;
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (walletCallback != null) {
                            walletCallback.onResult(finalSuccess, null, coins, null, -1);
                        }
                    });
                } catch (JSONException e) {
                    handleError(false, e, walletCallback, coins);
                }
            }

            @Override
            public void onException(Exception e) {
                Log.e(TAG, "Exception " + e.getMessage());
                handleError(true, e, walletCallback, 0L);
            }
        });
    }*/

    public static void getWalletBalance(Context context, WalletCallback walletCallback) {
        String userName = LocalStorage.getUserId();
        /*App42API.buildScoreBoardService().getLastScoreByUser(MyApp.GAME_NAME, userName, new App42CallBack() {
            @Override
            public void onSuccess(Object o) {
                Log.i(TAG, "Success " + o.toString());
                boolean success;
                long walletBalance = 0L;
                try {
                    JSONObject jsonObject = new JSONObject(o.toString());
                    JSONObject jsonObjectResponse = jsonObject.getJSONObject("app42").getJSONObject("response");
                    success = jsonObjectResponse.getBoolean("success");
                    if (success) {
                        walletBalance = jsonObjectResponse.getJSONObject("games")
                                .getJSONObject("game")
                                .getJSONObject("scores")
                                .getJSONObject("score")
                                .getLong("value");
                    }
                    boolean finalSuccess = success;
                    long finalWalletBalance = walletBalance;
                    LocalStorage.putValue(finalWalletBalance, "finalWalletBalance");
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (walletCallback != null) {
                            walletCallback.onResult(finalSuccess, null, finalWalletBalance, null, -1);
                        }
                    });
                } catch (JSONException e) {
                    handleError(false, e, walletCallback, walletBalance);
                }
            }

            @Override
            public void onException(Exception e) {
                Log.e(TAG, "Exception " + e.getMessage());
                handleError(true, e, walletCallback, 0L);
            }
        });*/

        try {
            Map<String, String> params = new HashMap<>();
            APIRequest apiRequest = new APIRequest(Url.GET_WALLET_BALANCE, Request.Method.GET, params, null, context);
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, (response, error, headers, statusCode) -> {
                try {
                    if (error != null) {
                        //AlertUtils.showToast(error.getMessage(), 1, context);
                    } else {
                        if (response != null) {
                            Log.e("response", response);
                            JSONObject jsonObjectResponse = new JSONObject(response);
                            String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                            if (type.equalsIgnoreCase("error")) {
                                String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                                //AlertUtils.showToast(message, 1, context);
                            } else if (type.equalsIgnoreCase("ok")) {
                                JSONObject jsonObject = jsonObjectResponse.has("msg") ? jsonObjectResponse.getJSONObject("msg") : new JSONObject();
                                //String message = jsonObject.has("message") ? jsonObject.getString("message") : APIManager.GENERIC_API_ERROR_MESSAGE;
                                long coinBalance = jsonObject.has("balance") ? (long) jsonObject.getInt("balance") : 0;
                                long diamonds = jsonObject.has("diamonds") ? (long) jsonObject.getInt("diamonds") : 0;
                                new Handler(Looper.getMainLooper()).post(() -> {
                                    if (walletCallback != null) {
                                        walletCallback.onResult(true, null, coinBalance, diamonds, 0, null, -1);
                                    }
                                });
                                //AlertUtils.showToast(message, 1, context);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getTransactionHistory(Context context, int actionType, int nextPageNo, int maxRecord, WalletCallback walletCallback) {
        /*String userName = LocalStorage.getUserId();
        StorageService storage = App42API.buildStorageService();
        Query query1 = QueryBuilder.build("userName", userName, QueryBuilder.Operator.EQUALS);
        Query query2 = QueryBuilder.build("actionType", actionType, QueryBuilder.Operator.EQUALS);
        Query finalQuery = QueryBuilder.compoundOperator(query1, QueryBuilder.Operator.AND, query2);

        // Data by Paging
        storage.findDocumentsByQueryWithPaging(MyApp.GAME_NAME, COLLECTION_NAME, finalQuery, maxRecord, offset, new App42CallBack() {
            @Override
            public void onSuccess(Object response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.toString());
                    JSONObject jsonObjectResponse = jsonObject.getJSONObject("app42").getJSONObject("response");
                    boolean success = jsonObjectResponse.getBoolean("success");
                    JSONArray jsonDoc = jsonObjectResponse.getJSONObject("storage").getJSONArray("jsonDoc");

                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (walletCallback != null) {
                            walletCallback.onResult(success, null, 0, jsonDoc, -1);
                        }
                    });
                } catch (JSONException e) {
                    handleError(false, e, walletCallback, 0);
                }
            }

            @Override
            public void onException(Exception e) {
                handleError(true, e, walletCallback, 0);
            }
        });*/


        try {
            Map<String, String> params = new HashMap<>();
            APIRequest apiRequest = new APIRequest(Url.GET_ICOINS_HISTORY + "&limit=" + maxRecord + "&page_no=" + nextPageNo + "&transaction_type=" + actionType, Request.Method.GET, params, null, context);
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, (response, error, headers, statusCode) -> {
                try {
                    if (error != null) {
                        //AlertUtils.showToast(error.getMessage(), 1, context);
                        handleError(error.getMessage(), walletCallback, 0);
                    } else {
                        if (response != null) {
                            Log.e("response", response);
                            JSONObject jsonObjectResponse = new JSONObject(response);
                            String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                            if (type.equalsIgnoreCase("error")) {
                                String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                                //AlertUtils.showToast(message, 1, context);
                                handleError(message, walletCallback, 0);
                            } else if (type.equalsIgnoreCase("ok")) {
                                JSONObject jsonObject = jsonObjectResponse.has("msg") ? jsonObjectResponse.getJSONObject("msg") : new JSONObject();
                                JSONArray jsonArray = jsonObject.has("items") ? jsonObject.getJSONArray("items") : new JSONArray();
                                int nextPage = jsonObject.has("next_page") ? jsonObject.getInt("next_page") : 0;
                                String totalCount = jsonObjectResponse.has("total_count") ? jsonObjectResponse.getString("total_count") : "";
                                new Handler(Looper.getMainLooper()).post(() -> {
                                    if (walletCallback != null) {
                                        if (jsonArray.length() < 1) {
                                            handleError("", walletCallback, 0);
                                        } else {
                                            walletCallback.onResult(true, null, 0, 0, 0, jsonArray, nextPage);
                                        }
                                    }
                                });
                                //AlertUtils.showToast(message, 1, context);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    handleError(false, e, walletCallback, 0);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            handleError(false, e, walletCallback, 0);
        }

    }

    public static void getHistoryCount(int actionType, WalletCallback walletCallback) {
        String userName = LocalStorage.getUserId();
        StorageService storage = App42API.buildStorageService();
        Query query1 = QueryBuilder.build("userName", userName, QueryBuilder.Operator.EQUALS);
        Query query2 = QueryBuilder.build("actionType", actionType, QueryBuilder.Operator.EQUALS);
        Query finalQuery = QueryBuilder.compoundOperator(query1, QueryBuilder.Operator.AND, query2);

        // Data by Paging
        storage.findDocumentsByQuery(MyApp.GAME_NAME, COLLECTION_NAME, finalQuery, new App42CallBack() {
            @Override
            public void onSuccess(Object response) {
                Storage storageData = (Storage) response;
                int historyCount = 0;
                try {
                    JSONObject jsonObject = new JSONObject(response.toString());
                    String app42 = jsonObject.getString("app42");
                    JSONObject jsonObject2 = new JSONObject(app42);
                    String responsee = jsonObject2.getString("response");
                    JSONObject jsonObject3 = new JSONObject(responsee);
                    String storage = jsonObject3.getString("storage");
                    JSONObject jsonObject4 = new JSONObject(storage);
                    String recordCount = jsonObject4.getString("recordCount");
                    historyCount = Integer.parseInt(recordCount);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                int finalHistoryCount = historyCount;
                new Handler(Looper.getMainLooper()).post(() -> {

                    if (walletCallback != null) {

                        walletCallback.onResult(storageData.isResponseSuccess(), null, 0, 0, 0, null, finalHistoryCount);
                    }
                });
            }

            /**
             * Old Implementation
             */
                /*int historyCount = storageData.getRecordCount();
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (walletCallback != null) {
                        walletCallback.onResult(storageData.isResponseSuccess(), null, 0, null, historyCount);
                    }
                });*/
            @Override
            public void onException(Exception e) {
                handleError(true, e, walletCallback, 0);
            }
        });
    }

    /**
     * This method handles error
     *
     * @param isApp42Error   true if error occurs in App42 API else false (E.g. JSONException)
     * @param e              Exception object to get message and display in Alert Dialog
     * @param walletCallback Instance of WalletCallback interface
     * @param coins          Number of coins earned or redeemed
     */
    private static void handleError(boolean isApp42Error, Exception e, WalletCallback walletCallback, long coins) {
        String error = e.getMessage();
        if (error != null) {
            if (isApp42Error) {
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(error);
                    JSONObject app42Fault = jsonObject.getJSONObject("app42Fault");
                    if (app42Fault.has("message")) {
                        if (app42Fault.getString("message").contains(ERROR_MESSAGE_NOT_FOUND)) {
                            error = app42Fault.getString("message");
                        } else {
                            error = jsonObject.getJSONObject("app42Fault").getString("details");
                        }
                    } else {
                        error = jsonObject.getJSONObject("app42Fault").getString("details");
                    }
                } catch (JSONException e1) {
                    error = e1.getMessage();
                }
            } else {
                error = e.getMessage();
            }
        }
        if (error == null || error.isEmpty()) {
            error = MyApp.getAppContext().getString(R.string.GENERIC_API_ERROR_MESSAGE);
        }

        String finalError = error;
        new Handler(Looper.getMainLooper()).post(() -> {
            if (walletCallback != null) {
                walletCallback.onResult(false, finalError, coins, 0, 0, null, -1);
            }
        });
    }

    private static void handleError(String error, WalletCallback walletCallback, long coins) {
        if (error == null || error.isEmpty()) {
            error = MyApp.getAppContext().getString(R.string.GENERIC_API_ERROR_MESSAGE);
        }

        String finalError = error;
        new Handler(Looper.getMainLooper()).post(() -> {
            if (walletCallback != null) {
                walletCallback.onResult(false, finalError, coins, 0, 0, null, -1);
            }
        });
    }


    private static JSONObject generateCoinJSON(int action, String userName, int Id, String description, String flag, long coins) {
        String keyPoint = "";
        switch (action) {
            case ACTION_ADD:
                keyPoint = "rewardPoints";
                break;
            case ACTION_REDEEM:
                keyPoint = "redeemPoints";
                break;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("level", "level1");
            jsonObject.put("timetaken", 0);
            jsonObject.put("firstName", userName);
            jsonObject.put(keyPoint, coins);
            jsonObject.put("trascationId", Id);
            jsonObject.put("userName", userName);
            jsonObject.put("desc", description);
            jsonObject.put("actionType", action);
            jsonObject.put("flag", flag);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
