package com.aconno.sensorics.device.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.SharedPreferences
import com.aconno.sensorics.device.BluetoothCharacteristicValueConverter
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.domain.model.GattCallbackPayload
import com.aconno.sensorics.domain.model.ScanResult
import com.aconno.sensorics.domain.scanning.Bluetooth
import com.aconno.sensorics.domain.scanning.BluetoothState
import com.aconno.sensorics.domain.scanning.ScanEvent
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import java.util.*

//TODO: This needs refactoring.
class BluetoothImpl(
    private val context: Context,
    private val sharedPrefs: SharedPreferences,
    private val bluetoothAdapter: BluetoothAdapter,
    private val bluetoothPermission: BluetoothPermission,
    private val bluetoothStateListener: BluetoothStateListener,
    private val bluetoothCharacteristicValueConverter: BluetoothCharacteristicValueConverter
) : Bluetooth {

    private val scanResults: PublishSubject<ScanResult> = PublishSubject.create()
    private val connectGattResults: PublishSubject<GattCallbackPayload> = PublishSubject.create()

    private val scanEvent = PublishSubject.create<ScanEvent>()

    override fun getScanEvent(): Flowable<ScanEvent> =
        scanEvent.toFlowable(BackpressureStrategy.BUFFER)

    private val scanCallback: ScanCallback = BluetoothScanCallback(scanResults, scanEvent)

    private val gattCallback: BluetoothGattCallback = BluetoothGattCallback(connectGattResults)
    private var lastConnectedDeviceAddress: String? = null
    private var lastConnectedGatt: BluetoothGatt? = null

    private fun getScanSettings(
        devices: List<Device>? = null
    ): Pair<List<ScanFilter>?, ScanSettings> {
        val settingsBuilder = ScanSettings.Builder()

        val scanMode = sharedPrefs.getString("scan_mode", "3").toInt()
        when (scanMode) {
            1 -> settingsBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
            2 -> settingsBuilder.setScanMode(ScanSettings.SCAN_MODE_BALANCED)
            3 -> settingsBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
        }

        settingsBuilder.setReportDelay(0)

        val scanFilterBuilder = ScanFilter.Builder()
        val scanFilterList = mutableListOf<ScanFilter>()

        if (devices != null && devices.isNotEmpty()) {
            devices.forEach {
                scanFilterBuilder.setDeviceAddress(it.macAddress)
                scanFilterList.add(
                    scanFilterBuilder.build()
                )
            }
        } else {
            scanFilterList.add(
                scanFilterBuilder.build()
            )
        }

        return Pair<List<ScanFilter>?, ScanSettings>(
            scanFilterList,
            settingsBuilder.build()
        )
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

    override fun readCharacteristic(serviceUUID: UUID, characteristicUUID: UUID): Boolean {
        return lastConnectedGatt?.let {
            val characteristic = it.getService(serviceUUID)
                .getCharacteristic(characteristicUUID)

            if (characteristic != null) {
                it.readCharacteristic(characteristic)
                true
            } else {
                false
            }
        }!!
    }

    override fun writeCharacteristic(
        serviceUUID: UUID,
        characteristicUUID: UUID,
        type: String,
        value: Any
    ): Boolean {
        return lastConnectedGatt?.let {
            val characteristic = it.getService(serviceUUID)
                .getCharacteristic(characteristicUUID)

            bluetoothCharacteristicValueConverter.setValue(characteristic, type, value)
            if (characteristic != null) {
                it.writeCharacteristic(characteristic)
                true
            } else {
                false
            }
        }!!
    }

    override fun connect(address: String) {

        if (lastConnectedDeviceAddress != null && address == lastConnectedDeviceAddress
            && lastConnectedGatt != null
        ) {
            if (lastConnectedGatt!!.connect()) {
                gattCallback.updateConnecting()
            } else {
                gattCallback.updateError()
            }
        } else {
            val remoteDevice = bluetoothAdapter.getRemoteDevice(address)
            if (remoteDevice == null) {
                gattCallback.updateDeviceNotFound()
            }

            gattCallback.updateConnecting()
            lastConnectedGatt = remoteDevice.connectGatt(context, false, gattCallback)
            lastConnectedDeviceAddress = address
        }
    }

    override fun disconnect() {
        if (lastConnectedGatt != null) {
            lastConnectedGatt!!.disconnect()
        }
    }

    override fun closeConnection() {
        if (lastConnectedGatt != null) {
            lastConnectedGatt!!.close()
            lastConnectedGatt = null
        }
    }

    override fun startScanning(devices: List<Device>) {
        Timber.i("Start Bluetooth scanning, devices: $devices")
        val bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
        if (bluetoothPermission.isGranted) {
            val scanSettings = getScanSettings(devices)
            scanEvent.onNext(ScanEvent.start())
            bluetoothLeScanner.startScan(scanSettings.first, scanSettings.second, scanCallback)
        } else {
            throw BluetoothException("Bluetooth permission not granted")
        }
    }

    override fun startScanning() {
        Timber.i("Start Bluetooth scanning")
        val bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
        if (bluetoothPermission.isGranted) {
            val scanSettings = getScanSettings()
            scanEvent.onNext(ScanEvent.start())
            bluetoothLeScanner.startScan(scanSettings.first, scanSettings.second, scanCallback)
        } else {
            throw BluetoothException("Bluetooth permission not granted")
        }
    }

    override fun stopScanning() {
        val bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
        bluetoothLeScanner?.let {
            scanEvent.onNext(ScanEvent.stop())
            it.stopScan(scanCallback)
        }
    }

    override fun getGattResults(): Flowable<GattCallbackPayload> {
        return connectGattResults.toFlowable(BackpressureStrategy.BUFFER)
    }

    override fun getScanResults(): Flowable<ScanResult> {
        return scanResults.toFlowable(BackpressureStrategy.LATEST).observeOn(Schedulers.io())
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

    override fun enableCharacteristicNotification(
        characteristicUUID: UUID,
        serviceUUID: UUID,
        isEnabled: Boolean
    ): Boolean {
        lastConnectedGatt?.let { bluetoothGatt ->
            val characteristic = bluetoothGatt.getService(serviceUUID)
                .getCharacteristic(characteristicUUID) ?: return false

            characteristic.let {
                bluetoothGatt.setCharacteristicNotification(characteristic, isEnabled)
                val descriptor = characteristic.getDescriptor(
                    UUID.fromString(
                        CHARACTERISTIC_UPDATE_NOTIFICATION_DESCRIPTOR_UUID
                    )
                )

                descriptor.value =
                    if (isEnabled) BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE else byteArrayOf(
                        0x00,
                        0x00
                    )
                return bluetoothGatt.writeDescriptor(descriptor)
            }
        }

        return false
    }

    companion object {
        private const val CHARACTERISTIC_UPDATE_NOTIFICATION_DESCRIPTOR_UUID =
            "00002902-0000-1000-8000-00805f9b34fb"
    }
}