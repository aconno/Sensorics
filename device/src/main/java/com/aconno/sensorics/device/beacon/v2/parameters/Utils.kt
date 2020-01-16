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
        var match: String = matcher.group(0)

        if (match.startsWith("$")) {
            match = match.substring(1)

            if (match.startsWith("\"") && match.endsWith("\"")) {
                match = match.substring(1, match.length - 1)
            }

            parameters.flatten().find { it.name == match }?.let {
                data.add('$'.toByte())
                data.addAll(ValueConverters.UINT8.serialize(it.id.toShort()).toList())
            }
        } else {
            data.add(match.substring(2).hexPairToByteGuarded())
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
                        builder.append('"').append(it.name).append('"')
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