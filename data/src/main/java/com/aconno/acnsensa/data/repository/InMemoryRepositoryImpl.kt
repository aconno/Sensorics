package com.aconno.acnsensa.data.repository

import com.aconno.acnsensa.domain.model.Reading
import com.aconno.acnsensa.domain.model.ReadingType
import com.aconno.acnsensa.domain.repository.InMemoryRepository
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.CopyOnWriteArrayList

class InMemoryRepositoryImpl : InMemoryRepository {

    private val temperatureReadings = CopyOnWriteArrayList<Reading>()
    private val lightReadings = CopyOnWriteArrayList<Reading>()
    private val humidityReadings = CopyOnWriteArrayList<Reading>()
    private val pressureReadings = CopyOnWriteArrayList<Reading>()
    private val magnetometerXReadings = CopyOnWriteArrayList<Reading>()
    private val magnetometerYReadings = CopyOnWriteArrayList<Reading>()
    private val magnetometerZReadings = CopyOnWriteArrayList<Reading>()
    private val accelerometerXReadings = CopyOnWriteArrayList<Reading>()
    private val accelerometerYReadings = CopyOnWriteArrayList<Reading>()
    private val accelerometerZReadings = CopyOnWriteArrayList<Reading>()
    private val gyroscopeXReadings = CopyOnWriteArrayList<Reading>()
    private val gyroscopeYReadings = CopyOnWriteArrayList<Reading>()
    private val gyroscopeZReadings = CopyOnWriteArrayList<Reading>()
    private val batteryLevelReadings = CopyOnWriteArrayList<Reading>()

    private val temperatureSubject = PublishSubject.create<List<Reading>>()
    private val lightSubject = PublishSubject.create<List<Reading>>()
    private val humiditySubject = PublishSubject.create<List<Reading>>()
    private val pressureSubject = PublishSubject.create<List<Reading>>()
    private val magnetometerXSubject = PublishSubject.create<List<Reading>>()
    private val magnetometerYSubject = PublishSubject.create<List<Reading>>()
    private val magnetometerZSubject = PublishSubject.create<List<Reading>>()
    private val accelerometerXSubject = PublishSubject.create<List<Reading>>()
    private val accelerometerYSubject = PublishSubject.create<List<Reading>>()
    private val accelerometerZSubject = PublishSubject.create<List<Reading>>()
    private val gyroscopeXSubject = PublishSubject.create<List<Reading>>()
    private val gyroscopeYSubject = PublishSubject.create<List<Reading>>()
    private val gyroscopeZSubject = PublishSubject.create<List<Reading>>()
    private val batteryLevelSubject = PublishSubject.create<List<Reading>>()

    override fun addReading(reading: Reading) {
        when (reading.type) {
            ReadingType.TEMPERATURE -> {
                addToBuffer(reading, temperatureReadings)
                temperatureSubject.onNext(temperatureReadings)
            }
            ReadingType.LIGHT -> {
                addToBuffer(reading, lightReadings)
                lightSubject.onNext(lightReadings)
            }
            ReadingType.HUMIDITY -> {
                addToBuffer(reading, humidityReadings)
                humiditySubject.onNext(humidityReadings)
            }
            ReadingType.PRESSURE -> {
                addToBuffer(reading, pressureReadings)
                pressureSubject.onNext(pressureReadings)
            }
            ReadingType.MAGNETOMETER_X -> {
                addToBuffer(reading, magnetometerXReadings)
                magnetometerXSubject.onNext(magnetometerXReadings)
            }
            ReadingType.MAGNETOMETER_Y -> {
                addToBuffer(reading, magnetometerYReadings)
                magnetometerYSubject.onNext(magnetometerYReadings)
            }
            ReadingType.MAGNETOMETER_Z -> {
                addToBuffer(reading, magnetometerZReadings)
                magnetometerZSubject.onNext(magnetometerZReadings)
            }
            ReadingType.ACCELEROMETER_X -> {
                addToBuffer(reading, accelerometerXReadings)
                accelerometerXSubject.onNext(accelerometerXReadings)
            }
            ReadingType.ACCELEROMETER_Y -> {
                addToBuffer(reading, accelerometerYReadings)
                accelerometerYSubject.onNext(accelerometerYReadings)
            }
            ReadingType.ACCELEROMETER_Z -> {
                addToBuffer(reading, accelerometerZReadings)
                accelerometerZSubject.onNext(accelerometerZReadings)
            }
            ReadingType.GYROSCOPE_X -> {
                addToBuffer(reading, gyroscopeXReadings)
                gyroscopeXSubject.onNext(gyroscopeXReadings)
            }
            ReadingType.GYROSCOPE_Y -> {
                addToBuffer(reading, gyroscopeYReadings)
                gyroscopeYSubject.onNext(gyroscopeYReadings)
            }
            ReadingType.GYROSCOPE_Z -> {
                addToBuffer(reading, gyroscopeZReadings)
                gyroscopeZSubject.onNext(gyroscopeZReadings)
            }
            ReadingType.BATTERY_LEVEL -> {
                addToBuffer(reading, batteryLevelReadings)
                batteryLevelSubject.onNext(batteryLevelReadings)
            }
        }
    }

    private fun addToBuffer(reading: Reading, readings: MutableList<Reading>) {
        if (readings.size >= BUFFER_SIZE) {
            readings.removeAt(0)
        }
        readings.add(reading)
    }

    override fun getReadingsFor(type: ReadingType): Observable<List<Reading>> {
        return when (type) {
            ReadingType.TEMPERATURE -> temperatureSubject
            ReadingType.LIGHT -> lightSubject
            ReadingType.HUMIDITY -> humiditySubject
            ReadingType.PRESSURE -> pressureSubject
            ReadingType.MAGNETOMETER_X -> magnetometerXSubject
            ReadingType.MAGNETOMETER_Y -> magnetometerYSubject
            ReadingType.MAGNETOMETER_Z -> magnetometerZSubject
            ReadingType.ACCELEROMETER_X -> accelerometerXSubject
            ReadingType.ACCELEROMETER_Y -> accelerometerYSubject
            ReadingType.ACCELEROMETER_Z -> accelerometerZSubject
            ReadingType.GYROSCOPE_X -> gyroscopeXSubject
            ReadingType.GYROSCOPE_Y -> gyroscopeYSubject
            ReadingType.GYROSCOPE_Z -> gyroscopeZSubject
            ReadingType.BATTERY_LEVEL -> batteryLevelSubject
            ReadingType.OTHER -> Observable.empty<List<Reading>>()
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