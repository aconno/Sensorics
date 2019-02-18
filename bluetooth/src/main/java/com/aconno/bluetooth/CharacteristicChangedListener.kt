package com.aconno.bluetooth

import android.bluetooth.BluetoothGattCharacteristic


interface CharacteristicChangedListener {
    fun onCharacteristicChanged(characteristic: BluetoothGattCharacteristic, value: ByteArray)
}