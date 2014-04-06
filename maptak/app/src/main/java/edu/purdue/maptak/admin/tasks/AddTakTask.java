package edu.purdue.maptak.admin.tasks;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import edu.purdue.maptak.admin.activities.MainActivity;
import edu.purdue.maptak.admin.data.MapID;
import edu.purdue.maptak.admin.data.MapTakDB;
import edu.purdue.maptak.admin.data.TakObject;

public class AddTakTask extends AsyncTask<Void, Void, String>  {

    private static final String BASE_URL = "http://mapitapps.appspot.com/api/tak";

    private Context c;
    private TakObject tak;
    private MapID mapID;

    public AddTakTask(Context c, TakObject tak, MapID mapID) {
        this.c = c;
        this.tak = tak;
        this.mapID = mapID;;
    }

    @Override
    public void onPreExecute(){

    }

    @Override
    protected String doInBackground(Void... voids) {

        // Get access to the shared preferences
        SharedPreferences prefs = c.getSharedPreferences(MainActivity.SHARED_PREFS_NAME, 0);

        // Get user information from the preferences
        String userName = prefs.getString(MainActivity.PREF_USER_GPLUS_NAME, "ERROR");
        String userEmail = prefs.getString(MainActivity.PREF_USER_GPLUS_EMAIL, "ERROR");
        String userID = prefs.getString(MainActivity.PREF_USER_MAPTAK_TOKEN, "ERROR");

        if (userName.equals("ERROR") || userEmail.equals("ERROR") || userID.equals("ERROR")) {
            Log.d(MainActivity.LOG_TAG, "Error sending tak. Not logged in.");
            return null;
        }

        // Get tak information from object passed in
        String takName = tak.getLabel();
        double takLat = tak.getLatitude();
        double takLng = tak.getLongitude();

        // Sanitize the strings
        userName = userName.replaceAll(" ", "%20");
        takName = takName.replaceAll(" ", "%20");

        // Create the URL we will post to
        String url = BASE_URL +
                "?name=" + userName +
                "&id=" + userID +
                "&mapId=" + mapID.toString() +
                "&title=" + takName +
                "&lat=" + takLat +
                "&lng=" + takLng;

        // Create our asynchronous http client and issue a post request to a given URL
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);

        // Make the post and get the response as a JSON string
        String responseString = null;
        try {
            HttpResponse response = client.execute(post);
            responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Return the JSON string
        return responseString;

    }

    @Override
    protected void onPostExecute(String s) {

    }

}