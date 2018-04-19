package com.aconno.acnsensa.domain.interactor

import com.aconno.acnsensa.domain.FileStorage
import com.aconno.acnsensa.domain.interactor.type.CompletableUseCaseWithParameter
import com.aconno.acnsensa.domain.model.SensorType
import com.aconno.acnsensa.domain.model.readings.Reading
import io.reactivex.Completable

/**
 * @aconno
 */
class LogReadingUseCase(private val fileStorage: FileStorage) :
    CompletableUseCaseWithParameter<List<Reading>> {
    override fun execute(parameter: List<Reading>): Completable {
        for (reading in parameter) {
            logReading(reading)
        }
        return Completable.complete()
    }

    private fun logReading(reading: Reading) {
        when (reading.sensorType) {
            SensorType.TEMPERATURE -> fileStorage.storeReading(reading, "temperature.csv")
            SensorType.LIGHT -> fileStorage.storeReading(reading, "light.csv")
            SensorType.HUMIDITY -> fileStorage.storeReading(reading, "humidity.csv")
            SensorType.PRESSURE -> fileStorage.storeReading(reading, "pressure.csv")
            SensorType.MAGNETOMETER -> fileStorage.storeReading(reading, "magnetometer.csv")
            SensorType.ACCELEROMETER -> fileStorage.storeReading(reading, "accelerometer.csv")
            SensorType.GYROSCOPE -> fileStorage.storeReading(reading, "gyroscope.csv")
            SensorType.BATTERY_LEVEL -> fileStorage.storeReading(reading, "battery.csv")
        }
    }
}