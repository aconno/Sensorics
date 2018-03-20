package com.aconno.acnsensa.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.aconno.acnsensa.domain.interactor.bluetooth.GetSensorValuesUseCase
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
    private val getSensorValuesUseCase: GetSensorValuesUseCase
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

    val temperatureGraph = BleGraph("Temperature", "Temperature Graph", listOf(temperatureSeries))
    val lightGraph = BleGraph("Light", "Light Graph", listOf(lightSeries))
    val humidityGraph = BleGraph("Humidity", "Humidity Graph", listOf(humiditySeries))
    val pressureGraph = BleGraph("Test", "Description", listOf(pressureSeries))
    val magnetometerGraph = BleGraph(
        "Test",
        "Description",
        listOf(xMagnetometerSeries, yMagnetometerSeries, zMagnetometerSeries)
    )
    val accelerometerGraph = BleGraph(
        "Test",
        "Description",
        listOf(xAccelerometerSeries, yAccelerometerSeries, zAccelerometerSeries)
    )
    val gyroscopeGraph = BleGraph(
        "Test",
        "Description",
        listOf(xGyroscopeSeries, yGyroscopeSeries, zGyroscopeSeries)
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
                getSensorValuesUseCase
                    .execute(SensorType.TEMPERATURE)
                    .subscribe { readings -> updateTemperatureValues(readings) }
            GraphType.LIGHT ->
                getSensorValuesUseCase
                    .execute(SensorType.LIGHT)
                    .subscribe { readings -> updateLightValues(readings) }
            GraphType.HUMIDITY ->
                getSensorValuesUseCase
                    .execute(SensorType.HUMIDITY)
                    .subscribe { readings -> updateHumidityValues(readings) }
            GraphType.PRESSURE ->
                getSensorValuesUseCase
                    .execute(SensorType.PRESSURE)
                    .subscribe { readings -> updatePressureValues(readings) }
            GraphType.MAGNETOMETER -> updateMagnetometerValues()
            GraphType.ACCELEROMETER -> updateAccelerometerValues()
            GraphType.GYROSCOPE -> updateGyroscopeValues()
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

    private fun updateMagnetometerValues() {

    }

    private fun updateAccelerometerValues() {

    }

    private fun updateGyroscopeValues() {

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

class BleGraph(val title: String, val description: String, val series: List<BleDataSeries>) {


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
}