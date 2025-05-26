package com.app.itaptv.structure;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by poonam on 28/8/18.
 */

public class FeedData implements Parcelable {
    public String title = "";
    public String tabType = "";
    public String viewType = "";
    public String tileShape = "";
    public String showText = "";
    public String backgroundStatus = "";
    public String backgroundImage = "";
    public String feedType = "";
    public String contentType = "";
    public String itemsFrom = "";
    public String name = "";
    public String type = "";
    public String postTitle = "";
    public int id;
    public int termId;
    public int sortOrder;
    public FeedContentData feedContentObjectData = null;
    public ArrayList<FeedContentData> arrayListFeedContent = new ArrayList<>();
    public ArrayList<GameData> array = new ArrayList<>();

    public static final String VIEW_TYPE_SLIDER = "slider";
    public static final String VIEW_TYPE_H_LIST = "h_list";
    public static final String VIEW_TYPE_AD = "ad";

    public static final String ITEMS_FROM_MANUAL = "manual";
    public static final String ITEMS_FROM_AUTO = "auto";

    public JSONArray contentsJSONArray = null;

    public FeedData(JSONObject jsonObject, int position) {
        try {
            if (jsonObject.has("title")) {
                title = jsonObject.getString("title");
            }

            if (jsonObject.has("ID")) {
                id = jsonObject.getInt("ID");
            }

            if (jsonObject.has("term_id")) {
                termId = jsonObject.getInt("term_id");
            }

            if (jsonObject.has("sort_order")) {
                sortOrder = jsonObject.getInt("sort_order");
            }

            if (jsonObject.has("tab_type")) {
                tabType = jsonObject.getString("tab_type");
            }

            if (jsonObject.has("view_type")) {
                viewType = jsonObject.getString("view_type");
            }

            if (jsonObject.has("tile_shape")) {
                tileShape = jsonObject.getString("tile_shape");
            }

            if (jsonObject.has("show_text")) {
                showText = jsonObject.getString("show_text");
            }

            if (jsonObject.has("backgroud_status")) {
                backgroundStatus = jsonObject.getString("backgroud_status");
            }

            if (jsonObject.has("backgroud_image")) {
                backgroundImage = jsonObject.getString("backgroud_image");
            }

            if (jsonObject.has("feed_type")) {
                feedType = jsonObject.getString("feed_type");
            }

            if (jsonObject.has("content_type")) {
                contentType = jsonObject.getString("content_type");
            }

            if (jsonObject.has("items_from")) {
                itemsFrom = jsonObject.getString("items_from");
            }
            if (jsonObject.has("name")) {
                name = jsonObject.getString("name");
            }
            if (jsonObject.has("type")) {
                type = jsonObject.getString("type");
            }
            if (jsonObject.has("post_title")) {
                postTitle = jsonObject.getString("post_title");
            }

            if (jsonObject.has("contents")) {
                contentsJSONArray = jsonObject.getJSONArray("contents");
                String contentTypesubType = contentType.equalsIgnoreCase("") ? type : contentType;
                 /*This switch case is required as for the view type slider and horizontal list we get json array with the key 'contents'
                 whereas for the view type ad we get json object with the key 'contents'*/

                switch (viewType) {
                    case VIEW_TYPE_SLIDER:
                    case VIEW_TYPE_H_LIST:
                        JSONArray jsonArrayContent = jsonObject.getJSONArray("contents");
                        for (int i = 0; i < jsonArrayContent.length(); i++) {
                            FeedContentData feedContentData = new FeedContentData(tabType, contentTypesubType, itemsFrom, jsonArrayContent.getJSONObject(i), position);
                            arrayListFeedContent.add(feedContentData);
                        }
                        break;
                    case VIEW_TYPE_AD:
                        feedContentObjectData = new FeedContentData(tabType, contentTypesubType, itemsFrom, jsonObject.getJSONObject("contents"), position);
                        break;
                }
            }
        } catch (JSONException e) {

        }
    }

    protected FeedData(Parcel in) {
        title = in.readString();
        tabType = in.readString();
        viewType = in.readString();
        tileShape = in.readString();
        showText = in.readString();
        backgroundStatus = in.readString();
        backgroundImage = in.readString();
        feedType = in.readString();
        contentType = in.readString();
        id = in.readInt();
        sortOrder = in.readInt();
    }

    public static final Creator<FeedData> CREATOR = new Creator<FeedData>() {
        @Override
        public FeedData createFromParcel(Parcel in) {
            return new FeedData(in);
        }

        @Override
        public FeedData[] newArray(int size) {
            return new FeedData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(tabType);
        dest.writeString(viewType);
        dest.writeString(tileShape);
        dest.writeString(showText);
        dest.writeString(backgroundImage);
        dest.writeString(backgroundStatus);
        dest.writeString(feedType);
        dest.writeString(contentType);
        dest.writeInt(id);
        dest.writeInt(sortOrder);
    }
}
