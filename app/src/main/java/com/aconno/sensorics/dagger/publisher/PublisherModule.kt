package com.aconno.sensorics.dagger.publisher

import com.aconno.sensorics.data.mapper.*
import com.aconno.sensorics.data.repository.SensoricsDatabase
import com.aconno.sensorics.data.repository.azuremqttpublish.AzureMqttPublishRepositoryImpl
import com.aconno.sensorics.data.repository.googlepublish.GooglePublishRepositoryImpl
import com.aconno.sensorics.data.repository.mqttpublish.MqttPublishRepositoryImpl
import com.aconno.sensorics.data.repository.restpublish.RestPublishRepositoryImpl
import com.aconno.sensorics.domain.ifttt.*
import com.aconno.sensorics.domain.interactor.ifttt.UpdatePublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.azuremqttpublish.*
import com.aconno.sensorics.domain.interactor.ifttt.googlepublish.*
import com.aconno.sensorics.domain.interactor.ifttt.mqttpublish.*
import com.aconno.sensorics.domain.interactor.ifttt.restpublish.*
import com.aconno.sensorics.domain.interactor.publisher.ConvertJsonToPublishersUseCase
import com.aconno.sensorics.domain.interactor.publisher.ConvertObjectsToJsonUseCase
import com.aconno.sensorics.domain.interactor.repository.*
import dagger.Module
import dagger.Provides

@Module
class PublisherModule {


    @Provides
    @PublisherScope
    fun provideConvertPublishersToJsonUseCase(): ConvertObjectsToJsonUseCase<BasePublish> {
        return ConvertObjectsToJsonUseCase()
    }

    @Provides
    @PublisherScope
    fun provideConvertJsonToPublishersUseCase(): ConvertJsonToPublishersUseCase {
        return ConvertJsonToPublishersUseCase()
    }

    @Provides
    @PublisherScope
    fun provideGetAllEnabledGooglePublishUseCase(
        googlePublishRepository: GooglePublishRepository
    ): GetAllEnabledGooglePublishUseCase {
        return GetAllEnabledGooglePublishUseCase(
            googlePublishRepository
        )
    }

    @Provides
    @PublisherScope
    fun provideGetAllEnabledRESTPublishUseCase(
        restPublishRepository: RestPublishRepository
    ): GetAllEnabledRestPublishUseCase {
        return GetAllEnabledRestPublishUseCase(
            restPublishRepository
        )
    }

    @Provides
    @PublisherScope
    fun provideGetAllEnabledMqttPublishUseCase(
        mqttPublishRepository: MqttPublishRepository
    ): GetAllEnabledMqttPublishUseCase {
        return GetAllEnabledMqttPublishUseCase(
            mqttPublishRepository
        )
    }

    @Provides
    @PublisherScope
    fun provideGetAllEnabledAzureMqttPublishUseCase(
        azureMqttPublishRepository: AzureMqttPublishRepository
    ): GetAllEnabledAzureMqttPublishUseCase {
        return GetAllEnabledAzureMqttPublishUseCase(
            azureMqttPublishRepository
        )
    }

    @Provides
    @PublisherScope
    fun provideGetRESTHeadersByIdUseCase(
        restPublishRepository: RestPublishRepository
    ): GetRestHeadersByIdUseCase {
        return GetRestHeadersByIdUseCase(restPublishRepository)
    }

    @Provides
    @PublisherScope
    fun provideGetRESTHttpGetParamsByIdUseCase(
        restPublishRepository: RestPublishRepository
    ): GetRestHttpGetParamsByIdUseCase {
        return GetRestHttpGetParamsByIdUseCase(restPublishRepository)
    }


    @Provides
    @PublisherScope
    fun provideAddGooglePublishUseCase(
        googlePublishRepository: GooglePublishRepository
    ): AddGooglePublishUseCase {
        return AddGooglePublishUseCase(
            googlePublishRepository
        )
    }


