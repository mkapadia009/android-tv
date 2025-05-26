package com.app.itaptv.structure.vouchers;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

public class AllCouponsFeedsDataContents implements Parcelable {

    public int id = 0;
    public String type = "";
    public String name = "";
    public String image = "";
    public String rectImage = "";

    // types
    public static String COUPON_TYPE_STORE = "store";
    public static String COUPON_TYPE_CATEGORIES = "coupon_category";

    public AllCouponsFeedsDataContents(JSONObject jsonObject) {
        try {
            if (jsonObject.has("id")) {
                id = jsonObject.getInt("id");
            }
            if (jsonObject.has("type")) {
                type = jsonObject.getString("type");
            }
            if (jsonObject.has("name")) {
                name = jsonObject.getString("name");
            }
            if (jsonObject.has("image")) {
                image = jsonObject.getString("image");
            }
            if (jsonObject.has("image_reactangle")) {
                rectImage = jsonObject.getString("image_reactangle");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    protected AllCouponsFeedsDataContents(Parcel in) {
        id = in.readInt();
        type = in.readString();
        name = in.readString();
        image = in.readString();
        rectImage = in.readString();
    }

    public static final Creator<AllCouponsFeedsDataContents> CREATOR = new Creator<AllCouponsFeedsDataContents>() {
        @Override
        public AllCouponsFeedsDataContents createFromParcel(Parcel in) {
            return new AllCouponsFeedsDataContents(in);
        }

        @Override
        public AllCouponsFeedsDataContents[] newArray(int size) {
            return new AllCouponsFeedsDataContents[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(type);
        dest.writeString(name);
        dest.writeString(image);
        dest.writeString(rectImage);
    }
}
