package com.aconno.sensorics.device

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattCharacteristic.*
import android.bluetooth.BluetoothGattDescriptor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BluetoothGattAttributeValueConverter @Inject constructor() {

    companion object {
        /**
         * Characteristic value format type float (32-bit float)
         */
        private const val FORMAT_STRING = 0x36
        /**
         * Characteristic value format type float (32-bit float)
         */
        private const val FORMAT_BYTE = 0x38
    }

    private fun getType(type: String): Int {

        return when (type) {
            "UINT8" -> FORMAT_UINT8
            "UINT16" -> FORMAT_UINT16
            "UINT32" -> FORMAT_UINT32
            "SINT8" -> FORMAT_SINT8
            "SINT16" -> FORMAT_SINT16
            "SINT32" -> FORMAT_SINT32
            "SFLOAT" -> FORMAT_SFLOAT
            "FLOAT" -> FORMAT_FLOAT
            "STRING" -> FORMAT_STRING
            "BYTE" -> FORMAT_BYTE
            else -> BluetoothGattCharacteristic.FORMAT_UINT8
        }
    }

    /**
     * @value will not be converted to ByteArray or String or Float or Int. Only it will be casted.
     */
    fun setValue(characteristic: BluetoothGattCharacteristic, type: String, value: Any) {

        @Suppress("DEPRECATION")
        when (val realType = getType(type)) {
             FORMAT_BYTE ->characteristic.value = value as ByteArray
            FORMAT_STRING -> characteristic.setValue(value as String)
            FORMAT_UINT8, FORMAT_UINT16, FORMAT_UINT32, FORMAT_SINT8, FORMAT_SINT16, FORMAT_SINT32 -> characteristic.setValue(
                value as Int,
                realType,
                0
            )
            FORMAT_SFLOAT, FORMAT_FLOAT -> {
                val pair = getMantissaAndExp(value as Float)
                characteristic.setValue(pair.first, pair.second, realType, 0)
            }
            else -> throw IllegalArgumentException("$type Format is not supported.")
        }
    }

    /**
     * @value will not be converted to ByteArray or String or Float or Int. Only it will be casted.
     */
    fun setValue(descriptor: BluetoothGattDescriptor, type: String, value: Any) {

        val realType = getType(type)

        when (realType) {
            FORMAT_BYTE -> descriptor.value = value as ByteArray
            else -> throw IllegalArgumentException("$type Format is not supported.")
        }
    }

    private fun getMantissaAndExp(value: Float): Pair<Int, Int> {
        //http://s-j.github.io/java-float/
        val bits = java.lang.Float.floatToIntBits(value)
        val exp = (bits.ushr(23) and (1 shl 9) - 1) - ((1 shl 7) - 1)
        val mantissa = bits and (1 shl 23) - 1

        return Pair(mantissa, exp)
    }
}