package com.aconno.acnsensa.model

import android.content.Context
import com.aconno.acnsensa.domain.ifttt.Condition
import com.aconno.acnsensa.domain.ifttt.LimitCondition

fun Condition.toString(context: Context): String {
    val sensor = readingType.toString()
    //TODO: Refactor constraint type
    val constraint = when (type) {
        LimitCondition.MORE_THAN -> ">"
        LimitCondition.LESS_THAN -> "<"
        else -> throw IllegalArgumentException("Int is not valid constraint identifier: $type")
    }
    val value = limit.toString()
    return "$sensor $constraint $value"
}