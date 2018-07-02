package com.aconno.acnsensa.ui.settings

import android.os.Build
import android.os.Bundle
import android.preference.ListPreference
import android.preference.PreferenceFragment
import com.aconno.acnsensa.R
import com.aconno.acnsensa.ui.settings.publishers.PublishListActivity

class SettingsFragment : PreferenceFragment() {

    private lateinit var listPreference: ListPreference

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

        listPreference = findPreference("scan_mode") as ListPreference
        listPreference.setOnPreferenceChangeListener { _, newValue ->
            setScanModeSummarize(newValue as String)
            true
        }

        initSummaries()
    }

    private fun initSummaries() {
        val scanMode = preferenceManager
            .sharedPreferences.getString("scan_mode", "3")
        setScanModeSummarize(scanMode)
    }

    private fun setScanModeSummarize(scanMode: String) {
        when (scanMode.toInt()) {
            1 -> listPreference.summary = "Low Power"
            2 -> listPreference.summary = "Balanced"
            3 -> listPreference.summary = "Low Latency"
        }
    }
}