package com.droptak.android.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.droptak.android.activities.MainActivity;
import com.droptak.android.data.MapID;
import com.droptak.android.data.MapObject;
import com.droptak.android.data.MapTakDB;
import com.droptak.android.data.TakObject;
import com.droptak.android.data.User;
import com.droptak.android.interfaces.OnMapIDUpdateListener;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;

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

public class DeleteMapTask extends AsyncTask<Void, Void, Void> {

    private static String BASE_URL = "http://mapitapps.appspot.com/api/v1/map/";

    private MapObject mapToPush;
    private Context c;
    private OnMapIDUpdateListener listener;

    public DeleteMapTask(Context c, MapObject toPush) {
        this.c = c;
        this.mapToPush = toPush;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {

        // Assign the map a temporary ID while it is being pushed to the server
        String tempID = "TEMPID-" + UUID.randomUUID().toString().substring(0,15);
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
                mapToPush.getID().toString()+"/";

        // Create the http client we use to post to the server
        HttpClient client = new DefaultHttpClient();
        HttpDelete delete = new HttpDelete(url);

        // Do the post and get the JSON as a response
        String responseString = null;
        try {
            HttpResponse response = client.execute(delete);
            responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Parse the returned JSON to get the new MapID
        try {
            Log.d("debug","response="+responseString);
            JSONObject j = new JSONObject(responseString);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return null;
    }

}