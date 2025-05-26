package com.app.itaptv.structure;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by poonam on 8/2/19.
 */

public class MessageData {
    public String id;
    public String senderId;
    public String senderName;
    public String message;

    public MessageData(JSONObject jsonObject) {
        try {
            id = jsonObject.has("id") ? jsonObject.getString("id") : "";
            senderId = jsonObject.has("senderId") ? jsonObject.getString("senderId") : "";
            senderName = jsonObject.has("senderName") ? jsonObject.getString("senderName") : "";
            message = jsonObject.has("message") ? jsonObject.getString("message") : "";

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
