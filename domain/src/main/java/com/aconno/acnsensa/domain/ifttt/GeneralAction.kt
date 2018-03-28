package com.aconno.acnsensa.domain.ifttt

/**
 * @author aconno
 */
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