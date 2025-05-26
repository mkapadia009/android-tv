package com.app.itaptv.structure;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SubscriptionPlanData implements Parcelable {
    public int ID;
    public String postTitle = "";
    public String subsAmount = "";
    public String durationType="";
    public String duration="";
    public ArrayList<DiscountPlanData> discountPlanDataArrayList = new ArrayList<>();

    public SubscriptionPlanData(JSONObject jsonObject) {
        setData(jsonObject);
    }

    public void setData(JSONObject jsonObject){
        try {

            if (jsonObject.has("ID")) {
                ID = jsonObject.getInt("ID");
            }
            if (jsonObject.has("post_title")) {
                postTitle = jsonObject.getString("post_title");
            }
            if (jsonObject.has("subs_amount")) {
                subsAmount = jsonObject.getString("subs_amount");
            }
            if (jsonObject.has("pricing")) {
                JSONObject jsonObjectPricing = jsonObject.getJSONObject("pricing");
                if (jsonObjectPricing.has("duration_type")) {
                    durationType = jsonObjectPricing.getString("duration_type");
                }
                if (jsonObjectPricing.has("duration")) {
                    duration = jsonObjectPricing.getString("duration");
                }
            }

            if (jsonObject.has("plans")) {
                JSONArray jsonArrayPlans = jsonObject.getJSONArray("plans");
                for (int i = 0; i < jsonArrayPlans.length(); i++) {
                    DiscountPlanData discountPlanData = new DiscountPlanData(jsonArrayPlans.getJSONObject(i));
                    discountPlanDataArrayList.add(discountPlanData);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public SubscriptionPlanData(Parcel in) {
        ID = in.readInt();
        postTitle = in.readString();
        subsAmount = in.readString();
        durationType = in.readString();
        duration = in.readString();
        discountPlanDataArrayList = in.createTypedArrayList(DiscountPlanData.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ID);
        dest.writeString(postTitle);
        dest.writeString(subsAmount);
        dest.writeString(durationType);
        dest.writeString(duration);
        dest.writeTypedList(discountPlanDataArrayList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SubscriptionPlanData> CREATOR = new Creator<SubscriptionPlanData>() {
        @Override
        public SubscriptionPlanData createFromParcel(Parcel in) {
            return new SubscriptionPlanData(in);
        }

        @Override
        public SubscriptionPlanData[] newArray(int size) {
            return new SubscriptionPlanData[size];
        }
    };
}
