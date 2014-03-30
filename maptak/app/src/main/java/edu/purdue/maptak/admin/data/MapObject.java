package edu.purdue.maptak.admin.data;

import android.content.Context;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/** Encapsulates all of the information for a single Map. MapObjects are immutable. */
public class MapObject {

    /** A label the user has supplied for the map */
    private String label;

    /** A unique ID that can identify each map */
    private MapID mapID;

    /** List of Taks that are related to the map */
    private List<TakObject> takList;

    /** List of Managers */
    private List<UUID> managerList;

    /** Map created by the backend. MapID is known. */
    public MapObject(String label, MapID id, List<TakObject> taks) {
        this.label = label;
        this.mapID = id;
        this.takList = taks;
        this.managerList = new LinkedList<UUID>();
    }

    /** Map created from the app/user. mapID is generated randomly until a sync with the server. */
    public MapObject(String label, List<TakObject> taks) {
        this(label, new MapID(""), taks);
    }

    /** Returns the label for the map */
    public String getLabel() {
        return this.label;
    }

    /** Returns the ID for the map */
    public MapID getID() {
        return this.mapID;
    }

    /** Returns the TakList backing this map */
    public List<TakObject> getTakList() {
        return this.takList;
    }

    /** Returns the list of managers associated with this map */
    public List<UUID> getManagerList() {
        return this.managerList;
    }

}
