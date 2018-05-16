package com.aconno.acnsensa.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.support.v4.content.ContextCompat
import com.aconno.acnsensa.R
import com.aconno.acnsensa.domain.interactor.bluetooth.GetReadingsUseCase
import com.aconno.acnsensa.domain.model.SensorType
import com.aconno.acnsensa.domain.model.readings.Reading
import com.aconno.acnsensa.model.DataSeriesSettings
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

    private val graphSettingsX = DataSeriesSettings(
        ContextCompat.getColor(application, R.color.graph_x),
        application.resources.getDimension(R.dimen.graph_line_width),
        application.resources.getDimension(R.dimen.graph_circle_radius)
    )

    private val graphSettingsY = DataSeriesSettings(
        ContextCompat.getColor(application, R.color.graph_y),
        application.resources.getDimension(R.dimen.graph_line_width),
        application.resources.getDimension(R.dimen.graph_circle_radius)
    )

    private val graphSettingsZ = DataSeriesSettings(
        ContextCompat.getColor(application, R.color.graph_z),
        application.resources.getDimension(R.dimen.graph_line_width),
        application.resources.getDimension(R.dimen.graph_circle_radius)
    )

    private val temperatureSeries = BleDataSeries(
        getApplication<Application>().getString(R.string.temperature), graphSettingsX
    )

    private val lightSeries = BleDataSeries(
        getApplication<Application>().getString(R.string.light), graphSettingsX
    )

    private val humiditySeries = BleDataSeries(
        getApplication<Application>().getString(R.string.humidity), graphSettingsX
    )

    private val pressureSeries = BleDataSeries(
        getApplication<Application>().getString(R.string.pressure), graphSettingsX
    )

    private val xMagnetometerSeries = BleDataSeries(
        getApplication<Application>().getString(R.string.magnetometer_x), graphSettingsX
    )

    private val yMagnetometerSeries = BleDataSeries(
        getApplication<Application>().getString(R.string.magnetometer_y), graphSettingsY
    )

    private val zMagnetometerSeries = BleDataSeries(
        getApplication<Application>().getString(R.string.magnetometer_z), graphSettingsZ
    )

    private val xAccelerometerSeries = BleDataSeries(
        getApplication<Application>().getString(R.string.accelerometer_x), graphSettingsX
    )

    private val yAccelerometerSeries = BleDataSeries(
        getApplication<Application>().getString(R.string.accelerometer_y), graphSettingsY
    )

    private val zAccelerometerSeries = BleDataSeries(
        getApplication<Application>().getString(R.string.accelerometer_z), graphSettingsZ
    )

    private val xGyroscopeSeries = BleDataSeries(
        getApplication<Application>().getString(R.string.gyro_x), graphSettingsX
    )

    private val yGyroscopeSeries = BleDataSeries(
        getApplication<Application>().getString(R.string.gyro_y), graphSettingsY
    )

    private val zGyroscopeSeries = BleDataSeries(
        getApplication<Application>().getString(R.string.gyro_z), graphSettingsZ
    )

    private val batteryLevelSeries = BleDataSeries(
        getApplication<Application>().getString(R.string.battery_level), graphSettingsX
    )

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

    private fun updateTemperatureValues(readings: List<Reading>) {
        val temperatureReadings: List<Reading> =
            readings.filter { reading -> reading.sensorType == SensorType.TEMPERATURE }

        val dataPoints = temperatureReadings.map { Pair(it.timestamp, it.values[0]) }
        temperatureSeries.updateDataSet(dataPoints)
    }

    private fun updateLightValues(readings: List<Reading>) {
        val lightReadings: List<Reading> =
            readings.filter { reading -> reading.sensorType == SensorType.LIGHT }
        val dataPoints = lightReadings.map { Pair(it.timestamp, it.values[0]) }
        lightSeries.updateDataSet(dataPoints)
    }

    private fun updateHumidityValues(readings: List<Reading>) {
        val humidityReadings: List<Reading> =
            readings.filter { reading -> reading.sensorType == SensorType.HUMIDITY }
        val dataPoints = humidityReadings.map { Pair(it.timestamp, it.values[0]) }
        humiditySeries.updateDataSet(dataPoints)
    }

    private fun updatePressureValues(readings: List<Reading>) {
        val pressureValues: List<Reading> =
            readings.filter { reading -> reading.sensorType == SensorType.PRESSURE }
        val dataPoints = pressureValues.map { Pair(it.timestamp, it.values[0]) }
        pressureSeries.updateDataSet(dataPoints)
    }

    private fun updateMagnetometerValues(readings: List<Reading>) {
        val magnetometerValues: List<Reading> =
            readings.filter { reading -> reading.sensorType == SensorType.MAGNETOMETER }
        val xDataPoints = magnetometerValues.map { Pair(it.timestamp, it.values[0]) }
        val yDataPoints = magnetometerValues.map { Pair(it.timestamp, it.values[1]) }
        val zDataPoints = magnetometerValues.map { Pair(it.timestamp, it.values[2]) }

        xMagnetometerSeries.updateDataSet(xDataPoints)
        yMagnetometerSeries.updateDataSet(yDataPoints)
        zMagnetometerSeries.updateDataSet(zDataPoints)
    }

    private fun updateAccelerometerValues(readings: List<Reading>) {
        val accelerometerValues: List<Reading> =
            readings.filter { reading -> reading.sensorType == SensorType.ACCELEROMETER }
        val xDataPoints = accelerometerValues.map { Pair(it.timestamp, it.values[0]) }
        val yDataPoints = accelerometerValues.map { Pair(it.timestamp, it.values[1]) }
        val zDataPoints = accelerometerValues.map { Pair(it.timestamp, it.values[2]) }

        xAccelerometerSeries.updateDataSet(xDataPoints)
        yAccelerometerSeries.updateDataSet(yDataPoints)
        zAccelerometerSeries.updateDataSet(zDataPoints)
    }

    private fun updateGyroscopeValues(readings: List<Reading>) {
        val gyroscopeValues: List<Reading> =
            readings.filter { reading -> reading.sensorType == SensorType.GYROSCOPE }
        val xDataPoints = gyroscopeValues.map { Pair(it.timestamp, it.values[0]) }
        val yDataPoints = gyroscopeValues.map { Pair(it.timestamp, it.values[1]) }
        val zDataPoints = gyroscopeValues.map { Pair(it.timestamp, it.values[2]) }

        xGyroscopeSeries.updateDataSet(xDataPoints)
        yGyroscopeSeries.updateDataSet(yDataPoints)
        zGyroscopeSeries.updateDataSet(zDataPoints)
    }

    private fun updateBatteryLevelValues(readings: List<Reading>) {
        val batteryReadings: List<Reading> =
            readings.filter { reading -> reading.sensorType == SensorType.BATTERY_LEVEL }
        val dataPoints = batteryReadings.map { Pair(it.timestamp, it.values[0]) }
        batteryLevelSeries.updateDataSet(dataPoints)
    }
}