
package com.aconno.sensorics.dagger.actionlist

import com.aconno.sensorics.domain.actions.Action
import com.aconno.sensorics.domain.actions.ActionsRepository
import com.aconno.sensorics.domain.interactor.ifttt.action.AddActionUseCase
import com.aconno.sensorics.domain.interactor.ifttt.action.DeleteActionUseCase
import com.aconno.sensorics.domain.interactor.ifttt.action.GetAllActionsUseCase
import com.aconno.sensorics.domain.interactor.publisher.ConvertJsonToActionsUseCase
import com.aconno.sensorics.domain.interactor.publisher.ConvertObjectsToJsonUseCase
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

    @Provides
    @ActionListScope
    fun provideDeleteActionUseCase(
        actionsRepository: ActionsRepository
    ) = DeleteActionUseCase(actionsRepository)

    @Provides
    @ActionListScope
    fun provideAddActionUseCase(
            actionsRepository: ActionsRepository
    ) = AddActionUseCase(actionsRepository)

    @Provides
    @ActionListScope
    fun provideConvertActionsToJsonUseCase() = ConvertObjectsToJsonUseCase<Action>()

    @Provides
    @ActionListScope
    fun provideConvertJsonToActionsUseCase() = ConvertJsonToActionsUseCase()



}