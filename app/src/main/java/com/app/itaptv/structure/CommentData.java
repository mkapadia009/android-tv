package com.app.itaptv.structure;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class CommentData implements Parcelable
{

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("msg")
    @Expose
    private ArrayList<PostComment> comment = new ArrayList<>();
    public final static Parcelable.Creator<CommentData> CREATOR = new Creator<CommentData>() {


        @SuppressWarnings({
                "unchecked"
        })
        public CommentData createFromParcel(Parcel in) {
            return new CommentData(in);
        }

        public CommentData[] newArray(int size) {
            return (new CommentData[size]);
        }

    };

    public CommentData(Parcel in) {
        this.type = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.comment, (PostComment.class.getClassLoader()));
    }

    public CommentData() {
    }

    public CommentData(JSONObject jsonObject) {
        try{
            if (jsonObject.has("type")) {
                type = jsonObject.getString("type");
            }
            if (jsonObject.has("msg")) {
                JSONArray list = jsonObject.getJSONArray("msg");
                for(int i = 0; i < list.length(); i++){
                    comment.add(new PostComment(list.getJSONObject(i)));
                }
            }
        }catch (Exception e){

        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<PostComment> getcomment() {
        return comment;
    }

    public PostComment getcomment(int index){
        return  getcomment().get(index);
    }

    public void setcomment(ArrayList<PostComment> postComment) {
        this.comment = postComment;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(type);
        dest.writeList(comment);
    }

    public int describeContents() {
        return 0;
    }

}

