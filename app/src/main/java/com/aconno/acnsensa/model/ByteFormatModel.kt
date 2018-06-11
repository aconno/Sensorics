package com.aconno.acnsensa.model

data class ByteFormatModel(
    val name: String,
    val startIndexInclusive: Int,
    val endIndexExclusive: Int,
    val isReversed: Boolean,
    val dataType: String
)