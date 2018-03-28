package com.aconno.acnsensa.domain.ifttt

import com.aconno.acnsensa.domain.interactor.type.CompletableUseCaseWithParameter
import io.reactivex.Completable

/**
 * @author aconno
 */
class DeleteActionUseCase(
    private val actionsRepository: ActionsRepository
) : CompletableUseCaseWithParameter<Action> {
    override fun execute(parameter: Action): Completable {
        actionsRepository.deleteAction(parameter)
        return Completable.complete()
    }
}