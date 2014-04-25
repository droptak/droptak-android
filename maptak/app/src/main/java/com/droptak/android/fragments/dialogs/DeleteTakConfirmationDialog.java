package com.droptak.android.fragments.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;

import com.droptak.android.R;
import com.droptak.android.data.MapTakDB;
import com.droptak.android.data.TakID;
import com.droptak.android.data.TakObject;
import com.droptak.android.fragments.DrawerFragment;
import com.droptak.android.fragments.SplashFragment;
import com.droptak.android.tasks.DeleteTakTask;

public class DeleteTakConfirmationDialog extends DialogFragment {

    private Dialog parent;
    private TakID tak;

    public DeleteTakConfirmationDialog(Dialog parent, TakID tak) {
        this.parent = parent;
        this.tak = tak;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Confirm");
        builder.setMessage("Are you sure you want to delete this tak?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            // delete the map from the db, and close the dialogs
            public void onClick(DialogInterface dialog, int which) {

                // Close the dialogs
                dialog.dismiss();
                parent.dismiss();

                // Get the tak object
                MapTakDB db = MapTakDB.getDB(getActivity());
                TakObject takO = db.getTak(tak);

                // Start the delete task
                final FragmentManager manager = getFragmentManager();
                DeleteTakTask deleteTakTask = new DeleteTakTask(getActivity(), takO);
                deleteTakTask.execute();

                // Refresh the drawer
                getFragmentManager().beginTransaction().replace(R.id.left_drawer,new DrawerFragment()).commit();
                getFragmentManager().beginTransaction().replace(R.id.mainview, new SplashFragment()).commit();
            }

        });

        // Bring user back to MapInfoDialog
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return builder.create();
    }
}
