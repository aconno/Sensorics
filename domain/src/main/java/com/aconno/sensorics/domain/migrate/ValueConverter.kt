package com.aconno.sensorics.domain.migrate

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.Charset
import kotlin.experimental.or


abstract class ValueConverterBase<T>(
        val length: Int = -1,
        private val default: T,
        private val clazz: Class<T>
) {
    @Throws(IllegalArgumentException::class)
    fun parse(string: String): T {
        return this.parseInternal(string)
                ?: throw IllegalArgumentException(
                        "$string cannot be parsed into a ${clazz.simpleName}"
                )
    }

    fun serializeOrNull(data: T, order: ByteOrder = ByteOrder.LITTLE_ENDIAN): ByteArray? {
        return this.serializeInternal(data)?.also {
            if (order != ByteOrder.LITTLE_ENDIAN) it.reverse()
        }
    }

    @Throws(IllegalArgumentException::class)
    fun serialize(data: T, order: ByteOrder = ByteOrder.LITTLE_ENDIAN): ByteArray {
        return this.serializeOrNull(data, order) ?: throw IllegalArgumentException(
                "$data is an illegal serialization value for type ${clazz.simpleName}"
        )
    }

    fun deserializeOrNull(data: ByteArray, start: Int, length: Int = this.length, order: ByteOrder = ByteOrder.LITTLE_ENDIAN): T? {
        return this.deserializeOrNull(data.copyOfRange(start, start + length), order)
    }

    fun deserializeOrNull(data: ByteArray, order: ByteOrder = ByteOrder.LITTLE_ENDIAN): T? {
        if (length != -1 && data.size != length) return null
        return this.deserializeInternal(data.also {
            if (order != ByteOrder.LITTLE_ENDIAN) it.reverse()
        })
    }

    @Throws(IllegalArgumentException::class)
    fun deserialize(data: ByteArray, start: Int, length: Int = this.length, order: ByteOrder = ByteOrder.LITTLE_ENDIAN): T {
        return this.deserialize(data.copyOfRange(start, start + length), order)
    }

    @Throws(IllegalArgumentException::class)
    fun deserialize(data: ByteArray, order: ByteOrder = ByteOrder.LITTLE_ENDIAN): T {
        // TODO: Double check but necessary for good error info
        if (length != -1 && data.size != length) {
            throw IllegalArgumentException(
                    "Length of data ${data.toHex()} (${data.size}) does not match the specified length: $length"
            )
        }
        return this.deserializeOrNull(data, order) ?: throw IllegalArgumentException(
                "${data.toHex()} is an illegal deserialization value for ${clazz.simpleName}"
        )
    }

    protected abstract fun parseInternal(string: String): T?

    protected abstract fun serializeInternal(data: T): ByteArray?

    protected abstract fun deserializeInternal(data: ByteArray): T?
}

