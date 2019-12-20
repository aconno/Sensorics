package com.aconno.sensorics.domain.virtualscanningsources.mqtt

import com.aconno.sensorics.domain.virtualscanningsources.VirtualScanningSourceType

class GeneralMqttVirtualScanningSource(
        override val id: Long,
        override val name: String,
        override var enabled: Boolean
//TODO add other params
) : MqttVirtualScanningSource {
    override val type: VirtualScanningSourceType = VirtualScanningSourceType.MQTT
}