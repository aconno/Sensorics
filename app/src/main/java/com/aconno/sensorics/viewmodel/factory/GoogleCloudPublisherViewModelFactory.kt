package com.aconno.sensorics.viewmodel.factory

import androidx.lifecycle.ViewModel
import com.aconno.sensorics.domain.interactor.ifttt.googlepublish.GetGooglePublishByIdUseCase
import com.aconno.sensorics.domain.interactor.ifttt.publish.AddAnyPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.publish.GetAllDeviceParameterPlaceholderStringsUseCase
import com.aconno.sensorics.domain.interactor.repository.DeletePublishDeviceJoinUseCase
import com.aconno.sensorics.domain.interactor.repository.SavePublishDeviceJoinUseCase
import com.aconno.sensorics.model.mapper.GooglePublishDataMapper
import com.aconno.sensorics.model.mapper.GooglePublishModelDataMapper
import com.aconno.sensorics.viewmodel.GoogleCloudPublisherViewModel

class GoogleCloudPublisherViewModelFactory(
    private val addAnyPublishUseCase: AddAnyPublishUseCase,
    private val getGooglePublishByIdUseCase: GetGooglePublishByIdUseCase,
    private val savePublishDeviceJoinUseCase: SavePublishDeviceJoinUseCase,
    private val deletePublishDeviceJoinUseCase: DeletePublishDeviceJoinUseCase,
    private val googlePublishModelDataMapper: GooglePublishModelDataMapper,
    private val googlePublishDataMapper: GooglePublishDataMapper,
    private val getAllDeviceParameterPlaceholderStringsUseCase: GetAllDeviceParameterPlaceholderStringsUseCase
) : BaseViewModelFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = GoogleCloudPublisherViewModel(
            addAnyPublishUseCase,
            getGooglePublishByIdUseCase,
            googlePublishModelDataMapper,
            savePublishDeviceJoinUseCase,
            deletePublishDeviceJoinUseCase,
            googlePublishDataMapper,
            getAllDeviceParameterPlaceholderStringsUseCase
        )
        return getViewModel(viewModel, modelClass)
    }
}