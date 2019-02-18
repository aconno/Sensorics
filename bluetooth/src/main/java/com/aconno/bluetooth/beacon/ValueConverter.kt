package com.aconno.bluetooth.beacon

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.Charset
import kotlin.experimental.or

enum class ValueConverter(var default: Any, var converter: Converter<*>) {
    BOOLEAN(false, object : Converter<Boolean>(false) {
        override fun fromString(string: String): Boolean {
            return if (string.equals("true", true) || string == "1") true
            else if (string.equals("false", true) || string == "0") false
            else throw IllegalArgumentException("$string is an illegal value for type Boolean")
        }

        override fun serializeInternal(data: Boolean): ByteArray {
            return byteArrayOf(if (data) 0x01 else 0x00)
        }

        override fun deserializeInternal(data: ByteArray): Boolean {
            return when {
                data[0] == 0x01.toByte() -> true
                data[0] == 0x00.toByte() -> false
                else -> throw IllegalArgumentException("${bytesToHex(data)} is an illegal value for type Boolean")
            }
        }
    }),
    BYTE(0, object : Converter<Byte>(0) {
        override fun fromString(string: String): Byte {
            return string.toByte()
        }

        override fun serializeInternal(data: Byte): ByteArray {
            return byteArrayOf(data)
        }

        override fun deserializeInternal(data: ByteArray): Byte {
            return data[0]
        }
    }),
    MAC_ADDRESS(0, object : Converter<String>("00:11:22:33:44:55") {
        override fun serialize(data: String, order: ByteOrder): ByteArray {
            return serializeInternal(fromString(data))
        }

        override fun deserialize(data: ByteArray, order: ByteOrder): String {
            if (data.size != length && length != -1) {
                throw IllegalArgumentException("Invalid buffer length, expected $length, got ${data.size}")
            } else {
                return deserializeInternal(data)
            }
        }

        override fun fromString(string: String): String = string

        override fun serializeInternal(data: String): ByteArray =
            data.split(':').map { it.toByte() }.toList().toByteArray()

        override fun deserializeInternal(data: ByteArray): String =
            data.joinToString(":") { String.format("%02x", it) }
    }),
    SINT8(0, object : Converter<Byte>(0) {
        override fun fromString(string: String): Byte {
            return string.toByte()
        }

        override fun serializeInternal(data: Byte): ByteArray {
            return byteArrayOf(data)
        }

        override fun deserializeInternal(data: ByteArray): Byte {
            return data[0]
        }
    }),
    UINT8(0, object : Converter<Short>(0) {
        override fun fromString(string: String): Short {
            return string.toShort()
        }

        override fun serializeInternal(data: Short): ByteArray {
            return byteArrayOf(if (data >= 128) (data - 256).toByte() else data.toByte())
        }

        override fun deserializeInternal(data: ByteArray): Short {
            val v: Short = data[0].toShort()
            return (if (v < 0) (v + 256).toShort() else v)
        }
    }),
    SINT16(0, object : Converter<Short>(0) {
        override fun fromString(string: String): Short {
            return string.toShort()
        }

        override fun serializeInternal(data: Short): ByteArray {
            return ByteBuffer.allocate(2).putShort(data).array()
        }

        override fun deserializeInternal(data: ByteArray): Short {
            var short: Short = 0
            var i = 1
            short = short or
                    ((if (data[i].toShort() < 0) (data[i].toShort() + 256).toShort() else data[i].toShort()).toInt() shl 0).toShort()
            i = 0
            short = short or
                    ((if (data[i].toShort() < 0) (data[i].toShort() + 256).toShort() else data[i].toShort()).toInt() shl 8).toShort()
            return short
        }
    }),
    UINT16(0, object : Converter<Int>(0) {
        override fun fromString(string: String): Int {
            return string.toInt()
        }

        override fun serializeInternal(data: Int): ByteArray {
            return ByteBuffer.allocate(2).putShort((data).and(0xFFFF).toShort()).array()
        }

        override fun deserializeInternal(data: ByteArray): Int {
            var v: Short = 0
            var i = 1
            v = v or
                    ((if (data[i].toShort() < 0) (data[i].toShort() + 256).toShort() else data[i].toShort()).toInt() shl 0).toShort()
            i = 0
            v = v or
                    ((if (data[i].toShort() < 0) (data[i].toShort() + 256).toShort() else data[i].toShort()).toInt() shl 8).toShort()
            return (if (v < 0) v.toInt() + 65536 else v.toInt())
        }

    }),
    SINT32(0, object : Converter<Int>(0) {
        override fun fromString(string: String): Int {
            return string.toInt()
        }

        override fun serializeInternal(data: Int): ByteArray {
            return ByteBuffer.allocate(4).putInt(data).array()
        }

        override fun deserializeInternal(data: ByteArray): Int {
            var int = 0
            for (i in 3 downTo 0) {
                int = int or
                        ((if (data[i].toShort() < 0) (data[i].toShort() + 256).toShort() else data[i].toShort()).toInt() shl ((3 - i) * 8))
            }
            return int
        }

    }),
    UINT32(0, object : Converter<Long>(0) {
        override fun fromString(string: String): Long {
            return string.toLong()
        }

        override fun serializeInternal(data: Long): ByteArray {
            return ByteBuffer.allocate(4).putInt((data).and(0xFFFFFFFF).toInt()).array()
        }

        override fun deserializeInternal(data: ByteArray): Long {
            var v = 0
            for (i in 3 downTo 0) {
                v = v or
                        ((if (data[i].toShort() < 0) (data[i].toShort() + 256).toShort() else data[i].toShort()).toInt() shl ((3 - i) * 8))
            }
            return (if (v < 0) v.toLong() + 4294967296L else v.toLong())
        }
    }),
    UTF8STRING("", object : Converter<String>("") {
        private val ASCII = Charset.forName("ASCII")

        override fun fromString(string: String): String {
            return string
        }

        override fun serialize(data: String, order: ByteOrder): ByteArray {
            return super.serialize(data, ByteOrder.BIG_ENDIAN)
        }

        override fun serializeInternal(data: String): ByteArray {
            return data.toByteArray(ASCII)
        }

        override fun deserializeInternal(data: ByteArray): String {
            return data.toString(ASCII).trim(0x00.toChar())
        }

    }),
    TIME(0, object : Converter<Long>(0) {
        override fun fromString(string: String): Long {
            return string.toLong()
        }

        override fun serializeInternal(data: Long): ByteArray {
            return ByteBuffer.allocate(8).putLong(data).array().copyOfRange(0, 6)
        }

        override fun deserializeInternal(data: ByteArray): Long {
            var time: Long = 0
            for (i in 5 downTo 0) {
                time += ((if (data[i].toShort() < 0) (data[i].toShort() + 256).toShort() else data[i].toShort()).toLong() shl ((5 - i) * 8))
            }
            return time
        }

    }),
    FLOAT(0f, object : Converter<Float>(0f) {
        override fun fromString(string: String): Float {
            return string.toFloat()
        }

        override fun serializeInternal(data: Float): ByteArray {
            val byteBuffer = ByteBuffer.allocate(4)
            byteBuffer.putFloat(data)
            return byteBuffer.array()
        }

        override fun deserializeInternal(data: ByteArray): Float {
            val byteBuffer = ByteBuffer.wrap(data)
            return byteBuffer.getFloat(0)
        }
    }),
    ENUM(0, object : Converter<Long>(0) {
        override fun fromString(string: String): Long {
            return string.toLong()
        }

        override fun serializeInternal(data: Long): ByteArray {
            return ByteBuffer.allocate(4).putInt((data).and(0xFFFFFFFF).toInt()).array()
        }

        override fun deserializeInternal(data: ByteArray): Long {
            var v = 0
            for (i in 3 downTo 0) {
                v = v or
                        ((if (data[i].toShort() < 0) (data[i].toShort() + 256).toShort() else data[i].toShort()).toInt() shl ((3 - i) * 8))
            }
            return (if (v < 0) v.toLong() + 4294967296L else v.toLong())
        }
    });

