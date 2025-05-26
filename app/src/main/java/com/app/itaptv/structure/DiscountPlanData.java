package com.app.itaptv.structure;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class DiscountPlanData implements Parcelable {
    public String discount = "";
    public String planId = "";
    public String amount="";

    public DiscountPlanData(JSONObject jsonObject) {
        setData(jsonObject);
    }

    protected DiscountPlanData(Parcel in) {
        discount = in.readString();
        planId = in.readString();
        amount = in.readString();
    }

    public static final Creator<DiscountPlanData> CREATOR = new Creator<DiscountPlanData>() {
        @Override
        public DiscountPlanData createFromParcel(Parcel in) {
            return new DiscountPlanData(in);
        }

        @Override
        public DiscountPlanData[] newArray(int size) {
            return new DiscountPlanData[size];
        }
    };

    public void setData(JSONObject jsonObject){
        try {

            if (jsonObject.has("discount")) {
                discount = jsonObject.getString("discount");
            }
            if (jsonObject.has("plan_id")) {
                planId = jsonObject.getString("plan_id");
            }
            if (jsonObject.has("amount")) {
                amount = jsonObject.getString("amount");
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
        dest.writeString(discount);
        dest.writeString(planId);
        dest.writeString(amount);
    }
}
