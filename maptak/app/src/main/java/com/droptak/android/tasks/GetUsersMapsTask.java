package com.droptak.android.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.droptak.android.activities.MainActivity;
import com.droptak.android.data.MapObject;
import com.droptak.android.data.MapTakDB;
import com.droptak.android.data.TakMetadata;
import com.droptak.android.data.TakObject;
import com.droptak.android.data.User;
import com.droptak.android.interfaces.OnMapsRefreshListener;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Task which downloads all of the user's maps and stores them in the database.
 *  If a map downloaded already exists in the database (by map ID), it is deleted and replaced by the new map */
public class GetUsersMapsTask extends AsyncTask<Void, Void, Void> {

    private static final String BASE_URL = "http://mapitapps.appspot.com/api/v1/user/";

    private Context c;
    private OnMapsRefreshListener listener;

    public GetUsersMapsTask(Context c, OnMapsRefreshListener listener) {
        this.c = c;
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(Void... params) {

        // Construct the URL
        SharedPreferences prefs = c.getSharedPreferences(MainActivity.SHARED_PREFS_NAME, 0);
        String userID = prefs.getString(MainActivity.PREF_USER_MAPTAK_TOKEN, "");
        if (userID.equals("")) {
            Log.d(MainActivity.LOG_TAG, "Error refreshing local DB. User not signed in");
            return null;
        }
        String url = BASE_URL + userID + "/maps";

        // Create the http objects
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet get = new HttpGet(url);

        // Execute the http client and get a JSONArray
        JSONArray jsonArray = null;
        try {
            HttpResponse response = httpClient.execute(get);
            String jsonStr = EntityUtils.toString(response.getEntity(), "UTF-8");
            jsonArray = new JSONArray(jsonStr);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Parse the JSON array into a list of maps
        List<MapObject> maps = new ArrayList<MapObject>();
        for (int i = 0; i < jsonArray.length(); i++) {

            try {
                JSONObject obj = jsonArray.getJSONObject(i);
                MapObject m = MapObject.createFromJSON(obj.toString());
                maps.add(m);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        // Add each map to the database
        MapTakDB db = MapTakDB.getDB(c);
        for (MapObject map : maps) {

            // Get the old version of the map in the event data is updated
            MapObject oldMap = db.getMap(map.getID());

            // If the old map exists in the database
            if (oldMap != null) {
                // Delete it
                db.deleteMap(oldMap.getID());
            }

            // And re-add it
            db.addMap(map);

            // Delete its admins and re-add them
            if (oldMap != null) {
                for (User u : oldMap.getManagers()) {
                    db.deleteAdmin(u, oldMap.getID());
                }
            }

            // Re-add them
            for (User u : map.getManagers()) {
                db.addAdmin(u, map.getID());
            }

            // Delete its taks
            if (oldMap != null) {
                for (TakObject t : oldMap.getTaks()) {
                    db.deleteTak(t.getID());

                    // Delete its metadata
                    for (TakMetadata m : t.getMeta().values()) {
                        db.deleteTakMetadata(m);
                    }
                }
            }

            // Re-add them
            for (TakObject t : map.getTaks()) {
                db.addTak(t, map.getID());

                // Add the metadata for each tak
                for (TakMetadata m : t.getMeta().values()) {
                    db.addTakMetadata(t.getID(), m);
                }
            }

        }

        // Alert the listeners that we've finished
        if (listener != null) {
            listener.onMapsRefresh();
        }

        return null;
    }



}
