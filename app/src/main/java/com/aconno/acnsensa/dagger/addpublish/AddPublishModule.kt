package com.aconno.acnsensa.dagger.addpublish

import android.arch.lifecycle.ViewModelProviders
import com.aconno.acnsensa.domain.ifttt.GooglePublishRepository
import com.aconno.acnsensa.domain.ifttt.PublishDeviceJoinRepository
import com.aconno.acnsensa.domain.ifttt.RESTPublishRepository
import com.aconno.acnsensa.domain.interactor.ifttt.gpublish.AddGooglePublishUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.rpublish.AddRESTPublishUseCase
import com.aconno.acnsensa.domain.interactor.repository.*
import com.aconno.acnsensa.domain.repository.DeviceRepository
import com.aconno.acnsensa.model.mapper.DeviceRelationModelMapper
import com.aconno.acnsensa.model.mapper.GooglePublishModelDataMapper
import com.aconno.acnsensa.model.mapper.RESTHeaderModelMapper
import com.aconno.acnsensa.model.mapper.RESTPublishModelDataMapper
import com.aconno.acnsensa.ui.settings.AddPublishActivity
import com.aconno.acnsensa.viewmodel.PublishViewModel
import com.aconno.acnsensa.viewmodel.factory.PublishViewModelFactory
import dagger.Module
import dagger.Provides

/**
 * @author aconno
 */
@Module
class AddPublishModule(private val addPublishActivity: AddPublishActivity) {

    @Provides
    @AddPublishActivityScope
    fun providePublishViewModel(
        publishViewModelFactory: PublishViewModelFactory
    ) = ViewModelProviders.of(addPublishActivity, publishViewModelFactory)
        .get(PublishViewModel::class.java)

    @Provides
    @AddPublishActivityScope
    fun providePublishViewModelFactory(
        addGooglePublishUseCase: AddGooglePublishUseCase,
        addRESTPublishUseCase: AddRESTPublishUseCase,
        googlePublishModelDataMapper: GooglePublishModelDataMapper,
        restPublishModelDataMapper: RESTPublishModelDataMapper,
        savePublishDeviceJoinUseCase: SavePublishDeviceJoinUseCase,
        deletePublishDeviceJoinUseCase: DeletePublishDeviceJoinUseCase,
        getDevicesThatConnectedWithGooglePublishUseCase: GetDevicesThatConnectedWithGooglePublishUseCase,
        getDevicesThatConnectedWithRESTPublishUseCase: GetDevicesThatConnectedWithRESTPublishUseCase,
        getSavedDevicesUseCase: GetSavedDevicesMaybeUseCase,
        deviceRelationModelMapper: DeviceRelationModelMapper,
        saveRESTHeaderUseCase: SaveRESTHeaderUseCase,
        deleteRESTHeaderUseCase: DeleteRESTHeaderUseCase,
        getRESTHeadersByIdUseCase: GetRESTHeadersByIdUseCase,
        restHeaderModelMapper: RESTHeaderModelMapper
    ) =
        PublishViewModelFactory(
            addGooglePublishUseCase,
            addRESTPublishUseCase,
            googlePublishModelDataMapper,
            restPublishModelDataMapper,
            savePublishDeviceJoinUseCase,
            deletePublishDeviceJoinUseCase,
            getDevicesThatConnectedWithGooglePublishUseCase,
            getDevicesThatConnectedWithRESTPublishUseCase,
            getSavedDevicesUseCase,
            deviceRelationModelMapper,
            saveRESTHeaderUseCase,
            deleteRESTHeaderUseCase,
            getRESTHeadersByIdUseCase,
            restHeaderModelMapper
        )

    @Provides
    @AddPublishActivityScope
    fun provideAddGooglePublishUseCase(googlePublishRepository: GooglePublishRepository): AddGooglePublishUseCase {
        return AddGooglePublishUseCase(
            googlePublishRepository
        )
    }

    @Provides
    @AddPublishActivityScope
    fun provideAddRESTPublishUseCase(restPublishRepository: RESTPublishRepository): AddRESTPublishUseCase {
        return AddRESTPublishUseCase(
            restPublishRepository
        )
    }

    @Provides
    @AddPublishActivityScope
    fun provideSavePublishDeviceJoinUseCase(publishDeviceJoinRepository: PublishDeviceJoinRepository): SavePublishDeviceJoinUseCase {
        return SavePublishDeviceJoinUseCase(publishDeviceJoinRepository)
    }

    @Provides
    @AddPublishActivityScope
    fun provideDeletePublishDeviceJoinUseCase(publishDeviceJoinRepository: PublishDeviceJoinRepository): DeletePublishDeviceJoinUseCase {
        return DeletePublishDeviceJoinUseCase(publishDeviceJoinRepository)
    }

    @Provides
    @AddPublishActivityScope
    fun provideGetDevicesThatConnectedWithGooglePublishUseCase(publishDeviceJoinRepository: PublishDeviceJoinRepository): GetDevicesThatConnectedWithGooglePublishUseCase {
        return GetDevicesThatConnectedWithGooglePublishUseCase(publishDeviceJoinRepository)
    }

    @Provides
    @AddPublishActivityScope
    fun provideGetDevicesThatConnectedWithRESTPublishUseCase(publishDeviceJoinRepository: PublishDeviceJoinRepository): GetDevicesThatConnectedWithRESTPublishUseCase {
        return GetDevicesThatConnectedWithRESTPublishUseCase(publishDeviceJoinRepository)
    }

    @Provides
    @AddPublishActivityScope
    fun provideGetSavedDevicesUseCase(deviceRepository: DeviceRepository): GetSavedDevicesMaybeUseCase {
        return GetSavedDevicesMaybeUseCase(deviceRepository)
    }

    @Provides
    @AddPublishActivityScope
    fun provideSaveRESTHeaderUseCase(restPublishRepository: RESTPublishRepository): SaveRESTHeaderUseCase {
        return SaveRESTHeaderUseCase(restPublishRepository)
    }

    @Provides
    @AddPublishActivityScope
    fun provideDeleteRESTHeaderUseCase(restPublishRepository: RESTPublishRepository): DeleteRESTHeaderUseCase {
        return DeleteRESTHeaderUseCase(restPublishRepository)
    }

    @Provides
    @AddPublishActivityScope
    fun provideGetRESTHeadersByIdUseCase(restPublishRepository: RESTPublishRepository): GetRESTHeadersByIdUseCase {
        return GetRESTHeadersByIdUseCase(restPublishRepository)
    }
}