package com.aconno.sensorics.viewmodel.factory

import androidx.lifecycle.ViewModel
import com.aconno.sensorics.domain.interactor.ifttt.mqttpublish.GetMqttPublishByIdUseCase
import com.aconno.sensorics.domain.interactor.ifttt.publish.AddAnyPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.publish.GetAllDeviceParameterPlaceholderStringsUseCase
import com.aconno.sensorics.domain.interactor.repository.DeletePublishDeviceJoinUseCase
import com.aconno.sensorics.domain.interactor.repository.SavePublishDeviceJoinUseCase
import com.aconno.sensorics.model.mapper.MqttPublishModelDataMapper
import com.aconno.sensorics.viewmodel.MqttPublisherViewModel

class MqttPublisherViewModelFactory(
    private val addAnyPublishUseCase: AddAnyPublishUseCase,
    private val getMqttPublishByIdUseCase: GetMqttPublishByIdUseCase,
    private val savePublishDeviceJoinUseCase: SavePublishDeviceJoinUseCase,
    private val deletePublishDeviceJoinUseCase: DeletePublishDeviceJoinUseCase,
    private val mqttPublishModelDataMapper: MqttPublishModelDataMapper,
    private val getAllDeviceParameterPlaceholderStringsUseCase: GetAllDeviceParameterPlaceholderStringsUseCase
) : BaseViewModelFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel = MqttPublisherViewModel(
            savePublishDeviceJoinUseCase,
            deletePublishDeviceJoinUseCase,
            addAnyPublishUseCase,
            getMqttPublishByIdUseCase,
            mqttPublishModelDataMapper,
            getAllDeviceParameterPlaceholderStringsUseCase
        )
        return getViewModel(viewModel, modelClass)
    }
}