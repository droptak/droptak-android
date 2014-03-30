package edu.purdue.maptak.admin.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.MapFragment;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import edu.purdue.maptak.admin.R;
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

                /** Create a new map with label entered by user */
                MapObject newMap = new MapObject(mapNameText.getText().toString(), taks);

                /** Add map to the DB */
                newDB.addMap(newMap);
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
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.activity_map_mapview,new MapListFragment());
                ft.commit();
            }
        });

        return view;
    }
}
