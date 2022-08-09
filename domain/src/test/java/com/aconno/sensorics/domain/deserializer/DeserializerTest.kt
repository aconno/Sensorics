package com.aconno.sensorics.domain.deserializer

import com.aconno.sensorics.domain.format.ByteFormat
import com.aconno.sensorics.domain.serialization.DeserializerImpl
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.nio.ByteBuffer
import kotlin.test.assertEquals


@RunWith(MockitoJUnitRunner::class)
class DeserializerTest {

    private val deserializer = DeserializerImpl()

    private val signedByteByteFormat = ByteFormat("", 0, 1, false, "BYTE", null, 0xFF.toByte())
    private val unsignedByteByteFormat = ByteFormat("", 0, 1, false, "UINT8", null, 0xFF.toByte())
    private val signedShortByteFormat = ByteFormat("", 0, 2, false, "SINT16", null, 0xFF.toByte())
    private val unsignedShortByteFormat = ByteFormat("", 0, 2, false, "UINT16", null, 0xFF.toByte())
    private val signedIntByteFormat = ByteFormat("", 0, 4, false, "INT32", null, 0xFF.toByte())
    private val unsignedIntByteFormat = ByteFormat("", 0, 4, false, "UINT32", null, 0xFF.toByte())
    private val signedLongByteFormat = ByteFormat("", 0, 8, false, "INT64", null, 0xFF.toByte())
    private val floatByteFormat = ByteFormat("", 0, 4, false, "FLOAT", null, 0xFF.toByte())

    @Test
    fun signedByteTest() {
        assertEquals((-1).toByte(),
            deserializer.deserializeNumber(byteArrayOf(0xFF.toByte()), signedByteByteFormat))
        assertEquals((-128).toByte(),
            deserializer.deserializeNumber(byteArrayOf(0x80.toByte()), signedByteByteFormat))
        assertEquals(0.toByte(),
            deserializer.deserializeNumber(byteArrayOf(0x00.toByte()), signedByteByteFormat))
        assertEquals(127.toByte(),
            deserializer.deserializeNumber(byteArrayOf(0x7F.toByte()), signedByteByteFormat))
    }

    @Test
    fun unsignedByteTest() {
        assertEquals(255.toShort(),
            deserializer.deserializeNumber(byteArrayOf(0xFF.toByte()), unsignedByteByteFormat))
        assertEquals(0.toShort(),
            deserializer.deserializeNumber(byteArrayOf(0x00.toByte()), unsignedByteByteFormat))
        assertEquals(127.toShort(),
            deserializer.deserializeNumber(byteArrayOf(0x7F.toByte()), unsignedByteByteFormat))
    }

    @Test
    fun signedShortTest() {
        assertEquals(Short.MAX_VALUE, deserializer.deserializeNumber(byteArrayOf(
            0x7F.toByte(), 0xFF.toByte()), signedShortByteFormat))
        assertEquals(0.toShort(), deserializer.deserializeNumber(byteArrayOf(
            0x00.toByte(), 0x00.toByte()), signedShortByteFormat))
        assertEquals((-1).toShort(), deserializer.deserializeNumber(byteArrayOf(
            0xFF.toByte(), 0xFF.toByte()), signedShortByteFormat))
        assertEquals(Short.MIN_VALUE, deserializer.deserializeNumber(byteArrayOf(
            0x80.toByte(), 0x00.toByte()), signedShortByteFormat))
    }

    @Test
    fun unsignedShortTest() {
        assertEquals(65535, deserializer.deserializeNumber(byteArrayOf(
            0xFF.toByte(), 0xFF.toByte()), unsignedShortByteFormat))
        assertEquals(0, deserializer.deserializeNumber(byteArrayOf(
            0x00.toByte(), 0x00.toByte()), unsignedShortByteFormat))
        assertEquals(Short.MAX_VALUE.toInt(), deserializer.deserializeNumber(byteArrayOf(
            0x7F.toByte(), 0xFF.toByte()), unsignedShortByteFormat))
    }

    @Test
    fun signedIntTest() {
        assertEquals(Int.MAX_VALUE, deserializer.deserializeNumber(byteArrayOf(
            0x7F.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte()), signedIntByteFormat))
        assertEquals(0, deserializer.deserializeNumber(byteArrayOf(
            0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()), signedIntByteFormat))
        assertEquals(-1, deserializer.deserializeNumber(byteArrayOf(
            0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte()), signedIntByteFormat))
        assertEquals(Int.MIN_VALUE, deserializer.deserializeNumber(byteArrayOf(
            0x80.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()), signedIntByteFormat))
    }

    @Test
    fun unsignedIntTest() {
        assertEquals(4294967295, deserializer.deserializeNumber(byteArrayOf(
            0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte()), unsignedIntByteFormat))
        assertEquals(0.toLong(), deserializer.deserializeNumber(byteArrayOf(
            0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()), unsignedIntByteFormat))
        assertEquals(Int.MAX_VALUE.toLong(), deserializer.deserializeNumber(byteArrayOf(
            0x7F.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte()), unsignedIntByteFormat))
    }

    @Test
    fun signedLongTest() {
        assertEquals(Long.MAX_VALUE, deserializer.deserializeNumber(byteArrayOf(
            0x7F.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(),
            0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte()), signedLongByteFormat))
        assertEquals(0.toLong(), deserializer.deserializeNumber(byteArrayOf(
            0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
            0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()), signedLongByteFormat))
        assertEquals((-1).toLong(), deserializer.deserializeNumber(byteArrayOf(
            0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(),
            0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte()), signedLongByteFormat))
        assertEquals(Long.MIN_VALUE, deserializer.deserializeNumber(byteArrayOf(
            0x80.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
            0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()), signedLongByteFormat))
    }

    @Test
    fun floatTest() {
        assertEquals(4.20.toFloat(), deserializer.deserializeNumber(
            ByteBuffer.allocate(4).putFloat(4.20.toFloat()).array(), floatByteFormat))
        assertEquals(0.toFloat(), deserializer.deserializeNumber(
            ByteBuffer.allocate(4).putFloat(0.toFloat()).array(), floatByteFormat))
        assertEquals((-12.34).toFloat(), deserializer.deserializeNumber(
            ByteBuffer.allocate(4).putFloat((-12.34).toFloat()).array(), floatByteFormat))
    }
}