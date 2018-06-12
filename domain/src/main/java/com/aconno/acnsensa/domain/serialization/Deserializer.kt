package com.aconno.acnsensa.domain.serialization

import com.aconno.acnsensa.domain.format.ByteFormat

interface Deserializer {

    fun deserializeNumber(rawData: List<Byte>, byteFormat: ByteFormat): Number
}