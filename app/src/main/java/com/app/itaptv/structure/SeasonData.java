package com.app.itaptv.structure;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by poonam on 5/11/18.
 */

public class SeasonData implements Parcelable {

    public String termId = "";
    public String name = "";
    public String taxonomy = "";
    public String description = "";
    public ArrayList<FeedContentData> arrayListFeedContentData = new ArrayList<>();

    public SeasonData(JSONObject jsonObject) {
        try {

            if (jsonObject.has("term_id")) {
                termId = jsonObject.getString("term_id");
            }

            if (jsonObject.has("name")) {
                name = jsonObject.getString("name");
            }

            if (jsonObject.has("taxonomy")) {
                taxonomy = jsonObject.getString("taxonomy");
            }

            if (jsonObject.has("description")) {
                description = jsonObject.getString("description");
            }
            if (jsonObject.has("episodes")) {
                JSONArray jsonArrayEpisodeData = jsonObject.getJSONArray("episodes");
                for (int i = 0; i < jsonArrayEpisodeData.length(); i++) {
                   /* if(jsonArrayEpisodeData.getJSONObject(i).getJSONObject("episode").getString("season").equalsIgnoreCase(termId)) {
                        EpisodeData episodeData = new EpisodeData(jsonArrayEpisodeData.getJSONObject(i));
                        arrayListEpisodeData.add(episodeData);
                    }*/
                    if (jsonArrayEpisodeData.getJSONObject(i).getJSONObject("episode").getString("season").equalsIgnoreCase(termId)) {
                        FeedContentData episodeData = new FeedContentData(jsonArrayEpisodeData.getJSONObject(i), -1);
                        arrayListFeedContentData.add(episodeData);
                    }

                }


            }

        } catch (JSONException e) {

        }
    }

    protected SeasonData(Parcel in) {
        termId = in.readString();
        name = in.readString();
        taxonomy = in.readString();
        description = in.readString();
        arrayListFeedContentData = in.createTypedArrayList(FeedContentData.CREATOR);
    }

    public static final Creator<SeasonData> CREATOR = new Creator<SeasonData>() {
        @Override
        public SeasonData createFromParcel(Parcel in) {
            return new SeasonData(in);
        }

        @Override
        public SeasonData[] newArray(int size) {
            return new SeasonData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(termId);
        parcel.writeString(name);
        parcel.writeString(taxonomy);
        parcel.writeString(description);
        parcel.writeTypedList(arrayListFeedContentData);
    }

    @Override
    public String toString() {
        return name;
    }
}
