package com.app.itaptv.structure;

import org.json.JSONException;
import org.json.JSONObject;

public class LeaderBoardData {
    public String value="";
    public String displayName="";
    public String mobileNo="";
    public String img="";
    JSONObject UserObject=null;
    public String ID="";
    public int icon;
    public int index;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public LeaderBoardData(JSONObject object)
    {
        try {
         if(object.has("balance"))
         {
            this.value=object.getString("balance");
         }
         if(object.has("user"))
         {
             UserObject=object.getJSONObject("user");
             if(UserObject.has("display_name"))
             {
                 displayName=UserObject.getString("display_name");
             }
             if(UserObject.has("mobile_no"))
             {
                 mobileNo=UserObject.getString("mobile_no");
             }

             if(UserObject.has("ID"))
             {
                 ID=UserObject.getString("ID");
             }

             if(UserObject.has("img"))
             {
                 img=UserObject.getString("img");
             }
         }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
