package com.droptak.android.tasks;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.URLUtil;

import com.droptak.android.activities.MainActivity;
import com.droptak.android.data.MapID;
import com.droptak.android.data.MapObject;
import com.droptak.android.data.MapTakDB;
import com.droptak.android.interfaces.OnMapUpdateListener;

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

public class EditMapTask extends AsyncTask<Void, Void, Void> {

    private static final String BASE_URL = "http://mapitapps.appspot.com/api/v1/map/";

    private Context c;
    private MapID map;
    private MapObject newMapInfo;
    private OnMapUpdateListener listener;

    /** Changes the information contained in the map corresponding to the MapID
     *  to that information which is in toEditTo. Currently this only includes the map's name
     *  and whether it is public or private. */
    public EditMapTask(Context c, MapObject toEditTo, OnMapUpdateListener listener) {
        this.c = c;
        this.map = toEditTo.getID();
        this.newMapInfo = toEditTo;
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(Void... params) {

        // Parse changed information out of the map passed in
        String newName = newMapInfo.getName();
        String newIsPublic = newMapInfo.isPublic() ? "true" : "false";

        // Sanitize them
        newName = newName.replace(" ", "%20");

        // Construct the URL
        String urlStr = BASE_URL + map.toString() + "/" +
                "?name=" + newName +
                "&isPublic=" + newIsPublic;

        // Sanitize the URL
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
        db.setMapName(map, newName.replace("%20", " "));
        db.setMapIsPublic(map, newMapInfo.isPublic());

        // Alert listeners
        if (listener != null) {
            listener.onMapUpdate(map);
        }

        return null;
    }
}
