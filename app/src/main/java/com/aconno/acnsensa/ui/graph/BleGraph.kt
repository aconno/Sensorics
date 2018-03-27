package com.aconno.acnsensa.ui.graph

import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.LineData

/**
 * @author aconno
 */
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