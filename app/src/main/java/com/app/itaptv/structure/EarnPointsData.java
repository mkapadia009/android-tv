package com.app.itaptv.structure;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class EarnPointsData implements Parcelable {
    public int id;
    public String title = "";
    public String titleType = "";
    public String coins = "";

    public EarnPointsData(JSONObject jsonObject) {
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
            if (jsonObject.has("coins")) {
                coins = jsonObject.getString("coins");
            }
        } catch (JSONException e) {

        }
    }

    protected EarnPointsData(Parcel in) {
        id = in.readInt();
        title = in.readString();
        titleType = in.readString();
        coins = in.readString();
    }

    public static final Creator<EarnPointsData> CREATOR = new Creator<EarnPointsData>() {
        @Override
        public EarnPointsData createFromParcel(Parcel in) {
            return new EarnPointsData(in);
        }

        @Override
        public EarnPointsData[] newArray(int size) {
            return new EarnPointsData[size];
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
        dest.writeString(coins);
    }
}
