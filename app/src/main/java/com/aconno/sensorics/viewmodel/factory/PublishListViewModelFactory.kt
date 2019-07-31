package com.aconno.sensorics.viewmodel.factory

import androidx.lifecycle.ViewModel
import com.aconno.sensorics.domain.interactor.ifttt.UpdatePublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.googlepublish.AddGooglePublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.googlepublish.DeleteGooglePublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.googlepublish.GetAllGooglePublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.mqttpublish.AddMqttPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.mqttpublish.DeleteMqttPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.mqttpublish.GetAllMqttPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.restpublish.AddRestPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.restpublish.DeleteRestPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.restpublish.GetAllRestPublishUseCase
import com.aconno.sensorics.model.mapper.*
import com.aconno.sensorics.viewmodel.PublishListViewModel

class PublishListViewModelFactory(
    private val getAllGooglePublishUseCase: GetAllGooglePublishUseCase,
    private val getAllRestPublishUseCase: GetAllRestPublishUseCase,
    private val googlePublishDataMapper: GooglePublishDataMapper,
    private val googlePublishModelDataMapper: GooglePublishModelDataMapper,
    private val restPublishDataMapper: RESTPublishDataMapper,
    private val restPublishModelDataMapper: RESTPublishModelDataMapper,
    private val deleteGooglePublishUseCase: DeleteGooglePublishUseCase,
    private val deleteRestPublishUseCase: DeleteRestPublishUseCase,
    private val getAllMqttPublishUseCase: GetAllMqttPublishUseCase,
    private val mqttPublishModelDataMapper: MqttPublishModelDataMapper,
    private val deleteMqttPublishUseCase: DeleteMqttPublishUseCase,
    private val updatePublishUseCase: UpdatePublishUseCase,
    private val addGooglePublishUseCase: AddGooglePublishUseCase,
    private val addRestPublishUseCase: AddRestPublishUseCase,
    private val addMqttPublishUseCase: AddMqttPublishUseCase

) : BaseViewModelFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = PublishListViewModel(
            getAllGooglePublishUseCase,
            getAllRestPublishUseCase,
            googlePublishDataMapper,
            googlePublishModelDataMapper,
            restPublishDataMapper,
            restPublishModelDataMapper,
            deleteGooglePublishUseCase,
            deleteRestPublishUseCase,
            getAllMqttPublishUseCase,
            mqttPublishModelDataMapper,
            deleteMqttPublishUseCase,
            updatePublishUseCase,
            addGooglePublishUseCase,
            addRestPublishUseCase,
            addMqttPublishUseCase
        )
        return getViewModel(viewModel, modelClass)
    }
}