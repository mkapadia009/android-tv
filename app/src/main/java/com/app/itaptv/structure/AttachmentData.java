package com.app.itaptv.structure;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by poonam on 31/8/18.
 */

public class AttachmentData implements Parcelable, Serializable {

    public static final String TYPE_AUDIO = "audio";
    public static final String TYPE_IMAGE = "image";

    public String title = "";
    public String filename = "";
    public String filesize = "";
    public String url = "";
    public String hls_url = "";
    public String wv_hls_url = "";
    public String wv_dash_url = "";
    public String wv_content_id = "";
    public String link = "";
    public String author = "";
    public String description = "";
    public String caption = "";
    public String name = "";
    public String date = "";
    public String mimeType = "";
    public String type = "";
    public String subtype = "";
    public String icon = "";
    public String duration = "";
    public int id;
    public boolean hasAttachmantData;
    // Used for ad. Initialised with -1 because 0 is for starting ad before the content.
    public int play_at = -1;
    public boolean adPlayed = false;
    public String postId = "";

    public AttachmentData() {

    }

    public AttachmentData(JSONObject jsonObject) {
        setData(jsonObject);
    }

    public AttachmentData(JSONObject jsonObject, String postId) {
        this.postId = postId;
        setData(jsonObject);
    }

    protected AttachmentData(Parcel in) {
        title = in.readString();
        filename = in.readString();
        filesize = in.readString();
        url = in.readString();
        hls_url = in.readString();
        wv_hls_url = in.readString();
        wv_dash_url = in.readString();
        wv_content_id = in.readString();
        link = in.readString();
        author = in.readString();
        description = in.readString();
        caption = in.readString();
        name = in.readString();
        date = in.readString();
        mimeType = in.readString();
        type = in.readString();
        subtype = in.readString();
        icon = in.readString();
        duration = in.readString();
        id = in.readInt();
        hasAttachmantData = in.readByte() != 0;
        play_at = in.readInt();
        adPlayed = in.readByte() != 0;
        postId = in.readString();
    }

    public static final Creator<AttachmentData> CREATOR = new Creator<AttachmentData>() {
        @Override
        public AttachmentData createFromParcel(Parcel in) {
            return new AttachmentData(in);
        }

        @Override
        public AttachmentData[] newArray(int size) {
            return new AttachmentData[size];
        }
    };

    private void setData(JSONObject jsonObject) {
        try {
            if (jsonObject.length() == 0) {
                hasAttachmantData = false;
                return;
            }
            hasAttachmantData = true;

            if (jsonObject.has("title")) {
                title = jsonObject.getString("title");
            }

            if (jsonObject.has("filename")) {
                filename = jsonObject.getString("filename");
            }

            if (jsonObject.has("filesize")) {
                filesize = jsonObject.getString("filesize");
            }

            if (jsonObject.has("url")) {
                url = jsonObject.getString("url");
            }

            if (jsonObject.has("hls_url")) {
                hls_url = jsonObject.getString("hls_url");
            }

            if (jsonObject.has("wv_hls_url")) {
                wv_hls_url = jsonObject.getString("wv_hls_url");
            }
            if (jsonObject.has("wv_dash_url")) {
                wv_dash_url = jsonObject.getString("wv_dash_url");
            }

            if (jsonObject.has("wv_content_id")) {
                wv_content_id = jsonObject.getString("wv_content_id");
            }

            if (jsonObject.has("link")) {
                link = jsonObject.getString("link");
            }

            if (jsonObject.has("author")) {
                author = jsonObject.getString("author");
            }

            if (jsonObject.has("description")) {
                description = jsonObject.getString("description");
            }

            if (jsonObject.has("caption")) {
                caption = jsonObject.getString("caption");
            }

            if (jsonObject.has("name")) {
                name = jsonObject.getString("name");
            }

            if (jsonObject.has("ID")) {
                id = jsonObject.getInt("ID");
            }

            if (jsonObject.has("date")) {
                date = jsonObject.getString("date");
            }

            if (jsonObject.has("mime_type")) {
                mimeType = jsonObject.getString("mime_type");
            }

            if (jsonObject.has("type")) {
                type = jsonObject.getString("type");
            }

            if (jsonObject.has("subtype")) {
                subtype = jsonObject.getString("subtype");
            }

            if (jsonObject.has("subtype")) {
                subtype = jsonObject.getString("subtype");
            }

            if (jsonObject.has("icon")) {
                icon = jsonObject.getString("icon");
            }

            if (jsonObject.has("duration")) {
                duration = jsonObject.getString("duration");
            }


        } catch (JSONException e) {

        }
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(filename);
        parcel.writeString(filesize);
        parcel.writeString(url);
        parcel.writeString(hls_url);
        parcel.writeString(wv_hls_url);
        parcel.writeString(wv_dash_url);
        parcel.writeString(wv_content_id);
        parcel.writeString(link);
        parcel.writeString(author);
        parcel.writeString(description);
        parcel.writeString(caption);
        parcel.writeString(name);
        parcel.writeString(date);
        parcel.writeString(mimeType);
        parcel.writeString(type);
        parcel.writeString(subtype);
        parcel.writeString(icon);
        parcel.writeString(duration);
        parcel.writeInt(id);
        parcel.writeByte((byte) (hasAttachmantData ? 1 : 0));
        parcel.writeInt(play_at);
        parcel.writeByte((byte) (adPlayed ? 1 : 0));
        parcel.writeString(postId);
    }
}
