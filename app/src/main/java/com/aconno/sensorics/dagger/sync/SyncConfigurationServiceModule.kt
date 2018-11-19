package com.aconno.sensorics.dagger.sync

import com.aconno.sensorics.SensoricsApplication
import com.aconno.sensorics.data.api.ResourcesApi
import com.aconno.sensorics.data.repository.resources.ResourcesRepositoryImpl
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
class SyncConfigurationServiceModule {

    @Provides
    @SyncConfigurationServiceScope
    fun providesResourcesApi(
        gson: Gson
    ): ResourcesApi {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val okHttpClient = OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        return retrofit.create(ResourcesApi::class.java)
    }

    @Provides
    @Singleton
    fun provideResourcesRepositoryImpl(
        application: SensoricsApplication,
        resourcesApi: ResourcesApi
    ): ResourcesRepositoryImpl {
        return ResourcesRepositoryImpl(application.cacheDir, resourcesApi)
    }
}