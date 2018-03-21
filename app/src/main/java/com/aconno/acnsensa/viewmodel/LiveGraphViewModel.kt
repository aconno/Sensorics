package com.aconno.acnsensa.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.aconno.acnsensa.domain.interactor.bluetooth.GetReadingsUseCase
import com.aconno.acnsensa.domain.model.SensorType
import com.aconno.acnsensa.domain.model.readings.*
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import io.reactivex.Flowable

/**
 * @aconno
 */
class LiveGraphViewModel(
    private val sensorValues: Flowable<Map<String, Number>>,
    private val getReadingsUseCase: GetReadingsUseCase
) : ViewModel() {
    private val refreshTimestamp: MutableLiveData<Long> = MutableLiveData()

    fun getUpdates(): MutableLiveData<Long> {
        return refreshTimestamp
    }

    //TODO Custom setter to check if graph type is valid.
    var graphType: Int = -1

    private val temperatureSeries = BleDataSeries("Temperature")
    private val lightSeries = BleDataSeries("Light")
    private val humiditySeries = BleDataSeries("Humidity")
    private val pressureSeries = BleDataSeries("Pressure")
    private val xMagnetometerSeries = BleDataSeries("Magnetometer X")
    private val yMagnetometerSeries = BleDataSeries("Magnetometer Y")
    private val zMagnetometerSeries = BleDataSeries("Magnetometer Z")
    private val xAccelerometerSeries = BleDataSeries("Accelerometer X")
    private val yAccelerometerSeries = BleDataSeries("Accelerometer Y")
    private val zAccelerometerSeries = BleDataSeries("Accelerometer Z")
    private val xGyroscopeSeries = BleDataSeries("Gyroscope X")
    private val yGyroscopeSeries = BleDataSeries("Gyroscope Y")
    private val zGyroscopeSeries = BleDataSeries("Gyroscope Z")
    private val batteryLevelSeries = BleDataSeries("Battery Level")

    private val temperatureGraph =
        BleGraph("Temperature", "Temperature Graph", listOf(temperatureSeries))
    private val lightGraph = BleGraph("Light", "Light Graph", listOf(lightSeries))
    private val humidityGraph = BleGraph("Humidity", "Humidity Graph", listOf(humiditySeries))
    private val pressureGraph = BleGraph("Pressure", "Pressure Graph", listOf(pressureSeries))
    private val magnetometerGraph = BleGraph(
        "Magnetometer",
        "Magnetometer Graph",
        listOf(xMagnetometerSeries, yMagnetometerSeries, zMagnetometerSeries)
    )
    private val accelerometerGraph = BleGraph(
        "Accelerometer",
        "Accelerometer Graph",
        listOf(xAccelerometerSeries, yAccelerometerSeries, zAccelerometerSeries)
    )
    private val gyroscopeGraph = BleGraph(
        "Gyroscope",
        "Gyroscope Graph",
        listOf(xGyroscopeSeries, yGyroscopeSeries, zGyroscopeSeries)
    )

    private val batteryLevelGraph =
        BleGraph("Battery Level", "Battery Level Graph", listOf(batteryLevelSeries))

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


class BleDataSeries(val title: String) {
    private val entries: MutableList<Entry> = mutableListOf(Entry(0f, 0f))

    val lineDataSet = LineDataSet(entries, title)

    init {
        //lineDataSet.color = ContextCompat.getColor()
    }

    fun updateDataSet(newEntries: List<Pair<Long, Number>>) {

        entries.clear()

        if (!newEntries.isEmpty()) {
            val timeZero = newEntries[0].first
            newEntries.forEach { (timestamp, value) ->
                val entry = Entry((timestamp - timeZero).toFloat(), value.toFloat())
                lineDataSet.addEntry(entry)
            }
        } else {
            lineDataSet.addEntry(Entry(0f, 0f))
        }
        lineDataSet.notifyDataSetChanged()
    }
}

class BleGraph(val title: String, private val description: String, series: List<BleDataSeries>) {


    val lineData = LineData(series.map { it.lineDataSet })


    init {
        //lineDataSet.color = ContextCompat.getColor()
    }

    fun getDescription(): Description {
        val description = Description()
        description.text = this.description
        return description
    }
}

object GraphType {
    const val TEMPERATURE: Int = 1
    const val LIGHT: Int = 2
    const val HUMIDITY: Int = 3
    const val PRESSURE: Int = 4
    const val MAGNETOMETER: Int = 5
    const val ACCELEROMETER: Int = 6
    const val GYROSCOPE: Int = 7
    const val BATTERY_LEVEL: Int = 8
}