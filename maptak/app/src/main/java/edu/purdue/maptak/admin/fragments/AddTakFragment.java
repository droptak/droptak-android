package edu.purdue.maptak.admin.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import edu.purdue.maptak.admin.R;
import edu.purdue.maptak.admin.managers.TakFragmentManager;
import edu.purdue.maptak.admin.managers.UserLocationManager;
import edu.purdue.maptak.admin.data.MapID;
import edu.purdue.maptak.admin.data.MapObject;
import edu.purdue.maptak.admin.data.MapTakDB;
import edu.purdue.maptak.admin.data.TakID;
import edu.purdue.maptak.admin.data.TakObject;
import edu.purdue.maptak.admin.tasks.AddTakTask;

public class AddTakFragment extends Fragment implements View.OnClickListener {

    /** Location manager for location updates */
    private UserLocationManager locManager;

    /** EditText widgets on screen */
    private EditText etName, etDescription;

    /** Stores the MapID of the map that all taks created with this fragment should use */
    private MapID id;

    /** Create a new instance of this fragment with the given MapID as the owner for all taks
     *  created inside this fragment */
    public static AddTakFragment newInstanceOf(MapID id) {
        AddTakFragment f = new AddTakFragment();
        f.id = id;
        return f;
    }

    /** Inflates the view for this fragment. */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_addtak, container, false);

        // Get widgets from the screen
        etName = (EditText) view.findViewById(R.id.addtak_et_takname);
        etDescription = (EditText) view.findViewById(R.id.addtak_et_description);
        Button buFromCurrent = (Button) view.findViewById(R.id.addtak_bu_fromcurrent);
        Button buSelectLoc = (Button) view.findViewById(R.id.addtak_bu_selectedlocation);

        // Set on click listeners
        buFromCurrent.setOnClickListener(this);
        buSelectLoc.setOnClickListener(this);

        // Create the location client
        locManager = new UserLocationManager(getActivity());

        Log.d("debug","mapId="+id.toString());

        return view;
    }

    /** Called when the fragment leaves the screen */
    public void onStop() {
        super.onStop();
        locManager.disconnect();
    }

    /** Called when the user clicks one of the buttons on the screen */
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.addtak_bu_fromcurrent:

                // Get the user's current location
                if (locManager.isLocationAvailable()) {

                    String name = etName.getText().toString();

                    double lat = locManager.getLat();
                    double lng = locManager.getLng();
                    String jsonString = null;
                    String tid = null;
                    //TakObject newTak = new TakObject(name, lat, lng);
                    AddTakTask addTakTask = new AddTakTask(name,""+lat,""+lng,id,this.getActivity());
                    try {
                        jsonString = addTakTask.execute().get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    JSONObject jsonObject = null;

                    try {
                         jsonObject = new JSONObject(jsonString);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        tid = jsonObject.getString("takId");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    TakID takID = new TakID(tid);
                    TakObject tak = new TakObject(takID,name,lat,lng);
                    MapTakDB db = MapTakDB.getDB(getActivity());
                    db.addTak(tak, id);

                } else {
                    Toast.makeText(getActivity(), "Location is not currently available.", Toast.LENGTH_SHORT).show();
                }

                // Return back to the main screen
                exitToMap();

                break;

            case R.id.addtak_bu_selectedlocation:

                break;
        }

    }

    /** Removes this fragment from the screen and returns back to the main activity */
    public void exitToMap() {

        // Hide the keyboard if necessary
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Reinflate a map fragment
        MapTakDB db = MapTakDB.getDB(getActivity());
        MapObject mo = db.getMap(id);
        TakFragmentManager.switchToMap(getActivity(), mo);

    }


}

