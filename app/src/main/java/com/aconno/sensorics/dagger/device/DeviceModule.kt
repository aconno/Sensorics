package com.aconno.sensorics.dagger.device

import com.aconno.sensorics.dagger.mainactivity.MainActivityScope
import com.aconno.sensorics.domain.interactor.repository.DeleteDeviceUseCase
import com.aconno.sensorics.domain.interactor.repository.GetSavedDevicesUseCase
import com.aconno.sensorics.domain.interactor.repository.SaveDeviceUseCase
import com.aconno.sensorics.domain.repository.DeviceRepository
import dagger.Module
import dagger.Provides

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
    @MainActivityScope
    fun provideSaveDeviceUseCase(
            deviceRepository: DeviceRepository
    ): SaveDeviceUseCase {
        return SaveDeviceUseCase(deviceRepository)
    }

    @Provides
    @MainActivityScope
    fun provideDeleteDeviceUseCase(
            deviceRepository: DeviceRepository
    ): DeleteDeviceUseCase {
        return DeleteDeviceUseCase(deviceRepository)
    }


}