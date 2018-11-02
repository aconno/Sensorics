package com.aconno.sensorics.ui.settings

import android.os.Build
import android.os.Bundle
import android.preference.ListPreference
import android.preference.Preference
import android.preference.PreferenceFragment
import com.aconno.sensorics.R
import com.aconno.sensorics.ui.settings.publishers.PublishListActivity

class SettingsFragment : PreferenceFragment(), Preference.OnPreferenceChangeListener {

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

        initSummaries()
    }

    override fun onResume() {
        super.onResume()
        listPreference.onPreferenceChangeListener = this
    }

    override fun onPause() {
        super.onPause()
        listPreference.onPreferenceChangeListener = null
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        setScanModeSummarize(newValue as String)
        return true
    }

    private fun initSummaries() {
        val scanMode = preferenceManager
            .sharedPreferences.getString("scan_mode", "3")

        scanMode?.let {
            setScanModeSummarize(scanMode)
        }
    }

    private fun setScanModeSummarize(scanMode: String) {
        when (scanMode.toInt()) {
            1 -> listPreference.summary = getString(R.string.scan_mode_low_power)
            2 -> listPreference.summary = getString(R.string.scan_mode_balanced)
            3 -> listPreference.summary = getString(R.string.scan_mode_low_latency)
        }
    }
}