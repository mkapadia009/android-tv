package com.app.itaptv.structure;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by poonam on 20/12/18.
 */

public class FeedGameData implements Parcelable {

    public String id = "";
    public String postTitle = "";
    public String backgroundStatus = "";
    public String backgroundImage = "";
    public ArrayList<GameData> arrayListGameData = new ArrayList<>();

    public FeedGameData(JSONObject jsonObject) {
        try {
            if (jsonObject.has("ID")) {
                id = jsonObject.getString("ID");
            }

            if (jsonObject.has("post_title")) {
                postTitle = jsonObject.getString("post_title");
            }

            if (jsonObject.has("backgroundStatus")) {
                backgroundStatus = jsonObject.getString("backgroundStatus");
            }

            if (jsonObject.has("backgroundImage")) {
                backgroundImage = jsonObject.getString("backgroundImage");
            }

            if (jsonObject.has("contents")) {
                JSONArray jsonArrayContents = jsonObject.getJSONArray("contents");
                for (int i = 0; i < jsonArrayContents.length(); i++) {
                    GameData gameData = new GameData(jsonArrayContents.getJSONObject(i), id);
                    arrayListGameData.add(gameData);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected FeedGameData(Parcel in) {
        id = in.readString();
        postTitle = in.readString();
        backgroundStatus = in.readString();
        backgroundImage = in.readString();
        arrayListGameData = in.createTypedArrayList(GameData.CREATOR);
    }

    public static final Creator<FeedGameData> CREATOR = new Creator<FeedGameData>() {
        @Override
        public FeedGameData createFromParcel(Parcel in) {
            return new FeedGameData(in);
        }

        @Override
        public FeedGameData[] newArray(int size) {
            return new FeedGameData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(postTitle);
        dest.writeString(backgroundStatus);
        dest.writeString(backgroundImage);
        dest.writeTypedList(arrayListGameData);
    }
}
