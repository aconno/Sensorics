package com.aconno.sensorics.model.mapper

import com.aconno.sensorics.device.beacon.Beacon
import com.aconno.sensorics.device.beacon.Parameters
import com.aconno.sensorics.device.beacon.Slot
import javax.inject.Inject

class WebViewAppBeaconMapper @Inject constructor(private val parametersAdContentMapper: ParametersAdvertisingContentMapper) {

    fun prepareAdContentForWebView(beacon: Beacon) {
        beacon.slots.forEach {
            convertToReadableAdvContent(it, beacon.parameters)
        }
    }

    fun restoreAdContent(beacon: Beacon) {
        beacon.slots.forEach {
            convertToHexAdvContent(it, beacon.parameters)
        }
    }

    private fun convertToReadableAdvContent(
        slot: Slot,
        parameters: Parameters
    ) {
        if (slot.getType() == Slot.Type.CUSTOM || slot.getType() == Slot.Type.DEFAULT) {
            parametersAdContentMapper.convertToReadableAdContent(
                slot.advertisingContent, parameters
            )
        }
    }

    private fun convertToHexAdvContent(
        slot: Slot,
        parameters: Parameters
    ) {
        if (slot.getType() == Slot.Type.CUSTOM || slot.getType() == Slot.Type.DEFAULT) {
            parametersAdContentMapper.getHexAdContent(slot.advertisingContent, parameters)
        }
    }
}