class ValueConverters {
    companion object {
        val BOOLEAN: ValueConverterBase<Boolean> = object : ValueConverterBase<Boolean>(
                1,
                false,
                Boolean::class.java
        ) {
            override fun parseInternal(string: String): Boolean? {
                return when (string.toLowerCase()) {
                    "true", "1" -> true
                    "false", "0" -> false
                    else -> null
                }
            }

            override fun serializeInternal(data: Boolean): ByteArray? {
                return byteArrayOf(if (data) 0x01 else 0x00)
            }

            override fun deserializeInternal(data: ByteArray): Boolean? {
                return when {
                    data[0] == 0x01.toByte() -> true
                    data[0] == 0x00.toByte() -> false
                    else -> null
                }
            }
        }

        val BYTE: ValueConverterBase<Byte> = object : ValueConverterBase<Byte>(
                1,
                0,
                Byte::class.java
        ) {
            override fun parseInternal(string: String): Byte? {
                return try {
                    string.toByte()
                } catch (nfe: NumberFormatException) {
                    null
                }
            }

            override fun serializeInternal(data: Byte): ByteArray? {
                return byteArrayOf(data)
            }

            override fun deserializeInternal(data: ByteArray): Byte? {
                return data[0]
            }
        }

        val MAC_ADDRESS: ValueConverterBase<String> = object : ValueConverterBase<String>(
                6,
                "AA:BB:CC:DD:EE:FF",
                String::class.java
        ) {
            override fun parseInternal(string: String): String? = string

            override fun serializeInternal(data: String): ByteArray? {
                return data.split(':')
                        .takeIf { it.size == 6 }
                        ?.mapNotNull {
                            try {
                                it.hexPairToByte()
                            } catch (iae: IllegalArgumentException) {
                                null
                            }
                        }?.takeIf { it.size == 6 }
                        ?.toByteArray()
            }

            override fun deserializeInternal(data: ByteArray): String? {
                return data.joinToString(":") {
                    String.format("%02x", it)
                }.toUpperCase()
            }
        }

        val INT8: ValueConverterBase<Byte> = BYTE

        val UINT8: ValueConverterBase<Short> = object : ValueConverterBase<Short>(
                1,
                0,
                Short::class.java
        ) {
            override fun parseInternal(string: String): Short? {
                return try {
                    string.toShort()
                } catch (nfe: NumberFormatException) {
                    null
                }
            }

            override fun serializeInternal(data: Short): ByteArray? {
                return byteArrayOf(if (data >= 128) (data - 256).toByte() else data.toByte())
            }

            override fun deserializeInternal(data: ByteArray): Short? {
                val v: Short = data[0].toShort()
                return (if (v < 0) (v + 256).toShort() else v)
            }
        }

        val INT16: ValueConverterBase<Short> = object : ValueConverterBase<Short>(
                2,
                0,
                Short::class.java
        ) {
            override fun parseInternal(string: String): Short? {
                return try {
                    string.toShort()
                } catch (nfe: NumberFormatException) {
                    null
                }
            }

            override fun serializeInternal(data: Short): ByteArray? = ByteArray(2) { i ->
                ((data.toInt() shr 8 * i) and 0xFF).toByte()
            }

            override fun deserializeInternal(data: ByteArray): Short? {
                var v: Short = 0
                for (i in 0 until 2) {
                    v = v or (data[i].toInt().let { if (it < 0) it + 256 else it + 0 } shl i * 8).toShort()
                }
                return v
            }
        }

        val UINT16: ValueConverterBase<Int> = object : ValueConverterBase<Int>(
                2,
                0,
                Int::class.java
        ) {
            override fun parseInternal(string: String): Int? {
                return try {
                    string.toInt()
                } catch (nfe: NumberFormatException) {
                    null
                }
            }

            override fun serializeInternal(data: Int): ByteArray? = ByteArray(2) { i ->
                ((data shr 8 * i) and 0xFF).toByte()
            }


            override fun deserializeInternal(data: ByteArray): Int? {
                var v: Int = 0
                for (i in 0 until 2) {
                    v = v or data[i].toInt().let { (if (it < 0) it + 256 else it) shl i * 8 }
                }
                return v
            }
        }

        val INT32: ValueConverterBase<Int> = object : ValueConverterBase<Int>(
                4,
                0,
                Int::class.java
        ) {
            override fun parseInternal(string: String): Int? {
                return try {
                    string.toInt()
                } catch (nfe: NumberFormatException) {
                    null
                }
            }

            override fun serializeInternal(data: Int): ByteArray? = ByteArray(4) { i ->
                ((data shr 8 * i) and 0xFF).toByte()
            }

            override fun deserializeInternal(data: ByteArray): Int? {
                var v: Int = 0
                for (i in 0 until 4) {
                    v = v or (data[i].toInt().let { if (it < 0) it + 256 else it + 0 } shl i * 8)
                }
                return v
            }
        }

        val UINT32: ValueConverterBase<Long> = object : ValueConverterBase<Long>(
                4,
                0,
                Long::class.java
        ) {
            override fun parseInternal(string: String): Long? {
                return try {
                    string.toLong()
                } catch (nfe: NumberFormatException) {
                    null
                }
            }

            override fun serializeInternal(data: Long): ByteArray? = ByteArray(4) { i ->
                ((data shr 8 * i) and 0xFF).toByte()
            }


            override fun deserializeInternal(data: ByteArray): Long? {
                var v: Long = 0
                for (i in 0 until 4) {
                    v = v or data[i].toLong().let { (if (it < 0) it + 256 else it) shl i * 8 }
                }
                return v
            }
        }

        val ASCII_STRING: ValueConverterBase<String> = object : ValueConverterBase<String>(
                -1,
                "",
                String::class.java
        ) {
            private val ASCII = Charset.forName("ASCII")

            override fun parseInternal(string: String): String? = string

            override fun serializeInternal(data: String): ByteArray? {
                return data.toByteArray(ASCII).reversedArray()
            }


            override fun deserializeInternal(data: ByteArray): String? {
                return data.reversedArray().toString(ASCII).upToNull()
            }
        }

        val UTF8_STRING: ValueConverterBase<String> = object : ValueConverterBase<String>(
                -1,
                "",
                String::class.java
        ) {
            private val UTF8 = Charset.forName("UTF-8")

            override fun parseInternal(string: String): String? = string

            override fun serializeInternal(data: String): ByteArray? {
                return data.toByteArray(UTF8).reversedArray()
            }


            override fun deserializeInternal(data: ByteArray): String? {
                return data.reversedArray().toString(UTF8).trim(0x00.toChar())
            }
        }

        val TIME: ValueConverterBase<Long> = object : ValueConverterBase<Long>(
                6,
                0,
                Long::class.java
        ) {
            // TODO: Refactor with the newer code from above classes

            override fun parseInternal(string: String): Long? {
                return try {
                    string.toLong()
                } catch (nfe: NumberFormatException) {
                    null
                }
            }

            override fun serializeInternal(data: Long): ByteArray? {
                return ByteBuffer.allocate(8).putLong(data).array().copyOfRange(0, 6)
            }

            override fun deserializeInternal(data: ByteArray): Long? {
                var time: Long = 0
                for (i in 5 downTo 0) {
                    time += ((if (data[i].toShort() < 0) (data[i].toShort() + 256).toShort() else data[i].toShort()).toLong() shl ((5 - i) * 8))
                }
                return time
            }
        }

        val FLOAT: ValueConverterBase<Float> = object : ValueConverterBase<Float>(
                4,
                0F,
                Float::class.java
        ) {
            override fun parseInternal(string: String): Float? {
                return try {
                    string.toFloat()
                } catch (nfe: NumberFormatException) {
                    null
                }
            }

            override fun serializeInternal(data: Float): ByteArray? {
                return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putFloat(data).array()
            }

            override fun deserializeInternal(data: ByteArray): Float? {
                return ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).float
            }
        }

