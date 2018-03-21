package com.aconno.acnsensa.domain.advertisement

import com.aconno.acnsensa.domain.format.ScalarsAdvertisementFormat
import com.aconno.acnsensa.domain.model.Advertisement

/**
 * @author aconno
 */
class ScalarsAdvertisementDeserializer(
    private val scalarsAdvertisementFormat: ScalarsAdvertisementFormat
) : AdvertisementDeserializer {

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

        val batteryLevel = getValue(
            advertisement,
            scalarsAdvertisementFormat,
            ScalarsAdvertisementFormat.BATTERY_LEVEL
        )


        return mapOf(
            Pair(ScalarsAdvertisementFormat.TEMPERATURE, temperature),
            Pair(ScalarsAdvertisementFormat.HUMIDITY, humidity),
            Pair(ScalarsAdvertisementFormat.PRESSURE, pressure),
            Pair(ScalarsAdvertisementFormat.LIGHT, light),
            Pair(ScalarsAdvertisementFormat.BATTERY_LEVEL, batteryLevel)
        )
    }
}