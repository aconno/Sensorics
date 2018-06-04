package com.aconno.acnsensa.domain.repository

import com.aconno.acnsensa.domain.model.SensorReading
import com.aconno.acnsensa.domain.model.SensorTypeSingle

interface InMemoryRepository {

    fun addSensorReading(sensorReading: SensorReading)

    fun getSensorReadingsFor(sensorType: SensorTypeSingle): List<SensorReading>

    fun deleteAllSensorReadings()
}