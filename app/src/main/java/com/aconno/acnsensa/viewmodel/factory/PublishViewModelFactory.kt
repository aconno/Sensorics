package com.aconno.acnsensa.viewmodel.factory

import android.arch.lifecycle.ViewModel
import com.aconno.acnsensa.domain.interactor.ifttt.gpublish.AddGooglePublishUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.rpublish.AddRESTPublishUseCase
import com.aconno.acnsensa.domain.interactor.repository.*
import com.aconno.acnsensa.model.mapper.DeviceRelationModelMapper
import com.aconno.acnsensa.model.mapper.GooglePublishModelDataMapper
import com.aconno.acnsensa.model.mapper.RESTHeaderModelMapper
import com.aconno.acnsensa.model.mapper.RESTPublishModelDataMapper
import com.aconno.acnsensa.viewmodel.PublishViewModel

class PublishViewModelFactory(
    private val addGooglePublishUseCase: AddGooglePublishUseCase,
    private val addRESTPublishUseCase: AddRESTPublishUseCase,
    private val googlePublishModelDataMapper: GooglePublishModelDataMapper,
    private val restPublishModelDataMapper: RESTPublishModelDataMapper,
    private val savePublishDeviceJoinUseCase: SavePublishDeviceJoinUseCase,
    private val deletePublishDeviceJoinUseCase: DeletePublishDeviceJoinUseCase,
    private val devicesThatConnectedWithGooglePublishUseCase: GetDevicesThatConnectedWithGooglePublishUseCase,
    private val devicesThatConnectedWithRESTPublishUseCase: GetDevicesThatConnectedWithRESTPublishUseCase,
    private val savedDevicesUseCase: GetSavedDevicesMaybeUseCase,
    private val deviceRelationModelMapper: DeviceRelationModelMapper,
    private val saveRESTHeaderUseCase: SaveRESTHeaderUseCase,
    private val deleteRESTHeaderUseCase: DeleteRESTHeaderUseCase,
    private val getRESTHeadersByIdUseCase: GetRESTHeadersByIdUseCase,
    private val restHeaderModelMapper: RESTHeaderModelMapper
) : BaseViewModelFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = PublishViewModel(
            addGooglePublishUseCase,
            addRESTPublishUseCase,
            googlePublishModelDataMapper,
            restPublishModelDataMapper,
            savePublishDeviceJoinUseCase,
            deletePublishDeviceJoinUseCase,
            devicesThatConnectedWithGooglePublishUseCase,
            devicesThatConnectedWithRESTPublishUseCase,
            savedDevicesUseCase,
            deviceRelationModelMapper,
            saveRESTHeaderUseCase,
            deleteRESTHeaderUseCase,
            getRESTHeadersByIdUseCase,
            restHeaderModelMapper
        )
        return getViewModel(viewModel, modelClass)
    }
}