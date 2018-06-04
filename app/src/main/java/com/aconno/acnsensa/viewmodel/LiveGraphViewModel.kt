package com.aconno.acnsensa.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.support.v4.content.ContextCompat
import com.aconno.acnsensa.R
import com.aconno.acnsensa.domain.interactor.bluetooth.GetSensorReadingsUseCase
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
    private val getSensorReadingsUseCase: GetSensorReadingsUseCase,
    application: Application
) : AndroidViewModel(application) {
    private val refreshTimestamp: MutableLiveData<Long> = MutableLiveData()

    private var disposable: Disposable? = null

    private lateinit var macAddress: String

    fun setMacAddress(macAddress: String) {
        this.macAddress = macAddress
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
                getSensorReadingsUseCase.execute(SensorTypeSingle.TEMPERATURE)
                    .subscribe { temperatureReadings ->
                        val filtered = temperatureReadings.filter {
                            it.device.macAddress == macAddress
                        }
                        temperatureSeries.updateDataSet(filtered)
                    }
            GraphType.LIGHT ->
                getSensorReadingsUseCase.execute(SensorTypeSingle.LIGHT)
                    .subscribe { lightReadings ->
                        val filtered = lightReadings.filter {
                            it.device.macAddress == macAddress
                        }
                        lightSeries.updateDataSet(filtered)
                    }
            GraphType.HUMIDITY ->
                getSensorReadingsUseCase.execute(SensorTypeSingle.HUMIDITY)
                    .subscribe { humidityReadings ->
                        val filtered = humidityReadings.filter {
                            it.device.macAddress == macAddress
                        }
                        humiditySeries.updateDataSet(filtered)
                    }
            GraphType.PRESSURE ->
                getSensorReadingsUseCase.execute(SensorTypeSingle.PRESSURE)
                    .subscribe { pressureReadings ->
                        val filtered = pressureReadings.filter {
                            it.device.macAddress == macAddress
                        }
                        pressureSeries.updateDataSet(filtered)
                    }
            GraphType.MAGNETOMETER -> {
                getSensorReadingsUseCase.execute(SensorTypeSingle.MAGNETOMETER_X)
                    .subscribe { magnetometerXReadings ->
                        val filtered = magnetometerXReadings.filter {
                            it.device.macAddress == macAddress
                        }
                        xMagnetometerSeries.updateDataSet(filtered)
                    }
                getSensorReadingsUseCase.execute(SensorTypeSingle.MAGNETOMETER_Y)
                    .subscribe { magnetometerYReadings ->
                        val filtered = magnetometerYReadings.filter {
                            it.device.macAddress == macAddress
                        }
                        yMagnetometerSeries.updateDataSet(filtered)
                    }
                getSensorReadingsUseCase.execute(SensorTypeSingle.MAGNETOMETER_Z)
                    .subscribe { magnetometerZReadings ->
                        val filtered = magnetometerZReadings.filter {
                            it.device.macAddress == macAddress
                        }
                        zMagnetometerSeries.updateDataSet(filtered)
                    }
            }
            GraphType.ACCELEROMETER -> {
                getSensorReadingsUseCase.execute(SensorTypeSingle.ACCELEROMETER_X)
                    .subscribe { accelerometerXReadings ->
                        val filtered = accelerometerXReadings.filter {
                            it.device.macAddress == macAddress
                        }
                        xAccelerometerSeries.updateDataSet(filtered)
                    }
                getSensorReadingsUseCase.execute(SensorTypeSingle.ACCELEROMETER_Y)
                    .subscribe { accelerometerYReadings ->
                        val filtered = accelerometerYReadings.filter {
                            it.device.macAddress == macAddress
                        }
                        yAccelerometerSeries.updateDataSet(filtered)
                    }
                getSensorReadingsUseCase.execute(SensorTypeSingle.ACCELEROMETER_Z)
                    .subscribe { accelerometerZReadings ->
                        val filtered = accelerometerZReadings.filter {
                            it.device.macAddress == macAddress
                        }
                        zAccelerometerSeries.updateDataSet(filtered)
                    }
            }
            GraphType.GYROSCOPE -> {
                getSensorReadingsUseCase.execute(SensorTypeSingle.GYROSCOPE_X)
                    .subscribe { gyroscopeXReadings ->
                        val filtered = gyroscopeXReadings.filter {
                            it.device.macAddress == macAddress
                        }
                        xGyroscopeSeries.updateDataSet(filtered)
                    }
                getSensorReadingsUseCase.execute(SensorTypeSingle.GYROSCOPE_Y)
                    .subscribe { gyroscopeYReadings ->
                        val filtered = gyroscopeYReadings.filter {
                            it.device.macAddress == macAddress
                        }
                        yGyroscopeSeries.updateDataSet(filtered)
                    }
                getSensorReadingsUseCase.execute(SensorTypeSingle.GYROSCOPE_Z)
                    .subscribe { gyroscopeZReadings ->
                        val filtered = gyroscopeZReadings.filter {
                            it.device.macAddress == macAddress
                        }
                        zGyroscopeSeries.updateDataSet(filtered)
                    }
            }
            GraphType.BATTERY_LEVEL ->
                getSensorReadingsUseCase.execute(SensorTypeSingle.BATTERY_LEVEL)
                    .subscribe { batteryLevelReadings ->
                        val filtered = batteryLevelReadings.filter {
                            it.device.macAddress == macAddress
                        }
                        batteryLevelSeries.updateDataSet(filtered)
                    }
            else -> throw IllegalArgumentException()
        }

        refreshTimestamp.value = System.currentTimeMillis()
    }
}