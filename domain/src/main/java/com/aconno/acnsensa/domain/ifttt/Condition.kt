package com.aconno.acnsensa.domain.ifttt

import com.aconno.acnsensa.domain.interactor.filter.ReadingType

interface Condition {
    val readingType: ReadingType
    val limit: Float
    val type: Int
    fun isSatisfied(input: Input): Boolean
}