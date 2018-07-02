package com.aconno.acnsensa.domain.interactor

import com.aconno.acnsensa.domain.FileStorage
import com.aconno.acnsensa.domain.model.Reading
import com.aconno.acnsensa.domain.model.ReadingType
import com.aconno.acnsensa.domain.interactor.type.CompletableUseCaseWithParameter
import io.reactivex.Completable

class LogReadingUseCase(
    private val fileStorage: FileStorage
) : CompletableUseCaseWithParameter<List<Reading>> {

    override fun execute(parameter: List<Reading>): Completable {
        for (reading in parameter) {
            logReading(reading)
        }
        return Completable.complete()
    }

    private fun logReading(reading: Reading) {
        when (reading.type) {
            ReadingType.OTHER -> fileStorage.storeReading(reading, "other.csv")
            ReadingType.TEMPERATURE -> fileStorage.storeReading(reading, "temperature.csv")
            ReadingType.LIGHT -> fileStorage.storeReading(reading, "light.csv")
            ReadingType.HUMIDITY -> fileStorage.storeReading(reading, "humidity.csv")
            ReadingType.PRESSURE -> fileStorage.storeReading(reading, "pressure.csv")
            ReadingType.MAGNETOMETER_X -> fileStorage.storeReading(reading, "magnetometer_x.csv")
            ReadingType.MAGNETOMETER_Y -> fileStorage.storeReading(reading, "magnetometer_y.csv")
            ReadingType.MAGNETOMETER_Z -> fileStorage.storeReading(reading, "magnetometer_z.csv")
            ReadingType.ACCELEROMETER_X -> fileStorage.storeReading(reading, "accelerometer_x.csv")
            ReadingType.ACCELEROMETER_Y -> fileStorage.storeReading(reading, "accelerometer_y.csv")
            ReadingType.ACCELEROMETER_Z -> fileStorage.storeReading(reading, "accelerometer_z.csv")
            ReadingType.GYROSCOPE_X -> fileStorage.storeReading(reading, "gyroscope_x.csv")
            ReadingType.GYROSCOPE_Y -> fileStorage.storeReading(reading, "gyroscope_y.csv")
            ReadingType.GYROSCOPE_Z -> fileStorage.storeReading(reading, "gyroscope_z.csv")
            ReadingType.BATTERY_LEVEL -> fileStorage.storeReading(reading, "battery_level.csv")
        }
    }
}