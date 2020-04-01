package com.aconno.sensorics.domain.ifttt

class LimitCondition(
    override val readingType: String,
    override val limit: Float,
    override val type: Int
) : Condition {

    override fun isSatisfied(input: Input): Boolean {
        return when (type) {
            LESS_THAN -> input.value <= limit
            MORE_THAN -> input.value >= limit
            EQUAL_TO -> input.value == limit
            else -> false
        }
    }

    override fun toString(): String {
        val sign = getConditionTypeAsString()
        return "$readingType $sign $limit"
    }

    override fun getConditionTypeAsString(): String {
        return when (type) {
            LESS_THAN -> "<"
            MORE_THAN -> ">"
            EQUAL_TO -> "="
            else -> throw IllegalArgumentException("Invalid constraint type: $type")
        }
    }

    companion object {

        const val LESS_THAN = 0
        const val MORE_THAN = 1
        const val EQUAL_TO = 2

        fun typeFromString(type: String): Int {
            return when (type) {
                "<" -> LESS_THAN
                ">" -> MORE_THAN
                "=" -> EQUAL_TO
                else -> throw IllegalArgumentException("Invalid constraint type: $type")
            }
        }

    }
}