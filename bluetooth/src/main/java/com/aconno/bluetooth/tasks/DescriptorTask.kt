package com.aconno.bluetooth.tasks

import android.bluetooth.BluetoothGattDescriptor
import java.util.*

abstract class DescriptorTask(
    open var descriptor: BluetoothGattDescriptor?,
    open val descriptorUUID: UUID = descriptor!!.uuid,
    open val characteristicUUID: UUID = descriptor!!.characteristic.uuid
) : Task()