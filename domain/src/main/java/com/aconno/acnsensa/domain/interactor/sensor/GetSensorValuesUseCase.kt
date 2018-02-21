package com.aconno.acnsensa.domain.interactor.sensor

import com.aconno.acnsensa.domain.interactor.type.SingleUseCaseWithParameter
import com.aconno.acnsensa.domain.model.Advertisement
import com.aconno.acnsensa.domain.model.ScanResult
import io.reactivex.Single
import java.nio.ByteBuffer


class GetSensorValuesUseCase(private val advertisementMatcher: AdvertisementMatcher) :
    SingleUseCaseWithParameter<Map<String, Number>, ScanResult> {

    override fun execute(parameter: ScanResult): Single<Map<String, Number>> =
        Single.just(parameter).map { toSensorValues(it) }

    private fun toSensorValues(scanResult: ScanResult): Map<String, Number> {
        val advertisement: Advertisement = scanResult.advertisement

        val advertisementFormat: AdvertisementFormat =
            advertisementMatcher.matchAdvertisementToFormat(advertisement)
        val advertisementDeserializer: AdvertisementDeserializer =
            getAdvertisementDeserializer(advertisementFormat)


        return advertisementDeserializer.deserialize(advertisement)
    }

    private fun getAdvertisementDeserializer(advertisementFormat: AdvertisementFormat): AdvertisementDeserializer {
        return when (advertisementFormat) {
            is ScalarsAdvertisementFormat -> ScalarsAdvertisementDeserializer(advertisementFormat)
            is VectorsAdvertisementFormat -> VectorsAdvertisementDeserializer(advertisementFormat)
            else -> throw IllegalArgumentException("Invalid advertisement format")
        }
    }
}

class AdvertisementMatcher {
    private val supportedFormats: List<AdvertisementFormat> =
        listOf(ScalarsAdvertisementFormat(), VectorsAdvertisementFormat())

    fun matchAdvertisementToFormat(advertisement: Advertisement): AdvertisementFormat {
        val matchedFormats: MutableList<AdvertisementFormat> = ArrayList()
        for (format in supportedFormats) {
            val matches: Boolean = bytesMatchMask(
                advertisement.rawData,
                format.getRequiredFormat(),
                format.getMaskBytePositions()
            )
            if (matches) {
                matchedFormats.add(format)
            }
        }

        if (matchedFormats.size == 1) {
            return matchedFormats[0]
        }

        throw IllegalArgumentException("Advertisement must match only 1 format. Matched formats = ${matchedFormats.size}")

    }

    private fun bytesMatchMask(bytes: List<Byte>, target: List<Byte>, mask: List<Int>): Boolean {
        if (bytes.size < target.size) {
            return false
        } else if (bytes.size >= target.size) {
            val inputBytesArePadded =
                bytes.filterIndexed { index, _ -> index >= target.size }.all { it.toInt() == 0x00 }
            if (inputBytesArePadded) {
                val unpadded: List<Byte> = bytes.filterIndexed { index, _ -> index < target.size }
                val output: List<Byte> = unpadded.mapIndexed { position, inputByte ->
                    if (mask.contains(position)) {
                        inputByte
                    } else {
                        0

                    }
                }

                return output == target
            }
        }

        return false

    }
}

interface AdvertisementDeserializer {

    fun deserialize(advertisement: Advertisement): Map<String, Number>
}

class ScalarsAdvertisementDeserializer(private val scalarsAdvertisementFormat: ScalarsAdvertisementFormat) :
    AdvertisementDeserializer {

    override fun deserialize(advertisement: Advertisement): Map<String, Number> {
        val temperature = getValue(
            advertisement,
            scalarsAdvertisementFormat,
            ScalarsAdvertisementFormat.TEMPERATURE
        )
        val humidity =
            getValue(advertisement, scalarsAdvertisementFormat, ScalarsAdvertisementFormat.HUMIDITY)
        val pressure =
            getValue(advertisement, scalarsAdvertisementFormat, ScalarsAdvertisementFormat.PRESSURE)
        val light =
            getValue(advertisement, scalarsAdvertisementFormat, ScalarsAdvertisementFormat.LIGHT)

        return mapOf(
            Pair(ScalarsAdvertisementFormat.TEMPERATURE, temperature),
            Pair(ScalarsAdvertisementFormat.HUMIDITY, humidity),
            Pair(ScalarsAdvertisementFormat.PRESSURE, pressure),
            Pair(ScalarsAdvertisementFormat.LIGHT, light)
        )
    }
}

