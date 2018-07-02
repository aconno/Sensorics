package com.aconno.acnsensa.dagger.restpublisher

import android.arch.lifecycle.ViewModelProviders
import com.aconno.acnsensa.domain.ifttt.PublishDeviceJoinRepository
import com.aconno.acnsensa.domain.ifttt.RESTPublishRepository
import com.aconno.acnsensa.domain.interactor.ifttt.rpublish.AddRESTPublishUseCase
import com.aconno.acnsensa.domain.interactor.repository.*
import com.aconno.acnsensa.domain.repository.DeviceRepository
import com.aconno.acnsensa.model.mapper.DeviceRelationModelMapper
import com.aconno.acnsensa.model.mapper.RESTHeaderModelMapper
import com.aconno.acnsensa.model.mapper.RESTPublishModelDataMapper
import com.aconno.acnsensa.ui.settings.publishers.selectpublish.RESTPublisherActivity
import com.aconno.acnsensa.viewmodel.RestPublisherViewModel
import com.aconno.acnsensa.viewmodel.factory.RestPublisherViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class RESTPublisherModule(private val restPublisherActivity: RESTPublisherActivity) {

    @Provides
    @RESTPublisherScope
    fun provideRestPublisherViewModel(
        restPublisherViewModelFactory: RestPublisherViewModelFactory
    ) = ViewModelProviders.of(restPublisherActivity, restPublisherViewModelFactory)
        .get(RestPublisherViewModel::class.java)

    @Provides
    @RESTPublisherScope
    fun provideRestPublisherViewModelFactory(
        addRESTPublishUseCase: AddRESTPublishUseCase,
        restPublishModelDataMapper: RESTPublishModelDataMapper,
        savePublishDeviceJoinUseCase: SavePublishDeviceJoinUseCase,
        deletePublishDeviceJoinUseCase: DeletePublishDeviceJoinUseCase,
        devicesThatConnectedWithRESTPublishUseCase: GetDevicesThatConnectedWithRESTPublishUseCase,
        savedDevicesMaybeUseCase: GetSavedDevicesMaybeUseCase,
        deviceRelationModelMapper: DeviceRelationModelMapper,
        saveRESTHeaderUseCase: SaveRESTHeaderUseCase,
        deleteRESTHeaderUseCase: DeleteRESTHeaderUseCase,
        getRESTHeadersByIdUseCase: GetRESTHeadersByIdUseCase,
        restHeaderModelMapper: RESTHeaderModelMapper
    ) = RestPublisherViewModelFactory(
        addRESTPublishUseCase,
        restPublishModelDataMapper,
        savePublishDeviceJoinUseCase,
        deletePublishDeviceJoinUseCase,
        devicesThatConnectedWithRESTPublishUseCase,
        savedDevicesMaybeUseCase,
        deviceRelationModelMapper,
        saveRESTHeaderUseCase,
        deleteRESTHeaderUseCase,
        getRESTHeadersByIdUseCase,
        restHeaderModelMapper
    )

    @Provides
    @RESTPublisherScope
    fun provideAddRESTPublishUseCase(restPublishRepository: RESTPublishRepository): AddRESTPublishUseCase {
        return AddRESTPublishUseCase(restPublishRepository)
    }

    @Provides
    @RESTPublisherScope
    fun provideSavePublishDeviceJoinUseCase(publishDeviceJoinRepository: PublishDeviceJoinRepository): SavePublishDeviceJoinUseCase {
        return SavePublishDeviceJoinUseCase(publishDeviceJoinRepository)
    }

    @Provides
    @RESTPublisherScope
    fun provideDeletePublishDeviceJoinUseCase(publishDeviceJoinRepository: PublishDeviceJoinRepository): DeletePublishDeviceJoinUseCase {
        return DeletePublishDeviceJoinUseCase(publishDeviceJoinRepository)
    }

    @Provides
    @RESTPublisherScope
    fun provideGetDevicesThatConnectedWithRESTPublishUseCase(publishDeviceJoinRepository: PublishDeviceJoinRepository): GetDevicesThatConnectedWithRESTPublishUseCase {
        return GetDevicesThatConnectedWithRESTPublishUseCase(publishDeviceJoinRepository)
    }

    @Provides
    @RESTPublisherScope
    fun provideSaveRESTHeaderUseCase(restPublishRepository: RESTPublishRepository): SaveRESTHeaderUseCase {
        return SaveRESTHeaderUseCase(restPublishRepository)
    }

    @Provides
    @RESTPublisherScope
    fun provideDeleteRESTHeaderUseCase(restPublishRepository: RESTPublishRepository): DeleteRESTHeaderUseCase {
        return DeleteRESTHeaderUseCase(restPublishRepository)
    }

    @Provides
    @RESTPublisherScope
    fun provideGetRESTHeadersByIdUseCase(restPublishRepository: RESTPublishRepository): GetRESTHeadersByIdUseCase {
        return GetRESTHeadersByIdUseCase(restPublishRepository)
    }
}