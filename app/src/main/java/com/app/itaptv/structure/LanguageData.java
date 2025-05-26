package com.app.itaptv.structure;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by poonam on 27/8/18.
 */

public class LanguageData {

    public String name = "";
    public String slug = "";
    public String taxonomy = "";
    public String description = "";
    public String filter = "";
    public int termId;
    public int termGroup;
    public int termTaxonomyId;
    public int parent;
    public int count;

    public LanguageData(JSONObject jsonObject) {
        try {

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
                description = jsonObject.getString("description");
            }

            if (jsonObject.has("filter")) {
                filter = jsonObject.getString("filter");
            }

            if (jsonObject.has("term_id")) {
                termId = jsonObject.getInt("term_id");
            }

            if (jsonObject.has("term_group")) {
                termGroup = jsonObject.getInt("term_group");
            }

            if (jsonObject.has("term_taxonomy_id")) {
                termTaxonomyId = jsonObject.getInt("term_taxonomy_id");
            }

            if (jsonObject.has("parent")) {
                parent = jsonObject.getInt("parent");
            }

            if (jsonObject.has("count")) {
                count = jsonObject.getInt("count");
            }

        } catch (JSONException e) {

        }
    }
}
