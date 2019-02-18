package com.aconno.bluetooth

import android.bluetooth.le.ScanResult
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer

val REQUEST_ENABLE_BLUETOOTH: Int = 0x9000

interface Bluetooth {
    fun startScan(consumer: Consumer<ScanResult>? = null): Disposable?
    fun startScanForDevice(address: String, consumer: Consumer<ScanResult>? = null): Disposable?
    fun <T : DeviceSpec> startScanForDevice(
        device: Class<T>,
        consumer: Consumer<ScanResult>? = null
    ): Disposable?

    fun startScanForDevices(
        devices: List<Class<out DeviceSpec>>,
        consumer: Consumer<ScanResult>? = null
    ): Disposable?

    fun stopScan()
    fun connect(device: BluetoothDevice, callback: BluetoothGattCallback? = null)
}