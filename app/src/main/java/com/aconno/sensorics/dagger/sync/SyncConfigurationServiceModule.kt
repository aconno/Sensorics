package com.aconno.sensorics.dagger.sync

import android.content.SharedPreferences
import com.aconno.sensorics.SensoricsApplication
import com.aconno.sensorics.data.api.ResourcesApi
import com.aconno.sensorics.data.repository.resources.ResourceSyncerImpl
import com.aconno.sensorics.domain.ResourceSyncer
import com.aconno.sensorics.domain.interactor.sync.SyncUseCase
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor


@Module
class SyncConfigurationServiceModule {

    @Provides
    @SyncConfigurationServiceScope
    fun provideResourcesApi(
        gson: Gson
    ): ResourcesApi {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val okHttpClient = OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()
        return ResourcesApi(gson, okHttpClient)
    }

    @Provides
    @SyncConfigurationServiceScope
    fun provideResourceSyncer(
        application: SensoricsApplication,
        resourcesApi: ResourcesApi,
        sharedPreferences: SharedPreferences
    ): ResourceSyncer {
        return ResourceSyncerImpl(application.cacheDir, resourcesApi, sharedPreferences)
    }

    @Provides
    @SyncConfigurationServiceScope
    fun provideSyncUseCase(
        resourceSyncer: ResourceSyncer
    ) = SyncUseCase(resourceSyncer)
}