package com.droptak.android.tasks;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.URLUtil;

import com.droptak.android.activities.MainActivity;
import com.droptak.android.data.MapID;
import com.droptak.android.data.MapObject;
import com.droptak.android.data.MapTakDB;
import com.droptak.android.data.TakID;
import com.droptak.android.data.TakObject;
import com.droptak.android.interfaces.OnMapUpdateListener;
import com.droptak.android.interfaces.OnTakUpdateListener;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class EditTakTask extends AsyncTask<Void, Void, Void> {

    private static final String BASE_URL = "http://mapitapps.appspot.com/api/v1/tak/";

    private Context c;
    private TakID tak;
    private TakObject newTakInfo;
    private OnTakUpdateListener listener;

    /** Changes the information contained in the map corresponding to the MapID
     *  to that information which is in toEditTo. Currently this only includes the map's name
     *  and whether it is public or private. */
    public EditTakTask(Context c, TakObject toEditTo, OnTakUpdateListener listener) {
        this.c = c;
        this.tak = toEditTo.getID();
        this.newTakInfo = toEditTo;
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(Void... params) {

        // Parse changed information out of the map passed in
        String newName = newTakInfo.getName();
        String newLat = newTakInfo.getLat() + "";
        String newLng = newTakInfo.getLng() + "";

        // Construct the URL
        String urlStr = BASE_URL + tak.toString() + "/" +
                "?name=" + newName +
                "&lat=" + newLat +
                "&lng=" + newLng;

        // Sanitize it
        urlStr = urlStr.replace(" ", "%20");

        if (!URLUtil.isValidUrl(urlStr)) {
            return null;
        }

        // Create the HTTP client
        HttpClient client = new DefaultHttpClient();
        HttpPut put = new HttpPut(urlStr);

        // Execute it
        String jsonStr = null;
        try {
            HttpResponse response = client.execute(put);
            jsonStr = EntityUtils.toString(response.getEntity(), "UTF-8");
        } catch(IOException e) {
            e.printStackTrace();
        }

        // Parse through the JSON just to ensure that it added correctly
        Log.d(MainActivity.LOG_TAG, jsonStr);
        int result = 0;
        try {
            result = new JSONObject(jsonStr).getInt("code");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (result == 400) {
            return null;
        }

        // Update it in the local database
        MapTakDB db = MapTakDB.getDB(c);
        db.setTakName(tak, newName.replace("%20", " "));
        db.setTakLatLng(tak, newTakInfo.getLat(), newTakInfo.getLng());

        // Alert listeners
        if (listener != null) {
            listener.onTakUpdate(tak);
        }

        return null;
    }
}
