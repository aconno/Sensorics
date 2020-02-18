package com.aconno.sensorics.dagger.restpublisher

import androidx.lifecycle.ViewModelProviders
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
class RESTPublisherActivityModule {

    @Provides
    @RESTPublisherActivityScope
    fun provideRestPublisherViewModel(
        restPublisherActivity: RestPublisherActivity,
        restPublisherViewModelFactory: RestPublisherViewModelFactory
    ) = ViewModelProviders.of(restPublisherActivity, restPublisherViewModelFactory)
        .get(RestPublisherViewModel::class.java)

    @Provides
    @RESTPublisherActivityScope
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




}
