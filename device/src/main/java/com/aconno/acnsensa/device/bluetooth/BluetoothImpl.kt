package com.aconno.acnsensa.device.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanSettings
import com.aconno.acnsensa.domain.Bluetooth
import com.aconno.acnsensa.domain.model.ScanResult
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.subjects.PublishSubject

@SuppressLint("MissingPermission")
class BluetoothImpl(
    val bluetoothAdapter: BluetoothAdapter,
    val bluetoothPermission: BluetoothPermission
) : Bluetooth {

    var scanCallback: ScanCallback? = null
    val scanResults: PublishSubject<ScanResult> = PublishSubject.create()

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

    override fun startScanning() {
        val bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
        if (bluetoothPermission.isGranted) {
            scanCallback = BluetoothScanCallback(scanResults)
            val settingsBuilder = ScanSettings.Builder()

            settingsBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).setReportDelay(0)
            bluetoothLeScanner.startScan(null, settingsBuilder.build(), scanCallback)
        } else {
            throw BluetoothException("Bluetooth permission not granted")
        }
    }

    override fun stopScanning() {
        val bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
        scanCallback?.let { bluetoothLeScanner.stopScan(it) }
    }

    override fun getScanResults(): Flowable<ScanResult> {
        return scanResults.toFlowable(BackpressureStrategy.LATEST)
    }
}