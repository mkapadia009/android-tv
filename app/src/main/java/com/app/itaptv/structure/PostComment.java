package com.app.itaptv.structure;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

public class PostComment implements Parcelable
{

    @SerializedName("comment_ID")
    @Expose
    private String commentID;
    @SerializedName("comment_post_ID")
    @Expose
    private String commentPostID;
    @SerializedName("comment_author")
    @Expose
    private String commentAuthor;
    @SerializedName("comment_author_email")
    @Expose
    private String commentAuthorEmail;
    @SerializedName("comment_author_url")
    @Expose
    private String commentAuthorUrl;
    @SerializedName("comment_author_IP")
    @Expose
    private String commentAuthorIP;
    @SerializedName("comment_date")
    @Expose
    private String commentDate;
    @SerializedName("comment_date_gmt")
    @Expose
    private String commentDateGmt;
    @SerializedName("comment_content")
    @Expose
    private String commentContent;
    @SerializedName("comment_karma")
    @Expose
    private String commentKarma;
    @SerializedName("comment_approved")
    @Expose
    private String commentApproved;
    @SerializedName("comment_agent")
    @Expose
    private String commentAgent;
    @SerializedName("comment_type")
    @Expose
    private String commentType;
    @SerializedName("comment_parent")
    @Expose
    private String commentParent;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("author")
    @Expose
    private CommentAuthor author;
    public final static Creator<PostComment> CREATOR = new Creator<PostComment>() {


        @SuppressWarnings({
                "unchecked"
        })
        public PostComment createFromParcel(Parcel in) {
            return new PostComment(in);
        }

        public PostComment[] newArray(int size) {
            return (new PostComment[size]);
        }

    };

    protected PostComment(Parcel in) {
        this.commentID = ((String) in.readValue((String.class.getClassLoader())));
        this.commentPostID = ((String) in.readValue((String.class.getClassLoader())));
        this.commentAuthor = ((String) in.readValue((String.class.getClassLoader())));
        this.commentAuthorEmail = ((String) in.readValue((String.class.getClassLoader())));
        this.commentAuthorUrl = ((String) in.readValue((String.class.getClassLoader())));
        this.commentAuthorIP = ((String) in.readValue((String.class.getClassLoader())));
        this.commentDate = ((String) in.readValue((String.class.getClassLoader())));
        this.commentDateGmt = ((String) in.readValue((String.class.getClassLoader())));
        this.commentContent = ((String) in.readValue((String.class.getClassLoader())));
        this.commentKarma = ((String) in.readValue((String.class.getClassLoader())));
        this.commentApproved = ((String) in.readValue((String.class.getClassLoader())));
        this.commentAgent = ((String) in.readValue((String.class.getClassLoader())));
        this.commentType = ((String) in.readValue((String.class.getClassLoader())));
        this.commentParent = ((String) in.readValue((String.class.getClassLoader())));
        this.userId = ((String) in.readValue((String.class.getClassLoader())));
        this.author = ((CommentAuthor) in.readValue((CommentAuthor.class.getClassLoader())));
    }

    public PostComment() {
    }


