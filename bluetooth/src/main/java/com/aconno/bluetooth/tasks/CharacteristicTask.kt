package com.aconno.bluetooth.tasks

import android.bluetooth.BluetoothGattCharacteristic
import java.util.*

abstract class CharacteristicTask(
    open var characteristic: BluetoothGattCharacteristic?,
    open val characteristicUUID: UUID = characteristic!!.uuid
) : Task()
