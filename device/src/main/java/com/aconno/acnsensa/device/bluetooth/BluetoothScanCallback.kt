package com.aconno.acnsensa.device.bluetooth

import android.bluetooth.le.ScanCallback
import com.aconno.acnsensa.domain.interactor.filter.ScanResult
import com.aconno.acnsensa.domain.model.ScanEvent
import io.reactivex.subjects.PublishSubject
import timber.log.Timber

//TODO: This needs refactoring.
class BluetoothScanCallback(
    private val scanResults: PublishSubject<ScanResult>,
    private val scanEvents: PublishSubject<ScanEvent>
) : ScanCallback() {

    override fun onScanResult(callbackType: Int, result: android.bluetooth.le.ScanResult?) {
        super.onScanResult(callbackType, result)
        val scanResult = createScanResult(result)
        scanResults.onNext(scanResult)
    }

    private fun createScanResult(result: android.bluetooth.le.ScanResult?): ScanResult {
        val timestamp = System.currentTimeMillis()
        val macAddress = result?.device?.address ?: "Unknown"
        val bytes = result?.scanRecord?.bytes?.toList() ?: listOf()
        return ScanResult(timestamp, macAddress, bytes)
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