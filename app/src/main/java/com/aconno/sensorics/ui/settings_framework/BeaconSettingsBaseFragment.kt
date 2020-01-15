package com.aconno.sensorics.ui.settings_framework

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager

abstract class BeaconSettingsBaseFragment : Fragment() {

    private var beaconBroadcastReceiver : BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if(intent == null) {
                return
            }
            val beaconInformation = intent.getStringExtra(SettingsFrameworkActivity.BEACON_JSON_BROADCAST_EXTRA)

            onBeaconInformationLoaded(beaconInformation)
        }
    }

    abstract fun onBeaconInformationLoaded(beaconInformation : String)

    override fun onStop() {
        super.onStop()
        beaconBroadcastReceiver?.let {
            LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(it)
        }

    }

    override fun onStart() {
        super.onStart()
        beaconBroadcastReceiver?.let {
            LocalBroadcastManager.getInstance(requireContext()).registerReceiver(it,
                    IntentFilter(SettingsFrameworkActivity.BEACON_JSON_BROADCAST))
        }
    }

    protected fun requestBeaconInfo() {
        val intent = Intent(SettingsFrameworkActivity.BEACON_JSON_REQUEST_BROADCAST)
        LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
    }
}