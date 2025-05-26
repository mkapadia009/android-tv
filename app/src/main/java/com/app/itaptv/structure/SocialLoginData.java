package com.app.itaptv.structure;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by poonam on 23/8/18.
 */


/**
 * This structure is to store social loggedin data returned from facebook or google apis
 * and return the same to hit the app login api
 */

public class SocialLoginData {

    public enum LoginSource {
        FACEBOOK(1),
        GOOGLE(2);

        private int value;

        LoginSource(int value) {
            this.value = value;
        }

        public String getValue() {
            switch (value) {
                case 1:
                    return "fb";
                case 2:
                    return "google";
                default:
                    return "";
            }
        }
    }

    public String id = "";
    public String firstName = "";
    public String lastName = "";
    public String profileImage = "";
    public String email = "";
    public LoginSource loginSource;

    public SocialLoginData(JSONObject user, LoginSource loginSource) {
        this.loginSource = loginSource;
        switch (loginSource) {
            case FACEBOOK:
                facebookUserData(user);
                break;
            case GOOGLE:
                googleUserData(user);
                break;
        }
    }

    public SocialLoginData(GoogleSignInAccount account) {
        loginSource = LoginSource.GOOGLE;
        id = account.getId();
        firstName = account.getGivenName();
        lastName = account.getFamilyName();
        email = account.getEmail();
        profileImage = account.getPhotoUrl().toString();
    }

    private void facebookUserData(JSONObject user) {
        try {
            if (user.has("id")) {
                id = user.getString("id");
            }

            if (user.has("first_name")) {
                firstName = user.getString("first_name");
            }

            if (user.has("last_name")) {
                lastName = user.getString("last_name");
            }

            if (user.has("email")) {
                email = user.getString("email");
            }

            if (user.has("img")) {
                profileImage = user.getString("img");
            }
        } catch (JSONException ignored) {
        }
    }

    private void googleUserData(JSONObject user) {
        try {
            if (user.has("id")) {
                id = user.getString("id");
            }

            if (user.has("givenName")) {
                firstName = user.getString("givenName");
            }

            if (user.has("familyName")) {
                lastName = user.getString("familyName");
            }

            if (user.has("email")) {
                email = user.getString("email");
            }

            if (user.has("photoUrl")) {
                profileImage = user.getString("photoUrl");
            }
        } catch (JSONException ignored) {
        }
    }

}
