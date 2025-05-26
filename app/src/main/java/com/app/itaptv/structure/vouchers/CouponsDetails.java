package com.app.itaptv.structure.vouchers;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

import org.json.JSONObject;

public class CouponsDetails implements Parcelable {

    public String id = "";
    public String title = "";
    public String expiresOn = "";
    public String description = "";
    public String couponCode = "";
    public String imageUrl = "";
    public String type = "";
    public boolean isFromStore = false;
    public boolean isDeal = false;
    public String outLinkUrl = "";
    public Category category = null;
    public String rectImage = "";
    public String storeName = "";
    public boolean isRedeem = false;
    public int coins = 0;
    public String redeemDate ="";

    public CouponsDetails(JSONObject jsonObject, boolean isFromStore) {
        try {

            this.isFromStore = isFromStore;

            if (jsonObject.has("id")) {
                id = jsonObject.getString("id");
            }
            if (jsonObject.has("title")) {
                title = jsonObject.getString("title");
            }
            if (jsonObject.has("expires_on")) {
                expiresOn = jsonObject.getString("expires_on");
            }
            if (jsonObject.has("description")) {
                description = jsonObject.getString("description");
            }
            if (jsonObject.has("coupon_code")) {
                couponCode = jsonObject.getString("coupon_code");
            }
            if (jsonObject.has("image")) {
                imageUrl = jsonObject.getString("image");
            }
            if (jsonObject.has("type")) {
                type = jsonObject.getString("type");
            }
            if (jsonObject.has("outlink_url")) {
                outLinkUrl = jsonObject.getString("outlink_url");
            }
            if (jsonObject.has("is_deal")) {
                isDeal = jsonObject.getBoolean("is_deal");
            }
            if (jsonObject.has("image_rectangle")) {
                rectImage = jsonObject.getString("image_rectangle");
            }
            if (jsonObject.has("store_name")) {
                storeName = jsonObject.getString("store_name");
            }
            if (jsonObject.has("coins")) {
                coins = jsonObject.getInt("coins");
            }
            if (jsonObject.has("isPurchased")) {
                isRedeem = jsonObject.getBoolean("isPurchased");
            }
            if (jsonObject.has("redeem_date")) {
                redeemDate = jsonObject.getString("redeem_date");
            }
            if (jsonObject.has("category")) {
                category = new Category(jsonObject.getJSONObject("category"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected CouponsDetails(Parcel in) {
        id = in.readString();
        title = in.readString();
        expiresOn = in.readString();
        description = in.readString();
        couponCode = in.readString();
        imageUrl = in.readString();
        type = in.readString();
        isFromStore = in.readByte() != 0;
        isDeal = in.readByte() != 0;
        outLinkUrl = in.readString();
        category = in.readParcelable(Category.class.getClassLoader());
        rectImage = in.readString();
        storeName = in.readString();
        isRedeem = in.readByte() != 0;
        coins = in.readInt();
        redeemDate = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(expiresOn);
        dest.writeString(description);
        dest.writeString(couponCode);
        dest.writeString(imageUrl);
        dest.writeString(type);
        dest.writeByte((byte) (isFromStore ? 1 : 0));
        dest.writeByte((byte) (isDeal ? 1 : 0));
        dest.writeString(outLinkUrl);
        dest.writeParcelable(category, flags);
        dest.writeString(rectImage);
        dest.writeString(storeName);
        dest.writeByte((byte) (isRedeem ? 1 : 0));
        dest.writeInt(coins);
        dest.writeString(redeemDate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CouponsDetails> CREATOR = new Creator<CouponsDetails>() {
        @Override
        public CouponsDetails createFromParcel(Parcel in) {
            return new CouponsDetails(in);
        }

        @Override
        public CouponsDetails[] newArray(int size) {
            return new CouponsDetails[size];
        }
    };

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
