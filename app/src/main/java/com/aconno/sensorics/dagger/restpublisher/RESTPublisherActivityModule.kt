package com.aconno.sensorics.dagger.restpublisher

import androidx.lifecycle.ViewModelProvider
import com.aconno.sensorics.domain.interactor.ifttt.publish.AddAnyPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.restpublish.GetRestPublishByIdUseCase
import com.aconno.sensorics.domain.interactor.repository.*
import com.aconno.sensorics.model.mapper.RESTHeaderModelMapper
import com.aconno.sensorics.model.mapper.RESTHttpGetParamModelMapper
import com.aconno.sensorics.model.mapper.RESTPublishDataMapper
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
    ): RestPublisherViewModel = ViewModelProvider(
        restPublisherActivity,
        restPublisherViewModelFactory
    ).get(RestPublisherViewModel::class.java)

    @Provides
    @RESTPublisherActivityScope
    fun provideRestPublisherViewModelFactory(
        getRestPublishByIdUseCase: GetRestPublishByIdUseCase,
        addAnyPublishUseCase: AddAnyPublishUseCase,

        restPublishModelDataMapper: RESTPublishModelDataMapper,
        restPublishDataMapper: RESTPublishDataMapper,

        getRestHeadersByIdUseCase: GetRestHeadersByIdUseCase,
        saveRestHeaderUseCase: SaveRestHeaderUseCase,
        restHeaderModelMapper: RESTHeaderModelMapper,

        getRestHttpGetParamsByIdUseCase: GetRestHttpGetParamsByIdUseCase,
        saveRestHttpGetParamUseCase: SaveRestHttpGetParamUseCase,
        restHttpGetParamModelMapper: RESTHttpGetParamModelMapper,

        savePublishDeviceJoinUseCase: SavePublishDeviceJoinUseCase,
        deletePublishDeviceJoinUseCase: DeletePublishDeviceJoinUseCase
    ) = RestPublisherViewModelFactory(
        getRestPublishByIdUseCase,
        addAnyPublishUseCase,
        restPublishModelDataMapper,
        restPublishDataMapper,
        getRestHeadersByIdUseCase,
        saveRestHeaderUseCase,
        restHeaderModelMapper,
        getRestHttpGetParamsByIdUseCase,
        saveRestHttpGetParamUseCase,
        restHttpGetParamModelMapper,
        savePublishDeviceJoinUseCase,
        deletePublishDeviceJoinUseCase
    )


}
