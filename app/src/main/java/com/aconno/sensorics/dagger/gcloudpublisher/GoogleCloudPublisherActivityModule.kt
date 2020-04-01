package com.aconno.sensorics.dagger.gcloudpublisher

import androidx.lifecycle.ViewModelProviders
import com.aconno.sensorics.domain.interactor.ifttt.googlepublish.AddGooglePublishUseCase
import com.aconno.sensorics.domain.interactor.repository.DeletePublishDeviceJoinUseCase
import com.aconno.sensorics.domain.interactor.repository.SavePublishDeviceJoinUseCase
import com.aconno.sensorics.model.mapper.GooglePublishModelDataMapper
import com.aconno.sensorics.ui.settings.publishers.selectpublish.GoogleCloudPublisherActivity
import com.aconno.sensorics.viewmodel.GoogleCloudPublisherViewModel
import com.aconno.sensorics.viewmodel.factory.GoogleCloudPublisherViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class GoogleCloudPublisherActivityModule {

    @Provides
    @GoogleCloudPublisherActivityScope
    fun provideGoogleCloudPublisherViewModel(
        googleCloudPublisherActivity: GoogleCloudPublisherActivity,
        googleCloudPublisherViewModelFactory: GoogleCloudPublisherViewModelFactory
    ) = ViewModelProviders.of(googleCloudPublisherActivity, googleCloudPublisherViewModelFactory)
        .get(GoogleCloudPublisherViewModel::class.java)

    @Provides
    @GoogleCloudPublisherActivityScope
    fun provideGoogleCloudPublisherViewModelFactory(
        addGooglePublishUseCase: AddGooglePublishUseCase,
        googlePublishModelDataMapper: GooglePublishModelDataMapper,
        savePublishDeviceJoinUseCase: SavePublishDeviceJoinUseCase,
        deletePublishDeviceJoinUseCase: DeletePublishDeviceJoinUseCase
    ) = GoogleCloudPublisherViewModelFactory(
        addGooglePublishUseCase,
        googlePublishModelDataMapper,
        savePublishDeviceJoinUseCase,
        deletePublishDeviceJoinUseCase
    )


}