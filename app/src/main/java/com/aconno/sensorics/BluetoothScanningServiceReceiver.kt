package com.aconno.sensorics

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager

/**
 * @author aconno
 */
class BluetoothScanningServiceReceiver(
    private val bluetoothScanningService: BluetoothScanningService
) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        //TODO: Check intent action.
        val localBroadcastManager =
            LocalBroadcastManager.getInstance(bluetoothScanningService)
        localBroadcastManager.unregisterReceiver(this)
        bluetoothScanningService.stopScanning()
    }
}