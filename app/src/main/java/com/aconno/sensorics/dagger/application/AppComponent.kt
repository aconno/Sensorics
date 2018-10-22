package com.aconno.sensorics.dagger.application

import com.aconno.sensorics.SensoricsApplication
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        AppModule::class,
        DataModule::class,
        FormatModule::class,
        ActivityBuilder::class
    ]
)
@Singleton
interface AppComponent : AndroidInjector<SensoricsApplication> {

    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<SensoricsApplication>()
}
