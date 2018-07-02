package com.aconno.acnsensa.viewmodel.factory

import android.arch.lifecycle.ViewModel
import com.aconno.acnsensa.domain.interactor.ifttt.gpublish.AddGooglePublishUseCase
import com.aconno.acnsensa.domain.interactor.repository.DeletePublishDeviceJoinUseCase
import com.aconno.acnsensa.domain.interactor.repository.GetDevicesThatConnectedWithGooglePublishUseCase
import com.aconno.acnsensa.domain.interactor.repository.GetSavedDevicesMaybeUseCase
import com.aconno.acnsensa.domain.interactor.repository.SavePublishDeviceJoinUseCase
import com.aconno.acnsensa.model.mapper.DeviceRelationModelMapper
import com.aconno.acnsensa.model.mapper.GooglePublishModelDataMapper
import com.aconno.acnsensa.viewmodel.GoogleCloudPublisherViewModel

class GoogleCloudPublisherViewModelFactory(
    private val addGooglePublishUseCase: AddGooglePublishUseCase,
    private val googlePublishModelDataMapper: GooglePublishModelDataMapper,
    private val savePublishDeviceJoinUseCase: SavePublishDeviceJoinUseCase,
    private val deletePublishDeviceJoinUseCase: DeletePublishDeviceJoinUseCase,
    private val devicesThatConnectedWithGooglePublishUseCase: GetDevicesThatConnectedWithGooglePublishUseCase,
    private val savedDevicesMaybeUseCase: GetSavedDevicesMaybeUseCase,
    private val deviceRelationModelMapper: DeviceRelationModelMapper
) : BaseViewModelFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = GoogleCloudPublisherViewModel(
            addGooglePublishUseCase,
            googlePublishModelDataMapper,
            savePublishDeviceJoinUseCase,
            deletePublishDeviceJoinUseCase,
            devicesThatConnectedWithGooglePublishUseCase,
            savedDevicesMaybeUseCase,
            deviceRelationModelMapper
        )
        return getViewModel(viewModel, modelClass)
    }
}