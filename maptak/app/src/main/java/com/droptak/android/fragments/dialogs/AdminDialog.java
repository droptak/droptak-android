package com.droptak.android.fragments.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.droptak.android.R;
import com.droptak.android.data.MapID;
import com.droptak.android.data.MapObject;
import com.droptak.android.data.MapTakDB;
import com.droptak.android.data.User;
import com.droptak.android.interfaces.OnAdminIDUpdateListener;
import com.droptak.android.interfaces.OnAdminRevokedListener;
import com.droptak.android.tasks.AddAdminTask;
import com.droptak.android.widgets.AdminListItemAdapter;

import java.util.List;

/*
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AdministratorFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AdministratorFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class AdminDialog extends DialogFragment
        implements View.OnClickListener, OnAdminIDUpdateListener {

    private ListView lvAdmins;
    private EditText etAdminEmail;
    private MapID id;

    public AdminDialog(MapID id) {
        this.id = id;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Create builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Set title
        builder.setTitle("Map Administrators");

        // Inflate the view
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_administrator, null);
        builder.setView(v);

        // Set positive button
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                getDialog().cancel();
            }
        });

        // Get widgets on screen
        etAdminEmail = (EditText) v.findViewById(R.id.admin_et_adminemail);
        Button addAdmin = (Button) v.findViewById(R.id.admin_bu_addadmin);
        lvAdmins = (ListView) v.findViewById(R.id.admin_listview);

        // Get the list of admins to fill the listview
        final MapTakDB db = MapTakDB.getDB(getActivity());
        MapObject map = db.getMap(id);

        // Set the list adapter
        refresh();

        // Set a listener for the button
        addAdmin.setOnClickListener(this);

        return builder.create();
    }

    private void refresh() {

        Activity a = getActivity();
        if (a != null) {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    // Get the list of maps
                    MapTakDB db = MapTakDB.getDB(getActivity());
                    MapObject m = db.getMap(id);

                    // Create the listener
                    OnAdminRevokedListener listener = new OnAdminRevokedListener() {
                        public void onAdminRevoked(MapID id, User user) {
                            refresh();
                        }
                    };

                    // Create the adapter
                    AdminListItemAdapter adapter = new AdminListItemAdapter(getActivity(), id, m.getManagers(), listener);

                    // Add it to the list view
                    lvAdmins.setAdapter(adapter);
                }
            });
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.admin_bu_addadmin:

                // Get the email the user entered
                String email = etAdminEmail.getText().toString();

                // Execute task with this email
                User u = new User("", "", email);
                AddAdminTask addAdminTask = new AddAdminTask(getActivity(), u, id, this);
                addAdminTask.execute();

                // Clear out the edit text
                etAdminEmail.setText("");

                break;

        }
    }

    @Override
    public void onAdminIDUpdate(User newAdmin, MapID mapID) {

        // Refresh the list view
        Activity a = getActivity();
        if (a != null) {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    refresh();
                }
            });
        }

    }
}
