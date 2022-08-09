package com.aconno.sensorics.ui.settings

import android.os.Build
import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.aconno.sensorics.R
import com.aconno.sensorics.ui.settings.publishers.PublishListActivity
import com.aconno.sensorics.ui.settings.virtualscanningsources.VirtualScanningSourceListActivity

class SettingsFragment : PreferenceFragmentCompat(),
    Preference.OnPreferenceChangeListener {

    private var listPreference: ListPreference? = null

    override fun onResume() {
        super.onResume()
        listPreference?.onPreferenceChangeListener = this
    }

    override fun onPause() {
        super.onPause()
        listPreference?.onPreferenceChangeListener = null
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        findPreference<Preference>(PUBLISHERS_KEY)
            ?.setOnPreferenceClickListener {
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

        findPreference<Preference>(VIRTUAL_SCANNING_SOURCES_KEY)
            ?.setOnPreferenceClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    context?.let {
                        VirtualScanningSourceListActivity.start(it)
                    }
                } else {
                    activity?.let {
                        VirtualScanningSourceListActivity.start(it)
                    }
                }
                true
            }

        listPreference = findPreference(SCAN_MODE_KEY)

        initSummaries()
    }

    private fun initSummaries() {
        val scanMode = preferenceManager
            .sharedPreferences.getString(SCAN_MODE_KEY, "3")

        scanMode?.let {
            setScanModeSummarize(scanMode)
        }


    }

    private fun setScanModeSummarize(scanMode: String) {
        listPreference?.summary = when (scanMode.toInt()) {
            1 -> getString(R.string.scan_mode_low_power)
            2 -> getString(R.string.scan_mode_balanced)
            3 -> getString(R.string.scan_mode_low_latency)
            else -> getString(R.string.scan_mode_invalid)
        }
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        when (preference?.key) {
            SCAN_MODE_KEY -> setScanModeSummarize(newValue as String)
            else -> return false
        }
        return true
    }

    companion object {
        private const val SCAN_MODE_KEY = "scan_mode"
        private const val PUBLISHERS_KEY = "publishers"
        private const val VIRTUAL_SCANNING_SOURCES_KEY = "virtual_scanning_sources"
    }
}