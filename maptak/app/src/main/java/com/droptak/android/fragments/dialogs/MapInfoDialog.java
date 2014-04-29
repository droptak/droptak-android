package com.droptak.android.fragments.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.VideoView;

import com.droptak.android.R;
import com.droptak.android.activities.MainActivity;
import com.droptak.android.data.MapID;
import com.droptak.android.data.MapObject;
import com.droptak.android.data.MapTakDB;
import com.droptak.android.fragments.DrawerFragment;
import com.droptak.android.fragments.SplashFragment;
import com.droptak.android.interfaces.OnMapDeletedListener;
import com.droptak.android.interfaces.OnMapUpdateListener;
import com.droptak.android.tasks.DeleteMapTask;
import com.droptak.android.tasks.EditMapTask;

public class MapInfoDialog extends DialogFragment
        implements View.OnClickListener, DialogInterface.OnClickListener {

    private static final String BUNDLE_KEY_MAPID = "map_id";

    public static final MapInfoDialog newInstanceOf(MapID id) {
        MapInfoDialog dialog = new MapInfoDialog();
        Bundle b = new Bundle();
        b.putString(BUNDLE_KEY_MAPID, id.toString());
        dialog.setArguments(b);
        return dialog;
    }

    private MapID mapID;
    private EditText etMapName, etMapDesc;
    private TextView tvOwner, tvID;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the map from the arguments
        mapID = new MapID(getArguments().getString(BUNDLE_KEY_MAPID));
        MapObject map = MapTakDB.getDB(getActivity()).getMap(mapID);

        // Set title
        builder.setTitle("Edit Your Map");

        // Set the main content view
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_map_info, null);
        builder.setView(v);

        // Set positive and negative buttons
        builder.setPositiveButton("Submit", this);
        builder.setNegativeButton("Cancel", this);

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
        Button deleteButton = (Button) v.findViewById(R.id.mapinfo_bu_deletemap);
        buEditAdmins.setOnClickListener(this);
        deleteButton.setOnClickListener(this);

        // start proccess for deleting a map, creator a dialog to confirm
        deleteButton.setOnClickListener(this);

        return builder.create();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.mapinfo_bu_editadmins:

                // Create an admin dialog and create it
                AdminDialog d = AdminDialog.newInstanceOf(mapID);
                d.show(getFragmentManager(), "admin-edit-dialog");

                break;

            case R.id.mapinfo_bu_deletemap:

                // Create a delete map confirm dialog
                //new DeleteMapConfirmationDialog(this.getDialog(), mapID).show(getFragmentManager(), "asdf");

                break;

        }

    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

        switch (which) {

            case DialogInterface.BUTTON_POSITIVE:

                // Get the new name
                String name = etMapName.getText().toString();

                // Submit the new name to the server
                final FragmentManager manager = getFragmentManager();
                EditMapTask task = new EditMapTask(getActivity(), mapID, new OnMapUpdateListener() {
                    public void onMapUpdate(MapID id) {
                        // Update the drawer fragment
                        manager.beginTransaction().replace(R.id.left_drawer, new DrawerFragment()).commit();
                    }
                });
                task.execute();

                break;

            case DialogInterface.BUTTON_NEGATIVE:

                // Close the dialog
                getDialog().cancel();

                break;

        }

    }
}
