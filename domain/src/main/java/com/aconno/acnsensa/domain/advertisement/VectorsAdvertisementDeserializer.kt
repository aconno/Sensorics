package com.aconno.acnsensa.domain.advertisement

import com.aconno.acnsensa.domain.format.VectorsAdvertisementFormat
import com.aconno.acnsensa.domain.model.Advertisement

/**
 * @author aconno
 */
class VectorsAdvertisementDeserializer(
    private val vectorsAdvertisementFormat: VectorsAdvertisementFormat
) : AdvertisementDeserializer {

    override fun deserialize(advertisement: Advertisement): Map<String, Number> {
        val gyroscopeX = getValue(
            advertisement, vectorsAdvertisementFormat, VectorsAdvertisementFormat.GYROSCOPE_X
        )
        val gyroscopeY = getValue(
            advertisement, vectorsAdvertisementFormat, VectorsAdvertisementFormat.GYROSCOPE_Y
        )
        val gyroscopeZ = getValue(
            advertisement, vectorsAdvertisementFormat, VectorsAdvertisementFormat.GYROSCOPE_Z
        )

        val accelerometerX = getValue(
            advertisement, vectorsAdvertisementFormat, VectorsAdvertisementFormat.ACCELEROMETER_X
        )
        val accelerometerY = getValue(
            advertisement, vectorsAdvertisementFormat, VectorsAdvertisementFormat.ACCELEROMETER_Y
        )
        val accelerometerZ = getValue(
            advertisement, vectorsAdvertisementFormat, VectorsAdvertisementFormat.ACCELEROMETER_Z
        )

        val magnetometerX = getValue(
            advertisement, vectorsAdvertisementFormat, VectorsAdvertisementFormat.MAGNETOMETER_X
        )
        val magnetometerY = getValue(
            advertisement, vectorsAdvertisementFormat, VectorsAdvertisementFormat.MAGNETOMETER_Y
        )
        val magnetometerZ = getValue(
            advertisement, vectorsAdvertisementFormat, VectorsAdvertisementFormat.MAGNETOMETER_Z
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