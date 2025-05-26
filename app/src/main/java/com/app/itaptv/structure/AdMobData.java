package com.app.itaptv.structure;

import android.os.Parcel;
import android.os.Parcelable;

import com.app.itaptv.utils.Constant;

import org.json.JSONException;
import org.json.JSONObject;

public class AdMobData implements Parcelable {

    public String id = "";
    public String show = "";
    public String type = "";
    public String custom_ad = "";
    public FeedContentData feedContentObjectData = null;

    public AdMobData(JSONObject jsonObject) {
        try {

            if (jsonObject.has("id")) {
                id = jsonObject.getString("id");
            }

            if (jsonObject.has("show")) {
                show = jsonObject.getString("show");
            }
            if (jsonObject.has("type")) {
                type = jsonObject.getString("type");
            }
            if (type.equalsIgnoreCase(Constant.ADMOB)) {
                if (jsonObject.has("custom_ad")) {
                    custom_ad = jsonObject.getString("custom_ad");
                }
            } else {
                if (type.equalsIgnoreCase(Constant.CUSTOM)) {
                    if (jsonObject.has("custom_ad")) {
                        JSONObject jsonObjectCustomAd = jsonObject.getJSONObject("custom_ad");
                        feedContentObjectData = new FeedContentData(jsonObjectCustomAd);
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected AdMobData(Parcel in) {
        id = in.readString();
        show = in.readString();
        type = in.readString();
        custom_ad = in.readString();
    }

    public static final Creator<AdMobData> CREATOR = new Creator<AdMobData>() {
        @Override
        public AdMobData createFromParcel(Parcel in) {
            return new AdMobData(in);
        }

        @Override
        public AdMobData[] newArray(int size) {
            return new AdMobData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(show);
        dest.writeString(type);
        dest.writeString(custom_ad);
    }
}
