package com.app.itaptv.structure;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

public class HomeSliderObject implements Parcelable {

    public FeedContentData feedContentData = null;
    public GameData gameData = null;
    public int id;
    public String contentType = "";
    public String buttonText = "";
    public String image;
    public int pos;

    public HomeSliderObject(String image,int pos){
        this.image=image;
        this.pos=pos;
    }

    public HomeSliderObject(JSONObject object, int position) {
        try {
            if (object.has("ID")) {
                id = object.getInt("ID");
            }
            if (object.has("content_type")) {
                contentType = object.getString("content_type");
            }
            if (object.has("button_text")) {
                buttonText = object.getString("button_text");
            }
            if (contentType.equalsIgnoreCase(FeedContentData.CONTENT_TYPE_GAME)) {
                gameData = new GameData(object, "");
            } else {
                FeedContentData feedContentData = new FeedContentData();
                feedContentData.setSliderData(object);
                this.feedContentData = feedContentData;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected HomeSliderObject(Parcel in) {
        id = in.readInt();
        contentType = in.readString();
        buttonText = in.readString();
    }

    public static final Creator<HomeSliderObject> CREATOR = new Creator<HomeSliderObject>() {
        @Override
        public HomeSliderObject createFromParcel(Parcel in) {
            return new HomeSliderObject(in);
        }

        @Override
        public HomeSliderObject[] newArray(int size) {
            return new HomeSliderObject[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(contentType);
        dest.writeString(buttonText);
    }
}
