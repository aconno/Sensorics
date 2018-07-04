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
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable

class LiveGraphViewModel(
    private val getReadingsUseCase: GetReadingsUseCase,
    application: Application
) : AndroidViewModel(application) {

    private val refreshTimestamp: MutableLiveData<Long> = MutableLiveData()

    private val vectorGraphs = listOf("Magnetometer", "Accelerometer", "Gyroscope")
    private var disposable: Disposable? = null
    private var disposableY: Disposable? = null
    private var disposableZ: Disposable? = null

    private lateinit var macAddress: String
    private lateinit var graphName: String

    fun setMacAddressAndGraphName(macAddress: String, graphName: String) {
        this.macAddress = macAddress
        this.graphName = graphName

        if (graphName in vectorGraphs) {
            disposable?.dispose()
            disposable = getReadingsUseCase.execute(macAddress, "$graphName X")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    xDataSeries.updateDataSet(it)
                    refreshTimestamp.value = System.currentTimeMillis()
                }
            disposableY?.dispose()
            disposableY = getReadingsUseCase.execute(macAddress, "$graphName Y")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    yDataSeries.updateDataSet(it)
                    refreshTimestamp.value = System.currentTimeMillis()
                }
            disposableZ?.dispose()
            disposableZ = getReadingsUseCase.execute(macAddress, "$graphName Z")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    zDataSeries.updateDataSet(it)
                    refreshTimestamp.value = System.currentTimeMillis()
                }
        } else {
            disposable?.dispose()
            disposable = getReadingsUseCase.execute(macAddress, graphName)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    dataSeries.updateDataSet(it)
                    refreshTimestamp.value = System.currentTimeMillis()
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

    private val dataSeries = BleDataSeries(
        "data", graphSettingsX
    )

    private val xDataSeries = BleDataSeries(
        "xData", graphSettingsX
    )

    private val yDataSeries = BleDataSeries(
        "yData", graphSettingsY
    )

    private val zDataSeries = BleDataSeries(
        "zData", graphSettingsZ
    )

    fun getGraph(name: String): BleGraph {
        return if (name in vectorGraphs) {
            BleGraph(
                name,
                "$name Graph",
                listOf(xDataSeries, yDataSeries, zDataSeries)
            )
        } else {
            BleGraph(
                name,
                "$name Graph",
                listOf(dataSeries)
            )
        }
    }
}