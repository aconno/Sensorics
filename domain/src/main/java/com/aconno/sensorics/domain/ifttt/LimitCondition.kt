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

    override fun isSatisfied(input: Input, data: Any?): Boolean {
        return when (type) {
            LESS_THAN.opCode -> input.value <= limit
            MORE_THAN.opCode -> input.value >= limit
            EQUAL_TO.opCode -> input.value == limit
            CHANGED.opCode -> {
                (data as? Input)?.let { lastInput ->
                    input.value != lastInput.value
                } ?: false
            }
            else -> false
        }
    }

    override fun toString(): String {
        val sign = getConditionTypeAsString()

        return if (type == CHANGED.opCode) {
            "$readingType changed"
        } else {
            "$readingType $sign $limit"
        }
    }

    override fun getConditionTypeAsString(): String {
        return when (type) {
            LESS_THAN.opCode -> "<"
            MORE_THAN.opCode -> ">"
            EQUAL_TO.opCode -> "="
            CHANGED.opCode -> "c"
            else -> throw IllegalArgumentException("Invalid constraint type: $type")
        }
    }

    enum class LimitOperator(internal val opCode: Int) {
        LESS_THAN(0),
        MORE_THAN(1),
        EQUAL_TO(2),
        CHANGED(3)
    }
}