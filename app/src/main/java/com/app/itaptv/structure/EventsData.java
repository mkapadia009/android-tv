package com.app.itaptv.structure;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class EventsData implements Parcelable {

    public String eventName="";

    public EventsData(JSONObject jsonObject) {
        setData(jsonObject);
    }

    protected EventsData(Parcel in) {
        eventName = in.readString();
    }

    public static final Creator<EventsData> CREATOR = new Creator<EventsData>() {
        @Override
        public EventsData createFromParcel(Parcel in) {
            return new EventsData(in);
        }

        @Override
        public EventsData[] newArray(int size) {
            return new EventsData[size];
        }
    };

    public void setData(JSONObject jsonObject){
        try {

            if (jsonObject.has("name")) {
                eventName = jsonObject.getString("name");
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
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(eventName);
    }
}
