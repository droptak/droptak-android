package edu.purdue.maptak.admin;

import android.app.FragmentManager;

import edu.purdue.maptak.admin.data.MapID;
import edu.purdue.maptak.admin.data.MapObject;
import edu.purdue.maptak.admin.fragments.AddTakFragment;
import edu.purdue.maptak.admin.fragments.CreateMapFragment;
import edu.purdue.maptak.admin.fragments.LoginFragment;
import edu.purdue.maptak.admin.fragments.MapListFragment;
import edu.purdue.maptak.admin.fragments.TakListFragment;
import edu.purdue.maptak.admin.fragments.TakMapFragment;
import edu.purdue.maptak.admin.interfaces.OnMapSelectedListener;
import edu.purdue.maptak.admin.interfaces.OnTakSelectedListener;

public abstract class TakFragmentManager {

    /** Switches the main fragment view to the map given a specific map object */
    public static void switchToMap(FragmentManager manager, MapObject mo) {
        manager
                .beginTransaction()
                .replace(R.id.activity_map_mapview, TakMapFragment.newInstanceOf(mo))
                .commit();
    }

    /** Switches the main fragment view to the add tak view given a map ID which
     *  should be the parent of all taks created in this fragment */
    public static void switchToAddTak(FragmentManager manager, MapID id) {
        manager
                .beginTransaction()
                .replace(R.id.activity_map_mapview, AddTakFragment.newInstanceOf(id))
                .commit();
    }

    /** Switches the main fragment view to the create map fragment */
    public static void switchToCreateMap(FragmentManager manager) {
        manager
                .beginTransaction()
                .replace(R.id.activity_map_mapview, new CreateMapFragment())
                .commit();
    }

    /** Switches the main fragment view to the login fragment */
    public static void switchToLogin(FragmentManager manager) {
        manager
                .beginTransaction()
                .replace(R.id.activity_map_mapview, new LoginFragment())
                .commit();
    }

    /** Switches the main fragment view to the map list fragment, given an OnMapSelected listener */
    public static void switchToMapList(FragmentManager manager, OnMapSelectedListener listener) {
        manager
                .beginTransaction()
                .replace(R.id.activity_map_mapview, MapListFragment.newInstanceOf(listener))
                .commit();
    }

    /** Switches the main fragment view to the Tak List fragment, given an OnTakSelected listener
     *  and a mapID for which all the taks should belong */
    public static void switchToTakList(FragmentManager manager, MapID id, OnTakSelectedListener listener) {
        manager
                .beginTransaction()
                .replace(R.id.activity_map_mapview, TakListFragment.newInstanceOf(id, listener))
                .commit();
    }


}
