package com.aconno.sensorics.device.bluetooth

/**
 * @author aconno
 */
class BluetoothPermissionImpl : BluetoothPermission {
    override var isGranted: Boolean = true

    override fun request() {
        //Do nothing.
    }
}