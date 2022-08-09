package com.aconno.sensorics.device.bluetooth

interface BluetoothPermission {

    var isGranted: Boolean

    fun request()
}