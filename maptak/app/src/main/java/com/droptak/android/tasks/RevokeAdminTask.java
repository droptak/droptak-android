package com.droptak.android.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.widget.Toast;

import com.droptak.android.activities.MainActivity;
import com.droptak.android.data.MapID;
import com.droptak.android.data.MapObject;
import com.droptak.android.data.MapTakDB;
import com.droptak.android.data.User;
import com.droptak.android.interfaces.OnAdminRevokedListener;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class RevokeAdminTask extends AsyncTask<Void, Void, Void> {

    private static final String BASE_URL = "http://mapitapps.appspot.com/api/v1/map/";

    private Context c;
    private MapID mapID;
    private User user;
    private OnAdminRevokedListener listener;
    private boolean execute = true;

    public RevokeAdminTask(Context c, MapID map, User user, OnAdminRevokedListener listener) {
        this.c = c;
        this.mapID = map;
        this.user = user;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {

        // Check to see if the admin we're deleting is the owner
        MapTakDB db = MapTakDB.getDB(c);
        MapObject map = db.getMap(mapID);

        if (map.getOwner().equals(user)) {
            Toast.makeText(c, "You can't revoke admin access to the owner!", Toast.LENGTH_SHORT).show();
            execute = false;
        }

    }

    @Override
    protected Void doInBackground(Void... params) {

        if (!execute) {
            return null;
        }

        // Construct the URL
        String url = BASE_URL + mapID.toString() + "/admin/" + user.getEmail() + "/";

        // Create the HTTP client
        HttpClient client = new DefaultHttpClient();
        HttpDelete delete = new HttpDelete(url);

        // Execute it
        String responseString = null;
        try {
            HttpResponse response = client.execute(delete);
            responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Check to ensure that it passed
        int code = 0;
        try {
            code = new JSONObject(responseString).getInt("code");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (code == 400) {
            return null;
        }

        // Update the local database
        MapTakDB db = MapTakDB.getDB(c);
        db.deleteAdmin(user, mapID);

        if (listener != null) {
            listener.onAdminRevoked(mapID, user);
        }

        return null;
    }
}
