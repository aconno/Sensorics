package com.aconno.bluetooth

import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import io.reactivex.subjects.PublishSubject

class BluetoothLeScanCallback(val scanResults: PublishSubject<ScanResult>) : ScanCallback() {
    override fun onScanResult(callbackType: Int, result: ScanResult) {
        scanResults.onNext(result)
    }

    override fun onBatchScanResults(results: MutableList<ScanResult>) {
        results.forEach { scanResults.onNext(it) }
    }

    override fun onScanFailed(errorCode: Int) {
        super.onScanFailed(errorCode)
    }
}