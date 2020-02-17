package com.aconno.sensorics.dagger.action

import com.aconno.sensorics.data.repository.SensoricsDatabase
import com.aconno.sensorics.data.repository.action.ActionsRepositoryImpl
import com.aconno.sensorics.domain.actions.Action
import com.aconno.sensorics.domain.actions.ActionsRepository
import com.aconno.sensorics.domain.interactor.ifttt.action.*
import com.aconno.sensorics.domain.interactor.publisher.ConvertJsonToActionsUseCase
import com.aconno.sensorics.domain.interactor.publisher.ConvertObjectsToJsonUseCase
import dagger.Module
import dagger.Provides

@Module
class ActionModule {

    @Provides
    @ActionScope
    fun provideGetActionByIdUseCase(
            actionsRepository: ActionsRepository
    ) = GetActionByIdUseCase(actionsRepository)

    @Provides
    @ActionScope
    fun provideAddActionUseCase(
            actionsRepository: ActionsRepository
    ) = AddActionUseCase(actionsRepository)

    @Provides
    @ActionScope
    fun provideGetAllActionsUseCase(
            actionsRepository: ActionsRepository
    ): GetAllActionsUseCase {
        return GetAllActionsUseCase(
                actionsRepository
        )
    }

    @Provides
    @ActionScope
    fun provideConvertActionsToJsonUseCase() : ConvertObjectsToJsonUseCase<Action> {
        return ConvertObjectsToJsonUseCase()
    }

    @Provides
    @ActionScope
    fun provideJsonToActionsUseCase() : ConvertJsonToActionsUseCase {
        return ConvertJsonToActionsUseCase()
    }

    @Provides
    @ActionScope
    fun provideDeleteActionUseCase(
            actionsRepository: ActionsRepository
    ) = DeleteActionUseCase(actionsRepository)

    @Provides
    @ActionScope
    fun provideGetActionsByDeviceMacAddressUseCase(
            actionsRepository: ActionsRepository
    ) = GetActionsByDeviceMacAddressUseCase(actionsRepository)

    @Provides
    @ActionScope
    fun provideSetActionActiveByDeviceMacAddressUseCase(
            addActionUseCase: AddActionUseCase,
            getActionsByDeviceMacAddressUseCase: GetActionsByDeviceMacAddressUseCase
    ) = SetActionActiveByDeviceMacAddressUseCase(
            addActionUseCase, getActionsByDeviceMacAddressUseCase
    )

    @Provides
    @ActionScope
    fun provideActionsRepository(
        sensoricsDatabase: SensoricsDatabase
    ): ActionsRepository {
        return ActionsRepositoryImpl(sensoricsDatabase.actionDao())
    }
}