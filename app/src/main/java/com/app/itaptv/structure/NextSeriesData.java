package com.app.itaptv.structure;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

public class NextSeriesData implements Parcelable {

    public int id;
    public String title;
    public String postExcerpt;

    public NextSeriesData(JSONObject jsonObject) {

        try {
           if(jsonObject.has("ID")){
               id = jsonObject.getInt("ID");
           }
           if(jsonObject.has("post_title")){
               title = jsonObject.getString("post_title");
           }
            if(jsonObject.has("post_excerpt")){
                postExcerpt = jsonObject.getString("post_excerpt");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected NextSeriesData(Parcel in) {
        id = in.readInt();
        title = in.readString();
        postExcerpt = in.readString();
    }

    public static final Creator<NextSeriesData> CREATOR = new Creator<NextSeriesData>() {
        @Override
        public NextSeriesData createFromParcel(Parcel in) {
            return new NextSeriesData(in);
        }

        @Override
        public NextSeriesData[] newArray(int size) {
            return new NextSeriesData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeString(postExcerpt);
    }
}
