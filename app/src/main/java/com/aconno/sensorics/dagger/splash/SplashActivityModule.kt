package com.aconno.sensorics.dagger.splash

import com.aconno.sensorics.SensoricsApplication
import com.aconno.sensorics.data.repository.ResourcesInitializerImpl
import com.aconno.sensorics.domain.ResourcesInitializer
import com.aconno.sensorics.viewmodel.factory.SplashViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class SplashActivityModule {

    @Provides
    @SplashActivityScope
    fun provideSplashViewModelFactory(
        resourcesInitializer: ResourcesInitializer
    ): SplashViewModelFactory =
        SplashViewModelFactory(resourcesInitializer)

    @Provides
    @SplashActivityScope
    fun provideResourcesInitializer(
        application: SensoricsApplication
    ): ResourcesInitializer {
        return ResourcesInitializerImpl(application.cacheDir, application.assets)
    }
}