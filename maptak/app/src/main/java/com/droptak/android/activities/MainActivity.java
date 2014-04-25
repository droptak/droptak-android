package com.droptak.android.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.droptak.android.R;
import com.droptak.android.data.MapID;
import com.droptak.android.data.MapObject;
import com.droptak.android.data.MapTakDB;
import com.droptak.android.fragments.DrawerFragment;
import com.droptak.android.fragments.SplashFragment;
import com.droptak.android.interfaces.OnGPlusLoginListener;
import com.droptak.android.interfaces.OnMapsRefreshListener;
import com.droptak.android.qrcode.IntentIntegrator;
import com.droptak.android.qrcode.IntentResult;
import com.droptak.android.tasks.GPlusLoginTask;
import com.droptak.android.tasks.GetMapTask;
import com.droptak.android.tasks.GetUsersMapsTask;
import com.droptak.android.tasks.MapTakLoginTask;
import com.droptak.android.test.DBTests;

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

    /** Currently inflated fragment */
    private boolean isInPrefs = false;

    /** Object which handles G+ login */
    private GPlusLoginTask gplusLogin;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set main content view
        setContentView(R.layout.activity_main);

        // Check if the user has internet. If not, exit the app.
        boolean isConnected = isInternetConnected();
        if (!isConnected) {
            presentConnectivityErrorDialog();
        } else {
            // Attempt to sign the user into google plus and maptak
            gplusLogin = new GPlusLoginTask(this, new OnGPlusLoginListener() {
                public void onGooglePlusLogin() {
                    new MapTakLoginTask(MainActivity.this).execute();
                }
            });
        }

        // Set the background to a green color.
        getWindow().getDecorView().setBackgroundResource(R.drawable.splash);

        // Inflate the splash screen
        getFragmentManager().beginTransaction().replace(R.id.mainview, new SplashFragment()).commit();

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
        menuRes = R.menu.main_menu;
        getMenuInflater().inflate(menuRes, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handler for the action bar icon to toggle the drawer
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // Handler for every other icon
        switch (item.getItemId()) {

            case R.id.menu_refresh:
                GetUsersMapsTask task = new GetUsersMapsTask(this, new OnMapsRefreshListener() {
                    public void onMapsRefresh() {
                        getFragmentManager().beginTransaction().replace(R.id.left_drawer, new DrawerFragment()).commit();
                    }
                });
                task.execute();
                Toast.makeText(this, "Polling DropTak for maps.", Toast.LENGTH_SHORT).show();
                break;

            case R.id.menu_settings:

                // Clear out background just to be safe
                getWindow().getDecorView().setBackgroundColor(Color.WHITE);

                // Create preference fragment
                isInPrefs = true;
                getFragmentManager().beginTransaction().replace(R.id.mainview, new SettingsFragment()).commit();
                break;

        }

        return true;
    }

    @Override
    public void onBackPressed() {
        if (isInPrefs) {
            isInPrefs = false;
            getFragmentManager().beginTransaction().replace(R.id.mainview, new SplashFragment()).commit();
            getWindow().getDecorView().setBackgroundResource(R.drawable.splash);
        } else {
            super.onBackPressed();
        }
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

        switch (requestCode) {

            // Case for G+ signin failure
            case GPlusLoginTask.RC_SIGN_IN:
                gplusLogin.connect();
                break;

            // Case for QR code scanning return
            case IntentIntegrator.REQUEST_CODE:
                IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                TextView url = (TextView) findViewById(R.id.QRCodeTitle);
                if ( scanResult != null ){

                    // Parse out the ID
                    String idurl = scanResult.getContents();
                    if (idurl == null) {
                        return;
                    }

                    idurl = idurl.replace("http://mapitapps.appspot.com/maps/", "");
                    idurl = idurl.replace("/", "");

                    // Execute the get map task
                    GetMapTask getMapTask = new GetMapTask(this, new MapID(idurl), new OnMapsRefreshListener() {
                        public void onMapsRefresh() {
                            // Refresh drawer layout
                            getFragmentManager().beginTransaction().replace(R.id.left_drawer, new DrawerFragment()).commit();
                        }
                    });
                    getMapTask.execute();

                }
                break;

        }

    }

    /** Creates an error dialog which, when dismissed in any way, exits the application. It tells the user that
     *  they need internet in order for the app to function. */
    private void presentConnectivityErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage("DropTak requires an active internet connection to function. It appears you're off the grid.");
        builder.setPositiveButton("Sorry :(", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            public void onDismiss(DialogInterface dialog) {
                finish();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        builder.create().show();
    }

    private boolean isInternetConnected() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}

