package com.aconno.sensorics.domain.ifttt

interface Condition {
    val readingType: String
    val limit: Float
    val type: Int
    fun isSatisfied(input: Input, data: Any? = null): Boolean
    fun getConditionTypeAsString() : String

    fun toStringRepresentation(): String {
        val sensor = readingType
        val constraint = getConditionTypeAsString()
        val value = limit.toString()
        return "$sensor $constraint $value"
    }
}