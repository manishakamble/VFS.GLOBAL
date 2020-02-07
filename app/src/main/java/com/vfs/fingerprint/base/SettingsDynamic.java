package com.vfs.fingerprint.base;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreferenceCompat;
import com.vfs.fingerprint.R;
import com.vfs.fingerprint.Util.AbstractPreferenceActivity;

public class SettingsDynamic extends AbstractPreferenceActivity implements Preference.OnPreferenceClickListener {
    public static final String KEY_PREF_PASSPORT_SWITCH = "passport_switch";
    public static final String KEY_PREF_FACE_SWITCH = "face_switch";
    public static final String KEY_PREF_DOCUMENT_SWITCH = "document_switch";
    public static final String KEY_PREF_SIGNATURE_SWITCH = "signature_switch";
    public static final String KEY_PREF_NIST_MENU_SWITCH = "nist_menu_switch";
    public static final String KEY_PREF_XML_MENU_SWITCH = "xml_menu_switch";
    public static final String KEY_PREF_NIST_PASSPORT_SWITCH = "nist_passport_switch";
    public static final String KEY_PREF_NIST_FACE_SWITCH = "nist_face_switch";
    // public static final String KEY_PREF_NIST_FINGERPRINT_SWITCH = "nist_fingerprint_switch";
    public static final String KEY_PREF_NIST_SIGNATURE_SWITCH = "nist_signature_switch";
    public static final String KEY_PREF_XML_PASSPORT_SWITCH = "xml_passport_switch";
    public static final String KEY_PREF_XML_FACE_SWITCH = "xml_face_switch";
    // public static final String KEY_PREF_XML_FINGERPRINT_SWITCH = "xml_fingerprint_switch";
    public static final String KEY_PREF_XML_SIGNATURE_SWITCH = "xml_signature_switch";
    Boolean nist_menu_pref;
    Boolean xml_menu_pref;
    PreferenceScreen preferenceScreen;
    PreferenceCategory nistCategory;
    PreferenceCategory xmlCategory;
    CheckBoxPreference nist_chekbox;
    CheckBoxPreference xml_chekbox;
    CheckBoxPreference nist_face_chekbox;
    CheckBoxPreference nist_signature_chekbox;
    CheckBoxPreference nist_passport_chekbox;
    CheckBoxPreference xml_face_chekbox;
    CheckBoxPreference xml_signature_chekbox;
    CheckBoxPreference xml_passport_chekbox;
    SwitchPreferenceCompat passport_switch;
    SwitchPreferenceCompat face_switch;
    SwitchPreferenceCompat signature_switch;

    @Override
    protected void onCreateNavigation(@NonNull PreferenceFragmentCompat fragment) {
        fragment.addPreferencesFromResource(R.xml.navigation);
        SharedPreferences sharedPref = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);
        nist_menu_pref = sharedPref.getBoolean(KEY_PREF_NIST_MENU_SWITCH, false);
        xml_menu_pref = sharedPref.getBoolean(KEY_PREF_XML_MENU_SWITCH, false);
        preferenceScreen = (PreferenceScreen) fragment.findPreference(getResources().getString(R.string.setting_preference));
        nistCategory = (PreferenceCategory) fragment.findPreference(getResources().getString(R.string.nist_key_cat));
        xmlCategory = (PreferenceCategory) fragment.findPreference(getResources().getString(R.string.xml_key_cat));
        nist_chekbox = (CheckBoxPreference) fragment.findPreference(getResources().getString(R.string.nist_menu_switch));
        xml_chekbox = (CheckBoxPreference) fragment.findPreference(getResources().getString(R.string.xml_menu_switch));
        passport_switch = (SwitchPreferenceCompat) fragment.findPreference(getResources().getString(R.string.passport_switch));
        face_switch = (SwitchPreferenceCompat) fragment.findPreference(getResources().getString(R.string.face_switch));
        signature_switch = (SwitchPreferenceCompat) fragment.findPreference(getResources().getString(R.string.signature_switch));
        nist_face_chekbox = (CheckBoxPreference) fragment.findPreference(getResources().getString(R.string.nist_face_switch));
        nist_signature_chekbox = (CheckBoxPreference) fragment.findPreference(getResources().getString(R.string.nist_signature_switch));
        nist_passport_chekbox = (CheckBoxPreference) fragment.findPreference(getResources().getString(R.string.nist_passport_switch));
        xml_face_chekbox = (CheckBoxPreference) fragment.findPreference(getResources().getString(R.string.xml_face_switch));
        xml_signature_chekbox = (CheckBoxPreference) fragment.findPreference(getResources().getString(R.string.xml_signature_switch));
        xml_passport_chekbox = (CheckBoxPreference) fragment.findPreference(getResources().getString(R.string.xml_passport_switch));
        nist_chekbox.setOnPreferenceClickListener(this);
        xml_chekbox.setOnPreferenceClickListener(this);
        nist_face_chekbox.setOnPreferenceClickListener(this);
        nist_signature_chekbox.setOnPreferenceClickListener(this);
        nist_passport_chekbox.setOnPreferenceClickListener(this);
        xml_face_chekbox.setOnPreferenceClickListener(this);
        xml_signature_chekbox.setOnPreferenceClickListener(this);
        xml_passport_chekbox.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference.getKey().equalsIgnoreCase(KEY_PREF_NIST_MENU_SWITCH)) {
            if (nist_chekbox.isChecked()) {
                nistCategory.setEnabled(true);
                preferenceScreen.addPreference(nistCategory);
            } else {
                nistCategory.setEnabled(false);
                preferenceScreen.removePreference(nistCategory);
            }
        } else if (preference.getKey().equalsIgnoreCase(KEY_PREF_XML_MENU_SWITCH)) {
            if (xml_chekbox.isChecked()) {
                xmlCategory.setEnabled(true);
                preferenceScreen.addPreference(xmlCategory);
            } else {
                xmlCategory.setEnabled(false);
                preferenceScreen.removePreference(xmlCategory);
            }
        } else if (preference.getKey().equalsIgnoreCase(KEY_PREF_NIST_PASSPORT_SWITCH)) {
            if (nist_passport_chekbox.isChecked()) {
                passport_switch.setChecked(true);
            }
        } else if (preference.getKey().equalsIgnoreCase(KEY_PREF_NIST_FACE_SWITCH)) {
            if (nist_face_chekbox.isChecked()) {
                face_switch.setChecked(true);
            }
        } else if (preference.getKey().equalsIgnoreCase(KEY_PREF_NIST_SIGNATURE_SWITCH)) {
            if (nist_signature_chekbox.isChecked()) {
                signature_switch.setChecked(true);
            }
        } else if (preference.getKey().equalsIgnoreCase(KEY_PREF_XML_PASSPORT_SWITCH)) {
            if (xml_passport_chekbox.isChecked()) {
                passport_switch.setChecked(true);
            }
        } else if (preference.getKey().equalsIgnoreCase(KEY_PREF_XML_FACE_SWITCH)) {
            if (xml_face_chekbox.isChecked()) {
                face_switch.setChecked(true);
            }
        } else if (preference.getKey().equalsIgnoreCase(KEY_PREF_XML_SIGNATURE_SWITCH)) {
            if (xml_signature_chekbox.isChecked()) {
                signature_switch.setChecked(true);
            }
        }
        return false;
    }
}

