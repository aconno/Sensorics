@file:Suppress("unused")

package com.aconno.sensorics.domain.migrate

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import java.nio.ByteBuffer
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.zip.CRC32
import kotlin.experimental.and
import kotlin.math.abs
import kotlin.math.ceil

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

fun String.hexPairToByteGuarded(): Byte = toUpperCase().let {
    HEX_CHARS.indexOf(this[0]).let {
        if (it == -1) 0 else it
    }.shl(4).or(
        HEX_CHARS.indexOf(this[1]).let {
            if (it == -1) 0 else it
        }
    ).toByte()
}

@Throws(IllegalArgumentException::class)
fun String.hexPairToByte(): Byte = toUpperCase().let {
    HEX_CHARS.indexOf(this[0]).let {
        if (it == -1) {
            throw IllegalArgumentException("${this[0]} is not a valid hexadecimal character!")
        } else {
            it
        }
    }.shl(4).or(
        HEX_CHARS.indexOf(this[1]).let {
            if (it == -1) {
                throw IllegalArgumentException("${this[1]} is not a valid hexadecimal character!")
            } else {
                it
            }
        }
    ).toByte()
}

fun String.upToNull(): String = this.substring(0, indexOf(0x00.toChar()).takeUnless { it == -1 }
    ?: this.length)

fun ByteArray.toHex() = this.joinToString(separator = "") { it.toInt().and(0xff).toString(16).padStart(2, '0') }

fun ByteArray.toCompactHex(): String {
    val hexArray = CharArray(this.size * 2)

    map { it.toInt() }.forEachIndexed { index, value ->
        val hexIndex = index * 2
        hexArray[hexIndex] = HEX_CHARS[(value and 0xF0).ushr(4)]
        hexArray[hexIndex + 1] = HEX_CHARS[(value and 0x0F)]
    }

    return hexArray.joinToString("")
}

/**
 * Gets flag value
 *
 * @param pos position, 31 = MSB, 0 = LSB
 */
infix fun Int.extractFlag(pos: Int): Boolean = ((this shr pos) and 0x01) == 0x01

/**
 * Sets a flag value to true
 *
 * @param pos position, 31 = MSB, 0 = LSB
 */
infix fun Int.setFlag(pos: Int): Int = this or (0x01 shl pos)

/**
 * Sets a flag value to false
 *
 * @param pos position, 31 = MSB, 0 = LSB
 */
infix fun Int.clearFlag(pos: Int): Int = this and (0x01 shl pos).inv()

/**
 * Gets flag value
 *
 * @param pos position, 7 = MSB, 0 = LSB
 */
infix fun Byte.extractFlag(pos: Int): Boolean = (((this shr pos) and 0x01) == 0x01.toByte())

infix fun Byte.shl(i: Int): Byte = ((this.toInt() and 0xFF) shl i).toByte()

infix fun Byte.shr(i: Int): Byte = ((this.toInt() and 0xFF) shr i).toByte()

fun Byte.toHex(prefix: String = ""): String = toInt().let {
    prefix + "${HEX_CHARS[(it and 0xF0).ushr(4)]}${HEX_CHARS[(it and 0x0F)]}"
}

infix fun <T> Array<T>.chunk(size: Int): Array<Array<T>> = Array(
    ceil(this.size / size.toDouble()).toInt()
) {
    this.copyOfRange(it * size, (it + 1) * size)
}

infix fun ByteArray.chunk(size: Int): Array<ByteArray> = Array(
    ceil(this.size / size.toDouble()).toInt()
) {
    this.copyOfRange(it * size, (it + 1) * size)
}

fun Number.toBinaryString(): String = Integer.toBinaryString(this.toInt())

fun ByteArray.stringLength(offset: Int = 0): Int {
    for (i in offset until this.size) if (this[i] == 0x00.toByte()) return i - offset + 1
    return this.size - offset
}

fun ByteArray.readString(offset: Int = 0, length: Int = this.stringLength(offset)): ByteArray {
    return this.copyOfRange(offset, offset + length)
}

fun <T> Iterable<Array<T>>.flatten(): Array<T> = this.reduce { e1, e2 -> e1 + e2 }

fun Iterable<ByteArray>.flatten(): ByteArray = this.reduce { e1, e2 -> e1 + e2 }

fun <K, V : List<IV>, IV> Map<K, V>.flatten(): List<IV> = this.flatMap { it.value }

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

fun Long.timeMillisToHHMMSS(): String = (this % 86400000).let { time ->
    String.format("%02d:%02d:%02d",
        TimeUnit.MILLISECONDS.toHours(time),
        TimeUnit.MILLISECONDS.toMinutes(time) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time)),
        TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time))
    )
}