        val ENUM: ValueConverterBase<Long> = object : ValueConverterBase<Long>(
                4,
                0,
                Long::class.java
        ) {
            override fun parseInternal(string: String): Long? {
                return try {
                    string.toLong()
                } catch (nfe: NumberFormatException) {
                    null
                }
            }

            override fun serializeInternal(data: Long): ByteArray? = ByteArray(4) { i ->
                ((data shr 8 * i) and 0xFF).toByte()
            }


            override fun deserializeInternal(data: ByteArray): Long? {
                var v: Long = 0
                for (i in 0 until 4) {
                    v = v or data[i].toLong().let { (if (it < 0) it + 256 else it) shl i * 8 }
                }
                return v
            }
        }

        val INT64: ValueConverterBase<Long> = object : ValueConverterBase<Long>(
                8,
                0,
                Long::class.java
        ) {
            override fun parseInternal(string: String): Long? {
                return try {
                    string.toLong()
                } catch (nfe: NumberFormatException) {
                    null
                }
            }

            override fun serializeInternal(data: Long): ByteArray? = ByteArray(8) { i ->
                ((data shr 8 * i) and 0xFF).toByte()
            }

            override fun deserializeInternal(data: ByteArray): Long? {
                var v: Long = 0
                for (i in 0 until 8) {
                    v = v or (data[i].toLong().let { if (it < 0) it + 256 else it + 0 } shl i * 8)
                }
                return v
            }
        }
    }

    class Factory {
        companion object {
            fun <T> createFromTypeId(typeId: Int): ValueConverterBase<T> {
                return try {
                    @Suppress("UNCHECKED_CAST")
                    when (typeId) {
                        0 -> BOOLEAN
                        1 -> UINT8 // Done
                        2 -> UINT16 // Done
                        3 -> UINT32 // Done
                        4 -> INT8 // Done
                        5 -> INT16 // Done
                        6 -> INT32 // Done
                        7 -> FLOAT // Done
                        8 -> ENUM // Done
                        9 -> ASCII_STRING // Done
                        10 -> INT64 // Done
                        else -> throw UnsupportedOperationException("Unimplemented type")
                    } as ValueConverterBase<T>
                } catch (e: ClassCastException) {
                    throw e
                }
            }
        }
    }
}