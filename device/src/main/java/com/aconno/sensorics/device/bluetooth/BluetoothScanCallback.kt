package com.aconno.sensorics.device.bluetooth

import android.bluetooth.le.ScanCallback
import com.aconno.sensorics.domain.model.ScanEvent
import com.aconno.sensorics.domain.model.ScanResult
import io.reactivex.subjects.PublishSubject
import timber.log.Timber

//TODO: This needs refactoring.
class BluetoothScanCallback(
    private val scanResults: PublishSubject<ScanResult>,
    private val scanEvents: PublishSubject<ScanEvent>
) : ScanCallback() {

    override fun onScanResult(callbackType: Int, result: android.bluetooth.le.ScanResult?) {
        super.onScanResult(callbackType, result)
        result?.let {
            val scanResult = createScanResult(result)
            scanResults.onNext(scanResult)
        }
    }

    private fun createScanResult(result: android.bluetooth.le.ScanResult): ScanResult {
        val timestamp = System.currentTimeMillis()
        val macAddress = result.device.address
        val rssi = result.rssi
        val bytes = result.scanRecord.bytes.toList()
        return ScanResult(timestamp, macAddress, rssi, bytes)
    }

    override fun onScanFailed(errorCode: Int) {
        super.onScanFailed(errorCode)
        Timber.e("Scan failed with error code %d", errorCode)
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