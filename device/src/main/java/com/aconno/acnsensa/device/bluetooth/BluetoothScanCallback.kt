package com.aconno.acnsensa.device.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.le.ScanCallback
import com.aconno.acnsensa.domain.model.Advertisement
import com.aconno.acnsensa.domain.model.Device
import com.aconno.acnsensa.domain.model.ScanResult
import io.reactivex.subjects.PublishSubject
import timber.log.Timber

class BluetoothScanCallback(val scanResults: PublishSubject<ScanResult>) : ScanCallback() {

    override fun onScanResult(callbackType: Int, result: android.bluetooth.le.ScanResult?) {
        super.onScanResult(callbackType, result)
        val scanResult = createScanResult(result)
        scanResults.onNext(scanResult)
    }

    @SuppressLint("MissingPermission")
    private fun createScanResult(result: android.bluetooth.le.ScanResult?): ScanResult {
        val device = result?.device
        val deviceName = device?.name ?: "Unknown"
        val deviceAddress = device?.address ?: "Unknown"
        val scannedDevice = Device(deviceName, deviceAddress)

        val bytes = result?.scanRecord?.bytes ?: byteArrayOf()
        val advertisement = Advertisement(bytes.toList())

        return ScanResult(scannedDevice, advertisement)
    }

    override fun onScanFailed(errorCode: Int) {
        super.onScanFailed(errorCode)
        Timber.e("Scan failed with error code %d", errorCode)
    }
}