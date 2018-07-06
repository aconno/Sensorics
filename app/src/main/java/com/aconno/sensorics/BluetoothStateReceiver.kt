package com.aconno.sensorics

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.aconno.sensorics.device.bluetooth.BluetoothStateListener
import com.aconno.sensorics.domain.scanning.BluetoothState

/**
 * @author aconno
 */
class BluetoothStateReceiver(private val bluetoothStateListener: BluetoothStateListener) :
    BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        //TODO: Check intent action.
        val newState = getBluetoothState(intent)
        bluetoothStateListener.onBluetoothStateEvent(newState)
    }

    private fun getBluetoothState(intent: Intent?): BluetoothState {
        val bluetoothState =
            intent?.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)

        return when (bluetoothState) {
            BluetoothAdapter.STATE_ON -> BluetoothState(BluetoothState.BLUETOOTH_ON)
            BluetoothAdapter.STATE_OFF -> BluetoothState(BluetoothState.BLUETOOTH_OFF)
            else -> BluetoothState(BluetoothState.BLUETOOTH_OFF)
        }
    }
}