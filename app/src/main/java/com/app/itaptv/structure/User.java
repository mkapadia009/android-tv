package com.app.itaptv.structure;

import org.json.JSONObject;

public class User {
    public int userId = 0;
    public String userLogin = "";
    public String displayName = "";
    public String userEmail = "";
    public String mobile = "";
    public String img = "";
    public String avatar = "";
    public String fbProfile = "";
    public String googleProfile = "";
    public String referralCode = "";
    public String avatarBodyPicture = "";
    public String countryCode = "";
    public String currencyCode = "";
    public String countryId = "";
    public String currency = "";
    public boolean isVerified = false;

    public User(JSONObject object) {
        try {
            if (object.has("ID") && !object.isNull("ID")) {
                userId = object.getInt("ID");
            }
            if (object.has("user_login") && !object.isNull("user_login")) {
                userLogin = object.getString("user_login");
            }
            if (object.has("user_email") && !object.isNull("user_email")) {
                userEmail = object.getString("user_email");
            }
            if (object.has("mobile_no") && !object.isNull("mobile_no")) {
                mobile = object.getString("mobile_no");
            }
            if (object.has("display_name") && !object.isNull("display_name")) {
                displayName = object.getString("display_name");
            }
            if (object.has("img") && !object.isNull("img")) {
                img = object.getString("img");
            }
            if (object.has("avatar") && !object.isNull("avatar")) {
                avatar = object.getString("avatar");
            }
            if (object.has("fb_profile") && !object.isNull("fb_profile")) {
                fbProfile = object.getString("fb_profile");
            }
            if (object.has("google_profile") && !object.isNull("google_profile")) {
                googleProfile = object.getString("google_profile");
            }
            if (object.has("referrer_code") && !object.isNull("referrer_code")) {
                referralCode = object.getString("referrer_code");
            }

            if (object.has("avatar_body_picture") && !object.isNull("avatar_body_picture")) {
                avatarBodyPicture = object.getString("avatar_body_picture");
            }

            if (object.has("calling_code") && !object.isNull("calling_code")) {
                countryCode = object.getString("calling_code");
            }

            if (object.has("currency_symbol") && !object.isNull("currency_symbol")) {
                currency = object.getString("currency_symbol");
            }

            if (object.has("currency_code") && !object.isNull("currency_code")) {
                currencyCode = object.getString("currency_code");
            }
            if (object.has("country_id") && !object.isNull("country_id")) {
                countryId = object.getString("country_id");
            }
            if (object.has("is_verified") && !object.isNull("is_verified")) {
                isVerified = object.getBoolean("is_verified");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
