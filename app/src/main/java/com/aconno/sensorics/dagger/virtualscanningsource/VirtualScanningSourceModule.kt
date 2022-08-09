package com.aconno.sensorics.dagger.virtualscanningsource

import com.aconno.sensorics.data.mapper.MqttVirtualScanningSourceDataMapper
import com.aconno.sensorics.data.repository.SensoricsDatabase
import com.aconno.sensorics.data.repository.mqttvirtualscanningsource.MqttVirtualScanningSourceRepositoryImpl
import com.aconno.sensorics.domain.interactor.virtualscanningsource.UpdateVirtualScanningSourceUseCase
import com.aconno.sensorics.domain.interactor.virtualscanningsource.mqtt.*
import com.aconno.sensorics.domain.virtualscanningsources.mqtt.MqttVirtualScanningSourceRepository
import dagger.Module
import dagger.Provides

@Module
class VirtualScanningSourceModule {


    @Provides
    @VirtualScanningSourceScope
    fun provideGetAllEnabledMqttVirtualScanningSourceUseCase(
            mqttVirtualScanningSourceRepository: MqttVirtualScanningSourceRepository
    ): GetAllEnabledMqttVirtualScanningSourceUseCase {
        return GetAllEnabledMqttVirtualScanningSourceUseCase(
                mqttVirtualScanningSourceRepository
        )
    }

    @Provides
    @VirtualScanningSourceScope
    fun provideAddMqttVirtualScanningSourceUseCase(
            mqttVirtualScanningSourceRepository: MqttVirtualScanningSourceRepository
    ): AddMqttVirtualScanningSourceUseCase {
        return AddMqttVirtualScanningSourceUseCase(mqttVirtualScanningSourceRepository)
    }

    @Provides
    @VirtualScanningSourceScope
    fun provideGetMqttVirtualScanningSourceByIdUseCase(
            mqttVirtualScanningSourceRepository: MqttVirtualScanningSourceRepository
    ): GetMqttVirtualScanningSourceByIdUseCase {
        return GetMqttVirtualScanningSourceByIdUseCase(
                mqttVirtualScanningSourceRepository
        )
    }

    @Provides
    @VirtualScanningSourceScope
    fun provideGetAllMqttVirtualScanningSourceUseCase(
            mqttVirtualScanningSourceRepository: MqttVirtualScanningSourceRepository
    ): GetAllMqttVirtualScanningSourcesUseCase {
        return GetAllMqttVirtualScanningSourcesUseCase(mqttVirtualScanningSourceRepository)
    }

    @Provides
    @VirtualScanningSourceScope
    fun provideDeleteMqttVirtualScanningUseCase(
            mqttVirtualScanningSourceRepository: MqttVirtualScanningSourceRepository
    ): DeleteMqttVirtualScanningSourceUseCase {
        return DeleteMqttVirtualScanningSourceUseCase(mqttVirtualScanningSourceRepository)
    }

    @Provides
    @VirtualScanningSourceScope
    fun provideUpdateVirtualScanningSourceUseCase(
            mqttVirtualScanningSourceRepository: MqttVirtualScanningSourceRepository
    ): UpdateVirtualScanningSourceUseCase =
            UpdateVirtualScanningSourceUseCase(mqttVirtualScanningSourceRepository)

    @Provides
    @VirtualScanningSourceScope
    fun provideMqttVirtualScanningSourceRepository(
            sensoricsDatabase: SensoricsDatabase,
            mqttVirtualScanningSourceDataMapper: MqttVirtualScanningSourceDataMapper
    ): MqttVirtualScanningSourceRepository {
        return MqttVirtualScanningSourceRepositoryImpl(
                sensoricsDatabase.mqttVirtualScanningSourceDao(),
                mqttVirtualScanningSourceDataMapper
        )
    }
}