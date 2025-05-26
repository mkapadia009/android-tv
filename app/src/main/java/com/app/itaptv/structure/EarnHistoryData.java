package com.app.itaptv.structure;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by poonam on 26/9/18.
 */

public class EarnHistoryData {

    public String transactionId = "";
    public String level = "";
    public String description = "";
    public int timeTaken;
    public long rewardPoints;
    public long redeemPoints;
    public long points;
    public boolean isRedeem;
    public String flag = "";

    public int historyFlag;

    public EarnHistoryData(JSONObject jsonObject, int historyFlag) {
        this.historyFlag = historyFlag;
        try {
            if (jsonObject.has("transaction_id")) {
                transactionId = jsonObject.has("transaction_id") ? jsonObject.getString("transaction_id") : "";
            }

            if (jsonObject.has("level")) {
                level = jsonObject.has("level") ? jsonObject.getString("level") : "";
            }

            if (jsonObject.has("desc")) {
                description = jsonObject.has("desc") ? jsonObject.getString("desc") : "";
            }

            if (jsonObject.has("timetaken")) {
                timeTaken = jsonObject.has("timetaken") ? jsonObject.getInt("timetaken") : 0;
            }

            if (jsonObject.has("coins")) {
                rewardPoints = jsonObject.has("coins") ? jsonObject.getLong("coins") : 0;
                points = rewardPoints;
                isRedeem = false;
            }

            if (jsonObject.has("redeemPoints")) {
                redeemPoints = jsonObject.has("redeemPoints") ? jsonObject.getLong("redeemPoints") : 0;
                points = redeemPoints;
                isRedeem = true;
            }
            if (jsonObject.has("flag")) {
                flag = jsonObject.has("flag") ? jsonObject.getString("flag") : "";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