fun Long.timeToHighestOrder(): String {
    var copy: Long = this
    val millis: Int = (copy % 1000).toInt()
    copy -= millis
    val seconds: Int = ((copy / 1000) % 60).toInt()
    copy -= 1000 * seconds
    val minutes: Int = ((copy / 60000) % 60).toInt()
    copy -= 60000 * seconds
    val hours: Int = ((copy / 3600000)).toInt()
    copy -= 3600000 * hours
    val days: Int = ((copy / 86400000)).toInt()
    copy -= 86400000 * days
    val weeks: Int = ((copy / 604800000)).toInt()
    copy -= 604800000 * weeks
    val months: Int = ((copy / 2419200000)).toInt()
    copy -= 2419200000 * months
    return when {
        millis != 0 -> "Every ${millis + seconds * 1000} milliseconds"
        seconds != 0 -> "Every ${seconds + minutes * 60} seconds"
        minutes != 0 -> "Every ${minutes + hours * 60} minutes"
        hours != 0 -> "Every ${hours + days * 24} hours"
        days != 0 -> "Every ${days + weeks * 7} days"
        weeks != 0 -> "Every ${weeks + months * 4} weeks"
        else -> "Every $months months"
    }
}

fun Long.timeToMMWWDDHHMMSSMM(): String {
    var copy: Long = this
    val millis: Int = (copy % 1000).toInt()
    copy -= millis
    val seconds: Int = ((copy / 1000) % 60).toInt()
    copy -= 1000 * seconds
    val minutes: Int = ((copy / 60000) % 60).toInt()
    copy -= 60000 * minutes
    val hours: Int = ((copy / 3600000)).toInt()
    copy -= 3600000 * hours
    val days: Int = ((copy / 86400000)).toInt()
    copy -= 86400000 * days
    val weeks: Int = ((copy / 604800000)).toInt()
    copy -= 604800000 * weeks
    val months: Int = ((copy / 2419200000)).toInt()
    copy -= 2419200000 * months

    val sb: StringBuilder = StringBuilder()
    if (months != 0) {
        sb.append(months).append(" months, ")
    }
    if (weeks != 0) {
        sb.append(weeks).append(" weeks, ")
    }
    if (days != 0) {
        sb.append(days).append(" days, ")
    }
    if (hours != 0) {
        sb.append(hours).append(" hours, ")
    }
    if (minutes != 0) {
        sb.append(minutes).append(" minutes, ")
    }
    if (seconds != 0) {
        sb.append(seconds).append(" seconds, ")
    }
    if (millis != 0) {
        sb.append(millis).append(" millis, ")
    }
    return sb.toString().let { it.substring(0, it.length - 2) }

}


fun List<Byte>.getClosestElement(to: Byte): Byte {
    var chosen: Byte = this[0]
    var diff: Byte = abs(to - chosen).toByte()

    this.forEach {
        abs(to - it).toByte().let { newDiff ->
            if (newDiff < diff) {
                chosen = it
                diff = newDiff
            }
        }
    }

    return chosen
}

fun List<Byte>.getClosestElementIndex(to: Byte): Int = this.indexOf(this.getClosestElement(to))


fun List<Int>.getClosestElement(to: Int): Int {
    var chosen: Int = this[0]
    var diff: Int = abs(to - chosen)

    this.forEach {
        abs(to - it).let { newDiff ->
            if (newDiff < diff) {
                chosen = it
                diff = newDiff
            }
        }
    }

    return chosen
}

fun List<Int>.getClosestElementIndex(to: Int): Int = this.indexOf(this.getClosestElement(to))

fun List<Long>.getClosestElement(to: Long): Long {
    var chosen: Long = this[0]
    var diff: Long = abs(to - chosen)

    this.forEach {
        abs(to - it).let { newDiff ->
            if (newDiff < diff) {
                chosen = it
                diff = newDiff
            }
        }
    }

    return chosen
}

fun List<Long>.getClosestElementIndex(to: Long): Int = this.indexOf(this.getClosestElement(to))

fun <E> Deque<E>.addAllFirst(queue: Queue<E>): Boolean = queue.reversed().map { this.offerFirst(it) }.all { it }
fun <E> Deque<E>.addAllFirst(collection: Collection<E>): Boolean = collection.reversed().map { this.offerFirst(it) }.all { it }

fun CRC32.getValueForUpdate(data: ByteArray): Long = this.also { it.update(data) }.value

fun JsonObject.getObjectOrNull(name: String): JsonObject? = this.get(name)?.asObjectOrNull()
fun JsonObject.getArrayOrNull(name: String): JsonArray? = this.get(name)?.asArrayOrNull()
fun JsonObject.getStringOrNull(name: String): String? = this.get(name)?.asStringOrNull()
fun JsonObject.getNumberOrNull(name: String): Number? = this.get(name)?.asNumberOrNull()
fun JsonObject.getBooleanOrNull(name: String): Boolean? = this.get(name)?.asBooleanOrNull()

fun JsonElement.asObjectOrNull(): JsonObject? = (this as? JsonObject)?.takeIf { it.isJsonObject }?.asJsonObject
fun JsonElement.asArrayOrNull(): JsonArray? = (this as? JsonArray)?.takeIf { it.isJsonArray }?.asJsonArray
fun JsonElement.asStringOrNull(): String? = (this as? JsonPrimitive)?.takeIf { it.isString }?.asString
fun JsonElement.asNumberOrNull(): Number? = (this as? JsonPrimitive)?.takeIf { it.isNumber }?.asNumber
fun JsonElement.asBooleanOrNull(): Boolean? = (this as? JsonPrimitive)?.takeIf { it.isBoolean }?.asBoolean