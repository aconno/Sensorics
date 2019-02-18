package com.aconno.bluetooth.beacon

private val HEX_CHARS = "0123456789ABCDEF"

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

private val HEX_CHARS_ = "0123456789ABCDEF".toCharArray()

fun ByteArray.toCompactHex(): String {
    val result = StringBuffer()

    forEach {
        val octet = it.toInt()
        val firstIndex = (octet and 0xF0).ushr(4)
        val secondIndex = octet and 0x0F
        result.append(HEX_CHARS_[firstIndex])
        result.append(HEX_CHARS_[secondIndex])
    }

    return result.toString()
}

fun ByteArray.extendOrShorten(length: Int, init: (Int) -> Byte = { 0 }): ByteArray =
    if (this.size < length) this + ByteArray(
        length - this.size,
        init
    ) else if (this.size > length) this.copyOfRange(0, length) else this

fun ByteArray.prependOrShorten(length: Int, init: (Int) -> Byte = { 0 }): ByteArray =
    if (this.size < length) ByteArray(
        length - this.size,
        init
    ) + this else if (this.size > length) this.copyOfRange(0, length) else this

fun ByteArray.rangeContentEquals(fromIndex: Int, toIndex: Int, content: ByteArray): Boolean =
    this.copyOfRange(fromIndex, toIndex).contentEquals(content)