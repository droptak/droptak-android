package edu.purdue.maptak.admin.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;

import java.util.LinkedList;
import java.util.List;

import edu.purdue.maptak.admin.R;
import edu.purdue.maptak.admin.data.MapObject;
import edu.purdue.maptak.admin.data.MapTakDB;
import edu.purdue.maptak.admin.data.TakObject;


public class CreateMapFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_createmap, container, false);

        final EditText mapNameText = (EditText) view.findViewById(R.id.mapNameText);
        final EditText managerEmailText = (EditText) view.findViewById(R.id.managerEmail);
        final Switch privateSwitch = (Switch) view.findViewById(R.id.switch1);
        final MapTakDB newDB = new MapTakDB(getActivity());

        /** Button1 creates a tak at the user's current location */
        Button submitButton = (Button) view.findViewById(R.id.button1);
        Button addManagerButton = (Button) view.findViewById(R.id.button2);

        final List<String> managerList = new LinkedList<String>();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /** Create empty linked list for taks */
                List<TakObject> taks = new LinkedList<TakObject>();

                /** Create a new map with label entered by user */
                MapObject newMap = new MapObject(mapNameText.getText().toString(), taks, privateSwitch.isChecked());

                /** Add managers stored in managerList */
                while(!managerList.isEmpty()){
                    newMap.addManager(managerList.remove(0));
                }

                /** Add map to the DB */
                newDB.addMap(newMap);
            }
        });

        addManagerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                managerList.add(managerEmailText.getText().toString());
                managerEmailText.setText("");
                managerEmailText.setHint("Manager added successfully");

            }
        });

        return view;
    }
}
