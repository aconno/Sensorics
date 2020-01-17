package com.aconno.sensorics.ui.settings_framework.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.aconno.sensorics.ui.settings_framework.SettingsFrameworkActivity

abstract class BeaconSettingsBaseFragment(private val fragmentId : Int? = null) : Fragment() {
    protected var beaconInformation : String? = null

    private var beaconBroadcastReceiver : BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if(intent == null) {
                return
            }
            beaconInformation = intent.getStringExtra(SettingsFrameworkActivity.BEACON_JSON_BROADCAST_EXTRA)

            onBeaconInformationLoaded(beaconInformation!!)
        }
    }

    private var saveActionBroadcastReceiver : BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if(intent == null) {
                return
            }
            val targetFragmentId = intent.getIntExtra(SettingsFrameworkActivity.TARGET_FRAGMENT_ID_BROADCAST_EXTRA,-1)
            if(targetFragmentId==fragmentId) {
                saveChanges()
            }
        }
    }

    open fun saveChanges() {}

    abstract fun onBeaconInformationLoaded(beaconInformation : String)

    override fun onStop() {
        super.onStop()
        beaconBroadcastReceiver?.let {
            LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(it)
        }
        saveActionBroadcastReceiver?.let {
            LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(it)
        }

    }

    override fun onStart() {
        super.onStart()
        beaconBroadcastReceiver?.let {
            LocalBroadcastManager.getInstance(requireContext()).registerReceiver(it,
                    IntentFilter(SettingsFrameworkActivity.BEACON_JSON_BROADCAST))
        }
        saveActionBroadcastReceiver?.let {
            LocalBroadcastManager.getInstance(requireContext()).registerReceiver(it,
                    IntentFilter(SettingsFrameworkActivity.SAVE_CHANGES_BROADCAST))
        }
    }

    protected fun requestBeaconInfo() {
        val intent = Intent(SettingsFrameworkActivity.BEACON_JSON_REQUEST_BROADCAST)
        LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
    }
}