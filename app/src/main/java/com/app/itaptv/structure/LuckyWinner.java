package com.app.itaptv.structure;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class LuckyWinner {
    public ArrayList<User> winnersList = new ArrayList<>();
    public GameData gameData;

    public LuckyWinner(JSONObject object) {
        try {
            if (object.has("winners")) {
                JSONArray array = object.getJSONArray("winners");
                for (int i = 0; i < array.length(); i++) {
                    User user = new User(array.getJSONObject(i));
                    winnersList.add(user);
                }
            }
            if (object.has("game")) {
                gameData = new GameData(object.getJSONObject("game"),"");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
