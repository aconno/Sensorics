package com.aconno.sensorics.domain

import com.aconno.sensorics.domain.format.AdvertisementFormat
import com.aconno.sensorics.domain.model.ScanResult
import java.io.Closeable
import java.lang.StringBuilder
import kotlin.experimental.and

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

fun ScanResult.isSettingsSupportOn(format: AdvertisementFormat) : Boolean {
    format.getSettingsSupport()
        ?.let { settingsSupport ->
            return ByteOperations.isolateMsd(rawData)[settingsSupport.index] and settingsSupport.mask == settingsSupport.mask
        }

    return false
}