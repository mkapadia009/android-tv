package com.app.itaptv.structure;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class RedeemTabsList implements Parcelable {
    public String id;
    public String title = "";
    public String status = "";

    public RedeemTabsList(JSONObject jsonObject) {
        try {
            if (jsonObject.has("id")) {
                id = jsonObject.getString("id");
            }
            if (jsonObject.has("title")) {
                title = jsonObject.getString("title");
            }
            if (jsonObject.has("status")) {
                status = jsonObject.getString("status");
            }
        } catch (JSONException e) {

        }
    }

    protected RedeemTabsList(Parcel in) {
        id = in.readString();
        title = in.readString();
        status = in.readString();
    }

    public static final Creator<RedeemTabsList> CREATOR = new Creator<RedeemTabsList>() {
        @Override
        public RedeemTabsList createFromParcel(Parcel in) {
            return new RedeemTabsList(in);
        }

        @Override
        public RedeemTabsList[] newArray(int size) {
            return new RedeemTabsList[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(status);
    }
}
