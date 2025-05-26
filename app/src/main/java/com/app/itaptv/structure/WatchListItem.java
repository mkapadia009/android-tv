package com.app.itaptv.structure;

import android.os.Parcel;
import android.os.Parcelable;

import com.app.itaptv.utils.Log;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


import org.json.JSONObject;


public class WatchListItem implements Parcelable
{

    @SerializedName("ID")
    @Expose
    private Integer iD;
    @SerializedName("post_author")
    @Expose
    private String postAuthor;
    @SerializedName("post_date")
    @Expose
    private String postDate;

    @SerializedName("expired_at")
    @Expose
    private String expiredAt;

    @SerializedName("post_date_gmt")
    @Expose
    private String postDateGmt;
    @SerializedName("post_content")
    @Expose
    private String postContent;
    @SerializedName("post_title")
    @Expose
    private String postTitle;
    @SerializedName("post_excerpt")
    @Expose
    private String postExcerpt;
    @SerializedName("post_status")
    @Expose
    private String postStatus;
    @SerializedName("comment_status")
    @Expose
    private String commentStatus;
    @SerializedName("ping_status")
    @Expose
    private String pingStatus;
    @SerializedName("post_password")
    @Expose
    private String postPassword;
    @SerializedName("post_name")
    @Expose
    private String postName;
    @SerializedName("to_ping")
    @Expose
    private String toPing;
    @SerializedName("pinged")
    @Expose
    private String pinged;
    @SerializedName("post_modified")
    @Expose
    private String postModified;
    @SerializedName("post_modified_gmt")
    @Expose
    private String postModifiedGmt;
    @SerializedName("post_content_filtered")
    @Expose
    private String postContentFiltered;
    @SerializedName("post_parent")
    @Expose
    private Integer postParent;
    @SerializedName("guid")
    @Expose
    private String guid;
    @SerializedName("menu_order")
    @Expose
    private Integer menuOrder;
    @SerializedName("post_type")
    @Expose
    private String postType;
    @SerializedName("post_mime_type")
    @Expose
    private String postMimeType;
    @SerializedName("comment_count")
    @Expose
    private String commentCount;
    @SerializedName("filter")
    @Expose
    private String filter;
    @SerializedName("imgUrl")
    @Expose
    private String imgUrl;
    @SerializedName("like_count")
    @Expose
    private String likeCount;
    @SerializedName("view_count")
    @Expose
    private String viewCount;
    @SerializedName("ptype")
    @Expose
    private String ptype;
    @SerializedName("attachment")
    @Expose
    private WatchListItemAttachment attachment;
    @SerializedName("google_short_url")
    @Expose
    private String googleShortUrl;
    @SerializedName("average_rating")
    @Expose
    private String averageRating;
    @SerializedName("trending_score")
    @Expose
    private String trendingScore;
    @SerializedName("flag")
    @Expose
    private String flag;
    @SerializedName("flag_comments")
    @Expose
    private Boolean flagComments;
    @SerializedName("item_status")
    @Expose
    private String itemStatus;
    @SerializedName("coins")
    @Expose
    private String coins;
    @SerializedName("rupees")
    @Expose
    private String rupees;
    @SerializedName("liked_by_me")
    @Expose
    private Boolean likedByMe;

    @SerializedName("can_i_use")
    @Expose
    private Boolean canIUse;

    @SerializedName("is_watchlisted")
    @Expose
    private Boolean isWatchlisted;

    @SerializedName("payment")
    @Expose
    private JSONObject payment;

    @SerializedName("duration")
    @Expose
    private String duration;

    public final static Parcelable.Creator<WatchListItem> CREATOR = new Creator<WatchListItem>() {
        @SuppressWarnings({
                "unchecked"
        })
        public WatchListItem createFromParcel(Parcel in) {
            return new WatchListItem(in);
        }

        public WatchListItem[] newArray(int size) {
            return (new WatchListItem[size]);
        }

    }
            ;

