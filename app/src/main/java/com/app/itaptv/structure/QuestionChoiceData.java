package com.app.itaptv.structure;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class QuestionChoiceData implements Parcelable{
    public String text = "";
    public boolean correct = false;

    public QuestionChoiceData(JSONObject jsonObject) {
        try {
            this.text = jsonObject.has("text") ? jsonObject.getString("text") : "";
            this.correct = jsonObject.has("correct") ? jsonObject.getBoolean("correct") : false;

        } catch (JSONException e) {

        }
    }

    protected QuestionChoiceData(Parcel in) {
        text = in.readString();
        correct = in.readByte() != 0;
    }

    public static final Creator<QuestionChoiceData> CREATOR = new Creator<QuestionChoiceData>() {
        @Override
        public QuestionChoiceData createFromParcel(Parcel in) {
            return new QuestionChoiceData(in);
        }

        @Override
        public QuestionChoiceData[] newArray(int size) {
            return new QuestionChoiceData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(text);
        parcel.writeByte((byte) (correct ? 1 : 0));
    }
}
