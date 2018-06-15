package com.aconno.acnsensa.domain.ifttt

import com.aconno.acnsensa.domain.model.ReadingType

class LimitCondition(
    override val readingType: ReadingType,
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

    companion object {
        const val LESS_THAN = 0
        const val MORE_THAN = 1
    }
}