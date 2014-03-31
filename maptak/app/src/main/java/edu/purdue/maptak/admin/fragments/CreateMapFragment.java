package edu.purdue.maptak.admin.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.io.File;
import java.nio.channels.FileChannel;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import edu.purdue.maptak.admin.R;
import edu.purdue.maptak.admin.data.MapID;
import edu.purdue.maptak.admin.data.MapObject;
import edu.purdue.maptak.admin.data.MapTakDB;
import edu.purdue.maptak.admin.data.TakObject;
import edu.purdue.maptak.admin.tasks.AddMapTask;


public class CreateMapFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_createmap, container, false);
        final Context context = this.getActivity();
        final EditText mapNameText = (EditText) view.findViewById(R.id.mapNameText);
        final MapTakDB newDB = new MapTakDB(getActivity());

        /** Button1 creates a tak at the user's current location */
        Button button = (Button) view.findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /** Create empty linked list for taks */
                List<TakObject> taks = new LinkedList<TakObject>();
                AddMapTask addMapTask = new AddMapTask(mapNameText.getText().toString(),context);
                String jsonString = null;
                try {
                    jsonString = addMapTask.execute().get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                Log.d("debug","jsonString="+jsonString);
                JSONObject jsonObject = null;
                String id = null;
                try {
                    jsonObject = new JSONObject(jsonString);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    id = jsonObject.getString("mapId");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                MapID mID = new MapID(id);
                MapObject map = new MapObject(mapNameText.getText().toString(),mID,taks);
                Log.d("debug","mapid="+map.getID().getIDStr());
                newDB.addMap(map);
                try {
                    File sd = Environment.getExternalStorageDirectory();
                    File data = Environment.getDataDirectory();

                    if (sd.canWrite()) {
                        String currentDBPath = "//data//"+ "edu.purdue.maptak.admin" +"//databases//"+"database_cached_taks";
                        String backupDBPath = "database_cahced_taks";
                        File currentDB = new File(data, currentDBPath);
                        File backupDB = new File(sd, backupDBPath);

                        FileChannel src = new FileInputStream(currentDB).getChannel();
                        FileChannel dst = new FileOutputStream(backupDB).getChannel();
                        dst.transferFrom(src, 0, src.size());
                        src.close();
                        dst.close();
                       // Toast.makeText(, backupDB.toString(), Toast.LENGTH_LONG).show();
                        Log.d("debug","sql backupmade");

                    }
                } catch (Exception e) {

                    //Toast.makeText(this.getActivity(), e.toString(), Toast.LENGTH_LONG).show();


                }


                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.activity_map_mapview,new MapListFragment());
                ft.commit();

            }
        });



        return view;
    }
}
