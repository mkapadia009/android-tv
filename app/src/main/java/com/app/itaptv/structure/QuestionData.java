package com.app.itaptv.structure;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class QuestionData implements Parcelable,Serializable {

    public int id;
    public String postTitle = "";
    public String postType = "";
    public String points = "0";
    public QuestionChoiceData questionChoiceData = null;
    public ArrayList<QuestionChoiceData> choices = new ArrayList<>();
    public AttachmentData attachmentData = null;

    public QuestionData()
    {

    }

    public QuestionData(JSONObject jsonObject) {
        try {
            if (jsonObject.has("ID")) {
                id = jsonObject.getInt("ID");
            }
            postTitle = jsonObject.has("post_title") ? jsonObject.getString("post_title") : "";
            postType = jsonObject.has("post_type") ? jsonObject.getString("post_type") : "";

            if (jsonObject.has("points")) {
                points = jsonObject.getString("points");
            }


            if (jsonObject.has("attachment")) {
                attachmentData = new AttachmentData(jsonObject.getJSONObject("attachment"));
            }
            if (jsonObject.has("choices")) {
                JSONArray jsonArray = jsonObject.getJSONArray("choices");
                for (int i = 0; i < jsonArray.length(); i++) {
                    questionChoiceData = new QuestionChoiceData(jsonArray.getJSONObject(i));
                    choices.add(questionChoiceData);
                }
            }
        } catch (JSONException e) {

        }
    }

    protected QuestionData(Parcel in) {
        id = in.readInt();
        postTitle = in.readString();
        postType = in.readString();
        points = in.readString();
        in.readTypedList(choices, QuestionChoiceData.CREATOR);
        attachmentData = in.readParcelable(AttachmentData.class.getClassLoader());
    }

    public static final Creator<QuestionData> CREATOR = new Creator<QuestionData>() {
        @Override
        public QuestionData createFromParcel(Parcel in) {
            return new QuestionData(in);
        }

        @Override
        public QuestionData[] newArray(int size) {
            return new QuestionData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(postTitle);
        parcel.writeString(postType);
        parcel.writeString(points);
        parcel.writeTypedList(choices);
        parcel.writeParcelable(attachmentData, 1);
    }
}
