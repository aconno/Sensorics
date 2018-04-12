package com.aconno.acnsensa.domain.ifttt

import com.aconno.acnsensa.domain.interactor.type.SingleUseCaseWithParameter
import io.reactivex.Single

class GetActionByIdUseCase(
    private val actionsRepository: ActionsRepository
) : SingleUseCaseWithParameter<Action, Long> {

    override fun execute(parameter: Long): Single<Action> {
        return actionsRepository.getActionById(parameter)
    }
}