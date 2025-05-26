package com.app.itaptv.structure;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by poonam on 6/12/18.
 */

public class PriceData implements Parcelable {

    public String duration = "";
    public String durationType = "";
    public String costType = "";
    public String coins = "";
    public String getCoins = "";
    public String rupees = "";
    public String finalCost = "";
    public String percentDiscount = "";
    public String savings = "";
    public boolean voucherRedeem = false;

    public static String COST_TYPE_COINS = "coins";
    public static String COST_TYPE_COINS_AND_RUPEES = "coins_and_rupees";
    public static String COST_TYPE_RUPEES = "rupees";

    public PriceData(JSONObject jsonObject) {
        try {

            if (jsonObject.has("duration")) {
                duration = jsonObject.getString("duration");
            }

            if (jsonObject.has("duration_type")) {
                durationType = jsonObject.getString("duration_type");
            }

            if (jsonObject.has("cost_type")) {
                costType = jsonObject.getString("cost_type");
            }

            if (jsonObject.has("coins")) {
                coins = jsonObject.getString("coins");
            }

            if (jsonObject.has("get_coins")) {
                getCoins = jsonObject.getString("get_coins");
            }

            if (jsonObject.has("rupees")) {
                rupees = jsonObject.getString("rupees");
            }

            if (jsonObject.has("final_cost")) {
                finalCost = jsonObject.getString("final_cost");
            }
            if (jsonObject.has("percent_discount")) {
                percentDiscount = jsonObject.getString("percent_discount");
            }
            if (jsonObject.has("savings")) {
                savings = jsonObject.getString("savings");
            }
            if (jsonObject.has("voucher_redeem")) {
                voucherRedeem = jsonObject.getBoolean("voucher_redeem");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    protected PriceData(Parcel in) {
        duration = in.readString();
        durationType = in.readString();
        costType = in.readString();
        coins = in.readString();
        getCoins = in.readString();
        rupees = in.readString();
        finalCost = in.readString();
        percentDiscount = in.readString();
        savings = in.readString();
    }

    public static final Creator<PriceData> CREATOR = new Creator<PriceData>() {
        @Override
        public PriceData createFromParcel(Parcel in) {
            return new PriceData(in);
        }

        @Override
        public PriceData[] newArray(int size) {
            return new PriceData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(duration);
        parcel.writeString(durationType);
        parcel.writeString(costType);
        parcel.writeString(coins);
        parcel.writeString(getCoins);
        parcel.writeString(rupees);
        parcel.writeString(finalCost);
        parcel.writeString(percentDiscount);
        parcel.writeString(savings);
    }
}
