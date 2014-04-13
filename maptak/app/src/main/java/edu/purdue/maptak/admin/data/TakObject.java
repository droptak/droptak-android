package edu.purdue.maptak.admin.data;
import java.util.*;



public class TakObject {

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
