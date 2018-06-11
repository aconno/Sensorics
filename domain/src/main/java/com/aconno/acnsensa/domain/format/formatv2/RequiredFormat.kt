package com.aconno.acnsensa.domain.format.formatv2

data class RequiredFormat(
    val propertyName: String,
    val startInclusive: Int,
    val endInclusive: Int,
    val value: String
)