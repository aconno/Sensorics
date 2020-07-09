package com.aconno.sensorics.ui.settings

import android.os.Build
import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.aconno.sensorics.R
import com.aconno.sensorics.device.settings.LocalSettings
import com.aconno.sensorics.ui.settings.publishers.PublishListActivity
import com.aconno.sensorics.ui.settings.virtualscanningsources.VirtualScanningSourceListActivity
import com.troido.bless.ScanMode

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

        initScanModePreference()
    }

    private fun initScanModePreference() {
        findPreference<ListPreference>(LocalSettings.PREFERRED_BLE_SCAN_MODE_KEY)?.let { pref ->
            pref.entries = ScanMode.values().map { getScanModeString(it) }.toTypedArray()
            pref.entryValues = ScanMode.values().map { it.name }.toTypedArray()
            listPreference = pref
        }
        initScanModeSummary()
    }

    private fun getScanModeString(mode: ScanMode): String {
        return when (mode) {
            ScanMode.LOW_POWER -> getString(R.string.scan_mode_low_power)
            ScanMode.BALANCED -> getString(R.string.scan_mode_balanced)
            ScanMode.LOW_LATENCY -> getString(R.string.scan_mode_low_latency)
        }
    }

    private fun initScanModeSummary() {
        val modeName = preferenceManager.sharedPreferences.getString(
            LocalSettings.PREFERRED_BLE_SCAN_MODE_KEY,
            LocalSettings.DEFAULT_BLE_SCAN_MODE
        )!!
        updateScanModeSummary(modeName)
    }

    private fun updateScanModeSummary(scanMode: String) {
        listPreference?.summary =
            when (scanMode) {
                ScanMode.LOW_POWER.name -> getString(R.string.scan_mode_low_power)
                ScanMode.BALANCED.name -> getString(R.string.scan_mode_balanced)
                ScanMode.LOW_LATENCY.name -> getString(R.string.scan_mode_low_latency)
                else -> getString(R.string.scan_mode_invalid)
            }
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        return if (preference != null && preference.key == listPreference?.key) {
            updateScanModeSummary(newValue as String)
            true
        } else {
            false
        }
    }

    companion object {

        private const val PUBLISHERS_KEY = "publishers"
        private const val VIRTUAL_SCANNING_SOURCES_KEY = "virtual_scanning_sources"
    }
}