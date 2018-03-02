package com.aconno.acnsensa.domain.interactor.repository

import com.aconno.acnsensa.domain.interactor.type.CompletableUseCaseWithParameter
import com.aconno.acnsensa.domain.model.readings.*
import com.aconno.acnsensa.domain.repository.InMemoryRepository
import io.reactivex.Completable

class AddReadingToInMemoryRepoUseCase(private val inMemoryRepository: InMemoryRepository) :
    CompletableUseCaseWithParameter<Map<String, Number>> {

    //TODO: Clean this
    override fun execute(sensorValues: Map<String, Number>): Completable {

        if (sensorValues.containsKey(KEY_TEMPERATURE)) {
            inMemoryRepository.addReading(
                TemperatureReading(System.currentTimeMillis(), sensorValues[KEY_TEMPERATURE]!!)
            )
        }
        if (sensorValues.containsKey(KEY_LIGHT)) {
            inMemoryRepository.addReading(
                LightReading(System.currentTimeMillis(), sensorValues[KEY_LIGHT]!!)
            )
        }
        if (sensorValues.containsKey(KEY_HUMIDITY)) {
            inMemoryRepository.addReading(
                HumidityReading(System.currentTimeMillis(), sensorValues[KEY_HUMIDITY]!!)
            )
        }
        if (sensorValues.containsKey(KEY_PRESSURE)) {
            inMemoryRepository.addReading(
                PressureReading(System.currentTimeMillis(), sensorValues[KEY_PRESSURE]!!)
            )
        }
        if (sensorValues.containsKey(KEY_MAGNETOMETER_X)) {
            inMemoryRepository.addReading(
                MagnetometerReading(
                    System.currentTimeMillis(),
                    sensorValues[KEY_MAGNETOMETER_X]!!,
                    sensorValues[KEY_MAGNETOMETER_Y]!!,
                    sensorValues[KEY_MAGNETOMETER_Z]!!
                )
            )
        }
        if (sensorValues.containsKey(KEY_ACCELEROMETER_X)) {
            inMemoryRepository.addReading(
                AccelerometerReading(
                    System.currentTimeMillis(),
                    sensorValues[KEY_ACCELEROMETER_X]!!,
                    sensorValues[KEY_ACCELEROMETER_Y]!!,
                    sensorValues[KEY_ACCELEROMETER_Z]!!
                )
            )
        }
        if (sensorValues.containsKey(KEY_GYROSCOPE_X)) {
            inMemoryRepository.addReading(
                GyroscopeReading(
                    System.currentTimeMillis(),
                    sensorValues[KEY_GYROSCOPE_X]!!,
                    sensorValues[KEY_GYROSCOPE_Y]!!,
                    sensorValues[KEY_GYROSCOPE_Z]!!
                )
            )
        }
        return Completable.complete()
    }

    companion object {

        private val KEY_TEMPERATURE = "Temperature"
        private val KEY_LIGHT = "Light"
        private val KEY_HUMIDITY = "Humidity"
        private val KEY_PRESSURE = "Pressure"
        private val KEY_MAGNETOMETER_X = "Magnetometer X"
        private val KEY_MAGNETOMETER_Y = "Magnetometer Y"
        private val KEY_MAGNETOMETER_Z = "Magnetometer Z"
        private val KEY_ACCELEROMETER_X = "Accelerometer X"
        private val KEY_ACCELEROMETER_Y = "Accelerometer Y"
        private val KEY_ACCELEROMETER_Z = "Accelerometer Z"
        private val KEY_GYROSCOPE_X = "Gyroscope X"
        private val KEY_GYROSCOPE_Y = "Gyroscope Y"
        private val KEY_GYROSCOPE_Z = "Gyroscope Z"
    }
}