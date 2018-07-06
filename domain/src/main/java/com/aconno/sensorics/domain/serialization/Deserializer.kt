package com.aconno.sensorics.domain.serialization

import com.aconno.sensorics.domain.format.ByteFormat

interface Deserializer {

    fun deserializeNumber(rawData: List<Byte>, byteFormat: ByteFormat): Number
}