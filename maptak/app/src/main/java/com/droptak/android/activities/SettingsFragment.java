package com.droptak.android.activities;

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
        }

        return true;
    }

}
