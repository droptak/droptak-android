package edu.purdue.maptak.admin.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import edu.purdue.maptak.admin.R;
import edu.purdue.maptak.admin.data.MapID;
import edu.purdue.maptak.admin.data.MapObject;
import edu.purdue.maptak.admin.data.MapTakDB;
import edu.purdue.maptak.admin.fragments.CreateMapFragment;
import edu.purdue.maptak.admin.fragments.DrawerFragment;
import edu.purdue.maptak.admin.fragments.LoginFragment;
import edu.purdue.maptak.admin.managers.TakFragmentManager;
import edu.purdue.maptak.admin.qrcode.IntentIntegrator;
import edu.purdue.maptak.admin.qrcode.IntentResult;


public class MainActivity extends Activity {

    /** Log tag for debugging logcat output */
    public static final String LOG_TAG = "maptak_log_tag";

    /** Name of shared preferences where we store everything */
    public static final String SHARED_PREFS_NAME = "edu.purdue.maptak-sharedprefs";

    /** Strings for various keys in the preferences */
    public static final String PREF_CURRENT_MAP = "current_selected_map_id";
    public static final String PREF_USER_EMAIL = "user_email";
    public static final String PREF_USER_NAME = "user_name";
    public static final String PREF_USER_LOGIN_TOKEN = "user_login_token";
    public static final String PREF_IS_LOGGED_IN = "user_is_logged_in";

    /** Stores the currently inflated fragment. This is used by onCreateOptionsMenu, among
     *  other things, so it knows which options menu to inflate */
    public static MainFragmentState mainFragmentState;
    TextView url = null;
    public enum MainFragmentState { MAINMENU, MAP, LOGIN, QR, ADDTAK, ADDMAP, TAKLIST, MAPLIST, SEARCH }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(LOG_TAG, "MapActivity.onCreate() called.");
        setContentView(R.layout.activity_main);

        // Inflate the login fragment and the sidebar to the screen
        getFragmentManager().beginTransaction().replace(R.id.mainview, new LoginFragment()).commit();
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
    protected void onStop() {
        super.onStop();
        LoginFragment.mGoogleApiClient.disconnect();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        MapTakDB db = MapTakDB.getDB(this);

        switch (item.getItemId()) {
            case android.R.id.home:

                // Our view switches depending on where we're at currently
                switch (mainFragmentState) {
                    case MAPLIST: case LOGIN: case QR:
                        TakFragmentManager.switchToMainMenu(this);
                        break;
                    case MAP: case ADDMAP:
                        TakFragmentManager.switchToMapList(this);
                        break;
                    case ADDTAK: case TAKLIST:
                        TakFragmentManager.switchToMap(this, getCurrentSelectedMap());
                        break;
                }

                break;

            case R.id.menu_createmap:
                // Switch to create map view
                TakFragmentManager.switchToCreateMap(this);
                break;

            case R.id.menu_addtak:
                // Switch to addtak fragment
                TakFragmentManager.switchToAddTak(this, getCurrentSelectedMap().getID());
                break;

            case R.id.menu_taklist:
                // Switch to tak list
                // TODO: Create a tak selected listener
                TakFragmentManager.switchToTakList(this, getCurrentSelectedMap().getID(), null);
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
    public void onBackPressed() {
        switch (mainFragmentState) {
            case MAINMENU:
                super.onBackPressed();
                break;
            case MAPLIST: case QR: case LOGIN:
                TakFragmentManager.switchToMainMenu(this);
                break;
            case MAP: case ADDMAP:
                TakFragmentManager.switchToMapList(this);
                break;
            case ADDTAK: case TAKLIST:
                TakFragmentManager.switchToMap(this, getCurrentSelectedMap());
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        url = (TextView) findViewById(R.id.QRCodeTitle);
        if ( scanResult != null ){
            FragmentManager fm = getFragmentManager();
            //Fragment newFrame = QRCodeFragment.newInstance(scanResult.getContents());
            //fm.beginTransaction().replace(R.id.activity_map_mapview, newFrame).commit();
            TakFragmentManager.switchToQRCode(this, scanResult.getContents());
        } else {
            Log.d(MainActivity.LOG_TAG, "There was an error");
        }
    }


}
