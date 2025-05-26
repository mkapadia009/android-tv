package com.app.itaptv.structure;

import com.app.itaptv.custom_interface.PlayableMedia;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by poonam on 5/11/18.
 */

public class EpisodeData implements PlayableMedia {

    public String postTitle = "";
    public String ID="";
    public String imgUrl="";
    public String postType="";
    public String coins="";
    public String postContent="";
    public AttachmentData attachmentData = null;
    public QuestionData questionData = null;



    public EpisodeData(JSONObject jsonObject) {
        try {

            if (jsonObject.has("post_title")) {
                postTitle = jsonObject.getString("post_title");
            }
            if(jsonObject.has("ID"))
            {
                ID=jsonObject.getString("ID");
            }

            if(jsonObject.has("imgUrl"))
            {
                imgUrl=jsonObject.getString("imgUrl");
            }

            if(jsonObject.has("post_type"))
            {
                postType=jsonObject.getString("post_type");
            }
            if (jsonObject.has("post_content")) {
                postContent = jsonObject.getString("post_content");
            }
            if(jsonObject.has("attachment"))
            {
                attachmentData = new AttachmentData(jsonObject.getJSONObject("attachment"));
            }

            if(jsonObject.has("question"))
            {
                questionData=new QuestionData(jsonObject.getJSONObject("question"));
            }
            if(jsonObject.has("coins"))
            {
                coins=jsonObject.getString("coins");
            }
        } catch (JSONException e) {

        }

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
