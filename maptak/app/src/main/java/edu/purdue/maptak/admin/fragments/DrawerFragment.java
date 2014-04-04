package edu.purdue.maptak.admin.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import edu.purdue.maptak.admin.R;
import edu.purdue.maptak.admin.data.MapObject;
import edu.purdue.maptak.admin.data.MapTakDB;
import edu.purdue.maptak.admin.tasks.LoginTask;
import edu.purdue.maptak.admin.widgets.MapListItemAdapter;

public class DrawerFragment extends Fragment {

    /** Whether or not the user is logged in */
    private boolean isLoggedIn = false;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Get whether or not the user is logged in
        String id = getActivity().getPreferences(Context.MODE_PRIVATE).getString(LoginTask.PREF_USER_LOGIN_TOKEN, "");

        View v = null;
        //if (id == "") {
            // User is not logged in
            v = inflater.inflate(R.layout.fragment_drawer_notloggedin, container, false);
        //} else {
            // User is logged in
        //}

        // Get widgets on the screen
        Button buQR = (Button) v.findViewById(R.id.drawer_bu_qrcode);
        ListView lvMaps = (ListView) v.findViewById(R.id.drawer_lv_maplist);

        // Get user's stored maps
        MapTakDB db = MapTakDB.getDB(getActivity());
        List<MapObject> maps = db.getUsersMaps();

        // Set it on the list view
        lvMaps.setAdapter(new MapListItemAdapter(getActivity(), maps));

        return v;
    }
}
