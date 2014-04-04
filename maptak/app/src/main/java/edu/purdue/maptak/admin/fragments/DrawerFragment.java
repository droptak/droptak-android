package edu.purdue.maptak.admin.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import edu.purdue.maptak.admin.R;
import edu.purdue.maptak.admin.activities.MainActivity;
import edu.purdue.maptak.admin.data.MapObject;
import edu.purdue.maptak.admin.data.MapTakDB;
import edu.purdue.maptak.admin.tasks.LoginTask;
import edu.purdue.maptak.admin.widgets.MapListItemAdapter;

public class DrawerFragment extends Fragment implements View.OnClickListener {

    /** Whether or not the user is logged in */
    private boolean isLoggedIn = false;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Get whether or not the user is logged in
        String id = getActivity().getPreferences(Context.MODE_PRIVATE).getString(MainActivity.PREF_USER_LOGIN_TOKEN, "");

        View v = null;
        v = inflater.inflate(R.layout.fragment_drawer, container, false);

        // Get widgets on the screen
        Button buCreateMap = (Button) v.findViewById(R.id.drawer_bu_createmap);
        buCreateMap.setOnClickListener(this);
        ListView lvMaps = (ListView) v.findViewById(R.id.drawer_lv_maplist);

        // Get user's stored maps
        MapTakDB db = MapTakDB.getDB(getActivity());
        List<MapObject> maps = db.getUsersMaps();

        // Set it on the list view
        lvMaps.setAdapter(new MapListItemAdapter(getActivity(), maps));

        return v;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.drawer_bu_createmap:

                Toast.makeText(getActivity(), "asdf", Toast.LENGTH_SHORT).show();
                String[] items = new String[]{ "Create a new map", "Scan a QR Code", "Search"};

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Add a Map")
                    .setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                                // The 'which' argument contains the index position
                                // of the selected item
                            }
                        });

                builder.create();
                break;
        }

    }
}
