package com.app.itaptv.structure;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by poonam on 17/12/18.
 */

public class PlaylistData implements Parcelable {
    public ArrayList<FeedContentData> arrayListFeedContentData = new ArrayList<>();
    public FeedContentData feedContentData = null;

    public PlaylistData(JSONObject jsonObject) {
        try {
            if (jsonObject.has("contents")) {
                JSONArray jsonArrayContent = jsonObject.getJSONArray("contents");
                for (int i = 0; i < jsonArrayContent.length(); i++) {
                    FeedContentData feedContentData = new FeedContentData(jsonArrayContent.getJSONObject(i), -1);
                    arrayListFeedContentData.add(feedContentData);
                }
            }

            if (jsonObject.has("playlist")) {
                feedContentData = new FeedContentData(jsonObject.getJSONObject("playlist"), -1);
            }

        } catch (JSONException e) {
            e.printStackTrace();

        }
    }

    protected PlaylistData(Parcel in) {
        arrayListFeedContentData = in.createTypedArrayList(FeedContentData.CREATOR);
        feedContentData = in.readParcelable(FeedContentData.class.getClassLoader());
    }

    public static final Creator<PlaylistData> CREATOR = new Creator<PlaylistData>() {
        @Override
        public PlaylistData createFromParcel(Parcel in) {
            return new PlaylistData(in);
        }

        @Override
        public PlaylistData[] newArray(int size) {
            return new PlaylistData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(arrayListFeedContentData);
        dest.writeParcelable(feedContentData, flags);
    }
}
