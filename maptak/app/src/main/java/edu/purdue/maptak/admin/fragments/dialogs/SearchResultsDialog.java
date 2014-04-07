package edu.purdue.maptak.admin.fragments.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.purdue.maptak.admin.activities.MainActivity;
import edu.purdue.maptak.admin.data.MapObject;
import edu.purdue.maptak.admin.R;
import edu.purdue.maptak.admin.fragments.MapViewFragment;

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
            resultStrings.add(m.getLabel());
        }
        String[] array = resultStrings.toArray(new String[resultStrings.size()]);
        builder.setItems(array, this);

        return builder.create();

    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {

        // Get the object selected
        MapObject selected = results.get(i);

        // Set it as the selected map
        SharedPreferences p = getActivity().getSharedPreferences(MainActivity.SHARED_PREFS_NAME, 0);
        p.edit().putString(MainActivity.PREF_CURRENT_MAP, selected.getID().toString()).commit();

        // Close the dialog and inflate the map view
        getFragmentManager().beginTransaction().replace(R.id.mainview, new MapViewFragment()).commit();
        getDialog().cancel();
    }
}
