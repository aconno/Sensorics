package com.aconno.acnsensa

import android.app.Application
import android.bluetooth.BluetoothAdapter
import com.aconno.acnsensa.device.bluetooth.BluetoothImpl
import com.aconno.acnsensa.device.bluetooth.BluetoothPermission

//TODO: This needs refactoring
/**
 * @author aconno
 */
class AcnSensaApplication : Application() {

    val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    val bluetoothPermission: BluetoothPermission = object : BluetoothPermission {
        override var isGranted: Boolean
            get() = true
            set(value) {}

        override fun request() {

        }
    }

    val bluetooth = BluetoothImpl(bluetoothAdapter, bluetoothPermission)


}