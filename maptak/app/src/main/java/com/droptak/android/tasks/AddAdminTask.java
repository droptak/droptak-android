package com.droptak.android.tasks;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.content.Context;
import android.util.Log;
import android.webkit.URLUtil;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.UUID;

import com.droptak.android.activities.MainActivity;
import com.droptak.android.data.MapID;
import com.droptak.android.data.MapTakDB;
import com.droptak.android.data.TakID;
import com.droptak.android.data.TakMetadata;
import com.droptak.android.data.TakObject;
import com.droptak.android.data.User;
import com.droptak.android.interfaces.OnAdminIDUpdateListener;


/** Task which handles everything involved in attaching a new user as an admin to a map. */
public class AddAdminTask extends AsyncTask<Void, Void, Void>  {

    private static final String BASE_URL = "http://mapitapps.appspot.com/api/v1/map/";

    Activity c;
    private User user;
    private MapID mapID;
    private OnAdminIDUpdateListener listener;

    /** The tempUser passed in can have a null ID and name. The only thing important is the email */
    public AddAdminTask(Activity c, User tempUser, MapID mapID, OnAdminIDUpdateListener listener) {
        this.c = c;
        this.user = tempUser;
        this.mapID = mapID;
        this.listener = listener;
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
            Log.d(MainActivity.LOG_TAG, "Error sending admin. Not logged in. " + userName + userEmail + userID);
            return null;
        }

        // Create the URL we will post to
        String urlStr = BASE_URL + mapID.toString() + "/admin/" + user.getEmail() + "/";

        // Sanitize the URL
        urlStr = urlStr.replace(" ", "%20");

        // If it isn't valid, end
        if (!URLUtil.isValidUrl(urlStr)) {
            return null;
        }

        // Create our asynchronous http client and issue a post request to a given URL
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(urlStr);

        // Make the post and get the response as a JSON string
        String responseString = null;
        try {
            HttpResponse response = client.execute(post);
            responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Parse the new ID and name out of the JSON
        User newUser = null;
        try {
            JSONObject j = new JSONObject(responseString);
            String newIDStr = "" + j.getLong("id");
            String newName = j.getString("name");
            String newEmail = j.getString("email");
            newUser = new User(newIDStr, newName, newEmail);
        } catch (JSONException e) {
            c.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(c, "We don't recognize that email.", Toast.LENGTH_LONG).show();
                }
            });
            return null;
        }

        // Add teh user to the database
        MapTakDB db = MapTakDB.getDB(c);
        db.addAdmin(newUser, mapID);

        // Call the listener
        if (listener != null) {
            listener.onAdminIDUpdate(newUser, mapID);
        }

        return null;
    }


}