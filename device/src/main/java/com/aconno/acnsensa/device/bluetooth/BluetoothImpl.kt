package com.aconno.acnsensa.device.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import com.aconno.acnsensa.domain.Bluetooth
import com.aconno.acnsensa.domain.model.ScanResult
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

@SuppressLint("MissingPermission")
class BluetoothImpl(
    val bluetoothAdapter: BluetoothAdapter,
    val bluetoothPermission: BluetoothPermission
) : Bluetooth {

    var scanCallback: ScanCallback? = null

    override fun enable() {
        if (bluetoothPermission.isGranted) {
            bluetoothAdapter.enable()
        } else {
            throw BluetoothException("Bluetooth permission not granted")
        }
    }

    override fun disable() {
        if (bluetoothPermission.isGranted) {
            bluetoothAdapter.disable()
        } else {
            throw BluetoothException("Bluetooth permission not granted")
        }
    }

    override fun startScanning(): Observable<ScanResult> {
        val bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
        if (bluetoothPermission.isGranted) {
            val scanResults: PublishSubject<ScanResult> = PublishSubject.create()
            scanCallback = BluetoothScanCallback(scanResults)
            bluetoothLeScanner.startScan(scanCallback)
            return scanResults
        } else {
            throw BluetoothException("Bluetooth permission not granted")
        }
    }

    override fun stopScanning() {
        val bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
        scanCallback?.let { bluetoothLeScanner.stopScan(it) }
    }
}