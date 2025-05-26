package com.app.itaptv.structure.vouchers;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class AllCouponsFeedsData implements Parcelable {

    public String id = "";
    public String name = "";
    public String contentType = "";
    public ArrayList<AllCouponsFeedsDataContents> contentsArrayList = new ArrayList<>();

    public AllCouponsFeedsData(JSONObject jsonObject) {
        try {
            if (jsonObject.has("id")) {
                id = jsonObject.getString("id");
            }
            if (jsonObject.has("name")) {
                name = jsonObject.getString("name");
            }
            if (jsonObject.has("content_type")) {
                contentType = jsonObject.getString("content_type");
            }
            if (jsonObject.has("contents")) {
                JSONArray array = jsonObject.getJSONArray("contents");
                for (int i = 0; i < array.length(); i++) {
                    contentsArrayList.add(new AllCouponsFeedsDataContents(array.getJSONObject(i)));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected AllCouponsFeedsData(Parcel in) {
        id = in.readString();
        name = in.readString();
        contentType = in.readString();
    }

    public static final Creator<AllCouponsFeedsData> CREATOR = new Creator<AllCouponsFeedsData>() {
        @Override
        public AllCouponsFeedsData createFromParcel(Parcel in) {
            return new AllCouponsFeedsData(in);
        }

        @Override
        public AllCouponsFeedsData[] newArray(int size) {
            return new AllCouponsFeedsData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(contentType);
    }
}