private fun getValue(
    advertisement: Advertisement,
    advertisementFormat: AdvertisementFormat,
    type: String
): Number {
    val bytes: List<Byte> = advertisement.rawData
    val byteBuffer: ByteBuffer = generateByteBuffer(bytes, advertisementFormat, type)

    return getNumber(byteBuffer, type)
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
            return ByteBuffer.wrap(byteRange.toByteArray())
        }
    }

    throw NoSuchElementException("No type $type in the advertisement format.")
}

private fun getNumber(byteBuffer: ByteBuffer, targetType: String): Number {
    return when (targetType) {
        SupportedTypes.FLOAT -> byteBuffer.getFloat(0)
        SupportedTypes.SHORT -> byteBuffer.getShort(0)
        SupportedTypes.BYTE -> byteBuffer.get(0)
        else -> throw Exception("WTF")
    }
}

class VectorsAdvertisementDeserializer(private val vectorsAdvertisementFormat: VectorsAdvertisementFormat) :
    AdvertisementDeserializer {

    override fun deserialize(advertisement: Advertisement): Map<String, Number> {
        val gyroscopeX = getValue(
            advertisement,
            vectorsAdvertisementFormat,
            VectorsAdvertisementFormat.GYROSCOPE_X
        )
        val gyroscopeY = getValue(
            advertisement,
            vectorsAdvertisementFormat,
            VectorsAdvertisementFormat.GYROSCOPE_Y
        )
        val gyroscopeZ = getValue(
            advertisement,
            vectorsAdvertisementFormat,
            VectorsAdvertisementFormat.GYROSCOPE_Z
        )

        val accelerometerX = getValue(
            advertisement,
            vectorsAdvertisementFormat,
            VectorsAdvertisementFormat.ACCELEROMETER_X
        )
        val accelerometerY = getValue(
            advertisement,
            vectorsAdvertisementFormat,
            VectorsAdvertisementFormat.ACCELEROMETER_Y
        )
        val accelerometerZ = getValue(
            advertisement,
            vectorsAdvertisementFormat,
            VectorsAdvertisementFormat.ACCELEROMETER_Z
        )

        val magnetometerX = getValue(
            advertisement,
            vectorsAdvertisementFormat,
            VectorsAdvertisementFormat.MAGNETOMETER_X
        )
        val magnetometerY = getValue(
            advertisement,
            vectorsAdvertisementFormat,
            VectorsAdvertisementFormat.MAGNETOMETER_Y
        )
        val magnetometerZ = getValue(
            advertisement,
            vectorsAdvertisementFormat,
            VectorsAdvertisementFormat.MAGNETOMETER_Z
        )

        return mapOf(
            Pair(VectorsAdvertisementFormat.GYROSCOPE_X, gyroscopeX),
            Pair(VectorsAdvertisementFormat.GYROSCOPE_Y, gyroscopeY),
            Pair(VectorsAdvertisementFormat.GYROSCOPE_Z, gyroscopeZ),
            Pair(VectorsAdvertisementFormat.ACCELEROMETER_X, accelerometerX),
            Pair(VectorsAdvertisementFormat.ACCELEROMETER_Y, accelerometerY),
            Pair(VectorsAdvertisementFormat.ACCELEROMETER_Z, accelerometerZ),
            Pair(VectorsAdvertisementFormat.MAGNETOMETER_X, magnetometerX),
            Pair(VectorsAdvertisementFormat.MAGNETOMETER_Y, magnetometerY),
            Pair(VectorsAdvertisementFormat.MAGNETOMETER_Z, magnetometerZ)
        )
    }
}

interface AdvertisementFormat {
    fun getFormat(): Map<String, ByteFormat>
    fun getRequiredFormat(): List<Byte>
    fun getMaskBytePositions(): List<Int>

}

class VectorsAdvertisementFormat : AcnSensaBaseFormat() {

    private val requiredFormat: List<Byte> = generateAcnSensaRequiredFormat(0x00)

