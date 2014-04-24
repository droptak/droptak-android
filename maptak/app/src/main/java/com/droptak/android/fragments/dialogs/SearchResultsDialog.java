package com.droptak.android.fragments.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import com.droptak.android.activities.MainActivity;
import com.droptak.android.data.MapObject;
import com.droptak.android.R;
import com.droptak.android.data.MapTakDB;
import com.droptak.android.data.TakMetadata;
import com.droptak.android.data.TakObject;
import com.droptak.android.data.User;
import com.droptak.android.fragments.DrawerFragment;
import com.droptak.android.fragments.MapViewFragment;

public class SearchResultsDialog extends DialogFragment implements DialogInterface.OnClickListener {

    private List<MapObject> results;

    public SearchResultsDialog(List<MapObject> results) {
        this.results = results;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Get the builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Results");

        // Assemble list of strings and set them as items in the interface
        List<String> resultStrings = new ArrayList<String>();
        for (MapObject m : results) {
            resultStrings.add(m.getName());
        }
        String[] array = resultStrings.toArray(new String[resultStrings.size()]);
        builder.setItems(array, this);

        return builder.create();

    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {

        // Get the object selected
        MapObject selected = results.get(i);

        // Add it to the local database
        MapTakDB db = MapTakDB.getDB(getActivity());
        db.addMap(selected);
        for (TakObject t : selected.getTaks()) {
            db.addTak(t, selected.getID());

            for (TakMetadata tm : t.getMeta().values()) {
                db.addTakMetadata(t.getID(), tm);
            }
        }
        for (User u : selected.getManagers()) {
            db.addAdmin(u, selected.getID());
        }

        // Refresh the side bar
        getFragmentManager().beginTransaction().replace(R.id.left_drawer, new DrawerFragment()).commit();

        // Set it as the selected map
        SharedPreferences p = getActivity().getSharedPreferences(MainActivity.SHARED_PREFS_NAME, 0);
        p.edit().putString(MainActivity.PREF_CURRENT_MAP, selected.getID().toString()).commit();

        // Close the dialog and inflate the map view
        getFragmentManager().beginTransaction().replace(R.id.mainview, new MapViewFragment(true)).commit();
        getDialog().cancel();
    }
}
