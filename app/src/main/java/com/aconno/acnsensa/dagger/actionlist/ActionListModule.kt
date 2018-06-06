package com.aconno.acnsensa.dagger.actionlist

import com.aconno.acnsensa.domain.ifttt.ActionsRepository
import com.aconno.acnsensa.domain.interactor.ifttt.GetAllActionsUseCase
import com.aconno.acnsensa.domain.interactor.repository.GetSavedDevicesUseCase
import com.aconno.acnsensa.domain.repository.DeviceRepository
import dagger.Module
import dagger.Provides

@Module
class ActionListModule {

    @Provides
    @ActionListScope
    fun provideGetAllActionsUseCase(
        actionsRepository: ActionsRepository
    ): GetAllActionsUseCase {
        return GetAllActionsUseCase(actionsRepository)
    }

    @Provides
    @ActionListScope
    fun provideGetAllDevicesUseCase(
        deviceRepository: DeviceRepository
    ): GetSavedDevicesUseCase {
        return GetSavedDevicesUseCase(deviceRepository)
    }
}