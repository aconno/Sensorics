package com.aconno.sensorics.ui.settings

import android.os.Build
import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.aconno.sensorics.R
import com.aconno.sensorics.ui.settings.publishers.PublishListActivity

class SettingsFragment : PreferenceFragmentCompat(),
    Preference.OnPreferenceChangeListener {

    private lateinit var listPreference: ListPreference

    override fun onResume() {
        super.onResume()
        listPreference.onPreferenceChangeListener = this
    }

    override fun onPause() {
        super.onPause()
        listPreference.onPreferenceChangeListener = null
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        findPreference("publishers")
            .setOnPreferenceClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    context?.let {
                        PublishListActivity.start(it)
                    }
                } else {
                    activity?.let {
                        PublishListActivity.start(it)
                    }
                }
                true
            }

        listPreference = findPreference("scan_mode") as ListPreference

        initSummaries()
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

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        setScanModeSummarize(newValue as String)
        return true
    }
}