package com.aconno.bluetooth.tasks

import android.bluetooth.BluetoothGattCharacteristic
import java.util.*

abstract class CharacteristicWriteTask(
    override var characteristic: BluetoothGattCharacteristic? = null,
    override val characteristicUUID: UUID = characteristic!!.uuid,
    val value: ByteArray,
    val totalBytes: Int = value.size,
    val bytesWritten: Int = 0
) : CharacteristicTask(characteristic, characteristicUUID) {
    abstract fun onSuccess()
}