package com.aconno.sensorics

import android.app.Service
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattService
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.aconno.sensorics.device.bluetooth.BluetoothGattCallback
import com.aconno.sensorics.domain.model.GattCallbackPayload
import com.aconno.sensorics.domain.scanning.Bluetooth
import io.reactivex.Flowable
import timber.log.Timber
import java.util.*
import javax.inject.Inject


class BluetoothConnectService : Service() {

    @Inject
    lateinit var bluetooth: Bluetooth


    private val mBinder = LocalBinder()

    override fun onCreate() {
        super.onCreate()
        (applicationContext as SensoricsApplication).appComponent.inject(this)
    }

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

    fun readCharacteristic(serviceUUID: UUID, characteristicUUID: UUID): Boolean {
        return bluetooth.readCharacteristic(serviceUUID, characteristicUUID)
    }

    fun writeCharacteristic(
        serviceUUID: UUID,
        characteristicUUID: UUID,
        byteArray: ByteArray
    ): Boolean {
        return bluetooth.writeCharacteristic(serviceUUID, characteristicUUID, byteArray)
    }

    fun getConnectResults(): Flowable<GattCallbackPayload> {
        return bluetooth.getGattResults()
    }

    fun connect(deviceAddress: String) {
        bluetooth.connect(deviceAddress)
    }

    override fun onBind(intent: Intent?): IBinder {
        return mBinder
    }
}