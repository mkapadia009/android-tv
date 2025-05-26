package com.app.itaptv.structure;

import com.app.itaptv.utils.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by poonam on 18/10/18.
 */

public class GameChatScoreData {
    String TAG = getClass().getSimpleName();
    public HashMap<String, Integer> mapPlayers = new HashMap<String, Integer>();

    public GameChatScoreData(JSONObject jsonObject) {

        try {
            Iterator<String> iterator = jsonObject.keys();
            while (iterator.hasNext()) {
                String userId = iterator.next();
                int userScore = jsonObject.getInt(userId);
                mapPlayers.put(userId, userScore);
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());

        }

    }
}
