package com.aconno.sensorics.dagger.device

import com.aconno.sensorics.data.mapper.PublishDeviceJoinMapper
import com.aconno.sensorics.data.repository.SensoricsDatabase
import com.aconno.sensorics.data.repository.devices.DeviceMapper
import com.aconno.sensorics.data.repository.devices.DeviceRepositoryImpl
import com.aconno.sensorics.data.repository.publishdevicejoin.PublishDeviceJoinRepositoryImpl
import com.aconno.sensorics.domain.ifttt.PublishDeviceJoinRepository
import com.aconno.sensorics.domain.interactor.repository.*
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.domain.repository.DeviceRepository
import dagger.Module
import dagger.Provides
import io.reactivex.Flowable

@Module
class DeviceModule {

    @Provides
    @DeviceScope
    fun provideGetAllDevicesUseCase(
            deviceRepository: DeviceRepository
    ): GetSavedDevicesUseCase {
        return GetSavedDevicesUseCase(deviceRepository)
    }

    @Provides
    @DeviceScope
    fun provideSaveDeviceUseCase(
            deviceRepository: DeviceRepository
    ): SaveDeviceUseCase {
        return SaveDeviceUseCase(deviceRepository)
    }

    @Provides
    @DeviceScope
    fun provideDeleteDeviceUseCase(
            deviceRepository: DeviceRepository
    ): DeleteDeviceUseCase {
        return DeleteDeviceUseCase(deviceRepository)
    }

    @Provides
    @DeviceScope
    fun provideDeviceRepository(
            sensoricsDatabase: SensoricsDatabase,
            deviceMapper: DeviceMapper
    ): DeviceRepository {
        return DeviceRepositoryImpl(sensoricsDatabase.deviceDao(), deviceMapper)
    }

    @Provides
    @DeviceScope
    fun provideGetSavedDevicesList(
            deviceRepository: DeviceRepository
    ): Flowable<List<Device>> {
        return GetSavedDevicesUseCase(deviceRepository).execute()
    }

    @Provides
    @DeviceScope
    fun provideGetSavedDevicesMaybeUseCase(
            deviceRepository: DeviceRepository
    ): GetSavedDevicesMaybeUseCase {
        return GetSavedDevicesMaybeUseCase(deviceRepository)
    }

    @Provides
    @DeviceScope
    fun providePublishDeviceJoinRepository(
            sensoricsDatabase: SensoricsDatabase,
            deviceMapper: DeviceMapper,
            publishDeviceJoinMapper: PublishDeviceJoinMapper
    ): PublishDeviceJoinRepository {
        return PublishDeviceJoinRepositoryImpl(
                sensoricsDatabase.publishDeviceJoinDao(),
                deviceMapper,
                publishDeviceJoinMapper
        )
    }


    @Provides
    @DeviceScope
    fun provideGetDevicesThatConnectedWithGooglePublishUseCase(
            publishDeviceJoinRepository: PublishDeviceJoinRepository
    ): GetDevicesThatConnectedWithGooglePublishUseCase {
        return GetDevicesThatConnectedWithGooglePublishUseCase(publishDeviceJoinRepository)
    }

    @Provides
    @DeviceScope
    fun provideGetDevicesThatConnectedWithRESTPublishUseCase(
            publishDeviceJoinRepository: PublishDeviceJoinRepository
    ): GetDevicesThatConnectedWithRestPublishUseCase {
        return GetDevicesThatConnectedWithRestPublishUseCase(publishDeviceJoinRepository)
    }

    @Provides
    @DeviceScope
    fun provideGetDevicesThatConnectedWithMqttPublishUseCase(
            publishDeviceJoinRepository: PublishDeviceJoinRepository
    ): GetDevicesThatConnectedWithMqttPublishUseCase {
        return GetDevicesThatConnectedWithMqttPublishUseCase(publishDeviceJoinRepository)
    }

    @Provides
    @DeviceScope
    fun provideGetDevicesThatConnectedWithAzureMqttPublishUseCase(
            publishDeviceJoinRepository: PublishDeviceJoinRepository
    ): GetDevicesThatConnectedWithAzureMqttPublishUseCase {
        return GetDevicesThatConnectedWithAzureMqttPublishUseCase(publishDeviceJoinRepository)
    }

    @Provides
    @DeviceScope
    fun provideSavePublishDeviceJoinUseCase(
            publishDeviceJoinRepository: PublishDeviceJoinRepository
    ): SavePublishDeviceJoinUseCase {
        return SavePublishDeviceJoinUseCase(publishDeviceJoinRepository)
    }

    @Provides
    @DeviceScope
    fun provideDeletePublishDeviceJoinUseCase(
            publishDeviceJoinRepository: PublishDeviceJoinRepository
    ): DeletePublishDeviceJoinUseCase {
        return DeletePublishDeviceJoinUseCase(publishDeviceJoinRepository)
    }


}