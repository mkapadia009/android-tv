package com.app.itaptv.structure;

import org.json.JSONObject;

public class RowCouponsData {

    public int id = 0;
    public String name = "";
    public String image = "";

    public RowCouponsData(JSONObject object) {
        try {
            if (object.has("id")) {
                id = object.getInt("id");
            }
            if (object.has("name")) {
                name = object.getString("name");
            }
            if (object.has("image")) {
                image = object.getString("image");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   /* public RowCouponsData(int id, String name, int resourceId) {
        try {
            this.id = id;
            this.name = name;
            this.image = resourceId;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/


}
