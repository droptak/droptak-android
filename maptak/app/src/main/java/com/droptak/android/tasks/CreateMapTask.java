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
import com.droptak.android.data.MapObject;
import com.droptak.android.data.MapTakDB;
import com.droptak.android.data.TakObject;
import com.droptak.android.data.User;
import com.droptak.android.interfaces.OnMapIDUpdateListener;

/** Task which takes in a map object, adds it to the local database, pushes it to the server,
 *  then updates the local database with all the information is gets back from the server.
 *  All asynchronously.
 *  This task assumes that the map passed in has no taks associated with it. If it does, you
 *  need to manually call the CreateTakTask on each tak. */

public class CreateMapTask extends AsyncTask<Void, Void, Void>  {

    private static String BASE_URL = "http://mapitapps.appspot.com/api/v1/map/";

    private MapObject mapToPush;
    private Context c;
    private OnMapIDUpdateListener listener;

    public CreateMapTask(Context c, MapObject toPush, OnMapIDUpdateListener listener) {
        this.c = c;
        this.mapToPush = toPush;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {

        // Assign the map a temporary ID while it is being pushed to the server
        String tempID = "TEMPID-" + UUID.randomUUID().toString().substring(0,15);
        mapToPush.setID(new MapID(tempID));

        // Add the map to the local database
        MapTakDB db = MapTakDB.getDB(c);
        db.addMap(mapToPush);

        // And add its owner as an administrator in the table
        db.addAdmin(mapToPush.getOwner(), mapToPush.getID());

    }

    @Override
    protected Void doInBackground(Void... voids) {

        // Get user's personal information from the shared preferences
        SharedPreferences prefs = c.getSharedPreferences(MainActivity.SHARED_PREFS_NAME, 0);
        String userName = prefs.getString(MainActivity.PREF_USER_GPLUS_NAME, "");
        String userID = prefs.getString(MainActivity.PREF_USER_MAPTAK_TOKEN, "");

        // Get information about the map we are adding
        String mapName = mapToPush.getName();
        String isPublic = ""+mapToPush.isPublic();

        // Sanitize the strings
        userName = userName.replaceAll("\\s", "%20");
        mapName = mapName.replaceAll("\\s", "%20");

        // Construct the url we are going to post to
        String url = BASE_URL +
                "?owner=" + userID +
                "&name=" + mapName +
                "&isPublic=" + isPublic;

        // Create the http client we use to post to the server
        HttpClient client = new DefaultHttpClient();
        HttpPost get = new HttpPost(url);

        // Do the post and get the JSON as a response
        String responseString = null;
        try {
            HttpResponse response = client.execute(get);
            responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Parse the returned JSON to get the new MapID
        MapID newMapID = null;
        try {
            JSONObject j = new JSONObject(responseString);
            String realMapIDString = ""+j.getLong("id");
            newMapID = new MapID(realMapIDString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Update the map in the database to hold the new ID
        MapTakDB db = MapTakDB.getDB(c);
        db.setMapID(mapToPush.getID(), newMapID);

        // Update any taks that have been created for the map during the push with the new mapid
        // TODO: Update the server as well
        for (TakObject t : db.getTaks(mapToPush.getID())) {
            db.setTakMapID(t.getID(), newMapID);
        }

        // Update administrators added during the push with the new ID
        // TODO: Update the server as well
        for (User uid : db.getAdmins(mapToPush.getID())) {
            db.setMapAdminsMapID(uid, mapToPush.getID(), newMapID);
        }

        // If the user has this map selected as the current map, update the entry in shared preferences
        String curMap = prefs.getString(MainActivity.PREF_CURRENT_MAP, "");
        if (curMap.equals(mapToPush.getID().toString())) {
            prefs.edit().putString(MainActivity.PREF_CURRENT_MAP, newMapID.toString()).commit();
        }

        // Alert the listeners that the MapID has been updated
        if (listener != null) {
            listener.onMapIDUpdate(mapToPush.getID(), newMapID);
        }

        return null;
    }

}