package com.aconno.sensorics.dagger.publisher

import com.aconno.sensorics.data.mapper.*
import com.aconno.sensorics.data.repository.SensoricsDatabase
import com.aconno.sensorics.data.repository.azuremqttpublish.AzureMqttPublishRepositoryImpl
import com.aconno.sensorics.data.repository.googlepublish.GooglePublishRepositoryImpl
import com.aconno.sensorics.data.repository.mqttpublish.MqttPublishRepositoryImpl
import com.aconno.sensorics.data.repository.restpublish.RestPublishRepositoryImpl
import com.aconno.sensorics.domain.FormatListManager
import com.aconno.sensorics.domain.ifttt.BasePublish
import com.aconno.sensorics.domain.ifttt.publish.*
import com.aconno.sensorics.domain.interactor.ifttt.UpdateAnyPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.azuremqttpublish.GetAzureMqttPublishByIdUseCase
import com.aconno.sensorics.domain.interactor.ifttt.googlepublish.GetGooglePublishByIdUseCase
import com.aconno.sensorics.domain.interactor.ifttt.mqttpublish.GetMqttPublishByIdUseCase
import com.aconno.sensorics.domain.interactor.ifttt.publish.*
import com.aconno.sensorics.domain.interactor.ifttt.restpublish.GetRestPublishByIdUseCase
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
    fun provideAzureMqttPublishRepository(
        sensoricsDatabase: SensoricsDatabase,
        azureMqttPublishDataMapper: AzureMqttPublishDataMapper
    ): AzureMqttPublishRepository {
        return AzureMqttPublishRepositoryImpl(
            sensoricsDatabase.azureMqttPublishDao(),
            azureMqttPublishDataMapper
        )
    }

    @Provides
    @PublisherScope
    fun provideGooglePublishRepository(
        sensoricsDatabase: SensoricsDatabase,
        googlePublishDataMapper: GooglePublishDataMapper
    ): GooglePublishRepository {
        return GooglePublishRepositoryImpl(
            sensoricsDatabase.googlePublishDao(),
            googlePublishDataMapper
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
    fun provideRESTPublishRepository(
        sensoricsDatabase: SensoricsDatabase,
        restPublishDataMapper: RestPublishDataMapper,
        restHeaderDataMapper: RestHeaderDataMapper,
        restHttpGetParamDataMapper: RestHttpGetParamDataMapper
    ): RestPublishRepository {
        return RestPublishRepositoryImpl(
            sensoricsDatabase.restPublishDao(),
            restPublishDataMapper,
            restHeaderDataMapper,
            restHttpGetParamDataMapper
        )
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
    fun provideAddAnyPublishUseCase(
        getRepositoryForPublishUseCase: GetRepositoryForPublishUseCase
    ): AddAnyPublishUseCase {
        return AddAnyPublishUseCase(getRepositoryForPublishUseCase)
    }

    @Provides
    @PublisherScope
    fun provideUpdateAnyPublishUseCase(
        getRepositoryForPublishUseCase: GetRepositoryForPublishUseCase
    ): UpdateAnyPublishUseCase {
        return UpdateAnyPublishUseCase(getRepositoryForPublishUseCase)
    }

    @Provides
    @PublisherScope
    fun provideDeleteAnyPublishUseCase(
        getRepositoryForPublishUseCase: GetRepositoryForPublishUseCase
    ): DeleteAnyPublishUseCase {
        return DeleteAnyPublishUseCase(getRepositoryForPublishUseCase)
    }

    @Provides
    @PublisherScope
    fun provideGetAllPublishersUseCase(
        repositories: List<@JvmSuppressWildcards PublishRepository<out BasePublish>>
    ): GetAllPublishersUseCase {
        return GetAllPublishersUseCase(repositories)
    }

    @Provides
    @PublisherScope
    fun provideGetAllEnabledPublishersUseCase(
        repositories: List<@JvmSuppressWildcards PublishRepository<out BasePublish>>
    ): GetAllEnabledPublishersUseCase {
        return GetAllEnabledPublishersUseCase(repositories)
    }

    @Provides
    @PublisherScope
    fun provideAllPublishRepositories(
        azureMqttPublishRepository: AzureMqttPublishRepository,
        googlePublishRepository: GooglePublishRepository,
        mqttPublishRepository: MqttPublishRepository,
        restPublishRepository: RestPublishRepository
    ): List<PublishRepository<out BasePublish>> {
        return listOf(
            azureMqttPublishRepository,
            googlePublishRepository,
            mqttPublishRepository,
            restPublishRepository
        )
    }

    @Provides
    @PublisherScope
    fun provideGetRepositoryForPublishUseCase(
        azureMqttPublishRepository: AzureMqttPublishRepository,
        googlePublishRepository: GooglePublishRepository,
        mqttPublishRepository: MqttPublishRepository,
        restPublishRepository: RestPublishRepository
    ): GetRepositoryForPublishUseCase {
        return GetRepositoryForPublishUseCase(
            azureMqttPublishRepository,
            googlePublishRepository,
            mqttPublishRepository,
            restPublishRepository
        )
    }

    @Provides
    @PublisherScope
    fun provideGetAllDeviceParameterPlaceholderStringsUseCase(
        formatListManager: FormatListManager
    ): GetAllDeviceParameterPlaceholderStringsUseCase {
        return GetAllDeviceParameterPlaceholderStringsUseCase(formatListManager)
    }
}