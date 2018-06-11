package com.aconno.acnsensa.domain.advertisement

import com.aconno.acnsensa.domain.format.AdvertisementFormat
import com.aconno.acnsensa.domain.format.ScalarsAdvertisementFormat
import com.aconno.acnsensa.domain.model.Advertisement

class GeneralAdvertisementDeserializer(
    private val advertisementFormat: AdvertisementFormat
) : AdvertisementDeserializer {

    override fun deserialize(advertisement: Advertisement): Map<String, Number> {
        //TODO REMOVE COPIED FROM SCALAR
        val temperature = getValue(
            advertisement,
            advertisementFormat,
            ScalarsAdvertisementFormat.TEMPERATURE
        )
        val humidity =
            getValue(advertisement, advertisementFormat, ScalarsAdvertisementFormat.HUMIDITY)
        val pressure =
            getValue(advertisement, advertisementFormat, ScalarsAdvertisementFormat.PRESSURE)
        val light =
            getValue(advertisement, advertisementFormat, ScalarsAdvertisementFormat.LIGHT)

        val batteryLevel = getValue(
            advertisement,
            advertisementFormat,
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