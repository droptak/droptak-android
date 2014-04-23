package com.droptak.android.fragments.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import com.droptak.android.R;
import com.droptak.android.data.MapObject;
import com.droptak.android.data.MapTakDB;
import com.droptak.android.interfaces.OnMapSearchResultListener;
import com.droptak.android.tasks.SearchMapsTask;

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

        // Store the fragment manager for later use
        final FragmentManager manager = getFragmentManager();

        // Do the search
        SearchMapsTask task = new SearchMapsTask(query, new OnMapSearchResultListener() {
            public void onSearchResult(List<MapObject> results) {

                // Create the results dialog
                SearchResultsDialog d = new SearchResultsDialog(results);
                d.show(manager, "search-results-dialog");

            }
        });
        task.execute();

    }

}
