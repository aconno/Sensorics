package com.aconno.sensorics.domain.interactor.ifttt.action

import com.aconno.sensorics.domain.actions.Action
import com.aconno.sensorics.domain.actions.ActionsRepository
import com.aconno.sensorics.domain.interactor.type.SingleUseCaseWithParameter
import io.reactivex.Single

class AddActionUseCase(
    private val actionsRepository: ActionsRepository
) :
        SingleUseCaseWithParameter<Long, Action> {
    override fun execute(parameter: Action): Single<Long> {
        return actionsRepository.addAction(parameter)
    }

}