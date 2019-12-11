package com.aconno.sensorics.model

import com.aconno.sensorics.domain.ifttt.Condition

fun Condition.toStringRepresentation(): String {
    val sensor = readingType
    val constraint = getConditionTypeAsString()
    val value = limit.toString()
    return "$sensor $constraint $value"
}