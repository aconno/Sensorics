package com.aconno.acnsensa.domain.ifttt

import com.aconno.acnsensa.domain.interactor.type.CompletableUseCaseWithParameter
import com.aconno.acnsensa.domain.interactor.type.SingleUseCase
import io.reactivex.Completable
import io.reactivex.Single

/**
 * @aconno
 */
interface Input {
    val value: Float
    val type: Int
    val timestamp: Long
}

class GeneralInput(
    override val value: Float,
    override val type: Int,
    override val timestamp: Long
) : Input

interface Condition {
    fun isSatisfied(input: Input): Boolean
}

class LimitCondition(private val sensorType: Int, private val limit: Float, private val type: Int) :
    Condition {

    override fun isSatisfied(input: Input): Boolean {
        println("Condition Type: $type Value: ${input.value} Limit: $limit")
        return if (input.type == sensorType) {
            when (type) {
                LOWER_LIMIT -> input.value <= limit
                UPPER_LIMIT -> input.value >= limit
                else -> false
            }
        } else {
            false
        }
    }

    companion object {
        const val LOWER_LIMIT = 0
        const val UPPER_LIMIT = 1
    }
}

interface Outcome {
    fun execute()
}

class NotificationOutcome(
    private val message: String,
    private val notificationDisplay: NotificationDisplay
) : Outcome {
    override fun execute() {
        notificationDisplay.displayAlertNotification(message)
    }
}

interface NotificationDisplay {
    fun displayAlertNotification(message: String)
}

interface Action {
    val name: String
    fun processInput(input: Input)
}

class GeneralAction(
    override val name: String,
    private val condition: Condition,
    private val outcome: Outcome
) : Action {

    override fun processInput(input: Input) {
        if (condition.isSatisfied(input)) {
            outcome.execute()
        }
    }
}


class HandleInputUseCase(
    private val actionsRespository: ActionsRespository
) : CompletableUseCaseWithParameter<Input> {
    override fun execute(parameter: Input): Completable {
        actionsRespository.getAllActions()
            .subscribe { actions -> actions.forEach { it.processInput(parameter) } }
        return Completable.complete()
    }
}

class AddActionUseCase(
    private val actionsRespository: ActionsRespository
) :
    CompletableUseCaseWithParameter<Action> {
    override fun execute(parameter: Action): Completable {
        actionsRespository.addAction(parameter)
        return Completable.complete()
    }
}

class GetAllActionsUseCase(
    private val actionsRespository: ActionsRespository
) : SingleUseCase<List<Action>> {
    override fun execute(): Single<List<Action>> {
        return actionsRespository.getAllActions()
    }
}

class DeleteActionUseCase(
    private val actionsRespository: ActionsRespository
) : CompletableUseCaseWithParameter<Action> {
    override fun execute(parameter: Action): Completable {
        actionsRespository.deleteAction(parameter)
        return Completable.complete()
    }
}

interface ActionsRespository {
    fun addAction(action: Action)
    fun deleteAction(action: Action)
    fun getAllActions(): Single<List<Action>>
}


