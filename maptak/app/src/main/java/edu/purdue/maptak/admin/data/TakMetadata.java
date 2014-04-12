package edu.purdue.maptak.admin.data;

/**
 * Created by mike on 4/12/14.
 */
public class TakMetadata {

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

}
