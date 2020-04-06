package com.aconno.sensorics.dagger.gcloudpublisher

import androidx.lifecycle.ViewModelProvider
import com.aconno.sensorics.domain.interactor.ifttt.googlepublish.GetGooglePublishByIdUseCase
import com.aconno.sensorics.domain.interactor.ifttt.publish.AddAnyPublishUseCase
import com.aconno.sensorics.domain.interactor.repository.DeletePublishDeviceJoinUseCase
import com.aconno.sensorics.domain.interactor.repository.SavePublishDeviceJoinUseCase
import com.aconno.sensorics.model.mapper.GooglePublishDataMapper
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
    ): GoogleCloudPublisherViewModel = ViewModelProvider(
        googleCloudPublisherActivity,
        googleCloudPublisherViewModelFactory
    ).get(GoogleCloudPublisherViewModel::class.java)

    @Provides
    @GoogleCloudPublisherActivityScope
    fun provideGoogleCloudPublisherViewModelFactory(
        addAnyPublishUseCase: AddAnyPublishUseCase,
        getGooglePublishByIdUseCase: GetGooglePublishByIdUseCase,
        savePublishDeviceJoinUseCase: SavePublishDeviceJoinUseCase,
        deletePublishDeviceJoinUseCase: DeletePublishDeviceJoinUseCase,
        googlePublishModelDataMapper: GooglePublishModelDataMapper,
        googlePublishDataMapper: GooglePublishDataMapper
    ) = GoogleCloudPublisherViewModelFactory(
        addAnyPublishUseCase,
        getGooglePublishByIdUseCase,
        savePublishDeviceJoinUseCase,
        deletePublishDeviceJoinUseCase,
        googlePublishModelDataMapper,
        googlePublishDataMapper
    )


}