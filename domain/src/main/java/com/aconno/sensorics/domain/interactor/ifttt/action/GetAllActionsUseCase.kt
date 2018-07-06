package com.aconno.sensorics.domain.interactor.ifttt.action

import com.aconno.sensorics.domain.ifttt.Action
import com.aconno.sensorics.domain.ifttt.ActionsRepository
import com.aconno.sensorics.domain.interactor.type.SingleUseCase
import io.reactivex.Single

/**
 * @author aconno
 */
class GetAllActionsUseCase(
    private val actionsRepository: ActionsRepository
) : SingleUseCase<List<Action>> {
    override fun execute(): Single<List<Action>> {
        return actionsRepository.getAllActions()
    }
}