package com.aconno.sensorics.dagger.restpublisher

import androidx.lifecycle.ViewModelProviders
import com.aconno.sensorics.domain.ifttt.PublishDeviceJoinRepository
import com.aconno.sensorics.domain.ifttt.RestPublishRepository
import com.aconno.sensorics.domain.interactor.ifttt.restpublish.AddRestPublishUseCase
import com.aconno.sensorics.domain.interactor.repository.*
import com.aconno.sensorics.model.mapper.RESTHeaderModelMapper
import com.aconno.sensorics.model.mapper.RESTHttpGetParamModelMapper
import com.aconno.sensorics.model.mapper.RESTPublishModelDataMapper
import com.aconno.sensorics.ui.settings.publishers.selectpublish.RestPublisherActivity
import com.aconno.sensorics.viewmodel.RestPublisherViewModel
import com.aconno.sensorics.viewmodel.factory.RestPublisherViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class RESTPublisherModule {

    @Provides
    @RESTPublisherScope
    fun provideRestPublisherViewModel(
        restPublisherActivity: RestPublisherActivity,
        restPublisherViewModelFactory: RestPublisherViewModelFactory
    ) = ViewModelProviders.of(restPublisherActivity, restPublisherViewModelFactory)
        .get(RestPublisherViewModel::class.java)

    @Provides
    @RESTPublisherScope
    fun provideRestPublisherViewModelFactory(
        addRestPublishUseCase: AddRestPublishUseCase,
        restPublishModelDataMapper: RESTPublishModelDataMapper,
        savePublishDeviceJoinUseCase: SavePublishDeviceJoinUseCase,
        deletePublishDeviceJoinUseCase: DeletePublishDeviceJoinUseCase,
        saveRestHeaderUseCase: SaveRestHeaderUseCase,
        getRestHeadersByIdUseCase: GetRestHeadersByIdUseCase,
        restHeaderModelMapper: RESTHeaderModelMapper,
        saveRestHttpGetParamUseCase: SaveRestHttpGetParamUseCase,
        getRestHttpGetParamsByIdUseCase: GetRestHttpGetParamsByIdUseCase,
        restHttpGetParamModelMapper: RESTHttpGetParamModelMapper
    ) = RestPublisherViewModelFactory(
        addRestPublishUseCase,
        restPublishModelDataMapper,
        savePublishDeviceJoinUseCase,
        deletePublishDeviceJoinUseCase,
        saveRestHeaderUseCase,
        getRestHeadersByIdUseCase,
        restHeaderModelMapper,
        saveRestHttpGetParamUseCase,
        getRestHttpGetParamsByIdUseCase,
        restHttpGetParamModelMapper
    )

    @Provides
    @RESTPublisherScope
    fun provideAddRESTPublishUseCase(
        restPublishRepository: RestPublishRepository
    ): AddRestPublishUseCase {
        return AddRestPublishUseCase(restPublishRepository)
    }

    @Provides
    @RESTPublisherScope
    fun provideSavePublishDeviceJoinUseCase(
        publishDeviceJoinRepository: PublishDeviceJoinRepository
    ): SavePublishDeviceJoinUseCase {
        return SavePublishDeviceJoinUseCase(publishDeviceJoinRepository)
    }

    @Provides
    @RESTPublisherScope
    fun provideDeletePublishDeviceJoinUseCase(
        publishDeviceJoinRepository: PublishDeviceJoinRepository
    ): DeletePublishDeviceJoinUseCase {
        return DeletePublishDeviceJoinUseCase(publishDeviceJoinRepository)
    }

    @Provides
    @RESTPublisherScope
    fun provideSaveRESTHeaderUseCase(
        restPublishRepository: RestPublishRepository
    ): SaveRestHeaderUseCase {
        return SaveRestHeaderUseCase(restPublishRepository)
    }

    @Provides
    @RESTPublisherScope
    fun provideSaveRESTHttpGetParamUseCase(
        restPublishRepository: RestPublishRepository
    ): SaveRestHttpGetParamUseCase {
        return SaveRestHttpGetParamUseCase(restPublishRepository)
    }

    @Provides
    @RESTPublisherScope
    fun provideDeleteRESTHeaderUseCase(
        restPublishRepository: RestPublishRepository
    ): DeleteRestHeaderUseCase {
        return DeleteRestHeaderUseCase(restPublishRepository)
    }

    @Provides
    @RESTPublisherScope
    fun provideDeleteRESTHttpGetParamUseCase(
        restPublishRepository: RestPublishRepository
    ): DeleteRestHttpGetParamUseCase {
        return DeleteRestHttpGetParamUseCase(restPublishRepository)
    }

    @Provides
    @RESTPublisherScope
    fun provideGetRESTHeadersByIdUseCase(
        restPublishRepository: RestPublishRepository
    ): GetRestHeadersByIdUseCase {
        return GetRestHeadersByIdUseCase(restPublishRepository)
    }

    @Provides
    @RESTPublisherScope
    fun provideGetRESTHttpGetParamsByIdUseCase(
        restPublishRepository: RestPublishRepository
    ): GetRestHttpGetParamsByIdUseCase {
        return GetRestHttpGetParamsByIdUseCase(restPublishRepository)
    }
}
