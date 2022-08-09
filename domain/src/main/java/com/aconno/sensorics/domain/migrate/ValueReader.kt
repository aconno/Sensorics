package com.aconno.sensorics.domain.migrate

import com.aconno.sensorics.domain.migrate.ValueConverters.Companion.ASCII_STRING
import com.aconno.sensorics.domain.migrate.ValueConverters.Companion.BOOLEAN
import com.aconno.sensorics.domain.migrate.ValueConverters.Companion.BYTE
import com.aconno.sensorics.domain.migrate.ValueConverters.Companion.ENUM
import com.aconno.sensorics.domain.migrate.ValueConverters.Companion.FLOAT
import com.aconno.sensorics.domain.migrate.ValueConverters.Companion.INT16
import com.aconno.sensorics.domain.migrate.ValueConverters.Companion.INT32
import com.aconno.sensorics.domain.migrate.ValueConverters.Companion.INT64
import com.aconno.sensorics.domain.migrate.ValueConverters.Companion.INT8
import com.aconno.sensorics.domain.migrate.ValueConverters.Companion.MAC_ADDRESS
import com.aconno.sensorics.domain.migrate.ValueConverters.Companion.TIME
import com.aconno.sensorics.domain.migrate.ValueConverters.Companion.UINT16
import com.aconno.sensorics.domain.migrate.ValueConverters.Companion.UINT32
import com.aconno.sensorics.domain.migrate.ValueConverters.Companion.UINT8
import java.nio.ByteOrder

interface ValueReader {
    var currentIndex: Int

    fun <T> read(type: ValueConverterBase<T>, length: Int = type.length, order: ByteOrder = ByteOrder.LITTLE_ENDIAN): T

    fun readBoolean(): Boolean =
            read(BOOLEAN)

    fun readByte(): Byte =
            read(BYTE)

    fun readMac(order: ByteOrder = ByteOrder.LITTLE_ENDIAN): String =
            read(MAC_ADDRESS, order = order)

    fun readInt8(): Byte =
            read(INT8)

    fun readUInt8(): Short =
            read(UINT8)

    fun readInt16(order: ByteOrder = ByteOrder.LITTLE_ENDIAN): Short =
            read(INT16, order = order)

    fun readUInt16(order: ByteOrder = ByteOrder.LITTLE_ENDIAN): Int =
            read(UINT16, order = order)

    fun readInt32(order: ByteOrder = ByteOrder.LITTLE_ENDIAN): Int =
            read(INT32, order = order)

    fun readUInt32(order: ByteOrder = ByteOrder.LITTLE_ENDIAN): Long =
            read(UINT32, order = order)

    fun readInt64(order: ByteOrder = ByteOrder.LITTLE_ENDIAN): Long =
            read(INT64, order = order)

    fun readFloat(order: ByteOrder = ByteOrder.LITTLE_ENDIAN): Float =
            read(FLOAT, order = order)

    fun readAsciiString(length: Int = ASCII_STRING.length,
                        order: ByteOrder = ByteOrder.BIG_ENDIAN): String =
            read(ASCII_STRING, length = length, order = order)

    fun readTime(order: ByteOrder = ByteOrder.LITTLE_ENDIAN): Long =
            read(TIME, order = order)

    fun readEnumSelectedIndex(order: ByteOrder = ByteOrder.LITTLE_ENDIAN): Long =
            read(ENUM, order = order)

    fun readBytes(length: Int): ByteArray
}

class ValueReaderImpl(val data: ByteArray) : ValueReader {
    override var currentIndex: Int = 0

    override fun <T> read(type: ValueConverterBase<T>, length: Int, order: ByteOrder): T {
        return if (length == -1) {
            data.stringLength(currentIndex)
        } else {
            length
        }.let { dataLength ->
            type.deserialize(data, currentIndex, dataLength, order = order).also {
                currentIndex += dataLength
            }
        }
    }

    override fun readBytes(length: Int): ByteArray {
        return data.copyOfRange(currentIndex, currentIndex + length).also {
            currentIndex += length
        }
    }
}