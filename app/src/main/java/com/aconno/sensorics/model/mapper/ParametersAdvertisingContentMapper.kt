package com.aconno.sensorics.model.mapper

import com.aconno.sensorics.device.beacon.Parameters
import com.aconno.sensorics.device.beacon.Slot
import com.aconno.sensorics.device.beacon.v2.parameters.decodeHexParameterEmbedString
import com.aconno.sensorics.device.beacon.v2.parameters.encodeHexAsParameterEmbedString
import com.aconno.sensorics.domain.migrate.hexStringToByteArray
import com.aconno.sensorics.domain.migrate.toCompactHex
import javax.inject.Inject

private val KEY_CUSTOM = Slot.KEY_ADVERTISING_CONTENT_CUSTOM_CUSTOM
private val KEY_DEFAULT = Slot.KEY_ADVERTISING_CONTENT_DEFAULT_DATA

/**
 * Mapper that maps bytes data to Parameter string representation and vice versa
 */
class ParametersAdvertisingContentMapper @Inject constructor() {

    /**
     * Copy original map, change it's values corresponding to custom and default key to parameters
     * string and return it
     */
    fun getReadableAdContent(
        originalAdContent: Map<String, String>,
        parameters: Parameters
    ): MutableMap<String, String> {
        val adContentCopy = HashMap(originalAdContent)

        adContentCopy[KEY_CUSTOM]?.let { hexStr ->
            adContentCopy[KEY_CUSTOM] =
                encodeHexAsParameterEmbedString(hexStr.hexStringToByteArray(), parameters)
        }

        adContentCopy[KEY_DEFAULT]?.let { hexStr ->
            adContentCopy[KEY_DEFAULT] =
                encodeHexAsParameterEmbedString(hexStr.hexStringToByteArray(), parameters)
        }

        return adContentCopy
    }

    /**
     * Copy map, change it's values corresponding to custom and default key to
     * hex representation and return it
     */
    fun getHexAdContent(
        readableAdContent: Map<String, String>,
        parameters: Parameters
    ): MutableMap<String, String> {
        val adContentCopy = HashMap(readableAdContent)

        adContentCopy[KEY_CUSTOM]?.let { hexStr ->
            adContentCopy[KEY_CUSTOM] =
                decodeHexParameterEmbedString(hexStr, parameters).toCompactHex()
        }

        adContentCopy[KEY_DEFAULT]?.let { hexStr ->
            adContentCopy[KEY_DEFAULT] =
                decodeHexParameterEmbedString(hexStr, parameters).toCompactHex()
        }

        return adContentCopy
    }

}