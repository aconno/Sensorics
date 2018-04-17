package com.aconno.acnsensa.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.aconno.acnsensa.R
import com.aconno.acnsensa.domain.interactor.bluetooth.GetReadingsUseCase
import com.aconno.acnsensa.domain.model.SensorType
import com.aconno.acnsensa.domain.model.readings.*
import com.aconno.acnsensa.ui.graph.BleDataSeries
import com.aconno.acnsensa.ui.graph.BleGraph
import com.aconno.acnsensa.ui.graph.GraphType
import io.reactivex.Flowable

/**
 * @aconno
 */
class LiveGraphViewModel(
    private val sensorValues: Flowable<Map<String, Number>>,
    private val getReadingsUseCase: GetReadingsUseCase,
    application: Application
) : AndroidViewModel(application) {
    private val refreshTimestamp: MutableLiveData<Long> = MutableLiveData()

    fun getUpdates(): MutableLiveData<Long> {
        return refreshTimestamp
    }

    //TODO Custom setter to check if graph type is valid.
    var graphType: Int = -1

    private val temperatureSeries =
        BleDataSeries(getApplication<Application>().getString(R.string.temperature))

    private val lightSeries = BleDataSeries(getApplication<Application>().getString(R.string.light))

    private val humiditySeries =
        BleDataSeries(getApplication<Application>().getString(R.string.humidity))

    private val pressureSeries =
        BleDataSeries(getApplication<Application>().getString(R.string.pressure))

    private val xMagnetometerSeries =
        BleDataSeries(getApplication<Application>().getString(R.string.magnetometer_x))

    private val yMagnetometerSeries =
        BleDataSeries(getApplication<Application>().getString(R.string.magnetometer_y))

    private val zMagnetometerSeries =
        BleDataSeries(getApplication<Application>().getString(R.string.magnetometer_z))

    private val xAccelerometerSeries =
        BleDataSeries(getApplication<Application>().getString(R.string.accelerometer_x))

    private val yAccelerometerSeries =
        BleDataSeries(getApplication<Application>().getString(R.string.accelerometer_y))

    private val zAccelerometerSeries =
        BleDataSeries(getApplication<Application>().getString(R.string.accelerometer_z))

    private val xGyroscopeSeries =
        BleDataSeries(getApplication<Application>().getString(R.string.gyro_x))

    private val yGyroscopeSeries =
        BleDataSeries(getApplication<Application>().getString(R.string.gyro_y))

    private val zGyroscopeSeries =
        BleDataSeries(getApplication<Application>().getString(R.string.gyro_z))

    private val batteryLevelSeries =
        BleDataSeries(getApplication<Application>().getString(R.string.battery_level))

    private val temperatureGraph =
        BleGraph(
            getApplication<Application>().getString(R.string.temperature),
            getApplication<Application>().getString(R.string.temperature_graph_label),
            listOf(temperatureSeries)
        )

    private val lightGraph = BleGraph(
        getApplication<Application>().getString(R.string.light),
        getApplication<Application>().getString(R.string.light_graph_label),
        listOf(lightSeries)
    )
    private val humidityGraph = BleGraph(
        getApplication<Application>().getString(R.string.humidity),
        getApplication<Application>().getString(R.string.humidity_graph_label),
        listOf(humiditySeries)
    )
    private val pressureGraph = BleGraph(
        getApplication<Application>().getString(R.string.pressure),
        getApplication<Application>().getString(R.string.pressure_graph_label),
        listOf(pressureSeries)
    )
    private val magnetometerGraph = BleGraph(
        getApplication<Application>().getString(R.string.magnetometer),
        getApplication<Application>().getString(R.string.magnetometer_graph_label),
        listOf(xMagnetometerSeries, yMagnetometerSeries, zMagnetometerSeries)
    )
    private val accelerometerGraph = BleGraph(
        getApplication<Application>().getString(R.string.accelerometer),
        getApplication<Application>().getString(R.string.accelerometer_graph_label),
        listOf(xAccelerometerSeries, yAccelerometerSeries, zAccelerometerSeries)
    )
    private val gyroscopeGraph = BleGraph(
        getApplication<Application>().getString(R.string.gyro),
        getApplication<Application>().getString(R.string.gyro_graph_label),
        listOf(xGyroscopeSeries, yGyroscopeSeries, zGyroscopeSeries)
    )

    private val batteryLevelGraph =
        BleGraph(
            getApplication<Application>().getString(R.string.battery_level),
            getApplication<Application>().getString(R.string.battery_level_graph_label),
            listOf(batteryLevelSeries)
        )

    fun getGraph(type: Int): BleGraph {
        return when (type) {
            GraphType.TEMPERATURE -> temperatureGraph
            GraphType.LIGHT -> lightGraph
            GraphType.HUMIDITY -> humidityGraph
            GraphType.PRESSURE -> pressureGraph
            GraphType.MAGNETOMETER -> magnetometerGraph
            GraphType.ACCELEROMETER -> accelerometerGraph
            GraphType.GYROSCOPE -> gyroscopeGraph
            GraphType.BATTERY_LEVEL -> batteryLevelGraph
            else -> throw IllegalArgumentException()
        }
    }

    init {
        subscribe()
    }

    private fun subscribe() {
        sensorValues.subscribe { processSensorValues() }
    }

    private fun processSensorValues() {
        when (graphType) {
            GraphType.TEMPERATURE ->
                getReadingsUseCase
                    .execute(SensorType.TEMPERATURE)
                    .subscribe { readings -> updateTemperatureValues(readings) }
            GraphType.LIGHT ->
                getReadingsUseCase
                    .execute(SensorType.LIGHT)
                    .subscribe { readings -> updateLightValues(readings) }
            GraphType.HUMIDITY ->
                getReadingsUseCase
                    .execute(SensorType.HUMIDITY)
                    .subscribe { readings -> updateHumidityValues(readings) }
            GraphType.PRESSURE ->
                getReadingsUseCase
                    .execute(SensorType.PRESSURE)
                    .subscribe { readings -> updatePressureValues(readings) }
            GraphType.MAGNETOMETER ->
                getReadingsUseCase
                    .execute(SensorType.MAGNETOMETER)
                    .subscribe { readings -> updateMagnetometerValues(readings) }
            GraphType.ACCELEROMETER ->
                getReadingsUseCase
                    .execute(SensorType.ACCELEROMETER)
                    .subscribe { readings -> updateAccelerometerValues(readings) }
            GraphType.GYROSCOPE ->
                getReadingsUseCase
                    .execute(SensorType.GYROSCOPE)
                    .subscribe { readings -> updateGyroscopeValues(readings) }
            GraphType.BATTERY_LEVEL ->
                getReadingsUseCase
                    .execute(SensorType.BATTERY_LEVEL)
                    .subscribe { readings -> updateBatteryLevelValues(readings) }
            else -> throw IllegalArgumentException()
        }

        refreshTimestamp.value = System.currentTimeMillis()

    }

    private fun <T> getList(readings: List<Reading>, targetClass: Class<T>): List<T> {
        val typedReadings: List<T> = readings.filterIsInstance(targetClass)
        if (typedReadings.size == readings.size) {
            return typedReadings
        } else {
            throw IllegalStateException("List contains readings of different types.")
        }
    }

    private fun updateTemperatureValues(readings: List<Reading>) {
        val temperatureReadings: List<TemperatureReading> =
            getList(readings, TemperatureReading::class.java)

        val dataPoints = temperatureReadings.map { Pair(it.timestamp, it.temperature) }
        temperatureSeries.updateDataSet(dataPoints)
    }

    private fun updateLightValues(readings: List<Reading>) {
        val lightReadings: List<LightReading> = getList(readings, LightReading::class.java)
        val dataPoints = lightReadings.map { Pair(it.timestamp, it.light) }
        lightSeries.updateDataSet(dataPoints)
    }

    private fun updateHumidityValues(readings: List<Reading>) {
        val humidityReadings: List<HumidityReading> = getList(readings, HumidityReading::class.java)
        val dataPoints = humidityReadings.map { Pair(it.timestamp, it.humidity) }
        humiditySeries.updateDataSet(dataPoints)
    }

    private fun updatePressureValues(readings: List<Reading>) {
        val pressureValues: List<PressureReading> = getList(readings, PressureReading::class.java)
        val dataPoints = pressureValues.map { Pair(it.timestamp, it.pressure) }
        pressureSeries.updateDataSet(dataPoints)
    }

    private fun updateMagnetometerValues(readings: List<Reading>) {
        val magnetometerValues: List<MagnetometerReading> =
            getList(readings, MagnetometerReading::class.java)
        val xDataPoints = magnetometerValues.map { Pair(it.timestamp, it.magnetometerX) }
        val yDataPoints = magnetometerValues.map { Pair(it.timestamp, it.magnetometerY) }
        val zDataPoints = magnetometerValues.map { Pair(it.timestamp, it.magnetometerZ) }

        xMagnetometerSeries.updateDataSet(xDataPoints)
        yMagnetometerSeries.updateDataSet(yDataPoints)
        zMagnetometerSeries.updateDataSet(zDataPoints)
    }

    private fun updateAccelerometerValues(readings: List<Reading>) {
        val accelerometerValues: List<AccelerometerReading> =
            getList(readings, AccelerometerReading::class.java)
        val xDataPoints = accelerometerValues.map { Pair(it.timestamp, it.accelerometerX) }
        val yDataPoints = accelerometerValues.map { Pair(it.timestamp, it.accelerometerY) }
        val zDataPoints = accelerometerValues.map { Pair(it.timestamp, it.accelerometerZ) }

        xAccelerometerSeries.updateDataSet(xDataPoints)
        yAccelerometerSeries.updateDataSet(yDataPoints)
        zAccelerometerSeries.updateDataSet(zDataPoints)
    }

    private fun updateGyroscopeValues(readings: List<Reading>) {
        val gyroscopeValues: List<GyroscopeReading> =
            getList(readings, GyroscopeReading::class.java)
        val xDataPoints = gyroscopeValues.map { Pair(it.timestamp, it.gyroscopeX) }
        val yDataPoints = gyroscopeValues.map { Pair(it.timestamp, it.gyroscopeY) }
        val zDataPoints = gyroscopeValues.map { Pair(it.timestamp, it.gyroscopeZ) }

        xGyroscopeSeries.updateDataSet(xDataPoints)
        yGyroscopeSeries.updateDataSet(yDataPoints)
        zGyroscopeSeries.updateDataSet(zDataPoints)
    }

    private fun updateBatteryLevelValues(readings: List<Reading>) {
        val batteryReadings: List<BatteryReading> = getList(readings, BatteryReading::class.java)
        val dataPoints = batteryReadings.map { Pair(it.timestamp, it.batteryLevel) }
        batteryLevelSeries.updateDataSet(dataPoints)
    }
}