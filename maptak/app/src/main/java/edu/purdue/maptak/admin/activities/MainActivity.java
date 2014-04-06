package edu.purdue.maptak.admin.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.ExecutionException;

import edu.purdue.maptak.admin.R;
import edu.purdue.maptak.admin.data.MapID;
import edu.purdue.maptak.admin.data.MapObject;
import edu.purdue.maptak.admin.data.MapTakDB;
import edu.purdue.maptak.admin.fragments.DrawerFragment;
import edu.purdue.maptak.admin.interfaces.OnGPlusLoginListener;
import edu.purdue.maptak.admin.qrcode.IntentIntegrator;
import edu.purdue.maptak.admin.qrcode.IntentResult;
import edu.purdue.maptak.admin.tasks.GPlusLoginTask;
import edu.purdue.maptak.admin.tasks.MapTakLoginTask;
import edu.purdue.maptak.admin.data.TakObject;
import edu.purdue.maptak.admin.tasks.GetQRMapTask;
import edu.purdue.maptak.admin.tasks.GetQRTaksTask;

public class MainActivity extends Activity {

    /** Log tag for debugging logcat output */
    public static final String LOG_TAG = "maptak_log_tag";

    /** Name of shared preferences where we store everything */
    public static final String SHARED_PREFS_NAME = "edu.purdue.maptak-sharedprefs";

    /** Strings for various keys in the preferences */
    public static final String PREF_CURRENT_MAP = "current_selected_map_id";
    public static final String PREF_USER_GPLUS_EMAIL = "user_email";
    public static final String PREF_USER_GPLUS_NAME = "user_name";
    public static final String PREF_USER_GPLUS_ID = "google_plus_id";
    public static final String PREF_USER_GPLUS_TOKEN = "google_oauth_token";
    public static final String PREF_USER_GPLUS_ISLOGGEDIN = "user_is_logged_in";
    public static final String PREF_USER_MAPTAK_TOKEN = "maptak_token";

    /** Class variables related to the drawer */
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set main content view and log
        Log.d(LOG_TAG, "MapActivity.onCreate() called.");
        setContentView(R.layout.activity_main);

        // Attempt to sign the user into google plus and maptak
        new GPlusLoginTask(this, new OnGPlusLoginListener() {
            public void onGooglePlusLogin() {
                new MapTakLoginTask(MainActivity.this).execute();
            }
        });

        // Inflate the sidebar and main screen fragments
        getFragmentManager().beginTransaction().replace(R.id.left_drawer, new DrawerFragment()).commit();

        // Make the drawer layout openable with the app icon
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer_toggle, R.string.drawer_text_open, R.string.drawer_text_closed);
        drawerLayout.setDrawerListener(drawerToggle);

        // Configure action bar
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Re-create the drawer
        getFragmentManager().beginTransaction().replace(R.id.left_drawer, new DrawerFragment()).commit();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        int menuRes = -1;
        setUpEnabled(false);
        menuRes = R.menu.justsettings;
        getMenuInflater().inflate(menuRes, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.menu_settings:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                return true;
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

    /** Returns the currently selected map. */
    private MapObject getCurrentSelectedMap() {
        MapTakDB db = MapTakDB.getDB(this);
        String id = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE).getString(PREF_CURRENT_MAP, "");
        if (id != "") {
            return db.getMap(new MapID(id));
        }
        return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        TextView url = (TextView) findViewById(R.id.QRCodeTitle);
        if ( scanResult != null ){
            FragmentManager fm = getFragmentManager();
            //Fragment newFrame = QRCodeFragment.newInstance(scanResult.getContents());
            //fm.beginTransaction().replace(R.id.activity_map_mapview, newFrame).commit();
            MapObject map = null;
            GetQRMapTask getQRMapTask = new GetQRMapTask(scanResult.getContents(),this);
            try {
                map = getQRMapTask.execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            Log.d("debug","mapName="+map.getLabel());
            GetQRTaksTask task = new GetQRTaksTask(this,map.getLabel(),Long.parseLong(map.getID().toString()));
            List<TakObject> taks = null;
            try {
                taks = task.execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            Log.d("deubg","call");
            map.setTaskList(taks);
            MapTakDB db = MapTakDB.getDB(this);
            db.addMap(map);
            //TakFragmentManager.switchToQRCode(this, scanResult.getContents());
        } else {
            Log.d(MainActivity.LOG_TAG, "There was an error");
        }
    }


}
