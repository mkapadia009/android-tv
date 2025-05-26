package com.app.itaptv.structure;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

public class NextSeries implements Parcelable {

    public NextSeriesData nextSeriesData;
    public String buttonText = "";

    public NextSeries(JSONObject jsonObject) {
        try {
            if (jsonObject.has("series")) {
                JSONObject object = jsonObject.getJSONObject("series");
                nextSeriesData = new NextSeriesData(object);
            }

            if (jsonObject.has("button_text")) {
                buttonText = jsonObject.getString("button_text");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    protected NextSeries(Parcel in) {
        nextSeriesData = in.readParcelable(NextSeriesData.class.getClassLoader());
        buttonText = in.readString();
    }

    public static final Creator<NextSeries> CREATOR = new Creator<NextSeries>() {
        @Override
        public NextSeries createFromParcel(Parcel in) {
            return new NextSeries(in);
        }

        @Override
        public NextSeries[] newArray(int size) {
            return new NextSeries[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(nextSeriesData, i);
        parcel.writeString(buttonText);
    }
}