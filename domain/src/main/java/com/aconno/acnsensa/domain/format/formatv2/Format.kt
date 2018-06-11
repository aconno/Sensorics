package com.aconno.acnsensa.domain.format.formatv2

data class Format(
    val propertyName: String,
    val startInclusive: Int,
    val endExclusive: Int,
    val reversed: Boolean,
    val type: String
)