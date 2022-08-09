package com.aconno.sensorics

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.aconno.sensorics.device.bluetooth.BluetoothStateListener
import com.aconno.sensorics.domain.scanning.BluetoothState
import timber.log.Timber

/**
 * @author aconno
 */
class BluetoothStateReceiver(private val bluetoothStateListener: BluetoothStateListener) :
    BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == BluetoothAdapter.ACTION_STATE_CHANGED) {
            getBluetoothState(intent)?.also {
                bluetoothStateListener.onBluetoothStateEvent(it)
            }
        }
    }

    private fun getBluetoothState(intent: Intent): BluetoothState? {
        val bluetoothState =
            intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)

        Timber.d("Bluetooth state has been changed. New state. state = $bluetoothState")

        return when (bluetoothState) {
            BluetoothAdapter.STATE_ON -> BluetoothState.BLUETOOTH_ON
            BluetoothAdapter.STATE_OFF -> BluetoothState.BLUETOOTH_OFF
            else -> BluetoothState.BLUETOOTH_OFF
        }
    }
}