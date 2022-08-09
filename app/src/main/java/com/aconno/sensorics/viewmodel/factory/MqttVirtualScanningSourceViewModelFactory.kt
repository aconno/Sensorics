package com.aconno.sensorics.viewmodel.factory

import androidx.lifecycle.ViewModel
import com.aconno.sensorics.domain.interactor.virtualscanningsource.mqtt.AddMqttVirtualScanningSourceUseCase
import com.aconno.sensorics.model.mapper.MqttVirtualScanningSourceModelDataMapper
import com.aconno.sensorics.viewmodel.MqttVirtualScanningSourceViewModel

class MqttVirtualScanningSourceViewModelFactory(
        private val addMqttVirtualScanningSourceUseCase: AddMqttVirtualScanningSourceUseCase,
        private val mqttVirtualScanningSourceModelDataMapper: MqttVirtualScanningSourceModelDataMapper
) : BaseViewModelFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = MqttVirtualScanningSourceViewModel(
                addMqttVirtualScanningSourceUseCase,
                mqttVirtualScanningSourceModelDataMapper
        )
        return getViewModel(viewModel, modelClass)
    }
}