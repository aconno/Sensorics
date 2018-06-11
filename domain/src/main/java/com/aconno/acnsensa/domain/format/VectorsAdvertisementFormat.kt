package com.aconno.acnsensa.domain.format

/**
 * @author aconno
 */
class VectorsAdvertisementFormat : AcnSensaBaseFormat() {

    private val requiredFormat: List<ByteFormatRequired> = generateAcnSensaRequiredFormat(0x00)

    override fun getFormat(): Map<String, ByteFormat> {
        val baseFormat: Map<String, ByteFormat> = super.getFormat()

        val gyroscopeX = Pair(
            GYROSCOPE_X, ByteFormat(
                name = GYROSCOPE_X,
                startIndexInclusive = 10,
                endIndexExclusive = 12,
                isReversed = true,
                dataType = SupportedTypes.SHORT
            )
        )
        val gyroscopeY = Pair(
            GYROSCOPE_Y, ByteFormat(
                name = GYROSCOPE_Y,
                startIndexInclusive = 12,
                endIndexExclusive = 14,
                isReversed = true,
                dataType = SupportedTypes.SHORT
            )
        )
        val gyroscopeZ = Pair(
            GYROSCOPE_Z, ByteFormat(
                name = GYROSCOPE_Z,
                startIndexInclusive = 14,
                endIndexExclusive = 16,
                isReversed = true,
                dataType = SupportedTypes.SHORT
            )
        )
        val accelerometerX = Pair(
            ACCELEROMETER_X, ByteFormat(
                name = ACCELEROMETER_X,
                startIndexInclusive = 16,
                endIndexExclusive = 18,
                isReversed = true,
                dataType = SupportedTypes.SHORT
            )
        )
        val accelerometerY = Pair(
            ACCELEROMETER_Y, ByteFormat(
                name = ACCELEROMETER_Y,
                startIndexInclusive = 18,
                endIndexExclusive = 20,
                isReversed = true,
                dataType = SupportedTypes.SHORT
            )
        )
        val accelerometerZ = Pair(
            ACCELEROMETER_Z, ByteFormat(
                name = ACCELEROMETER_Z,
                startIndexInclusive = 20,
                endIndexExclusive = 22,
                isReversed = true,
                dataType = SupportedTypes.SHORT
            )
        )
        val magnetometerX = Pair(
            MAGNETOMETER_X, ByteFormat(
                name = MAGNETOMETER_X,
                startIndexInclusive = 22,
                endIndexExclusive = 24,
                isReversed = true,
                dataType = SupportedTypes.SHORT
            )
        )
        val magnetometerY = Pair(
            MAGNETOMETER_Y, ByteFormat(
                name = MAGNETOMETER_Y,
                startIndexInclusive = 24,
                endIndexExclusive = 26,
                isReversed = true,
                dataType = SupportedTypes.SHORT
            )
        )
        val magnetometerZ = Pair(
            MAGNETOMETER_Z, ByteFormat(
                name = MAGNETOMETER_Z,
                startIndexInclusive = 26,
                endIndexExclusive = 28,
                isReversed = true,
                dataType = SupportedTypes.SHORT
            )
        )
        val accelerometerScaleFactor = Pair(
            ACCELEROMETER_SCALE_FACTOR, ByteFormat(
                name = ACCELEROMETER_SCALE_FACTOR,
                startIndexInclusive = 28,
                endIndexExclusive = 30,
                isReversed = true,
                dataType = SupportedTypes.UNSIGNED_SHORT
            )
        )

        return baseFormat + listOf(
            gyroscopeX,
            gyroscopeY,
            gyroscopeZ,
            accelerometerX,
            accelerometerY,
            accelerometerZ,
            magnetometerX,
            magnetometerY,
            magnetometerZ,
            accelerometerScaleFactor
        )
    }

    override fun getRequiredFormat(): List<ByteFormatRequired> = requiredFormat

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

        const val ACCELEROMETER_SCALE_FACTOR = "Accelerometer Scale Factor"
    }
}
