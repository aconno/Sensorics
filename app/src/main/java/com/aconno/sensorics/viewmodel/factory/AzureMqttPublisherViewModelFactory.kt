package com.aconno.sensorics.viewmodel.factory

import androidx.lifecycle.ViewModel
import com.aconno.sensorics.domain.interactor.ifttt.azuremqttpublish.GetAzureMqttPublishByIdUseCase
import com.aconno.sensorics.domain.interactor.ifttt.publish.AddAnyPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.publish.GetAllDeviceParameterPlaceholderStringsUseCase
import com.aconno.sensorics.domain.interactor.repository.DeletePublishDeviceJoinUseCase
import com.aconno.sensorics.domain.interactor.repository.SavePublishDeviceJoinUseCase
import com.aconno.sensorics.model.mapper.AzureMqttPublishModelDataMapper
import com.aconno.sensorics.viewmodel.AzureMqttPublisherViewModel

class AzureMqttPublisherViewModelFactory(
    private val addAnyPublishUseCase: AddAnyPublishUseCase,
    private val getAzureMqttPublishByIdUseCase: GetAzureMqttPublishByIdUseCase,
    private val savePublishDeviceJoinUseCase: SavePublishDeviceJoinUseCase,
    private val deletePublishDeviceJoinUseCase: DeletePublishDeviceJoinUseCase,
    private val azureMqttPublishModelDataMapper: AzureMqttPublishModelDataMapper,
    private val getAllDeviceParameterPlaceholderStringsUseCase: GetAllDeviceParameterPlaceholderStringsUseCase
) : BaseViewModelFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel = AzureMqttPublisherViewModel(
            addAnyPublishUseCase,
            getAzureMqttPublishByIdUseCase,
            savePublishDeviceJoinUseCase,
            deletePublishDeviceJoinUseCase,
            azureMqttPublishModelDataMapper,
            getAllDeviceParameterPlaceholderStringsUseCase
        )
        return getViewModel(viewModel, modelClass)
    }
}