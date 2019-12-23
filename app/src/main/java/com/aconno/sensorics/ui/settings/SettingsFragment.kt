package com.aconno.sensorics.ui.settings

import android.os.Build
import android.os.Bundle
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.aconno.sensorics.R
import com.aconno.sensorics.ui.settings.publishers.PublishListActivity

class SettingsFragment : PreferenceFragmentCompat(),
    Preference.OnPreferenceChangeListener {

    private lateinit var listPreference: ListPreference
    private lateinit var mqttUriPreference: EditTextPreference
    private lateinit var mqttClientIdPreference: EditTextPreference

    override fun onResume() {
        super.onResume()
        listPreference.onPreferenceChangeListener = this
        mqttUriPreference.onPreferenceChangeListener = this
        mqttClientIdPreference.onPreferenceChangeListener = this
    }

    override fun onPause() {
        super.onPause()
        listPreference.onPreferenceChangeListener = null
        mqttUriPreference.onPreferenceChangeListener = null
        mqttClientIdPreference.onPreferenceChangeListener = null
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        findPreference(PUBLISHERS_KEY)
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

        listPreference = findPreference(SCAN_MODE_KEY) as ListPreference

        mqttUriPreference = findPreference(MQTT_SERVER_URI_KEY) as EditTextPreference
        mqttClientIdPreference = findPreference(MQTT_CLIENT_ID_KEY) as EditTextPreference

        initSummaries()
    }

    private fun initSummaries() {
        val scanMode = preferenceManager
            .sharedPreferences.getString(SCAN_MODE_KEY, "3")

        scanMode?.let {
            setScanModeSummarize(scanMode)
        }

        mqttUriPreference.summary = preferenceManager.sharedPreferences.getString(MQTT_SERVER_URI_KEY,"")
        mqttClientIdPreference.summary = preferenceManager.sharedPreferences.getString(MQTT_CLIENT_ID_KEY,"")

    }

    private fun setScanModeSummarize(scanMode: String) {
        when (scanMode.toInt()) {
            1 -> listPreference.summary = getString(R.string.scan_mode_low_power)
            2 -> listPreference.summary = getString(R.string.scan_mode_balanced)
            3 -> listPreference.summary = getString(R.string.scan_mode_low_latency)
        }
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        when(preference?.key) {
            SCAN_MODE_KEY -> setScanModeSummarize(newValue as String)
            MQTT_SERVER_URI_KEY -> mqttUriPreference.summary = newValue as String
            MQTT_CLIENT_ID_KEY -> mqttClientIdPreference.summary = newValue as String
            else -> return false
        }
        return true
    }

    companion object {
        private const val SCAN_MODE_KEY = "scan_mode"
        private const val MQTT_SERVER_URI_KEY = "mqtt_server_uri"
        private const val MQTT_CLIENT_ID_KEY = "mqtt_client_id"
        private const val PUBLISHERS_KEY = "publishers"
    }
}