    protected WatchListItem(Parcel in) {
        this.iD = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.postAuthor = ((String) in.readValue((String.class.getClassLoader())));
        this.postDate = ((String) in.readValue((String.class.getClassLoader())));
        this.postDateGmt = ((String) in.readValue((String.class.getClassLoader())));
        this.postContent = ((String) in.readValue((String.class.getClassLoader())));
        this.postTitle = ((String) in.readValue((String.class.getClassLoader())));
        this.postExcerpt = ((String) in.readValue((String.class.getClassLoader())));
        this.postStatus = ((String) in.readValue((String.class.getClassLoader())));
        this.commentStatus = ((String) in.readValue((String.class.getClassLoader())));
        this.pingStatus = ((String) in.readValue((String.class.getClassLoader())));
        this.postPassword = ((String) in.readValue((String.class.getClassLoader())));
        this.postName = ((String) in.readValue((String.class.getClassLoader())));
        this.toPing = ((String) in.readValue((String.class.getClassLoader())));
        this.pinged = ((String) in.readValue((String.class.getClassLoader())));
        this.postModified = ((String) in.readValue((String.class.getClassLoader())));
        this.postModifiedGmt = ((String) in.readValue((String.class.getClassLoader())));
        this.postContentFiltered = ((String) in.readValue((String.class.getClassLoader())));
        this.postParent = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.guid = ((String) in.readValue((String.class.getClassLoader())));
        this.menuOrder = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.postType = ((String) in.readValue((String.class.getClassLoader())));
        this.postMimeType = ((String) in.readValue((String.class.getClassLoader())));
        this.commentCount = ((String) in.readValue((String.class.getClassLoader())));
        this.filter = ((String) in.readValue((String.class.getClassLoader())));
        this.imgUrl = ((String) in.readValue((String.class.getClassLoader())));
        this.likeCount = ((String) in.readValue((String.class.getClassLoader())));
        this.viewCount = ((String) in.readValue((String.class.getClassLoader())));
        this.ptype = ((String) in.readValue((String.class.getClassLoader())));
        this.attachment = ((WatchListItemAttachment) in.readValue((WatchListItemAttachment.class.getClassLoader())));
        this.googleShortUrl = ((String) in.readValue((String.class.getClassLoader())));
        this.averageRating = ((String) in.readValue((String.class.getClassLoader())));
        this.trendingScore = ((String) in.readValue((String.class.getClassLoader())));
        this.flag = ((String) in.readValue((String.class.getClassLoader())));
        this.flagComments = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
        this.itemStatus = ((String) in.readValue((String.class.getClassLoader())));
        this.coins = ((String) in.readValue((String.class.getClassLoader())));
        this.likedByMe = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
        this.canIUse = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
        this.isWatchlisted = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
    }

