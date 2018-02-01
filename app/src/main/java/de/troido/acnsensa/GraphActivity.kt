package de.troido.acnsensa

import android.content.Context
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import de.troido.acnsensa.data.*
import de.troido.acnsensa.service.SensorBinder
import de.troido.acnsensa.service.SensorBleService
import de.troido.ekstend.android.services.bindService
import de.troido.ekstend.android.services.subscriptionConnection
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

private const val LIMIT: Int = 500

class GraphActivity : AppCompatActivity() {

    private val receiver: PublishSubject<Sensor<*>> = PublishSubject.create()

    /**
     * Updates the UI on each data observation through the bound service.
     */
    private val conn = subscriptionConnection<SensorData, SensorBinder> {
        when (it) {
            is SensorRaw -> it.asList().forEach { receiver.onNext(it) }
        }
    }


    private val values: Flowable<Sensor<*>> =
        receiver.toFlowable(BackpressureStrategy.LATEST)

    private interface BleGraph<T> {
        val description: Description
        val dataPoints: Flowable<T>
        val lineData: LineData
        fun addEntry(timestamp: Float, value: T)
    }

    private class TemperatureGraph(
        title: String,
        description: Description,
        values: Flowable<Sensor<*>>
    ) : ValueGraph(title, description, values) {

        override fun extractValue(sensor: Sensor<*>) =
            if (sensor is Temperature) {
                sensor.value
            } else {
                null
            }
    }

    private class LightGraph(
        title: String,
        description: Description,
        values: Flowable<Sensor<*>>
    ) : ValueGraph(title, description, values) {

        override fun extractValue(sensor: Sensor<*>) =
            if (sensor is Light) {
                sensor.value
            } else {
                null
            }
    }

    private class HumidityGraph(
        title: String,
        description: Description,
        values: Flowable<Sensor<*>>
    ) : ValueGraph(title, description, values) {

        override fun extractValue(sensor: Sensor<*>) =
            if (sensor is Humidity) {
                sensor.value
            } else {
                null
            }
    }

    private class PressureGraph(
        title: String,
        description: Description,
        values: Flowable<Sensor<*>>
    ) : ValueGraph(title, description, values) {

        override fun extractValue(sensor: Sensor<*>) =
            if (sensor is Pressure) {
                sensor.value
            } else {
                null
            }
    }

    private class MagnetometerGraph(
        context: Context,
        title: String,
        description: Description,
        values: Flowable<Sensor<*>>
    ) : TripleGraph(context, title, description, values) {

        override fun extractValue(sensor: Sensor<*>) =
            if (sensor is MagnetometerAxis) {
                when (sensor.axis) {
                    Axis.X -> Pair(1, sensor.value)
                    Axis.Y -> Pair(2, sensor.value)
                    Axis.Z -> Pair(3, sensor.value)
                }
            } else {
                null
            }
    }

    private class GyroscopeGraph(
        context: Context,
        title: String,
        description: Description,
        values: Flowable<Sensor<*>>
    ) : TripleGraph(context, title, description, values) {

        override fun extractValue(sensor: Sensor<*>) =
            if (sensor is GyroscopeAxis) {
                when (sensor.axis) {
                    Axis.X -> Pair(1, sensor.value)
                    Axis.Y -> Pair(2, sensor.value)
                    Axis.Z -> Pair(3, sensor.value)
                }
            } else {
                null
            }
    }

    private class AccelerometerGraph(
        context: Context,
        title: String,
        description: Description,
        values: Flowable<Sensor<*>>
    ) : TripleGraph(context, title, description, values) {
        override fun extractValue(sensor: Sensor<*>) =
            if (sensor is AccelerometerAxis) {
                when (sensor.axis) {
                    Axis.X -> Pair(1, sensor.value)
                    Axis.Y -> Pair(2, sensor.value)
                    Axis.Z -> Pair(3, sensor.value)
                }
            } else {
                null
            }
    }

