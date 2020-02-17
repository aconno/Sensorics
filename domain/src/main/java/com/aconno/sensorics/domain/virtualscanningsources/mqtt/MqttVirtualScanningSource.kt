package com.aconno.sensorics.domain.virtualscanningsources.mqtt

import com.aconno.sensorics.domain.interactor.virtualscanningsource.mqtt.MqttVirtualScanningSourceProtocol
import com.aconno.sensorics.domain.virtualscanningsources.BaseVirtualScanningSource

interface MqttVirtualScanningSource : BaseVirtualScanningSource {
    val protocol : MqttVirtualScanningSourceProtocol
    val address : String
    val port : Int
    val path : String
    val clientId : String
    val username : String
    val password : String
    val qualityOfService : Int

    fun getUri(): String
}