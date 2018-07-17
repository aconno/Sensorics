package com.aconno.sensorics.domain.ifttt

class LimitCondition(
    override val readingType: String,
    override val limit: Float,
    override val type: Int
) : Condition {

    override fun isSatisfied(input: Input): Boolean {
        return if (input.type == readingType) {
            when (type) {
                LESS_THAN -> input.value <= limit
                MORE_THAN -> input.value >= limit
                else -> false
            }
        } else {
            false
        }
    }

    override fun toString(): String {
        val sign = when (type) {
            LESS_THAN -> "<"
            MORE_THAN -> ">"
            else -> throw IllegalArgumentException("Invalid constraint type: $type")
        }
        return "$readingType $sign $limit"
    }

    companion object {
        const val LESS_THAN = 0
        const val MORE_THAN = 1
    }
}