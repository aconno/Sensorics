package com.aconno.sensorics.viewmodel.factory

import android.arch.lifecycle.ViewModel
import com.aconno.sensorics.domain.interactor.ifttt.rpublish.AddRESTPublishUseCase
import com.aconno.sensorics.domain.interactor.repository.*
import com.aconno.sensorics.model.mapper.DeviceRelationModelMapper
import com.aconno.sensorics.model.mapper.RESTHeaderModelMapper
import com.aconno.sensorics.model.mapper.RESTPublishModelDataMapper
import com.aconno.sensorics.viewmodel.RestPublisherViewModel

class RestPublisherViewModelFactory(
    private val addRESTPublishUseCase: AddRESTPublishUseCase,
    private val restPublishModelDataMapper: RESTPublishModelDataMapper,
    private val savePublishDeviceJoinUseCase: SavePublishDeviceJoinUseCase,
    private val deletePublishDeviceJoinUseCase: DeletePublishDeviceJoinUseCase,
    private val devicesThatConnectedWithRESTPublishUseCase: GetDevicesThatConnectedWithRESTPublishUseCase,
    private val savedDevicesMaybeUseCase: GetSavedDevicesMaybeUseCase,
    private val deviceRelationModelMapper: DeviceRelationModelMapper,
    private val saveRESTHeaderUseCase: SaveRESTHeaderUseCase,
    private val deleteRESTHeaderUseCase: DeleteRESTHeaderUseCase,
    private val getRESTHeadersByIdUseCase: GetRESTHeadersByIdUseCase,
    private val restHeaderModelMapper: RESTHeaderModelMapper
) : BaseViewModelFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = RestPublisherViewModel(
            addRESTPublishUseCase,
            restPublishModelDataMapper,
            savePublishDeviceJoinUseCase,
            deletePublishDeviceJoinUseCase,
            devicesThatConnectedWithRESTPublishUseCase,
            savedDevicesMaybeUseCase,
            deviceRelationModelMapper,
            saveRESTHeaderUseCase,
            deleteRESTHeaderUseCase,
            getRESTHeadersByIdUseCase,
            restHeaderModelMapper
        )
        return getViewModel(viewModel, modelClass)
    }
}