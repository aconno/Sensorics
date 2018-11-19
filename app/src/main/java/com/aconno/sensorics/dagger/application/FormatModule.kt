package com.aconno.sensorics.dagger.application

import com.aconno.sensorics.SensoricsApplication
import com.aconno.sensorics.device.usecase.LocalUseCaseRepositoryImpl
import com.aconno.sensorics.domain.ConfigListManager
import com.aconno.sensorics.domain.FormatListManager
import com.aconno.sensorics.domain.format.FormatMatcher
import com.aconno.sensorics.domain.interactor.format.GetConfigsUseCase
import com.aconno.sensorics.domain.interactor.format.GetFormatsUseCase
import com.aconno.sensorics.domain.repository.LocalUseCaseRepository
import com.aconno.sensorics.viewmodel.factory.SplashViewModelFactory
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class FormatModule {

    @Provides
    @Singleton
    fun provideSplashViewModelFactory(): SplashViewModelFactory =
        SplashViewModelFactory()

    @Provides
    @Singleton
    fun provideFormatMatcher(getFormatsUseCase: GetFormatsUseCase) =
        FormatMatcher(getFormatsUseCase)

    @Provides
    @Singleton
    fun provideGetFormatsUseCase(
        formatListManager: FormatListManager
    ): GetFormatsUseCase =
        GetFormatsUseCase(formatListManager)

    @Provides
    @Singleton
    fun provideGetConfigsUseCase(
        configsListManager: ConfigListManager
    ): GetConfigsUseCase =
        GetConfigsUseCase(configsListManager)

    @Provides
    @Singleton
    fun provideUseCaseRepository(
        context: SensoricsApplication
    ): LocalUseCaseRepository =
        LocalUseCaseRepositoryImpl(context)
}