    abstract class Converter<T>(val default: T, val length: Int = -1) {
        fun toString(value: T): String {
            return value.toString()
        }

        abstract fun fromString(string: String): T

        open fun serialize(data: String, order: ByteOrder = ByteOrder.LITTLE_ENDIAN): ByteArray {
            return serializeInternal(fromString(data)).apply {
                if (order == ByteOrder.LITTLE_ENDIAN) reverse()
            }
        }

        open fun serialize(data: Any, order: ByteOrder = ByteOrder.LITTLE_ENDIAN): ByteArray {
            return serialize(data.toString(), order)
        }

        abstract fun serializeInternal(data: T): ByteArray

        open fun deserialize(data: ByteArray, order: ByteOrder = ByteOrder.LITTLE_ENDIAN): T {
            if (data.size != length && length != -1) {
                throw IllegalArgumentException("Invalid buffer length, expected $length, got ${data.size}")
            } else {
                data.apply {
                    if (order == ByteOrder.LITTLE_ENDIAN) reverse()
                }
                return deserializeInternal(data)
            }
        }

        abstract fun deserializeInternal(data: ByteArray): T

        fun bytesToHex(`in`: ByteArray): String {
            val builder = StringBuilder()
            for (b in `in`) {
                builder.append(String.format("%02x, ", b))
            }
            return builder.toString()
        }
    }
}