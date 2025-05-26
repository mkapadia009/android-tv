package com.app.itaptv.structure;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CommerceProductData implements Parcelable {

    public String description = "";
    public String link = "";
    public String price = "";
    public String discountPrice = "";
    public String discount = "";
    public String termsAndConditions = "";
    public String ID = "";
    public String openIn = "";
    public String postTitle = "";
    public String postStatus = "";
    public String postName = "";
    public String postType = "";
    public ArrayList<MediaData> arrayMedia = new ArrayList<>();

    public String termId = "";
    public String name = "";
    public String slug = "";
    public String taxonomy = "";
    public String categoryDescription = "";

    public CommerceProductData(JSONObject jsonObject) {
        setData(jsonObject);
    }

    protected CommerceProductData(Parcel in) {
        description = in.readString();
        link = in.readString();
        price = in.readString();
        discountPrice = in.readString();
        discount = in.readString();
        termsAndConditions = in.readString();
        ID = in.readString();
        openIn = in.readString();
        postTitle = in.readString();
        postStatus = in.readString();
        postName = in.readString();
        postType = in.readString();
        arrayMedia = in.createTypedArrayList(MediaData.CREATOR);
        termId = in.readString();
        name = in.readString();
        slug = in.readString();
        taxonomy = in.readString();
        categoryDescription = in.readString();
    }

    public static final Creator<CommerceProductData> CREATOR = new Creator<CommerceProductData>() {
        @Override
        public CommerceProductData createFromParcel(Parcel in) {
            return new CommerceProductData(in);
        }

        @Override
        public CommerceProductData[] newArray(int size) {
            return new CommerceProductData[size];
        }
    };

    public void setData(JSONObject jsonObject) {
        try {
            if (jsonObject.has("category")) {
                setCategory(jsonObject.getJSONObject("category"));
            }
            if (jsonObject.has("desc")) {
                description = jsonObject.getString("desc");
            }
            if (jsonObject.has("link")) {
                link = jsonObject.getString("link");
            }
            if (jsonObject.has("price")) {
                price = jsonObject.getString("price");
            }
            if (jsonObject.has("discount_price")) {
                discountPrice = jsonObject.getString("discount_price");
            }
            if (jsonObject.has("discount")) {
                discount = jsonObject.getString("discount");
            }
            if (jsonObject.has("terms_and_conditions")) {
                termsAndConditions = jsonObject.getString("terms_and_conditions");
            }
            if (jsonObject.has("ID")) {
                ID = jsonObject.getString("ID");
            }
            if (jsonObject.has("open_in")) {
                openIn = jsonObject.getString("open_in");
            }
            if (jsonObject.has("post_title")) {
                postTitle = jsonObject.getString("post_title");
            }
            if (jsonObject.has("post_status")) {
                postStatus = jsonObject.getString("post_status");
            }
            if (jsonObject.has("post_name")) {
                postName = jsonObject.getString("post_name");
            }
            if (jsonObject.has("post_type")) {
                postType = jsonObject.getString("post_type");
            }

            if (jsonObject.has("media")) {
                JSONArray jsonArrayContent = jsonObject.getJSONArray("media");
                for (int i = 0; i < jsonArrayContent.length(); i++) {
                    MediaData mediaData = new MediaData(jsonArrayContent.getJSONObject(i));
                    arrayMedia.add(mediaData);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setCategory(JSONObject jsonObject) {
        try {
            if (jsonObject.has("term_id")) {
                termId = jsonObject.getString("term_id");
            }
            if (jsonObject.has("name")) {
                name = jsonObject.getString("name");
            }
            if (jsonObject.has("slug")) {
                slug = jsonObject.getString("slug");
            }
            if (jsonObject.has("taxonomy")) {
                taxonomy = jsonObject.getString("taxonomy");
            }
            if (jsonObject.has("description")) {
                categoryDescription = jsonObject.getString("description");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(description);
        dest.writeString(link);
        dest.writeString(price);
        dest.writeString(discountPrice);
        dest.writeString(discount);
        dest.writeString(termsAndConditions);
        dest.writeString(ID);
        dest.writeString(openIn);
        dest.writeString(postTitle);
        dest.writeString(postStatus);
        dest.writeString(postName);
        dest.writeString(postType);
        dest.writeTypedList(arrayMedia);
        dest.writeString(termId);
        dest.writeString(name);
        dest.writeString(slug);
        dest.writeString(taxonomy);
        dest.writeString(categoryDescription);
    }
}
