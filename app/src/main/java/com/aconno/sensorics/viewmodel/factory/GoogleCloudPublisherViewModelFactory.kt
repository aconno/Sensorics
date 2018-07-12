package com.aconno.sensorics.viewmodel.factory

import android.arch.lifecycle.ViewModel
import com.aconno.sensorics.domain.interactor.ifttt.gpublish.AddGooglePublishUseCase
import com.aconno.sensorics.domain.interactor.repository.DeletePublishDeviceJoinUseCase
import com.aconno.sensorics.domain.interactor.repository.SavePublishDeviceJoinUseCase
import com.aconno.sensorics.model.mapper.GooglePublishModelDataMapper
import com.aconno.sensorics.viewmodel.GoogleCloudPublisherViewModel

class GoogleCloudPublisherViewModelFactory(
    private val addGooglePublishUseCase: AddGooglePublishUseCase,
    private val googlePublishModelDataMapper: GooglePublishModelDataMapper,
    private val savePublishDeviceJoinUseCase: SavePublishDeviceJoinUseCase,
    private val deletePublishDeviceJoinUseCase: DeletePublishDeviceJoinUseCase
) : BaseViewModelFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = GoogleCloudPublisherViewModel(
            addGooglePublishUseCase,
            googlePublishModelDataMapper,
            savePublishDeviceJoinUseCase,
            deletePublishDeviceJoinUseCase
        )
        return getViewModel(viewModel, modelClass)
    }
}