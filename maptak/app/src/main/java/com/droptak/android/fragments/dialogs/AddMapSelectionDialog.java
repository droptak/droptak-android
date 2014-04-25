package com.droptak.android.fragments.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

public class AddMapSelectionDialog extends DialogFragment implements DialogInterface.OnClickListener {

    public static final int RC_QR_SCANNED = 143756;

    private String[] listItems = new String[] {
            "Create A Map",
            "Scan A QR Code",
            "Search"
    };

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Add A Map...");
        builder.setItems(listItems, this);

        return builder.create();

    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {

        switch (i) {
            case 0:
                // Create a new map
                CreateMapDialog d = new CreateMapDialog();
                d.show(getFragmentManager(), "create_map_dialog");
                break;
            case 1:
                // Scan a QR code
                getDialog().cancel();
                initiateQRScan();
                break;
            case 2:
                // Search for a map
                SearchDialog e = new SearchDialog();
                e.show(getFragmentManager(), "search_dialog");
                break;
        }

    }

    private void initiateQRScan() {
        // Craft intent to zxing
        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.putExtra("SCAN_MODE", "QR_CODE_MODE");

        try {
            getActivity().startActivityForResult(intent, RC_QR_SCANNED);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getActivity(), "Please install a QR code scanning application", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.google.zxing.client.android")));
        }
    }

}
