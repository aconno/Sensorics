package com.aconno.sensorics.domain.interactor.ifttt

import com.aconno.sensorics.domain.actions.Action
import com.aconno.sensorics.domain.actions.ActionsRepository
import com.aconno.sensorics.domain.ifttt.Input
import com.aconno.sensorics.domain.ifttt.outcome.Outcome
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

    private val previousConditions: MutableMap<Long, MutableMap<String, Boolean>> =
        mutableMapOf()

    private fun actionsToOutcomes(actions: List<Action>, input: Input): List<Outcome> {
        val result = mutableListOf<Outcome>()

        actions.filter { it.device.macAddress == input.macAddress }
            .forEach { action ->
                val actionPreviousConditions = previousConditions[action.id] ?: mutableMapOf()
                val previousCondition = actionPreviousConditions[input.type]
                previousCondition?.let {
                    if (action.condition.isSatisfied(input) && !it) {
                        result.add(action.outcome)
                    }
                }
                actionPreviousConditions[input.type] = action.condition.isSatisfied(input)
                previousConditions[action.id] = actionPreviousConditions
            }

        return result
    }
}