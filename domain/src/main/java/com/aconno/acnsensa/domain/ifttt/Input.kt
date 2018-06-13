package com.aconno.acnsensa.domain.ifttt

import com.aconno.acnsensa.domain.interactor.filter.ReadingType

interface Input {
    val macAddress: String
    val value: Float
    val type: ReadingType
    val timestamp: Long
}