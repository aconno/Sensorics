package com.aconno.sensorics.domain.virtualscanningsources.mqtt

import com.aconno.sensorics.domain.interactor.virtualscanningsource.mqtt.MqttVirtualScanningSourceProtocol
import com.aconno.sensorics.domain.virtualscanningsources.VirtualScanningSourceType

class GeneralMqttVirtualScanningSource(
        override val id: Long,
        override val name: String,
        override var enabled: Boolean,
        override val protocol : MqttVirtualScanningSourceProtocol,
        override val address : String,
        override val port : Int,
        override val path : String,
        override val clientId : String,
        override val username : String,
        override val password : String,
        override val qualityOfService : Int
) : MqttVirtualScanningSource {
    override val type: VirtualScanningSourceType = VirtualScanningSourceType.MQTT
}