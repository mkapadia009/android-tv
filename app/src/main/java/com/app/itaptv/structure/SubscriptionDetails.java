package com.app.itaptv.structure;

import org.json.JSONObject;

public class SubscriptionDetails {
    public String status = "";
    public String start_date = "";
    public String expiry_date = "";
    public String allow_rental = "";

    public SubscriptionDetails(JSONObject object) {
        try {
            if (object.has("status")) {
                status = object.getString("status");
            }
            if (object.has("start_date")) {
                start_date = object.getString("start_date");
            }
            if (object.has("expiry_date")) {
                expiry_date = object.getString("expiry_date");
            }
            if (object.has("allow_rental")) {
                allow_rental = object.getString("allow_rental");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
