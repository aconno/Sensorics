package com.aconno.sensorics.ui.livegraph

interface LiveGraphOpener {
    fun openLiveGraph(macAddress: String, sensorName: String)
}