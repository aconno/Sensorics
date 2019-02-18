package com.aconno.bluetooth

open class BluetoothGattCallback {
    open fun onDeviceConnected(device: BluetoothDevice) {}
    open fun onDeviceDisconnected(device: BluetoothDevice) {}
    open fun onDeviceConnecting(device: BluetoothDevice) {}
    open fun onDeviceDisconnecting(device: BluetoothDevice) {}
    open fun onServicesDiscovered(device: BluetoothDevice) {}
}