package com.app.itaptv.structure.vouchers;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class AllCoupons implements Parcelable {

    public ArrayList<AllCouponsFeedsData> feedsDataArrayList = new ArrayList<>();

    public AllCoupons(JSONObject jsonObject) {
        try {
            if (jsonObject.has("feeds")) {
                JSONArray array = jsonObject.getJSONArray("feeds");
                for (int i = 0; i < array.length(); i++) {
                    feedsDataArrayList.add(new AllCouponsFeedsData(array.getJSONObject(i)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected AllCoupons(Parcel in) {
        feedsDataArrayList = in.createTypedArrayList(AllCouponsFeedsData.CREATOR);
    }

    public static final Creator<AllCoupons> CREATOR = new Creator<AllCoupons>() {
        @Override
        public AllCoupons createFromParcel(Parcel in) {
            return new AllCoupons(in);
        }

        @Override
        public AllCoupons[] newArray(int size) {
            return new AllCoupons[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(feedsDataArrayList);
    }
}