    override fun getFormat(): Map<String, ByteFormat> {
        val baseFormat: Map<String, ByteFormat> = super.getFormat()

        val gyroscopeX = Pair(
            GYROSCOPE_X,
            ByteFormat(
                startIndexInclusive = 10,
                endIndexExclusive = 12,
                isReversed = true,
                targetType = SupportedTypes.SHORT
            )
        )
        val gyroscopeY = Pair(
            GYROSCOPE_Y,
            ByteFormat(
                startIndexInclusive = 12,
                endIndexExclusive = 14,
                isReversed = true,
                targetType = SupportedTypes.SHORT
            )
        )
        val gyroscopeZ = Pair(
            GYROSCOPE_Z,
            ByteFormat(
                startIndexInclusive = 14,
                endIndexExclusive = 16,
                isReversed = true,
                targetType = SupportedTypes.SHORT
            )
        )

        val accelerometerX = Pair(
            ACCELEROMETER_X,
            ByteFormat(
                startIndexInclusive = 16,
                endIndexExclusive = 18,
                isReversed = true,
                targetType = SupportedTypes.SHORT
            )
        )
        val accelerometerY = Pair(
            ACCELEROMETER_Y,
            ByteFormat(
                startIndexInclusive = 18,
                endIndexExclusive = 20,
                isReversed = true,
                targetType = SupportedTypes.SHORT
            )
        )
        val accelerometerZ = Pair(
            ACCELEROMETER_Z,
            ByteFormat(
                startIndexInclusive = 20,
                endIndexExclusive = 22,
                isReversed = true,
                targetType = SupportedTypes.SHORT
            )
        )

        val magnetometerX = Pair(
            MAGNETOMETER_X,
            ByteFormat(
                startIndexInclusive = 22,
                endIndexExclusive = 24,
                isReversed = true,
                targetType = SupportedTypes.SHORT
            )
        )
        val magnetometerY = Pair(
            MAGNETOMETER_Y,
            ByteFormat(
                startIndexInclusive = 24,
                endIndexExclusive = 26,
                isReversed = true,
                targetType = SupportedTypes.SHORT
            )
        )
        val magnetometerZ = Pair(
            MAGNETOMETER_Z,
            ByteFormat(
                startIndexInclusive = 26,
                endIndexExclusive = 28,
                isReversed = true,
                targetType = SupportedTypes.SHORT
            )
        )

        return baseFormat + mapOf(
            gyroscopeX,
            gyroscopeY,
            gyroscopeZ,
            accelerometerX,
            accelerometerY,
            accelerometerZ,
            magnetometerX,
            magnetometerY,
            magnetometerZ
        )
    }

    override fun getRequiredFormat(): List<Byte> = requiredFormat

    override fun getMaskBytePositions(): List<Int> = acnSensaMaskBytesPosition

    companion object {
        const val GYROSCOPE_X = "Gyroscope X"
        const val GYROSCOPE_Y = "Gyroscope Y"
        const val GYROSCOPE_Z = "Gyroscope Z"

        const val ACCELEROMETER_X = "Accelerometer X"
        const val ACCELEROMETER_Y = "Accelerometer Y"
        const val ACCELEROMETER_Z = "Accelerometer Z"

        const val MAGNETOMETER_X = "Magnetometer X"
        const val MAGNETOMETER_Y = "Magnetometer Y"
        const val MAGNETOMETER_Z = "Magnetometer Z"
    }
}

class ScalarsAdvertisementFormat : AcnSensaBaseFormat() {

    private val requiredFormat: List<Byte> = generateAcnSensaRequiredFormat(0x01)

    override fun getFormat(): Map<String, ByteFormat> {
        val baseFormat: Map<String, ByteFormat> = super.getFormat()

        val temperature = Pair(
            TEMPERATURE,
            ByteFormat(
                startIndexInclusive = 10,
                endIndexExclusive = 14,
                isReversed = true,
                targetType = SupportedTypes.FLOAT
            )
        )
        val humidity = Pair(
            HUMIDITY,
            ByteFormat(
                startIndexInclusive = 14,
                endIndexExclusive = 18,
                isReversed = true,
                targetType = SupportedTypes.FLOAT
            )
        )
        val pressure = Pair(
            PRESSURE,
            ByteFormat(
                startIndexInclusive = 18,
                endIndexExclusive = 22,
                isReversed = true,
                targetType = SupportedTypes.FLOAT
            )
        )
        val light = Pair(
            LIGHT,
            ByteFormat(
                startIndexInclusive = 22,
                endIndexExclusive = 26,
                isReversed = true,
                targetType = SupportedTypes.FLOAT
            )
        )

        return baseFormat + mapOf(temperature, humidity, pressure, light)
    }

