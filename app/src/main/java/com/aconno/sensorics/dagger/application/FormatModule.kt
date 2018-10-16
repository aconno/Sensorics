package com.aconno.sensorics.dagger.application

import com.aconno.sensorics.data.repository.SensoricsDatabase
import com.aconno.sensorics.data.repository.format.FormatRepositoryImpl
import com.aconno.sensorics.device.format.FormatJsonConverterImpl
import com.aconno.sensorics.device.format.FormatLocatorUseCaseImpl
import com.aconno.sensorics.domain.format.AdvertisementFormatJsonConverter
import com.aconno.sensorics.domain.format.FormatLocatorUseCase
import com.aconno.sensorics.domain.repository.FormatRepository
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class FormatModule {

    @Provides
    @Singleton
    fun provideFormatLocatorUseCase(
        formatRepository: FormatRepository
    ): FormatLocatorUseCase = FormatLocatorUseCaseImpl(formatRepository)

    @Provides
    @Singleton
    fun provideFormatRepository(
        sensoricsDatabase: SensoricsDatabase,
        advertisementFormatJsonConverter: AdvertisementFormatJsonConverter
    ): FormatRepository =
        FormatRepositoryImpl(sensoricsDatabase.formatDao(), advertisementFormatJsonConverter)

    @Provides
    @Singleton
    fun provideAdvertisementFormatJsonConverter(gson: Gson): AdvertisementFormatJsonConverter =
        FormatJsonConverterImpl(gson)
}
