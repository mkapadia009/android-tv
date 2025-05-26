package com.app.itaptv.structure;

import com.app.itaptv.custom_interface.PlayableMedia;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by poonam on 30/8/18.
 */

public class ItemData implements PlayableMedia, Serializable {

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
    public String imgUrl = "";
    public String googleShortUrl = "";
    public String averageRating = "";
    public String coins = "";
    public int postParent;
    public int menuOrder;
    public int commentCount;// String from API
    public int ptype; // String from API
    public int trendingScore; //String from API
    public int flag; // String from API
    public AttachmentData attachmentData = null;
    public QuestionData questionData = null;

    public ItemData(JSONObject jsonObject) {
        try {
            if (jsonObject.has("ID")) {
                postId = jsonObject.getString("ID");
            }

            if (jsonObject.has("post_author")) {
                postAuthor = jsonObject.getString("post_author");
            }

            if (jsonObject.has("post_date_gmt")) {
                postDateGmt = jsonObject.getString("post_date_gmt");
            }
            if (jsonObject.has("post_content")) {
                postContent = jsonObject.getString("post_content");
            }

            if (jsonObject.has("post_title")) {
                postTitle = jsonObject.getString("post_title");
            }

            if (jsonObject.has("post_excerpt")) {
                postExcerpt = jsonObject.getString("post_excerpt");
            }

            if (jsonObject.has("post_status")) {
                postStatus = jsonObject.getString("post_status");
            }

            if (jsonObject.has("comment_status")) {
                commentStatus = jsonObject.getString("comment_status");
            }

            if (jsonObject.has("ping_status")) {
                pingStatus = jsonObject.getString("ping_status");
            }

            if (jsonObject.has("post_name")) {
                postName = jsonObject.getString("post_name");
            }

            if (jsonObject.has("to_ping")) {
                toPing = jsonObject.getString("to_ping");
            }

            if (jsonObject.has("pinged")) {
                pinged = jsonObject.getString("pinged");
            }

            if (jsonObject.has("post_modified")) {
                postModified = jsonObject.getString("post_modified");
            }

            if (jsonObject.has("post_modified_gmt")) {
                postModifiedGmt = jsonObject.getString("post_modified_gmt");
            }

            if (jsonObject.has("post_content_filtered")) {
                postContentFiltered = jsonObject.getString("post_content_filtered");
            }

            if (jsonObject.has("guid")) {
                guid = jsonObject.getString("guid");
            }

            if (jsonObject.has("post_type")) {
                postType = jsonObject.getString("post_type");
            }

            if (jsonObject.has("post_mime_type")) {
                postMimeType = jsonObject.getString("post_mime_type");
            }

            if (jsonObject.has("filter")) {
                filter = jsonObject.getString("filter");
            }

            if (jsonObject.has("imgUrl")) {
                /*imgUrl = channelImageUrl.equals("") ? jsonObject.getString("imgUrl") : channelImageUrl;*/
                imgUrl = jsonObject.getString("imgUrl");
            }

            if (jsonObject.has("google_short_url")) {
                googleShortUrl = jsonObject.getString("google_short_url");
            }

            if (jsonObject.has("average_rating")) {
                averageRating = jsonObject.getString("average_rating");
            }

            if (jsonObject.has("coins")) {
                coins = jsonObject.getString("coins");
            }


            if (jsonObject.has("post_parent")) {
                postParent = jsonObject.getInt("post_parent");
            }

            if (jsonObject.has("menu_order")) {
                menuOrder = jsonObject.getInt("menu_order");
            }

            if (jsonObject.has("comment_count")) {
                commentCount = getValue(jsonObject.get("comment_count"));
            }

            if (jsonObject.has("ptype")) {
                ptype = getValue(jsonObject.get("ptype"));
            }

            if (jsonObject.has("trending_score")) {
                trendingScore = getValue(jsonObject.get("trending_score"));
            }

            if (jsonObject.has("flag")) {
                flag = getValue(jsonObject.get("flag"));
            }

            if (jsonObject.has("attachment")) {
                attachmentData = new AttachmentData(jsonObject.getJSONObject("attachment"));
            }

            if (jsonObject.has("question")) {
                questionData = new QuestionData(jsonObject.getJSONObject("question"));
            }

        } catch (JSONException e) {

        }

    }

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
    public String getMediaId() {
        if (attachmentData != null) {
            return String.valueOf(attachmentData.id);
        }
        return null;
    }

    @Override
    public String getMediaTitle() {
        if (attachmentData != null) {
            return postTitle;
        }
        return null;
    }

    @Override
    public String getMediaDesc() {
        if (attachmentData != null) {
            return postContent;
        }
        return null;
    }

    @Override
    public String getMediaImageUrl() {
        if (attachmentData != null) {
            return imgUrl;
        }
        return null;
    }

    @Override
    public String getMediaUrl() {
        if (attachmentData != null) {
            return attachmentData.url;
        }
        return null;
    }

    @Override
    public String getTeaserUrl() {
        return null;
    }
}
