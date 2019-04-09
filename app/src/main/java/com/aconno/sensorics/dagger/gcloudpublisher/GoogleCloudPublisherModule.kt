package com.aconno.sensorics.dagger.gcloudpublisher

import androidx.lifecycle.ViewModelProviders
import com.aconno.sensorics.domain.ifttt.GooglePublishRepository
import com.aconno.sensorics.domain.ifttt.PublishDeviceJoinRepository
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
class GoogleCloudPublisherModule {

    @Provides
    @GoogleCloudPublisherScope
    fun provideGoogleCloudPublisherViewModel(
        googleCloudPublisherActivity: GoogleCloudPublisherActivity,
        googleCloudPublisherViewModelFactory: GoogleCloudPublisherViewModelFactory
    ) = ViewModelProviders.of(googleCloudPublisherActivity, googleCloudPublisherViewModelFactory)
        .get(GoogleCloudPublisherViewModel::class.java)

    @Provides
    @GoogleCloudPublisherScope
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

    @Provides
    @GoogleCloudPublisherScope
    fun provideAddGooglePublishUseCase(
        googlePublishRepository: GooglePublishRepository
    ): AddGooglePublishUseCase {
        return AddGooglePublishUseCase(
            googlePublishRepository
        )
    }

    @Provides
    @GoogleCloudPublisherScope
    fun provideSavePublishDeviceJoinUseCase(
        publishDeviceJoinRepository: PublishDeviceJoinRepository
    ): SavePublishDeviceJoinUseCase {
        return SavePublishDeviceJoinUseCase(publishDeviceJoinRepository)
    }

    @Provides
    @GoogleCloudPublisherScope
    fun provideDeletePublishDeviceJoinUseCase(
        publishDeviceJoinRepository: PublishDeviceJoinRepository
    ): DeletePublishDeviceJoinUseCase {
        return DeletePublishDeviceJoinUseCase(publishDeviceJoinRepository)
    }
}