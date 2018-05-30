package com.aconno.acnsensa.domain.interactor.ifttt

import com.aconno.acnsensa.domain.ifttt.Action
import com.aconno.acnsensa.domain.ifttt.ActionsRepository
import com.aconno.acnsensa.domain.ifttt.Input
import com.aconno.acnsensa.domain.ifttt.outcome.Outcome
import com.aconno.acnsensa.domain.interactor.type.SingleUseCaseWithParameter
import io.reactivex.Observable
import io.reactivex.Single

/**
 * @author aconno
 */
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

    private val previousConditions: MutableMap<Long, MutableMap<Int, Boolean>> = mutableMapOf()

    private fun actionsToOutcomes(actions: List<Action>, input: Input): List<Outcome> {
        val result = mutableListOf<Outcome>()

        for (action in actions) {
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