package com.droptak.android.fragments.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.droptak.android.R;
import com.droptak.android.activities.MainActivity;
import com.droptak.android.data.MapObject;
import com.droptak.android.fragments.DrawerFragment;
import com.droptak.android.fragments.SplashFragment;
import com.droptak.android.tasks.DeleteMapTask;

/**
 * Created by mike on 4/24/14.
 */
public class DeleteMapConfirmationDialog extends DialogFragment {

    private Dialog parent;
    private MapObject toDelete;

    public DeleteMapConfirmationDialog(Dialog parent, MapObject toDelete) {
        this.parent = parent;
        this.toDelete = toDelete;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Confirm");
        builder.setMessage("Are you sure you want to delete this map?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            // delete the map from the db, and close the dialogs
            public void onClick(DialogInterface dialog, int which) {

                // Close the dialogs
                dialog.dismiss();
                parent.dismiss();

                // Start the delete task
                final FragmentManager manager = getFragmentManager();
                DeleteMapTask deleteMapTask = new DeleteMapTask(getActivity(), toDelete, null);
                deleteMapTask.execute();

                // Refresh the drawer
                getFragmentManager().beginTransaction().replace(R.id.left_drawer,new DrawerFragment()).commit();

                // If the map we're deleting is the one selected, clear the main view
                SharedPreferences prefs = getActivity().getSharedPreferences(MainActivity.SHARED_PREFS_NAME, 0);
                String activeID = prefs.getString(MainActivity.PREF_CURRENT_MAP, "");
                if (activeID.equals(toDelete.getID().toString())) {
                    getFragmentManager().beginTransaction().replace(R.id.mainview, new SplashFragment()).commit();
                }

            }

        });

        // Bring user back to MapInfoDialog
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return builder.create();

    }
}
