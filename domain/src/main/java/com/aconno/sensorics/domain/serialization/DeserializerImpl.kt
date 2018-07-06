package com.aconno.sensorics.domain.serialization

import com.aconno.sensorics.domain.format.ByteFormat
import com.aconno.sensorics.domain.format.SupportedTypes
import java.nio.ByteBuffer

class DeserializerImpl : Deserializer {

    override fun deserializeNumber(rawData: List<Byte>, byteFormat: ByteFormat): Number {
        return when (byteFormat.dataType) {
            SupportedTypes.BYTE -> deserializeByte(rawData, byteFormat)
            SupportedTypes.SHORT -> deserializeShort(rawData, byteFormat)
            SupportedTypes.UNSIGNED_SHORT -> deserializeUnsignedShort(rawData, byteFormat)
            SupportedTypes.FLOAT -> deserializeFloat(rawData, byteFormat)
            else -> throw IllegalArgumentException("Data type not supported: ${byteFormat.dataType}")
        }
    }

    private fun deserializeByte(rawData: List<Byte>, byteFormat: ByteFormat): Byte {
        val bytes = rawData.subList(byteFormat.startIndexInclusive, byteFormat.endIndexExclusive)
        return bytes[0]
    }

    private fun deserializeShort(rawData: List<Byte>, byteFormat: ByteFormat): Short {
        var bytes = rawData.subList(byteFormat.startIndexInclusive, byteFormat.endIndexExclusive)
        if (byteFormat.isReversed) {
            bytes = bytes.reversed()
        }
        val byteBuffer = ByteBuffer.wrap(bytes.toByteArray())
        return byteBuffer.getShort(0)
    }

    private fun deserializeUnsignedShort(rawData: List<Byte>, byteFormat: ByteFormat): Int {
        var bytes = rawData.subList(byteFormat.startIndexInclusive, byteFormat.endIndexExclusive)
        if (byteFormat.isReversed) {
            bytes = bytes.reversed()
        }
        val byteBuffer = ByteBuffer.wrap(bytes.toByteArray())
        val signed = byteBuffer.getShort(0)
        if (signed < 0) {
            return signed.toInt() + 65536
        } else {
            return signed.toInt()
        }
    }

    private fun deserializeFloat(rawData: List<Byte>, byteFormat: ByteFormat): Float {
        var bytes = rawData.subList(byteFormat.startIndexInclusive, byteFormat.endIndexExclusive)
        if (byteFormat.isReversed) {
            bytes = bytes.reversed()
        }
        val byteBuffer = ByteBuffer.wrap(bytes.toByteArray())
        return byteBuffer.getFloat(0)
    }
}