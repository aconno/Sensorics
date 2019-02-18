package com.aconno.bluetooth.testing

import java.util.*

interface BluetoothGattService {
    val characteristics: MutableList<BluetoothGattCharacteristic>
    val includedServices: MutableList<BluetoothGattService>

    val instanceId: Int
    val uuid: UUID
    val type: Int

}