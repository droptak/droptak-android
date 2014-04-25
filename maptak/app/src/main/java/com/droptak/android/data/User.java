package com.droptak.android.data;

import org.json.JSONException;
import org.json.JSONObject;

public class User {

    /** Creates a new User object from a JSON String */
    public static User createFromJSON(String jsonStr) {

        User u = null;
        try {

            JSONObject j = new JSONObject(jsonStr);

            // Parse id, name, and email
            String id = j.getString("id");
            String name = j.getString("name");
            String email = j.getString("email");

            // Create user object
            u = new User(id, name, email);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return u;

    }


    /** Unique ID of the user */
    private String id;

    /** Printable name of the user */
    private String name;

    /** User's email */
    private String email;

    /** Constructor */
    public User(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    /** Compares two User objects by ID */
    public boolean equals(User u) {
        if (this.getID().equals(u.getID())) {
            return true;
        }
        return false;
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
