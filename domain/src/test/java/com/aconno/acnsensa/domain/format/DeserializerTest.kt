package com.aconno.acnsensa.domain.format

import org.junit.Assert
import org.junit.Test

class DeserializerTest {

    @Test
    fun deserializeShortTest() {
        val bytes = listOf(0x0F.toByte(), 0x7F.toByte())
        var short = 0
        short = short or bytes[0].toInt()
        short = short shl 8
        short = short or bytes[1].toInt()
        Assert.assertEquals(32767, short)
    }
}