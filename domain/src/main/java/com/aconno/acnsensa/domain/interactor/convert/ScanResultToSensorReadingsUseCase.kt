package com.aconno.acnsensa.domain.interactor.convert

import com.aconno.acnsensa.domain.advertisement.AdvertisementMatcher
import com.aconno.acnsensa.domain.format.Deserializer
import com.aconno.acnsensa.domain.interactor.type.SingleUseCaseWithParameter
import com.aconno.acnsensa.domain.model.Device
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
        val device = Device(
            scanResult.device.name,
            scanResult.device.macAddress,
            advertisementFormat.getIcon()
        )
        advertisementFormat.getFormat().forEach { name, byteFormat ->
            sensorReadings.add(
                SensorReading(
                    System.currentTimeMillis(),
                    device,
                    deserializer.deserializeNumber(rawData, byteFormat),
                    getSensorType(name)
                )
            )
        }
        return sensorReadings
    }

    private fun getSensorType(sensorType: String): SensorTypeSingle {
        return when (sensorType) {
            "Temperature" -> SensorTypeSingle.TEMPERATURE
            "Light" -> SensorTypeSingle.LIGHT
            "Humidity" -> SensorTypeSingle.HUMIDITY
            "Pressure" -> SensorTypeSingle.PRESSURE
            "Battery Level" -> SensorTypeSingle.BATTERY_LEVEL
            "Accelerometer X" -> SensorTypeSingle.ACCELEROMETER_X
            "Accelerometer Y" -> SensorTypeSingle.ACCELEROMETER_Y
            "Accelerometer Z" -> SensorTypeSingle.ACCELEROMETER_Z
            "Gyroscope X" -> SensorTypeSingle.GYROSCOPE_X
            "Gyroscope Y" -> SensorTypeSingle.GYROSCOPE_Y
            "Gyroscope Z" -> SensorTypeSingle.GYROSCOPE_Z
            "Magnetometer X" -> SensorTypeSingle.MAGNETOMETER_X
            "Magnetometer Y" -> SensorTypeSingle.MAGNETOMETER_Y
            "Magnetometer Z" -> SensorTypeSingle.MAGNETOMETER_Z
            else -> SensorTypeSingle.OTHER
        }
    }
}