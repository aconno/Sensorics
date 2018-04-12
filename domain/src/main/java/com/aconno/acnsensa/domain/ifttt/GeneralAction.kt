package com.aconno.acnsensa.domain.ifttt

/**
 * @author aconno
 */
class GeneralAction(
    override val id: Long,
    override val name: String,
    override val condition: Condition,
    override val outcome: Outcome
) : Action {

    override fun processInput(input: Input) {
        if (condition.isSatisfied(input)) {
            outcome.execute()
        }
    }
}