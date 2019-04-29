package com.aconno.sensorics.ui

import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.ui.connect.BluetoothServiceConnection
import java.util.*

interface Connectable {
    fun connect(device: Device)
    fun disconnect()
    fun writeCharacteristic(
        serviceUUID: UUID,
        characteristicUUID: UUID,
        type: String,
        value: Any
    )

    fun isConnectedOrConnecting(): Boolean
    fun shutDownConnectionService()

    fun registerConnectionCallback(connectionCallback: BluetoothServiceConnection.ConnectionCallback)
    fun unRegisterConnectionCallback(connectionCallback: BluetoothServiceConnection.ConnectionCallback)
}
