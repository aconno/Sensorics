package com.aconno.acnsensa.data.repository

import com.aconno.acnsensa.domain.model.SensorType
import com.aconno.acnsensa.domain.model.readings.Reading
import com.aconno.acnsensa.domain.repository.InMemoryRepository

class InMemoryRepositoryImpl : InMemoryRepository {

    private val temperatureReadings = mutableListOf<Reading>()
    private val lightReadings = mutableListOf<Reading>()
    private val humidityReadings = mutableListOf<Reading>()
    private val pressureReadings = mutableListOf<Reading>()
    private val magnetometerReadings = mutableListOf<Reading>()
    private val accelerometerReadings = mutableListOf<Reading>()
    private val gyroscopeReadings = mutableListOf<Reading>()


    override fun addReading(reading: Reading) {
        when (reading.getSensorType()) {
            SensorType.TEMPERATURE -> addToBuffer(reading, temperatureReadings)
            SensorType.LIGHT -> addToBuffer(reading, lightReadings)
            SensorType.HUMIDITY -> addToBuffer(reading, humidityReadings)
            SensorType.PRESSURE -> addToBuffer(reading, pressureReadings)
            SensorType.MAGNETOMETER -> addToBuffer(reading, magnetometerReadings)
            SensorType.ACCELEROMETER -> addToBuffer(reading, accelerometerReadings)
            SensorType.GYROSCOPE -> addToBuffer(reading, gyroscopeReadings)
        }
    }

    private fun addToBuffer(reading: Reading, readings: MutableList<Reading>) {
        if (readings.size >= BUFFER_SIZE) {
            readings.removeAt(0)
        }
        readings.add(reading)
    }

    override fun getReadingsFor(sensorType: SensorType): List<Reading> {
        return when (sensorType) {
            SensorType.TEMPERATURE -> temperatureReadings
            SensorType.LIGHT -> lightReadings
            SensorType.HUMIDITY -> humidityReadings
            SensorType.PRESSURE -> pressureReadings
            SensorType.MAGNETOMETER -> magnetometerReadings
            SensorType.ACCELEROMETER -> accelerometerReadings
            SensorType.GYROSCOPE -> gyroscopeReadings
        }
    }

    companion object {

        private const val BUFFER_SIZE = 500
    }
}