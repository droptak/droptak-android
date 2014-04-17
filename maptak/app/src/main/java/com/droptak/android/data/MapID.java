package com.droptak.android.data;

/** Since we are no longer using UUIDs to describe the IDs for each map and tak, this class
 *  encapulates the data necessary to describe a unique ID which describes a Map object. */
public class MapID {

    /** String representation of the map ID */
    private String mapID;

    /** Constructor. Pass in the id. */
    public MapID(String id) {
        this.mapID = id;
    }

    /** Checks for equivelence between two IDs */
    public boolean equals(MapID m2) {
        if (mapID.equals(m2.toString())) {
            return true;
        }
        return false;
    }

    /** Returns a string representation of the mapID */
    public String toString() {
        return mapID;
    }

}
