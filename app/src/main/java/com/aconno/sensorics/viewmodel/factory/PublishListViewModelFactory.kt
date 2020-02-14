package com.aconno.sensorics.viewmodel.factory

import androidx.lifecycle.ViewModel
import com.aconno.sensorics.domain.interactor.ifttt.UpdatePublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.azuremqttpublish.AddAzureMqttPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.azuremqttpublish.DeleteAzureMqttPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.azuremqttpublish.GetAllAzureMqttPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.azuremqttpublish.GetAzureMqttPublishByIdUseCase
import com.aconno.sensorics.domain.interactor.ifttt.googlepublish.AddGooglePublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.googlepublish.DeleteGooglePublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.googlepublish.GetAllGooglePublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.googlepublish.GetGooglePublishByIdUseCase
import com.aconno.sensorics.domain.interactor.ifttt.mqttpublish.AddMqttPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.mqttpublish.DeleteMqttPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.mqttpublish.GetAllMqttPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.mqttpublish.GetMqttPublishByIdUseCase
import com.aconno.sensorics.domain.interactor.ifttt.restpublish.AddRestPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.restpublish.DeleteRestPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.restpublish.GetAllRestPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.restpublish.GetRestPublishByIdUseCase
import com.aconno.sensorics.model.mapper.*
import com.aconno.sensorics.viewmodel.PublishListViewModel

class PublishListViewModelFactory(
        private val getAllGooglePublishUseCase: GetAllGooglePublishUseCase,
        private val getAllRestPublishUseCase: GetAllRestPublishUseCase,
        private val getAllAzureMqttPublishUseCase: GetAllAzureMqttPublishUseCase,
        private val getAllMqttPublishUseCase: GetAllMqttPublishUseCase,

        private val getGooglePublishByIdUseCase: GetGooglePublishByIdUseCase,
        private val getRestPublishByIdUseCase: GetRestPublishByIdUseCase,
        private val getMqttPublishByIdUseCase: GetMqttPublishByIdUseCase,
        private val getAzureMqttPublishByIdUseCase: GetAzureMqttPublishByIdUseCase,

        private val addGooglePublishUseCase: AddGooglePublishUseCase,
        private val addRestPublishUsecase: AddRestPublishUseCase,
        private val addMqttPublishUseCase: AddMqttPublishUseCase,
        private val addAzureMqttPublishUseCase: AddAzureMqttPublishUseCase,

        private val deleteGooglePublishUseCase: DeleteGooglePublishUseCase,
        private val deleteRestPublishUseCase: DeleteRestPublishUseCase,
        private val deleteMqttPublishUseCase: DeleteMqttPublishUseCase,
        private val deleteAzureMqttPublishUseCase: DeleteAzureMqttPublishUseCase,

        private val updatePublishUseCase: UpdatePublishUseCase,

        private val googlePublishDataMapper: GooglePublishDataMapper,
        private val restPublishDataMapper: RESTPublishDataMapper,

        private val googlePublishModelDataMapper: GooglePublishModelDataMapper,
        private val restPublishModelDataMapper: RESTPublishModelDataMapper,
        private val mqttPublishModelDataMapper: MqttPublishModelDataMapper,
        private val azureMqttPublishModelDataMapper: AzureMqttPublishModelDataMapper
) : BaseViewModelFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = PublishListViewModel(
            getAllGooglePublishUseCase,
            getAllRestPublishUseCase,
            getAllAzureMqttPublishUseCase,
                getAllMqttPublishUseCase,

                getGooglePublishByIdUseCase,
                getRestPublishByIdUseCase,
                getMqttPublishByIdUseCase,
                getAzureMqttPublishByIdUseCase,

                addGooglePublishUseCase,
                addRestPublishUsecase,
                addMqttPublishUseCase,
                addAzureMqttPublishUseCase,

                deleteGooglePublishUseCase,
                deleteRestPublishUseCase,
                deleteMqttPublishUseCase,
                deleteAzureMqttPublishUseCase,

                updatePublishUseCase,

                googlePublishDataMapper,
                restPublishDataMapper,

                googlePublishModelDataMapper,
                restPublishModelDataMapper,
                mqttPublishModelDataMapper,
                azureMqttPublishModelDataMapper
        )
        return getViewModel(viewModel, modelClass)
    }
}