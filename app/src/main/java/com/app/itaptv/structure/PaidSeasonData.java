package com.app.itaptv.structure;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PaidSeasonData implements Parcelable {

    public String season = "";
    public Boolean teaser_promo = false;
    public String teaser_image = "";
    public String description = "";
    public ArrayList<PriceData> arrayListPriceData = new ArrayList<>();

    public PaidSeasonData(JSONObject jsonObject) {
        try {

            if (jsonObject.has("season")) {
                season = jsonObject.getString("season");
            }

            if (jsonObject.has("teaser_promo")) {
                teaser_promo = jsonObject.getBoolean("teaser_promo");
            }

            if (jsonObject.has("teaser_image")) {
                teaser_image = jsonObject.getString("teaser_image");
            }

            if (jsonObject.has("description")) {
                description = jsonObject.getString("description");
            }
            if (jsonObject.has("pricing")) {
                JSONArray jsonArrayPricing = jsonObject.getJSONArray("pricing");
                for (int i = 0; i < jsonArrayPricing.length(); i++) {
                    PriceData priceData = new PriceData(jsonArrayPricing.getJSONObject(i));
                    arrayListPriceData.add(priceData);
                }
            }

        } catch (JSONException e) {

        }
    }

    protected PaidSeasonData(Parcel in) {
        season = in.readString();
        byte tmpTeaser_promo = in.readByte();
        teaser_promo = tmpTeaser_promo == 0 ? null : tmpTeaser_promo == 1;
        teaser_image = in.readString();
        description = in.readString();
        arrayListPriceData = in.createTypedArrayList(PriceData.CREATOR);
    }

    public static final Creator<PaidSeasonData> CREATOR = new Creator<PaidSeasonData>() {
        @Override
        public PaidSeasonData createFromParcel(Parcel in) {
            return new PaidSeasonData(in);
        }

        @Override
        public PaidSeasonData[] newArray(int size) {
            return new PaidSeasonData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(season);
        dest.writeByte((byte) (teaser_promo == null ? 0 : teaser_promo ? 1 : 2));
        dest.writeString(teaser_image);
        dest.writeString(description);
        dest.writeTypedList(arrayListPriceData);
    }
}