    public WatchListItem(JSONObject jsonObject){
        try {
            if (jsonObject.has("ID")) {
                this.iD = jsonObject.getInt("ID");
            }
            if (jsonObject.has("post_author")) {
                postAuthor = jsonObject.getString("post_author");
            }
            if (jsonObject.has("post_date")) {
                postDate = jsonObject.getString("post_date");
            }
            if (jsonObject.has("expired_at")) {
                expiredAt = jsonObject.getString("expired_at");
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
            if (jsonObject.has("post_password")) {
                postPassword = jsonObject.getString("post_password");
            }
            if (jsonObject.has("post_name")) {
                postName = jsonObject.getString("post_name");
            }
            if (jsonObject.has("comment_count")) {
                commentCount = jsonObject.getString("comment_count");
            }
            if (jsonObject.has("imgUrl")) {
                imgUrl = jsonObject.getString("imgUrl");
            }
            if (jsonObject.has("like_count")) {
                likeCount = jsonObject.getString("like_count");
            }
            if (jsonObject.has("view_count")) {
                viewCount = jsonObject.getString("view_count");
            }

            if (jsonObject.has("average_rating")) {
                averageRating = jsonObject.getString("average_rating");
            }
            if (jsonObject.has("trending_score")) {
                trendingScore = jsonObject.getString("trending_score");
            }
            if (jsonObject.has("item_status")) {
                itemStatus = jsonObject.getString("item_status");
            }
            if (jsonObject.has("liked_by_me")) {
                likedByMe = jsonObject.getBoolean("liked_by_me");
            }
            if (jsonObject.has("can_i_use")) {
                canIUse = jsonObject.getBoolean("can_i_use");
            }
            if (jsonObject.has("is_watchlisted")) {
                isWatchlisted = jsonObject.getBoolean("is_watchlisted");
            }
            if (jsonObject.has("attachment")) {
                attachment = new WatchListItemAttachment(jsonObject.getJSONObject("attachment"));
            }
            if (jsonObject.has("payment")) {
                payment = jsonObject.getJSONObject("payment");
                Log.e("avinash", "payment = " + String.valueOf(payment));
            }else{
                Log.e("avinash", "payment not available");
            }
            if (jsonObject.has("coins")) {
                coins = jsonObject.getString("coins");
            }
            if (jsonObject.has("rupees")) {
                rupees = jsonObject.getString("rupees");
            }
            Log.e("avinash", "ITem Duration = "+jsonObject.getString("duration"));
            if (jsonObject.has("duration")) {
                duration = jsonObject.getString("duration");
            }
        }catch (Exception e){

        }
    }
    public WatchListItem() {
    }


    public JSONObject getPayment(){
        return payment;
    }
    public void setPayment(JSONObject pay){
        this.payment = pay;
    }

    public Integer getID() {
        return iD;
    }

    public void setID(Integer iD) {
        this.iD = iD;
    }

    public String getPostAuthor() {
        return postAuthor;
    }

    public void setPostAuthor(String postAuthor) {
        this.postAuthor = postAuthor;
    }

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    public String getExpiryDate() {
        return expiredAt;
    }

    public void setExpiryDate(String date) {
        this.expiredAt = date;
    }

    public String getPostDateGmt() {
        return postDateGmt;
    }

    public void setPostDateGmt(String postDateGmt) {
        this.postDateGmt = postDateGmt;
    }

    public String getPostContent() {
        return postContent;
    }

    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostExcerpt() {
        return postExcerpt;
    }

    public void setPostExcerpt(String postExcerpt) {
        this.postExcerpt = postExcerpt;
    }

    public String getPostStatus() {
        return postStatus;
    }

    public void setPostStatus(String postStatus) {
        this.postStatus = postStatus;
    }

    public String getCommentStatus() {
        return commentStatus;
    }

    public void setCommentStatus(String commentStatus) {
        this.commentStatus = commentStatus;
    }

    public String getPingStatus() {
        return pingStatus;
    }

    public void setPingStatus(String pingStatus) {
        this.pingStatus = pingStatus;
    }

    public String getPostPassword() {
        return postPassword;
    }

    public void setPostPassword(String postPassword) {
        this.postPassword = postPassword;
    }

    public String getPostName() {
        return postName;
    }

    public void setPostName(String postName) {
        this.postName = postName;
    }

    public String getToPing() {
        return toPing;
    }

    public void setToPing(String toPing) {
        this.toPing = toPing;
    }

    public String getPinged() {
        return pinged;
    }

    public void setPinged(String pinged) {
        this.pinged = pinged;
    }

    public String getPostModified() {
        return postModified;
    }

    public void setPostModified(String postModified) {
        this.postModified = postModified;
    }

    public String getPostModifiedGmt() {
        return postModifiedGmt;
    }

    public void setPostModifiedGmt(String postModifiedGmt) {
        this.postModifiedGmt = postModifiedGmt;
    }

    public String getPostContentFiltered() {
        return postContentFiltered;
    }

    public void setPostContentFiltered(String postContentFiltered) {
        this.postContentFiltered = postContentFiltered;
    }

    public Integer getPostParent() {
        return postParent;
    }

    public void setPostParent(Integer postParent) {
        this.postParent = postParent;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Integer getMenuOrder() {
        return menuOrder;
    }

    public void setMenuOrder(Integer menuOrder) {
        this.menuOrder = menuOrder;
    }

    public String getPostType() {
        return postType;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }

    public String getPostMimeType() {
        return postMimeType;
    }

    public void setPostMimeType(String postMimeType) {
        this.postMimeType = postMimeType;
    }

    public String getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(String commentCount) {
        this.commentCount = commentCount;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(String likeCount) {
        this.likeCount = likeCount;
    }

    public String getViewCount() {
        return viewCount;
    }

    public void setViewCount(String viewCount) {
        this.viewCount = viewCount;
    }

    public String getPtype() {
        return ptype;
    }

    public void setPtype(String ptype) {
        this.ptype = ptype;
    }

    public WatchListItemAttachment getAttachment() {
        return attachment;
    }

    public void setAttachment(WatchListItemAttachment attachment) {
        this.attachment = attachment;
    }

    public String getGoogleShortUrl() {
        return googleShortUrl;
    }

    public void setGoogleShortUrl(String googleShortUrl) {
        this.googleShortUrl = googleShortUrl;
    }

    public String getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(String averageRating) {
        this.averageRating = averageRating;
    }

    public String getTrendingScore() {
        return trendingScore;
    }

    public void setTrendingScore(String trendingScore) {
        this.trendingScore = trendingScore;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public Boolean getFlagComments() {
        return flagComments;
    }

    public void setFlagComments(Boolean flagComments) {
        this.flagComments = flagComments;
    }

    public String getItemStatus() {
        return itemStatus;
    }

    public void setItemStatus(String itemStatus) {
        this.itemStatus = itemStatus;
    }

    public String getCoins() {
        return coins;
    }

    public void setCoins(String coins) {
        this.coins = coins;
    }

    public String getRupees() {
        return rupees;
    }

    public void setRupees(String rs) {
        this.rupees = rs;
    }

    public Boolean getLikedByMe() {
        return likedByMe;
    }

    public void setLikedByMe(Boolean likedByMe) {
        this.likedByMe = likedByMe;
    }

    public Boolean getCanIUse() {
        return canIUse;
    }

    public void setCanIUse(Boolean canI) {
        this.canIUse = canI;
    }

    public Boolean getIsWatchlisted() {
        return isWatchlisted;
    }

    public void setIsWatchlisted(Boolean isWatchlisted) {
        this.isWatchlisted = isWatchlisted;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String d) {
        this.duration = d;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(iD);
        dest.writeValue(postAuthor);
        dest.writeValue(postDate);
        dest.writeValue(postDateGmt);
        dest.writeValue(postContent);
        dest.writeValue(postTitle);
        dest.writeValue(postExcerpt);
        dest.writeValue(postStatus);
        dest.writeValue(commentStatus);
        dest.writeValue(pingStatus);
        dest.writeValue(postPassword);
        dest.writeValue(postName);
        dest.writeValue(toPing);
        dest.writeValue(pinged);
        dest.writeValue(postModified);
        dest.writeValue(postModifiedGmt);
        dest.writeValue(postContentFiltered);
        dest.writeValue(postParent);
        dest.writeValue(guid);
        dest.writeValue(menuOrder);
        dest.writeValue(postType);
        dest.writeValue(postMimeType);
        dest.writeValue(commentCount);
        dest.writeValue(filter);
        dest.writeValue(imgUrl);
        dest.writeValue(likeCount);
        dest.writeValue(viewCount);
        dest.writeValue(ptype);
        dest.writeValue(attachment);
        dest.writeValue(googleShortUrl);
        dest.writeValue(averageRating);
        dest.writeValue(trendingScore);
        dest.writeValue(flag);
        dest.writeValue(flagComments);
        dest.writeValue(itemStatus);
        dest.writeValue(coins);
        dest.writeValue(likedByMe);
        dest.writeValue(isWatchlisted);
    }

    public int describeContents() {
        return 0;
    }

}