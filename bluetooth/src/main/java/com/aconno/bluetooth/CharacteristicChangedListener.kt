package com.aconno.bluetooth

import android.bluetooth.BluetoothGattCharacteristic
import java.util.*


interface CharacteristicChangedListener {
    fun onCharacteristicChanged(characteristic: BluetoothGattCharacteristic, value: ByteArray)
}