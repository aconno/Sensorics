package com.aconno.sensorics.dagger.mqtt

import com.aconno.sensorics.SensoricsApplication
import com.aconno.sensorics.device.mqtt.MqttVirtualScannerImpl
import com.aconno.sensorics.domain.mqtt.MqttVirtualScanner
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class MqttScannerModule {

    @Provides
    @Singleton
    fun provideMqttVirtualScanner(
        sensoricsApplication: SensoricsApplication
    ): MqttVirtualScanner {
        return MqttVirtualScannerImpl(sensoricsApplication)
    }

}