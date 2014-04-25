package com.droptak.android.fragments.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.droptak.android.R;
import com.droptak.android.data.MapTakDB;
import com.droptak.android.data.TakID;
import com.droptak.android.data.TakObject;
import com.droptak.android.fragments.DrawerFragment;
import com.droptak.android.fragments.MapViewFragment;
import com.droptak.android.fragments.SplashFragment;
import com.droptak.android.interfaces.OnTakUpdateListener;
import com.droptak.android.tasks.DeleteTakTask;
import com.droptak.android.tasks.EditTakTask;

public class TakInfoDialog extends DialogFragment
        implements View.OnClickListener, DialogInterface.OnClickListener {

    private TakID takID;
    private EditText etName, etLat, etLng;

    public TakInfoDialog(TakID takID) {
        this.takID = takID;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Create the dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Set title
        builder.setTitle("Tak MetaData");

        // Inflate the view
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_tak_info, null);
        builder.setView(v);

        // Get a copy of the database and get the tak
        MapTakDB db = MapTakDB.getDB(getActivity());
        TakObject tak = db.getTak(takID);

        // Create edit texts
        etName = (EditText)v.findViewById(R.id.takinfo_et_name);
        etLat = (EditText)v.findViewById(R.id.takinfo_et_lat);
        etLng = (EditText)v.findViewById(R.id.takinfo_et_lng);

        // Set their text
        etName.setText(tak.getName());
        etLat.setText("" + tak.getLat());
        etLng.setText("" + tak.getLng());

        // Create buttons and set listeners
        Button metadataButton = (Button) v.findViewById(R.id.takinfo_bu_metadata);
        Button deleteButton =(Button) v.findViewById(R.id.takinfo_bu_delete);
        metadataButton.setOnClickListener(this);
        deleteButton.setOnClickListener(this);

        // Set the positive and negative buttons
        builder.setPositiveButton("Submit", this);
        builder.setNegativeButton("Cancel", this);

        // Build it
        return builder.create();

    }

    @Override
    public void onClick(View view) {

        // Get the tak object from the database
        MapTakDB db = MapTakDB.getDB(getActivity());
        TakObject t = db.getTak(takID);

        switch (view.getId()){

            case R.id.takinfo_bu_metadata:
                TakMetadataDialog takMetadataDialog = new TakMetadataDialog(takID);
                takMetadataDialog.show(getFragmentManager(), "something");
                break;

            case R.id.takinfo_bu_delete:

                // Create the dialog
                DeleteTakConfirmationDialog d = new DeleteTakConfirmationDialog(getDialog(), takID);
                d.show(getFragmentManager(), "asdf");
                break;

        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

        switch (which) {

            case DialogInterface.BUTTON_POSITIVE:

                // Get the name, lat, and lng
                String name = etName.getText().toString();
                double lat = Double.parseDouble(etLat.getText().toString());
                double lng = Double.parseDouble(etLng.getText().toString());

                // Get the tak object
                MapTakDB db = MapTakDB.getDB(getActivity());
                TakObject takO = db.getTak(takID);
                takO.setName(name);
                takO.setLat(lat);
                takO.setLng(lng);

                // Start the execution to update the tak
                final FragmentManager manager = getFragmentManager();
                EditTakTask task = new EditTakTask(getActivity(), takO, new OnTakUpdateListener() {
                    public void onTakUpdate(TakID id) {
                        // Recreate the map
                        manager.beginTransaction().replace(R.id.mainview, new MapViewFragment()).commit();
                    }
                });
                task.execute();

                break;

            case DialogInterface.BUTTON_NEGATIVE:
                getDialog().cancel();
                break;

        }

    }
}
