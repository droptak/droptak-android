package edu.purdue.maptak.admin.tasks;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import edu.purdue.maptak.admin.activities.MainActivity;

public class AddMapTask extends AsyncTask<Void, Void, String>  {
    private String mapName;
    private Context context;
    SharedPreferences settings;
    ProgressDialog processDialog;
    public static  String jsonsString;
    private boolean hasSuccess = false;

    public AddMapTask(String mapName, Context context) {
        this.mapName = mapName;
        settings = context.getSharedPreferences(MainActivity.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        processDialog = new ProgressDialog(context);

    }

    public void onPreExecute(){
        processDialog.setMessage("Adding Map");
        processDialog.show();
    }


    @Override
    protected String doInBackground(Void... voids) {
        String userName = settings.getString("name", "");
        String userId = settings.getString(MainActivity.PREF_USER_MAPTAK_TOKEN, "");
        userName = userName.replaceAll("\\s", "%20");
        mapName = mapName.replaceAll("\\s", "%20");
        Log.d("debug", userName);
        mapName.replaceAll(" ", "%20");
        AsyncHttpClient client = new AsyncHttpClient();
        client.post("http://mapitapps.appspot.com/api/map?userId=" + userId + "&username=" + userName + "&mapname=" + mapName, new AsyncHttpResponseHandler() {
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