    @Provides
    @PublisherScope
    fun provideAddMqttPublishUseCase(
        mqttPublishRepository: MqttPublishRepository
    ): AddMqttPublishUseCase {
        return AddMqttPublishUseCase(mqttPublishRepository)
    }

    @Provides
    @PublisherScope
    fun provideAddAzureMqttPublishUseCase(
        azureMqttPublishRepository: AzureMqttPublishRepository
    ): AddAzureMqttPublishUseCase {
        return AddAzureMqttPublishUseCase(azureMqttPublishRepository)
    }

    @Provides
    @PublisherScope
    fun provideAddRESTPublishUseCase(
        restPublishRepository: RestPublishRepository
    ): AddRestPublishUseCase {
        return AddRestPublishUseCase(restPublishRepository)
    }

    @Provides
    @PublisherScope
    fun provideGetGooglePublishByIdUseCase(
        googlePublishRepository: GooglePublishRepository
    ): GetGooglePublishByIdUseCase {
        return GetGooglePublishByIdUseCase(
            googlePublishRepository
        )
    }

    @Provides
    @PublisherScope
    fun provideGetMqttPublishByIdUseCase(
        mqttPublishRepository: MqttPublishRepository
    ): GetMqttPublishByIdUseCase {
        return GetMqttPublishByIdUseCase(
            mqttPublishRepository
        )
    }

    @Provides
    @PublisherScope
    fun provideGetAzureMqttPublishByIdUseCase(
        azureMqttPublishRepository: AzureMqttPublishRepository
    ): GetAzureMqttPublishByIdUseCase {
        return GetAzureMqttPublishByIdUseCase(
            azureMqttPublishRepository
        )
    }

    @Provides
    @PublisherScope
    fun provideGetRestPublishByIdUseCase(
        restPublishRepository: RestPublishRepository
    ): GetRestPublishByIdUseCase {
        return GetRestPublishByIdUseCase(
            restPublishRepository
        )
    }

    @Provides
    @PublisherScope
    fun provideGetAllGooglePublishUseCase(
        googlePublishRepository: GooglePublishRepository
    ): GetAllGooglePublishUseCase {
        return GetAllGooglePublishUseCase(
            googlePublishRepository
        )
    }

    @Provides
    @PublisherScope
    fun provideGetAllRESTPublishUseCase(
        restPublishRepository: RestPublishRepository
    ): GetAllRestPublishUseCase {
        return GetAllRestPublishUseCase(
            restPublishRepository
        )
    }

    @Provides
    @PublisherScope
    fun provideGetAllMqttPublishUseCase(
        mqttPublishRepository: MqttPublishRepository
    ): GetAllMqttPublishUseCase {
        return GetAllMqttPublishUseCase(
            mqttPublishRepository
        )
    }

    @Provides
    @PublisherScope
    fun provideGetAllAzureMqttPublishUseCase(
        azureMqttPublishRepository: AzureMqttPublishRepository
    ): GetAllAzureMqttPublishUseCase {
        return GetAllAzureMqttPublishUseCase(
            azureMqttPublishRepository
        )
    }

    @Provides
    @PublisherScope
    fun provideDeleteGooglePublishUseCase(
        googlePublishRepository: GooglePublishRepository
    ): DeleteGooglePublishUseCase {
        return DeleteGooglePublishUseCase(
            googlePublishRepository
        )
    }

    @Provides
    @PublisherScope
    fun provideDeleteRESTPublishUseCase(
        restPublishRepository: RestPublishRepository
    ): DeleteRestPublishUseCase {
        return DeleteRestPublishUseCase(
            restPublishRepository
        )
    }

    @Provides
    @PublisherScope
    fun provideDeleteMqttPublishUseCase(
        mqttPublishRepository: MqttPublishRepository
    ): DeleteMqttPublishUseCase {
        return DeleteMqttPublishUseCase(
            mqttPublishRepository
        )
    }

