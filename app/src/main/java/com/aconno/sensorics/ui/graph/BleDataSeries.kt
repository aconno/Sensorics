package com.aconno.sensorics.ui.graph

import com.aconno.sensorics.domain.model.Reading
import com.aconno.sensorics.model.DataSeriesSettings
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

    fun updateDataSet(readings: List<Reading>) {
        lineDataSet.clear()
        if (readings.isEmpty()) {
            lineDataSet.addEntry(Entry(0f, 0f))
        } else {
            readings.forEach {
                val entry = Entry((it.timestamp - timeStart).toFloat(), it.value.toFloat())
                lineDataSet.addEntry(entry)
            }
        }
        lineDataSet.notifyDataSetChanged()
    }
}