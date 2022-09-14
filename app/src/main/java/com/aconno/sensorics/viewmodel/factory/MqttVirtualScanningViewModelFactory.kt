package com.aconno.sensorics.viewmodel.factory

import androidx.lifecycle.ViewModel
import com.aconno.sensorics.SensoricsApplication
import com.aconno.sensorics.viewmodel.MqttVirtualScanningViewModel

class MqttVirtualScanningViewModelFactory(
    private val sensoricsApplication: SensoricsApplication
) : BaseViewModelFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel = MqttVirtualScanningViewModel(sensoricsApplication)
        return getViewModel(viewModel, modelClass)
    }
}