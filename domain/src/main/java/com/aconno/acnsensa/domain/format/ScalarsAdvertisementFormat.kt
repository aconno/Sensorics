package com.aconno.acnsensa.domain.format

abstract class ScalarsAdvertisementFormat : AcnSensaBaseFormat() {

    private val requiredFormat: List<ByteFormatRequired> = generateAcnSensaRequiredFormat(0x01)

    override fun getFormat(): Map<String, ByteFormat> {
        val baseFormat: Map<String, ByteFormat> = super.getFormat()

        val temperature = Pair(
            TEMPERATURE, ByteFormat(
                name = TEMPERATURE,
                startIndexInclusive = 10,
                endIndexExclusive = 14,
                isReversed = true,
                dataType = SupportedTypes.FLOAT
            )
        )
        val humidity = Pair(
            HUMIDITY, ByteFormat(
                name = HUMIDITY,
                startIndexInclusive = 14,
                endIndexExclusive = 18,
                isReversed = true,
                dataType = SupportedTypes.FLOAT
            )
        )
        val pressure = Pair(
            PRESSURE, ByteFormat(
                name = PRESSURE,
                startIndexInclusive = 18,
                endIndexExclusive = 22,
                isReversed = true,
                dataType = SupportedTypes.FLOAT
            )
        )
        val light = Pair(
            LIGHT, ByteFormat(
                name = LIGHT,
                startIndexInclusive = 22,
                endIndexExclusive = 26,
                isReversed = true,
                dataType = SupportedTypes.FLOAT
            )
        )
        val batteryLevel = Pair(
            BATTERY_LEVEL, ByteFormat(
                name = BATTERY_LEVEL,
                startIndexInclusive = 26,
                endIndexExclusive = 27,
                isReversed = false,
                dataType = SupportedTypes.BYTE
            )
        )

        return baseFormat + listOf(temperature, humidity, pressure, light, batteryLevel)
    }

    override fun getRequiredFormat(): List<ByteFormatRequired> = requiredFormat

    companion object {
        const val TEMPERATURE = "Temperature"
        const val HUMIDITY = "Humidity"
        const val PRESSURE = "Pressure"
        const val LIGHT = "Light"
        const val BATTERY_LEVEL = "Battery Level"
    }
}