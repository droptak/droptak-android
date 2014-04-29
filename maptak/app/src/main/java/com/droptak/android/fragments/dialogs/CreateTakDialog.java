package com.droptak.android.fragments.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.droptak.android.R;
import com.droptak.android.activities.MainActivity;
import com.droptak.android.data.MapID;
import com.droptak.android.data.TakObject;
import com.droptak.android.fragments.MapViewFragment;
import com.droptak.android.interfaces.OnLocationReadyListener;
import com.droptak.android.managers.UserLocationManager;
import com.droptak.android.tasks.CreateTakTask;
import com.google.android.gms.maps.model.LatLng;

public class CreateTakDialog extends DialogFragment
        implements DialogInterface.OnClickListener {

    private final static String BUNDLE_KEY_LAT = "point_lat";
    private final static String BUNDLE_KEY_LNG = "point_lng";

    public static CreateTakDialog newInstanceOf(LatLng loc) {
        CreateTakDialog dialog = new CreateTakDialog();
        Bundle b = new Bundle();
        b.putDouble(BUNDLE_KEY_LAT, loc.latitude);
        b.putDouble(BUNDLE_KEY_LNG, loc.longitude);
        dialog.setArguments(b);
        return dialog;
    }

    private LatLng loc;
    private UserLocationManager manager;
    private EditText etTakName, etTakDesc, etLat, etLng;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Get latitude and longitude
        double lat = getArguments().getDouble(BUNDLE_KEY_LAT);
        double lng = getArguments().getDouble(BUNDLE_KEY_LNG);
        loc = new LatLng(lat, lng);

        // Create the builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Set the title
        builder.setTitle("Pin a New Tak");

        // Set the main content view
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_createtak, null);
        builder.setView(v);

        // Set positive and negative buttons
        builder.setPositiveButton("Pin", this);
        builder.setNegativeButton("Cancel", this);

        // Get widgets on the dialog fragment
        etTakName = (EditText) v.findViewById(R.id.addtak_et_takname);
        etTakDesc = (EditText) v.findViewById(R.id.addtak_et_description);
        etLat = (EditText) v.findViewById(R.id.addtak_et_lat);
        etLng = (EditText) v.findViewById(R.id.addtak_et_lng);

        // Add location passed in to the edit texts
        etLat.setText("" + loc.latitude);
        etLng.setText("" + loc.longitude);

        return builder.create();

    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {

        switch (i) {
            case DialogInterface.BUTTON_POSITIVE:
                createTak();
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                getDialog().cancel();
                break;
        }

    }

    private void createTak() {

        // Get the currently selected mapID
        SharedPreferences prefs = getActivity().getSharedPreferences(MainActivity.SHARED_PREFS_NAME, 0);
        String mapIDStr = prefs.getString(MainActivity.PREF_CURRENT_MAP, "");
        MapID mapID = null;
        if (mapIDStr != "") {
            mapID = new MapID(mapIDStr);
        }

        // Get information for new tak
        String name = etTakName.getText().toString();
        String desc = etTakDesc.getText().toString();
        double lat = Double.parseDouble(etLat.getText().toString());
        double lng = Double.parseDouble(etLng.getText().toString());

        // If the name is empty, make them enter one
        if (name.equals("")) {
            Toast.makeText(getActivity(), "Enter a name for your tak!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create the first tak object
        TakObject oldtak = new TakObject();
        oldtak.setName(name);
        oldtak.setLat(lat);
        oldtak.setLng(lng);
        oldtak.setID(null);
        oldtak.setMetadata(null);

        // Pass it to the CreateTakTask and start it up
        new CreateTakTask(getActivity(), oldtak, mapID).execute();

        // Refresh the main view with the new tak
        getFragmentManager().beginTransaction().replace(R.id.mainview, new MapViewFragment()).commit();
        
        // Close the dialog box
        getDialog().cancel();

    }


}
