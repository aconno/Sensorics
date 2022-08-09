package com.aconno.sensorics

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager

/**
 * @author aconno
 */
class MqttVirtualScanningServiceReceiver(
    private val mqttVirtualScanningService: MqttVirtualScanningService
) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        //TODO: Check intent action.
        val localBroadcastManager = LocalBroadcastManager.getInstance(mqttVirtualScanningService)
        localBroadcastManager.unregisterReceiver(this)
        mqttVirtualScanningService.stopScanning()
    }
}