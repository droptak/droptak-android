package com.droptak.android.tasks;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.content.Context;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;

import com.droptak.android.activities.MainActivity;
import com.droptak.android.data.MapID;
import com.droptak.android.data.MapTakDB;
import com.droptak.android.data.TakID;
import com.droptak.android.data.TakMetadata;
import com.droptak.android.data.TakObject;
import com.droptak.android.data.User;


/** Task which handles everything involved in creating a new tak. This will add the tak
 *  to the local database with a temporary ID, push the map to the maptak servers, get a new
 *  ID from the maptak servers, and finally update the local database with this new ID */

public class AddAdminTask extends AsyncTask<Void, Void, Void>  {

    private static final String BASE_URL = "http://mapitapps.appspot.com/api/v1/map/";

    private Context c;
    private String email;
    private MapID mapID;
    private String tempID;
    private User tempUser;

    public AddAdminTask(Context c, String email, MapID mapID) {
        this.c = c;
        this.email = email;
        this.mapID = mapID;
    }

    @Override
    protected void onPreExecute() {
        SharedPreferences prefs = c.getSharedPreferences(MainActivity.SHARED_PREFS_NAME,0);

        // Create a temporary id for the tak we are adding
         tempID = "TEMPID-" + UUID.randomUUID().toString().substring(0,15);
        String userName = "TEMPUSERNAME";
         tempUser = new User(tempID,userName,email);




        // Add the tak to the database with this temporary ID
        MapTakDB db = MapTakDB.getDB(c);
        db.addAdmin(tempUser,mapID);



    }

    @Override
    protected Void doInBackground(Void... voids) {

        // Get access to the shared preferences
        SharedPreferences prefs = c.getSharedPreferences(MainActivity.SHARED_PREFS_NAME, 0);

        // Get user information from the preferences
        String userName = prefs.getString(MainActivity.PREF_USER_GPLUS_NAME, "ERROR");
        String userEmail = prefs.getString(MainActivity.PREF_USER_GPLUS_EMAIL, "ERROR");
        String userID = prefs.getString(MainActivity.PREF_USER_MAPTAK_TOKEN, "ERROR");

        if (userName.equals("ERROR") || userEmail.equals("ERROR") || userID.equals("ERROR")) {
            Log.d(MainActivity.LOG_TAG, "Error sending tak. Not logged in.");
            return null;
        }


        // Create the URL we will post to
        String url = BASE_URL + mapID.toString() + "/admin/" + email + "/";

        // Create our asynchronous http client and issue a post request to a given URL
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);

        // Make the post and get the response as a JSON string
        String responseString = null;
        try {
            HttpResponse response = client.execute(post);
            responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Parse the new ID out of the JSON
        User admin = null;
        try {
            Log.d("debug","addAdminResponse="+responseString);
            JSONObject j = new JSONObject(responseString);
            String newIDStr = ""+j.getLong("id");
            String newName = j.getString("name");
            String newEmail = j.getString("email");
            admin = new User(newIDStr,newName,newEmail);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Update the database with the new ID
        MapTakDB db = MapTakDB.getDB(c);
       // db.setTakID(tak.getID(), id);
        db.setMapAdminsUser(tempUser,admin);


        // TODO: Update any metadata pairs the user might have added


        return null;
    }


}