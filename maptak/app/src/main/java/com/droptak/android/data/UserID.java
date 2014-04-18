package com.droptak.android.data;

public class UserID {

    /** Unique ID of the user */
    private String id;

    /** Printable name of the user */
    private String name;

    /** User's email */
    private String email;

    /** Constructor */
    public UserID(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    /** Gets the unique ID of the user */
    public String getID() {
        return this.id;
    }

    /** Gets the name of the user */
    public String getName() {
        return this.name;
    }

    /** Gets the email address of the user */
    public String getEmail() { return this.email; }

    /** Sets the ID of the user */
    public void setID(String id) { this.id = id; }

    /** Sets the name of the user */
    public void setName(String name) { this.name = name; }

    /** Sets the email of the user */
    public void setEmail(String email) { this.email = email; }

}
