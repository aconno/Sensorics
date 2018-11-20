package com.aconno.sensorics.dagger.application

import com.aconno.sensorics.SensoricsApplication
import com.aconno.sensorics.data.mapper.ConfigFileJsonModelConverter
import com.aconno.sensorics.data.mapper.FormatJsonConverter
import com.aconno.sensorics.data.repository.resources.ResourcesRepositoryImpl
import com.aconno.sensorics.domain.ConfigListManager
import com.aconno.sensorics.domain.FormatListManager
import com.aconno.sensorics.domain.interactor.resources.GetMainResourceUseCase
import com.aconno.sensorics.domain.repository.*
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ResourcesModule {

    @Provides
    @Singleton
    fun provideResourcesRepository(
        application: SensoricsApplication,
        gson: Gson
    ): ResourcesRepository {
        return ResourcesRepositoryImpl(
            application.cacheDir,
            gson,
            ConfigFileJsonModelConverter(),
            FormatJsonConverter()
        )
    }

    @Provides
    @Singleton
    fun provideConfigRepository(resourcesRepository: ResourcesRepository): ConfigRepository =
        resourcesRepository

    @Provides
    @Singleton
    fun provideFormatRepository(resourcesRepository: ResourcesRepository): FormatRepository =
        resourcesRepository

    @Provides
    @Singleton
    fun provideUseCaseRepository(resourcesRepository: ResourcesRepository): UseCaseRepository =
        resourcesRepository

    @Provides
    @Singleton
    fun provideMainScreenRepository(resourcesRepository: ResourcesRepository): MainScreenRepository =
        resourcesRepository

    @Provides
    @Singleton
    fun provideFormatListManager(
        formatRepository: FormatRepository
    ): FormatListManager {
        return FormatListManager(formatRepository)
    }

    @Provides
    @Singleton
    fun provideConfigListManager(
        configRepository: ConfigRepository
    ): ConfigListManager {
        return ConfigListManager(configRepository)
    }

    @Provides
    @Singleton
    fun provideGetMainResourceUseCase(
        configListManager: ConfigListManager,
        application: SensoricsApplication
    ): GetMainResourceUseCase {
        return GetMainResourceUseCase(
            configListManager,
            application.cacheDir.absolutePath
        )
    }
}