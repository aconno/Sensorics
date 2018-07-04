package com.aconno.acnsensa.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.support.v4.content.ContextCompat
import com.aconno.acnsensa.R
import com.aconno.acnsensa.domain.interactor.repository.GetReadingsUseCase
import com.aconno.acnsensa.model.DataSeriesSettings
import com.aconno.acnsensa.ui.graph.BleDataSeries
import com.aconno.acnsensa.ui.graph.BleGraph
import com.aconno.acnsensa.ui.graph.GraphType
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable

class LiveGraphViewModel(
    private val getReadingsUseCase: GetReadingsUseCase,
    application: Application
) : AndroidViewModel(application) {

    private val refreshTimestamp: MutableLiveData<Long> = MutableLiveData()

    private var disposable: Disposable? = null
    private var disposableY: Disposable? = null
    private var disposableZ: Disposable? = null

    private lateinit var macAddress: String
    private var graphType: Int = -1

    fun setMacAddressAndGraphType(macAddress: String, graphType: Int) {
        this.macAddress = macAddress
        this.graphType = graphType
        disposable?.dispose()

        when (graphType) {
            GraphType.TEMPERATURE -> {
                disposable?.dispose()
                disposable = getReadingsUseCase.execute("Temperature")
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { readings ->
                        val filtered = readings.filter {
                            it.device.macAddress == macAddress
                        }
                        temperatureSeries.updateDataSet(filtered)
                        refreshTimestamp.value = System.currentTimeMillis()
                    }
            }
            GraphType.LIGHT -> {
                disposable?.dispose()
                disposable = getReadingsUseCase.execute("Light")
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { readings ->
                        val filtered = readings.filter {
                            it.device.macAddress == macAddress
                        }
                        lightSeries.updateDataSet(filtered)
                        refreshTimestamp.value = System.currentTimeMillis()
                    }
            }
            GraphType.HUMIDITY -> {
                disposable?.dispose()
                disposable = getReadingsUseCase.execute("Humidity")
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { readings ->
                        val filtered = readings.filter {
                            it.device.macAddress == macAddress
                        }
                        humiditySeries.updateDataSet(filtered)
                        refreshTimestamp.value = System.currentTimeMillis()
                    }
            }
            GraphType.PRESSURE -> {
                disposable?.dispose()
                disposable = getReadingsUseCase.execute("Pressure")
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { readings ->
                        val filtered = readings.filter {
                            it.device.macAddress == macAddress
                        }
                        pressureSeries.updateDataSet(filtered)
                        refreshTimestamp.value = System.currentTimeMillis()
                    }
            }
            GraphType.MAGNETOMETER -> {
                disposable?.dispose()
                disposable = getReadingsUseCase.execute("Magnetometer X")
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { readings ->
                        val filtered = readings.filter {
                            it.device.macAddress == macAddress
                        }
                        xMagnetometerSeries.updateDataSet(filtered)
                        refreshTimestamp.value = System.currentTimeMillis()
                    }
                disposableY?.dispose()
                disposableY = getReadingsUseCase.execute("Magnetometer Y")
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { readings ->
                        val filtered = readings.filter {
                            it.device.macAddress == macAddress
                        }
                        yMagnetometerSeries.updateDataSet(filtered)
                        refreshTimestamp.value = System.currentTimeMillis()
                    }
                disposableZ?.dispose()
                disposableZ = getReadingsUseCase.execute("Magnetometer Z")
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { readings ->
                        val filtered = readings.filter {
                            it.device.macAddress == macAddress
                        }
                        zMagnetometerSeries.updateDataSet(filtered)
                        refreshTimestamp.value = System.currentTimeMillis()
                    }
            }
            GraphType.ACCELEROMETER -> {
                disposable?.dispose()
                disposable = getReadingsUseCase.execute("Accelerometer X")
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { readings ->
                        val filtered = readings.filter {
                            it.device.macAddress == macAddress
                        }
                        xAccelerometerSeries.updateDataSet(filtered)
                        refreshTimestamp.value = System.currentTimeMillis()
                    }
                disposableY?.dispose()
                disposableY = getReadingsUseCase.execute("Accelerometer Y")
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { readings ->
                        val filtered = readings.filter {
                            it.device.macAddress == macAddress
                        }
                        yAccelerometerSeries.updateDataSet(filtered)
                        refreshTimestamp.value = System.currentTimeMillis()
                    }
                disposableZ?.dispose()
                disposableZ = getReadingsUseCase.execute("Accelerometer Z")
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { readings ->
                        val filtered = readings.filter {
                            it.device.macAddress == macAddress
                        }
                        zAccelerometerSeries.updateDataSet(filtered)
                        refreshTimestamp.value = System.currentTimeMillis()
                    }
            }
            GraphType.GYROSCOPE -> {
                disposable?.dispose()
                disposable = getReadingsUseCase.execute("Gyroscope X")
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { readings ->
                        val filtered = readings.filter {
                            it.device.macAddress == macAddress
                        }
                        xGyroscopeSeries.updateDataSet(filtered)
                        refreshTimestamp.value = System.currentTimeMillis()
                    }
                disposableY?.dispose()
                disposableY = getReadingsUseCase.execute("Gyroscope Y")
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { readings ->
                        val filtered = readings.filter {
                            it.device.macAddress == macAddress
                        }
                        yGyroscopeSeries.updateDataSet(filtered)
                        refreshTimestamp.value = System.currentTimeMillis()
                    }
                disposableZ?.dispose()
                disposableZ = getReadingsUseCase.execute("Gyroscope Z")
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { readings ->
                        val filtered = readings.filter {
                            it.device.macAddress == macAddress
                        }
                        zGyroscopeSeries.updateDataSet(filtered)
                        refreshTimestamp.value = System.currentTimeMillis()
                    }
            }
            GraphType.BATTERY_LEVEL -> {
                disposable?.dispose()
                disposable = getReadingsUseCase.execute("Battery Level")
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { readings ->
                        val filtered = readings.filter {
                            it.device.macAddress == macAddress
                        }
                        batteryLevelSeries.updateDataSet(filtered)
                        refreshTimestamp.value = System.currentTimeMillis()
                    }
            }
        }
    }

    fun getUpdates(): MutableLiveData<Long> {
        return refreshTimestamp
    }

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
}