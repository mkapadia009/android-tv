package com.app.itaptv.structure;

import org.json.JSONException;
import org.json.JSONObject;

public class QuestionsLiveNowData {

    //public String id;
    public String senderName;
    public String senderQuestion;
    public String senderTime;

    public QuestionsLiveNowData(JSONObject jsonObject) {
        try {
            //id = jsonObject.has("id") ? jsonObject.getString("id") : "";
            senderName = jsonObject.has("name") ? jsonObject.getString("name") : "";
            senderQuestion = jsonObject.has("message") ? jsonObject.getString("message") : "";
            senderTime = jsonObject.has("created_at") ? jsonObject.getString("created_at") : "";

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
