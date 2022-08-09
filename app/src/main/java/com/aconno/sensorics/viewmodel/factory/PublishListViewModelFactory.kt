package com.aconno.sensorics.viewmodel.factory

import androidx.lifecycle.ViewModel
import com.aconno.sensorics.domain.interactor.ifttt.UpdateAnyPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.azuremqttpublish.GetAzureMqttPublishByIdUseCase
import com.aconno.sensorics.domain.interactor.ifttt.googlepublish.GetGooglePublishByIdUseCase
import com.aconno.sensorics.domain.interactor.ifttt.mqttpublish.GetMqttPublishByIdUseCase
import com.aconno.sensorics.domain.interactor.ifttt.publish.AddAnyPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.publish.DeleteAnyPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.publish.GetAllPublishersUseCase
import com.aconno.sensorics.domain.interactor.ifttt.restpublish.GetRestPublishByIdUseCase
import com.aconno.sensorics.model.mapper.*
import com.aconno.sensorics.viewmodel.PublishListViewModel

class PublishListViewModelFactory(
    private val getRestPublishByIdUseCase: GetRestPublishByIdUseCase,
    private val getGooglePublishByIdUseCase: GetGooglePublishByIdUseCase,
    private val getMqttPublishByIdUseCase: GetMqttPublishByIdUseCase,
    private val getAzureMqttPublishByIdUseCase: GetAzureMqttPublishByIdUseCase,

    private val getAllPublishersUseCase: GetAllPublishersUseCase,
    private val deleteAnyPublishUseCase: DeleteAnyPublishUseCase,
    private val addAnyPublishUseCase: AddAnyPublishUseCase,
    private val updateAnyPublishUseCase: UpdateAnyPublishUseCase,

    private val googlePublishDataMapper: GooglePublishDataMapper, // TODO: Standardize
    private val googlePublishModelDataMapper: GooglePublishModelDataMapper,
    private val restPublishDataMapper: RESTPublishDataMapper,
    private val restPublishModelDataMapper: RESTPublishModelDataMapper,
    private val mqttPublishModelDataMapper: MqttPublishModelDataMapper,
    private val azureMqttPublishModelDataMapper: AzureMqttPublishModelDataMapper
) : BaseViewModelFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = PublishListViewModel(
            getRestPublishByIdUseCase,
            getGooglePublishByIdUseCase,
            getMqttPublishByIdUseCase,
            getAzureMqttPublishByIdUseCase,

            getAllPublishersUseCase,
            deleteAnyPublishUseCase,
            addAnyPublishUseCase,
            updateAnyPublishUseCase,

            googlePublishDataMapper,
            googlePublishModelDataMapper,
            restPublishDataMapper,
            restPublishModelDataMapper,
            mqttPublishModelDataMapper,
            azureMqttPublishModelDataMapper
        )

        return getViewModel(viewModel, modelClass)
    }
}