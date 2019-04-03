package com.aconno.bluetooth.beacon

import android.bluetooth.BluetoothGattCharacteristic
import java.nio.ByteBuffer
import java.util.*

private val HEX_CHARS = "0123456789ABCDEF".toCharArray()

fun String.hexStringToByteArray(): ByteArray {
    this.toUpperCase().let { s ->
        val result = ByteArray(length / 2)

        for (i in 0 until (length.and(0x01.inv())) step 2) {
            val firstIndex = HEX_CHARS.indexOf(s[i])
            val secondIndex = HEX_CHARS.indexOf(s[i + 1])

            val octet = firstIndex.shl(4).or(secondIndex)
            result[i.shr(1)] = octet.toByte()
        }

        return result
    }
}

fun String.hexPairToByte(): Byte = toUpperCase().let {
    HEX_CHARS.indexOf(this[0]).let {
        if (it == -1) 0 else it
    }.shl(4).or(
            HEX_CHARS.indexOf(this[1]).let {
                if (it == -1) 0 else it
            }
    ).toByte()
}

fun ByteArray.toCompactHex(): String {
    val hexArray: CharArray = CharArray(this.size * 2)

    map { it.toInt() }.forEachIndexed { index, value ->
        val hexIndex = index * 2
        hexArray[hexIndex] = HEX_CHARS[(value and 0xF0).ushr(4)]
        hexArray[hexIndex + 1] = HEX_CHARS[(value and 0x0F)]
    }

    return hexArray.joinToString("")
}

fun Byte.toHex(): String = toInt().let {
    "${HEX_CHARS[(it and 0xF0).ushr(4)]}${HEX_CHARS[(it and 0x0F)]}"
}

fun <T> Array<T>.chunk(size: Int): Array<Array<T>> = Array(
        Math.ceil(this.size / size.toDouble()).toInt()
) {
    this.copyOfRange(it * size, (it + 1) * size)
}

fun ByteArray.chunk(size: Int): Array<ByteArray> = Array(
        Math.ceil(this.size / size.toDouble()).toInt()
) {
    this.copyOfRange(it * size, (it + 1) * size)
}

fun Number.toBinaryString(): String = Integer.toBinaryString(this.toInt())

fun ByteArray.toAsciiHexEscaped(): String {
    val buffer = StringBuffer()
    forEach { buffer.append(if ((0x20 <= it) and (it <= 0x7E)) (if (it == 0x5C.toByte()) "\\\\" else it.toChar()) else "\\x${it.toHex()}") }
    return buffer.toString()
}

fun String.toHexUsingAsciiEscaped(): ByteArray {
    var skip: Int = 0
    return windowed(4, 1, true).mapNotNull {
        if (skip > 0) {
            skip--
            null
        } else {
            if (it.startsWith("\\x") && it.length == 4) {
                skip = 3
                it.substring(2).hexPairToByte()
            } else if (it.startsWith("\\\\")) {
                skip = 1
                0x5C
            } else {
                it[0].toByte()
            }
        }
    }.toByteArray()
}


fun ByteArray.stringLength(offset: Int = 0): Int {
    for (i in offset until this.size) if (this[i] == 0x00.toByte()) return i - offset + 1
    return this.size - offset
}

fun UUID.toBytes(): ByteArray {
    val bb = ByteBuffer.wrap(ByteArray(16))
    bb.putLong(this.mostSignificantBits)
    bb.putLong(this.leastSignificantBits)
    return bb.array()
}

fun bytesToUUID(bytes: ByteArray): UUID {
    val bb = ByteBuffer.wrap(bytes)
    return UUID(bb.long, bb.long)
}

fun ByteArray.prependOrShorten(length: Int, init: (Int) -> Byte = { 0 }): ByteArray = if (this.size < length) ByteArray(length - this.size, init) + this else if (this.size > length) this.copyOfRange(0, length) else this

fun ByteArray.rangeContentEquals(fromIndex: Int, toIndex: Int, content: ByteArray): Boolean = this.copyOfRange(fromIndex, toIndex).contentEquals(content)

fun BluetoothGattCharacteristic.isReadable(): Boolean = this.permissions.and(0x01) != 0

fun BluetoothGattCharacteristic.isWriteable(): Boolean = this.permissions.and(0x10) != 0

