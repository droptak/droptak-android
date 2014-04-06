package edu.purdue.maptak.admin.fragments;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

import edu.purdue.maptak.admin.R;
import edu.purdue.maptak.admin.activities.MainActivity;
import edu.purdue.maptak.admin.data.MapID;
import edu.purdue.maptak.admin.data.MapTakDB;
import edu.purdue.maptak.admin.data.TakObject;
import edu.purdue.maptak.admin.fragments.dialogs.CreateTakDialog;

public class MapViewFragment extends Fragment implements View.OnClickListener {

    private TakMapFragment mapFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_takmapview, container, false);

        // Prepare buttons on screen
        Button buAddTak = (Button) v.findViewById(R.id.takmapview_bu_addtak);
        buAddTak.setOnClickListener(this);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Add the google map to the screen
        // This is done here because layout_mainview isn't inflated until after onCreateView
        mapFragment = new TakMapFragment();
        getFragmentManager().beginTransaction().replace(R.id.takmapview_layout_mainview, mapFragment).commit();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.takmapview_bu_addtak:

                // Show the dialog box for creating a new tak
                new CreateTakDialog().show(getFragmentManager(), "create_tak_dialog");

                // Get the shared preferences and database
                MapTakDB db = MapTakDB.getDB(getActivity());
                SharedPreferences prefs = getActivity().getSharedPreferences(MainActivity.SHARED_PREFS_NAME, 0);

                // Update the map with the new tak we just added
                MapID id = new MapID(prefs.getString(MainActivity.PREF_CURRENT_MAP, ""));
                mapFragment.addTaksToGMap(db.getMap(id));

                break;
        }

    }
}
