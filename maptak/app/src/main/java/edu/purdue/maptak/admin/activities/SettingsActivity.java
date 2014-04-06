package edu.purdue.maptak.admin.activities;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

import edu.purdue.maptak.admin.R;
import edu.purdue.maptak.admin.data.MapTakDB;

/**
 * Created by mike on 4/6/14.
 */
public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        } else if (id.equals("pref_account_logout")) {

        } else if (id.equals("pref_debug_clearcache")) {
            MapTakDB db = MapTakDB.getDB(this);
            db.destroy();
        }

        return true;
    }

}
