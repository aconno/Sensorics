package com.aconno.bluetooth.tasks

import android.bluetooth.BluetoothGattCharacteristic
import com.aconno.bluetooth.ReadCallback
import java.util.*

abstract class ReadTask(
    characteristic: UUID,
    override var retriesAllowed: Int = RETRIES_ALLOWED
) : Task(characteristic) {
    constructor(
        characteristic: BluetoothGattCharacteristic,
        callback: ReadCallback,
        retriesAllowed: Int = RETRIES_ALLOWED
    ) : this(characteristic.uuid, retriesAllowed = retriesAllowed)

    abstract fun onSuccess(value: ByteArray)
}
