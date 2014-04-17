package com.droptak.android.data;

import java.util.List;

/** Encapsulates all of the information for a single Map. MapObjects are immutable. */
public class MapObject {

    /** A label the user has supplied for the map */
    private String label;

    /** A unique ID that can identify each map */
    private MapID mapID;

    /** List of Taks that are related to the map */
    private List<TakObject> takList;

    /** List of Managers */
    private List<UserID> managerList;

    /** Owner of the map (as UUID) */
    private UserID mapOwner;

    /** Determines whether map has private visibility or not */
    private boolean isPublic;


    /** === GETTERS === */

    public String getName() {
        if (label == null) {
            throw new RuntimeException("Map label field has not been properly set.");
        }
        return this.label;
    }

    public MapID getID() {
        if (mapID == null) {
            throw new RuntimeException("Map ID field has not been properly set.");
        }
        return this.mapID;
    }

    public List<TakObject> getTaks() {
        if (takList == null) {
            throw new RuntimeException("Map TakList field has not been properly set.");
        }
        return this.takList;
    }

    public UserID getOwner() {
        if (mapOwner == null) {
            throw new RuntimeException("Map Owner field has not been properly set.");
        }
        return this.mapOwner;
    }

    public List<UserID> getManagers() {
        if (managerList == null) {
            throw new RuntimeException("Map Managers field has not been properly set.");
        }
        return this.managerList;
    }

    public boolean isPublic() { return this.isPublic; }


    /** === SETTERS ===
     *  Note that none of these methods change the backing database. */

    public void setName(String name) { this.label = name; }

    public void setID(MapID id) { this.mapID = id; }

    public void setTaks(List<TakObject> list) { this.takList = list; }

    public void setOwner(UserID owner) { this.mapOwner = owner; }

    public void setManagers(List<UserID> managers) { this.managerList = managers; }

    public void setIsPublic(boolean isPublic) { this.isPublic = isPublic; }


    /** == MISC ===
     *  Again, note that none of these methods change the backing database. Use them wisely */

    public void addTak(TakObject t) { this.takList.add(t); }

    public void addManager(UserID i) { this.managerList.add(i); }

}
