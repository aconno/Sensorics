package com.aconno.sensorics.dagger.devicegroup

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
    fun provideUpdateDeviceGroupUseCase(
        deviceGroupRepository: DeviceGroupRepository
    ): UpdateDeviceGroupUseCase {
        return UpdateDeviceGroupUseCase(deviceGroupRepository)
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
    fun provideGetDevicesBelongingSomeDeviceGroupUseCase(
        deviceGroupDeviceJoinRepository: DeviceGroupDeviceJoinRepository
    ): GetDevicesBelongingSomeDeviceGroupUseCase {
        return GetDevicesBelongingSomeDeviceGroupUseCase(deviceGroupDeviceJoinRepository)
    }

    @Provides
    @DeviceGroupScope
    fun provideDeleteDeviceGroupDeviceJoinUseCase(
        deviceGroupDeviceJoinRepository: DeviceGroupDeviceJoinRepository
    ): DeleteDeviceGroupDeviceJoinUseCase {
        return DeleteDeviceGroupDeviceJoinUseCase(deviceGroupDeviceJoinRepository)
    }

}