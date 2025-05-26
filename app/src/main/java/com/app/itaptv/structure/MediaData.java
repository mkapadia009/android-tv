package com.app.itaptv.structure;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class MediaData implements Parcelable {
    public String type = "";
    public String file = "";

    public MediaData(JSONObject jsonObject) {
        setData(jsonObject);
    }

    protected MediaData(Parcel in) {
        type = in.readString();
        file = in.readString();
    }

    public static final Creator<MediaData> CREATOR = new Creator<MediaData>() {
        @Override
        public MediaData createFromParcel(Parcel in) {
            return new MediaData(in);
        }

        @Override
        public MediaData[] newArray(int size) {
            return new MediaData[size];
        }
    };

    public void setData(JSONObject jsonObject){
        try {

            if (jsonObject.has("type")) {
                type = jsonObject.getString("type");
            }
            if (jsonObject.has("file")) {
                file = jsonObject.getString("file");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeString(file);
    }
}
