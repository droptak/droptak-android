package edu.purdue.maptak.admin.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

import edu.purdue.maptak.admin.R;
import edu.purdue.maptak.admin.data.MapObject;
import edu.purdue.maptak.admin.data.MapTakDB;
import edu.purdue.maptak.admin.fragments.dialogs.AddMapSelectionDialog;
import edu.purdue.maptak.admin.widgets.MapListItemAdapter;

public class DrawerFragment extends Fragment implements View.OnClickListener {

    /** Whether or not the user is logged in */
    private boolean isLoggedIn = false;

    /** Drawer Layout */
    private DrawerLayout drawer;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_drawer, container, false);

        // Get widgets on the screen
        drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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

}