    public PostComment(JSONObject jsonObject){
        try {
            if (jsonObject.has("comment_ID")) {
                commentID = jsonObject.getString("comment_ID");
            }
            if (jsonObject.has("comment_post_ID")) {
                commentPostID = jsonObject.getString("comment_post_ID");
            }
            if (jsonObject.has("comment_author")) {
                commentAuthor = jsonObject.getString("comment_author");
            }
            if (jsonObject.has("comment_author_email")) {
                commentAuthorEmail = jsonObject.getString("comment_author_email");
            }
            if (jsonObject.has("comment_author_url")) {
                commentAuthorUrl = jsonObject.getString("comment_author_url");
            }
            if (jsonObject.has("comment_author_IP")) {
                commentAuthorIP = jsonObject.getString("comment_author_IP");
            }
            if (jsonObject.has("comment_date")) {
                commentDate = jsonObject.getString("comment_date");
            }
            if (jsonObject.has("comment_date_gmt")) {
                commentDateGmt = jsonObject.getString("comment_date_gmt");
            }
            if (jsonObject.has("comment_content")) {
                commentContent = jsonObject.getString("comment_content");
            }
            if (jsonObject.has("comment_karma")) {
                commentKarma = jsonObject.getString("comment_karma");
            }
            if (jsonObject.has("comment_approved")) {
                commentApproved = jsonObject.getString("comment_approved");
            }
            if (jsonObject.has("comment_agent")) {
                commentAgent = jsonObject.getString("comment_agent");
            }
            if (jsonObject.has("comment_type")) {
                commentType = jsonObject.getString("comment_type");
            }
            if (jsonObject.has("comment_parent")) {
                commentParent = jsonObject.getString("comment_parent");
            }
            if (jsonObject.has("user_id")) {
                userId = jsonObject.getString("user_id");
            }
            if (jsonObject.has("author")) {
                author = new CommentAuthor(jsonObject.getJSONObject("author"));
            }
        }catch (Exception e){

        }
    }

    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
    }

    public String getCommentPostID() {
        return commentPostID;
    }

    public void setCommentPostID(String commentPostID) {
        this.commentPostID = commentPostID;
    }

    public String getCommentAuthor() {
        return commentAuthor;
    }

    public void setCommentAuthor(String commentAuthor) {
        this.commentAuthor = commentAuthor;
    }

    public String getCommentAuthorEmail() {
        return commentAuthorEmail;
    }

    public void setCommentAuthorEmail(String commentAuthorEmail) {
        this.commentAuthorEmail = commentAuthorEmail;
    }

    public String getCommentAuthorUrl() {
        return commentAuthorUrl;
    }

    public void setCommentAuthorUrl(String commentAuthorUrl) {
        this.commentAuthorUrl = commentAuthorUrl;
    }

    public String getCommentAuthorIP() {
        return commentAuthorIP;
    }

    public void setCommentAuthorIP(String commentAuthorIP) {
        this.commentAuthorIP = commentAuthorIP;
    }

    public String getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(String commentDate) {
        this.commentDate = commentDate;
    }

    public String getCommentDateGmt() {
        return commentDateGmt;
    }

    public void setCommentDateGmt(String commentDateGmt) {
        this.commentDateGmt = commentDateGmt;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public String getCommentKarma() {
        return commentKarma;
    }

    public void setCommentKarma(String commentKarma) {
        this.commentKarma = commentKarma;
    }

    public String getCommentApproved() {
        return commentApproved;
    }

    public void setCommentApproved(String commentApproved) {
        this.commentApproved = commentApproved;
    }

    public String getCommentAgent() {
        return commentAgent;
    }

    public void setCommentAgent(String commentAgent) {
        this.commentAgent = commentAgent;
    }

    public String getCommentType() {
        return commentType;
    }

    public void setCommentType(String commentType) {
        this.commentType = commentType;
    }

    public String getCommentParent() {
        return commentParent;
    }

    public void setCommentParent(String commentParent) {
        this.commentParent = commentParent;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public CommentAuthor getAuthor() {
        return author;
    }

    public void setAuthor(CommentAuthor author) {
        this.author = author;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(commentID);
        dest.writeValue(commentPostID);
        dest.writeValue(commentAuthor);
        dest.writeValue(commentAuthorEmail);
        dest.writeValue(commentAuthorUrl);
        dest.writeValue(commentAuthorIP);
        dest.writeValue(commentDate);
        dest.writeValue(commentDateGmt);
        dest.writeValue(commentContent);
        dest.writeValue(commentKarma);
        dest.writeValue(commentApproved);
        dest.writeValue(commentAgent);
        dest.writeValue(commentType);
        dest.writeValue(commentParent);
        dest.writeValue(userId);
        dest.writeValue(author);
    }

    public int describeContents() {
        return 0;
    }

}
