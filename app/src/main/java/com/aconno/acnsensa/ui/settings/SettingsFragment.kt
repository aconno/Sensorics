package com.aconno.acnsensa.ui.settings

import android.os.Build
import android.os.Bundle
import android.preference.PreferenceFragment
import com.aconno.acnsensa.R
import com.aconno.acnsensa.ui.settings.publishers.PublishListActivity

class SettingsFragment : PreferenceFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences)

        findPreference("publishers")
            .setOnPreferenceClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PublishListActivity.start(context)
                } else {
                    PublishListActivity.start(activity)
                }
                true
            }

    }
}