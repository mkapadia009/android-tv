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

public class FeedCombinedData implements Parcelable {
    public static final String VIEW_TYPE_SLIDER = "slider";
    public static final String VIEW_TYPE_BIG_SLIDER = "big_slider";
    public static final String VIEW_TYPE_PROMOTION = "promotion";
    public static final String VIEW_TYPE_H_LIST = "h_list";
    public static final String VIEW_TYPE_AD = "ad";
    public static final String VIEW_TYPE_TABS = "tabs";
    public static final String FEED_TYPE_CONTENT = "content";
    public static final String FEED_TYPE_GAME = "game";
    public static final String FEED_TYPE_BUY = "buy";
    public static final String FEED_TYPE_CHANNEL = "channel";
    public static final String FEED_TYPE_PROMOTION = "promotion";
    public static final String FEED_TYPE_FIREWORKS = "fireworks";
    public static final String ITEMS_FROM_MANUAL = "manual";
    public static final String ITEMS_FROM_AUTO = "auto";
    public static final String FEED_TYPE_AD = "marketing";
    public static final String GAME_TAB = "GameTab";
    public static final String PREMIUM_TAB = "PremiumTab";
    public static final String SUBSCRIPTION_TAB = "SubscriptionTab";
    public static final String NORMAL = "Normal";
    public static final Creator<FeedCombinedData> CREATOR = new Creator<FeedCombinedData>() {
        @Override
        public FeedCombinedData createFromParcel(Parcel in) {
            return new FeedCombinedData(in);
        }

        @Override
        public FeedCombinedData[] newArray(int size) {
            return new FeedCombinedData[size];
        }
    };
    public String title = "";
    public String contentOrientation = "";
    public String tabType = "";
    public String viewType = "";
    public boolean isCategoryCoin = false;
    public int categoryCoinAmount;
    public String tileShape = "";
    public String showText = "";
    public String backgroundStatus = "";
    public String backgroundImage = "";
    public String arrowLinking = "";
    public String promotionImage = "";
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
    public ArrayList<FeedContentData> arrayListBuyFeedContent = new ArrayList<>();
    public ArrayList<FeedContentData> arrayListChannelFeedContent = new ArrayList<>();
    public ArrayList<GameData> arrayListGameData = new ArrayList<>();
    public ArrayList<HomeSliderObject> homeSliderObjectArrayList = new ArrayList<>();
    public ArrayList<FeedCombinedData> bigSliderObjectArrayList = new ArrayList<>();
    public JSONArray contentsJSONArray = null;

