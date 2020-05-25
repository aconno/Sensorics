package com.aconno.sensorics.domain

import java.io.Closeable
import java.lang.StringBuilder

inline fun <T : Closeable?, R> T.tryUse(block: (T) -> R, catch: (Throwable) -> R): R {
    return try {
        this.use {
            block(this)
        }
    } catch (e: Throwable) {
        catch(e)
    }
}


fun String.toSnakeCase(): String {
    if(this.find { it.isLowerCase() } == null) {
        return this.toLowerCase()
    }

    val stringBuilder = StringBuilder()
    this.forEachIndexed { index, char ->
        if(char.isUpperCase() && index > 0 && this[index-1]!='-') {
            stringBuilder.append("_${char.toLowerCase()}")
        } else if(char == '-') {
            stringBuilder.append("_")
        }
        else if(!char.isWhitespace()) {
            stringBuilder.append(char.toLowerCase())
        }
    }
    return stringBuilder.toString()
}