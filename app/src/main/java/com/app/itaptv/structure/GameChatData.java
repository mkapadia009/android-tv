package com.app.itaptv.structure;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by poonam on 18/10/18.
 */

public class GameChatData {

    public int timer;
    public QuestionData questionData = null;
    public GameChatScoreData gameChatScoreData = null;


    public GameChatData(JSONObject jsonObject) {
        try {
            if (jsonObject.has("timer")) {
                timer = jsonObject.getInt("timer");
            }

            if (jsonObject.has("scores")) {
                gameChatScoreData = new GameChatScoreData(jsonObject.getJSONObject("scores"));
            }

            if (jsonObject.has("question")) {
                questionData = new QuestionData(jsonObject.getJSONObject("question"));
            }

        } catch (JSONException e) {

        }

    }
}
