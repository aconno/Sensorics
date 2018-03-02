package com.aconno.acnsensa.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import io.reactivex.Flowable

/**
 * @aconno
 */
class LiveGraphViewModel(
) : ViewModel() {
    val updates: MutableLiveData<Unit> = MutableLiveData()

    private val dataSeries = BleDataSeries("test")
    private val dataSeries2 = BleDataSeries("test2")

    val graph = BleGraph("Test", "Description", listOf(dataSeries, dataSeries2))

    fun getGraph(type: Int): BleGraph {
        return graph
    }

    init {
        subscribe()
    }

    private fun subscribe() {
        val type = 0
        val observable: Flowable<List<Pair<Long, Number>>> = getObservableSensorReadings(type)
        observable.subscribe {
            dataSeries.updateDataSet(it)
            updates.value = Unit
        }

        val type2 = 1
        val observable2: Flowable<List<Pair<Long, Number>>> = getObservableSensorReadings(type2)
        observable2.subscribe {
            dataSeries2.updateDataSet(it)
            updates.value = Unit
        }
    }

    private fun getObservableSensorReadings(type: Int): Flowable<List<Pair<Long, Number>>> {
        return when (type) {
            0 -> Flowable.fromArray(mockReadings())
            1 -> Flowable.fromArray(mockReadings2())
            else -> throw IllegalArgumentException()
        }

    }

    private fun mockReadings(): List<Pair<Long, Number>> {
        return arrayOf(Pair(0L, 0), Pair(1L, 1), Pair(2L, 2), Pair(3L, 3)).toList()
    }

    private fun mockReadings2(): List<Pair<Long, Number>> {
        return arrayOf(Pair(0L, 0), Pair(-1L, -1), Pair(-2L, -2), Pair(-3L, -3)).toList()
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
        newEntries.forEach { (timestamp, value) ->
            val entry = Entry(timestamp.toFloat(), value.toFloat())
            lineDataSet.addEntry(entry)
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