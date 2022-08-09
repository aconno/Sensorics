package com.aconno.sensorics.domain.serialization

import com.aconno.sensorics.domain.format.ByteFormat
import com.aconno.sensorics.domain.format.SupportedTypes
import java.nio.ByteBuffer

class DeserializerImpl : Deserializer {
    override fun deserializeNumber(rawData: ByteArray, byteFormat: ByteFormat): Number {
        return when (byteFormat.dataType) {
            SupportedTypes.BYTE -> deserializeByte(rawData, byteFormat)
            SupportedTypes.UNSIGNED_BYTE -> deserializeUnsignedByte(rawData, byteFormat)
            SupportedTypes.SHORT -> deserializeShort(rawData, byteFormat)
            SupportedTypes.UNSIGNED_SHORT -> deserializeUnsignedShort(rawData, byteFormat)
            SupportedTypes.INT -> deserializeInt(rawData, byteFormat)
            SupportedTypes.UNSIGNED_INT -> deserializeUnsignedInt(rawData, byteFormat)
            SupportedTypes.LONG -> deserializeLong(rawData, byteFormat)
            SupportedTypes.FLOAT -> deserializeFloat(rawData, byteFormat)
            else -> throw IllegalArgumentException("Data type not supported: ${byteFormat.dataType}")
        }
    }

    private fun deserializeByte(rawData: ByteArray, byteFormat: ByteFormat): Byte {
        val bytes =
            rawData.copyOfRange(byteFormat.startIndexInclusive, byteFormat.endIndexExclusive)
        return bytes[0]
    }

    private fun deserializeShort(rawData: ByteArray, byteFormat: ByteFormat): Short {
        val bytes =
            rawData.copyOfRange(byteFormat.startIndexInclusive, byteFormat.endIndexExclusive)
        if (byteFormat.isReversed) {
            bytes.reverse()
        }
        val byteBuffer = ByteBuffer.wrap(bytes)
        return byteBuffer.getShort(0)
    }

    private fun deserializeUnsignedShort(rawData: ByteArray, byteFormat: ByteFormat): Int {
        val bytes =
            rawData.copyOfRange(byteFormat.startIndexInclusive, byteFormat.endIndexExclusive)
        if (byteFormat.isReversed) {
            bytes.reverse()
        }
        val byteBuffer = ByteBuffer.wrap(bytes)
        val signed = byteBuffer.getShort(0)
        return if (signed < 0) {
            signed.toInt() + 65536
        } else {
            signed.toInt()
        }
    }

    private fun deserializeFloat(rawData: ByteArray, byteFormat: ByteFormat): Float {
        val bytes =
            rawData.copyOfRange(byteFormat.startIndexInclusive, byteFormat.endIndexExclusive)
        if (byteFormat.isReversed) {
            bytes.reverse()
        }
        val byteBuffer = ByteBuffer.wrap(bytes)
        return byteBuffer.getFloat(0)
    }

    private fun deserializeUnsignedByte(rawData: ByteArray, byteFormat: ByteFormat): Short {
        val bytes =
            rawData.copyOfRange(byteFormat.startIndexInclusive, byteFormat.endIndexExclusive)
        return if(bytes[0] < 0) {
            (bytes[0] + 256).toShort()
        } else {
            bytes[0].toShort()
        }
    }

    private fun deserializeInt(rawData: ByteArray, byteFormat: ByteFormat): Int {
        val bytes =
            rawData.copyOfRange(byteFormat.startIndexInclusive, byteFormat.endIndexExclusive)
        if (byteFormat.isReversed) {
            bytes.reverse()
        }
        val byteBuffer = ByteBuffer.wrap(bytes)
        return byteBuffer.int
    }

    private fun deserializeUnsignedInt(rawData: ByteArray, byteFormat: ByteFormat): Long {
        val bytes =
            rawData.copyOfRange(byteFormat.startIndexInclusive, byteFormat.endIndexExclusive)
        if (byteFormat.isReversed) {
            bytes.reverse()
        }
        val byteBuffer = ByteBuffer.wrap(bytes)
        val signed = byteBuffer.int
        return if (signed < 0) {
            signed + (1.toLong() shl 32).toLong()
        } else {
            signed.toLong()
        }
    }

    private fun deserializeLong(rawData: ByteArray, byteFormat: ByteFormat): Long {
        val bytes =
            rawData.copyOfRange(byteFormat.startIndexInclusive, byteFormat.endIndexExclusive)
        if (byteFormat.isReversed) {
            bytes.reverse()
        }
        val byteBuffer = ByteBuffer.wrap(bytes)
        return byteBuffer.getLong()
    }

}