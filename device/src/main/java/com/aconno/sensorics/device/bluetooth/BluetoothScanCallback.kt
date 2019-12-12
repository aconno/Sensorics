package com.aconno.sensorics.device.bluetooth

import android.bluetooth.le.ScanCallback
import com.aconno.sensorics.domain.model.ScanResult
import com.aconno.sensorics.domain.scanning.ScanEvent
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import timber.log.Timber

class BluetoothScanCallback(
    private val scanResults: PublishSubject<ScanResult>,
    private val scanEvents: Subject<ScanEvent>
) : ScanCallback() {

    override fun onScanResult(callbackType: Int, result: android.bluetooth.le.ScanResult?) {
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
        val tmp = bytes.joinToString { "\\x%02x".format(it) }
        return ScanResult(timestamp, macAddress, rssi, bytes)
    }

    override fun onScanFailed(errorCode: Int) {
        Timber.e("Bluetooth scan failed, error code: $errorCode")
        when (errorCode) {
            SCAN_FAILED_ALREADY_STARTED ->
                scanEvents.onNext(ScanEvent.failedAlreadyStarted(errorCode))
            else ->
                scanEvents.onNext(ScanEvent.failed(errorCode))
        }
    }
}