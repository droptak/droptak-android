package edu.purdue.maptak.admin.tasks;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.client.params.ClientPNames;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import edu.purdue.maptak.admin.data.MapID;
import edu.purdue.maptak.admin.data.MapObject;
import edu.purdue.maptak.admin.data.MapTakDB;
import edu.purdue.maptak.admin.data.TakID;
import edu.purdue.maptak.admin.data.TakObject;

public class GetQRMapTask extends AsyncTask<Void, Void,MapObject>  {
    private String url;
    private Context context;
    SharedPreferences settings;
    ProgressDialog processDialog;
    public static  String jsonsString;
    public static String mapCreator = null;
    public static String mapName = null;
    public static long mapCreatorId = 0;
    public static long mapId = 0;
    public static boolean hasSuccess = false;
    List<TakObject> takObjects = new ArrayList<TakObject>();

    public GetQRMapTask(String url, Context context) {
        this.url = url;
        GetQRMapTask.hasSuccess = false;
        settings = context.getSharedPreferences("settings", 0);
        processDialog = new ProgressDialog(context);

    }

    public void onPreExecute(){
        processDialog.setMessage("Downloading Map");
        //processDialog.show();
    }




    @Override
    protected MapObject doInBackground(Void... voids) {
        String userName = settings.getString("name", "");
        String userId = settings.getString("id", "");
        userName = userName.replaceAll("\\s", "%20");
        url = url.replaceAll("\\s", "%20");
        Log.d("debug", userName);
        //mapName.replaceAll(" ", "%20");\
        Log.d("debug","url="+url);
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                Log.d("debug",response);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                long id = 0;

                String name = null;
                long creatorId = 0;
                String creator = null;

                try {
                    id = jsonObject.getLong("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                GetQRMapTask.mapId = id;
                try {
                    name = jsonObject.getString("name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                GetQRMapTask.mapName = name;
                try {
                    creatorId = jsonObject.getLong("creatorId");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                GetQRMapTask.mapCreatorId = creatorId;
                try {
                    creator = jsonObject.getString("creator");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                GetQRMapTask.mapCreator = creator;
                GetQRMapTask.hasSuccess = true;
                return;
            }
        });

        while(!GetQRMapTask.hasSuccess){
            ;
        }
        Log.d("deubg","return");
        MapID mid = new MapID(""+mapId);
        MapObject map = new MapObject(mapName,mid,takObjects,false);
        return map;
    }
    protected void onPostExecute(Void v) {
        Log.d("onpost","");
    }
}