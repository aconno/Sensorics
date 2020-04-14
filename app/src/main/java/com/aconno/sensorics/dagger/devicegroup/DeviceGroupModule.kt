package com.aconno.sensorics.dagger.devicegroup

import com.aconno.sensorics.data.repository.SensoricsDatabase
import com.aconno.sensorics.data.repository.devicegroups.DeviceGroupMapper
import com.aconno.sensorics.data.repository.devicegroups.DeviceGroupRepositoryImpl
import com.aconno.sensorics.domain.interactor.repository.DeleteDeviceGroupUseCase
import com.aconno.sensorics.domain.interactor.repository.GetSavedDeviceGroupsUseCase
import com.aconno.sensorics.domain.interactor.repository.SaveDeviceGroupUseCase
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
    fun provideDeviceGroupRepository(
        sensoricsDatabase: SensoricsDatabase,
        deviceGroupMapper: DeviceGroupMapper
    ): DeviceGroupRepository {
        return DeviceGroupRepositoryImpl(sensoricsDatabase.deviceGroupDao(), deviceGroupMapper)
    }
}