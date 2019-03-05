package com.aconno.bluetooth.tasks

import android.bluetooth.BluetoothGattCharacteristic
import java.util.*

abstract class CharacteristicReadTask(
    override var characteristic: BluetoothGattCharacteristic? = null,
    override val characteristicUUID: UUID = characteristic!!.uuid
) : CharacteristicTask(characteristic, characteristicUUID) {
    abstract fun onSuccess(value: ByteArray)
}
