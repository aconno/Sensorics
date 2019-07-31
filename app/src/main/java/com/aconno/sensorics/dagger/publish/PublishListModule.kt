package com.aconno.sensorics.dagger.publish

import androidx.lifecycle.ViewModelProviders
import com.aconno.sensorics.domain.ifttt.GooglePublishRepository
import com.aconno.sensorics.domain.ifttt.MqttPublishRepository
import com.aconno.sensorics.domain.ifttt.RestPublishRepository
import com.aconno.sensorics.domain.interactor.ifttt.UpdatePublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.googlepublish.AddGooglePublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.googlepublish.DeleteGooglePublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.googlepublish.GetAllGooglePublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.mqttpublish.AddMqttPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.mqttpublish.DeleteMqttPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.mqttpublish.GetAllMqttPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.restpublish.AddRestPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.restpublish.DeleteRestPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.restpublish.GetAllRestPublishUseCase
import com.aconno.sensorics.domain.interactor.publisher.ConvertJsonToPublishersUseCase
import com.aconno.sensorics.domain.interactor.publisher.ConvertPublishersToJsonUseCase
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
    fun provideAddGooglePublishUseCase(
        repository: GooglePublishRepository
    ) : AddGooglePublishUseCase {
        return AddGooglePublishUseCase(repository)
    }


    @Provides
    @PublishListScope
    fun provideAddRestPublishUseCase(
        repository: RestPublishRepository
    ) : AddRestPublishUseCase {
        return AddRestPublishUseCase(repository)
    }


    @Provides
    @PublishListScope
    fun provideAddMqttPublishUseCase(
        repository: MqttPublishRepository
    ) : AddMqttPublishUseCase {
        return AddMqttPublishUseCase(repository)
    }

    @Provides
    @PublishListScope
    fun providePublishListViewModelFactory(
        getAllGooglePublishUseCase: GetAllGooglePublishUseCase,
        getAllRestPublishUseCase: GetAllRestPublishUseCase,
        googlePublishDataMapper: GooglePublishDataMapper,
        googlePublishModelDataMapper: GooglePublishModelDataMapper,
        restPublishDataMapper: RESTPublishDataMapper,
        restPublishModelDataMapper: RESTPublishModelDataMapper,
        deleteGooglePublishUseCase: DeleteGooglePublishUseCase,
        deleteRestPublishUseCase: DeleteRestPublishUseCase,
        getAllMqttPublishUseCase: GetAllMqttPublishUseCase,
        mqttPublishModelDataMapper: MqttPublishModelDataMapper,
        deleteMqttPublishUseCase: DeleteMqttPublishUseCase,
        updatePublishUseCase: UpdatePublishUseCase,
        addGooglePublishUseCase: AddGooglePublishUseCase,
        addRestPublishUseCase: AddRestPublishUseCase,
        addMqttPublishUseCase: AddMqttPublishUseCase
    ) =
        PublishListViewModelFactory(
            getAllGooglePublishUseCase,
            getAllRestPublishUseCase,
            googlePublishDataMapper,
            googlePublishModelDataMapper,
            restPublishDataMapper,
            restPublishModelDataMapper,
            deleteGooglePublishUseCase,
            deleteRestPublishUseCase,
            getAllMqttPublishUseCase,
            mqttPublishModelDataMapper,
            deleteMqttPublishUseCase,
            updatePublishUseCase,
            addGooglePublishUseCase,
            addRestPublishUseCase,
            addMqttPublishUseCase
        )

    @Provides
    @PublishListScope
    fun provideConvertPublishersToJsonUseCase() : ConvertPublishersToJsonUseCase {
        return ConvertPublishersToJsonUseCase()
    }

    @Provides
    @PublishListScope
    fun provideConvertJsonToPublishersUseCase() : ConvertJsonToPublishersUseCase {
        return ConvertJsonToPublishersUseCase()
    }
}