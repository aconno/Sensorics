package com.aconno.acnsensa.domain.interactor

import com.aconno.acnsensa.domain.FileStorage
import com.aconno.acnsensa.domain.interactor.type.CompletableUseCaseWithParameter
import com.aconno.acnsensa.domain.model.readings.*
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
        when (reading) {
            is TemperatureReading -> fileStorage.storeReading(reading, "temperature.csv")
            is LightReading -> fileStorage.storeReading(reading, "light.csv")
            is HumidityReading -> fileStorage.storeReading(reading, "humidity.csv")
            is PressureReading -> fileStorage.storeReading(reading, "pressure.csv")
            is MagnetometerReading -> fileStorage.storeReading(reading, "magnetometer.csv")
            is AccelerometerReading -> fileStorage.storeReading(reading, "accelerometer.csv")
            is GyroscopeReading -> fileStorage.storeReading(reading, "gyroscope.csv")
            is BatteryReading -> fileStorage.storeReading(reading, "battery.csv")
        }
    }
}