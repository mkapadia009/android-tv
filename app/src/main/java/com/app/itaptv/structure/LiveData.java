package com.app.itaptv.structure;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LiveData implements Parcelable {
    public String title = "";
    public int id;
    public ArrayList<LiveNowData> arrayListLiveNowData = new ArrayList<>();

    public LiveData()
    {

    }

    public LiveData(JSONObject jsonObject)
    {
        try {
            if (jsonObject.has("title")) {
                title = jsonObject.getString("title");
            }

            if (jsonObject.has("ID")) {
                id = jsonObject.getInt("ID");
            }

            if (jsonObject.has("contents")) {
                JSONArray jsonArrayContent = jsonObject.getJSONArray("contents");

                for (int i = 0; i < jsonArrayContent.length(); i++) {
                    LiveNowData liveNowData = new LiveNowData(jsonArrayContent.getJSONObject(i));
                    arrayListLiveNowData.add(liveNowData);
                }
            }
        } catch (JSONException e) {

        }
        }

    protected LiveData(Parcel in) {
        title = in.readString();
        id = in.readInt();
        arrayListLiveNowData = in.createTypedArrayList(LiveNowData.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeInt(id);
        dest.writeTypedList(arrayListLiveNowData);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<LiveData> CREATOR = new Creator<LiveData>() {
        @Override
        public LiveData createFromParcel(Parcel in) {
            return new LiveData(in);
        }

        @Override
        public LiveData[] newArray(int size) {
            return new LiveData[size];
        }
    };
}
