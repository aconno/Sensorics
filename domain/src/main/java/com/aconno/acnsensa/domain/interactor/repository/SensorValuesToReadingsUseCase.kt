package com.aconno.acnsensa.domain.interactor.repository

import com.aconno.acnsensa.domain.interactor.type.SingleUseCaseWithParameter
import com.aconno.acnsensa.domain.model.SensorType
import com.aconno.acnsensa.domain.model.SupportedNames
import com.aconno.acnsensa.domain.model.readings.Reading
import io.reactivex.Single

class SensorValuesToReadingsUseCase :
    SingleUseCaseWithParameter<List<Reading>, Map<String, Number>> {
    override fun execute(parameter: Map<String, Number>): Single<List<Reading>> {
        val timestamp: Long = System.currentTimeMillis()

        val readings: List<Reading?> = listOf(
            makeAccelerometerReading(timestamp, parameter),
            makeGyroscopeReading(timestamp, parameter),
            makeHumidityReading(timestamp, parameter),
            makeLightReading(timestamp, parameter),
            makeMagnetometerReading(timestamp, parameter),
            makePressureReading(timestamp, parameter),
            makeTemperatureReading(timestamp, parameter),
            makeBatteryLevelReading(timestamp, parameter)
        )

        return Single.just(readings.filterNotNull())
    }

    private fun makeAccelerometerReading(
        timestamp: Long,
        values: Map<String, Number>
    ): Reading? {
        val x = values[SupportedNames.ACCELEROMETER_X]
        val y = values[SupportedNames.ACCELEROMETER_Y]
        val z = values[SupportedNames.ACCELEROMETER_Z]

        return if (x != null && y != null && z != null) {
            Reading(listOf(x, y, z), timestamp, SensorType.ACCELEROMETER)
        } else {
            null
        }
    }

    private fun makeGyroscopeReading(
        timestamp: Long,
        values: Map<String, Number>
    ): Reading? {
        val x = values[SupportedNames.GYROSCOPE_X]
        val y = values[SupportedNames.GYROSCOPE_Y]
        val z = values[SupportedNames.GYROSCOPE_Z]

        return if (x != null && y != null && z != null) {
            Reading(listOf(x, y, z), timestamp, SensorType.GYROSCOPE)
        } else {
            null
        }
    }

    private fun makeHumidityReading(
        timestamp: Long,
        values: Map<String, Number>
    ): Reading? {
        val humidity = values[SupportedNames.HUMIDITY]
        humidity?.let { return Reading(listOf(it), timestamp, SensorType.HUMIDITY) }
        return null
    }

    private fun makeLightReading(
        timestamp: Long,
        values: Map<String, Number>
    ): Reading? {
        val light = values[SupportedNames.LIGHT]
        light?.let { return Reading(listOf(it), timestamp, SensorType.LIGHT) }
        return null
    }

    private fun makeMagnetometerReading(
        timestamp: Long,
        values: Map<String, Number>
    ): Reading? {
        val x = values[SupportedNames.MAGNETOMETER_X]
        val y = values[SupportedNames.MAGNETOMETER_Y]
        val z = values[SupportedNames.MAGNETOMETER_Z]

        return if (x != null && y != null && z != null) {
            Reading(listOf(x, y, z), timestamp, SensorType.MAGNETOMETER)
        } else {
            null
        }
    }

    private fun makePressureReading(
        timestamp: Long,
        values: Map<String, Number>
    ): Reading? {
        val pressure = values[SupportedNames.PRESSURE]
        pressure?.let { return Reading(listOf(it), timestamp, SensorType.PRESSURE) }
        return null
    }

    private fun makeTemperatureReading(
        timestamp: Long,
        values: Map<String, Number>
    ): Reading? {
        val temperature = values[SupportedNames.TEMPERATURE]
        temperature?.let { return Reading(listOf(it), timestamp, SensorType.TEMPERATURE) }
        return null
    }

    private fun makeBatteryLevelReading(
        timestamp: Long,
        values: Map<String, Number>
    ): Reading? {
        val batteryLevel = values[SupportedNames.BATTERY_LEVEL]
        batteryLevel?.let { return Reading(listOf(it), timestamp, SensorType.BATTERY_LEVEL) }
        return null
    }
}
