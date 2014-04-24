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

    public DeleteMapTask(Context c, MapObject toPush) {
        this.c = c;
        this.mapToPush = toPush;
    }


    @Override
    protected Void doInBackground(Void... voids) {
        // Construct the url we are going to post to
        String url = BASE_URL +
                mapToPush.getID().toString()+"/";

        // Create the http client we use to delete to the server
        HttpClient client = new DefaultHttpClient();
        HttpDelete delete = new HttpDelete(url);

        // Do the delete and get the JSON as a response
        String responseString = null;
        try {
            HttpResponse response = client.execute(delete);
            responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}