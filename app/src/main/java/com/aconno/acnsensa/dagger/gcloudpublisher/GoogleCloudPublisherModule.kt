package com.aconno.acnsensa.dagger.gcloudpublisher

import android.arch.lifecycle.ViewModelProviders
import com.aconno.acnsensa.domain.ifttt.GooglePublishRepository
import com.aconno.acnsensa.domain.ifttt.PublishDeviceJoinRepository
import com.aconno.acnsensa.domain.interactor.ifttt.gpublish.AddGooglePublishUseCase
import com.aconno.acnsensa.domain.interactor.repository.DeletePublishDeviceJoinUseCase
import com.aconno.acnsensa.domain.interactor.repository.GetDevicesThatConnectedWithGooglePublishUseCase
import com.aconno.acnsensa.domain.interactor.repository.GetSavedDevicesMaybeUseCase
import com.aconno.acnsensa.domain.interactor.repository.SavePublishDeviceJoinUseCase
import com.aconno.acnsensa.domain.repository.DeviceRepository
import com.aconno.acnsensa.model.mapper.DeviceRelationModelMapper
import com.aconno.acnsensa.model.mapper.GooglePublishModelDataMapper
import com.aconno.acnsensa.ui.settings.selectpublish.GoogleCloudPublisherActivity
import com.aconno.acnsensa.viewmodel.GoogleCloudPublisherViewModel
import com.aconno.acnsensa.viewmodel.factory.GoogleCloudPublisherViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class GoogleCloudPublisherModule(private val googleCloudPublisherActivity: GoogleCloudPublisherActivity) {

    @Provides
    @GoogleCloudPublisherScope
    fun provideGoogleCloudPublisherViewModel(
        googleCloudPublisherViewModelFactory: GoogleCloudPublisherViewModelFactory
    ) = ViewModelProviders.of(googleCloudPublisherActivity, googleCloudPublisherViewModelFactory)
        .get(GoogleCloudPublisherViewModel::class.java)

    @Provides
    @GoogleCloudPublisherScope
    fun provideGoogleCloudPublisherViewModelFactory(
        addGooglePublishUseCase: AddGooglePublishUseCase,
        googlePublishModelDataMapper: GooglePublishModelDataMapper,
        savePublishDeviceJoinUseCase: SavePublishDeviceJoinUseCase,
        deletePublishDeviceJoinUseCase: DeletePublishDeviceJoinUseCase,
        devicesThatConnectedWithGooglePublishUseCase: GetDevicesThatConnectedWithGooglePublishUseCase,
        savedDevicesMaybeUseCase: GetSavedDevicesMaybeUseCase,
        deviceRelationModelMapper: DeviceRelationModelMapper
    ) = GoogleCloudPublisherViewModelFactory(
        addGooglePublishUseCase,
        googlePublishModelDataMapper,
        savePublishDeviceJoinUseCase,
        deletePublishDeviceJoinUseCase,
        devicesThatConnectedWithGooglePublishUseCase,
        savedDevicesMaybeUseCase,
        deviceRelationModelMapper
    )

    @Provides
    @GoogleCloudPublisherScope
    fun provideAddGooglePublishUseCase(googlePublishRepository: GooglePublishRepository): AddGooglePublishUseCase {
        return AddGooglePublishUseCase(
            googlePublishRepository
        )
    }

    @Provides
    @GoogleCloudPublisherScope
    fun provideSavePublishDeviceJoinUseCase(publishDeviceJoinRepository: PublishDeviceJoinRepository): SavePublishDeviceJoinUseCase {
        return SavePublishDeviceJoinUseCase(publishDeviceJoinRepository)
    }

    @Provides
    @GoogleCloudPublisherScope
    fun provideDeletePublishDeviceJoinUseCase(publishDeviceJoinRepository: PublishDeviceJoinRepository): DeletePublishDeviceJoinUseCase {
        return DeletePublishDeviceJoinUseCase(publishDeviceJoinRepository)
    }

    @Provides
    @GoogleCloudPublisherScope
    fun provideGetDevicesThatConnectedWithGooglePublishUseCase(publishDeviceJoinRepository: PublishDeviceJoinRepository): GetDevicesThatConnectedWithGooglePublishUseCase {
        return GetDevicesThatConnectedWithGooglePublishUseCase(publishDeviceJoinRepository)
    }

    @Provides
    @GoogleCloudPublisherScope
    fun provideGetSavedDevicesUseCase(deviceRepository: DeviceRepository): GetSavedDevicesMaybeUseCase {
        return GetSavedDevicesMaybeUseCase(deviceRepository)
    }

}