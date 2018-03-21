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

        val accelerometerBaseScale = getValue(
            advertisement,
            vectorsAdvertisementFormat,
            VectorsAdvertisementFormat.ACCELEROMETER_SCALE_FACTOR
        )

        val accelerometerScaleFactor = accelerometerBaseScale.toShort() / 65536f
        val gyroscopeScaleFactor = 245.0f / 32768.0f
        val magnetometerScaleFactor = 0.00014f

        return mapOf(
            Pair(
                VectorsAdvertisementFormat.GYROSCOPE_X,
                gyroscopeX.toFloat() * gyroscopeScaleFactor
            ),
            Pair(
                VectorsAdvertisementFormat.GYROSCOPE_Y,
                gyroscopeY.toFloat() * gyroscopeScaleFactor
            ),
            Pair(
                VectorsAdvertisementFormat.GYROSCOPE_Z,
                gyroscopeZ.toFloat() * gyroscopeScaleFactor
            ),
            Pair(
                VectorsAdvertisementFormat.ACCELEROMETER_X,
                accelerometerX.toFloat() * accelerometerScaleFactor
            ),
            Pair(
                VectorsAdvertisementFormat.ACCELEROMETER_Y,
                accelerometerY.toFloat() * accelerometerScaleFactor
            ),
            Pair(
                VectorsAdvertisementFormat.ACCELEROMETER_Z,
                accelerometerZ.toFloat() * accelerometerScaleFactor
            ),
            Pair(
                VectorsAdvertisementFormat.MAGNETOMETER_X,
                magnetometerX.toFloat() * magnetometerScaleFactor
            ),
            Pair(
                VectorsAdvertisementFormat.MAGNETOMETER_Y,
                magnetometerY.toFloat() * magnetometerScaleFactor
            ),
            Pair(
                VectorsAdvertisementFormat.MAGNETOMETER_Z,
                magnetometerZ.toFloat() * magnetometerScaleFactor
            )
        )
    }
}