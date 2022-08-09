package com.aconno.sensorics.device.beacon.v2.parameters

import com.aconno.sensorics.device.beacon.Parameters
import com.aconno.sensorics.domain.migrate.ValueConverters
import com.aconno.sensorics.domain.migrate.flatten
import com.aconno.sensorics.domain.migrate.hexPairToByteGuarded
import com.aconno.sensorics.domain.migrate.toHex
import java.util.regex.Matcher
import java.util.regex.Pattern


private val pattern: Pattern =
    Pattern.compile("((?:0x[\\dA-Fa-f]{2})|(?:\\\$[^\"]+?(?=(?:0x)| +|\\\$))|(?:\\\$\".+?\"(?=(?:0x)| +|\\\$)))")


fun decodeHexParameterEmbedString(string: String, parameters: Parameters): ByteArray {
    val data: MutableList<Byte> = mutableListOf()

    val matcher: Matcher = pattern.matcher(string)
    while (matcher.find()) {
        matcher.group(0)?.let { match ->
            if (match.startsWith("$")) {
                match.substring(1).let { matchedParameter ->

                    val toMatch = if (matchedParameter.startsWith("\"") && matchedParameter.endsWith("\"")) {
                        matchedParameter.substring(1, matchedParameter.length - 1)
                    } else {
                        matchedParameter
                    }

                    parameters.flatten().find { it.name == toMatch }?.let {
                        data.add('$'.toByte())
                        data.addAll(ValueConverters.UINT8.serialize(it.id.toShort()).toList())
                    }
                }
            } else {
                data.add(match.substring(2).hexPairToByteGuarded())
            }
        }
    }

    return data.toByteArray()
}

fun encodeHexAsParameterEmbedString(bytes: ByteArray, parameters: Parameters): String {
    val builder: StringBuilder = StringBuilder()

    var nextIsParameter = false
    for (byte in bytes) {
        when {
            nextIsParameter -> {
                val id = ValueConverters.UINT8.deserialize(byteArrayOf(byte)).toInt()
                parameters.flatten().find { it.id == id }?.let {
                    builder.append('$')
                    if (it.name.contains(' ')) {
                        builder.append("\"").append(it.name).append("\"")
                    } else {
                        builder.append(it.name)
                    }
                    builder.append(' ')
                }
                nextIsParameter = false
            }
            byte == '$'.toByte() -> nextIsParameter = true
            else -> builder.append(byte.toHex("0x")).append(' ')
        }
    }

    return builder.toString().trim()
}

fun <T : Number> getAsGivenTypeOrNull(value: String, type: Class<T>): T? {
    return try {
        when (type.name) {
            Byte::class.java.name -> value.toByte() as T
            Short::class.java.name -> value.toShort() as T
            Int::class.java.name -> value.toInt() as T
            Long::class.java.name -> value.toLong() as T
            Float::class.java.name -> value.toFloat() as T
            Double::class.java.name -> value.toDouble() as T
            else -> throw NotImplementedError("class ${type.name} not implemented")
        }
    } catch (e: NumberFormatException) {
        null
    }
}