package com.aconno.acnsensa.domain.format

/**
 * @author aconno
 */
class VectorsAdvertisementFormat : AcnSensaBaseFormat() {

    private val requiredFormat: List<Byte> = generateAcnSensaRequiredFormat(0x00)

    override fun getFormat(): Map<String, ByteFormat> {
        val baseFormat: Map<String, ByteFormat> = super.getFormat()

        val gyroscopeX = Pair(
            GYROSCOPE_X,
            ByteFormat(
                startIndexInclusive = 10, endIndexExclusive = 12,
                isReversed = true, targetType = SupportedTypes.SHORT
            )
        )
        val gyroscopeY = Pair(
            GYROSCOPE_Y,
            ByteFormat(
                startIndexInclusive = 12, endIndexExclusive = 14,
                isReversed = true, targetType = SupportedTypes.SHORT
            )
        )
        val gyroscopeZ = Pair(
            GYROSCOPE_Z,
            ByteFormat(
                startIndexInclusive = 14, endIndexExclusive = 16,
                isReversed = true, targetType = SupportedTypes.SHORT
            )
        )
        val accelerometerX = Pair(
            ACCELEROMETER_X,
            ByteFormat(
                startIndexInclusive = 16, endIndexExclusive = 18,
                isReversed = true, targetType = SupportedTypes.SHORT
            )
        )
        val accelerometerY = Pair(
            ACCELEROMETER_Y,
            ByteFormat(
                startIndexInclusive = 18, endIndexExclusive = 20,
                isReversed = true, targetType = SupportedTypes.SHORT
            )
        )
        val accelerometerZ = Pair(
            ACCELEROMETER_Z,
            ByteFormat(
                startIndexInclusive = 20, endIndexExclusive = 22,
                isReversed = true, targetType = SupportedTypes.SHORT
            )
        )
        val magnetometerX = Pair(
            MAGNETOMETER_X,
            ByteFormat(
                startIndexInclusive = 22, endIndexExclusive = 24,
                isReversed = true, targetType = SupportedTypes.SHORT
            )
        )
        val magnetometerY = Pair(
            MAGNETOMETER_Y,
            ByteFormat(
                startIndexInclusive = 24, endIndexExclusive = 26,
                isReversed = true, targetType = SupportedTypes.SHORT
            )
        )
        val magnetometerZ = Pair(
            MAGNETOMETER_Z,
            ByteFormat(
                startIndexInclusive = 26, endIndexExclusive = 28,
                isReversed = true, targetType = SupportedTypes.SHORT
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
