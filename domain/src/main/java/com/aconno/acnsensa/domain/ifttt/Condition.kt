package com.aconno.acnsensa.domain.ifttt

interface Condition {
    val readingType: String
    val limit: Float
    val type: Int
    fun isSatisfied(input: Input): Boolean
}