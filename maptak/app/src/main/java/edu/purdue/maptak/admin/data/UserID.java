package edu.purdue.maptak.admin.data;

public class UserID {

    /** Unique ID of the user */
    private String id;

    /** Printable name of the user */
    private String name;

    /** Constructor */
    public UserID(String id, String name) {
        this.id = id;
        this.name = name;
    }

    /** Gets the unique ID of the user */
    public String getID() {
        return this.id;
    }

    /** Gets the name of the user */
    public String getName() {
        return this.name;
    }

}
