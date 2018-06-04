package com.aconno.acnsensa.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.support.v4.content.ContextCompat
import com.aconno.acnsensa.R
import com.aconno.acnsensa.domain.interactor.filter.FilterReadingsByMacAddressUseCase
import com.aconno.acnsensa.domain.model.SensorReading
import com.aconno.acnsensa.domain.model.SensorTypeSingle
import com.aconno.acnsensa.model.DataSeriesSettings
import com.aconno.acnsensa.ui.graph.BleDataSeries
import com.aconno.acnsensa.ui.graph.BleGraph
import com.aconno.acnsensa.ui.graph.GraphType
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable

class LiveGraphViewModel(
    private val sensorReadings: Flowable<List<SensorReading>>,
    private val filterReadingsByMacAddressUseCase: FilterReadingsByMacAddressUseCase,
    application: Application
) : AndroidViewModel(application) {
    private val refreshTimestamp: MutableLiveData<Long> = MutableLiveData()

    private var disposable: Disposable? = null

    fun setMacAddress(macAddress: String) {
        disposable?.dispose()
        disposable = sensorReadings.concatMap {
            filterReadingsByMacAddressUseCase.execute(it, macAddress).toFlowable()
        }.subscribe { processSensorValues(it) }
    }

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

    private fun processSensorValues(sensorReadings: List<SensorReading>) {
        when (graphType) {
            GraphType.TEMPERATURE ->
                sensorReadings.filter { it.sensorType == SensorTypeSingle.TEMPERATURE }
                    .map { temperatureSeries.updateDataSet(it.timestamp, it.value) }
            GraphType.LIGHT ->
                sensorReadings.filter { it.sensorType == SensorTypeSingle.LIGHT }
                    .map { lightSeries.updateDataSet(it.timestamp, it.value) }
            GraphType.HUMIDITY ->
                sensorReadings.filter { it.sensorType == SensorTypeSingle.HUMIDITY }
                    .map { humiditySeries.updateDataSet(it.timestamp, it.value) }
            GraphType.PRESSURE ->
                sensorReadings.filter { it.sensorType == SensorTypeSingle.PRESSURE }
                    .map { pressureSeries.updateDataSet(it.timestamp, it.value) }
            GraphType.MAGNETOMETER -> {
                sensorReadings.filter { it.sensorType == SensorTypeSingle.MAGNETOMETER_X }
                    .map { xMagnetometerSeries.updateDataSet(it.timestamp, it.value) }
                sensorReadings.filter { it.sensorType == SensorTypeSingle.MAGNETOMETER_Y }
                    .map { yMagnetometerSeries.updateDataSet(it.timestamp, it.value) }
                sensorReadings.filter { it.sensorType == SensorTypeSingle.MAGNETOMETER_Z }
                    .map { zMagnetometerSeries.updateDataSet(it.timestamp, it.value) }
            }
            GraphType.ACCELEROMETER -> {
                sensorReadings.filter { it.sensorType == SensorTypeSingle.ACCELEROMETER_X }
                    .map { xAccelerometerSeries.updateDataSet(it.timestamp, it.value) }
                sensorReadings.filter { it.sensorType == SensorTypeSingle.ACCELEROMETER_Y }
                    .map { yAccelerometerSeries.updateDataSet(it.timestamp, it.value) }
                sensorReadings.filter { it.sensorType == SensorTypeSingle.ACCELEROMETER_Z }
                    .map { zAccelerometerSeries.updateDataSet(it.timestamp, it.value) }
            }
            GraphType.GYROSCOPE -> {
                sensorReadings.filter { it.sensorType == SensorTypeSingle.GYROSCOPE_X }
                    .map { xGyroscopeSeries.updateDataSet(it.timestamp, it.value) }
                sensorReadings.filter { it.sensorType == SensorTypeSingle.GYROSCOPE_Y }
                    .map { yGyroscopeSeries.updateDataSet(it.timestamp, it.value) }
                sensorReadings.filter { it.sensorType == SensorTypeSingle.GYROSCOPE_Z }
                    .map { zGyroscopeSeries.updateDataSet(it.timestamp, it.value) }
            }
            GraphType.BATTERY_LEVEL ->
                sensorReadings.filter { it.sensorType == SensorTypeSingle.BATTERY_LEVEL }
                    .map { batteryLevelSeries.updateDataSet(it.timestamp, it.value) }
            else -> throw IllegalArgumentException()
        }

        refreshTimestamp.value = System.currentTimeMillis()

    }
}