package com.aconno.acnsensa.ui.graph

import com.aconno.acnsensa.model.DataSeriesSettings
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet

/**
 * @author aconno
 */
class BleDataSeries(val title: String, settings: DataSeriesSettings) {
    private val entries: MutableList<Entry> = mutableListOf(Entry(0f, 0f))

    val lineDataSet = LineDataSet(entries, title)

    init {
        lineDataSet.color = settings.color
        lineDataSet.lineWidth = settings.lineWidth
        lineDataSet.circleRadius = settings.circleRadius
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