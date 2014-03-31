package edu.purdue.maptak.admin;

import android.app.Activity;
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
    public static void switchToMap(Activity activity, MapObject mo) {
        activity.getFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_map_mapview, TakMapFragment.newInstanceOf(mo))
                .commit();
        MainActivity.mainFragmentState = MainActivity.MainFragmentState.MAP;
        activity.invalidateOptionsMenu();
    }

    /** Switches the main fragment view to the add tak view given a map ID which
     *  should be the parent of all taks created in this fragment */
    public static void switchToAddTak(Activity activity, MapID id) {
        activity.getFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_map_mapview, AddTakFragment.newInstanceOf(id))
                .commit();
        MainActivity.mainFragmentState = MainActivity.MainFragmentState.ADDTAK;
        activity.invalidateOptionsMenu();
    }

    /** Switches the main fragment view to the create map fragment */
    public static void switchToCreateMap(Activity activity) {
        activity.getFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_map_mapview, new CreateMapFragment())
                .commit();
        MainActivity.mainFragmentState = MainActivity.MainFragmentState.ADDMAP;
        activity.invalidateOptionsMenu();
    }

    /** Switches the main fragment view to the login fragment */
    public static void switchToLogin(Activity activity) {
        activity.getFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_map_mapview, new LoginFragment())
                .commit();
        MainActivity.mainFragmentState = MainActivity.MainFragmentState.LOGIN;
        activity.invalidateOptionsMenu();
    }

    /** Switches the main fragment view to the map list fragment, given an OnMapSelected listener */
    public static void switchToMapList(Activity activity, OnMapSelectedListener listener) {
        activity.getFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_map_mapview, MapListFragment.newInstanceOf(listener))
                .commit();
        MainActivity.mainFragmentState = MainActivity.MainFragmentState.MAPLIST;
        activity.invalidateOptionsMenu();
    }

    /** Switches the main fragment view to the Tak List fragment, given an OnTakSelected listener
     *  and a mapID for which all the taks should belong */
    public static void switchToTakList(Activity activity, MapID id, OnTakSelectedListener listener) {
        activity.getFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_map_mapview, TakListFragment.newInstanceOf(id, listener))
                .commit();
        MainActivity.mainFragmentState = MainActivity.MainFragmentState.TAKLIST;
        activity.invalidateOptionsMenu();
    }


}
