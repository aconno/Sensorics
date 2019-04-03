package com.aconno.bluetooth.testing

import java.util.*

interface BluetoothGattDescriptor {
    val characteristic: BluetoothGattCharacteristic
    val permissions: Int
    val uuid: UUID
    var value: ByteArray
}