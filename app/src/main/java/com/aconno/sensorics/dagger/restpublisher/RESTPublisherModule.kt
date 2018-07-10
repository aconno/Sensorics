package com.aconno.sensorics.dagger.restpublisher

import android.arch.lifecycle.ViewModelProviders
import com.aconno.sensorics.domain.ifttt.PublishDeviceJoinRepository
import com.aconno.sensorics.domain.ifttt.RESTPublishRepository
import com.aconno.sensorics.domain.interactor.ifttt.rpublish.AddRESTPublishUseCase
import com.aconno.sensorics.domain.interactor.repository.*
import com.aconno.sensorics.domain.repository.DeviceRepository
import com.aconno.sensorics.model.mapper.DeviceRelationModelMapper
import com.aconno.sensorics.model.mapper.RESTHeaderModelMapper
import com.aconno.sensorics.model.mapper.RESTHttpGetParamModelMapper
import com.aconno.sensorics.model.mapper.RESTPublishModelDataMapper
import com.aconno.sensorics.ui.settings.publishers.selectpublish.RESTPublisherActivity
import com.aconno.sensorics.viewmodel.RestPublisherViewModel
import com.aconno.sensorics.viewmodel.factory.RestPublisherViewModelFactory
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
        restHeaderModelMapper: RESTHeaderModelMapper,
        saveRESTHttpGetParamUseCase: SaveRESTHttpGetParamUseCase,
        deleteRESTHttpGetParamUseCase: DeleteRESTHttpGetParamUseCase,
        getRESTHttpGetParamsByIdUseCase: GetRESTHttpGetParamsByIdUseCase,
        restHttpGetParamModelMapper: RESTHttpGetParamModelMapper
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
        restHeaderModelMapper,
        saveRESTHttpGetParamUseCase,
        deleteRESTHttpGetParamUseCase,
        getRESTHttpGetParamsByIdUseCase,
        restHttpGetParamModelMapper
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
    fun provideSaveRESTHttpGetParamUseCase(restPublishRepository: RESTPublishRepository): SaveRESTHttpGetParamUseCase {
        return SaveRESTHttpGetParamUseCase(restPublishRepository)
    }

    @Provides
    @RESTPublisherScope
    fun provideDeleteRESTHeaderUseCase(restPublishRepository: RESTPublishRepository): DeleteRESTHeaderUseCase {
        return DeleteRESTHeaderUseCase(restPublishRepository)
    }

    @Provides
    @RESTPublisherScope
    fun provideDeleteRESTHttpGetParamUseCase(restPublishRepository: RESTPublishRepository): DeleteRESTHttpGetParamUseCase {
        return DeleteRESTHttpGetParamUseCase(restPublishRepository)
    }

    @Provides
    @RESTPublisherScope
    fun provideGetRESTHeadersByIdUseCase(restPublishRepository: RESTPublishRepository): GetRESTHeadersByIdUseCase {
        return GetRESTHeadersByIdUseCase(restPublishRepository)
    }

    @Provides
    @RESTPublisherScope
    fun provideGetRESTHttpGetParamsByIdUseCase(restPublishRepository: RESTPublishRepository): GetRESTHttpGetParamsByIdUseCase {
        return GetRESTHttpGetParamsByIdUseCase(restPublishRepository)
    }
}