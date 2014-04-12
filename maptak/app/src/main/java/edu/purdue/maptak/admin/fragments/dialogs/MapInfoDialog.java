package edu.purdue.maptak.admin.fragments.dialogs;

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

import edu.purdue.maptak.admin.R;
import edu.purdue.maptak.admin.data.MapObject;

public class MapInfoDialog extends DialogFragment {

    private MapObject map;

    /** Views */
    private EditText etMapName, etMapDesc;
    private TextView tvOwner;

    public MapInfoDialog(MapObject mo) {
        this.map = mo;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Set title
        builder.setTitle(map.getLabel());

        // Set the main content view
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_map_info, null);
        builder.setView(v);

        // Set positive and negative buttons
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                getDialog().cancel();
            }
        });

        // Set the owner of the map
        etMapName = (EditText) v.findViewById(R.id.mapinfo_et_mapname);
        etMapName.setText(map.getLabel());

        // Set the administrators of the map
        etMapDesc = (EditText) v.findViewById(R.id.mapinfo_et_mapdescription);
        //etMapDesc.setText(map.getDescription());

        // Set up the owner
        tvOwner = (TextView) v.findViewById(R.id.mapinfo_tv_owner);
        tvOwner.setText(map.getOwner().toString());

        // Get button on the view and wire it up
        Button buEditAdmins = (Button) v.findViewById(R.id.mapinfo_bu_editadmins);
        buEditAdmins.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //getDialog().setContentView();
            }
        });

        return builder.create();
    }
}
