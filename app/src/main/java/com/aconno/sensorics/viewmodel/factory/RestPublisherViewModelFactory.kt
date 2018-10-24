package com.aconno.sensorics.viewmodel.factory

import android.arch.lifecycle.ViewModel
import com.aconno.sensorics.domain.interactor.ifttt.restpublish.AddRestPublishUseCase
import com.aconno.sensorics.domain.interactor.repository.*
import com.aconno.sensorics.model.mapper.RESTHeaderModelMapper
import com.aconno.sensorics.model.mapper.RESTHttpGetParamModelMapper
import com.aconno.sensorics.model.mapper.RESTPublishModelDataMapper
import com.aconno.sensorics.viewmodel.RestPublisherViewModel

class RestPublisherViewModelFactory(
    private val addRestPublishUseCase: AddRestPublishUseCase,
    private val restPublishModelDataMapper: RESTPublishModelDataMapper,
    private val savePublishDeviceJoinUseCase: SavePublishDeviceJoinUseCase,
    private val deletePublishDeviceJoinUseCase: DeletePublishDeviceJoinUseCase,
    private val saveRestHeaderUseCase: SaveRestHeaderUseCase,
    private val getRestHeadersByIdUseCase: GetRestHeadersByIdUseCase,
    private val restHeaderModelMapper: RESTHeaderModelMapper,
    private val saveRestHttpGetParamUseCase: SaveRestHttpGetParamUseCase,
    private val getRestHttpGetParamsByIdUseCase: GetRestHttpGetParamsByIdUseCase,
    private val restHttpGetParamModelMapper: RESTHttpGetParamModelMapper
) : BaseViewModelFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = RestPublisherViewModel(
            addRestPublishUseCase,
            restPublishModelDataMapper,
            savePublishDeviceJoinUseCase,
            deletePublishDeviceJoinUseCase,
            saveRestHeaderUseCase,
            getRestHeadersByIdUseCase,
            restHeaderModelMapper,
            saveRestHttpGetParamUseCase,
            getRestHttpGetParamsByIdUseCase,
            restHttpGetParamModelMapper
        )
        return getViewModel(viewModel, modelClass)
    }
}