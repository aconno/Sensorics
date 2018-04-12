package com.aconno.acnsensa.domain.ifttt

import com.aconno.acnsensa.domain.interactor.type.CompletableUseCaseWithParameter
import io.reactivex.Completable

class UpdateActionUseCase(
    private val actionsRepository: ActionsRepository
) : CompletableUseCaseWithParameter<Action> {

    override fun execute(parameter: Action): Completable {
        return Completable.fromAction {
            actionsRepository.updateAction(parameter)
        }
    }
}