package com.droptak.android.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.droptak.android.activities.MainActivity;
import com.droptak.android.data.MapID;
import com.droptak.android.data.MapObject;
import com.droptak.android.data.MapTakDB;
import com.droptak.android.data.TakObject;
import com.droptak.android.data.User;
import com.droptak.android.interfaces.OnMapsRefreshListener;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/** Gets a single map specified by a given ID from the server and stores it in the local database. */
public class GetMapTask extends AsyncTask<Void, Void, Void> {

    private static final String BASE_URL = "http://mapitapps.appspot.com/api/v1/map/";

    private Context c;
    private MapID mapToGet;
    private OnMapsRefreshListener listener;

    public GetMapTask(Context c, MapID id, OnMapsRefreshListener listener) {
        this.c = c;
        this.mapToGet = id;
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(Void... params) {

        // Construct the URL
        String url = BASE_URL + mapToGet.toString();

        // Prepare the GET
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet get = new HttpGet(url);

        // And execute it to get the JSON
        String jsonStr = null;
        try {
            HttpResponse response = httpClient.execute(get);
            jsonStr = EntityUtils.toString(response.getEntity(), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        // Parse the reply string into a MapObject
        MapObject map = MapObject.createFromJSON(jsonStr);

        // Add the map and all of its information to the database
        MapTakDB db = MapTakDB.getDB(c);
        db.addMap(map);
        db.addAdmin(map.getOwner(), map.getID());
        for (TakObject t : map.getTaks()) {
            db.addTak(t, map.getID());
        }
        for (User u : map.getManagers()) {
            db.addAdmin(u, map.getID());
        }

        Log.d(MainActivity.LOG_TAG, "Map " + map.getName() + " successfully added from server.");

        // Alert listeners
        if (listener != null) {
            listener.onMapsRefresh();
        }

        return null;
    }



}
