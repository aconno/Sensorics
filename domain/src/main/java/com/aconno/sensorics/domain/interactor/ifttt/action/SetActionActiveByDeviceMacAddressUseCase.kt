package com.aconno.sensorics.domain.interactor.ifttt.action

import com.aconno.sensorics.domain.actions.GeneralAction
import com.aconno.sensorics.domain.interactor.type.CompletableUseCaseWithTwoParameters
import io.reactivex.Completable
import io.reactivex.Observable

class SetActionActiveByDeviceMacAddressUseCase(
    private val addActionUseCase: AddActionUseCase,
    private val getActionsByDeviceMacAddressUseCase: GetActionsByDeviceMacAddressUseCase
) : CompletableUseCaseWithTwoParameters<String, Boolean> {

    override fun execute(parameter1: String, parameter2: Boolean): Completable {
        return getActionsByDeviceMacAddressUseCase.execute(parameter1)
            .flatMapObservable { Observable.fromIterable(it) }
            .flatMapCompletable {
                val action = GeneralAction(
                    id = it.id,
                    name = it.name,
                    device = it.device,
                    condition = it.condition,
                    outcome = it.outcome,
                    active = parameter2
                )
                addActionUseCase.execute(action)
            }
    }
}