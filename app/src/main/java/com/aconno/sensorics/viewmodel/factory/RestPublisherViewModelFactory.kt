package com.aconno.sensorics.viewmodel.factory

import androidx.lifecycle.ViewModel
import com.aconno.sensorics.domain.interactor.ifttt.publish.AddAnyPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.publish.GetAllDeviceParameterPlaceholderStringsUseCase
import com.aconno.sensorics.domain.interactor.ifttt.restpublish.GetRestPublishByIdUseCase
import com.aconno.sensorics.domain.interactor.repository.*
import com.aconno.sensorics.model.mapper.RESTHeaderModelMapper
import com.aconno.sensorics.model.mapper.RESTHttpGetParamModelMapper
import com.aconno.sensorics.model.mapper.RESTPublishDataMapper
import com.aconno.sensorics.model.mapper.RESTPublishModelDataMapper
import com.aconno.sensorics.viewmodel.RestPublisherViewModel

class RestPublisherViewModelFactory(
    private val getRestPublishByIdUseCase: GetRestPublishByIdUseCase,
    private val addAnyPublishUseCase: AddAnyPublishUseCase,

    private val restPublishModelDataMapper: RESTPublishModelDataMapper,
    private val restPublishDataMapper: RESTPublishDataMapper,

    private val getRestHeadersByIdUseCase: GetRestHeadersByIdUseCase,
    private val saveRestHeaderUseCase: SaveRestHeaderUseCase,
    private val restHeaderModelMapper: RESTHeaderModelMapper,

    private val getRestHttpGetParamsByIdUseCase: GetRestHttpGetParamsByIdUseCase,
    private val saveRestHttpGetParamUseCase: SaveRestHttpGetParamUseCase,
    private val restHttpGetParamModelMapper: RESTHttpGetParamModelMapper,

    private val savePublishDeviceJoinUseCase: SavePublishDeviceJoinUseCase,
    private val deletePublishDeviceJoinUseCase: DeletePublishDeviceJoinUseCase,

    private val getAllDeviceParameterPlaceholderStringsUseCase: GetAllDeviceParameterPlaceholderStringsUseCase
) : BaseViewModelFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel = RestPublisherViewModel(
            getRestPublishByIdUseCase,
            addAnyPublishUseCase,
            restPublishModelDataMapper,
            restPublishDataMapper,

            getRestHeadersByIdUseCase,
            saveRestHeaderUseCase,
            restHeaderModelMapper,

            getRestHttpGetParamsByIdUseCase,
            saveRestHttpGetParamUseCase,
            restHttpGetParamModelMapper,

            savePublishDeviceJoinUseCase,
            deletePublishDeviceJoinUseCase,

            getAllDeviceParameterPlaceholderStringsUseCase
        )
        return getViewModel(viewModel, modelClass)
    }
}