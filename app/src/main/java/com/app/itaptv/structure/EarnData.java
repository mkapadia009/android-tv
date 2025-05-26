package com.app.itaptv.structure;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class EarnData implements Parcelable {
    public int id;
    public String title = "";
    public String titleType = "";
    public ArrayList<EarnPointsData> earnPointsDataArrayList = new ArrayList<>();
    public JSONArray contentsJSONArray = null;

    public EarnData(JSONObject jsonObject, int position) {
        try {
            if (jsonObject.has("id")) {
                id = jsonObject.getInt("id");
            }
            if (jsonObject.has("title")) {
                title = jsonObject.getString("title");
            }
            if (jsonObject.has("title_type")) {
                titleType = jsonObject.getString("title_type");
            }
            if (jsonObject.has("contents")) {
                contentsJSONArray = jsonObject.getJSONArray("contents");
                JSONArray jsonArrayContent = jsonObject.getJSONArray("contents");
                for (int i = 0; i < jsonArrayContent.length(); i++) {
                    EarnPointsData earnPointsData = new EarnPointsData(jsonArrayContent.getJSONObject(i));
                    earnPointsDataArrayList.add(earnPointsData);
                }
            }
        } catch (JSONException e) {

        }
    }

    protected EarnData(Parcel in) {
        id = in.readInt();
        title = in.readString();
        titleType = in.readString();
    }

    public static final Creator<EarnData> CREATOR = new Creator<EarnData>() {
        @Override
        public EarnData createFromParcel(Parcel in) {
            return new EarnData(in);
        }

        @Override
        public EarnData[] newArray(int size) {
            return new EarnData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(titleType);
    }
}
