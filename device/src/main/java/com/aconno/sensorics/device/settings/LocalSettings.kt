package com.aconno.sensorics.device.settings

import android.content.SharedPreferences
import com.troido.bless.ScanMode
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalSettings @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {

    var preferredBleScanMode: ScanMode
        get() {
            val modeName =
                sharedPreferences.getString(PREFERRED_BLE_SCAN_MODE_KEY, DEFAULT_BLE_SCAN_MODE)!!
            return if (modeName in ScanMode.values().map { it.name }) {
                ScanMode.valueOf(modeName)
            } else {
                Timber.e("Invalid preferred BLE scan mode name: $modeName, instead using: $DEFAULT_BLE_SCAN_MODE.")
                ScanMode.valueOf(DEFAULT_BLE_SCAN_MODE)
            }
        }
        set(value) {
            sharedPreferences.edit().putString(PREFERRED_BLE_SCAN_MODE_KEY, value.name).apply()
        }

    companion object {

        const val PREFERRED_BLE_SCAN_MODE_KEY = "com.troido.bless.ScanMode#preferred"

        val DEFAULT_BLE_SCAN_MODE: String = ScanMode.LOW_LATENCY.name
    }
}