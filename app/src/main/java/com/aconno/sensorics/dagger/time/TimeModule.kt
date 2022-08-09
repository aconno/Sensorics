package com.aconno.sensorics.dagger.time

import com.aconno.sensorics.device.time.TimeProviderImpl
import com.aconno.sensorics.domain.interactor.time.GetLocalTimeOfDayInSecondsUseCase
import com.aconno.sensorics.domain.time.TimeProvider
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class TimeModule {

    @Provides
    @Singleton
    fun provideTimeProvider(): TimeProvider {
        return TimeProviderImpl()
    }

    @Provides
    @Singleton
    fun provideGetLocalTimeOfDayInSecondsUseCase(
            timeProvider: TimeProvider
    ): GetLocalTimeOfDayInSecondsUseCase {
        return GetLocalTimeOfDayInSecondsUseCase(timeProvider)
    }
}