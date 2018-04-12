package com.aconno.acnsensa.domain.ifttt

import com.aconno.acnsensa.domain.interactor.type.CompletableUseCaseWithParameter
import io.reactivex.Completable

/**
 * @author aconno
 */
class HandleInputUseCase(
    private val actionsRepository: ActionsRepository
) : CompletableUseCaseWithParameter<Input> {
    override fun execute(parameter: Input): Completable {
        return Completable.fromAction {
            actionsRepository.getAllActions()
                .subscribe { actions ->
                    actions.forEach { it.processInput(parameter) }
                }
        }
    }
}