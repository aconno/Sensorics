package com.aconno.acnsensa.data.repository

import com.aconno.acnsensa.domain.model.SensorReading
import com.aconno.acnsensa.domain.model.SensorTypeSingle
import com.aconno.acnsensa.domain.repository.InMemoryRepository

class InMemoryRepositoryImpl : InMemoryRepository {

    private val temperatureReadings = mutableListOf<SensorReading>()
    private val lightReadings = mutableListOf<SensorReading>()
    private val humidityReadings = mutableListOf<SensorReading>()
    private val pressureReadings = mutableListOf<SensorReading>()
    private val magnetometerXReadings = mutableListOf<SensorReading>()
    private val magnetometerYReadings = mutableListOf<SensorReading>()
    private val magnetometerZReadings = mutableListOf<SensorReading>()
    private val accelerometerXReadings = mutableListOf<SensorReading>()
    private val accelerometerYReadings = mutableListOf<SensorReading>()
    private val accelerometerZReadings = mutableListOf<SensorReading>()
    private val gyroscopeXReadings = mutableListOf<SensorReading>()
    private val gyroscopeYReadings = mutableListOf<SensorReading>()
    private val gyroscopeZReadings = mutableListOf<SensorReading>()
    private val batteryLevelReadings = mutableListOf<SensorReading>()


    override fun addSensorReading(sensorReading: SensorReading) {
        when (sensorReading.sensorType) {
            SensorTypeSingle.TEMPERATURE -> addToBuffer(sensorReading, temperatureReadings)
            SensorTypeSingle.LIGHT -> addToBuffer(sensorReading, lightReadings)
            SensorTypeSingle.HUMIDITY -> addToBuffer(sensorReading, humidityReadings)
            SensorTypeSingle.PRESSURE -> addToBuffer(sensorReading, pressureReadings)
            SensorTypeSingle.MAGNETOMETER_X -> addToBuffer(sensorReading, magnetometerXReadings)
            SensorTypeSingle.MAGNETOMETER_Y -> addToBuffer(sensorReading, magnetometerYReadings)
            SensorTypeSingle.MAGNETOMETER_Z -> addToBuffer(sensorReading, magnetometerZReadings)
            SensorTypeSingle.ACCELEROMETER_X -> addToBuffer(sensorReading, accelerometerXReadings)
            SensorTypeSingle.ACCELEROMETER_Y -> addToBuffer(sensorReading, accelerometerYReadings)
            SensorTypeSingle.ACCELEROMETER_Z -> addToBuffer(sensorReading, accelerometerZReadings)
            SensorTypeSingle.GYROSCOPE_X -> addToBuffer(sensorReading, gyroscopeXReadings)
            SensorTypeSingle.GYROSCOPE_Y -> addToBuffer(sensorReading, gyroscopeYReadings)
            SensorTypeSingle.GYROSCOPE_Z -> addToBuffer(sensorReading, gyroscopeZReadings)
            SensorTypeSingle.BATTERY_LEVEL -> addToBuffer(sensorReading, batteryLevelReadings)
        }
    }

    private fun addToBuffer(
        sensorReading: SensorReading,
        sensorReadings: MutableList<SensorReading>
    ) {
        if (sensorReadings.size >= BUFFER_SIZE) {
            sensorReadings.removeAt(0)
        }
        sensorReadings.add(sensorReading)
    }

    override fun getSensorReadingsFor(sensorType: SensorTypeSingle): List<SensorReading> {
        return when (sensorType) {
            SensorTypeSingle.TEMPERATURE -> temperatureReadings
            SensorTypeSingle.LIGHT -> lightReadings
            SensorTypeSingle.HUMIDITY -> humidityReadings
            SensorTypeSingle.PRESSURE -> pressureReadings
            SensorTypeSingle.MAGNETOMETER_X -> magnetometerXReadings
            SensorTypeSingle.MAGNETOMETER_Y -> magnetometerYReadings
            SensorTypeSingle.MAGNETOMETER_Z -> magnetometerZReadings
            SensorTypeSingle.ACCELEROMETER_X -> accelerometerXReadings
            SensorTypeSingle.ACCELEROMETER_Y -> accelerometerYReadings
            SensorTypeSingle.ACCELEROMETER_Z -> accelerometerZReadings
            SensorTypeSingle.GYROSCOPE_X -> gyroscopeXReadings
            SensorTypeSingle.GYROSCOPE_Y -> gyroscopeYReadings
            SensorTypeSingle.GYROSCOPE_Z -> gyroscopeZReadings
            SensorTypeSingle.BATTERY_LEVEL -> batteryLevelReadings
        }
    }

    override fun deleteAllSensorReadings() {
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