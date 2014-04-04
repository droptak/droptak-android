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

import java.util.List;
import java.util.concurrent.ExecutionException;

import edu.purdue.maptak.admin.R;
import edu.purdue.maptak.admin.data.MapID;
import edu.purdue.maptak.admin.data.MapObject;
import edu.purdue.maptak.admin.data.MapTakDB;
import edu.purdue.maptak.admin.data.TakObject;
import edu.purdue.maptak.admin.managers.TakFragmentManager;
import edu.purdue.maptak.admin.qrcode.IntentIntegrator;
import edu.purdue.maptak.admin.qrcode.IntentResult;
import edu.purdue.maptak.admin.tasks.GetQRMapTask;
import edu.purdue.maptak.admin.tasks.GetQRTaksTask;


public class MainActivity extends Activity {

    /** Log tag for debugging logcat output */
    public static final String LOG_TAG = "maptak_log_tag";

    /** Strings for various keys in the preferences */
    public static final String PREF_CURRENT_MAP = "current_selected_map_id";

    /** Stores the currently inflated fragment. This is used by onCreateOptionsMenu, among
     *  other things, so it knows which options menu to inflate */
    public static MainFragmentState mainFragmentState = null;
    TextView url = null;
    public enum MainFragmentState { MAINMENU, MAP, LOGIN, QR, ADDTAK, ADDMAP, TAKLIST, MAPLIST, SEARCH, SEARCH_RESULTS }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(LOG_TAG, "MapActivity.onCreate() called.");
        setContentView(R.layout.activity_main);

        // Inflate the login fragment to the screen
        TakFragmentManager.switchToMainMenu(this);
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        int menuRes = -1;
        switch (mainFragmentState) {
            case MAINMENU:
                setUpEnabled(false);
                menuRes = R.menu.justsettings;
                break;
            case MAP:
                setUpEnabled(true);
                menuRes = R.menu.mapselected;
                break;
            case QR:
                setUpEnabled(true);
                menuRes = R.menu.justsettings;
                break;
            case ADDTAK:
                setUpEnabled(true);
                menuRes = R.menu.justsettings;
                break;
            case ADDMAP:
                setUpEnabled(true);
                menuRes = R.menu.justsettings;
                break;
            case TAKLIST:
                setUpEnabled(true);
                menuRes = R.menu.justsettings;
                break;
            case MAPLIST:
                setUpEnabled(true);
                menuRes = R.menu.maplist;
                break;
            case SEARCH:
                setUpEnabled(true);
                menuRes = R.menu.justsettings;
                break;
            case SEARCH_RESULTS:
                setUpEnabled(true);
                menuRes = R.menu.justsettings;
                break;
            default:
                setUpEnabled(false);
                menuRes = R.menu.justsettings;
                break;
        }

        getMenuInflater().inflate(menuRes, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        MapTakDB db = MapTakDB.getDB(this);

        switch (item.getItemId()) {
            case android.R.id.home:

                // Our view switches depending on where we're at currently
                switch (mainFragmentState) {
                    case MAPLIST: case LOGIN: case QR: case SEARCH:
                        TakFragmentManager.switchToMainMenu(this);
                        break;
                    case MAP: case ADDMAP:
                        TakFragmentManager.switchToMapList(this);
                        break;
                    case ADDTAK: case TAKLIST:
                        TakFragmentManager.switchToMap(this, getCurrentSelectedMap());
                        break;
                    case SEARCH_RESULTS:
                        TakFragmentManager.switchToSearch(this);
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
        String id = getPreferences(MODE_PRIVATE).getString(PREF_CURRENT_MAP, "");
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
            case MAPLIST: case QR: case LOGIN: case SEARCH: case SEARCH_RESULTS:
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
            TakFragmentManager.switchToQRCode(this, scanResult.getContents());
        } else {
            Log.d(MainActivity.LOG_TAG,"There was an error");
        }
    }


}
