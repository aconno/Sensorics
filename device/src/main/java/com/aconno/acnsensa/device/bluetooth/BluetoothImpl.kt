package com.aconno.acnsensa.device.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.aconno.acnsensa.domain.model.ScanResult
import com.aconno.acnsensa.domain.model.ScanEvent
import com.aconno.acnsensa.domain.scanning.Bluetooth
import com.aconno.acnsensa.domain.scanning.BluetoothState
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

//TODO: This needs refactoring.
class BluetoothImpl(
    context: Context,
    private val bluetoothAdapter: BluetoothAdapter,
    private val bluetoothPermission: BluetoothPermission,
    private val bluetoothStateListener: BluetoothStateListener
) : Bluetooth {

    private val sharedPrefs: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    private val scanResults: PublishSubject<ScanResult> = PublishSubject.create()
    private val scanEvents: PublishSubject<ScanEvent> = PublishSubject.create()
    private val scanCallback: ScanCallback = BluetoothScanCallback(scanResults, scanEvents)

    private fun getScanSettings(): Pair<List<ScanFilter>?, ScanSettings> {
        val settingsBuilder = ScanSettings.Builder()

        val scanMode = sharedPrefs.getString("scan_mode", "3").toInt()
        when (scanMode) {
            1 -> settingsBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
            2 -> settingsBuilder.setScanMode(ScanSettings.SCAN_MODE_BALANCED)
            3 -> settingsBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
        }
        //TODO Fast Scan
        settingsBuilder.setReportDelay(0)

        val scanInBackground = sharedPrefs.getBoolean("scan_background", true)
        //Note: This is only for Andorid 8.1.0. TODO Implementation for prior 8.1.0
        //https://stackoverflow.com/questions/48077690/ble-scan-is-not-working-when-screen-is-off-on-android-8-1-0/48079800#48079800
        return if (scanInBackground) {
            val scanFilterBuilder = ScanFilter.Builder()
            Pair<List<ScanFilter>?, ScanSettings>(
                listOf(scanFilterBuilder.build()),
                settingsBuilder.build()
            )
        } else {
            Pair<List<ScanFilter>?, ScanSettings>(
                null,
                settingsBuilder.build()
            )
        }
    }

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
            scanEvents.onNext(
                ScanEvent(ScanEvent.SCAN_START, "Scan start at ${System.currentTimeMillis()}")
            )

            val scanSettings = getScanSettings()
            bluetoothLeScanner.startScan(scanSettings.first, scanSettings.second, scanCallback)
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