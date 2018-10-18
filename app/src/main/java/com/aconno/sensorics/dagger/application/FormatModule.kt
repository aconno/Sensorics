package com.aconno.sensorics.dagger.application

import com.aconno.sensorics.data.repository.SensoricsDatabase
import com.aconno.sensorics.data.repository.format.FormatRepositoryImpl
import com.aconno.sensorics.device.format.FormatJsonConverterImpl
import com.aconno.sensorics.device.format.FormatLocatorUseCaseImpl
import com.aconno.sensorics.device.format.RemoteAdvertisementFormatRepository
import com.aconno.sensorics.device.format.RetrofitAdvertisementFormatApi
import com.aconno.sensorics.domain.format.AdvertisementFormatJsonConverter
import com.aconno.sensorics.domain.format.FormatLocatorUseCase
import com.aconno.sensorics.domain.format.FormatMatcher
import com.aconno.sensorics.domain.interactor.format.GetFormatsUseCase
import com.aconno.sensorics.domain.repository.AdvertisementFormatRepository
import com.aconno.sensorics.domain.repository.FormatRepository
import com.aconno.sensorics.viewmodel.factory.SplashViewModelFactory
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Singleton

@Module
class FormatModule {

    @Provides
    @Singleton
    fun provideSplashViewModelFactory(
        advertisementFormatRepository: AdvertisementFormatRepository
    ): SplashViewModelFactory = SplashViewModelFactory(advertisementFormatRepository)

    @Provides
    @Singleton
    fun provideFormatMatcher(getFormatsUseCase: GetFormatsUseCase) =
        FormatMatcher(getFormatsUseCase)

    @Provides
    @Singleton
    fun provideRetrofitRemoteAdvertisementRepository(): RetrofitAdvertisementFormatApi {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://playground.simvelop.de:8095")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()

        return retrofit.create(RetrofitAdvertisementFormatApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRemoteAdvertisementRepository(
        retrofitAdvertisementFormatApi: RetrofitAdvertisementFormatApi,
        formatRepository: FormatRepository
    ): AdvertisementFormatRepository =
        RemoteAdvertisementFormatRepository(retrofitAdvertisementFormatApi, formatRepository)

    @Provides
    @Singleton
    fun provideGetFormatsUseCase(
        advertisementFormatRepository: AdvertisementFormatRepository
    ): GetFormatsUseCase =
        GetFormatsUseCase(advertisementFormatRepository)

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
