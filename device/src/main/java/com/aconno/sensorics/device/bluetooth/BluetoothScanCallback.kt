package com.aconno.sensorics.device.bluetooth

import android.bluetooth.le.ScanCallback
import com.aconno.sensorics.domain.model.ScanEvent
import com.aconno.sensorics.domain.model.ScanResult
import io.reactivex.subjects.PublishSubject
import timber.log.Timber

class BluetoothScanCallback(
    private val scanResults: PublishSubject<ScanResult>,
    private val scanEvents: PublishSubject<ScanEvent>
) : ScanCallback() {

    override fun onScanResult(callbackType: Int, result: android.bluetooth.le.ScanResult?) {
        Timber.i("Bluetooth scan result, mac: ${result?.device?.address}")
        result?.let {
            val scanResult = createScanResult(result)
            scanResults.onNext(scanResult)
        }
    }

    private fun createScanResult(result: android.bluetooth.le.ScanResult): ScanResult {
        val timestamp = System.currentTimeMillis()
        val macAddress = result.device.address
        val rssi = result.rssi
        val bytes = result.scanRecord!!.bytes
        return ScanResult(timestamp, macAddress, rssi, bytes)
    }

    override fun onScanFailed(errorCode: Int) {
        Timber.e("Bluetooth scan failed, error code: $errorCode")
        when (errorCode) {
            SCAN_FAILED_ALREADY_STARTED ->
                scanEvents.onNext(
                    ScanEvent(
                        ScanEvent.SCAN_FAILED_ALREADY_STARTED,
                        "Scan Failed with error code $errorCode"
                    )
                )
            else ->
                scanEvents.onNext(
                    ScanEvent(ScanEvent.SCAN_FAILED, "Scan failed with error code $errorCode")
                )
        }
    }
}