    public FeedCombinedData(JSONObject jsonObject, int position) {
        try {
            if (jsonObject.has("title")) {
                title = jsonObject.getString("title");
            }
            if (jsonObject.has("content_orientation")) {
                contentOrientation = jsonObject.getString("content_orientation");
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

            if (jsonObject.has("view_type")) {
                viewType = jsonObject.getString("view_type");
            }

            if (jsonObject.has("isCategoryCoin")) {
                isCategoryCoin = jsonObject.getBoolean("isCategoryCoin");
            }

            if (jsonObject.has("category_coin_amount")) {
                categoryCoinAmount = jsonObject.getInt("category_coin_amount");
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

            if (jsonObject.has("arrow_linking")) {
                arrowLinking = jsonObject.getString("arrow_linking");
            }

            if (jsonObject.has("promotion_image") && !jsonObject.getString("promotion_image").isEmpty()) {
                promotionImage = jsonObject.getString("promotion_image");
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
                    case VIEW_TYPE_BIG_SLIDER:
                    case VIEW_TYPE_H_LIST:
                        /*JSONArray jsonArrayContent = jsonObject.getJSONArray("contents");
                        for (int i = 0; i < jsonArrayContent.length(); i++) {
                            FeedContentData feedContentData = new FeedContentData(tabType, contentTypesubType, itemsFrom, jsonArrayContent.getJSONObject(i), position);
                            arrayListFeedContent.add(feedContentData);
                        }*/
                        switch (feedType) {
                            case FEED_TYPE_CONTENT:
                                JSONArray jsonArrayContent = jsonObject.getJSONArray("contents");
                                if (jsonArrayContent.length() > 0) {
                                    Object obj = jsonArrayContent.get(0);
                                    JSONArray toBeParsed = new JSONArray();
                                    if (obj instanceof JSONArray) {
                                        toBeParsed = (JSONArray) obj;
                                    }
                                    if (obj instanceof JSONObject) {
                                        toBeParsed = jsonArrayContent;
                                    }
                                    if (toBeParsed.length() > 0) {
                                        for (int i = 0; i < toBeParsed.length(); i++) {
                                            FeedContentData feedContentData = new FeedContentData(tabType, contentTypesubType, itemsFrom,
                                                    toBeParsed.getJSONObject(i), position);
                                            if (feedContentData.contentOrientation.equalsIgnoreCase("null")) {
                                                feedContentData.contentOrientation = contentOrientation;
                                            } else if (feedContentData.contentOrientation.isEmpty()) {
                                                feedContentData.contentOrientation = contentOrientation;
                                            }
                                            feedContentData.categoryId = id;
                                            feedContentData.isCategoryCoin = isCategoryCoin;
                                            arrayListFeedContent.add(feedContentData);
                                        }
                                    }
                                }
                            case FEED_TYPE_BUY:
                                JSONArray jsonArrayContent1 = jsonObject.getJSONArray("contents");
                                if (jsonArrayContent1.length() > 0) {
                                    Object obj = jsonArrayContent1.get(0);
                                    JSONArray toBeParsed = new JSONArray();
                                    if (obj instanceof JSONArray) {
                                        toBeParsed = (JSONArray) obj;
                                    }
                                    if (obj instanceof JSONObject) {
                                        toBeParsed = jsonArrayContent1;
                                    }
                                    if (toBeParsed.length() > 0) {
                                        for (int i = 0; i < toBeParsed.length(); i++) {
                                            FeedContentData feedContentData = new FeedContentData(tabType,
                                                    contentTypesubType, itemsFrom, toBeParsed.getJSONObject(i), position);
                                            if (feedContentData.contentOrientation.equalsIgnoreCase("null")) {
                                                feedContentData.contentOrientation = contentOrientation;
                                            } else if (feedContentData.contentOrientation.isEmpty()) {
                                                feedContentData.contentOrientation = contentOrientation;
                                            }
                                            feedContentData.categoryId = id;
                                            feedContentData.isCategoryCoin = isCategoryCoin;
                                            arrayListBuyFeedContent.add(feedContentData);
                                        }
                                    }
                                }
                            case FEED_TYPE_CHANNEL:
                                JSONArray jsonArrayContent4 = jsonObject.getJSONArray("contents");
                                if (jsonArrayContent4.length() > 0) {
                                    Object obj = jsonArrayContent4.get(0);
                                    JSONArray toBeParsed = new JSONArray();
                                    if (obj instanceof JSONArray) {
                                        toBeParsed = (JSONArray) obj;
                                    }
                                    if (obj instanceof JSONObject) {
                                        toBeParsed = jsonArrayContent4;
                                    }
                                    if (toBeParsed.length() > 0) {
                                        for (int i = 0; i < toBeParsed.length(); i++) {
                                            FeedContentData feedContentData = new FeedContentData(tabType,
                                                    contentTypesubType, itemsFrom, toBeParsed.getJSONObject(i), position);
                                            if (feedContentData.contentOrientation.equalsIgnoreCase("null")) {
                                                feedContentData.contentOrientation = contentOrientation;
                                            } else if (feedContentData.contentOrientation.isEmpty()) {
                                                feedContentData.contentOrientation = contentOrientation;
                                            }
                                            feedContentData.categoryId = id;
                                            feedContentData.isCategoryCoin = isCategoryCoin;
                                            arrayListChannelFeedContent.add(feedContentData);
                                        }
                                    }
                                }
                            case FEED_TYPE_GAME:
                                JSONArray jsonArrayContent2 = jsonObject.getJSONArray("contents");
                                if (jsonArrayContent2.length() > 0) {
                                    Object obj = jsonArrayContent2.get(0);
                                    JSONArray toBeParsed = new JSONArray();
                                    if (obj instanceof JSONArray) {
                                        toBeParsed = (JSONArray) obj;
                                    }
                                    if (obj instanceof JSONObject) {
                                        toBeParsed = jsonArrayContent2;
                                    }
                                    if (toBeParsed.length() > 0) {
                                        for (int i = 0; i < toBeParsed.length(); i++) {
                                            GameData gameData = new GameData(toBeParsed.getJSONObject(i), "");
                                            arrayListGameData.add(gameData);
                                        }
                                    }
                                }
                            case FEED_TYPE_PROMOTION:
                                JSONArray jsonArrayContent3 = jsonObject.getJSONArray("contents");
                                if (jsonArrayContent3.length() > 0) {
                                    Object obj = jsonArrayContent3.get(0);
                                    JSONArray toBeParsed = new JSONArray();
                                    if (obj instanceof JSONArray) {
                                        toBeParsed = (JSONArray) obj;
                                    }
                                    if (obj instanceof JSONObject) {
                                        toBeParsed = jsonArrayContent3;
                                    }
                                    if (toBeParsed.length() > 0) {
                                        for (int i = 0; i < toBeParsed.length(); i++) {
                                            HomeSliderObject homeSliderObject = new HomeSliderObject(toBeParsed.getJSONObject(i), i);
                                            homeSliderObjectArrayList.add(homeSliderObject);
                                        }
                                    }
                                }
                                break;

                            case FEED_TYPE_AD:
                                JSONArray jsonArrayContent5 = jsonObject.getJSONArray("contents");
                                if (jsonArrayContent5.length() > 0) {
                                    Object obj = jsonArrayContent5.get(0);
                                    JSONArray toBeParsed = new JSONArray();
                                    if (obj instanceof JSONArray) {
                                        toBeParsed = (JSONArray) obj;
                                    }
                                    if (obj instanceof JSONObject) {
                                        toBeParsed = jsonArrayContent5;
                                    }
                                    if (toBeParsed.length() > 0) {
                                        for (int i = 0; i < toBeParsed.length(); i++) {
                                            HomeSliderObject homeSliderObject = new HomeSliderObject(toBeParsed.getJSONObject(i), i);
                                            homeSliderObjectArrayList.add(homeSliderObject);
                                        }
                                    }
                                }
                        }
                        break;
                    case VIEW_TYPE_TABS:
                        JSONArray jsonArrayContent6 = jsonObject.getJSONArray("contents");
                        if (jsonArrayContent6.length() > 0) {
                            Object obj = jsonArrayContent6.get(0);
                            JSONArray toBeParsed = new JSONArray();
                            if (obj instanceof JSONArray) {
                                toBeParsed = (JSONArray) obj;
                            }
                            if (obj instanceof JSONObject) {
                                toBeParsed = jsonArrayContent6;
                            }
                            if (toBeParsed.length() > 0) {
                                for (int i = 0; i < toBeParsed.length(); i++) {
                                    FeedCombinedData feedCombinedData = new FeedCombinedData(jsonArrayContent6.getJSONObject(i), position + i);
                                    bigSliderObjectArrayList.add(feedCombinedData);
                                }
                            }
                        }
                        break;

                    case VIEW_TYPE_AD:
                        feedContentObjectData = new FeedContentData(tabType, contentTypesubType, itemsFrom, jsonObject.getJSONObject("contents"), position);
                        break;

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected FeedCombinedData(Parcel in) {
        title = in.readString();
        contentOrientation = in.readString();
        tabType = in.readString();
        viewType = in.readString();
        isCategoryCoin = in.readByte() != 0;
        categoryCoinAmount = in.readInt();
        tileShape = in.readString();
        showText = in.readString();
        backgroundStatus = in.readString();
        backgroundImage = in.readString();
        arrowLinking = in.readString();
        promotionImage = in.readString();
        feedType = in.readString();
        contentType = in.readString();
        id = in.readInt();
        sortOrder = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(contentOrientation);
        dest.writeString(tabType);
        dest.writeString(viewType);
        dest.writeByte((byte) (isCategoryCoin ? 1 : 0));
        dest.writeInt(categoryCoinAmount);
        dest.writeString(tileShape);
        dest.writeString(showText);
        dest.writeString(backgroundImage);
        dest.writeString(arrowLinking);
        dest.writeString(backgroundStatus);
        dest.writeString(promotionImage);
        dest.writeString(feedType);
        dest.writeString(contentType);
        dest.writeInt(id);
        dest.writeInt(sortOrder);
    }
}
