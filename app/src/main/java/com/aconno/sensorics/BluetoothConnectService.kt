package com.aconno.sensorics

import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.aconno.sensorics.domain.model.GattCallbackPayload
import com.aconno.sensorics.domain.scanning.Bluetooth
import dagger.android.DaggerService
import io.reactivex.Flowable
import java.util.*
import javax.inject.Inject


class BluetoothConnectService : DaggerService() {

    @Inject
    lateinit var bluetooth: Bluetooth


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
}