    private abstract class ValueGraph(
        title: String,
        override val description: Description,
        values: Flowable<Sensor<*>>
    ) : BleGraph<Float> {

        abstract fun extractValue(sensor: Sensor<*>): Float?

        override val dataPoints: Flowable<Float> = values
            .map {
                extractValue(it) ?: Float.MAX_VALUE
            }
            .filter { it != Float.MAX_VALUE }

        private val entries: MutableList<Entry> = mutableListOf(Entry(0f, 0f))
        private val dataSet = LineDataSet(entries, title)
        override val lineData = LineData(dataSet)

        override fun addEntry(timestamp: Float, value: Float) {
            val lastTimestamp = entries.last().x
            if (timestamp - lastTimestamp > 1000) {
                val entry = Entry(timestamp, value)
                if (entries.size > LIMIT) {
                    dataSet.removeFirst()
                }
                dataSet.addEntry(entry)
                dataSet.notifyDataSetChanged()
                lineData.notifyDataChanged()
            }
        }
    }

    private abstract class TripleGraph(
        context: Context,
        title: String,
        override val description: Description,
        values: Flowable<Sensor<*>>
    ) : BleGraph<Pair<Int, Float>> {

        abstract fun extractValue(sensor: Sensor<*>): Pair<Int, Float>?

        override val dataPoints: Flowable<Pair<Int, Float>> = values
            .map {
                extractValue(it) ?: Pair(-1, Float.MAX_VALUE)
            }
            .filter {
                it.second != Float.MAX_VALUE
            }

        private val xEntries: MutableList<Entry> = mutableListOf(Entry(0f, 0f))
        private val yEntries: MutableList<Entry> = mutableListOf(Entry(0f, 0f))
        private val zEntries: MutableList<Entry> = mutableListOf(Entry(0f, 0f))

        private val xDataSet = LineDataSet(xEntries, "$title x")
        private val yDataSet = LineDataSet(yEntries, "$title y")
        private val zDataSet = LineDataSet(zEntries, "$title z")

        init {
            xDataSet.color = ContextCompat.getColor(context, R.color.graph_x)
            yDataSet.color = ContextCompat.getColor(context, R.color.graph_y)
            zDataSet.color = ContextCompat.getColor(context, R.color.graph_z)
        }

        override val lineData = LineData(xDataSet, yDataSet, zDataSet)

        override fun addEntry(timestamp: Float, value: Pair<Int, Float>) {

            when (value.first) {
                1 -> {
                    val lastTimestamp = xEntries.last().x
                    if (timestamp - lastTimestamp > 1000) {
                        val xEntry = Entry(timestamp, value.second)
                        if (xEntries.size > LIMIT) {
                            xDataSet.removeFirst()
                        }
                        xDataSet.addEntry(xEntry)
                        xDataSet.notifyDataSetChanged()
                    }

                }
                2 -> {
                    val lastTimestamp = yEntries.last().x
                    if (timestamp - lastTimestamp > 1000) {
                        val yEntry = Entry(timestamp, value.second)
                        if (yEntries.size > LIMIT) {
                            yDataSet.removeFirst()
                        }
                        yDataSet.addEntry(yEntry)
                        yDataSet.notifyDataSetChanged()
                    }

                }
                3 -> {
                    val lastTimestamp = zEntries.last().x
                    if (timestamp - lastTimestamp > 1000) {
                        val zEntry = Entry(timestamp, value.second)
                        if (zEntries.size > LIMIT) {
                            zDataSet.removeFirst()
                        }
                        zDataSet.addEntry(zEntry)
                        zDataSet.notifyDataSetChanged()
                    }
                }
            }

            lineData.notifyDataChanged()


        }
    }

    private val initialTime = System.currentTimeMillis()

    private lateinit var toolbar: Toolbar
    private lateinit var chart: LineChart
    private lateinit var graphTabs: TabLayout

