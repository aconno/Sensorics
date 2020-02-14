package com.aconno.sensorics.dagger.publishlist

import androidx.lifecycle.ViewModelProviders
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
import com.aconno.sensorics.ui.settings.publishers.PublishListActivity
import com.aconno.sensorics.viewmodel.PublishListViewModel
import com.aconno.sensorics.viewmodel.factory.PublishListViewModelFactory
import dagger.Module
import dagger.Provides

/**
 * @author aconno
 */
@Module
class PublishListActivityModule {


    @Provides
    @PublishListActivityScope
    fun providePublishListViewModel(
        activity: PublishListActivity,
        publishViewModelFactory: PublishListViewModelFactory
    ) = ViewModelProviders.of(activity, publishViewModelFactory)
        .get(PublishListViewModel::class.java)

    @Provides
    @PublishListActivityScope
    fun providePublishListViewModelFactory(
            getAllGooglePublishUseCase: GetAllGooglePublishUseCase,
            getAllRestPublishUseCase: GetAllRestPublishUseCase,
            getAllAzureMqttPublishUseCase: GetAllAzureMqttPublishUseCase,
            getAllMqttPublishUseCase: GetAllMqttPublishUseCase,

            getGooglePublishByIdUseCase: GetGooglePublishByIdUseCase,
            getRestPublishByIdUseCase: GetRestPublishByIdUseCase,
            getMqttPublishByIdUseCase: GetMqttPublishByIdUseCase,
            getAzureMqttPublishByIdUseCase: GetAzureMqttPublishByIdUseCase,

            addGooglePublishUseCase: AddGooglePublishUseCase,
            addRestPublishUsecase: AddRestPublishUseCase,
            addMqttPublishUseCase: AddMqttPublishUseCase,
            addAzureMqttPublishUseCase: AddAzureMqttPublishUseCase,

            deleteGooglePublishUseCase: DeleteGooglePublishUseCase,
            deleteRestPublishUseCase: DeleteRestPublishUseCase,
            deleteMqttPublishUseCase: DeleteMqttPublishUseCase,
            deleteAzureMqttPublishUseCase: DeleteAzureMqttPublishUseCase,

            updatePublishUseCase: UpdatePublishUseCase,

            googlePublishDataMapper: GooglePublishDataMapper,
            restPublishDataMapper: RESTPublishDataMapper,

            googlePublishModelDataMapper: GooglePublishModelDataMapper,
            restPublishModelDataMapper: RESTPublishModelDataMapper,
            mqttPublishModelDataMapper: MqttPublishModelDataMapper,
            azureMqttPublishModelDataMapper: AzureMqttPublishModelDataMapper
    ) =
        PublishListViewModelFactory(
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


}