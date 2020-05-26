package com.example.cocktailapp.ui.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.example.cocktailapp.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        /* Add preferences, defined in the XML file */
        setPreferencesFromResource(R.xml.preference, rootKey)
    }

}

