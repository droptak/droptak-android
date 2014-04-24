package com.droptak.android.fragments.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.VideoView;

import com.droptak.android.R;
import com.droptak.android.data.MapObject;
import com.droptak.android.data.MapTakDB;
import com.droptak.android.fragments.DrawerFragment;
import com.droptak.android.tasks.DeleteMapTask;

public class MapInfoDialog extends DialogFragment {

    private MapObject map;

    /** Views */
    private EditText etMapName, etMapDesc;
    private TextView tvOwner, tvID;
    private Dialog mapInfoDialog;

    public MapInfoDialog() {}

    public MapInfoDialog(MapObject mo) {
        this.map = mo;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());



        // Set title
        builder.setTitle("Edit Your Map");

        // Set the main content view
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_map_info, null);
        builder.setView(v);

        // Set positive and negative buttons
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                getDialog().cancel();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                getDialog().cancel();
            }
        });

        // Set the name of the map to be editable
        etMapName = (EditText) v.findViewById(R.id.mapinfo_et_mapname);
        etMapName.setText(map.getName());

        // Set the description of the map to be editable
        etMapDesc = (EditText) v.findViewById(R.id.mapinfo_et_mapdescription);
        //etMapDesc.setText(map.getDescription());

        // Set up the owner
        tvOwner = (TextView) v.findViewById(R.id.mapinfo_tv_owner);
        tvOwner.setText("Owned by " + map.getOwner().getName());

        // Set the ID
        tvID = (TextView) v.findViewById(R.id.mapinfo_tv_mapid);
        tvID.setText("ID: " + map.getID().toString());

        // Get button on the view and wire it up
        Button buEditAdmins = (Button) v.findViewById(R.id.mapinfo_bu_editadmins);
        Button deleteButton= (Button)v.findViewById(R.id.deleteMap);
        buEditAdmins.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                // Create an admin dialog and create it
                AdminDialog d = new AdminDialog(map.getID());
                d.show(getFragmentManager(), "admin-edit-dialog");

            }
        });

        // start proccess for deleting a map, creator a dialog to confirm
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure you want to delete this map?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    // delete the map from the db, and close the dialogs
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                        dialog.dismiss();
                        mapInfoDialog.dismiss();
                        // Delete map from web
                        DeleteMapTask deleteMapTask = new DeleteMapTask(getActivity(),map);
                        deleteMapTask.execute();
                        // Delete map from local db
                        MapTakDB db =  MapTakDB.getDB(getActivity());
                        db.deleteMap(map.getID());
                        // Refresh the drawer
                        getFragmentManager().beginTransaction().replace(R.id.left_drawer,new DrawerFragment()).commit();
                    }

                });

                // bring user back to MapInfoDialog
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }

        });


        mapInfoDialog = builder.create();
        return mapInfoDialog;
    }
}
