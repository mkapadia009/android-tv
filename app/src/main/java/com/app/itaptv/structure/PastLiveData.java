package com.app.itaptv.structure;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class PastLiveData implements Parcelable, Serializable {

    public int id = 0;
    public int comments = 0;
    public int questions = 0;
    public int views = 0;
    public int reaction_HEART = 0;
    public int reaction_LAUGH = 0;
    public int reaction_CLAPS = 0;
    public int reaction_WOW = 0;
    public String imageUrl = "";
    public String title = "";
    public String time = "";
    public String duration = "";
    public String viewers = "";
    public String description = "";
    public String pastLiveStreamUrl = "";

    public PastLiveData() {

    }

    public PastLiveData(JSONObject jsonObject) {
        try {
            id = jsonObject.has("ID") ? jsonObject.getInt("ID") : 0;
            comments = jsonObject.has("comments") ? jsonObject.getInt("comments") : 0;
            questions = jsonObject.has("questions") ? jsonObject.getInt("questions") : 0;
            views = jsonObject.has("views") ? jsonObject.getInt("views") : 0;
            reaction_HEART = jsonObject.has("reaction_HEART") ? jsonObject.getInt("reaction_HEART") : 0;
            reaction_LAUGH = jsonObject.has("reaction_LAUGH") ? jsonObject.getInt("reaction_LAUGH") : 0;
            reaction_CLAPS = jsonObject.has("reaction_CLAPS") ? jsonObject.getInt("reaction_CLAPS") : 0;
            reaction_WOW = jsonObject.has("reaction_WOW") ? jsonObject.getInt("reaction_WOW") : 0;
            imageUrl = jsonObject.has("imgUrl") ? jsonObject.getString("imgUrl") : "";
            title = jsonObject.has("post_title") ? jsonObject.getString("post_title") : "";
            time = jsonObject.has("scheduled_time") ? jsonObject.getString("scheduled_time") : "";
            duration = jsonObject.has("duration") ? jsonObject.getString("duration") : "";
            viewers = jsonObject.has("view_count") ? jsonObject.getString("view_count") : "";
            description = jsonObject.has("description") ? jsonObject.getString("description") : "";
            pastLiveStreamUrl = jsonObject.has("hls_url") ? jsonObject.getString("hls_url") : "";

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected PastLiveData(Parcel in) {
        id = in.readInt();
        comments = in.readInt();
        questions = in.readInt();
        views = in.readInt();
        reaction_HEART = in.readInt();
        reaction_LAUGH = in.readInt();
        reaction_CLAPS = in.readInt();
        reaction_WOW = in.readInt();
        imageUrl = in.readString();
        title = in.readString();
        time = in.readString();
        duration = in.readString();
        viewers = in.readString();
        description = in.readString();
        pastLiveStreamUrl = in.readString();
    }

    public static final Creator<PastLiveData> CREATOR = new Creator<PastLiveData>() {
        @Override
        public PastLiveData createFromParcel(Parcel in) {
            return new PastLiveData(in);
        }

        @Override
        public PastLiveData[] newArray(int size) {
            return new PastLiveData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(comments);
        parcel.writeInt(questions);
        parcel.writeInt(views);
        parcel.writeInt(reaction_HEART);
        parcel.writeInt(reaction_LAUGH);
        parcel.writeInt(reaction_CLAPS);
        parcel.writeInt(reaction_WOW);
        parcel.writeString(imageUrl);
        parcel.writeString(title);
        parcel.writeString(time);
        parcel.writeString(duration);
        parcel.writeString(viewers);
        parcel.writeString(description);
        parcel.writeString(pastLiveStreamUrl);
    }
}