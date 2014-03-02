package edu.purdue.maptak.admin;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.MapFragment;

import java.util.ArrayList;
import java.util.List;

import edu.purdue.maptak.admin.data.MapID;
import edu.purdue.maptak.admin.data.MapObject;
import edu.purdue.maptak.admin.data.MapTakDB;
import edu.purdue.maptak.admin.data.TakObject;
import edu.purdue.maptak.admin.test.DummyData;

public class MainActivity extends Activity implements OnMapSelectedListener {

    /** Log tag for debugging logcat output */
    public static final String LOG_TAG = "maptak";

    /** Save the menu object so it can be changed dynamically later */
    private Menu menu;

    /** Store the current map the user has displayed as a static variable.
     *  This way, fragments can access it as necessary when adding new taks to the current map. */
    private MapID currentSelectedMap = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(LOG_TAG, "MapActivity.onCreate() called.");
        setContentView(R.layout.activity_map);

        // Create a new map fragment for the screen
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.activity_map_mapview, new TakMapFragment());
        ft.commit();

        /* TODO: Adding some sample Maps to the database for testing purposes */

        MapTakDB db = new MapTakDB(this);
        db.addMap(DummyData.createDummyMapObject());

        /* TODO: End testing code */
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                // Re-inflate the map
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.activity_map_mapview, new TakMapFragment())
                        .commit();

                // Change the menu bar back to normal
                menu.clear();
                getMenuInflater().inflate(R.menu.main, menu);

                // Disable the back button
                setUpEnabled(false);
                break;

            case R.id.menu_maplist:

                // Set the main view to a map list fragment
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.activity_map_mapview, new MapListFragment())
                        .commit();

                // Change the menu bar
                menu.clear();
                getMenuInflater().inflate(R.menu.maplist, menu);

                // Enable the back button on the action bar
                setUpEnabled(true);

                break;

            case R.id.menu_createmap:

                // Set the main view to the create map fragment
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.activity_map_mapview, new CreateMapFragment())
                        .commit();

                // Change the menu bar
                menu.clear();

                // Enable the back button on the action bar
                setUpEnabled(true);

                break;

            case R.id.menu_settings:

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
    public void onMapSelected(MapID map) {
        // Re-inflate the map
        setContentView(R.layout.activity_map_addtak);
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_map_mapview, new TakMapFragment())
                .commit();

        // Display the pins and zoom the map to encompass those pins
    }
}
