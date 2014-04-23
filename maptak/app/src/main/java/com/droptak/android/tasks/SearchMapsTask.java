package com.droptak.android.tasks;

import android.os.AsyncTask;

import com.droptak.android.data.MapObject;
import com.droptak.android.interfaces.OnMapSearchResultListener;

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


/** Searches the server for a given query and returns a list of MapObjects which match the query.
 *  The search results will NOT be added to the local database in this call. It only returns them. */
public class SearchMapsTask extends AsyncTask<Void, Void, Void> {

    private static final String BASE_URL = "http://mapitapps.appspot.com/api/v1/search";

    private String query;
    private OnMapSearchResultListener listener;

    public SearchMapsTask(String query, OnMapSearchResultListener listener) {
        this.query = query;
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(Void... params) {

        // Sanitize query
        query = query.replaceAll(" ", "%20");

        // Construct the url
        String url = BASE_URL +
                "?query_type=mapName" +
                "&query=" + query;

        // Create http client
        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(url);

        // Execute it
        String responseString = null;
        try {
            HttpResponse response = client.execute(get);
            responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Parse the response string for maps
        List<MapObject> maps = new ArrayList<MapObject>();
        try {

            JSONArray resultsArray = new JSONArray(responseString).getJSONObject(0).getJSONArray("result");

            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject j = resultsArray.getJSONObject(i);

                // Parse it into a mapobject
                MapObject map = MapObject.createFromJSON(j.toString());

                // Add to array
                maps.add(map);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Alert listeners
        if (listener != null) {
            listener.onSearchResult(maps);
        }

        return null;
    }
}
