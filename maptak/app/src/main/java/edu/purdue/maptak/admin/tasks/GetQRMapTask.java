package edu.purdue.maptak.admin.tasks;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

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

public class GetQRMapTask extends AsyncTask<Void, Void, String>  {
    private String url;
    private Context context;
    SharedPreferences settings;
    ProgressDialog processDialog;
    public static  String jsonsString;
    public static String creator = null;
    public static String name = null;
    public static String creatorId = null;
    public static String mapId = null;
    private boolean hasSuccess = false;
    List<TakObject> takObjects = new ArrayList<TakObject>();

    public GetQRMapTask(String url, Context context) {
        this.url = url;
        settings = context.getSharedPreferences("settings", 0);
        processDialog = new ProgressDialog(context);

    }

    public void onPreExecute(){
        processDialog.setMessage("Downloading Map");
        //processDialog.show();
    }




    @Override
    protected String doInBackground(Void... voids) {
        String userName = settings.getString("name", "");
        String userId = settings.getString("id", "");
        userName = userName.replaceAll("\\s", "%20");
        url = url.replaceAll("\\s", "%20");
        Log.d("debug", userName);
        url = "http://"+url;
        //mapName.replaceAll(" ", "%20");\
        Log.d("debug","url="+url);
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                Log.d("debug", "onSucess="+response);
                hasSuccess = true;
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    GetQRMapTask.creator = jsonObject.getString("creator");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    GetQRMapTask.name = jsonObject.getString("name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    GetQRMapTask.creatorId = jsonObject.getString("creatorId");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    GetQRMapTask.mapId = jsonObject.getString("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }




            }
        });
        while(!hasSuccess){
            ;
        }

        Log.d("debug","endFirstcall");
        Log.d("debug","addMapName="+name);
        MapID mid = new MapID(mapId);
        MapObject map = new MapObject(name,mid,takObjects,false);
        MapTakDB db = MapTakDB.getDB(context);
        Log.d("debug","mapis"+map);
        db.addMap(map);

        return "";
    }
    protected void onPostExecute(Void v) {
        //processDialog.dismiss();
    }
}