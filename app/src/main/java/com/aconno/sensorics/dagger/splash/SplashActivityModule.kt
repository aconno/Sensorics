package com.aconno.sensorics.dagger.splash

import androidx.lifecycle.ViewModelProvider
import com.aconno.sensorics.SensoricsApplication
import com.aconno.sensorics.data.repository.ResourcesInitializerImpl
import com.aconno.sensorics.domain.ResourcesInitializer
import com.aconno.sensorics.ui.SplashActivity
import com.aconno.sensorics.viewmodel.SplashViewModel
import com.aconno.sensorics.viewmodel.factory.SplashViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class SplashActivityModule {

    @Provides
    @SplashActivityScope
    fun provideSplashViewModelFactory(
        resourcesInitializer: ResourcesInitializer
    ): SplashViewModelFactory = SplashViewModelFactory(resourcesInitializer)

    @Provides
    @SplashActivityScope
    fun provideSplashViewModel(
        activity: SplashActivity,
        splashViewModelFactory: SplashViewModelFactory
    ): SplashViewModel = ViewModelProvider(
        activity,
        splashViewModelFactory
    ).get(SplashViewModel::class.java)

    @Provides
    @SplashActivityScope
    fun provideResourcesInitializer(
        application: SensoricsApplication
    ): ResourcesInitializer {
        return ResourcesInitializerImpl(application.cacheDir, application.assets)
    }
}