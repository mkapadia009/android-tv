package com.app.itaptv.structure;

import android.os.Parcel;
import android.os.Parcelable;

import com.app.itaptv.utils.Constant;

import org.json.JSONException;
import org.json.JSONObject;

public class AdFieldsData implements Parcelable {
    public String adType = "";
    public String urlType = "";
    public String externalUrl = "";
    public String mediaType = "";
    public String attachment = "";
    // Used for ad. Initialised with -1 because 0 is for starting ad before the content.
    public int play_at = -1;
    public boolean adPlayed = false;
    public String postId = "";
    public String contentType = "";
    public String pageType = "";
    public int contentId;

    public AdFieldsData(JSONObject jsonObject) {
        setData(jsonObject);
    }

    public AdFieldsData(JSONObject jsonObject, String postId) {
        this.postId = postId;
        setData(jsonObject);
    }

    public void setData(JSONObject jsonObject) {
        try {

            if (jsonObject.has("ad_type")) {
                adType = jsonObject.getString("ad_type");
            }
            if (jsonObject.has("url_type")) {
                urlType = jsonObject.getString("url_type");
            }
            if (jsonObject.has("external_url")) {
                externalUrl = jsonObject.getString("external_url");
            }
            if (jsonObject.has("media_type")) {
                mediaType = jsonObject.getString("media_type");
            }
            if (jsonObject.has("attachment")) {
                attachment = jsonObject.getString("attachment");
            }
            if (adType.equalsIgnoreCase(FeedContentData.AD_TYPE_IN_APP)) {
                if (jsonObject.has("content_type")) {
                    contentType = jsonObject.getString("content_type");
                }
                if (contentType.equalsIgnoreCase(Constant.PAGES)) {
                    if (jsonObject.has("page_type")) {
                        pageType = jsonObject.getString("page_type");
                    }
                } else {
                    if (jsonObject.has("item")) {
                        JSONObject jsonObject1 = jsonObject.getJSONObject("item");
                        if (jsonObject1.has("ID")) {
                            contentId = jsonObject1.getInt("ID");
                        }
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected AdFieldsData(Parcel in) {
        adType = in.readString();
        urlType = in.readString();
        externalUrl = in.readString();
        mediaType = in.readString();
        attachment = in.readString();
        play_at = in.readInt();
        adPlayed = in.readByte() != 0;
        contentType = in.readString();
        pageType = in.readString();
        contentId = in.readInt();
    }

    public static final Creator<AdFieldsData> CREATOR = new Creator<AdFieldsData>() {
        @Override
        public AdFieldsData createFromParcel(Parcel in) {
            return new AdFieldsData(in);
        }

        @Override
        public AdFieldsData[] newArray(int size) {
            return new AdFieldsData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(adType);
        dest.writeString(urlType);
        dest.writeString(externalUrl);
        dest.writeString(mediaType);
        dest.writeString(attachment);
        dest.writeInt(play_at);
        dest.writeByte((byte) (adPlayed ? 1 : 0));
        dest.writeString(contentType);
        dest.writeString(pageType);
        dest.writeInt(contentId);
    }
}
