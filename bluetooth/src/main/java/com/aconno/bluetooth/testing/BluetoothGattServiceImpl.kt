package com.aconno.bluetooth.testing

import java.util.*


class BluetoothGattServiceImpl(
        val service: android.bluetooth.BluetoothGattService
): BluetoothGattService {
    override val characteristics: MutableList<BluetoothGattCharacteristic> = service.characteristics.map { BluetoothGattCharacteristicImpl(it, this) }.toMutableList()
    override val includedServices: MutableList<BluetoothGattService> = service.includedServices.map { BluetoothGattServiceImpl(it) }.toMutableList()

    override val instanceId: Int = service.instanceId
    override val uuid: UUID = service.uuid

    override val type: Int = service.type
}