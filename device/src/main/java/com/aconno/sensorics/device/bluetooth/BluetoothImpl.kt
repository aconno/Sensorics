package com.aconno.sensorics.device.bluetooth

import android.Manifest
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivity
import com.aconno.sensorics.device.BluetoothGattAttributeValueConverter
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.domain.model.GattCallbackPayload
import com.aconno.sensorics.domain.model.ScanResult
import com.aconno.sensorics.domain.scanning.Bluetooth
import com.aconno.sensorics.domain.scanning.BluetoothState
import com.aconno.sensorics.domain.scanning.ScanEvent
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import java.util.*

//TODO: This needs refactoring.
class BluetoothImpl(
    private val context: Context,
    private val sharedPrefs: SharedPreferences,
    private val bluetoothAdapter: BluetoothAdapter?,
    private val bluetoothPermission: BluetoothPermission,
    private val bluetoothStateListener: BluetoothStateListener,
    private val bluetoothGattAttributeValueConverter: BluetoothGattAttributeValueConverter
) : Bluetooth, Consumer<GattCallbackPayload> {

    override var mtu: Int = 20

    private val scanResults: PublishSubject<ScanResult> = PublishSubject.create()
    private val connectGattResults: PublishSubject<GattCallbackPayload> = PublishSubject.create()

    private val scanEvent = PublishSubject.create<ScanEvent>()

    override fun getScanEvent(): Flowable<ScanEvent> {
        return scanEvent.toFlowable(BackpressureStrategy.BUFFER)
    }

    private val scanCallback: ScanCallback = BluetoothScanCallback(scanResults, scanEvent)

    private val gattCallback: BluetoothGattCallback =
        BluetoothGattCallback(context, connectGattResults)
    private var lastConnectedDeviceAddress: String? = null
    private var lastConnectedGatt: BluetoothGatt? = null

    init {
        getGattResults().subscribe(this)
    }

    override fun accept(payload: GattCallbackPayload) {
        Timber.i(payload.action)
        when (payload.action) {
            BluetoothGattCallback.ACTION_GATT_MTU_CHANGED -> {
                this.mtu = (payload.payload as? Int) ?: mtu
            }
        }
    }

    private fun getScanSettings(
        devices: List<Device>? = null
    ): Pair<List<ScanFilter>?, ScanSettings> {
        val settingsBuilder = ScanSettings.Builder()

        when (sharedPrefs.getString("scan_mode", null)?.toInt() ?: 3) {
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
        checkPermissionState()
        checkConnectPermission()
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivity(context, enableBtIntent, null)
    }

    override fun disable() {
        checkPermissionState()
        checkConnectPermission()

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S) {
            @Suppress("DEPRECATION")
            bluetoothAdapter?.disable()
        } else {
            // Starting with Build.VERSION_CODES.TIRAMISU (API level 33), applications are not allowed to enable/disable Bluetooth
        }
    }

    override fun readCharacteristic(serviceUUID: UUID, characteristicUUID: UUID): Boolean {
        checkConnectPermission()

        return lastConnectedGatt?.let { gatt ->
            gatt.getService(serviceUUID)
                ?.getCharacteristic(characteristicUUID)
                ?.let { characteristic ->
                    gatt.readCharacteristic(characteristic)
                }
        } ?: false
    }

    override fun writeCharacteristic(
        serviceUUID: UUID,
        characteristicUUID: UUID,
        type: String,
        value: Any
    ): Boolean {
        checkConnectPermission()

        return lastConnectedGatt?.let { gatt ->
            gatt.getService(serviceUUID)
                ?.getCharacteristic(characteristicUUID)
                ?.let { characteristic ->
                    if (Build.VERSION.SDK_INT < 33) {
                        bluetoothGattAttributeValueConverter.setValue(characteristic, type, value)
                        @Suppress("DEPRECATION")
                        gatt.writeCharacteristic(characteristic)
                    } else {
                        gatt.writeCharacteristic(
                            characteristic,
                            value as ByteArray,
                            BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                        ) == BluetoothStatusCodes.SUCCESS
                    }
                }
        } ?: false

//                                  10FA011800200028143000380040004A04776173647364
//                0A0961636E50696D70656B10FA011800200028143000380040004A0477617364
//                0a0961636e50696d70656b10fa011800200028143000380040004a0477617364
    }

    override fun readDescriptor(
        serviceUUID: UUID,
        characteristicUUID: UUID,
        descriptorUUID: UUID
    ): Boolean {
        checkConnectPermission()

        return lastConnectedGatt?.let { gatt ->
            gatt.getService(serviceUUID)
                ?.getCharacteristic(characteristicUUID)
                ?.getDescriptor(descriptorUUID)
                ?.let { descriptor ->
                    gatt.readDescriptor(descriptor)
                }
        } ?: false
    }

    override fun writeDescriptor(
        serviceUUID: UUID,
        characteristicUUID: UUID,
        descriptorUUID: UUID,
        type: String,
        value: Any
    ): Boolean {
        checkConnectPermission()

        return lastConnectedGatt?.let { gatt ->
            gatt.getService(serviceUUID)
                ?.getCharacteristic(characteristicUUID)
                ?.getDescriptor(descriptorUUID)
                ?.let { descriptor ->
                    bluetoothGattAttributeValueConverter.setValue(descriptor, type, value)
                    gatt.readDescriptor(descriptor)
                }
        } ?: false
    }

    override fun requestMtu(mtu: Int): Boolean {
        checkConnectPermission()
        return lastConnectedGatt?.requestMtu(mtu) ?: false
    }

    override fun connect(address: String) {
        checkConnectPermission()

        if (lastConnectedDeviceAddress != null && address == lastConnectedDeviceAddress
            && lastConnectedGatt != null
        ) {
            if (lastConnectedGatt!!.connect()) {
                gattCallback.updateConnecting()
            } else {
                gattCallback.updateError()
            }
        } else {
            val remoteDevice = bluetoothAdapter?.getRemoteDevice(address)
            if (remoteDevice == null) {
                gattCallback.updateDeviceNotFound()
            }

            gattCallback.updateConnecting()

            lastConnectedGatt = remoteDevice?.connectGatt(context, false, gattCallback)
            lastConnectedDeviceAddress = address
        }
    }

    override fun disconnect() {
        checkConnectPermission()

        if (lastConnectedGatt != null) {
            lastConnectedGatt!!.disconnect()
        }
    }

    override fun closeConnection() {
        checkConnectPermission()

        if (lastConnectedGatt != null) {
            lastConnectedGatt!!.close()
            lastConnectedGatt = null
        }
    }

    override fun startScanning(devices: List<Device>) {
        checkPermissionState()

        checkScanPermission()

        if (bluetoothAdapter?.isEnabled == true) {
            Timber.i("Start Bluetooth scanning, devices: $devices")
            val bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
            val scanSettings = getScanSettings(devices)
            scanEvent.onNext(ScanEvent.start())
            bluetoothLeScanner.startScan(scanSettings.first, scanSettings.second, scanCallback)
        } else {
            Timber.w("Bluetooth is not enabled. Can't start scanning")
        }
    }


    override fun startScanning() {
        checkPermissionState()

        checkScanPermission()

        if (bluetoothAdapter?.isEnabled == true) {
            Timber.i("Start Bluetooth scanning")
            val bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
            val scanSettings = getScanSettings()
            scanEvent.onNext(ScanEvent.start())
            bluetoothLeScanner.startScan(scanSettings.first, scanSettings.second, scanCallback)
        } else {
            Timber.w("Bluetooth is not enabled. Can't start scanning")
        }
    }

    override fun stopScanning() {
        checkScanPermission()

        val bluetoothLeScanner = bluetoothAdapter?.bluetoothLeScanner
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
        val currentState =
            Observable.just(bluetoothAdapter?.state ?: BluetoothAdapter.STATE_OFF).map {
                when (it) {
                    BluetoothAdapter.STATE_ON -> BluetoothState.BLUETOOTH_ON
                    BluetoothAdapter.STATE_OFF -> BluetoothState.BLUETOOTH_OFF
                    else -> BluetoothState.BLUETOOTH_OFF
                }
            }

        return currentState.mergeWith(bluetoothStateListener.getBluetoothStates())
            .toFlowable(BackpressureStrategy.BUFFER)
    }

    override fun enableCharacteristicNotification(
        characteristicUUID: UUID,
        serviceUUID: UUID,
        isEnabled: Boolean
    ): Boolean {
        checkConnectPermission()

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

                val value =
                    if (isEnabled) BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE else byteArrayOf(
                        0x00,
                        0x00
                    )

                if (Build.VERSION.SDK_INT >= 33) {
                    return bluetoothGatt.writeDescriptor(
                        descriptor,
                        value
                    ) == BluetoothStatusCodes.SUCCESS
                } else {
                    @Suppress("DEPRECATION")
                    return bluetoothGatt.writeDescriptor(descriptor.apply {
                        this.value = value
                    })
                }
            }
        }

        return false
    }

    private fun checkConnectPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            throw BluetoothException("Bluetooth permission not granted")
        }
    }

    private fun checkPermissionState() {
        if (!bluetoothPermission.isGranted) throw BluetoothException("Bluetooth permission not granted")
    }

    private fun checkScanPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            throw BluetoothException("Bluetooth permission not granted")
        }
    }

    companion object {
        private const val CHARACTERISTIC_UPDATE_NOTIFICATION_DESCRIPTOR_UUID =
            "00002902-0000-1000-8000-00805f9b34fb"
    }
}