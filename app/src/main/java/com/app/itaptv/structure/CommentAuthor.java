package com.app.itaptv.structure;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

public class CommentAuthor implements Parcelable {

    @SerializedName("mobile_no")
    @Expose
    private String mobile_no = "";
    @SerializedName("ID")
    @Expose
    private String iD = "";
    @SerializedName("user_login")
    @Expose
    private String userLogin = "";
    @SerializedName("user_nicename")
    @Expose
    private String userNicename = "";
    @SerializedName("user_email")
    @Expose
    private String userEmail = "";
    @SerializedName("user_url")
    @Expose
    private String userUrl = "";
    @SerializedName("user_registered")
    @Expose
    private String userRegistered = "";
    @SerializedName("user_activation_key")
    @Expose
    private String userActivationKey = "";
    @SerializedName("user_status")
    @Expose
    private String userStatus = "";
    @SerializedName("display_name")
    @Expose
    private String displayName = "";
    @SerializedName("first_name")
    @Expose
    private String firstName = "";
    @SerializedName("last_name")
    @Expose
    private String lastName = "";
    @SerializedName("bio")
    @Expose
    private String bio = "";
    @SerializedName("handle")
    @Expose
    private String handle = "";
    @SerializedName("source")
    @Expose
    private String source = "";
    @SerializedName("img")
    @Expose
    private String img = "";
    public final static Creator<CommentAuthor> CREATOR = new Creator<CommentAuthor>() {


        @SuppressWarnings({
                "unchecked"
        })
        public CommentAuthor createFromParcel(Parcel in) {
            return new CommentAuthor(in);
        }

        public CommentAuthor[] newArray(int size) {
            return (new CommentAuthor[size]);
        }

    };

    protected CommentAuthor(Parcel in) {
        this.mobile_no = ((String) in.readValue((String.class.getClassLoader())));
        this.iD = ((String) in.readValue((String.class.getClassLoader())));
        this.userLogin = ((String) in.readValue((String.class.getClassLoader())));
        this.userNicename = ((String) in.readValue((String.class.getClassLoader())));
        this.userEmail = ((String) in.readValue((String.class.getClassLoader())));
        this.userUrl = ((String) in.readValue((String.class.getClassLoader())));
        this.userRegistered = ((String) in.readValue((String.class.getClassLoader())));
        this.userActivationKey = ((String) in.readValue((String.class.getClassLoader())));
        this.userStatus = ((String) in.readValue((String.class.getClassLoader())));
        this.displayName = ((String) in.readValue((String.class.getClassLoader())));
        this.firstName = ((String) in.readValue((String.class.getClassLoader())));
        this.lastName = ((String) in.readValue((String.class.getClassLoader())));
        this.bio = ((String) in.readValue((String.class.getClassLoader())));
        this.handle = ((String) in.readValue((String.class.getClassLoader())));
        this.source = ((String) in.readValue((String.class.getClassLoader())));
        this.img = ((String) in.readValue((String.class.getClassLoader())));
    }

    public CommentAuthor() {
    }

    public CommentAuthor(JSONObject jsonObject) {
        try {
            if (jsonObject.has("mobile_no")) {
                mobile_no = jsonObject.getString("mobile_no");
            }
            if (jsonObject.has("ID")) {
                iD = jsonObject.getString("ID");
            }
            if (jsonObject.has("user_nicename")) {
                userLogin = jsonObject.getString("user_nicename");
            }
            if (jsonObject.has("user_email")) {
                userEmail = jsonObject.getString("user_email");
            }
            if (jsonObject.has("user_url")) {
                userUrl = jsonObject.getString("user_url");
            }
            if (jsonObject.has("user_registered")) {
                userRegistered = jsonObject.getString("user_registered");
            }
            if (jsonObject.has("user_activation_key")) {
                userActivationKey = jsonObject.getString("user_activation_key");
            }
            if (jsonObject.has("user_status")) {
                userStatus = jsonObject.getString("user_status");
            }
            if (jsonObject.has("display_name")) {
                displayName = jsonObject.getString("display_name");
            }
            if (jsonObject.has("first_name")) {
                firstName = jsonObject.getString("first_name");
            }
            if (jsonObject.has("last_name")) {
                lastName = jsonObject.getString("last_name");
            }
            if (jsonObject.has("bio")) {
                bio = jsonObject.getString("bio");
            }
            if (jsonObject.has("handle")) {
                handle = jsonObject.getString("handle");
            }
            if (jsonObject.has("source")) {
                source = jsonObject.getString("source");
            }
            if (jsonObject.has("img")) {
                img = jsonObject.getString("img");
            }

        } catch (Exception e) {

        }
    }

    public String getMobile_no() {
        return mobile_no;
    }

    public void setMobile_no(String iD) {
        this.mobile_no = mobile_no;
    }

    public String getID() {
        return iD;
    }

    public void setID(String iD) {
        this.iD = iD;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public String getUserNicename() {
        return userNicename;
    }

    public void setUserNicename(String userNicename) {
        this.userNicename = userNicename;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserUrl() {
        return userUrl;
    }

    public void setUserUrl(String userUrl) {
        this.userUrl = userUrl;
    }

    public String getUserRegistered() {
        return userRegistered;
    }

    public void setUserRegistered(String userRegistered) {
        this.userRegistered = userRegistered;
    }

    public String getUserActivationKey() {
        return userActivationKey;
    }

    public void setUserActivationKey(String userActivationKey) {
        this.userActivationKey = userActivationKey;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(mobile_no);
        dest.writeValue(iD);
        dest.writeValue(userLogin);
        dest.writeValue(userNicename);
        dest.writeValue(userEmail);
        dest.writeValue(userUrl);
        dest.writeValue(userRegistered);
        dest.writeValue(userActivationKey);
        dest.writeValue(userStatus);
        dest.writeValue(displayName);
        dest.writeValue(firstName);
        dest.writeValue(lastName);
        dest.writeValue(bio);
        dest.writeValue(handle);
        dest.writeValue(source);
        dest.writeValue(img);
    }

    public int describeContents() {
        return 0;
    }

}
