package com.aconno.sensorics.domain.interactor.ifttt.action

import com.aconno.sensorics.domain.actions.Action
import com.aconno.sensorics.domain.ifttt.ActionsRepository
import com.aconno.sensorics.domain.interactor.type.CompletableUseCaseWithParameter
import io.reactivex.Completable

/**
 * @author aconno
 */
class DeleteActionUseCase(
    private val actionsRepository: ActionsRepository
) : CompletableUseCaseWithParameter<Action> {
    override fun execute(parameter: Action): Completable {
        return Completable.fromAction {
            actionsRepository.deleteAction(parameter)
        }
    }
}