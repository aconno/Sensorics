package com.aconno.acnsensa.ui.graph

import com.aconno.acnsensa.model.DataSeriesSettings
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet

class BleDataSeries(val title: String, settings: DataSeriesSettings) {

    val timeStart = System.currentTimeMillis()
    val lineDataSet = LineDataSet(mutableListOf(Entry(0f, 0f)), title)

    init {
        lineDataSet.color = settings.color
        lineDataSet.lineWidth = settings.lineWidth
        lineDataSet.circleRadius = settings.circleRadius
    }

    fun updateDataSet(timestamp: Long, value: Number) {
        val entry = Entry((timestamp - timeStart).toFloat(), value.toFloat())
        lineDataSet.addEntry(entry)
        lineDataSet.notifyDataSetChanged()
    }
}