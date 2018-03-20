package com.aconno.acnsensa.device.bluetooth

/**
 * @author aconno
 */
class BluetoothPermissionImpl : BluetoothPermission {
    override var isGranted: Boolean = true

    override fun request() {
        //Do nothing.
    }
}