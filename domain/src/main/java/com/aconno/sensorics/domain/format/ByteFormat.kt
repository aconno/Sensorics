package com.aconno.sensorics.domain.format

import com.udojava.evalex.Expression

data class ByteFormat(
    val name: String,
    val startIndexInclusive: Int,
    val endIndexExclusive: Int,
    val isReversed: Boolean,
    val dataType: String,
    val formula: Expression?
)