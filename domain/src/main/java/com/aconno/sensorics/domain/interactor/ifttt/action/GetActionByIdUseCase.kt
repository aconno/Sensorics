package com.aconno.sensorics.domain.interactor.ifttt.action

import com.aconno.sensorics.domain.ifttt.Action
import com.aconno.sensorics.domain.ifttt.ActionsRepository
import com.aconno.sensorics.domain.interactor.type.SingleUseCaseWithParameter
import io.reactivex.Single

class GetActionByIdUseCase(
    private val actionsRepository: ActionsRepository
) : SingleUseCaseWithParameter<Action, Long> {

    override fun execute(parameter: Long): Single<Action> {
        return actionsRepository.getActionById(parameter)
    }
}