package com.aconno.acnsensa.domain.interactor.convert

import com.aconno.acnsensa.domain.advertisement.AdvertisementMatcher
import com.aconno.acnsensa.domain.format.Deserializer
import com.aconno.acnsensa.domain.format.ScalarsAdvertisementFormat
import com.aconno.acnsensa.domain.format.VectorsAdvertisementFormat
import com.aconno.acnsensa.domain.interactor.type.SingleUseCaseWithParameter
import com.aconno.acnsensa.domain.model.ScanResult
import com.aconno.acnsensa.domain.model.SensorReading
import com.aconno.acnsensa.domain.model.SensorTypeSingle
import io.reactivex.Single

class ScanResultToSensorReadingsUseCase(
    private val advertisementMatcher: AdvertisementMatcher,
    private val deserializer: Deserializer
) : SingleUseCaseWithParameter<List<SensorReading>, ScanResult> {

    override fun execute(scanResult: ScanResult): Single<List<SensorReading>> {
        return Single.just(scanResult).map { toSensorReadings(it) }
    }

    private fun toSensorReadings(scanResult: ScanResult): List<SensorReading> {
        val sensorReadings = mutableListOf<SensorReading>()
        val rawData = scanResult.advertisement.rawData
        val advertisementFormat =
            advertisementMatcher.matchAdvertisementToFormat(scanResult.advertisement)
        advertisementFormat.getFormat().forEach { name, byteFormat ->
            sensorReadings.add(
                SensorReading(
                    System.currentTimeMillis(),
                    scanResult.device,
                    deserializer.deserializeNumber(rawData, byteFormat),
                    getSensorType(name)
                )
            )
        }
        return sensorReadings
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
            else -> SensorTypeSingle.OTHER
        }
    }
}