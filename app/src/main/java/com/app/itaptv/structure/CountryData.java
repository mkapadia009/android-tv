package com.app.itaptv.structure;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class CountryData implements Parcelable {
    public int id;
    public String countryName = "";
    public String countryImageUrl = "";
    public String countryCode = "";
    public String numberLimit = "";

    public CountryData(JSONObject jsonObject) {
        try {
            if (jsonObject.has("ID")) {
                id = jsonObject.getInt("ID");
            }
            if (jsonObject.has("country_name")) {
                countryName = jsonObject.getString("country_name");
            }
            if (jsonObject.has("country_image_url")) {
                countryImageUrl = jsonObject.getString("country_image_url");
            }
            if (jsonObject.has("calling_code")) {
                countryCode = jsonObject.getString("calling_code");
            }
            if (jsonObject.has("number_limit")) {
                numberLimit = jsonObject.getString("number_limit");
            }
        } catch (JSONException e) {

        }
    }

    protected CountryData(Parcel in) {
        id=in.readInt();
        countryName = in.readString();
        countryImageUrl = in.readString();
        countryCode = in.readString();
        numberLimit = in.readString();
    }

    public static final Creator<CountryData> CREATOR = new Creator<CountryData>() {
        @Override
        public CountryData createFromParcel(Parcel in) {
            return new CountryData(in);
        }

        @Override
        public CountryData[] newArray(int size) {
            return new CountryData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(countryName);
        dest.writeString(countryImageUrl);
        dest.writeString(countryCode);
        dest.writeString(numberLimit);
    }
}
