package com.droptak.android.fragments;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

import com.droptak.android.R;
import com.droptak.android.activities.MainActivity;
import com.droptak.android.data.MapObject;
import com.droptak.android.data.MapTakDB;
import com.droptak.android.data.MapID;
import com.droptak.android.fragments.dialogs.AddMapSelectionDialog;
import com.droptak.android.interfaces.OnMapIDUpdateListener;
import com.droptak.android.widgets.MapListItemAdapter;

public class DrawerFragment extends Fragment
        implements View.OnClickListener, AdapterView.OnItemClickListener, OnMapIDUpdateListener {

    /** Whether or not the user is logged in */
    private boolean isLoggedIn = false;

    /** Drawer Layout */
    private DrawerLayout drawer;

    /** THe backing list of maps we are displaying in the drawer list view */
    private List<MapObject> maps;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_drawer, container, false);

        // Get widgets on the screen
        drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        Button buCreateMap = (Button) v.findViewById(R.id.drawer_bu_createmap);
        buCreateMap.setOnClickListener(this);
        ListView lvMaps = (ListView) v.findViewById(R.id.drawer_lv_maplist);
        lvMaps.setOnItemClickListener(this);

        // Get user's stored maps
        MapTakDB db = MapTakDB.getDB(getActivity());
        maps = db.getUsersMaps();

        // Set it on the list view
        lvMaps.setAdapter(new MapListItemAdapter(getActivity(), maps));

        return v;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.drawer_bu_createmap:
                drawer.closeDrawers();
                new AddMapSelectionDialog().show(getFragmentManager(), "add_map_select_dialog");
                break;
        }

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        drawer.closeDrawers();
        MapObject selected = maps.get(i);

        // Set the map as the currently selected map
        SharedPreferences prefs = getActivity().getSharedPreferences(MainActivity.SHARED_PREFS_NAME, 0);
        prefs.edit().putString(MainActivity.PREF_CURRENT_MAP, selected.getID().toString()).commit();

        // Inflate a new tak map fragment
        getFragmentManager().beginTransaction().replace(R.id.mainview, new MapViewFragment()).commit();

    }

    @Override
    public void onMapIDUpdate(MapID oldID, MapID newID) {
        for (MapObject m : maps) {
            if (m.getID().equals(oldID)) {
                m.setID(newID);
            }
        }
    }

}
