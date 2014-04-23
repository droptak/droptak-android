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

import com.droptak.android.R;
import com.droptak.android.data.MapObject;

public class MapInfoDialog extends DialogFragment {

    private MapObject map;

    /** Views */
    private EditText etMapName, etMapDesc;
    private TextView tvOwner, tvID;

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
        buEditAdmins.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

            }
        });

        return builder.create();
    }
}
