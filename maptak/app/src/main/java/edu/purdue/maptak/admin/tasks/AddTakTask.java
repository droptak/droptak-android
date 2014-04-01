package edu.purdue.maptak.admin.tasks;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import edu.purdue.maptak.admin.data.MapID;
import edu.purdue.maptak.admin.data.MapTakDB;

public class AddTakTask extends AsyncTask<Void, Void, String>  {
    private String takName;
    private String lat;
    private String lng;
    private MapID id;
    private Context context;
    SharedPreferences settings;
    ProgressDialog processDialog;
    public static  String jsonsString;
    private boolean hasSuccess = false;

    public AddTakTask(String takName,String lat, String lng, MapID id, Context context) {
        this.takName =takName;
        this.lat = lat;
        this.lng = lng;
        this.id = id;

        settings = context.getSharedPreferences("settings", 0);
        processDialog = new ProgressDialog(context);

    }

    public void onPreExecute(){
        processDialog.setMessage("Adding Map");
        processDialog.show();
    }




    @Override
    protected String doInBackground(Void... voids) {
        String userName = settings.getString("name", "");
        String userId = settings.getString("id", "");
        userName = userName.replaceAll("\\s", "%20");
        takName = takName.replaceAll("\\s", "%20");
        Log.d("debug", userName);
        takName.replaceAll(" ", "%20");
        AsyncHttpClient client = new AsyncHttpClient();
        client.post("http://mapitapps.appspot.com/api/tak?name="+userName+"&id="+userId+"&mapId="+id.toString()+"&title="+takName+"&lat="+lat+"&lng="+lng, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                Log.d("debug", "onSucess");
                AddMapTask.jsonsString = response;
                hasSuccess = true;

            }
        });
        while(!hasSuccess){
            ;
        }
        processDialog.dismiss();
        return AddMapTask.jsonsString;

    }
    protected void onPostExecute(Void v) {
        //processDialog.dismiss();
    }
}