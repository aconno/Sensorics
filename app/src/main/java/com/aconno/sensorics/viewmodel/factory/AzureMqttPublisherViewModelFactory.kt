package com.aconno.sensorics.viewmodel.factory

import androidx.lifecycle.ViewModel
import com.aconno.sensorics.domain.interactor.ifttt.azuremqttpublish.AddAzureMqttPublishUseCase
import com.aconno.sensorics.domain.interactor.repository.DeletePublishDeviceJoinUseCase
import com.aconno.sensorics.domain.interactor.repository.SavePublishDeviceJoinUseCase
import com.aconno.sensorics.model.mapper.AzureMqttPublishModelDataMapper
import com.aconno.sensorics.viewmodel.AzureMqttPublisherViewModel

class AzureMqttPublisherViewModelFactory (
    private val savePublishDeviceJoinUseCase: SavePublishDeviceJoinUseCase,
    private val deletePublishDeviceJoinUseCase: DeletePublishDeviceJoinUseCase,
    private val addAzureMqttPublishUseCase: AddAzureMqttPublishUseCase,
    private val azureMqttPublishModelDataMapper: AzureMqttPublishModelDataMapper
    ) : BaseViewModelFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val viewModel = AzureMqttPublisherViewModel(
                    savePublishDeviceJoinUseCase,
                    deletePublishDeviceJoinUseCase,
                    addAzureMqttPublishUseCase,
                    azureMqttPublishModelDataMapper
            )
            return getViewModel(viewModel, modelClass)
        }
}