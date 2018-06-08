package com.aconno.acnsensa.domain.ifttt

import com.aconno.acnsensa.domain.model.SensorTypeSingle

class LimitCondition(
    override val sensorType: SensorTypeSingle,
    override val limit: Float,
    override val type: Int
) :
    Condition {

    override fun isSatisfied(input: Input): Boolean {
        println("Condition Type: $type Value: ${input.value} Limit: $limit")
        return if (input.type == sensorType) {
            when (type) {
                LESS_THAN -> input.value <= limit
                MORE_THAN -> input.value >= limit
                else -> false
            }
        } else {
            false
        }
    }

    companion object {
        const val LESS_THAN = 0
        const val MORE_THAN = 1
    }
}