package com.app.itaptv.structure;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by poonam on 26/9/18.
 */

public class CouponData implements Serializable {
    public String Id = "";
    public String postTitle = "";
    public String rewardCoins="";
    public String rewardText="";
    public String termsAndConditions="";
    public String imgUrl="";

    public CouponData(JSONObject jsonObject)
    {
        try {
            this.Id=jsonObject.has("ID")? jsonObject.getString("ID"):"";
            this.postTitle=jsonObject.has("post_title")? jsonObject.getString("post_title"):"";
            this.rewardCoins=jsonObject.has("reward_coins")? jsonObject.getString("reward_coins"):"";
            this.rewardText=jsonObject.has("reward_text")? jsonObject.getString("reward_text"):"";
            this.termsAndConditions=jsonObject.has("terms_and_conditions")? jsonObject.getString("terms_and_conditions"):"";
            this.imgUrl=jsonObject.has("imgUrl")? jsonObject.getString("imgUrl"):"";
        }catch (JSONException e)
        {

        }
    }
}
