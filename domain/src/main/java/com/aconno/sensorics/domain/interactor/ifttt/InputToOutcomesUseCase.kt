package com.aconno.sensorics.domain.interactor.ifttt

import com.aconno.sensorics.domain.actions.Action
import com.aconno.sensorics.domain.actions.ActionsRepository
import com.aconno.sensorics.domain.actions.outcomes.Outcome
import com.aconno.sensorics.domain.ifttt.Input
import com.aconno.sensorics.domain.interactor.type.SingleUseCaseWithParameter
import io.reactivex.Observable
import io.reactivex.Single

class InputToOutcomesUseCase(
    private val actionsRepository: ActionsRepository
) : SingleUseCaseWithParameter<List<Outcome>, Input> {

    override fun execute(parameter: Input): Single<List<Outcome>> {
        val observable = actionsRepository.getAllActions().toObservable()
            .concatMap { actions ->
                Observable.just(actionsToOutcomes(actions, parameter))
            }
        return Single.fromObservable(observable)
    }
    
    private fun actionsToOutcomes(actions: List<Action>, input: Input): List<Outcome> {
        val result = mutableListOf<Outcome>()

        actions.filter { it.device.macAddress == input.macAddress }
            .forEach { action ->
                if (action.condition.isSatisfied(input)) {
                    result.add(action.outcome)

                }

            }

        return result
    }


}