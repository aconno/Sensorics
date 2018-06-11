package com.aconno.acnsensa.domain.format

data class ByteFormat(
    val name: String,
    val startIndexInclusive: Int,
    val endIndexExclusive: Int,
    val isReversed: Boolean,
    val dataType: String
)