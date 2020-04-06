package com.aconno.sensorics.dagger.publishlist

import androidx.lifecycle.ViewModelProviders
import com.aconno.sensorics.domain.interactor.ifttt.UpdateAnyPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.azuremqttpublish.GetAzureMqttPublishByIdUseCase
import com.aconno.sensorics.domain.interactor.ifttt.googlepublish.GetGooglePublishByIdUseCase
import com.aconno.sensorics.domain.interactor.ifttt.mqttpublish.GetMqttPublishByIdUseCase
import com.aconno.sensorics.domain.interactor.ifttt.publish.AddAnyPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.publish.DeleteAnyPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.publish.GetAllPublishersUseCase
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
        getRestPublishByIdUseCase: GetRestPublishByIdUseCase,
        getGooglePublishByIdUseCase: GetGooglePublishByIdUseCase,
        getMqttPublishByIdUseCase: GetMqttPublishByIdUseCase,
        getAzureMqttPublishByIdUseCase: GetAzureMqttPublishByIdUseCase,

        getAllPublishersUseCase: GetAllPublishersUseCase,
        deleteAnyPublishUseCase: DeleteAnyPublishUseCase,
        addAnyPublishUseCase: AddAnyPublishUseCase,
        updateAnyPublishUseCase: UpdateAnyPublishUseCase,

        googlePublishDataMapper: GooglePublishDataMapper, // TODO: Standardize
        googlePublishModelDataMapper: GooglePublishModelDataMapper,
        restPublishDataMapper: RESTPublishDataMapper,
        restPublishModelDataMapper: RESTPublishModelDataMapper,
        mqttPublishModelDataMapper: MqttPublishModelDataMapper,
        azureMqttPublishModelDataMapper: AzureMqttPublishModelDataMapper
    ) = PublishListViewModelFactory(
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


}