package com.aconno.sensorics.domain.ifttt

import com.aconno.sensorics.domain.ifttt.LimitCondition.LimitOperator.*

class LimitCondition(
    override val readingType: String,
    override val type: Int,
    override val limit: Float
) : Condition {
    constructor(
        readingType: String,
        type: LimitOperator,
        limit: Float
    ) : this(readingType, type.opCode, limit)

    override fun isSatisfied(input: Input): Boolean {
        return when (type) {
            LESS_THAN.opCode -> input.value <= limit
            MORE_THAN.opCode -> input.value >= limit
            EQUAL_TO.opCode -> input.value == limit
            else -> false
        }
    }

    override fun toString(): String {
        val sign = getConditionTypeAsString()
        return "$readingType $sign $limit"
    }

    override fun getConditionTypeAsString(): String {
        return when (type) {
            LESS_THAN.opCode -> "<"
            MORE_THAN.opCode -> ">"
            EQUAL_TO.opCode -> "="
            else -> throw IllegalArgumentException("Invalid constraint type: $type")
        }
    }

    enum class LimitOperator(internal val opCode: Int) {
        LESS_THAN(0),
        MORE_THAN(1),
        EQUAL_TO(2)
    }
}