package com.aconno.sensorics.ui.settings_framework

import android.os.Build
import androidx.annotation.RequiresApi
import com.aconno.sensorics.device.beacon.Beacon
import com.aconno.sensorics.device.beacon.Slot
import com.aconno.sensorics.device.beacon.Slot.Companion.KEY_ADVERTISING_CONTENT_DEFAULT_DATA
import com.google.gson.GsonBuilder
import java.io.Serializable

class SlotDefaultParameters : Serializable {

    var name: String = ""
    var type: String = "Default"
    var advertisingContent: MutableMap<String, String> = mutableMapOf()
    var packetCount: String = ""
    var txPower: String = ""
    var advertisingMode: Boolean = false

    class Builder {
        @RequiresApi(Build.VERSION_CODES.N)
        fun buildFromSlot(
            slot: Slot,
            beacon: Beacon
        ): SlotDefaultParameters {
            val defaultSlot = SlotDefaultParameters()

            defaultSlot.name = slot.name
            defaultSlot.type = slot.getType().tabName
            val data: ByteArray =
                slot.advertisingContent.getOrDefault(KEY_ADVERTISING_CONTENT_DEFAULT_DATA, "")
                    ?.let {
                        it.toByteArray()
                    }
             val gson = GsonBuilder().create()
            defaultSlot.advertisingContent = slot.advertisingContent

            defaultSlot.packetCount = slot.packetCount.toString()
            defaultSlot.advertisingMode = when (slot.advertisingMode) {
                Slot.AdvertisingModeParameters.Mode.INTERVAL -> false
                Slot.AdvertisingModeParameters.Mode.EVENT -> true
            }
            defaultSlot.txPower = beacon.supportedTxPowers.indexOf(slot.txPower).toString()
            return defaultSlot
        }
    }

  /*
  @RequiresApi(Build.VERSION_CODES.N)
    private fun getLocalType(type: Slot.Type, slot: Slot, beacon: Beacon): MutableMap<String, String> {
        return when (type) {
            Slot.Type.DEFAULT -> {
                val data = slot.advertisingContent.getOrDefault(KEY_ADVERTISING_CONTENT_DEFAULT_DATA, "")
                    ?.let {
                        it.toByteArray()
                    }
                slot.advertisingContent.mapValues {
                    encodeHexAsParameterEmbedString(data, beacon.parameters)
                }.toMutableMap()

            }
            Slot.Type.UID -> {


            }
            Slot.Type.URL -> {

                slot.advertisingContent.getOrDefault(Slot.KEY_ADVERTISING_CONTENT_URL_URL, "")
                    ?.let {
                        it.toByteArray()
                    }
            }
            Slot.Type.I_BEACON -> {
                slot.advertisingContent.getOrDefault(Slot.KEY_ADVERTISING_CONTENT_IBEACON_UUID, "")
                    ?.let {
                        it.toByteArray()
                    } +
                        slot.advertisingContent.getOrDefault(
                            Slot.KEY_ADVERTISING_CONTENT_IBEACON_UUID,
                            ""
                        )
                            ?.let {
                                it.toByteArray()
                            }

            }
            Slot.Type.DEVICE_INFO, Slot.Type.EMPTY, Slot.Type.CUSTOM -> Slot.KEY_ADVERTISING_CONTENT_CUSTOM_CUSTOM
            Slot.Type.TLM -> TODO()
        }
    }


    internal fun encodeHexAsParameterEmbedString(bytes: ByteArray, parameters: Parameters): String {
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
    }*/
}

