package com.aconno.sensorics.domain.interactor.ifttt.action

import com.aconno.sensorics.domain.actions.Action
import com.aconno.sensorics.domain.actions.ActionsRepository
import com.aconno.sensorics.domain.interactor.type.SingleUseCaseWithParameter
import io.reactivex.Single

class GetActionsByDeviceMacAddressUseCase(
    private val actionsRepository: ActionsRepository
) : SingleUseCaseWithParameter<List<Action>, String> {

    override fun execute(parameter: String): Single<List<Action>> {
        return actionsRepository.getActionsByDeviceMacAddress(parameter)
    }
}