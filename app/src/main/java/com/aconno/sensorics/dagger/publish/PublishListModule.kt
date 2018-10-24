package com.aconno.sensorics.dagger.publish

import android.arch.lifecycle.ViewModelProviders
import com.aconno.sensorics.domain.ifttt.GooglePublishRepository
import com.aconno.sensorics.domain.ifttt.MqttPublishRepository
import com.aconno.sensorics.domain.ifttt.RestPublishRepository
import com.aconno.sensorics.domain.interactor.ifttt.googlepublish.DeleteGooglePublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.googlepublish.GetAllGooglePublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.googlepublish.UpdateGooglePublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.mqttpublish.DeleteMqttPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.mqttpublish.GetAllMqttPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.mqttpublish.UpdateMqttPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.restpublish.DeleteRestPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.restpublish.GetAllRestPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.restpublish.UpdateRestPublishUserCase
import com.aconno.sensorics.model.mapper.*
import com.aconno.sensorics.ui.settings.publishers.PublishListActivity
import com.aconno.sensorics.viewmodel.PublishListViewModel
import com.aconno.sensorics.viewmodel.factory.PublishListViewModelFactory
import dagger.Module
import dagger.Provides

/**
 * @author aconno
 */
@Module
class PublishListModule {

    @Provides
    @PublishListScope
    fun provideGetAllGooglePublishUseCase(
        googlePublishRepository: GooglePublishRepository
    ): GetAllGooglePublishUseCase {
        return GetAllGooglePublishUseCase(
            googlePublishRepository
        )
    }

    @Provides
    @PublishListScope
    fun provideGetAllRESTPublishUseCase(
        restPublishRepository: RestPublishRepository
    ): GetAllRestPublishUseCase {
        return GetAllRestPublishUseCase(
            restPublishRepository
        )
    }

    @Provides
    @PublishListScope
    fun provideGetAllMqttPublishUseCase(
        mqttPublishRepository: MqttPublishRepository
    ): GetAllMqttPublishUseCase {
        return GetAllMqttPublishUseCase(
            mqttPublishRepository
        )
    }

    @Provides
    @PublishListScope
    fun provideUpdateGooglePublishUseCase(
        googlePublishRepository: GooglePublishRepository
    ): UpdateGooglePublishUseCase {
        return UpdateGooglePublishUseCase(
            googlePublishRepository
        )
    }

    @Provides
    @PublishListScope
    fun provideUpdateRESTPublishUseCase(
        restPublishRepository: RestPublishRepository
    ): UpdateRestPublishUserCase {
        return UpdateRestPublishUserCase(
            restPublishRepository
        )
    }

    @Provides
    @PublishListScope
    fun provideUpdateMqttPublishUseCase(
        mqttPublishRepository: MqttPublishRepository
    ): UpdateMqttPublishUseCase {
        return UpdateMqttPublishUseCase(
            mqttPublishRepository
        )
    }

    @Provides
    @PublishListScope
    fun provideDeleteGooglePublishUseCase(
        googlePublishRepository: GooglePublishRepository
    ): DeleteGooglePublishUseCase {
        return DeleteGooglePublishUseCase(
            googlePublishRepository
        )
    }

    @Provides
    @PublishListScope
    fun provideDeleteRESTPublishUseCase(
        restPublishRepository: RestPublishRepository
    ): DeleteRestPublishUseCase {
        return DeleteRestPublishUseCase(
            restPublishRepository
        )
    }

    @Provides
    @PublishListScope
    fun provideDeleteMqttPublishUseCase(
        mqttPublishRepository: MqttPublishRepository
    ): DeleteMqttPublishUseCase {
        return DeleteMqttPublishUseCase(
            mqttPublishRepository
        )
    }

    @Provides
    @PublishListScope
    fun providePublishListViewModel(
        activity: PublishListActivity,
        publishViewModelFactory: PublishListViewModelFactory
    ) = ViewModelProviders.of(activity, publishViewModelFactory)
        .get(PublishListViewModel::class.java)

    @Provides
    @PublishListScope
    fun providePublishListViewModelFactory(
        getAllGooglePublishUseCase: GetAllGooglePublishUseCase,
        getAllRestPublishUseCase: GetAllRestPublishUseCase,
        updateGooglePublishUseCase: UpdateGooglePublishUseCase,
        updateRestPublishUserCase: UpdateRestPublishUserCase,
        googlePublishDataMapper: GooglePublishDataMapper,
        googlePublishModelDataMapper: GooglePublishModelDataMapper,
        restPublishDataMapper: RESTPublishDataMapper,
        restPublishModelDataMapper: RESTPublishModelDataMapper,
        deleteGooglePublishUseCase: DeleteGooglePublishUseCase,
        deleteRestPublishUseCase: DeleteRestPublishUseCase,
        getAllMqttPublishUseCase: GetAllMqttPublishUseCase,
        updateMqttPublishUseCase: UpdateMqttPublishUseCase,
        mqttPublishModelDataMapper: MqttPublishModelDataMapper,
        deleteMqttPublishUseCase: DeleteMqttPublishUseCase

    ) =
        PublishListViewModelFactory(
            getAllGooglePublishUseCase,
            getAllRestPublishUseCase,
            updateGooglePublishUseCase,
            updateRestPublishUserCase,
            googlePublishDataMapper,
            googlePublishModelDataMapper,
            restPublishDataMapper,
            restPublishModelDataMapper,
            deleteGooglePublishUseCase,
            deleteRestPublishUseCase,
            getAllMqttPublishUseCase,
            updateMqttPublishUseCase,
            mqttPublishModelDataMapper,
            deleteMqttPublishUseCase
        )
}