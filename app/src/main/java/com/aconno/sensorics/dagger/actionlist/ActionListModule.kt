package com.aconno.sensorics.dagger.actionlist

import com.aconno.sensorics.domain.actions.ActionsRepository
import com.aconno.sensorics.domain.interactor.ifttt.action.GetAllActionsUseCase
import com.aconno.sensorics.domain.interactor.repository.GetSavedDevicesUseCase
import com.aconno.sensorics.domain.repository.DeviceRepository
import dagger.Module
import dagger.Provides

@Module
class ActionListModule {

    @Provides
    @ActionListScope
    fun provideGetAllActionsUseCase(
        actionsRepository: ActionsRepository
    ): GetAllActionsUseCase {
        return GetAllActionsUseCase(
            actionsRepository
        )
    }

    @Provides
    @ActionListScope
    fun provideGetAllDevicesUseCase(
        deviceRepository: DeviceRepository
    ): GetSavedDevicesUseCase {
        return GetSavedDevicesUseCase(deviceRepository)
    }
}