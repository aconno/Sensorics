package com.aconno.sensorics.domain.interactor.ifttt

import com.aconno.sensorics.domain.actions.Action
import com.aconno.sensorics.domain.actions.ActionsRepository
import com.aconno.sensorics.domain.actions.outcomes.Outcome
import com.aconno.sensorics.domain.ifttt.Input
import com.aconno.sensorics.domain.interactor.time.GetLocalTimeOfDayInSecondsUseCase
import com.aconno.sensorics.domain.interactor.type.SingleUseCaseWithParameter
import io.reactivex.Observable
import io.reactivex.Single

class InputToOutcomesUseCase(
    private val actionsRepository: ActionsRepository,
    private val getLocalTimeOfDayInSecondsUseCase: GetLocalTimeOfDayInSecondsUseCase
) : SingleUseCaseWithParameter<List<Outcome>, Input> {

    override fun execute(parameter: Input): Single<List<Outcome>> {
        val observable = getLocalTimeOfDayInSecondsUseCase.execute()
            .flatMapObservable { timeOfDayInSeconds ->
                actionsRepository.getAllActions().toObservable()
                    .concatMap { actions ->
                        Observable.just(actionsToOutcomes(actions, parameter, timeOfDayInSeconds))
                    }
            }
        return Single.fromObservable(observable)
    }

    private fun actionsToOutcomes(
        actions: List<Action>,
        input: Input,
        timeOfDayInSeconds: Int
    ): List<Outcome> {
        val result = mutableListOf<Outcome>()

        actions.asSequence().filter {
            it.device.macAddress == input.macAddress
        }.filter { action ->
            action.active
        }.filter { action ->
            // Suppressing because of the last else if branch, I wanted it to be readable
            @Suppress("RedundantIf")
            if (action.timeFrom == action.timeTo) {
                // Times are the same so 24h a day
                true
            } else if (action.timeFrom < action.timeTo && (timeOfDayInSeconds >= action.timeFrom && timeOfDayInSeconds <= action.timeTo)) {
                /*
                     Time from is lesser than time to, therefore it is set in the same day
                     We just check if time of day in seconds is in these bounds
                     */
                true
            } else if (action.timeFrom > action.timeTo && (timeOfDayInSeconds >= action.timeFrom || timeOfDayInSeconds <= action.timeTo)) {
                /*
                     Time from is bigger than time to therefore it is set across midnight
                     We check if time of day in seconds is either larger then time from so that it
                     fires from time from until 23:59:59, and checks if its lesser than time to so
                     that it fires from 00:00:00 until time to
                     */
                true
            } else {
                /*
                     None of the conditions have been satisfied therefore the outcome should
                     not fire
                    */
                false
            }
        }.filter { action ->
            action.condition.isSatisfied(input)
        }.map { action ->
            action.outcome
        }.toList().let { outcomes ->
            result.addAll(outcomes)
        }

        return result
    }
}