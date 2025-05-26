package com.app.itaptv.structure;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by poonam on 13/12/18.
 */

public class PurchaseData implements Parcelable {

    public String orderId = "";
    public String paymentMode = "";
    public String postTitle = "";
    public String imgUrl = "";
    public String postType = "";
    public String seriesId = "";
    public String seasonId = "";
    public String itemId = "";
    public String expiry = "";
    public boolean isExpired;
    public PriceData priceData;

    public PurchaseData(JSONObject jsonObject) {

        try {
            if (jsonObject.has("order_id")) {
                orderId = jsonObject.getString("order_id");
            }

            if (jsonObject.has("payment_mode")) {
                paymentMode = jsonObject.getString("payment_mode");
            }

            if (jsonObject.has("item")) {
                JSONObject jsonObjectItem = jsonObject.getJSONObject("item");
                if (jsonObjectItem.has("post_title")) {
                    postTitle = jsonObjectItem.getString("post_title");
                }

                if (jsonObjectItem.has("imgUrl")) {
                    imgUrl = jsonObjectItem.getString("imgUrl");
                }

                if (jsonObjectItem.has("post_type")) {
                    postType = jsonObjectItem.getString("post_type");
                }

                if (jsonObjectItem.has("series")) {
                    seriesId = jsonObjectItem.getString("series");
                }

                if (jsonObjectItem.has("season")) {
                    seasonId = jsonObjectItem.getString("season");
                }
            }

            if (jsonObject.has("item_id")) {
                itemId = jsonObject.getString("item_id");
            }

            if (jsonObject.has("expiry")) {
                expiry = jsonObject.getString("expiry");
            }

            if (jsonObject.has("is_expired")) {
                isExpired = jsonObject.getBoolean("is_expired");
            }

            if (jsonObject.has("package")) {
                JSONObject jsonObjectPackage = jsonObject.getJSONObject("package");
                priceData = new PriceData(jsonObjectPackage);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    protected PurchaseData(Parcel in) {
        orderId = in.readString();
        paymentMode = in.readString();
        postTitle = in.readString();
        imgUrl = in.readString();
        postType = in.readString();
        seriesId = in.readString();
        seasonId = in.readString();
        itemId = in.readString();
        expiry = in.readString();
        isExpired = in.readByte() != 0;
        priceData = in.readParcelable(PriceData.class.getClassLoader());
    }

    public static final Creator<PurchaseData> CREATOR = new Creator<PurchaseData>() {
        @Override
        public PurchaseData createFromParcel(Parcel in) {
            return new PurchaseData(in);
        }

        @Override
        public PurchaseData[] newArray(int size) {
            return new PurchaseData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(orderId);
        parcel.writeString(paymentMode);
        parcel.writeString(postTitle);
        parcel.writeString(imgUrl);
        parcel.writeString(postType);
        parcel.writeString(seriesId);
        parcel.writeString(seasonId);
        parcel.writeString(itemId);
        parcel.writeString(expiry);
        parcel.writeByte((byte) (isExpired ? 1 : 0));
        parcel.writeParcelable(priceData, flags);
    }
}
