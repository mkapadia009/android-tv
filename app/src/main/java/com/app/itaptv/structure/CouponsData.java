package com.app.itaptv.structure;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class CouponsData {

    public int id = 0;
    public String title = "";
    public ArrayList<RowCouponsData> rowCouponsDataArrayList = new ArrayList<>();


    public CouponsData(JSONObject jsonObject) {
        try {
            if (jsonObject.has("id")) {
                id = jsonObject.getInt("id");
            }
            if (jsonObject.has("title")) {
                title = jsonObject.getString("title");
            }
            if (jsonObject.has("couponsData")) {
                JSONArray array = jsonObject.getJSONArray("couponsData");
                for (int i = 0; i < array.length(); i++) {
                    rowCouponsDataArrayList.add(new RowCouponsData(array.getJSONObject(i)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CouponsData(int id, String title, ArrayList<RowCouponsData> rowCouponsDataArrayList) {
        try {
            this.id = id;
            this.title = title;
            this.rowCouponsDataArrayList.clear();
            this.rowCouponsDataArrayList.addAll(rowCouponsDataArrayList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
