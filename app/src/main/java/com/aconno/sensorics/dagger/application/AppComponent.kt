package com.aconno.sensorics.dagger.application

import com.aconno.sensorics.SensoricsApplication
import com.aconno.sensorics.dagger.mqtt.MqttScannerModule
import com.aconno.sensorics.dagger.worker.WorkerAssistedInjectModule
import com.aconno.sensorics.dagger.worker.WorkerBindingModule
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        AppModule::class,
        BluetoothModule::class,
        MqttScannerModule::class,
        DataModule::class,
        ResourcesModule::class,
        ActivityBuilder::class,
        WorkerAssistedInjectModule::class,
        WorkerBindingModule::class
    ]
)
@Singleton
interface AppComponent : AndroidInjector<SensoricsApplication> {

    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<SensoricsApplication>()
}
