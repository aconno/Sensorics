package com.aconno.acnsensa.domain.ifttt

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