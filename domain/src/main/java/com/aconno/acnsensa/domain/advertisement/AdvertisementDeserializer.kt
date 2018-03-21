package com.aconno.acnsensa.domain.advertisement

import com.aconno.acnsensa.domain.format.AdvertisementFormat
import com.aconno.acnsensa.domain.format.SupportedTypes
import com.aconno.acnsensa.domain.model.Advertisement
import java.nio.ByteBuffer

/**
 * @author aconno
 */
interface AdvertisementDeserializer {
    fun deserialize(advertisement: Advertisement): Map<String, Number>

    fun getValue(
        advertisement: Advertisement,
        advertisementFormat: AdvertisementFormat,
        type: String
    ): Number {
        val bytes: List<Byte> = advertisement.rawData
        val byteBuffer: ByteBuffer = generateByteBuffer(bytes, advertisementFormat, type)

        val valFormat = advertisementFormat.getFormat()[type]
        valFormat?.let {
            return getNumber(byteBuffer, valFormat.targetType)
        }

        throw NoSuchElementException("No type $type in the advertisement format.")

    }

    private fun generateByteBuffer(
        rawBytes: List<Byte>,
        advertisementFormat: AdvertisementFormat,
        type: String
    ): ByteBuffer {
        val byteFormat = advertisementFormat.getFormat()
        val format = byteFormat[type]
        format?.let {
            var byteRange = rawBytes.subList(format.startIndexInclusive, format.endIndexExclusive)
            if (format.isReversed) {
                byteRange = byteRange.reversed()
            }
            return ByteBuffer.wrap(byteRange.toByteArray())
        }

        throw NoSuchElementException("No type $type in the advertisement format.")
    }

    private fun getNumber(byteBuffer: ByteBuffer, targetType: String): Number {
        return when (targetType) {
            SupportedTypes.FLOAT -> byteBuffer.getFloat(0)
            SupportedTypes.SHORT -> byteBuffer.getShort(0)
            SupportedTypes.BYTE -> byteBuffer.get(0)
            else -> throw Exception("Invalid target Type: $targetType")
        }
    }

}