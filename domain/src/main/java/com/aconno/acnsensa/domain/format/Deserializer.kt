package com.aconno.acnsensa.domain.format

interface Deserializer {

    fun deserializeNumber(rawData: List<Byte>, byteFormat: ByteFormat): Number
}