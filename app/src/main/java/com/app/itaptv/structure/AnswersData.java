package com.app.itaptv.structure;

import org.json.JSONException;
import org.json.JSONObject;

public class AnswersData {

    public String name = "";

    public AnswersData(JSONObject jsonObject) {
        try {

            if (jsonObject.has("name")) {
                name = jsonObject.getString("name");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
