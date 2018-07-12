package com.aconno.sensorics.viewmodel.factory

import android.arch.lifecycle.ViewModel
import com.aconno.sensorics.domain.interactor.ifttt.gpublish.DeleteGooglePublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.gpublish.GetAllGooglePublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.gpublish.UpdateGooglePublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.mpublish.DeleteMqttPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.mpublish.GetAllMqttPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.mpublish.UpdateMqttPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.rpublish.DeleteRestPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.rpublish.GetAllRESTPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.rpublish.UpdateRESTPublishUserCase
import com.aconno.sensorics.model.mapper.*
import com.aconno.sensorics.viewmodel.PublishListViewModel

class PublishListViewModelFactory(
    private val getAllGooglePublishUseCase: GetAllGooglePublishUseCase,
    private val getAllRESTPublishUseCase: GetAllRESTPublishUseCase,
    private val updateGooglePublishUseCase: UpdateGooglePublishUseCase,
    private val updateRESTPublishUserCase: UpdateRESTPublishUserCase,
    private val googlePublishDataMapper: GooglePublishDataMapper,
    private val googlePublishModelDataMapper: GooglePublishModelDataMapper,
    private val restPublishDataMapper: RESTPublishDataMapper,
    private val restPublishModelDataMapper: RESTPublishModelDataMapper,
    private val deleteGooglePublishUseCase: DeleteGooglePublishUseCase,
    private val deleteRestPublishUseCase: DeleteRestPublishUseCase,
    private val getAllMqttPublishUseCase: GetAllMqttPublishUseCase,
    private val updateMqttPublishUseCase: UpdateMqttPublishUseCase,
    private val mqttPublishModelDataMapper: MqttPublishModelDataMapper,
    private val deleteMqttPublishUseCase: DeleteMqttPublishUseCase
) : BaseViewModelFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = PublishListViewModel(
            getAllGooglePublishUseCase,
            getAllRESTPublishUseCase,
            updateGooglePublishUseCase,
            updateRESTPublishUserCase,
            googlePublishDataMapper,
            googlePublishModelDataMapper,
            restPublishDataMapper,
            restPublishModelDataMapper,
            deleteGooglePublishUseCase,
            deleteRestPublishUseCase,
            getAllMqttPublishUseCase,
            updateMqttPublishUseCase,
            mqttPublishModelDataMapper,
            deleteMqttPublishUseCase
        )
        return getViewModel(viewModel, modelClass)
    }
}