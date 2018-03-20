package com.aconno.acnsensa.domain.interactor

import com.aconno.acnsensa.domain.FileStorage
import com.aconno.acnsensa.domain.interactor.type.CompletableUseCaseWithParameter
import com.aconno.acnsensa.domain.model.readings.*
import io.reactivex.Completable

/**
 * @aconno
 */
class LogReadingUseCase(private val fileStorage: FileStorage) :
    CompletableUseCaseWithParameter<Reading> {
    override fun execute(parameter: Reading): Completable {
        when (parameter) {
            is TemperatureReading -> fileStorage.storeReading(parameter, "temperature.csv")
            is LightReading -> fileStorage.storeReading(parameter, "light.csv")
            is HumidityReading -> fileStorage.storeReading(parameter, "humidity.csv")
            is PressureReading -> fileStorage.storeReading(parameter, "pressure.csv")
            is MagnetometerReading -> fileStorage.storeReading(parameter, "magnetometer.csv")
            is AccelerometerReading -> fileStorage.storeReading(parameter, "accelerometer.csv")
            is GyroscopeReading -> fileStorage.storeReading(parameter, "gyroscope.csv")
        }

        return Completable.complete()
    }
}