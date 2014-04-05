package edu.purdue.maptak.admin.tasks;

import android.content.Context;
import android.os.AsyncTask;
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

public class GetQRTaksTask extends AsyncTask<Void,Void,List<TakObject>> {
    private Context context;
    private String mapName;
    private  long mapId;
    public static boolean hasSuccess = false;
    public static boolean addOnce = false;
    public static List<TakObject> takObjects;

    public GetQRTaksTask(Context c, String mapName, long mapId){
        GetQRTaksTask.hasSuccess = false;
        this.context = c;
        this.mapId = mapId;
        this.mapName = mapName;

    }

    @Override
    protected List<TakObject> doInBackground(Void... voids) {
        AsyncHttpClient takClient = new AsyncHttpClient();
        takClient.getHttpClient().getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
        takClient.get("http://mapitapps.appspot.com/api/maps/"+mapId,new AsyncHttpResponseHandler(){
            public void onSuccess(String resp){
                try {
                    takObjects = new ArrayList<TakObject>();
                    Log.d("debug", "taks=" + resp);
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = new JSONArray(resp);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jObject = null;
                        try {
                            jObject = jsonArray.getJSONObject(i);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        String takCreator = null;
                        String takTitle = null;
                        long creatorId = 0;
                        double lat = 0.0;
                        double lng = 0.0;
                        long takId = 0;

                        try {
                            takCreator = jObject.getString("creator");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            takTitle = jObject.getString("title");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            creatorId = jObject.getLong("creatorId");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            lat = jObject.getDouble("lat");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            lng = jObject.getDouble("lng");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            takId = jObject.getLong("id");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        TakID tid = new TakID("" + takId);
                        TakObject tak = new TakObject(tid, takTitle, lat, lng);
                        takObjects.add(tak);


                    }
                    GetQRTaksTask.hasSuccess=true;


                    return;
                } catch(NullPointerException e){
                    e.printStackTrace();
                }

            }

        });
        while(!GetQRTaksTask.hasSuccess){
            ;
        }
        Log.d("debug","returnTaks");
        return GetQRTaksTask.takObjects;
    }
}
