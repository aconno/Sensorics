package com.aconno.sensorics.service

import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.aconno.sensorics.domain.model.GattCallbackPayload
import com.aconno.sensorics.domain.scanning.Bluetooth
import dagger.android.DaggerService
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import timber.log.Timber
import java.util.*
import javax.inject.Inject


class BluetoothConnectService : DaggerService() {

    @Inject
    lateinit var bluetooth: Bluetooth

    private val connectResultsLiveData = MutableLiveData<GattCallbackPayload>()

    private var connectResultsDisposable: Disposable? = null

    private val mBinder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): BluetoothConnectService {
            return this@BluetoothConnectService
        }
    }

    override fun onUnbind(intent: Intent): Boolean {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        bluetooth.closeConnection()
        return super.onUnbind(intent)
    }

    fun writeCharacteristic(
        serviceUUID: UUID,
        characteristicUUID: UUID,
        type: String,
        value: Any
    ): Boolean {
        return bluetooth.writeCharacteristic(serviceUUID, characteristicUUID, type, value)
    }

    fun enableNotifications(characteristicUUID: UUID, serviceUUID: UUID, isEnabled: Boolean) {
        bluetooth.enableCharacteristicNotification(
            characteristicUUID = characteristicUUID,
            serviceUUID = serviceUUID,
            isEnabled = isEnabled
        )
    }

    /**
     * LiveData can drop some results if they come quickly
     */
    fun getConnectResultsLiveData(): LiveData<GattCallbackPayload> = connectResultsLiveData

    fun startConnectionStream() {
        connectResultsDisposable = bluetooth.getGattResults()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                connectResultsLiveData.postValue(it)
            }, {
                Timber.e(it)
            })
    }

    fun getConnectResults(): Flowable<GattCallbackPayload> {
        return bluetooth.getGattResults()
    }

    fun connect(deviceAddress: String) {
        bluetooth.connect(deviceAddress)
    }

    fun disconnect() {
        bluetooth.disconnect()
    }

    fun close() {
        bluetooth.closeConnection()
    }

    override fun onBind(intent: Intent?): IBinder {
        return mBinder
    }

    override fun onDestroy() {
        connectResultsDisposable?.dispose()
        super.onDestroy()
    }
}