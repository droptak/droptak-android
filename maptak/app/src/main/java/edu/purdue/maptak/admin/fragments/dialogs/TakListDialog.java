package edu.purdue.maptak.admin.fragments.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import java.util.List;

import edu.purdue.maptak.admin.data.TakObject;

public class TakListDialog extends DialogFragment implements DialogInterface.OnClickListener {

    private List<TakObject> taks;

    public TakListDialog(List<TakObject> taks) {
        this.taks = taks;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Get the builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Set the title
        builder.setTitle("Your Taks");

        // Set the items in the view
        String[] items = new String[taks.size()];
        for (int i = 0; i < taks.size(); i++) {
            items[i] = taks.get(i).getName();
        }
        builder.setItems(items, this);

        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {

    }
}
