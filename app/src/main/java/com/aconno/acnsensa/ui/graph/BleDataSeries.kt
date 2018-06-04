package com.aconno.acnsensa.ui.graph

import com.aconno.acnsensa.domain.model.SensorReading
import com.aconno.acnsensa.model.DataSeriesSettings
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet

class BleDataSeries(val title: String, settings: DataSeriesSettings) {

    private val timeStart = System.currentTimeMillis()
    val lineDataSet = LineDataSet(mutableListOf(Entry(0f, 0f)), title)

    init {
        lineDataSet.color = settings.color
        lineDataSet.lineWidth = settings.lineWidth
        lineDataSet.circleRadius = settings.circleRadius
    }

    fun updateDataSet(sensorReadings: List<SensorReading>) {
        lineDataSet.clear()
        if (sensorReadings.isEmpty()) {
            lineDataSet.addEntry(Entry(0f, 0f))
        } else {
            sensorReadings.forEach {
                val entry = Entry((it.timestamp - timeStart).toFloat(), it.value.toFloat())
                lineDataSet.addEntry(entry)
            }
        }
        lineDataSet.notifyDataSetChanged()
    }
}