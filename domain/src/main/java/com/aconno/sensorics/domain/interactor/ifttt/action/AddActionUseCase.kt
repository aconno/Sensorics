package com.aconno.sensorics.domain.interactor.ifttt.action

import com.aconno.sensorics.domain.actions.Action
import com.aconno.sensorics.domain.actions.ActionsRepository
import com.aconno.sensorics.domain.interactor.type.CompletableUseCaseWithParameter
import io.reactivex.Completable

class AddActionUseCase(
    private val actionsRepository: ActionsRepository
) :
    CompletableUseCaseWithParameter<Action> {
    override fun execute(parameter: Action): Completable {
        return actionsRepository.addAction(parameter)
    }
}