    @Provides
    @PublisherScope
    fun provideDeleteAzureMqttPublishUseCase(
        azureMqttPublishRepository: AzureMqttPublishRepository
    ): DeleteAzureMqttPublishUseCase {
        return DeleteAzureMqttPublishUseCase(
            azureMqttPublishRepository
        )
    }


    @Provides
    @PublisherScope
    fun provideSaveRESTHeaderUseCase(
        restPublishRepository: RestPublishRepository
    ): SaveRestHeaderUseCase {
        return SaveRestHeaderUseCase(restPublishRepository)
    }

    @Provides
    @PublisherScope
    fun provideSaveRESTHttpGetParamUseCase(
        restPublishRepository: RestPublishRepository
    ): SaveRestHttpGetParamUseCase {
        return SaveRestHttpGetParamUseCase(restPublishRepository)
    }

    @Provides
    @PublisherScope
    fun provideDeleteRESTHeaderUseCase(
        restPublishRepository: RestPublishRepository
    ): DeleteRestHeaderUseCase {
        return DeleteRestHeaderUseCase(restPublishRepository)
    }

    @Provides
    @PublisherScope
    fun provideDeleteRESTHttpGetParamUseCase(
        restPublishRepository: RestPublishRepository
    ): DeleteRestHttpGetParamUseCase {
        return DeleteRestHttpGetParamUseCase(restPublishRepository)
    }

    @Provides
    @PublisherScope
    fun provideGooglePublishRepository(
        sensoricsDatabase: SensoricsDatabase,
        googlePublishEntityDataMapper: GooglePublishEntityDataMapper,
        googlePublishDataMapper: GooglePublishDataMapper
    ): GooglePublishRepository {
        return GooglePublishRepositoryImpl(
            sensoricsDatabase.googlePublishDao(),
            googlePublishEntityDataMapper,
            googlePublishDataMapper
        )
    }

    @Provides
    @PublisherScope
    fun provideUpdatePublishUseCase(
        googlePublishRepository: GooglePublishRepository,
        mqttPublishRepository: MqttPublishRepository,
        restPublishRepository: RestPublishRepository,
        azureMqttPublishRepository: AzureMqttPublishRepository
    ): UpdatePublishUseCase =
        UpdatePublishUseCase(
            googlePublishRepository,
            mqttPublishRepository,
            azureMqttPublishRepository,
            restPublishRepository
        )

    @Provides
    @PublisherScope
    fun provideRESTPublishRepository(
        sensoricsDatabase: SensoricsDatabase,
        restPublishEntityDataMapper: RestPublishEntityDataMapper,
        restPublishDataMapper: RestPublishDataMapper,
        restHeaderDataMapper: RestHeaderDataMapper,
        restHttpGetParamDataMapper: RestHttpGetParamDataMapper
    ): RestPublishRepository {
        return RestPublishRepositoryImpl(
            sensoricsDatabase.restPublishDao(),
            restPublishEntityDataMapper,
            restPublishDataMapper,
            restHeaderDataMapper,
            restHttpGetParamDataMapper
        )
    }

    @Provides
    @PublisherScope
    fun provideMqttPublishRepository(
        sensoricsDatabase: SensoricsDatabase,
        mqttPublishDataMapper: MqttPublishDataMapper
    ): MqttPublishRepository {
        return MqttPublishRepositoryImpl(
            sensoricsDatabase.mqttPublishDao(),
            mqttPublishDataMapper
        )
    }

    @Provides
    @PublisherScope
    fun provideAzureMqttPublishRepository(
        sensoricsDatabase: SensoricsDatabase,
        azureMqttPublishDataMapper: AzureMqttPublishDataMapper
    ): AzureMqttPublishRepository {
        return AzureMqttPublishRepositoryImpl(
            sensoricsDatabase.azureMqttPublishDao(),
            azureMqttPublishDataMapper
        )
    }
}