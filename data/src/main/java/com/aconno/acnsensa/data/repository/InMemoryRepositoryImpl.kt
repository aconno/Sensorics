package com.aconno.acnsensa.data.repository

import com.aconno.acnsensa.domain.model.Reading
import com.aconno.acnsensa.domain.model.ReadingType
import com.aconno.acnsensa.domain.repository.InMemoryRepository

class InMemoryRepositoryImpl : InMemoryRepository {

    private val temperatureReadings = mutableListOf<Reading>()
    private val lightReadings = mutableListOf<Reading>()
    private val humidityReadings = mutableListOf<Reading>()
    private val pressureReadings = mutableListOf<Reading>()
    private val magnetometerXReadings = mutableListOf<Reading>()
    private val magnetometerYReadings = mutableListOf<Reading>()
    private val magnetometerZReadings = mutableListOf<Reading>()
    private val accelerometerXReadings = mutableListOf<Reading>()
    private val accelerometerYReadings = mutableListOf<Reading>()
    private val accelerometerZReadings = mutableListOf<Reading>()
    private val gyroscopeXReadings = mutableListOf<Reading>()
    private val gyroscopeYReadings = mutableListOf<Reading>()
    private val gyroscopeZReadings = mutableListOf<Reading>()
    private val batteryLevelReadings = mutableListOf<Reading>()


    override fun addReading(reading: Reading) {
        when (reading.type) {
            ReadingType.TEMPERATURE -> addToBuffer(reading, temperatureReadings)
            ReadingType.LIGHT -> addToBuffer(reading, lightReadings)
            ReadingType.HUMIDITY -> addToBuffer(reading, humidityReadings)
            ReadingType.PRESSURE -> addToBuffer(reading, pressureReadings)
            ReadingType.MAGNETOMETER_X -> addToBuffer(reading, magnetometerXReadings)
            ReadingType.MAGNETOMETER_Y -> addToBuffer(reading, magnetometerYReadings)
            ReadingType.MAGNETOMETER_Z -> addToBuffer(reading, magnetometerZReadings)
            ReadingType.ACCELEROMETER_X -> addToBuffer(reading, accelerometerXReadings)
            ReadingType.ACCELEROMETER_Y -> addToBuffer(reading, accelerometerYReadings)
            ReadingType.ACCELEROMETER_Z -> addToBuffer(reading, accelerometerZReadings)
            ReadingType.GYROSCOPE_X -> addToBuffer(reading, gyroscopeXReadings)
            ReadingType.GYROSCOPE_Y -> addToBuffer(reading, gyroscopeYReadings)
            ReadingType.GYROSCOPE_Z -> addToBuffer(reading, gyroscopeZReadings)
            ReadingType.BATTERY_LEVEL -> addToBuffer(reading, batteryLevelReadings)
        }
    }

    private fun addToBuffer(reading: Reading, readings: MutableList<Reading>) {
        if (readings.size >= BUFFER_SIZE) {
            readings.removeAt(0)
        }
        readings.add(reading)
    }

    override fun getReadingsFor(type: ReadingType): List<Reading> {
        return when (type) {
            ReadingType.TEMPERATURE -> temperatureReadings
            ReadingType.LIGHT -> lightReadings
            ReadingType.HUMIDITY -> humidityReadings
            ReadingType.PRESSURE -> pressureReadings
            ReadingType.MAGNETOMETER_X -> magnetometerXReadings
            ReadingType.MAGNETOMETER_Y -> magnetometerYReadings
            ReadingType.MAGNETOMETER_Z -> magnetometerZReadings
            ReadingType.ACCELEROMETER_X -> accelerometerXReadings
            ReadingType.ACCELEROMETER_Y -> accelerometerYReadings
            ReadingType.ACCELEROMETER_Z -> accelerometerZReadings
            ReadingType.GYROSCOPE_X -> gyroscopeXReadings
            ReadingType.GYROSCOPE_Y -> gyroscopeYReadings
            ReadingType.GYROSCOPE_Z -> gyroscopeZReadings
            ReadingType.BATTERY_LEVEL -> batteryLevelReadings
            ReadingType.OTHER -> emptyList()
        }
    }

    override fun deleteAllReadings() {
        temperatureReadings.clear()
        lightReadings.clear()
        humidityReadings.clear()
        pressureReadings.clear()
        magnetometerXReadings.clear()
        magnetometerYReadings.clear()
        magnetometerZReadings.clear()
        accelerometerXReadings.clear()
        accelerometerYReadings.clear()
        accelerometerZReadings.clear()
        gyroscopeXReadings.clear()
        gyroscopeYReadings.clear()
        gyroscopeZReadings.clear()
        batteryLevelReadings.clear()
    }

    companion object {

        private const val BUFFER_SIZE = 500
    }
}