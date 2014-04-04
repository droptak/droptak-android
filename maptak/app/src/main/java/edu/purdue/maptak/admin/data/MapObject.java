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
    private List<UserID> managerList;

    /** Owner of the map (as UUID) */
    private UserID mapOwner;

    /** Determines whether map has private visibility or not */
    private boolean isPrivate;

    /** Map created by the backend. MapID is known. */
    public MapObject(String label, MapID id, List<TakObject> taks, boolean isPrivate) {
        this.label = label;
        this.mapID = id;
        this.takList = taks;
        this.isPrivate = isPrivate;
    }

    /** Map created from the app/user. mapID is generated randomly until a sync with the server. */
    public MapObject(String label, List<TakObject> taks, boolean isPrivate) {
        this(label, new MapID(UUID.randomUUID().toString().substring(0, 12)), taks, isPrivate);
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

    public void setTaskList(List<TakObject> taks){ this.takList = taks;}

    /** Returns the private attribute of the map */
    public boolean isPrivate(){
        return this.isPrivate;
    }

    /** Sets private attribute on/off */
    public void setPrivate(boolean priv){
        this.isPrivate = priv;
    }

    /** Returns the owner(UUID) of the map */
    public UserID getOwner() {
        return mapOwner;
    }

    /** Add a manager(email) to the mapObject */
    public void addManager(String managerID, String managerEmail) {
        UserID uid = new UserID(managerID, managerEmail);
        managerList.add(uid);
    }

    /** Returns the list of managers associated with this map */
    public List<UserID> getManagerList() {
        return this.managerList;
    }

}
