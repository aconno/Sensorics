package com.aconno.sensorics.viewmodel.factory

import androidx.lifecycle.ViewModel
import com.aconno.sensorics.domain.interactor.ifttt.mqttpublish.AddMqttPublishUseCase
import com.aconno.sensorics.domain.interactor.repository.DeletePublishDeviceJoinUseCase
import com.aconno.sensorics.domain.interactor.repository.SavePublishDeviceJoinUseCase
import com.aconno.sensorics.model.mapper.MqttPublishModelDataMapper
import com.aconno.sensorics.viewmodel.MqttPublisherViewModel

class MqttPublisherViewModelFactory(
    private val savePublishDeviceJoinUseCase: SavePublishDeviceJoinUseCase,
    private val deletePublishDeviceJoinUseCase: DeletePublishDeviceJoinUseCase,
    private val addMqttPublishUseCase: AddMqttPublishUseCase,
    private val mqttPublishModelDataMapper: MqttPublishModelDataMapper

    ) : BaseViewModelFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = MqttPublisherViewModel(
            savePublishDeviceJoinUseCase,
            deletePublishDeviceJoinUseCase,
            addMqttPublishUseCase,
            mqttPublishModelDataMapper
        )
        return getViewModel(viewModel, modelClass)
    }
}