package com.aconno.acnsensa.device.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanSettings
import com.aconno.acnsensa.domain.scanning.Bluetooth
import com.aconno.acnsensa.domain.scanning.BluetoothState
import com.aconno.acnsensa.domain.model.ScanEvent
import com.aconno.acnsensa.domain.model.ScanResult
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

//TODO: This needs refactoring.
class BluetoothImpl(
    private val bluetoothAdapter: BluetoothAdapter,
    private val bluetoothPermission: BluetoothPermission,
    private val bluetoothStateListener: BluetoothStateListener
) : Bluetooth {

    private val scanResults: PublishSubject<ScanResult> = PublishSubject.create()
    private val scanEvents: PublishSubject<ScanEvent> = PublishSubject.create()
    private val scanCallback: ScanCallback = BluetoothScanCallback(scanResults, scanEvents)

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
            val settingsBuilder = ScanSettings.Builder()

            settingsBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).setReportDelay(0)
            scanEvents.onNext(
                ScanEvent(ScanEvent.SCAN_START, "Scan start at ${System.currentTimeMillis()}")
            )
            bluetoothLeScanner.startScan(null, settingsBuilder.build(), scanCallback)
        } else {
            throw BluetoothException("Bluetooth permission not granted")
        }
    }

    override fun stopScanning() {
        val bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
        scanEvents.onNext(
            ScanEvent(ScanEvent.SCAN_STOP, "Scan stop at ${System.currentTimeMillis()}")
        )
        bluetoothLeScanner.stopScan(scanCallback)
    }

    override fun getScanResults(): Flowable<ScanResult> {
        return scanResults.toFlowable(BackpressureStrategy.LATEST)
    }

    override fun getScanEvents(): Flowable<ScanEvent> {
        return scanEvents.toFlowable(BackpressureStrategy.BUFFER)
    }

    override fun getStateEvents(): Flowable<BluetoothState> {
        val currentState = Observable.just(bluetoothAdapter.state).map {
            when (it) {
                BluetoothAdapter.STATE_ON -> BluetoothState(BluetoothState.BLUETOOTH_ON)
                BluetoothAdapter.STATE_OFF -> BluetoothState(BluetoothState.BLUETOOTH_OFF)
                else -> BluetoothState(BluetoothState.BLUETOOTH_OFF)
            }
        }

        return currentState.mergeWith(bluetoothStateListener.getBluetoothStates())
            .toFlowable(BackpressureStrategy.LATEST)
    }
}