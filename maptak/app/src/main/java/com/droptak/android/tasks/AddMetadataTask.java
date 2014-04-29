package com.droptak.android.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.webkit.URLUtil;

import com.droptak.android.data.MapTakDB;
import com.droptak.android.data.TakID;
import com.droptak.android.data.TakMetadata;

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

public class AddMetadataTask extends AsyncTask<Void, Void, Void> {

    private static final String BASE_URL = "http://mapitapps.appspot.com/api/v1/tak/";

    private Context c;
    private TakID id;
    private TakMetadata metadata;

    public AddMetadataTask(Context c, TakID id, TakMetadata data) {
        this.c = c;
        this.id = id;
        this.metadata = data;
    }

    @Override
    protected void onPreExecute() {

        // Add the tak metadata object to the database
        MapTakDB db = MapTakDB.getDB(c);
        db.addTakMetadata(id, metadata);

    }

    @Override
    protected Void doInBackground(Void... params) {

        // Get the key and value from the metadata object
        String key = metadata.getKey();
        String value = metadata.getValue();

        // Generate the URL
        String urlStr = BASE_URL + id.toString() + "/metadata/" +
                "?key=" + key +
                "&value=" + value;

        // Sanitize it
        urlStr = urlStr.replace(" ", "%20");

        if (!URLUtil.isValidUrl(urlStr)) {
            return null;
        }

        // Create the http client
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(urlStr);

        // Execute it
        String result = null;
        try {
            HttpResponse response = client.execute(post);
            result= EntityUtils.toString(response.getEntity(), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Extract the error code to ensure it worked
        int code = -1;
        try {
            code = new JSONObject(result).getInt("code");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (code != 200) {
            return null;
        }

        return null;
    }
}
