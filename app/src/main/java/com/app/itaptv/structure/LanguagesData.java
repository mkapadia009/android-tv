package com.app.itaptv.structure;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class LanguagesData implements Parcelable {
    public int id;
    public String language = "";
    public String short_code = "";

    public LanguagesData(JSONObject jsonObject) {
        try {
            if (jsonObject.has("ID")) {
                id = jsonObject.getInt("ID");
            }
            if (jsonObject.has("language")) {
                language = jsonObject.getString("language");
            }
            if (jsonObject.has("short_code")) {
                short_code = jsonObject.getString("short_code");
            }
        } catch (JSONException e) {

        }
    }

    protected LanguagesData(Parcel in) {
        id = in.readInt();
        language = in.readString();
        short_code = in.readString();
    }

    public static final Creator<LanguagesData> CREATOR = new Creator<LanguagesData>() {
        @Override
        public LanguagesData createFromParcel(Parcel in) {
            return new LanguagesData(in);
        }

        @Override
        public LanguagesData[] newArray(int size) {
            return new LanguagesData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(language);
        parcel.writeString(short_code);
    }
}
