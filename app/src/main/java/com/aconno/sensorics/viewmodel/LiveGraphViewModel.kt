package com.aconno.sensorics.viewmodel

import android.app.Application
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aconno.sensorics.R
import com.aconno.sensorics.domain.interactor.repository.GetReadingsUseCase
import com.aconno.sensorics.model.DataSeriesSettings
import com.aconno.sensorics.ui.graph.BleDataSeries
import com.aconno.sensorics.ui.graph.BleGraph
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

class LiveGraphViewModel(
    private val getReadingsUseCase: GetReadingsUseCase,
    application: Application
) : ViewModel() {

    private val refreshTimestamp: MutableLiveData<Long> = MutableLiveData()

    private val vectorGraphs = listOf("Magnetometer", "Accelerometer", "Gyroscope")
    private var compositeDisposable: CompositeDisposable? = null

    private lateinit var macAddress: String
    private lateinit var graphName: String

    fun setMacAddressAndGraphName(macAddress: String, graphName: String) {
        this.macAddress = macAddress
        this.graphName = graphName
        compositeDisposable?.clear()
        this.compositeDisposable = CompositeDisposable()

        if (graphName in vectorGraphs) {
            val disposable = getReadingsUseCase.execute(macAddress, "$graphName X")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    xDataSeries.updateDataSet(it)
                    refreshTimestamp.value = System.currentTimeMillis()
                }
            val disposableY = getReadingsUseCase.execute(macAddress, "$graphName Y")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    yDataSeries.updateDataSet(it)
                    refreshTimestamp.value = System.currentTimeMillis()
                }
            val disposableZ = getReadingsUseCase.execute(macAddress, "$graphName Z")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    zDataSeries.updateDataSet(it)
                    refreshTimestamp.value = System.currentTimeMillis()
                }

            compositeDisposable?.addAll(
                disposable, disposableY, disposableZ
            )

        } else {
            val disposable = getReadingsUseCase.execute(macAddress, graphName)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    dataSeries.updateDataSet(it)
                    refreshTimestamp.value = System.currentTimeMillis()
                }
            compositeDisposable?.add(disposable)
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

    override fun onCleared() {
        compositeDisposable?.clear()
        super.onCleared()
    }
}