package com.droptak.android.data;

import org.json.JSONException;
import org.json.JSONObject;

public class TakMetadata {

    /** Creates a TakMetadata object from a JSON string */
    public static TakMetadata createFromJSON(String jsonStr) {

        TakMetadata metadata = null;
        try {

            JSONObject j = new JSONObject(jsonStr);

            // Get id, key, and value
            String id = j.getString("id");
            String key = j.getString("key");
            String value = j.getString("value");

            metadata = new TakMetadata(id, key, value);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return metadata;
    }


    private String id, key, value;

    public TakMetadata(String id, String key, String value) {
        this.id = id;
        this.key = key;
        this.value = value;
    }

    public String getID() {
        return this.id;
    }

    public String getKey() {
        return this.key;
    }

    public String getValue() {
        return this.value;
    }

    public void setID(String id) {
        this.id = id;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