    override fun getRequiredFormat(): List<Byte> = requiredFormat

    override fun getMaskBytePositions(): List<Int> = acnSensaMaskBytesPosition

    companion object {

        const val TEMPERATURE = "Temperature"
        const val HUMIDITY = "Humidity"
        const val PRESSURE = "Pressure"
        const val LIGHT = "Light"
    }
}

private val acnSensaMaskBytesPosition: List<Int> = (0..9).toList()
private fun generateAcnSensaRequiredFormat(type: Byte) = listOf(
    0x02,
    0x01,
    0x04,
    0x1A,
    0xFF.toByte(),
    0x59,
    0x00,
    0x17,
    0xCF.toByte(),
    type
) + List(20) { 0x00.toByte() }

abstract class AcnSensaBaseFormat : AdvertisementFormat {
    override fun getFormat(): Map<String, ByteFormat> {
        val flagsLength = Pair(
            FLAGS_LENGTH,
            ByteFormat(
                startIndexInclusive = 0,
                endIndexExclusive = 1,
                isReversed = false,
                targetType = SupportedTypes.BYTE
            )
        )
        val flagsType = Pair(
            FLAGS_TYPE,
            ByteFormat(
                startIndexInclusive = 1,
                endIndexExclusive = 2,
                isReversed = false,
                targetType = SupportedTypes.BYTE
            )
        )
        val flags = Pair(
            FLAGS,
            ByteFormat(
                startIndexInclusive = 2,
                endIndexExclusive = 3,
                isReversed = false,
                targetType = SupportedTypes.BYTE
            )
        )
        val contentLength = Pair(
            CONTENT_LENGTH,
            ByteFormat(
                startIndexInclusive = 3,
                endIndexExclusive = 4,
                isReversed = false,
                targetType = SupportedTypes.BYTE
            )
        )
        val contentType = Pair(
            CONTENT_TYPE,
            ByteFormat(
                startIndexInclusive = 4,
                endIndexExclusive = 5,
                isReversed = false,
                targetType = SupportedTypes.BYTE
            )
        )
        val vendorId = Pair(
            VENDOR_ID,
            ByteFormat(
                startIndexInclusive = 5,
                endIndexExclusive = 7,
                isReversed = false,
                targetType = SupportedTypes.SHORT
            )
        )
        val appId = Pair(
            APP_ID,
            ByteFormat(
                startIndexInclusive = 7,
                endIndexExclusive = 9,
                isReversed = false,
                targetType = SupportedTypes.SHORT
            )
        )
        val advertisementType = Pair(
            ADVERTISEMENT_TYPE,
            ByteFormat(
                startIndexInclusive = 9,
                endIndexExclusive = 10,
                isReversed = false,
                targetType = SupportedTypes.BYTE
            )
        )

        return mapOf(
            flagsLength,
            flagsType,
            flags,
            contentLength,
            contentType,
            vendorId,
            appId,
            advertisementType
        )
    }

    companion object {
        const val ADVERTISEMENT_TYPE = "Advertisement Type"
        const val FLAGS_LENGTH = "Flags Length"
        const val FLAGS_TYPE = "Flags Type"
        const val FLAGS = "FLags"
        const val CONTENT_LENGTH = "Content Length"
        const val CONTENT_TYPE = "Content Type"
        const val VENDOR_ID = "Vendor Id"
        const val APP_ID = "App Id"
    }
}

object SupportedTypes {
    const val FLOAT = "Float"
    const val SHORT = "Short"
    const val BYTE = "Byte"
}

data class ByteFormat(
    val startIndexInclusive: Int,
    val endIndexExclusive: Int,
    val isReversed: Boolean,
    val targetType: String
)