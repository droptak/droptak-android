package edu.purdue.maptak.admin;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Random;

import edu.purdue.maptak.admin.fragments.TakMapFragment;
import edu.purdue.maptak.admin.qrcode.QRCodeScanner;
import edu.purdue.maptak.admin.data.MapID;
import edu.purdue.maptak.admin.data.MapObject;
import edu.purdue.maptak.admin.data.MapTakDB;
import edu.purdue.maptak.admin.interfaces.OnMapSelectedListener;
import edu.purdue.maptak.admin.test.DummyData;


public class MainActivity extends Activity implements OnMapSelectedListener {

    /** Log tag for debugging logcat output */
    public static final String LOG_TAG = "maptak_log_tag";

    /** Save the menu object so it can be changed dynamically later */
    private Menu menu;

    /** Store the current map the user has displayed as a static variable.
     *  This way, fragments can access it as necessary when adding new taks to the current map. */
    public static MapID currentSelectedMap = null;

    /** Stores the currently inflated fragment. This is used by onCreateOptionsMenu, among
     *  other things, so it knows which options menu to inflate */
    public static MainFragmentState mainFragmentState = null;
    public enum MainFragmentState { MAP, LOGIN, QR, ADDTAK, ADDMAP, TAKLIST, MAPLIST }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(LOG_TAG, "MapActivity.onCreate() called.");
        setContentView(R.layout.activity_main);

        // Inflate the login fragment to the screen
        TakFragmentManager.switchToLogin(this);

        /* TODO: Adding some sample Maps to the database for testing purposes */
        MapTakDB db = new MapTakDB(this);
        Random r = new Random();
        if (r.nextInt(100) <= 50) {
            db.destroy();
            Toast.makeText(this, "DEBUG: Clearing database.", Toast.LENGTH_LONG).show();
        }
        db.addMap(DummyData.createDummyMapObject());
        /* TODO: End testing code */
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;

        int menuRes = -1;
        switch (mainFragmentState) {
            case MAP:
                setUpEnabled(false);
                if (currentSelectedMap == null) {
                    menuRes = R.menu.main_nomapselected;
                    setUpEnabled(false);
                } else {
                    menuRes = R.menu.main_mapselected;
                }
                break;
            case LOGIN:
                setUpEnabled(true);
                return super.onCreateOptionsMenu(menu);
            case QR:
                setUpEnabled(true);
                return super.onCreateOptionsMenu(menu);
            case ADDMAP:
                setUpEnabled(true);
                menuRes = R.menu.main_nomapselected;
                break;
            case ADDTAK:
                setUpEnabled(true);
                menuRes = R.menu.main_nomapselected;
                break;
            case MAPLIST:
                setUpEnabled(true);
                menuRes = R.menu.maplist;
                break;
            case TAKLIST:
                setUpEnabled(true);
                return super.onCreateOptionsMenu(menu);
            default:
                setUpEnabled(false);
                menuRes = R.menu.main_nomapselected;
                break;
        }

        getMenuInflater().inflate(menuRes, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        MapTakDB db = new MapTakDB(this);
        switch (item.getItemId()) {
            case android.R.id.home:
                // Switch to the map view
                if (currentSelectedMap == null) {
                    getFragmentManager().beginTransaction().replace(R.id.activity_map_mapview, new TakMapFragment()).commit();
                    invalidateOptionsMenu();
                } else {
                    TakFragmentManager.switchToMap(this, db.getMap(currentSelectedMap));
                }
                break;

            case R.id.menu_maplist:
                // Switch to map list
                TakFragmentManager.switchToMapList(this, this);
                break;

            case R.id.menu_createmap:
                // Switch to create map view
                TakFragmentManager.switchToCreateMap(this, this);
                break;

            case R.id.menu_addtak:
                // Switch to addtak fragment
                TakFragmentManager.switchToAddTak(this, currentSelectedMap);
                break;

            case R.id.menu_taklist:
                // Switch to tak list
                // TODO: Create a tak selected listener
                TakFragmentManager.switchToTakList(this, currentSelectedMap, null);
                break;

            case R.id.menu_settings:

                break;

            case R.id.CreateQRCode:
                QRCodeScanner qrCodeScanner = new QRCodeScanner(this);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /** Enabled the "up" button on the action bar app icon, which will take the user back to
     *  the map screen. */
    private void setUpEnabled(boolean enabled) {
        ActionBar ab = getActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(enabled);
        }
    }

    /** Overrides the back button. Currently does nothing, but will be used later. */
    public void onBackPressed() {
        super.onBackPressed();
    }

    /** Called when a map is selected in MapListFragment */
    public void onMapSelected(MapID selectedMapID) {
        // Reset the state of the action bar
        //menu.clear();
        //getMenuInflater().inflate(R.menu.main_mapselected, menu);
        //setUpEnabled(false);

        // Set the global currently selected map
        currentSelectedMap = selectedMapID;

        // Re-inflate the main view
        MapTakDB db = new MapTakDB(this);
        MapObject mo = db.getMap(selectedMapID);
        TakFragmentManager.switchToMap(this, mo);

    }

}
