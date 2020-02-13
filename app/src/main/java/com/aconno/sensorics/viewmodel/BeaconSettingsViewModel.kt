package com.aconno.sensorics.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aconno.sensorics.device.beacon.Beacon
import com.aconno.sensorics.device.bluetooth.BluetoothGattCallback
import com.aconno.sensorics.device.bluetooth.tasks.GenericExecutableTask
import com.aconno.sensorics.device.bluetooth.tasks.lock.LockStateRequestCallback
import com.aconno.sensorics.domain.model.GattCallbackPayload
import com.aconno.sensorics.domain.scanning.Bluetooth
import com.aconno.sensorics.model.mapper.WebViewAppBeaconMapper
import com.google.gson.Gson
import com.google.gson.JsonParser
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import org.apache.commons.text.StringEscapeUtils
import timber.log.Timber

sealed class BeaconSettingsState {
    object Connecting : BeaconSettingsState()
    object Connected : BeaconSettingsState()
    object Disconnected : BeaconSettingsState()
    object RequireUnlock : BeaconSettingsState()
    object Unlocking : BeaconSettingsState()
    object Unlocked : BeaconSettingsState()
    object Reading : BeaconSettingsState()
    object Writing : BeaconSettingsState()
    object SettingsWritten : BeaconSettingsState()
    object Done : BeaconSettingsState()
    object ErrorOccurred : BeaconSettingsState()
}

data class BeaconData(val beaconJson: String, val slotCount: Int)

class BeaconSettingsViewModel(
    private val bluetooth: Bluetooth,
    private val beacon: Beacon,
    val webViewAppBeaconMapper: WebViewAppBeaconMapper
) : ViewModel() {

    var isFirstTime = true
        private set
        get() {
            val tmp = field
            field = false
            return tmp
        }
    private var resultsDisposable: Disposable? = null
    private val gson = Gson()
    private val _beaconLiveData = MutableLiveData<BeaconData>()
    private val _connectionResultEvent = MutableLiveData<BeaconSettingsState>()
    val connectionResultEvent: LiveData<BeaconSettingsState>
        get() = _connectionResultEvent
    val beaconLiveData: LiveData<BeaconData>
        get() = _beaconLiveData
    var isConnected = false
        private set

    init {
        resultsDisposable = bluetooth.getGattResults()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                watchPayload(it)
                beacon.taskProcessor.accept(it)
            }, {
                Timber.e(it)
                _connectionResultEvent.value = BeaconSettingsState.ErrorOccurred
            })
    }

    private fun watchPayload(payload: GattCallbackPayload) {
        when (payload.action) {
            BluetoothGattCallback.ACTION_GATT_CONNECTED -> {
                Timber.d("Connected to GATT")
                bluetooth.requestMtu(512)
                _connectionResultEvent.value =
                    BeaconSettingsState.Connected
                isConnected = true

                beacon.requestDeviceLockStatus(deviceLockStatusCallback)
            }
            BluetoothGattCallback.ACTION_GATT_DISCONNECTED -> {
                _connectionResultEvent.value =
                    BeaconSettingsState.Disconnected
                _beaconLiveData.value = null
                isConnected = false
            }
        }
    }

    fun connect(deviceMac: String) {
        _connectionResultEvent.value =
            BeaconSettingsState.Connecting
        bluetooth.connect(deviceMac)
    }

    fun disconnect() {
        bluetooth.disconnect()
    }

    fun unlockBeacon(password: String) {
        beacon.unlock(password, deviceLockStatusCallback)
        _connectionResultEvent.value =
            BeaconSettingsState.Unlocking
    }

    fun writeSettingsToDevice() {
        _connectionResultEvent.value = BeaconSettingsState.Writing
        beacon.write(true, object : GenericExecutableTask() {
            override fun onSuccess() {
                _connectionResultEvent.value = BeaconSettingsState.SettingsWritten
            }
        })
    }

    fun beaconJsonUpdated(updatedJson: String) {
        beacon.loadChangesFromJson(JsonParser().parse(updatedJson).asJsonObject)
        webViewAppBeaconMapper.restoreAdContent(beacon)
        val newEscapedJson = StringEscapeUtils.escapeJson(gson.toJson(beacon.toJson()))
        _beaconLiveData.value = BeaconData(newEscapedJson, beacon.slotCount.toInt())
        _connectionResultEvent.value = BeaconSettingsState.Done
    }

    private val deviceLockStatusCallback = object : LockStateRequestCallback {
        override fun onDeviceLockStateRead(unlocked: Boolean) {
            if (unlocked) {
                _connectionResultEvent.value =
                    BeaconSettingsState.Unlocked
                beacon.read(readCompleteCallback)
                _connectionResultEvent.value =
                    BeaconSettingsState.Reading
            } else {
                _connectionResultEvent.value =
                    BeaconSettingsState.RequireUnlock
            }
        }

        override fun onError(e: Exception) {
            _connectionResultEvent.value =
                BeaconSettingsState.ErrorOccurred
        }
    }

    private val readCompleteCallback = object : GenericExecutableTask("On Read Completed Task") {
        override fun onSuccess() {
            webViewAppBeaconMapper.prepareAdContentForWebView(beacon)
            val beaconJs = StringEscapeUtils.escapeJson(gson.toJson(beacon.toJson()))
            _beaconLiveData.value = BeaconData(beaconJs, beacon.slotCount.toInt())
            _connectionResultEvent.value = BeaconSettingsState.Done
            webViewAppBeaconMapper.restoreAdContent(beacon)
        }
    }

    override fun onCleared() {
        super.onCleared()
        resultsDisposable?.dispose()
    }
}