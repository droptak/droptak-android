package com.droptak.android.data;

import android.util.Log;

import com.droptak.android.activities.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Encapsulates all of the information for a single Map. MapObjects are immutable. */
public class MapObject {

    /** Method which parses a standardized MapObject JSON string from the server and returns a MapObject */
    public static MapObject createFromJSON(String jsonStr) {

        // Parse the JSON to create the MapObject
        MapObject map = new MapObject();
        try {
            // Create the JSON Object
            JSONObject j = new JSONObject(jsonStr);

            // Parse ID and name
            map.setID(new MapID(j.getString("id")));
            map.setName(j.getString("name"));

            // Parse is public or private
            String mapIsPublic = j.getString("public");
            map.setIsPublic(mapIsPublic.equals("true") ? true : false);

            // Parse out other objects and owners
            JSONObject mapOwner = j.getJSONObject("owner");
            JSONArray mapAdminsArray = j.getJSONArray("admins");
            JSONArray mapTaks = j.getJSONArray("taks");

            // Parse Map Owner
            String ownerName = mapOwner.getString("name");
            String ownerEmail = mapOwner.getString("email");
            String ownerID = mapOwner.getString("id");
            User owner = new User(ownerID, ownerName, ownerEmail);
            map.setOwner(owner);

            // Parse admins
            List<User> admins = new ArrayList<User>();
            for (int i = 0; i < mapAdminsArray.length(); i++) {
                User u = User.createFromJSON(mapAdminsArray.getJSONObject(i).toString());
                admins.add(u);
            }
            map.setManagers(admins);

            // Parse taks
            List<TakObject> taks = new ArrayList<TakObject>();
            for (int i = 0; i < mapTaks.length(); i++) {
                TakObject t = TakObject.createFromJSON(mapTaks.getJSONObject(i).toString());
                taks.add(t);
            }
            map.setTaks(taks);

        } catch (JSONException e) {
            Log.d(MainActivity.LOG_TAG, "MapObject.createFromJSON: Error in parsing JSON.");
            e.printStackTrace();
        }

        return map;
    }

    /** A label the user has supplied for the map */
    private String label;

    /** A unique ID that can identify each map */
    private MapID mapID;

    /** List of Taks that are related to the map */
    private List<TakObject> takList;

    /** List of Managers */
    private List<User> managerList;

    /** Owner of the map (as UUID) */
    private User mapOwner;

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

    public User getOwner() {
        if (mapOwner == null) {
            throw new RuntimeException("Map Owner field has not been properly set.");
        }
        return this.mapOwner;
    }

    public List<User> getManagers() {
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

    public void setOwner(User owner) { this.mapOwner = owner; }

    public void setManagers(List<User> managers) { this.managerList = managers; }

    public void setIsPublic(boolean isPublic) { this.isPublic = isPublic; }


    /** == MISC ===
     *  Again, note that none of these methods change the backing database. Use them wisely */

    public void addTak(TakObject t) { this.takList.add(t); }

    public void addManager(User i) { this.managerList.add(i); }

}
