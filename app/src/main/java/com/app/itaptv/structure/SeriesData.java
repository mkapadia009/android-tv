package com.app.itaptv.structure;

import android.os.Parcel;
import android.os.Parcelable;

import com.app.itaptv.utils.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by poonam on 5/11/18.
 */

public class SeriesData implements Parcelable,Serializable{
    public String postId = "";
    public String postAuthor = "";
    public String postDate = "";
    public String postDateGmt = "";
    public String postContent = "";
    public String postTitle = "";
    public String postExcerpt = "";
    public String postStatus = "";
    public String commentStatus = "";
    public String pingStatus = "";
    public String postName = "";
    public String toPing = "";
    public String pinged = "";
    public String postModified = "";
    public String postModifiedGmt = "";
    public String postContentFiltered = "";
    public String guid = "";
    public String postType = "";
    public String postMimeType = "";
    public String filter = "";
    public int commentCount;// String from API
    public String imgUrl="";
    public String coins="";

    public ArrayList<SeasonData> arrayListSeasonData = new ArrayList<>();

    public SeriesData(JSONObject jsonObject) {
        try {
            // Series
            if (jsonObject.has("series")) {

                JSONObject jsonObjectSeries = jsonObject.getJSONObject("series");
                if (jsonObjectSeries.has("ID")) {
                    postId = jsonObjectSeries.getString("ID");
                }

                if (jsonObjectSeries.has("post_author")) {
                    postAuthor = jsonObjectSeries.getString("post_author");
                }

                if (jsonObjectSeries.has("post_date")) {
                    postDate = jsonObjectSeries.getString("post_date");
                }

                if (jsonObjectSeries.has("post_date_gmt")) {
                    postDateGmt = jsonObjectSeries.getString("post_date_gmt");
                }

                if (jsonObjectSeries.has("post_content")) {
                    postContent = jsonObjectSeries.getString("post_content");
                }

                if (jsonObjectSeries.has("post_title")) {
                    postTitle = jsonObjectSeries.getString("post_title");
                }

                if (jsonObjectSeries.has("post_excerpt")) {
                    postExcerpt = jsonObjectSeries.getString("post_excerpt");
                }

                if (jsonObjectSeries.has("post_status")) {
                    postStatus = jsonObjectSeries.getString("post_status");
                }

                if (jsonObjectSeries.has("comment_status")) {
                    commentStatus = jsonObjectSeries.getString("comment_status");
                }

                if (jsonObjectSeries.has("ping_status")) {
                    pingStatus = jsonObjectSeries.getString("ping_status");
                }

                if (jsonObjectSeries.has("post_name")) {
                    postName = jsonObjectSeries.getString("post_name");
                }

                if (jsonObjectSeries.has("to_ping")) {
                    toPing = jsonObjectSeries.getString("to_ping");
                }

                if (jsonObjectSeries.has("pinged")) {
                    pinged = jsonObjectSeries.getString("pinged");
                }

                if (jsonObjectSeries.has("post_modified")) {
                    postModified = jsonObjectSeries.getString("post_modified");
                }

                if (jsonObjectSeries.has("post_modified_gmt")) {
                    postModifiedGmt = jsonObjectSeries.getString("post_modified_gmt");
                }

                if (jsonObjectSeries.has("post_content_filtered")) {
                    postContentFiltered = jsonObjectSeries.getString("post_content_filtered");
                }

                if (jsonObjectSeries.has("guid")) {
                    guid = jsonObjectSeries.getString("guid");
                }

                if (jsonObjectSeries.has("post_type")) {
                    postType = jsonObjectSeries.getString("post_type");
                }

                if (jsonObjectSeries.has("post_mime_type")) {
                    postMimeType = jsonObjectSeries.getString("post_mime_type");
                }

                if (jsonObjectSeries.has("comment_count")) {
                    commentCount = getValue(jsonObjectSeries.getInt("comment_count"));
                }

                if (jsonObjectSeries.has("filter")) {
                    filter = jsonObjectSeries.getString("filter");
                }

                if(jsonObjectSeries.has("imgUrl"))
                {
                    imgUrl=jsonObjectSeries.getString("imgUrl");
                }

                if(jsonObjectSeries.has("coins"))
                {
                    coins=jsonObjectSeries.getString("coins");
                }

            }

            // Seasons
            if (jsonObject.has("seasons")) {
                JSONArray jsonArraySeasons = jsonObject.getJSONArray("seasons");
                JSONArray jsonArrayEpisodes = jsonObject.getJSONArray("episodes");
                for (int i = 0; i < jsonArraySeasons.length(); i++) {
                    JSONObject jsonObjectSeason = jsonArraySeasons.getJSONObject(i);
                    JSONArray jsonArrayEp = new JSONArray();
                    for (int j = 0; j < jsonArrayEpisodes.length(); j++) {
                        JSONObject jsonObjectEpisode = jsonArrayEpisodes.getJSONObject(j).getJSONObject("episode");
                        String termId = jsonObjectSeason.getString("term_id");
                        String seasonId = jsonObjectEpisode.getString("season");
                        if (termId.equals(seasonId)) {
                            jsonArrayEp.put(jsonObjectEpisode);
                        }
                    }
                    jsonObjectSeason.put("episodes", jsonArrayEpisodes);
                    SeasonData seasonData = new SeasonData(jsonObjectSeason);
                    arrayListSeasonData.add(seasonData);

                }

            }

        } catch (JSONException e) {
            Log.e("JSON Exception--",e.getMessage());

        }
    }

