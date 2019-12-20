package com.aconno.sensorics.domain.interactor.virtualscanningsource

import com.aconno.sensorics.domain.virtualscanningsources.BaseVirtualScanningSource
import com.aconno.sensorics.domain.virtualscanningsources.mqtt.MqttVirtualScanningSource
import com.aconno.sensorics.domain.virtualscanningsources.mqtt.MqttVirtualScanningSourceRepository

class UpdateVirtualScanningSourceUseCase(
        private val mqttSourceRepository: MqttVirtualScanningSourceRepository
) {
    fun execute(parameter: BaseVirtualScanningSource) {
        when (parameter) {
            is MqttVirtualScanningSource -> mqttSourceRepository.updateMqttVirtualScanningSource(parameter)
            else -> throw IllegalArgumentException("Unexpected virtual scanning source type.")
        }
    }
}