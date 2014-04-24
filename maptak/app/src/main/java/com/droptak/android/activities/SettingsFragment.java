package com.droptak.android.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import com.droptak.android.R;
import com.droptak.android.data.MapTakDB;
import com.droptak.android.fragments.DrawerFragment;
import com.droptak.android.interfaces.OnGPlusLoginListener;
import com.droptak.android.tasks.GPlusLoginTask;
import com.droptak.android.tasks.MapTakLoginTask;

public class SettingsFragment extends PreferenceFragment
        implements Preference.OnPreferenceClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);

        // Set up preferences
        findPreference("pref_account_login").setOnPreferenceClickListener(this);
        findPreference("pref_account_logout").setOnPreferenceClickListener(this);
        findPreference("pref_debug_clearcache").setOnPreferenceClickListener(this);
        findPreference("pref_info_help").setOnPreferenceClickListener(this);
        findPreference("pref_info_aboutus").setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String id = preference.getKey();

        if (id.equals("pref_account_login")) {

            // Re-create a Google Plus login client ane execute it
            new GPlusLoginTask(getActivity(), new OnGPlusLoginListener() {
                public void onGooglePlusLogin() {
                    new MapTakLoginTask(getActivity()).execute();
                }
            });


        } else if (id.equals("pref_account_logout")) {

            // Call logout on the MapTakLogin object
            GPlusLoginTask.logout(getActivity());

        } else if (id.equals("pref_debug_clearcache")) {
            MapTakDB db = MapTakDB.getDB(getActivity());
            db.destroy();

            // Re-create side bar
            getFragmentManager().beginTransaction().replace(R.id.left_drawer, new DrawerFragment()).commit();

        } else if (id.equals("pref_info_help")) {


        } else if (id.equals("pref_info_aboutus")) {

            // Create an about us dialog
            AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
            b.setTitle("About Us");
            b.setPositiveButton("Thanks!", null);
            b.setView(getActivity().getLayoutInflater().inflate(R.layout.fragment_aboutus, null));
            b.create().show();

        }



        return true;
    }

}
