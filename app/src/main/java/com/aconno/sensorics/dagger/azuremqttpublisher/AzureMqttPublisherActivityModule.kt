package com.aconno.sensorics.dagger.azuremqttpublisher

import androidx.lifecycle.ViewModelProvider
import com.aconno.sensorics.domain.interactor.ifttt.azuremqttpublish.GetAzureMqttPublishByIdUseCase
import com.aconno.sensorics.domain.interactor.ifttt.publish.AddAnyPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.publish.GetAllDeviceParameterPlaceholderStringsUseCase
import com.aconno.sensorics.domain.interactor.repository.DeletePublishDeviceJoinUseCase
import com.aconno.sensorics.domain.interactor.repository.SavePublishDeviceJoinUseCase
import com.aconno.sensorics.model.mapper.AzureMqttPublishModelDataMapper
import com.aconno.sensorics.ui.settings.publishers.selectpublish.AzureMqttPublisherActivity
import com.aconno.sensorics.viewmodel.AzureMqttPublisherViewModel
import com.aconno.sensorics.viewmodel.factory.AzureMqttPublisherViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class AzureMqttPublisherActivityModule {

    @Provides
    @AzureMqttPublisherActivityScope
    fun provideAzureMqttPublisherViewModel(
        azureMqttPublisherActivity: AzureMqttPublisherActivity,
        azureMqttPublisherViewModelFactory: AzureMqttPublisherViewModelFactory
    ): AzureMqttPublisherViewModel = ViewModelProvider(
        azureMqttPublisherActivity,
        azureMqttPublisherViewModelFactory
    ).get(AzureMqttPublisherViewModel::class.java)

    @Provides
    @AzureMqttPublisherActivityScope
    fun provideAzureMqttPublisherViewModelFactory(
        addAnyPublishUseCase: AddAnyPublishUseCase,
        getAzureMqttPublishByIdUseCase: GetAzureMqttPublishByIdUseCase,
        savePublishDeviceJoinUseCase: SavePublishDeviceJoinUseCase,
        deletePublishDeviceJoinUseCase: DeletePublishDeviceJoinUseCase,
        azureMqttPublishModelDataMapper: AzureMqttPublishModelDataMapper,
        getAllDeviceParameterPlaceholderStringsUseCase: GetAllDeviceParameterPlaceholderStringsUseCase
    ) = AzureMqttPublisherViewModelFactory(
        addAnyPublishUseCase,
        getAzureMqttPublishByIdUseCase,
        savePublishDeviceJoinUseCase,
        deletePublishDeviceJoinUseCase,
        azureMqttPublishModelDataMapper,
        getAllDeviceParameterPlaceholderStringsUseCase
    )

}