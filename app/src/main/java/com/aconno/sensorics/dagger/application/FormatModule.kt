package com.aconno.sensorics.dagger.application

import com.aconno.sensorics.AdvertisementFormatReaderImpl
import com.aconno.sensorics.SensoricsApplication
import com.aconno.sensorics.data.repository.SensoricsDatabase
import com.aconno.sensorics.data.repository.format.LocalFormatRepositoryImpl
import com.aconno.sensorics.device.format.AdvertisementFormatReader
import com.aconno.sensorics.device.format.FormatJsonConverterImpl
import com.aconno.sensorics.device.format.RemoteFormatRepositoryImpl
import com.aconno.sensorics.device.format.RetrofitAdvertisementFormatApi
import com.aconno.sensorics.domain.format.AdvertisementFormatJsonConverter
import com.aconno.sensorics.domain.format.FormatMatcher
import com.aconno.sensorics.domain.interactor.format.GetFormatsUseCase
import com.aconno.sensorics.domain.repository.LocalFormatRepository
import com.aconno.sensorics.domain.repository.RemoteFormatRepository
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
        remoteFormatRepository: RemoteFormatRepository
    ): SplashViewModelFactory = SplashViewModelFactory(remoteFormatRepository)

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
        localFormatRepository: LocalFormatRepository,
        advertisementFormatReader: AdvertisementFormatReader,
        advertisementFormatJsonConverter: AdvertisementFormatJsonConverter
    ): RemoteFormatRepository =
        RemoteFormatRepositoryImpl(
            retrofitAdvertisementFormatApi,
            localFormatRepository, advertisementFormatReader, advertisementFormatJsonConverter
        )

    @Provides
    @Singleton
    fun provideAdvertisementFormatReader(
        sensoricsApplication: SensoricsApplication
    ): AdvertisementFormatReader = AdvertisementFormatReaderImpl(sensoricsApplication)

    @Provides
    @Singleton
    fun provideGetFormatsUseCase(
        remoteFormatRepository: RemoteFormatRepository
    ): GetFormatsUseCase =
        GetFormatsUseCase(remoteFormatRepository)

    @Provides
    @Singleton
    fun provideFormatRepository(
        sensoricsDatabase: SensoricsDatabase,
        advertisementFormatJsonConverter: AdvertisementFormatJsonConverter
    ): LocalFormatRepository =
        LocalFormatRepositoryImpl(sensoricsDatabase.formatDao(), advertisementFormatJsonConverter)

    @Provides
    @Singleton
    fun provideAdvertisementFormatJsonConverter(gson: Gson): AdvertisementFormatJsonConverter =
        FormatJsonConverterImpl(gson)
}
