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



    override fun getUri(): String {
        val protocol = when (protocol) {
            MqttVirtualScanningSourceProtocol.TCP -> "tcp"
            MqttVirtualScanningSourceProtocol.WEBSOCKET -> "ws"
        }
        val path = if(path.isNotEmpty()) {
            "/{${path}}"
        } else {
            ""
        }

        return "$protocol://${address}:${port}$path"

    }


}