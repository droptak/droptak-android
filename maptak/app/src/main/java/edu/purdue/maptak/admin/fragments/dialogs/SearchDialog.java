package edu.purdue.maptak.admin.fragments.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import edu.purdue.maptak.admin.R;
import edu.purdue.maptak.admin.data.MapObject;
import edu.purdue.maptak.admin.data.MapTakDB;

public class SearchDialog extends DialogFragment implements DialogInterface.OnClickListener {

    private EditText etSearchQuery;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Get the dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Set the title
        builder.setTitle("Search For Maps...");

        // Set the main content view
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_search, null);
        builder.setView(v);

        // Set positive and negative buttons
        builder.setPositiveButton("Search", this);
        builder.setNegativeButton("Cancel", this);

        // Get the edit text
        etSearchQuery = (EditText) v.findViewById(R.id.search_et_searchbar);

        // Return the dialog
        return builder.create();

    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {

        switch (i) {
            case DialogInterface.BUTTON_POSITIVE:

                // Get the query
                String query = etSearchQuery.getText().toString();

                // Do the search
                doSearch(query);

                break;

            case DialogInterface.BUTTON_NEGATIVE:
                getDialog().cancel();
                break;
        }
    }

    private void doSearch(String query) {

        // Lower-case the query
        query = query.toLowerCase();

        // Get the database and iterate over local maps
        MapTakDB db = MapTakDB.getDB(getActivity());
        List<MapObject> maps = db.getUsersMaps();

        // Assemble a list of possible results
        List<MapObject> results = new ArrayList<MapObject>();

        for (MapObject map : maps) {
            String name = map.getName();
            if (name.contains(query)) {
                results.add(map);
            }
        }

        // Create the search results dialog and display it.
        getDialog().cancel();
        SearchResultsDialog d = new SearchResultsDialog(results);
        d.show(getFragmentManager(), "search_results_dialog");


    }

}
