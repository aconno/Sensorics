package com.aconno.bluetooth.testing

import java.util.*

interface BluetoothGattCharacteristic {
    val service: BluetoothGattService
    val descriptors: List<BluetoothGattDescriptor>
    val instanceId: Int
    val permissions: Int
    val properties: Int
    val uuid: UUID
    var value: ByteArray
    var writeType: Int
    fun write(value: ByteArray? = null)
}