package com.aconno.acnsensa.device.bluetooth

interface BluetoothPermission {

    var isGranted: Boolean

    fun request()
}