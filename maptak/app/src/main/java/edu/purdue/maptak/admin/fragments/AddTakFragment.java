package edu.purdue.maptak.admin.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.location.LocationManager;
import android.widget.EditText;
import android.widget.Toast;
//import android.accounts.AccountManager;
//import android.accounts.Account;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;

import edu.purdue.maptak.admin.MainActivity;
import edu.purdue.maptak.admin.R;
import edu.purdue.maptak.admin.TakFragmentManager;
import edu.purdue.maptak.admin.data.MapID;
import edu.purdue.maptak.admin.data.MapObject;
import edu.purdue.maptak.admin.data.MapTakDB;
import edu.purdue.maptak.admin.data.TakID;
import edu.purdue.maptak.admin.data.TakObject;
import edu.purdue.maptak.admin.interfaces.OnGMapLoadedListener;

public class AddTakFragment extends Fragment implements
        View.OnClickListener, GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {

    /** Stores the user's location while this fragment is on the screen */
    private boolean isLocationAvailable = false;
    private LocationClient userLocation;

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
        etName = (EditText) view.findViewById(R.id.addmap_et_takname);
        etDescription = (EditText) view.findViewById(R.id.addmap_et_description);
        Button buFromCurrent = (Button) view.findViewById(R.id.addmap_bu_fromcurrent);
        Button buSelectLoc = (Button) view.findViewById(R.id.addmap_bu_selectedlocation);

        // Set on click listeners
        buFromCurrent.setOnClickListener(this);
        buSelectLoc.setOnClickListener(this);

        // Create the location client
        userLocation = new LocationClient(getActivity(), this, this);
        userLocation.connect();

        return view;
    }

    /** Called when the fragment leaves the screen */
    public void onStop() {
        super.onStop();
        userLocation.disconnect();
    }

    /** Called when the user clicks one of the buttons on the screen */
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.addmap_bu_fromcurrent:

                // Get the user's current location
                if (isLocationAvailable) {
                    String name = etName.getText().toString();
                    double lat = userLocation.getLastLocation().getLatitude();
                    double lng = userLocation.getLastLocation().getLongitude();

                    TakObject newTak = new TakObject(name, lat, lng);
                    MapTakDB db = new MapTakDB(getActivity());
                    db.addTak(newTak, MainActivity.currentSelectedMap);
                } else {
                    Toast.makeText(getActivity(), "Location is not currently available.", Toast.LENGTH_SHORT).show();
                }

                // Return back to the main screen
                exitToMap();

                break;

            case R.id.addmap_bu_selectedlocation:

                break;
        }

    }

    /** Removes this fragment from the screen and returns back to the main activity */
    public void exitToMap() {

        // Hide the keyboard if necessary
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Reinflate a map fragment
        MapTakDB db = new MapTakDB(getActivity());
        MapObject mo = db.getMap(MainActivity.currentSelectedMap);
        TakFragmentManager.switchToMap(getActivity().getFragmentManager(), mo);

    }

    /** Called by GPlayServices when the request to establish a connection is completed */
    public void onConnected(Bundle bundle) {
        isLocationAvailable = true;
    }

    @Override
    public void onDisconnected() {
        isLocationAvailable = false;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        isLocationAvailable = false;
    }
}

