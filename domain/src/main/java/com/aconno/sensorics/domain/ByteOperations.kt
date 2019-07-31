package com.aconno.sensorics.domain

import com.aconno.sensorics.domain.ByteOperations.NextByteType.*

object ByteOperations {

    fun isolateMsd(bytes: ByteArray): ByteArray {
        try {
            var i = 0
            while (i < bytes.size) {
                val length = bytes[i].toInt()
                val type = bytes[i + 1].toInt()

                if (type == -1) {
                    return bytes.copyOfRange(i + 2, i + length + 1)
                }

                if (i + length > bytes.size) {
                    return bytes
                }

                i += length
                i++
            }

            return bytes
        } catch (ex: Exception) {
            return bytes
        }
    }

    fun isolateAdvertisementTypes(bytes: ByteArray): Map<Byte, ByteArray> {
        var nextByteType = LENGTH
        var bytesLeftInType = 0
        var currentType: Byte = 0
        val map = mutableMapOf<Byte, MutableList<Byte>>()

        bytes.forEachIndexed { i, byte ->
            when(nextByteType) {
                LENGTH -> {
                    bytesLeftInType = byte.toInt()
                    if (bytesLeftInType > 0x00) {
                        nextByteType = TYPE
                    }
                }
                TYPE -> {
                    currentType = byte

                    map.getOrPut(currentType, {
                        mutableListOf()
                    })

                    nextByteType = DATA

                    bytesLeftInType--
                    if(bytesLeftInType == 0x00) {
                        nextByteType = LENGTH
                    }
                }
                DATA -> {
                    map[currentType]?.add(byte)

                    bytesLeftInType--
                    if(bytesLeftInType == 0x00) {
                        nextByteType = LENGTH
                    }
                }
            }
        }

        return map.mapValues { entry ->
            entry.value.toByteArray()
        }
    }

    enum class NextByteType{
        LENGTH,TYPE,DATA
    }
}