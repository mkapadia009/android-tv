package com.app.itaptv.structure.vouchers;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

public class Category implements Parcelable {

    public int catId = 0;
    public String catName = "";
    public String iconUrl = "";
    public int noOfOffers = 0;

    public Category(JSONObject jsonObject) {
        try {
            if (jsonObject.has("category_id")) {
                catId = jsonObject.getInt("category_id");
            }
            if (jsonObject.has("category_name")) {
                catName = jsonObject.getString("category_name");
            }
            if (jsonObject.has("icon_url")) {
                iconUrl = jsonObject.getString("icon_url");
            }
            if (jsonObject.has("num_offers")) {
                noOfOffers = jsonObject.getInt("num_offers");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected Category(Parcel in) {
        catId = in.readInt();
        catName = in.readString();
        iconUrl = in.readString();
        noOfOffers = in.readInt();
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(catId);
        dest.writeString(catName);
        dest.writeString(iconUrl);
        dest.writeInt(noOfOffers);
    }
}
