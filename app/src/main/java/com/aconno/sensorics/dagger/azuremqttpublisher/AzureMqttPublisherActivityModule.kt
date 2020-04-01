package com.aconno.sensorics.dagger.azuremqttpublisher

import androidx.lifecycle.ViewModelProviders
import com.aconno.sensorics.domain.interactor.ifttt.azuremqttpublish.AddAzureMqttPublishUseCase
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
    ) = ViewModelProviders.of(azureMqttPublisherActivity, azureMqttPublisherViewModelFactory)
        .get(AzureMqttPublisherViewModel::class.java)

    @Provides
    @AzureMqttPublisherActivityScope
    fun provideAzureMqttPublisherViewModelFactory(
        savePublishDeviceJoinUseCase: SavePublishDeviceJoinUseCase,
        deletePublishDeviceJoinUseCase: DeletePublishDeviceJoinUseCase,
        addAzureMqttPublishUseCase: AddAzureMqttPublishUseCase,
        azureMqttPublishModelDataMapper: AzureMqttPublishModelDataMapper
    ) = AzureMqttPublisherViewModelFactory(
        savePublishDeviceJoinUseCase,
        deletePublishDeviceJoinUseCase,
        addAzureMqttPublishUseCase,
        azureMqttPublishModelDataMapper
    )

}