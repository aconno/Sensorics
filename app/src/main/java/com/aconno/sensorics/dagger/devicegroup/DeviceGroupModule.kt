package com.aconno.sensorics.dagger.devicegroup

import com.aconno.sensorics.data.repository.SensoricsDatabase
import com.aconno.sensorics.data.repository.devicegroupdevicejoin.DeviceGroupDeviceJoinMapper
import com.aconno.sensorics.data.repository.devicegroupdevicejoin.DeviceGroupDeviceJoinRepositoryImpl
import com.aconno.sensorics.data.repository.devicegroups.DeviceGroupMapper
import com.aconno.sensorics.data.repository.devicegroups.DeviceGroupRepositoryImpl
import com.aconno.sensorics.data.repository.devices.DeviceMapper
import com.aconno.sensorics.domain.interactor.repository.*
import com.aconno.sensorics.domain.repository.DeviceGroupDeviceJoinRepository
import com.aconno.sensorics.domain.repository.DeviceGroupRepository
import dagger.Module
import dagger.Provides

@Module
class DeviceGroupModule {

    @Provides
    @DeviceGroupScope
    fun provideSaveDeviceGroupUseCase(
        deviceGroupRepository: DeviceGroupRepository
    ): SaveDeviceGroupUseCase {
        return SaveDeviceGroupUseCase(deviceGroupRepository)
    }

    @Provides
    @DeviceGroupScope
    fun provideGetSavedDeviceGroupsUseCase(
        deviceGroupRepository: DeviceGroupRepository
    ): GetSavedDeviceGroupsUseCase {
        return GetSavedDeviceGroupsUseCase(deviceGroupRepository)
    }

    @Provides
    @DeviceGroupScope
    fun provideDeleteDeviceGroupUseCase(
        deviceGroupRepository: DeviceGroupRepository
    ): DeleteDeviceGroupUseCase {
        return DeleteDeviceGroupUseCase(deviceGroupRepository)
    }

    @Provides
    @DeviceGroupScope
    fun provideSaveDeviceGroupDeviceJoinUseCase(
        deviceGroupDeviceJoinRepository: DeviceGroupDeviceJoinRepository
    ): SaveDeviceGroupDeviceJoinUseCase {
        return SaveDeviceGroupDeviceJoinUseCase(deviceGroupDeviceJoinRepository)
    }

    @Provides
    @DeviceGroupScope
    fun provideGetDevicesFromDeviceGroupUseCase(
        deviceGroupDeviceJoinRepository: DeviceGroupDeviceJoinRepository
    ): GetDevicesInDeviceGroupUseCase {
        return GetDevicesInDeviceGroupUseCase(deviceGroupDeviceJoinRepository)
    }

    @Provides
    @DeviceGroupScope
    fun provideDeleteDeviceGroupDeviceJoinUseCase(
        deviceGroupDeviceJoinRepository: DeviceGroupDeviceJoinRepository
    ): DeleteDeviceGroupDeviceJoinUseCase {
        return DeleteDeviceGroupDeviceJoinUseCase(deviceGroupDeviceJoinRepository)
    }

    @Provides
    @DeviceGroupScope
    fun provideDeviceGroupRepository(
        sensoricsDatabase: SensoricsDatabase,
        deviceGroupMapper: DeviceGroupMapper
    ): DeviceGroupRepository {
        return DeviceGroupRepositoryImpl(sensoricsDatabase.deviceGroupDao(), deviceGroupMapper)
    }

    @Provides
    @DeviceGroupScope
    fun provideDeviceGroupDeviceJoinRepository(
        sensoricsDatabase: SensoricsDatabase,
        deviceMapper: DeviceMapper,
        deviceGroupDeviceJoinMapper: DeviceGroupDeviceJoinMapper
    ): DeviceGroupDeviceJoinRepository {
        return DeviceGroupDeviceJoinRepositoryImpl(sensoricsDatabase.deviceGroupDeviceJoinDao(), deviceMapper,deviceGroupDeviceJoinMapper)
    }
}