package com.vfs.fingerprint.base;

import android.os.Bundle;
import androidx.preference.PreferenceFragmentCompat;

import com.vfs.fingerprint.R;


public class SettingsFragment extends PreferenceFragmentCompat{

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }


}
