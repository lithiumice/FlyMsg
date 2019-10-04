package online.hualin.flymsg.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import androidx.appcompat.widget.Toolbar;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.google.android.material.snackbar.Snackbar;

import online.hualin.flymsg.App;
import online.hualin.flymsg.R;

public class SettingFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {
    private final int REQUEST_CODE_ALERT_RINGTONE = 1;
    private final String KEY_RINGTONE_PREFERENCE = "";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.root_preferences);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findPreference("switch_notify").setOnPreferenceChangeListener(this);
        findPreference("AutoReceive").setOnPreferenceChangeListener(this);
        findPreference("download_pref_list").setOnPreferenceChangeListener(this);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(App.getContext());
        onPreferenceChange(findPreference("switch_notify"), preferences.getBoolean("switch_notify", true));
        onPreferenceChange(findPreference("AutoReceive"), preferences.getBoolean("AutoReceive", true));
        onPreferenceChange(findPreference("download_pref_list"), preferences.getString("download_pref_list", "/mnt/sdcard/Download"));
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference.getKey() != null) {
            switch (preference.getKey()) {
                case "switch_notify":
                    Snackbar.make(getListView(), "switch notify", Snackbar.LENGTH_SHORT).show();
                    break;
                case "AutoReceive":
                    Snackbar.make(getListView(), "switch auto receive", Snackbar.LENGTH_SHORT).show();
                    break;
            }
            return true;
        }
        return super.onPreferenceTreeClick(preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String stringValue = newValue.toString();
        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(stringValue);
            preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);
        } else {
            preference.setSummary(stringValue);
        }
        return true;
    }
}