package com.droptak.android.fragments.dialogs;



import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.droptak.android.R;
import com.droptak.android.activities.MainActivity;
import com.droptak.android.data.MapTakDB;
import com.droptak.android.data.TakID;
import com.droptak.android.data.TakMetadata;
import com.droptak.android.data.TakObject;
import com.droptak.android.fragments.DrawerFragment;
import com.droptak.android.fragments.SplashFragment;
import com.droptak.android.tasks.DeleteMapTask;
import com.droptak.android.tasks.DeleteTakTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 *
 */
public class TakInfoDialog extends DialogFragment implements View.OnClickListener {

    ListView listView;
    TakID takID;
    TakObject tak;
    Button button;
    EditText key;
    EditText value;
    Dialog takInfoDialog;

    public TakInfoDialog(TakID takID) {
        this.takID = takID;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        AlertDialog.Builder dataList= new AlertDialog.Builder(getActivity());
        dataList.setTitle("Tak MetaData");
        MapTakDB db = MapTakDB.getDB(getActivity());
         tak = db.getTak(takID);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_tak_info, null);
        dataList.setView(v);


        EditText takName = (EditText)v.findViewById(R.id.takInfo_editName);
        EditText takLat = (EditText)v.findViewById(R.id.takInfo_editLat);
        EditText takLong = (EditText)v.findViewById(R.id.takInfo_editLon);
        Button metadataButton = (Button)v.findViewById(R.id.takInfo_metadataButton);
        Button deleteButton =(Button)v.findViewById(R.id.takInfo_deleteButton);
        metadataButton.setOnClickListener(this);
        deleteButton.setOnClickListener(this);

        takName.setText(tak.getName());
        takLat.setText(""+tak.getLat());
        takLong.setText(""+tak.getLng());


        takInfoDialog = dataList.create();
        return  takInfoDialog;
    }

    @Override
    public void onClick(View view) {
        //add the metadata to the TakObject and refresh the
        switch (view.getId()){
            case R.id.takInfo_metadataButton:
                TakMetadataDialog takMetadataDialog = new TakMetadataDialog(tak.getID());
                takMetadataDialog.show(getFragmentManager(), "something");
                break;

            case R.id.takInfo_deleteButton:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure you want to delete this tak?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    // delete the map from the db, and close the dialogs
                    public void onClick(DialogInterface dialog, int which) {

                        // Close the dialogs
                        dialog.dismiss();
                        takInfoDialog.dismiss();

                        // Start the delete task
                        final FragmentManager manager = getFragmentManager();
                        DeleteTakTask deleteTakTask = new DeleteTakTask(getActivity(),tak);
                        deleteTakTask.execute();

                        // Refresh the drawer
                        getFragmentManager().beginTransaction().replace(R.id.left_drawer,new DrawerFragment()).commit();
                        getFragmentManager().beginTransaction().replace(R.id.mainview, new SplashFragment()).commit();
                    }

                });

                // Bring user back to MapInfoDialog
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
                break;
            

        }
    }


}
