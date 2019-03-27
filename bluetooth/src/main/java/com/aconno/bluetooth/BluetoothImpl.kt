package com.aconno.bluetooth

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.bluetooth.le.ScanSettings.CALLBACK_TYPE_ALL_MATCHES
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.functions.Predicate
import io.reactivex.subjects.PublishSubject
import timber.log.Timber

fun ByteArray.toHex() = this.joinToString(separator = "") { it.toInt().and(0xff).toString(16).padStart(2, '0') }
class BluetoothImpl(val context: Activity) : Bluetooth, ScanCallback() {
    /**
     * Bluetooth Manager
     */
    private var manager: BluetoothManager
    /**
     * Bluetooth Adapter
     */
    private var adapter: BluetoothAdapter
    /**
     * Is currently scanning
     */
    private var scanning: Boolean = false

    /**
     * Publish Subject for scan results
     */
    private val scanResults: PublishSubject<ScanResult> = PublishSubject.create()
    /**
     * Set of scan filters
     */
    private val scanFilters: MutableSet<Predicate<ScanResult>> = mutableSetOf()

    init {
        if (context !is BluetoothEnableRequestListener) {
            throw IllegalArgumentException("Context must implement BluetoothEnableRequestListener")
        }
        if (context.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            adapter = manager.adapter ?: throw BluetoothAdapterMissingException()
        } else throw BleNotSupportedException()
    }

    override fun startScan(consumer: Consumer<ScanResult>?): Disposable? {
        if (adapter.isEnabled) {
            if (adapter.bluetoothLeScanner != null) {
                adapter.bluetoothLeScanner.startScan(mutableListOf(), ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build(), this)
                scanning = true
                return consumer?.let { scanResults.subscribe(it) }
            } else {
                scanning = false
            }
        } else requestEnableBluetooth()
        return null
    }

    override fun startScanForDevice(address: String, consumer: Consumer<ScanResult>?): Disposable? {
        scanFilters.clear()
        scanFilters.add(Predicate { it.device.address == address })
        return startScan(consumer)
    }

    override fun <T> startScanForDevice(device: Class<T>, consumer: Consumer<ScanResult>?): Disposable? where T : DeviceSpec {
        return startScanForDevices(listOf(device), consumer)
    }

    override fun startScanForDevices(devices: List<Class<out DeviceSpec>>, consumer: Consumer<ScanResult>?): Disposable? {
        scanFilters.clear()
        scanFilters.addAll(devices.mapNotNull {
            try {
                @Suppress("UNCHECKED_CAST")
                it.getDeclaredField("matcher").get(null) as Predicate<ScanResult>
            } catch (e: Exception) {
                when (e) {
                    is ClassCastException, is NoSuchFieldException -> {
                        Timber.e("Class needs to have a static constant matcher of type Predicate<ByteArray> to qualify as a Device specification.")
                        null
                    }
                    else -> throw e
                }
            }
        })

        return startScan(consumer)
    }

    override fun stopScan(): Boolean {
        return adapter.bluetoothLeScanner?.let {
            it.stopScan(this)
            scanning = false
            true
        } ?: false
    }

    private fun requestEnableBluetooth() {
        context.startActivityForResult(
                Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),
                REQUEST_ENABLE_BLUETOOTH
        )
    }

    interface BluetoothEnableRequestListener {
        fun onBluetoothRequestActivityResult()
    }

    override fun connect(device: BluetoothDevice, callback: BluetoothGattCallback?) {
        if (!scanning) {
            startScan()
            if (!scanning) return
        }
        device.connect(false, object : BluetoothGattCallback() {
            override fun onDeviceConnected(device: BluetoothDevice) {
                stopScan()
            }
        })
        callback?.let { device.addBluetoothGattCallback(it) }
    }

    override fun onScanResult(callbackType: Int, result: ScanResult) {
        if (scanFilters.size == 0
                || scanFilters.any { it.test(result) }) {
            Timber.e(result.device.address)
            scanResults.onNext(result)
        }
    }

    override fun onScanFailed(errorCode: Int) {
        Timber.e("Scan failed $errorCode")
        scanning = when (errorCode) {
            SCAN_FAILED_ALREADY_STARTED -> true
            else -> false
        }
    }

    override fun onBatchScanResults(results: MutableList<ScanResult>) {
        results.forEach { onScanResult(CALLBACK_TYPE_ALL_MATCHES, it) }
    }

    class BluetoothAdapterMissingException : Exception()
    class BleNotSupportedException : Exception()
}
