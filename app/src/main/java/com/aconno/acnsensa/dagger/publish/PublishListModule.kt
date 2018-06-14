package com.aconno.acnsensa.dagger.publish

import android.arch.lifecycle.ViewModelProviders
import com.aconno.acnsensa.domain.ifttt.GooglePublishRepository
import com.aconno.acnsensa.domain.ifttt.MqttPublishRepository
import com.aconno.acnsensa.domain.ifttt.RESTPublishRepository
import com.aconno.acnsensa.domain.interactor.ifttt.gpublish.DeleteGooglePublishUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.gpublish.GetAllGooglePublishUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.gpublish.UpdateGooglePublishUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.mpublish.AddMqttPublishUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.mpublish.DeleteMqttPublishUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.mpublish.GetAllMqttPublishUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.rpublish.DeleteRestPublishUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.rpublish.GetAllRESTPublishUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.rpublish.UpdateRESTPublishUserCase
import com.aconno.acnsensa.model.mapper.*
import com.aconno.acnsensa.ui.settings.PublishListFragment
import com.aconno.acnsensa.viewmodel.PublishListViewModel
import com.aconno.acnsensa.viewmodel.factory.PublishListViewModelFactory
import dagger.Module
import dagger.Provides

/**
 * @author aconno
 */
@Module
class PublishListModule(private val publishListFragment: PublishListFragment) {

    @Provides
    @PublishListScope
    fun provideGetAllGooglePublishUseCase(googlePublishRepository: GooglePublishRepository): GetAllGooglePublishUseCase {
        return GetAllGooglePublishUseCase(
            googlePublishRepository
        )
    }

    @Provides
    @PublishListScope
    fun provideGetAllRESTPublishUseCase(restPublishRepository: RESTPublishRepository): GetAllRESTPublishUseCase {
        return GetAllRESTPublishUseCase(
            restPublishRepository
        )
    }

    @Provides
    @PublishListScope
    fun provideGetAllMqttPublishUseCase(mqttPublishRepository: MqttPublishRepository): GetAllMqttPublishUseCase {
        return GetAllMqttPublishUseCase(
            mqttPublishRepository
        )
    }

    @Provides
    @PublishListScope
    fun provideUpdateGooglePublishUseCase(googlePublishRepository: GooglePublishRepository): UpdateGooglePublishUseCase {
        return UpdateGooglePublishUseCase(
            googlePublishRepository
        )
    }

    @Provides
    @PublishListScope
    fun provideUpdateRESTPublishUseCase(restPublishRepository: RESTPublishRepository): UpdateRESTPublishUserCase {
        return UpdateRESTPublishUserCase(
            restPublishRepository
        )
    }

    @Provides
    @PublishListScope
    fun provideAddMqttPublishUseCase(mqttPublishRepository: MqttPublishRepository): AddMqttPublishUseCase {
        return AddMqttPublishUseCase(
            mqttPublishRepository
        )
    }

    @Provides
    @PublishListScope
    fun provideDeleteGooglePublishUseCase(googlePublishRepository: GooglePublishRepository): DeleteGooglePublishUseCase {
        return DeleteGooglePublishUseCase(
            googlePublishRepository
        )
    }

    @Provides
    @PublishListScope
    fun provideDeleteRESTPublishUseCase(restPublishRepository: RESTPublishRepository): DeleteRestPublishUseCase {
        return DeleteRestPublishUseCase(
            restPublishRepository
        )
    }

    @Provides
    @PublishListScope
    fun provideDeleteMqttPublishUseCase(mqttPublishRepository: MqttPublishRepository): DeleteMqttPublishUseCase {
        return DeleteMqttPublishUseCase(
            mqttPublishRepository
        )
    }

    @Provides
    @PublishListScope
    fun providePublishListViewModel(
        publishViewModelFactory: PublishListViewModelFactory
    ) = ViewModelProviders.of(publishListFragment, publishViewModelFactory)
        .get(PublishListViewModel::class.java)

    @Provides
    @PublishListScope
    fun providePublishListViewModelFactory(
        getAllGooglePublishUseCase: GetAllGooglePublishUseCase,
        getAllRESTPublishUseCase: GetAllRESTPublishUseCase,
        updateGooglePublishUseCase: UpdateGooglePublishUseCase,
        updateRESTPublishUserCase: UpdateRESTPublishUserCase,
        googlePublishDataMapper: GooglePublishDataMapper,
        googlePublishModelDataMapper: GooglePublishModelDataMapper,
        restPublishDataMapper: RESTPublishDataMapper,
        restPublishModelDataMapper: RESTPublishModelDataMapper,
        deleteGooglePublishUseCase: DeleteGooglePublishUseCase,
        deleteRestPublishUseCase: DeleteRestPublishUseCase,
        getAllMqttPublishUseCase: GetAllMqttPublishUseCase,
        updateMqttPublishUseCase: AddMqttPublishUseCase,
        mqttPublishModelDataMapper: MqttPublishModelDataMapper,
        deleteMqttPublishUseCase: DeleteMqttPublishUseCase

    ) =
        PublishListViewModelFactory(
            getAllGooglePublishUseCase,
            getAllRESTPublishUseCase,
            updateGooglePublishUseCase,
            updateRESTPublishUserCase,
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