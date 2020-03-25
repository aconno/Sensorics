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
     * Change map values corresponding to custom and default key to parameters string representation
     */
    fun convertToReadableAdContent(
        content: MutableMap<String, String>,
        parameters: Parameters
    ) {

        content[KEY_CUSTOM]?.let { hexStr ->
            content[KEY_CUSTOM] =
                encodeHexAsParameterEmbedString(hexStr.hexStringToByteArray(), parameters)
        }

        content[KEY_DEFAULT]?.let { hexStr ->
            content[KEY_DEFAULT] =
                encodeHexAsParameterEmbedString(hexStr.hexStringToByteArray(), parameters)
        }
    }

    /**
     * Change map values corresponding to custom and default key to
     * hex representation
     */
    fun getHexAdContent(
        content: MutableMap<String, String>,
        parameters: Parameters
    ) {

        content[KEY_CUSTOM]?.let { hexStr ->
            content[KEY_CUSTOM] =
                decodeHexParameterEmbedString(hexStr, parameters).toCompactHex()
        }

        content[KEY_DEFAULT]?.let { hexStr ->
            content[KEY_DEFAULT] =
                decodeHexParameterEmbedString(hexStr, parameters).toCompactHex()
        }
    }

}