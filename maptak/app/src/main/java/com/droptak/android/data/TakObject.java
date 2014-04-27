package com.droptak.android.data;
import android.util.Log;

import com.droptak.android.activities.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;



public class TakObject {

    /** Parses JSON from the server and returns a map object */
    public static TakObject createFromJSON(String jsonStr) {
        Log.d("debug","tak jsonStr="+jsonStr);
        TakObject takO = new TakObject();
        try {
            JSONObject tak = new JSONObject(jsonStr);

            // Get name, id, lat, and lng
            takO.setName(tak.getString("name"));
            takO.setID(new TakID(tak.getString("id")));
            takO.setLat(Double.parseDouble(tak.getString("lat")));
            takO.setLng(Double.parseDouble(tak.getString("lng")));

            // Get metadata by iterating over metadata array and creating the map
            JSONArray metadataArray = tak.getJSONArray("metadata");
            Map<String, TakMetadata> metadata = new HashMap<String, TakMetadata>();
            for (int k = 0; k < metadataArray.length(); k++) {
                TakMetadata m = TakMetadata.createFromJSON(metadataArray.getJSONObject(k).toString());
                metadata.put(m.getKey(), m);
            }
            takO.setMetadata(metadata);

        } catch (JSONException e) {
            Log.d(MainActivity.LOG_TAG, "TakObject.createFromJSON: Error in parsing JSON.");
            e.printStackTrace();
        }

        return takO;
    }

    private String name;
    private double lat = -9999, lng = -9999;
    private TakID id;
    private Map<String, TakMetadata> metadata;


    /** === GETTERS === */

    public String getName() {
        if (name == null) {
            throw new RuntimeException("Tak name field has not been properly set.");
        }
        return name;
    }

    public double getLat() {
        if (lat == -9999) {
            throw new RuntimeException("Tak latitude field has not been properly set.");
        }
        return lat;

    }

    public double getLng() {
        if (lng == -9999) {
            throw new RuntimeException("Tak longitude field has not been properly set.");
        }
        return lng;
    }

    public TakID getID() {
        if (id == null) {
            throw new RuntimeException("Tak ID field has not been properly set.");
        }
        return id;
    }

    public Map<String, TakMetadata> getMeta() {
        if (metadata == null) {
            throw new RuntimeException("Tak metadata field has not been properly set.");
        }
        return metadata;
    }

    public String getMetaValue(String key) {
        if (metadata == null) {
            throw new RuntimeException("Tak metadata field has not been properly set.");
        }
        return metadata.get(key).getValue();
    }


    /** === SETTERS ==
     *  Note that none of these modify the backing database. Use with caution */

    public void setName(String name) { this.name = name; }

    public void setLat(double lat) { this.lat = lat; }

    public void setLng(double lng) { this.lng = lng; }

    public void setID(TakID id) { this.id = id; }

    public void setMetadata(Map<String, TakMetadata> meta) { this.metadata = meta; }

}
