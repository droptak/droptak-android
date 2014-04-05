package edu.purdue.maptak.admin.tasks;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.content.*;
import android.app.Activity;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import edu.purdue.maptak.admin.activities.MainActivity;

/** Sends a user's Google OAUTH token to the MapTak servers for authentication */
public class MapTakLoginTask extends AsyncTask<Void,Void,String> {

    private Activity callingActivity;
    private ProgressDialog progressDialog;
    private static String BASE_LOGIN_URL = "http://mapitapps.appspot.com/api/login";

    /** Constructor. Pass in the calling activity and a connected Google API Client */
    public MapTakLoginTask(Activity activity) {
        this.callingActivity = activity;
    }


    public void onPreExecute() {
        // Create a process dialog to tell the user we are logging in
        callingActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialog(callingActivity);
                progressDialog.setMessage("Logging in to MapTak");
                progressDialog.show();
            }
        });
    }


    protected String doInBackground(Void... voids) {

        // Get instance of shared preferences
        SharedPreferences preferences = callingActivity.getSharedPreferences(MainActivity.SHARED_PREFS_NAME, Context.MODE_PRIVATE);

        // Get user information from the shared preferences
        // This is set in LoginFragment
        String name = preferences.getString(MainActivity.PREF_USER_GPLUS_NAME, "ERROR");
        String email = preferences.getString(MainActivity.PREF_USER_GPLUS_EMAIL, "ERROR");
        String gplusID = preferences.getString(MainActivity.PREF_USER_GPLUS_ID, "ERROR");
        String oauth = preferences.getString(MainActivity.PREF_USER_GPLUS_TOKEN, "ERROR");

        // If any of the information is equal to ERROR the user is not signed in to google plus. Exit and fail.
        if (name.equals("ERROR") || email.equals("ERROR") || gplusID.equals("ERROR") || oauth.equals("ERROR")) {
            Log.d(MainActivity.LOG_TAG, "Not signed in to Google+. Canceling MapTak authentication.");
            return null;
        }

        // Sanitize the user's name
        name = name.replace(" ", "%20");

        // Construct the URL we will hit to log in
        String url = BASE_LOGIN_URL +
                "?storeToken=" + oauth +
                "&name=" + name +
                "&email=" + email +
                "&id=" + gplusID;

        // Create our asynchronous http client and issue a post request to a given URL
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);

        String responseString = null;
        try {
            HttpResponse response = client.execute(post);
            responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Parse the response into a JSON Object and get the id
        String id = null;
        try {
            JSONObject jobj = new JSONObject(responseString);
            id = jobj.getString("uuid");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Add it to the shared preferences
        SharedPreferences prefs = callingActivity.getSharedPreferences(MainActivity.SHARED_PREFS_NAME, 0);
        prefs.edit().putString(MainActivity.PREF_USER_MAPTAK_TOKEN, id).commit();

        return null;
    }


    @Override
    protected void onPostExecute(String token) {
        callingActivity.runOnUiThread(new Runnable() {
            public void run() {
                progressDialog.dismiss();
            }
        });
    }


}