    private lateinit var temperatureGraph: BleGraph<Float>
    private lateinit var lightGraph: BleGraph<Float>
    private lateinit var humidityGraph: BleGraph<Float>
    private lateinit var pressureGraph: BleGraph<Float>
    private lateinit var magnetometerGraph: BleGraph<Pair<Int, Float>>
    private lateinit var gyroscopeGraph: BleGraph<Pair<Int, Float>>
    private lateinit var accelerometerGraph: BleGraph<Pair<Int, Float>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)

        toolbar = findViewById(R.id.graphs_toolbar)
        chart = findViewById(R.id.chart)
        graphTabs = findViewById(R.id.graph_tabs)

        graphTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> displayGraph(temperatureGraph, getString(R.string.temperature_chart_title))
                    1 -> displayGraph(lightGraph, getString(R.string.light_chart_title))
                    2 -> displayGraph(humidityGraph, getString(R.string.humidity_chart_title))
                    3 -> displayGraph(pressureGraph, getString(R.string.pressure_chart_title))
                    4 -> displayGraph(
                            magnetometerGraph,
                            getString(R.string.magnetometer_chart_title)
                    )
                    5 -> displayGraph(gyroscopeGraph, getString(R.string.gyro_chart_title))
                    6 -> displayGraph(
                            accelerometerGraph,
                            getString(R.string.accelerometer_chart_title)
                    )
                }
            }
        })

        val temperatureGraphDescription = Description()
        temperatureGraphDescription.text = getString(R.string.temperature_chart_description)
        temperatureGraph = TemperatureGraph(
                getString(R.string.temperature_chart_title),
                temperatureGraphDescription,
                values
        )

        val lightGraphDescription = Description()
        lightGraphDescription.text = getString(R.string.light_chart_description)
        lightGraph = LightGraph(
                getString(R.string.light_chart_title),
                lightGraphDescription,
                values
        )

        val humidityGraphDescription = Description()
        humidityGraphDescription.text = getString(R.string.humidity_chart_description)
        humidityGraph = HumidityGraph(
                getString(R.string.humidity_chart_title),
                humidityGraphDescription,
                values
        )

        val pressureGraphDescription = Description()
        pressureGraphDescription.text = getString(R.string.pressure_chart_description)
        pressureGraph = PressureGraph(
                getString(R.string.pressure_chart_title),
                pressureGraphDescription,
                values
        )

        val magnetometerGraphDescription = Description()
        magnetometerGraphDescription.text = getString(R.string.magnetometer_chart_description)
        magnetometerGraph = MagnetometerGraph(
                this,
                getString(R.string.magnetometer_chart_title),
                magnetometerGraphDescription,
                values
        )

        val gyroscopeGraphDescription = Description()
        gyroscopeGraphDescription.text = getString(R.string.gyro_chart_description)
        gyroscopeGraph = GyroscopeGraph(
                this,
                getString(R.string.gyro_chart_title),
                gyroscopeGraphDescription,
                values
        )

        val accelerometerGraphDescription = Description()
        accelerometerGraphDescription.text = getString(R.string.accelerometer_chart_description)
        accelerometerGraph = AccelerometerGraph(
                this,
                getString(R.string.accelerometer_chart_title),
                accelerometerGraphDescription,
                values
        )

        subscribeAll()
        displayGraph(temperatureGraph, getString(R.string.temperature_chart_title))
    }

    private fun subscribeAll() {
        subscribe(temperatureGraph)
        subscribe(lightGraph)
        subscribe(humidityGraph)
        subscribe(pressureGraph)
        subscribe(magnetometerGraph)
        subscribe(gyroscopeGraph)
        subscribe(accelerometerGraph)
    }

    private fun <T> subscribe(graph: BleGraph<T>) {
        graph.dataPoints.subscribeOn(Schedulers.io()).subscribe {
            graph.addEntry((System.currentTimeMillis() - initialTime).toFloat(), it)
            chart.notifyDataSetChanged()
            chart.invalidate()
        }
    }

    private fun <T> displayGraph(graph: BleGraph<T>, title: String) {
        toolbar.title = title

        chart.description = graph.description
        chart.data = graph.lineData
        chart.invalidate()
    }

    override fun onResume() {
        super.onResume()
        bindService<SensorBleService>(conn)
    }

    override fun onPause() {
        super.onPause()
        unbindService(conn)
        finish()
    }
}