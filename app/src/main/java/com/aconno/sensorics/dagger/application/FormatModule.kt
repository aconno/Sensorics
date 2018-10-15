package com.aconno.sensorics.dagger.application

import com.aconno.sensorics.device.format.FormatLocatorUseCaseImpl
import com.aconno.sensorics.domain.format.FormatLocatorUseCase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class FormatModule {

    @Provides
    @Singleton
    fun provideFormatLocatorUseCase(): FormatLocatorUseCase = FormatLocatorUseCaseImpl()
}
