package edu.purdue.maptak.admin.managers;

import android.app.Activity;

import java.util.List;

import edu.purdue.maptak.admin.activities.MainActivity;
import edu.purdue.maptak.admin.R;
import edu.purdue.maptak.admin.data.MapID;
import edu.purdue.maptak.admin.data.MapObject;
import edu.purdue.maptak.admin.fragments.AddTakFragment;
import edu.purdue.maptak.admin.fragments.CreateMapFragment;
import edu.purdue.maptak.admin.fragments.LoginFragment;
import edu.purdue.maptak.admin.fragments.MainMenuFragment;
import edu.purdue.maptak.admin.fragments.MapListFragment;
import edu.purdue.maptak.admin.fragments.QRCodeFragment;
import edu.purdue.maptak.admin.fragments.SearchFragment;
import edu.purdue.maptak.admin.fragments.SearchResultsFragment;
import edu.purdue.maptak.admin.fragments.TakListFragment;
import edu.purdue.maptak.admin.fragments.TakMapFragment;
import edu.purdue.maptak.admin.interfaces.OnTakSelectedListener;

public abstract class TakFragmentManager {

    /** Switches to the main menu fragment */
    public static void switchToMainMenu(Activity activity) {
        activity.getFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_map_mapview, new MainMenuFragment())
                .commit();
        MainActivity.mainFragmentState = MainActivity.MainFragmentState.MAINMENU;
        activity.invalidateOptionsMenu();
    }

    /** Switches the main fragment view to the map given a specific map object */
    public static void switchToMap(Activity activity, MapObject mo) {
        TakMapFragment fragment = null;
        if (mo == null) {
            fragment = new TakMapFragment();
        } else {
            fragment = TakMapFragment.newInstanceOf(mo);
        }
        activity.getFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_map_mapview, fragment)
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
                .replace(R.id.activity_map_mapview, CreateMapFragment.newInstanceOf())
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
    public static void switchToMapList(Activity activity) {
        activity.getFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_map_mapview, MapListFragment.newInstanceOf())
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

    public static void switchToSearch(Activity activity) {
        activity.getFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_map_mapview, new SearchFragment())
                .commit();
        MainActivity.mainFragmentState = MainActivity.MainFragmentState.SEARCH;
        activity.invalidateOptionsMenu();
    }

    public static void switchToQRCode(Activity activity, String string) {
        activity.getFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_map_mapview, QRCodeFragment.newInstance(string))
                .commit();
        MainActivity.mainFragmentState  = MainActivity.MainFragmentState.QR;
        activity.invalidateOptionsMenu();
    }

    public static void switchToSearchResults(Activity activity, List<MapObject> listOfObjects){
        activity.getFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_map_mapview, new SearchResultsFragment(listOfObjects))
                .commit();
        MainActivity.mainFragmentState = MainActivity.MainFragmentState.SEARCH_RESULTS;
        activity.invalidateOptionsMenu();
    }

}
