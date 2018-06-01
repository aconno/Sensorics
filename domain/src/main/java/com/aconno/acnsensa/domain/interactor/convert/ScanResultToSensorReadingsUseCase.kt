package com.aconno.acnsensa.domain.interactor.convert

import com.aconno.acnsensa.domain.advertisement.AdvertisementDeserializer
import com.aconno.acnsensa.domain.advertisement.AdvertisementMatcher
import com.aconno.acnsensa.domain.advertisement.ScalarsAdvertisementDeserializer
import com.aconno.acnsensa.domain.advertisement.VectorsAdvertisementDeserializer
import com.aconno.acnsensa.domain.format.AdvertisementFormat
import com.aconno.acnsensa.domain.format.ScalarsAdvertisementFormat
import com.aconno.acnsensa.domain.format.VectorsAdvertisementFormat
import com.aconno.acnsensa.domain.interactor.type.SingleUseCaseWithParameter
import com.aconno.acnsensa.domain.model.ScanResult
import com.aconno.acnsensa.domain.model.SensorReading
import com.aconno.acnsensa.domain.model.SensorTypeSingle
import io.reactivex.Single

class ScanResultToSensorReadingsUseCase(
    private val advertisementMatcher: AdvertisementMatcher
) : SingleUseCaseWithParameter<List<SensorReading>, ScanResult> {

    override fun execute(scanResult: ScanResult): Single<List<SensorReading>> {
        return Single.just(scanResult).map { toSensorReadings(it) }
    }

    private fun toSensorReadings(scanResult: ScanResult): List<SensorReading> {
        val advertisementFormat =
            advertisementMatcher.matchAdvertisementToFormat(scanResult.advertisement)
        val advertisementDeserializer = getAdvertisementDeserializer(advertisementFormat)
        val values = advertisementDeserializer.deserialize(scanResult.advertisement)
        return values.map {
            SensorReading(
                System.currentTimeMillis(), //TODO: Read this value from scan reading
                scanResult.device,
                it.value,
                getSensorType(it.key)
            )
        }
    }

    private fun getAdvertisementDeserializer(advertisementFormat: AdvertisementFormat):
            AdvertisementDeserializer {
        return when (advertisementFormat) {
            is ScalarsAdvertisementFormat -> ScalarsAdvertisementDeserializer(advertisementFormat)
            is VectorsAdvertisementFormat -> VectorsAdvertisementDeserializer(advertisementFormat)
            else -> throw IllegalArgumentException("Invalid advertisement format")
        }
    }

    private fun getSensorType(sensorType: String): SensorTypeSingle {
        return when (sensorType) {
            ScalarsAdvertisementFormat.TEMPERATURE -> SensorTypeSingle.TEMPERATURE
            ScalarsAdvertisementFormat.LIGHT -> SensorTypeSingle.LIGHT
            ScalarsAdvertisementFormat.HUMIDITY -> SensorTypeSingle.HUMIDITY
            ScalarsAdvertisementFormat.PRESSURE -> SensorTypeSingle.PRESSURE
            ScalarsAdvertisementFormat.BATTERY_LEVEL -> SensorTypeSingle.BATTERY_LEVEL
            VectorsAdvertisementFormat.ACCELEROMETER_X -> SensorTypeSingle.ACCELEROMETER_X
            VectorsAdvertisementFormat.ACCELEROMETER_Y -> SensorTypeSingle.ACCELEROMETER_Y
            VectorsAdvertisementFormat.ACCELEROMETER_Z -> SensorTypeSingle.ACCELEROMETER_Z
            VectorsAdvertisementFormat.GYROSCOPE_X -> SensorTypeSingle.GYROSCOPE_X
            VectorsAdvertisementFormat.GYROSCOPE_Y -> SensorTypeSingle.GYROSCOPE_Y
            VectorsAdvertisementFormat.GYROSCOPE_Z -> SensorTypeSingle.GYROSCOPE_Z
            VectorsAdvertisementFormat.MAGNETOMETER_X -> SensorTypeSingle.MAGNETOMETER_X
            VectorsAdvertisementFormat.MAGNETOMETER_Y -> SensorTypeSingle.MAGNETOMETER_Y
            VectorsAdvertisementFormat.MAGNETOMETER_Z -> SensorTypeSingle.MAGNETOMETER_Z
            else -> throw IllegalArgumentException("Sensor type not valid: $sensorType")
        }
    }
}