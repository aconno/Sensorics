package com.aconno.sensorics.domain.interactor.ifttt.action

import com.aconno.sensorics.domain.actions.Action
import com.aconno.sensorics.domain.actions.ActionsRepository
import com.aconno.sensorics.domain.interactor.type.SingleUseCase
import io.reactivex.Single

class GetAllActionsUseCase(
    private val actionsRepository: ActionsRepository
) : SingleUseCase<List<Action>> {
    override fun execute(): Single<List<Action>> {
        return actionsRepository.getAllActions()
    }
}