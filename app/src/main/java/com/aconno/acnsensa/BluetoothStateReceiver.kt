package com.aconno.acnsensa

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.aconno.acnsensa.device.bluetooth.BluetoothStateListener
import com.aconno.acnsensa.domain.BluetoothState
import timber.log.Timber

/**
 * @author aconno
 */
class BluetoothStateReceiver(private val bluetoothStateListener: BluetoothStateListener) :
    BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Timber.e("Got bluetooth state change broadcast.")
        val newState = getBluetoothState(intent)
        bluetoothStateListener.onBluetoothStateEvent(newState)
    }

    private fun getBluetoothState(intent: Intent?): BluetoothState {
        val bluetoothState = intent?.getIntExtra(BluetoothAdapter.EXTRA_STATE, ERROR_STATE)

        return when (bluetoothState) {
            BluetoothAdapter.STATE_ON -> BluetoothState(BluetoothState.BLUETOOTH_ON)
            BluetoothAdapter.STATE_OFF -> BluetoothState(BluetoothState.BLUETOOTH_OFF)
            else -> BluetoothState(BluetoothState.BLUETOOTH_OFF)
        }
    }

    companion object {
        private const val ERROR_STATE = -1
    }
}