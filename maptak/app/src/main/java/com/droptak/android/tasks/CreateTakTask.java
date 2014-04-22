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


/** Task which handles everything involved in creating a new tak. This will add the tak
 *  to the local database with a temporary ID, push the map to the maptak servers, get a new
 *  ID from the maptak servers, and finally update the local database with this new ID */

public class CreateTakTask extends AsyncTask<Void, Void, Void>  {

    private static final String BASE_URL = "http://mapitapps.appspot.com/api/v1/tak/";

    private Context c;
    private TakObject tak;
    private MapID mapID;

    public CreateTakTask(Context c, TakObject tak, MapID mapID) {
        this.c = c;
        this.tak = tak;
        this.mapID = mapID;
    }

    @Override
    protected void onPreExecute() {

        // Create a temporary id for the tak we are adding
        String tempID = "TEMPID-" + UUID.randomUUID().toString().substring(0,15);
        tak.setID(new TakID(tempID));

        // Add the tak to the database with this temporary ID
        MapTakDB db = MapTakDB.getDB(c);
        db.addTak(tak, mapID);

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

        // Get tak information from object passed in
        String takName = tak.getName();
        double takLat = tak.getLat();
        double takLng = tak.getLng();

        // Sanitize the strings
        userName = userName.replaceAll(" ", "%20");
        takName = takName.replaceAll(" ", "%20");

        // Create the URL we will post to
        String url = BASE_URL +
                "?userid=" + userID +
                "&mapid=" + mapID.toString() +
                "&name=" + takName +
                "&lat=" + takLat +
                "&lng=" + takLng;

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
        TakID id = null;
        try {
            JSONObject j = new JSONObject(responseString);
            String newIDStr = ""+j.getLong("id");
            id = new TakID(newIDStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Update the database with the new ID
        MapTakDB db = MapTakDB.getDB(c);
        db.setTakID(tak.getID(), id);

        // TODO: Update any metadata pairs the user might have added


        return null;
    }


}