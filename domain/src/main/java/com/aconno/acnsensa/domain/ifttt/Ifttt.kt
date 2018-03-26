package com.aconno.acnsensa.domain.ifttt

import com.aconno.acnsensa.domain.interactor.type.CompletableUseCaseWithParameter
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

object InputType {
    const val TEMPERATURE_READING = 0
}

class GeneralInput(
    override val value: Float,
    override val type: Int,
    override val timestamp: Long
) : Input

interface Condition {
    fun isSatisfied(input: Input): Boolean
}

class LimitCondition(private val limit: Float, private val type: Int) : Condition {

    override fun isSatisfied(input: Input): Boolean {
        return when (type) {
            LOWER_LIMIT -> input.value <= limit
            UPPER_LIMIT -> input.value >= limit
            else -> false
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

class NotificationOutcome(private val message: String) : Outcome {
    override fun execute() {
        println(message)
    }
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


