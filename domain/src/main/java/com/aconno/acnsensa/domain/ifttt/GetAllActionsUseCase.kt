package com.aconno.acnsensa.domain.ifttt

import com.aconno.acnsensa.domain.interactor.type.SingleUseCase
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