    protected SeriesData(Parcel in) {
        postId = in.readString();
        postAuthor = in.readString();
        postDate = in.readString();
        postDateGmt = in.readString();
        postContent = in.readString();
        postTitle = in.readString();
        postExcerpt = in.readString();
        postStatus = in.readString();
        commentStatus = in.readString();
        pingStatus = in.readString();
        postName = in.readString();
        toPing = in.readString();
        pinged = in.readString();
        postModified = in.readString();
        postModifiedGmt = in.readString();
        postContentFiltered = in.readString();
        guid = in.readString();
        postType = in.readString();
        postMimeType = in.readString();
        filter = in.readString();
        commentCount = in.readInt();
        imgUrl = in.readString();
        coins=in.readString();
        in.readTypedList(arrayListSeasonData, SeasonData.CREATOR);
    }

    public static final Creator<SeriesData> CREATOR = new Creator<SeriesData>() {
        @Override
        public SeriesData createFromParcel(Parcel in) {
            return new SeriesData(in);
        }

        @Override
        public SeriesData[] newArray(int size) {
            return new SeriesData[size];
        }
    };

    // Returns object as integer value
    private int getValue(Object object) {
        if (object instanceof String) {
            if (object.equals("")) {
                return 0;
            }
            return Integer.parseInt((String) object);
        } else if (object instanceof Integer) {
            return (int) object;
        }
        return 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(postId);
        parcel.writeString(postAuthor);
        parcel.writeString(postDate);
        parcel.writeString(postDateGmt);
        parcel.writeString(postContent);
        parcel.writeString(postTitle);
        parcel.writeString(postExcerpt);
        parcel.writeString(postStatus);
        parcel.writeString(commentStatus);
        parcel.writeString(pingStatus);
        parcel.writeString(postName);
        parcel.writeString(toPing);
        parcel.writeString(pinged);
        parcel.writeString(postModified);
        parcel.writeString(postModifiedGmt);
        parcel.writeString(postContentFiltered);
        parcel.writeString(guid);
        parcel.writeString(postType);
        parcel.writeString(postMimeType);
        parcel.writeString(filter);
        parcel.writeInt(commentCount);
        parcel.writeString(imgUrl);
        parcel.writeString(coins);
        parcel.writeTypedList(arrayListSeasonData);
    }
}
