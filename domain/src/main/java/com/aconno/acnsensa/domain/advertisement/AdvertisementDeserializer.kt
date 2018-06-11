package com.aconno.acnsensa.domain.advertisement

import com.aconno.acnsensa.domain.format.AdvertisementFormat
import com.aconno.acnsensa.domain.format.ByteFormat
import com.aconno.acnsensa.domain.format.SupportedTypes
import com.aconno.acnsensa.domain.model.Advertisement
import java.nio.ByteBuffer

interface AdvertisementDeserializer {

    fun deserialize(advertisement: Advertisement): Map<String, Number>

    fun getValue(
        advertisement: Advertisement,
        advertisementFormat: AdvertisementFormat,
        propertyName: String
    ): Number {
        val bytes: List<Byte> = advertisement.rawData
        val propertyFormat = advertisementFormat.getFormat()[propertyName]

        if (propertyFormat == null) {
            throw NoSuchElementException("No propertyName $propertyName in the advertisement format.")
        } else {
            val byteBuffer: ByteBuffer = generateByteBuffer(bytes, propertyFormat)
            return getNumber(byteBuffer, propertyFormat.dataType)
        }
    }

    private fun generateByteBuffer(
        rawBytes: List<Byte>,
        propertyFormat: ByteFormat
    ): ByteBuffer {
        var byteRange =
            rawBytes.subList(propertyFormat.startIndexInclusive, propertyFormat.endIndexExclusive)
        if (propertyFormat.isReversed) {
            byteRange = byteRange.reversed()
        }
        return ByteBuffer.wrap(byteRange.toByteArray())
    }

    private fun getNumber(byteBuffer: ByteBuffer, targetType: String): Number {
        return when (targetType) {
            SupportedTypes.FLOAT -> byteBuffer.getFloat(0)
            SupportedTypes.SHORT -> byteBuffer.getShort(0)
            SupportedTypes.UNSIGNED_SHORT -> deserializeUnsignedShort(byteBuffer)
            SupportedTypes.BYTE -> byteBuffer.get(0)
            else -> throw Exception("Invalid target Type: $targetType")
        }
    }

    private fun deserializeUnsignedShort(byteBuffer: ByteBuffer): Int {
        val size = byteBuffer.capacity()
        if (size == 2) {
            val signedValue = byteBuffer.short
            return if (signedValue < 0) {
                signedValue.toInt() + 65536
            } else {
                signedValue.toInt()
            }
        }
        throw IllegalArgumentException("Invalid size for unsigned short: $size")
    }
}