package com.aconno.sensorics.domain.interactor.virtualscanningsource.mqtt

import com.aconno.sensorics.domain.virtualscanningsources.BaseVirtualScanningSource
import com.aconno.sensorics.domain.virtualscanningsources.mqtt.MqttVirtualScanningSourceRepository

class GetAllEnabledMqttVirtualScanningSourceUseCase (
        private val mqttSourceRepository: MqttVirtualScanningSourceRepository
) {
    fun execute(): List<BaseVirtualScanningSource> {
        return mqttSourceRepository.getAllEnabledMqttVirtualScanningSource()
    }
}