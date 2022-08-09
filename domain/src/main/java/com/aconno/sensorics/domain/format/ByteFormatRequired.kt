package com.aconno.sensorics.domain.format

data class ByteFormatRequired(
    val name: String,
    val position: Int,
    val value: Byte,
    val source: Byte
)