package com.app.itaptv.structure;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class HomeTabsList implements Parcelable {

    public String id;
    public String title = "";
    public boolean status;
    public int position;

    public HomeTabsList(JSONObject jsonObject) {
        try {
            if (jsonObject.has("ID")) {
                id = jsonObject.getString("ID");
            }
            if (jsonObject.has("tab_name")) {
                title = jsonObject.getString("tab_name");
            }
            if (jsonObject.has("status")) {
                status = jsonObject.getBoolean("status");
            }

            if (jsonObject.has("position")) {
                position = jsonObject.getInt("position");
            }
        } catch (JSONException e) {

        }
    }

    protected HomeTabsList(Parcel in) {
        id = in.readString();
        title = in.readString();
        status = in.readByte() != 0;
        position = in.readInt();
    }

    public static final Creator<HomeTabsList> CREATOR = new Creator<HomeTabsList>() {
        @Override
        public HomeTabsList createFromParcel(Parcel in) {
            return new HomeTabsList(in);
        }

        @Override
        public HomeTabsList[] newArray(int size) {
            return new HomeTabsList[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeByte((byte) (status ? 1 : 0));
        dest.writeInt(position);
    }
}
