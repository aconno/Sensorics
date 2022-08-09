package com.aconno.sensorics.device.beacon.v2.parameters

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.core.IsInstanceOf
import org.junit.Assert.assertThat
import org.junit.Test

class UtilsKtTest {

    @Test
    fun getAsGivenTypeOrNull_passValueOfIncorrectRange_ShouldReturnNull() {
        val byte = getAsGivenTypeOrNull("-129",Byte::class.java )
        assertThat(byte, `is`(nullValue()))

        val short = getAsGivenTypeOrNull("32,768",Short::class.java)
        assertThat(short, `is`(nullValue()))

        val int = getAsGivenTypeOrNull("-2147483649",Int::class.java)
        assertThat(int, `is`(nullValue()))

        val long = getAsGivenTypeOrNull("9223372036854775808", Long::class.java)
        assertThat(long, `is`(nullValue()))
    }

    @Test
    fun getAsGivenTypeOrNull_passIncorrectData_ShouldReturnNull() {
        val byte = getAsGivenTypeOrNull("NaN",Byte::class.java)
        assertThat(byte, `is`(nullValue()))

        val short = getAsGivenTypeOrNull("NaN",Short::class.java)
        assertThat(short, `is`(nullValue()))

        val int = getAsGivenTypeOrNull("NaN",Int::class.java)
        assertThat(int, `is`(nullValue()))

        val long = getAsGivenTypeOrNull("NaN",Long::class.java)
        assertThat(long, `is`(nullValue()))

        val float = getAsGivenTypeOrNull("notANumber",Float::class.java)
        assertThat(float, `is`(nullValue()))

        val double = getAsGivenTypeOrNull("notANumber",Double::class.java)
        assertThat(double, `is`(nullValue()))
    }

    @Test
    fun getAsGivenTypeOrNull_passValidData_ReturnValidType() {
        val byte = getAsGivenTypeOrNull("-1",Byte::class.java)
        assertThat(byte, IsInstanceOf(Byte::class.java))
        assertThat(byte, `is`((-1).toByte()))

        val short = getAsGivenTypeOrNull("2",Short::class.java)
        assertThat(short, IsInstanceOf(Short::class.java))
        assertThat(short, `is`(2.toShort()))

        val int = getAsGivenTypeOrNull("30000",Int::class.java)
        assertThat(int, IsInstanceOf(Int::class.java))
        assertThat(int, `is`(30000))

        val long = getAsGivenTypeOrNull("200000000000000",Long::class.java)
        assertThat(long, IsInstanceOf(Long::class.java))
        assertThat(long, `is`(200000000000000L))

        val float = getAsGivenTypeOrNull("0.001",Float::class.java)
        assertThat(float, IsInstanceOf(Float::class.java))
        assertThat(float, `is`(0.001.toFloat()))

        val double = getAsGivenTypeOrNull("0.001",Double::class.java)
        assertThat(double, IsInstanceOf(Double::class.java))
        assertThat(double, `is`(0.001))
    }
}