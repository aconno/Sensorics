package com.aconno.sensorics

import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.aconno.sensorics.device.bluetooth.BluetoothGattCallback
import com.aconno.sensorics.domain.model.GattCallbackPayload
import com.aconno.sensorics.domain.scanning.Bluetooth
import com.aconno.sensorics.ui.devicecon.WriteCommand
import dagger.android.DaggerService
import io.reactivex.Flowable
import java.util.*
import javax.inject.Inject


class BluetoothConnectService : DaggerService() {

    @Inject
    lateinit var bluetooth: Bluetooth


    private val mBinder = LocalBinder()

    private val writeCommandQueue: Queue<WriteCommand> = ArrayDeque<WriteCommand>()
    private var isConnected = false

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
    ) {
        offerACommandToQueue(WriteCommand(serviceUUID, characteristicUUID, type, value))
    }

    private fun offerACommandToQueue(writeCommand: WriteCommand) {
        writeCommandQueue.offer(writeCommand)

        if (writeCommandQueue.size == 1) {
            writeCommandToDevice(writeCommand)
        }
    }

    fun getConnectResults(): Flowable<GattCallbackPayload> {
        return bluetooth.getGattResults().doOnNext {
            if (it.action == BluetoothGattCallback.ACTION_GATT_SERVICES_DISCOVERED) {
                isConnected = true
            } else if (it.action == BluetoothGattCallback.ACTION_GATT_CHAR_WRITE) {
                //remove the first one
                writeCommandQueue.poll()

                //write next one
                writeCommandQueue.peek()?.let { writeCommand ->
                    writeCommandToDevice(writeCommand)
                }
            }
        }
    }

    private fun writeCommandToDevice(writeCommand: WriteCommand) =
        if (isConnected) {
            bluetooth.writeCharacteristic(
                writeCommand.serviceUUID,
                writeCommand.charUUID,
                writeCommand.type,
                writeCommand.value
            )
        } else {
            false
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