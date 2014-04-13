package edu.purdue.maptak.admin.fragments.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import java.io.File;
import java.nio.channels.FileChannel;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import edu.purdue.maptak.admin.R;
import edu.purdue.maptak.admin.activities.MainActivity;
import edu.purdue.maptak.admin.data.MapID;
import edu.purdue.maptak.admin.data.MapObject;
import edu.purdue.maptak.admin.data.MapTakDB;
import edu.purdue.maptak.admin.data.TakObject;
import edu.purdue.maptak.admin.data.UserID;
import edu.purdue.maptak.admin.fragments.DrawerFragment;
import edu.purdue.maptak.admin.tasks.CreateMapTask;


public class CreateMapDialog extends DialogFragment implements DialogInterface.OnClickListener {

    private EditText etName;
    private Switch swIsPrivate;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Create builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Set the alert dialog title
        builder.setTitle("Create A New Map");

        // Set main content view
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_createmap, null);
        builder.setView(v);

        // Set positive and negative buttons
        builder.setPositiveButton("Create", this);
        builder.setNegativeButton("Cancel", this);

        // Prepare the widgets on the screen
        etName = (EditText) v.findViewById(R.id.addmap_et_mapname);
        swIsPrivate = (Switch) v.findViewById(R.id.addmap_sw_isprivate);

        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int id) {

        // Do something whether the user has clicked "create" or "exit"
        switch (id) {
            case DialogInterface.BUTTON_POSITIVE:
                String name = etName.getText().toString();
                boolean isPrivate = swIsPrivate.isChecked();
                createMap(name, isPrivate);
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                getDialog().cancel();
                break;
        }

    }

    /** Creates the map and pushes it to the database using a background task */
    private void createMap(String mapName, boolean isPrivate) {

        // Get the shared preferences
        SharedPreferences prefs = getActivity().getSharedPreferences(MainActivity.SHARED_PREFS_NAME, 0);
        String uid = prefs.getString(MainActivity.PREF_USER_MAPTAK_TOKEN, "");
        String userName = prefs.getString(MainActivity.PREF_USER_GPLUS_NAME, "");

        // Create a filler map object that will hold all the information pushed to the server
        MapObject map = new MapObject();
        map.setName(mapName);
        map.setOwner(new UserID(uid, userName));

        // TODO: Properly set if it is public or not
        map.setIsPublic(false);

        // Create and execute the task which adds the map to the database and the server
        CreateMapTask task = new CreateMapTask(getActivity(), map);
        task.execute();

        // Close the dialog and re-inflate the side drawer to refresh the map list
        getFragmentManager().beginTransaction().replace(R.id.left_drawer, new DrawerFragment()).commit();
        getDialog().cancel();
    }

    /** Saved this code from kyle's implementation of this functionality */
    private void backupDatabase() throws FileNotFoundException, IOException {
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();

        if (sd.canWrite()) {
            String currentDBPath = "//data//"+ "edu.purdue.maptak.admin" +"//databases//"+"database_cached_taks";
            String backupDBPath = "database_cahced_taks";
            File currentDB = new File(data, currentDBPath);
            File backupDB = new File(sd, backupDBPath);

            FileChannel src = new FileInputStream(currentDB).getChannel();
            FileChannel dst = new FileOutputStream(backupDB).getChannel();
            dst.transferFrom(src, 0, src.size());
            src.close();
            dst.close();
            // Toast.makeText(, backupDB.toString(), Toast.LENGTH_LONG).show();
            Log.d("debug","sql backupmade");

        }
    }

}
