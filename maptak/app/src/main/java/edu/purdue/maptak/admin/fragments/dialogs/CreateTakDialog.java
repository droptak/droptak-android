package edu.purdue.maptak.admin.fragments.dialogs;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import edu.purdue.maptak.admin.R;
import edu.purdue.maptak.admin.activities.MainActivity;
import edu.purdue.maptak.admin.data.MapID;
import edu.purdue.maptak.admin.data.MapTakDB;
import edu.purdue.maptak.admin.data.TakID;
import edu.purdue.maptak.admin.data.TakObject;
import edu.purdue.maptak.admin.interfaces.OnLocationReadyListener;
import edu.purdue.maptak.admin.managers.UserLocationManager;
import edu.purdue.maptak.admin.tasks.AddTakTask;

public class CreateTakDialog extends DialogFragment implements View.OnClickListener, DialogInterface.OnClickListener {

    private UserLocationManager manager;
    private EditText etTakName, etTakDesc, etLat, etLng;
    private Button buSelectLoc;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Create the builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Set the title
        builder.setTitle("Pin a New Tak");

        // Set the main content view
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_addtak, null);
        builder.setView(v);

        // Set positive and negative buttons
        builder.setPositiveButton("Pin", this);
        builder.setNegativeButton("Cancel", this);

        // Get widgets on the dialog fragment
        etTakName = (EditText) v.findViewById(R.id.addtak_et_takname);
        etTakDesc = (EditText) v.findViewById(R.id.addtak_et_description);
        etLat = (EditText) v.findViewById(R.id.addtak_et_lat);
        etLng = (EditText) v.findViewById(R.id.addtak_et_lng);
        buSelectLoc = (Button) v.findViewById(R.id.addtak_bu_selectlocation);
        buSelectLoc.setOnClickListener(this);

        // Create our location client
        manager = new UserLocationManager(getActivity());
        manager.setOnLocationReadyListener(new OnLocationReadyListener() {
            public void onLocationReady() {
                etLat.setText(("" + manager.getLat()).substring(0,9));
                etLng.setText(("" + manager.getLng()).substring(0,9));
            }
        });

        return builder.create();

    }

    @Override
    public void onClick(View view) {

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

        // Create the first tak object
        TakObject oldtak = new TakObject(name, lat, lng);

        // Pass it to the AddTakTask and start it up
        AddTakTask task = new AddTakTask(getActivity(), oldtak, mapID);
        String json = null;
        try {
            json = task.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        // Create a json object from that string and extract the new takid
        String takID = null;
        try {
            JSONObject jObj = new JSONObject(json);
            takID = jObj.getString("takId");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Create the TakID object and the new TakObject
        TakID id = new TakID(takID);
        TakObject newtak = new TakObject(id, name, lat, lng);

        // Add it to the database
        MapTakDB db = MapTakDB.getDB(getActivity());
        db.addTak(newtak, mapID);

        // Close the dialog box
        getDialog().cancel();

    }


}
