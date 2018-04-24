package com.aconno.acnsensa.domain.interactor.ifttt

import com.aconno.acnsensa.domain.ifttt.Action
import com.aconno.acnsensa.domain.ifttt.ActionsRepository
import com.aconno.acnsensa.domain.interactor.type.CompletableUseCaseWithParameter
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