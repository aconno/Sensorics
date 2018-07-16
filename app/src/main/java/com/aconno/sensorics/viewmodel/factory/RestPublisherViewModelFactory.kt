package com.aconno.sensorics.viewmodel.factory

import android.arch.lifecycle.ViewModel
import com.aconno.sensorics.domain.interactor.ifttt.rpublish.AddRESTPublishUseCase
import com.aconno.sensorics.domain.interactor.repository.*
import com.aconno.sensorics.model.mapper.RESTHeaderModelMapper
import com.aconno.sensorics.model.mapper.RESTHttpGetParamModelMapper
import com.aconno.sensorics.model.mapper.RESTPublishModelDataMapper
import com.aconno.sensorics.viewmodel.RestPublisherViewModel

class RestPublisherViewModelFactory(
    private val addRESTPublishUseCase: AddRESTPublishUseCase,
    private val restPublishModelDataMapper: RESTPublishModelDataMapper,
    private val savePublishDeviceJoinUseCase: SavePublishDeviceJoinUseCase,
    private val deletePublishDeviceJoinUseCase: DeletePublishDeviceJoinUseCase,
    private val saveRESTHeaderUseCase: SaveRESTHeaderUseCase,
    private val getRESTHeadersByIdUseCase: GetRESTHeadersByIdUseCase,
    private val restHeaderModelMapper: RESTHeaderModelMapper,
    private val saveRESTHttpGetParamUseCase: SaveRESTHttpGetParamUseCase,
    private val getRESTHttpGetParamsByIdUseCase: GetRESTHttpGetParamsByIdUseCase,
    private val restHttpGetParamModelMapper: RESTHttpGetParamModelMapper
) : BaseViewModelFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = RestPublisherViewModel(
            addRESTPublishUseCase,
            restPublishModelDataMapper,
            savePublishDeviceJoinUseCase,
            deletePublishDeviceJoinUseCase,
            saveRESTHeaderUseCase,
            getRESTHeadersByIdUseCase,
            restHeaderModelMapper,
            saveRESTHttpGetParamUseCase,
            getRESTHttpGetParamsByIdUseCase,
            restHttpGetParamModelMapper
        )
        return getViewModel(viewModel, modelClass)
    }
}